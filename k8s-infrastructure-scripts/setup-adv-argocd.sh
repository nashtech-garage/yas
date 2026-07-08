#!/bin/bash
set -e

echo "=============================================="
echo "  SETUP ARGOCD AND ROOT APP (ADVANCED GITOPS) "
echo "=============================================="

echo -e "\n[*] Installing ArgoCD..."
kubectl create namespace argocd --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -n argocd -f https://cdn.jsdelivr.net/gh/argoproj/argo-cd@stable/manifests/install.yaml --server-side --force-conflicts

echo -e "\n[*] Waiting for ArgoCD Server to start (about 30s-1m)..."
kubectl wait --for=condition=available deployment/argocd-server -n argocd --timeout=300s
kubectl wait --for=condition=available deployment/argocd-repo-server -n argocd --timeout=300s
kubectl rollout status statefulset/argocd-application-controller -n argocd --timeout=300s

echo -e "\n[*] The default login password for 'admin' is:"
ARGOCD_PASS=$(kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d)
echo -e "\e[32m$ARGOCD_PASS\e[0m"

echo -e "\n[*] Deploying App of Apps (Root Apps) for DEV and STAGING environments..."

cat <<EOF | kubectl apply -f -
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
EOF

echo -e "\n[V] Root Apps creation commands sent. Port-forward ArgoCD and open browser to see the results!"
echo "Port-forward command: sudo kubectl port-forward --address 0.0.0.0 svc/argocd-server -n argocd 8080:443"
echo "Access at: https://<Public-IP-cua-AWS>:8080"
