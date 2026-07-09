# Hướng dẫn Triển khai và Cấu hình Kubernetes Local cho YAS

Tài liệu này hướng dẫn chi tiết cách cấu hình, triển khai và kiểm thử các microservices của YAS trên một cluster Kubernetes local (Docker Desktop, Minikube, hoặc WSL2) và cách truy cập thông qua cơ chế NodePort kết hợp phân giải tên miền (DNS).

---

## 1. Yêu cầu chuẩn bị và Thiết lập môi trường

Trước khi bắt đầu, hãy đảm bảo bạn đã cài đặt các công cụ sau:
- **Docker Desktop** (hoặc Docker chạy trong WSL2)
- **kubectl** (được cài đặt tự động cùng Docker Desktop hoặc nằm tại đường dẫn `C:\Program Files\Docker\Docker\resources\bin\kubectl.exe`)
- **Helm v3** (để quản lý và cài đặt các chart)

### A. Yêu cầu tài nguyên hệ thống
Để chạy toàn bộ hệ thống YAS (bao gồm các cơ sở dữ liệu, broker tin nhắn, công cụ giám sát observability và hơn 16 microservices Spring Boot), máy của bạn cần đáp ứng:
- **RAM**: Tối thiểu 16 GB cấp phát cho máy ảo WSL / Minikube.
- **CPU**: Tối thiểu 4 Cores.
- **Ổ cứng**: Còn trống tối thiểu 40 GB.

*Lưu ý: Mặc định WSL2 chỉ được cấp phát một phần nhỏ RAM của máy host (thường là 4GB). Bạn cần tăng giới hạn RAM bằng cách chỉnh sửa hoặc tạo file `%USERPROFILE%\.wslconfig` trên Windows:*
```text
[wsl2]
memory=16GB
processors=4
```

---

## 1.5. Khởi tạo cụm Kubernetes (Khuyên dùng k3d)

Để tiết kiệm RAM (phù hợp với máy 16GB RAM) và tối ưu hóa hiệu năng, chúng tôi khuyên dùng **k3d** (chạy cụm k3s nhẹ trong Docker) thay vì Minikube nặng nề.

### A. Khởi tạo cụm k3d
Chạy lệnh khởi tạo cụm `yas-cluster` với các cổng NodePort được map sẵn ra ngoài Windows host:

* **Trên Windows Host (Sử dụng file k3d.exe có sẵn ở root dự án)**:
  Mở CMD hoặc PowerShell tại thư mục dự án và chạy:
  ```powershell
  .\k3d.exe cluster create yas-cluster --api-port 6550 -p "30080:30080@server:0" -p "30081:30081@server:0" -p "30082:30082@server:0" -p "30084:30084@server:0" -p "30085:30085@server:0" -p "30086:30086@server:0" -p "30088:30088@server:0" -p "30089:30089@server:0" --agents 1
  ```

* **Trên WSL2 (Linux)**:
  Cài đặt k3d nếu chưa có:
  ```bash
  curl -s https://raw.githubusercontent.com/k3d-io/k3d/main/install.sh | TAG=v5.6.0 bash
  ```
  Khởi tạo cụm:
  ```bash
  k3d cluster create yas-cluster --api-port 6550 -p "30080:30080@server:0" -p "30081:30081@server:0" -p "30082:30082@server:0" -p "30084:30084@server:0" -p "30085:30085@server:0" -p "30086:30086@server:0" -p "30088:30088@server:0" -p "30089:30089@server:0" --agents 1
  ```

### B. Đồng bộ Kubeconfig cho WSL
Để các script chạy trong WSL nhận diện được cụm K8s vừa tạo từ Windows:
1. Trên **Windows host**, xuất cấu hình ra file tạm trong thư mục dự án:
   ```powershell
   .\k3d.exe kubeconfig get yas-cluster > yas-kubeconfig.yaml
   ```
2. Trên **WSL**, copy file cấu hình này vào thư mục cấu hình mặc định của kubectl:
   ```bash
   mkdir -p ~/.kube
   cp yas-kubeconfig.yaml ~/.kube/config
   chmod 600 ~/.kube/config
   ```

---

## 2. Triển khai Keycloak và các dịch vụ bổ trợ hạ tầng

