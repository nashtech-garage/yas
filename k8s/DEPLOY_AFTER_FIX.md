# Deploy sau khi cập nhật bản fix K8s

## 1. Kết luận

Không cần deploy lại từ đầu. Không cần xóa namespace, không cần cài lại Minikube.

Chỉ cần:

```bash
git pull
bash k8s/infra/coredns/setup-yas-local-dns.sh
helm dependency build các chart liên quan
helm upgrade --install các release liên quan
```

## 2. Các phần đã fix trong bản này

- `storefront-ui` dev/staging dùng đúng `API_BASE_PATH`.
- `storefront-ui` dùng `ui.image.tag: main`.
- `storefront-bff` dev/staging dùng đúng `backend.ingress.host`.
- `storefront-bff` không dùng `hostAliases` trong env values.
- CoreDNS script được dùng để resolve `identity.yas.local.com` trong cluster.
- `backend` chart có `startupProbe`, `JAVA_TOOL_OPTIONS`, liveness/readiness phù hợp Minikube.
- `gatewayRoutesConfig` dùng key mới `spring.cloud.gateway.server.webflux.routes`.
- BFF route `/api/**` sang internal service `http://nginx`.
- Internal nginx rewrite `/api/product/...` thành `/product/...`, `/api/cart/...` thành `/cart/...`, v.v.
- `yas-api-routes` dùng `.Values.hosts.api` để tách dev/staging.

## 3. Pull code mới trên VM

```bash
cd ~/yas_devops_gitcheck
git pull
```

Nếu đang dùng branch riêng:

```bash
git checkout <branch-cua-ban>
git pull
```

## 4. Cấu hình CoreDNS

Chạy một lần sau khi pull bản fix:

```bash
bash k8s/infra/coredns/setup-yas-local-dns.sh
```

Test DNS trong cluster:

```bash
kubectl run dns-test -n yas-dev --rm -it --restart=Never \
  --image=busybox:1.36 -- nslookup identity.yas.local.com
```

Test Keycloak issuer:

```bash
kubectl run curl-test -n yas-dev --rm -it --restart=Never \
  --image=curlimages/curl -- \
  curl -i http://identity.yas.local.com/realms/Yas/.well-known/openid-configuration
```

## 5. Build Helm dependencies

```bash
for svc in product cart customer inventory media order tax storefront-bff; do
  helm dependency build k8s/charts/$svc
done

helm dependency build k8s/charts/storefront-ui
helm dependency build k8s/charts/yas-configuration
```

Nếu chart nào báo không có dependency để update thì bỏ qua.

## 6. Upgrade dev

```bash
helm upgrade --install yas-configuration k8s/charts/yas-configuration \
  -n yas-dev \
  -f k8s/environments/dev/yas-configuration-values.yaml
```

```bash
helm upgrade --install product k8s/charts/product -n yas-dev --set backend.image.tag=main
helm upgrade --install cart k8s/charts/cart -n yas-dev --set backend.image.tag=main
helm upgrade --install customer k8s/charts/customer -n yas-dev --set backend.image.tag=main
helm upgrade --install inventory k8s/charts/inventory -n yas-dev --set backend.image.tag=main
helm upgrade --install media k8s/charts/media -n yas-dev --set backend.image.tag=main
helm upgrade --install order k8s/charts/order -n yas-dev --set backend.image.tag=main
helm upgrade --install tax k8s/charts/tax -n yas-dev --set backend.image.tag=main
```

```bash
helm upgrade --install storefront-ui k8s/charts/storefront-ui \
  -n yas-dev \
  -f k8s/environments/dev/storefront-ui-values.yaml
```

```bash
helm upgrade --install storefront-bff k8s/charts/storefront-bff \
  -n yas-dev \
  -f k8s/environments/dev/storefront-bff-values.yaml
```

## 7. Kiểm tra runtime

```bash
kubectl get pods -n yas-dev
kubectl get ingress -n yas-dev -o wide
```

Expected ingress:

```text
storefront-bff   nginx   storefront.dev.yas.local.com
swagger-ui       nginx   api.dev.yas.local.com
yas-api-routes   nginx   api.dev.yas.local.com
```

Check `storefront-ui`:

```bash
kubectl get deploy storefront-ui -n yas-dev \
  -o jsonpath='{.spec.template.spec.containers[0].image}{"\n"}{.spec.template.spec.containers[0].env[?(@.name=="API_BASE_PATH")].value}{"\n"}'
```

Expected:

```text
nguyenmanhha/yas-storefront:main
http://storefront.dev.yas.local.com/api
```

## 8. Test API

```bash
curl -I -H "Host: storefront.dev.yas.local.com" http://$(minikube ip)/
```

```bash
curl -i -H "Host: storefront.dev.yas.local.com" \
  http://$(minikube ip)/api/product/storefront/categories | head -40
```

Nếu vẫn lỗi, test từng lớp:

```bash
kubectl run tmp-curl -n yas-dev --rm -it --restart=Never \
  --image=curlimages/curl -- \
  curl -i http://storefront-bff/api/product/storefront/categories
```

```bash
kubectl run tmp-curl -n yas-dev --rm -it --restart=Never \
  --image=curlimages/curl -- \
  curl -i http://nginx/api/product/storefront/categories
```

## 9. Windows hosts và SSH tunnel

Windows hosts vẫn cần cho browser:

```text
127.0.0.1 storefront.dev.yas.local.com
127.0.0.1 api.dev.yas.local.com
127.0.0.1 identity.yas.local.com
127.0.0.1 storefront.staging.yas.local.com
127.0.0.1 api.staging.yas.local.com
```

SSH tunnel:

```powershell
ssh -i yas_key.pem -L 80:192.168.49.2:80 hd@20.24.209.134
```

Mở browser:

```text
http://storefront.dev.yas.local.com
http://api.dev.yas.local.com/swagger-ui
```
