# Đồ Án 2 — CD Pipeline: Kế Hoạch Tổng Thể

## I. Kiến Trúc Hệ Thống

```
Developer push code
        │
        ▼
GitHub Webhook ──► Jenkins Controller (AWS EC2 t3.small)
                            │
                            ▼ dispatch job
                   Jenkins Agent (GCP VM — gcp-k8s-agent)
                    ├── mvn test (service thay đổi)
                    ├── docker build + push → Docker Hub
                    └── update gitops-manifest-k8s repo
                                    │
                                    ▼ ArgoCD polls
                           gitops-manifest-k8s (GitHub)
                                    │
                        ┌───────────┴───────────┐
                        ▼                       ▼
                  namespace dev          namespace staging
                  (auto-sync)         (manual sync, tag v*)
                        │                       │
                  K3s Cluster (GCP VM — all-in-one)
                  └── dev     namespace: microservices
                  └── staging namespace: microservices
                  └── developer-build: NodePort services
                  └── argocd:          ArgoCD
                  └── istio-system:    Istio + Kiali
```

---

## II. Quyết Định Kiến Trúc

### GCP VM — All-in-One
**Chọn e2-standard-8 (32GB RAM, 8 vCPU, 100GB SSD)**

| Component | RAM |
|-----------|-----|
| K3s + OS | ~2 GB |
| Jenkins Agent | ~3 GB |
| ArgoCD | ~1 GB |
| Istio + Kiali + Prometheus | ~3 GB |
| PostgreSQL | ~2 GB |
| Keycloak | ~1 GB |
| Kafka + Zookeeper | ~2 GB |
| Elasticsearch | ~2 GB |
| Redis | ~0.5 GB |
| App pods (19 svc) | ~8 GB |
| **Tổng** | **~25 GB** |

> 16GB quá nguy hiểm. Tối thiểu 32GB.

### GitOps Repo — Tách Riêng
Dùng repo riêng `gitops-manifest-k8s` (không chung repo source code):
- ArgoCD chỉ watch repo này → không bị trigger bởi code commit bình thường
- Audit trail rõ ràng: ai thay đổi manifest, khi nào
- Separation of concerns: dev chỉ cần đọc, CI mới được write

### Kustomize (không dùng Helm thuần)
- TV2 update tag bằng 1 lệnh: `kustomize edit set image`
- Patch service type (NodePort/ClusterIP) theo environment dễ hơn Helm
- ArgoCD hỗ trợ native, không cần plugin
- Repo đã có Helm charts trong `k8s/charts/` → dùng làm tham khảo port/env, không deploy từ đây

### developer-build dùng infra của dev namespace
Để tiết kiệm RAM, `developer-build` namespace kết nối tới PostgreSQL/Kafka/Keycloak đã chạy trong `dev` namespace qua cross-namespace DNS: `postgres.dev.svc.cluster.local`

---

## III. Convention Toàn Team

| Item | Giá trị |
|------|---------|
| Docker Hub account | `bingsu1103` |
| Image format | `bingsu1103/<service>:<tag>` |
| Tag: feature branch | `<short-commit-id>` (7 chars) |
| Tag: main branch | `latest` + `<short-commit-id>` |
| Tag: release | `v1.2.3` |
| Jenkins Agent label | `gcp-k8s-agent` |
| Namespaces | `dev`, `staging`, `developer-build`, `argocd`, `istio-system` |
| GitOps repo | `gitops-manifest-k8s` |
| Kustomize structure | `base/` + `environments/{dev,staging,developer-build}/` |

---

## IV. Luồng Dữ Liệu End-to-End

### Luồng 1: Feature Branch → Docker Hub
```
git push feature/tax-fix
  → webhook → Jenkins multibranch trigger
  → detect changed: tax/
  → mvn test -pl tax
  → docker build bingsu1103/tax:<commit-id>
  → docker push bingsu1103/tax:<commit-id>
```

### Luồng 2: Main Merge → Dev Namespace (ArgoCD)
```
git merge → main
  → Jenkins CI: build image + push tag latest + <commit-id>
  → scripts/update-gitops-manifest.sh dev
      → git clone gitops-manifest-k8s
      → kustomize edit set image bingsu1103/tax=bingsu1103/tax:<commit-id>
      → git push
  → ArgoCD detects change (30s poll)
  → ArgoCD sync → kubectl apply -k environments/dev/
  → pods rolling update trong namespace dev
```

