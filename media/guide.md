# Hướng dẫn viết Test và cấu hình coverage cho service Media

## 1. Cấu hình JaCoCo trong `pom.xml`
Để JaCoCo có thể đo lường độ phủ của code (coverage) trong quá trình chạy test, plugin `jacoco-maven-plugin` đã được thiết lập với 2 goal chính là `prepare-agent` (cho phép agent thu thập data khi chạy test) và `report` (trích xuất báo cáo từ data sau khi test chạy xong).

Nội dung plugin được thêm trong `yas/media/pom.xml`:
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
</plugin>
```

## 2. Viết thêm Unit Test để đảm bảo Coverage > 70%
Ban đầu, test coverage của package `media` nằm ở mức khoảng 55%. Rất nhiều đoạn mã trong Controller và Validator chưa được kiểm tra. 

Để nâng độ phủ code lên trên 70%, các unit test sau đã được bổ sung:
- **`MediaControllerTest.java`**: Viết test cho toàn bộ API trong `MediaController` (create, delete, get, getByIds, getFile). Sử dụng **Mockito** để mock `MediaService` thay vì phải load context của Spring Boot, giúp test chạy nhanh và ổn định.
- **`FileTypeValidatorTest.java`**: Viết test cho `FileTypeValidator`, chuyên xác thực tính hợp lệ của multipart files (content type & valid buffer image). Một stub image hợp lệ được tái tạo bằng `BufferedImage` và `ImageIO` để test pass validation.

Sau khi thêm các test này, code coverage của service `media` đã tăng lên mức **> 78%**.

## 3. Cách chạy Unit test và xuất report
Đảm bảo bạn đang đứng ở thư mục gốc chứa các file dự án (nơi có parent `pom.xml` của `yas`), hoặc đứng tại thư mục `yas`. Do module `media` có phụ thuộc vào module `common-library`, nên việc build cần tham chiếu đến các project khác trong hệ thống.

**Lệnh chạy:**
```bash
# Đứng tại thư mục yas/ (thư mục chứa các module)
mvn clean test jacoco:report -pl media -am
```
- `-pl media`: Chỉ định thư mục cần chạy là `media`.
- `-am`: Cũng build các module mà `media` cần (ví dụ: `common-library`).
- `test`: Chạy các file unit test.
- `jacoco:report`: Xuất file báo cáo.

**Cách xem báo cáo:**
Sau khi test chạy xong thành công, bạn mở file kết quả theo đường dẫn dưới đây bằng trình duyệt web để xem chi tiết Coverage:
`yas/media/target/site/jacoco/index.html`
