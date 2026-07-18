# C3. Báo cáo đánh giá Coverage và Kế hoạch viết Test

> **Người thực hiện:** Lê Xuân Trí  
> **Ngày thực hiện:** 2026-04-30  
> **Mục tiêu:** Xác định % Line Coverage hiện tại của các module backend ưu tiên thông qua JaCoCo, từ đó lập kế hoạch viết Unit Test hiệu quả để đảm bảo mục tiêu > 70% coverage.

---

## 1. Kết quả đo Baseline Coverage (Local)

Dữ liệu được lấy từ việc chạy lệnh `mvn clean test jacoco:report` và phân tích file `jacoco.csv` của các module mục tiêu (đã loại trừ code config theo `pom.xml`).

| Service | Tổng Line Coverage | Đánh giá so với ngưỡng 70% | Mức độ ưu tiên viết test |
|---------|--------------------|----------------------------|--------------------------|
| **cart** | **86.72%** | ✅ Vượt ngưỡng (An toàn) | Thấp (Chỉ cần giữ vững) |
| **media** | **51.28%** | ⚠️ Gần đạt | Cao (Cần bổ sung một ít để đạt 70%) |
| **tax** | **7.04%** | ❌ Rất thấp | Cao (Service nhỏ, ít logic, cực dễ nâng điểm nhanh) |
| **product**| **35.84%** | ❌ Thấp | Trung bình (Service lớn, nghiệp vụ nhiều, cần nhiều nỗ lực) |

---

## 2. Phân tích chi tiết và Kế hoạch thực hiện (Test Plan)

Dựa trên số liệu baseline, kế hoạch viết Unit Test của Trí sẽ thay đổi như sau:

### Ưu tiên 1: `media`
*   **Hiện trạng**: 51.28%.
*   **Kế hoạch**: Chỉ cần bổ sung test cho 1-2 controller hoặc service nhỏ (ví dụ: các luồng báo lỗi `NotFoundException`, validation input) là đủ vượt 70%.
*   **Hành động**: Tạo branch `media/add-tests`, chốt sổ nhanh gọn.

### Ưu tiên 2: `tax`
*   **Hiện trạng**: 7.04%.
*   **Kế hoạch**: Dù đang rất thấp, nhưng `tax` là một service cực kỳ gọn nhẹ. Việc cover toàn bộ `TaxService` và `TaxController` sẽ kéo coverage lên > 80% chỉ trong vài tiếng viết test.
*   **Hành động**: Tạo branch `tax/add-tests`. Đây là mục tiêu "ngon ăn" để giúp pipeline có đủ 2-3 service đạt coverage.

### Ưu tiên 3: `product`
*   **Hiện trạng**: 35.84%.
*   **Kế hoạch**: Việc nâng `product` lên 70% sẽ tốn rất nhiều thời gian vì codebase lớn. Sẽ lùi lại thực hiện ở cuối Phase 2 hoặc Phase 3 nếu các service khác đã ổn định định.
*   **Hành động**: Ưu tiên test các luồng `GET` (search, list) vì ít phụ thuộc database hơn các luồng `POST`/`PUT`.

### Chuyển hướng cho `cart`
*   **Hiện trạng**: 86.72%.
*   **Kế hoạch**: Service này đã làm quá tốt. Trí **không cần** tốn thời gian viết thêm test cho `cart` trong giai đoạn này, mà sẽ dùng thời gian đó để "gánh" coverage cho `tax` và `media`.

---

## 3. Tổng kết chuyển giao cho CI (Vinh.NL)
*   **Ngưỡng Quality Gate (SonarQube)**: Có thể tự tin thiết lập yêu cầu **Line Coverage > 70%** cho các PR.
*   **Pipeline Demo**: Đề xuất chọn `media` hoặc `tax` làm service để demo việc "push code thiếu test làm CI fail" và "push thêm test làm CI pass".
