# 🚀 Hướng Dẫn Thiết Lập Hệ Thống Yas Hybrid Cluster (PC Master 64GB + Laptop Worker)

Tài liệu này hướng dẫn chi tiết từng bước để kết nối máy tính bàn (PC 64GB ở nhà) và laptop (mang lên trường) qua Tailscale, tạo cụm Kubernetes (k3s) phân vùng tải, cài đặt Jenkins, cấu hình ArgoCD GitOps 100%, và cài đặt Istio Service Mesh kèm Grafana Observability.

---

## 📋 Phân vai trò các máy trong nhóm
* **PC Master Node (64GB ở nhà)**: Đóng vai trò **Master Node** (Chạy Control Plane, Ingress, ArgoCD, Observability, databases, và toàn bộ microservices backend).
* **Laptop Worker Node (mang đi demo)**: Đóng vai trò **Worker Node** (Chạy các pod UI tĩnh cực nhẹ gồm `storefront-ui`, `backoffice-ui` và chạy Jenkins CD).

---

## 🛠️ Chuẩn bị trước khi cài đặt (Yêu cầu trên cả 2 máy)
1. **Cài đặt WSL2 (Ubuntu)**:
   Mở PowerShell với quyền Administrator và gõ:
   ```powershell
   wsl --install
   ```
   *Khởi động lại máy tính sau khi cài đặt xong.*
2. **Cài đặt Tailscale**:
   * Đăng ký tài khoản và tải ứng dụng Tailscale cho Windows.
   * Cài đặt và đăng nhập cùng 1 tài khoản trên cả 2 máy (PC và Laptop) để chúng có thể ping thấy nhau qua dải IP Tailscale (dạng `100.x.y.z`).
   * **Bắt buộc**: Cài đặt Tailscale cả ở môi trường WSL2 bằng lệnh:
     ```bash
     curl -fsSL https://tailscale.com/install.sh | sh
     sudo tailscale up
     ```
3. **Docker Desktop**: Cài đặt Docker Desktop trên **Laptop** và tích chọn **Use WSL2 backend** để sẵn sàng chạy Jenkins.

---

## 📡 Bước 1: Kết nối cụm K8s qua Tailscale

### 1.1 Khởi tạo Master Node (Chạy trên PC 64GB ở nhà)
Mở terminal WSL2 Ubuntu của **PC 64GB**, di chuyển tới thư mục dự án và chạy:
```bash
chmod +x scripts/setup-k3s-master-wsl2.sh
sudo ./scripts/setup-k3s-master-wsl2.sh
```
* **Kết quả**: Script sẽ khởi tạo Master, tự động gỡ bỏ taint `NoSchedule` trên Master để nó nhận pod backend, và gán nhãn `type=heavy`.
* **Thông tin cần lưu**: Ở cuối log màn hình, sao chép 2 thông tin sau:
   * **IP Tailscale** của PC Master (Ví dụ: `100.115.80.20`)
   * **Join Token** (Mã token kết nối dài)

### 1.2 Kết nối Worker Node (Chạy trên Laptop mang đi demo)
Mở terminal WSL2 Ubuntu của **Laptop**, chạy lệnh gia nhập cụm:
```bash
export K3S_URL="https://<IP-Tailscale-PC-Master>:6443"
export K3S_TOKEN="<Mã-Join-Token-từ-PC>"
export NODE_ROLE="laptop-b"
chmod +x scripts/setup-k3s-agent-wsl2.sh
sudo -E ./scripts/setup-k3s-agent-wsl2.sh
```
* **Kết quả**: Laptop tự động kết nối vào cụm Master PC và được gán nhãn `type=light`.

### 1.3 Kiểm tra kết nối cụm (Kiểm tra trên PC hoặc Laptop)
Gõ lệnh sau trên máy bất kỳ:
```bash
kubectl get nodes -o wide --show-labels
```
*Kết quả hiển thị node PC (`k3s-server-0` có label `type=heavy`) và node Laptop (`laptop-b` có label `type=light`) ở trạng thái `Ready` là thành công.*

