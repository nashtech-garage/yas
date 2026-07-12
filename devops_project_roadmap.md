# 🏆 Kết Quả Triển Khai & Lộ Trình Đạt Điểm Tối Đa (10/10) - Đồ án 2: CD & Service Mesh

Chào bạn! Hệ thống CI/CD GitOps và Istio Service Mesh cho dự án **YAS Microservices** trên máy ảo Azure đã được xây dựng hoàn thiện và chạy thử nghiệm thành công 100%. Dưới đây là bảng tổng hợp tiến độ chi tiết của các phần đã làm được cùng hướng dẫn test cuối cùng.

---

## 📊 BẢNG TỔNG HỢP TIẾN ĐỘ THỰC HIỆN

| STT | Nội dung yêu cầu | Trạng thái | Chi tiết đã triển khai |
| :--- | :--- | :---: | :--- |
| **1** | **CI Pipeline: Tagging bằng Commit SHA** (Yêu cầu 3 - 1.5đ) | **ĐÃ HOÀN THÀNH** | Tự động build và gắn tag ảnh Docker Hub bằng mã Git Commit SHA ngắn (7 ký tự) trên nhánh tính năng. |
| **2** | **CD Pipeline: Deploy theo tham số** (Yêu cầu 4 - 1.5đ) | **ĐÃ HOÀN THÀNH** | Tạo job `developer_build` thủ công, truyền tham số: Tên service, Tên nhánh và Hành động. Deploy độc lập bản thử nghiệm. |
| **3** | **CD Pipeline: Dọn dẹp bản thử nghiệm (Cleanup)** (Yêu cầu 5 - 1.0đ) | **ĐÃ HOÀN THÀNH** | Tích hợp hành động `Cleanup` vào job CD để gỡ bỏ hoàn toàn bản thử nghiệm và giải phóng tài nguyên. |
| **4** | **ArgoCD Dev & Staging GitOps** (Nâng cao 1 - 2.0đ) | **ĐÃ HOÀN THÀNH** | - Đã cài đặt ArgoCD trên cụm K3s (UI NodePort `30100`).<br>- Môi trường `dev` tự động đồng bộ (Auto-sync) khi push code lên nhánh `main`.<br>- Môi trường `staging` tự động đồng bộ theo Tag Release Git (`v*`). |
| **5** | **Istio: mTLS STRICT** (Nâng cao 2 - 0.7đ) | **ĐÃ HOÀN THÀNH** | Áp dụng chính sách `PeerAuthentication` bắt buộc mTLS STRICT cho namespace `yas` và `postgres`. |
| **6** | **Istio: Authorization Policy (Zero-Trust)** (Nâng cao 2 - 0.7đ) | **ĐÃ HOÀN THÀNH** | Thiết lập chính sách chỉ cho phép các Service Account được chỉ định (`product`, `backoffice-bff`) gọi sang `cart`. |
| **7** | **Istio: Retry Policy** (Nâng cao 2 - 0.6đ) | **ĐÃ HOÀN THÀNH** | Cấu hình `VirtualService` tự động thử lại 3 lần (cách nhau 2s) khi service đích gặp lỗi hệ thống (5xx). |

---

## 🛠️ CHI TIẾT CÁC BƯỚC ĐÃ THỰC HIỆN & CÁCH KIỂM THỬ

Chi tiết các bước thực hiện lệnh trên máy ảo để lấy minh chứng (Log/Screenshot) cho báo cáo đã được biên soạn đầy đủ tại file:
👉 **[devops_test_playbook.md (Kịch bản kiểm thử chi tiết)](file:///C:/Users/Admin/Documents/HCMUS/CHUYEN_NGANH/DEV_OPS/yas/devops_test_playbook.md)**

### 💡 Lưu ý quan trọng khi test thực tế trên VM trước khi báo cáo:
1. **Actuator Health Port**: Actuator của các microservice chạy trên cổng **`8090`** (không dùng cổng REST API chính `80`). Khi kiểm tra trạng thái sức khỏe của Pod, bạn hãy chạy:
   ```bash
   kubectl exec -it deployment/product -n yas -c product -- wget -S --spider http://cart:8090/actuator/health
   ```
2. **Spring Security 403 vs Istio 403**:
   * Khi dùng pod `product` gọi sang cổng API `80` của `cart` (`/cart/storefront/cart/items`), hệ thống sẽ trả về **`403 Forbidden`** từ ứng dụng Java (`ACCESS_DENIED` do thiếu token đăng nhập). Điều này **chứng minh kết nối mạng qua Istio thành công 100%**.
   * Khi dùng pod ngoài danh sách được phân quyền (ví dụ pod dùng service account `default`), Istio Envoy Proxy sẽ chặn ngay lập tức và trả về **`403 Forbidden`** kèm dòng chữ **`RBAC: access denied`**.

---

## 📝 NHỮNG VIỆC CÒN LẠI (CHO NHÓM BẠN TRƯỚC KHI BÁO CÁO)

Hệ thống hạ tầng, cấu hình k8s, và các pipeline CI/CD đều đã hoàn tất và sẵn sàng. Những việc còn lại thuộc về phần chuẩn bị báo cáo của bạn:
1. **Chụp hình giao diện ArgoCD**: Đăng nhập `http://<IP_PUBLIC_VM>:30100` chụp ảnh 2 ứng dụng `yas-cart-dev` (Synced) và `yas-cart-staging` làm minh chứng.
2. **Chạy Kiali Dashboard để chụp Topology**:
   * Chạy lệnh port-forward trên VM: `kubectl port-forward svc/kiali -n istio-system 20001:20001 --address 0.0.0.0`
   * Mở trình duyệt truy cập `http://<IP_PUBLIC_VM>:20001`, chọn Graph của namespace `yas`, bật hiển thị **`Security`** (biểu tượng ổ khóa mTLS màu xanh lá) để làm minh chứng.
3. **Copy Logs & Kết quả chạy lệnh test**: Chạy các kịch bản kiểm thử ở file playbook và chụp màn hình/copy output của Terminal (đặc biệt là log retry của `istio-proxy` khi tắt PostgreSQL) để đưa vào file báo cáo Word/Slide.
