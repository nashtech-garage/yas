# 👤 TV4 — Service Mesh (Istio) + Security + Báo Cáo Tổng Hợp

> **Vai trò:** Cài Istio, cấu hình mTLS/AuthzPolicy/Retry, Kiali visualization, viết báo cáo cuối.  
> **Ưu tiên:** Tuần 1 viết YAML sẵn (độc lập). Tuần 2 apply khi cluster ready.

---

## Phase 1 — Viết Istio YAML Manifests (Tuần 1, độc lập)

> Viết sẵn tất cả YAML trước khi cluster ready. Khi TV1 xong → apply ngay.

### 1.1 Tạo cấu trúc thư mục
```
istio/
├── peer-authentication.yaml
├── destination-rule.yaml
├── authorization/
│   ├── deny-all.yaml
│   ├── allow-storefront-bff.yaml
│   ├── allow-backoffice-bff.yaml
│   ├── allow-order.yaml
│   └── ... (theo service pairs)
├── virtual-services/
│   ├── product-vs.yaml
│   ├── order-vs.yaml
│   ├── payment-vs.yaml
│   ├── cart-vs.yaml
│   └── inventory-vs.yaml
└── README.md
```
- [ ] Tạo toàn bộ cấu trúc thư mục

### 1.2 Viết PeerAuthentication (mTLS)
```yaml
# istio/peer-authentication.yaml
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
  namespace: istio-system
spec:
  mtls:
    mode: STRICT
```
- [ ] File `peer-authentication.yaml` sẵn sàng

### 1.3 Viết DestinationRule (mTLS)
```yaml
# istio/destination-rule.yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: default
  namespace: dev
spec:
  host: "*.dev.svc.cluster.local"
  trafficPolicy:
    tls:
      mode: ISTIO_MUTUAL
```
- [ ] File `destination-rule.yaml` cho dev
- [ ] Tạo thêm cho staging nếu cần

### 1.4 Viết Authorization Policies

**Deny-all (mặc định):**
```yaml
# istio/authorization/deny-all.yaml
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: deny-all
  namespace: dev
spec: {}
```

**Allow policies cho từng service pair:**

| Source | Allowed Destinations |
|--------|---------------------|
| `storefront-bff` | product, media, cart, order, customer, rating, search, promotion |
| `backoffice-bff` | product, media, order, inventory, promotion, customer |
| `order` | inventory, payment, customer, cart, delivery |
| `payment` | order, payment-paypal |
| `cart` | product, media, promotion |
| `delivery` | order |
| `recommendation` | product, order |

- [ ] Viết `allow-storefront-bff.yaml`:
```yaml
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: allow-from-storefront-bff
  namespace: dev
spec:
  action: ALLOW
  rules:
  - from:
    - source:
        principals: ["cluster.local/ns/dev/sa/storefront-bff"]
    to:
    - operation:
        methods: ["GET", "POST", "PUT", "DELETE"]
```
- [ ] Viết allow policy cho từng source service (7+ files)
- [ ] Liệt kê rõ service pairs trong README

### 1.5 Viết VirtualService (Retry Policy)
Cho 5 critical services: `product`, `order`, `payment`, `cart`, `inventory`

```yaml
# istio/virtual-services/product-vs.yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: product-vs
  namespace: dev
spec:
  hosts:
  - product
  http:
  - route:
    - destination:
        host: product
    retries:
      attempts: 3
      perTryTimeout: 2s
      retryOn: 5xx,reset,connect-failure
```
- [ ] `product-vs.yaml`
- [ ] `order-vs.yaml`
- [ ] `payment-vs.yaml`
- [ ] `cart-vs.yaml`
- [ ] `inventory-vs.yaml`

---

## Phase 2 — Istio Installation (Tuần 2, sau khi TV1 xong cluster)

### 2.1 Cài Istio
```bash
# Download istioctl
curl -L https://istio.io/downloadIstio | sh -
cd istio-*
export PATH=$PWD/bin:$PATH

# Install với profile demo
istioctl install --set profile=demo -y
```
- [ ] Verify: `kubectl get pods -n istio-system` → tất cả Running
- [ ] 📸 Screenshot: Istio pods running

### 2.2 Enable sidecar injection
```bash
kubectl label namespace dev istio-injection=enabled
kubectl label namespace staging istio-injection=enabled
kubectl label namespace developer-build istio-injection=enabled
```
- [ ] Verify labels: `kubectl get ns --show-labels`
- [ ] Restart pods để inject sidecar: `kubectl rollout restart deployment -n dev`
- [ ] Verify sidecar: `kubectl get pods -n dev` → mỗi pod có 2/2 containers
- [ ] 📸 Screenshot: Pods với sidecar (2/2 READY)

### 2.3 Cài Kiali + Addons
```bash
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/kiali.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/prometheus.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/grafana.yaml
```
- [ ] Chờ pods ready: `kubectl get pods -n istio-system`
- [ ] 📸 Screenshot: Kiali + Prometheus + Grafana pods running

---

## Phase 3 — Apply mTLS [NC2.1] (Tuần 2)

### 3.1 Apply PeerAuthentication
```bash
kubectl apply -f istio/peer-authentication.yaml
```
- [ ] Verify mTLS active:
```bash
istioctl x describe pod <pod-name> -n dev
```
- [ ] Expected output: `mTLS mode: STRICT`
- [ ] 📸 Screenshot: mTLS verification output

### 3.2 Apply DestinationRule
```bash
kubectl apply -f istio/destination-rule.yaml
```
- [ ] Verify: traffic giữa services được encrypt

---

## Phase 4 — Apply Authorization Policy [NC2.3] (Tuần 2-3)

