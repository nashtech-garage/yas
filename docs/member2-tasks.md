# TV2 — CI/CD Pipelines (Jenkins)

> **Vai trò:** Viết tất cả Jenkinsfile và scripts cho CI build image, developer_build, cleanup, GitOps triggers.
> **Điểm phụ thuộc:** Cần TV1 cung cấp kubeconfig + Jenkins Agent online trước khi integration test.
> Tuần 1 làm hoàn toàn độc lập (viết Jenkinsfile, scripts); Tuần 2 test trên infrastructure thật.

---

## Convention Thống Nhất

```
Docker Hub:      bingsu1103/<service>:<tag>
Agent label:     gcp-k8s-agent
Namespaces:      dev, staging, developer-build
GitOps repo:     https://github.com/<org>/gitops-manifest-k8s
Tag: feature     <short-commit-id> (7 chars)
Tag: main        latest + <short-commit-id>
Tag: release     v1.2.3
```

**Jenkins Credentials cần setup (trên Jenkins Controller):**
| Credential ID | Type | Nội dung |
|--------------|------|----------|
| `dockerhub-cred` | Username/Password | Docker Hub bingsu1103 + token |
| `gcp-kubeconfig` | Secret file | kubeconfig-external.yaml từ TV1 |
| `github-token` | Secret text | GitHub PAT để push gitops repo |
| `sonar-token` | Secret text | SonarQube token (đã có từ ĐA1) |
| `snyk-token` | Secret text | Snyk token (đã có từ ĐA1) |

---

## Phase 1 — Jenkinsfile.ci: CI + Docker Build + GitOps Update [YC3 + NC1] (Tuần 1)

Tạo file `Jenkinsfile.ci` ở root repo, **mở rộng từ Jenkinsfile hiện tại**.

### Logic tổng thể
```
Pre-check → Check Skip (DOCS_ONLY) → Secret Scanning → Monorepo Test
→ Code Quality → Quality Gate → Coverage Report → Dependency Scan
→ [MỚI] Docker Build & Push → [MỚI] Update GitOps Manifest
```

### Stages mới thêm vào sau Coverage Report

**Stage: Docker Build & Push**
```groovy
stage('Docker Build & Push') {
    when { expression { env.DOCS_ONLY == 'false' } }
    steps {
        withCredentials([usernamePassword(
            credentialsId: 'dockerhub-cred',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
        )]) {
            script {
                def commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                def branch = (env.GIT_BRANCH ?: '').replace('origin/', '')
                def mergeBase = sh(script: 'git merge-base origin/main HEAD', returnStdout: true).trim()
                def changedFiles = sh(script: "git diff --name-only ${mergeBase} HEAD", returnStdout: true).trim().split('\n')

                sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"

                def services = [
                    'media', 'product', 'order', 'inventory', 'payment', 'promotion',
                    'rating', 'delivery', 'sampledata', 'recommendation', 'customer',
                    'location', 'cart', 'tax', 'search', 'webhook',
                    'backoffice-bff', 'storefront-bff', 'payment-paypal'
                ]

                for (service in services) {
                    if (changedFiles.any { it.startsWith("${service}/") }) {
                        echo "Building Docker image for: ${service}"
                        sh "docker build -t bingsu1103/${service}:${commitId} ./${service}/"
                        sh "docker push bingsu1103/${service}:${commitId}"

                        if (branch == 'main') {
                            sh "docker tag bingsu1103/${service}:${commitId} bingsu1103/${service}:latest"
                            sh "docker push bingsu1103/${service}:latest"
                        }
                        echo "✅ ${service}:${commitId} pushed to Docker Hub"
                    } else {
                        echo "⏭️  ${service}: no changes, skip build"
                    }
                }
            }
        }
    }
}
```

**Stage: Update GitOps Manifest (dev) — chỉ chạy khi merge vào main**
```groovy
stage('Update GitOps — Dev') {
    when {
        allOf {
            expression { env.DOCS_ONLY == 'false' }
            expression { (env.GIT_BRANCH ?: '').replace('origin/', '') == 'main' }
        }
    }
    steps {
        withCredentials([string(credentialsId: 'github-token', variable: 'GH_TOKEN')]) {
            sh 'bash scripts/update-gitops-manifest.sh dev'
        }
    }
}
```

