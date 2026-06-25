#!/bin/bash
# =============================================================================
# Phase 2 - Nguười A: Deploy YAS đa namespace (parametrized)
# Usage:  ./deploy-env.sh <env>      ví dụ:  ./deploy-env.sh dev
# Đọc biến từ ./env/<env>.env (NAMESPACE, DOMAIN, IMAGE_TAG, IMAGE_REGISTRY)
# Hạ tầng dùng chung (Postgres/Kafka/ES/Keycloak) KHÔNG nhân bản: các app
# tham chiếu qua FQDN cross-namespace đã cấu hình trong yas-configuration.
# =============================================================================
set -euo pipefail

ENV_NAME="${1:-}"
if [[ -z "$ENV_NAME" ]]; then
  echo "Usage: $0 <env>   (cần file ./env/<env>.env)"
  exit 1
fi

ENV_FILE="./env/${ENV_NAME}.env"
if [[ ! -f "$ENV_FILE" ]]; then
  echo "ERROR: không tìm thấy env file: $ENV_FILE"
  exit 1
fi

# Nạp biến môi trường
# shellcheck disable=SC1090
set -a; source "$ENV_FILE"; set +a

: "${NAMESPACE:?NAMESPACE chưa đặt trong $ENV_FILE}"
: "${DOMAIN:?DOMAIN chưa đặt trong $ENV_FILE}"
: "${IMAGE_TAG:?IMAGE_TAG chưa đặt trong $ENV_FILE}"
IMAGE_REGISTRY="${IMAGE_REGISTRY:-}"

echo "==> Deploy YAS | namespace='$NAMESPACE' domain='$DOMAIN' tag='$IMAGE_TAG' registry='${IMAGE_REGISTRY:-<chart default>}'"

# Map tên chart -> tên image (mặc định yas-<chart>; riêng UI khác tên)
image_name_for() {
  case "$1" in
    storefront-ui) echo "yas-storefront" ;;
    backoffice-ui) echo "yas-backoffice" ;;
    *)             echo "yas-$1" ;;
  esac
}

# Sinh các cờ --set cho image. $1=chart, $2=prefix values (backend|ui)
image_set_args() {
  local chart="$1" prefix="$2"

  # payment-paypal: image upstream (ghcr.io/.../yas-payment-paypal:latest) hỏng -> "no main manifest".
  # Khi KHÔNG có registry riêng, dùng image build tay đã nạp vào minikube (yas-payment-paypal:local).
  if [[ "$chart" == "payment-paypal" && -z "$IMAGE_REGISTRY" ]]; then
    printf '%s\n' "--set" "${prefix}.image.repository=${PAYPAL_LOCAL_IMAGE:-yas-payment-paypal}" \
                  "--set" "${prefix}.image.tag=${PAYPAL_LOCAL_TAG:-local}" \
                  "--set" "${prefix}.image.pullPolicy=IfNotPresent"
    return
  fi

  local args=("--set" "${prefix}.image.tag=${IMAGE_TAG}")
  if [[ -n "$IMAGE_REGISTRY" ]]; then
    args+=("--set" "${prefix}.image.repository=${IMAGE_REGISTRY}/$(image_name_for "$chart")")
  fi
  printf '%s\n' "${args[@]}"
}

helm repo add stakater https://stakater.github.io/stakater-charts
helm repo update

# 1) Cấu hình Spring dùng chung (ConfigMap/Secret) vào namespace này
helm dependency build ../charts/yas-configuration
helm upgrade --install yas-configuration ../charts/yas-configuration \
  --namespace "$NAMESPACE" --create-namespace

# 2) BFF + UI (theo đúng thứ tự bản gốc)
helm dependency build ../charts/backoffice-bff
helm upgrade --install backoffice-bff ../charts/backoffice-bff \
  --namespace "$NAMESPACE" --create-namespace \
  --set backend.ingress.host="backoffice.$DOMAIN" \
  $(image_set_args backoffice-bff backend)

helm dependency build ../charts/backoffice-ui
helm upgrade --install backoffice-ui ../charts/backoffice-ui \
  --namespace "$NAMESPACE" --create-namespace \
  $(image_set_args backoffice-ui ui)

sleep 30

helm dependency build ../charts/storefront-bff
helm upgrade --install storefront-bff ../charts/storefront-bff \
  --namespace "$NAMESPACE" --create-namespace \
  --set backend.ingress.host="storefront.$DOMAIN" \
  $(image_set_args storefront-bff backend)

helm dependency build ../charts/storefront-ui
helm upgrade --install storefront-ui ../charts/storefront-ui \
  --namespace "$NAMESPACE" --create-namespace \
  $(image_set_args storefront-ui ui)

sleep 30

# swagger-ui dùng ingress.host ở top-level (không phải backend.*)
helm upgrade --install swagger-ui ../charts/swagger-ui \
  --namespace "$NAMESPACE" --create-namespace \
  --set ingress.host="api.$DOMAIN"

sleep 20

# 3) Các microservice backend (loop)
for chart in cart customer inventory location media order payment payment-paypal product promotion rating search tax recommendation webhook sampledata ; do
  helm dependency build "../charts/$chart"
  helm upgrade --install "$chart" "../charts/$chart" \
    --namespace "$NAMESPACE" --create-namespace \
    --set backend.ingress.host="api.$DOMAIN" \
    $(image_set_args "$chart" backend)
  sleep 30
done

echo "==> Hoàn tất deploy env '$ENV_NAME' vào namespace '$NAMESPACE'."