### Luồng 3: Tag Release → Staging (ArgoCD)
```
git tag v1.2.3 && git push origin v1.2.3
  → Jenkins detects tag v*
  → build image tag v1.2.3
  → scripts/update-gitops-manifest.sh staging
  → ArgoCD yas-staging: manual sync (hoặc auto)
  → pods update trong namespace staging
```

### Luồng 4: developer_build Job
```
Developer mở Jenkins → developer_build job
  → nhập parameters: tax=feature/tax-fix, còn lại=main
  → Jenkins resolve tags:
      tax → git ls-remote origin feature/tax-fix → <commit-id>
      product, cart, ... → latest
  → clone gitops-manifest-k8s
  → patch environments/developer-build/ image tags
  → kubectl apply -k environments/developer-build/ --namespace developer-build
  → kubectl rollout status...
  → in bảng: tax → <GCP-IP>:3XXXX, product → <GCP-IP>:3XXXX, ...
  → Developer thêm <GCP-IP> yas.local.com vào /etc/hosts → test
```

---

## V. Danh Sách Services + Ports Thực Tế

Tra từ `application.properties` từng service:

| # | Service | App Port | NodePort (developer-build) |
|:-:|---------|:--------:|:--------------------------:|
| 1 | product | 8080 | auto-assigned |
| 2 | payment | 8081 | auto-assigned |
| 3 | media | 8083 | auto-assigned |
| 4 | cart | 8084 | auto-assigned |
| 5 | order | 8085 | auto-assigned |
| 6 | location | 8086 | auto-assigned |
| 7 | backoffice-bff | 8087 | auto-assigned |
| 8 | storefront-bff | 8087 | auto-assigned |
| 9 | customer | 8088 | auto-assigned |
| 10 | rating | 8089 | auto-assigned |
| 11 | inventory | 8090 | auto-assigned |
| 12 | tax | 8091 | auto-assigned |
| 13 | promotion | 8092 | auto-assigned |
| 14 | search | 8092 | auto-assigned |
| 15 | webhook | 8092 | auto-assigned |
| 16 | payment-paypal | 8093 | auto-assigned |
| 17 | sampledata | 8094 | auto-assigned |
| 18 | recommendation | 8095 | auto-assigned |
| 19 | delivery | 8080 | auto-assigned |

> NodePort được K8s auto-assign (range 30000–32767). Jenkins job query và in ra bảng sau khi deploy.

---

## VI. Phân Chia Công Việc

### TV1 — Hạ Tầng GCP + K8s + ArgoCD + Infrastructure
Chi tiết: `docs/member1-tasks.md`

| Phase | Nội dung | Thời gian |
|-------|----------|-----------|
| 1 | GCP VM provision + tools | Ngày 1-2 |
| 2 | K3s + namespaces + secrets | Ngày 2-3 |
| 3 | ArgoCD install + Applications | Ngày 4-5 |
| 4 | **Infrastructure services** (Postgres, Kafka, Keycloak, ES, Redis) | Tuần 2 |
| 5 | Jenkins Agent (JNLP) | Tuần 2 |
| 6 | Integration + fix | Tuần 3 |

### TV2 — CI/CD Pipelines
Chi tiết: `docs/member2-tasks.md`

| Phase | Nội dung | Thời gian |
|-------|----------|-----------|
| 1 | `Jenkinsfile.ci` (test + docker build + push + gitops update) | Tuần 1 |
| 2 | `Jenkinsfile.developer-build` | Tuần 1-2 |
| 3 | `Jenkinsfile.cleanup` | Tuần 2 |
| 4 | Scripts: `update-gitops-manifest.sh`, `deploy-developer-build.sh` | Tuần 1-2 |
| 5 | Test E2E toàn pipeline | Tuần 3 |

### TV3 — GitOps Manifests (repo gitops-manifest-k8s)
Chi tiết: `docs/member3-tasks.md`

| Phase | Nội dung | Thời gian |
|-------|----------|-----------|
| 1 | Base manifests: Deployment + Service + **ServiceAccount** (x19) | Tuần 1 |
| 2 | Environment overlays: dev, staging, developer-build | Tuần 1-2 |
| 3 | NodePort patch + validate | Tuần 2 |
| 4 | Dry-run + ArgoCD sync test | Tuần 2-3 |

> TV3 KHÔNG viết infrastructure manifests — TV1 deploy infra bằng Helm từ k8s/deploy/.

