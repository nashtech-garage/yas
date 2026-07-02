#!/bin/bash
set -e

GITOPS_REPO="https://${GH_TOKEN}@github.com/com-suon-bi-cha/gitops-manifest-k8s.git"
WORKDIR="/tmp/gitops-devbuild-$$"

echo "=== Cloning GitOps repo ==="
git clone "${GITOPS_REPO}" "${WORKDIR}"
cd "${WORKDIR}/environments/developer-build"

echo "=== Updating image tags ==="

# Map TAG__<PARAM> env vars → service image name
declare -A SERVICE_MAP=(
    ["TAG__MEDIA"]="media"
    ["TAG__PRODUCT"]="product"
    ["TAG__ORDER"]="order"
    ["TAG__INVENTORY"]="inventory"
    ["TAG__PAYMENT"]="payment"
    ["TAG__CUSTOMER"]="customer"
    ["TAG__CART"]="cart"
    ["TAG__TAX"]="tax"
    ["TAG__SEARCH"]="search"
    ["TAG__BACKOFFICE_BFF"]="backoffice-bff"
    ["TAG__STOREFRONT_BFF"]="storefront-bff"
    ["TAG__BACKOFFICE_UI"]="backoffice"
    ["TAG__STOREFRONT_UI"]="storefront"
    ["TAG__SAMPLEDATA"]="sampledata"
)

for env_var in "${!SERVICE_MAP[@]}"; do
    svc="${SERVICE_MAP[$env_var]}"
    tag="${!env_var:-latest}"
    echo "  ${svc} → ${tag}"
    kustomize edit set image "bingsu1103/${svc}:${tag}"
done

echo "=== Applying to namespace developer-build ==="
# Apply trực tiếp (bypass ArgoCD — faster for developer use)
kubectl apply -k . --namespace developer-build --kubeconfig="${KUBECONFIG}" --insecure-skip-tls-verify

echo "=== Waiting for pods to be ready ==="
kubectl rollout status deployment --all -n developer-build \
  --kubeconfig="${KUBECONFIG}" --insecure-skip-tls-verify --timeout=180s || true

rm -rf "${WORKDIR}"
echo "=== Deploy developer-build completed ==="
