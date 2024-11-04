#!/bin/bash
set -x

# Auto restart when change configmap or secret
helm repo add stakater https://stakater.github.io/stakater-charts
helm repo update

read -rd '' DOMAIN \
< <(yq -r '.domain' ./cluster-config.yaml)

helm dependency build ../charts/backoffice-bff
helm upgrade --install backoffice-bff ../charts/backoffice-bff \
--namespace yas --create-namespace \
--set backend.ingress.host="backoffice.$DOMAIN"

helm dependency build ../charts/backoffice-ui
helm upgrade --install backoffice-ui ../charts/backoffice-ui \
--namespace yas --create-namespace

sleep 60

helm dependency build ../charts/storefront-bff
helm upgrade --install storefront-bff ../charts/storefront-bff \
--namespace yas --create-namespace \
--set backend.ingress.host="storefront.$DOMAIN"

helm dependency build ../charts/storefront-ui
helm upgrade --install storefront-ui ../charts/storefront-ui \
--namespace yas --create-namespace

sleep 60

helm upgrade --install swagger-ui ../charts/swagger-ui \
--namespace yas --create-namespace \
--set ingress.host="api.$DOMAIN"

sleep 20

for chart in {"cart","customer","inventory","location","media","order","payment","payment-paypal","product","promotion","rating","search","tax"} ; do
    helm dependency build ../charts/"$chart"
    helm upgrade --install "$chart" ../charts/"$chart" \
    --namespace yas --create-namespace \
    --set backend.ingress.host="api.$DOMAIN"
    sleep 60
done

helm upgrade --install eventuate-cdc ../charts/eventuate-cdc \
--namespace yas --create-namespace