#!/bin/bash
set -x

# Auto restart when change configmap or secret
helm repo add stakater https://stakater.github.io/stakater-charts
helm repo update

read -rd '' DOMAIN \
< <(yq -r '.domain' ./cluster-config.yaml)

helm dependency update ../charts/backoffice-bff
helm upgrade --install backoffice-bff ../charts/backoffice-bff \
--namespace yas --create-namespace \
--set backend.ingress.host="backoffice.$DOMAIN"

helm dependency update ../charts/backoffice-ui
helm upgrade --install backoffice-ui ../charts/backoffice-ui \
--namespace yas --create-namespace

sleep 10

helm dependency update ../charts/storefront-bff
helm upgrade --install storefront-bff ../charts/storefront-bff \
--namespace yas --create-namespace \
--set backend.ingress.host="storefront.$DOMAIN"

helm dependency update ../charts/storefront-ui
helm upgrade --install storefront-ui ../charts/storefront-ui \
--namespace yas --create-namespace

sleep 10

helm upgrade --install swagger-ui ../charts/swagger-ui \
--namespace yas --create-namespace \
--set ingress.host="api.$DOMAIN"

sleep 5

for chart in {"cart","customer","inventory","media","order","product","search","tax","sampledata"} ; do
    helm dependency update ../charts/"$chart"
    helm upgrade --install "$chart" ../charts/"$chart" \
    --namespace yas --create-namespace \
    --set backend.ingress.host="api.$DOMAIN"
    sleep 10
done
