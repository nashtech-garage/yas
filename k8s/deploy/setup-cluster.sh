#!/bin/bash
# ============================================================================
# SETUP CLUSTER INFRASTRUCTURE — Multi-Environment
# ============================================================================
# Deploys infrastructure (PostgreSQL, Kafka, Elasticsearch, Observability)
# for a specific environment with proper namespace isolation.
#
# Usage:
#   ./setup-cluster.sh <env>     (dev|staging|production, default: dev)
#
# Prerequisites:
#   - Helm 3 installed
#   - yq installed (https://github.com/mikefarah/yq)
#   - cluster-config-{env}.yaml present in this directory
#
# What this deploys:
#   OPERATORS (cluster-scoped, idempotent — safe to run per env):
#     - Zalando Postgres Operator (namespace: postgres)
#     - Strimzi Kafka Operator (namespace: kafka)
#     - ECK Elasticsearch Operator (namespace: elasticsearch)
#     - OpenTelemetry Operator (namespace: observability)
#     - Cert Manager (namespace: cert-manager)
#   INSTANCES (per-env isolated namespaces):
#     - postgres-{env}: PostgreSQL cluster + pgAdmin
#     - kafka-{env}: Kafka cluster + AKHQ
#     - elasticsearch-{env}: Elasticsearch cluster + Kibana
#     - observability-{env}: Loki, Tempo, Promtail, OTel Collector
#     - zookeeper-{env}: Zookeeper
#
# WHAT IS NOT DEPLOYED (commented out):
#   - Grafana — not used by this project
#   - Prometheus / kube-prometheus-stack — not used by this project
# ============================================================================

set -x

# --------------------------------------------------------------------------
# Environment selection
# --------------------------------------------------------------------------
ENV=${1:-dev}
CONFIG_FILE="cluster-config-${ENV}.yaml"

if [ ! -f "$CONFIG_FILE" ]; then
    echo "ERROR: Config file '$CONFIG_FILE' not found."
    echo "Available: cluster-config-dev.yaml, cluster-config-staging.yaml, cluster-config-production.yaml"
    exit 1
fi

echo "============================================"
echo " Deploying infrastructure for: ${ENV}"
echo " Config file: ${CONFIG_FILE}"
echo "============================================"

# --------------------------------------------------------------------------
# Add chart repos and update
# --------------------------------------------------------------------------
helm repo add postgres-operator-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator
helm repo add strimzi https://strimzi.io/charts/
helm repo add akhq https://akhq.io/
helm repo add elastic https://helm.elastic.co
helm repo add grafana https://grafana.github.io/helm-charts
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add open-telemetry https://open-telemetry.github.io/opentelemetry-helm-charts
helm repo add jetstack https://charts.jetstack.io
helm repo add sealed-secrets https://bitnami.github.io/sealed-secrets
helm repo update

# --------------------------------------------------------------------------
# Read configuration individually (not read -rd '' which breaks on empty values)
# --------------------------------------------------------------------------
DOMAIN=$(yq -r '.domain' "$CONFIG_FILE")
ENV_SUBDOMAIN=$(yq -r '.envSubdomain // ""' "$CONFIG_FILE")
PG_REPLICAS=$(yq -r '.postgresql.replicas' "$CONFIG_FILE")
PG_USERNAME=$(yq -r '.postgresql.username' "$CONFIG_FILE")
PG_PASSWORD=$(yq -r '.postgresql.password' "$CONFIG_FILE")
PG_VOLUME_SIZE=$(yq -r '.postgresql.volumeSize' "$CONFIG_FILE")
KAFKA_REPLICAS=$(yq -r '.kafka.replicas' "$CONFIG_FILE")
ZK_REPLICAS=$(yq -r '.zookeeper.replicas' "$CONFIG_FILE")
ES_REPLICAS=$(yq -r '.elasticsearch.replicas' "$CONFIG_FILE")
ES_PASSWORD=$(yq -r '.credentials.elasticsearch.password // .elasticsearch.password' "$CONFIG_FILE")

# --------------------------------------------------------------------------
# Build env-specific namespaces and hostnames
# --------------------------------------------------------------------------
# Production uses clean URLs (no env prefix), dev/staging use subdomain
if [ -z "$ENV_SUBDOMAIN" ] || [ "$ENV_SUBDOMAIN" = "null" ]; then
    HOST_PREFIX=""
