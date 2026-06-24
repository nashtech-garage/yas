# 03-setup-argocd-advanced.ps1
Write-Host "=============================================="
Write-Host "  CAI DAT ARGOCD VA ROOT APP (GITOPS NANG CAO) "
Write-Host "=============================================="

# 1. Cài đặt ArgoCD
Write-Host "`n[*] Đang cài đặt ArgoCD..."
kubectl create namespace argocd --dry-run=client -o yaml | kubectl apply -f -
helm repo add argo https://argoproj.github.io/argo-helm
helm repo update
helm upgrade --install argocd argo/argo-cd --namespace argocd --version 7.7.11 --set server.extraArgs={--insecure}

Write-Host "`n[*] Đang chờ ArgoCD Server khởi động (khoảng 30s-1p)..."
kubectl wait --for=condition=available deployment/argocd-server -n argocd --timeout=300s
kubectl wait --for=condition=available deployment/argocd-repo-server -n argocd --timeout=300s
kubectl wait --for=condition=available deployment/argocd-application-controller -n argocd --timeout=300s

# 2. Lấy mật khẩu đăng nhập
Write-Host "`n[*] Mật khẩu đăng nhập mặc định của tài khoản 'admin' là:"
$ARGOCD_PASS = [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String((kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}")))
Write-Host $ARGOCD_PASS -ForegroundColor Green

# 3. Triển khai Root App (App of Apps)
Write-Host "`n[*] Đang triển khai App of Apps (Root Apps) cho môi trường DEV và STAGING..."

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

Write-Host "`n[V] Đã gửi lệnh tạo Root Apps. Hãy port-forward ArgoCD và mở trình duyệt để xem thành quả!"
Write-Host "Lệnh port-forward: kubectl port-forward svc/argocd-server -n argocd 8080:443"
Write-Host "Truy cập: https://localhost:8080"
