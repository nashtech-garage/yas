# 🟨 Lê Xuân Trí – Unit Test & Coverage

> **Vai trò:** Phân tích code service, viết unit test, đảm bảo coverage > 70%  
> **Phối hợp:** Vinh.NL (pipeline, path filter), Vinh.PQ (SonarQube results)

---

## Phase 1: Setup & Foundation (Tuần 1)

### C1. Khảo sát cấu trúc monorepo YAS
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 1-2 | **Status:** ✅
- **Chi tiết:**
  - Clone repo YAS đã fork
  - Liệt kê tất cả service và đường dẫn thư mục:
    - `media/` → Media Service
    - `product/` → Product Service
    - `cart/` → Cart Service
    - `order/` → Order Service
    - `customer/` → Customer Service
    - `inventory/` → Inventory Service
    - `rating/` → Rating Service
    - `tax/` → Tax Service
    - `search/` → Search Service
    - `location/` → Location Service
  - Xác định đường dẫn chính xác để cung cấp cho Vinh.NL cấu hình path filter
- **Output:** Bảng liệt kê service + đường dẫn thư mục

### C2. Xác định test framework
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 2 | **Status:** ✅
- **Chi tiết:**
  - Kiểm tra `pom.xml` hoặc `build.gradle` từng service
  - Xác định: Maven hay Gradle? JUnit 4 hay 5? Có Mockito/Testcontainers không?
  - Kiểm tra lệnh chạy test: `mvn test` hay `gradle test`
  - Tìm thư mục test hiện tại: `src/test/java/...`
- **Output:** Bảng tổng hợp build tool + test framework từng service

### C3. Đánh giá coverage hiện tại
- **Ưu tiên:** 🟡 TB | **Deadline:** Ngày 3 | **Status:** ✅
- **Chi tiết:**
  - Chạy test + JaCoCo report local cho từng service:
    ```bash
    cd <service-dir>
    mvn test jacoco:report
    # Report tại target/site/jacoco/index.html
    ```
  - Ghi nhận coverage hiện tại của mỗi service
  - Xác định service nào cần thêm test nhiều nhất
- **Output:** Bảng coverage hiện tại, plan viết test

---

## Phase 2: CI Pipeline Core (Tuần 2)

### C4. Viết unit test cho Media Service
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 1-2 | **Status:** ⬜
- **Chi tiết:**
  - Tạo branch: `git checkout -b media/add-tests`
  - Phân tích code Media Service → xác định class cần test
  - Viết test cho:
    - Controller layer (MockMvc)
    - Service layer (Mockito)
    - Repository layer (nếu custom query)
  - Ví dụ test pattern:
    ```java
    @ExtendWith(MockitoExtension.class)
    class MediaServiceTest {
        @Mock private MediaRepository mediaRepository;
        @InjectMocks private MediaService mediaService;

        @Test
        void getMedia_shouldReturnMedia_whenExists() {
            // given
            when(mediaRepository.findById(1L)).thenReturn(Optional.of(new Media()));
            // when
            var result = mediaService.getMedia(1L);
            // then
            assertNotNull(result);
        }
    }
    ```
  - Chạy `mvn test jacoco:report` verify coverage
  - Commit + push branch
- **Output:** Branch `media/add-tests` với unit test mới

### C5. Viết unit test cho Product Service
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 2-3 | **Status:** ⬜
- **Chi tiết:**
  - Tạo branch: `git checkout -b product/add-tests`
  - Test các class: ProductService, ProductController, CategoryService...
  - Focus vào business logic: CRUD operations, validation, search
  - Target coverage: ≥ 70%
- **Output:** Branch `product/add-tests` với unit test mới

### C6. Viết unit test cho Cart Service
- **Ưu tiên:** 🟡 TB | **Deadline:** Ngày 3-4 | **Status:** ⬜
- **Chi tiết:**
  - Tạo branch: `git checkout -b cart/add-tests`
  - Test: add to cart, remove, update quantity, calculate total
  - Target coverage: ≥ 70%
- **Output:** Branch `cart/add-tests` với unit test mới

### C7. Hỗ trợ Vinh.NL cấu hình path filter
- **Ưu tiên:** 🟡 TB | **Deadline:** Ngày 2 | **Status:** ⬜
- **Chi tiết:**
  - Cung cấp danh sách đường dẫn service chính xác (từ task C1)
  - Hỗ trợ test path filter: tạo commit chỉ thay đổi 1 service → verify
- **Output:** Path filter mapping chính xác

---

## Phase 3: Advanced & Báo cáo (Tuần 3)

### C8. Viết unit test cho service còn lại
- **Ưu tiên:** 🟡 TB | **Deadline:** Ngày 1-3 | **Status:** ⬜
- **Chi tiết:**
  - Ưu tiên: Order → Customer → Inventory (theo độ phức tạp)
  - Mỗi service tạo branch riêng: `order/add-tests`, `customer/add-tests`...
  - Không cần cover tất cả, focus service có business logic nhiều
- **Output:** Thêm test cho ≥ 2 service nữa

### C9. Đảm bảo coverage > 70%
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 3-4 | **Status:** ⬜
- **Chi tiết:**
  - Chạy lại JaCoCo cho tất cả service đã viết test
  - Nếu service nào < 70% → bổ sung test
  - Các trick tăng coverage:
    - Test edge cases (null input, empty list)
    - Test exception handling
    - Test getter/setter nếu cần (dùng lombok thì bỏ qua)
  - Verify pipeline fail đúng khi coverage < 70% (phối hợp Vinh.NL task A9)
- **Output:** Tất cả service target đạt coverage > 70%

### C10. Tạo PR từ branch test
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 4 | **Status:** ⬜
- **Chi tiết:**
  - Tạo PR cho mỗi branch test → main
  - Mô tả PR: service nào, bao nhiêu test thêm, coverage đạt bao nhiêu
  - Đảm bảo **ít nhất 1 PR đang open** (yêu cầu nộp bài)
  - Request review từ 2 thành viên còn lại
  - Chờ CI chạy → verify pass
- **Output:** PRs trên GitHub, ≥ 1 PR đang open

---

## 📋 Tiến độ

| Phase | Done | Total | Progress |
|-------|------|-------|----------|
| Phase 1 | 0 | 3 | ░░░░░░░░░░ 0% |
| Phase 2 | 0 | 4 | ░░░░░░░░░░ 0% |
| Phase 3 | 0 | 3 | ░░░░░░░░░░ 0% |
| **Tổng** | **0** | **10** | ░░░░░░░░░░ **0%** |
