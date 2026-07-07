# HƯỚNG DẪN CHẠY ĐỒ ÁN SIÊU TỐC TÍCH HỢP SERVICE MESH (ISTIO)

## PHẦN 1: KHỞI ĐỘNG VÀ THIẾT LẬP HẠ TẦNG (BẮT BUỘC)

### 1. Khởi động Minikube
Tạo cụm Kubernetes với RAM/CPU đủ mạnh để gánh hệ thống. Mở Terminal quyền Admin:
```bash
minikube start --driver=docker --memory=12288 --cpus=6 --kubernetes-version=v1.28.15
```

### 2. Tạo Namespace
Theo thiết kế hiện tại của hệ thống cơ bản, toàn bộ ứng dụng và hạ tầng của chúng ta sẽ nằm trong namespace `yas`. Các chính sách bảo mật của Service Mesh (Istio) cũng sẽ được áp dụng trực tiếp lên namespace này để bảo vệ các service.

```bash
kubectl create namespace yas
```

### 3. Cài đặt các thành phần hạ tầng cốt lõi (cd vào yas-devops-1)

**a. Cài yas-configuration (ConfigMap chung):**
```bash
helm upgrade --install yas-configuration ./k8s/charts/yas-configuration --namespace yas --create-namespace
```

**b. Cài PostgreSQL (Database):**
*Lệnh 1 (chỉ chạy 1 lần lúc mới cài): Cài Operator*
```bash
helm repo add postgres-operator-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator
helm repo update
helm upgrade --install postgres-operator postgres-operator-charts/postgres-operator --namespace postgres --create-namespace
```
*Lệnh 2 (chạy mỗi lần): Cài Cluster*
```bash
helm upgrade --install postgres ./k8s/deploy/postgres/postgresql --namespace postgres --create-namespace --set auth.postgresPassword=admin
```

**c. Cài Keycloak (Xác thực):**
*Lệnh 1 (chỉ chạy 1 lần): Cài Operator*
```bash
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/kubernetes.yml -n yas
```
*Lệnh 2 (chạy mỗi lần): Cài Cluster*
```bash
helm upgrade --install keycloak ./k8s/deploy/keycloak/keycloak --namespace yas
```

**d. Cài Redis, Kafka và Elasticsearch (Chỉ chạy 1 lần):**
```bash
# Redis
helm upgrade --install redis oci://registry-1.docker.io/bitnamicharts/redis -n redis --create-namespace --set auth.password=redis

# Kafka
helm repo add strimzi https://strimzi.io/charts/
helm repo update
helm upgrade --install kafka-operator strimzi/strimzi-kafka-operator --version 0.38.0 --create-namespace --namespace kafka
helm upgrade --install kafka-cluster ./k8s/deploy/kafka/kafka-cluster --create-namespace --namespace kafka --set kafka.replicas=1 --set zookeeper.replicas=1 --set postgresql.username=yasadminuser --set postgresql.password=admin

# Elasticsearch
helm repo add elastic https://helm.elastic.co
helm repo update
helm upgrade --install elastic-operator elastic/eck-operator --create-namespace --namespace elasticsearch
helm upgrade --install elasticsearch-cluster ./k8s/deploy/elasticsearch/elasticsearch-cluster --create-namespace --namespace elasticsearch --set elasticsearch.replicas=1 --set kibana.ingress.hostname=kibana.yas.local.com
```

**e. Bật Ingress Controller (Cổng kết nối Web):**
```bash
minikube addons enable ingress
```

**f. Cấu hình DNS nội bộ (Để các Pod gọi được tên miền của nhau):**
Mở PowerShell chạy đoạn này:
```powershell
$MINIKUBE_IP = minikube ip
$COREDNS_CLEAN = @"
apiVersion: v1
kind: ConfigMap
metadata:
  name: coredns
  namespace: kube-system
data:
  Corefile: |
    .:53 {
        errors
        health {
           lameduck 5s
        }
        ready
        hosts {
           $MINIKUBE_IP identity.yas.local.com backoffice.yas.local.com storefront.yas.local.com identity.dev.local.com backoffice.dev.local.com storefront.dev.local.com identity.staging.local.com backoffice.staging.local.com storefront.staging.local.com
           fallthrough
        }
        kubernetes cluster.local in-addr.arpa ip6.arpa {
           pods insecure
           fallthrough in-addr.arpa ip6.arpa
           ttl 30
        }
        prometheus :9153
        forward . /etc/resolv.conf {
           max_concurrent 1000
        }
        cache 30
        loop
        reload
        loadbalance
    }
"@
$COREDNS_CLEAN | Out-File -Encoding utf8 "$env:TEMP\coredns-patch.yaml"
kubectl apply -f "$env:TEMP\coredns-patch.yaml"
kubectl rollout restart deployment coredns -n kube-system
```

**⚠️ CHỜ ĐỢI TẤT CẢ PODs RUNNING MỚI ĐƯỢC ĐI TIẾP:**
```bash
kubectl get pods -n yas -w
```

*Mục đích: Mở luồng để Github chui vào máy bạn đắp code. Các ứng dụng sẽ được deploy theo luồng CI/CD truyền thống.*

**1. Mở hầm Ngrok:**
```bash
kubectl cluster-info   # Lấy số cổng 
ngrok tcp 8443         # Mở port (Giữ terminal này luôn chạy)
```

