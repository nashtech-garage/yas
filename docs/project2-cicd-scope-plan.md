# Project 2 - Checklist Điều Chỉnh Phạm Vi CI/CD Cho YAS

Tài liệu này dùng để theo dõi tiến độ điều chỉnh CI/CD, GitOps và Service Mesh theo phạm vi đã chốt cho đồ án DevOps.

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
- [ ] Đối chiếu mapping với repo `gitops-manifest-k8s`.
- [ ] Xác nhận với các TV khác nếu service nào phải giữ thêm: `promotion`, `location`, `rating`.

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
- [ ] Cả nhóm đồng ý với danh sách service bị loại và service được giữ thêm nếu có.

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

- [ ] CI không build service ngoài scope.
- [ ] CI build thành công các service trong scope.
- [ ] Docker Hub có image/tag đúng yêu cầu.

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

- [ ] Có thể deploy riêng 1 service theo branch.
- [ ] Các service còn lại dùng image default.
- [ ] Developer có URL/NodePort để test.

---

## Phase 4 - Điều Chỉnh GitOps Manifest

Mục tiêu: repo `gitops-manifest-k8s` deploy đúng phạm vi service cho `dev` và `staging`.

### Checklist

- [x] Cập nhật script `scripts/update-gitops-manifest.sh` trong repo `yas` để chỉ update image theo scope mới.
- [ ] Cập nhật base manifest.
- [ ] Cập nhật environment `dev`.
- [ ] Cập nhật environment `staging`.
- [ ] Xóa/không include service ngoài scope:
  - [ ] `delivery`
  - [ ] `recommendation`
  - [ ] `webhook`
  - [ ] `payment-paypal`
- [ ] Đảm bảo có Deployment/Service/ServiceAccount cho 15 service trong scope.
- [ ] Đảm bảo label:
  - [ ] `app=<service-name>`
  - [ ] `environment=dev` hoặc `environment=staging`
- [ ] Đảm bảo image name khớp `bingsu1103/<service>:<tag>`.
- [ ] Đảm bảo ArgoCD app sync đúng path.

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

- [ ] `dev` render đúng scope.
- [ ] `staging` render đúng scope.
- [ ] ArgoCD sync thành công.
- [ ] Không còn workload ngoài scope nếu không có lý do demo.

---

## Phase 5 - Dev Và Staging CD Flow

Mục tiêu: thỏa mãn yêu cầu CD trong `project2.md`.

### Checklist Dev

- [ ] Merge/push vào `main` trigger CI.
- [ ] CI build image với tag `<short-commit-id>` và `latest`/`main`.
- [ ] Script update GitOps manifest cho namespace `dev`.
- [ ] ArgoCD sync vào `dev`.
- [ ] Pod rollout thành công.

### Checklist Staging

- [ ] Tạo Git tag dạng `vX.Y.Z`.
- [ ] CI build image với release tag.
- [ ] Script update GitOps manifest cho namespace `staging`.
- [ ] ArgoCD sync vào `staging`.
- [ ] Pod rollout thành công.

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
- [ ] Image tag trên cluster khớp với yêu cầu.

---

## Phase 6 - Service Mesh Nền Tảng

Mục tiêu: namespace `dev` và/hoặc `staging` chạy trong Istio mesh với mTLS.

### Checklist

- [ ] Istio đã cài trên cluster.
- [ ] Kiali truy cập được.
- [ ] Namespace có label sidecar injection.
- [ ] Rollout restart workload sau khi bật injection.
- [ ] Pod app có `2/2` containers: app + `istio-proxy`.
- [ ] Apply `PeerAuthentication` STRICT.
- [ ] Apply `DestinationRule` `ISTIO_MUTUAL` cho service trong scope.

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

- [ ] Service trong scope có sidecar.
- [ ] mTLS không làm hỏng traffic hợp lệ.
- [ ] Kiali hiển thị namespace và workload.

---

## Phase 7 - Demo VirtualService Retry

Mục tiêu: demo retry policy bằng Istio `VirtualService`, ưu tiên service `tax`.

### Checklist

- [ ] Tạo/cập nhật `VirtualService` cho `tax`.
- [ ] Cấu hình retry:
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

- [ ] `VirtualService` retry tồn tại.
- [ ] Curl/log/Kiali có bằng chứng traffic.
- [ ] Báo cáo giải thích được retry policy đang retry trong trường hợp nào.

---

## Phase 8 - Demo AuthorizationPolicy

Mục tiêu: demo chỉ service được phép mới gọi được service đích. Ưu tiên demo với `search`.

### Checklist

- [ ] Tạo `deny-all` cho namespace `dev` sau khi các allow policy đã sẵn sàng.
- [ ] Tạo allow policy cho service hợp lệ gọi `search`.
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
- [ ] Không apply `deny-all` khi allow policy chưa đủ.
- [ ] Kiali thể hiện traffic/policy liên quan.

---

## Phase 9 - Kiểm Thử End-to-End Và Báo Cáo

Mục tiêu: có bằng chứng đầy đủ để nộp đồ án.

### Checklist

- [ ] Frontend `storefront-ui` truy cập được.
- [ ] Frontend `backoffice-ui` truy cập được.
- [ ] `swagger-ui` truy cập được.
- [ ] Health check backend service trong scope.
- [ ] Flow demo tối thiểu:
  - [ ] Xem sản phẩm
  - [ ] Tìm kiếm sản phẩm
  - [ ] Thêm vào cart
  - [ ] Tạo checkout/order
  - [ ] Chọn/ghi nhận payment nếu UI/API hỗ trợ
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

- [ ] Hệ thống deploy đúng scope.
- [ ] CI/CD đúng yêu cầu `project2.md`.
- [ ] Service Mesh có mTLS, retry, AuthorizationPolicy.
- [ ] Có đầy đủ bằng chứng để viết báo cáo.

---

## Thứ Tự Thực Hiện Khuyến Nghị

1. Hoàn tất Phase 1 trước khi sửa pipeline.
2. Sửa CI và developer build trước.
3. Sửa GitOps manifest sau khi danh sách image đã thống nhất.
4. Deploy dev/staging và đảm bảo pod ready.
5. Chỉ apply `deny-all` AuthorizationPolicy khi allow policy đã đủ.
6. Chụp bằng chứng sau từng phase, không đợi đến cuối đồ án mới tổng hợp.
