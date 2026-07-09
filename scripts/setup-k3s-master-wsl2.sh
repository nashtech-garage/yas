#!/bin/bash
# =============================================================
# YAS — Setup k3s MASTER node on Windows laptop-a via WSL2
# Run this script INSIDE WSL2 on laptop-a (the master node)
#
# Prerequisites on laptop-a:
#   1. WSL2 installed: wsl --install (then reboot)
#   2. Tailscale installed in WSL2: curl -fsSL https://tailscale.com/install.sh | sh
#   3. Tailscale logged in: sudo tailscale up
#
# Usage (inside WSL2):
#   chmod +x setup-k3s-master-wsl2.sh
#   sudo ./setup-k3s-master-wsl2.sh
#
# After running, get the join token for agents:
#   sudo cat /var/lib/rancher/k3s/server/node-token
# =============================================================
set -e

# ── Configuration ──────────────────────────────────────────────
# Node roles matching k3d simulation:
# laptop-a = server-0 (master)
# laptop-b = agent-0, laptop-c = agent-1, laptop-d = agent-2

CLUSTER_DOMAIN="yas.local.com"
NODE_ROLE="laptop-a"

# ── Detect Tailscale IP ────────────────────────────────────────
echo "==> Detecting Tailscale IP..."
# Try tailscale CLI first
if command -v tailscale &>/dev/null; then
    TAILSCALE_IP=$(tailscale ip -4 2>/dev/null | head -1)
fi
# Fallback: find tailscale interface IP
if [ -z "$TAILSCALE_IP" ]; then
    TAILSCALE_IP=$(ip addr show tailscale0 2>/dev/null | grep 'inet ' | awk '{print $2}' | cut -d/ -f1)
fi
if [ -z "$TAILSCALE_IP" ]; then
    echo "ERROR: Cannot detect Tailscale IP. Make sure Tailscale is running: sudo tailscale up"
    exit 1
fi
echo "    Tailscale IP: $TAILSCALE_IP"

# ── Install k3s as server (master) ────────────────────────────
echo ""
echo "==> Installing k3s server (master node)..."
curl -sfL https://get.k3s.io | INSTALL_K3S_VERSION="v1.31.4+k3s1" sh -s - server \
    --node-ip="$TAILSCALE_IP" \
    --advertise-address="$TAILSCALE_IP" \
    --bind-address="$TAILSCALE_IP" \
    --flannel-iface=tailscale0 \
    --disable=traefik \
    --disable=servicelb \
    --node-label="node-role=$NODE_ROLE" \
    --node-label="kubernetes.io/hostname=k3s-server-0" \
    --tls-san="$TAILSCALE_IP" \
    --kube-apiserver-arg="service-node-port-range=1-65535" \
    --write-kubeconfig-mode=644

echo "    Waiting for k3s to start..."
sleep 15

# ── Install Helm ───────────────────────────────────────────────
echo ""
echo "==> Installing Helm..."
if ! command -v helm &>/dev/null; then
    curl -sSL https://get.helm.sh/helm-v3.14.0-linux-amd64.tar.gz | tar -xz
    sudo mv linux-amd64/helm /usr/local/bin/helm
    rm -rf linux-amd64
fi
echo "    Helm version: $(helm version --short)"

# ── Install Traefik as ingress controller ─────────────────────
echo ""
echo "==> Installing Traefik ingress controller..."
export KUBECONFIG=/etc/rancher/k3s/k3s.yaml
helm repo add traefik https://helm.traefik.io/traefik 2>/dev/null || true
helm repo update
helm upgrade --install traefik traefik/traefik \
    --namespace kube-system \
    --set service.type=NodePort \
    --set ports.web.nodePort=80 \
    --set ports.websecure.nodePort=443

# ── Apply node labels ──────────────────────────────────────────
echo ""
echo "==> Applying node labels..."
MASTER_NODE_NAME=$(kubectl get node -o jsonpath='{.items[0].metadata.name}')
kubectl label node $MASTER_NODE_NAME node-role=$NODE_ROLE --overwrite
kubectl label node $MASTER_NODE_NAME type=heavy --overwrite

# Remove NoSchedule taint on Master node to allow running microservices and DBs
echo "==> Removing master taints to allow scheduling workloads..."
kubectl taint node $MASTER_NODE_NAME node-role.kubernetes.io/master:NoSchedule- --ignore-not-found=true || true
kubectl taint node $MASTER_NODE_NAME node-role.kubernetes.io/control-plane:NoSchedule- --ignore-not-found=true || true

# ── Expose NodePorts (via Windows host — firewall rules) ───────
echo ""
echo "==> NodePort ranges needed (open these on Windows Firewall):"
echo "    30080 - storefront-ui"
echo "    30081 - backoffice-ui"
echo "    30082 - swagger-ui"
echo "    30084 - keycloak"
echo "    30085 - storefront-bff"
echo "    30086 - backoffice-bff"
echo "    30088 - argocd"
echo "    30089 - kiali"

# ── Print kubeconfig for remote access ────────────────────────
echo ""
echo "==> Kubeconfig for other laptops (replace 127.0.0.1 with Tailscale IP):"
KUBECONFIG_CONTENT=$(cat /etc/rancher/k3s/k3s.yaml | sed "s/127.0.0.1/$TAILSCALE_IP/g")
echo "$KUBECONFIG_CONTENT" > /tmp/kubeconfig-tailscale.yaml
echo "    Saved to: /tmp/kubeconfig-tailscale.yaml"
echo "    Copy this file to other laptops as ~/.kube/config"

# ── Print join token ───────────────────────────────────────────
echo ""
echo "==> Join token for agent nodes:"
echo "    $(sudo cat /var/lib/rancher/k3s/server/node-token)"
echo ""
echo "    Run on each agent laptop (laptop-b/c/d):"
echo "    K3S_URL=https://$TAILSCALE_IP:6443 K3S_TOKEN=<token-above> ./setup-k3s-agent-wsl2.sh"

echo ""
echo "================================================================"
echo "  k3s MASTER setup complete on laptop-a ($TAILSCALE_IP)"
echo "  Now run setup-k3s-agent-wsl2.sh on laptop-b, c, d"
echo "================================================================"
