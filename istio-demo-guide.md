# Hướng dẫn Demo Yêu cầu Service Mesh (Istio) - Đồ án DevOps

Tài liệu này hướng dẫn bạn từng bước thực hiện demo trực tiếp phần **Service Mesh (Istio)** cho giáo viên trên cụm Kubernetes (K8s) của dự án **YAS**.

---

## 1. Chuẩn bị trước demo (Pre-requisites)
Hãy đảm bảo bạn đã:
1. Mở sẵn trình duyệt ở tab ẩn danh và đăng nhập vào **ArgoCD** (`http://argocd.yas.local.com`) để chứng minh hệ thống đang chạy sạch và đồng bộ.
2. Mở sẵn file hosts để chứng minh bạn đã map đúng IP sang các domain:
   ```hosts
   100.95.207.79  kiali.yas.local.com
   100.95.207.79  backoffice.dev.yas.local.com
   ```

---

## 2. Kịch bản Demo Trực tiếp (3 Bước)

### Bước 1: Trình chiếu Sơ đồ mạng & mã hóa mTLS trên Kiali
*Giới thiệu khả năng quan sát (Observability) và bảo mật tự động của Service Mesh.*

1. **Chạy giả lập traffic** để Kiali có dữ liệu vẽ biểu đồ:
   Mở terminal PowerShell trên máy của bạn và chạy script:
   ```powershell
   .\scripts\simulate-traffic.ps1 -DurationSeconds 120 -Namespace dev
   ```
2. **Mở Kiali**: Truy cập `http://kiali.yas.local.com` (User: `admin` / Pass: `admin`).
3. **Thao tác trên Kiali**:
   * Vào mục **Graph** ở menu bên trái.
   * Chọn Namespace: `dev`.
   * Ở mục **Display** (trên cùng bên phải biểu đồ), tích chọn:
     * `Security` (Hiển thị biểu tượng ổ khóa mTLS).
     * `Traffic Animation` (Hiển thị luồng dữ liệu chạy động).
   * 👉 **Lời thoại demo**: 
     > *"Thưa thầy/cô, đây là sơ đồ kết nối thực tế (Topology) giữa các microservices của hệ thống YAS do Kiali vẽ lại. Thầy/cô có thể thấy **biểu tượng chiếc ổ khóa (padlock)** xuất hiện trên tất cả các đường truyền. Điều này chứng minh toàn bộ lưu lượng giao tiếp nội bộ giữa các microservices đều đang được mã hóa bằng **mTLS (Mutual TLS)** tự động bởi Istio ở tầng mạng mà không can thiệp vào code."*

---

### Bước 2: Demo Chính sách phân quyền (Authorization Policy)
*Chứng minh chính sách Zero-Trust: Chỉ những kết nối được khai báo rõ ràng mới được phép đi qua, còn lại sẽ bị chặn.*

Mở terminal chạy 2 lệnh sau để đối chiếu trực tiếp:

#### 🚫 Trường hợp 1: Kết nối bị CHẶN (Blocked - 403 Forbidden)
Gọi từ Pod `product` sang Pod `cart`. Theo thiết kế hệ thống, dịch vụ Product **không** có nhiệm vụ gì liên quan đến giỏ hàng nên không được phép gọi trực tiếp sang `cart`.
* **Lệnh chạy**:
  ```bash
  wsl kubectl exec -n dev $(wsl kubectl get pod -l app.kubernetes.io/name=product -n dev -o jsonpath='{.items[0].metadata.name}') -c product -- wget -S -O- http://cart/cart/actuator/health
  ```
* **Kết quả thực tế (Đã test pass)**:
  ```text
  Connecting to cart (10.43.192.116:80)
    HTTP/1.1 403 Forbidden
  wget: server returned error: HTTP/1.1 403 Forbidden
  ```
* 👉 **Lời thoại demo**: 
  > *"Như thầy/cô thấy, khi em đứng từ pod `product` gọi sang `cart`, Envoy Proxy của Istio đã ngay lập tức chặn đứng kết nối này và trả về lỗi **`403 Forbidden`** vì kết nối này không nằm trong danh sách whitelisted của `cart-authz`."*

---

####  Trường hợp 2: Kết nối được CHO PHÉP (Allowed - 200/500)
Gọi từ Pod `order` sang Pod `cart` để lấy thông tin giỏ hàng tạo hóa đơn. Đây là luồng nghiệp vụ hợp lệ và đã được cấu hình cho phép.
* **Lệnh chạy**:
  ```bash
  wsl kubectl exec -n dev $(wsl kubectl get pod -l app.kubernetes.io/name=order -n dev -o jsonpath='{.items[0].metadata.name}') -c order -- wget -S -O- http://cart/cart/actuator/health
  ```
* **Kết quả thực tế (Đã test pass)**:
  ```text
  Connecting to cart (10.43.192.116:80)
    HTTP/1.1 500 Internal Server Error
  ```
* 💡 **Giải thích điểm kỹ thuật**: 
  > *"Ở trường hợp này, kết nối trả về lỗi **`500`** của ứng dụng (do môi trường chưa đầy đủ cơ sở dữ liệu/Redis ở local nên Spring Boot Health Check trả về Down) chứ **không phải lỗi 403 của Istio**. Điều này chứng minh **yêu cầu mạng đã được Istio thông qua** thành công để truyền đến được ứng dụng đích."*

---

### Bước 3: Demo Tự động thử lại (Retry Policy)
*Chứng minh khả năng chịu lỗi (Resilience) tự động của Service Mesh khi có sự cố mạng hoặc dịch vụ chập chờn.*

1. **Hiển thị cấu hình VirtualService**:
   Chạy lệnh để hiển thị cấu hình routing của dịch vụ `cart`:
   ```bash
   wsl kubectl get virtualservice cart-vs -n dev -o yaml
   ```
2. **Giải thích cấu hình**:
   Trỏ vào đoạn cấu hình sau để giải thích cho giáo viên:
   ```yaml
   spec:
     hosts:
       - cart
     http:
       - route:
           - destination:
               host: cart
         retries:
           attempts: 3
           perTryTimeout: 2s
           retryOn: 5xx,connect-failure,refused-stream
   ```
   * 👉 **Lời thoại demo**: 
     > *"Chúng em đã định nghĩa **Retry Policy** trực tiếp trong `VirtualService` của Istio. Khi có một request từ dịch vụ khác gọi tới dịch vụ giỏ hàng mà gặp lỗi `5xx` hoặc lỗi kết nối, sidecar proxy của Istio sẽ tự động thử lại (retry) **3 lần**, mỗi lần cách nhau **2 giây** trước khi trả về lỗi thật cho client. Cơ chế này giúp ứng dụng hoạt động ổn định hơn đối với các lỗi chập chờn tạm thời (transient network failures)."*

---

**Chúc bạn có một buổi bảo vệ đồ án thành công tốt đẹp!**