**Stage: Update GitOps Manifest (staging) — chỉ khi có git tag v***
```groovy
stage('Update GitOps — Staging') {
    when {
        expression {
            def tag = sh(script: 'git tag --points-at HEAD 2>/dev/null || true', returnStdout: true).trim()
            return tag ==~ /v\d+\.\d+\.\d+.*/
        }
    }
    steps {
        withCredentials([string(credentialsId: 'github-token', variable: 'GH_TOKEN')]) {
            sh 'bash scripts/update-gitops-manifest.sh staging'
        }
    }
}
```

**Tasks:**
- [ ] Tạo `Jenkinsfile.ci` hoàn chỉnh (copy Jenkinsfile hiện tại + thêm 3 stages trên)
- [ ] Tạo Jenkins Multibranch Pipeline job, trỏ Jenkinsfile path = `Jenkinsfile.ci`
- [ ] 📸 Screenshot: Job configuration + pipeline stages view

---

## Phase 2 — Script: update-gitops-manifest.sh (Tuần 1)

Tạo `scripts/update-gitops-manifest.sh`:

```bash
#!/bin/bash
set -e

ENV=${1:?Usage: update-gitops-manifest.sh <dev|staging>}
COMMIT_ID=$(git rev-parse --short HEAD)
GITOPS_REPO="https://${GH_TOKEN}@github.com/<org>/gitops-manifest-k8s.git"
WORKDIR="/tmp/gitops-update-$$"

# Detect which services changed (compare vs main merge-base)
MERGE_BASE=$(git merge-base origin/main HEAD)
CHANGED_FILES=$(git diff --name-only "${MERGE_BASE}" HEAD)

echo "=== Updating environment: ${ENV} ==="
echo "Commit: ${COMMIT_ID}"
echo "Changed files: ${CHANGED_FILES}"

git clone "${GITOPS_REPO}" "${WORKDIR}"
cd "${WORKDIR}/environments/${ENV}"

SERVICES="media product order inventory payment promotion rating delivery \
          sampledata recommendation customer location cart tax search webhook \
          backoffice-bff storefront-bff payment-paypal"

UPDATED=0
for svc in $SERVICES; do
    if echo "${CHANGED_FILES}" | grep -q "^${svc}/"; then
        echo "Updating ${svc} → ${COMMIT_ID}"
        kustomize edit set image "bingsu1103/${svc}=bingsu1103/${svc}:${COMMIT_ID}"
        UPDATED=$((UPDATED + 1))
    fi
done

if [ $UPDATED -eq 0 ]; then
    echo "No service manifests to update."
    exit 0
fi

git config user.email "jenkins-ci@project.local"
git config user.name "Jenkins CI"
git add -A
git commit -m "ci(${ENV}): update ${UPDATED} service(s) to ${COMMIT_ID}"
git push

rm -rf "${WORKDIR}"
echo "=== GitOps manifest updated ==="
```

- [ ] Tạo `scripts/update-gitops-manifest.sh`
- [ ] `chmod +x scripts/update-gitops-manifest.sh`
- [ ] Test script locally: `GH_TOKEN=xxx bash scripts/update-gitops-manifest.sh dev`

---

## Phase 3 — Jenkinsfile.developer-build [YC4] (Tuần 1-2)

Tạo `Jenkinsfile.developer-build`:

