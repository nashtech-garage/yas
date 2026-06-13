# 👤 TV2 — CI/CD Pipelines (Jenkins)

> **Vai trò:** Viết tất cả Jenkinsfile/Pipeline cho CI build, developer_build, cleanup, dev/staging triggers.  
> **Ưu tiên:** Tuần 1 làm song song (chỉ cần convention), Tuần 2 integration test.

---

## Convention Thống Nhất

```
Docker Hub:    bingsu1103/<service>:<tag>
Agent label:   gcp-k8s-agent
Namespaces:    dev, staging, developer-build
GitOps repo:   gitops-manifest-k8s
Tag strategy:  main → latest, feature → <short-commit-id>, release → v1.2.3
```

---

## Phase 1 — CI Pipeline: Build & Push Image [YC3] (Tuần 1)

### 1.1 Tạo `Jenkinsfile.ci` (Multibranch Pipeline)
- [ ] Tạo file `Jenkinsfile.ci` ở root repo `yas`
- [ ] Logic chính:
  1. **Detect changed services** (monorepo): dùng `git diff` so sánh commit trước
  2. Cho mỗi service thay đổi: `docker build` → `docker push`
  3. Tag = `<short-commit-id>` (7 ký tự)
  4. Nếu branch là `main` → push thêm tag `latest`
- [ ] Tích hợp SonarQube scan (gọi AWS SonarQube server)
- [ ] Gitleaks + Snyk scan (tái sử dụng từ Đồ án 1)
- [ ] Publish test results + JaCoCo coverage
- [ ] 📸 Screenshot: Pipeline config trên Jenkins

### 1.2 Viết script detect changed services
```bash
# scripts/detect-changes.sh
#!/bin/bash
CHANGED=$(git diff --name-only HEAD~1 HEAD | cut -d'/' -f1 | sort -u)
SERVICES="media product order inventory payment promotion rating delivery sampledata recommendation customer location cart tax search webhook backoffice-bff storefront-bff payment-paypal"
for svc in $SERVICES; do
  if echo "$CHANGED" | grep -q "^$svc$"; then
    echo "$svc"
  fi
done
```
- [ ] Test script: push commit vào 1 service → chỉ detect service đó

### 1.3 Cấu hình Jenkins Multibranch Pipeline Job
- [ ] Trên Jenkins UI: New Item → Multibranch Pipeline
- [ ] Branch Source: GitHub, trỏ tới repo `yas`
- [ ] Build Configuration: by Jenkinsfile, path = `Jenkinsfile.ci`
- [ ] Scan Multibranch Pipeline Triggers: interval 1 minute hoặc webhook
- [ ] 📸 Screenshot: Job configuration

### 1.4 Test CI Pipeline
- [ ] Push commit vào 1 branch → verify:
  - Pipeline tự trigger
  - Docker image được build + push lên Docker Hub
  - Tag đúng format `<commit-id>`
- [ ] 📸 Screenshot: Console output + Docker Hub image

---

## Phase 2 — Job `developer_build` [YC4] (Tuần 1-2)

### 2.1 Tạo `Jenkinsfile.developer-build`
- [ ] Tạo file `Jenkinsfile.developer-build`
- [ ] 19 String parameters (mỗi service 1 param, default = `main`):
  ```groovy
  parameters {
      string(name: 'MEDIA_BRANCH', defaultValue: 'main')
      string(name: 'PRODUCT_BRANCH', defaultValue: 'main')
      string(name: 'ORDER_BRANCH', defaultValue: 'main')
      // ... tất cả 19 services
  }
  ```

### 2.2 Logic pipeline
- [ ] Cho mỗi service:
  - Nếu branch ≠ `main` → lấy commit cuối → image tag = commit id → build image
  - Nếu branch = `main` → image tag = `latest`
- [ ] Deploy vào namespace `developer-build`:
  - Dùng `kubectl apply` hoặc update gitops manifest
  - Service type = **NodePort**
- [ ] Output cuối: in bảng `service → nodeIP:nodePort`

### 2.3 Cấu hình Jenkins Job
- [ ] New Item → Pipeline, tên `developer_build`
- [ ] Pipeline script from SCM hoặc inline
- [ ] 📸 Screenshot: Job parameters page

### 2.4 Test developer_build
- [ ] Tạo branch `dev_tax_service`, push code thay đổi
- [ ] Chạy `developer_build`, set `TAX_BRANCH=dev_tax_service`, còn lại = `main`
- [ ] Verify:
  - Tax service dùng image tag commit id
  - Các service khác dùng tag `latest`
  - NodePort accessible: `curl http://<GCP_IP>:<NodePort>`
