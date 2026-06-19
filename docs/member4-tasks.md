# TV4 — Service Mesh (Istio) + Security + Báo Cáo Tổng Hợp

> **Vai trò:** Cài Istio, cấu hình mTLS/AuthzPolicy/Retry, Kiali visualization, viết báo cáo cuối.
> **Tuần 1:** Viết YAML manifests sẵn (hoàn toàn độc lập — không cần cluster).
> **Tuần 2:** Apply khi TV1 xong cluster + TV3 đảm bảo ServiceAccount có trong manifests.
> **Dependency quan trọng:** TV3 phải có ServiceAccount cho từng service → TV4 mới apply AuthorizationPolicy được.

---

## Phụ Thuộc Cần Xác Nhận Trước Khi Apply

| Điều kiện | Người cung cấp | Kiểm tra |
|----------|---------------|---------|
| K3s cluster running | TV1 | `kubectl get nodes` |
| Namespaces: dev, staging, developer-build | TV1 | `kubectl get ns` |
| Pods running trong `dev` | TV1 + TV3 + TV2 | `kubectl get pods -n dev` |
| **ServiceAccount cho 19 services** | **TV3** | `kubectl get sa -n dev` |
| Istio chưa label namespace trước | TV4 tự xử lý | label TRƯỚC khi deploy app pods |

---

## YAS Service Communication Map (Authorization Policy)

```
storefront-bff  → product, media, cart, order, customer, rating,
                  search, promotion, tax, location
backoffice-bff  → product, media, order, inventory, promotion,
                  rating, webhook, customer, location
order           → inventory, payment, customer, cart, tax, webhook, delivery
cart            → product, promotion, tax
customer        → location
payment         → webhook, payment-paypal
delivery        → order
recommendation  → product, order
```

---

## Phase 1 — Viết Istio YAML Manifests (Tuần 1 — độc lập)

Tạo thư mục `istio/` trong repo yas:

```
istio/
├── peer-authentication.yaml
├── destination-rule-dev.yaml
├── destination-rule-staging.yaml
├── authorization/
│   ├── deny-all.yaml
│   ├── allow-storefront-bff.yaml
│   ├── allow-backoffice-bff.yaml
│   ├── allow-order.yaml
│   ├── allow-cart.yaml
│   ├── allow-payment.yaml
│   ├── allow-customer.yaml
│   ├── allow-delivery.yaml
│   └── allow-recommendation.yaml
├── virtual-services/
│   ├── product-vs.yaml
│   ├── order-vs.yaml
│   ├── payment-vs.yaml
│   ├── cart-vs.yaml
│   └── inventory-vs.yaml
└── README.md
```

### 1.1 PeerAuthentication — mTLS STRICT

```yaml
# istio/peer-authentication.yaml
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
  namespace: istio-system   # apply toàn bộ cluster
spec:
  mtls:
    mode: STRICT
```

### 1.2 DestinationRule — enable ISTIO_MUTUAL

```yaml
# istio/destination-rule-dev.yaml
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

```yaml
# istio/destination-rule-staging.yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: default
  namespace: staging
spec:
  host: "*.staging.svc.cluster.local"
  trafficPolicy:
    tls:
      mode: ISTIO_MUTUAL
```

### 1.3 AuthorizationPolicy — Deny All (mặc định)

```yaml
# istio/authorization/deny-all.yaml
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: deny-all
  namespace: dev
spec: {}
  # spec rỗng = deny tất cả traffic vào tất cả pods trong namespace dev
```

### 1.4 AuthorizationPolicy — Allow Policies

Format principal: `cluster.local/ns/<namespace>/sa/<serviceaccount-name>`
ServiceAccount name = service name (TV3 đảm bảo điều này).

**allow-storefront-bff.yaml** (storefront-bff được gọi tới product, media, cart, order, customer, rating, search, promotion, tax, location):
```yaml
# istio/authorization/allow-storefront-bff.yaml
# Một policy cho mỗi TARGET service — storefront-bff là SOURCE được phép gọi