**2. Cấu hình Github Secrets:**
Sửa file `C:\Users\<Tên-bạn>\.kube\config`, đổi IP thành link Ngrok vừa cấp. Copy toàn bộ file thả vào Github Secret `KUBECONFIG_DATA`. (Kèm theo `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`).

**3. Chạy job Developer Build trên Github Action:**
Vào nhánh bạn đang làm, chạy workflow để nó deploy các service (`product`, `cart`, `storefront-bff`, v.v.) vào namespace `yas`. Sau khi xong, các pod sẽ có trạng thái `1/1` (không có Envoy proxy vì ta không dùng Service Mesh ở đây).

---

## PHẦN 4: TEST ỨNG DỤNG BẰNG TRÌNH DUYỆT

**1. Mở cổng Ingress (Chính thức):**
```bash
kubectl port-forward -n ingress-nginx svc/ingress-nginx-controller 80:80
```
Sửa file `C:\Windows\System32\drivers\etc\hosts`:
```text
127.0.0.1 storefront.yas.local.com
127.0.0.1 backoffice.yas.local.com
127.0.0.1 identity.yas.local.com
```

**2. Nạp Sample Data & Truy cập web:**
Vào web `http://storefront.yas.local.com`, cuộn xuống cùng bấm nút **Sample Data**. F5 lại trang sẽ thấy dữ liệu hiển thị.

---

## BƯỚC ĐỆM: DỌN DẸP CHUYỂN SANG NÂNG CAO
Xóa hạ tầng cũ để giải phóng RAM cho ArgoCD.
```bash
kubectl delete namespace yas redis kafka elasticsearch ingress-nginx postgres
```
# (Khuyên dùng: minikube delete rồi tạo lại cụm cho sạch hoàn toàn)

*(Các phần phía dưới liên quan đến GitOps ArgoCD giữ nguyên như bản chuẩn, vì ArgoCD deploy qua một flow riêng không nằm chung cấu trúc trên).*

---

## PHẦN 6: CHUYỂN SANG ARGOCD (MÔ HÌNH NÂNG CAO)

**1. Chuẩn bị: Tự động tách cấu hình DEV/STAGING**
Mở PowerShell và chạy đoạn mã sau để tự sinh ra 14 file `values-dev.yaml` và `values-staging.yaml` (giúp tách domain cho 2 môi trường). *Lưu ý: Bạn phải đổi đường dẫn cd cho đúng với máy của bạn!*

```powershell
cd "E:\Nhập môn DevOps\Project\Project2\yas-gitops"

$CHARTS_DIR = "k8s\charts"
Get-ChildItem -Path $CHARTS_DIR -Directory | ForEach-Object {
    $svcPath = $_.FullName; $valPath = Join-Path $svcPath "values.yaml"
    if (Test-Path $valPath) {
        $c = Get-Content $valPath -Raw
        $c -replace "\.yas\.local\.com", ".dev.local.com" -replace "\.yas\.svc\.cluster\.local", ".dev.svc.cluster.local" | Out-File -Encoding utf8 (Join-Path $svcPath "values-dev.yaml")
        $c -replace "\.yas\.local\.com", ".staging.local.com" -replace "\.yas\.svc\.cluster\.local", ".staging.svc.cluster.local" | Out-File -Encoding utf8 (Join-Path $svcPath "values-staging.yaml")
    }
}
Write-Host "Success!" -ForegroundColor Green
```
*(Chạy xong nhớ git add, commit & push thay đổi lên nhánh `main` của repo `yas-gitops`!)*
*Chỉ chạy 1 lần thôi, tui chạy rồi mn khỏi cần làm.*

**2. Cài Minikube và ArgoCD:**
```bash
minikube start --driver=docker --memory=12288 --cpus=6 --kubernetes-version=v1.28.15
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

**3. Khởi tạo hạ tầng dùng chung (Postgres, Redis, Kafka, Elasticsearch):**
```bash
minikube addons enable ingress

# Nên chờ ingress cài xong

# Chạy lại script PowerShell CoreDNS ở Phần 1 mục f.

# Cài DB dùng chung
helm repo add postgres-operator-charts https://opensource.zalando.com/postgres-operator/charts/postgres-operator
helm repo update
helm upgrade --install postgres-operator postgres-operator-charts/postgres-operator --namespace postgres --create-namespace
helm upgrade --install postgres ./k8s/deploy/postgres/postgresql --namespace postgres --create-namespace --set auth.postgresPassword=admin

# Cài Redis
helm upgrade --install redis oci://registry-1.docker.io/bitnamicharts/redis -n redis --create-namespace --set auth.password=redis

# Cài Kafka
helm repo add strimzi https://strimzi.io/charts/
helm repo update
helm upgrade --install kafka-operator strimzi/strimzi-kafka-operator --version 0.38.0 --namespace kafka --create-namespace
helm upgrade --install kafka-cluster ./k8s/deploy/kafka/kafka-cluster --namespace kafka --set kafka.replicas=1 --set zookeeper.replicas=1 --set postgresql.username=yasadminuser --set postgresql.password=admin

# Cài Elasticsearch
helm repo add elastic https://helm.elastic.co
helm repo update
helm upgrade --install elastic-operator elastic/eck-operator --namespace elasticsearch --create-namespace
helm upgrade --install elasticsearch-cluster ./k8s/deploy/elasticsearch/elasticsearch-cluster --namespace elasticsearch --set elasticsearch.replicas=1 --set kibana.ingress.hostname=kibana.yas.local.com

