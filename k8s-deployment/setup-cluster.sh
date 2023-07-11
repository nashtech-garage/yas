#!/bin/bash
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
KAFKA_REPLICAS ZOOKEEPER_REPLICAS ELASTICSEARCH_REPLICAES KEYCLOAK_REALM_BACKOFFICE_URL KEYCLOAK_REALM_STOREFRONT_URL \
GRAFANA_USERNAME GRAFANA_PASSWORD \
< <(yq -r '.domain, .postgresql.replicas, .postgresql.username,
 .postgresql.password, .kafka.replicas, .zookeeper.replicas,
 .elasticsearch.replicas, .keycloakRealm.backofficeUrl,
 .keycloakRealm.storefrontUrl, .grafana.username, .grafana.password' ./cluster-config.yaml)

# Install the postgres-operator
helm upgrade --install postgres-operator postgres-operator-charts/postgres-operator \
 --create-namespace --namespace postgres

#Install postgresql
helm upgrade --install postgres ./postgres/postgresql \
--create-namespace --namespace postgres \
--set replicas="$POSTGRESQL_REPLICAS" \
--set username="$POSTGRESQL_USERNAME" \
--set password="$POSTGRESQL_PASSWORD"

#Install pgadmin
hostname="pgadmin.$DOMAIN" yq -i '.hostname=env(hostname)' ./postgres/pgadmin/values.yaml
helm upgrade --install pgadmin ./postgres/pgadmin \
--create-namespace --namespace postgres \

#Install strimzi-kafka-operator
helm upgrade --install kafka-operator strimzi/strimzi-kafka-operator \
--create-namespace --namespace kafka

#Install kafka and postgresql connector
helm upgrade --install kafka-cluster ./kafka/kafka-cluster \
--create-namespace --namespace kafka \
--set kafka.replicas="$KAFKA_REPLICAS" \
--set zookeeper.replicas="$ZOOKEEPER_REPLICAS" \
--set postgresql.username="$POSTGRESQL_USERNAME" \
--set postgresql.password="$POSTGRESQL_PASSWORD"

#Install akhq
hostname="akhq.$DOMAIN" yq -i '.hostname=env(hostname)' ./kafka/akhq.values.yaml
helm upgrade --install akhq akhq/akhq \
--create-namespace --namespace kafka \
--values ./kafka/akhq.values.yaml

#Install elastic-operator
helm upgrade --install elastic-operator elastic/eck-operator \
 --create-namespace --namespace elasticsearch

# Install elasticsearch-cluster
helm upgrade --install elasticsearch-cluster ./elasticsearch/elasticsearch-cluster \
--create-namespace --namespace elasticsearch \
--set elasticsearch.replicas="$ELASTICSEARCH_REPLICAES" \
--set kibana.ingress.hostname="kibana.$DOMAIN"

#Install CRD keycloak
kubectl create namespace keycloak
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/21.1.2/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/21.1.2/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/21.1.2/kubernetes/kubernetes.yml -n keycloak

#Install keycloak
helm upgrade --install keycloak ./keycloak/keycloak \
--namespace keycloak \
--set postgresql.username="$POSTGRESQL_USERNAME" \
--set postgresql.password="$POSTGRESQL_PASSWORD" \
--set hostname="identity.$DOMAIN" \
--set backofficeUrl="$KEYCLOAK_REALM_BACKOFFICE_URL" \
--set storefrontUrl="$KEYCLOAK_REALM_STOREFRONT_URL"

#Install loki
helm upgrade --install loki grafana/loki \
 --create-namespace --namespace observability \
 -f ./observability/loki.values.yaml

#Install tempo
helm upgrade --install tempo grafana/tempo \
--create-namespace --namespace observability \
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
--create-namespace --namespace observability

#Install opentelemetry-collector
helm upgrade --install opentelemetry-collector ./observability/opentelemetry \
--create-namespace --namespace observability

#Install promtail
helm upgrade --install promtail grafana/promtail \
--create-namespace --namespace observability \
--values ./observability/promtail.values.yaml

#Install prometheus + grafana
hostname="grafana.$DOMAIN" yq -i '.hostname=env(hostname)' ./observability/prometheus.values.yaml
postgresqlUsername="$POSTGRESQL_USERNAME" yq -i '.grafana."grafana.ini".database.user=env(postgresqlUsername)' ./observability/prometheus.values.yaml
postgresqlPassword="$POSTGRESQL_PASSWORD" yq -i '.grafana."grafana.ini".database.password=env(postgresqlPassword)' ./observability/prometheus.values.yaml
helm upgrade --install prometheus prometheus-community/kube-prometheus-stack \
 --create-namespace --namespace observability \
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