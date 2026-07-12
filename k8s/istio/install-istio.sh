#!/usr/bin/env bash
set -euo pipefail

# Directory of this script (k8s/istio).
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Istio release dir that ships samples/addons (Prometheus, Kiali, ...).
ISTIO_DIR="${ISTIO_DIR:-$HERE/../../istio-1.30.2}"

# 1) Install the Istio control plane (demo profile: istiod + gateways).
istioctl install --set profile=demo -y

# 2) Namespaces + sidecar injection (yas, dev, staging, ingress-nginx).
kubectl apply -f "$HERE/../namespaces.yaml"
kubectl label namespace ingress-nginx istio-injection=enabled --overwrite

# 3) Base mesh policy (NON-recursive: only the root *.yaml, NOT demo/).
#    - peer-authentication.yaml  : STRICT mTLS (+ PERMISSIVE public entrypoints)
#    - destination-rules.yaml    : ISTIO_MUTUAL + connection pools
#    - virtual-services.yaml     : retry policies (tax, order)
#    - public-entrypoints.yaml   : PERMISSIVE for NGINX-facing BFFs
#    - authorization-policies.yaml : product allow-list (deny all others)
#    - kiali-ingress.yaml        : Kiali dashboard via NGINX ingress
kubectl apply -f "$HERE"

# 4) Observability addons required by Kiali (lightweight Prometheus + Kiali).
if [ -d "$ISTIO_DIR/samples/addons" ]; then
  kubectl apply -f "$ISTIO_DIR/samples/addons/prometheus.yaml"
  kubectl apply -f "$ISTIO_DIR/samples/addons/kiali.yaml"
else
  echo "WARN: $ISTIO_DIR/samples/addons not found; install Prometheus+Kiali manually."
fi

# 5) Restart workloads so sidecars are injected.
kubectl rollout restart deployment -n yas
kubectl rollout restart deployment ingress-nginx-controller -n ingress-nginx

cat <<'EOF'

Done. Service-mesh demo artifacts (NOT applied by default) live in k8s/istio/demo/:
  - authorization-policy-strict-demo.yaml : "only order -> product" (breaks browsing; demo only)
  - mesh-test-clients.yaml                : sidecar curl pods (SA=order allowed, SA=default denied)
  - retry-demo-httpbin.yaml               : httpbin /status/500 backend + retry VirtualService

Kiali: add "<LB_IP> kiali.yas.local.com" to /etc/hosts, then open http://kiali.yas.local.com
EOF
