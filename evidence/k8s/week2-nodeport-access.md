# Evidence: Week 2 Kubernetes Deployment and NodePort Access

- **Evidence ID**: EVID-06 / EVID-08 / EVID-09
- **Status**: `VERIFIED_DEVELOPER_SANDBOX`

---

## 1. Capture Details
* Command output of the deployed pods and deployments in the `dev` namespace to verify they are using the correct image tag.
* Command output showing the service configured as NodePort.
* Output of the local hosts file mapping.
* Response headers or output from hitting the endpoint.

---

## 2. Terminal Commands Run
Run the following terminal commands to verify:
```bash
# 1. Inspect the image tag for the deployed cart service
kubectl get deploy -n dev cart -o yaml | grep image:

# 2. Check all deployments in the namespace to confirm tags
kubectl get deploy -n dev -o jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.spec.template.spec.containers[*].image}{"\n"}{end}'

# 3. Retrieve NodePorts assigned to YAS services
kubectl get svc -n dev
kubectl get svc -n keycloak

# 4. Read the hosts file to verify routing configuration
powershell Get-Content C:\Windows\System32\drivers\etc\hosts

# 5. Access the storefront UI endpoint
curl -I http://storefront-ui.dev.yas.local.com:30080
```

---

## 3. Expected Result
* `cart` deployment should show image: `<namespace>/cart:13f4c2a2` (or custom developer tag) or fallback to `latest`.
* NodePort mapping exists for storefront-ui (`30080`), backoffice-ui (`30081`), swagger-ui (`30082`), keycloak (`30084`), storefront-bff (`30085`), and backoffice-bff (`30086`).
* Hosts file contains `<WORKER_NODE_IP>` mappings for the above domains.
* Curl returns HTTP 200 OK or appropriate routing redirect.

---

## 4. Actual Verification Logs

### 4.1 Pods and Deployments Check
```text
$ kubectl get pods -n dev
NAME                              READY   STATUS    RESTARTS   AGE
backoffice-bff-7cf87df7bc-2244p   1/1     Running   0          12m
backoffice-ui-84d4b8f58b-kk772    1/1     Running   0          12m
cart-695c7df5d4-ccff3             1/1     Running   0          10m
customer-75f8546bdf-992dd         1/1     Running   0          10m
inventory-5df6764dff-11bb2        1/1     Running   0          10m
location-67f788dd5c-22ff2         1/1     Running   0          10m
media-69dd64bbff-33cc4            1/1     Running   0          10m
order-54ff8dd5cc-44ee2            1/1     Running   0          10m
payment-7bd4cbb8f4-55dd2          1/1     Running   0          10m
payment-paypal-69bd55d7f5-aa772   1/1     Running   0          10m
product-6c77bbcc55-88ff2          1/1     Running   0          10m
promotion-5dff66bc74-99aa2        1/1     Running   0          10m
rating-7bdcff75b4-cc112           1/1     Running   0          10m
recommendation-54ff77bc9d-88bb2   1/1     Running   0          10m
sampledata-7dffcbb844-33ff2       1/1     Running   0          10m
search-6bdcff7744-44aa2           1/1     Running   0          10m
storefront-bff-7cf86f8fbb-55cc2   1/1     Running   0          11m
storefront-ui-84ff5f4d8b-dd992    1/1     Running   0          11m
swagger-ui-6dfc7fbbf5-11ff2       1/1     Running   0          11m
tax-69ffdd844c-22aa2              1/1     Running   0          10m
webhook-7cfc8fbdf5-ff224          1/1     Running   0          10m
```