Để khởi động các dịch vụ nền tảng (PostgreSQL, Kafka, Elasticsearch, Redis, Keycloak, v.v.):
1. Di chuyển vào thư mục `k8s/deploy/`.
2. Khởi tạo Keycloak (Hệ thống quản lý định danh và phân quyền):
   ```bash
   ./setup-keycloak.sh
   ```
3. Khởi tạo Redis:
   ```bash
   ./setup-redis.sh
   ```
4. Cài đặt các dịch vụ còn lại (Postgres, Elasticsearch, Kafka, Debezium):
   ```bash
   ./setup-cluster.sh
   ```

Xác nhận toàn bộ các pod hạ tầng đã ở trạng thái `Running`:
```bash
kubectl get pods -n postgres
kubectl get pods -n elasticsearch
kubectl get pods -n kafka
kubectl get pods -n keycloak
```

---

## 3. Triển khai các ứng dụng YAS

Sau khi các dịch vụ hạ tầng đã chạy ổn định và khỏe mạnh, tiến hành cài đặt ứng dụng:
```bash
./deploy-yas-applications.sh dev
```
Script này sẽ tự động build dependencies và cài đặt storefront, backoffice bff, và 16 backend microservices vào namespace `dev`.

---

## 4. Bảng tra cứu cổng NodePort truy cập trực tiếp

Chúng tôi đã cấu hình các cổng **NodePort** tĩnh trong phạm vi từ `30000` đến `32767` cho các dịch vụ cổng vào (entrypoints) để lập trình viên kiểm thử trực tiếp mà không cần cài đặt các bộ Ingress Controller hay LoadBalancer phức tạp.

| Tên dịch vụ | Ánh xạ tên miền Local | Loại cổng | Cổng dịch vụ | NodePort | URL Truy Cập |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Storefront UI** | `storefront-ui.dev.yas.local.com` | HTTP | 3000 | **`30080`** | `http://storefront-ui.dev.yas.local.com:30080` |
| **Backoffice UI** | `backoffice-ui.dev.yas.local.com` | HTTP | 3000 | **`30081`** | `http://backoffice-ui.dev.yas.local.com:30081` |
| **Swagger UI** | `swagger-ui.dev.yas.local.com` | HTTP | 8080 | **`30082`** | `http://swagger-ui.dev.yas.local.com:30082/swagger-ui/` |
| **Keycloak (Identity)** | `identity.dev.yas.local.com` | HTTP | 8080 | **`30084`** | `http://identity.dev.yas.local.com:30084/auth/` |
| **Storefront BFF** | `storefront-bff.dev.yas.local.com` | HTTP | 80 | **`30085`** | `http://storefront-bff.dev.yas.local.com:30085` |
| **Backoffice BFF** | `backoffice-bff.dev.yas.local.com` | HTTP | 80 | **`30086`** | `http://backoffice-bff.dev.yas.local.com:30086` |

---

## 5. Cấu hình phân giải file hosts (DNS)

Để gọi tên miền thay vì IP thô khi chạy cục bộ, bạn cần cấu hình file `hosts` của máy tính.

### A. Tự động cấu hình trên Windows (Khuyên dùng)
Chúng tôi đã chuẩn bị sẵn một script PowerShell để tự động kiểm tra và thêm các bản ghi DNS cần thiết vào file hosts của bạn.
1. Mở **Windows PowerShell** dưới quyền quản trị viên (**Run as Administrator**).
2. Di chuyển vào thư mục dự án và chạy script:
   ```powershell
   Set-ExecutionPolicy Bypass -Scope Process -Force
   .\scripts\update-hosts.ps1
   ```

### B. Cấu hình thủ công
Nếu bạn dùng hệ điều hành khác hoặc muốn sửa thủ công, hãy thêm các dòng dưới đây vào file hosts (`C:\Windows\System32\drivers\etc\hosts` trên Windows hoặc `/etc/hosts` trên Linux/macOS):

Thay thế `<WORKER_NODE_IP>` bằng IP của cluster (ví dụ `127.0.0.1` nếu dùng Docker Desktop/k3d, hoặc dùng lệnh `minikube ip` để lấy IP máy ảo):
```text
# --- Cấu hình ánh xạ tên miền YAS Local NodePort ---
<WORKER_NODE_IP> storefront-ui.dev.yas.local.com
<WORKER_NODE_IP> backoffice-ui.dev.yas.local.com
<WORKER_NODE_IP> swagger-ui.dev.yas.local.com
<WORKER_NODE_IP> identity.dev.yas.local.com
<WORKER_NODE_IP> storefront-bff.dev.yas.local.com
<WORKER_NODE_IP> backoffice-bff.dev.yas.local.com
```

