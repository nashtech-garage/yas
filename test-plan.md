# 🧪 Kế hoạch kiểm thử toàn diện Đồ án 2 (YAS CI/CD & Service Mesh)

Tài liệu này hướng dẫn chi tiết từng bước kiểm thử và thu thập minh chứng cho tất cả các yêu cầu của **Đồ án 2** bao gồm hệ thống CI/CD, tối ưu hóa dịch vụ (14 core services), ArgoCD và Istio Service Mesh.

---

## 📋 Danh sách các phần kiểm thử

| Phần | Nội dung kiểm thử | Trạng thái | Yêu cầu kiểm chứng |
| :--- | :--- | :--- | :--- |
| **Phần 1** | **Jenkins CI/CD & Cơ chế Fallback** | Sẵn sàng | Trình kích hoạt Git, các tham số đầu vào, file artifact và log build |
| **Phần 2** | **ArgoCD Dev & Staging** | Sẵn sàng | Đồng bộ tự động các ứng dụng trong namespace tương ứng |
| **Phần 3** | **Service Mesh mTLS** | Sẵn sàng | mTLS STRICT, chặn truy cập từ pod không thuộc mesh |
| **Phần 4** | **Service Mesh Retries & Timeouts** | Sẵn sàng | Tự động thử lại khi gặp mã lỗi 5xx |
| **Phần 5** | **Service Mesh Authorization Policy** | Sẵn sàng | Phân quyền truy cập tối thiểu (least-privilege) giữa các microservices |

---

## 🛠️ Hướng dẫn chi tiết từng bước kiểm thử

### Phần 1: Kiểm thử Jenkins CI/CD & Cơ chế Fallback

#### Case 1: Tự động CI (Branch Commit Build)
1. Tạo một nhánh test mới: `git checkout -b test/ci-flow`
2. Thực hiện thay đổi nhỏ ở service `cart` (ví dụ thêm comment vào code) và commit code.
3. Push nhánh này lên remote repository: `git push -u origin test/ci-flow`
4. **Kiểm tra kết quả trên Jenkins**:
   - Jenkins Multibranch Pipeline sẽ phát hiện commit mới trên nhánh `test/ci-flow`.
   - Pipeline chỉ kích hoạt tiến trình Build & Push Docker Image cho dịch vụ `cart`.
   - Log build phải chứa lệnh build tag theo định dạng Commit ID (8 ký tự đầu):
     `docker build -t <dockerhub-username>/cart:<git-commit-short-hash> cart/`
5. **Kiểm tra trên Docker Hub**:
   - Truy cập trang Docker Hub của bạn và kiểm tra xem image `cart` đã được đẩy lên với tag là mã commit đó hay chưa.

#### Case 2: CD Job cho Developer (`developer_build` với tham số đầu vào)
1. Mở Jenkins, truy cập job **`developer_build`**.
2. Chọn **Build with Parameters**.
3. Nhập giá trị cho các tham số:
   - `NAMESPACE`: `dev`
   - `BRANCH_cart`: Nhánh test của bạn (ví dụ: `test/ci-flow`).
   - Các tham số còn lại giữ nguyên mặc định là `main` hoặc `latest`.
4. Bấm **Build**.
5. **Kiểm tra kết quả**:
   - Pipeline sẽ tự động phân giải (resolve) nhánh `test/ci-flow` của service `cart` thành mã tag tương ứng (ví dụ: `a1b2c3d4`).
   - Các service khác không thay đổi nhánh sẽ tự động kích hoạt cơ chế **Fallback** và sử dụng tag mặc định là `latest`.
   - Log của Helm Upgrade sẽ hiển thị:
     - `helm upgrade --install cart helm/cart ... --set backend.image.tag=<commit-hash>` (Sử dụng tag tùy chỉnh)
     - `helm upgrade --install product helm/product ... --set backend.image.tag=latest` (Sử dụng tag fallback)

#### Case 3: Xóa triển khai môi trường Sandbox/Dev (`developer_cleanup`)
1. Vào Jenkins, mở job **`developer_cleanup`**.
2. Nhập tham số:
   - `NAMESPACE`: `dev`
   - Bỏ tích chọn `DRY_RUN` để thực thi thật.
3. Bấm **Build**.
4. **Kiểm tra kết quả**:
   - Log hiển thị lệnh `helm uninstall` cho toàn bộ 15 releases trong namespace `dev`.
   - Kiểm tra bằng lệnh: `kubectl get all -n dev` (không còn tài nguyên nào sót lại).

#### Case 4: Tự động deploy Dev khi nhánh `main` thay đổi
1. Merge code thay đổi vào nhánh `main` và push lên.
2. Kiểm tra Jenkins job `auto-deploy-dev` tự động chạy.
3. Kiểm tra các dịch vụ được tự động deploy đè vào namespace `dev` thông qua Helm.

#### Case 5: Deploy Staging khi tạo thẻ Release Tag
1. Trên nhánh `main`, tạo tag mới: `git tag v1.0.0`
2. Push tag lên remote: `git push origin v1.0.0`
3. Jenkins job `release-tag` sẽ được tự động kích hoạt:
   - Build toàn bộ 13 services + 1 UI/BFF và push lên Docker Hub với tag `v1.0.0`.
   - Deploy toàn bộ ứng dụng vào namespace `staging`.
4. Kiểm tra trên K8s: `kubectl get pods -n staging` (tất cả pod đều chạy image phiên bản `v1.0.0`).

