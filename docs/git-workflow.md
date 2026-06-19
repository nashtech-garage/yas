# 🔀 Development Guide — Git Workflow cho Đồ Án YAS CI

> Tài liệu này quy định luồng làm việc Git của nhóm, đảm bảo tuân thủ **Yêu cầu số 3** của đồ án:
> *Branch Protection — không push trực tiếp vào `main`, mọi thay đổi phải qua PR với ≥ 2 reviewer approve và CI pass.*

---

## 1. Chiến Lược Nhánh (Branching Strategy)

Nhóm sử dụng mô hình **Feature Branch Workflow** dựa trên nhánh `main`:

```
main (protected)
 ├── feat/media-upload-api         ← Tính năng mới
 ├── test/media                    ← Bổ sung unit test cho service
 ├── fix/cart-null-pointer          ← Sửa bug
 ├── ci/jenkinsfile-coverage-gate   ← Thay đổi pipeline CI
 └── docs/report-week2             ← Tài liệu, báo cáo
```

### Quy tắc quan trọng:
- ⛔ **KHÔNG BAO GIỜ** push trực tiếp vào `main` (đã bị chặn bởi Branch Protection).
- ✅ Mọi thay đổi **PHẢI** đi qua Pull Request.
- ✅ Mỗi người tạo nhánh riêng cho task của mình, **KHÔNG** dùng chung nhánh.
- ✅ Luôn tạo nhánh mới từ `main` mới nhất.

---

## 2. Quy Tắc Đặt Tên Nhánh

### Format:
```
<type>/<scope hoặc mô-tả-ngắn>
```

### Các prefix được sử dụng:

| Prefix | Mục đích | Ví dụ |
|--------|----------|-------|
| `feat/` | Tính năng mới | `feat/product-search-api` |
| `test/` | Bổ sung unit test | `test/media`, `test/cart` |
| `fix/` | Sửa bug | `fix/order-total-calculation` |
| `ci/` | Thay đổi cấu hình CI/Jenkins | `ci/add-sonarqube-stage` |
| `docs/` | Tài liệu, báo cáo | `docs/setup-guide` |
| `refactor/` | Tái cấu trúc code | `refactor/cart-service-cleanup` |

### Quy tắc:
- Sử dụng **chữ thường**, phân cách bằng dấu gạch ngang `-`.
- Tên nhánh phải **ngắn gọn, mô tả rõ** mục đích.
- ❌ Sai: `MyBranch`, `test_1`, `update`, `abc`
- ✅ Đúng: `test/product`, `ci/coverage-gate-70`, `fix/media-upload-error`

---

## 3. Quy Tắc Commit Message (Conventional Commits)

### Format:
```
<type>(<scope>): <mô tả ngắn gọn>
```

### Bảng type:

| Type | Ý nghĩa | Ví dụ |
|------|----------|-------|
| `feat` | Thêm tính năng mới | `feat(product): add search endpoint` |
| `fix` | Sửa bug | `fix(cart): handle null quantity` |
| `test` | Thêm hoặc sửa test | `test(media): add upload service tests` |
| `ci` | Thay đổi CI pipeline | `ci(jenkins): add coverage gate stage` |
| `docs` | Thay đổi tài liệu | `docs: update README with setup guide` |
| `refactor` | Tái cấu trúc (không đổi logic) | `refactor(order): extract validation logic` |
| `chore` | Việc vặt (config, dependencies) | `chore: update spring-boot to 3.2.5` |

### Quy tắc:
- **Scope** = tên service hoặc module bị ảnh hưởng (`media`, `cart`, `product`, `jenkins`...).
- Mô tả viết bằng **tiếng Anh**, bắt đầu bằng **động từ nguyên mẫu** (add, fix, update, remove...).
- Dòng đầu tiên **≤ 72 ký tự**.
- ❌ Sai: `update code`, `fix bug`, `done`, `asdfg`
- ✅ Đúng: `test(cart): add unit tests for CartService`, `ci(jenkins): integrate gitleaks scanning`

---

## 4. Quy Trình Tạo Pull Request (PR)

### Bước 1: Chuẩn bị nhánh

