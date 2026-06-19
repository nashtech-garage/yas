# Bảng Phân Chia Công Việc — Đồ Án 1: Triển Khai Hệ Thống CI

> **Công cụ CI/CD:** Jenkins  
> **Repository:** Đã fork về GitHub của nhóm  
> **Thời gian thực hiện:** 9 ngày (25/04 → 03/05/2026) | **Deadline:** 03/05/2026  
> **Số thành viên:** 4 người

---

## Tổng Quan Phân Công

| Thành viên | Mảng phụ trách | Unit Test |
|:----------:|----------------|-----------|
| TV1 | Jenkins Infrastructure & Pipeline | Không viết test |
| TV2 | Branch Protection & Unit Test | 10 module (4 đã xong + 6 mới) |
| TV3 | Security Scanning (Gitleaks, SonarQube, Snyk) | Không viết test |
| TV4 | Coverage Gate & Unit Test | 6 module |

Tất cả thành viên tự screenshot và viết báo cáo cho phần mình. Cuối cùng ghép thành 1 file `.docx`.

---

## TV1 — Jenkins Infrastructure & Pipeline

**Phạm vi:** Toàn bộ hạ tầng Jenkins và logic pipeline cốt lõi.  
**Ưu tiên hoàn thành sớm** vì các TV khác phụ thuộc vào Jenkinsfile để thêm stage.

| # | Task | Mô tả | Deliverable |
|:-:|------|-------|-------------|
| 1 | Cài đặt Jenkins Server | Triển khai Jenkins bằng Docker. Cài plugin: Git, Pipeline, Multibranch Pipeline, JaCoCo, Warnings NG. | Jenkins dashboard hoạt động |
| 2 | Cấu hình Webhook GitHub → Jenkins | Tạo Webhook trên GitHub trỏ về Jenkins (dùng ngrok/cloudflare tunnel nếu chạy local). Cấu hình credential (PAT hoặc SSH key). | Push event trigger Jenkins build |
| 3 | Thiết lập Multibranch Pipeline | Tạo Multibranch Pipeline Job, cấu hình Branch Source từ GitHub repo. Jenkins tự quét và nhận diện branch mới. | Jenkins scan được tất cả branch |
| 4 | Viết Jenkinsfile (Test → Build) | Viết `Jenkinsfile` ở root repo với 2 stage cơ bản: **Test** (`mvn test` + JUnit publisher) và **Build** (`mvn package -DskipTests`). | Pipeline chạy thành công |
| 5 | Cấu hình Monorepo Change Detection | Trong `Jenkinsfile`, dùng `git diff` để detect thư mục thay đổi → chỉ build/test service tương ứng. | Push vào `media/` chỉ trigger build `media` |
| 6 | Kiểm thử end-to-end | Tạo feature branch, push code, verify pipeline tự kích hoạt đúng service. | Demo luồng hoạt động |

---

## TV2 — Branch Protection & Unit Test

**Phạm vi:** Cấu hình bảo vệ nhánh trên GitHub + viết unit test cho 10 service module (coverage >= 70%).

### Công việc chính (non-test)

| # | Task | Mô tả | Deliverable |
|:-:|------|-------|-------------|
| 1 | Cấu hình GitHub Branch Protection | Require PR, Require 2 approvals, Require status checks (Jenkins), Block direct push. | Push trực tiếp vào `main` bị reject |
| 2 | Tạo PR demo (trạng thái Open) | Tạo ít nhất 1 PR vào `main` ở trạng thái Open để minh chứng luồng làm việc. | 1 PR Open trên GitHub |

### Unit Test — 10 module

| # | Module | Trạng thái | Nhánh |
|:-:|--------|:----------:|-------|
| 1 | `media` | Da hoan thanh | `test/media` |
| 2 | `product` | Da hoan thanh | `test/product` |
| 3 | `order` | Da hoan thanh | `test/order` |
| 4 | `inventory` | Da hoan thanh | `test/inventory` |
| 5 | `payment` | Chua lam | `test/payment` |
| 6 | `promotion` | Chua lam | `test/promotion` |
| 7 | `rating` | Chua lam | `test/rating` |
| 8 | `delivery` | Chua lam | `test/delivery` |
| 9 | `sampledata` | Chua lam | `test/sampledata` |
| 10 | `recommendation` | Chua lam | `test/recommendation` |

---

## TV3 — Security Scanning

**Phạm vi:** Tích hợp 3 công cụ quét bảo mật/chất lượng code vào Jenkins pipeline.  
**Không viết unit test** — khối lượng tích hợp security đã chiếm toàn bộ effort.

| # | Task | Mô tả | Deliverable |
|:-:|------|-------|-------------|
| 1 | Tích hợp Gitleaks | Thêm stage `Secret Scanning` trong Jenkinsfile chạy Gitleaks. Cấu hình `.gitleaksignore` cho false positive. Pipeline FAIL nếu phát hiện secret bị leak. | Stage Gitleaks hoạt động |
| 2 | Tích hợp SonarQube | Deploy SonarQube server (Docker). Cài SonarQube Scanner plugin trên Jenkins. Thêm stage `Code Quality` trong Jenkinsfile. | SonarQube dashboard hiển thị kết quả |
| 3 | Tích hợp Snyk | Đăng ký Snyk account, lấy API token. Thêm stage `Dependency Scan` trong Jenkinsfile chạy `snyk test`. | Snyk report trong Jenkins |

