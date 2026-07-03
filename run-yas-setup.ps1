# 🚀 YAS Kubernetes Setup Script for Windows Native PowerShell
# This script deploys all the infrastructure, databases, and configuration for the YAS E-Commerce platform on k3d.

# 1. Reload Environment Path to ensure newly installed tools (k3d, helm, yq) are available
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

Write-Host "==========================================================" -ForegroundColor Green
Write-Host "Starting YAS Kubernetes Infrastructure Setup..." -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green

# 2. Ensure k3d cluster exists, create if not
Write-Host "Checking k3d cluster..." -ForegroundColor Cyan
$clusterExists = k3d cluster list 2>$null | Select-String "yas-cluster"
if (-not $clusterExists) {
    Write-Host "Cluster 'yas-cluster' not found. Creating 1 server + 3 agents cluster..." -ForegroundColor Yellow
    k3d cluster create yas-cluster `
      --servers 1 `
      --agents 3 `
      --api-port 6550 `
      --image rancher/k3s:v1.31.4-k3s1 `
      -p "30080:30080@server:0" `
      -p "30081:30081@server:0" `
      -p "30082:30082@server:0" `
      -p "30084:30084@server:0" `
      -p "30085:30085@server:0" `
      -p "30086:30086@server:0" `
      -p "30088:30088@server:0" `
      -p "30089:30089@server:0"
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to create k3d cluster. Make sure Docker Desktop is running."
        exit 1
    }
    Write-Host "Cluster created successfully." -ForegroundColor Green
} else {
    Write-Host "Cluster 'yas-cluster' already exists." -ForegroundColor Green
}

# Merge kubeconfig so kubectl uses the correct dynamic API port
k3d kubeconfig merge yas-cluster --kubeconfig-merge-default 2>$null

# Fix: host.docker.internal may resolve to LAN IP instead of 127.0.0.1 on some Windows setups
$kubeconfigPath = "$env:USERPROFILE\.kube\config"
(Get-Content $kubeconfigPath) -replace 'https://host\.docker\.internal:6550', 'https://127.0.0.1:6550' | Set-Content $kubeconfigPath

# Label nodes to mirror 4-laptop topology (laptop-a=master, laptop-b/c/d=workers)
# This ensures Helm charts tested here will work identically on real laptops
Write-Host "Labeling nodes to mirror 4-laptop topology..." -ForegroundColor Cyan
Start-Sleep -Seconds 5
kubectl label node k3d-yas-cluster-server-0 node-role=laptop-a --overwrite 2>$null
kubectl label node k3d-yas-cluster-agent-0  node-role=laptop-b --overwrite 2>$null
kubectl label node k3d-yas-cluster-agent-1  node-role=laptop-c --overwrite 2>$null
kubectl label node k3d-yas-cluster-agent-2  node-role=laptop-d --overwrite 2>$null
Write-Host "Node labels applied." -ForegroundColor Green

Write-Host "Checking connection to K8s cluster..." -ForegroundColor Cyan
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
# Use --set to pass hostname directly, avoiding yq PowerShell quoting issues
helm upgrade --install pgadmin k8s/deploy/postgres/pgadmin `
  --create-namespace --namespace postgres `
  --set hostname="pgadmin.$DOMAIN"