---
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: allow-storefront-to-product
  namespace: dev
spec:
  selector:
    matchLabels:
      app: product
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/dev/sa/storefront-bff"

---
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: allow-storefront-to-media
  namespace: dev
spec:
  selector:
    matchLabels:
      app: media
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/dev/sa/storefront-bff"

---
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: allow-storefront-to-cart
  namespace: dev
spec:
  selector:
    matchLabels:
      app: cart
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/dev/sa/storefront-bff"
# ... (tương tự cho order, customer, rating, search, promotion, tax, location)
```

**allow-backoffice-bff.yaml** (backoffice-bff → product, media, order, inventory, promotion, rating, webhook, customer, location):
```yaml
# istio/authorization/allow-backoffice-bff.yaml
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: allow-backoffice-to-product
  namespace: dev
spec:
  selector:
    matchLabels:
      app: product
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/dev/sa/backoffice-bff"
# ... (tương tự cho media, order, inventory, promotion, rating, webhook, customer, location)
```

**allow-order.yaml** (order → inventory, payment, customer, cart, tax, webhook, delivery):
```yaml
# istio/authorization/allow-order.yaml
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: allow-order-to-inventory
  namespace: dev
spec:
  selector:
    matchLabels:
      app: inventory
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/dev/sa/order"
# ... (tương tự cho payment, customer, cart, tax, webhook, delivery)
```

**allow-cart.yaml** (cart → product, promotion, tax):
```yaml
# istio/authorization/allow-cart.yaml
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: allow-cart-to-product
  namespace: dev
spec:
  selector:
    matchLabels:
      app: product
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/dev/sa/cart"
# ... (tương tự cho promotion, tax)
```

**allow-payment.yaml** (payment → webhook, payment-paypal):
```yaml
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: allow-payment-to-webhook
  namespace: dev
spec:
  selector:
    matchLabels:
      app: webhook
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/dev/sa/payment"
```

**allow-customer.yaml** (customer → location):
```yaml
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: allow-customer-to-location
  namespace: dev
spec:
  selector:
    matchLabels:
      app: location
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/dev/sa/customer"
```

**allow-delivery.yaml** (delivery → order):
```yaml
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: allow-delivery-to-order
  namespace: dev
spec:
  selector:
    matchLabels:
      app: order
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/dev/sa/delivery"
```

**allow-recommendation.yaml** (recommendation → product, order):
```yaml
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: allow-recommendation-to-product
  namespace: dev
spec:
  selector:
    matchLabels:
      app: product
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/dev/sa/recommendation"
```

- [ ] Tạo đủ tất cả authorization YAML files

### 1.5 VirtualService — Retry Policy

Cho 5 critical services: product, order, payment, cart, inventory.

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
        retryOn: 5xx,reset,connect-failure,retriable-4xx
      timeout: 10s
```

Tạo 5 files tương tự: `order-vs.yaml`, `payment-vs.yaml`, `cart-vs.yaml`, `inventory-vs.yaml`

- [ ] Tạo 5 VirtualService files

---

## Phase 2 — Cài Istio (Tuần 2, sau khi TV1 xong cluster)

### 2.1 Download và cài Istio
```bash
# Trên GCP VM (hoặc máy có kubectl access)
curl -L https://istio.io/downloadIstio | ISTIO_VERSION=1.20.0 sh -
cd istio-1.20.0
export PATH=$PWD/bin:$PATH

# Verify
istioctl version

# Install với profile demo (bao gồm ingress/egress gateway)
istioctl install --set profile=demo -y
```
- [ ] Verify: `kubectl get pods -n istio-system` → tất cả Running
- [ ] 📸 Screenshot: Istio pods running (`istiod`, `istio-ingressgateway`, `istio-egressgateway`)

### 2.2 Enable sidecar injection cho namespaces

> **QUAN TRỌNG:** Label namespace TRƯỚC khi deploy app pods.
> Nếu pods đã chạy → phải restart để sidecar được inject.

