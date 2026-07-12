# YAS DevOps Project 02 — Final Report

> **Team**: Khoa + Huy  
> **Date**: 2026-07-06  
> **Cluster**: DigitalOcean Managed K8s (DOKS), 3 worker nodes (`s-4vcpu-8gb`)  
> **Source**: https://github.com/23122004/Y3S2-DevOps

---

## 1. Tổng Quan

Dự án xây dựng hệ thống CI/CD hoàn chỉnh cho **YAS (Yet Another Shop)** — ứng dụng microservices e-commerce gồm 14 services chính, triển khai trên Kubernetes cluster với ArgoCD GitOps, Istio Service Mesh, và Observability stack.

### Kiến Trúc Hệ Thống

```
GitHub Repository
    │
    ├── push non-main ──→ CI: Build + Push image:$SHA (ci.yml)
    │
    ├── push main ──→ CD Dev: Build + Update values.yaml → ArgoCD auto-sync (deploy-dev.yml)
    │
    ├── push tag v* ──→ CD Staging: Build + Update values.yaml → ArgoCD auto-sync (deploy-staging.yml)
    │
    └── workflow_dispatch ──→ CD Developer: Deploy per-branch images (cd-developer.yml)

Kubernetes Cluster (DOKS, 3 nodes)
    ├── dev namespace ──→ ArgoCD auto-sync from environments/dev/values.yaml
    ├── staging namespace ──→ ArgoCD auto-sync from environments/staging/values.yaml
    ├── yas-developer namespace ──→ Helm deploy from cd-developer workflow
    ├── observability namespace ──→ Prometheus + Grafana + Loki + Tempo + Promtail + OTel
    ├── istio-system ──→ Istio control plane + Kiali
    ├── argocd ──→ ArgoCD server + controllers
    └── infrastructure ──→ Postgres, Kafka, Elasticsearch, Keycloak, Redis
```

---

## 2. Yêu Cầu Bắt Buộc (6đ)

### 2.1 Kubernetes Cluster — ✅

- DigitalOcean Managed Kubernetes, 3 worker nodes `m-2vcpu-16gb` (Memory-Optimized)
- Node pool: `devops-project02-mem-opt`
- Load Balancer IP: `129.212.208.194`

### 2.2 CI Pipeline — ✅

**File**: `.github/workflows/ci.yml`

- Trigger: push to any branch (except `main`)
- Matrix build cho 14 services (backend Maven + Docker, UI Docker, static Docker)
- Tag images: `ghcr.io/23122004/yas-<service>:<commit-sha>` + `:<branch-name>`
- Push lên GitHub Container Registry (GHCR)

### 2.3 CD Developer Build — ✅

**File**: `.github/workflows/cd-developer.yml`

- Trigger: `workflow_dispatch` (manual)
- Input: per-service branch names (product_branch, cart_branch, tax_branch, ...)
- Action: `deploy` hoặc `cleanup`
- Profile: `lean` (tiết kiệm RAM, không Istio sidecar) hoặc `full` (có Istio)
- Deploy tới namespace `yas-developer`
- Resolve branch → commit SHA → deploy image tag tương ứng
- Output: URL endpoints trong GitHub Actions summary

### 2.4 Delete Deployment — ✅

Tích hợp trong `cd-developer.yml` với action=`cleanup` → xóa namespace `yas-developer`.

### 2.5 Dev + Staging Environments — ✅

| Environment | Trigger | Workflow | Namespace | ArgoCD App |
|---|---|---|---|---|
| **Dev** | Push to `main` | `deploy-dev.yml` | `dev` | `dev-yas-app` |
| **Staging** | Push tag `v*` | `deploy-staging.yml` | `staging` | `staging-yas-app` |

**Flow**: CI build images → Update `k8s/environments/{dev,staging}/values.yaml` với new image tags → Git commit → ArgoCD auto-sync.

**Dev** (14 services): Tất cả `2/2 Running` ✅  
**Staging** (14 services): Tất cả `2/2 Running` ✅

---

## 3. Yêu Cầu Nâng Cao

### 3.1 ArgoCD GitOps (Advanced 1) — ✅

- ArgoCD installed trong namespace `argocd`
- 2 Applications: `dev-yas-app`, `staging-yas-app`
- Auto-sync enabled: `prune=true`, `selfHeal=true`, `CreateNamespace=true`
- Source: `k8s/charts/yas-umbrella` + per-env values files
- Truy cập: `http://argocd.yas.local.com`

### 3.2 Service Mesh — Istio (Advanced 2) — ✅

**Installed**: Istio `1.30.2` + Kiali `v2.2`

| Feature | Resource | Trạng thái |
|---|---|---|
| **mTLS STRICT** | `PeerAuthentication/default-strict-mtls` | Applied, all pods 2/2 |
| **Public entrypoints** | PeerAuthentication PERMISSIVE cho BFFs + swagger-ui | Applied |
| **mTLS origination** | `DestinationRule/*-istio-mutual` | Applied |
| **AuthorizationPolicy** | ALLOW list cho product (order, cart, search, BFFs) | Applied |
| **Retry policy** | `VirtualService/tax-retry`, `order-retry` (3 attempts, 5xx) | Applied |
| **Kiali** | Ingress `kiali.yas.local.com`, 30 nodes / 16 edges | Running |

