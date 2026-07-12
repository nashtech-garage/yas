# Walkthrough: Tối Ưu Hóa CPU Requests & Vận Hành Cluster 2 Nodes

Dưới đây là chi tiết các bước tối ưu hóa cấu hình tài nguyên của hệ thống để hạ số lượng node trong cụm Kubernetes từ 3 nodes xuống **2 nodes (Memory-Optimized `m-2vcpu-16gb`)**, giúp tiết kiệm chi phí và chuẩn bị tốt nhất cho buổi demo.

---

## 1. Hiện Tượng & Điểm Kẹt Ban Đầu
- Ban đầu, khi scale down node pool về **2 nodes**, một số lượng lớn pod ở môi trường `dev` và `staging` rơi vào trạng thái `Pending` do lỗi **`Insufficient cpu`**.
- Lý do: Tổng CPU Requests mặc định của toàn bộ microservices và databases hạ tầng (Kafka, Elasticsearch, PostgreSQL, Keycloak, Observability) vượt quá mức CPU Allocatable của 2 node (~3.8 vCPU).
- Đặc biệt, do cơ chế fallback của Helm, dù comment CPU requests ở `dev/values.yaml` và `staging/values.yaml` thì nó vẫn tự động lấy CPU request từ base Helm charts (VD: `cpu: 50m` cho backend chart).

---

## 2. Giải Pháp & Các Bước Triển Khai

### 2.1. Tối Ưu Hóa CPU Requests Cho Microservices
Thay vì comment (dẫn đến fallback về default chart), chúng ta đã khai báo tường minh CPU requests ở mức tối thiểu để scheduler dễ dàng lập lịch:
- Sửa đổi cấu hình trong [dev/values.yaml](file:///d:/Y3S2/[DevOps]_Project02/k8s/environments/dev/values.yaml) và [staging/values.yaml](file:///d:/Y3S2/[DevOps]_Project02/k8s/environments/staging/values.yaml):
  - Set cứng `requests.cpu: 1m` cho tất cả backend microservices.
  - Set cứng `requests.cpu: 1m` cho các frontend UI (`storefront-ui`, `backoffice-ui`).
- Để đảm bảo không có service nào tự động nhận CPU request mặc định, chúng ta cũng vô hiệu hóa (comment) CPU requests mặc định trong các file base chart:
  - Comment `cpu: 50m` trong [backend/values.yaml](file:///d:/Y3S2/[DevOps]_Project02/k8s/charts/backend/values.yaml).
  - Comment `cpu: 25m` trong [ui/values.yaml](file:///d:/Y3S2/[DevOps]_Project02/k8s/charts/ui/values.yaml).
  - Comment `cpu: 10m` trong [swagger-ui/values.yaml](file:///d:/Y3S2/[DevOps]_Project02/k8s/charts/swagger-ui/values.yaml).

### 2.2. Tối Ưu Hóa CPU Requests Cho Hạ Tầng (Databases)
Các database hạ tầng và operators cũng được hạ CPU requests xuống mức an toàn để vận hành ổn định trên 2 nodes:
- **Kafka Cluster**: Giảm CPU requests của Kafka, Zookeeper, TopicOperator, và UserOperator từ `100m - 250m` xuống **`20m`** trong [kafka-cluster.yaml](file:///d:/Y3S2/[DevOps]_Project02/k8s/deploy/kafka/kafka-cluster/templates/kafka-cluster.yaml).
- **Debezium Connect**: Giảm CPU requests của Debezium Connect từ `250m` xuống **`20m`** trong [debezium-connect-cluster.yaml](file:///d:/Y3S2/[DevOps]_Project02/k8s/deploy/kafka/kafka-cluster/templates/debezium-connect-cluster.yaml).
- **Elasticsearch**: Giảm CPU requests của nodeset Elasticsearch từ `250m` xuống **`20m`** trong [elasticsearch.yaml](file:///d:/Y3S2/[DevOps]_Project02/k8s/deploy/elasticsearch/elasticsearch-cluster/templates/elasticsearch.yaml).
- **Keycloak & Keycloak Operator**:
  - Giảm CPU requests của Keycloak Operator từ `300m` xuống **`10m`** (bằng cách patch deployment `keycloak-operator`).
  - Giảm CPU requests của Custom Resource Keycloak từ `100m` xuống **`20m`** trong [keycloak.yaml](file:///d:/Y3S2/[DevOps]_Project02/k8s/deploy/keycloak/keycloak/templates/keycloak.yaml).
- **Redis & PostgreSQL**: Giữ nguyên ở mức `100m` CPU requests để bảo toàn hiệu năng đọc ghi dữ liệu.

### 2.3. Khôi Phục CPU Limits Để Tránh Throttling
- *Lưu ý quan trọng*: Ban đầu, quá trình thay thế regex tự động đã vô tình giới hạn CPU Limits của các container xuống `1m`. Điều này khiến JVM (Java Virtual Machine) của microservices bị throttling (nghẹt) đến mức không thể khởi động xong hoặc ghi logs.
- *Khắc phục*: Chúng ta đã khôi phục lại các dòng comment CPU limits (tức là không giới hạn limits hoặc trả về limits mặc định `500m` - `100m` để microservices có thể tự do burst CPU lên khi khởi động/xử lý requests).

### 2.4. Scale Down Node Pool Về 2 Nodes & Tái Đồng Bộ (ArgoCD)
- Sử dụng `doctl` scale down node pool `devops-project02-mem-opt` từ 3 nodes xuống **2 nodes** thành công.
- Thực hiện force refresh và hard sync cho các ArgoCD applications (`dev-yas-app` và `staging-yas-app`).
- Khởi động lại (delete/rollout restart) toàn bộ pod cũ để dọn sạch tài nguyên và thúc đẩy Kubernetes lập lịch các pod mới theo cấu hình tối ưu.

---

## 3. Kết Quả Xác Minh Cuối Cùng
- **Trạng thái Pods**: **100% pods** ở cả hai namespace `dev` và `staging` đều ở trạng thái **`Running`**. Không còn bất kỳ pod nào bị kẹt `Pending` hay `Terminating`.
- **Trạng thái Cluster**: Cụm 2 nodes Memory-Optimized hoạt động cực kỳ ổn định, lượng CPU requests được kiểm soát chặt chẽ dưới ngưỡng tối đa của cụm.
- **Trạng thái ArgoCD**: Cả 2 ứng dụng `dev-yas-app` và `staging-yas-app` đều đã ở trạng thái **`Synced`** với commit mới nhất (`efb746c1`).
- **Ingress**: Các Ingress controller hoạt động bình thường, định tuyến chính xác đến `storefront.dev.yas.local.com`, `backoffice.dev.yas.local.com` và các host staging tương ứng.
