# 🚀 YAS Kubernetes Setup Script for Windows Native PowerShell
# This script deploys all the infrastructure, databases, and configuration for the YAS E-Commerce platform on k3d.

# 1. Reload Environment Path to ensure newly installed tools (k3d, helm, yq) are available
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

Write-Host "==========================================================" -ForegroundColor Green
Write-Host "Starting YAS Kubernetes Infrastructure Setup..." -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green

# 2. Check connections
Write-Host "Checking connection to K8s cluster..." -ForegroundColor Cyan
kubectl config set-cluster k3d-yas-cluster --server=https://127.0.0.1:6550
$nodes = kubectl get nodes 2>$null
if (-not $nodes) {
    Write-Error "Could not connect to the Kubernetes cluster. Please make sure Docker Desktop is running."
    exit 1
}
Write-Host "Connected successfully to nodes:" -ForegroundColor Green
$nodes | Out-String | Write-Host

# 3. Create Namespaces and enable Istio Sidecar Injection
Write-Host "Creating namespaces and enabling Istio injection..." -ForegroundColor Cyan
kubectl apply -f k8s/namespaces.yaml

# 4. Load Configurations from cluster-config.yaml
Write-Host "Loading configurations from cluster-config.yaml..." -ForegroundColor Cyan
$configPath = "k8s/deploy/cluster-config.yaml"
if (-not (Test-Path $configPath)) {
    Write-Error "Could not find cluster-config.yaml at $configPath"
    exit 1
}

$config = yq -o=json . $configPath | ConvertFrom-Json
$DOMAIN = $config.domain
$POSTGRESQL_REPLICAS = $config.postgresql.replicas
$POSTGRESQL_USERNAME = $config.postgresql.username
$POSTGRESQL_PASSWORD = $config.postgresql.password
$KAFKA_REPLICAS = $config.kafka.replicas
$ZOOKEEPER_REPLICAS = $config.zookeeper.replicas
$ELASTICSEARCH_REPLICAS = $config.elasticsearch.replicas
$GRAFANA_USERNAME = $config.grafana.username
$GRAFANA_PASSWORD = $config.grafana.password
$REDIS_PASSWORD = $config.redis.password
$KEYCLOAK_ADMIN_USER = $config.keycloak.bootstrapAdmin.username
$KEYCLOAK_ADMIN_PASS = $config.keycloak.bootstrapAdmin.password
$KEYCLOAK_BO_REDIRECT = $config.keycloak.backofficeRedirectUrl
$KEYCLOAK_SF_REDIRECT = $config.keycloak.storefrontRedirectUrl

Write-Host "Configuration loaded successfully for Domain: $DOMAIN" -ForegroundColor Green

# 5. Add Helm Repositories
Write-Host "Updating Helm charts repositories..." -ForegroundColor Cyan
helm repo add postgres-operator-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator
helm repo add strimzi https://strimzi.io/charts/
helm repo add akhq https://akhq.io/
helm repo add elastic https://helm.elastic.co
helm repo add grafana https://grafana.github.io/helm-charts
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add open-telemetry https://open-telemetry.github.io/opentelemetry-helm-charts
helm repo add jetstack https://charts.jetstack.io
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add stakater https://stakater.github.io/stakater-charts
helm repo update

# 6. Install Redis
Write-Host "Deploying Redis..." -ForegroundColor Cyan
helm upgrade --install redis bitnami/redis `
  --create-namespace --namespace redis `
  --set auth.password="$REDIS_PASSWORD"

# 7. Install Keycloak Operator and Keycloak
Write-Host "Deploying Keycloak Operator..." -ForegroundColor Cyan
kubectl create namespace keycloak 2>$null
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/kubernetes.yml -n keycloak

Write-Host "Deploying Keycloak Instance..." -ForegroundColor Cyan
helm upgrade --install keycloak k8s/deploy/keycloak/keycloak `
  --namespace keycloak `
  --set hostname="identity.$DOMAIN" `
  --set postgresql.username="$POSTGRESQL_USERNAME" `
  --set postgresql.password="$POSTGRESQL_PASSWORD" `
  --set bootstrapAdmin.username="$KEYCLOAK_ADMIN_USER" `
  --set bootstrapAdmin.password="$KEYCLOAK_ADMIN_PASS" `
  --set backofficeRedirectUrl="$KEYCLOAK_BO_REDIRECT" `
  --set storefrontRedirectUrl="$KEYCLOAK_SF_REDIRECT"