```groovy
pipeline {
    agent { label 'gcp-k8s-agent' }

    parameters {
        string(name: 'media',           defaultValue: 'main', description: 'Branch for media service')
        string(name: 'product',         defaultValue: 'main', description: 'Branch for product service')
        string(name: 'order',           defaultValue: 'main', description: 'Branch for order service')
        string(name: 'inventory',       defaultValue: 'main', description: 'Branch for inventory service')
        string(name: 'payment',         defaultValue: 'main', description: 'Branch for payment service')
        string(name: 'promotion',       defaultValue: 'main', description: 'Branch for promotion service')
        string(name: 'rating',          defaultValue: 'main', description: 'Branch for rating service')
        string(name: 'delivery',        defaultValue: 'main', description: 'Branch for delivery service')
        string(name: 'customer',        defaultValue: 'main', description: 'Branch for customer service')
        string(name: 'location',        defaultValue: 'main', description: 'Branch for location service')
        string(name: 'cart',            defaultValue: 'main', description: 'Branch for cart service')
        string(name: 'tax',             defaultValue: 'main', description: 'Branch for tax service')
        string(name: 'search',          defaultValue: 'main', description: 'Branch for search service')
        string(name: 'webhook',         defaultValue: 'main', description: 'Branch for webhook service')
        string(name: 'backoffice_bff',  defaultValue: 'main', description: 'Branch for backoffice-bff')
        string(name: 'storefront_bff',  defaultValue: 'main', description: 'Branch for storefront-bff')
        string(name: 'payment_paypal',  defaultValue: 'main', description: 'Branch for payment-paypal')
        string(name: 'recommendation',  defaultValue: 'main', description: 'Branch for recommendation')
        string(name: 'sampledata',      defaultValue: 'main', description: 'Branch for sampledata')
    }

    stages {
        stage('Resolve Image Tags') {
            steps {
                script {
                    // Map: param name → service name
                    def serviceParamMap = [
                        'media':          'media',
                        'product':        'product',
                        'order':          'order',
                        'inventory':      'inventory',
                        'payment':        'payment',
                        'promotion':      'promotion',
                        'rating':         'rating',
                        'delivery':       'delivery',
                        'customer':       'customer',
                        'location':       'location',
                        'cart':           'cart',
                        'tax':            'tax',
                        'search':         'search',
                        'webhook':        'webhook',
                        'backoffice_bff': 'backoffice-bff',
                        'storefront_bff': 'storefront-bff',
                        'payment_paypal': 'payment-paypal',
                        'recommendation': 'recommendation',
                        'sampledata':     'sampledata'
                    ]

                    env.SERVICE_TAGS = ""
                    serviceParamMap.each { paramName, serviceName ->
                        def branch = params[paramName] ?: 'main'
                        def tag = 'latest'

                        if (branch != 'main') {
                            // Lấy commit ID 7 ký tự cuối của branch trên remote
                            def remoteCommit = sh(
                                script: "git ls-remote origin refs/heads/${branch} | cut -c1-7",
                                returnStdout: true
                            ).trim()

                            if (!remoteCommit) {
                                error("Branch '${branch}' not found for service '${serviceName}'")
                            }
                            tag = remoteCommit
                            echo "  ${serviceName}: branch=${branch} → tag=${tag}"
                        } else {
                            echo "  ${serviceName}: main → tag=latest"
                        }

                        // Lưu tag vào env var (dùng ở script tiếp theo)
                        env["TAG__${paramName.toUpperCase()}"] = tag
                    }
                }
            }
        }

        stage('Deploy to developer-build') {
            steps {
                withCredentials([
                    file(credentialsId: 'gcp-kubeconfig', variable: 'KUBECONFIG'),
                    string(credentialsId: 'github-token', variable: 'GH_TOKEN')
                ]) {
                    sh 'bash scripts/deploy-developer-build.sh'
                }
            }
        }

        stage('Print Access Info') {
            steps {
                withCredentials([file(credentialsId: 'gcp-kubeconfig', variable: 'KUBECONFIG')]) {
                    sh '''
                    echo ""
                    echo "╔══════════════════════════════════════════════════╗"
                    echo "║          Developer Build — Access Info            ║"
                    echo "╚══════════════════════════════════════════════════╝"

                    WORKER_IP=$(kubectl get nodes \
                      -o jsonpath='{.items[0].status.addresses[?(@.type=="ExternalIP")].address}' \
                      --kubeconfig=${KUBECONFIG} 2>/dev/null || \
                      kubectl get nodes \
                      -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}' \
                      --kubeconfig=${KUBECONFIG})

                    echo "Worker Node IP: ${WORKER_IP}"
                    echo ""
                    echo "Add to /etc/hosts:"
                    echo "  ${WORKER_IP}  yas.local.com"
                    echo ""
                    echo "Service Endpoints:"
                    printf "%-25s %-10s %-20s\\n" "SERVICE" "PORT" "URL"
                    printf "%-25s %-10s %-20s\\n" "-------" "----" "---"

                    kubectl get svc -n developer-build --no-headers \
                      --kubeconfig=${KUBECONFIG} | while read name type clusterip externalip ports age; do
                        nodeport=$(echo $ports | grep -oE '[0-9]+:([0-9]+)/TCP' | grep -oE ':[0-9]+' | tr -d ':')
                        if [ -n "$nodeport" ]; then
                            printf "%-25s %-10s %-20s\\n" "$name" "$nodeport" "http://${WORKER_IP}:${nodeport}"
                        fi
                    done
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ developer_build deployed successfully"
        }
        failure {
            echo "❌ developer_build failed"
        }
    }
}
```

