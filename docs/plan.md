# 📋 Kế hoạch triển khai Đồ án 1: Hệ thống CI cho YAS

> **Dự án:** YAS - Yet Another Shop | **Leader:** Nguyễn Lê Thế Vinh | **Nhóm:** 3 thành viên

---

## 👥 Thành viên

| Họ và tên | Vai trò | Viết tắt |
|-----------|---------|----------|
| Nguyễn Lê Thế Vinh | Leader – CI Pipeline & GitHub Config | **Vinh.NL** |
| Phạm Quang Vinh | Security Scanning & Infrastructure | **Vinh.PQ** |
| Lê Xuân Trí | Unit Test & Coverage | **Trí** |

---

## 🗓️ Timeline (3 tuần)

```
Tuần 1 ██████████░░░░░░░░░░  Phase 1: Setup & Foundation
Tuần 2 ░░░░░░░░░░██████████  Phase 2: CI Pipeline Core
Tuần 3 ░░░░░░░░░░░░░░░░░░██  Phase 3: Advanced & Report
```

---

## 🚀 Phase 1: Setup & Foundation (Tuần 1)

| # | Task | Phụ trách | Status |
|---|------|-----------|--------|
| 1.1 | Fork repo YAS, cấu hình Branch Protection (block push main, 2 reviewer, CI pass) | Vinh.NL | ⬜ |
| 1.2 | Cài đặt Jenkins server (Docker) + kết nối GitHub webhook | Vinh.PQ | ⬜ |
| 1.3 | Khảo sát cấu trúc monorepo YAS, liệt kê service + build tool | Trí | ⬜ |
| 1.4 | Tạo Jenkinsfile / GitHub Actions workflow cơ bản | Vinh.NL | ⬜ |
| 1.5 | Cài đặt SonarQube server (Docker) | Vinh.PQ | ⬜ |

**✅ Milestone 1:** Repo fork xong, branch protection bật, Jenkins + SonarQube chạy được.

---

## ⚙️ Phase 2: CI Pipeline Core (Tuần 2)

| # | Task | Phụ trách | Status |
|---|------|-----------|--------|
| 2.1 | Viết pipeline 2 phase: **Test → Build**, upload JUnit + JaCoCo report | Vinh.NL | ⬜ |
| 2.2 | Cấu hình **path filter** monorepo (chỉ trigger service thay đổi) | Vinh.NL + Trí | ⬜ |
| 2.3 | Tích hợp **Gitleaks** (secret scan) vào pipeline | Vinh.PQ | ⬜ |
| 2.4 | Tích hợp **SonarQube** + **Snyk** vào pipeline | Vinh.PQ | ⬜ |
| 2.5 | Viết unit test cho 2-3 service chính (Media, Product, Cart) | Trí | ⬜ |
| 2.6 | Test pipeline bằng cách tạo PR thử | Cả nhóm | ⬜ |

**✅ Milestone 2:** Pipeline CI hoạt động, monorepo filter OK, security tools tích hợp xong.

---

## 🏆 Phase 3: Advanced & Báo cáo (Tuần 3)

| # | Task | Phụ trách | Status |
|---|------|-----------|--------|
| 3.1 | Viết thêm unit test, đảm bảo **coverage > 70%** | Trí | ⬜ |
| 3.2 | Cấu hình pipeline **fail khi coverage < 70%** | Vinh.NL | ⬜ |
| 3.3 | Tạo branch riêng cho mỗi service (media/add-tests, product/add-tests...) | Trí | ⬜ |
| 3.4 | Review & fix issues từ SonarQube, Snyk | Vinh.PQ | ⬜ |
| 3.5 | Tạo **ít nhất 1 PR đang open** | Cả nhóm | ⬜ |
| 3.6 | Chụp screenshots cấu hình + Viết báo cáo `.docx` | Vinh.PQ + Vinh.NL | ⬜ |
| 3.7 | Review toàn bộ, dry-run lần cuối | Cả nhóm | ⬜ |

**✅ Milestone 3:** Coverage > 70%, PR open, báo cáo hoàn chỉnh, sẵn sàng nộp.

---

## 📊 Pipeline CI mục tiêu

```
Gitleaks ──▶ Test (JUnit + JaCoCo) ──▶ Build ──▶ SonarQube + Snyk
               │                                       │
          Upload Report                           Quality Gate
          (coverage > 70%)                     (pass/fail scan)
```

---

## 🔍 Checklist trước khi nộp

- [ ] Repo fork từ yas, branch protection bật (2 reviewer + CI pass)
- [ ] Pipeline ≥ 2 phase (test + build), upload test result + coverage
- [ ] Monorepo: chỉ build service thay đổi
- [ ] Unit test thêm, coverage > 70%
- [ ] Gitleaks + SonarQube + Snyk tích hợp
- [ ] Có ≥ 1 PR đang open
- [ ] Screenshots đầy đủ
- [ ] File `.docx` đặt tên đúng format MSSV

---

## ⚠️ Rủi ro & Giải pháp

| Rủi ro | Giải pháp |
|--------|-----------|
| Jenkins setup phức tạp | Dùng Docker image chính thức, hoặc chuyển GitHub Actions |
| Monorepo path filter khó | Dùng `dorny/paths-filter` (GH Actions) hoặc Jenkins changeset |
| Coverage khó đạt 70% | Tập trung 3-4 service nhỏ, test logic đơn giản trước |
| Build quá lâu | Cache Maven dependencies, parallel build |

> 📝 Leader (Vinh.NL) review tiến độ cuối mỗi tuần và điều phối lại nếu cần.
