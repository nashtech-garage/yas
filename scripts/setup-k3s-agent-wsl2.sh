#!/bin/bash
# =============================================================
# YAS — Setup k3s AGENT node on Windows laptop-b/c/d via WSL2
# Run this script INSIDE WSL2 on each agent laptop
#
# Prerequisites on each agent laptop:
#   1. WSL2 installed and running Ubuntu
#   2. Tailscale installed: curl -fsSL https://tailscale.com/install.sh | sh
#   3. Tailscale logged in to SAME network: sudo tailscale up
#   4. Get K3S_URL and K3S_TOKEN from master (laptop-a)
#
# Usage (inside WSL2):
#   export K3S_URL="https://<laptop-a-tailscale-ip>:6443"
#   export K3S_TOKEN="<token-from-master>"
#   export NODE_ROLE="laptop-b"   # or laptop-c or laptop-d
#   chmod +x setup-k3s-agent-wsl2.sh
#   sudo -E ./setup-k3s-agent-wsl2.sh
# =============================================================
set -e

# ── Validate env vars ─────────────────────────────────────────
if [ -z "$K3S_URL" ] || [ -z "$K3S_TOKEN" ]; then
    echo "ERROR: K3S_URL and K3S_TOKEN must be set."
    echo "  Get from master: sudo cat /var/lib/rancher/k3s/server/node-token"
    echo "  Usage: K3S_URL=https://100.x.x.1:6443 K3S_TOKEN=<token> NODE_ROLE=laptop-b sudo -E $0"
    exit 1
fi
NODE_ROLE="${NODE_ROLE:-laptop-b}"

# ── Detect Tailscale IP ────────────────────────────────────────
echo "==> Detecting Tailscale IP..."
if command -v tailscale &>/dev/null; then
    TAILSCALE_IP=$(tailscale ip -4 2>/dev/null | head -1)
fi
if [ -z "$TAILSCALE_IP" ]; then
    TAILSCALE_IP=$(ip addr show tailscale0 2>/dev/null | grep 'inet ' | awk '{print $2}' | cut -d/ -f1)
fi
if [ -z "$TAILSCALE_IP" ]; then
    echo "ERROR: Cannot detect Tailscale IP. Run: sudo tailscale up"
    exit 1
fi
echo "    Tailscale IP: $TAILSCALE_IP (this agent)"
echo "    Master URL  : $K3S_URL"
echo "    Node role   : $NODE_ROLE"

# ── Install k3s as agent ──────────────────────────────────────
echo ""
echo "==> Installing k3s agent..."
curl -sfL https://get.k3s.io | INSTALL_K3S_VERSION="v1.31.4+k3s1" \
    K3S_URL="$K3S_URL" \
    K3S_TOKEN="$K3S_TOKEN" \
    sh -s - agent \
        --node-ip="$TAILSCALE_IP" \
        --flannel-iface=tailscale0 \
        --node-label="node-role=$NODE_ROLE" \
        --node-label="type=light"

echo ""
echo "    Waiting 20s for agent to register..."
sleep 20

echo ""
echo "================================================================"
echo "  k3s AGENT setup complete on $NODE_ROLE ($TAILSCALE_IP)"
echo "  Verify on master: kubectl get nodes"
echo "================================================================"
