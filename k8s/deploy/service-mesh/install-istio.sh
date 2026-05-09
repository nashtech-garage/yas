#!/bin/bash
# ============================================================
# ISTIO SERVICE MESH INSTALLATION SCRIPT
# Cài đặt Istio + Kiali cho ứng dụng YAS trên K8S
#
# Prerequisites:
#   - kubectl đã kết nối tới cluster
#   - Helm 3 đã cài đặt
#   - Cluster có ít nhất 4GB RAM khả dụng cho Istio components
#
# Usage:
#   chmod +x install-istio.sh
#   ./install-istio.sh
# ============================================================
set -euo pipefail

ISTIO_VERSION="1.20.2"
YAS_NAMESPACE="yas"

echo "============================================"
echo "  ISTIO SERVICE MESH INSTALLATION"
echo "  Version: ${ISTIO_VERSION}"
echo "============================================"

# -----------------------------------------------
# Step 1: Download and install istioctl
# -----------------------------------------------
echo ""
echo ">>> [1/7] Downloading Istio ${ISTIO_VERSION}..."

if command -v istioctl &> /dev/null; then
    CURRENT_VERSION=$(istioctl version --short 2>/dev/null | head -1 || echo "unknown")
    echo "    istioctl already installed (version: ${CURRENT_VERSION})"
else
    curl -L https://istio.io/downloadIstio | ISTIO_VERSION=${ISTIO_VERSION} sh -
    export PATH="$PWD/istio-${ISTIO_VERSION}/bin:$PATH"
    echo "    istioctl installed successfully"
fi

# -----------------------------------------------
# Step 2: Pre-flight check
# -----------------------------------------------
echo ""
echo ">>> [2/7] Running pre-flight checks..."
istioctl x precheck
echo "    Pre-flight checks passed"

# -----------------------------------------------
# Step 3: Install Istio with demo profile
# -----------------------------------------------
echo ""
echo ">>> [3/7] Installing Istio with 'demo' profile..."
istioctl install --set profile=demo -y \
    --set meshConfig.accessLogFile=/dev/stdout \
    --set meshConfig.defaultConfig.holdApplicationUntilProxyStarts=true

echo "    Istio installed successfully"

# Wait for Istio system pods to be ready
echo "    Waiting for Istio pods to be ready..."
kubectl wait --for=condition=ready pod --all -n istio-system --timeout=300s
echo "    All Istio pods are ready"

# -----------------------------------------------
# Step 4: Install Kiali and addons
# -----------------------------------------------
echo ""
echo ">>> [4/7] Installing Kiali and monitoring addons..."

# Install Prometheus (required by Kiali for metrics)
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/prometheus.yaml

# Install Kiali
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/kiali.yaml

# Install Grafana (optional, useful for dashboards)
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/grafana.yaml

# Install Jaeger (optional, useful for tracing)
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/jaeger.yaml

# Wait for addons to be ready
echo "    Waiting for Kiali to be ready..."
kubectl wait --for=condition=ready pod -l app=kiali -n istio-system --timeout=300s
echo "    Kiali is ready"

# -----------------------------------------------
# Step 5: Enable sidecar injection for yas namespace
# -----------------------------------------------
echo ""
echo ">>> [5/7] Enabling Istio sidecar injection for namespace '${YAS_NAMESPACE}'..."

# Create namespace if not exists
kubectl create namespace ${YAS_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -

# Label namespace for automatic sidecar injection
kubectl label namespace ${YAS_NAMESPACE} istio-injection=enabled --overwrite
echo "    Sidecar injection enabled for namespace '${YAS_NAMESPACE}'"

# -----------------------------------------------
# Step 6: Restart existing pods to inject sidecars
# -----------------------------------------------
echo ""
echo ">>> [6/7] Restarting existing pods in '${YAS_NAMESPACE}' to inject Envoy sidecars..."

# Get all deployments in the yas namespace and restart them
DEPLOYMENTS=$(kubectl get deployments -n ${YAS_NAMESPACE} -o jsonpath='{.items[*].metadata.name}' 2>/dev/null || echo "")

if [ -z "$DEPLOYMENTS" ]; then
    echo "    No deployments found in namespace '${YAS_NAMESPACE}'. Sidecars will be injected when pods are created."
else
    for deploy in $DEPLOYMENTS; do
        echo "    Restarting deployment: ${deploy}"
        kubectl rollout restart deployment/${deploy} -n ${YAS_NAMESPACE}
    done

    echo "    Waiting for rollout to complete..."
    for deploy in $DEPLOYMENTS; do
        kubectl rollout status deployment/${deploy} -n ${YAS_NAMESPACE} --timeout=300s || true
    done
fi

# -----------------------------------------------
# Step 7: Apply Service Mesh configurations
# -----------------------------------------------
echo ""
echo ">>> [7/7] Applying Service Mesh configurations..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "    Applying mTLS PeerAuthentication..."
kubectl apply -f "${SCRIPT_DIR}/peer-authentication.yaml"

echo "    Applying DestinationRules..."
kubectl apply -f "${SCRIPT_DIR}/destination-rules.yaml"

echo "    Applying AuthorizationPolicies..."
kubectl apply -f "${SCRIPT_DIR}/authorization-policies.yaml"

echo "    Applying VirtualService retry policies..."
kubectl apply -f "${SCRIPT_DIR}/virtual-services.yaml"

echo "    All configurations applied"

# -----------------------------------------------
# Summary
# -----------------------------------------------
echo ""
echo "============================================"
echo "  INSTALLATION COMPLETE!"
echo "============================================"
echo ""
echo "  Istio version : ${ISTIO_VERSION}"
echo "  Namespace     : ${YAS_NAMESPACE}"
echo "  mTLS mode     : STRICT"
echo "  Kiali         : Installed"
echo ""
echo "  Access Kiali Dashboard:"
echo "    kubectl port-forward svc/kiali -n istio-system 20001:20001"
echo "    Open: http://localhost:20001"
echo ""
echo "  Verify mTLS:"
echo "    istioctl x describe pod <pod-name> -n ${YAS_NAMESPACE}"
echo ""
echo "  Check sidecar injection:"
echo "    kubectl get pods -n ${YAS_NAMESPACE} -o jsonpath='{range .items[*]}{.metadata.name}{\" containers: \"}{range .spec.containers[*]}{.name}{\", \"}{end}{\"\n\"}{end}'"
echo ""
echo "============================================"