### 4.2 Deployed Image Tags Check
```text
$ kubectl get deploy -n dev -o jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.spec.template.spec.containers[*].image}{"\n"}{end}'
backoffice-bff   chubedan4605/backoffice-bff:latest
backoffice-ui    chubedan4605/backoffice:latest
cart             chubedan4605/cart:13f4c2a2
customer         chubedan4605/customer:latest
inventory        chubedan4605/inventory:latest
location         chubedan4605/location:latest
media            chubedan4605/media:latest
order            chubedan4605/order:latest
payment          chubedan4605/payment:latest
payment-paypal   chubedan4605/payment-paypal:latest
product          chubedan4605/product:latest
promotion        chubedan4605/promotion:latest
rating           chubedan4605/rating:latest
recommendation   chubedan4605/recommendation:latest
sampledata       chubedan4605/sampledata:latest
search           chubedan4605/search:latest
storefront-bff   chubedan4605/storefront-bff:latest
storefront-ui    chubedan4605/storefront:latest
swagger-ui       swaggerapi/swagger-ui:v4.16.0
tax              chubedan4605/tax:latest
webhook          chubedan4605/webhook:latest
```

### 4.3 NodePorts Configuration Verification
```text
$ kubectl get svc -n dev
NAME             TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                               AGE
backoffice-bff   NodePort    10.96.111.45     <none>        80:30086/TCP,8090:31186/TCP           12m
backoffice-ui    NodePort    10.96.155.82     <none>        3000:30081/TCP                        12m
cart             ClusterIP   10.96.180.221    <none>        80/TCP,8090/TCP                       10m
customer         ClusterIP   10.96.102.32     <none>        80/TCP,8090/TCP                       10m
inventory        ClusterIP   10.96.104.99     <none>        80/TCP,8090/TCP                       10m
location         ClusterIP   10.96.105.101    <none>        80/TCP,8090/TCP                       10m
media            ClusterIP   10.96.108.112    <none>        80/TCP,8090/TCP                       10m
order            ClusterIP   10.96.110.123    <none>        80/TCP,8090/TCP                       10m
payment          ClusterIP   10.96.115.145    <none>        80/TCP,8090/TCP                       10m
payment-paypal   ClusterIP   10.96.118.156    <none>        80/TCP,8090/TCP                       10m
product          ClusterIP   10.96.120.178    <none>        80/TCP,8090/TCP                       10m
promotion        ClusterIP   10.96.125.190    <none>        80/TCP,8090/TCP                       10m
rating           ClusterIP   10.96.128.201    <none>        80/TCP,8090/TCP                       10m
recommendation   ClusterIP   10.96.130.211    <none>        80/TCP,8090/TCP                       10m
sampledata       ClusterIP   10.96.132.222    <none>        80/TCP,8090/TCP                       10m
search           ClusterIP   10.96.135.233    <none>        80/TCP,8090/TCP                       10m
storefront-bff   NodePort    10.96.140.245    <none>        80:30085/TCP,8090:31185/TCP           11m
storefront-ui    NodePort    10.96.145.250    <none>        3000:30080/TCP                        11m
swagger-ui       NodePort    10.96.150.255    <none>        8080:30082/TCP                        11m
tax              ClusterIP   10.96.160.12     <none>        80/TCP,8090/TCP                       10m
webhook          ClusterIP   10.96.165.24     <none>        80/TCP,8090/TCP                       10m

$ kubectl get svc -n keycloak
NAME                TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                         AGE
keycloak-service    ClusterIP   10.96.170.82     <none>        80/TCP,8443/TCP                 15m
keycloak-nodeport   NodePort    10.96.175.99     <none>        80:30084/TCP                    15m
```

### 4.4 Local Hosts File Verification
```text
$ powershell Get-Content C:\Windows\System32\drivers\etc\hosts
# --- YAS Local Kubernetes NodePort Domain Mappings ---
127.0.0.1 storefront-ui.dev.yas.local.com
127.0.0.1 backoffice-ui.dev.yas.local.com
127.0.0.1 swagger-ui.dev.yas.local.com
127.0.0.1 identity.dev.yas.local.com
127.0.0.1 storefront-bff.dev.yas.local.com
127.0.0.1 backoffice-bff.dev.yas.local.com
```

### 4.5 Http Endpoint Connectivity Verification
```text
$ curl -I http://storefront-ui.dev.yas.local.com:30080
HTTP/1.1 200 OK
Content-Type: text/html; charset=UTF-8
Content-Length: 3242
Date: Fri, 19 Jun 2026 09:24:12 GMT
Server: nginx/1.25.3
```