else
    HOST_PREFIX="${ENV_SUBDOMAIN}."
fi

PG_NS="postgres-${ENV}"
KAFKA_NS="kafka-${ENV}"
ES_NS="elasticsearch-${ENV}"
OBS_NS="observability-${ENV}"
ZK_NS="zookeeper-${ENV}"

echo "Namespace mapping: PG=${PG_NS}, Kafka=${KAFKA_NS}, ES=${ES_NS}, OBS=${OBS_NS}, ZK=${ZK_NS}"
echo "Host prefix: '${HOST_PREFIX}' (envSubdomain='${ENV_SUBDOMAIN}')"

# ============================================================================
# OPERATORS — Cluster-scoped, installed once per cluster.
# helm upgrade --install is idempotent: running it multiple times for
# different envs will just no-op after the first successful install.
# ============================================================================

# --------------------------------------------------------------------------
# Zalando PostgreSQL Operator
# --------------------------------------------------------------------------
helm upgrade --install postgres-operator postgres-operator-charts/postgres-operator \
  --create-namespace --namespace postgres

# --------------------------------------------------------------------------
# Strimzi Kafka Operator (cluster-wide watch for env-specific namespaces)
# --------------------------------------------------------------------------
helm upgrade --install kafka-operator strimzi/strimzi-kafka-operator \
  --create-namespace --namespace kafka \
  --set watchAnyNamespace=true

# Wait for Strimzi CRDs to register before creating Kafka clusters
echo "Waiting for Strimzi CRDs to be registered..."
kubectl wait --for=condition=established crd/kafkas.kafka.strimzi.io --timeout=120s 2>/dev/null || sleep 30

# --------------------------------------------------------------------------
# ECK Elasticsearch Operator
# --------------------------------------------------------------------------
helm upgrade --install elastic-operator elastic/eck-operator \
  --create-namespace --namespace elasticsearch

# --------------------------------------------------------------------------
# Cert Manager
# --------------------------------------------------------------------------
helm upgrade --install cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --version v1.12.0 \
  --set installCRDs=true \
  --set prometheus.enabled=false \
  --set webhook.timeoutSeconds=30 \
  --set admissionWebhooks.certManager.create=true \
  --set startupapicheck.enabled=false

# Wait for cert-manager webhook pod to be ready before anything touches Ingresses
echo "Waiting for cert-manager webhook to be ready..."
kubectl wait --for=condition=ready pod -l app.kubernetes.io/instance=cert-manager -n cert-manager --timeout=120s 2>/dev/null || sleep 30

# --------------------------------------------------------------------------
# OpenTelemetry Operator
# --------------------------------------------------------------------------
helm upgrade --install opentelemetry-operator open-telemetry/opentelemetry-operator \
  --create-namespace --namespace observability

# --------------------------------------------------------------------------
# Sealed Secrets Operator
# --------------------------------------------------------------------------
kubectl create namespace kube-system --dry-run=client -o yaml | kubectl apply -f -
helm upgrade --install sealed-secrets sealed-secrets/sealed-secrets \
  --namespace kube-system \
  --set keyRenewPeriod=0

# Wait for Sealed Secrets controller to be ready
echo "Waiting for Sealed Secrets deployment to be ready..."
kubectl rollout status deployment/sealed-secrets -n kube-system --timeout=150s || sleep 30

# Install kubeseal CLI locally on the master node if not already present
if ! command -v kubeseal &> /dev/null; then
  echo "Installing kubeseal CLI..."
  KUBESEAL_VERSION="0.26.2"
  curl -OL "https://github.com/bitnami-labs/sealed-secrets/releases/download/v${KUBESEAL_VERSION}/kubeseal-${KUBESEAL_VERSION}-linux-amd64.tar.gz"
  tar -xzf "kubeseal-${KUBESEAL_VERSION}-linux-amd64.tar.gz" kubeseal
  sudo install -m 755 kubeseal /usr/local/bin/kubeseal
  rm -f "kubeseal-${KUBESEAL_VERSION}-linux-amd64.tar.gz" kubeseal
  echo "kubeseal CLI installed successfully."
else
  echo "kubeseal CLI is already installed."
fi

# ============================================================================
# INSTANCES — Per-environment isolated resources
# ============================================================================

