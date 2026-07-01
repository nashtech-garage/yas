# Walkthrough - Tích hợp quản lý khóa bảo mật với Sealed Secrets

Tài liệu này tóm tắt toàn bộ các bước thiết lập và di chuyển các thông tin nhạy cảm (secrets) sang công cụ mã hóa **Bitnami Sealed Secrets**, loại bỏ hoàn toàn mật khẩu dạng plaintext khỏi Git.

## Các thay đổi đã thực hiện

### 1. Cài đặt Sealed Secrets Operator & CLI
* **[setup-cluster.sh](file:///c:/Users/PC/ci-cd/yas/yas/k8s/deploy/setup-cluster.sh)**:
  * Thêm Helm repository chính xác của Sealed Secrets (`https://bitnami.github.io/sealed-secrets`).
  * Tích hợp cài đặt tự động Sealed Secrets Operator vào cụm với cờ `--set keyRenewPeriod=0` để quản lý xoay vòng khóa thủ công.
  * Đảm bảo kiểm tra và khởi tạo an toàn namespace `kube-system`.
  * **Mới bổ sung**: Thêm bước kiểm tra trạng thái Deployment của Sealed Secrets (`kubectl rollout status`) để đảm bảo dịch vụ đã sẵn sàng hoạt động trước khi tiếp tục.
  * **Mới bổ sung**: Tự động tải xuống, kiểm tra và cài đặt công cụ dòng lệnh `kubeseal` (CLI) trực tiếp lên máy Master nếu hệ thống chưa cài đặt.

### 2. Loại bỏ Secrets dạng Plain-Text
Đã dọn dẹp các trường thông tin nhạy cảm (`password`, `client-secret`, `apiKey`) trong các tệp values cấu hình:
* **[values.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/charts/yas-configuration/values.yaml)** của `yas-configuration`.
* **[cluster-config-dev.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/deploy/cluster-config-dev.yaml)**
* **[cluster-config-staging.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/deploy/cluster-config-staging.yaml)**
* **[cluster-config-production.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/deploy/cluster-config-production.yaml)**
* **[values.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/deploy/keycloak/keycloak/values.yaml)** của Keycloak.
* **[values.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/deploy/postgres/postgresql/values.yaml)** của Postgresql.
* **[values.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/deploy/kafka/kafka-cluster/values.yaml)** của Kafka-cluster.

### 3. Vô hiệu hóa các tệp template tạo Secret cũ
Ghi đè nội dung các tệp template tạo Secret tĩnh chứa plaintext bằng dòng chú thích deprecate (vô hiệu hóa) để Helm không sinh ra các Secret này nữa:
* **[yas-credentials.secret.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/charts/yas-configuration/templates/yas-credentials.secret.yaml)**
* **[postgresql-credential.secret.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/deploy/keycloak/keycloak/templates/postgresql-credential.secret.yaml)**
* **[keycloak-credential.secret.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/deploy/keycloak/keycloak/templates/keycloak-credential.secret.yaml)**
* **[credentials.secret.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/deploy/postgres/postgresql/templates/credentials.secret.yaml)**
* **[user-credentials.secret.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/deploy/elasticsearch/elasticsearch-cluster/templates/user-credentials.secret.yaml)**

### 4. Tạo các thư mục và mã hóa SealedSecrets
* Tạo cấu trúc thư mục lưu trữ cho dev, staging và production tại `k8s/sealed-secrets/`.
* Tải tệp khóa công khai `sealed-secrets-public.key` do người dùng cung cấp về dự án.
* Chạy mã hóa tự động offline thông qua công cụ `kubeseal` và sinh ra các tệp SealedSecret bảo mật:
  * **dev**: `yas-credentials.yaml`, `postgresql-credentials.yaml`, `keycloak-db-credentials.yaml`, `keycloak-admin-credentials.yaml`, `elasticsearch-credentials.yaml`.
  * **staging**: Tương tự cho môi trường staging.
  * **production**: Tương tự cho môi trường production.

### 5. Tích hợp Gitleaks trong CI/CD
* **[gitleaks.toml](file:///c:/Users/PC/ci-cd/yas/yas/gitleaks.toml)**: Thêm khóa công khai `sealed-secrets-public.key` và đường dẫn chứa các SealedSecret `k8s/sealed-secrets/.*\.yaml` vào allowlist để Gitleaks bỏ qua (do chúng đã được mã hóa an toàn).
* **[.github/workflows/ci.yml](file:///c:/Users/PC/ci-cd/yas/yas/.github/workflows/ci.yml)**:
  * Thêm bộ lọc thay đổi cho thư mục `k8s/` (`k8s/**`).
  * Thêm job `gitleaks-scan-k8s` để tự động quét kiểm tra nếu phát hiện rò rỉ secret trong thư mục `k8s/`.
  * Cập nhật job `ci-gate` bắt buộc phải vượt qua bước quét này trước khi merge.

### 6. Tích hợp đồng bộ tự động qua ArgoCD
* **[sealed-secrets-applicationset.yaml](file:///c:/Users/PC/ci-cd/yas/yas/k8s/argocd/applicationsets/sealed-secrets-applicationset.yaml)**: Tạo tệp cấu hình ApplicationSet mới cho ArgoCD để quản lý đồng bộ Sealed Secrets của 3 môi trường tại Sync Wave là `-3` (đảm bảo giải mã và có sẵn Secret thực tế trước khi cấu hình ứng dụng được nạp ở wave `-1`).

---

## Kết quả
Toàn bộ quy trình cấu hình, mã hóa và thiết lập CI/CD, GitOps cho Sealed Secrets đã được triển khai hoàn chỉnh, đúng cú pháp và không còn rò rỉ dữ liệu nhạy cảm dạng plaintext trong dự án.
