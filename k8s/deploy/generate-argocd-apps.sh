#!/bin/bash
set -e

# Detect Git Remote URL
REPO_URL=$(git config --get remote.origin.url || echo "https://github.com/luc1fe4/yas.git")
# Clean Git URL if it ends with .git
REPO_URL=${REPO_URL%.git}
REPO_URL="${REPO_URL}.git"

# Detect Active Branch
BRANCH_NAME=$(git branch --show-current || echo "feature/k8s-infra-setup")

echo "=========================================================="
echo "Generating ArgoCD Applications..."
echo "Repo URL: $REPO_URL"
echo "Branch  : $BRANCH_NAME"
echo "=========================================================="

# Create directories
mkdir -p ../../argocd/apps/dev
mkdir -p ../../argocd/apps/staging

# List of all YAS charts
services=(
    "cart" "customer" "inventory" "location" "media" "order" "payment" 
    "payment-paypal" "product" "promotion" "rating" "search" "tax" 
    "recommendation" "webhook" "sampledata" "storefront-ui" "backoffice-ui" 
    "storefront-bff" "backoffice-bff" "swagger-ui" "yas-configuration"
)

# 1. Generate Applications for DEV namespace
for svc in "${services[@]}"; do
    cat <<EOF > ../../argocd/apps/dev/dev-$svc.yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: dev-$svc
  namespace: argocd
  finalizers:
    - resources-finalizer.argocd.argoproj.io
spec:
  project: default
  source:
    repoURL: '$REPO_URL'
    targetRevision: '$BRANCH_NAME'
    path: k8s/charts/$svc
  destination:
    server: https://kubernetes.default.svc
    namespace: dev
  syncPolicy:
    automated:
      prune: false
      selfHeal: false
    syncOptions:
      - CreateNamespace=true
EOF
done

# 2. Generate Applications for STAGING namespace
for svc in "${services[@]}"; do
    cat <<EOF > ../../argocd/apps/staging/staging-$svc.yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: staging-$svc
  namespace: argocd
  finalizers:
    - resources-finalizer.argocd.argoproj.io
spec:
  project: default
  source:
    repoURL: '$REPO_URL'
    targetRevision: '$BRANCH_NAME'
    path: k8s/charts/$svc
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
done

echo "✅ Generated $((${#services[@]} * 2)) ArgoCD application YAML files successfully!"
echo "Files located in: /argocd/apps/dev/ and /argocd/apps/staging/"
echo "=========================================================="
