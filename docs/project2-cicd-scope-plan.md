# Project 2 - Checklist Điều Chỉnh Phạm Vi CI/CD Cho YAS

Tài liệu này dùng để theo dõi tiến độ điều chỉnh CI/CD, GitOps và Service Mesh theo phạm vi đã chốt cho đồ án DevOps.

## Trạng Thái Cập Nhật 2026-07-02

### Tóm tắt hiện tại

- Repo `yas`: branch `feature/yas-cicd-scope-plan`, working tree sạch tại thời điểm kiểm tra.
- Repo `gitops-manifest-k8s`: branch `main`, sạch và đã push commit `6cb2dab fix: route storefront through bff ingress`.
- ArgoCD app `yas-dev`: `Synced/Healthy`.
- Namespace `dev`: 15 workload trong scope đều `2/2 Running`.
- GitOps render:
  - `environments/dev`: 15 Deployment, không render `delivery`, `recommendation`, `webhook`, `payment-paypal`.
  - `environments/staging`: 15 Deployment, không render `delivery`, `recommendation`, `webhook`, `payment-paypal`.
- `storefront-ui` truy cập được qua Istio ingress port-forward: `GET / -> 200 OK`.
- `sampledata` seed chạy được: `POST /api/sampledata/storefront/sampledata -> 200 OK`.
- Redis đã ổn định sau khi xử lý lỗi replication/mTLS: `redis-master` và 3 `redis-replicas` đều `2/2 Running`.

### Thay đổi GitOps đã hoàn tất

- Đã GitOps hóa `Gateway`/`VirtualService` cho frontend:
  - `Gateway yas-gateway`: `hosts: ["*"]`, không phụ thuộc DNS cũ.
  - `VirtualService yas-ingress-vs`: route `/api`, `/authentication`, `/oauth2`, `/login/oauth2`, `/logout` về `storefront-bff:8087`; route còn lại về `storefront-ui:3000`.
- Đã thêm ConfigMap `storefront-bff-config` để profile `k8s` của `storefront-bff` route `/api/<service>/**` tới các service nội bộ đúng port.
- `storefront-bff` đã mount ConfigMap qua `SPRING_CONFIG_ADDITIONAL_LOCATION=optional:file:/config/`.

### Blocker còn lại cho demo order flow frontend

- `GET /api/payment/storefront/payment-providers -> 403 RBAC: access denied`.
  - Nguyên nhân: Istio `AuthorizationPolicy allow-to-payment` hiện mới cho `order` gọi `payment`, chưa cho `storefront-bff` gọi `payment`.
  - Cần sửa GitOps policy để thêm principal `cluster.local/ns/dev/sa/storefront-bff` vào quyền gọi `payment`.
- `GET /api/product/storefront/categories -> 500` với lỗi `CircuitBreaker 'restCircuitBreaker' is OPEN`.
  - `sampledata` đã seed thành công, nhưng product API đang cần điều tra thêm sau khi circuit breaker mở.
- Login Keycloak qua browser chưa hoàn tất.
  - Nếu chưa login, cart/profile trả `403/401` là đúng hành vi bảo mật.
  - Muốn demo `add cart -> checkout -> order`, cần route/host cho Keycloak để browser hoàn thành OAuth flow.
- `COD` trong frontend hiện chỉ báo `COD payment feature is under construction`.
  - Nếu cần demo thanh toán thành công thật, phải đưa `payment-paypal` vào scope demo riêng và cấu hình đầy đủ.

## 0. Phạm Vi Đã Chốt

### 0.1 Service giữ lại

Phạm vi chính thức: **14 service chính trong file PDF + `payment`**. `sampledata` chỉ cần chạy một lần để nạp dữ liệu mẫu, sau đó có thể scale về 0 nếu dữ liệu đã có.

