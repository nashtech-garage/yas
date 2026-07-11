#!/bin/bash
# =============================================================================
# YAS Laptop Node Setup Script (k3s + Tailscale)
# =============================================================================
# Run this on EACH laptop ONCE. The script detects whether to install k3s as
# master (laptop-a) or worker (laptop-b/c/d) based on the ROLE argument.
#
# Usage:
#   Laptop A (master): bash setup-laptop-node.sh master
#   Laptop B/C/D:      bash setup-laptop-node.sh worker <MASTER_TAILSCALE_IP> <NODE_TOKEN>
#
# After all 4 laptops are joined, run on Laptop A:
#   bash setup-laptop-node.sh deploy
# =============================================================================

set -e

ROLE=${1:-""}
MASTER_IP=${2:-""}
NODE_TOKEN=${3:-""}

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

log()  { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
err()  { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# ── Detect Tailscale IP ────────────────────────────────────────────────────
get_tailscale_ip() {
    local ip
    ip=$(tailscale ip -4 2>/dev/null)
    if [ -z "$ip" ]; then
        err "Tailscale is not running or not logged in. Run: tailscale up"
    fi
    echo "$ip"
}

# ── MASTER setup (Laptop A) ───────────────────────────────────────────────
setup_master() {
    log "Setting up k3s MASTER on Laptop A..."
    local TAILSCALE_IP
    TAILSCALE_IP=$(get_tailscale_ip)
    log "Tailscale IP: $TAILSCALE_IP"

    curl -sfL https://get.k3s.io | INSTALL_K3S_VERSION="v1.31.4+k3s1" sh -s - server \
        --node-ip="$TAILSCALE_IP" \
        --advertise-address="$TAILSCALE_IP" \
        --tls-san="$TAILSCALE_IP" \
        --flannel-iface=tailscale0 \
        --disable traefik \
        --write-kubeconfig-mode 644

    log "Waiting for node to be Ready..."
    until kubectl get node 2>/dev/null | grep -q "Ready"; do sleep 2; done

    # Label this node as laptop-a (same label as tested on desktop)
    NODE_NAME=$(kubectl get nodes -o jsonpath='{.items[0].metadata.name}')
    kubectl label node "$NODE_NAME" node-role=laptop-a --overwrite

    echo ""
    echo -e "${CYAN}============================================================${NC}"
    echo -e "${GREEN}Master ready! Share these with Laptop B/C/D:${NC}"
    echo -e "  MASTER_IP  = ${YELLOW}$TAILSCALE_IP${NC}"
    echo -e "  NODE_TOKEN = ${YELLOW}$(sudo cat /var/lib/rancher/k3s/server/node-token)${NC}"
    echo -e "${CYAN}============================================================${NC}"
    echo ""
    echo "On each worker laptop run:"
    echo "  bash setup-laptop-node.sh worker $TAILSCALE_IP <NODE_TOKEN>"
}

# ── WORKER setup (Laptop B/C/D) ──────────────────────────────────────────
setup_worker() {
    local LAPTOP_LABEL=$4
    [ -z "$MASTER_IP" ]  && err "Usage: $0 worker <MASTER_IP> <NODE_TOKEN> <laptop-b|laptop-c|laptop-d>"
    [ -z "$NODE_TOKEN" ] && err "NODE_TOKEN is required"
    [ -z "$LAPTOP_LABEL" ] && err "Laptop label required (laptop-b, laptop-c, or laptop-d)"

    local TAILSCALE_IP
    TAILSCALE_IP=$(get_tailscale_ip)
    log "Setting up k3s WORKER ($LAPTOP_LABEL) — Tailscale IP: $TAILSCALE_IP"

    curl -sfL https://get.k3s.io | INSTALL_K3S_VERSION="v1.31.4+k3s1" \
        K3S_URL="https://$MASTER_IP:6443" \
        K3S_TOKEN="$NODE_TOKEN" \
        sh -s - agent \
        --node-ip="$TAILSCALE_IP" \
        --flannel-iface=tailscale0

    log "Worker joined! Back on Laptop A, label this node:"
    log "  kubectl label node \$(hostname) node-role=$LAPTOP_LABEL --overwrite"
}

# ── LABEL workers from Laptop A ──────────────────────────────────────────
label_workers() {
    log "Current nodes:"
    kubectl get nodes -o wide
    echo ""
    warn "Manually label each worker node:"
    echo "  kubectl label node <NODE_NAME> node-role=laptop-b --overwrite"
    echo "  kubectl label node <NODE_NAME> node-role=laptop-c --overwrite"
    echo "  kubectl label node <NODE_NAME> node-role=laptop-d --overwrite"
}

# ── DEPLOY YAS (run on Laptop A after all 4 nodes joined) ────────────────
deploy_yas() {
    log "Deploying YAS infrastructure (same config as desktop test)..."

    # Check all 4 nodes are ready
    local READY_COUNT
    READY_COUNT=$(kubectl get nodes --no-headers | grep -c "Ready" || true)
    if [ "$READY_COUNT" -lt 4 ]; then
        warn "Only $READY_COUNT/4 nodes ready. Continuing anyway..."
    fi

    # Run the same Helm deploy logic (mirrors run-yas-setup.ps1)
    cd "$(dirname "$0")/../k8s/deploy" || exit 1
    bash setup-keycloak.sh
    bash setup-redis.sh
    bash setup-cluster.sh
    log "YAS deployment complete! Run: kubectl get pods -A"
}

# ── UPDATE HOSTS on all laptops ──────────────────────────────────────────
update_hosts() {
    local MASTER_IP
    MASTER_IP=$(tailscale ip -4 2>/dev/null)
    [ -z "$MASTER_IP" ] && read -rp "Enter Laptop A Tailscale IP: " MASTER_IP

    log "Add these lines to /etc/hosts on ALL 4 laptops:"
    echo ""
    echo "$MASTER_IP  storefront-ui.dev.yas.local.com"
    echo "$MASTER_IP  backoffice-ui.dev.yas.local.com"
    echo "$MASTER_IP  swagger-ui.dev.yas.local.com"
    echo "$MASTER_IP  identity.dev.yas.local.com"
    echo "$MASTER_IP  storefront-bff.dev.yas.local.com"
    echo "$MASTER_IP  backoffice-bff.dev.yas.local.com"
    echo "$MASTER_IP  akhq.yas.local.com"
    echo "$MASTER_IP  kibana.yas.local.com"
    echo "$MASTER_IP  pgadmin.yas.local.com"
    echo "$MASTER_IP  grafana.yas.local.com"
}

# ── Main ─────────────────────────────────────────────────────────────────
case "$ROLE" in
    master)  setup_master ;;
    worker)  setup_worker "$@" ;;
    label)   label_workers ;;
    deploy)  deploy_yas ;;
    hosts)   update_hosts ;;
    *)
        echo "Usage:"
        echo "  $0 master                                              # Laptop A"
        echo "  $0 worker <MASTER_IP> <TOKEN> <laptop-b|c|d>          # Laptop B/C/D"
        echo "  $0 label                                               # Label workers (run on A)"
        echo "  $0 deploy                                              # Deploy YAS (run on A)"
        echo "  $0 hosts                                               # Print /etc/hosts entries"
        exit 1
        ;;
esac