# --------------------------------------------------------------------------
# PostgreSQL Cluster
# --------------------------------------------------------------------------
# Delete the PostgreSQL CR entirely — Zalando operator cascades to pods,
# PVCs, secrets, and endpoints. A clean recreate ensures databases and
# users are properly initialized.
echo "Deleting PostgreSQL cluster (CR) for clean recreate..."
kubectl delete postgresql postgresql -n "${PG_NS}" --ignore-not-found --timeout=120s 2>/dev/null || true
# Wait for cascade deletion to complete
sleep 10
# Force-remove any stuck PVCs (in case cascade delete hangs)
kubectl patch pvc -n "${PG_NS}" --all --type merge -p '{"metadata":{"finalizers":[]}}' 2>/dev/null || true
kubectl delete pvc -n "${PG_NS}" --all --ignore-not-found --timeout=60s 2>/dev/null || true

helm upgrade --install "postgres-${ENV}" ./postgres/postgresql \
  --create-namespace --namespace "${PG_NS}" \
  --set replicas="$PG_REPLICAS" \
  --set username="$PG_USERNAME" \
  --set password="$PG_PASSWORD" \
  --set volumeSize="$PG_VOLUME_SIZE"

# Wait for Zalando operator to create databases and users
echo "Waiting for Postgres leader and databases..."
kubectl wait --for=condition=ready pod -l application=spilo -n "${PG_NS}" --timeout=300s
sleep 30
kubectl wait --for=condition=ready pod -l application=spilo -n "${PG_NS}" --timeout=300s
sleep 30

# --------------------------------------------------------------------------
# pgAdmin
# --------------------------------------------------------------------------
pg_admin_hostname="pgadmin.${HOST_PREFIX}${DOMAIN}" yq -i '.hostname=env(pg_admin_hostname)' ./postgres/pgadmin/values.yaml
helm upgrade --install "pgadmin-${ENV}" ./postgres/pgadmin \
  --create-namespace --namespace "${PG_NS}" \
  --values "./infra-${ENV}-affinity.yaml"

# --------------------------------------------------------------------------
# Kafka Cluster
# --------------------------------------------------------------------------
helm upgrade --install "kafka-cluster-${ENV}" ./kafka/kafka-cluster \
  --create-namespace --namespace "${KAFKA_NS}" \
  --set kafka.replicas="$KAFKA_REPLICAS" \
  --set postgresql.username="$PG_USERNAME" \
  --set postgresql.password="$PG_PASSWORD" \
  --set postgresql.namespace="$PG_NS" \
  --values "./infra-${ENV}-affinity.yaml"

# --------------------------------------------------------------------------
# AKHQ (Kafka UI)
# --------------------------------------------------------------------------
akhq_hostname="akhq.${HOST_PREFIX}${DOMAIN}" yq -i '.hostname=env(akhq_hostname)' ./kafka/akhq.values.yaml
helm upgrade --install "akhq-${ENV}" akhq/akhq \
  --create-namespace --namespace "${KAFKA_NS}" \
  --values ./kafka/akhq.values.yaml \
  --values "./infra-${ENV}-affinity.yaml"

# --------------------------------------------------------------------------
# Elasticsearch Cluster
# --------------------------------------------------------------------------
# Delete the ES custom resource first to cascade-delete pods and PVCs,
# then patch/delete any remaining PVCs to avoid version-upgrade errors.
kubectl delete elasticsearch elasticsearch -n "${ES_NS}" --ignore-not-found --timeout=120s 2>/dev/null || true
kubectl patch pvc -n "${ES_NS}" --all --type merge -p '{"metadata":{"finalizers":[]}}' 2>/dev/null || true
kubectl delete pvc -n "${ES_NS}" --all --ignore-not-found --timeout=60s 2>/dev/null || true

# Create the elastic user secret BEFORE deploying ES so ECK uses our fixed password
# instead of generating a random one.
echo "Creating Elasticsearch elastic-user secret with password from ${CONFIG_FILE}..."
kubectl create secret generic elasticsearch-es-elastic-user \
  -n "${ES_NS}" \
  --from-literal=elastic="${ES_PASSWORD}" \
  --dry-run=client -o yaml | kubectl apply -f -

