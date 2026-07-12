# 🏆 KỊCH BẢN KIỂM THỬ DEVOPS CHI TIẾT (ĐẠT ĐIỂM TỐI ĐA)

Tài liệu này hướng dẫn chi tiết từng bước kiểm thử để hoàn thành các yêu cầu của **Đồ án 2: CD & Service Mesh**. Với mỗi kịch bản, tài liệu ghi rõ **vị trí chạy lệnh (hoặc cách mở giao diện UI)** và **kết quả thành công dự kiến** để bạn làm minh chứng cho báo cáo.

---

## 📅 CẤU HÌNH TRUY CẬP TRƯỚC KHI TEST (PREREQUISITES)

Để truy cập các giao diện quản trị Web (UI) trên máy cá nhân, bạn cần cấu hình trỏ tên miền về địa chỉ IP của máy ảo Azure.

* **Vị trí thực hiện**: Trên máy tính cá nhân của bạn (Lập trình viên).
* **Cách mở và sửa**:
  1. Trên Windows: Mở phần mềm **Notepad** bằng quyền Administrator (Run as Administrator) -> Mở file `C:\Windows\System32\drivers\etc\hosts`.
  2. Trên Linux/macOS: Mở Terminal chạy lệnh `sudo nano /etc/hosts`.
  3. Thêm dòng sau vào cuối file (thay thế `<IP_PUBLIC_VM>` bằng IP thực tế của máy ảo Azure):
     ```text
     <IP_PUBLIC_VM> pgadmin.yas.local.com akhq.yas.local.com grafana.yas.local.com kibana.yas.local.com
     ```
  4. Lưu file lại.

---

## 🧪 KỊCH BẢN 1: KIỂM THỬ CI PIPELINE (Yêu cầu 3 - 1.5đ)
> **Mục tiêu**: Đẩy code lên một nhánh bất kỳ, hệ thống tự động build ảnh có tag là Git Commit SHA ngắn và push lên Docker Hub.

### 1. Bước thực hiện & Vị trí chạy lệnh:
* **Bước 1**: Tạo nhánh test phụ và sửa code nhẹ.
  * **Vị trí**: Terminal máy tính cá nhân của bạn (thư mục dự án `yas`).
  * **Lệnh chạy**:
    ```bash
    git checkout main
    git pull origin main
    git checkout -b feature/verify-ci-sha-tagging
    
    # Mở file cart/src/main/resources/application.yaml và thêm một dòng comment trống ở cuối file:
    # verify ci tagging
    
    git add .
    git commit -m "test: verify commit SHA tagging configuration"
    git push origin feature/verify-ci-sha-tagging
    ```
* **Bước 2**: Lấy mã Commit SHA ngắn (7 chữ số đầu) của commit vừa tạo:
  * **Vị trí**: Terminal máy tính cá nhân.
  * **Lệnh chạy**:
    ```bash
    git rev-parse --short HEAD
    ```
    *(Ví dụ kết quả trả về: **`7a8b9c2`**)*

### 2. Cách mở công cụ kiểm tra (UI/Web):
* **Kiểm tra GitHub Actions**: 
  * Mở trình duyệt -> Truy cập địa chỉ repository của bạn trên GitHub -> Chọn tab **Actions** -> Chọn workflow **`cart service ci`** vừa được kích hoạt.
* **Kiểm tra Docker Hub**: 
  * Mở trình duyệt -> Truy cập địa chỉ `https://hub.docker.com/` -> Đăng nhập tài khoản -> Vào repository `yas-cart` (hoặc tên repo của bạn).

### 3. Kết quả thành công dự kiến (Expected Successful Output):
* **Trên GitHub Actions**: Workflow `cart service ci` chạy thành công (màu xanh lá ✅).
* **Trên Docker Hub**: Trong danh sách các **Tags**, xuất hiện một thẻ tag mới trùng khớp hoàn toàn với mã Commit SHA ngắn ở Bước 2.
  * *Ví dụ*: Xuất hiện tag **`7a8b9c2`** được push cách đây vài phút.

---

## 🧪 KỊCH BẢN 2: CD JOB `developer_build` - DEPLOY THEO THAM SỐ (Yêu cầu 4 - 1.5đ)
> **Mục tiêu**: Chạy job CD để deploy thử nghiệm duy nhất service `cart` với image tag Commit SHA vừa build ở Kịch bản 1, các dịch vụ khác vẫn giữ nguyên tag mặc định.