```bash
kubectl label namespace dev istio-injection=enabled
kubectl label namespace staging istio-injection=enabled
kubectl label namespace developer-build istio-injection=enabled

# Verify labels
kubectl get ns --show-labels | grep istio-injection

# Nếu pods đã running → restart để inject sidecar
kubectl rollout restart deployment --all -n dev
```
- [ ] Verify: `kubectl get pods -n dev` → mỗi pod có **2/2** containers (app + istio-proxy)
- [ ] 📸 Screenshot: Pods với 2/2 READY

### 2.3 Cài Kiali + Addons
```bash
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/kiali.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/prometheus.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.20/samples/addons/grafana.yaml

# Chờ pods ready
kubectl wait --for=condition=Ready pods --all -n istio-system --timeout=300s
```
- [ ] 📸 Screenshot: Kiali + Prometheus + Grafana pods running

---

## Phase 3 — Apply mTLS [NC2.1] (Tuần 2)

### 3.1 Apply PeerAuthentication + DestinationRule
```bash
kubectl apply -f istio/peer-authentication.yaml
kubectl apply -f istio/destination-rule-dev.yaml
```

### 3.2 Verify mTLS active
```bash
# Lấy tên 1 pod bất kỳ trong dev
POD=$(kubectl get pods -n dev -o jsonpath='{.items[0].metadata.name}')

# Kiểm tra mTLS
istioctl x describe pod ${POD} -n dev

# Expected output chứa:
# mTLS mode: STRICT
# Incoming TLS: TLS
```
- [ ] 📸 Screenshot: Output `istioctl x describe pod` với mTLS STRICT

---

## Phase 4 — Apply Authorization Policy [NC2.3] (Tuần 2-3)

### 4.1 Apply deny-all trước
```bash
kubectl apply -f istio/authorization/deny-all.yaml
```

### 4.2 Test deny-all hoạt động
```bash
# Chọn 1 pod bất kỳ làm client
CLIENT_POD=$(kubectl get pods -n dev -l app=storefront-bff -o jsonpath='{.items[0].metadata.name}')

# Gọi tới product (khi chưa có allow policy) → phải trả 403
kubectl exec -n dev ${CLIENT_POD} -c storefront-bff -- \
  curl -v http://product.dev.svc.cluster.local:8080/ 2>&1 | grep "RBAC\|403\|forbidden"
# Expected: "RBAC: access denied" hoặc HTTP 403
```
- [ ] 📸 Screenshot: 403 response từ deny-all

### 4.3 Apply allow policies
```bash
kubectl apply -f istio/authorization/
# Apply tất cả files trong thư mục authorization/
```

### 4.4 Test allowed connections
```bash
# storefront-bff → product (ALLOWED)
CLIENT=$(kubectl get pods -n dev -l app=storefront-bff -o jsonpath='{.items[0].metadata.name}')

kubectl exec -n dev ${CLIENT} -c storefront-bff -- \
  curl -s -o /dev/null -w "%{http_code}" http://product.dev.svc.cluster.local:8080/product/products
# Expected: 200 (hoặc redirect, không phải 403)
```
- [ ] 📸 Screenshot: 200 OK response (allowed)

### 4.5 Test denied connections
```bash
# search → payment (DENIED — không có policy cho cặp này)
SEARCH_POD=$(kubectl get pods -n dev -l app=search -o jsonpath='{.items[0].metadata.name}')

kubectl exec -n dev ${SEARCH_POD} -c search -- \
  curl -v http://payment.dev.svc.cluster.local:8081/ 2>&1 | tail -20
# Expected: "RBAC: access denied" hoặc connection reset
```
- [ ] 📸 Screenshot: 403/denied response

---

## Phase 5 — Test Retry Policy [NC2.3] (Tuần 3)

### 5.1 Apply VirtualServices
```bash
kubectl apply -f istio/virtual-services/
```

### 5.2 Inject fault vào product service (50% error)
```bash
# Tạm thời inject fault để test retry
kubectl apply -f - <<'EOF'
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
EOF
```