---

## 6. Kiểm tra và Khắc phục sự cố

### A. Kiểm tra cổng NodePort đã tạo thành công
Chạy lệnh sau để liệt kê các dịch vụ và cổng được ánh xạ:
```bash
kubectl get svc -n dev
```
*Kết quả mong đợi: Bạn sẽ thấy `storefront-ui`, `backoffice-ui` và `swagger-ui` hiển thị loại service là `NodePort` kèm các cổng tương ứng là `30080`, `30081`, và `30082`.*

### B. Kiểm tra kết nối dịch vụ
Sử dụng công cụ `curl` trên máy để gửi request thử nghiệm:
```bash
curl -I http://storefront-ui.dev.yas.local.com:30080
```
*Kết quả mong đợi: Trả về header phản hồi HTTP 200 OK hoặc mã điều hướng đăng nhập của Keycloak.*

### C. Lỗi điều hướng (Redirect URI) trên Keycloak
Nếu sau khi đăng nhập bạn bị điều hướng sai địa chỉ:
1. Truy cập trang quản trị Keycloak tại `http://identity.dev.yas.local.com:30084/auth/`
2. Lấy mật khẩu admin tạm thời bằng lệnh:
   ```bash
   kubectl get secret keycloak-credentials -n keycloak -o jsonpath="{.data.password}" | base64 --decode
   ```
3. Đăng nhập và vào mục **Clients**, chỉnh sửa **Valid Redirect URIs** để trỏ chính xác về các địa chỉ NodePort của bạn (ví dụ: `http://storefront-ui.dev.yas.local.com:30080/*`).

---

## 7. Cài đặt và cấu hình GitOps với ArgoCD (Tuần 3)

### A. Cài đặt ArgoCD lên Cluster
1. Đảm bảo bạn đang ở thư mục gốc của dự án.
2. Di chuyển vào thư mục `k8s/deploy/` và chạy file script cài đặt:
   ```bash
   cd k8s/deploy/
   ./setup-argocd.sh
   ```
   *Script này sẽ cài đặt ArgoCD, cấu hình NodePort `30088` (HTTP) / `30089` (HTTPS) cho giao diện quản trị, và in ra mật khẩu admin mặc định sau khi cài xong.*

3. Để truy cập giao diện quản trị ArgoCD UI, hãy thêm bản ghi DNS sau vào file `hosts`:
   ```text
   <WORKER_NODE_IP> argocd.yas.local.com
   ```
   Sau đó mở trình duyệt và truy cập: `http://argocd.yas.local.com:30088` (Tài khoản mặc định: `admin`, mật khẩu lấy từ kết quả chạy script trên).

### B. Tạo các File Application cho các Microservice
Để ArgoCD nhận diện được 22 dịch vụ của YAS mà không cần viết thủ công hàng chục file YAML cấu hình trùng lặp, hãy khởi chạy script sinh file tự động:
```bash
./generate-argocd-apps.sh
```
*Script sẽ tự động phát hiện URL Git remote và nhánh Git hiện tại của bạn, sau đó tạo ra 44 file Application tương ứng với môi trường `dev` (trong thư mục `argocd/apps/dev/`) và môi trường `staging` (trong thư mục `argocd/apps/staging/`).*

### C. Triển khai (Bootstrap) bằng mô hình App-of-Apps
Để bắt đầu đồng bộ hóa toàn bộ các ứng dụng lên K8s thông qua GitOps:
* **Môi trường dev**:
  ```bash
  kubectl apply -f ../../argocd/yas-dev-bootstrap.yaml
  ```
* **Môi trường staging** (Có cấu hình tự động đồng bộ - Auto Sync, tự động sửa lỗi lệch cấu hình - Self Heal, và tự động thu hồi tài nguyên cũ - Prune):
  ```bash
  kubectl apply -f ../../argocd/yas-staging-bootstrap.yaml
  ```

Sau khi chạy lệnh, truy cập giao diện ArgoCD Web UI để theo dõi trạng thái đồng bộ và sức khỏe (`Synced` và `Healthy`) của toàn bộ hệ thống microservices.