### TV4 — Service Mesh (Istio) + Báo Cáo
Chi tiết: `docs/member4-tasks.md`

| Phase | Nội dung | Thời gian |
|-------|----------|-----------|
| 1 | Viết YAML: PeerAuth, DestRule, AuthzPolicy, VirtualService | Tuần 1 (độc lập) |
| 2 | Cài Istio + Kiali (sau khi TV1 xong cluster) | Tuần 2 |
| 3 | Apply mTLS + verify | Tuần 2 |
| 4 | Apply AuthzPolicy + test curl 200/403 | Tuần 2-3 |
| 5 | Test retry policy (fault injection) | Tuần 3 |
| 6 | Kiali topology screenshots | Tuần 3 |
| 7 | Báo cáo docx tổng hợp | Tuần 3 |

---

## VII. Dependencies Giữa Các TV

```
TV3 viết manifests (Tuần 1)
    └──► TV1 dùng manifests → connect ArgoCD (Tuần 1-2)
    └──► ServiceAccount trong manifests → TV4 cần cho AuthzPolicy (Tuần 2)

TV2 viết Jenkinsfile (Tuần 1)
    └──► Cần TV1: Jenkins Agent label gcp-k8s-agent
    └──► Cần TV1: kubeconfig credential
    └──► Cần TV3: gitops repo structure (environments/ path)

TV4 apply Istio (Tuần 2)
    └──► Cần TV1: cluster ready
    └──► Cần TV3: ServiceAccount có trong manifests
    └──► Cần TV1: label namespace istio-injection=enabled TRƯỚC khi deploy pods
```

**Critical path:** TV1 Phase 1-2 phải xong trước Tuần 2 để các TV còn lại integration test.

---

## VIII. YAS Service Communication Map (cho TV4)

```
storefront-bff  → product, media, cart, order, customer, rating,
                  search, promotion, tax, location
backoffice-bff  → product, media, order, inventory, promotion,
                  rating, webhook, customer, location
order           → inventory, payment, customer, cart, tax, webhook
cart            → product, promotion, tax
customer        → location
payment         → webhook, payment-paypal
delivery        → order
recommendation  → product, order
```

---

## IX. Điểm Số Mapping

| Yêu cầu | Điểm | Người phụ trách |
|---------|------|----------------|
| K8s Cluster (YC2) | cơ bản | TV1 |
| CI Image Build & Push (YC3) | cơ bản | TV2 |
| Job developer_build (YC4) | cơ bản | TV2 + TV3 |
| Job Cleanup (YC5) | cơ bản | TV2 |
| ArgoCD dev + staging (NC1) | +2đ | TV1 + TV2 + TV3 |
| Service Mesh mTLS + AuthzPolicy + Retry (NC2) | +2đ | TV4 |
| **Tổng tối đa** | **10đ** | |

---

## X. Timeline 3 Tuần

| | TV1 | TV2 | TV3 | TV4 |
|--|-----|-----|-----|-----|
| **Tuần 1** | GCP VM + K3s + ArgoCD | Jenkinsfile.ci + developer_build | Base manifests + ServiceAccounts | Viết Istio YAML (độc lập) |
| **Tuần 2** | Infra services + Jenkins Agent | Cleanup + scripts + integration | Overlays + NodePort + dry-run | Cài Istio + apply mTLS + AuthzPolicy |
| **Tuần 3** | Fix issues + support | Test E2E + fix | Validate + ArgoCD sync | Retry test + Kiali + Báo cáo |

---

## XI. Files Cần Tạo (trong repo yas)

```
Jenkinsfile.ci
Jenkinsfile.developer-build
Jenkinsfile.cleanup
scripts/
├── update-gitops-manifest.sh
└── deploy-developer-build.sh
istio/
├── peer-authentication.yaml
├── destination-rule.yaml
├── authorization/
│   ├── deny-all.yaml
│   ├── allow-storefront-bff.yaml
│   ├── allow-backoffice-bff.yaml
│   ├── allow-order.yaml
│   ├── allow-cart.yaml
│   ├── allow-payment.yaml
│   ├── allow-customer.yaml
│   └── allow-recommendation.yaml
└── virtual-services/
    ├── product-vs.yaml
    ├── order-vs.yaml
    ├── payment-vs.yaml
    ├── cart-vs.yaml
    └── inventory-vs.yaml
```
