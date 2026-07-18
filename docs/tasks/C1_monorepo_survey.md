# C1. Báo cáo khảo sát cấu trúc monorepo YAS

> **Người thực hiện:** Lê Xuân Trí  
> **Ngày cập nhật:** 2026-04-27  
> **Người nhận:** Nguyễn Lê Thế Vinh  
> **Mục đích:** cung cấp dữ liệu chính xác để cấu hình CI, path filter, phạm vi build/test và xác định các điểm cần bổ sung trong pipeline monorepo.

---

## 1. Bảng tóm tắt điều hành

| Hạng mục | Kết quả xác minh |
|---------|-------------------|
| Kiểu repo | Monorepo |
| Root build tool | Maven multi-module tại `pom.xml` |
| Java version | 25 |
| Spring Boot parent | 4.0.2 |
| Spring Cloud BOM | 2025.1.1 |
| Số module trong Maven reactor | 20 |
| Số frontend ngoài Maven reactor | 2 module: `backoffice`, `storefront` |
| Module Java ngoài Maven reactor | `automation-ui` có `pom.xml` riêng |
| Kiểu CI hiện có | GitHub Actions theo từng module |
| Shared module quan trọng | `common-library` |
| Điểm cần xử lý gấp cho CI | trigger khi `common-library/**` thay đổi, bổ sung workflow cho `delivery`, xác định phạm vi rerun khi đổi root/shared files |

---

## 2. Bảng tổng hợp module để Vinh.NL cấu hình CI

### 2.1 Maven reactor modules

| # | Module | Loại | Build tool | Main files | Unit test files | IT files | Workflow hiện có | Path filter chính | Mức độ ưu tiên CI | Ghi chú cho Vinh.NL |
|---|--------|------|------------|-----------:|----------------:|---------:|------------------|-------------------|------------------|---------------------|
| 1 | `common-library` | Shared library | Maven | 39 | 4 | 1 | Không có workflow riêng | `common-library/**` | Rất cao | Đây là shared dependency, đổi tại đây có thể ảnh hưởng nhiều service |
| 2 | `backoffice-bff` | BFF | Maven | 4 | 0 | 0 | `backoffice-bff-ci.yaml` | `backoffice-bff/**` | Cao | Nên rerun khi đổi `pom.xml` và action chung |
| 3 | `cart` | Backend service | Maven | 19 | 4 | 2 | `cart-ci.yaml` | `cart/**` | Rất cao | Nên rerun khi đổi `common-library/**` |
| 4 | `customer` | Backend service | Maven | 30 | 6 | 3 | `customer-ci.yaml` | `customer/**` | Cao | Nên rerun khi đổi `common-library/**` |
| 5 | `inventory` | Backend service | Maven | 42 | 6 | 9 | `inventory-ci.yaml` | `inventory/**` | Cao | IT khá nhiều, cần cân nhắc thời gian build |
| 6 | `location` | Backend service | Maven | 40 | 7 | 4 | `location-ci.yaml` | `location/**` | Cao | Nên rerun khi đổi shared files |
| 7 | `media` | Backend service | Maven | 21 | 2 | 1 | `media-ci.yaml` | `media/**` | Rất cao | Service gọn, phù hợp làm mẫu coverage/test |
| 8 | `order` | Backend service | Maven | 65 | 11 | 5 | `order-ci.yaml` | `order/**` | Rất cao | Nên rerun khi đổi `common-library/**` |
| 9 | `payment-paypal` | Backend service | Maven | 12 | 4 | 1 | `payment-paypal-ci.yaml` | `payment-paypal/**` | Trung bình | Service nhỏ, dễ tách workflow |
| 10 | `payment` | Backend service | Maven | 41 | 5 | 5 | `payment-ci.yaml` | `payment/**` | Cao | Có thể cần scan security kỹ hơn do liên quan thanh toán |
| 11 | `product` | Backend service | Maven | 125 | 16 | 6 | `product-ci.yaml` | `product/**` | Rất cao | Module lớn, nên ưu tiên trigger chính xác |
| 12 | `promotion` | Backend service | Maven | 36 | 5 | 1 | `promotion-ci.yaml` | `promotion/**` | Trung bình | Nên rerun khi đổi shared files |
| 13 | `rating` | Backend service | Maven | 23 | 5 | 1 | `rating-ci.yaml` | `rating/**` | Trung bình | Workflow riêng đã có |
| 14 | `search` | Backend service | Maven | 28 | 8 | 4 | `search-ci.yaml` | `search/**` | Cao | Có liên quan Elasticsearch/Kafka khi verify sau |
| 15 | `storefront-bff` | BFF | Maven | 11 | 0 | 0 | `storefront-bff-ci.yaml` | `storefront-bff/**` | Cao | Nên theo dõi khi thay đổi auth/gateway |
| 16 | `tax` | Backend service | Maven | 30 | 1 | 5 | `tax-ci.yaml` | `tax/**` | Cao | Ít unit test, phù hợp nâng coverage nhanh |
| 17 | `webhook` | Backend service | Maven | 43 | 4 | 1 | `webhook-ci.yaml` | `webhook/**` | Trung bình | Workflow riêng đã có |
| 18 | `sampledata` | Backend service | Maven | 11 | 0 | 0 | `sampledata-ci.yaml` | `sampledata/**` | Thấp | Ít logic, tuy nhiên vẫn có workflow riêng |
| 19 | `recommendation` | Backend service | Maven | 36 | 5 | 2 | `recommendation-ci.yaml` | `recommendation/**` | Trung bình | Workflow riêng đã có |
| 20 | `delivery` | Backend service | Maven | 3 | 0 | 0 | Chưa có workflow riêng | `delivery/**` | Cao | Nên bổ sung workflow nếu nhóm cần cover đầy đủ các service |

