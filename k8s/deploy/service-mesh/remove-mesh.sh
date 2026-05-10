#!/bin/bash
# ============================================================
# REMOVE SERVICE MESH FROM NAMESPACE
# ============================================================
# Xoá service mesh config khỏi namespace trước khi scale-down
# hoặc khi chuyển mesh sang namespace khác.
#
# Usage:
#   ./remove-mesh.sh yas
#   ./remove-mesh.sh yas-dev-john-42
#   ./remove-mesh.sh --all   # Remove from ALL yas namespaces
# ============================================================
set -euo pipefail

remove_from_namespace() {
    local NS="$1"
    echo ">>> Removing service-mesh from namespace: $NS"

    # Uninstall Helm release
    if helm list -n "$NS" -q | grep -q "^service-mesh$"; then
        helm uninstall service-mesh -n "$NS" --wait --timeout 2m
        echo "    Helm release uninstalled"
    else
        echo "    No Helm release found, cleaning up manually..."
        kubectl delete authorizationpolicy --all -n "$NS" 2>/dev/null || true
        kubectl delete peerauthentication --all -n "$NS" 2>/dev/null || true
        kubectl delete destinationrule --all -n "$NS" 2>/dev/null || true
        kubectl delete virtualservice --all -n "$NS" 2>/dev/null || true
    fi

    # Clean up test pods
    kubectl delete pod test-client test-allowed-client -n "$NS" 2>/dev/null || true
    kubectl delete sa test-client -n "$NS" 2>/dev/null || true

    # Remove sidecar injection label (optional)
    kubectl label namespace "$NS" istio-injection- 2>/dev/null || true
    echo "    Sidecar injection label removed"

    # Restart pods to remove sidecars
    echo "    Restarting pods to remove Envoy sidecars..."
    kubectl rollout restart deployment --all -n "$NS" 2>/dev/null || true

    echo "    Done for namespace: $NS"
    echo ""
}

if [ $# -eq 0 ]; then
    echo "Usage: $0 <namespace> | --all"
    echo ""
    echo "Examples:"
    echo "  $0 yas"
    echo "  $0 yas-dev-john-42"
    echo "  $0 --all"
    exit 1
fi

if [ "$1" == "--all" ]; then
    echo "============================================"
    echo "  REMOVING SERVICE MESH FROM ALL NAMESPACES"
    echo "============================================"

    for NS in yas staging; do
        if kubectl get namespace "$NS" &>/dev/null; then
            remove_from_namespace "$NS"
        fi
    done

    # yas-dev-* namespaces
    DEV_NS=$(kubectl get namespaces -o jsonpath='{.items[*].metadata.name}' | tr ' ' '\n' | grep '^yas-dev-' || true)
    for NS in $DEV_NS; do
        remove_from_namespace "$NS"
    done

    echo "All namespaces cleaned"
else
    remove_from_namespace "$1"
fi

echo "============================================"
echo "  SERVICE MESH REMOVED"
echo "============================================"