| Nhóm | Service | Ghi chú |
|------|---------|---------|
| Core e-commerce | `product` | Trung tâm dữ liệu sản phẩm |
| Core e-commerce | `cart` | Giỏ hàng, phục vụ flow mua hàng |
| Core e-commerce | `order` | Đơn hàng, trung tâm demo order flow và retry |
| Core e-commerce | `customer` | Thông tin khách hàng |
| Core e-commerce | `inventory` | Kho hàng, order flow có phụ thuộc |
| Core e-commerce | `tax` | Thuế, dùng để demo VirtualService retry |
| Core e-commerce | `payment` | Bổ sung để order flow đầy đủ theo `order -> cart/payment/inventory/tax` |
| Supporting | `media` | Hình ảnh sản phẩm |
| Supporting | `search` | Tìm kiếm, dùng để demo AuthorizationPolicy |
| Frontend/BFF | `storefront-bff` | BFF cho giao diện người dùng |
| Frontend/BFF | `storefront-ui` | Giao diện cửa hàng để demo cho giảng viên |
| Backoffice/BFF | `backoffice-bff` | BFF cho quản trị |
| Backoffice/BFF | `backoffice-ui` | Giao diện quản trị |
| Tooling | `swagger-ui` | API documentation |
| Data seed | `sampledata` | Chạy 1 lần để nạp dữ liệu mẫu |

### 0.2 Service loại khỏi phạm vi nếu không có kịch bản demo riêng

| Service | Lý do loại |
|---------|------------|
| `delivery` | Không nằm trong PDF và không cần cho demo order flow tối thiểu |
| `recommendation` | Không nằm trong PDF, không cần cho retry/authz demo |
| `webhook` | Không nằm trong PDF, chỉ giữ nếu có demo event/webhook riêng |
| `payment-paypal` | Không bắt buộc; chỉ giữ nếu demo thanh toán PayPal |

### 0.3 Service cần cân nhắc nếu phát sinh dependency thực tế

| Service | Khi nào giữ |
|---------|-------------|
| `promotion` | Nếu checkout/order flow đang gọi promotion hoặc frontend cần discount |
| `location` | Nếu tax/customer/inventory cần location data để chạy đúng |
| `rating` | Nếu product/storefront cần hiển thị rating trong demo |

Nếu giữ thêm các service trên, phải cập nhật đồng bộ trong Jenkinsfile, GitOps manifest, Istio DestinationRule và AuthorizationPolicy.

### 0.4 Mapping service dùng cho CI/CD và GitOps

| Service scope | Source path | Dockerfile | Docker image | K8s Deployment/Service/SA | App port | Health/manual check |
|---------------|-------------|------------|--------------|----------------------------|----------|---------------------|
| `product` | `product/` | `product/Dockerfile` | `bingsu1103/product:<tag>` | `product` | `8080` | `/product/actuator/health` |
| `cart` | `cart/` | `cart/Dockerfile` | `bingsu1103/cart:<tag>` | `cart` | `8084` | `/cart/actuator/health` |
| `order` | `order/` | `order/Dockerfile` | `bingsu1103/order:<tag>` | `order` | `8085` | `/order/actuator/health` |
| `customer` | `customer/` | `customer/Dockerfile` | `bingsu1103/customer:<tag>` | `customer` | `8088` | `/customer/actuator/health` |
| `inventory` | `inventory/` | `inventory/Dockerfile` | `bingsu1103/inventory:<tag>` | `inventory` | `8090` | `/inventory/actuator/health` |
| `tax` | `tax/` | `tax/Dockerfile` | `bingsu1103/tax:<tag>` | `tax` | `8091` | `/tax/actuator/health` |
| `payment` | `payment/` | `payment/Dockerfile` | `bingsu1103/payment:<tag>` | `payment` | `8081` | `/payment/actuator/health` |
| `media` | `media/` | `media/Dockerfile` | `bingsu1103/media:<tag>` | `media` | `8083` | `/actuator/health` |
| `search` | `search/` | `search/Dockerfile` | `bingsu1103/search:<tag>` | `search` | `8092` | `/search/actuator/health` |
| `storefront-bff` | `storefront-bff/` | `storefront-bff/Dockerfile` | `bingsu1103/storefront-bff:<tag>` | `storefront-bff` | `8087` | `/actuator/health` |
| `storefront-ui` | `storefront/` | `storefront/Dockerfile` | `bingsu1103/storefront:<tag>` | `storefront-ui` | Theo manifest | Truy cập frontend |
| `backoffice-bff` | `backoffice-bff/` | `backoffice-bff/Dockerfile` | `bingsu1103/backoffice-bff:<tag>` | `backoffice-bff` | `8087` | `/actuator/health` |
| `backoffice-ui` | `backoffice/` | `backoffice/Dockerfile` | `bingsu1103/backoffice:<tag>` | `backoffice-ui` | Theo manifest | Truy cập frontend |
| `swagger-ui` | `k8s/charts/swagger-ui/` | Không build image riêng | `swaggerapi/swagger-ui` | `swagger-ui` | `8080` | Truy cập `/swagger-ui` |
| `sampledata` | `sampledata/` | `sampledata/Dockerfile` | `bingsu1103/sampledata:<tag>` | `sampledata` | `8094` | `/sampledata/actuator/health`; chạy seed 1 lần |