### 2.2 Frontend modules ngoài Maven reactor

| # | Module | Loại | Build manifest | Workflow hiện có | Path filter chính | Ghi chú cho Vinh.NL |
|---|--------|------|----------------|------------------|-------------------|---------------------|
| 21 | `backoffice` | Next.js frontend | `backoffice/package.json` | `backoffice-ci.yaml` | `backoffice/**` | Có thể cần rerun khi BFF contract thay đổi |
| 22 | `storefront` | Next.js frontend | `storefront/package.json` | `storefront-ci.yaml` | `storefront/**` | Có thể cần rerun khi `storefront-bff/**` đổi nếu nhóm muốn chặt chẽ |

### 2.3 Module phụ trợ ngoài phạm vi reactor chính

| Module | Loại | Build manifest | Nên đưa vào CI chính không | Ghi chú |
|--------|------|----------------|----------------------------|---------| 
| `automation-ui` | Java test automation | `automation-ui/pom.xml` | Không bắt buộc trong phase này | Không nằm trong `<modules>` của root `pom.xml` |

---

## 3. Bảng tổng hợp workflow GitHub Actions hiện tại

| Nhóm | Workflow files xác minh được | Nhận xét |
|------|-------------------------------|----------|
| Backend/BFF/frontend | `backoffice-bff-ci.yaml`, `backoffice-ci.yaml`, `cart-ci.yaml`, `customer-ci.yaml`, `inventory-ci.yaml`, `location-ci.yaml`, `media-ci.yaml`, `order-ci.yaml`, `payment-ci.yaml`, `payment-paypal-ci.yaml`, `product-ci.yaml`, `promotion-ci.yaml`, `rating-ci.yaml`, `recommendation-ci.yaml`, `sampledata-ci.yaml`, `search-ci.yaml`, `storefront-bff-ci.yaml`, `storefront-ci.yaml`, `tax-ci.yaml`, `webhook-ci.yaml` | Phần lớn module chính đã có workflow riêng |
| Security/general | `gitleaks-check.yaml`, `codeql.yml`, `charts-ci.yaml` | Repo đã có nền security/quality cơ bản |
| Template | `.github/workflow-template.yaml` | Đây là file mẫu, không phải workflow đang chạy |
| Thiếu | Không thấy `delivery-ci.yaml` | `delivery` là lỗ hổng hiện tại trong bao phủ workflow |

---

## 4. Bảng trigger/path filter hiện tại và điểm cần bổ sung

### 4.1 Mẫu trigger đang được sử dụng

| Thành phần trigger | Trạng thái hiện tại | Ý nghĩa |
|--------------------|---------------------|---------| 
| `module/**` | Đã có | Workflow chỉ chạy khi module tương ứng thay đổi |
| `.github/workflows/actions/action.yaml` | Đã có | Khi action dùng chung đổi, workflow module sẽ được chạy lại |
| `.github/workflows/<module>-ci.yaml` | Đã có | Khi sửa workflow của module, workflow sẽ tự trigger lại |
| `pom.xml` | Đã có | Khi root dependency/version đổi, workflow module sẽ chạy lại |

### 4.2 Các path còn thiếu nên bổ sung

| Path/nhóm path | Nên áp dụng cho ai | Lý do |
|----------------|--------------------|-------|
| `common-library/**` | `product`, `order`, `cart`, `customer`, `inventory`, `location`, `media`, `payment`, `payment-paypal`, `promotion`, `rating`, `search`, `tax`, `webhook`, `recommendation`, có thể cả BFF | Đây là shared dependency, đổi ở đây có thể làm vỡ build/test của nhiều service |
| `backoffice-bff/**` | Cần cân nhắc cho `backoffice` | Nếu nhóm muốn test theo contract frontend-BFF chặt chẽ hơn |
| `storefront-bff/**` | Cần cân nhắc cho `storefront` | Tương tự contract frontend-BFF |
| `checkstyle/**` | Tất cả Java workflows nếu workflow sử dụng checkstyle | Sửa rule style có thể làm đổi kết quả validate |
| `common-library/**` + `pom.xml` | Rất nên ưu tiên đưa vào workflow coverage/test demo | Giảm nguy cơ CI báo xanh sai |

