
set -x

#Read configuration value from cluster-config.yaml file
read -rd '' DOMAIN POSTGRESQL_USERNAME POSTGRESQL_PASSWORD \
BOOTSTRAP_ADMIN_USERNAME BOOTSTRAP_ADMIN_PASSWORD \
KEYCLOAK_BACKOFFICE_REDIRECT_URL KEYCLOAK_STOREFRONT_REDIRECT_URL \
< <(python3 -c "import yaml; cfg = yaml.safe_load(open('./cluster-config.yaml')); print('\n'.join([
    str(cfg.get('domain', '')),
    str(cfg.get('postgresql', {}).get('username', '')),
    str(cfg.get('postgresql', {}).get('password', '')),
    str(cfg.get('keycloak', {}).get('bootstrapAdmin', {}).get('username', '')),
    str(cfg.get('keycloak', {}).get('bootstrapAdmin', {}).get('password', '')),
    str(cfg.get('keycloak', {}).get('backofficeRedirectUrl', '')),
    str(cfg.get('keycloak', {}).get('storefrontRedirectUrl', ''))
]))")

#Install CRD keycloak
kubectl create namespace keycloak
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/kubernetes.yml -n keycloak

# Install keycloak
helm upgrade --install keycloak ./keycloak/keycloak \
--namespace keycloak \
--set hostname="identity.$DOMAIN" \
--set postgresql.username="$POSTGRESQL_USERNAME" \
--set postgresql.password="$POSTGRESQL_PASSWORD" \
--set bootstrapAdmin.username="$BOOTSTRAP_ADMIN_USERNAME" \
--set bootstrapAdmin.password="$BOOTSTRAP_ADMIN_PASSWORD" \
--set backofficeRedirectUrl="$KEYCLOAK_BACKOFFICE_REDIRECT_URL" \
--set storefrontRedirectUrl="$KEYCLOAK_STOREFRONT_REDIRECT_URL"