Ghi chú:

- `storefront-ui` và `backoffice-ui` là tên workload trong Kubernetes, nhưng source folder và Docker image tương ứng là `storefront` và `backoffice`.
- `swagger-ui` dùng image public, vì vậy CI không build/push image `bingsu1103/swagger-ui` trừ khi nhóm tự bổ sung Dockerfile riêng.
- `sampledata` nên có manifest để chạy seed data, nhưng sau khi dữ liệu mẫu đã nạp thành công có thể scale về 0 để tiết kiệm tài nguyên.

---

## Phase 1 - Chốt Phạm Vi Và Mapping Service

Mục tiêu: có bảng mapping chính xác trước khi sửa CI/CD và GitOps.

### Checklist

- [x] Chốt scope: 14 service trong PDF + `payment`.
- [x] Xác định `sampledata` chỉ cần chạy 1 lần.
- [x] Lập bảng mapping service:
  - [x] Source path
  - [x] Dockerfile path
  - [x] Docker image name
  - [x] Kubernetes Deployment name
  - [x] Kubernetes Service name
  - [x] ServiceAccount name
  - [x] App port
  - [x] Health endpoint
- [x] Đối chiếu mapping với source repo `yas`.
- [x] Đối chiếu mapping với repo `gitops-manifest-k8s`.
- [x] Xác nhận phạm vi hiện tại: chưa giữ thêm `promotion`, `location`, `rating` trong GitOps dev/staging.

### Manual Check

Chạy trong repo `yas`:

```bash
git status --short --branch
find . -maxdepth 2 -name Dockerfile | sort
find k8s/charts -maxdepth 2 -type f -name Chart.yaml | sort
```

Kiểm tra port của backend service:

```bash
for svc in product cart order customer inventory tax payment media search storefront-bff backoffice-bff sampledata; do
  echo "===== $svc ====="
  grep -nE "server.port|server.servlet.context-path" "$svc/src/main/resources/application.properties" || true
done
```

Kiểm tra trên cluster nếu đã deploy:

```bash
kubectl get deploy,svc,sa -n dev
kubectl get deploy -n dev -o jsonpath='{range .items[*]}{.metadata.name}{" -> sa="}{.spec.template.spec.serviceAccountName}{" labels="}{.spec.template.metadata.labels}{"\n"}{end}'
```

### Done Criteria

- [x] Có bảng mapping đầy đủ cho 15 service trong scope.
- [x] Biết service nào thiếu Dockerfile, chart, manifest hoặc health endpoint.
- [x] Danh sách service bị loại đã phản ánh trong CI/GitOps hiện tại.

---

## Phase 2 - Điều Chỉnh CI Build & Push

Mục tiêu: pipeline CI chỉ build/test/push image cho service đúng phạm vi.

### Checklist

- [x] Cập nhật `Jenkinsfile.ci`.
- [x] Danh sách Maven build/test chỉ gồm backend service trong scope.
- [x] Danh sách Docker build/push gồm đúng service trong scope.
- [x] Thêm/giữ image cho:
  - [x] `product`
  - [x] `cart`
  - [x] `order`
  - [x] `customer`
  - [x] `inventory`
  - [x] `tax`
  - [x] `payment`
  - [x] `media`
  - [x] `search`
  - [x] `storefront-bff`
  - [x] `storefront-ui` qua image `bingsu1103/storefront:<tag>`
  - [x] `backoffice-bff`
  - [x] `backoffice-ui` qua image `bingsu1103/backoffice:<tag>`
  - [x] `swagger-ui` giữ qua image public `swaggerapi/swagger-ui`
  - [x] `sampledata`