### 4.3 Đánh giá tác động cho Vinh.NL

| Vấn đề | Mức độ | Tác động nếu không xử lý |
|--------|--------|--------------------------| 
| `common-library/**` chưa được trigger lại các service phụ thuộc | Rất cao | Có thể merge code làm hỏng module khác nhưng workflow không chạy |
| `delivery` chưa có workflow riêng | Cao | Bao phủ CI chưa đầy đủ theo danh sách service |
| Frontend và BFF chưa liên động theo contract | Trung bình | Có thể bỏ sót lỗi tích hợp, tuỳ thuộc mục tiêu đồ án |
| `automation-ui` ngoài reactor | Thấp | Không ảnh hưởng trực tiếp phase path filter backend chính |

---

## 5. Bảng kỹ thuật build, test và coverage

| Hạng mục | Giá trị xác minh | Ghi chú sử dụng cho CI |
|---------|-------------------|------------------------|
| Build tool backend/BFF | Maven | Dùng `-pl <module> -am` để build đúng module và dependencies |
| Build tool frontend | npm / Next.js | Tách workflow riêng cho `backoffice`, `storefront` |
| Có Gradle không | Không | Không cần xử lý Gradle trong pipeline |
| Unit test framework | JUnit 5 qua `spring-boot-starter-test` | Có thể dùng chung pattern unit test cho nhiều service |
| Mocking | Mockito | Phù hợp viết unit test service/controller |
| Controller test | `@WebMvcTest` + MockMvc | Dùng cho controller layer |
| Integration test | `@SpringBootTest`, Testcontainers PostgreSQL, Testcontainers Keycloak, REST Assured | Chạy trong `verify`, tồn tại ở nhiều module |
| Test data | Instancio 5.0.2 | Có thể tận dụng để tạo test data nhanh |
| Unit test runner | Maven Surefire | Tự động với `mvn test` |
| IT runner | Maven Failsafe | Chạy với `mvn verify` |
| IT source dir | `src/it/java` | Được thêm bởi `build-helper-maven-plugin` |
| IT resources dir | `src/it/resources` | Được thêm bởi `build-helper-maven-plugin` |
| Coverage tool | JaCoCo 0.8.14 | Đã có trong root/plugin của nhiều module |
| JaCoCo excludes | `*Application.class`, `config/**`, `exception/**`, `constants/**` | Cần nhớ khi đọc coverage |

---

## 6. Bảng plugin/cấu hình root `pom.xml` liên quan đến CI

| Plugin/cấu hình | Có trong root không | Vai trò |
|-----------------|---------------------|---------|
| `build-helper-maven-plugin` | Có | Thêm `src/it/java` và `src/it/resources` vào quá trình test |
| `maven-failsafe-plugin` | Có | Chạy integration test với pattern `**/**IT.java` |
| `jacoco-maven-plugin` | Có | Gắn `prepare-agent`, sinh `report` |
| `maven-checkstyle-plugin` | Có trong `pluginManagement` | Workflow Java đang sử dụng checkstyle check |
| `dependency-check-maven` | Có trong `pluginManagement` | Hỗ trợ security/dependency scan |

---

## 7. Bảng đề xuất path filter bàn giao cho Vinh.NL

