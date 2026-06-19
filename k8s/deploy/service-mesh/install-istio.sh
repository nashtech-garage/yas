#!/bin/bash
# ============================================================
# ISTIO SERVICE MESH INSTALLATION SCRIPT
# ============================================================
# Cài đặt Istio + Kiali trên cluster.
# Script này CHỈ cài Istio system components (1 lần duy nhất).
# Sau đó dùng apply-mesh.sh để áp dụng mesh cho namespace cụ thể.
#
# Prerequisites:
#   - kubectl đã kết nối tới cluster
#   - Cluster có ít nhất 4GB RAM khả dụng cho Istio components
#
# Usage:
#   chmod +x install-istio.sh
#   ./install-istio.sh
#
# Sau khi cài xong:
#   ./apply-mesh.sh           # Auto-detect active namespace
#   ./apply-mesh.sh yas       # Apply cho namespace yas
#   ./apply-mesh.sh yas-dev-john-42
# ============================================================
set -euo pipefail

ISTIO_VERSION="1.20.2"

echo "============================================"
echo "  ISTIO SERVICE MESH INSTALLATION"
echo "  Version: ${ISTIO_VERSION}"
echo "============================================"

# -----------------------------------------------
# Step 1: Download and install istioctl
# -----------------------------------------------
echo ""
echo ">>> [1/4] Downloading Istio ${ISTIO_VERSION}..."

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
echo ">>> [2/4] Running pre-flight checks..."
istioctl x precheck
echo "    Pre-flight checks passed"

# -----------------------------------------------
# Step 3: Install Istio with demo profile
# -----------------------------------------------
echo ""
echo ">>> [3/4] Installing Istio with 'demo' profile..."
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
echo ">>> [4/4] Installing Kiali and monitoring addons..."

kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/prometheus.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/kiali.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/grafana.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/jaeger.yaml

echo "    Waiting for Kiali to be ready..."
kubectl wait --for=condition=ready pod -l app=kiali -n istio-system --timeout=300s
echo "    Kiali is ready"

# -----------------------------------------------
# Summary
# -----------------------------------------------
echo ""
echo "============================================"
echo "  ISTIO INSTALLATION COMPLETE!"
echo "============================================"
echo ""
echo "  Istio version : ${ISTIO_VERSION}"
echo "  Kiali         : Installed"
echo "  Grafana       : Installed"
echo "  Jaeger        : Installed"
echo ""
echo "  NEXT STEP: Apply mesh to your active namespace:"
echo ""
echo "    ./apply-mesh.sh              # Auto-detect"
echo "    ./apply-mesh.sh yas          # Dev namespace"
echo "    ./apply-mesh.sh staging      # Staging namespace"
echo "    ./apply-mesh.sh yas-dev-xxx  # Developer build"
echo ""
echo "  Kiali Dashboard:"
echo "    kubectl port-forward svc/kiali -n istio-system 20001:20001"
echo "    → http://localhost:20001"
echo ""
echo "============================================"
