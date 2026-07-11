# Hướng dẫn Triển khai YAS trên Kubernetes
## Các tài liệu tham khảo cài đặt tài nguyên Cluster
- **Postgresql:** https://github.com/zalando/postgres-operator
- **Elasticsearch:** https://github.com/elastic/cloud-on-k8s
- **Kafka:** https://github.com/strimzi/strimzi-kafka-operator
- **Debezium Connect:** https://debezium.io/documentation/reference/stable/operations/kubernetes.html
- **Keycloak:** https://www.keycloak.org/operator/installation
- **Redis:** https://artifacthub.io/packages/helm/bitnami/redis
- **Reloader:** https://github.com/stakater/Reloader
- **Prometheus:** https://github.com/prometheus-community/helm-charts/tree/main/charts/kube-prometheus-stack
- **Grafana:** https://github.com/grafana-operator/grafana-operator
- **Loki:** https://github.com/grafana/loki/tree/main/production/helm/loki
- **Tempo:** https://github.com/grafana/helm-charts/tree/main/charts/tempo
- **Promtail:** https://github.com/grafana/helm-charts/tree/main/charts/promtail
- **Opentelemetry:** https://github.com/open-telemetry/opentelemetry-operator

---

## Các bước cài đặt trực tiếp trên Local
- Yêu cầu cấu hình Minikube tối thiểu **16G Memory** và **40G Disk Space** chạy trên hệ điều hành Ubuntu hoặc WSL2:
```shell
minikube start --disk-size='40000mb' --memory='16g'
```
- Kích hoạt add-on Ingress trên Minikube:
```shell
minikube addons enable ingress
```
- Cài đặt công cụ quản lý Helm:
  https://helm.sh/
- Cài đặt công cụ `yq` (dùng để đọc và cập nhật file cấu hình YAML):
  https://github.com/mikefarah/yq
- Di chuyển vào thư mục cài đặt `k8s/deploy/`.
- Thực thi file [setup-keycloak.sh](setup-keycloak.sh) để cấu hình máy chủ quản lý định danh và phân quyền Keycloak:
```shell
./setup-keycloak.sh
```
- Thực thi file [setup-redis.sh](setup-redis.sh) để cài đặt máy chủ Redis lưu trữ session cho backend:
```shell
./setup-redis.sh
```
- Thực thi file [setup-cluster.sh](setup-cluster.sh) để cài đặt toàn bộ dịch vụ hạ tầng nền tảng: `postgresql`, `elasticsearch`, `kafka`, `debezium connect`.
```shell
./setup-cluster.sh
```
- Kiểm tra và đảm bảo tất cả dịch vụ hạ tầng đã khởi chạy thành công trên các namespace: `postgres`, `elasticsearch`, `kafka`, `keycloak`.
- Khi tất cả các dịch vụ trên ở trạng thái hoạt động bình thường, thực thi file [deploy-yas-applications.sh](deploy-yas-applications.sh) để triển khai các ứng dụng của YAS lên namespace `dev` (hoặc cấu hình tùy ý):
```shell
./deploy-yas-applications.sh dev
```
Toàn bộ các microservices của YAS sẽ được triển khai lên namespace của bạn.

---

## Cấu hình file Hosts cục bộ
Chỉnh sửa file `/etc/hosts` (trên Linux/WSL) hoặc `C:\Windows\System32\drivers\etc\hosts` (trên Windows) để phân giải tên miền:
```shell
192.168.49.2 pgoperator.yas.local.com
192.168.49.2 pgadmin.yas.local.com
192.168.49.2 akhq.yas.local.com
192.168.49.2 kibana.yas.local.com
192.168.49.2 identity.yas.local.com
192.168.49.2 backoffice.yas.local.com
192.168.49.2 storefront.yas.local.com
192.168.49.2 grafana.yas.local.com
```
*Lưu ý: Thay `192.168.49.2` bằng IP thực tế của máy ảo Minikube lấy bằng lệnh:*
```shell
minikube ip
```

---

## Thông tin tài khoản Admin mặc định của Keycloak
Tài khoản và mật khẩu admin của Keycloak được lưu trữ trong secret tên là `keycloak-credentials` thuộc namespace `keycloak`.
Chạy lệnh sau để giải mã mật khẩu quản trị:
```shell
kubectl get secret keycloak-credentials -n keycloak -o jsonpath="{.data.password}" | base64 --decode
```
*Đây là tài khoản admin khởi tạo ban đầu. Để đảm bảo an toàn bảo mật, bạn nên tạo một tài khoản admin cố định mới và xóa tài khoản mặc định này.*

---

## Cấu hình Cluster và Ứng dụng YAS
- Mọi thiết lập của cluster được định nghĩa tại file [cluster-config.yaml](cluster-config.yaml) trong thư mục `k8s/deploy/`.
- Mọi cấu hình chung của ứng dụng YAS được đặt trong Helm chart tên là `yas-configuration`. Tham khảo các giá trị cấu hình tại file [values.yaml](../charts/yas-configuration/values.yaml).
- Tất cả các Helm charts của các microservices YAS được lưu trong thư mục `charts`.
- Xem thêm về các phiên bản đóng gói Helm chart của dự án tại: [https://nashtech-garage.github.io/yas/](https://nashtech-garage.github.io/yas/)

---

## Giám sát hệ thống (Observability)
Dự án YAS áp dụng mô hình thu thập dữ liệu giám sát theo tiêu chuẩn Open Telemetry:
- **Lưu trữ Logs**: Sử dụng Promtail thu thập log từ tất cả ứng dụng gửi về Open Telemetry Collector, sau đó lưu vào máy chủ Loki.
- **Traces và Metrics**: Các ứng dụng gửi dữ liệu tracing và metrics về Open Telemetry Collector, sau đó collector phân phối dữ liệu tới máy chủ Tempo và Prometheus.
- Xem chi tiết file cấu hình Open Telemetry Collector tại [opentelemetry](./observability/opentelemetry/values.yaml).

### Cách xem Logs trên giao diện Grafana
Ở menu bên trái, chọn `Explore` -> chọn datasource là `Loki` -> cấu hình bộ lọc theo nhãn (Label filters):
- `namespace`
- `container` (Tên microservice cần xem)

Loki cũng hỗ trợ liên kết tìm kiếm theo mã `traceId` kết hợp với Tempo để xem biểu đồ luồng request (Node graph) giữa các dịch vụ cực kỳ trực quan.
