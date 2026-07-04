# 🚀 Hướng Dẫn Thiết Lập Hệ Thống Yas 4 Laptops (K8s + Tailscale + Jenkins + ArgoCD + Istio)

Tài liệu này hướng dẫn chi tiết từng bước để nhóm 4 người kết nối máy tính qua Tailscale, tạo cụm Kubernetes (k3s), cài đặt Jenkins, cấu hình ArgoCD GitOps 100%, và cài đặt Istio Service Mesh kèm Kiali.

---

## 📋 Phân vai trò các máy trong nhóm
* **Laptop A (Trưởng nhóm)**: Đóng vai trò **Master Node** (Chạy Control Plane, Ingress, ArgoCD, Jenkins, và Kiali).
* **Laptop B, C, D (Thành viên)**: Đóng vai trò **Worker Nodes** (Chạy các Pods/Microservices được phân tải bởi Master).

---

## 🛠️ Chuẩn bị trước khi cài đặt (Yêu cầu trên cả 4 máy)
1. **Cài đặt WSL2 (Ubuntu)**:
   Mở PowerShell với quyền Administrator và gõ:
   ```powershell
   wsl --install
   ```
   *Khởi động lại máy tính sau khi cài đặt xong.*
2. **Cài đặt Tailscale**:
   * Đăng ký tài khoản và tải ứng dụng Tailscale cho Windows.
   * Cài đặt và đăng nhập cùng 1 tài khoản (hoặc share kết nối mạng) để cả 4 máy có thể ping thấy nhau qua dải IP Tailscale (dạng `100.x.y.z`).
   * **Bắt buộc**: Cài đặt Tailscale cả ở môi trường WSL2 bằng lệnh:
     ```bash
     curl -fsSL https://tailscale.com/install.sh | sh
     sudo tailscale up
     ```
3. **Docker Desktop**: Cài đặt Docker Desktop trên máy chạy Jenkins (thường là Laptop A) và tích chọn **Use WSL2 backend**.

---

## 📡 Bước 1: Kết nối cụm K8s qua Tailscale

### 1.1 Khởi tạo Master Node (Chạy trên Laptop A)
Mở terminal WSL2 Ubuntu của **Laptop A**, di chuyển tới thư mục dự án và chạy:
```bash
chmod +x scripts/setup-k3s-master-wsl2.sh
sudo ./scripts/setup-k3s-master-wsl2.sh
```
* **Kết quả**: Script sẽ khởi tạo Master, tự động gán nhãn `laptop-a`.
* **Thông tin cần lưu**: Ở cuối log màn hình, sao chép 2 thông tin sau:
  * **IP Tailscale** của Laptop A (Ví dụ: `100.115.80.20`)
  * **Join Token** (Mã token kết nối dài)

### 1.2 Kết nối Worker Nodes (Chạy trên Laptop B, C, D)
Mở terminal WSL2 Ubuntu của các máy thành viên, chạy lệnh gia nhập cụm:

* **Trên Laptop B**:
  ```bash
  export K3S_URL="https://<IP-Tailscale-Laptop-A>:6443"
  export K3S_TOKEN="<Mã-Join-Token>"
  export NODE_ROLE="laptop-b"
  chmod +x scripts/setup-k3s-agent-wsl2.sh
  sudo -E ./scripts/setup-k3s-agent-wsl2.sh
  ```
* **Trên Laptop C**: Đổi `NODE_ROLE="laptop-c"`, chạy tương tự.
* **Trên Laptop D**: Đổi `NODE_ROLE="laptop-d"`, chạy tương tự.

### 1.3 Kiểm tra kết nối cụm (Kiểm tra trên Laptop A)
Gõ lệnh sau trên Master:
```bash
kubectl get nodes
```
*Kết quả hiển thị đủ 4 node ở trạng thái `Ready` là thành công.*

---

## ⚓ Bước 2: Deploy cơ sở hạ tầng & Vá lỗi DNS

### 2.1 Cài đặt cơ sở dữ liệu, Keycloak và Kafka (Chạy trên Laptop A)
Tại WSL2 Laptop A, chạy lệnh deploy hạ tầng:
```bash
# Chạy script deploy để tạo các namespace, cài đặt Postgres, Kafka, Keycloak,...
./scripts/setup-laptop-node.sh deploy
```
*Đợi khoảng 5 - 7 phút cho các database và Keycloak khởi chạy ổn định.*