- [x] Loại khỏi CI nếu không demo:
  - [x] `delivery`
  - [x] `recommendation`
  - [x] `webhook`
  - [x] `payment-paypal`
- [x] Đảm bảo tag image:
  - [x] Branch/commit: `<short-commit-id>`
  - [x] Main/default: `latest` hoặc `main`
  - [x] Release staging: `vX.Y.Z`

### Manual Check

Kiểm tra danh sách service trong Jenkinsfile:

```bash
grep -nE "delivery|recommendation|webhook|payment-paypal|payment|storefront|backoffice|swagger-ui" Jenkinsfile.ci
```

Kiểm tra Dockerfile:

```bash
for svc in product cart order customer inventory tax payment media search storefront-bff backoffice-bff sampledata storefront backoffice; do
  test -f "$svc/Dockerfile" && echo "OK $svc" || echo "MISSING $svc/Dockerfile"
done
```

Sau khi chạy Jenkins CI:

```bash
docker pull bingsu1103/product:latest
docker pull bingsu1103/order:latest
docker pull bingsu1103/payment:latest
```

### Done Criteria

- [x] Cấu hình CI không build service ngoài scope.
- [ ] Chưa có bằng chứng mới trong tài liệu này rằng Jenkins CI full run đã xanh toàn bộ sau mọi thay đổi.
- [x] Cluster `dev` đang dùng image/tag đúng yêu cầu `bingsu1103/<service>:latest` cho service trong scope; UI image mirror đã có `bingsu1103/storefront:latest` và `bingsu1103/backoffice:latest`.

---

## Phase 3 - Điều Chỉnh Job `developer_build`

Mục tiêu: developer có thể chọn branch cho service cần test; service còn lại dùng image default.

### Checklist

- [x] Cập nhật `Jenkinsfile.developer-build`.
- [x] Cập nhật script deploy developer build nếu có.
- [x] Parameter chỉ gồm service trong scope.
- [x] Thêm parameter cho `payment` nếu đang thiếu.
- [x] Thêm/cập nhật parameter cho UI service:
  - [x] `storefront-ui`
  - [x] `backoffice-ui`
  - [x] `swagger-ui` không có parameter branch vì dùng image public, không build từ source trong repo
- [x] Loại parameter của service ngoài scope.
- [x] In ra URL/NodePort sau khi deploy thành công.

### Manual Check

Kiểm tra parameter:

```bash
grep -nE "choice|SERVICE|payment|delivery|recommendation|webhook|payment-paypal|storefront|backoffice|swagger" Jenkinsfile.developer-build
```

Test kịch bản:

```text
developer_build:
- tax = feature/dev-tax-service
- các service khác = main/latest
```

Kiểm tra sau deploy:

```bash
kubectl get pods,svc -n developer-build
kubectl get deploy -n developer-build -o jsonpath='{range .items[*]}{.metadata.name}{" -> "}{.spec.template.spec.containers[0].image}{"\n"}{end}'
```

### Done Criteria

- [x] Cấu hình parameter developer build đã giới hạn theo scope.
- [ ] Chưa ghi nhận bằng chứng chạy Jenkins `developer_build` thành công end-to-end sau thay đổi scope.
- [ ] Developer có URL/NodePort để test.

---

## Phase 4 - Điều Chỉnh GitOps Manifest

Mục tiêu: repo `gitops-manifest-k8s` deploy đúng phạm vi service cho `dev` và `staging`.

### Checklist

- [x] Cập nhật script `scripts/update-gitops-manifest.sh` trong repo `yas` để chỉ update image theo scope mới.
- [x] Cập nhật base manifest.
- [x] Cập nhật environment `dev`.
- [x] Cập nhật environment `staging`.
- [x] Xóa/không include service ngoài scope:
  - [x] `delivery`
  - [x] `recommendation`
  - [x] `webhook`
  - [x] `payment-paypal`
- [x] Đảm bảo có Deployment/Service/ServiceAccount cho 15 service trong scope.
- [x] Đảm bảo label:
  - [x] `app=<service-name>`
  - [x] `environment=dev` hoặc `environment=staging`