- [ ] 📸 Screenshot: Job output + bảng service:port + curl result

---

## Phase 3 — Job Cleanup [YC5] (Tuần 2)

### 3.1 Tạo `Jenkinsfile.cleanup`
```groovy
pipeline {
    agent { label 'gcp-k8s-agent' }
    stages {
        stage('Cleanup developer-build') {
            steps {
                sh 'kubectl delete all --all -n developer-build'
                sh 'kubectl delete configmap --all -n developer-build'
                sh 'kubectl delete secret --field-selector type!=kubernetes.io/service-account-token --all -n developer-build'
                echo 'Cleanup complete. Namespace developer-build cleared.'
            }
        }
    }
}
```
- [ ] Giữ lại namespace + ImagePullSecret, chỉ xóa workloads

### 3.2 Cấu hình Jenkins Job
- [ ] New Item → Pipeline, tên `developer_build_cleanup`
- [ ] 📸 Screenshot: Job configuration

### 3.3 Test cleanup
- [ ] Chạy `developer_build` trước (có workloads)
- [ ] Chạy `developer_build_cleanup`
- [ ] Verify: `kubectl get all -n developer-build` → trống
- [ ] 📸 Screenshot: Before/After cleanup

---

## Phase 4 — Trigger ArgoCD cho Dev & Staging [NC1] (Tuần 2)

### 4.1 Dev trigger (main branch → auto deploy)
- [ ] Khi `main` thay đổi → CI build images tag `latest`
- [ ] Chạy script `update-gitops-manifest.sh`:
  - Clone gitops repo
  - Update image tags trong `environments/dev/kustomization.yaml`
  - Commit & push
- [ ] ArgoCD auto-sync → deploy vào namespace `dev`

### 4.2 Staging trigger (tag v* → deploy)
- [ ] Khi có tag `v*` trên `main`:
  - CI build images với tag version (e.g., `v1.2.3`)
  - Update `environments/staging/kustomization.yaml`
  - Commit & push gitops repo
- [ ] ArgoCD sync → deploy vào namespace `staging`

### 4.3 Viết `scripts/update-gitops-manifest.sh`
```bash
#!/bin/bash
# Params: SERVICE_NAME, IMAGE_TAG, ENVIRONMENT
GITOPS_REPO="https://github.com/<org>/gitops-manifest-k8s.git"
git clone $GITOPS_REPO /tmp/gitops
cd /tmp/gitops/environments/$ENVIRONMENT
# Update image tag in kustomization.yaml
yq e ".images[] |= select(.name == \"bingsu1103/$SERVICE_NAME\").newTag = \"$IMAGE_TAG\"" \
  -i kustomization.yaml
git add . && git commit -m "Update $SERVICE_NAME to $IMAGE_TAG in $ENVIRONMENT"
git push
rm -rf /tmp/gitops
```
- [ ] 📸 Screenshot: GitOps repo commit history showing auto-updates

### 4.4 Test E2E
- [ ] Push code vào `main` → verify dev auto-deploy
- [ ] Tạo tag `v1.0.0` → verify staging deploy
- [ ] 📸 Screenshot: ArgoCD sync + pods running

---

## Deliverables Checklist

- [ ] `Jenkinsfile.ci` — CI multibranch pipeline
- [ ] `Jenkinsfile.developer-build` — developer_build job
- [ ] `Jenkinsfile.cleanup` — cleanup job
- [ ] `scripts/detect-changes.sh` — monorepo change detection
- [ ] `scripts/update-gitops-manifest.sh` — gitops update script
- [ ] Jenkins jobs configured + screenshots
- [ ] README hướng dẫn sử dụng từng job

---

## Phase 5 — Documentation (Tuần 3)

- [ ] Viết báo cáo phần CI/CD Pipelines:
  1. CI Pipeline: flow, stages, monorepo detection
  2. developer_build: parameters, logic, demo
  3. Cleanup job: logic, demo
  4. Dev/Staging triggers: ArgoCD integration
- [ ] Gửi text + screenshots cho TV4 tổng hợp

---

## ✅ Checklist Cuối Cùng

- [ ] CI pipeline: push → detect → build → push image → Docker Hub
- [ ] developer_build: parameterized → build → deploy → NodePort
- [ ] Cleanup: xóa workloads namespace developer-build
- [ ] Dev trigger: main push → update gitops → ArgoCD sync
- [ ] Staging trigger: tag v* → update gitops → ArgoCD sync
- [ ] Tất cả scripts + Jenkinsfiles đã commit
- [ ] Báo cáo + screenshots gửi TV4