### 2.2 Vá lỗi CoreDNS để các Pod nhận diện nhau (Chạy trên Laptop A)
Do Keycloak chạy bằng ClusterIP và phân giải qua tên miền riêng, chạy script vá DNS để K8s tự động định tuyến:
```bash
chmod +x scripts/patch-coredns-tailscale.sh
./scripts/patch-coredns-tailscale.sh
```

### 2.3 Cấu hình file `hosts` trên cả 4 máy Laptop
Chạy lệnh sau trên Laptop A để lấy danh sách cấu hình hosts:
```bash
./scripts/setup-laptop-node.sh hosts
```
Sao chép đoạn IP hiển thị và dán vào file hosts của **cả 4 máy**:
* **Windows**: `C:\Windows\System32\drivers\etc\hosts` (Mở Notepad bằng Administrator để lưu).
* **WSL2 (Ubuntu)**: `/etc/hosts` (Dùng lệnh `sudo nano /etc/hosts`).

---

## 🛠️ Bước 3: Cài đặt và cấu hình Jenkins CD

### 3.1 Khởi động Jenkins (Chạy trên Laptop A hoặc máy chạy Docker)
Di chuyển tới thư mục `docker-jenkins` và khởi chạy container:
```bash
cd docker-jenkins
docker compose up -d --build
```
* Jenkins sẽ chạy ở cổng `8899` (`http://localhost:8899`).
* Mật khẩu đăng nhập ban đầu lấy bằng lệnh: `docker logs jenkins_master`.

### 3.2 Cung cấp Kubeconfig cho Jenkins
Để Jenkins có thể giao tiếp với cụm K8s Tailscale:
1. Trên Laptop A, sao chép file cấu hình Kubeconfig đã tự động đổi IP Tailscale tại đường dẫn: `/tmp/kubeconfig-tailscale.yaml`.
2. Dán đè (ghi đè) nội dung này vào file **`docker-jenkins/kubeconfig-docker`** trong thư mục code.
3. Restart lại Jenkins: `docker restart jenkins_master`.

### 3.3 Cấu hình Credentials trên Jenkins (Truy cập `http://localhost:8899`)
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
* Đường dẫn truy cập: `http://<IP-Tailscale-Laptop-A>:30088` (hoặc `http://argocd.yas.local.com:30088`).
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
ArgoCD sẽ tự động tải các Helm chart từ Git và phân phối tải/triển khai các Pods đều ra 4 máy laptop của nhóm bạn.

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
*Lệnh này cấu hình tự động thử lại (retry 2 lần) nếu các service backend gặp lỗi phản hồi 5xx.*

### 5.3 Áp dụng Authorization Policies (Phân quyền kết nối)
```bash
kubectl apply -f istio/security/authz-policies.yaml
```
*Chỉ những service được cấp quyền (Least Privilege) mới có thể gọi sang nhau. Các kết nối trái phép sẽ bị chặn ngay lập tức với mã lỗi HTTP 403.*

---

## 📊 Bước 6: Quan sát qua Kiali Dashboard
* Cổng mặc định của Kiali Dashboard là `30089`.
* Đường dẫn truy cập: `http://<IP-Tailscale-Laptop-A>:30089` (hoặc `http://kiali.yas.local.com:30089`).
* Bạn có thể xem Graph/Topology của các dịch vụ đang chạy, kiểm tra các luồng traffic thực tế để làm báo cáo đồ án.

---

## 📈 Bước 7: Hệ thống Giám sát & Tracing (Observability - Grafana)
Vì đã bật lại toàn bộ hệ thống Observability (Prometheus, Grafana, Loki, Tempo, OpenTelemetry) trong script deploy, bạn có thể giám sát chi tiết log, metrics và tracing:

* **Đường dẫn truy cập**: `http://grafana.yas.local.com` (Đã được cấu hình Ingress chạy trực tiếp qua cổng `80`).
* **Tài khoản đăng nhập**: 
  * Username: `admin`
  * Password: `admin` (Được cấu hình trong [prometheus.values.yaml](file:///c:/Users/Admin/Documents/A-devops/yas/k8s/deploy/observability/prometheus.values.yaml)).
* **Thành quả**: Trong Grafana đã được tích hợp sẵn các datasource cho Loki (Logs), Tempo (Traces) và Prometheus (Metrics) để bạn truy vấn dữ liệu chi tiết của 14 services đúng theo ảnh chụp trong dự án gốc.

