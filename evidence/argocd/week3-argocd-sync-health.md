# Evidence: Week 3 – ArgoCD Sync/Health Status

- **Task**: GitOps Evidence — Kiểm tra ArgoCD sync/health cho dev và staging
- **Owner**: Công Phúc (Project Lead & Quality Control)
- **Week**: Tuần 3 (18/06 – 24/06)
- **Status**: `PARTIAL – ArgoCD not deployed in current cluster`
- **Date**: 2026-06-24

---

## 1. Mục tiêu

Kiểm tra trạng thái Synced/Healthy của ArgoCD Applications cho namespace `dev` và `staging`.

---

## 2. Trạng thái thực tế: ArgoCD chưa deploy

### 2.1 Kiểm tra namespace argocd

```text
$ kubectl get ns | grep argocd
(không có output — namespace argocd chưa tồn tại)

$ kubectl get all -n argocd
No resources found in argocd namespace.
```

> **Kết luận**: ArgoCD **chưa được cài đặt** trên cluster k3d hiện tại (`k3d-yas-cluster`). Cluster này là môi trường developer sandbox chạy trực tiếp bằng Helm, không thông qua ArgoCD GitOps.

### 2.2 Cấu hình ArgoCD trong repo

Repo có chứa cấu hình ArgoCD applications trong thư mục `argocd/apps/`:

```text
argocd/
└── apps/
    └── .gitkeep   ← placeholder, chưa có app manifests đã apply
```

Script deploy ArgoCD có sẵn tại `k8s/deploy/setup-argocd.sh`.

---

## 3. Thay thế: Helm Release Health (tương đương ArgoCD Synced/Healthy)

Vì ArgoCD không có, trạng thái tương đương được xác nhận qua Helm và kubectl:

### 3.1 Dev namespace — Pods Running (Healthy)

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

**→ dev: 13/13 core pods READY (1/1) = Healthy ✅**

### 3.2 Staging namespace — Chưa có workload

```text
$ kubectl get pods -n staging
No resources found in staging namespace.

$ kubectl get svc -n staging
No resources found in staging namespace.

$ kubectl get deploy -n staging
No resources found in staging namespace.
```

**→ staging: namespace tồn tại, chưa có workload deploy = chưa Synced**

---

## 4. Kế hoạch tiếp theo (Next Steps)

| Bước | Hành động |
|------|-----------|
| 1 | Chạy `k8s/deploy/setup-argocd.sh` để cài ArgoCD lên cluster |
| 2 | Apply ArgoCD app manifests cho `dev` và `staging` |
| 3 | Verify Applications trạng thái `Synced` + `Healthy` trên ArgoCD UI |
| 4 | Chụp screenshot ArgoCD dashboard cho EVID-12, EVID-13, EVID-14 |

---

## 5. Evidence IDs liên quan

| Evidence ID | Requirement | Trạng thái |
|-------------|-------------|------------|
| EVID-12 | ADV-ARGO-01 — ArgoCD UI apps dashboard | `PENDING` — ArgoCD chưa deploy |
| EVID-13 | ADV-ARGO-02 — ArgoCD dev sync history | `PENDING` — ArgoCD chưa deploy |
| EVID-14 | ADV-ARGO-03 — ArgoCD staging sync history | `PENDING` — ArgoCD chưa deploy |
