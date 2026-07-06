# Tại sao phải khảo sát Monorepo và ý nghĩa của Báo cáo C1

Tài liệu này giải thích mối quan hệ giữa bản khảo sát **C1_monorepo_survey.md**, hướng dẫn kiểm tra **manual_verification_guide.md** và tầm quan trọng của chúng trong đồ án DevOps.

---

## 1. Mối quan hệ giữa hai tài liệu
*   **C1_monorepo_survey.md (Bản khảo sát)**: Là "Bản đồ" của hệ thống. Nó cho biết hệ thống đang có cái gì, nằm ở đâu, chạy bằng công cụ gì.
*   **manual_verification_guide.md (Hướng dẫn kiểm tra)**: Là "Bộ lọc kiểm chứng". Nó cung cấp cách thức để bất kỳ ai trong nhóm (hoặc giảng viên) có thể kiểm tra xem "Bản đồ" trên có vẽ đúng thực tế hay không.

**Nguyên tắc**: Automation (Tự động hóa) chỉ tin cậy khi dữ liệu đầu vào (khảo sát) chính xác.

---

## 2. Tại sao phải làm báo cáo khảo sát này?

Trong một dự án Monorepo lớn như YAS (hơn 20 module), việc không có khảo sát kỹ lưỡng sẽ dẫn đến các vấn đề nghiêm trọng:

### A. Tối ưu hóa thời gian Build (Path Filtering)
Nếu bạn thay đổi code ở module `cart`, bạn không muốn hệ thống phải build và test lại cả 20 module khác (mất 20-30 phút). 
*   **Tác dụng của báo cáo**: Cung cấp danh sách "Path filter" chính xác để CI chỉ chạy đúng module bị thay đổi.

### B. Quản lý sự phụ thuộc (Shared Libraries)
Module `common-library` được dùng bởi hầu hết các service. Nếu sửa `common-library` mà CI không tự động chạy lại test cho các service phụ thuộc, lỗi có thể bị lọt vào hệ thống mà không ai biết.
*   **Tác dụng của báo cáo**: Xác định rõ các "điểm chạm" (dependencies) để cấu hình trigger thông minh.

### C. Đồng bộ hóa công cụ (Build & Test Tooling)
Bạn không thể viết script tự động nếu không biết hệ thống dùng Maven hay Gradle, Java 17 hay Java 25, JUnit 4 hay 5.
*   **Tác dụng của báo cáo**: Chốt hạ bộ khung kỹ thuật để các thành viên khác (Vinh.NL, Vinh.PQ) viết Jenkinsfile hoặc GitHub Actions workflow mà không phải đoán mò.

---

## 3. Tác dụng của báo cáo C1 trong Đồ án

Báo cáo này đóng vai trò là **"Hợp đồng kỹ thuật"** giữa các thành viên:

1.  **Cho người làm Pipeline (Vinh.NL)**: Biết chính xác các lệnh Maven cần chạy (`-pl`, `-am`) và các đường dẫn cần cấu hình trigger.
2.  **Cho người làm Security (Vinh.PQ)**: Biết module nào quan trọng (như `payment`) để tập trung quét lỗ hổng sâu hơn, và biết chỗ nào chứa report JaCoCo để đẩy lên SonarQube.
3.  **Cho việc nộp bài & Demo**: Khi giảng viên hỏi: *"Tại sao em biết module này cần build lại, module kia thì không?"* hoặc *"Cấu trúc test của em dựa trên cơ sở nào?"* -> Báo cáo C1 chính là bằng chứng kỹ thuật (evidence) chuyên nghiệp nhất.

---

## 4. Mối liên hệ trực tiếp với Kế hoạch tổng thể (plan.md)

Báo cáo C1 không đứng độc lập mà là "mắt xích" quyết định tiến độ của dự án theo [plan.md](file:///home/pearspringmind/Studying/Devops/Lab%201/yas/docs/plan.md):

### A. Thực thi Phase 1 (Setup & Foundation)
Trong `plan.md`, **Task 1.3** giao cho Trí việc khảo sát monorepo. 
*   **Ý nghĩa**: Hoàn thành báo cáo C1 đồng nghĩa với việc hoàn thành 1/5 khối lượng công việc của Tuần 1. Nếu không có báo cáo này, nhóm không thể tiến tới **Milestone 1** (nền tảng sẵn sàng).

### B. Đầu vào cho Phase 2 (CI Pipeline Core)
**Task 2.2** yêu cầu Vinh.NL + Trí cấu hình **path filter**.
*   **Liên quan**: Vinh.NL không thể tự biết đường dẫn nào là của service nào, hay service nào phụ thuộc vào `common-library`. Dữ liệu từ báo cáo C1 là "đề bài" để Vinh.NL lập trình cho Pipeline ở Tuần 2. Nếu báo cáo C1 sai hoặc chậm, toàn bộ Phase 2 sẽ bị đình trệ.

### C. Giải quyết rủi ro (Risk Mitigation)
Phần **⚠️ Rủi ro & Giải pháp** trong `plan.md` có nêu: *"Monorepo path filter khó"*.
*   **Giải pháp**: Báo cáo C1 chính là hành động cụ thể để xử lý rủi ro này ngay từ đầu. Thay vì để đến Tuần 2 mới loay hoay tìm đường dẫn, Trí đã dọn đường sẵn bằng báo cáo C1.

### D. Chuẩn bị cho Phase 3 (Coverage Gate)
**Task 3.1 & 3.2** yêu cầu đảm bảo coverage > 70%.
*   **Liên quan**: Báo cáo C1 đã xác định JaCoCo là công cụ đo và các class cần loại trừ (`excludes`). Điều này đảm bảo rằng khi bước vào Tuần 3, nhóm có số liệu coverage "sạch" và chính xác để báo cáo, tránh việc số liệu bị ảo do tính cả code config.

---

## 5. Kết luận
Việc viết ra file `.md` này không chỉ là thủ tục, mà là để **biến tri thức cá nhân thành tài sản chung của nhóm**. Nó đảm bảo rằng hệ thống CI/CD được xây dựng trên một nền tảng hiểu biết thực tế về codebase, thay vì làm theo cảm tính.
