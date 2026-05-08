# 🛠 Developer Testing Guide

Hướng dẫn sử dụng **Developer Build CD** pipeline để deploy và test code trên K8s cluster.

---

## 1. Workflow tổng quan

| Workflow | Mục đích |
|----------|----------|
| `Developer Build CD` | Deploy tất cả services vào namespace riêng để test |
| `Developer Cleanup` | Xóa namespace và toàn bộ resources đã deploy |

---

## 2. Sử dụng Developer Build CD

### Bước 1: Trigger workflow

1. Vào **GitHub Actions** → chọn workflow **"Developer Build CD"**
2. Click **"Run workflow"**
3. Điền branch cho service bạn muốn test, các service khác để mặc định `main`

**Ví dụ:** Bạn đang làm việc trên branch `dev_tax_service`:

| Parameter | Giá trị |
|-----------|---------|
| `tax_branch` | `dev_tax_service` |
| Các service khác | `main` (mặc định) |

> ⚠️ **Lưu ý**: Branch phải đã được push lên remote và CI pipeline đã build Docker image cho branch đó.

### Bước 2: Đọc kết quả

Sau khi workflow hoàn tất, vào **Summary** tab để xem:
- **Namespace** được tạo (VD: `yas-dev-username-42`)
- **Worker Node IP**
- **NodePort** cho từng service entry-point
- **Image tags** đã apply

---

## 3. Truy cập services

### 3.1. Cấu hình file hosts

Thêm dòng sau vào file hosts trên máy của bạn:

**Windows:** `C:\Windows\System32\drivers\etc\hosts`  
**Linux/Mac:** `/etc/hosts`

```
<WORKER_NODE_IP>    dev.yas.local
```

> Thay `<WORKER_NODE_IP>` bằng IP hiển thị trong workflow summary.

### 3.2. Truy cập qua NodePort

Các service entry-point được expose qua **NodePort** (K8s tự assign port 30000-32767):

| Service | Mô tả | Cách truy cập |
|---------|--------|---------------|
| **Backoffice BFF** | API gateway cho Backoffice | `http://<WORKER_IP>:<NODEPORT>` |
| **Storefront BFF** | API gateway cho Storefront | `http://<WORKER_IP>:<NODEPORT>` |
| **Backoffice UI** | Giao diện Backoffice | `http://<WORKER_IP>:<NODEPORT>` |
| **Storefront UI** | Giao diện Storefront | `http://<WORKER_IP>:<NODEPORT>` |
| **Nginx API GW** | API gateway cho backend services | `http://<WORKER_IP>:<NODEPORT>` |

> NodePort cụ thể được hiển thị trong workflow summary sau khi deploy.

### 3.3. Truy cập backend API qua Nginx Gateway

Nginx API Gateway proxy tất cả backend services. Ví dụ:

```
http://<WORKER_IP>:<NGINX_NODEPORT>/product/    → Product service
http://<WORKER_IP>:<NGINX_NODEPORT>/cart/        → Cart service
http://<WORKER_IP>:<NGINX_NODEPORT>/tax/         → Tax service
http://<WORKER_IP>:<NGINX_NODEPORT>/order/       → Order service
...
```

---

## 4. Xóa deployment (Cleanup)

Sau khi test xong, **bắt buộc** xóa deployment để giải phóng tài nguyên:

1. Vào **GitHub Actions** → chọn workflow **"Developer Cleanup"**
2. Click **"Run workflow"**
3. Điền **namespace** từ summary của `Developer Build CD` (VD: `yas-dev-username-42`)
4. Workflow sẽ:
   - Uninstall tất cả Helm releases
   - Xóa toàn bộ resources
   - Xóa namespace

> ⚠️ Chỉ namespace có prefix `yas-dev-` mới được xóa (bảo vệ production).

---

## 5. Lưu ý quan trọng

1. **Tài nguyên có hạn**: Workflow tự động scale down namespace `dev` và `staging` trước khi deploy. Sau khi cleanup, dev/staging sẽ được restore khi có CI build tiếp theo.

2. **Image phải tồn tại**: Trước khi trigger `Developer Build`, đảm bảo CI đã build image cho branch của bạn. Kiểm tra DockerHub nếu cần.

3. **Một namespace cho mỗi run**: Mỗi lần trigger tạo namespace mới. Nếu chạy nhiều lần, nhớ cleanup tất cả.

4. **Không ảnh hưởng GitOps**: Developer namespace deploy trực tiếp (không qua ArgoCD), nên không ảnh hưởng state GitOps của dev/staging.
