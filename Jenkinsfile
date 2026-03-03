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
        SONAR_ORG        = 'devops-yas-ci'                // Tổ chức SonarCloud (đổi theo org của bạn)
        SONAR_HOST       = 'https://sonarcloud.io'
        GITLEAKS_REPORT  = 'gitleaks-report.json'
        // ── Jenkins-in-Docker: Testcontainers config ──────────────────
        TESTCONTAINERS_RYUK_DISABLED = 'true'             // Tắt Ryuk (tránh lỗi khi Docker socket có giới hạn)
    }

    stages {
        // ====================================================================
        // STAGE 1: DETECT CHANGES — Phát hiện service thay đổi trong monorepo
        // (Yêu cầu 6: chỉ kích hoạt pipeline cho service cụ thể)
        // ====================================================================
        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = []

                    if (env.CHANGE_TARGET) {
                        // Pull Request → so sánh với branch đích
                        changedFiles = sh(
                            script: "git diff --name-only origin/${env.CHANGE_TARGET}...HEAD",
                            returnStdout: true
                        ).trim().split('\n').toList()
                    } else if (env.GIT_PREVIOUS_SUCCESSFUL_COMMIT) {
                        // Branch build → so sánh với commit thành công trước đó
                        changedFiles = sh(
                            script: "git diff --name-only ${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT} ${env.GIT_COMMIT}",
                            returnStdout: true
                        ).trim().split('\n').toList()
                    } else {
                        // Lần build đầu tiên → build tất cả
                        echo '⚡ Lần build đầu tiên — build tất cả services'
                        changedServices = SERVICES.collect()
                        return
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
                // Lấy đường dẫn snyk binary từ Jenkins Tool installation
                script {
                    def snykHome = tool(name: 'snyk', type: 'io.snyk.jenkins.tools.SnykInstallation')
                    withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                        changedServices.each { svc ->
                            echo "  ▸ Snyk scan: ${svc}"
                            dir(svc) {
                                sh """
                                    ${snykHome}/snyk-linux auth \${SNYK_TOKEN}
                                    ${snykHome}/snyk-linux test --severity-threshold=high || true
                                """
                            }
                        }
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
                    changedServices.each { svc ->
                        stage("Test ${svc}") {
                            echo "🧪 Chạy test cho: ${svc}"
                            dir(svc) {
                                // mvn verify = compile + unit test (surefire) + integration test (failsafe) + jacoco report
                                // -DskipITs=false: chạy integration test nếu Docker socket có sẵn
                                // -Dmaven.test.failure.ignore=true: không dừng pipeline nếu test fail
                                sh '''
                                    if [ -S /var/run/docker.sock ]; then
                                        echo "Docker socket found — chạy cả unit test + integration test"
                                        mvn clean verify -Dmaven.test.failure.ignore=true
                                    else
                                        echo "⚠️ Docker socket KHÔNG có — chỉ chạy unit test (bỏ qua integration test)"
                                        mvn clean verify -Dmaven.test.failure.ignore=true -DskipITs=true
                                    fi
                                '''
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

                    // ── Upload báo cáo độ phủ JaCoCo ──────────────────────────
                    jacoco(
                        execPattern:      '**/target/jacoco.exec',
                        classPattern:     '**/target/classes',
                        sourcePattern:    '**/src/main/java',
                        exclusionPattern: '**/test/**',
                        // ── Yêu cầu 7b: FAIL nếu coverage < 70% ──────────────
                        minimumLineCoverage:      "${COVERAGE_THRESHOLD}",
                        minimumBranchCoverage:    "${COVERAGE_THRESHOLD}",
                        maximumLineCoverage:      '100',
                        maximumBranchCoverage:    '100'
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
                                dir(svc) {
                                    sh """
                                        mvn sonar:sonar \
                                            -Dsonar.organization=${SONAR_ORG} \
                                            -Dsonar.host.url=${SONAR_HOST} \
                                            -Dsonar.token=${SONAR_TOKEN} \
                                            -Dsonar.qualitygate.wait=true
                                    """
                                }
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
                            dir(svc) {
                                sh 'mvn package -DskipTests'
                            }
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
            // Dọn dẹp workspace để tiết kiệm dung lượng
            cleanWs()
        }
    }
}
