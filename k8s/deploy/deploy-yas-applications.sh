#!/bin/bash
set -x

# Auto restart when change configmap or secret
helm repo add stakater https://stakater.github.io/stakater-charts
helm repo update

# Namespace parameter (defaults to dev)
NAMESPACE=${1:-dev}

read -rd '' DOMAIN \
< <(yq -r '.domain' ./cluster-config.yaml)

helm dependency build ../charts/backoffice-bff
helm upgrade --install backoffice-bff ../charts/backoffice-bff \
--namespace "$NAMESPACE" --create-namespace \
--set backend.ingress.host="backoffice.$NAMESPACE.$DOMAIN"

helm dependency build ../charts/backoffice-ui
helm upgrade --install backoffice-ui ../charts/backoffice-ui \
--namespace "$NAMESPACE" --create-namespace

sleep 60

helm dependency build ../charts/storefront-bff
helm upgrade --install storefront-bff ../charts/storefront-bff \
--namespace "$NAMESPACE" --create-namespace \
--set backend.ingress.host="storefront.$NAMESPACE.$DOMAIN"

helm dependency build ../charts/storefront-ui
helm upgrade --install storefront-ui ../charts/storefront-ui \
--namespace "$NAMESPACE" --create-namespace

sleep 60

helm upgrade --install swagger-ui ../charts/swagger-ui \
--namespace "$NAMESPACE" --create-namespace \
--set ingress.host="api.$NAMESPACE.$DOMAIN"

sleep 20

for chart in {"cart","customer","inventory","media","order","product","search","tax","sampledata"} ; do
    helm dependency build ../charts/"$chart"
    helm upgrade --install "$chart" ../charts/"$chart" \
    --namespace "$NAMESPACE" --create-namespace \
    --set backend.ingress.host="api.$NAMESPACE.$DOMAIN"
    sleep 60
done

