#!/bin/bash
# ============================================================
# VERSION COMPATIBILITY MATRIX
# Kubernetes: v1.28.x  (minikube start --kubernetes-version=v1.28.0)
# Strimzi:    0.35.1   (Kafka 3.4.0, v1beta2 API, ZooKeeper mode)
# Debezium:   ghcr.io/nashtech-garage/debezium-connect-postgresql:latest (Kafka 3.4.0 base)
#
# To start minikube with correct k8s version:
#   minikube start --kubernetes-version=v1.28.0 --disk-size='40000mb' --memory='16g'
# ============================================================
set -x

# Add chart repos and update
helm repo add postgres-operator-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator
helm repo add strimzi https://strimzi.io/charts/
helm repo add akhq https://akhq.io/
helm repo add elastic https://helm.elastic.co
helm repo add grafana https://grafana.github.io/helm-charts
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add open-telemetry https://open-telemetry.github.io/opentelemetry-helm-charts
helm repo add jetstack https://charts.jetstack.io
helm repo update

#Read configuration value from cluster-config.yaml file
read -rd '' DOMAIN POSTGRESQL_REPLICAS POSTGRESQL_USERNAME POSTGRESQL_PASSWORD \
KAFKA_REPLICAS ZOOKEEPER_REPLICAS ELASTICSEARCH_REPLICAES \
GRAFANA_USERNAME GRAFANA_PASSWORD \
< <(yq -r '.domain, .postgresql.replicas, .postgresql.username,
 .postgresql.password, .kafka.replicas, .zookeeper.replicas,
 .elasticsearch.replicas, .grafana.username, .grafana.password' ./cluster-config.yaml)

# Install the postgres-operator
helm upgrade --install postgres-operator postgres-operator-charts/postgres-operator \
 --create-namespace --namespace postgres \
 --version 1.10.1

#Install postgresql
helm upgrade --install postgres ./postgres/postgresql \
--create-namespace --namespace postgres \
--set replicas="$POSTGRESQL_REPLICAS" \
--set username="$POSTGRESQL_USERNAME" \
--set password="$POSTGRESQL_PASSWORD"

#Install pgadmin
pg_admin_hostname="pgadmin.$DOMAIN" yq -i '.hostname=env(pg_admin_hostname)' ./postgres/pgadmin/values.yaml
helm upgrade --install pgadmin ./postgres/pgadmin \
--create-namespace --namespace postgres \

#Install strimzi-kafka-operator
# Pin to 0.35.x to match debezium-connect-postgresql image (built on Kafka 3.4.0 / Strimzi 0.35.x)
# Upgrading Strimzi beyond 0.35.x will break the nashtech-garage debezium image
helm upgrade --install kafka-operator strimzi/strimzi-kafka-operator \
--create-namespace --namespace kafka \
--version 0.35.1

# Wait for Strimzi operator pod to be ready
kubectl wait --for=condition=ready pod \
  -l name=strimzi-cluster-operator \
  -n kafka \
  --timeout=120s

# Wait for Strimzi CRDs to be fully established in the API server
# This is required before Helm can resolve the kafka.strimzi.io API group
kubectl wait --for=condition=established crd/kafkas.kafka.strimzi.io --timeout=120s
kubectl wait --for=condition=established crd/kafkaconnects.kafka.strimzi.io --timeout=120s
kubectl wait --for=condition=established crd/kafkaconnectors.kafka.strimzi.io --timeout=120s

# Clear Helm API discovery cache so it picks up newly installed Strimzi CRDs
rm -rf ~/.kube/cache/discovery/

#Install kafka and postgresql connector
helm upgrade --install kafka-cluster ./kafka/kafka-cluster \
--create-namespace --namespace kafka \
--set kafka.replicas="$KAFKA_REPLICAS" \
--set zookeeper.replicas="$ZOOKEEPER_REPLICAS" \
--set postgresql.username="$POSTGRESQL_USERNAME" \
--set postgresql.password="$POSTGRESQL_PASSWORD"

#Install akhq
akhq_hostname="akhq.$DOMAIN" yq -i '.hostname=env(akhq_hostname)' ./kafka/akhq.values.yaml
helm upgrade --install akhq akhq/akhq \
--create-namespace --namespace kafka \
--version 0.24.0 \
--values ./kafka/akhq.values.yaml

#Install elastic-operator
helm upgrade --install elastic-operator elastic/eck-operator \
 --create-namespace --namespace elasticsearch \
 --version 3.4.0

# Wait for ECK operator and CRDs before installing elasticsearch-cluster
kubectl wait --for=condition=ready pod \
  -l control-plane=elastic-operator \
  -n elasticsearch --timeout=120s
kubectl wait --for=condition=established crd/elasticsearches.elasticsearch.k8s.elastic.co --timeout=120s

# Install elasticsearch-cluster
helm upgrade --install elasticsearch-cluster ./elasticsearch/elasticsearch-cluster \
--create-namespace --namespace elasticsearch \
--set elasticsearch.replicas="$ELASTICSEARCH_REPLICAES" \
--set kibana.ingress.hostname="kibana.$DOMAIN"

#Install loki
helm upgrade --install loki grafana/loki \
 --create-namespace --namespace observability \
 --version 5.41.6 \
 -f ./observability/loki.values.yaml

#Install tempo
helm upgrade --install tempo grafana/tempo \
--create-namespace --namespace observability \
--version 1.7.2 \
-f ./observability/tempo.values.yaml

#Install cert manager
helm upgrade --install cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --version v1.12.0 \
  --set installCRDs=true \
  --set prometheus.enabled=false \
  --set webhook.timeoutSeconds=4 \
  --set admissionWebhooks.certManager.create=true

#Install opentelemetry-operator
helm upgrade --install opentelemetry-operator open-telemetry/opentelemetry-operator \
--create-namespace --namespace observability \
--version 0.43.1

# Wait for OpenTelemetry CRDs before installing collector
kubectl wait --for=condition=established crd/opentelemetrycollectors.opentelemetry.io --timeout=120s

# Wait for opentelemetry-operator webhook to be ready
kubectl wait --for=condition=ready pod \
  -l app.kubernetes.io/name=opentelemetry-operator \
  -n observability --timeout=120s

#Install opentelemetry-collector
helm upgrade --install opentelemetry-collector ./observability/opentelemetry \
--create-namespace --namespace observability

#Install promtail
helm upgrade --install promtail grafana/promtail \
--create-namespace --namespace observability \
--version 6.15.5 \
--values ./observability/promtail.values.yaml

#Install prometheus + grafana
grafana_hostname="grafana.$DOMAIN" yq -i '.hostname=env(grafana_hostname)' ./observability/prometheus.values.yaml
postgresql_username="$POSTGRESQL_USERNAME" yq -i '.grafana."grafana.ini".database.user=env(postgresql_username)' ./observability/prometheus.values.yaml
postgresql_password="$POSTGRESQL_PASSWORD" yq -i '.grafana."grafana.ini".database.password=env(postgresql_password)' ./observability/prometheus.values.yaml
helm upgrade --install prometheus prometheus-community/kube-prometheus-stack \
 --create-namespace --namespace observability \
--version 55.5.2 \
-f ./observability/prometheus.values.yaml \

#Install grafana operator
helm upgrade --install grafana-operator oci://ghcr.io/grafana-operator/helm-charts/grafana-operator \
--version v5.0.2 \
--create-namespace --namespace observability

#Add datasource and dashboard to grafana
helm upgrade --install grafana ./observability/grafana \
--create-namespace --namespace observability \
--set hotname="grafana.$DOMAIN" \
--set grafana.username="$GRAFANA_USERNAME" \
--set grafana.password="$GRAFANA_PASSWORD" \
--set postgresql.username="$POSTGRESQL_USERNAME" \
--set postgresql.password="$POSTGRESQL_PASSWORD"

helm upgrade --install zookeeper ./zookeeper \
 --namespace zookeeper --create-namespace

# Wait for ingress-nginx controller to be ready
# (ingress addon is enabled manually via `minikube addons enable ingress` before running this script)
kubectl wait --for=condition=ready pod \
  -l app.kubernetes.io/component=controller \
  -n ingress-nginx --timeout=120s

# Patch CoreDNS to resolve cluster-external domains inside pods.
# Pods cannot read host /etc/hosts, so api/backoffice/storefront.yas.local.com
# must be mapped to the ingress-nginx ClusterIP via CoreDNS.
NGINX_IP=$(kubectl get svc ingress-nginx-controller -n ingress-nginx -o jsonpath='{.spec.clusterIP}')
CURRENT_COREFILE=$(kubectl get configmap coredns -n kube-system -o jsonpath='{.data.Corefile}')
if ! echo "$CURRENT_COREFILE" | grep -q "api.$DOMAIN"; then
  export NGINX_IP DOMAIN
  kubectl get configmap coredns -n kube-system -o json | \
    python3 -c "
import sys, json, os
cm = json.load(sys.stdin)
corefile = cm['data']['Corefile']
nginx_ip = os.environ['NGINX_IP']
domain = os.environ['DOMAIN']
entries  = '       ' + nginx_ip + ' api.' + domain + '\n'
entries += '       ' + nginx_ip + ' backoffice.' + domain + '\n'
entries += '       ' + nginx_ip + ' storefront.' + domain + '\n'
corefile = corefile.replace('       fallthrough\n    }', entries + '       fallthrough\n    }', 1)
cm['data']['Corefile'] = corefile
print(json.dumps(cm))
" | kubectl apply -f -
  kubectl rollout restart deployment coredns -n kube-system
  kubectl rollout status deployment coredns -n kube-system --timeout=60s
fi