---

## ⚓ Bước 2: Deploy cơ sở hạ tầng & Vá lỗi DNS

### 2.1 Cài đặt cơ sở dữ liệu, Keycloak và Kafka (Chạy trên PC Master)
Tại WSL2 PC, chạy lệnh deploy hạ tầng:
```bash
# Chạy script deploy để tạo các namespace, cài đặt Postgres, Kafka, Keycloak, Elasticsearch,...
./scripts/setup-laptop-node.sh deploy
```
*Đợi khoảng 5 - 7 phút cho các database và Keycloak khởi chạy ổn định.*

### 2.2 Vá lỗi CoreDNS để các Pod nhận diện nhau (Chạy trên PC Master)
Do Keycloak chạy bằng ClusterIP và phân giải qua tên miền riêng, chạy script vá DNS để K8s tự động định tuyến:
```bash
chmod +x scripts/patch-coredns-tailscale.sh
./scripts/patch-coredns-tailscale.sh
```

### 2.3 Cấu hình file `hosts` trên cả 2 máy (PC và Laptop)
Chạy lệnh sau trên PC Master để lấy danh sách cấu hình hosts:
```bash
./scripts/setup-laptop-node.sh hosts
```
Sao chép đoạn IP hiển thị và dán vào file hosts của **cả 2 máy**:
* **Windows**: `C:\Windows\System32\drivers\etc\hosts` (Mở Notepad bằng Administrator để lưu).
* **WSL2 (Ubuntu)**: `/etc/hosts` (Dùng lệnh `sudo nano /etc/hosts`).

---

## 🛠️ Bước 3: Cài đặt và cấu hình Jenkins CD

### 3.1 Khởi động Jenkins (Chạy trên Laptop demo)
Di chuyển tới thư mục `docker-jenkins` trên Laptop và khởi chạy container:
```bash
cd docker-jenkins
docker compose up -d --build
```
* Jenkins sẽ chạy ở cổng `8899` (`http://localhost:8899`).
* Mật khẩu đăng nhập ban đầu lấy bằng lệnh: `docker logs jenkins_master`.

### 3.2 Cung cấp Kubeconfig cho Jenkins (Chạy trên Laptop)
Để Jenkins trên Laptop có thể ra lệnh deploy sang cụm K8s Tailscale:
1. Sao chép file cấu hình Kubeconfig từ PC Master mang sang đặt tại Laptop (Đường dẫn gốc ở PC là `/tmp/kubeconfig-tailscale.yaml`).
2. Dán đè (ghi đè) nội dung này vào file **`docker-jenkins/kubeconfig-docker`** trên Laptop.
3. Restart lại Jenkins trên Laptop: `docker restart jenkins_master`.

### 3.3 Cấu hình Credentials trên Jenkins (Truy cập `http://localhost:8899` trên Laptop)
* Vào **Manage Jenkins** > **Credentials** > **global** > **Add Credentials**:
  * **Credential 1 (GitHub)**:
    * Kind: `Username with password`
    * Username: Tài khoản GitHub của bạn
    * Password: Personal Access Token (PAT) có full quyền write/push code
    * ID: **`github-credentials`** (Bắt buộc đặt đúng ID này)
  * **Credential 2 (Docker Hub)**:
    * Kind: `Username with password`
    * Username: Tài khoản Docker Hub
    * Password: Mật khẩu hoặc token Docker Hub
    * ID: **`dockerhub-credentials`** (Bắt buộc đặt đúng ID này)

---

## 🐙 Bước 4: Khởi động ArgoCD & Đồng bộ GitOps 100%

### 4.1 Đăng nhập ArgoCD UI
* Cổng mặc định của ArgoCD được cấu hình NodePort là `30088`.
* Đường dẫn truy cập: `http://<IP-Tailscale-PC-Master>:30088` (hoặc `http://argocd.yas.local.com:30088`).
* Tài khoản: `admin`. Mật khẩu lấy qua lệnh:
  ```bash
  kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
  ```

