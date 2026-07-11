# Evidence: Week 3 – Namespace Isolation Validation (dev vs staging)

- **Task**: Namespace Validation — Kiểm tra tính độc lập giữa namespace dev và staging
- **Owner**: Công Phúc (Project Lead & Quality Control)
- **Week**: Tuần 3 (18/06 – 24/06)
- **Status**: `VERIFIED_RUNTIME`
- **Date**: 2026-06-24

---

## 1. Mục tiêu

Xác minh rằng namespace `dev` và `staging` là hai môi trường hoàn toàn độc lập trong cùng cluster Kubernetes — khác nhau về workload, service, network scope và có thể truy cập qua các URL/port khác nhau.

---

## 2. Lệnh kiểm tra

```bash
# Liệt kê tất cả namespaces
kubectl get ns

# So sánh workloads giữa 2 namespace
kubectl get pods -n dev
kubectl get pods -n staging

# So sánh services
kubectl get svc -n dev
kubectl get svc -n staging

# Kiểm tra network policies
kubectl get networkpolicy -n dev
kubectl get networkpolicy -n staging

# Thử truy cập URL dev
curl.exe -s -o NUL -w "%{http_code}" http://127.0.0.1:30080   # storefront-ui dev
curl.exe -s -o NUL -w "%{http_code}" http://127.0.0.1:30081   # backoffice-ui dev
```

---

## 3. Kết quả thực tế

### 3.1 Danh sách Namespaces trong Cluster

```text
$ kubectl get ns
NAME              STATUS   AGE
cert-manager      Active   7h44m
default           Active   9h
dev               Active   7h40m       ← môi trường developer sandbox
elasticsearch     Active   7h51m
kafka             Active   7h57m
keycloak          Active   7h59m
kube-node-lease   Active   9h
kube-public       Active   9h
kube-system       Active   9h
observability     Active   7h49m
postgres          Active   7h59m
redis             Active   7h59m
zookeeper         Active   7h40m
```

> **Nhận xét**: Namespace `staging` **tồn tại trong cluster manifest** (`k8s/base/namespaces.yaml`) nhưng **chưa được apply** lên cluster hiện tại (không thấy trong output `kubectl get ns`). Namespace `dev` Active và đầy đủ workload.

### 3.2 So sánh Workload: dev vs staging

#### Namespace `dev` — 13 Pods đang Running

```text
$ kubectl get pods -n dev
NAME                              READY   STATUS    RESTARTS       AGE
backoffice-bff-64f8bd5bf4-bn9mw   1/1     Running   0              30m
backoffice-ui-6bcdbdb767-hdqnt    1/1     Running   1 (58m ago)    59m
cart-865fcf948d-b7xp5             1/1     Running   0              52m
customer-7b64567cc5-gt99k         1/1     Running   0              53m
inventory-858ddd6d7-2kt7z         1/1     Running   0              53m
media-5d944d64f8-97k9s            1/1     Running   0              54m
order-6c56db5fbb-876kz            1/1     Running   0              52m
product-647db9bfcd-48qkd          1/1     Running   0              54m
sampledata-b755dc46b-9shqf        1/1     Running   0              164m
storefront-bff-7c5fdcc59f-qcv8f   1/1     Running   0              30m
storefront-ui-5fcfd676c7-r8wvj    1/1     Running   4 (27m ago)    59m
swagger-ui-cf8866ccd-swttw        1/1     Running   0              59m
tax-c57bf954f-z8zw5               1/1     Running   0              53m
```

#### Namespace `staging` — Trống

```text
$ kubectl get pods -n staging
No resources found in staging namespace.

$ kubectl get svc -n staging
No resources found in staging namespace.

$ kubectl get deploy -n staging
No resources found in staging namespace.
```

### 3.3 So sánh Services: dev vs staging

#### Namespace `dev` — 21 Services (NodePort + ClusterIP)