# Cài CRD Keycloak
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloaks.k8s.keycloak.org-v1.yml
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml
```

Nếu có lỗi trong quá trình cài:
```bash
kubectl delete validatingwebhookconfigurations ingress-nginx-admission
```

**4. Cài đặt Cốt lõi Service Mesh (Istio) & Khởi tạo môi trường DEV/STAGING:**
Bây giờ chúng ta sẽ cài Istio. Nhờ ArgoCD, các policies (mTLS, Zero-Trust) và cấu hình Retry sẽ được kéo về tự động sau này.

**Bước 4a – Tải và cài đặt istioctl (chỉ làm một lần):**
```powershell
# Tải istioctl
Invoke-WebRequest `
  -Uri "https://github.com/istio/istio/releases/download/1.26.1/istioctl-1.26.1-win-amd64.zip" `
  -OutFile "istioctl.zip" -UseBasicParsing

Expand-Archive -Path "istioctl.zip" -DestinationPath "istioctl-bin" -Force
```

**Bước 4b – Kiểm tra cluster đủ điều kiện cài Istio:**
```powershell
.\istioctl-bin\istioctl.exe x precheck
```
Kết quả mong đợi: tất cả các mục đều hiện `Install Pre-Check passed`.

**Bước 4c – Cài Istio với profile demo:**
```powershell
.\istioctl-bin\istioctl.exe install --set profile=demo -y
```

**Bước 4d – Bật Istio Injection cho 2 namespace:**
```bash
kubectl label namespace dev istio-injection=enabled --overwrite
kubectl label namespace staging istio-injection=enabled --overwrite
```

**Bước 4e – Cài Kiali (dashboard topology) và Prometheus (thu thập metrics):**
```powershell
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.26/samples/addons/prometheus.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.26/samples/addons/kiali.yaml
```

**Bước 4f – Kiểm tra Istio đã cài thành công:**
```powershell
kubectl get pods -n istio-system
```
Kết quả mong đợi (tất cả pod ở trạng thái Running):
```
NAME                                    READY   STATUS    RESTARTS   AGE
istiod-75cf956749-qkdjw                 1/1     Running   0          5m
istio-ingressgateway-cfb6f6999-gh8kl    1/1     Running   0          5m
istio-egressgateway-765d694f69-h5dzn    1/1     Running   0          5m
kiali-5c87c84765-4wrgg                  1/1     Running   0          3m
prometheus-5dcf95999d-x7zvg             2/2     Running   0          3m
```

**Bước 4g – Khởi tạo môi trường DEV và STAGING:**
```bash
# Cho DEV
helm upgrade --install yas-configuration ./k8s/charts/yas-configuration --namespace dev --create-namespace
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/kubernetes.yml -n dev
helm upgrade --install keycloak ./k8s/deploy/keycloak/keycloak --namespace dev --create-namespace

# Cho STAGING
helm upgrade --install yas-configuration ./k8s/charts/yas-configuration --namespace staging --create-namespace
kubectl apply -f https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/kubernetes.yml -n staging
helm upgrade --install keycloak ./k8s/deploy/keycloak/keycloak --namespace staging --create-namespace
```

**⚠️ Lưu ý cực kỳ quan trọng:** Toàn bộ cấu hình Service Mesh (mTLS, Zero-Trust, Retry) cho môi trường `dev` và `staging` đã được chuyển sang quản lý theo chuẩn GitOps trong repo `yas-gitops`. Khi bạn cài ArgoCD ở các bước tiếp theo, ArgoCD sẽ tự động lấy các cấu hình này từ Github và deploy vào cụm, bạn KHÔNG CẦN phải chạy lệnh apply cấu hình Istio thủ công nữa!

**Cấu trúc Istio Helm Chart trong yas-gitops:**
```
yas-gitops/k8s/charts/istio-policies/
|-- Chart.yaml                              # Khai báo metadata của Helm chart
|-- values.yaml                             # Tham số cấu hình (namespace, retry config)
|-- templates/
    |-- mtls.yaml                           # PeerAuthentication + DestinationRule (mTLS STRICT)
    |-- destination-rules-infra.yaml        # Tắt mTLS cho hạ tầng không có Envoy (Postgres, Redis, Kafka, ES)
    |-- authz.yaml                          # 16 AuthorizationPolicy (deny-all + per-service)
    |-- virtual-services.yaml               # Retry policy cho các backend services
