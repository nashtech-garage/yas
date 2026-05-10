#!/bin/bash
# ============================================================
# APPLY SERVICE MESH TO ACTIVE NAMESPACE
# ============================================================
# Script tự động phát hiện namespace đang active (có pods running)
# và apply service mesh configuration vào namespace đó.
#
# Hỗ trợ 3 loại namespace:
#   - yas       (dev environment - GitOps)
#   - staging   (staging environment - GitOps)
#   - yas-dev-* (developer build namespace)
#
# Usage:
#   ./apply-mesh.sh              # Auto-detect active namespace
#   ./apply-mesh.sh yas          # Specify namespace explicitly
#   ./apply-mesh.sh yas-dev-john-42
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CHART_DIR="${SCRIPT_DIR}/../../charts/service-mesh"

# -----------------------------------------------
# Detect or use specified namespace
# -----------------------------------------------
detect_active_namespace() {
    echo "Detecting active namespace..."

    # Check candidate namespaces in priority order
    local CANDIDATES=()

    # 1. Check yas-dev-* namespaces (developer builds)
    local DEV_NS
    DEV_NS=$(kubectl get namespaces -o jsonpath='{.items[*].metadata.name}' | tr ' ' '\n' | grep '^yas-dev-' || true)
    for ns in $DEV_NS; do
        CANDIDATES+=("$ns")
    done

    # 2. Check standard namespaces
    CANDIDATES+=("yas" "staging")

    # Find first namespace with running pods (replicas > 0)
    for ns in "${CANDIDATES[@]}"; do
        if kubectl get namespace "$ns" &>/dev/null; then
            local RUNNING_PODS
            RUNNING_PODS=$(kubectl get pods -n "$ns" --field-selector=status.phase=Running --no-headers 2>/dev/null | wc -l)
            if [ "$RUNNING_PODS" -gt 0 ]; then
                echo "  Found active namespace: $ns ($RUNNING_PODS running pods)"
                echo "$ns"
                return 0
            fi
        fi
    done

    echo "ERROR: No active namespace found with running pods"
    return 1
}

if [ $# -ge 1 ]; then
    TARGET_NS="$1"
    echo "Using specified namespace: $TARGET_NS"
else
    TARGET_NS=$(detect_active_namespace)
fi

# Validate namespace exists
if ! kubectl get namespace "$TARGET_NS" &>/dev/null; then
    echo "ERROR: Namespace '$TARGET_NS' does not exist"
    exit 1
fi

echo ""
echo "============================================"
echo "  APPLYING SERVICE MESH"
echo "  Namespace: ${TARGET_NS}"
echo "============================================"

# -----------------------------------------------
# Step 1: Ensure Istio is installed
# -----------------------------------------------
echo ""
echo ">>> [1/4] Checking Istio installation..."

if ! kubectl get namespace istio-system &>/dev/null; then
    echo "    ERROR: Istio is not installed. Run install-istio.sh first."
    echo "    Hint: cd $(dirname "$0") && ./install-istio.sh"
    exit 1
fi

ISTIOD_READY=$(kubectl get pods -n istio-system -l app=istiod --field-selector=status.phase=Running --no-headers 2>/dev/null | wc -l)
if [ "$ISTIOD_READY" -eq 0 ]; then
    echo "    ERROR: Istiod is not running. Check istio-system namespace."
    exit 1
fi
echo "    Istio is installed and running"

# -----------------------------------------------
# Step 2: Enable sidecar injection
# -----------------------------------------------
echo ""
echo ">>> [2/4] Enabling sidecar injection for namespace '${TARGET_NS}'..."
kubectl label namespace "$TARGET_NS" istio-injection=enabled --overwrite
echo "    Done"

# -----------------------------------------------
# Step 3: Deploy service-mesh Helm chart
# -----------------------------------------------
echo ""
echo ">>> [3/4] Deploying service-mesh Helm chart..."

helm upgrade --install service-mesh "$CHART_DIR" \
    --namespace "$TARGET_NS" \
    --wait --timeout 2m

echo "    Helm chart deployed"

# -----------------------------------------------
# Step 4: Restart pods to inject sidecars
# -----------------------------------------------
echo ""
echo ">>> [4/4] Restarting pods to inject Envoy sidecars..."

DEPLOYMENTS=$(kubectl get deployments -n "$TARGET_NS" -o jsonpath='{.items[*].metadata.name}' 2>/dev/null || echo "")

if [ -z "$DEPLOYMENTS" ]; then
    echo "    No deployments found. Sidecars will be injected when pods are created."
else
    for deploy in $DEPLOYMENTS; do
        echo "    Restarting: ${deploy}"
        kubectl rollout restart "deployment/${deploy}" -n "$TARGET_NS"
    done

    echo "    Waiting for rollout..."
    for deploy in $DEPLOYMENTS; do
        kubectl rollout status "deployment/${deploy}" -n "$TARGET_NS" --timeout=300s || true
    done
fi

# -----------------------------------------------
# Summary
# -----------------------------------------------
echo ""
echo "============================================"
echo "  SERVICE MESH APPLIED SUCCESSFULLY"
echo "============================================"
echo ""
echo "  Namespace    : ${TARGET_NS}"
echo "  mTLS mode    : STRICT"
echo "  Auth policies: $(kubectl get authorizationpolicy -n "$TARGET_NS" --no-headers 2>/dev/null | wc -l) rules"
echo "  Retry        : $(kubectl get virtualservice -n "$TARGET_NS" --no-headers 2>/dev/null | wc -l) services"
echo ""
echo "  Verify mTLS:"
echo "    istioctl x describe pod \$(kubectl get pod -n $TARGET_NS -o jsonpath='{.items[0].metadata.name}') -n $TARGET_NS"
echo ""
echo "  Test authorization (DENY):"
echo "    kubectl apply -f <(helm template service-mesh $CHART_DIR -n $TARGET_NS -s templates/tests/test-pods.yaml)"
echo "    kubectl exec -n $TARGET_NS test-client -- curl -v http://product.$TARGET_NS:80/product/"
echo ""
echo "  Kiali dashboard:"
echo "    kubectl port-forward svc/kiali -n istio-system 20001:20001"
echo "    → http://localhost:20001"
echo ""
echo "============================================"
