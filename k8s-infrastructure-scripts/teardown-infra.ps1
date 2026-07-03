Write-Host "Deleting all shared and environment-specific infrastructure..."

# Xóa hạ tầng chung
helm uninstall elasticsearch-cluster --namespace elasticsearch
helm uninstall elastic-operator --namespace elasticsearch
kubectl delete namespace elasticsearch

helm uninstall kafka-cluster --namespace kafka
helm uninstall kafka-operator --namespace kafka
kubectl delete namespace kafka

helm uninstall redis --namespace redis
kubectl delete namespace redis

helm uninstall postgres --namespace postgres
helm uninstall postgres-operator --namespace postgres
kubectl delete namespace postgres

# Xóa hạ tầng cơ bản
helm uninstall keycloak --namespace yas
helm uninstall yas-configuration --namespace yas

# Xóa hạ tầng nâng cao
helm uninstall keycloak --namespace dev
helm uninstall yas-configuration --namespace dev
helm uninstall keycloak --namespace staging
helm uninstall yas-configuration --namespace staging

Write-Host "Infrastructure cleaned up successfully!"
