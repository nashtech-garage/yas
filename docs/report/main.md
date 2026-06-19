# BÁO CÁO ĐỒ ÁN 1: TRIỂN KHAI HỆ THỐNG CI

**Môn học:** DevOps  
**Đề tài:** Xây dựng CI Pipeline cho hệ thống YAS — Yet Another Shop  
**Công cụ CI/CD:** Jenkins  
**Thời gian thực hiện:** 25/04/2026 – 03/05/2026  

---

## I. Thông Tin Nhóm

| Thành viên | MSSV | Phần phụ trách |
|------------|:----:|----------------|
| [Họ và tên TV1] | `XXXXXXXX` | Jenkins Infrastructure và Pipeline |
| [Họ và tên TV2] | `XXXXXXXX` | Branch Protection và Unit Test (10 modules) |
| [Họ và tên TV3] | `XXXXXXXX` | Security Scanning |
| [Họ và tên TV4] | `XXXXXXXX` | Coverage Gate và Unit Test (6 modules) |

**Link GitHub Repository:** `https://github.com/<ten-nhom>/yas`

---

## II. Kiến Trúc Pipeline

Nhóm triển khai CI pipeline theo luồng sau:

```
Developer Push / Pull Request
            |
            v
    GitHub Webhook
            |
            v
    Jenkins (Multibranch Pipeline)
            |
            |-- Stage 1: Secret Scanning    (Gitleaks)
            |-- Stage 2: Test               (Maven + JUnit)
            |-- Stage 3: Coverage Report    (JaCoCo >= 70%)
            |-- Stage 4: Code Quality       (SonarQube)
            |-- Stage 5: Dependency Scan    (Snyk)
            |-- Stage 6: Build              (Maven package)
            |
            v
    GitHub Branch Protection
    (Yêu cầu 2 approvals + CI Pass mới cho phép merge)
```

**Tối ưu hóa Monorepo:** Pipeline chỉ kích hoạt build/test cho service có thay đổi, sử dụng `git diff` để phát hiện thư mục bị ảnh hưởng.

---

## III. Phân Công Công Việc

| Thành viên | Nội dung thực hiện | File báo cáo chi tiết |
|:----------:|--------------------|-----------------------|
| TV1 | Cài đặt Jenkins Server, cấu hình Webhook, tạo Multibranch Pipeline, viết Jenkinsfile với logic Monorepo | [tv1-jenkins.md](./tv1-jenkins.md) |
| TV2 | Cấu hình Branch Protection trên GitHub, viết unit test cho 10 service module | [tv2-branch-protection.md](./tv2-branch-protection.md) |
| TV3 | Tích hợp Gitleaks, SonarQube, Snyk vào pipeline | [tv3-security-scanning.md](./tv3-security-scanning.md) |
| TV4 | Cấu hình JaCoCo Coverage Gate (>= 70%); viết unit test cho 6 service module; tổng hợp báo cáo | [tv4-coverage-gate.md](./tv4-coverage-gate.md) |

---

## IV. Checklist Yêu Cầu Đồ Án

### Yêu cầu bắt buộc

| # | Yêu cầu | Trạng thái |
|:-:|---------|:----------:|
| 1 | Công cụ CI/CD: Jenkins | Hoàn thành |
| 2 | Fork repository về GitHub của nhóm | Hoàn thành |
| 3 | Branch Protection: cấm push thẳng vào `main`, yêu cầu 2 approvals + CI pass | Hoàn thành |
| 4 | Multibranch Pipeline: Jenkins tự quét và chạy pipeline cho từng branch | Hoàn thành |
| 5 | Pipeline có ít nhất 2 giai đoạn: Test và Build | Hoàn thành |
| 6 | Upload kết quả test và báo cáo độ phủ | Hoàn thành |
| 7 | Monorepo Optimization: chỉ build/test service có thay đổi | Hoàn thành |

### Yêu cầu nâng cao

| # | Yêu cầu | Trạng thái |
|:-:|---------|:----------:|
| a | Unit test cho từng service, tạo branch riêng biệt | Hoàn thành |
| b | Pipeline fail khi coverage < 70% (JaCoCo Gate) | Hoàn thành |
| c | Tích hợp Gitleaks, SonarQube, Snyk | Hoàn thành |

### Yêu cầu nộp bài

| # | Yêu cầu | Giá trị |
|:-:|---------|---------|
| 1 | Link GitHub Repository | `https://github.com/<ten-nhom>/yas` |
| 2 | Pull Request đang ở trạng thái Open | `https://github.com/<ten-nhom>/yas/pull/<so>` |
| 3 | File báo cáo | `<MSSV1>_<MSSV2>_<MSSV3>_<MSSV4>.docx` |

---

## V. Tổng Hợp Kết Quả Coverage

| Service | Coverage (Instructions) | Coverage (Branches) | Đạt >= 70% |
|---------|:-----------------------:|:-------------------:|:----------:|
| media   | %                       | %                   |            |
| product | %                       | %                   |            |
| order   | %                       | %                   |            |
| inventory| %                       | %                   |            |
| payment | %                       | %                   |            |
| promotion| %                       | %                   |            |
| rating  | %                       | %                   |            |
| delivery| %                       | %                   |            |
| sampledata| %                      | %                   |            |
| recommendation| %                  | %                   |            |
| customer| %                       | %                   |            |
| location| %                       | %                   |            |
| cart    | %                       | %                   |            |
| tax     | %                       | %                   |            |
| search  | %                       | %                   |            |
| webhook | %                       | %                   |            |

---

*Báo cáo này được tổng hợp từ 4 phần của các thành viên. TV4 chịu trách nhiệm ghép thành file `.docx` cuối cùng.*
