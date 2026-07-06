#!/bin/bash
set -e

echo "--- SETUP BASIC INFRASTRUCTURE ---"
minikube addons enable ingress

echo "1. Configuring CoreDNS..."
MINIKUBE_IP=$(minikube ip)
cat <<EOF > /tmp/coredns-patch.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: coredns
  namespace: kube-system
data:
  Corefile: |
    .:53 {
        errors
        health {
           lameduck 5s
        }
        ready
        hosts {
           $MINIKUBE_IP identity.yas.local.com backoffice.yas.local.com storefront.yas.local.com identity.dev.local.com backoffice.dev.local.com storefront.dev.local.com identity.staging.local.com backoffice.staging.local.com storefront.staging.local.com
           fallthrough
        }
        kubernetes cluster.local in-addr.arpa ip6.arpa {
           pods insecure
           fallthrough in-addr.arpa ip6.arpa
           ttl 30
        }
        prometheus :9153
        forward . /etc/resolv.conf {
           max_concurrent 1000
        }
        cache 30
        loop
        reload
        loadbalance
    }
EOF
kubectl apply -f /tmp/coredns-patch.yaml
kubectl rollout restart deployment coredns -n kube-system

echo "2. Installing yas-configuration (Namespace: yas)..."
helm repo add stakater https://stakater.github.io/stakater-charts
helm repo update
helm dependency build ../k8s/charts/yas-configuration
helm upgrade --install yas-configuration ../k8s/charts/yas-configuration --namespace yas --create-namespace

echo "3. Installing PostgreSQL (Shared)..."
helm repo add postgres-operator-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator
helm repo update
helm upgrade --install postgres-operator postgres-operator-charts/postgres-operator --namespace postgres --create-namespace
helm upgrade --install postgres ../k8s/deploy/postgres/postgresql --namespace postgres --create-namespace --set auth.postgresPassword=admin

echo "4. Installing Keycloak (Namespace: yas)..."
kubectl apply -f https://cdn.jsdelivr.net/gh/keycloak/keycloak-k8s-resources@26.0.2/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://cdn.jsdelivr.net/gh/keycloak/keycloak-k8s-resources@26.0.2/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
kubectl apply -f https://cdn.jsdelivr.net/gh/keycloak/keycloak-k8s-resources@26.0.2/kubernetes/kubernetes.yml -n yas
helm upgrade --install keycloak ../k8s/deploy/keycloak/keycloak --namespace yas --create-namespace

echo "5. Installing Redis (Shared)..."
helm upgrade --install redis oci://registry-1.docker.io/bitnamicharts/redis -n redis --create-namespace --set auth.password=redis

echo "6. Installing Kafka (Shared)..."
helm repo add strimzi https://strimzi.io/charts/
helm repo update
helm upgrade --install kafka-operator strimzi/strimzi-kafka-operator --version 0.38.0 --namespace kafka --create-namespace
helm upgrade --install kafka-cluster ../k8s/deploy/kafka/kafka-cluster --namespace kafka --set kafka.replicas=1 --set zookeeper.replicas=1 --set postgresql.username=yasadminuser --set postgresql.password=admin

echo "7. Installing Elasticsearch (Shared)..."
helm repo add elastic https://helm.elastic.co
helm repo update
helm upgrade --install elastic-operator elastic/eck-operator --namespace elasticsearch --create-namespace
helm upgrade --install elasticsearch-cluster ../k8s/deploy/elasticsearch/elasticsearch-cluster --namespace elasticsearch --set elasticsearch.replicas=1 --set kibana.ingress.hostname=kibana.yas.local.com

echo "DONE! Wait for all Pods to be running (kubectl get pods -A), then you can run the developer-build Github Actions workflow."
