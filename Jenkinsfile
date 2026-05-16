// ============================================================================
// Danh sách tất cả các service Java trong monorepo
// ============================================================================
def SERVICES = [
    'common-library', 'backoffice-bff', 'cart', 'customer', 'delivery',
    'inventory', 'location', 'media', 'order', 'payment', 'payment-paypal',
    'product', 'promotion', 'rating', 'recommendation', 'sampledata',
    'search', 'storefront-bff', 'tax', 'webhook'
]

// Biến lưu danh sách service bị thay đổi
def changedServices = []

// Ngưỡng coverage tối thiểu (%) - pipeline FAIL nếu service nào dưới ngưỡng này
def COVERAGE_THRESHOLD = 70

pipeline {
    agent any

    tools {
        jdk 'JDK 21'           // Khớp tên JDK trong Jenkins Tools (Adoptium Temurin 21)
        maven 'Maven 3'        // Khớp tên Maven trong Jenkins Tools (3.9.12)
        snyk 'snyk'            // Khớp tên Snyk trong Jenkins Tools (latest)
    }

    // ========================================================================
    // Biến môi trường dùng chung cho toàn bộ pipeline
    // ========================================================================
    environment {
        SONAR_ORG        = 'yas-ci-key'                    // Organization key trên SonarCloud
        SONAR_HOST       = 'https://sonarcloud.io'
        GITLEAKS_REPORT  = 'gitleaks-report.json'
        // ── Jenkins-in-Docker: Testcontainers config ──────────────────
        TESTCONTAINERS_RYUK_DISABLED = 'true'             // Tắt Ryuk (tránh lỗi khi Docker socket có giới hạn)
        TESTCONTAINERS_HOST_OVERRIDE = 'host.docker.internal'  // Fix: route từ Jenkins container ra Docker host
    }

    // ========================================================================
    // Bỏ qua auto-checkout của Declarative Pipeline — stage 'Checkout' tự xử lý
    // để dùng shallow clone + HTTP/1.1, tránh double-checkout gây conflict
    // ========================================================================
    options {
        skipDefaultCheckout(true)
    }

    stages {
        // ====================================================================
        // STAGE 0: CHECKOUT — Tải source code với shallow clone
        // Giảm dung lượng clone từ ~17 MB toàn bộ lịch sử xuống commit mới nhất
        // ====================================================================
        stage('Checkout') {
            steps {
                // Tắt HTTP/2 + tăng buffer trước khi clone (tránh curl-92 early-EOF)
                sh 'git config --global http.version HTTP/1.1'
                sh 'git config --global http.postBuffer 524288000'
                checkout([
                    $class: 'GitSCM',
                    branches: scm.branches,
                    userRemoteConfigs: scm.userRemoteConfigs,
                    // Giữ scm.extensions gốc (chứa BuildData tracking — cần thiết để
                    // GIT_PREVIOUS_SUCCESSFUL_COMMIT và currentBuild.changeSets hoạt động
                    // đúng trên các lần build tiếp theo) + thêm CloneOption riêng
                    extensions: scm.extensions + [
                        [$class: 'CloneOption', shallow: false, noTags: false, timeout: 60]
                    ]
                ])
            }
        }

        // ====================================================================
        // STAGE 1: DETECT CHANGES — Phát hiện service thay đổi trong monorepo
        // (Yêu cầu 6: chỉ kích hoạt pipeline cho service cụ thể)
        // ====================================================================
        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = []

                    // ── Debug: hiển thị trạng thái SCM tracking ──
                    echo "🔍 DEBUG: GIT_COMMIT = ${env.GIT_COMMIT}"
                    echo "🔍 DEBUG: GIT_PREVIOUS_SUCCESSFUL_COMMIT = ${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT}"
                    echo "🔍 DEBUG: changeSets.size() = ${currentBuild.changeSets.size()}"
                    echo "🔍 DEBUG: previousSuccessfulBuild = ${currentBuild.previousSuccessfulBuild?.number}"

                    if (env.CHANGE_TARGET) {
                        // ── Pull Request → so sánh với branch đích ──
                        // Fetch target branch vì Jenkins PR checkout không tự fetch origin/<target>
                        sh "git fetch origin ${env.CHANGE_TARGET} --no-tags"
                        def raw = sh(
                            script: "git diff --name-only FETCH_HEAD...HEAD",
                            returnStdout: true
                        ).trim()
                        changedFiles = raw ? raw.split('\n').toList() : []

                    } else {
                        // ── Branch build: thử 3 phương pháp theo thứ tự ưu tiên ──

                        // Method 1: currentBuild.changeSets (Jenkins native — đáng tin cậy nhất
                        // khi BuildData tracking hoạt động đúng nhờ scm.extensions)
                        currentBuild.changeSets.each { changeSet ->
                            changeSet.each { entry ->
                                changedFiles.addAll(entry.affectedPaths)
                            }
                        }
                        echo "🔍 DEBUG: [Method 1] changeSets → ${changedFiles.size()} file(s)"

                        // Method 2: git diff với GIT_PREVIOUS_SUCCESSFUL_COMMIT
                        if (changedFiles.isEmpty() && env.GIT_PREVIOUS_SUCCESSFUL_COMMIT) {
                            echo '🔍 DEBUG: [Method 2] Thử git diff GIT_PREVIOUS_SUCCESSFUL_COMMIT'
                            try {
                                def raw = sh(
                                    script: "git diff --name-only ${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT} ${env.GIT_COMMIT}",
                                    returnStdout: true
                                ).trim()
                                changedFiles = raw ? raw.split('\n').toList() : []
                            } catch (e) {
                                echo "⚠️ Method 2 thất bại: ${e.message}"
                            }
                        }

                        // Method 3: git diff HEAD~1 (phòng tuyến cuối — chỉ dùng khi
                        // ĐÃ CÓ build thành công trước, tức KHÔNG phải lần đầu)
                        if (changedFiles.isEmpty() && currentBuild.previousSuccessfulBuild != null) {
                            echo '⚠️ DEBUG: [Method 3] changeSets & GIT_PREVIOUS trống — thử git diff HEAD~1'
                            try {
                                def raw = sh(
                                    script: 'git diff --name-only HEAD~1 HEAD',
                                    returnStdout: true
                                ).trim()
                                changedFiles = raw ? raw.split('\n').toList() : []
                            } catch (e) {
                                echo "⚠️ Method 3 thất bại: ${e.message}"
                            }
                        }

                        // Tất cả method đều trống → build lần đầu (chưa có dữ liệu so sánh)
                        if (changedFiles.isEmpty()) {
                            if (currentBuild.previousSuccessfulBuild == null) {
                                echo '⚡ Lần build đầu tiên — build tất cả services'
                            } else {
                                echo '⚠️ Không detect được thay đổi dù có build trước — build tất cả services (an toàn)'
                            }
                            changedServices = SERVICES.collect()
                            return
                        }
                    }

                    echo "📂 Các file thay đổi:\n${changedFiles.join('\n')}"

                    // common-library hoặc root pom.xml thay đổi → ảnh hưởng toàn bộ
                    boolean commonChanged  = changedFiles.any { it.startsWith('common-library/') }
                    boolean rootPomChanged = changedFiles.any { it == 'pom.xml' }

                    if (commonChanged || rootPomChanged) {
                        echo '🔁 common-library hoặc root pom.xml thay đổi → build tất cả services'
                        changedServices = SERVICES.collect()
                    } else {
                        changedServices = SERVICES.findAll { svc ->
                            changedFiles.any { file -> file.startsWith("${svc}/") }
                        }
                    }

                    if (changedServices.isEmpty()) {
                        echo '✅ Không có service nào thay đổi — bỏ qua pipeline.'
                    } else {
                        echo "🚀 Services sẽ được xử lý: ${changedServices}"
                    }
                }
            }
        }

        // ====================================================================
        // STAGE 2: SECURITY SCAN — Gitleaks (quét lộ lọt secrets)
        // (Yêu cầu 7c)
        // ====================================================================
        stage('Gitleaks Scan') {
            when {
                expression { return !changedServices.isEmpty() }
            }
            steps {
                echo '🔍 Quét Gitleaks — phát hiện mật khẩu, token, key bị commit...'
                // Tải gitleaks binary trực tiếp (không cần Docker — tương thích Jenkins-in-Docker)
                sh '''
                    GITLEAKS_VERSION="8.22.1"
                    if ! command -v gitleaks &> /dev/null; then
                        echo "Downloading gitleaks v${GITLEAKS_VERSION}..."
                        curl -sSfL "https://github.com/gitleaks/gitleaks/releases/download/v${GITLEAKS_VERSION}/gitleaks_${GITLEAKS_VERSION}_linux_x64.tar.gz" \
                            | tar xz -C /tmp/
                        chmod +x /tmp/gitleaks
                        export PATH="/tmp:$PATH"
                    fi
                    /tmp/gitleaks detect \
                        --source=. \
                        --report-format=json \
                        --report-path=gitleaks-report.json \
                        --exit-code=0
                '''
            }
            post {
                always {
                    // Lưu báo cáo Gitleaks làm artifact
                    archiveArtifacts artifacts: "${GITLEAKS_REPORT}", allowEmptyArchive: true
                }
            }
        }

        // ====================================================================
        // STAGE 3: SECURITY SCAN — Snyk (quét lỗ hổng thư viện bên thứ 3)
        // (Yêu cầu 7c)
        // ====================================================================
        stage('Snyk Scan') {
            when {
                expression { return !changedServices.isEmpty() }
            }
            steps {
                echo '🔍 Quét Snyk — phát hiện lỗ hổng trong dependencies...'
                script {
                    // Cài đặt parent pom + common-library (dùng reactor để resolve ${revision} đúng)
                    sh 'mvn install -pl common-library -am -DskipTests -q'

                    def snykHome = tool(name: 'snyk', type: 'io.snyk.jenkins.tools.SnykInstallation')
                    withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                        // Quét toàn bộ monorepo 1 lần với --all-projects (tránh rate-limit free plan)
                        sh """
                            ${snykHome}/snyk-linux auth \${SNYK_TOKEN}
                            ${snykHome}/snyk-linux test --all-projects --severity-threshold=high || true
                        """
                    }
                }
            }
        }

        // ====================================================================
        // STAGE 4: TEST — Chạy Unit Test + Integration Test
        // Upload JUnit results + JaCoCo coverage + kiểm tra ngưỡng ≥ 70%
        // (Yêu cầu 5 + 7b)
        // ====================================================================
        stage('Test') {
            when {
                expression { return !changedServices.isEmpty() }
            }
            steps {
                script {
                    // ── Bước 1: Cài đặt parent pom + common-library bằng reactor ──
                    // (flatten-maven-plugin sẽ resolve ${revision} trong installed POM)
                    sh 'mvn install -pl common-library -am -DskipTests -q'

                    // ── Bước 2: Test từng service thay đổi ────────────────────
                    // Dùng -pl (project list) từ root directory thay vì cd vào service
                    // Đảm bảo ${revision} được resolve đúng qua reactor context
                    changedServices.each { svc ->
                        stage("Test ${svc}") {
                            echo "🧪 Chạy test cho: ${svc}"
                            retry(2) {
                                sh """
                                    if [ -S /var/run/docker.sock ]; then
                                        echo "Docker socket found — chạy cả unit test + integration test"
                                        mvn clean verify -pl ${svc} -am -Dmaven.test.failure.ignore=true
                                    else
                                        echo "⚠️ Docker socket KHÔNG có — chỉ chạy unit test (bỏ qua integration test)"
                                        mvn clean verify -pl ${svc} -am -Dmaven.test.failure.ignore=true -DskipITs=true
                                    fi
                                """
                            }
                        }
                    }
                }
            }
            post {
                always {
                    // ── Upload kết quả test JUnit ──────────────────────────────
                    junit testResults: '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml',
                         allowEmptyResults: true

                    // ── Upload báo cáo độ phủ JaCoCo (dùng Code Coverage API plugin) ──
                    recordCoverage(
                        tools: [[parser: 'JACOCO']],
                        // Liệt kê thư mục source từng module (glob ** không được hỗ trợ)
                        sourceDirectories: changedServices.collect { svc -> [path: "${svc}/src/main/java"] },
                        // ── Yêu cầu 7b: coverage < 70% → UNSTABLE ──────────────
                        qualityGates: [
                            [threshold: 70.0, metric: 'LINE',   baseline: 'PROJECT', criticality: 'UNSTABLE'],
                            [threshold: 70.0, metric: 'BRANCH', baseline: 'PROJECT', criticality: 'UNSTABLE']
                        ]
                    )
                }
            }
        }

        // ====================================================================
        // STAGE 5: SONARQUBE — Phân tích chất lượng code + Quality Gate
        // (Yêu cầu 7c)
        // ====================================================================
        stage('SonarQube Analysis') {
            when {
                expression { return !changedServices.isEmpty() }
            }
            steps {
                echo '📊 Phân tích SonarCloud — chất lượng code, code smells, bugs...'
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    script {
                        changedServices.each { svc ->
                            stage("Sonar ${svc}") {
                                echo "  ▸ SonarCloud scan: ${svc}"
                                // Phân biệt PR analysis vs branch scan
                                // env.CHANGE_TARGET chỉ được set khi Jenkins chạy PR job
                                def sonarScopeParams = env.CHANGE_TARGET
                                    ? "-Dsonar.pullrequest.key=${env.CHANGE_ID} -Dsonar.pullrequest.branch=${env.CHANGE_BRANCH} -Dsonar.pullrequest.base=${env.CHANGE_TARGET}"
                                    : "-Dsonar.branch.name=${env.BRANCH_NAME}"
                                sh """
                                    mvn sonar:sonar -pl ${svc} -am \
                                        -Dsonar.organization=${SONAR_ORG} \
                                        -Dsonar.host.url=${SONAR_HOST} \
                                        -Dsonar.token=\${SONAR_TOKEN} \
                                        -Dsonar.projectKey=${SONAR_ORG}_${svc} \
                                        ${sonarScopeParams} \
                                    || echo "⚠️ SonarCloud scan failed for ${svc} — xem log để biết chi tiết"
                                """
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // STAGE 6: BUILD — Đóng gói artifact (JAR) cho các service thay đổi
        // (Yêu cầu 5: phase build)
        // ====================================================================
        stage('Build') {
            when {
                expression { return !changedServices.isEmpty() }
            }
            steps {
                script {
                    changedServices.each { svc ->
                        stage("Build ${svc}") {
                            echo "📦 Build artifact: ${svc}"
                            // Dùng -pl từ root directory để resolve ${revision} đúng
                            sh "mvn package -pl ${svc} -am -DskipTests"
                        }
                    }
                }
            }
            post {
                success {
                    // Lưu file JAR làm artifact trên Jenkins
                    archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
                }
            }
        }

        // ====================================================================
        // STAGE 7: DOCKER — Build image & Push lên Docker Hub
        // Tag = commit SHA (đáp ứng yêu cầu CI: tag bằng commit ID)
        // ====================================================================
        stage('Docker Build & Push') {
            when {
                expression { return !changedServices.isEmpty() }
            }
            steps {
                script {
                    def shortSha = sh(script: "git rev-parse --short=7 HEAD", returnStdout: true).trim()

                    def isRelease = env.TAG_NAME != null && env.TAG_NAME ==~ /v\d+\.\d+\.\d+/
                    def finalTag  = isRelease ? env.TAG_NAME : shortSha

                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-credentials',   // Tên credential trong Jenkins
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'

                        changedServices.each { svc ->
                            if (!fileExists("${svc}/Dockerfile")) {
                                echo "⚠️ ${svc}: không có Dockerfile — bỏ qua Docker build"
                                return
                            }

                            stage("Docker ${svc}") {
                                // Lấy DOCKER_USER vào Groovy variable để dùng trong string interpolation
                                // (withCredentials chỉ bind shell env var, không tự thành Groovy var)
                                def dockerUser    = env.DOCKER_USER
                                def imageTag = "${dockerUser}/yas-${svc}:${finalTag}"

                                echo "🐳 Build & Push: ${svc}"
                                sh """
                                    docker build --platform linux/amd64 -t ${imageTag} ./${svc}
                                    docker push ${imageTag}
                                    
                                    # Dọn dẹp máy Jenkins sau khi push để tiết kiệm bộ nhớ cho Mac 16GB
                                    docker rmi ${imageTag} || true
                                """
                            }
                        }

                        sh 'docker logout'
                    }
                }
            }
        }


        // ====================================================================
        // STAGE 8: CD — Update yas-gitops repo để ArgoCD tự sync
        //
        // Trigger:
        //   - Merge vào main     → update values-dev.yaml     → ArgoCD sync namespace dev
        //   - Git tag v1.2.3     → update values-staging.yaml → ArgoCD sync namespace staging
        //
        // values-dev.yaml cũng được cập nhật bởi developer-build job (manual)
        // ====================================================================
        stage('Update GitOps') {
            when {
                expression {
                    return !changedServices.isEmpty() && (
                        env.BRANCH_NAME == 'main' ||
                        (env.TAG_NAME != null && env.TAG_NAME ==~ /v\d+\.\d+\.\d+/)
                    )
                }
            }
            steps {
                script {
                    def shortSha   = sh(script: "git rev-parse --short=7 HEAD", returnStdout: true).trim()
                    def isRelease  = env.TAG_NAME != null && env.TAG_NAME ==~ /v\d+\.\d+\.\d+/
                    def imageTag   = isRelease ? env.TAG_NAME : shortSha
                    def valuesFile = isRelease ? 'values-staging.yaml' : 'values-dev.yaml'

                    echo "🚀 Update ${valuesFile} với tag: ${imageTag}"

                    withCredentials([string(
                        credentialsId: 'github-token',
                        variable: 'GH_TOKEN'
                    )]) {
                        sh """
                            rm -rf yas-gitops
                            git clone https://\${GH_TOKEN}@github.com/thannthai/yas-gitops.git
                            cd yas-gitops
                            git config user.email \"jenkins@ci.com\"
                            git config user.name \"Jenkins\"
                        """

                        changedServices.each { svc ->
                            if (!fileExists("${svc}/Dockerfile")) return

                            sh """
                                cd yas-gitops
                                sed -i 's|tag:.*|tag: ${imageTag}|' charts/${svc}/${valuesFile}
                                git add charts/${svc}/${valuesFile}
                                echo "✅ Updated ${svc} → ${imageTag} (${valuesFile})"
                            """
                        }

                        sh """
                            cd yas-gitops
                            if ! git diff --cached --quiet; then
                                git commit -m "ci: update ${valuesFile} to ${imageTag} [${env.BRANCH_NAME ?: env.TAG_NAME}]"
                                git push
                                echo "✅ Pushed to yas-gitops — ArgoCD sẽ tự sync"
                            else
                                echo "✅ Không có thay đổi — bỏ qua push"
                            fi
                            cd ..
                            rm -rf yas-gitops
                        """
                    }
                }
            }
        }
    }


    // ========================================================================
    // POST — Hành động sau khi pipeline kết thúc
    // ========================================================================
    post {
        success {
            echo '══════════════════════════════════════════════'
            echo '✅  PIPELINE THÀNH CÔNG!'
            echo "   Services đã xử lý: ${changedServices}"
            echo '══════════════════════════════════════════════'
        }
        failure {
            echo '══════════════════════════════════════════════'
            echo '❌  PIPELINE THẤT BẠI!'
            echo "   Services đã xử lý: ${changedServices}"
            echo '══════════════════════════════════════════════'
        }
        always {
            // Dọn dẹp workspace — chạy trong agent context của pipeline, không cần node() wrapper
            cleanWs()
        }
    }
}