```

**5. Lấy mật khẩu và đăng nhập ArgoCD:**
```powershell
[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String((kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}")))
```
Mở web:
```bash
kubectl port-forward svc/argocd-server -n argocd 8080:443
```

**6. Triển khai cấu trúc App of Apps (Triển khai toàn bộ DEV và STAGING cùng lúc):**
Thay vì gõ lệnh tạo từng app rườm rà, ta chỉ cần tạo ra 2 "App Gốc" (Root Apps), và chúng sẽ tự động đọc repo `yas-gitops` để đẻ ra toàn bộ 14 app con cho mỗi môi trường!

Mở PowerShell và chạy đoạn mã sau để trực tiếp cài đặt Root Apps:
```powershell
$ROOT_APPS_YAML = @"
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: yas-root-dev
  namespace: argocd
  finalizers:
    - resources-finalizer.argocd.argoproj.io
spec:
  project: default
  source:
    repoURL: https://github.com/dorayakiiiiz/yas-gitops.git
    targetRevision: main
    path: .
    helm:
      valueFiles:
        - values-dev.yaml
  destination:
    server: https://kubernetes.default.svc
    namespace: dev
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
    syncOptions:
      - CreateNamespace=true
---
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: yas-root-staging
  namespace: argocd
  finalizers:
    - resources-finalizer.argocd.argoproj.io
spec:
  project: default
  source:
    repoURL: https://github.com/dorayakiiiiz/yas-gitops.git
    targetRevision: main
    path: .
    helm:
      valueFiles:
        - values-staging.yaml
  destination:
    server: https://kubernetes.default.svc
    namespace: staging
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
    syncOptions:
      - CreateNamespace=true
"@

$ROOT_APPS_YAML | kubectl apply -f -
```
Sau khi chạy thành công, mở web ArgoCD lên và bạn sẽ thấy toàn bộ các dịch vụ tự động được kéo về và cài đặt!

**7. Cách Release lên Staging theo Tag:**
1. Lên GitHub, tạo một Release mới (VD: `customer-v1.0.0`). **Mục tiêu (Target) chọn `gitops`**.
2. Vào giao diện Web ArgoCD, tìm App tương ứng (VD: `customer-staging`), nhấn **Edit**.
3. Sửa dòng Revision từ `gitops` thành tên Tag vừa tạo (`customer-v1.0.0`) rồi lưu lại. App sẽ tự kéo bản mới về chạy.

*Lệnh dọn dẹp nếu muốn xóa:*
```powershell
$SERVICES = @("product", "cart", "order", "customer", "inventory", "tax", "media", "search", "storefront-bff", "storefront-ui", "backoffice-bff", "backoffice-ui", "swagger-ui", "sampledata")
foreach ($svc in $SERVICES) { kubectl delete application "$svc-dev" -n argocd }
```
*Lệnh dọn rác mồ côi (nếu lỡ xóa sai cách):*
```powershell
$SERVICES = @("product", "cart", "order", "customer", "inventory", "tax", "media", "search", "storefront-bff", "storefront-ui", "backoffice-bff", "backoffice-ui", "swagger-ui", "sampledata")
foreach ($svc in $SERVICES) { kubectl delete all,ingress,configmap,secret -n dev -l app.kubernetes.io/instance="$svc-dev" }
```

**8. Truy cập kết quả cuối cùng:**
Sửa `hosts` và vào các link:
- `http://storefront.yas.local.com`
- `http://backoffice.yas.local.com`
- `http://api.yas.local.com/swagger-ui/index.html`

**9. Kiểm tra và giải thích Service Mesh (Dành cho Demo):**
Khi demo luồng Nâng Cao, bạn có thể giải thích những tính năng tự động này của GitOps:
- **Cài đặt Kiali:** Mở port `kubectl port-forward svc/kiali 20001:20001 -n istio-system`. Vào `http://localhost:20001` mục Graph.
- **mTLS STRICT (Mã hóa đường truyền):** Theo mặc định, traffic nội bộ K8s đi dưới dạng plain-text. Nhưng nhờ ArgoCD deploy cấu hình mTLS, tất cả traffic nội bộ giờ đã bị mã hóa 2 chiều (Bạn sẽ thấy icon 🔒 trên sơ đồ Kiali).
- **Zero-Trust Network (Authorization Policy):** ArgoCD tự động cấu hình quy tắc Deny All và chỉ Allow đúng các luồng hợp lệ (Ingress -> BFF -> Backend -> DB). Kẻ gian nếu vào được 1 pod cũng không thể quét mạng hay gọi bừa bãi.
- **Tự động phục hồi (VirtualService Retry):** Cấu hình Retry (tối đa 3 lần, mỗi lần 5s) đã được đính kèm vào từng Helm Chart và tự động sinh ra cho cả 14 app. Giúp hệ thống không bị crash nếu mạng chập chờn.

---

## PHẦN 7: KIỂM THỬ SERVICE MESH (THỦ CÔNG - CHẠY SAU KHI ARGOCD SYNC XONG)

> **Điều kiện tiên quyết:** 14 service trong namespace `dev` đều ở trạng thái `2/2 Running` (có Envoy sidecar). Kiểm tra bằng:
> ```powershell
> kubectl get pods -n dev
> ```

### Thông tin môi trường kiểm thử

| Mục | Giá trị |
|---|---|
| Kubernetes | Minikube v1.38.1, Kubernetes v1.35.1, Docker driver |
| Istio | v1.26.1, profile demo |
| Namespace | dev (istio-injection=enabled) |
| Số service trong mesh | 14 microservices (2/2 Running, có Envoy sidecar) |

### Bước 0: Khởi động Kiali để quan sát topology

```powershell
# Mở port Kiali (giữ terminal này mở)
kubectl port-forward svc/kiali 20001:20001 -n istio-system
```
Truy cập: `http://localhost:20001` → Graph → Namespace: dev → bật **Security** để hiện biểu tượng mTLS 🔒.

Sinh traffic để Kiali có dữ liệu hiển thị:
```powershell
for ($i = 1; $i -le 20; $i++) {
    kubectl exec -n dev deployment/storefront-bff -- `
        wget -q -O /dev/null `
        "http://product.dev.svc.cluster.local/product/storefront/products/featured" 2>$null
    Start-Sleep -Milliseconds 500
}
```

---

### TEST 1 – mTLS STRICT: Pod ngoài mesh bị chặn

**Mục đích:** Chứng minh `PeerAuthentication mode: STRICT` hoạt động đúng — pod không có Envoy sidecar không thể gọi vào service trong mesh.

**Caller:** Pod tạm `mtls-test` trong namespace `default` (không có Envoy sidecar, không có certificate)
**Target:** `product.dev.svc.cluster.local`

```powershell
kubectl run mtls-test --image=curlimages/curl --namespace=default `
  --rm -it --restart=Never -- `
  curl -v --max-time 5 http://product.dev.svc.cluster.local/product/storefront/products/featured
```

**Kết quả mong đợi:**
```
* Trying 10.98.61.147:80...
* Established connection to product.dev.svc.cluster.local port 80
* using HTTP/1.x
> GET /product/storefront/products/featured HTTP/1.1
> ...
* Request completely sent off
* Recv failure: Connection reset by peer
* closing connection #0
curl: (56) Recv failure: Connection reset by peer
```

**Giải thích:** Pod tạm trong namespace `default` không có Envoy sidecar → gửi HTTP thường (plain-text). Envoy của `product` đang chờ TLS handshake theo cấu hình `PeerAuthentication mode: STRICT`. Không nhận được TLS handshake → Envoy reset kết nối ngay lập tức. `Connection reset by peer` là bằng chứng trực tiếp mTLS STRICT đang hoạt động.

**File cấu hình liên quan:** `yas-gitops/k8s/charts/istio-policies/templates/mtls.yaml` (PeerAuthentication mode: STRICT)

---

### TEST 2 – Authorization Policy DENY: Service không có quyền bị 403

**Mục đích:** Chứng minh `AuthorizationPolicy` chặn đúng service không có quyền — service `tax` không nằm trong danh sách principals của `allow-to-customer`, nên phải bị từ chối.

**Caller:** Pod `tax` trong namespace `dev` (có Envoy sidecar, có certificate hợp lệ)
**Target:** `customer.dev.svc.cluster.local`

```powershell
kubectl exec -n dev deployment/tax -- `
  wget -S -q -O /dev/null --timeout=5 `
  "http://customer.dev.svc.cluster.local/customer/storefront/customers/profile"
```

**Kết quả mong đợi:**
```
  HTTP/1.1 403 Forbidden
wget: server returned error: HTTP/1.1 403 Forbidden
```

**Giải thích:** `tax` nằm trong mesh, có certificate hợp lệ (mTLS pass). Tuy nhiên, Envoy của `customer` đọc identity từ certificate: `cluster.local/ns/dev/sa/tax`. Kiểm tra danh sách principals trong `allow-to-customer`: chỉ có storefront-bff, backoffice-bff, sampledata. `tax` không khớp bất kỳ rule nào → `deny-all` áp dụng → HTTP 403 ngay tại tầng Envoy, không bao giờ tới ứng dụng.

**File cấu hình liên quan:** `yas-gitops/k8s/charts/istio-policies/templates/authz.yaml` (deny-all + allow-to-customer)

---

### TEST 3 – Authorization Policy ALLOW: Service có quyền được 200

**Mục đích:** Chứng minh `AuthorizationPolicy` cho phép đúng service có quyền — `storefront-bff` nằm trong danh sách principals của `allow-to-product`, nên phải được chấp nhận.

**Caller:** Pod `storefront-bff` trong namespace `dev` (có Envoy sidecar, có quyền gọi product)
**Target:** `product.dev.svc.cluster.local`

```powershell
kubectl exec -n dev deployment/storefront-bff -- `
  wget -S -q -O /dev/null --timeout=10 `
  "http://product.dev.svc.cluster.local/product/storefront/products/featured"
```

**Kết quả mong đợi:**
```
  HTTP/1.1 200 OK
  vary: Origin,Access-Control-Request-Method,Access-Control-Request-Headers
  x-content-type-options: nosniff
  content-type: application/json
  x-envoy-upstream-service-time: 16
  server: envoy
```

**Giải thích:** `storefront-bff` có certificate `cluster.local/ns/dev/sa/storefront-bff`. Envoy của `product` kiểm tra principals → khớp → cho phép. Header `server: envoy` xác nhận traffic đã đi qua Envoy proxy. Header `x-envoy-upstream-service-time` cho biết thời gian xử lý tại upstream.

**File cấu hình liên quan:** `yas-gitops/k8s/charts/istio-policies/templates/authz.yaml` (allow-to-product)

---

### TEST 4 – Retry Policy: Envoy tự động retry khi gặp lỗi 503

**Mục đích:** Chứng minh `VirtualService retry policy` hoạt động — dùng Fault Injection của Istio để inject lỗi 503 nhân tạo vào product, rồi quan sát Envoy có tự động retry không.

**Bước 4a – Inject lỗi 503 nhân tạo (30% request bị trả 503):**
```powershell
@"
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: product-fault-injection
  namespace: dev
spec:
  hosts:
    - product
  http:
    - fault:
        abort:
          percentage:
            value: 30
          httpStatus: 503
      retries:
        attempts: 3
        perTryTimeout: 5s
        retryOn: 5xx,gateway-error,connect-failure
      timeout: 20s
      route:
        - destination:
            host: product
            port:
              number: 80
"@ | kubectl apply -f -

# Chờ 10 giây để Istio phân phối cấu hình xuống Envoy
Start-Sleep -Seconds 10
```

**Bước 4b – Gửi 10 request và quan sát:**
```powershell
for ($i = 1; $i -le 10; $i++) {
    $r = kubectl exec -n dev deployment/storefront-bff -- `
        wget -S -q -O /dev/null --timeout=20 `
        "http://product.dev.svc.cluster.local/product/storefront/products/featured" 2>&1
    if ($r -match "200") {
        Write-Host "Request $i -> 200 OK (Retry hấp thụ lỗi)" -ForegroundColor Green
    } else {
        Write-Host "Request $i -> FAIL" -ForegroundColor Red
    }
    Start-Sleep -Seconds 1
}
```

**Kết quả mong đợi:**
```
Request 1  -> 200 OK (Retry hấp thụ lỗi)
Request 2  -> 200 OK (Retry hấp thụ lỗi)
...
Request 10 -> 200 OK (Retry hấp thụ lỗi)
```
Ít nhất 8/10 request phải thành công. Xác suất thất bại hoàn toàn = 0.3³ = 2.7%.

**Bước 4c – Dọn dẹp fault injection:**
```powershell
kubectl delete virtualservice product-fault-injection -n dev
```

**Giải thích:** Mặc dù 30% request bị inject lỗi 503, retry policy cho phép Envoy tự động gửi lại tối đa 3 lần. Ứng dụng `storefront-bff` không cần viết bất kỳ retry logic nào — Envoy xử lý hoàn toàn trong suốt.

**File cấu hình liên quan:** `yas-gitops/k8s/charts/istio-policies/templates/virtual-services.yaml` (retry config)

---

### Bảng tổng hợp kết quả kiểm thử

| STT | Kịch bản | Caller | Target | Kết quả mong đợi | Cấu hình liên quan |
|---|---|---|---|---|---|
| 1 | mTLS STRICT | Pod ngoài mesh (default ns) | product | Connection reset by peer | mtls.yaml – PeerAuthentication STRICT |
| 2 | AuthzPolicy DENY | tax (trong mesh) | customer | HTTP 403 Forbidden | authz.yaml – deny-all + allow-to-customer |
| 3 | AuthzPolicy ALLOW | storefront-bff (trong mesh) | product | HTTP 200 OK, server: envoy | authz.yaml – allow-to-product |
| 4 | Retry Policy | storefront-bff | product (30% lỗi 503) | ≥8/10 thành công | virtual-services.yaml – retries: attempts 3 |

**So sánh 3 tầng bảo vệ của Service Mesh:**

| Tầng | Cơ chế | Kết quả khi vi phạm | Vị trí xử lý |
|---|---|---|---|
| Transport | mTLS STRICT (PeerAuthentication) | Connection reset by peer | Envoy từ chối TLS handshake |
| Network | Authorization Policy (deny-all + ALLOW) | HTTP 403 Forbidden | Envoy kiểm tra principals từ certificate |
| Resilience | Retry Policy (VirtualService) | Tự động retry, ứng dụng nhận 200 OK | Envoy retry trong suốt |

---

### Lệnh kiểm tra nhanh Service Mesh

```powershell
# Xem trạng thái tất cả pod (cần 2/2 Running)
kubectl get pods -n dev

# Xem tất cả PeerAuthentication (mTLS)
kubectl get peerauthentication -n dev

# Xem tất cả DestinationRule (mTLS outbound + infra exceptions)
kubectl get destinationrule -n dev

# Xem tất cả AuthorizationPolicy (16 chính sách)
kubectl get authorizationpolicy -n dev

# Xem tất cả VirtualService (retry + fault injection nếu có)
kubectl get virtualservice -n dev

# Mở Kiali Dashboard
kubectl port-forward svc/kiali 20001:20001 -n istio-system
# Truy cập: http://localhost:20001

# Mở ArgoCD Dashboard
kubectl port-forward svc/argocd-server -n argocd 8080:443
# Truy cập: https://localhost:8080
```

### Xử lý sự cố Service Mesh thường gặp

**Pod hiện 1/2 thay vì 2/2:**
```powershell
# Kiểm tra namespace có label istio-injection không
kubectl get namespace dev --show-labels

# Nếu thiếu, thêm label rồi restart
kubectl label namespace dev istio-injection=enabled --overwrite
kubectl rollout restart deployment/<ten-service> -n dev
```

**Service gọi nhau bị 403 sau khi apply AuthorizationPolicy:**
Mở file `authz.yaml` trong `yas-gitops/k8s/charts/istio-policies/templates/`, tìm chính sách của target service và thêm identity của caller vào `principals`:
```yaml
principals:
  - "cluster.local/ns/dev/sa/<ten-service-caller>"
```

**Kết nối tới PostgreSQL/Redis bị lỗi sau khi bật mTLS:**
Kiểm tra file `destination-rules-infra.yaml` đã có entry cho namespace tương ứng chưa. Các hạ tầng không có Envoy (Postgres, Redis, Kafka, Elasticsearch) cần có `DestinationRule` với `mode: DISABLE`.

**AuthorizationPolicy không có hiệu lực ngay:**
Chờ 10–15 giây sau khi apply rồi mới test (Istio cần thời gian phân phối cấu hình xuống Envoy).

**Kiali graph trống:**
Chưa có traffic trong khoảng thời gian đang xem. Dùng vòng lặp wget ở Bước 0 để sinh traffic trước khi xem graph.

---

# XEM Ở ĐÂY: SỬ DỤNG SCRIPT CÀI TỰ ĐỘNG NHANH HƠN

Thay vì phải gõ thủ công từng lệnh hạ tầng ở trên, bạn có thể chạy các script có sẵn trong thư mục `k8s-infrastructure-scripts` để tiết kiệm thời gian.

Trước tiên ta khởi động Minikube, nếu lỗi thì vào .wslconfig cấp phát thêm vùng nhớ.
```bash
minikube start --driver=docker --memory=13312 --cpus=6 --kubernetes-version=v1.28.15
```

Kiểm tra cache còn lại và xóa cache:
```bash
wsl free -h
wsl -u root sysctl -w vm.drop_caches=3
```

**1. Nếu muốn test luồng Cơ Bản (dùng Github Actions):**
**BƯỚC A: Chạy Script cài móng hạ tầng**
Mở PowerShell quyền Admin, gõ lệnh sau để cấp quyền chạy script (chỉ cần làm 1 lần):
```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force
```
Sau đó di chuyển vào thư mục script và chạy lệnh cài hạ tầng gốc:
```powershell
cd "E:\Nhập môn DevOps\Project\Project2\yas-devops-1\k8s-infrastructure-scripts"
.\setup-infra-basic.ps1
# Lệnh này sẽ cài các hạ tầng dùng chung (Postgres, Keycloak, Kafka...) vào namespace `yas`.
# Lưu ý: chạy xong phải chờ cho toàn bộ pod ready nhé, khá lâu mn kiên nhẫn.
```

*(Script này tự động cài Postgres, Redis, Kafka, Elasticsearch dùng chung và Keycloak vào namespace `yas`)*.

**BƯỚC B: Chạy Github Actions cài App**
1. Lên Github Repo của bạn -> Vào tab **Actions** -> Chọn luồng **Developer Build CD** bên trái -> Bấm nút **Run workflow** (cần cấu hình secret và mở port máy gốc thông ra -> đọc phần bên trên từ đầu).
2. Nhập tên 1 service muốn test (VD: `customer`) và tên nhánh (VD: `main`) -> Bấm Run.
3. Ngồi chờ Github Actions đúc Image và gọi lệnh Helm deploy toàn bộ 14 service vào K8s.

**👉 CÁCH TRUY CẬP ĐỂ TEST:**
1. **Mở cổng Ingress:** Mở 1 tab Terminal mới gõ:
   `kubectl port-forward -n ingress-nginx svc/ingress-nginx-controller 80:80`
2. **Cập nhật file hosts:** Mở Notepad (quyền Admin), mở `C:\Windows\System32\drivers\etc\hosts` thêm dòng:
   `127.0.0.1 storefront.yas.local.com backoffice.yas.local.com api.yas.local.com identity.yas.local.com`
3. **Truy cập Web:**
   - Trang Storefront: `http://storefront.yas.local.com`
   - Trang quản lý Backoffice: `http://backoffice.yas.local.com`
   - Test API Swagger: `http://api.yas.local.com/swagger-ui/index.html`

Để xóa tài nguyên app từ luồng cơ bản, chạy job Developer Destroy trên GHA
*(Hoặc đơn giản nhất là xóa mẹ minikube đi r cài lại từ đầu :)) )*

