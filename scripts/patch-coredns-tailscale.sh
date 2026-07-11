#!/bin/bash
# =============================================================
# YAS — Patch CoreDNS after migrating to k3s + Tailscale
# Run this on ANY laptop that has kubectl access to the cluster
#
# Why needed:
#   CoreDNS custom hosts map identity.yas.local.com → ClusterIP of keycloak-service
#   This ClusterIP changes between clusters (k3d vs real k3s)
#   This script auto-detects the new ClusterIP and patches CoreDNS
#
# Usage:
#   chmod +x patch-coredns-tailscale.sh
#   ./patch-coredns-tailscale.sh
# =============================================================
set -e

export KUBECONFIG=/etc/rancher/k3s/k3s.yaml
DOMAIN="yas.local.com"

echo "================================================================"
echo "  Patching CoreDNS for k3s + Tailscale cluster"
echo "================================================================"

# ── Detect keycloak-service ClusterIP ─────────────────────────
echo ""
echo "==> Detecting Keycloak service ClusterIP..."
KEYCLOAK_IP=$(kubectl get svc keycloak-service -n keycloak -o jsonpath='{.spec.clusterIP}' 2>/dev/null)
if [ -z "$KEYCLOAK_IP" ]; then
    echo "ERROR: keycloak-service not found in namespace 'keycloak'"
    echo "       Make sure Keycloak is deployed first (run run-yas-setup.ps1 via WSL2)"
    exit 1
fi
echo "    keycloak-service ClusterIP: $KEYCLOAK_IP"

# ── Get current CoreDNS ConfigMap ─────────────────────────────
echo ""
echo "==> Reading current CoreDNS ConfigMap..."
kubectl get configmap coredns -n kube-system -o yaml > /tmp/coredns-current.yaml

# ── Build new NodeHosts entry ─────────────────────────────────
# Remove old identity.yas.local.com entries, add new one
NODE_HOSTS=$(kubectl get configmap coredns -n kube-system \
    -o jsonpath='{.data.NodeHosts}' 2>/dev/null | \
    grep -v "identity\.$DOMAIN" || true)

NEW_HOST_ENTRY="$KEYCLOAK_IP identity.$DOMAIN"

# ── Apply patch ───────────────────────────────────────────────
echo ""
echo "==> Patching CoreDNS ConfigMap..."
CURRENT_NODEHOSTS=$(kubectl get configmap coredns -n kube-system \
    -o jsonpath='{.data.NodeHosts}' 2>/dev/null | \
    grep -v "identity\.$DOMAIN" || true)

# Use yq to merge the updated NodeHosts cleanly, avoiding JSON newline issues
export NEW_NODEHOSTS="${CURRENT_NODEHOSTS}
${KEYCLOAK_IP} identity.${DOMAIN}"

# Step 1: Read the current config map in JSON format
kubectl get configmap coredns -n kube-system -o json > /tmp/coredns-patch.json

# Step 2: Modify it in-place using python3
python3 -c "import json, os; d = json.load(open('/tmp/coredns-patch.json')); d['data']['NodeHosts'] = os.environ['NEW_NODEHOSTS']; json.dump(d, open('/tmp/coredns-patched.json', 'w'))"

# Step 3: Apply the modified config map back
kubectl apply -f /tmp/coredns-patched.json

echo "    Added: $NEW_HOST_ENTRY"

# ── Restart CoreDNS to pick up changes ────────────────────────
echo ""
echo "==> Restarting CoreDNS..."
kubectl rollout restart deployment/coredns -n kube-system 2>/dev/null || \
kubectl delete pod -n kube-system -l k8s-app=kube-dns 2>/dev/null || true
sleep 10

# ── Verify DNS resolution ─────────────────────────────────────
echo ""
echo "==> Verifying DNS resolution from inside cluster..."
TEST_RESULT=$(kubectl run dns-verify --image=busybox:1.28 \
    --restart=Never --rm -it -n default \
    --command -- nslookup "identity.$DOMAIN" 2>&1 || true)

if echo "$TEST_RESULT" | grep -q "$KEYCLOAK_IP"; then
    echo "    OK: identity.$DOMAIN → $KEYCLOAK_IP (DNS resolution working)"
else
    echo "    WARN: DNS verification inconclusive. Check manually:"
    echo "    kubectl run dns-test --image=busybox:1.28 --rm -it -- nslookup identity.$DOMAIN"
fi

echo ""
echo "================================================================"
echo "  CoreDNS patched successfully!"
echo "  Next: Update hosts file on each Windows laptop:"
echo "  Add: <Tailscale-IP-of-laptop-a>  yas.local.com"
echo "  Add: <Tailscale-IP-of-laptop-a>  identity.yas.local.com"
echo "  Add: <Tailscale-IP-of-laptop-a>  backoffice.dev.yas.local.com"
echo "  Add: <Tailscale-IP-of-laptop-a>  storefront.dev.yas.local.com"
echo "  Add: <Tailscale-IP-of-laptop-a>  api.dev.yas.local.com"
echo "================================================================"
