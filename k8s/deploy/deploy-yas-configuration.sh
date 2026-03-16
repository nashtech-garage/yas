
set -x

# Auto restart when change configmap or secret
helm repo add stakater https://stakater.github.io/stakater-charts
helm repo update

helm dependency build ../charts/yas-configuration

HELM_SECRET_ARGS=""

if [ -n "${KEYCLOAK_BACKOFFICE_BFF_CLIENT_SECRET:-}" ]; then
	HELM_SECRET_ARGS="$HELM_SECRET_ARGS --set-string credentials.keycloak.backofficeBffClientSecret=${KEYCLOAK_BACKOFFICE_BFF_CLIENT_SECRET}"
fi

if [ -n "${KEYCLOAK_STOREFRONT_BFF_CLIENT_SECRET:-}" ]; then
	HELM_SECRET_ARGS="$HELM_SECRET_ARGS --set-string credentials.keycloak.storefrontBffClientSecret=${KEYCLOAK_STOREFRONT_BFF_CLIENT_SECRET}"
fi

if [ -n "${KEYCLOAK_CUSTOMER_MANAGEMENT_CLIENT_SECRET:-}" ]; then
	HELM_SECRET_ARGS="$HELM_SECRET_ARGS --set-string credentials.keycloak.customerManagementClientSecret=${KEYCLOAK_CUSTOMER_MANAGEMENT_CLIENT_SECRET}"
fi

if [ -n "$HELM_SECRET_ARGS" ]; then
	# shellcheck disable=SC2086
	helm upgrade --install yas-configuration ../charts/yas-configuration \
	--namespace yas --create-namespace \
	$HELM_SECRET_ARGS
else
	helm upgrade --install yas-configuration ../charts/yas-configuration \
	--namespace yas --create-namespace
fi

