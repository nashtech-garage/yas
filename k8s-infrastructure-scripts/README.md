# Kịch bản triển khai Hạ Tầng (K8s Infrastructure Scripts)

Thư mục này chứa các script PowerShell giúp tự động hóa quá trình khởi tạo hạ tầng Kubernetes theo từng giai đoạn của đồ án (Cơ bản và Nâng cao).

## Hướng dẫn chung
- Yêu cầu: Đã cài đặt Minikube, Helm, Kubectl.
- Chạy PowerShell với quyền **Administrator**.
- Di chuyển vào thư mục này trước khi chạy: `cd k8s-infrastructure-scripts`

---

## 1. Dành cho PHẦN CƠ BẢN (Github Actions CD)
Trong phần cơ bản, các ứng dụng được cài vào namespace `yas`. Do đó, hạ tầng cấu hình (Keycloak, yas-configuration) cũng được cài vào namespace `yas`.

- **`setup-infra-basic.ps1`**: Cài đặt CoreDNS, Postgres, Redis, Kafka, Elasticsearch (dùng chung) VÀ cài Keycloak, yas-configuration vào namespace `yas`.
  👉 *Chạy script này đầu tiên nếu bạn đang test Phần Cơ Bản.*

---

## 2. Dành cho PHẦN NÂNG CAO (ArgoCD GitOps)
Trong phần nâng cao, các ứng dụng được triển khai ra 2 môi trường `dev` và `staging`. Do đó, hạ tầng cấu hình phải được cài riêng rẽ cho từng môi trường.

- **`setup-infra-advanced.ps1`**: Cài đặt CoreDNS, Postgres, Redis, Kafka, Elasticsearch (dùng chung) VÀ cài Keycloak, yas-configuration vào 2 namespace `dev` và `staging`.
  👉 *Chạy script này đầu tiên nếu bạn đang test Phần Nâng Cao.*

---

## 3. Dọn dẹp
- **`teardown-infra.ps1`**: Xóa toàn bộ hạ tầng đã cài đặt (cả cơ bản lẫn nâng cao) để giải phóng RAM cho Minikube.
