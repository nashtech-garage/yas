# Kịch bản kiểm thử hệ thống CI/CD

## Kịch bản 1: Kiểm thử luồng CI tự động (Mục II.3)

### Mục tiêu
Kiểm tra khi commit code trên một branch mới, Jenkins tự động phát hiện, build Docker image và push lên Docker Hub với tag là **Commit ID**.

### Bước 1. Tạo branch mới

```bash
git checkout -b dev_tax_service
```

### Bước 2. Chỉnh sửa source code

Sửa đổi một phần nhỏ trong service **tax**, ví dụ thêm một dòng log hoặc comment trong file:

- `TaxClassController.java`

### Bước 3. Commit và Push

```bash
git add .
git commit -m "test: update tax service for ci test"
git push origin dev_tax_service
```

### Bước 4. Kiểm tra Jenkins

- Truy cập Jenkins.
- Chọn Job **yas_ci** (Multibranch Pipeline).
- Kiểm tra branch `dev_tax_service` được tự động quét (**Scan Multibranch Pipeline**) và kích hoạt build.

### Bước 5. Chụp ảnh màn hình

Trong log Jenkins:

- Docker image được gắn tag bằng Commit ID:

```
Using commit tag: <commit_id_8_ky_tu>
```

- Docker push:

```
docker push besukem/tax:<commit_id_8_ky_tu>
```

### Bước 6. Kiểm tra Docker Hub

- Đăng nhập Docker Hub (tài khoản `besukem`).
- Mở repository `besukem/tax`.
- Chụp ảnh tag mới trùng với Commit ID.

---

# Kịch bản 2: Kiểm thử CD Job cho Developer (developer_build - Mục II.4)

## Mục tiêu

Triển khai thử nghiệm theo cơ chế **Fallback**:

- Service đang phát triển sử dụng image theo Commit ID.
- Các service còn lại sử dụng image `latest`.

## Bước 1. Build with Parameters

Chọn Job:

```
developer_build
```

Sau đó chọn **Build with Parameters**.

### Thiết lập tham số

| Tham số | Giá trị |
|----------|----------|
| NAMESPACE | `dev` (hoặc namespace riêng) |
| DRY_RUN | ✓ nếu chỉ chạy giả lập, bỏ chọn nếu deploy thật |
| BRANCH_tax | `dev_tax_service` |
| Các BRANCH khác | `main` hoặc `latest` |

---

## Bước 2. Kiểm tra log Jenkins

### Chụp phần phân giải branch

```
Resolved branch 'dev_tax_service'
for service 'tax'
to commit tag: <commit_id>
```

Các service khác:

```
Using default branch 'main'
Tag: latest
```

### Chụp phần Helm

```
Service tax
Image:
besukem/tax:<commit_id>
```

```
Service cart
Image:
besukem/cart:latest
```

---

## Bước 3. Nếu deploy thật

Ở cuối log sẽ xuất hiện hướng dẫn sửa file hosts.

Ví dụ:

```
IP_NODE storefront.dev.yas.local

IP_NODE api.dev.yas.local
```

Chụp ảnh phần này.

---

# Kịch bản 3: Kiểm thử Job Cleanup (developer_cleanup - Mục II.5)

## Mục tiêu

Xóa môi trường thử nghiệm của developer.

## Bước 1. Chạy Job

Chọn Job:

```
developer_cleanup
```

Chọn:

```
Build with Parameters
```

Điền:

| Tham số | Giá trị |
|----------|----------|
| NAMESPACE | `dev` |

Nhấn **Build**.

---

## Bước 2. Chụp ảnh kết quả

Chụp log Jenkins hiển thị:

- Thực thi `helm uninstall`
- Toàn bộ service trong namespace được xóa thành công.

---

# Kịch bản 4: Kiểm thử tự động deploy Dev qua GitOps (ArgoCD)

## Mục tiêu

Merge vào `main` → Jenkins tự build → cập nhật GitOps → ArgoCD tự triển khai namespace `dev`.

## Bước 1. Merge vào main

Merge branch:

```
dev_tax_service
```

vào

```
main
```

hoặc commit trực tiếp lên `main`.

---

## Bước 2. Kiểm tra Jenkins

Job:

```
auto_deploy_dev
```

được cấu hình quét định kỳ:

```
scm('H/5 * * * *')
```

### Chụp log Jenkins

Bao gồm:

- Build Docker image
- Push Commit ID
- Push tag `latest`

Ví dụ:

```
docker push ...:<commit_id>

docker push ...:latest
```

---

### Chụp log cập nhật GitOps

Python script cập nhật:

```
argocd/apps/dev/dev-tax.yaml
```

Sau đó:

```
git commit

chore(dev): update image tags to <commit_id> [skip ci]
```

và

```
git push
```

---

## Bước 3. Kiểm tra ArgoCD

Mở giao diện ArgoCD.

Chọn ứng dụng trong namespace:

```
dev
```

Chụp ảnh trạng thái:

- Reconciling
- Synced

đã cập nhật sang Commit ID mới nhất.

---

# Kịch bản 5: Kiểm thử Release Staging qua GitOps (ArgoCD)

## Mục tiêu

Khi tạo Release Tag (`vX.Y.Z`), Jenkins tự build và cập nhật GitOps triển khai lên namespace `staging`.

## Bước 1. Tạo Release Tag

```bash
git tag v1.0.0
git push origin v1.0.0
```

---

## Bước 2. Trigger Jenkins

Chọn Job:

```
release_tag_deploy
```

Chọn:

```
Build with Parameters
```

Điền:

| Tham số | Giá trị |
|----------|----------|
| RELEASE_TAG | `v1.0.0` |

Sau đó nhấn **Build**.

---

## Bước 3. Chụp ảnh kết quả

### Jenkins Log

Bao gồm:

- Build Docker image
- Push image tag `v1.0.0`
- Cập nhật GitOps trong:

```
argocd/apps/staging/
```

---

### ArgoCD

Mở giao diện ArgoCD.

Chụp trạng thái môi trường:

```
staging
```

đang tự động đồng bộ với:

```
targetRevision: v1.0.0
```