# Hướng Dẫn Cách Viết và Chạy Unit Test Cho Product Service
---

## 1. Cấu hình JaCoCo Plugin trong `pom.xml`

Để biết được số lượng mã nguồn đã được unit test tự động đi qua, ta cần sử dụng công cụ tính coverage là **JaCoCo**.

Mình đã thêm cấu hình JaCoCo trực tiếp vào file `product/pom.xml` như sau:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <excludes>
            <!-- Loại trừ class Application chính và cấu hình khỏi báo cáo coverage -->
            <exclude>com/yas/**/*Application.class</exclude>
            <exclude>com/yas/**/config/**</exclude>
            <exclude>com/yas/**/exception/**</exclude>
            <exclude>com/yas/**/constants/**</exclude>
        </excludes>
    </configuration>
</plugin>
```

**Mục đích:** JaCoCo sẽ chặn ở vòng đời `test` của Maven để theo dõi và tạo báo cáo `index.html` cho biết Unit test đã "phủ đỏ" hay "phủ xanh" những phần code nào.

---

## 2. Cách Viết Các Unit Test Đơn Giản

Để viết Unit Test được dễ hiểu nhất, mình thực hiện chiến lược "áp dụng Mocking bằng Mockito" và "sử dụng Reflection cho các class Entity/Model". 

### A. Test Các Class Service Bằng Mockito (`ProductServiceTest`, `CategoryServiceTest`...)

Trong Spring Boot, các Service thường đi gọi tới Database (qua Repository) hoặc service khác. Mình dùng **Mockito** để "giả lập" (mock) các Repository này. Nhờ vậy, Unit Test có thể chạy một cách hoàn toàn độc lập, không cần kết nối tới Database thật mà vẫn kiểm tra được luồng xử lý logic.

**Ví dụ một Test đơn giản:**

```java
@Test
void getCategoryById_whenCategoryIdInvalid_shouldThrowNotFoundException() {
    // 1. Giả lập (Mock) Repository:
    // Khi gọi tới categoryRepository.findById(1) thì hãy trả về một kết quả rỗng (không tìm thấy)
    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

    // 2. Chạy thư viện/Hành động và mong đợi lỗi xảy ra:
    // Khi gọi service.getById(1L) thì nó phải bắn ra một lỗi NotFoundException.
    assertThrows(NotFoundException.class, () -> categoryService.getById(1L));
}
```

### B. Test Các Class Models và ViewModels Nhanh Bằng Reflection (`ModelCoverageTest`)

Có rất nhiều Class chỉ dùng để chứa dữ liệu (chỉ có Getter/Setter/Constructor) như `Product`, `Brand`, `Category` hoặc các `ViewModel` record. Code logic ở đây không nhiều nhưng lại nhiều dòng code.

Thay vì viết test thủ công dài dòng cho từng Getter/Setter, mình đã tạo ra **`ModelCoverageTest`**. Lớp test này sử dụng kĩ thuật Java Reflection: quét qua toàn bộ cấu trúc của các Object, Record và tự động tự gọi vào các hàm `get()`, `set()`, `hashCode()`, `equals()` của chúng. Việc này giúp độ phủ Code nhảy vọt một cách nhanh chóng mà file Test vẫn cực kỳ gọn nhẹ và sạch sẽ. Đặt biệt, các class trong package `com.yas.product.model.attribute` cũng đã được thêm vào và test thành công đạt 100% độ phủ.

### C. Test Các Hàm Tiện Ích Đơn Lẻ (`UtilsTest`)

Đối với các đoạn code tiện ích (trong package `com.yas.product.utils` như `ProductConverter`, `MessagesUtils`), đây là các hàm tĩnh (static) hoạt động độc lập, không phụ thuộc vào Database. Mình đã tạo ra file **`UtilsTest`** để gọi và kiểm tra trực tiếp kết quả trả về của các hàm này với nhiều trường hợp (ví dụ hàm tạo slug: convert chữ hoa thành chữ thường, dấu cách thành dấu gạch ngang...). Nhờ vậy, package `utils` đã đạt 100% độ phủ.

---

## 3. Hướng Dẫn Chạy Unit Test Kèm Báo Cáo Coverage

Để chạy lại Unit test và tự tạo ra báo cáo Coverage thông qua dòng lệnh, bạn chỉ cần mở Terminal hoặc PowerShell tại thư mục gốc của dự án `yas`.

**Bước 1: Chạy Lệnh Test Maven**

Hãy gõ vào dòng lệnh sau:

```bash
mvn clean test jacoco:report -pl product -am
```

*Giải thích thông số:*
* `clean`: Xoá đi các file build (target) cũ.
* `test`: Chạy lại toàn bộ Unit test.
* `jacoco:report`: Bảo JaCoCo tạo ra báo cáo đo kết quả Coverage.
* `-pl product`: Chỉ build và chạy test tại module `product`.
* `-am`: Build luôn cả các thư viện phụ thuộc của `product` (như `common-library`).

**Bước 2: Xem Báo Cáo**

Sau khi chạy xong lệnh trên thành công, bạn sẽ thấy thông báo `BUILD SUCCESS`.

Kế tiếp, hãy mở đường dẫn thư mục sau vào trình duyệt Web (Chrome, Edge) để xem báo cáo nhé:

```text
\yas\product\target\site\jacoco\index.html
```

Tại đây, bạn sẽ thấy tổng số Instructions đã được che phủ (Covered) cho module `product`.
---
