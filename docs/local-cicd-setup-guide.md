# Hướng dẫn Khởi chạy và Kiểm thử CI/CD (Jenkins JCasC) tại Local

Tài liệu này hướng dẫn các thành viên trong team cách tự khởi động một máy chủ Jenkins trên máy cá nhân và kiểm thử hệ thống CI/CD (đặc biệt là Job **developer_build**) mà không cần bất cứ cài đặt phức tạp nào nhờ vào tính năng **Jenkins Configuration as Code (JCasC)**.

---

## 1. Yêu cầu chuẩn bị
- **Docker Desktop**: Đã được cài đặt và đang chạy.
- Không yêu cầu cài đặt sẵn Kubernetes (có thể chạy chế độ mô phỏng DRY_RUN).
- Đã Pull code mới nhất từ nhánh cấu hình CI/CD về máy.

---

## 2. Khởi động Jenkins Local

Chúng ta đã cấu hình Jenkins chạy hoàn toàn bằng Docker với cấu trúc **Configuration as Code**. Các plugin và Job sẽ được tạo tự động 100%.

1. Mở Terminal (hoặc PowerShell, Git Bash) tại thư mục gốc của dự án.
2. Di chuyển vào thư mục `docker-jenkins`:
   ```bash
   cd docker-jenkins
   ```
3. Xoá bộ đệm cũ (nếu có) và khởi động lại Jenkins kèm theo build ảnh mới:
   ```bash
   docker-compose down
   docker-compose up -d --build
   ```
   *(Lưu ý: Lần chạy đầu tiên sẽ mất khoảng 1-3 phút để Docker tải các layer hệ điều hành và cài đặt các Plugin cho Jenkins).*

---

## 3. Truy cập và Cấu hình tự động

1. Mở trình duyệt web và truy cập vào địa chỉ: **http://localhost:8899**
2. Bạn sẽ thấy giao diện **Dashboard** của Jenkins.
3. 🎉 **Điều kỳ diệu của JCasC:** Bạn không cần phải tốn công click chuột tạo Job hay cấu hình tham số bằng tay! Tại Dashboard, bạn sẽ thấy hệ thống đã tự động "đẻ" sẵn 2 Job:
   - `developer_build`
   - `developer_cleanup`

---

## 4. Chạy thử nghiệm Job `developer_build`

Tính năng **developer_build** được thiết kế để Lập trình viên có thể chọn riêng lẻ phiên bản (Tag / Commit ID) của service mà mình đang phát triển để Deploy, trong khi các service khác giữ nguyên.

1. Tại Dashboard, nhấn vào Job **`developer_build`**.
2. Nhấn vào **"Build with Parameters"** ở menu bên trái. Bạn sẽ thấy danh sách hơn 20 tham số được tự động khởi tạo.
3. **Cấu hình tham số để test:**
   - Cờ `DRY_RUN`: **Bắt buộc tích chọn (Check)** nếu máy tính bạn chưa cài Kubernetes/Helm cục bộ. Chế độ này sẽ chỉ giả lập in ra lệnh chứ không gọi vào K8s, giúp Pipeline thành công mà không bị lỗi mạng.
   - Thử nghiệm cơ chế Fallback:
     - Tại ô `TAG_cart`: Xoá chữ `latest` và điền một mã tuỳ ý (Ví dụ: `abc1234`).
     - Tại ô `TAG_customer`: Điền `xyz9876`.
     - Các ô `TAG_...` còn lại: Bỏ qua, giữ nguyên `latest`.
4. Kéo xuống dưới cùng và nhấn nút **Build**.

---

## 5. Đọc Log và Kiểm chứng thành quả

1. Nhìn sang mục **Build History** bên góc trái dưới, nhấn vào con số `#1` (hoặc số build mới nhất).
2. Nhấn vào **Console Output**.
3. Cuộn xuống xem Log. Hệ thống Pipeline sẽ xử lý cực kỳ thông minh và phân tách rõ ràng:
   ```text
   ----------------------------------------------------------
   🚀 [CUSTOM] cart: Sử dụng phiên bản thử nghiệm (commit) -> 'abc1234'
   ⚙️ [DRY_RUN] Sẽ thực thi lệnh: helm upgrade --install cart helm/cart ...
   ----------------------------------------------------------
   🚀 [CUSTOM] customer: Sử dụng phiên bản thử nghiệm (commit) -> 'xyz9876'
   ⚙️ [DRY_RUN] Sẽ thực thi lệnh: helm upgrade --install customer helm/customer ...
   ----------------------------------------------------------
   ✅ [FALLBACK] inventory: Sử dụng phiên bản mặc định -> 'latest'
   ⚙️ [DRY_RUN] Sẽ thực thi lệnh: helm upgrade --install inventory helm/inventory ...
   ```
4. **Kết luận:** Nếu bạn thấy thông báo `✅ DEVELOPER BUILD COMPLETED SUCCESSFULLY` và Pipeline có màu xanh lá cây, nghĩa là hệ thống CI/CD trên máy bạn đã chạy hoàn hảo!