### 5.3 Gửi requests và observe retry
```bash
# Gửi 30 requests từ storefront-bff
CLIENT=$(kubectl get pods -n dev -l app=storefront-bff -o jsonpath='{.items[0].metadata.name}')

for i in $(seq 1 30); do
  STATUS=$(kubectl exec -n dev ${CLIENT} -c storefront-bff -- \
    curl -s -o /dev/null -w "%{http_code}" \
    http://product.dev.svc.cluster.local:8080/product/products)
  echo "Request $i: HTTP $STATUS"
done
```
- [ ] Expected: phần lớn requests trả 200 nhờ retry (dù 50% fault inject)
- [ ] 📸 Screenshot: Retry evidence trong Kiali (Workload → product → Inbound Metrics → showing retries)

### 5.4 Dọn dẹp fault injection sau test
```bash
kubectl delete virtualservice product-fault-test -n dev
kubectl apply -f istio/virtual-services/product-vs.yaml  # restore retry config
```

---

## Phase 6 — Kiali Topology [NC2.2] (Tuần 3)

### 6.1 Expose Kiali
```bash
# NodePort (permanent)
kubectl patch svc kiali -n istio-system \
  -p '{"spec":{"type":"NodePort"}}'

# Hoặc port-forward (tạm thời)
kubectl port-forward svc/kiali -n istio-system 20001:20001 &
```
- [ ] Truy cập: `http://<GCP_IP>:<NodePort>` hoặc `http://localhost:20001`
- [ ] Login: admin/admin (hoặc không cần login nếu anonymous access)

### 6.2 Generate traffic để có topology data
```bash
# Chạy trong vòng 5-10 phút để Kiali có đủ data
CLIENT=$(kubectl get pods -n dev -l app=storefront-bff -o jsonpath='{.items[0].metadata.name}')

while true; do
  kubectl exec -n dev ${CLIENT} -c storefront-bff -- \
    curl -s http://product.dev.svc.cluster.local:8080/product/products > /dev/null
  kubectl exec -n dev ${CLIENT} -c storefront-bff -- \
    curl -s http://cart.dev.svc.cluster.local:8084/cart > /dev/null
  sleep 2
done
```

### 6.3 Screenshots bắt buộc trong báo cáo
- [ ] 📸 **Graph view**: Graph → Namespace=dev → Display=Service Graph (toàn bộ topology)
- [ ] 📸 **Security view**: Graph → Display → Security → thấy padlock icons (mTLS)
- [ ] 📸 **Traffic animation**: Graph → Display → Traffic Animation
- [ ] 📸 **Service detail**: click vào 1 service (e.g., product) → xem metrics
- [ ] 📸 **Retry evidence**: Workloads → product → Inbound Metrics (sau khi test retry)

### 6.4 Vẽ flow chart
- [ ] Vẽ service communication diagram dựa trên Kiali data
- [ ] Giải thích từng flow chính:
  - storefront-bff → product: người dùng xem sản phẩm
  - storefront-bff → cart: thêm vào giỏ hàng
  - order → payment: thanh toán đơn hàng
  - cart → product + promotion + tax: tính giá giỏ hàng

---

## Phase 7 — Báo Cáo Tổng Hợp (Tuần 3)

### 7.1 Thu thập từ team
- [ ] TV1: báo cáo GCP + K3s + ArgoCD + Infrastructure (text + screenshots)
- [ ] TV2: báo cáo CI/CD Pipelines (text + screenshots + demo logs)
- [ ] TV3: báo cáo GitOps Manifests (text + screenshots)

### 7.2 Cấu trúc báo cáo (format `.docx`)

**Tên file:** `<MSSV1>_<MSSV2>_<MSSV3>_<MSSV4>.docx` (MSSV sắp xếp tăng dần)