- [x] Đảm bảo image name khớp `bingsu1103/<service>:<tag>` với service build nội bộ; `swagger-ui` dùng image public.
- [x] Đảm bảo ArgoCD app sync đúng path `environments/dev`.

### Manual Check

Trong repo `gitops-manifest-k8s`:

```bash
kubectl kustomize environments/dev > /tmp/dev-rendered.yaml
kubectl kustomize environments/staging > /tmp/staging-rendered.yaml
```

Kiểm tra resource:

```bash
grep -n "kind: Deployment" /tmp/dev-rendered.yaml
grep -nE "name: (delivery|recommendation|webhook|payment-paypal)" /tmp/dev-rendered.yaml || true
grep -nE "name: (payment|order|tax|search|storefront-ui|backoffice-ui|swagger-ui)" /tmp/dev-rendered.yaml
```

Sau khi sync/apply:

```bash
kubectl get pods,svc,sa -n dev
kubectl get pods,svc,sa -n staging
```

### Done Criteria

- [x] `dev` render đúng scope: 15 Deployment, không có `delivery`, `recommendation`, `webhook`, `payment-paypal`.
- [x] `staging` render đúng scope: 15 Deployment, không có `delivery`, `recommendation`, `webhook`, `payment-paypal`.
- [x] ArgoCD `yas-dev` sync thành công và đang `Synced/Healthy`.
- [x] Không còn workload ngoài scope trong render dev/staging.

---

## Phase 5 - Dev Và Staging CD Flow

Mục tiêu: thỏa mãn yêu cầu CD trong `project2.md`.

### Checklist Dev

- [ ] Merge/push vào `main` trigger CI.
- [ ] CI build image với tag `<short-commit-id>` và `latest`/`main`.
- [x] Script update GitOps manifest cho namespace `dev`.
- [x] ArgoCD sync vào `dev`.
- [x] Pod rollout thành công cho 15 workload trong scope.
- [x] Sửa Jenkins diff logic để job chạy trên `main` vẫn diff được `HEAD~1..HEAD` khi `merge-base == HEAD`.

### Checklist Staging

- [ ] Tạo Git tag dạng `vX.Y.Z`.
- [x] Cấu hình CI build image với release tag.
- [x] Cấu hình release tag build toàn bộ image trong scope để staging có baseline đầy đủ.
- [x] Script update GitOps manifest cho namespace `staging`.
- [x] Sửa script staging release để cập nhật toàn bộ image trong scope sang tag `vX.Y.Z`.
- [ ] ArgoCD sync vào `staging`.
- [ ] Pod rollout thành công.
- [ ] Tạo release image tag thật trên Docker Hub trước khi sync staging.

### Trạng thái kiểm tra 2026-07-02

- `yas-dev`: `Synced/Healthy`.
- `yas-staging`: `OutOfSync/Healthy`; namespace `staging` hiện chưa có Deployment/Pod.
- `kubectl apply --dry-run=server -f /tmp/yas-staging-rendered.yaml`: pass.
- Docker Hub hiện chưa có đủ tag `v1.0.0` cho các image trong scope, nên chưa sync staging để tránh `ImagePullBackOff`.
- Namespace `staging` còn service/serviceAccount cũ ngoài scope từ lần sync trước; không xóa thủ công trong Phase 5 vì thao tác prune/delete cần sync ArgoCD staging hoặc xác nhận riêng.

### Preflight trước khi sync staging

Kiểm tra đủ release image tag:

```bash
for img in product cart order customer inventory tax payment media search storefront-bff backoffice-bff sampledata storefront backoffice; do
  docker manifest inspect bingsu1103/$img:vX.Y.Z >/dev/null \
    && echo "OK $img" \
    || echo "MISSING $img"
done
```

Chỉ sync staging khi tất cả image cần deploy đã có tag release:

```bash
kubectl annotate application yas-staging -n argocd argocd.argoproj.io/refresh=hard --overwrite
kubectl get application yas-staging -n argocd
```

### Manual Check

```bash
kubectl get pods -n dev
kubectl get pods -n staging
kubectl get deploy -n dev -o jsonpath='{range .items[*]}{.metadata.name}{" -> "}{.spec.template.spec.containers[0].image}{"\n"}{end}'
kubectl get deploy -n staging -o jsonpath='{range .items[*]}{.metadata.name}{" -> "}{.spec.template.spec.containers[0].image}{"\n"}{end}'
```

