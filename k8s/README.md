# Tài liệu Hướng dẫn Hạ tầng và Triển khai Kubernetes (YAS)

Tài liệu này cung cấp toàn bộ hướng dẫn cần thiết để xây dựng cụm cluster Kubernetes local, cấu hình ánh xạ tên miền thông qua file `hosts`, triển khai ứng dụng bằng Helm charts, và vận hành hệ thống GitOps tự động bằng ArgoCD.

---

## 1. Mục lục
1. [Yêu cầu tài nguyên và Thiết lập local](#2-yêu-cầu-tài-nguyên-và-thiết-lập-local)
2. [Cấu hình file hosts cục bộ](#3-cấu-hình-file-hosts-cục-bộ)
3. [Triển khai hạ tầng và ứng dụng bằng Helm](#4-triển-khai-hạ-tầng-và-ứng-dụng-bằng-helm)
4. [Cấu hình cổng NodePort và Cách truy cập](#5-cấu-hình-cổng-nodeport-và-cách-truy-cập)
5. [Vận hành GitOps bằng ArgoCD](#6-vận-hành-gitops-bằng-argocd)
6. [Khắc phục sự cố thường gặp](#7-khắc-phục-sự-cố-thường-gặp)

---

## 2. Yêu cầu tài nguyên và Thiết lập local

Để chạy toàn bộ hệ thống YAS (bao gồm cơ sở dữ liệu, Kafka broker, Elasticsearch, hệ thống giám sát Prometheus/Grafana/Loki/Tempo và hơn 16 microservices):
* **Cấu hình máy đề nghị**: RAM tối thiểu **16GB** (tốt nhất là 24GB - 32GB), 4 Cores CPU và 40GB đĩa trống.
* **Cấu hình máy ảo WSL2 (Windows)**: Tạo hoặc sửa file `%USERPROFILE%\.wslconfig` trên Windows và khởi động lại WSL bằng lệnh `wsl --shutdown` trong Powershell:
  ```text
  [wsl2]
  memory=16GB
  processors=4
  ```

### Các bước khởi động cluster local (bằng Minikube):
```bash
# Khởi động cụm cluster với cấu hình tài nguyên đủ mạnh
minikube start --disk-size='40000mb' --memory='16g'

# Kích hoạt Ingress addon
minikube addons enable ingress
```

---

## 3. Cấu hình file hosts cục bộ

Thêm các bản ghi sau vào file `hosts` của bạn để phân giải tên miền thay vì truy cập bằng IP thô:
* **Windows**: `C:\Windows\System32\drivers\etc\hosts` (Sửa bằng Notepad mở dưới quyền Administrator)
* **Linux/macOS**: `/etc/hosts` (Sửa bằng `sudo nano /etc/hosts`)

*(Thay thế `<WORKER_NODE_IP>` bằng IP của cluster, ví dụ `127.0.0.1` nếu dùng Docker Desktop, hoặc dùng lệnh `minikube ip` để lấy IP máy ảo)*
```text
# --- Cấu hình ánh xạ tên miền YAS Local NodePort ---
<WORKER_NODE_IP> storefront-ui.dev.yas.local.com
<WORKER_NODE_IP> backoffice-ui.dev.yas.local.com
<WORKER_NODE_IP> swagger-ui.dev.yas.local.com
<WORKER_NODE_IP> identity.dev.yas.local.com
<WORKER_NODE_IP> storefront-bff.dev.yas.local.com
<WORKER_NODE_IP> backoffice-bff.dev.yas.local.com
<WORKER_NODE_IP> argocd.yas.local.com
```

---

## 4. Triển khai hạ tầng và ứng dụng bằng Helm

### Bước 4.1: Triển khai các dịch vụ nền tảng (Database, Broker, Identity)
Di chuyển vào thư mục `k8s/deploy/` và chạy các script theo thứ tự sau:
```bash
cd k8s/deploy/

# Cài đặt Keycloak (Hệ thống quản lý định danh)
./setup-keycloak.sh

# Cài đặt Redis (Lưu trữ session)
./setup-redis.sh

# Cài đặt PostgreSQL, Kafka, Elasticsearch, Observability (Prometheus, Grafana, Loki, Tempo)
./setup-cluster.sh
```

### Bước 4.2: Triển khai các ứng dụng YAS
Sau khi các dịch vụ hạ tầng đã sẵn sàng (`Running`), chạy lệnh:
```bash
./deploy-yas-applications.sh dev
```
Toàn bộ storefront, backoffice, và 16 microservices của YAS sẽ được đồng loạt deploy lên namespace `dev`.

---

## 5. Cấu hình cổng NodePort và Cách truy cập

Chúng tôi đã thiết lập sẵn các cổng NodePort tĩnh để phục vụ cho việc kiểm thử của lập trình viên dễ dàng, không cần cấu hình DNS ngoài:

* **Storefront UI**: [storefront-ui.dev.yas.local.com:30080](http://storefront-ui.dev.yas.local.com:30080)
* **Backoffice UI**: [backoffice-ui.dev.yas.local.com:30081](http://backoffice-ui.dev.yas.local.com:30081)
* **Swagger UI**: [swagger-ui.dev.yas.local.com:30082](http://swagger-ui.dev.yas.local.com:30082/swagger-ui/)
* **Keycloak (Identity)**: [identity.dev.yas.local.com:30084](http://identity.dev.yas.local.com:30084/auth/)
* **Storefront BFF**: [storefront-bff.dev.yas.local.com:30085](http://storefront-bff.dev.yas.local.com:30085)
* **Backoffice BFF**: [backoffice-bff.dev.yas.local.com:30086](http://backoffice-bff.dev.yas.local.com:30086)
* **ArgoCD Web UI**: [argocd.yas.local.com:30088](http://argocd.yas.local.com:30088)

---

## 6. Vận hành GitOps bằng ArgoCD

Hệ thống hỗ trợ cơ chế tự động hóa triển khai đồng bộ bằng mô hình GitOps (App-of-Apps).

### Bước 6.1: Cài đặt ArgoCD lên Cluster
Triển khai ArgoCD và tự động cấu hình NodePort `30088` / `30089` cùng mật khẩu admin mặc định:
```bash
cd k8s/deploy/
./setup-argocd.sh
```

### Bước 6.2: Khởi tạo các Application tự động
Tự động sinh các file YAML Application kết nối với Git của nhóm:
```bash
./generate-argocd-apps.sh
```

### Bước 6.3: Triển khai bằng file Bootstrap
Áp dụng mẫu Bootstrap để ArgoCD tự động đồng bộ hóa và quản lý trạng thái:
* **Môi trường dev**:
  ```bash
  kubectl apply -f ../argocd/yas-dev-bootstrap.yaml
  ```
* **Môi trường staging** (Có bật tự động đồng bộ - Auto Sync, tự động phục hồi cấu hình bị lệch - Self-Heal, tự thu hồi tài nguyên cũ - Prune):
  ```bash
  kubectl apply -f ../argocd/yas-staging-bootstrap.yaml
  ```

---

## 7. Khắc phục sự cố thường gặp

### A. Lỗi chuyển hướng đăng nhập trên Keycloak (Redirect URI)
Nếu đăng nhập thành công từ giao diện storefront/backoffice nhưng bị chuyển hướng sai tên miền hoặc cổng:
1. Đăng nhập vào trang quản trị Keycloak tại `http://identity.dev.yas.local.com:30084/auth/` bằng tài khoản admin.
2. Lấy mật khẩu admin bằng lệnh:
   ```bash
   kubectl get secret keycloak-credentials -n keycloak -o jsonpath="{.data.password}" | base64 --decode
   ```
3. Truy cập **Clients** -> Chọn client của storefront/backoffice -> Sửa **Valid Redirect URIs** trỏ chính xác về cổng NodePort tương ứng (Ví dụ: `http://storefront-ui.dev.yas.local.com:30080/*`).

### B. Kiểm tra cổng dịch vụ K8s thực tế
Nếu không truy cập được, kiểm tra cổng NodePort đã tạo thành công chưa bằng lệnh:
```bash
kubectl get svc -n dev
```
Đảm bảo rằng loại dịch vụ là `NodePort` và các cổng tĩnh `30080`, `30081`, `30082` đã được gán chính xác.