**2. Nếu muốn test luồng Nâng Cao (ArgoCD & GitOps):**
Mở PowerShell quyền Admin, gõ lệnh sau để cấp quyền chạy script (chỉ làm 1 lần nếu cửa sổ mới):
```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force
```
**Note!!!**

Script setup-argocd-advanced.ps1 trong folder k8s-infrastructure-scripts sẽ cài 2 namespace dev và staging chung 1 lần nên trước khi chạy cái script này ở bước cuối cùng bên dưới thì nhớ vào trang web argoCD để xóa app của staging đi nha (xóa từ dòng 50->75, nếu không xóa thì nó sẽ bị x2 RAM). Xóa rồi kệ nha đừng có push cái script đó lên lại, cái đó tiêu chuẩn thôi chứ chạy trên máy mình nặng quá thì cài 1 dev được rồi (hoặc 1 staging xóa dev đi cũng được).

Sau đó di chuyển vào thư mục script và chạy tuần tự:
```powershell
# Nhớ sửa folder lại cho đúng
cd "E:\Nhập môn DevOps\Project\Project2\yas-devops-1\k8s-infrastructure-scripts"

# BƯỚC A: Cài đặt móng hạ tầng + Cài Istio Service Mesh
.\setup-infra-advanced.ps1
# Máy ai yếu thì cài xong vào delete namespace staging đi nhé vì cnay nó cài tận 2 namespace dev staging là x2 RAM á
# Lệnh xóa namespace: kubectl delete namespace staging --grace-period=0
# Lưu ý: chạy xong phải chờ cho toàn bộ pod ready 1/1 nhé, khá lâu mn kiên nhẫn (chú ý con keycloak-0 và con ingress-nginx-controller phải lên 1/1).


# BƯỚC B: Tách file cấu hình & Cài đặt ArgoCD
# Đầu tiên, sinh file values riêng cho 2 môi trường (Chỉ cần chạy 1 lần duy nhất):
cd "E:\Nhập môn DevOps\Project\Project2\yas-gitops"
.\generate-values.ps1
# Cái này tui chạy và gen rồi mn khỏi cần chạy nha

# Tiếp theo, cài đặt toàn bộ ArgoCD & Root Apps:
cd "E:\Nhập môn DevOps\Project\Project2\yas-devops-1\k8s-infrastructure-scripts"
.\setup-argocd-advanced.ps1
# Máy yếu thì vào sửa script như bên trên nói, hoặc chạy script này xong vào web argoCD xóa app của staging liền đi, cách vào bên dưới
```
*(Script setup-argocd-advanced.ps1 sẽ tự động gọi sang repo `yas-gitops` và kéo toàn bộ 14 service của môi trường `dev` về máy, bao gồm cả Istio policies chart)*.

