# 🌐 YAS — Hosts File Configuration Guide

Hướng dẫn cấu hình file `hosts` trên máy developer để truy cập YAS platform.

---

## Môi trường 1: k3d (giả lập, 1 máy Windows)

Thêm vào file `C:\Windows\System32\drivers\etc\hosts`:

```
# ─── YAS Platform (k3d local) ─────────────────────────────────
127.0.0.1   yas.local.com
127.0.0.1   identity.yas.local.com
127.0.0.1   backoffice.dev.yas.local.com
127.0.0.1   storefront.dev.yas.local.com
127.0.0.1   api.dev.yas.local.com
127.0.0.1   backoffice.staging.yas.local.com
127.0.0.1   storefront.staging.yas.local.com
127.0.0.1   api.staging.yas.local.com
127.0.0.1   pgadmin.yas.local.com
127.0.0.1   akhq.yas.local.com
127.0.0.1   kibana.yas.local.com
```

**NodePort access (k3d):**
| Service | URL |
|---|---|
| Storefront UI | http://localhost:30080 |
| Backoffice UI | http://localhost:30081 |
| Swagger UI | http://localhost:30082 |
| Keycloak | http://localhost:30084 |
| Storefront BFF | http://localhost:30085 |
| Backoffice BFF | http://localhost:30086 |
| **ArgoCD** | http://localhost:30088 |
| **Kiali** | http://localhost:30089 |

---

## Môi trường 2: k3s + Tailscale (4 laptop thật)

### Bước 1: Lấy Tailscale IP của laptop-a (master)
```powershell
# Chạy trong WSL2 trên laptop-a
tailscale ip -4
# Ví dụ: 100.64.0.1
```

### Bước 2: Thêm vào hosts trên TẤT CẢ 4 laptop
File: `C:\Windows\System32\drivers\etc\hosts`

```
# ─── YAS Platform (k3s + Tailscale) ───────────────────────────
# Thay 100.64.0.1 bằng Tailscale IP thật của laptop-a (master)
100.64.0.1  yas.local.com
100.64.0.1  identity.yas.local.com
100.64.0.1  backoffice.dev.yas.local.com
100.64.0.1  storefront.dev.yas.local.com
100.64.0.1  api.dev.yas.local.com
100.64.0.1  backoffice.staging.yas.local.com
100.64.0.1  storefront.staging.yas.local.com
100.64.0.1  api.staging.yas.local.com
100.64.0.1  pgadmin.yas.local.com
100.64.0.1  akhq.yas.local.com
100.64.0.1  kibana.yas.local.com
```

### Bước 3: Patch CoreDNS (chạy 1 lần sau khi cluster k3s lên)
```bash
# Chạy trong WSL2 trên laptop-a (sau khi infra đã deploy)
chmod +x scripts/patch-coredns-tailscale.sh
./scripts/patch-coredns-tailscale.sh
```

**NodePort access (k3s + Tailscale) — dùng Tailscale IP laptop-a:**
| Service | URL |
|---|---|
| Storefront UI | http://100.64.0.1:30080 |
| Backoffice UI | http://100.64.0.1:30081 |
| Swagger UI | http://100.64.0.1:30082 |
| Keycloak | http://100.64.0.1:30084 |
| Storefront BFF | http://100.64.0.1:30085 |
| Backoffice BFF | http://100.64.0.1:30086 |
| **ArgoCD** | http://100.64.0.1:30088 |
| **Kiali** | http://100.64.0.1:30089 |

---

## ⚡ Quick Edit Hosts (PowerShell — chạy với quyền Admin)

```powershell
# k3d mode (localhost)
$hostsFile = "C:\Windows\System32\drivers\etc\hosts"
$entries = @(
    "127.0.0.1 identity.yas.local.com",
    "127.0.0.1 backoffice.dev.yas.local.com",
    "127.0.0.1 storefront.dev.yas.local.com",
    "127.0.0.1 api.dev.yas.local.com",
    "127.0.0.1 pgadmin.yas.local.com",
    "127.0.0.1 akhq.yas.local.com",
    "127.0.0.1 kibana.yas.local.com"
)
$entries | Add-Content $hostsFile

# k3s + Tailscale mode (thay IP)
$MASTER_IP = "100.64.0.1"  # <-- đổi thành IP thật của laptop-a
$entries = @(
    "$MASTER_IP identity.yas.local.com",
    "$MASTER_IP backoffice.dev.yas.local.com",
    "$MASTER_IP storefront.dev.yas.local.com",
    "$MASTER_IP api.dev.yas.local.com"
)
$entries | Add-Content $hostsFile
```

---

## So sánh 2 môi trường

| Điểm khác biệt | k3d (dev) | k3s + Tailscale (thật) |
|---|---|---|
| Master IP | `127.0.0.1` | `100.x.x.x` (Tailscale) |
| CoreDNS identity entry | Auto-set khi setup | Chạy `patch-coredns-tailscale.sh` |
| NodePort binding | Docker port-mapping | Tailscale IP của laptop-a |
| Tất cả YAML Istio/ArgoCD | **Giống hệt** | **Giống hệt** ✅ |
| Tất cả Helm chart | **Giống hệt** | **Giống hệt** ✅ |
