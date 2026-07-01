# TV3 — GitOps Manifests (Kustomize) — repo gitops-manifest-k8s

> **Vai trò:** Tạo toàn bộ K8s manifests cho 19 services trong repo riêng `gitops-manifest-k8s`.
> **Làm hoàn toàn độc lập từ Tuần 1** — chỉ cần convention đã thống nhất.
> **Lưu ý quan trọng:** Infrastructure (Postgres, Kafka, Keycloak, ES, Redis) do TV1 deploy bằng Helm — TV3 KHÔNG viết infra manifests.

---

## Cấu Trúc Repo gitops-manifest-k8s

```
gitops-manifest-k8s/
├── base/
│   ├── kustomization.yaml          ← list tất cả 19 services
│   ├── media/
│   │   ├── deployment.yaml
│   │   ├── service.yaml
│   │   └── serviceaccount.yaml     ← BẮT BUỘC cho Istio AuthorizationPolicy
│   ├── product/ (tương tự)
│   ├── ... (19 services)
│   └── _common/
│       └── namespace-default.yaml  ← không cần nếu overlay override namespace
├── environments/
│   ├── dev/
│   │   └── kustomization.yaml
│   ├── staging/
│   │   └── kustomization.yaml
│   └── developer-build/
│       ├── kustomization.yaml
│       └── patches/
│           └── nodeport-patch.yaml
└── README.md
```

---

## Danh Sách Services + Ports Thực Tế

> Ports tra từ `application.properties` trong từng service của repo yas:

| # | Service | App Port | Ghi chú |
|:-:|---------|:--------:|---------|
| 1 | product | 8080 | |
| 2 | payment | 8081 | |
| 3 | media | 8083 | |
| 4 | cart | 8084 | |
| 5 | order | 8085 | |
| 6 | location | 8086 | |
| 7 | backoffice-bff | 8087 | Spring Boot, YAML config |
| 8 | storefront-bff | 8087 | Spring Boot, YAML config |
| 9 | customer | 8088 | |
| 10 | rating | 8089 | |
| 11 | inventory | 8090 | |
| 12 | tax | 8091 | |
| 13 | promotion | 8092 | conflict port với search/webhook |
| 14 | search | 8092 | conflict port, OK vì khác pod |
| 15 | webhook | 8092 | conflict port, OK vì khác pod |
| 16 | payment-paypal | 8093 | |
| 17 | sampledata | 8094 | |
| 18 | recommendation | 8095 | |
| 19 | delivery | 8080 | default Spring Boot port |

---

## Phase 1 — Base Manifests (Tuần 1)

### 1.1 Tạo repo gitops-manifest-k8s trên GitHub
```bash
# Tạo repo mới trên GitHub: gitops-manifest-k8s
git init gitops-manifest-k8s
cd gitops-manifest-k8s
mkdir -p base/{media,product,order,inventory,payment,promotion,rating,delivery,\
sampledata,recommendation,customer,location,cart,tax,search,webhook,\
backoffice-bff,storefront-bff,payment-paypal}
mkdir -p environments/{dev,staging,developer-build/patches}
```

### 1.2 Template deployment.yaml (dùng cho tất cả 19 services)

**base/media/deployment.yaml:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: media
  labels:
    app: media
spec:
  replicas: 1
  selector:
    matchLabels:
      app: media
  template:
    metadata:
      labels:
        app: media
    spec:
      serviceAccountName: media      # BẮT BUỘC — TV4 dùng cho AuthorizationPolicy
      imagePullSecrets:
        - name: dockerhub-secret
      containers:
        - name: media
          image: bingsu1103/media:latest
          ports:
            - containerPort: 8083
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "k8s"
          resources:
            requests:
              memory: "256Mi"
              cpu: "100m"
            limits:
              memory: "512Mi"
              cpu: "500m"
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 8083
            initialDelaySeconds: 30
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 8083
            initialDelaySeconds: 60
            periodSeconds: 30
