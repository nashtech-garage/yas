#!/bin/bash
set -x

# Add chart repos and update
helm repo add postgres-operator-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator
helm repo add postgres-operator-ui-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator-ui
helm repo add strimzi https://strimzi.io/charts/
helm repo add akhq https://akhq.io/
helm repo add elastic https://helm.elastic.co
helm repo update

#Read configuration value from cluster-config.yaml file
read -rd '' DOMAIN POSTGRESQL_REPLICAS POSTGRESQL_USERNAME POSTGRESQL_PASSWORD \
KAFKA_REPLICAS ZOOKEEPER_REPLICAS ELASTICSEARCH_REPLICAES KEYCLOAK_REALM_BACKOFFICE_URL KEYCLOAK_REALM_STOREFRONT_URL  \
< <(yq -r '.domain, .postgresql.replicas, .postgresql.username,
 .postgresql.password, .kafka.replicas, .zookeeper.replicas,
 .elasticsearch.replicas, .keycloakRealm.backofficeUrl, .keycloakRealm.storefrontUrl' ./cluster-config.yaml)

# Install the postgres-operator
helm upgrade --install postgres-operator postgres-operator-charts/postgres-operator \
 --create-namespace --namespace postgres

#Install the postgres-operator-ui
hostname="pgoperator.$DOMAIN" yq -i '.hostname=env(hostname)' ./postgres/postgres-operator-ui.values.yaml
helm upgrade --install postgres-operator-ui postgres-operator-ui-charts/postgres-operator-ui \
 --create-namespace --namespace postgres \
 --set envs.appUrl=http://"pgoperator.$DOMAIN" \
 --values ./postgres/postgres-operator-ui.values.yaml

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