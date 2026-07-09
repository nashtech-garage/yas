# ⚙️ Istio Service Mesh Configuration for YAS

This directory contains the Istio Service Mesh configuration manifests for the **YAS (Yet Another Shop)** microservice application, covering mTLS, Traffic Routing (Retry & Timeout), and Service-to-Service Authorization Policies.

## 📁 Directory Structure

```text
istio/
├── README.md                          # This documentation
├── mtls/
│   ├── peer-authentication.yaml       # STRICT mTLS enforcement for dev, staging, and sandbox namespaces
│   └── destination-rule.yaml          # DestinationRules to use ISTIO_MUTUAL TLS
├── traffic/
│   ├── virtual-service-product.yaml   # Retry (3 attempts) & Timeout (10s) for product service
│   ├── virtual-service-tax.yaml       # Retry (2 attempts) & Timeout (5s) for tax service
│   ├── virtual-service-inventory.yaml # Retry (3 attempts) & Timeout (10s) for inventory service
│   └── virtual-service-cart.yaml      # Retry (3 attempts) & Timeout (10s) for cart service
└── security/
    └── authz-policies.yaml            # Least-privilege communication rules for all core microservices
```

---

## 🚀 How to Apply

To apply these configurations to your Kubernetes cluster, run the following commands from the project root directory:

### 1. Apply mTLS Policies
```bash
kubectl apply -f istio/mtls/peer-authentication.yaml
kubectl apply -f istio/mtls/destination-rule.yaml
```

### 2. Apply Traffic Routing Policies (Retries & Timeouts)
```bash
kubectl apply -f istio/traffic/virtual-service-product.yaml
kubectl apply -f istio/traffic/virtual-service-tax.yaml
kubectl apply -f istio/traffic/virtual-service-inventory.yaml
kubectl apply -f istio/traffic/virtual-service-cart.yaml
```

### 3. Apply Authorization Policies
```bash
kubectl apply -f istio/security/authz-policies.yaml
```

---

## 🔍 Verification Commands

### 1. Verify STRICT mTLS

Check that the PeerAuthentication mode is `STRICT`:
```bash
kubectl get peerauthentication -n dev
kubectl get peerauthentication -n staging
```

Execute a connection test from an un-injected pod or another namespace (e.g., standard `default` namespace) to a backend service in `dev` to verify it gets rejected due to lack of client TLS credentials:
```bash
# Spin up an un-injected temporary curl pod
kubectl run curl-test --image=curlimages/curl --restart=Never -it --rm -- curl -v http://product.dev.svc.cluster.local/actuator/health
```
*Expected Result:* Connection refused/reset, or handshake failure, indicating STRICT mTLS is blocking non-mesh traffic.

### 2. Verify Retry Policy

We configured a retry policy for `tax`, `product`, `inventory`, and `cart`.
You can view the active virtual service configuration with:
```bash
kubectl get virtualservices -n dev
```

To see retries in action, you can temporarily simulate errors in the target service (e.g., stopping or corrupting the backend server database, or injecting faults via an Istio VirtualService Fault Injection) and watch the client service logs or Envoy sidecar logs:
```bash
# Monitor Envoy sidecar access logs in the caller service (e.g. order calling tax)
kubectl logs -f deployment/order -c istio-proxy -n dev
```
Look for retry count flags or multiple request attempts for the same transaction ID when an HTTP 500 error occurs.

### 3. Verify Authorization Policies

Let's test the least-privilege connection rules.
According to `authz-policies.yaml`, `cart` can **only** be called by `storefront-bff` and `order`. `product` can be called by `cart`, `order`, etc., but **not** by arbitrary external services.

*   **Test Case A: Allowed Access**
    Run a request from `order` to `cart`:
    ```bash
    # Exec into order pod and curl cart
    ORDER_POD=$(kubectl get pod -l app.kubernetes.io/name=order -n dev -o jsonpath='{.items[0].metadata.name}')
    kubectl exec -n dev $ORDER_POD -c order -- curl -s -o /dev/null -w "%{http_code}\n" http://cart/actuator/health
    ```
    *Expected Result:* `200` (Access Allowed)

*   **Test Case B: Blocked Access**
    Run a request from `search` to `cart` (this is NOT allowed by our policy):
    ```bash
    # Exec into search pod and curl cart
    SEARCH_POD=$(kubectl get pod -l app.kubernetes.io/name=search -n dev -o jsonpath='{.items[0].metadata.name}')
    kubectl exec -n dev $SEARCH_POD -c search -- curl -s -o /dev/null -w "%{http_code}\n" http://cart/actuator/health
    ```
    *Expected Result:* `403` (RBAC: access denied)