### 4.2 Kích hoạt GitOps (App of Apps)
Sử dụng kubectl để cài đặt ứng dụng mẹ của ArgoCD:
```bash
# Áp dụng cấu hình để ArgoCD tự động quét và sync môi trường dev/staging
kubectl apply -f argocd/yas-dev-bootstrap.yaml
kubectl apply -f argocd/yas-staging-bootstrap.yaml
```
ArgoCD sẽ tự động tải các Helm chart từ Git và phân phối tải: **Các pod nặng (Backends, DBs, ES, Kafka) sẽ được deploy trên PC Master (`type=heavy`), các pod UI tĩnh nhẹ (`storefront-ui`, `backoffice-ui`) sẽ chạy trên Laptop (`type=light`).**

---

## 🕸️ Bước 5: Cấu hình Istio Service Mesh (mTLS & Traffic Policies)

Sau khi cụm ứng dụng đã chạy ổn định và hiển thị màu xanh lá cây trên ArgoCD, thực hiện thiết lập các chính sách bảo mật mạng và định tuyến:

### 5.1 Kích hoạt mTLS STRICT (Bảo mật kênh truyền)
```bash
kubectl apply -f istio/mtls/peer-authentication.yaml
kubectl apply -f istio/mtls/destination-rule.yaml
```
*Lệnh này bắt buộc tất cả các service giao tiếp với nhau bằng giao thức mã hóa TLS.*

### 5.2 Áp dụng VirtualServices (Retry Policy lỗi 500)
```bash
kubectl apply -f istio/traffic/virtual-service-product.yaml
kubectl apply -f istio/traffic/virtual-service-tax.yaml
kubectl apply -f istio/traffic/virtual-service-inventory.yaml
kubectl apply -f istio/traffic/virtual-service-cart.yaml
```
*Cấu hình tự động thử lại (retry 2 lần) nếu các service backend gặp lỗi phản hồi 5xx.*

### 5.3 Áp dụng Authorization Policies (Phân quyền kết nối)
```bash
kubectl apply -f istio/security/authz-policies.yaml
```
*Chỉ những service được cấp quyền mới có thể gọi sang nhau. Các kết nối trái phép sẽ bị chặn ngay lập tức với mã lỗi HTTP 403.*

---

## 📊 Bước 6: Quan sát qua Kiali Dashboard
* Cổng mặc định của Kiali Dashboard là `30089`.
* Đường dẫn truy cập: `http://<IP-Tailscale-PC-Master>:30089` (hoặc `http://kiali.yas.local.com:30089`).
* Bạn có thể xem Graph/Topology mạng mTLS của các dịch vụ đang chạy để làm báo cáo đồ án.

---

## 📈 Bước 7: Hệ thống Giám sát & Tracing (Observability - Grafana)
Vì đã bật lại toàn bộ hệ thống Observability (Prometheus, Grafana, Loki, Tempo, OpenTelemetry) trong script deploy, bạn có thể giám sát chi tiết log, metrics và tracing:

* **Đường dẫn truy cập**: `http://grafana.yas.local.com` (Đã được cấu hình Ingress chạy trực tiếp qua cổng `80`).
* **Tài khoản đăng nhập**: 
  * Username: `admin`
  * Password: `admin` (Được cấu hình trong [prometheus.values.yaml](file:///c:/Users/Admin/Documents/A-devops/yas/k8s/deploy/observability/prometheus.values.yaml)).
* **Thành quả**: Trong Grafana đã được tích hợp sẵn các datasource cho Loki (Logs), Tempo (Traces) và Prometheus (Metrics) để bạn truy vấn dữ liệu chi tiết của 14 services đúng theo ảnh chụp trong dự án gốc.

Giả lập traffic: `powershell.exe -File ./simulate-traffic.ps1 -DurationSeconds 120`