*Nếu lỗi đụng port 80 chạy lệnh sau:*
```bash
netstat -ano | findstr :80
taskkill /PID 4567 /F # 4567 là số PID tìm được ở trên
```

**👉 CÁCH TRUY CẬP ĐỂ TEST (Bắt buộc làm sau khi cài xong):**
1. **Mở cổng Ingress:** Mở 1 tab Terminal mới gõ:
   `kubectl port-forward -n ingress-nginx svc/ingress-nginx-controller 80:80`
2. **Cập nhật file hosts:** Mở Notepad (quyền Admin), mở file `C:\Windows\System32\drivers\etc\hosts` và thêm dòng này vào cuối:
   `127.0.0.1 storefront.dev.local.com backoffice.dev.local.com identity.dev.local.com`
3. **Truy cập Web:** Mở trình duyệt và vào `http://storefront.dev.local.com`
4. **Vào giao diện ArgoCD (Nếu muốn xem):** 
   - Lấy pass: `[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String((kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}")))`
   - Mở port: `kubectl port-forward svc/argocd-server -n argocd 8080:443`
   - Truy cập: `https://localhost:8080` (Tài khoản: `admin`, Pass: kết quả vừa lấy).

**5. Kiểm tra và giải thích Service Mesh (Dành cho Demo):**
Khi demo luồng Nâng Cao, bạn có thể giải thích những tính năng tự động này của GitOps:
- **Cài đặt Kiali:** Mở port `kubectl port-forward svc/kiali 20001:20001 -n istio-system`. Vào `http://localhost:20001` mục Graph.
- **mTLS STRICT (Mã hóa đường truyền):** Theo mặc định, traffic nội bộ K8s đi dưới dạng plain-text. Nhưng nhờ ArgoCD deploy cấu hình mTLS, tất cả traffic nội bộ giờ đã bị mã hóa 2 chiều (Bạn sẽ thấy icon 🔒 trên sơ đồ Kiali).
- **Zero-Trust Network (Authorization Policy):** ArgoCD tự động cấu hình quy tắc Deny All và chỉ Allow đúng các luồng hợp lệ (Ingress -> BFF -> Backend -> DB). Kẻ gian nếu vào được 1 pod cũng không thể quét mạng hay gọi bừa bãi.
- **Tự động phục hồi (VirtualService Retry):** Cấu hình Retry (tối đa 3 lần, mỗi lần 5s) đã được đính kèm vào từng Helm Chart và tự động sinh ra cho cả 14 app. Giúp hệ thống không bị crash nếu mạng chập chờn.