**Tasks:**
- [ ] Tạo `Jenkinsfile.developer-build`
- [ ] Tạo Jenkins Pipeline job tên `developer_build`
- [ ] 📸 Screenshot: Parameters page của job
- [ ] Test: điền 1 service với branch khác main → verify deploy + NodePort table

---

## Phase 4 — Script: deploy-developer-build.sh (Tuần 2)

Tạo `scripts/deploy-developer-build.sh`:

```bash
#!/bin/bash
set -e

GITOPS_REPO="https://${GH_TOKEN}@github.com/<org>/gitops-manifest-k8s.git"
WORKDIR="/tmp/gitops-devbuild-$$"

git clone "${GITOPS_REPO}" "${WORKDIR}"
cd "${WORKDIR}"

# Map TAG__<PARAM> env vars → kustomize edit set image
declare -A SERVICE_MAP=(
    ["TAG__MEDIA"]="media"
    ["TAG__PRODUCT"]="product"
    ["TAG__ORDER"]="order"
    ["TAG__INVENTORY"]="inventory"
    ["TAG__PAYMENT"]="payment"
    ["TAG__PROMOTION"]="promotion"
    ["TAG__RATING"]="rating"
    ["TAG__DELIVERY"]="delivery"
    ["TAG__CUSTOMER"]="customer"
    ["TAG__LOCATION"]="location"
    ["TAG__CART"]="cart"
    ["TAG__TAX"]="tax"
    ["TAG__SEARCH"]="search"
    ["TAG__WEBHOOK"]="webhook"
    ["TAG__BACKOFFICE_BFF"]="backoffice-bff"
    ["TAG__STOREFRONT_BFF"]="storefront-bff"
    ["TAG__PAYMENT_PAYPAL"]="payment-paypal"
    ["TAG__RECOMMENDATION"]="recommendation"
    ["TAG__SAMPLEDATA"]="sampledata"
)

cd environments/developer-build

for env_var in "${!SERVICE_MAP[@]}"; do
    svc="${SERVICE_MAP[$env_var]}"
    tag="${!env_var:-latest}"
    kustomize edit set image "bingsu1103/${svc}=bingsu1103/${svc}:${tag}"
    echo "  ${svc} → ${tag}"
done

# Apply trực tiếp (bypass ArgoCD — faster for developer use)
kubectl apply -k . --namespace developer-build --kubeconfig="${KUBECONFIG}"

# Wait for rollout
echo "Waiting for pods to be ready..."
kubectl rollout status deployment --all -n developer-build \
  --kubeconfig="${KUBECONFIG}" --timeout=180s || true

rm -rf "${WORKDIR}"
```

- [ ] Tạo `scripts/deploy-developer-build.sh`
- [ ] `chmod +x scripts/deploy-developer-build.sh`

---

## Phase 5 — Jenkinsfile.cleanup [YC5] (Tuần 2)

Tạo `Jenkinsfile.cleanup`:

```groovy
pipeline {
    agent { label 'gcp-k8s-agent' }

    stages {
        stage('Cleanup developer-build') {
            steps {
                withCredentials([file(credentialsId: 'gcp-kubeconfig', variable: 'KUBECONFIG')]) {
                    sh '''
                    echo "Cleaning up namespace: developer-build"

                    # Xóa workloads — giữ namespace + dockerhub-secret
                    kubectl delete deployments --all -n developer-build \
                      --kubeconfig=${KUBECONFIG} --ignore-not-found=true
                    kubectl delete services --all -n developer-build \
                      --kubeconfig=${KUBECONFIG} --ignore-not-found=true
                    kubectl delete configmaps --all -n developer-build \
                      --kubeconfig=${KUBECONFIG} --ignore-not-found=true
                    kubectl delete replicasets --all -n developer-build \
                      --kubeconfig=${KUBECONFIG} --ignore-not-found=true
                    kubectl delete pods --all -n developer-build \
                      --kubeconfig=${KUBECONFIG} --ignore-not-found=true

                    echo "Verify cleanup:"
                    kubectl get all -n developer-build --kubeconfig=${KUBECONFIG}
                    echo "✅ developer-build namespace cleared (namespace preserved)"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ Cleanup completed"
        }
    }
}
```