```

**base/media/service.yaml:**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: media
  labels:
    app: media
spec:
  selector:
    app: media
  ports:
    - port: 8083
      targetPort: 8083
      protocol: TCP
  type: ClusterIP
```

**base/media/serviceaccount.yaml:**
```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: media
  labels:
    app: media
```

> Tạo 3 files này cho **tất cả 19 services**, chỉ thay tên service và port.

### 1.3 Port mapping cho từng service

Thay `name: media`, `containerPort: 8083`, `port: 8083` theo bảng:

| Service | name | containerPort |
|---------|------|:-------------:|
| media | media | 8083 |
| product | product | 8080 |
| order | order | 8085 |
| inventory | inventory | 8090 |
| payment | payment | 8081 |
| promotion | promotion | 8092 |
| rating | rating | 8089 |
| delivery | delivery | 8080 |
| sampledata | sampledata | 8094 |
| recommendation | recommendation | 8095 |
| customer | customer | 8088 |
| location | location | 8086 |
| cart | cart | 8084 |
| tax | tax | 8091 |
| search | search | 8092 |
| webhook | webhook | 8092 |
| backoffice-bff | backoffice-bff | 8087 |
| storefront-bff | storefront-bff | 8087 |
| payment-paypal | payment-paypal | 8093 |

- [ ] Tạo `deployment.yaml`, `service.yaml`, `serviceaccount.yaml` cho **19 services**
- [ ] 📸 Screenshot: Cấu trúc thư mục base/

### 1.4 base/kustomization.yaml
```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  # media
  - media/deployment.yaml
  - media/service.yaml
  - media/serviceaccount.yaml
  # product
  - product/deployment.yaml
  - product/service.yaml
  - product/serviceaccount.yaml
  # order
  - order/deployment.yaml
  - order/service.yaml
  - order/serviceaccount.yaml
  # inventory
  - inventory/deployment.yaml
  - inventory/service.yaml
  - inventory/serviceaccount.yaml
  # payment
  - payment/deployment.yaml
  - payment/service.yaml
  - payment/serviceaccount.yaml
  # promotion
  - promotion/deployment.yaml
  - promotion/service.yaml
  - promotion/serviceaccount.yaml
  # rating
  - rating/deployment.yaml
  - rating/service.yaml
  - rating/serviceaccount.yaml
  # delivery
  - delivery/deployment.yaml
  - delivery/service.yaml
  - delivery/serviceaccount.yaml
  # sampledata
  - sampledata/deployment.yaml
  - sampledata/service.yaml
  - sampledata/serviceaccount.yaml
  # recommendation
  - recommendation/deployment.yaml
  - recommendation/service.yaml
  - recommendation/serviceaccount.yaml
  # customer
  - customer/deployment.yaml
  - customer/service.yaml
  - customer/serviceaccount.yaml
  # location
  - location/deployment.yaml
  - location/service.yaml
  - location/serviceaccount.yaml
  # cart
  - cart/deployment.yaml
  - cart/service.yaml
  - cart/serviceaccount.yaml
  # tax
  - tax/deployment.yaml
  - tax/service.yaml
  - tax/serviceaccount.yaml
  # search
  - search/deployment.yaml
  - search/service.yaml
  - search/serviceaccount.yaml
  # webhook
  - webhook/deployment.yaml
  - webhook/service.yaml
  - webhook/serviceaccount.yaml
  # backoffice-bff
  - backoffice-bff/deployment.yaml
  - backoffice-bff/service.yaml
  - backoffice-bff/serviceaccount.yaml
  # storefront-bff
  - storefront-bff/deployment.yaml
  - storefront-bff/service.yaml
  - storefront-bff/serviceaccount.yaml
  # payment-paypal
  - payment-paypal/deployment.yaml
  - payment-paypal/service.yaml
  - payment-paypal/serviceaccount.yaml
```

- [ ] Tạo `base/kustomization.yaml` với 57 resources (19 × 3)

---

## Phase 2 — Environment Overlays (Tuần 1-2)