```
1. Giới thiệu
   1.1 Mô tả dự án YAS (Yet Another Shop)
   1.2 Mục tiêu đồ án 2 (CI/CD + GitOps + Service Mesh)
   1.3 Công nghệ sử dụng (K3s, Jenkins, ArgoCD, Kustomize, Istio, Kiali)

2. Kiến trúc hệ thống
   2.1 Sơ đồ tổng quan (Developer → GitHub → Jenkins → Docker Hub → GitOps → K8s)
   2.2 Mô tả từng thành phần
   2.3 GCP VM all-in-one architecture

3. Hạ tầng K8s (TV1)
   3.1 GCP VM provisioning (screenshots)
   3.2 K3s cluster setup (kubectl get nodes screenshot)
   3.3 ArgoCD installation + configuration (screenshots)
   3.4 Infrastructure services (Postgres, Kafka, Keycloak, ES, Redis)
   3.5 Jenkins Agent setup

4. CI/CD Pipelines (TV2)
   4.1 CI Pipeline: monorepo detection + Docker build + push (screenshots + logs)
   4.2 developer_build job: parameters + deploy + NodePort table (demo)
   4.3 Cleanup job: before/after screenshots
   4.4 Dev/Staging triggers: ArgoCD sync (screenshots)

5. GitOps Manifests (TV3)
   5.1 Kustomize structure (thư mục screenshot)
   5.2 Base manifests + ServiceAccount (code snippets)
   5.3 Environment overlays (dev/staging/developer-build)
   5.4 ArgoCD sync kết quả

6. Service Mesh — Istio (TV4)
   6.1 Cài đặt Istio (pods running screenshots)
   6.2 mTLS: PeerAuthentication + DestinationRule + verify (`istioctl describe`)
   6.3 Authorization Policy: deny-all + allow rules + test 200 vs 403 (curl logs)
   6.4 Retry Policy: VirtualService config + fault injection + retry evidence
   6.5 Kiali topology: 4+ screenshots + flow chart + giải thích

7. Kết luận
   7.1 Tổng kết kết quả đạt được
   7.2 Những khó khăn và bài học kinh nghiệm
   7.3 Hướng phát triển tiếp theo
```

- [ ] Draft báo cáo xong trong Tuần 3
- [ ] Review format + spelling
- [ ] Gửi team review lần cuối 1-2 ngày trước deadline

---

## Checklist Cuối Cùng

**Istio YAMLs:**
- [ ] `istio/peer-authentication.yaml` (STRICT mTLS)
- [ ] `istio/destination-rule-dev.yaml` + `destination-rule-staging.yaml`
- [ ] `istio/authorization/deny-all.yaml`
- [ ] `istio/authorization/allow-storefront-bff.yaml`
- [ ] `istio/authorization/allow-backoffice-bff.yaml`
- [ ] `istio/authorization/allow-order.yaml`
- [ ] `istio/authorization/allow-cart.yaml`
- [ ] `istio/authorization/allow-payment.yaml`
- [ ] `istio/authorization/allow-customer.yaml`
- [ ] `istio/authorization/allow-delivery.yaml`
- [ ] `istio/authorization/allow-recommendation.yaml`
- [ ] `istio/virtual-services/product-vs.yaml`
- [ ] `istio/virtual-services/order-vs.yaml`
- [ ] `istio/virtual-services/payment-vs.yaml`
- [ ] `istio/virtual-services/cart-vs.yaml`
- [ ] `istio/virtual-services/inventory-vs.yaml`

**Tests:**
- [ ] mTLS STRICT verified: `istioctl x describe pod` → mTLS mode: STRICT
- [ ] deny-all: curl từ any pod → 403 (trước khi apply allow policies)
- [ ] allow test: storefront-bff → product → 200 OK
- [ ] deny test: search → payment → 403 RBAC denied
- [ ] retry test: fault inject 50% → phần lớn requests vẫn success
- [ ] Kiali: 4+ screenshots (graph, security, traffic, service detail)

**Báo cáo:**
- [ ] `.docx` file hoàn chỉnh, tên đúng format MSSV
- [ ] Screenshots từ TV1, TV2, TV3, TV4
- [ ] Test logs (curl results, retry evidence)
- [ ] `istio/README.md` hướng dẫn setup từng bước