# 10. Install Strimzi Kafka Operator and Kafka Cluster
Write-Host "Deploying Strimzi Kafka Operator..." -ForegroundColor Cyan
helm upgrade --install kafka-operator strimzi/strimzi-kafka-operator `
  --create-namespace --namespace kafka --version 0.43.0

Write-Host "⏳ Waiting for Strimzi Kafka CRDs to be registered..." -ForegroundColor Yellow
$kafkaCrdReady = $false
for ($i = 0; $i -lt 24; $i++) {
    $crd = kubectl get crd kafkas.kafka.strimzi.io 2>$null
    if ($crd) { $kafkaCrdReady = $true; break }
    Write-Host "  CRDs not ready yet, retrying ($([int]($i+1)*5)s)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 5
}
if (-not $kafkaCrdReady) { Write-Warning "Kafka CRDs not found after 2 minutes, proceeding anyway..." }

Write-Host "Deploying Kafka Cluster..." -ForegroundColor Cyan
helm upgrade --install kafka-cluster k8s/deploy/kafka/kafka-cluster `
  --create-namespace --namespace kafka `
  --set kafka.replicas="$KAFKA_REPLICAS" `
  --set zookeeper.replicas="$ZOOKEEPER_REPLICAS" `
  --set postgresql.username="$POSTGRESQL_USERNAME" `
  --set postgresql.password="$POSTGRESQL_PASSWORD"

# 11. Deploy AKHQ
Write-Host "Deploying AKHQ..." -ForegroundColor Cyan
# Use --set to pass hostname directly, avoiding yq PowerShell quoting issues
helm upgrade --install akhq akhq/akhq `
  --create-namespace --namespace kafka `
  --values k8s/deploy/kafka/akhq.values.yaml `
  --set ingress.host="akhq.$DOMAIN"

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

# 15. Install ArgoCD
Write-Host "Installing ArgoCD..." -ForegroundColor Cyan
kubectl create namespace argocd 2>$null | Out-Null
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/v2.12.0/manifests/install.yaml

Write-Host "  Waiting for ArgoCD server to be ready (up to 3 minutes)..." -ForegroundColor Yellow
kubectl wait --for=condition=available deployment/argocd-server -n argocd --timeout=180s 2>$null | Out-Null

# Patch ArgoCD server to NodePort 30088 (same port as k3d port-mapping)
kubectl apply -f k8s/deploy/argocd/argocd-nodeport-patch.yaml

# Disable TLS on ArgoCD server (using HTTP for local dev — no cert needed)
kubectl patch deployment argocd-server -n argocd `
    --type json `
    -p='[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--insecure"}]' 2>$null | Out-Null

# Get initial admin password
$argocdPass = kubectl -n argocd get secret argocd-initial-admin-secret `
    -o jsonpath="{.data.password}" 2>$null |
    ForEach-Object { [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($_)) }
Write-Host "  ArgoCD ready!" -ForegroundColor Green
Write-Host "  URL     : http://localhost:30088" -ForegroundColor Cyan
Write-Host "  Username: admin" -ForegroundColor Cyan
Write-Host "  Password: $argocdPass" -ForegroundColor Cyan

# Bootstrap ArgoCD apps (dev + staging)
Write-Host "  Bootstrapping ArgoCD applications..." -ForegroundColor Yellow
kubectl apply -f argocd/yas-dev-bootstrap.yaml
kubectl apply -f argocd/yas-staging-bootstrap.yaml
Write-Host "  ArgoCD apps bootstrapped (syncing from main branch)" -ForegroundColor Green

# 16. Install Istio
Write-Host "" 
Write-Host "Installing Istio Service Mesh..." -ForegroundColor Cyan

# Download istioctl if not present
if (-not (Get-Command istioctl -ErrorAction SilentlyContinue)) {
    Write-Host "  Downloading istioctl..." -ForegroundColor Yellow
    $istioctlVersion = "1.23.0"
    $istioctlUrl = "https://github.com/istio/istio/releases/download/$istioctlVersion/istioctl-$istioctlVersion-win.zip"
    Invoke-WebRequest -Uri $istioctlUrl -OutFile "$env:TEMP\istioctl.zip" -UseBasicParsing
    Expand-Archive -Path "$env:TEMP\istioctl.zip" -DestinationPath "$env:TEMP\istioctl" -Force
    $istioctlExe = Get-ChildItem "$env:TEMP\istioctl" -Filter "istioctl.exe" -Recurse | Select-Object -First 1
    Copy-Item $istioctlExe.FullName -Destination "$env:USERPROFILE\bin\istioctl.exe" -Force
    $env:PATH = "$env:USERPROFILE\bin;" + $env:PATH
    Write-Host "  istioctl installed to $env:USERPROFILE\bin\istioctl.exe" -ForegroundColor Green
}

