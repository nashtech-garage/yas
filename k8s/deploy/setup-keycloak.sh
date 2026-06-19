
set -x

#Read configuration value from cluster-config.yaml file
read -rd '' DOMAIN POSTGRESQL_USERNAME POSTGRESQL_PASSWORD \
BOOTSTRAP_ADMIN_USERNAME BOOTSTRAP_ADMIN_PASSWORD \
< <(yq -r '.domain,
  .postgresql.username, .postgresql.password,
  .keycloak.bootstrapAdmin.username, .keycloak.bootstrapAdmin.password' ./cluster-config.yaml)

KEYCLOAK_BACKOFFICE_REDIRECT_URLS=$(yq -o=json '.keycloak.backofficeRedirectUrls' ./cluster-config.yaml)
KEYCLOAK_STOREFRONT_REDIRECT_URLS=$(yq -o=json '.keycloak.storefrontRedirectUrls' ./cluster-config.yaml)

#Install CRD keycloak
kubectl create namespace keycloak --dry-run=client -o yaml | kubectl apply -f -
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
--set-json backofficeRedirectUrls="$KEYCLOAK_BACKOFFICE_REDIRECT_URLS" \
--set-json storefrontRedirectUrls="$KEYCLOAK_STOREFRONT_REDIRECT_URLS"

# Patch CoreDNS to resolve identity.<DOMAIN> inside the cluster.
# Pods cannot read the host's /etc/hosts, so identity.yas.local.com (used by BFF
# services for OAuth2 issuer-uri) would be unresolvable without this patch.
# Adds the Keycloak ClusterIP to the existing CoreDNS hosts block.

# Wait for keycloak-service to be created by the Keycloak operator (may take 30-60s)
echo "Waiting for keycloak-service to be created..."
until kubectl get svc keycloak-service -n keycloak &>/dev/null; do
  echo "  keycloak-service not ready yet, retrying in 5s..."
  sleep 5
done

KEYCLOAK_IP=$(kubectl get svc keycloak-service -n keycloak -o jsonpath='{.spec.clusterIP}')
if [ -z "$KEYCLOAK_IP" ]; then
  echo "ERROR: Could not get Keycloak ClusterIP, skipping CoreDNS patch"
  exit 1
fi
CURRENT_COREFILE=$(kubectl get configmap coredns -n kube-system -o jsonpath='{.data.Corefile}')
if ! echo "$CURRENT_COREFILE" | grep -q "identity.$DOMAIN"; then
  export KEYCLOAK_IP DOMAIN
  kubectl get configmap coredns -n kube-system -o json | \
    python3 -c "
import sys, json, os
cm = json.load(sys.stdin)
corefile = cm['data']['Corefile']
keycloak_ip = os.environ['KEYCLOAK_IP']
domain = os.environ['DOMAIN']
entry = '       ' + keycloak_ip + ' identity.' + domain + '\n'
corefile = corefile.replace('       fallthrough\n    }', entry + '       fallthrough\n    }', 1)
cm['data']['Corefile'] = corefile
print(json.dumps(cm))
" | kubectl apply -f -
  kubectl rollout restart deployment coredns -n kube-system
  kubectl rollout status deployment coredns -n kube-system --timeout=60s
fi
