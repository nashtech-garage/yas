# 👤 TV3 — GitOps Manifests (Kustomize)

> **Vai trò:** Tạo toàn bộ K8s manifests trong repo `gitops-manifest-k8s`.  
> **Ưu tiên:** Làm song song từ Tuần 1 — chỉ cần biết danh sách services + image convention.

---

## Danh sách 19 Microservices

```
media, product, order, inventory, payment, promotion, rating, delivery,
sampledata, recommendation, customer, location, cart, tax, search, webhook,
backoffice-bff, storefront-bff, payment-paypal
```

---

## Phase 1 — Base Manifests (Tuần 1)

### 1.1 Tạo cấu trúc thư mục
```
gitops-manifest-k8s/
├── base/
│   ├── kustomization.yaml
│   ├── media/
│   │   ├── deployment.yaml
│   │   └── service.yaml
│   ├── product/
│   │   ├── deployment.yaml
│   │   └── service.yaml
│   └── ... (19 services)
├── environments/
│   ├── dev/
│   │   └── kustomization.yaml
│   ├── staging/
│   │   └── kustomization.yaml
│   └── developer-build/
│       └── kustomization.yaml
└── infrastructure/
    ├── keycloak/
    ├── postgres/
    ├── kafka/
    └── elasticsearch/
```
- [ ] Tạo toàn bộ cấu trúc thư mục

### 1.2 Viết base Deployment cho mỗi service (x19)
Template cho mỗi service (ví dụ `media`):
```yaml
# base/media/deployment.yaml
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
      containers:
      - name: media
        image: bingsu1103/media:latest
        ports:
        - containerPort: 8080  # Thay đổi theo port thực tế
        resources:
          requests:
            memory: "256Mi"
            cpu: "100m"
          limits:
            memory: "512Mi"
            cpu: "500m"
      imagePullSecrets:
      - name: dockerhub-secret
```

Danh sách service và port cần xác nhận (tra source code):

| # | Service | Port |
|:-:|---------|:----:|
| 1 | media | 8080 |
| 2 | product | 8080 |
| 3 | order | 8080 |
| 4 | inventory | 8080 |
| 5 | payment | 8080 |
| 6 | promotion | 8080 |
| 7 | rating | 8080 |
| 8 | delivery | 8080 |
| 9 | sampledata | 8080 |
| 10 | recommendation | 8080 |
| 11 | customer | 8080 |
| 12 | location | 8080 |
| 13 | cart | 8080 |
| 14 | tax | 8080 |
| 15 | search | 8080 |
| 16 | webhook | 8080 |
| 17 | backoffice-bff | 8080 |
| 18 | storefront-bff | 8080 |
| 19 | payment-paypal | 8080 |

- [ ] Tra source code xác nhận port từng service
- [ ] Viết 19 file deployment.yaml
- [ ] 📸 Screenshot: Cấu trúc thư mục base/

### 1.3 Viết base Service cho mỗi service (x19)
```yaml
# base/media/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: media
spec:
  selector:
    app: media
  ports:
  - port: 8080
    targetPort: 8080
  type: ClusterIP
```
- [ ] Viết 19 file service.yaml

### 1.4 Viết base kustomization.yaml
```yaml
# base/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
- media/deployment.yaml
- media/service.yaml
- product/deployment.yaml
- product/service.yaml
# ... tất cả 19 services (38 resources)
```
- [ ] Liệt kê đầy đủ 38 resources (19 deployment + 19 service)

---

## Phase 2 — Environment Overlays (Tuần 1-2)

### 2.1 Dev overlay
```yaml
# environments/dev/kustomization.yaml
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
# ... 19 images (TV2 sẽ auto-update tags)
```
- [ ] Viết kustomization.yaml cho dev
- [ ] Set namespace: `dev`
- [ ] Labels: `environment: dev`
- [ ] Image tags: default `latest` (Jenkins sẽ update)

### 2.2 Staging overlay
```yaml
# environments/staging/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: staging
resources:
- ../../base
commonLabels:
  environment: staging
# Có thể tăng replicas
patches:
- target:
    kind: Deployment
  patch: |-
    - op: replace
      path: /spec/replicas
      value: 2
images:
- name: bingsu1103/media
  newTag: v1.0.0
# ... 19 images
```
- [ ] Viết kustomization.yaml cho staging
- [ ] Namespace: `staging`, labels: `environment: staging`
- [ ] Replicas có thể > 1