**Test Evidence**:

| Scenario | Command | Result |
|---|---|---|
| ALLOW (SA=order → product) | `kubectl exec mesh-tester-order -- curl product/` | HTTP 404 (reached app) ✅ |
| DENY (SA=default → product) | `kubectl exec mesh-tester-default -- curl product/` | `RBAC: access denied` 403 ✅ |
| RETRY (httpbin /status/500) | `curl httpbin:8000/status/500` | 4 server hits (1 + 3 retries) ✅ |

### 3.3 Observability (Advanced 3) — ✅

| Component | Chức năng | Trạng thái |
|---|---|---|
| **Prometheus** | Metrics collection | Running (20+ targets) |
| **Grafana** | Dashboards | Running 3/3, Ingress `grafana.yas.local.com` |
| **Loki** | Log aggregation | Running (write/read/backend/gateway) |
| **Promtail** | Log collection | Running (3 nodes DaemonSet) |
| **Tempo** | Distributed tracing | Running |
| **OpenTelemetry Collector** | Telemetry routing | Running |
| **Alertmanager** | Alert management | Running |

Datasources cấu hình tự động: Prometheus, Loki (linked to Tempo), Tempo (linked to Loki + Prometheus).

---

## 4. 14 Services Đang Chạy

| Service | Dev | Staging | Mô tả |
|---|---|---|---|
| product | 2/2 ✅ | 2/2 ✅ | Product management |
| cart | 2/2 ✅ | 2/2 ✅ | Shopping cart |
| order | 2/2 ✅ | 2/2 ✅ | Order processing |
| customer | 2/2 ✅ | 2/2 ✅ | Customer info |
| inventory | 2/2 ✅ | 2/2 ✅ | Inventory tracking |
| tax | 2/2 ✅ | 2/2 ✅ | Tax calculation |
| media | 2/2 ✅ | 2/2 ✅ | Media upload |
| search | 2/2 ✅ | 2/2 ✅ | Product search |
| storefront-bff | 2/2 ✅ | 2/2 ✅ | Storefront BFF |
| storefront-ui | 2/2 ✅ | 2/2 ✅ | Store frontend |
| backoffice-bff | 2/2 ✅ | 2/2 ✅ | Admin BFF |
| backoffice-ui | 2/2 ✅ | 2/2 ✅ | Admin frontend |
| swagger-ui | 2/2 ✅ | 2/2 ✅ | API docs |
| sampledata | 2/2 ✅ | 2/2 ✅ | Sample data loader |

---

## 5. Truy Cập Hệ Thống

| Service | URL |
|---|---|
| ArgoCD | `http://argocd.yas.local.com` |
| Grafana | `http://grafana.yas.local.com` (admin/admin) |
| Kiali | `http://kiali.yas.local.com` |
| Storefront Dev | `http://storefront.dev.yas.local.com` |
| Backoffice Dev | `http://backoffice.dev.yas.local.com` |
| Swagger Dev | `http://api.dev.yas.local.com/swagger-ui` |
| Storefront Staging | `http://storefront.staging.yas.local.com` |
| Backoffice Staging | `http://backoffice.staging.yas.local.com` |
| PgAdmin | `http://pgadmin.yas.local.com` |
| Kibana | `http://kibana.yas.local.com` |
| AKHQ (Kafka) | `http://akhq.yas.local.com` |

Hosts file entry:
```
129.212.208.194  argocd.yas.local.com grafana.yas.local.com kiali.yas.local.com storefront.dev.yas.local.com backoffice.dev.yas.local.com api.dev.yas.local.com storefront.staging.yas.local.com backoffice.staging.yas.local.com api.staging.yas.local.com pgadmin.yas.local.com kibana.yas.local.com akhq.yas.local.com identity.yas.local.com
```

---

## 6. Repository Structure

```
.github/workflows/
├── ci.yml                    # CI: build + push images per branch
├── cd-developer.yml          # CD: developer_build with branch inputs
├── deploy-dev.yml            # CD: auto-deploy dev on push main
├── deploy-staging.yml        # CD: deploy staging on tag v*
├── charts-ci.yaml            # Helm chart releases
├── codeql.yml                # Code security scanning
├── gitleaks-check.yaml       # Secret leak detection
└── *-ci.yaml                 # Per-service CI workflows

k8s/
├── charts/                   # Helm charts (umbrella + per-service)
├── environments/             # Per-env values (dev, staging)
├── deploy/                   # Cluster setup scripts + observability configs
├── istio/                    # Service mesh manifests (mTLS, authz, retry)
└── namespaces.yaml           # Namespace definitions

docs/                         # Documentation
screenshots/                  # Reference screenshots for demo
```

---

## 7. GitHub Secrets & Variables

| Type | Key | Purpose |
|---|---|---|
| Secret | `DIGITALOCEAN_ACCESS_TOKEN` | DOKS cluster access |
| Variable | `DOKS_CLUSTER_NAME` | Cluster name for kubeconfig |
| Variable | `BASE_DOMAIN` | `yas.local.com` |
