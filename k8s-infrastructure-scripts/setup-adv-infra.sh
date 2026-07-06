#!/bin/bash
set -e

echo "--- SETUP ADVANCED INFRASTRUCTURE (GITOPS) WITH ISTIO SERVICE MESH ---"
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

echo "2. Preparing Namespaces & Installing Istio Service Mesh..."
kubectl create namespace dev --dry-run=client -o yaml | kubectl apply -f -
kubectl create namespace staging --dry-run=client -o yaml | kubectl apply -f -

echo "Installing Istio profile=demo..."
if ! command -v istioctl &> /dev/null
then
    echo "istioctl not found, installing..."
    curl -L https://istio.io/downloadIstio | sh -
    sudo mv istio-*/bin/istioctl /usr/local/bin/
    rm -rf istio-*
fi
istioctl install --set profile=demo -y

echo "Enabling Istio Injection for 'dev' and 'staging' namespaces..."
kubectl label namespace dev istio-injection=enabled --overwrite
kubectl label namespace staging istio-injection=enabled --overwrite

echo "Installing Kiali and Prometheus..."
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.26/samples/addons/prometheus.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.26/samples/addons/kiali.yaml

echo "3. Installing PostgreSQL (Shared)..."
helm repo add postgres-operator-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator
helm repo update
helm upgrade --install postgres-operator postgres-operator-charts/postgres-operator --namespace postgres --create-namespace
helm upgrade --install postgres ../k8s/deploy/postgres/postgresql --namespace postgres --create-namespace --set auth.postgresPassword=admin

echo "4. Installing Redis (Shared)..."
helm upgrade --install redis oci://registry-1.docker.io/bitnamicharts/redis -n redis --create-namespace --set auth.password=redis

echo "5. Installing Kafka (Shared)..."
helm repo add strimzi https://strimzi.io/charts/
helm repo update
helm upgrade --install kafka-operator strimzi/strimzi-kafka-operator --version 0.38.0 --namespace kafka --create-namespace
helm upgrade --install kafka-cluster ../k8s/deploy/kafka/kafka-cluster --namespace kafka --set kafka.replicas=1 --set zookeeper.replicas=1 --set postgresql.username=yasadminuser --set postgresql.password=admin

echo "6. Installing Elasticsearch (Shared)..."
helm repo add elastic https://helm.elastic.co
helm repo update
helm upgrade --install elastic-operator elastic/eck-operator --namespace elasticsearch --create-namespace
helm upgrade --install elasticsearch-cluster ../k8s/deploy/elasticsearch/elasticsearch-cluster --namespace elasticsearch --set elasticsearch.replicas=1 --set kibana.ingress.hostname=kibana.yas.local.com

echo "7. Installing Keycloak CRDs (Shared)..."
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml

echo "8. Installing yas-configuration & Keycloak for DEV environment..."
helm upgrade --install yas-configuration ../k8s/charts/yas-configuration --namespace dev --create-namespace
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/kubernetes.yml -n dev
helm upgrade --install keycloak ../k8s/deploy/keycloak/keycloak --namespace dev --create-namespace

echo "9. Installing yas-configuration & Keycloak for STAGING environment..."
helm upgrade --install yas-configuration ../k8s/charts/yas-configuration --namespace staging --create-namespace
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/kubernetes.yml -n staging
helm upgrade --install keycloak ../k8s/deploy/keycloak/keycloak --namespace staging --create-namespace

echo "10. Naming and labeling complete. Istio Policies will be automatically deployed by ArgoCD (yas-gitops)!"

echo "DONE! Wait for all Pods to be running (kubectl get pods -A), then execute the ArgoCD setup script."
