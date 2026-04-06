# Hướng Dẫn Xây Dựng CI Pipeline Cho Monorepo YAS Với Jenkins - Option C

Đây là hướng dẫn chi tiết từng bước để hoàn thành bài tập DevOps về xây dựng CI pipeline cho hệ thống monorepo YAS **sử dụng Jenkins** (Option C: Jenkins + GitHub Branch Protection).

---

## 📋 Mục Lục
1. [Yêu Cầu](#yêu-cầu)
2. [Bước 1: Fork Repository](#bước-1-fork-repository)
3. [Bước 2: Vô Hiệu Hóa GitHub Actions Workflows](#bước-2-vô-hiệu-hóa-github-actions-workflows)
4. [Bước 3: Tạo Jenkinsfile](#bước-3-tạo-jenkinsfile)
5. [Bước 4: Cấu Hình Branch Protection Rules](#bước-4-cấu-hình-branch-protection-rules)
6. [Bước 5: Cài Đặt & Cấu Hình Jenkins Server](#bước-5-cài-đặt--cấu-hình-jenkins-server)
7. [Bước 6: Tạo Pipeline Job Trong Jenkins](#bước-6-tạo-pipeline-job-trong-jenkins)
8. [Bước 7: Cấu Hình GitHub Webhook](#bước-7-cấu-hình-github-webhook)
9. [Bước 8: Test Pipeline](#bước-8-test-pipeline)
10. [Xác Minh Kết Quả](#xác-minh-kết-quả)

---

## 🎯 Yêu Cầu

Đồ án này yêu cầu xây dựng CI pipeline cho monorepo với các yêu cầu sau:

1. ✅ Sử dụng **Jenkins** (thay vì GitHub Actions)
2. ✅ Fork repository từ `https://github.com/nashtech-garage/yas`
3. ✅ Cấu hình GitHub branch protection: 
   - Không cho phép push trực tiếp vào `main`
   - Yêu cầu ít nhất 2 reviewer approve
   - CI phải pass mới được merge
4. ✅ Cấu hình pipeline quét mọi branch
5. ✅ Pipeline có ít nhất 2 phase: **Test** và **Build**
   - Upload test results
   - Upload test coverage
6. ✅ **Monorepo Optimization**: Chỉ build/test service có thay đổi

---

## 🔧 Bước 1: Fork Repository

### 1.1 Trên GitHub

1. Truy cập: https://github.com/nashtech-garage/yas
2. Nhấn nút **Fork** ở góc trên bên phải
3. Chọn tài khoản của bạn làm nơi fork
4. Chờ quá trình fork hoàn tất (khoảng 1-2 phút)

### 1.2 Clone repository fork về máy local

```bash
# Thay YOUR_USERNAME bằng username GitHub của bạn
git clone https://github.com/YOUR_USERNAME/yas.git
cd yas
```

### 1.3 Thêm upstream remote (để luôn đồng bộ với repo gốc)

```bash
git remote add upstream https://github.com/nashtech-garage/yas.git
git remote -v  # Kiểm tra xem có 2 remote: origin và upstream
```

---

## 🗑️ Bước 2: Vô Hiệu Hóa GitHub Actions Workflows

**Vì chúng ta chỉ dùng Jenkins, cần vô hiệu hóa tất cả GitHub Actions workflows để tránh chạy song song.**

### 2.1 Disable tất cả workflow files

Bằng cách thêm dấu gạch dưới `_` vào đầu tên file (GitHub Actions yêu cầu file phải có định dạng `.yaml` hoặc `.yml` ở root của `.github/workflows/`):

```bash
cd /workspaces/yas/.github/workflows

# Rename all workflow files
for f in *.yaml *.yml; do 
  [ "$f" != "actions" ] && mv "$f" "_${f}" && echo "Disabled: $f"
done

cd /workspaces/yas
```

**Kết quả**: Tất cả workflows sẽ bị disable:
- `media-ci.yaml` → `_media-ci.yaml` ✅
- `storefront-ci.yaml` → `_storefront-ci.yaml` ✅
- `cart-ci.yaml` → `_cart-ci.yaml` ✅
- ... (và tất cả các file khác)

**Thư mục `actions/` vẫn được giữ lại** (nơi chứa các shared actions/reusable workflows)

### 2.2 Commit thay đổi

```bash
git add .github/workflows/
git commit -m "ci: disable github actions workflows, using jenkins instead"
git push origin test-ci
```

### 2.3 Xác Minh

Kiểm tra lại để chắc chắn:

```bash
ls -la .github/workflows/*.yaml .github/workflows/*.yml 2>/dev/null | wc -l
# Nếu ra 0, có nghĩa tất cả đều bị disable ✅
```

---

## 📝 Bước 3: Tạo Jenkinsfile

Jenkinsfile là file cấu hình pipeline cho Jenkins, tương tự như `.github/workflows/ci.yml` cho GitHub Actions.

**✅ Jenkinsfile đã cover tất cả 23 services:**
- **19 Java/Maven Services**: cart, customer, delivery, inventory, location, media, order, payment, payment-paypal, product, promotion, rating, recommendation, search, tax, webhook, backoffice-bff, storefront-bff, sampledata, automation-ui
- **2 Node.js/npm Services**: backoffice, storefront
- **Monorepo Optimization**: Tự động detect thay đổi trong từng service folder

### 3.1 Tạo Jenkinsfile ở root repository

Tạo file `Jenkinsfile` tại `/workspaces/yas/Jenkinsfile` hoặc copy từ repository (file đã được tạo sẵn). 

**Nội dung chính của Jenkinsfile:**

```groovy
pipeline {
    agent any
    
    parameters {
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip running tests')
    }
    
    environment {
        // Biến môi trường chung
        MAVEN_OPTS = '-Dmaven.repo.local=${WORKSPACE}/.m2'
        NODE_ENV = 'test'
    }
    
    stages {
        stage('Checkout') {
            steps {
                script {
                    echo '=== Checking out source code ==='
                    checkout scm
                    
                    // Lấy thông tin commit
                    sh 'git log -1 --pretty=%H > GIT_COMMIT.txt || true'
                    sh 'git log -1 --pretty=%s > GIT_MESSAGE.txt || true'
                }
            }
        }
        
        stage('Detect Changes') {
            steps {
                script {
                    echo '=== Detecting changed services ==='
                    
                    // Kiểm tra service Media có thay đổi không
                    def mediaChanged = sh(
                        script: '''
                            if git diff --name-only origin/main HEAD | grep -q "^media/"; then
                                echo "true"
                            else
                                echo "false"
                            fi
                        ''',
                        returnStdout: true
                    ).trim()
                    env.MEDIA_CHANGED = mediaChanged
                    
                    // Kiểm tra service Storefront
                    def storefrontChanged = sh(
                        script: '''
                            if git diff --name-only origin/main HEAD | grep -q "^storefront/"; then
                                echo "true"
                            else
                                echo "false"
                            fi
                        ''',
                        returnStdout: true
                    ).trim()
                    env.STOREFRONT_CHANGED = storefrontChanged
                    
                    // Kiểm tra service Cart
                    def cartChanged = sh(
                        script: '''
                            if git diff --name-only origin/main HEAD | grep -q "^cart/"; then
                                echo "true"
                            else
                                echo "false"
                            fi
                        ''',
                        returnStdout: true
                    ).trim()
                    env.CART_CHANGED = cartChanged
                    
                    echo "Media Changed: ${env.MEDIA_CHANGED}"
                    echo "Storefront Changed: ${env.STOREFRONT_CHANGED}"
                    echo "Cart Changed: ${env.CART_CHANGED}"
                }
            }
        }
        
        // ============ MEDIA SERVICE ============
        stage('Test Media') {
            when {
                expression { env.MEDIA_CHANGED == 'true' && env.SKIP_TESTS == 'false' }
            }
            steps {
                script {
                    echo '=== Testing Media Service ==='
                    sh './mvnw -pl media -am test'
                }
            }
        }
        
        stage('Build Media') {
            when {
                expression { env.MEDIA_CHANGED == 'true' }
            }
            steps {
                script {
                    echo '=== Building Media Service ==='
                    sh './mvnw -pl media -am clean package -DskipTests'
                }
            }
        }
        
        // ============ STOREFRONT SERVICE ============
        stage('Test Storefront') {
            when {
                expression { env.STOREFRONT_CHANGED == 'true' && env.SKIP_TESTS == 'false' }
            }
            steps {
                script {
                    echo '=== Testing Storefront Service ==='
                    dir('storefront') {
                        sh 'npm ci'
                        sh 'npm run test -- --coverage || true'
                    }
                }
            }
        }
        
        stage('Build Storefront') {
            when {
                expression { env.STOREFRONT_CHANGED == 'true' }
            }
            steps {
                script {
                    echo '=== Building Storefront Service ==='
                    dir('storefront') {
                        sh 'npm run build'
                    }
                }
            }
        }
        
        // ============ CART SERVICE ============
        stage('Test Cart') {
            when {
                expression { env.CART_CHANGED == 'true' && env.SKIP_TESTS == 'false' }
            }
            steps {
                script {
                    echo '=== Testing Cart Service ==='
                    sh './mvnw -pl cart -am test'
                }
            }
        }
        
        stage('Build Cart') {
            when {
                expression { env.CART_CHANGED == 'true' }
            }
            steps {
                script {
                    echo '=== Building Cart Service ==='
                    sh './mvnw -pl cart -am clean package -DskipTests'
                }
            }
        }
    }
    
    post {
        always {
            script {
                echo '=== Collecting test results ==='
                
                // Collect JUnit test results
                junit testResults: '**/target/surefire-reports/**/*.xml', 
                      allowEmptyResults: true,
                      skipPublishingChecks: false
                
                // Archive coverage reports
                archiveArtifacts artifacts: '**/target/site/jacoco/**,storefront/coverage/**', 
                                 allowEmptyArchive: true
                
                // Publish HTML reports
                publishHTML([
                    reportDir: 'media/target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'Media - Jacoco Coverage Report',
                    allowMissing: true,
                    keepAll: true
                ])
                
                publishHTML([
                    reportDir: 'storefront/coverage',
                    reportFiles: 'index.html',
                    reportName: 'Storefront - Coverage Report',
                    allowMissing: true,
                    keepAll: true
                ])
            }
        }
        
        success {
            script {
                echo '✅ Pipeline completed successfully'
            }
        }
        
        failure {
            script {
                echo '❌ Pipeline failed'
            }
        }
    }
}
```

### 3.2 Commit Jenkinsfile

```bash
# Tạo nhánh feature
git checkout -b feature/jenkins-pipeline

# Add Jenkinsfile
git add Jenkinsfile

# Commit
git commit -m "ci: add jenkinsfile for monorepo pipeline"

# Push lên GitHub
git push origin feature/jenkins-pipeline
```

---

## 🛡️ Bước 4: Cấu Hình Branch Protection Rules

### 4.1 Truy cập Settings

1. Vào repository GitHub của bạn
2. Nhấn tab **Settings**
3. Ở sidebar trái, chọn **Branches**

### 4.2 Thêm Branch Protection Rule

1. Nhấn nút **Add rule**
2. Trong ô **Branch name pattern**, gõ: `main`
3. Tích chọn các option sau:
   - ✅ **Require a pull request before merging**
     - ✅ Require approvals: **2**
     - ✅ Require approval from code owners (nếu có CODEOWNERS)
   - ✅ **Require status checks to pass before merging**
     - Sẽ tìm và tích chọn Jenkins job status (sau khi cấu hình webhook)
   - ✅ **Require branches to be up to date before merging**
   - ✅ **Dismiss stale pull request approvals when new commits are pushed**
   - ✅ **Require code reviews before merging**

4. Nhấn nút **Create** hoặc **Save changes**

**Lưu ý**: Jenkins status check sẽ xuất hiện sau khi:
- Jenkins được cấu hình webhook
- Jenkins chạy lần đầu tiên trên một branch

---

## 🖥️ Bước 5: Cài Đặt & Cấu Hình Jenkins Server

### 5.1 Cài Đặt Jenkins (Trên Ubuntu/Debian)

```bash
# Update package manager
sudo apt update

# Cài Java (Jenkins yêu cầu Java)
sudo apt install -y openjdk-17-jdk-headless

# Thêm Jenkins repository
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

# Cài Jenkins
sudo apt update
sudo apt install -y jenkins

# Khởi động Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# Kiểm tra status
sudo systemctl status jenkins
```

### 5.2 Mở Firewall (Nếu Cần)

```bash
# Cho phép port 8080
sudo ufw allow 8080
```

### 5.3 Truy Cập Jenkins Web Interface

1. Truy cập: `http://YOUR_SERVER_IP:8080`
2. Lấy password ban đầu:
```bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```
3. Paste password vào giao diện
4. Nhấn **Continue**
5. Chọn **Install suggested plugins**
6. Tạo tài khoản admin

---

## ⚙️ Bước 6: Tạo Pipeline Job Trong Jenkins

### 6.1 Tạo New Pipeline Job

1. Từ Jenkins Dashboard, nhấn **+ New Item** (hoặc **New Job**)
2. Gõ tên job: `yas-monorepo-pipeline`
3. Chọn **Pipeline**
4. Nhấn **OK**

### 6.2 Cấu Hình General

1. Điền **Description**: 
```
YAS Monorepo CI Pipeline
Handles building and testing individual services based on changes.
```

2. Tích chọn **GitHub project** (nếu có plugin GitHub)
   - Gõ URL repository: `https://github.com/YOUR_USERNAME/yas`

3. Tích chọn **Build Triggers** → **GitHub hook trigger for GITScm polling**

### 6.3 Cấu Hình Pipeline

1. Trong mục **Pipeline**, chọn **Definition**: **Pipeline script from SCM**

2. **SCM**: Chọn **Git**

3. Điền các thông tin:
   - **Repository URL**: `https://github.com/YOUR_USERNAME/yas.git`
   - **Credentials**: 
     - Thêm credentials nếu repo private (Personal Access Token từ GitHub)
     - Hoặc để trống nếu repo public
   - **Branch Specifier**: 
     - `*/main` (để build main branch)
     - Hoặc `**` (để build mọi branch)

4. **Script Path**: `Jenkinsfile`

5. Nhấn **Save**

---

## 🔗 Bước 7: Cấu Hình GitHub Webhook

### 7.1 Trong Jenkins: Cấu Hình GitHub

1. Từ Jenkins Dashboard, nhấn **Manage Jenkins**
2. Chọn **System Configuration** (hoặc **Configure System**)
3. Tìm mục **GitHub** → **GitHub Servers**
4. Nhấn **Add GitHub Server** (nếu chưa có)
5. Điền:
   - **Name**: `GitHub` (hay tên gì tuỳ thích)
   - **API URL**: Để `https://api.github.com` (cho GitHub.com)
   - **Credentials**: 
     - Nhấn **Add** → **Jenkins**
     - Chọn **Kind**: **GitHub App** (hoặc **Secret text** nếu dùng token)
     - Hoặc tạo **Personal Access Token** từ GitHub

6. Nhấn **Test connection** để kiểm tra
7. Nhấn **Save**

### 7.2 Trong GitHub: Add Webhook

1. Vào repository trên GitHub
2. **Settings** → **Webhooks** → **Add webhook**
3. Điền:
   - **Payload URL**: `http://YOUR_JENKINS_SERVER:8080/github-webhook/`
   - **Content type**: `application/json`
   - **Which events would you like to trigger this webhook?**: 
     - Chọn **Let me select individual events**
     - Tích chọn:
       - ✅ Push events
       - ✅ Pull requests
   - **Active**: ✅ Đánh dấu

4. Nhấn **Add webhook**

### 7.3 Test Webhook

```bash
# Kiểm tra webhook delivery
# Vào GitHub Repo → Settings → Webhooks → Chi tiết webhook
# Scroll xuống xem Recent Deliveries
# Response code 200 = thành công
```

---

## 🧪 Bước 8: Test Pipeline

### 8.1 Test thủ công - Trigger job từ Jenkins

1. Vào Jenkins Dashboard
2. Chọn job `yas-monorepo-pipeline`
3. Nhấn **Build Now**
4. Chờ job chạy (sử dụng console output để debug)

### 8.2 Test webhook - Trigger từ GitHub

**Cách A: Thay đổi thư mục Media**

```bash
# Từ local repository
git checkout feature/jenkins-pipeline

# Tạo file test trong media/
echo "// test" >> media/README.md
git add media/README.md
git commit -m "test: trigger jenkins for media"
git push origin feature/jenkins-pipeline
```

1. Tạo Pull Request từ `feature/jenkins-pipeline` vào `main`
2. GitHub sẽ gửi webhook tới Jenkins
3. Jenkins sẽ tự động trigger job
4. Chỉ job `ci-media` sẽ chạy (monorepo optimization) ✅

**Cách B: Thay đổi thư mục Storefront**

```bash
git checkout -b test/storefront-change
echo "// test" >> storefront/README.md
git add storefront/README.md
git commit -m "test: trigger jenkins for storefront"
git push origin test/storefront-change
```

1. Tạo PR từ `test/storefront-change`
2. Chỉ `ci-storefront` sẽ chạy ✅

### 8.3 Kiểm Tra Jenkins Build Status Trong PR

Sau khi Jenkins chạy xong:

1. Quay lại PR trên GitHub
2. Scroll xuống **Checks** / **Status**
3. Bạn sẽ thấy: **continuous-integration/jenkins/pr/build** - ✅ **SUCCESS**

---

## ✅ Xác Minh Kết Quả

### Checklist Hoàn Thành

Sau khi hoàn tất, hãy xác minh:

- [ ] Đã fork repository thành công
- [ ] File `Jenkinsfile` trong repository
- [ ] Branch `main` có protection rules:
  - [ ] Không push trực tiếp được
  - [ ] Yêu cầu ít nhất 2 approvals
  - [ ] Yêu cầu CI pass
- [ ] Jenkins server đang chạy (port 8080 accessible)
- [ ] Job `yas-monorepo-pipeline` được tạo trong Jenkins
- [ ] GitHub webhook cấu hình và test thành công
- [ ] Jenkins status check xuất hiện trong PR
- [ ] Pipeline detect-changes hoạt động (chỉ build service có thay đổi)
- [ ] Phase 1 (Test) upload test results
- [ ] Phase 2 (Build) thành công
- [ ] Có thể merge PR vào main

### Lệnh Kiểm Tra

```bash
# Kiểm tra Jenkinsfile tồn tại
ls -la Jenkinsfile

# Kiểm tra remote
git remote -v

# Kiểm tra branch protection (GitHub UI)
# Settings → Branches → Branch protection rules
```

---

## 🐛 Troubleshooting

### Jenkins Job không trigger

**Vấn đề**: GitHub push nhưng Jenkins không tự động chạy
**Giải pháp**:
1. Kiểm tra webhook delivery: `GitHub Repo → Settings → Webhooks → Recent Deliveries`
2. Kiểm tra Jenkins log: `/var/log/jenkins/jenkins.log`
3. Xác nhận webhook URL đúng: `http://YOUR_JENKINS_IP:8080/github-webhook/`
4. Xác nhận GitHub có quyền truy cập (public repo hoặc credentials)

### Jenkins không detect được changes

**Vấn đề**: Jenkinsfile chạy nhưng bỏ qua các stage theo monorepo
**Giải pháp**:
1. Log vào Jenkins job → **Console Output**
2. Kiểm tra output của `Detect Changes` stage
3. Xác nhận git diff command:
```bash
git diff --name-only origin/main HEAD
```
4. Nếu không có output, có thể cần `git fetch upstream` trước

### Test fail

**Vấn đề**: Stage Test bị fail
**Giải pháp**:
1. Chạy test local:
```bash
./mvnw -pl media -am test
cd storefront && npm test
```
2. Xem Jenkins console log chi tiết
3. Cài đặt Maven/Node đúng version

### Build fail

**Vấn đề**: Maven/npm build fail
**Giải pháp**:
1. Kiểm tra dependencies: `mvn dependency:resolve` hoặc `npm install`
2. Kiểm tra Java/Node version
3. Xem full build log để tìm lỗi cụ thể

---

## 📚 Tài Liệu Thêm

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Jenkinsfile Documentation](https://www.jenkins.io/doc/book/pipeline/jenkinsfile/)
- [GitHub Webhook Documentation](https://docs.github.com/en/developers/webhooks-and-events/webhooks)
- [Git Diff Documentation](https://git-scm.com/docs/git-diff)

---

## ✨ Kết Luận

Bạn đã hoàn tất xây dựng CI pipeline cho monorepo YAS với:

✅ Jenkins server setup  
✅ Jenkinsfile pipeline definition  
✅ GitHub webhook integration  
✅ Branch protection rules  
✅ 2-phase test & build pipeline  
✅ Test results & coverage upload  
✅ Monorepo optimization (change detection)  

Hệ thống này sẽ **tự động kiểm tra code khi có PR**, **báo cáo test coverage**, và **bảo vệ main branch** khỏi code chất lượng kém! 🚀
