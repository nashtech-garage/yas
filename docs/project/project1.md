# Đồ án 1: Triển khai hệ thống CI

## I. Mô tả
Trong môn học này, các bạn được yêu cầu xây dựng một quy trình, hệ thống **CI/CD** và **Monitoring** để có thể deploy, vận hành và giám sát hệ thống **"YAS: Yet Another Shop"**.

- **Link dự án:** [https://github.com/nashtech-garage/yas](https://github.com/nashtech-garage/yas)
- **Giới thiệu:** YAS là một dự án cá nhân nhằm mục đích thực hành xây dựng một ứng dụng microservice điển hình bằng Java.

### Các công nghệ và framework sử dụng:
*   **Ngôn ngữ & Framework:** Java 21, Spring Boot 3.2
*   **Testing:** Testcontainers
*   **Frontend:** Next.js
*   **Security & Middleware:** Keycloak, Kafka, Elasticsearch
*   **Orchestration:** K8s (Kubernetes)
*   **CI/CD:** GitHub Actions, Jenkins, GitLab CI/CD
*   **Quality & Security:** SonarCloud, Gitleaks, Snyk
*   **Monitoring:** OpenTelemetry, Grafana, Loki, Prometheus, Tempo

---

## II. Yêu cầu
Đây là đồ án đầu tiên trong chuỗi đồ án môn học DevOps. Các bạn cần xây dựng pipeline cho quá trình **CI (Continuous Integration)** với các yêu cầu cụ thể sau:

1.  **Công cụ:** Có thể sử dụng **GitHub Actions**, **GitLab CI/CD**, hoặc **Jenkins**.
2.  **Repository:** Fork một repo mới từ [YAS GitHub](https://github.com/nashtech-garage/yas) cho riêng nhóm của mình.
3.  **Branch Protection:** Cấu hình GitHub để không cho phép push trực tiếp vào branch `main`. Mỗi PR cần ít nhất **2 reviewer approve** và **CI pass** mới cho phép merge.
4.  **Multi-branch Pipeline:** Cấu hình để hệ thống có thể quét và chạy pipeline cho từng branch.
5.  **Pipeline Phases:** Pipeline cần có ít nhất 2 giai đoạn: **Test** và **Build**.
    *   Phase **Test** cần upload kết quả test và báo cáo độ phủ (test coverage).
6.  **Monorepo Optimization:** Vì hệ thống sử dụng mô hình monorepo, các bạn cần cấu hình để pipeline chỉ kích hoạt cho service cụ thể khi có thay đổi trong thư mục của service đó (ví dụ: thay đổi trong `media-service` thì chỉ build/test lại `media-service`).
7.  **Yêu cầu nâng cao:**
    *   **a.** Thêm unit test vào code để tăng độ phủ. Tạo branch riêng cho từng service (Media, Product, Cart...) khi thêm testcase.
    *   **b.** Điều chỉnh pipeline để chỉ cho phép pass khi **độ phủ testcase > 70%**.
    *   **c.** Sử dụng **Gitleaks**, **SonarQube**, **Snyk** để quét lỗ hổng bảo mật và đánh giá chất lượng code.

---

## III. Quy định

### 1. Thành phần nhóm
*   Mỗi nhóm gồm **4 sinh viên**.

### 2. Thời gian thực hiện
*   Thời gian làm bài: **3 tuần** (Hạn chót: **17/3/2026**).

### 3. Hình thức nộp bài
Các nhóm tạo một file báo cáo (format `.docx`) gồm các thông tin:
*   **a.** Link tới GitHub repository của nhóm (tối thiểu phải có 1 PR đang ở trạng thái Open).
*   **b.** Hình ảnh minh họa các bước cấu hình (Jenkins job, Gitleaks, SonarQube, Snyk...).
*   **c.** **Quy tắc đặt tên file:** `<MSSV1>_<MSSV2>_<MSSV3>_<MSSV4>.docx`
    *   Thứ tự MSSV sắp xếp **tăng dần**.
    *   Ví dụ: `23120000_23120001_23120002_23120003.docx`.
