# Service Mesh Configuration - Istio + Kiali cho YAS Microservices

## Mục lục

- [1. Tổng quan](#1-tổng-quan)
- [2. Kiến trúc](#2-kiến-trúc)
- [3. Prerequisites](#3-prerequisites)
- [4. Cài đặt Istio (1 lần)](#4-cài-đặt-istio-1-lần)
- [5. Apply Mesh cho Namespace](#5-apply-mesh-cho-namespace)
- [6. Cấu hình mTLS](#6-cấu-hình-mtls)
- [7. Authorization Policy](#7-authorization-policy)
- [8. Retry Policy](#8-retry-policy)
- [9. Kịch bản Test](#9-kịch-bản-test)
- [10. Kiali Dashboard](#10-kiali-dashboard)
- [11. Chuyển đổi Namespace](#11-chuyển-đổi-namespace)
- [12. Troubleshooting](#12-troubleshooting)

---

## 1. Tổng quan

### Vấn đề

Do tài nguyên máy tính hạn chế, chỉ **1 trong 3 namespace** chạy tại một thời điểm:

- `yas` (dev environment - GitOps)
- `staging` (staging environment - GitOps)
- `yas-dev-*` (developer build - tạo động)

Khi namespace A chạy → namespace B,C bị scale về 0.

### Giải pháp

Service mesh được đóng gói thành **Helm chart** (`k8s/charts/service-mesh/`) → deploy vào **bất kỳ namespace nào** đang active:

```bash
# Apply cho namespace đang chạy
helm upgrade --install service-mesh k8s/charts/service-mesh -n <NAMESPACE>
```

### Cấu trúc file

```
k8s/
├── charts/
│   └── service-mesh/              # ⭐ Helm chart (namespace-agnostic)
│       ├── Chart.yaml
│       ├── values.yaml            # Tham số: services, mTLS, retry, auth
│       └── templates/
│           ├── _helpers.tpl
│           ├── mtls.yaml          # PeerAuthentication + DestinationRules
│           ├── authorization.yaml # Deny-all + Allow policies
│           ├── retry.yaml         # VirtualService retry
│           └── tests/
│               └── test-pods.yaml # Test denied/allowed pods
│
├── deploy/
│   └── service-mesh/
│       ├── install-istio.sh       # Cài Istio system (1 lần)
│       ├── apply-mesh.sh          # Apply mesh cho NS active
│       ├── remove-mesh.sh         # Xoá mesh khỏi NS
│       ├── test-service-mesh.sh   # ⭐ Script test tự động
│       ├── README.md              # File này
│       ├── peer-authentication.yaml    # Standalone (fallback)
│       ├── destination-rules.yaml      # Standalone (fallback)
│       ├── authorization-policies.yaml # Standalone (fallback)
│       ├── virtual-services.yaml       # Standalone (fallback)
│       └── test-denied-pod.yaml        # Standalone (fallback)
```

---

## 2. Kiến trúc

### Flow Service Mesh

```
┌──────────────────────────────────────────────────────────────┐
│  Cluster                                                     │
│                                                              │
│  ┌─────────────────┐   ┌──────────────────────────────────┐  │
│  │  istio-system    │   │  Active Namespace (yas/staging/  │  │
│  │                  │   │  yas-dev-*)                      │  │
│  │  ┌────────────┐  │   │                                  │  │
│  │  │  istiod    │──┼──►│  [Pod+Sidecar] ◄──mTLS──►       │  │
│  │  └────────────┘  │   │  [Pod+Sidecar] ◄──mTLS──►       │  │
│  │  ┌────────────┐  │   │  [Pod+Sidecar] ◄──mTLS──►       │  │
│  │  │  kiali     │  │   │                                  │  │
│  │  └────────────┘  │   │  AuthorizationPolicy: deny-all   │  │
│  │  ┌────────────┐  │   │  + per-service ALLOW policies    │  │
│  │  │ prometheus │  │   │  VirtualService: retry 3x on 5xx│  │
│  │  └────────────┘  │   └──────────────────────────────────┘  │
│  └─────────────────┘                                         │
│                         ┌──────────────────────────────────┐  │
│                         │  Inactive Namespaces              │  │
│                         │  (scaled to 0, no mesh config)    │  │
│                         └──────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

### Access Matrix (Authorization)

| Target ↓ / Source → | storefront-bff | backoffice-bff | nginx | order | cart | payment |
| ------------------- | :------------: | :------------: | :---: | :---: | :--: | :-----: |
| **product**         |       ✅       |       ✅       |  ✅   |  ✅   |  ✅  |   ❌    |
| **cart**            |       ✅       |       ✅       |  ✅   |  ✅   |  —   |   ❌    |
| **order**           |       ✅       |       ✅       |  ✅   |   —   |  ❌  |   ✅    |
| **payment**         |       ❌       |       ❌       |  ✅   |  ✅   |  ❌  |    —    |
| **customer**        |       ✅       |       ✅       |  ✅   |  ✅   |  ❌  |   ❌    |
| **inventory**       |       ✅       |       ✅       |  ✅   |  ✅   |  ❌  |   ❌    |

✅ = Allowed | ❌ = Denied | — = Self

---

## 3. Prerequisites

```bash
# Kiểm tra cluster
kubectl cluster-info

# Kiểm tra Helm
helm version
```

---

## 4. Cài đặt Istio (1 lần)

Chỉ cần chạy **1 lần** trên cluster:

```bash
cd k8s/deploy/service-mesh
chmod +x install-istio.sh
./install-istio.sh
```

Verify:

```bash
kubectl get pods -n istio-system
# istiod, kiali, prometheus, grafana, jaeger phải Running
```

---

## 5. Apply Mesh cho Namespace

### Cách 1: Auto-detect namespace đang active

```bash
cd k8s/deploy/service-mesh
chmod +x apply-mesh.sh
./apply-mesh.sh
```

Script tự tìm namespace có pods đang chạy (ưu tiên: `yas-dev-*` → `yas` → `staging`).

### Cách 2: Chỉ định namespace

```bash
# Dev namespace
./apply-mesh.sh yas

# Staging
./apply-mesh.sh staging

# Developer build
./apply-mesh.sh yas-dev-john-42
```

### Cách 3: Dùng Helm trực tiếp

```bash
helm upgrade --install service-mesh k8s/charts/service-mesh \
    --namespace yas-dev-john-42
```

### Cách 4: Tự động trong CI/CD

Đã tích hợp vào workflow `developer_build.yaml` → khi deploy developer build, service mesh tự động apply vào namespace mới.

---

## 6. Cấu hình mTLS

### Cách hoạt động

```yaml
# PeerAuthentication (server-side) - yêu cầu client gửi mTLS
PeerAuthentication:
    mtls:
        mode: STRICT # Reject plaintext traffic

# DestinationRule (client-side) - cấu hình client gửi mTLS
DestinationRule:
    trafficPolicy:
        tls:
            mode: ISTIO_MUTUAL # Istio tự quản lý certificates
```

### Verify

```bash
NS=yas  # hoặc namespace đang active

# Kiểm tra mTLS trên pod
POD=$(kubectl get pod -n $NS -l app.kubernetes.io/name=product -o jsonpath='{.items[0].metadata.name}')
istioctl x describe pod $POD -n $NS

# Kiểm tra PeerAuthentication
kubectl get peerauthentication -n $NS
```

---

## 7. Authorization Policy

### Chiến lược: Deny-by-Default + Allow-List

Helm chart tự động tạo:

1. **`deny-all-default`** → chặn tất cả traffic
2. **`allow-<service>-access`** → cho phép các caller cụ thể

### Customize allow list

Chỉnh trong `values.yaml`:

```yaml
backendServices:
    - name: payment
      allowedCallers:
          - order # Chỉ order được gọi payment
          # Thêm service khác nếu cần
```

---

## 8. Retry Policy

### Cấu hình

```yaml
# values.yaml
retry:
    attempts: 3 # Retry 3 lần
    perTryTimeout: 5s # Timeout mỗi lần: 5s
    retryOn: "5xx,connect-failure,refused-stream,reset"
    timeout: 30s # Timeout tổng: 30s
```

---

## 9. Kịch bản Test

### Chạy Test Tự Động (Khuyến nghị)

```bash
cd k8s/deploy/service-mesh
chmod +x test-service-mesh.sh
./test-service-mesh.sh              # Auto-detect namespace
./test-service-mesh.sh yas          # Hoặc chỉ định namespace
```

Script tự động chạy tất cả test cases và hiển thị kết quả ✅ PASS / ❌ FAIL.

### Giải thích HTTP Status Code

| HTTP Code | Ý nghĩa | Layer |
|---|---|---|
| **403** | `RBAC: access denied` - Istio chặn | Istio AuthorizationPolicy (network) |
| **401** | Unauthorized - App yêu cầu JWT token | Spring Security (application) |
| **200** | OK - Request thành công | Application |

> **Lưu ý**: Khi test ALLOW policy, dùng `/actuator/health` thay vì `/product/` để tránh bị 401 từ app-level auth. Mục đích test là chứng minh **Istio policy hoạt động**, không phải app auth.
> - Test ALLOW: HTTP code **≠ 403** → Istio cho phép traffic đi qua ✅
> - Test DENY: HTTP code **= 403** → Istio chặn traffic ✅

### Test 1: mTLS Verification

```bash
NS=yas  # namespace đang active
POD=$(kubectl get pod -n $NS -o jsonpath='{.items[0].metadata.name}')
istioctl x describe pod $POD -n $NS
# Expected: mTLS mode: STRICT
```

### Test 2: Authorization ALLOW

```bash
NS=yas

# Deploy test pods
kubectl apply -f <(helm template service-mesh k8s/charts/service-mesh \
    -n $NS -s templates/tests/test-pods.yaml)
kubectl wait --for=condition=ready pod/test-allowed-client -n $NS --timeout=120s

# storefront-bff SA → product: ALLOWED
# Dùng /actuator/health để tránh 401 từ Spring Security
kubectl exec -n $NS test-allowed-client -- \
    curl -s -o /dev/null -w "%{http_code}" http://product.$NS:80/actuator/health
# Expected: 200 (hoặc bất kỳ code nào ≠ 403)
# Nếu nhận 401 → Istio policy ALLOW hoạt động, app yêu cầu JWT (bình thường)
# Nếu nhận 403 → Istio policy DENY → cần kiểm tra AuthorizationPolicy
```

### Test 3: Authorization DENY

```bash
NS=yas
kubectl wait --for=condition=ready pod/test-client -n $NS --timeout=120s

# test-client SA → product: DENIED
kubectl exec -n $NS test-client -- \
    curl -s -o /dev/null -w "%{http_code}" http://product.$NS:80/actuator/health
# Expected: HTTP 403 RBAC: access denied

# test-client SA → payment: DENIED
kubectl exec -n $NS test-client -- \
    curl -s -o /dev/null -w "%{http_code}" http://payment.$NS:80/actuator/health
# Expected: HTTP 403 RBAC: access denied
```

### Test 4: Cross-service DENY

```bash
NS=yas
CART_POD=$(kubectl get pod -n $NS -l app.kubernetes.io/name=cart -o jsonpath='{.items[0].metadata.name}')

# Cart → Payment: DENIED (cart không trong allow-list của payment)
kubectl exec -n $NS $CART_POD -c cart -- \
    curl -s -o /dev/null -w "%{http_code}" http://payment.$NS:80/actuator/health
# Expected: HTTP 403 RBAC: access denied
```

### Test 5: Cross-service ALLOW

```bash
NS=yas
ORDER_POD=$(kubectl get pod -n $NS -l app.kubernetes.io/name=order -o jsonpath='{.items[0].metadata.name}')

# Order → Payment: ALLOWED (order nằm trong allow-list của payment)
kubectl exec -n $NS $ORDER_POD -c order -- \
    curl -s -o /dev/null -w "%{http_code}" http://payment.$NS:80/actuator/health
# Expected: HTTP ≠ 403 (200 hoặc 401)
```

### Test 6: Retry Evidence

```bash
NS=yas
POD=$(kubectl get pod -n $NS -l app.kubernetes.io/name=product -o jsonpath='{.items[0].metadata.name}')

# Kiểm tra VirtualService retry config
kubectl get virtualservice product-retry -n $NS -o yaml | grep -A5 retries

# Kiểm tra Envoy retry stats
kubectl exec -n $NS $POD -c istio-proxy -- \
    pilot-agent request GET stats | grep -E "upstream_rq_retry|upstream_rq_5xx"
```

### Cleanup Test Pods

```bash
kubectl delete pod test-client test-allowed-client -n $NS --grace-period=0 --force
kubectl delete sa test-client -n $NS
```

---

## 10. Kiali Dashboard

```bash
kubectl port-forward svc/kiali -n istio-system 20001:20001
# Mở: http://localhost:20001
```

1. **Graph** → chọn namespace active → xem topology
2. **Security badge (🔒)** = mTLS active
3. **VS badge** = VirtualService (retry) configured
4. **Traffic animation** = request flow giữa services

---

## 11. Chuyển đổi Namespace

Khi cần chuyển mesh từ namespace A sang B:

```bash
# 1. Xoá mesh khỏi namespace cũ
./remove-mesh.sh yas

# 2. Apply mesh cho namespace mới
./apply-mesh.sh staging

# Hoặc xoá tất cả
./remove-mesh.sh --all
```

### Workflow khi developer build

```
developer_build triggers:
  1. Scale down dev + staging (replicas=0)
  2. Create yas-dev-* namespace
  3. Deploy services
  4. Auto-apply service mesh (via workflow)  ← TỰ ĐỘNG
  5. Done

developer_cleanup triggers:
  1. Remove service mesh resources          ← TỰ ĐỘNG
  2. Uninstall Helm releases
  3. Delete namespace
```

---

## 12. Troubleshooting

### Pods không inject sidecar

```bash
# Kiểm tra label
kubectl get namespace $NS --show-labels | grep istio-injection

# Nếu thiếu, apply lại
kubectl label namespace $NS istio-injection=enabled --overwrite
kubectl rollout restart deployment --all -n $NS
```

### Service bị chặn không mong muốn

```bash
# Kiểm tra policy
istioctl x describe pod $POD -n $NS

# Tạm tắt authorization
kubectl delete authorizationpolicy deny-all-default -n $NS

# Hoặc switch sang PERMISSIVE
kubectl patch peerauthentication ${NS}-strict-mtls -n $NS \
    --type merge -p '{"spec":{"mtls":{"mode":"PERMISSIVE"}}}'
```

### Xem Envoy proxy logs

```bash
kubectl logs $POD -n $NS -c istio-proxy | grep "rbac"
```

### Lệnh hữu ích

```bash
# Liệt kê tất cả Istio resources
kubectl get peerauthentication,destinationrule,virtualservice,authorizationpolicy -n $NS

# Phân tích cấu hình
istioctl analyze -n $NS

# Mở Kiali
istioctl dashboard kiali
```

---

## Tham khảo

- [Istio Documentation](https://istio.io/latest/docs/)
- [Istio PeerAuthentication](https://istio.io/latest/docs/reference/config/security/peer_authentication/)
- [Istio AuthorizationPolicy](https://istio.io/latest/docs/reference/config/security/authorization-policy/)
- [Istio VirtualService](https://istio.io/latest/docs/reference/config/networking/virtual-service/)
- [Kiali Documentation](https://kiali.io/docs/)