# Install Istio with minimal profile + laptop-optimized resources
Write-Host "  Installing Istio control plane (minimal profile)..." -ForegroundColor Yellow
istioctl install -f istio/istio-operator.yaml -y --verify 2>&1 |
    Where-Object { $_ -match "✔|✗|ERROR|WARNING|installed" } |
    ForEach-Object { Write-Host "  $_" }

# Enable Istio sidecar injection for app namespaces (already set in namespaces.yaml)
kubectl apply -f k8s/namespaces.yaml

# Install Istio addons: Prometheus (required by Kiali), Kiali, Jaeger
Write-Host "  Installing Istio addons (Prometheus, Kiali, Jaeger)..." -ForegroundColor Yellow
$istioRelease = "1.23"
$addons = @("prometheus", "kiali", "jaeger")
foreach ($addon in $addons) {
    $addonUrl = "https://raw.githubusercontent.com/istio/istio/release-$istioRelease/samples/addons/$addon.yaml"
    Write-Host "    Installing $addon..." -ForegroundColor Yellow
    kubectl apply -f $addonUrl 2>&1 | Out-Null
}

# Patch Kiali service to NodePort 30089 (same port as k3d port-mapping)
Write-Host "  Patching Kiali service to NodePort 30089..." -ForegroundColor Yellow
kubectl patch svc kiali -n istio-system --type='json' `
    -p='[{"op":"replace","path":"/spec/type","value":"NodePort"},{"op":"add","path":"/spec/ports/0/nodePort","value":30089}]' `
    2>$null | Out-Null

Write-Host "  Waiting for Istio components to stabilize (30s)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host "  Kiali ready!" -ForegroundColor Green
Write-Host "  URL: http://localhost:30089" -ForegroundColor Cyan

# Apply Istio mesh configs
Write-Host "  Applying Istio mesh configurations (mTLS, VirtualServices, AuthZ)..." -ForegroundColor Yellow
kubectl apply -f istio/mtls/peer-authentication.yaml
kubectl apply -f istio/mtls/destination-rule.yaml
if (Test-Path "istio/mtls/service-entry.yaml") { kubectl apply -f istio/mtls/service-entry.yaml }
Get-ChildItem "istio/traffic/*.yaml" | ForEach-Object { kubectl apply -f $_.FullName 2>&1 | Out-Null }
kubectl apply -f istio/security/authz-policies.yaml
Write-Host "  Istio mesh config applied!" -ForegroundColor Green

Write-Host "===========================================================" -ForegroundColor Green
Write-Host "🎉 Infrastructure Setup Complete!" -ForegroundColor Green
Write-Host "All databases, operators, middleware, ArgoCD and Istio deployed." -ForegroundColor Green
Write-Host ""
Write-Host "  Services access (localhost or Tailscale-IP-laptop-a):" -ForegroundColor Cyan
Write-Host "  Storefront UI    : http://localhost:30080" -ForegroundColor Cyan
Write-Host "  Backoffice UI    : http://localhost:30081" -ForegroundColor Cyan
Write-Host "  Swagger UI       : http://localhost:30082" -ForegroundColor Cyan
Write-Host "  Keycloak         : http://localhost:30084" -ForegroundColor Cyan
Write-Host "  ArgoCD           : http://localhost:30088  (admin / see password above)" -ForegroundColor Cyan
Write-Host "  Kiali            : http://localhost:30089" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Next: Apply Istio sidecars to existing pods:" -ForegroundColor Yellow
Write-Host "    .\scripts\apply-istio-mesh.ps1" -ForegroundColor Yellow
Write-Host "===========================================================" -ForegroundColor Green
