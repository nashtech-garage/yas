# Hướng dẫn Kiểm tra Thủ công Cấu trúc Monorepo YAS

Tài liệu này hướng dẫn cách tự tay kiểm tra (verify) tính chính xác của các thông tin trong báo cáo khảo sát [C1_monorepo_survey.md](file:///home/pearspringmind/Studying/Devops/Lab%201/yas/docs/tasks/C1_monorepo_survey.md).

---

## 1. Kiểm tra Cấu trúc Monorepo & Phiên bản
Mở file **root [pom.xml](file:///home/pearspringmind/Studying/Devops/Lab%201/yas/pom.xml)** để xác nhận các thông số sau:

*   **Java version**: Tìm thẻ `<java.version>`.
*   **Spring Boot & Cloud**: Tìm thẻ `<parent>` và `<dependencyManagement>` (BOM).
*   **Danh sách Module**: Cuộn xuống thẻ `<modules>`. Xác nhận danh sách 20 module Java.
*   **Plugins**: Tìm các plugin quan trọng trong thẻ `<pluginManagement>` hoặc `<plugins>`:
    *   `build-helper-maven-plugin`: Thêm source IT.
    *   `jacoco-maven-plugin`: Đo coverage.
    *   `maven-checkstyle-plugin`: Kiểm tra code style.
    *   `dependency-check-maven`: Quét lỗ hổng bảo mật.

---

## 2. Kiểm tra Module Frontend & Phụ trợ
*   **Frontend**: Kiểm tra thư mục `backoffice/` và `storefront/`. Mở `package.json` trong từng thư mục để xác nhận framework (Next.js).
*   **Automation UI**: Kiểm tra thư mục `automation-ui/`. Mở `automation-ui/pom.xml` và xác nhận nó **không** nằm trong danh sách `<modules>` của file `pom.xml` gốc.

---

## 3. Kiểm tra Workflow GitHub Actions
Truy cập thư mục [**.github/workflows/**](file:///home/pearspringmind/Studying/Devops/Lab%201/yas/.github/workflows/):

*   **Danh sách file**: Chạy lệnh `ls .github/workflows/` để kiểm tra các file CI của từng service.
*   **Trigger & Path Filter**: Mở một file CI (ví dụ `product-ci.yaml`), kiểm tra phần `on: push: paths:`.
*   **Xác nhận thiếu sót**: Kiểm tra xem đã có `delivery-ci.yaml` chưa (theo khảo sát là đang thiếu).

---

## 4. Kiểm tra Cấu trúc Test & Coverage
Chọn một module bất kỳ (ví dụ: `product/`):

*   **Cấu trúc thư mục**: Xác nhận sự tồn tại của:
    *   `src/test/java`: Chứa Unit Test.
    *   `src/it/java`: Chứa Integration Test (IT).
*   **IT Pattern**: Kiểm tra các file trong `src/it/java`, tên file thường kết thúc bằng `IT.java`.
*   **JaCoCo Excludes**: Kiểm tra trong `pom.xml` các lớp bị loại trừ khỏi báo cáo coverage (như `*Application.class`, `config/**`).

---

## 5. Các lệnh kiểm tra nhanh (Local Commands)
Chạy các lệnh sau tại thư mục gốc để đối chiếu hành vi của build tool:

| Mục đích | Lệnh |
|----------|------|
| Build 1 module (kèm shared lib) | `mvn clean install -pl <module-name> -am -DskipTests` |
| Chạy Unit Test & sinh report | `mvn test -pl <module-name> -am` |
| Chạy Integration Test | `mvn verify -pl <module-name> -am -DskipUnitTests` |

> [!TIP]
> Báo cáo JaCoCo sau khi chạy sẽ nằm tại: `<module-name>/target/site/jacoco/index.html`

---

## 6. Kiểm tra Shared Dependency
Mở file `pom.xml` của một service bất kỳ (ví dụ: `cart/pom.xml`).
Xác nhận có dependency `common-library` trong thẻ `<dependencies>`. Điều này chứng minh rằng bất kỳ thay đổi nào trong `common-library` cũng cần trigger lại workflow của service này.