**Tasks:**
- [ ] Tạo `Jenkinsfile.cleanup`
- [ ] Tạo Jenkins Pipeline job tên `developer_build_cleanup`
- [ ] Test: chạy developer_build trước → chạy cleanup → verify `kubectl get all -n developer-build` trống
- [ ] 📸 Screenshot: Before/After cleanup

---

## Phase 6 — Cấu hình Jenkins Jobs & Credentials (Tuần 2)

### 6.1 Setup Credentials trên Jenkins Controller
- [ ] dockerhub-cred: Manage Jenkins → Credentials → add Username/Password
- [ ] gcp-kubeconfig: add Secret file (upload file từ TV1)
- [ ] github-token: add Secret text (GitHub PAT với quyền repo write)
- [ ] 📸 Screenshot: Credentials list

### 6.2 Cấu hình Jenkins Multibranch Pipeline (Jenkinsfile.ci)
- [ ] New Item → Multibranch Pipeline → tên `yas-ci`
- [ ] Branch Sources: GitHub, repo `yas`
- [ ] Build Configuration: by Jenkinsfile, path = `Jenkinsfile.ci`
- [ ] Triggers: webhook từ GitHub (hoặc scan interval 1 min)
- [ ] 📸 Screenshot: Job configuration

### 6.3 Cấu hình Pipeline Jobs
- [ ] `developer_build`: New Item → Pipeline, script from SCM, `Jenkinsfile.developer-build`
- [ ] `developer_build_cleanup`: New Item → Pipeline, script from SCM, `Jenkinsfile.cleanup`
- [ ] 📸 Screenshot: Jobs list

---

## Phase 7 — Test E2E (Tuần 3)

### Test Case 1: Feature branch → Docker Hub
1. Tạo branch `feature/test-tax`
2. Sửa file trong `tax/`
3. Push → Jenkins trigger → verify image `bingsu1103/tax:<commit-id>` trên Docker Hub
- [ ] 📸 Screenshot: Docker Hub image list

### Test Case 2: Main merge → dev namespace
1. Merge PR vào `main`
2. Jenkins CI build + push `latest`
3. `update-gitops-manifest.sh dev` chạy → verify gitops repo có commit mới
4. ArgoCD detect → sync → pods restart trong `dev`
- [ ] 📸 Screenshot: ArgoCD sync + gitops repo commit history

### Test Case 3: developer_build
1. Chạy `developer_build`, nhập `tax=feature/test-tax`, còn lại `main`
2. Verify:
   - tax dùng tag `<commit-id>`, các service khác `latest`
   - NodePort table được in ra
   - `curl http://<GCP_IP>:<NodePort>/tax` → response
- [ ] 📸 Screenshot: Console output + NodePort table + curl result

### Test Case 4: Cleanup
1. Chạy `developer_build_cleanup`
2. `kubectl get all -n developer-build` → empty
- [ ] 📸 Screenshot: Empty namespace

### Test Case 5: Staging deploy (tag v*)
1. `git tag v1.0.0 && git push origin v1.0.0`
2. Jenkins detect tag → build + push `v1.0.0`
3. gitops `environments/staging/` updated
4. ArgoCD yas-staging: sync manually → pods in `staging`
- [ ] 📸 Screenshot: ArgoCD staging sync

---

## Deliverables Checklist

- [ ] `Jenkinsfile.ci` (extends current Jenkinsfile + Docker Build + GitOps stages)
- [ ] `Jenkinsfile.developer-build`
- [ ] `Jenkinsfile.cleanup`
- [ ] `scripts/update-gitops-manifest.sh`
- [ ] `scripts/deploy-developer-build.sh`
- [ ] Jenkins jobs configured: `yas-ci`, `developer_build`, `developer_build_cleanup`
- [ ] Credentials setup: dockerhub-cred, gcp-kubeconfig, github-token
- [ ] Test cases 1-5 pass với screenshots
- [ ] Báo cáo phần CI/CD Pipelines gửi TV4

---

## Checklist Cuối Cùng

- [ ] CI: push any branch → detect changed service → build image → push Docker Hub ✓
- [ ] CI main: push main → push `latest` tag → update gitops dev → ArgoCD sync ✓
- [ ] CI tag: push `v*` tag → update gitops staging → ArgoCD sync staging ✓
- [ ] developer_build: params → resolve tags → deploy → NodePort table ✓
- [ ] cleanup: xóa workloads developer-build namespace ✓
- [ ] Tất cả scripts executable và committed