```bash
# Cập nhật main mới nhất
git checkout main
git pull origin main

# Tạo nhánh mới từ main
git checkout -b feat/product-search-api

# Code, commit theo conventional commits
git add .
git commit -m "feat(product): add search endpoint with pagination"

# Push nhánh lên remote
git push origin feat/product-search-api
```

### Bước 2: Tạo PR trên GitHub

1. Vào repository trên GitHub → nhấn **"Compare & pull request"**.
2. Điền thông tin PR theo template:

```markdown
## Mô tả
- Thêm API search cho product service với hỗ trợ phân trang.

## Loại thay đổi
- [x] Tính năng mới (feat)
- [ ] Sửa bug (fix)
- [ ] Bổ sung test (test)
- [ ] CI/CD pipeline (ci)

## Service bị ảnh hưởng
- [x] product

## Checklist
- [x] Code đã pass local test (`mvn test`)
- [x] Commit message theo Conventional Commits
- [x] Không chứa secret/credential trong code
```

3. Chọn **base branch**: `main`.
4. Gán **ít nhất 2 reviewer** (các thành viên khác trong nhóm).
5. Nhấn **"Create pull request"**.

### Bước 3: Review & Merge

```
PR Created → Jenkins Pipeline tự động chạy
                    │
         ┌──────────┴──────────┐
         │                     │
    ❌ CI Fail            ✅ CI Pass
    → Fix code &          → Chờ Review
      push lại
                               │
                    ┌──────────┴──────────┐
                    │                     │
              ❌ < 2 Approve       ✅ ≥ 2 Approve
              → Nhắc reviewer      → Merge vào main
                                          │
                                   Xóa branch sau merge ✅
```

### Quy tắc review:
- Reviewer phải **đọc code thật sự**, không approve bừa.
- Nếu có góp ý → Comment trực tiếp trên dòng code.
- Chỉ approve khi: code đúng logic, test pass, commit message chuẩn.
- **Merge strategy:** Sử dụng **"Squash and merge"** để giữ lịch sử `main` sạch sẽ.

---

## 5. Xử Lý Conflict

Khi nhánh của bạn bị conflict với `main`:

```bash
# Cập nhật main mới nhất
git checkout main
git pull origin main

# Quay lại nhánh của bạn và rebase
git checkout feat/product-search-api
git rebase main

# Giải quyết conflict (nếu có)
# Sửa file conflict → git add → git rebase --continue

# Force push (vì đã rebase)
git push origin feat/product-search-api --force-with-lease
```

> ⚠️ **Lưu ý:** Sử dụng `--force-with-lease` thay vì `--force` để tránh ghi đè commit của người khác.

---

## 6. Lưu Ý Đặc Biệt cho Monorepo

Vì YAS là monorepo, Jenkins pipeline được cấu hình để **chỉ build/test service có thay đổi**. Do đó:

- ✅ Khi viết unit test cho `media`, **chỉ sửa file trong thư mục `media/`**.
- ✅ Mỗi PR nên tập trung vào **1 service duy nhất** khi có thể.
- ⚠️ Nếu PR sửa nhiều service → pipeline sẽ build/test tất cả service bị thay đổi → thời gian chạy lâu hơn.

### Ví dụ nhánh theo service (Yêu cầu nâng cao 7a):

```
test/media       ← TV1 viết unit test cho media service
test/product     ← TV2 viết unit test cho product service
test/cart        ← TV3 viết unit test cho cart service
test/order       ← TV4 viết unit test cho order service
```

---

## 7. Tóm Tắt Nhanh (Quick Reference)

```
📌 TẠO NHÁNH:     git checkout -b <type>/<scope>
📌 COMMIT:         git commit -m "<type>(<scope>): <mô tả>"
📌 PUSH:           git push origin <tên-nhánh>
📌 TẠO PR:         GitHub → Compare & pull request → Gán 2 reviewer
📌 MERGE:          Squash and merge (sau khi CI pass + 2 approve)
📌 SAU MERGE:      Xóa branch trên remote và local
📌 CẬP NHẬT:       git checkout main && git pull origin main
```