---

## TV4 — Coverage Gate & Unit Test

**Phạm vi:** Cấu hình pipeline chặn khi coverage thấp + viết unit test cho 6 service module (coverage >= 70%).

### Công việc chính (non-test)

| # | Task | Mô tả | Deliverable |
|:-:|------|-------|-------------|
| 1 | Cấu hình JaCoCo + Coverage Gate | Thêm JaCoCo plugin vào `pom.xml`. Thêm stage `Coverage Report` trong Jenkinsfile, publish JaCoCo report. Pipeline FAIL nếu coverage < 70%. | Pipeline tự fail khi coverage < 70% |
| 2 | Tổng hợp báo cáo | Ghép 4 phần báo cáo thành 1 file `.docx` duy nhất, thêm link repo + format đúng quy định. | File `.docx` hoàn chỉnh |

### Unit Test — 6 module

| # | Module | ~Dòng service | Test có sẵn | Nhánh |
|:-:|--------|:-------------:|:-----------:|-------|
| 1 | `customer` | 401 | 5 | `test/customer` |
| 2 | `location` | 364 | 7 | `test/location` |
| 3 | `cart` | 202 | 3 | `test/cart` |
| 4 | `tax` | 325 | 1 | `test/tax` |
| 5 | `search` | 258 | 4 | `test/search` |
| 6 | `webhook` | 248 | 4 | `test/webhook` |

---

## Quy Trình Documentation (Tất cả thành viên)

Ai cài đặt phần nào thì tự screenshot và viết báo cáo phần đó. Cuối cùng ghép lại.

| Bước | Mô tả | Thời điểm |
|:----:|-------|:---------:|
| 1 | Mỗi người screenshot từng bước cài đặt/cấu hình ngay khi làm | Trong lúc làm |
| 2 | Mỗi người viết mô tả cho phần mình (file `.md` riêng hoặc Google Docs) | Tuần 2-3 |
| 3 | TV4 tổng hợp 4 phần thành 1 file `.docx` duy nhất | Tuần 3 |
| 4 | Cả nhóm review báo cáo lần cuối trước khi nộp | Trước deadline |

**Đặt tên file báo cáo:** `<MSSV1>_<MSSV2>_<MSSV3>_<MSSV4>.docx` (MSSV sắp xếp tăng dần)

---

## Timeline (9 ngày)

| Giai đoạn | Ngày | Nội dung |
|:---------:|:----:|----------|
| Phase 1 | 25/04 – 27/04 (3 ngày) | Jenkins Infrastructure & Branch Protection |
| Phase 2 | 28/04 – 30/04 (3 ngày) | Unit Test, Coverage Gate & Security Tools |
| Phase 3 | 01/05 – 03/05 (3 ngày) | Hoàn thiện, viết docs & nộp bài |

```
       Phase 1 (25-27/04)       Phase 2 (28-30/04)       Phase 3 (01-03/05)
  ──────────────────────   ──────────────────────   ──────────────────────

  TV1 ████████████████████ ░░░░░░░░░░░░░░░░░░░░░░ ░░░░░░░░░░░░░░░░░░░░░░
      Jenkins + Webhook     (hoan thanh)            Viet docs phan minh
      Multibranch Pipeline
      Jenkinsfile + Monorepo

  TV2 ████████████░░░░░░░░ ████████████████████░░ ████████████████████░░
      Branch Protection     Unit Test (da xong 4)   Unit Test (6 module moi)
                                                    + viet docs phan minh

  TV3 ░░░░░░░░░░░░░░░░░░░░ ████████████████████░░ ████████████████████░░
      (cho Jenkinsfile)     Gitleaks + SonarQube     Snyk
                                                    + viet docs phan minh

  TV4 ░░░░░░░░░░░░░░░░░░░░ ████████████████████░░ ████████████████████░░
      (cho Jenkinsfile)     JaCoCo + Coverage Gate   Unit Test (6 module)
                                                    + viet docs + ghep .docx
  ──────────────────────   ──────────────────────   ──────────────────────
                                                      03/05 Deadline
```

---

## Thu Tu Phu Thuoc

```
TV1: Jenkins + Jenkinsfile + Monorepo
         |
         v (Jenkinsfile san sang)
    ┌────┴─────────────┐
    v                  v
   TV3                TV4
   Them stage         Them stage
   Security Scan      Coverage Gate
    |                  |
    v                  v
   TV2: Branch Protection bat Status Check
         |
         v
   Tat ca PR phai pass pipeline moi merge duoc
```

> **Luu y:** TV1 can hoan thanh Jenkinsfile co ban trong Phase 1 (truoc 28/04) de TV3 va TV4 co the them stage cua minh tu Phase 2. TV2 co the lam Branch Protection song song nhung nen bat Status Check sau khi Jenkins pipeline da on dinh.