---

### Phần 2: Kiểm thử ArgoCD Sync

1. Truy cập vào trang giao diện quản trị ArgoCD UI.
2. Kiểm tra xem 2 ứng dụng bootstrap `yas-dev-bootstrap` và `yas-staging-bootstrap` đã được tạo và kích hoạt hay chưa.
3. Kiểm tra danh sách các ứng dụng con trong namespace `dev` và `staging`:
   - Phải có đúng 15 ứng dụng con (bao gồm 14 services được chọn giữ lại + `yas-configuration`).
   - Các ứng dụng không thuộc danh sách giữ lại (ví dụ `payment`, `location`, `webhook`, `promotion`) **không** được xuất hiện.
   - Trạng thái các ứng dụng hiển thị màu xanh lá cây (`Synced` và `Healthy`).

---

### Phần 3: Kiểm thử Service Mesh mTLS (STRICT)

1. Áp dụng cấu hình mTLS:
   ```bash
   kubectl apply -f istio/mtls/peer-authentication.yaml
   kubectl apply -f istio/mtls/destination-rule.yaml
   ```
2. **Kiểm thử cho phép truy cập (Trong mesh)**:
   Mở Kiali hoặc gọi thử từ 1 pod trong mesh sang 1 pod khác trong mesh:
   ```bash
   # Lấy tên pod order
   ORDER_POD=$(kubectl get pod -l app.kubernetes.io/name=order -n dev -o jsonpath='{.items[0].metadata.name}')
   # Curl tới service product (cùng nằm trong mesh dev)
   kubectl exec -n dev $ORDER_POD -c order -- curl -s -o /dev/null -w "%{http_code}\n" http://product/actuator/health
   ```
   *Kết quả mong đợi:* Trả về code `200` (Thành công qua kênh mTLS).

3. **Kiểm thử chặn truy cập (Không thuộc mesh)**:
   Chạy một pod kiểm thử không có sidecar Istio (ở namespace default hoặc tắt injection) để gọi vào service product ở namespace `dev`:
   ```bash
   kubectl run curl-no-mesh --image=curlimages/curl --restart=Never -it --rm -- curl -iv http://product.dev.svc.cluster.local/actuator/health
   ```
   *Kết quả mong đợi:* Kết nối bị từ chối hoặc trả về lỗi handshake SSL (`reset` / `handshake failure`), chứng minh STRICT mTLS đang hoạt động hiệu quả.

---

### Phần 4: Kiểm thử Service Mesh Retries

1. Áp dụng cấu hình traffic VirtualService:
   ```bash
   kubectl apply -f istio/traffic/virtual-service-product.yaml
   kubectl apply -f istio/traffic/virtual-service-tax.yaml
   kubectl apply -f istio/traffic/virtual-service-inventory.yaml
   kubectl apply -f istio/traffic/virtual-service-cart.yaml
   ```
2. Để kiểm thử tính năng tự động thử lại (Retry) khi gặp lỗi 500:
   - Bạn có thể chủ động cấu hình lỗi 500 giả lập trên service `tax` bằng cách dùng tính năng fault injection của Istio hoặc tạm thời ngắt kết nối database của `tax` khiến nó trả về lỗi 500 khi gọi endpoint `/tax`.
   - Thực hiện gửi yêu cầu từ `order` tới `tax`.
   - Kiểm tra log Envoy sidecar của pod `order`:
     ```bash
     kubectl logs -l app.kubernetes.io/name=order -c istio-proxy -n dev --tail=100
     ```
     *Kết quả mong đợi:* Log của Envoy proxy hiển thị ít nhất 2 nỗ lực (attempts) gửi request tới dịch vụ `tax` (lần 1 bị lỗi 500, sau đó retry thêm 1 lần nữa trước khi trả về kết quả lỗi).

---

### Phần 5: Kiểm thử Service Mesh Authorization Policy (RBAC)

1. Áp dụng chính sách phân quyền truy cập:
   ```bash
   kubectl apply -f istio/security/authz-policies.yaml
   ```
2. **Kịch bản kiểm thử 1 (Truy cập hợp lệ)**:
   Service `cart` chỉ cho phép `storefront-bff` và `order` gọi đến.
   ```bash
   # Lấy tên pod order
   ORDER_POD=$(kubectl get pod -l app.kubernetes.io/name=order -n dev -o jsonpath='{.items[0].metadata.name}')
   # Gửi yêu cầu từ order đến cart
   kubectl exec -n dev $ORDER_POD -c order -- curl -s -o /dev/null -w "%{http_code}\n" http://cart/actuator/health
   ```
   *Kết quả mong đợi:* Trả về code `200` (Được phép truy cập).

3. **Kịch bản kiểm thử 2 (Truy cập bị từ chối)**:
   Service `search` không được phép gọi đến `cart` theo quy định.
   ```bash
   # Lấy tên pod search
   SEARCH_POD=$(kubectl get pod -l app.kubernetes.io/name=search -n dev -o jsonpath='{.items[0].metadata.name}')
   # Gửi yêu cầu từ search đến cart
   kubectl exec -n dev $SEARCH_POD -c search -- curl -s -o /dev/null -w "%{http_code}\n" http://cart/actuator/health
   ```
   *Kết quả mong đợi:* Trả về code `403` (Chặn truy cập bởi RBAC Policy).
