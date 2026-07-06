# Service Mesh Report — AuthorizationPolicy, Kiali, Test Plan (huy_job)

Owner: Huy. Scope: the four huy_job tasks — AuthorizationPolicy, Kiali topology,
test plan, and executed allow/deny/retry evidence — on the DigitalOcean (DOKS)
cluster, namespace `yas`, Istio `1.30.2`, STRICT mTLS.

## 0. Mesh baseline (already in place, verified)

| Concern | Resource | State |
|---|---|---|
| mTLS | `PeerAuthentication/default-strict-mtls` | `STRICT` |
| Public entrypoints | PeerAuthentication for storefront-bff / backoffice-bff / swagger-ui | `PERMISSIVE` (so NGINX Ingress reaches them) |
| mTLS origination | `DestinationRule/*-istio-mutual` | `ISTIO_MUTUAL` + connection pools |
| Retry | `VirtualService/tax-retry`, `order-retry` | attempts=3, retryOn 5xx |

All `yas` app pods run `2/2` (app + istio-proxy sidecar).

## 1. AuthorizationPolicy (task 1)

Requirement: *only allow order-service to call product-service, block others.*

`product` is called by more than `order` in the live shop (the BFFs and `cart`
browse products), so a literal order-only rule would break the running
storefront that must stay demoable. Two manifests are provided:

- **`k8s/istio/authorization-policies.yaml`** (applied): an `ALLOW` policy scoped
  to `product` that permits its real callers
  (`order`, `cart`, `search`, `storefront-bff`, `backoffice-bff`).
  An `ALLOW` policy **implicitly denies every principal not listed**, so all
  other identities (e.g. `tax`, `customer`, `media`, `default`) are blocked.
- **`k8s/istio/demo/authorization-policy-strict-demo.yaml`** (on-demand): the
  exact assignment scenario — `product` reachable **only** from `order`.
  Apply it for a pure allow/deny demo, then delete it to restore the shop.

Principals are SPIFFE identities from each service's ServiceAccount under STRICT
mTLS: `cluster.local/ns/yas/sa/<service>`.

Testing traffic must originate from a **normal (captured) container** so the
sidecar wraps it in mTLS. `curl` run inside the `istio-proxy` container itself
runs as UID 1337, bypasses capture, and hits a STRICT peer as plaintext →
connection reset. Hence two dedicated sidecar-injected clients
(`k8s/istio/demo/mesh-test-clients.yaml`):
`mesh-tester-order` (SA=order, allowed) and `mesh-tester-default` (SA=default, denied).

## 2. Kiali + topology (task 2)

- Installed the lightweight Istio addons: **Prometheus** (scrapes Envoy
  sidecar metrics) and **Kiali** `v2.2` in `istio-system`
  (`istio-1.30.2/samples/addons/{prometheus,kiali}.yaml`). Chosen over the heavy
  kube-prometheus stack because the cluster is memory-constrained (see worklog).
- Exposed Kiali via NGINX Ingress: `k8s/istio/kiali-ingress.yaml`
  → `http://kiali.yas.local.com` (add `<LB_IP> kiali.yas.local.com` to hosts).
- Verified populated: Prometheus holds `istio_requests_total` across **11
  destination services**; Kiali service graph renders **30 nodes / 16 edges**
  (product, cart, order, tax, customer, inventory, search, media,
  storefront-bff/ui, backoffice-bff/ui, swagger-ui, postgres, kafka).
- **Screenshot to capture for the report:** Kiali → Graph → namespace `yas`,
  "Versioned app graph", enable Traffic Animation + Security (padlocks show
  mTLS edges).

## 3. Test plan

Prereqs: `kubectl apply -f k8s/istio/demo/mesh-test-clients.yaml` and
`kubectl apply -f k8s/istio/demo/retry-demo-httpbin.yaml`; wait for `2/2`.

| # | Scenario | Command | Expected |
|---|---|---|---|
| 1 | Authz ALLOW | `kubectl exec -n yas mesh-tester-order -c tester -- curl -s -o /dev/null -w '%{http_code}' http://product.yas.svc.cluster.local/` | reaches app (not 403) |
| 2 | Authz DENY | `kubectl exec -n yas mesh-tester-default -c tester -- curl -s http://product.yas.svc.cluster.local/` | `RBAC: access denied` / 403 |
| 3 | Retry | `kubectl exec -n yas mesh-tester-order -c tester -- curl -s -o /dev/null -w '%{http_code}' http://httpbin.yas.svc.cluster.local:8000/status/500` then count httpbin hits | 1 client call → 4 server hits (1 + 3 retries) |
| 4 | mTLS enforced | `kubectl exec -n yas deploy/tax -c istio-proxy -- curl -s http://product.yas.svc.cluster.local/` (plaintext, non-captured) | connection reset (STRICT rejects plaintext) |
| 5 | Strict variant | apply `demo/authorization-policy-strict-demo.yaml`; repeat #1 from `mesh-tester-default` | 403; then delete to restore |

## 4. Executed evidence (2026-07-05)

```
### SCENARIO 1 — ALLOW (SA=order -> product)
HTTP 404   (reached app; Envoy permitted, 404 only because "/" is not a product route)

### SCENARIO 2 — DENY (SA=default -> product)
RBAC: access denied
HTTP 403
# product istio-proxy access log:
"GET / HTTP/1.1" 403 - rbac_access_denied_matched_policy[none] ... inbound|80|| ...

### SCENARIO 3 — RETRY (httpbin /status/500, attempts=3)
client received: HTTP 500
httpbin server received: 4 requests  (= 1 original + 3 retries)
```

Raw capture: `docs/service-mesh-test-evidence.txt`.

## 5. Deliverables checklist

- [x] YAML: mTLS (`peer-authentication.yaml`), authz (`authorization-policies.yaml` + strict demo), retry (`virtual-services.yaml`, `retry-demo-httpbin.yaml`)
- [x] Kiali installed + exposed; topology graph populated (screenshot pending, human)
- [x] Test plan + executed logs (allow / deny / retry) — this doc + evidence file
- [x] Reproducible: `k8s/istio/install-istio.sh`, demo artifacts in `k8s/istio/demo/`
