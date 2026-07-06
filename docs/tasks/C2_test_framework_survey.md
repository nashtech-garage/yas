# C2. Báo cáo xác định Test Framework

> **Người thực hiện:** Lê Xuân Trí  
> **Ngày thực hiện:** 2026-04-30  
> **Mục tiêu:** Xác định công cụ build, test framework và đường dẫn test để định hướng việc viết Unit Test cho toàn bộ dự án YAS.

---

## 1. Công cụ Build và Quản lý Dependency
Hệ thống sử dụng **Maven** theo mô hình **Multi-module (Reactor)**.
*   **Root POM**: Quản lý toàn bộ phiên bản dependency (thông qua `<dependencyManagement>`) và các plugin dùng chung (thông qua `<pluginManagement>`).
*   **Java Version**: 25
*   Tất cả các module Java đều kế thừa cấu hình test chung từ root `pom.xml`, đảm bảo tính đồng nhất trên toàn hệ thống.

## 2. Các Test Framework đang sử dụng
Thông qua phân tích file `pom.xml` gốc, các thư viện phục vụ testing được sử dụng trong YAS bao gồm:

| Công cụ | Phiên bản (hoặc quản lý bởi) | Vai trò |
|---------|------------------------------|---------|
| **JUnit 5 (Jupiter)** | `spring-boot-starter-test` | Framework chính để chạy Unit Test. |
| **Mockito** | `spring-boot-starter-test` | Mocking framework dùng cho việc mock các service, repository. |
| **Spring Test / MockMvc** | `spring-boot-starter-test` | Test context Spring và test các REST Controller. |
| **AssertJ / Hamcrest** | `spring-boot-starter-test` | Viết các câu lệnh assert dễ đọc hơn. |
| **Testcontainers** | 2.0.3 | Quản lý container (PostgreSQL, Keycloak) cho Integration Test (IT). |
| **REST Assured** | 6.0.0 | Gọi và test API end-to-end cho Integration Test. |
| **Instancio** | 5.0.2 | Tự động sinh dữ liệu test (test data builder). |

## 3. Cấu trúc Thư mục và Lệnh chạy Test

### A. Unit Test (Cấp độ ưu tiên cao nhất cho Trí)
*   **Thư mục chứa code test**: `src/test/java/...`
*   **Lệnh chạy toàn bộ**: `mvn test`
*   **Lệnh chạy + Đo coverage**: `mvn test jacoco:report`
*   **Plugin thực thi**: Maven Surefire Plugin.

### B. Integration Test (IT)
*   **Thư mục chứa code test**: `src/it/java/...`
*   **Cấu hình**: Được thêm vào build thông qua `build-helper-maven-plugin`. Pattern file IT thường là `**/*IT.java`.
*   **Lệnh chạy**: `mvn verify`
*   **Plugin thực thi**: Maven Failsafe Plugin.

---

## 4. Kết luận
Dự án có bộ khung testing rất hiện đại và đầy đủ (JUnit 5 + Mockito cho UT, Testcontainers + REST Assured cho IT).
**Hướng đi tiếp theo của Trí:**
Chỉ tập trung viết test vào thư mục `src/test/java`, sử dụng **JUnit 5 + Mockito**, tránh đụng vào cấu hình `Testcontainers` của IT để tiết kiệm thời gian và đảm bảo build nhanh trên local.