### 2.1 environments/dev/kustomization.yaml
```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: dev
resources:
  - ../../base
commonLabels:
  environment: dev
images:
  - name: bingsu1103/media
    newTag: latest
  - name: bingsu1103/product
    newTag: latest
  - name: bingsu1103/order
    newTag: latest
  - name: bingsu1103/inventory
    newTag: latest
  - name: bingsu1103/payment
    newTag: latest
  - name: bingsu1103/promotion
    newTag: latest
  - name: bingsu1103/rating
    newTag: latest
  - name: bingsu1103/delivery
    newTag: latest
  - name: bingsu1103/sampledata
    newTag: latest
  - name: bingsu1103/recommendation
    newTag: latest
  - name: bingsu1103/customer
    newTag: latest
  - name: bingsu1103/location
    newTag: latest
  - name: bingsu1103/cart
    newTag: latest
  - name: bingsu1103/tax
    newTag: latest
  - name: bingsu1103/search
    newTag: latest
  - name: bingsu1103/webhook
    newTag: latest
  - name: bingsu1103/backoffice-bff
    newTag: latest
  - name: bingsu1103/storefront-bff
    newTag: latest
  - name: bingsu1103/payment-paypal
    newTag: latest
```
> Jenkins sẽ tự động update `newTag` bằng lệnh `kustomize edit set image bingsu1103/<svc>=bingsu1103/<svc>:<commit-id>`

- [ ] Tạo `environments/dev/kustomization.yaml`

### 2.2 environments/staging/kustomization.yaml
```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: staging
resources:
  - ../../base
commonLabels:
  environment: staging
# Staging dùng replicas: 1 (tiết kiệm RAM) — có thể tăng sau
patches:
  - target:
      kind: Deployment
      name: ".*"
    patch: |-
      - op: replace
        path: /spec/replicas
        value: 1
images:
  - name: bingsu1103/media
    newTag: v1.0.0
  - name: bingsu1103/product
    newTag: v1.0.0
  # ... (19 images, Jenkins update khi có tag v*)
```
- [ ] Tạo `environments/staging/kustomization.yaml` với đủ 19 images

### 2.3 environments/developer-build/kustomization.yaml
```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: developer-build
resources:
  - ../../base
commonLabels:
  environment: developer-build
patches:
  - path: patches/nodeport-patch.yaml
    target:
      kind: Service
      name: ".*"
images:
  - name: bingsu1103/media
    newTag: latest
  - name: bingsu1103/product
    newTag: latest
  # ... (19 images, deploy-developer-build.sh sẽ override)
```

### 2.4 environments/developer-build/patches/nodeport-patch.yaml

> Patch này thay type của TẤT CẢ Service thành NodePort, K8s tự assign port.

```yaml
- op: replace
  path: /spec/type
  value: NodePort
```

- [ ] Tạo `environments/developer-build/kustomization.yaml`
- [ ] Tạo `environments/developer-build/patches/nodeport-patch.yaml`

---

## Phase 3 — Validation (Tuần 2)

### 3.1 Dry-run kustomize build
```bash
git clone https://github.com/<org>/gitops-manifest-k8s.git
cd gitops-manifest-k8s

# Validate dev
kubectl kustomize environments/dev/ > /tmp/dev-output.yaml
echo "Dev: $? (0 = success)"
grep "kind: Deployment" /tmp/dev-output.yaml | wc -l  # phải = 19
grep "namespace: dev" /tmp/dev-output.yaml | wc -l     # phải = 57

# Validate staging
kubectl kustomize environments/staging/ > /tmp/staging-output.yaml
echo "Staging: $? (0 = success)"

# Validate developer-build
kubectl kustomize environments/developer-build/ > /tmp/devbuild-output.yaml
echo "Developer-build: $? (0 = success)"
grep "type: NodePort" /tmp/devbuild-output.yaml | wc -l  # phải = 19
```
- [ ] Cả 3 môi trường build thành công (exit code 0)
- [ ] developer-build: tất cả Services có `type: NodePort`
- [ ] 📸 Screenshot: `kustomize build` output cho mỗi environment