### Done Criteria

- [ ] Dev auto deploy khi `main` thay đổi.
- [ ] Staging deploy theo release tag.
- [x] Logic CI/GitOps đã hỗ trợ đúng tag cho dev và staging.
- [ ] Image tag trên cluster khớp với yêu cầu sau khi Jenkins release build và ArgoCD staging sync thật.

---

## Phase 6 - Service Mesh Nền Tảng

Mục tiêu: namespace `dev` và/hoặc `staging` chạy trong Istio mesh với mTLS.

### Checklist

- [x] Istio đã cài trên cluster.
- [ ] Kiali truy cập được.
- [x] Namespace có sidecar injection và workload trong `dev` đang có sidecar.
- [x] Rollout restart workload sau khi bật injection.
- [x] Pod app có `2/2` containers: app + `istio-proxy`.
- [x] Apply `PeerAuthentication` STRICT cho namespace `dev`.
- [x] Apply `DestinationRule` `ISTIO_MUTUAL` cho service trong scope.
- [ ] Rà soát lại các policy ngoại lệ `PERMISSIVE` hiện có cho `storefront-ui`, `storefront-bff`, Redis để giải thích trong báo cáo.

### Manual Check

```bash
kubectl get ns --show-labels
kubectl get pods -n dev
kubectl get peerauthentication -A
kubectl get destinationrule -n dev
```

Kiểm tra sidecar:

```bash
kubectl get pods -n dev -o jsonpath='{range .items[*]}{.metadata.name}{" containers="}{.spec.containers[*].name}{"\n"}{end}'
```

### Done Criteria

- [x] Service trong scope có sidecar.
- [ ] mTLS còn cần chỉnh policy cho flow order/payment: `storefront-bff -> payment` đang bị RBAC deny.
- [ ] Kiali hiển thị namespace và workload.

---

## Phase 7 - Demo VirtualService Retry

Mục tiêu: demo retry policy bằng Istio `VirtualService`, ưu tiên service `tax`.

### Checklist

- [ ] Tạo/cập nhật `VirtualService` cho `tax`.
- [x] Đã có VirtualService retry cho `product`, `cart`, `order`, `inventory`, `payment`.
- [ ] Cấu hình retry cho service demo chính thức:
  - [ ] `attempts`
  - [ ] `perTryTimeout`
  - [ ] `retryOn`
  - [ ] `timeout`
- [ ] Tạo traffic từ test pod hoặc service hợp lệ sang `tax`.
- [ ] Tạo kịch bản lỗi tạm thời nếu có thể.
- [ ] Chụp Kiali graph có traffic.
- [ ] Lưu output `kubectl describe virtualservice`.

### Manual Check

```bash
kubectl get virtualservice -n dev
kubectl describe virtualservice tax-retry -n dev
```

Test curl từ pod có sidecar:

```bash
kubectl exec -n dev <client-pod> -c <client-container> -- \
  curl -s -o /dev/null -w "%{http_code}\n" \
  http://tax.dev.svc.cluster.local:8091/tax/actuator/health
```

### Done Criteria

- [x] `VirtualService` retry tồn tại cho một số service trong `dev`.
- [ ] `VirtualService` retry cho `tax` chưa thấy trong cluster tại thời điểm kiểm tra.
- [ ] Curl/log/Kiali có bằng chứng traffic.
- [ ] Báo cáo giải thích được retry policy đang retry trong trường hợp nào.

---

## Phase 8 - Demo AuthorizationPolicy

Mục tiêu: demo chỉ service được phép mới gọi được service đích. Ưu tiên demo với `search`.

### Checklist

- [x] Tạo `deny-all` cho namespace `dev` sau khi các allow policy đã sẵn sàng.
- [x] Tạo allow policy cho service hợp lệ gọi `search`.
- [ ] Tạo allow policy cho các service bắt buộc trong order flow.
- [ ] Test từ pod hợp lệ: thành công.
- [ ] Test từ pod không hợp lệ: bị chặn.
- [ ] Chụp Kiali và lưu curl logs.