### 4.1 Apply deny-all
```bash
kubectl apply -f istio/authorization/deny-all.yaml
```
- [ ] Test: curl từ bất kỳ pod → bất kỳ service → expect **403 Forbidden**
```bash
kubectl exec -n dev <pod-A> -- curl -v http://product.dev:8080/
# Expected: 403 RBAC: access denied
```
- [ ] 📸 Screenshot: 403 response

### 4.2 Apply allow policies
```bash
kubectl apply -f istio/authorization/
```
- [ ] Test allowed connection:
```bash
# Từ storefront-bff → product (ALLOWED)
kubectl exec -n dev <storefront-bff-pod> -- curl -v http://product.dev:8080/
# Expected: 200 OK
```
- [ ] Test denied connection:
```bash
# Từ media → order (DENIED)
kubectl exec -n dev <media-pod> -- curl -v http://order.dev:8080/
# Expected: 403 RBAC: access denied
```
- [ ] 📸 Screenshot: 200 (allowed) vs 403 (denied)

---

## Phase 5 — Test Retry Policy [NC2.3] (Tuần 2-3)

### 5.1 Apply VirtualServices
```bash
kubectl apply -f istio/virtual-services/
```

### 5.2 Test retry behavior
- [ ] Inject fault vào 1 service:
```yaml
# Tạm thêm fault injection vào product
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: product-fault-test
  namespace: dev
spec:
  hosts:
  - product
  http:
  - fault:
      abort:
        percentage:
          value: 50
        httpStatus: 500
    route:
    - destination:
        host: product
```
- [ ] Gửi nhiều requests → observe retry:
```bash
for i in $(seq 1 20); do
  kubectl exec -n dev <client-pod> -- curl -s -o /dev/null -w "%{http_code}\n" http://product.dev:8080/
done
```
- [ ] Expected: phần lớn requests thành công nhờ retry (dù 50% fault)
- [ ] 📸 Screenshot: Retry evidence (logs / response codes)
- [ ] Xóa fault injection sau khi test xong

---

## Phase 6 — Kiali Topology [NC2.2] (Tuần 3)

### 6.1 Access Kiali dashboard
```bash
istioctl dashboard kiali
# Hoặc port-forward:
kubectl port-forward svc/kiali -n istio-system 20001:20001
```
- [ ] Truy cập: `http://localhost:20001`

### 6.2 Generate traffic
```bash
# Gửi traffic liên tục để Kiali có data
while true; do
  kubectl exec -n dev <client-pod> -- curl -s http://storefront-bff.dev:8080/
  sleep 1
done
```

### 6.3 Capture topology screenshots
- [ ] 📸 Screenshot: **Graph view** — toàn bộ service mesh topology
- [ ] 📸 Screenshot: **Versioned app graph** — hiển thị versions
- [ ] 📸 Screenshot: **Security view** — mTLS padlock icons
- [ ] 📸 Screenshot: **Traffic animation** — request flow
- [ ] 📸 Screenshot: **Service detail** — 1 service cụ thể (e.g., product)

### 6.4 Vẽ flow chart
- [ ] Vẽ diagram các service connections (dựa trên Kiali data)
- [ ] Giải thích từng flow: storefront → product, order → payment, etc.

---

## Phase 7 — Báo Cáo Tổng Hợp (Tuần 3)

### 7.1 Thu thập từ team
- [ ] Nhận báo cáo phần TV1 (GCP + K8s + ArgoCD + Agent)
- [ ] Nhận báo cáo phần TV2 (CI/CD Pipelines)
- [ ] Nhận báo cáo phần TV3 (GitOps Manifests)

### 7.2 Viết báo cáo `.docx`
Cấu trúc báo cáo:
```
1. Giới thiệu
   - Mô tả dự án YAS
   - Mục tiêu đồ án
   - Công nghệ sử dụng

2. Kiến trúc hệ thống
   - Sơ đồ tổng quan (AWS + GCP)
   - Mô tả từng thành phần

3. Hạ tầng K8s (TV1)
   - GCP VM setup
   - K3s cluster
   - ArgoCD configuration
   - Jenkins Agent

4. CI/CD Pipelines (TV2)
   - CI Pipeline (build + push)
   - developer_build job
   - Cleanup job
   - Dev/Staging triggers

5. GitOps Manifests (TV3)
   - Kustomize structure
   - Base manifests
   - Environment overlays
   - Infrastructure

6. Service Mesh — Istio (TV4)
   - mTLS setup + verification
   - Authorization Policy + test results
   - Retry Policy + test results
   - Kiali topology + flow analysis

7. Kết luận
   - Tổng kết
   - Bài học kinh nghiệm
```

### 7.3 Format báo cáo
- [ ] Tên file: `<MSSV1>_<MSSV2>_<MSSV3>_<MSSV4>.docx` (MSSV tăng dần)
- [ ] Chèn screenshots đúng vị trí
- [ ] Đính kèm test logs (curl results, retry evidence)
- [ ] Review format, spelling, layout
- [ ] Gửi team review lần cuối trước deadline

---

## ✅ Checklist Cuối Cùng

- [ ] **Istio YAMLs:** peer-auth, dest-rule, authz policies, virtual services
- [ ] **mTLS:** STRICT mode active, verified qua `istioctl describe`
- [ ] **Authorization:** deny-all + allow rules, test 200 vs 403
- [ ] **Retry:** VirtualService retry config, fault injection test
- [ ] **Kiali:** 5+ screenshots topology, security view, traffic flow
- [ ] **Báo cáo:** `.docx` hoàn chỉnh, format đúng, team đã review
- [ ] **README:** `istio/README.md` hướng dẫn setup từng bước
