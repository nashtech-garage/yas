Write-Host "--- CÀI ĐẶT HẠ TẦNG PHẦN CƠ BẢN ---"
minikube addons enable ingress

Write-Host "1. Cấu hình CoreDNS..."
$MINIKUBE_IP = minikube ip
$COREDNS_CLEAN = @"
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
"@
$COREDNS_CLEAN | Out-File -Encoding utf8 "$env:TEMP\coredns-patch.yaml"
kubectl apply -f "$env:TEMP\coredns-patch.yaml"
kubectl rollout restart deployment coredns -n kube-system

Write-Host "2. Cài đặt yas-configuration (Namespace: yas)..."
helm upgrade --install yas-configuration ../k8s/charts/yas-configuration --namespace yas --create-namespace

Write-Host "3. Cài đặt PostgreSQL (Dùng chung)..."
helm repo add postgres-operator-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator
helm upgrade --install postgres-operator postgres-operator-charts/postgres-operator --namespace postgres --create-namespace
helm upgrade --install postgres ../k8s/deploy/postgres/postgresql --namespace postgres --create-namespace --set auth.postgresPassword=admin

Write-Host "4. Cài đặt Keycloak (Namespace: yas)..."
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/kubernetes.yml -n yas
helm upgrade --install keycloak ../k8s/deploy/keycloak/keycloak --namespace yas --create-namespace

Write-Host "5. Cài đặt Redis (Dùng chung)..."
helm install redis oci://registry-1.docker.io/bitnamicharts/redis -n redis --create-namespace --set auth.password=redis

Write-Host "6. Cài đặt Kafka (Dùng chung)..."
helm repo add strimzi https://strimzi.io/charts/
helm upgrade --install kafka-operator strimzi/strimzi-kafka-operator --version 0.38.0 --namespace kafka --create-namespace
helm upgrade --install kafka-cluster ../k8s/deploy/kafka/kafka-cluster --namespace kafka --set kafka.replicas=1 --set zookeeper.replicas=1 --set postgresql.username=yasadminuser --set postgresql.password=admin

Write-Host "7. Cài đặt Elasticsearch (Dùng chung)..."
helm repo add elastic https://helm.elastic.co
helm upgrade --install elastic-operator elastic/eck-operator --namespace elasticsearch --create-namespace
helm upgrade --install elasticsearch-cluster ../k8s/deploy/elasticsearch/elasticsearch-cluster --namespace elasticsearch --set elasticsearch.replicas=1 --set kibana.ingress.hostname=kibana.yas.local.com

Write-Host "HOÀN TẤT! Đợi các Pod chạy xong (kubectl get pods -A) rồi bạn có thể dùng Github Actions luồng developer-build."