### 1. Bước thực hiện & Cách mở công cụ (UI/Web):
* **Vị trí**: Trình duyệt Web (Giao diện GitHub).
* **Cách mở**: 
  1. Vào kho mã nguồn trên GitHub -> Chọn tab **Actions**.
  2. Ở danh sách bên trái, chọn workflow tên là **`developer_build`**.
  3. Ở góc bên phải, nhấn nút thả xuống **Run workflow**.
  4. Nhập các tham số đầu vào như sau:
     * **Use workflow from**: Chọn nhánh `main`.
     * **Tên microservice muốn deploy/cleanup**: Chọn `cart`.
     * **Tên nhánh của service cần build/test**: Điền chính xác: `feature/verify-ci-sha-tagging`
     * **Hành động thực hiện**: Chọn `Deploy`.
  5. Nhấn nút **Run workflow** màu xanh.

### 2. Vị trí chạy lệnh kiểm tra kết quả:
* **Vị trí**: Terminal SSH của máy ảo Azure (K3s server).
* **Lệnh chạy**:
  ```bash
  # 1. Xem pod cart đã khởi chạy thành công chưa
  kubectl get pods -n yas -l app.kubernetes.io/name=cart
  
  # 2. Xem chi tiết image tag của pod cart
  kubectl get pods -n yas -l app.kubernetes.io/name=cart -o jsonpath="{.items[*].spec.containers[*].image}"
  
  # 3. Xem chi tiết image tag của một pod bất kỳ khác (ví dụ: product) để so sánh
  kubectl get pods -n yas -l app.kubernetes.io/name=product -o jsonpath="{.items[*].spec.containers[*].image}"
  ```

### 3. Kết quả thành công dự kiến (Expected Successful Output):
* Lệnh 1 trả về pod `cart` ở trạng thái `Running` (ví dụ: `cart-xxxxx-xxxx 1/1 Running`).
* Lệnh 2 (Kiểm tra image tag của `cart`): Trả về chính xác tag Commit SHA ngắn của nhánh thử nghiệm:
  ```text
  docker.io/<tài_khoản_docker_hub>/yas-cart:7a8b9c2
  ```
* Lệnh 3 (Kiểm tra image tag của `product`): Trả về tag mặc định là `latest` hoặc `main`:
  ```text
  docker.io/<tài_khoản_docker_hub>/yas-product:latest
  ```

---

## 🧪 KỊCH BẢN 3: CD JOB CLEANUP - XÓA BẢN THỬ NGHIỆM (Yêu cầu 5 - 1.0đ)
> **Mục tiêu**: Gỡ bỏ hoàn toàn bản deploy thử nghiệm của dịch vụ `cart` để giải phóng tài nguyên.

### 1. Bước thực hiện & Cách mở công cụ (UI/Web):
* **Vị trí**: Trình duyệt Web (Giao diện GitHub).
* **Cách mở**: 
  1. Vào tab **Actions** -> Chọn workflow **`developer_build`**.
  2. Nhấn nút **Run workflow** ở góc phải.
  3. Nhập các tham số đầu vào:
     * **Tên microservice muốn deploy/cleanup**: Chọn `cart`.
     * **Hành động thực hiện**: Chọn `Cleanup`.
  5. Nhấn nút **Run workflow** màu xanh.

### 2. Vị trí chạy lệnh kiểm tra kết quả:
* **Vị trí**: Terminal SSH của máy ảo Azure.
* **Lệnh chạy**:
  ```bash
  kubectl get pods -n yas -l app.kubernetes.io/name=cart
  ```

### 3. Kết quả thành công dự kiến (Expected Successful Output):
* Terminal trả về thông báo lỗi chỉ ra không tìm thấy tài nguyên nào:
  ```text
  No resources found in yas namespace.
  ```

---

## 🧪 KỊCH BẢN 4: ARGOCD DEV & STAGING (Nâng cao 1 - 2.0đ)
> **Mục tiêu**: Môi trường `dev` tự động cập nhật khi đẩy code lên `main`. Môi trường `staging` chỉ cập nhật khi gắn tag release (dạng `v*`).

### 1. Cách mở công cụ kiểm tra (UI/Web):
* **Mở ArgoCD**: Mở trình duyệt, truy cập địa chỉ `http://<IP_PUBLIC_VM>:30100` hoặc domain cấu hình `http://argocd.yas.local.com` (nếu đã cài).
* **Đăng nhập**: Sử dụng tài khoản admin của ArgoCD trên cụm K3s.

### 2. Các bước test & Vị trí chạy lệnh:

#### A. Kiểm thử môi trường Dev (Auto-sync từ nhánh `main`)
* **Vị trí chạy lệnh**: Terminal máy tính cá nhân.
* **Lệnh chạy**:
  ```bash
  git checkout main
  git pull origin main
  # Thực hiện chỉnh sửa nhỏ bất kỳ (ví dụ thêm 1 comment trống ở cấu hình)
  git add .
  git commit -m "ci: test auto-sync to dev namespace"
  git push origin main
  ```