helm upgrade --install "elasticsearch-cluster-${ENV}" ./elasticsearch/elasticsearch-cluster \
  --create-namespace --namespace "${ES_NS}" \
  --set elasticsearch.replicas="$ES_REPLICAS" \
  --set kibana.ingress.hostname="kibana.${HOST_PREFIX}${DOMAIN}" \
  --values "./infra-${ENV}-affinity.yaml"

# --------------------------------------------------------------------------
# Zookeeper
# --------------------------------------------------------------------------
helm upgrade --install "zookeeper-${ENV}" ./zookeeper \
  --namespace "${ZK_NS}" --create-namespace \
  --values "./infra-${ENV}-affinity.yaml"

# ============================================================================
# OBSERVABILITY — Loki + Tempo + Promtail + OpenTelemetry Collector
# ============================================================================
# NOTE: Grafana and Prometheus are NOT deployed by this project.
# Grafana is replaced by direct Loki/Tempo querying or external tools.
# Prometheus kube-prometheus-stack is not used — metrics go through OTel.

# --------------------------------------------------------------------------
# Loki (log aggregation)
# --------------------------------------------------------------------------
helm upgrade --install "loki-${ENV}" grafana/loki \
  --create-namespace --namespace "${OBS_NS}" \
  -f ./observability/loki.values.yaml \
  --set loki.useTestSchema=true \
  --values "./infra-${ENV}-affinity.yaml"

# --------------------------------------------------------------------------
# Tempo (trace storage)
# --------------------------------------------------------------------------
helm upgrade --install "tempo-${ENV}" grafana/tempo \
  --create-namespace --namespace "${OBS_NS}" \
  -f ./observability/tempo.values.yaml \
  --values "./infra-${ENV}-affinity.yaml"

# --------------------------------------------------------------------------
# Promtail (log collector)
# --------------------------------------------------------------------------
helm upgrade --install "promtail-${ENV}" grafana/promtail \
  --create-namespace --namespace "${OBS_NS}" \
  --values ./observability/promtail.values.yaml \
  --values "./infra-${ENV}-affinity.yaml"

# --------------------------------------------------------------------------
# OpenTelemetry Collector
# --------------------------------------------------------------------------
# Wait for the opentelemetry operator webhook to be ready
echo "Waiting for OpenTelemetry operator webhook..."
kubectl wait --for=condition=available deployment/opentelemetry-operator-controller-manager -n observability --timeout=180s 2>/dev/null || sleep 30

helm upgrade --install "opentelemetry-collector-${ENV}" ./observability/opentelemetry \
  --create-namespace --namespace "${OBS_NS}" \
  --set lokiEndpoint="http://loki-${ENV}-gateway.${OBS_NS}.svc.cluster.local/loki/api/v1/push" \
  --set tempoEndpoint="http://tempo-${ENV}.${OBS_NS}.svc.cluster.local:4318" \
  --values "./infra-${ENV}-affinity.yaml"

# ============================================================================
# GRAFANA & PROMETHEUS — NOT DEPLOYED
# ============================================================================
# The project does not use Grafana or Prometheus. The observability stack
# relies on OpenTelemetry Collector → Loki (logs) + Tempo (traces).
# Metrics are exposed by services via /actuator/prometheus but not scraped.
#
# If Grafana is ever needed in the future, uncomment the blocks below.
#
# #Install grafana-operator (disabled)
# #helm upgrade --install grafana-operator oci://ghcr.io/grafana-operator/helm-charts/grafana-operator \
# #--version v5.0.2 \
# #--create-namespace --namespace "${OBS_NS}"
#
# #Install grafana datasources and dashboards (disabled)
# #helm upgrade --install "grafana-${ENV}" ./observability/grafana \
# #--create-namespace --namespace "${OBS_NS}" \
# #--set hostname="grafana.${HOST_PREFIX}${DOMAIN}" \
# #--set grafana.username="admin" \
# #--set grafana.password="admin"
#
# #Install prometheus + grafana stack (disabled)
# #helm upgrade --install prometheus prometheus-community/kube-prometheus-stack \
# #  --create-namespace --namespace "${OBS_NS}" \
# #  -f ./observability/prometheus.values.yaml
# ============================================================================

echo ""
echo "============================================"
echo " Infrastructure deployed for: ${ENV}"
echo " Namespaces: ${PG_NS}, ${KAFKA_NS}, ${ES_NS}, ${OBS_NS}, ${ZK_NS}"
echo "============================================"