# 8. Install Postgres Operator and PostgreSQL Cluster
Write-Host "Deploying Postgres Operator..." -ForegroundColor Cyan
helm upgrade --install postgres-operator postgres-operator-charts/postgres-operator `
  --create-namespace --namespace postgres

Write-Host "⏳ Waiting 15s for Postgres Operator to initialize..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

Write-Host "Deploying PostgreSQL database cluster..." -ForegroundColor Cyan
helm upgrade --install postgres k8s/deploy/postgres/postgresql `
  --create-namespace --namespace postgres `
  --set replicas="$POSTGRESQL_REPLICAS" `
  --set username="$POSTGRESQL_USERNAME" `
  --set password="$POSTGRESQL_PASSWORD"

# 9. Deploy pgAdmin
Write-Host "Deploying pgAdmin..." -ForegroundColor Cyan
yq -i ".hostname = \"pgadmin.$DOMAIN\"" k8s/deploy/postgres/pgadmin/values.yaml
helm upgrade --install pgadmin k8s/deploy/postgres/pgadmin `
  --create-namespace --namespace postgres

# 10. Install Strimzi Kafka Operator and Kafka Cluster
Write-Host "Deploying Strimzi Kafka Operator..." -ForegroundColor Cyan
helm upgrade --install kafka-operator strimzi/strimzi-kafka-operator `
  --create-namespace --namespace kafka

Write-Host "⏳ Waiting 15s for Kafka Operator to initialize..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

Write-Host "Deploying Kafka Cluster..." -ForegroundColor Cyan
helm upgrade --install kafka-cluster k8s/deploy/kafka/kafka-cluster `
  --create-namespace --namespace kafka `
  --set kafka.replicas="$KAFKA_REPLICAS" `
  --set zookeeper.replicas="$ZOOKEEPER_REPLICAS" `
  --set postgresql.username="$POSTGRESQL_USERNAME" `
  --set postgresql.password="$POSTGRESQL_PASSWORD"

# 11. Deploy AKHQ
Write-Host "Deploying AKHQ..." -ForegroundColor Cyan
yq -i ".hostname = \"akhq.$DOMAIN\"" k8s/deploy/kafka/akhq.values.yaml
helm upgrade --install akhq akhq/akhq `
  --create-namespace --namespace kafka `
  --values k8s/deploy/kafka/akhq.values.yaml

# 12. Install Elastic Operator and Elasticsearch
Write-Host "Deploying ECK (Elastic Cloud on Kubernetes) Operator..." -ForegroundColor Cyan
helm upgrade --install elastic-operator elastic/eck-operator `
  --create-namespace --namespace elasticsearch

Write-Host "⏳ Waiting 15s for Elastic Operator to initialize..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

Write-Host "Deploying Elasticsearch Cluster..." -ForegroundColor Cyan
helm upgrade --install elasticsearch-cluster k8s/deploy/elasticsearch/elasticsearch-cluster `
  --create-namespace --namespace elasticsearch `
  --set elasticsearch.replicas="$ELASTICSEARCH_REPLICAS" `
  --set kibana.ingress.hostname="kibana.$DOMAIN"

# 13. Deploy Cert-Manager
Write-Host "Deploying Cert-Manager..." -ForegroundColor Cyan
helm upgrade --install cert-manager jetstack/cert-manager `
  --namespace cert-manager `
  --create-namespace `
  --version v1.12.0 `
  --set installCRDs=true `
  --set prometheus.enabled=false `
  --set webhook.timeoutSeconds=4 `
  --set admissionWebhooks.certManager.create=true

# 14. Deploy ZooKeeper
Write-Host "Deploying ZooKeeper..." -ForegroundColor Cyan
helm upgrade --install zookeeper k8s/deploy/zookeeper `
  --namespace zookeeper --create-namespace

Write-Host "==========================================================" -ForegroundColor Green
Write-Host "🎉 Infrastructure Setup Complete!" -ForegroundColor Green
Write-Host "All databases, operators, and middleware have been deployed." -ForegroundColor Green
Write-Host "You can monitor the status using: kubectl get pods -A" -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Green
