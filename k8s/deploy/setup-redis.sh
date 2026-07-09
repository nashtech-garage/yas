
set -x

#Read configuration value from cluster-config.yaml file
read -rd '' REDIS_PASSWORD \
< <(python3 -c "import yaml; cfg = yaml.safe_load(open('./cluster-config.yaml')); print(cfg.get('redis', {}).get('password', ''))")

helm upgrade --install redis \
  --set auth.password="$REDIS_PASSWORD" \
  oci://registry-1.docker.io/bitnamicharts/redis -n redis --create-namespace