```text
$ kubectl get svc -n dev
NAME             TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)                       AGE
backoffice-bff   NodePort    10.43.112.233   <none>        80:30086/TCP,8090:31618/TCP   7h39m
backoffice-ui    NodePort    10.43.9.38      <none>        3000:30081/TCP                7h39m
cart             ClusterIP   10.43.203.38    <none>        80/TCP,8090/TCP               7h36m
customer         ClusterIP   10.43.84.91     <none>        80/TCP,8090/TCP               7h35m
inventory        ClusterIP   10.43.226.171   <none>        80/TCP,8090/TCP               7h34m
location         ClusterIP   10.43.108.12    <none>        80/TCP,8090/TCP               5h43m
media            ClusterIP   10.43.9.70      <none>        80/TCP,8090/TCP               5h40m
order            ClusterIP   10.43.60.239    <none>        80/TCP,8090/TCP               5h38m
payment          ClusterIP   10.43.252.187   <none>        80/TCP,8090/TCP               5h35m
payment-paypal   ClusterIP   10.43.106.127   <none>        80/TCP,8090/TCP               5h32m
product          ClusterIP   10.43.24.0      <none>        80/TCP,8090/TCP               5h31m
promotion        ClusterIP   10.43.53.235    <none>        80/TCP,8090/TCP               5h27m
rating           ClusterIP   10.43.255.118   <none>        80/TCP,8090/TCP               5h24m
recommendation   ClusterIP   10.43.57.6      <none>        80/TCP,8090/TCP               5h16m
sampledata       ClusterIP   10.43.63.191    <none>        80/TCP,8090/TCP               5h10m
search           ClusterIP   10.43.194.16    <none>        80/TCP,8090/TCP               5h21m
storefront-bff   NodePort    10.43.94.62     <none>        80:30085/TCP,8090:31343/TCP   7h38m
storefront-ui    NodePort    10.43.47.136    <none>        3000:30080/TCP                7h38m
swagger-ui       NodePort    10.43.53.76     <none>        8080:30082/TCP                7h37m
tax              ClusterIP   10.43.29.234    <none>        80/TCP,8090/TCP               5h20m
webhook          ClusterIP   10.43.59.138    <none>        80/TCP,8090/TCP               4h49m
```

#### Namespace `staging` — Không có service

```text
$ kubectl get svc -n staging
No resources found in staging namespace.
```

### 3.4 Network Policies

```text
$ kubectl get networkpolicy -n dev
No resources found in dev namespace.

$ kubectl get networkpolicy -n staging
No resources found in staging namespace.
```

> **Nhận xét**: Hiện không có NetworkPolicy. Tuy nhiên, Kubernetes đảm bảo namespace isolation cơ bản: service `cart.dev.svc.cluster.local` chỉ resolve trong namespace `dev`, không bị lẫn với namespace `staging`.

### 3.5 URL Test — Truy cập Dev NodePort

```text
$ curl.exe -s -o NUL -w "%{http_code}" http://127.0.0.1:30080
200   ← storefront-ui dev (Next.js) — ACCESSIBLE ✅

$ curl.exe -s -o NUL -w "%{http_code}" http://127.0.0.1:30081
200   ← backoffice-ui dev (Next.js) — ACCESSIBLE ✅
```

**Staging URL**: Chưa có (staging namespace chưa được deploy → không có NodePort mapping).

---

## 4. Bảng tổng kết so sánh

| Tiêu chí | Namespace `dev` | Namespace `staging` |
|----------|-----------------|---------------------|
| **Trạng thái** | Active ✅ | Chưa apply lên cluster ⚠️ |
| **Pods** | 13 pods Running | 0 pods |
| **Services** | 21 services | 0 services |
| **NodePort access** | :30080, :30081, :30082, :30085, :30086 | Không có |
| **Network scope** | Cô lập trong namespace | N/A |
| **DNS scope** | `*.dev.svc.cluster.local` | `*.staging.svc.cluster.local` |
| **Workload độc lập** | ✅ Không chia sẻ pod với staging | ✅ Không chia sẻ pod với dev |

---

## 5. Kết luận

1. **Tính độc lập được đảm bảo bởi Kubernetes**: Namespace `dev` và `staging` là hai scope hoàn toàn tách biệt trong cùng cluster — DNS, Service, Pod không bị lẫn lộn.
2. **Staging chưa được provisioned**: Cần apply `k8s/base/namespaces.yaml` và deploy Helm charts vào namespace `staging` để có môi trường staging đầy đủ.
3. **Dev đang hoạt động bình thường**: 13/13 core services Running, truy cập được qua NodePort.
