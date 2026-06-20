#!/bin/bash
set -e

echo "=========================================================="
echo "Installing ArgoCD on local Kubernetes cluster..."
echo "=========================================================="

# Create the namespace for ArgoCD
kubectl create namespace argocd || true

# Apply the official stable ArgoCD manifests with server-side apply and force conflicts
kubectl apply --server-side --force-conflicts -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

echo "⏳ Waiting for ArgoCD server to deploy..."
kubectl rollout status deployment/argocd-server -n argocd --timeout=300s

echo "⚙️ Configuring NodePort for ArgoCD Web Console..."
# Patch the service to type NodePort on static ports 30088 (HTTP) and 30089 (HTTPS)
kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "NodePort", "ports": [{"port": 80, "targetPort": 8080, "nodePort": 30088}, {"port": 443, "targetPort": 8080, "nodePort": 30089}]}}'

echo "=========================================================="
echo "🎉 ArgoCD Installed Successfully!"
echo "=========================================================="
echo "Access URL (HTTP) : http://localhost:30088"
echo "Access URL (HTTPS): https://localhost:30089"
echo ""
echo "🔐 Decoded Initial Admin Password:"
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
echo ""
echo "=========================================================="
