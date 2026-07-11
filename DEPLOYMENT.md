# Hướng Dẫn Triển Khai Hệ Thống (Deployment & CI/CD Guide)

Dự án YAS (Yet Another Shop) được triển khai trên Kubernetes (K3s) sử dụng mô hình kết hợp giữa:
1. **Hạ tầng tĩnh (Infrastructure & Databases):** Cài đặt thủ công một lần trực tiếp trên máy ảo Azure VM.
2. **Ứng dụng động (Microservices):** Tự động hóa build, test và deploy thông qua các pipeline CI/CD của GitHub Actions.

Tài liệu này hướng dẫn chi tiết cách triển khai cả hai thành phần trên.

---

## 🛠️ PHẦN I: Triển Khai Hạ Tầng & Cơ Sở Dữ Liệu (Cài đặt 1 lần duy nhất)

Các dịch vụ này chạy cố định trên máy ảo để lưu trữ dữ liệu và hỗ trợ vận hành. Chúng không chạy qua pipeline push tự động để tránh mất dữ liệu.

### 1. Danh sách các dịch vụ hạ tầng
* **Cơ sở dữ liệu:** PostgreSQL (PostgreSQL Operator & Postgres StatefulSet).
* **Hàng đợi tin nhắn:** Kafka (Strimzi Kafka Operator & Kafka Cluster).
* **Moteur tìm kiếm:** Elasticsearch (ECK Operator & Elasticsearch Cluster).
* **Giám sát & Log (Mesh):** Prometheus, Grafana, Loki, Tempo, OpenTelemetry.
* **Giao diện quản trị:** pgAdmin (quản lý DB), AKHQ (quản lý Kafka), Kiali (quản lý Mesh).

### 2. Các bước cài đặt trực tiếp trên Azure VM
1. **Kết nối SSH vào máy ảo Azure của bạn:**
   ```bash
   ssh -i <private-key-path> <username>@<azure-vm-ip>
   ```
2. **Kéo mã nguồn dự án mới nhất về máy ảo:**
   ```bash
   # Nếu chưa clone repo
   git clone https://github.com/VyBui13/yas.git
   cd yas
   
   # Nếu đã clone, thực hiện cập nhật code mới nhất từ main
   git checkout main
   git pull origin main
   ```
3. **Cấu hình thông số (Tùy chọn):**
   Kiểm tra hoặc sửa đổi tên miền, cấu hình tài khoản trong file `k8s/deploy/cluster-config.yaml`.
4. **Chạy script cài đặt hạ tầng:**
   ```bash
   cd k8s/deploy
   chmod +x *.sh
   ./setup-cluster.sh
   ```
   *Đợi từ 3-5 phút để cụm tải ảnh và khởi tạo các StatefulSet.*

### 3. Kiểm tra trạng thái hạ tầng
* **Chạy các lệnh kiểm tra trạng thái pod:**
  ```bash
  kubectl get pods -n postgres
  kubectl get pods -n kafka
  kubectl get pods -n elasticsearch
  kubectl get pods -n observability
  ```
  *(Tất cả pod phải ở trạng thái Running hoặc Completed).*
* **Kiểm tra giao diện quản trị Web:**
  Thêm dòng sau vào file `hosts` trên máy tính cá nhân của bạn (Windows: `C:\Windows\System32\drivers\etc\hosts`):
  ```text
  <IP_PUBLIC_MÁY_ẢO_AZURE> pgadmin.yas.local.com akhq.yas.local.com grafana.yas.local.com
  ```
  Truy cập qua trình duyệt:
  * pgAdmin: `http://pgadmin.yas.local.com` (Đăng nhập: `admin@yas.com` / `admin`)
  * AKHQ: `http://akhq.yas.local.com`
  * Grafana: `http://grafana.yas.local.com` (Đăng nhập: `admin` / `admin`)

---

## 🚀 PHẦN II: Triển Khai Dịch Vụ Ứng Dụng (Deploy qua Pipeline GitHub)

Mỗi khi có sự thay đổi mã nguồn, các microservices sẽ được tự động build và deploy lên cụm.

### 1. Danh sách các microservices cần deploy
* **Dịch vụ giao diện:** `storefront-ui`, `backoffice-ui` (Next.js).
* **Dịch vụ cổng kết nối:** `storefront-bff`, `backoffice-bff` (Spring Cloud Gateway).
* **Các microservice backend nghiệp vụ:** `cart`, `product`, `tax`, `customer`, `order`, `inventory`, `media`, `payment`, `payment-paypal`, `promotion`, `rating`, `recommendation`, `search`, `location`, `webhook`, `sampledata`.

### 2. Mô hình CI (Tự động build/push theo tag Commit SHA)
Thiết lập mẫu tại [cart-ci.yaml](.github/workflows/cart-ci.yaml):
* **Kích hoạt:** Mọi sự kiện `push` lên bất kỳ nhánh tính năng nào (`**`).
* **Hoạt động:**
  * Nếu là nhánh **`main`**: Hệ thống build ảnh và push lên GitHub Container Registry (`ghcr.io/vybui13/yas-cart:latest`) để làm phiên bản ổn định.
  * Nếu là các nhánh **tính năng kiểm thử khác**: Hệ thống tự động lấy commit SHA ngắn, build và đẩy lên Docker Hub cá nhân (`<docker-username>/yas-cart:<commit-sha>`).

### 3. Mô hình CD thủ công (`developer_build`)
Thiết lập tại [developer-build.yaml](.github/workflows/developer-build.yaml):
* **Kích hoạt:** Chạy thủ công trên tab **Actions** của GitHub Web.
* **Cách sử dụng:**
  1. Vào tab **Actions** > Chọn **`developer_build`**.
  2. Bấm **Run workflow** và chọn:
     * **Microservice muốn deploy:** (Ví dụ: `cart`, `product`, `tax`...)
     * **Tên nhánh cần test:** Nhập tên nhánh chứa code thay đổi cần test.
     * **Hành động:** Chọn `Deploy` (để cài đặt mới/cập nhật) hoặc `Cleanup` (để gỡ bỏ hoàn toàn khỏi cụm).
  3. Nhấn **Run workflow** để kích hoạt CD tự động cài đặt từ GitHub xuống VM Azure.

---

## 🌟 PHẦN III: Triển Khai Các Tính Năng Nâng Cao

### 1. Đồng bộ ArgoCD (Môi trường Dev/Staging)
1. **Áp dụng các ứng dụng mẫu trên cụm (chạy trên Azure VM):**
   ```bash
   kubectl apply -f k8s/argocd/
   ```
2. **Môi trường `dev`:** ArgoCD sẽ tự động lắng nghe nhánh `main` trên GitHub và đồng bộ code mới nhất vào namespace `dev`.
3. **Môi trường `staging`:** ArgoCD lắng nghe theo các thẻ phiên bản (tag release) của GitHub (ví dụ: `v1.0.0`) và đồng bộ vào namespace `staging`.

### 2. Cấu hình bảo mật Istio Service Mesh
Áp dụng các chính sách an toàn thông tin và định tuyến giao tiếp (chạy trên Azure VM):
```bash
kubectl apply -f k8s/istio/
```
* **mTLS STRICT:** Mã hóa mọi luồng kết nối nội bộ trong cụm K3s.
* **Retry Policy:** Tự động gọi lại tối đa 3 lần khi dịch vụ cart gặp lỗi 5xx.
* **Authorization Policy:** Chặn đứng tất cả truy cập trái phép tới service `cart` ngoại trừ các BFF cổng chính.
