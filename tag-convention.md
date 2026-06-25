# CI Image Tag Convention

> **Author:** Thành viên B (CI Engineer)
> **Dành cho:** Thành viên C (Jenkins CD) và D (ArgoCD/GitOps)

---

## 1. Docker Hub Registry

| Thông tin | Giá trị |
|---|---|
| Registry | Docker Hub (`docker.io`) |
| Namespace | `$DOCKERHUB_USERNAME` (secret trong GitHub repo) |
| Format tên image | `<DOCKERHUB_USERNAME>/yas-<service-name>` |

---

## 2. Quy ước Tag

Mỗi lần CI chạy, **1 image được push với tối đa 3 tags**:

| Tag | Điều kiện | Ví dụ |
|---|---|---|
| `<commit-sha-7>` | **Luôn có** — mọi branch, mọi commit | `a1b2c3d` |
| `<branch-name>` | **Luôn có** — tên branch (ký tự đặc biệt thay bằng `-`) | `main`, `feature-login`, `ci-POC_location_image` |
| `latest` | **Chỉ khi push lên `main`** | `latest` |

### Quy tắc sanitize branch name thành tag

```
refs/heads/feature/login-ui  →  feature-login-ui
refs/heads/ci/POC_test        →  ci-POC_test
refs/heads/main               →  main
```

> Các ký tự **không thuộc** `[a-zA-Z0-9._-]` bị thay bằng `-`.

---

## 3. Bảng Image đầy đủ

| Service | Docker Hub Image | Pull (stable) | Pull (dev branch) |
|---|---|---|---|
| `backoffice` | `<USER>/yas-backoffice` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `backoffice-bff` | `<USER>/yas-backoffice-bff` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `cart` | `<USER>/yas-cart` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `customer` | `<USER>/yas-customer` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `inventory` | `<USER>/yas-inventory` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `location` | `<USER>/yas-location` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `media` | `<USER>/yas-media` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `order` | `<USER>/yas-order` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `payment` | `<USER>/yas-payment` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `payment-paypal` | `<USER>/yas-payment-paypal` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `product` | `<USER>/yas-product` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `promotion` | `<USER>/yas-promotion` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `rating` | `<USER>/yas-rating` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `recommendation` | `<USER>/yas-recommendation` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `search` | `<USER>/yas-search` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `storefront` | `<USER>/yas-storefront` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `storefront-bff` | `<USER>/yas-storefront-bff` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `tax` | `<USER>/yas-tax` | `:main` hoặc `:latest` | `:<commit-sha>` |
| `webhook` | `<USER>/yas-webhook` | `:main` hoặc `:latest` | `:<commit-sha>` |

> Thay `<USER>` bằng giá trị của secret `DOCKERHUB_USERNAME` trong repo GitHub.

---

## 4. Hướng dẫn cho Thành viên C — Jenkins `developer_build`

Job `developer_build` nhận tham số **branch** và **service** từ developer. Logic xác định tag image:

```
Nếu service == service_được_chọn_bởi_developer:
    tag = commit-sha-7 của branch đó    ← lấy từ GitHub API hoặc git ls-remote
Ngược lại (các service còn lại):
    tag = "main"
```

### Cách lấy commit-sha của 1 branch qua API

```bash
# Lấy short SHA (7 ký tự) của branch feature/login trên GitHub
BRANCH="feature/login"
REPO="hungvu09122005/yas"
COMMIT_SHA=$(curl -s \
  -H "Authorization: token $GITHUB_TOKEN" \
  "https://api.github.com/repos/$REPO/commits?sha=$BRANCH&per_page=1" \
  | jq -r '.[0].sha[0:7]')

echo "Image tag: ${DOCKERHUB_USERNAME}/yas-<service>:${COMMIT_SHA}"
```

### Hoặc dùng Git trực tiếp trong Jenkins

```groovy
// Jenkinsfile
def commitSha = sh(
    script: "git ls-remote https://github.com/hungvu09122005/yas.git refs/heads/${params.BRANCH} | cut -c1-7",
    returnStdout: true
).trim()

def imageTag = commitSha ?: "main"
```

### Ví dụ Helm values cho developer_build

```yaml
# values-dev-test.yaml — deploy location từ branch feature/new-api, còn lại dùng main
location:
  image:
    tag: "a1b2c3d"   # ← commit id của feature/new-api

cart:
  image:
    tag: "main"

product:
  image:
    tag: "main"
# ... tất cả service còn lại dùng "main"
```

---

## 5. Hướng dẫn cho Thành viên D — ArgoCD / GitOps

### Namespace `dev` — auto-deploy khi `main` thay đổi

```yaml
# values-dev.yaml
image:
  tag: "main"    # hoặc "latest" — cả hai đều trỏ cùng image
```

ArgoCD sync khi có image mới với tag `main` push lên Docker Hub (trigger qua webhook hoặc poll interval).

### Namespace `staging` — deploy theo tag `vX.Y.Z`

Khi tạo Git tag `v1.2.3`:
1. CI sẽ build image với tag = `v1.2.3`
2. ArgoCD sync staging với tag đó

```yaml
# values-staging.yaml
image:
  tag: "v1.2.3"   # ← Git release tag
```

> **Lưu ý:** Tag `vX.Y.Z` được push bởi workflow khi có Git tag tạo trên `main`. Thành viên B sẽ thêm trigger này khi cần (hiện chưa implement — ưu tiên sau).

---

## 6. Tóm tắt nhanh

```
Mọi branch commit  →  image:<7-char-sha>  (dùng cho developer_build)
Push lên main      →  image:main + image:latest  (dùng cho dev namespace)
Git tag vX.Y.Z     →  image:vX.Y.Z  (dùng cho staging namespace)
```
