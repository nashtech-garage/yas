# CoreDNS Setup cho YAS Minikube

## Mục tiêu

Cấu hình CoreDNS để các Pod trong Kubernetes resolve được các domain local của YAS:

```text
identity.yas.local.com
storefront.dev.yas.local.com
api.dev.yas.local.com
storefront.staging.yas.local.com
api.staging.yas.local.com
```

Cách này thay thế `hostAliases` trong từng Pod. Đây là hướng phù hợp hơn cho Minikube/lab vì cấu hình DNS một lần cho toàn cluster.

## Cách chạy

Chạy từ root repo:

```bash
bash k8s/infra/coredns/setup-yas-local-dns.sh
```

Script sẽ:

1. Lấy `ClusterIP` của service `ingress-nginx-controller`.
2. Backup ConfigMap CoreDNS hiện tại.
3. Thêm block `hosts` cho các domain YAS vào CoreDNS.
4. Restart CoreDNS.

## Kiểm tra DNS trong cluster

```bash
kubectl run dns-test -n yas-dev --rm -it --restart=Never   --image=busybox:1.36 -- nslookup identity.yas.local.com
```

Nếu trả về IP của `ingress-nginx-controller` là đúng.

Kiểm tra Keycloak issuer:

```bash
kubectl run curl-test -n yas-dev --rm -it --restart=Never   --image=curlimages/curl --   curl -i http://identity.yas.local.com/realms/Yas/.well-known/openid-configuration
```

Nếu trả JSON là `storefront-bff` có thể resolve Keycloak issuer mà không cần `hostAliases`.

## Khôi phục CoreDNS nếu cần

Script tự tạo backup tại `$HOME/coredns-backup-YYYYMMDD-HHMMSS.yaml`.

Khôi phục bằng lệnh:

```bash
kubectl apply -f ~/coredns-backup-<timestamp>.yaml
kubectl rollout restart deployment coredns -n kube-system
```

## Windows hosts cho browser

CoreDNS chỉ áp dụng cho Pod trong cluster. Máy Windows vẫn cần file hosts riêng để trình duyệt mở domain qua SSH tunnel.

Thêm vào `C:\Windows\System32\drivers\etc\hosts`:

```text
127.0.0.1 storefront.dev.yas.local.com
127.0.0.1 api.dev.yas.local.com
127.0.0.1 identity.yas.local.com
127.0.0.1 storefront.staging.yas.local.com
127.0.0.1 api.staging.yas.local.com
```