**6. Chạy script test Service Mesh tự động (Sau khi ArgoCD sync xong):**
```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force
cd "E:\Nhập môn DevOps\Project\Project2\yas-devops-1\k8s-infrastructure-scripts"
.\test-service-mesh.ps1
```

Script sẽ tự động chạy tuần tự cả 4 kịch bản test và in kết quả PASS/FAIL:
```
TEST 1: mTLS          -> PASS (Connection reset by peer)
TEST 2: AuthzPolicy   -> PASS (HTTP 403 Forbidden)
TEST 3: AuthzPolicy   -> PASS (HTTP 200 OK)
TEST 4: Retry Policy  -> PASS (>=8/10 thành công)
```

**3. Lệnh dọn dẹp:**
Khi muốn xóa toàn bộ để làm lại, hãy chạy:
```powershell
cd yas-devops-1\k8s-infrastructure-scripts
.\teardown-infra.ps1
```
*Hoặc xóa mẹ minikube đi cho rồi (minikube delete).*

**4. Luồng chạy thực tế GitOps (Tóm tắt):**
- **Với DEV (Tự động 100%):** Khi Merge code vào `main` của `yas-devops-1`, CI sẽ chạy Lần 3 -> Đóng gói Image -> Tự clone repo `yas-gitops` -> Sửa dòng tag trong `values-dev.yaml` thành mã commit mới -> Push lên nhánh `main` của `yas-gitops`. ArgoCD phát hiện thay đổi tự kéo Image về chạy.
- **Với STAGING (Thủ công):** Khi muốn Release, vào sửa file `values-staging.yaml` trên repo `yas-gitops` -> Tạo một Git Tag (VD: `yas-v1.0.0`) tại repo `yas-gitops` -> Vào giao diện ArgoCD tìm app `yas-root-staging`, nhấn **Edit** và đổi Target Revision thành tag `yas-v1.0.0`. ArgoCD sẽ update toàn bộ 14 app con theo tag đó!
