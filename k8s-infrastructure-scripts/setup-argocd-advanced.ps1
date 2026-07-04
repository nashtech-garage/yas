# 03-setup-argocd-advanced.ps1
Write-Host "=============================================="
Write-Host "  SETUP ARGOCD AND ROOT APP (ADVANCED GITOPS) "
Write-Host "=============================================="

# 1. Cài đặt ArgoCD
Write-Host "`n[*] Installing ArgoCD..."
kubectl create namespace argocd --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

Write-Host "`n[*] Waiting for ArgoCD Server to start (about 30s-1m)..."
kubectl wait --for=condition=available deployment/argocd-server -n argocd --timeout=300s
kubectl wait --for=condition=available deployment/argocd-repo-server -n argocd --timeout=300s
kubectl wait --for=condition=available deployment/argocd-application-controller -n argocd --timeout=300s

# 2. Lấy mật khẩu đăng nhập
Write-Host "`n[*] The default login password for 'admin' is:"
$ARGOCD_PASS = [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String((kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}")))
Write-Host $ARGOCD_PASS -ForegroundColor Green

# 3. Triển khai Root App (App of Apps)
Write-Host "`n[*] Deploying App of Apps (Root Apps) for DEV and STAGING environments..."

$ROOT_APPS_YAML = @"
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: yas-root-dev
  namespace: argocd
  finalizers:
    - resources-finalizer.argocd.argoproj.io
spec:
  project: default
  source:
    repoURL: https://github.com/dorayakiiiiz/yas-gitops.git
    targetRevision: main
    path: .
    helm:
      valueFiles:
        - values-dev.yaml
  destination:
    server: https://kubernetes.default.svc
    namespace: dev
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
    syncOptions:
      - CreateNamespace=true
---
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: yas-root-staging
  namespace: argocd
  finalizers:
    - resources-finalizer.argocd.argoproj.io
spec:
  project: default
  source:
    repoURL: https://github.com/dorayakiiiiz/yas-gitops.git
    targetRevision: main
    path: .
    helm:
      valueFiles:
        - values-staging.yaml
  destination:
    server: https://kubernetes.default.svc
    namespace: staging
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
    syncOptions:
      - CreateNamespace=true
"@

$ROOT_APPS_YAML | kubectl apply -f -

Write-Host "`n[V] Root Apps creation commands sent. Port-forward ArgoCD and open browser to see the results!"
Write-Host "Port-forward command: kubectl port-forward svc/argocd-server -n argocd 8080:443"
Write-Host "Access at: https://localhost:8080"
