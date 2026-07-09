# 🚀 YAS CI/CD Pipeline - Handover Document

Tài liệu này được tạo ra nhằm mục đích bàn giao và giải thích cấu trúc, luồng hoạt động của hệ thống CI/CD đã được cấu hình cho dự án YAS (Yet Another Storefront). Tài liệu giúp các thành viên trong team hiểu rõ những gì đã được xây dựng và cách vận hành chúng một cách trơn tru.

---

## 1. Tổng quan Kiến trúc CI/CD
Hệ thống CI/CD hiện tại được chia làm hai luồng (Pipeline) chính, đáp ứng đầy đủ yêu cầu của **Lab 2 / Đồ án DevOps**:
1. **Continuous Integration (CI):** Tự động đóng gói code của lập trình viên thành Docker Image và đẩy lên Docker Hub.
2. **Continuous Deployment (CD) - Developer Build:** Cung cấp giao diện tham số trên Jenkins để lập trình viên tự chọn phiên bản deploy, kết hợp với cơ chế "Fallback" thông minh.

---

## 2. CI Pipeline (`Jenkinsfile`)
File này chịu trách nhiệm cho toàn bộ quá trình Tích hợp liên tục (CI) khi một lập trình viên đẩy code lên Git.

### Luồng hoạt động chính:
- **Khởi tạo & Lấy biến môi trường:** Pipeline sẽ tự động trích xuất mã Commit ID (Short HEAD - 8 ký tự đầu của Git Commit) để làm định danh duy nhất cho bản build.
- **Biên dịch & Test:** Tự động chạy `./gradlew clean build` để kiểm tra lỗi và biên dịch source code Java thành file thực thi `.jar`.
- **Đóng gói Docker:** Khởi động lệnh `docker build` để đóng gói file `.jar` vào Docker image, với tag name chính là Commit ID vừa lấy được (VD: `chubedan4605/customer:1226c5c9`).
- **Push lên Docker Hub:** Tự động đăng nhập và đẩy (push) image vừa build lên Docker Hub, giúp Image sẵn sàng cho quá trình Deploy.

### Thành quả đạt được:
- Mã nguồn được đóng gói tiêu chuẩn.
- Các phiên bản image không bị ghi đè lẫn nhau nhờ việc dùng Git Commit ID làm Tag.

---

## 3. CD Pipeline - Developer Build (`Jenkinsfile-developer-build`)
Đây là công cụ cực kỳ mạnh mẽ dành riêng cho Lập trình viên để tự test code của mình trên môi trường tích hợp.

### Cơ chế Fallback Thông Minh (Điểm nhấn của hệ thống):
Hệ thống YAS có tới hơn 16 microservices. Nếu Lập trình viên chỉ sửa code ở 1 service (VD: `customer`), họ không cần thiết phải quan tâm tới 15 services còn lại.
- **`[CUSTOM]`:** Khi lập trình viên truyền tag `1226c5c9` vào tham số của `customer`, hệ thống sẽ deploy bản cập nhật riêng cho `customer`.
- **`[FALLBACK]`:** Tất cả 15 services còn lại do không được truyền tag, hệ thống sẽ tự động bắt lấy (fallback) và gán tag mặc định là `latest`, đảm bảo hệ thống vẫn đầy đủ các thành phần bổ trợ xung quanh.

### Luồng hoạt động chính:
- **Xử lý môi trường:** Pipeline tự động kiểm tra và tải công cụ `helm` về máy chủ Jenkins nếu máy chủ chưa có sẵn (Auto-download helm binary).
- **Cập nhật Dependency:** Chạy `helm dependency build` cho tất cả các dịch vụ trước khi deploy.
- **Deploy tuần tự:**
  1. Triển khai các dịch vụ Frontend & BFF (backoffice, storefront).
  2. Triển khai Swagger UI (API Docs).
  3. Triển khai toàn bộ Backend Services.
- **Chế độ Dry Run:** Có hỗ trợ biến cờ `DRY_RUN`. Nếu kích hoạt, hệ thống chỉ in ra các câu lệnh `helm upgrade` để mô phỏng (Dùng để chụp ảnh log làm báo cáo đồ án rất tốt khi chưa có cluster K8s thực tế).

---

## 4. Hướng dẫn sử dụng cho Team Member

### Bước 1: Build Image (CI)
1. Bạn sửa code ở service nào đó (VD: `customer`).
2. Gõ lệnh: `git commit -m "Fix bug"` và `git push`.
3. Chờ Jenkins chạy Job CI.
4. Mở Log Jenkins ra, copy lại cái Tag (Commit ID) vừa được tạo (VD: `1226c5c9`).

### Bước 2: Deploy thử nghiệm (CD)
1. Mở Jenkins, vào Job **`developer_build`**.
2. Chọn **"Build with Parameters"**.
3. Điền Tag vừa copy vào ô tương ứng với service của bạn (VD: Ô `TAG_customer` điền `1226c5c9`).
4. Các ô khác cứ để trống hoặc để mặc định là `latest`.
5. Tích chọn `DRY_RUN` nếu chỉ muốn xem thử log giả lập, hoặc bỏ tích để deploy thẳng lên Kubernetes (Nếu K8s đã được cấu hình).
6. Bấm **Build** và tận hưởng!

---

## 5. Danh sách các File cấu hình
- `Jenkinsfile`: Nơi chứa code CI Pipeline.
- `Jenkinsfile-developer-build`: Nơi chứa code CD Pipeline (cho Dev).
- `k8s/deploy/deploy-yas-applications.sh`: Script deploy thủ công (Đã được chuyển hóa vào Jenkinsfile).
- Thư mục `k8s/charts/`: Chứa các Helm charts gốc để deploy hệ thống lên K8s.

***Tài liệu này được viết để đính kèm vào source code giúp việc maintain về sau trở nên cực kỳ dễ dàng.***