* **Kết quả thành công dự kiến**: Trên giao diện ArgoCD, ứng dụng **`yas-cart-dev`** tự động kích hoạt tiến trình đồng bộ, hiển thị trạng thái `Synced` và các Pod trong namespace `dev` được cập nhật tự động.

#### B. Kiểm thử môi trường Staging (Chỉ chạy khi gắn tag Release)
* **Vị trí chạy lệnh**: Terminal máy tính cá nhân.
* **Lệnh chạy**:
  ```bash
  # Tạo một tag mới trên git đại diện cho phiên bản release
  git tag v1.0.0
  git push origin v1.0.0
  ```
* **Kết quả thành công dự kiến**: Trên giao diện ArgoCD, ứng dụng **`yas-cart-staging`** (đang theo dõi tag `v1.0.0`) nhận diện được sự thay đổi, tự động kéo code từ tag đó và deploy đồng bộ sang namespace `staging`.

---

## 🧪 KỊCH BẢN 5: ISTIO SERVICE MESH (Nâng cao 2 - 2.0đ)
> **Mục tiêu**: Cấu hình và chứng minh hoạt động của mTLS STRICT, Retry Policy, và Authorization Policy (Zero-Trust) trên cụm K3s.

### 1. Kiểm thử mTLS STRICT
* **Mục tiêu**: Kiểm tra xem đường truyền giữa các service có bắt buộc mã hóa mTLS không.

#### A. Vị trí chạy lệnh & Lệnh chạy (Gọi từ ngoài Namespace `yas` - Không có Sidecar):
* **Vị trí**: Terminal SSH của máy ảo Azure.
* **Lệnh chạy**:
  ```bash
  kubectl run curl-test --image=curlimages/curl -n default -i --tty --rm -- curl -I http://cart.yas.svc.cluster.local:8090/actuator/health
  ```
* **Kết quả thành công dự kiến**: Kết nối thất bại ngay lập tức vì không có chứng chỉ mTLS hợp lệ. Phản hồi trả về:
  ```text
  curl: (56) Recv failure: Connection reset by peer
  ```
  *(hoặc lỗi HTTP/1.1 `503 Service Unavailable`)*

#### B. Vị trí chạy lệnh & Lệnh chạy (Gọi từ trong Namespace `yas` - Có Sidecar):
* **Vị trí**: Terminal SSH của máy ảo Azure.
* **Lệnh chạy**:
  ```bash
  # Vì actuator được cấu hình trên management port 8090 (để tách biệt với port API 80)
  # Ta thực hiện curl trực tiếp tới port 8090 của cart
  kubectl exec -it deployment/product -n yas -c product -- curl -I http://cart:8090/actuator/health
  ```
* **Kết quả thành công dự kiến**: Kết nối thành công vì cả hai pod đều thuộc mesh và trao đổi qua mTLS. Phản hồi trả về:
  ```text
  HTTP/1.1 200 OK
  ```

---

### 2. Kiểm thử Retry Policy (Chính sách tự động thử lại khi gặp lỗi 5xx)
* **Mục tiêu**: Kiểm tra xem khi service `cart` bị lỗi 500, proxy của pod gọi (`product`) có tự động gửi lại request 3 lần trước khi trả lỗi về ứng dụng hay không.
* **Lưu ý**: VirtualService cấu hình chính sách Retry chỉ áp dụng cho port REST API là `80`.

#### A. Các bước thực hiện & Vị trí chạy lệnh:
* **Vị trí**: Terminal SSH của máy ảo Azure.
* **Lệnh chạy**:
  ```bash
  # Bước 1: Scale database PostgreSQL về 0 để làm cart mất kết nối DB (gây lỗi 500 khi truy cập dữ liệu)
  kubectl scale statefulset postgresql -n postgres --replicas=0
  
  # Đợi 10 giây để DB tắt hẳn...
  
  # Bước 2: Thực hiện cuộc gọi từ product sang cart trên API port 80
  kubectl exec -it deployment/product -n yas -c product -- curl -v http://cart:80/cart/storefront/cart/items
  
  # Bước 3: Xem log của istio-proxy trong pod product để kiểm tra số lần thử lại (gõ grep -i "cart")
  kubectl logs deployment/product -c istio-proxy -n yas --tail=150 | grep -i "cart"
  
  # Bước 4: Khôi phục lại DB postgresql về trạng thái hoạt động bình thường
  kubectl scale statefulset postgresql -n postgres --replicas=1
  ```

