# YAS K8S Local Deployment Guide (Updated)

## Yêu cầu

- Minikube với tối thiểu 16GB RAM, 40GB disk, chạy trên Ubuntu
- `helm` đã cài
- `yq` đã cài (https://github.com/mikefarah/yq)

## Các fix đã áp dụng so với README gốc

| Vấn đề | Fix |
|---|---|
| `postgres/postgresql/templates/postgresql.yaml` có lỗi template syntax ở `recommendation` và `webhook` | Sửa `{ { .Values.username } }` → `{{ .Values.username }}` |
| Strimzi operator mới (0.46+) không còn hỗ trợ Zookeeper | Chuyển sang KRaft mode, tạo `KafkaNodePool` resource |
| `deploy-yas-configuration.sh` không có trong README gốc | Cần chạy trước `deploy-yas-applications.sh` |
| Elasticsearch URL thiếu port trong `yas-configuration/values.yaml` | Thêm `:9200` vào URL |

---

## Bước 1 — Khởi động Minikube

```shell
minikube start --disk-size='40000mb' --memory='16g'
minikube addons enable ingress
```

## Bước 2 — Di chuyển vào thư mục deploy

```shell
cd k8s/deploy
```

## Bước 3 — Cài Keycloak (Identity Provider)

```shell
./setup-keycloak.sh
```

Chờ Keycloak running (phụ thuộc vào PostgreSQL được cài ở bước 5):
```shell
kubectl get pods -n keycloak -w
```

> **Lưu ý:** `keycloak-0` sẽ crash loop cho đến khi PostgreSQL ở bước 5 running. Bình thường.

## Bước 4 — Cài Redis

```shell
./setup-redis.sh
```

## Bước 5 — Cài hạ tầng (PostgreSQL, Kafka, Elasticsearch, Observability)

```shell
./setup-cluster.sh
```

### Verify PostgreSQL

```shell
kubectl get pods -n postgres
```

Cần thấy pod `postgresql-0` với status `Running`. Nếu **chỉ thấy** `postgres-operator` và `pgadmin` mà **không có** `postgresql-0`, chạy thủ công:

```shell
helm upgrade --install postgres ./postgres/postgresql \
  --create-namespace --namespace postgres \
  --set replicas=1 \
  --set username=yasadminuser \
  --set password=admin
```

### Verify Kafka

```shell
kubectl get pods -n kafka
```

Cần thấy `kafka-cluster-combined-0` với status `Running`.

Nếu Strimzi operator báo lỗi `No KafkaNodePools found` hoặc `ZooKeeper-based clusters are not supported`, xem [phần xử lý Kafka bên dưới](#xử-lý-lỗi-kafka).

### Verify Keycloak

Sau khi PostgreSQL running, Keycloak sẽ tự kết nối lại. Chờ:
```shell
kubectl get pods -n keycloak -w
```

Cần thấy `keycloak-0` là `1/1 Running` và một pod `yas-realm-kc-*` là `Completed` (realm import thành công).

## Bước 6 — Deploy configuration

> **Quan trọng:** Bước này bị thiếu trong README gốc, phải chạy trước khi deploy applications.

```shell
./deploy-yas-configuration.sh
```

## Bước 7 — Deploy YAS applications

```shell
./deploy-yas-applications.sh
```

Script này mất khoảng 15-20 phút do có nhiều `sleep 60`.

Kiểm tra:
```shell
kubectl get pods -n yas -w
```

## Bước 8 — Deploy nginx API Gateway

Storefront-BFF và Backoffice-BFF đều route `/api/**` đến service tên `nginx` trong namespace `yas`. Docker Compose dùng container `nginx` tích hợp sẵn, nhưng trong K8s cần deploy thủ công:

```shell
kubectl apply -f nginx-api-gateway.yaml
kubectl rollout status deployment/nginx -n yas
```

> **Lưu ý:** Bước này phải chạy **sau** bước 7 vì namespace `yas` phải tồn tại trước.

## Bước 9 — Cấu hình /etc/hosts

Lấy IP của Minikube:
```shell
minikube ip
```

Thêm vào `/etc/hosts` (thay `192.168.49.2` bằng IP thực):
```
192.168.49.2 pgoperator.yas.local.com
192.168.49.2 pgadmin.yas.local.com
192.168.49.2 akhq.yas.local.com
192.168.49.2 kibana.yas.local.com
192.168.49.2 identity.yas.local.com
192.168.49.2 backoffice.yas.local.com
192.168.49.2 storefront.yas.local.com
192.168.49.2 grafana.yas.local.com
192.168.49.2 api.yas.local.com
```

---

## Xử lý lỗi Kafka

Phiên bản Strimzi mới (0.39+) không còn hỗ trợ Zookeeper. Chart đã được cập nhật sang KRaft mode. Nếu gặp lỗi, chạy:

```shell
# Xóa Kafka cluster cũ
kubectl delete kafka kafka-cluster -n kafka

# Install lại
helm upgrade --install kafka-cluster ./kafka/kafka-cluster \
  --namespace kafka \
  --set kafka.replicas=1 \
  --set zookeeper.replicas=1 \
  --set postgresql.username=yasadminuser \
  --set postgresql.password=admin
```

---

## Clean up toàn bộ

Để xóa toàn bộ và làm lại từ đầu:

```shell
# Xóa toàn bộ namespaces
kubectl delete namespace yas
kubectl delete namespace postgres
kubectl delete namespace kafka
kubectl delete namespace keycloak
kubectl delete namespace elasticsearch
kubectl delete namespace redis
kubectl delete namespace observability
kubectl delete namespace cert-manager

# Xóa Helm releases còn sót lại (nếu có)
helm list -A

# Xóa Keycloak CRDs
kubectl delete -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl delete -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
```

Hoặc nếu muốn clean hoàn toàn:
```shell
minikube delete
minikube start --disk-size='40000mb' --memory='16g'
minikube addons enable ingress
```

---

## Kiểm tra nhanh sau deploy

```shell
# Tất cả namespaces
kubectl get pods -A | grep -v Running | grep -v Completed

# Namespace yas (bao gồm nginx)
kubectl get pods -n yas

# Ingress
kubectl get ingress -A
```