### Manual Check

```bash
kubectl get authorizationpolicy -n dev
kubectl describe authorizationpolicy -n dev
```

Test được phép:

```bash
kubectl exec -n dev <allowed-client-pod> -c <allowed-client-container> -- \
  curl -s -o /dev/null -w "%{http_code}\n" \
  http://search.dev.svc.cluster.local:8092/search/actuator/health
```

Test bị chặn:

```bash
kubectl exec -n dev <blocked-client-pod> -c <blocked-client-container> -- \
  curl -s -o /dev/null -w "%{http_code}\n" \
  http://search.dev.svc.cluster.local:8092/search/actuator/health
```

Kết quả mong đợi:

```text
allowed client -> 200 hoặc response hợp lệ
blocked client -> 403 hoặc RBAC access denied
```

### Done Criteria

- [ ] Có bằng chứng allow/deny rõ ràng.
- [ ] `deny-all` đã bật, nhưng allow policy chưa đủ cho order/payment flow; cần bổ sung trước khi demo.
- [ ] Kiali thể hiện traffic/policy liên quan.

---

## Phase 9 - Kiểm Thử End-to-End Và Báo Cáo

Mục tiêu: có bằng chứng đầy đủ để nộp đồ án.

### Checklist

- [x] Frontend `storefront-ui` truy cập được qua `istio-ingressgateway` port-forward.
- [ ] Frontend `backoffice-ui` truy cập được.
- [ ] `swagger-ui` truy cập được.
- [x] Health/pod readiness backend service trong scope: 15 workload `2/2 Running`.
- [ ] Flow demo tối thiểu:
  - [ ] Xem sản phẩm
  - [ ] Tìm kiếm sản phẩm
  - [ ] Thêm vào cart
  - [ ] Tạo checkout/order
  - [ ] Chọn/ghi nhận payment nếu UI/API hỗ trợ
- [x] Seed sample data qua storefront route: `POST /api/sampledata/storefront/sampledata -> 200 OK`.
- [ ] Sửa blocker product API: `GET /api/product/storefront/categories -> 500 CircuitBreaker OPEN`.
- [ ] Sửa blocker payment provider: `GET /api/payment/storefront/payment-providers -> 403 RBAC access denied`.
- [ ] Hoàn tất route login Keycloak cho browser để cart/checkout có session người dùng.
- [ ] Kiali topology có traffic thật.
- [ ] Có bằng chứng retry policy.
- [ ] Có bằng chứng AuthorizationPolicy.
- [ ] Báo cáo có screenshot:
  - [ ] Jenkins/GitHub pipeline
  - [ ] Docker Hub image tags
  - [ ] GitOps/ArgoCD sync
  - [ ] Kubernetes pods/services
  - [ ] Kiali topology
  - [ ] Curl test logs

### Manual Check Tổng Hợp

```bash
kubectl get pods,svc,deploy,sa -n dev
kubectl get pods,svc,deploy,sa -n staging
kubectl get virtualservice,destinationrule,authorizationpolicy -n dev
```

Kiểm tra image trên cluster:

```bash
kubectl get deploy -n dev -o jsonpath='{range .items[*]}{.metadata.name}{" -> "}{.spec.template.spec.containers[0].image}{"\n"}{end}'
```

### Done Criteria

- [x] Hệ thống deploy đúng scope trong `dev` và render đúng scope cho `staging`.
- [ ] CI/CD đúng yêu cầu `project2.md`.
- [ ] Service Mesh có mTLS, retry, AuthorizationPolicy; còn thiếu bằng chứng Kiali/curl và một số allow policy cho order flow.
- [ ] Có đầy đủ bằng chứng để viết báo cáo.

---

## Thứ Tự Thực Hiện Khuyến Nghị

1. Hoàn tất Phase 1 trước khi sửa pipeline.
2. Sửa CI và developer build trước.
3. Sửa GitOps manifest sau khi danh sách image đã thống nhất.
4. Deploy dev/staging và đảm bảo pod ready.
5. Chỉ apply `deny-all` AuthorizationPolicy khi allow policy đã đủ.
6. Chụp bằng chứng sau từng phase, không đợi đến cuối đồ án mới tổng hợp.