#### B. Kết quả thành công dự kiến:
Trong log xuất ra ở **Bước 3**, do DB bị tắt nên request trả về mã lỗi `500`. Bạn sẽ thấy **3 dòng log** liên tiếp thể hiện Envoy proxy của `product` tự động gửi lại request đến `cart` ở các mốc thời gian cách nhau 2 giây trước khi chính thức trả lỗi về cho ứng dụng:
```text
[2026-06-23T...] "GET /cart/storefront/cart/items HTTP/1.1" 500 - "-" ... outbound|80||cart.yas.svc.cluster.local
[2026-06-23T...] "GET /cart/storefront/cart/items HTTP/1.1" 500 - "-" ... outbound|80||cart.yas.svc.cluster.local (retry 1)
[2026-06-23T...] "GET /cart/storefront/cart/items HTTP/1.1" 500 - "-" ... outbound|80||cart.yas.svc.cluster.local (retry 2)
[2026-06-23T...] "GET /cart/storefront/cart/items HTTP/1.1" 500 - "-" ... outbound|80||cart.yas.svc.cluster.local (retry 3)
```

---

### 3. Kiểm thử Authorization Policy (Zero-Trust Security)
* **Mục tiêu**: Kiểm tra chính sách phân quyền: Chỉ cho phép `product` và `backoffice-bff` kết nối đến `cart`. Chặn tất cả các service khác.

#### A. Trường hợp được phép (Allow) - Vị trí chạy lệnh & Lệnh chạy:
* **Vị trí**: Terminal SSH của máy ảo Azure.
* **Lệnh chạy**:
  ```bash
  # Cách 1: Gọi endpoint actuator không cần token trên port 8090
  kubectl exec -it deployment/product -n yas -c product -- curl -I http://cart:8090/actuator/health
  
  # Cách 2: Gọi REST API trên port 80
  kubectl exec -it deployment/product -n yas -c product -- curl -I http://cart:80/cart/storefront/cart/items
  ```
* **Kết quả thành công dự kiến**:
  * Với **Cách 1 (port 8090)**: Phản hồi trả về `HTTP/1.1 200 OK`.
  * Với **Cách 2 (port 80)**: Phản hồi trả về `HTTP/1.1 403 Forbidden` (do gọi không kèm Keycloak token nên bị mã nguồn Java của `cart` từ chối `ACCESS_DENIED`). Điều quan trọng là **không** có dòng chữ `RBAC: access denied`, chứng minh gói tin đã đi qua được tường lửa Istio và chạm đến logic của ứng dụng.

#### B. Trường hợp bị chặn (Deny) - Vị trí chạy lệnh & Lệnh chạy:
* **Vị trí**: Terminal SSH của máy ảo Azure.
* **Lệnh chạy**:
  ```bash
  # Chạy pod curl-test-deny trong cùng namespace yas nhưng sử dụng service account mặc định (không được phân quyền)
  kubectl run curl-test-deny --image=curlimages/curl -n yas -i --tty --rm -- curl -I http://cart:80/cart/storefront/cart/items
  ```
* **Kết quả thành công dự kiến**: Lớp bảo mật Istio chặn đứng request của pod dùng service account mặc định ngay tại proxy (Envoy) và trả về lỗi **`403 Forbidden`** kèm thông báo RBAC:
  ```text
  HTTP/1.1 403 Forbidden
  content-length: 19
  content-type: text/plain
  date: Tue, 23 Jun 2026 ...
  server: istio-envoy
  
  RBAC: access denied
  ```

---

### 4. Cách mở và xem Flow chart / Topology bằng Kiali
* **Mục tiêu**: Trực quan hóa bản đồ kết nối (Topology) giữa các microservice bằng Kiali Dashboard.

#### A. Bước thực hiện & Cách mở công cụ (UI/Web):
* **Vị trí chạy lệnh chuyển tiếp cổng (Port-Forward)**: Terminal SSH của máy ảo Azure.
* **Lệnh chạy**:
  ```bash
  kubectl port-forward svc/kiali -n istio-system 20001:20001 --address 0.0.0.0
  ```
* **Cách mở Web**: 
  1. Mở trình duyệt Web trên máy cá nhân.
  2. Truy cập địa chỉ: `http://<IP_PUBLIC_VM>:20001`
  3. Chọn mục **Graph** ở danh sách menu bên trái.
  4. Ở bộ lọc Namespace, chọn namespace `yas`.
  5. Dưới mục **Display**, bật các tùy chọn: **`Security`** (để hiện biểu tượng ổ khóa mTLS) và **`Traffic Animation`** (để thấy hoạt ảnh đường truyền).

#### B. Kết quả thành công dự kiến:
* Giao diện Kiali vẽ ra biểu đồ kết nối dạng đồ thị giữa các service (`storefront-bff` trỏ tới `cart`, `product` trỏ tới database, v.v.).
* Trên các đường liên kết (mũi tên kết nối) xuất hiện **biểu tượng ổ khóa màu xanh lá** chứng minh các đường truyền đang được mã hóa nghiêm ngặt bằng mTLS STRICT.