### 3.2 Kiểm tra ServiceAccount
```bash
grep "kind: ServiceAccount" /tmp/dev-output.yaml | wc -l  # phải = 19
grep "serviceAccountName:" /tmp/dev-output.yaml | wc -l    # phải = 19
```
- [ ] Verify đủ 19 ServiceAccounts
- [ ] Verify mỗi Deployment có `serviceAccountName` tham chiếu đúng

### 3.3 Test với ArgoCD (phối hợp TV1)
- [ ] Push lên repo → ArgoCD yas-dev detect trong 30 giây
- [ ] Sync app → `kubectl get pods -n dev` → pods starting (có thể CrashLoop nếu chưa có infra config)
- [ ] 📸 Screenshot: ArgoCD sync + pods list

### 3.4 Fix common issues
Nếu pods CrashLoopBackOff vì thiếu config infra:
- Thêm env vars kết nối database vào deployment.yaml:
```yaml
env:
  - name: SPRING_PROFILES_ACTIVE
    value: "k8s"
  - name: SPRING_DATASOURCE_URL
    value: "jdbc:postgresql://postgres-postgresql.dev.svc.cluster.local:5432/<db-name>"
  - name: SPRING_DATASOURCE_USERNAME
    value: "postgres"
  - name: SPRING_DATASOURCE_PASSWORD
    value: "password"
  - name: SPRING_KAFKA_BOOTSTRAP_SERVERS
    value: "kafka.dev.svc.cluster.local:9092"
```
- [ ] Thêm env vars cần thiết cho từng service
- [ ] developer-build: các service kết nối tới infra trong namespace `dev`:
  - `postgres-postgresql.dev.svc.cluster.local:5432`
  - `kafka.dev.svc.cluster.local:9092`
  - `keycloak.dev.svc.cluster.local:80`

---

## Phase 4 — Documentation (Tuần 3)

### README.md cho gitops-manifest-k8s
```markdown
# gitops-manifest-k8s

GitOps manifests cho dự án YAS (Yet Another Shop).
Được quản lý bằng Kustomize, ArgoCD watch và sync.

## Cấu trúc
- `base/`: Base manifests cho 19 microservices (Deployment + Service + ServiceAccount)
- `environments/dev/`: Override namespace=dev, image tags (auto-update bởi Jenkins CI)
- `environments/staging/`: Override namespace=staging (update khi có tag v*)
- `environments/developer-build/`: Override namespace=developer-build, type=NodePort

## Service Ports
[Bảng port ở trên]

## Cách update image tag (thủ công)
cd environments/dev
kustomize edit set image bingsu1103/product=bingsu1103/product:abc1234

## Cách thêm service mới
1. Tạo thư mục base/<service-name>/
2. Copy và chỉnh sửa deployment.yaml, service.yaml, serviceaccount.yaml
3. Thêm vào base/kustomization.yaml
4. Thêm image entry vào 3 environment kustomization.yaml
```

- [ ] Viết README đầy đủ
- [ ] Gửi screenshots + nội dung báo cáo phần GitOps Manifests cho TV4

---

## Checklist Cuối Cùng

- [ ] 19 base deployments (port đúng theo bảng)
- [ ] 19 base services (ClusterIP)
- [ ] **19 ServiceAccounts** (tên = service name) — TV4 phụ thuộc
- [ ] base/kustomization.yaml: 57 resources
- [ ] environments/dev/kustomization.yaml: namespace=dev, 19 images
- [ ] environments/staging/kustomization.yaml: namespace=staging, 19 images
- [ ] environments/developer-build/kustomization.yaml: namespace=developer-build
- [ ] environments/developer-build/patches/nodeport-patch.yaml: type=NodePort
- [ ] `kustomize build environments/dev/` → success, 19 deployments, namespace=dev
- [ ] `kustomize build environments/developer-build/` → 19 services có type=NodePort
- [ ] ArgoCD detect + sync test thành công
- [ ] Báo cáo + screenshots gửi TV4
