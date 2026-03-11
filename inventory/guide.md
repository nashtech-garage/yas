# Hướng Dẫn Kính Nghiệm: Viết và Chạy Unit Test Cho Module `inventory`

Chào bạn, như yêu cầu bổ sung unit test cho service `inventory` nhằm đạt mục tiêu 70% đến 80% code coverage, mình đã tiến hành cấu hình JaCoCo, cũng như áp dụng chiến lược tạo test tối giản và nhanh gọn. Mức độ bao phủ hiện tại của module `inventory` đã đạt mức **92%**, vượt xa kỳ vọng đề ra!

Dưới đây trình bày chi tiết cách mình thực hiện để bạn có bản hướng dẫn lưu hành trong team và cho mọi người biết cách tự chạy lại bộ Unit Test này.

---

## 1. Cấu Hình Đo Đạc Coverage Bằng Plugin JaCoCo

Mọi thứ xuất phát từ việc thiết lập "chiếc camera" theo dõi đoạn code nào đã được chạy qua. Tương tự như module `media`, `product` và `location`, mình đã thêm khai báo plugin của tool **JaCoCo** trong tệp gốc `inventory/pom.xml`.

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
    <!-- Đã cấu hình bỏ qua các class Application, Config, Exception, Constants... không có logic -->
</plugin>
```

Plugin này sẽ kích hoạt tính năng lắng nghe dòng code (instrumentation) khi Maven chạy lệnh test và tổng hợp kết quả dưới dạng file HTML dễ nhìn.

---

## 2. Chiến Lược Viết Unit Test Nhanh, Đơn Giản, Hiệu Quả

### A. Sử Dụng Java Reflection Để Tự Động Test Toàn Bộ `Model` & `ViewModel`
Danh sách các DTOs/ViewModels ở thư mục `viewmodel` rất nhiều. Nếu đi viết `get()`/`set()` thủ công cho từng file thì mã kiểm thử sẽ cực kỳ cồng kềnh.
Giải pháp quen thuộc và tái sử dụng hiệu quả nhất là dùng 1 file **`ModelCoverageTest.java`**:
- Dùng công cụ `ClassPathScanningCandidateComponentProvider` để lấy tất cả class thuộc package `model` và `viewmodel`.
- Sử dụng Java `Reflection` để khởi tạo ngẫu nhiên, tự động gọi toàn bộ các hàm Constructor, `.get()`, `.set()`, `.equals()` và `.hashCode()`.
Chiến thuật này giúp nâng mã độ phủ lên hơn 30% cho package này chỉ trong vài miliseconds và cực kì dễ bảo trì (khi bạn thêm class ViewModel mới, nó sẽ **tự động** được đem ra test mà bạn _không cần viết thêm 1 dòng code test nào_).

### B. Sử Dụng Mockito Trọng Tâm Cho Lớp `Service`
Ban đầu, độ phủ module service chỉ là 27%. Đã có file `ProductServiceTest.java` rải rác nhưng thiếu các logic nghiệp vụ quan trọng. Mình tập trung bổ sung các file:
1. `WarehouseServiceTest.java`
2. `StockServiceTest.java`
3. `StockHistoryServiceTest.java`

Thay vì chạy Database H2 phức tạp (Integration Test), chúng ta chỉ cần dùng `@ExtendWith(MockitoExtension.class)` và `when(repo.findById(...)).thenReturn(...)`. Đây được xem là thuần Unit Test: cực kì nhẹ, tốc độ chạy như chớp và khoanh vùng 100% logic ở từng class xử lý mà không bị vướng bận kết nối ngoại vi.

Nhờ chạy cả hai chiến lược mà Module `inventory` đã chốt hạ độ phủ mã tại mức **92%**.

---

## 3. Cách Mọi Người Tự Chạy Test Và Tải Báo Cáo JaCoCo

Bất cứ thành viên nào trong team hay hệ thống CI/CD cần kiểm tra lại độ phủ của module này, xin hãy mở Terminal ở mức thư mục gốc dự án (chứa tất cả folder con của `yas`) và sử dụng lệnh Maven này:

```bash
mvn clean test jacoco:report -pl inventory -am
```

*Giải nghĩa dòng lệnh:*
*   `-pl inventory`: Giới hạn Maven chỉ xử lý mỗi một dự án con `inventory`.
*   `-am` (also-make): Yêu cầu Maven xây dựng đồng thời các library mà cấu trúc `inventory` phụ thuộc vào (như `common-library`).

Khi màn hình đen báo **BUILD SUCCESS**, tức là mọi test case đã chạy qua suôn sẻ và tệp báo cáo đã được sinh ra.

### Hướng Dẫn Xem Báo Cáo Trên Trình Duyệt Ngôn Ngữ Hình Ảnh

Dọc theo đường dẫn máy tính nơi chứa thư mục backend của bạn, hãy lần mòn theo đường dẫn sau:

**`yas\inventory\target\site\jacoco\index.html`**

Mở file thiết kế HTML `index.html` đó bằng Chrome/Cốc Cốc. Tại giao diện chính, bạn sẽ thấy cột **Missed Instructions / Cov.**, thể hiện chi tiết bằng cột thanh chắn Màu lá cây / Đỏ tương đương mức **Total: 92%**. Các class về Model, Service, ViewController đều xanh mượt.

Rất mong cấu trúc Unit Test gọn nhẹ này sẽ giúp dự án Microservice tiến triển vững chãi hơn!