| Module/nhóm | Path tối thiểu | Shared paths nên kèm theo | Khuyến nghị |
|-------------|----------------|---------------------------|-------------|
| `product` | `product/**` | `common-library/**`, `pom.xml`, `.github/workflows/actions/action.yaml`, `.github/workflows/product-ci.yaml` | Bắt buộc bổ sung `common-library/**` |
| `cart` | `cart/**` | `common-library/**`, `pom.xml`, `.github/workflows/actions/action.yaml`, `.github/workflows/cart-ci.yaml` | Bắt buộc bổ sung `common-library/**` |
| `media` | `media/**` | `common-library/**`, `pom.xml`, `.github/workflows/actions/action.yaml`, `.github/workflows/media-ci.yaml` | Nên dùng làm mẫu để test coverage workflow |
| `order` | `order/**` | `common-library/**`, `pom.xml`, action chung, workflow riêng | Ưu tiên cao |
| `customer` | `customer/**` | `common-library/**`, `pom.xml`, action chung, workflow riêng | Ưu tiên cao |
| `inventory` | `inventory/**` | `common-library/**`, `pom.xml`, action chung, workflow riêng | Cần lưu ý IT nhiều |
| `location` | `location/**` | `common-library/**`, `pom.xml`, action chung, workflow riêng | Ưu tiên cao |
| `payment` | `payment/**` | `common-library/**`, `pom.xml`, action chung, workflow riêng | Nên kết hợp security scan |
| `payment-paypal` | `payment-paypal/**` | `common-library/**`, `pom.xml`, action chung, workflow riêng | Trung bình |
| `promotion` | `promotion/**` | `common-library/**`, `pom.xml`, action chung, workflow riêng | Trung bình |
| `rating` | `rating/**` | `common-library/**`, `pom.xml`, action chung, workflow riêng | Trung bình |
| `search` | `search/**` | `common-library/**`, `pom.xml`, action chung, workflow riêng | Cao vì có tích hợp search/kafka |
| `tax` | `tax/**` | `common-library/**`, `pom.xml`, action chung, workflow riêng | Tốt cho mục tiêu coverage nhanh |
| `webhook` | `webhook/**` | `common-library/**`, `pom.xml`, action chung, workflow riêng | Trung bình |
| `recommendation` | `recommendation/**` | `common-library/**`, `pom.xml`, action chung, workflow riêng | Trung bình |
| `delivery` | `delivery/**` | `common-library/**`, `pom.xml`, action chung, workflow mới cần tạo | Cần thêm workflow nếu muốn đầy đủ |
| `backoffice-bff` | `backoffice-bff/**` | `pom.xml`, action chung, workflow riêng | Có thể cần liên động với `backoffice/**` |
| `storefront-bff` | `storefront-bff/**` | `pom.xml`, action chung, workflow riêng | Có thể cần liên động với `storefront/**` |
| `backoffice` | `backoffice/**` | workflow riêng, có thể thêm `backoffice-bff/**` nếu cần test contract | Quy theo mục tiêu demo |
| `storefront` | `storefront/**` | workflow riêng, có thể thêm `storefront-bff/**` nếu cần test contract | Quy theo mục tiêu demo |

---

## 8. Bảng lệnh làm việc để đối chiếu local và CI

| Mục đích | Lệnh khuyến nghị | Khi nào dùng |
|---------|------------------|--------------| 
| Chạy unit test 1 module | `mvn test -pl product -am` | Khi cần kiểm nhanh unit test và dependency liên quan |
| Chạy đầy đủ verify 1 module | `mvn verify -pl product -am` | Khi cần gồm cả IT + JaCoCo report để đối chiếu CI |
| Build 1 module theo hướng workflow | `mvn clean install -pl product -am` | Khi muốn mô phỏng workflow đang chạy |
| Chạy frontend test | `cd storefront && npm test` | Frontend workflow riêng |
| Chạy frontend test | `cd backoffice && npm test` | Frontend workflow riêng |

### Ghi chú sử dụng

| Nội dung | Khuyến nghị |
|---------|-------------|
| Lệnh để baseline coverage | Nên ưu tiên `mvn verify -pl <module> -am` thay vì chỉ `mvn test jacoco:report -f <module>` nếu muốn sát cách CI hơn |
| Khi nào cần `-am` | Khi module phụ thuộc `common-library` hoặc module khác trong reactor |
| Service nên chạy baseline trước | `media`, `cart`, `tax`, `product` |

---

## 9. Bảng khuyến nghị hành động cho Vinh.NL

| Ưu tiên | Hành động | Mục tiêu |
|--------|-----------|----------|
| 1 | Thêm `common-library/**` vào trigger của các workflow Java service phụ thuộc | Tránh bỏ sót lỗi do shared library thay đổi |
| 2 | Quyết định có tạo `delivery-ci.yaml` hay bỏ `delivery` khỏi phạm vi demo | Đồng bộ danh sách service và workflow |
| 3 | Chốt module nào được dùng để demo coverage gate | Giúp Trí tập trung viết test đúng cho pipeline nộp bài |
| 4 | Xác định có cần liên động frontend-BFF trong path filter hay không | Cân bằng giữa độ chặt và thời gian build |
| 5 | Giữ `pom.xml` và action chung trong trigger của mọi workflow | Bảo đảm thay đổi root/workflow chung không bị bỏ sót |

---

## 10. Kết luận báo cáo gửi Vinh.NL

| Nội dung kết luận | Trạng thái |
|-------------------|-----------|
| Repo đang dùng monorepo Maven + frontend Node tách riêng | Đã xác minh |
| Đã có workflow GitHub Actions riêng cho phần lớn module | Đã xác minh |
| `common-library` là điểm shared quan trọng nhất cần đưa vào path filter | Đã xác minh |
| `delivery` hiện chưa có workflow riêng | Đã xác minh |
| Có thể dùng ngay dữ liệu trong báo cáo này để chỉnh CI/path filter | Sẵn sàng bàn giao |
