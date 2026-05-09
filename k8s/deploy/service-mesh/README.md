# Service Mesh Configuration - Istio + Kiali cho YAS Microservices

## Mục lục
- [1. Tổng quan](#1-tổng-quan)
- [2. Kiến trúc Service Mesh](#2-kiến-trúc-service-mesh)
- [3. Prerequisites](#3-prerequisites)
- [4. Cài đặt Istio & Kiali](#4-cài-đặt-istio--kiali)
- [5. Cấu hình mTLS](#5-cấu-hình-mtls)
- [6. Cấu hình Authorization Policy](#6-cấu-hình-authorization-policy)
- [7. Cấu hình Retry Policy](#7-cấu-hình-retry-policy)
- [8. Kịch bản Test](#8-kịch-bản-test)
- [9. Sử dụng Kiali Dashboard](#9-sử-dụng-kiali-dashboard)
- [10. Troubleshooting](#10-troubleshooting)

---

## 1. Tổng quan

### Mục tiêu
- **mTLS (Mutual TLS)**: Mã hóa toàn bộ traffic giữa các services trong namespace `yas`
- **Authorization Policy**: Kiểm soát service nào được phép giao tiếp với service nào
- **Retry Policy**: Tự động retry khi service trả lỗi 5xx
- **Observability**: Sử dụng Kiali để visualize service mesh topology

### Các file cấu hình

| File | Mô tả |
|------|--------|
| `install-istio.sh` | Script tự động cài đặt Istio + Kiali |
| `peer-authentication.yaml` | Bật mTLS STRICT cho namespace yas |
| `destination-rules.yaml` | Enforce mTLS ở phía client (caller) |
| `authorization-policies.yaml` | Chính sách phân quyền service-to-service |
| `virtual-services.yaml` | Retry policies (3 lần retry cho 5xx) |
| `test-denied-pod.yaml` | Pod test để kiểm tra policy deny/allow |

---

## 2. Kiến trúc Service Mesh

### Flow tổng quan

```
                                    ┌─────────────────────────────────────────────┐
                                    │              ISTIO MESH (namespace: yas)     │
                                    │                                             │
  Client ──► Ingress ──► Nginx ────►│  ┌──────────┐     ┌──────────┐              │
                          (API GW)  │  │ product  │◄───►│  order   │              │
                                    │  └──────────┘     └──────────┘              │
                                    │       ▲                 ▲                   │
                                    │       │                 │                   │
                                    │  ┌──────────┐     ┌──────────┐              │
                                    │  │   cart   │     │ payment  │              │
                                    │  └──────────┘     └──────────┘              │
                                    │       ▲                                     │
                                    │       │                                     │
                                    │  ┌──────────┐     ┌──────────┐              │
                                    │  │storefront│     │backoffice│              │
                                    │  │   -bff   │     │   -bff   │              │
                                    │  └──────────┘     └──────────┘              │
                                    │                                             │
                                    │  Tất cả traffic đều qua Envoy sidecar       │
                                    │  với mTLS encryption (🔒)                   │
                                    └─────────────────────────────────────────────┘
```

### Services trong mesh

| Service | Port | Mô tả |
|---------|------|--------|
| product | 80 | Quản lý sản phẩm |
| cart | 80 | Giỏ hàng |
| order | 80 | Đơn hàng |
| payment | 80 | Thanh toán |
| payment-paypal | 80 | Thanh toán PayPal |
| customer | 80 | Khách hàng |
| inventory | 80 | Kho hàng |
| media | 80 | Media/Hình ảnh |
| location | 80 | Địa chỉ |
| tax | 80 | Thuế |
| promotion | 80 | Khuyến mãi |
| rating | 80 | Đánh giá |
| search | 80 | Tìm kiếm |
| recommendation | 80 | Gợi ý sản phẩm |
| webhook | 80 | Webhook events |
| sampledata | 80 | Dữ liệu mẫu |
| storefront-bff | 80 | BFF cho storefront |
| backoffice-bff | 80 | BFF cho backoffice |
| nginx | 80 | API Gateway |

---

## 3. Prerequisites

### Yêu cầu hệ thống
- Kubernetes cluster (minikube hoặc tương đương) đã chạy
- `kubectl` đã kết nối tới cluster
- `helm` v3+ đã cài đặt
- Cluster có ít nhất **4GB RAM khả dụng** cho Istio components
- Các services YAS đã deploy trong namespace `yas`

### Kiểm tra trạng thái cluster
```bash
# Kiểm tra cluster
kubectl cluster-info

# Kiểm tra namespace yas
kubectl get pods -n yas

# Kiểm tra tài nguyên khả dụng
kubectl top nodes
```

---

## 4. Cài đặt Istio & Kiali

### Cách 1: Sử dụng script tự động (Khuyến nghị)

```bash
cd k8s/deploy/service-mesh
chmod +x install-istio.sh
./install-istio.sh
```

Script sẽ tự động thực hiện 7 bước:
1. Download istioctl
2. Pre-flight check
3. Cài Istio (demo profile)
4. Cài Kiali + addons (Prometheus, Grafana, Jaeger)
5. Enable sidecar injection cho namespace `yas`
6. Restart pods để inject Envoy sidecar
7. Apply tất cả configurations (mTLS, policies, retry)

### Cách 2: Cài đặt thủ công từng bước

#### Bước 1: Cài Istio
```bash
# Download istioctl
curl -L https://istio.io/downloadIstio | ISTIO_VERSION=1.20.2 sh -
export PATH="$PWD/istio-1.20.2/bin:$PATH"

# Cài Istio
istioctl install --set profile=demo -y

# Verify
kubectl get pods -n istio-system
```

#### Bước 2: Cài Kiali
```bash
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/prometheus.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/kiali.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/grafana.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/jaeger.yaml
```

#### Bước 3: Enable sidecar injection
```bash
kubectl label namespace yas istio-injection=enabled --overwrite
```

#### Bước 4: Restart pods
```bash
kubectl rollout restart deployment --all -n yas
```

#### Bước 5: Verify sidecar injection
```bash
# Mỗi pod phải có 2 containers (app + istio-proxy)
kubectl get pods -n yas -o jsonpath='{range .items[*]}{.metadata.name}{" "}{range .spec.containers[*]}{.name}{","}{end}{"\n"}{end}'
```

---

## 5. Cấu hình mTLS

### Giải thích

mTLS (Mutual TLS) đảm bảo:
- **Encryption**: Traffic giữa services được mã hóa
- **Authentication**: Cả client và server đều xác thực lẫn nhau bằng certificate
- **Integrity**: Dữ liệu không bị thay đổi trong quá trình truyền

### Các thành phần cấu hình

#### PeerAuthentication (Server-side)
```yaml
# peer-authentication.yaml
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: yas-strict-mtls
  namespace: yas
spec:
  mtls:
    mode: STRICT  # Chỉ chấp nhận mTLS, reject plaintext
```

**Modes:**
| Mode | Mô tả |
|------|--------|
| `STRICT` | Chỉ chấp nhận mTLS traffic (khuyến nghị cho production) |
| `PERMISSIVE` | Chấp nhận cả mTLS và plaintext (dùng khi migration) |
| `DISABLE` | Tắt mTLS |

#### DestinationRule (Client-side)
```yaml
# destination-rules.yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: yas-default-mtls
  namespace: yas
spec:
  host: "*.yas.svc.cluster.local"
  trafficPolicy:
    tls:
      mode: ISTIO_MUTUAL  # Istio tự quản lý certificates
```

### Apply cấu hình
```bash
kubectl apply -f peer-authentication.yaml
kubectl apply -f destination-rules.yaml
```

### Verify mTLS
```bash
# Kiểm tra mTLS cho một pod cụ thể
istioctl x describe pod <pod-name> -n yas

# Kiểm tra certificate
istioctl proxy-config secret <pod-name> -n yas

# Xem TLS mode đang active
kubectl get peerauthentication -n yas
kubectl get destinationrule -n yas
```

---

## 6. Cấu hình Authorization Policy

### Chiến lược: Deny-by-Default + Allow-List

```
┌─────────────────────────────────────────────────┐
│  Authorization Flow                              │
│                                                  │
│  Request ──► deny-all-default ──► DENIED         │
│                                                  │
│  Request ──► allow-product-access ──► ALLOWED    │
│  (from storefront-bff)                           │
│                                                  │
│  Request ──► no matching ALLOW ──► DENIED        │
│  (from test-client)                              │
└─────────────────────────────────────────────────┘
```

### Ma trận quyền truy cập (Access Matrix)

| Target ↓ / Source → | storefront-bff | backoffice-bff | nginx | order | cart | payment | product | search | recommendation |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **product** | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | — | ✅ | ✅ |
| **cart** | ✅ | ✅ | ✅ | ✅ | — | ❌ | ❌ | ❌ | ❌ |
| **order** | ✅ | ✅ | ✅ | — | ❌ | ✅ | ❌ | ❌ | ❌ |
| **payment** | ❌ | ❌ | ✅ | ✅ | ❌ | — | ❌ | ❌ | ❌ |
| **customer** | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| **inventory** | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ |
| **media** | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ |

✅ = Allowed | ❌ = Denied | — = Self (N/A)

### Apply cấu hình
```bash
kubectl apply -f authorization-policies.yaml
```

### Verify
```bash
# Liệt kê tất cả policies
kubectl get authorizationpolicy -n yas

# Xem chi tiết một policy
kubectl describe authorizationpolicy allow-product-access -n yas
```

---

## 7. Cấu hình Retry Policy

### Giải thích

Retry policy cấu hình qua VirtualService, cho phép Envoy tự động retry request khi gặp lỗi.

```yaml
retries:
  attempts: 3                    # Retry tối đa 3 lần
  perTryTimeout: 5s              # Timeout mỗi lần retry: 5 giây
  retryOn: "5xx,connect-failure,refused-stream,reset"
```

### Các điều kiện retry

| Condition | Mô tả |
|-----------|--------|
| `5xx` | Server trả lỗi 500, 502, 503, 504 |
| `connect-failure` | Không kết nối được tới upstream |
| `refused-stream` | Upstream từ chối stream |
| `reset` | Connection bị reset |

### Apply cấu hình
```bash
kubectl apply -f virtual-services.yaml
```

### Verify
```bash
# Xem VirtualService đã tạo
kubectl get virtualservice -n yas

# Xem chi tiết
kubectl describe virtualservice product-retry -n yas

# Kiểm tra retry stats qua Envoy proxy
kubectl exec -n yas <pod-name> -c istio-proxy -- \
  pilot-agent request GET stats | grep retry
```

---

## 8. Kịch bản Test

### Test Plan

#### Test 1: Verify mTLS hoạt động

```bash
# Kiểm tra mTLS status trên pod product
PRODUCT_POD=$(kubectl get pod -n yas -l app.kubernetes.io/name=product -o jsonpath='{.items[0].metadata.name}')
istioctl x describe pod $PRODUCT_POD -n yas

# Expected output:
# Pod is STRICT, clients configured automatically
# mTLS mode: STRICT

# Kiểm tra certificate chain
istioctl proxy-config secret $PRODUCT_POD -n yas
```

#### Test 2: Authorization Policy - ALLOW (kết nối thành công)

```bash
# Deploy test pods
kubectl apply -f test-denied-pod.yaml

# Chờ pods ready
kubectl wait --for=condition=ready pod/test-allowed-client -n yas --timeout=120s

# Từ test-allowed-client (SA: storefront-bff) → product: ALLOWED
kubectl exec -n yas test-allowed-client -- curl -s -o /dev/null -w "%{http_code}" http://product.yas:80/product/

# Expected: 200 (hoặc response code từ product service)

# Từ test-allowed-client → cart: ALLOWED
kubectl exec -n yas test-allowed-client -- curl -s -o /dev/null -w "%{http_code}" http://cart.yas:80/cart/

# Expected: 200
```

#### Test 3: Authorization Policy - DENY (kết nối bị chặn)

```bash
# Chờ pods ready
kubectl wait --for=condition=ready pod/test-client -n yas --timeout=120s

# Từ test-client (SA: test-client, KHÔNG trong allow list) → product: DENIED
kubectl exec -n yas test-client -- curl -v http://product.yas:80/product/

# Expected output:
# < HTTP/1.1 403 Forbidden
# RBAC: access denied

# Từ test-client → payment: DENIED
kubectl exec -n yas test-client -- curl -v http://payment.yas:80/payment/

# Expected output:
# < HTTP/1.1 403 Forbidden
# RBAC: access denied

# Từ test-client → order: DENIED
kubectl exec -n yas test-client -- curl -v http://order.yas:80/order/

# Expected output:
# < HTTP/1.1 403 Forbidden
# RBAC: access denied
```

#### Test 4: Cross-service authorization (service A không có quyền gọi service B)

```bash
# Tìm pod cart
CART_POD=$(kubectl get pod -n yas -l app.kubernetes.io/name=cart -o jsonpath='{.items[0].metadata.name}')

# Cart → Payment: DENIED (cart không trong allow list của payment)
kubectl exec -n yas $CART_POD -c cart -- curl -v http://payment.yas:80/payment/

# Expected: HTTP 403 RBAC: access denied

# Tìm pod search
SEARCH_POD=$(kubectl get pod -n yas -l app.kubernetes.io/name=search -o jsonpath='{.items[0].metadata.name}')

# Search → Payment: DENIED
kubectl exec -n yas $SEARCH_POD -c search -- curl -v http://payment.yas:80/payment/

# Expected: HTTP 403 RBAC: access denied
```

#### Test 5: Retry Policy

```bash
# Kiểm tra retry statistics từ Envoy sidecar
PRODUCT_POD=$(kubectl get pod -n yas -l app.kubernetes.io/name=product -o jsonpath='{.items[0].metadata.name}')

kubectl exec -n yas $PRODUCT_POD -c istio-proxy -- \
  pilot-agent request GET stats | grep -E "upstream_rq_retry|upstream_rq_5xx"

# Expected output (sau khi có traffic):
# cluster.outbound|80||product.yas.svc.cluster.local.upstream_rq_retry: <N>
# cluster.outbound|80||product.yas.svc.cluster.local.upstream_rq_5xx: <N>

# Verify VirtualService retry config
kubectl get virtualservice product-retry -n yas -o yaml | grep -A 5 retries
```

### Cleanup Test Resources
```bash
kubectl delete -f test-denied-pod.yaml
```

---

## 9. Sử dụng Kiali Dashboard

### Truy cập Kiali

```bash
# Port-forward Kiali service
kubectl port-forward svc/kiali -n istio-system 20001:20001

# Mở browser
# URL: http://localhost:20001
```

### Xem Topology

1. **Mở Kiali** → đăng nhập (token hoặc anonymous tùy cấu hình)
2. **Menu trái** → chọn **Graph**
3. **Namespace dropdown** → chọn `yas`
4. **Display** → tick các options:
   - ✅ Traffic Animation
   - ✅ Security (hiện 🔒 mTLS)
   - ✅ Response Time
   - ✅ Throughput
5. **Graph Type** → chọn `Versioned app graph` hoặc `Workload graph`

### Các thông tin quan sát trên Kiali

| Thành phần | Ý nghĩa |
|------------|---------|
| 🔒 (Lock icon) | mTLS đang hoạt động giữa 2 services |
| Mũi tên xanh | Traffic thành công (2xx) |
| Mũi tên đỏ | Traffic lỗi (4xx, 5xx) |
| Đường nét đứt | Không có traffic hiện tại |
| Badge "VS" | Có VirtualService cấu hình |
| Badge "DR" | Có DestinationRule cấu hình |

### Screenshot Topology

Để capture topology cho báo cáo:
1. Mở Kiali Graph → chọn namespace `yas`
2. Tạo traffic bằng cách gọi API qua storefront
3. Chờ ~30s để Kiali thu thập metrics
4. Screenshot graph hiển thị:
   - Tất cả services với 🔒 mTLS icons
   - Traffic flow giữa các services
   - VirtualService và DestinationRule badges

---

## 10. Troubleshooting

### Vấn đề thường gặp

#### Pods không inject sidecar
```bash
# Kiểm tra label namespace
kubectl get namespace yas --show-labels
# Phải có: istio-injection=enabled

# Force restart pods
kubectl rollout restart deployment --all -n yas
```

#### mTLS không hoạt động
```bash
# Kiểm tra PeerAuthentication
kubectl get peerauthentication -n yas -o yaml

# Kiểm tra DestinationRule
kubectl get destinationrule -n yas -o yaml

# Debug với istioctl
istioctl analyze -n yas
```

#### Authorization Policy quá strict (service bị chặn không mong muốn)
```bash
# Tạm thời switch sang PERMISSIVE để debug
kubectl patch peerauthentication yas-strict-mtls -n yas \
  --type merge -p '{"spec":{"mtls":{"mode":"PERMISSIVE"}}}'

# Kiểm tra logs Envoy proxy
kubectl logs <pod-name> -n yas -c istio-proxy | grep "rbac"

# Xem policy đang apply cho pod
istioctl x describe pod <pod-name> -n yas
```

#### Muốn tạm disable authorization policies
```bash
# Xóa deny-all default (cho phép tất cả traffic)
kubectl delete authorizationpolicy deny-all-default -n yas

# Hoặc xóa tất cả policies
kubectl delete authorizationpolicy --all -n yas
```

### Lệnh hữu ích

```bash
# Xem tất cả Istio resources trong namespace yas
kubectl get peerauthentication,destinationrule,virtualservice,authorizationpolicy -n yas

# Phân tích mesh config
istioctl analyze -n yas

# Dashboard (mở trực tiếp Kiali)
istioctl dashboard kiali

# Xem proxy config
istioctl proxy-config cluster <pod-name> -n yas
istioctl proxy-config listener <pod-name> -n yas
istioctl proxy-config route <pod-name> -n yas
```

---

## Tham khảo

- [Istio Documentation](https://istio.io/latest/docs/)
- [Istio PeerAuthentication](https://istio.io/latest/docs/reference/config/security/peer_authentication/)
- [Istio AuthorizationPolicy](https://istio.io/latest/docs/reference/config/security/authorization-policy/)
- [Istio VirtualService](https://istio.io/latest/docs/reference/config/networking/virtual-service/)
- [Kiali Documentation](https://kiali.io/docs/)