### 2.3 Developer-build overlay
```yaml
# environments/developer-build/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
namespace: developer-build
resources:
- ../../base
commonLabels:
  environment: developer-build
patches:
- target:
    kind: Service
  patch: |-
    - op: replace
      path: /spec/type
      value: NodePort
images:
- name: bingsu1103/media
  newTag: latest
# ... 19 images
```
- [ ] Viết kustomization.yaml cho developer-build
- [ ] Service type patch: **NodePort** cho tất cả services
- [ ] Image tags override riêng

---

## Phase 3 — Infrastructure Manifests (Tuần 2)

### 3.1 Keycloak
- [ ] `infrastructure/keycloak/deployment.yaml` — Deployment + volume
- [ ] `infrastructure/keycloak/service.yaml` — Service (ClusterIP/NodePort)
- [ ] `infrastructure/keycloak/configmap.yaml` — Realm config
- [ ] 📸 Screenshot: Keycloak pod running

### 3.2 PostgreSQL
- [ ] `infrastructure/postgres/statefulset.yaml` — StatefulSet + PVC
- [ ] `infrastructure/postgres/service.yaml`
- [ ] `infrastructure/postgres/configmap.yaml` — init SQL script
- [ ] 📸 Screenshot: PostgreSQL pod running

### 3.3 Kafka + Zookeeper
- [ ] `infrastructure/kafka/zookeeper.yaml`
- [ ] `infrastructure/kafka/kafka.yaml`
- [ ] `infrastructure/kafka/service.yaml`
- [ ] Hoặc dùng Helm chart reference trong kustomization

### 3.4 Elasticsearch (optional)
- [ ] `infrastructure/elasticsearch/deployment.yaml`
- [ ] `infrastructure/elasticsearch/service.yaml`

### 3.5 Redis
- [ ] `infrastructure/redis/deployment.yaml`
- [ ] `infrastructure/redis/service.yaml`

### 3.6 Infrastructure kustomization
```yaml
# infrastructure/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
- keycloak/
- postgres/
- kafka/
- elasticsearch/
- redis/
```

---

## Phase 4 — Validation (Tuần 2-3)

### 4.1 Dry-run builds
```bash
kubectl kustomize environments/dev/
kubectl kustomize environments/staging/
kubectl kustomize environments/developer-build/
```
- [ ] Verify output YAML hợp lệ, không có lỗi
- [ ] Kiểm tra namespace đúng, labels đúng, image tags đúng
- [ ] 📸 Screenshot: `kustomize build` output (mỗi environment)

### 4.2 Apply test
- [ ] Push lên repo → ArgoCD detect (phối hợp TV1)
- [ ] Verify pods deploy thành công
- [ ] Fix issues nếu có (image pull errors, port conflicts, etc.)
- [ ] 📸 Screenshot: ArgoCD sync + pods running

### 4.3 Validate developer-build NodePort
- [ ] Kiểm tra services trong `developer-build` có type NodePort
- [ ] Verify truy cập được qua `<IP>:<NodePort>`

---

## Phase 5 — Documentation (Tuần 3)

- [ ] Viết README trong gitops-manifest-k8s repo:
  1. Cấu trúc thư mục Kustomize
  2. Giải thích base vs overlays
  3. Hướng dẫn thêm service mới
  4. Hướng dẫn update image tag
- [ ] Viết báo cáo phần GitOps Manifests
- [ ] Gửi text + screenshots cho TV4 tổng hợp

---

## ✅ Checklist Cuối Cùng

- [ ] 19 base deployments + 19 base services
- [ ] base/kustomization.yaml liệt kê đủ 38 resources
- [ ] environments/dev/kustomization.yaml (namespace dev, labels)
- [ ] environments/staging/kustomization.yaml (namespace staging)
- [ ] environments/developer-build/kustomization.yaml (NodePort patch)
- [ ] Infrastructure: keycloak, postgres, kafka, elasticsearch, redis
- [ ] Dry-run builds pass cho cả 3 environments
- [ ] ArgoCD detect + sync thành công
- [ ] README + báo cáo gửi TV4
