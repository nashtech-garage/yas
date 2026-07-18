# 📌 Phân công công việc – Đồ án CI/CD YAS

---

## 🟦 Nguyễn Lê Thế Vinh (Leader – CI Pipeline & GitHub Config)

### Trách nhiệm chính
Quản lý repo, xây dựng pipeline CI, cấu hình GitHub, tổng hợp báo cáo.

### Đầu việc

| # | Công việc | Phase | Ưu tiên | Status |
|---|-----------|-------|---------|--------|
| A1 | Fork repo YAS về GitHub nhóm | 1 | 🔴 Cao | ⬜ |
| A2 | Cấu hình Branch Protection Rules: block push main, require 2 reviewer approve, require CI pass | 1 | 🔴 Cao | ⬜ |
| A3 | Kết nối Jenkins/GitHub Actions với repo (webhook, credentials, token) | 1 | 🔴 Cao | ⬜ |
| A4 | Tạo Jenkinsfile hoặc `.github/workflows/*.yml` cơ bản | 1 | 🔴 Cao | ⬜ |
| A5 | Viết pipeline CI với 2 phase: **Test** → **Build** | 2 | 🔴 Cao | ⬜ |
| A6 | Cấu hình upload **JUnit test result** trong pipeline | 2 | 🔴 Cao | ⬜ |
| A7 | Cấu hình upload **JaCoCo coverage report** trong pipeline | 2 | 🔴 Cao | ⬜ |
| A8 | Cấu hình **path filter** cho monorepo (chỉ trigger service thay đổi) | 2 | 🔴 Cao | ⬜ |
| A9 | Cấu hình pipeline **fail khi coverage < 70%** (JaCoCo threshold) | 3 | 🟡 TB | ⬜ |
| A10 | Tổng hợp viết file báo cáo `.docx` theo format yêu cầu | 3 | 🔴 Cao | ⬜ |
| A11 | Review toàn bộ pipeline, điều phối nhóm, dry-run cuối cùng | 3 | 🔴 Cao | ⬜ |

### Deliverables
- ✅ GitHub repo với branch protection hoàn chỉnh
- ✅ Pipeline CI chạy Test → Build thành công
- ✅ Monorepo path filter hoạt động
- ✅ File báo cáo `.docx`

---

## 🟩 Phạm Quang Vinh (Security Scanning & Infrastructure)

### Trách nhiệm chính
Dựng hạ tầng Jenkins/SonarQube, tích hợp các công cụ security scanning.

### Đầu việc

| # | Công việc | Phase | Ưu tiên | Status |
|---|-----------|-------|---------|--------|
| B1 | Cài đặt **Jenkins server** bằng Docker (hoặc VM) | 1 | 🔴 Cao | ⬜ |
| B2 | Cấu hình Jenkins: cài plugin cần thiết (GitHub, Pipeline, JUnit, JaCoCo, Gitleaks) | 1 | 🔴 Cao | ⬜ |
| B3 | Cài đặt **SonarQube server** bằng Docker | 1 | 🟡 TB | ⬜ |
| B4 | Tích hợp **Gitleaks** vào pipeline – scan secret/credentials leak | 2 | 🔴 Cao | ⬜ |
| B5 | Tích hợp **SonarQube** vào pipeline – scan code quality, bugs, code smell | 2 | 🔴 Cao | ⬜ |
| B6 | Tích hợp **Snyk** vào pipeline – scan dependency vulnerabilities | 2 | 🔴 Cao | ⬜ |
| B7 | Cấu hình SonarQube Quality Gate (pass/fail conditions) | 2 | 🟡 TB | ⬜ |
| B8 | Review & fix các issues được phát hiện bởi SonarQube/Snyk | 3 | 🟡 TB | ⬜ |
| B9 | **Chụp screenshots** tất cả cấu hình: Jenkins job, Gitleaks, SonarQube, Snyk | 3 | 🔴 Cao | ⬜ |
| B10 | Hỗ trợ viết phần Security trong báo cáo | 3 | 🟡 TB | ⬜ |

### Deliverables
- ✅ Jenkins server chạy ổn định
- ✅ SonarQube server hoạt động
- ✅ Gitleaks + SonarQube + Snyk tích hợp trong pipeline
- ✅ Screenshots đầy đủ các bước cấu hình

---

## 🟨 Lê Xuân Trí (Unit Test & Coverage)

### Trách nhiệm chính
Phân tích code các service, viết unit test, đảm bảo coverage > 70%.

### Đầu việc

| # | Công việc | Phase | Ưu tiên | Status |
|---|-----------|-------|---------|--------|
| C1 | Khảo sát cấu trúc monorepo YAS: liệt kê tất cả service, build tool (Maven/Gradle) | 1 | 🔴 Cao | ⬜ |
| C2 | Xác định test framework đang dùng (JUnit 5, Mockito, Testcontainers) | 1 | 🔴 Cao | ⬜ |
| C3 | Đánh giá coverage hiện tại của từng service | 1 | 🟡 TB | ⬜ |
| C4 | Viết unit test cho **Media Service** (tạo branch `media/add-tests`) | 2 | 🔴 Cao | ⬜ |
| C5 | Viết unit test cho **Product Service** (tạo branch `product/add-tests`) | 2 | 🔴 Cao | ⬜ |
| C6 | Viết unit test cho **Cart Service** (tạo branch `cart/add-tests`) | 2 | 🟡 TB | ⬜ |
| C7 | Hỗ trợ Vinh.NL cấu hình path filter monorepo | 2 | 🟡 TB | ⬜ |
| C8 | Viết unit test cho các service còn lại (Order, Customer, Inventory...) | 3 | 🟡 TB | ⬜ |
| C9 | Đảm bảo **coverage > 70%** cho tất cả service có test | 3 | 🔴 Cao | ⬜ |
| C10 | Tạo PR từ các branch test để demo | 3 | 🔴 Cao | ⬜ |

### Deliverables
- ✅ Tài liệu khảo sát cấu trúc monorepo
- ✅ Unit test mới cho ≥ 3 service
- ✅ Coverage > 70% cho các service target
- ✅ PR open từ branch test

---

## 📅 Lịch họp nhóm đề xuất

| Thời điểm | Nội dung |
|-----------|----------|
| Đầu tuần 1 | Kick-off: phân chia công việc, setup môi trường |
| Cuối tuần 1 | Review Phase 1: demo Jenkins, SonarQube, repo setup |
| Cuối tuần 2 | Review Phase 2: demo pipeline chạy, security scan |
| Giữa tuần 3 | Review Phase 3: coverage check, chuẩn bị báo cáo |
| Cuối tuần 3 | Final review: dry-run, hoàn thiện báo cáo, nộp bài |

---

## 📈 Theo dõi tiến độ

| Phase | Vinh.NL | Vinh.PQ | Trí | Tổng |
|-------|---------|---------|-----|------|
| Phase 1 | 0/4 | 0/3 | 0/3 | **0/10** |
| Phase 2 | 0/4 | 0/4 | 0/4 | **0/12** |
| Phase 3 | 0/3 | 0/3 | 0/3 | **0/9** |
| **Tổng** | **0/11** | **0/10** | **0/10** | **0/31** |

> Cập nhật status: ⬜ Chưa làm | 🔄 Đang làm | ✅ Hoàn thành | ❌ Blocked
