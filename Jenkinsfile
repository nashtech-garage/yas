// ============================================================================
// Danh sách tất cả các service trong monorepo
// ============================================================================
def SERVICES = [
    'common-library', 'backoffice-bff', 'cart', 'customer', 'delivery',
    'inventory', 'location', 'media', 'order', 'payment', 'payment-paypal',
    'product', 'promotion', 'rating', 'recommendation', 'sampledata',
    'search', 'storefront-bff', 'tax', 'webhook'
]

// Biến lưu danh sách service bị thay đổi (sẽ được xác định ở stage đầu tiên)
def changedServices = []

pipeline {
    // Chạy trên bất kỳ agent nào có sẵn
    agent any

    // Khai báo các công cụ đã cấu hình trong Jenkins (phải khớp tên chính xác)
    tools {
        maven 'Maven 3'
    }

    stages {
        // ====================================================================
        // PHASE 0: Phát hiện thay đổi trong monorepo
        // Chỉ build/test service nào có file thay đổi trong thư mục của nó
        // ====================================================================
        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = []

                    if (env.CHANGE_TARGET) {
                        // Trường hợp Pull Request: so sánh với branch đích
                        changedFiles = sh(
                            script: "git diff --name-only origin/${env.CHANGE_TARGET}...HEAD",
                            returnStdout: true
                        ).trim().split('\n').toList()
                    } else if (env.GIT_PREVIOUS_SUCCESSFUL_COMMIT) {
                        // Trường hợp Branch build: so sánh với commit thành công trước đó
                        changedFiles = sh(
                            script: "git diff --name-only ${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT} ${env.GIT_COMMIT}",
                            returnStdout: true
                        ).trim().split('\n').toList()
                    } else {
                        // Lần build đầu tiên hoặc fallback → build tất cả
                        echo 'Lần build đầu tiên - build tất cả services'
                        changedServices = SERVICES.collect()
                        return
                    }

                    echo "Các file thay đổi: ${changedFiles}"

                    // Nếu common-library hoặc root pom.xml thay đổi → build tất cả
                    boolean commonChanged = changedFiles.any { it.startsWith('common-library/') }
                    boolean rootPomChanged = changedFiles.any { it == 'pom.xml' }

                    if (commonChanged || rootPomChanged) {
                        echo 'common-library hoặc root pom.xml thay đổi → build tất cả services'
                        changedServices = SERVICES.collect()
                    } else {
                        // Chỉ chọn service có file thay đổi trong thư mục của nó
                        changedServices = SERVICES.findAll { svc ->
                            changedFiles.any { file -> file.startsWith("${svc}/") }
                        }
                    }

                    if (changedServices.isEmpty()) {
                        echo 'Không phát hiện thay đổi ở service nào. Bỏ qua test & build.'
                    } else {
                        echo "Services sẽ được build: ${changedServices}"
                    }
                }
            }
        }

        // ====================================================================
        // PHASE 1: TEST - Chạy unit test + integration test cho các service thay đổi
        // Upload kết quả test (JUnit) và độ phủ test (JaCoCo)
        // ====================================================================
        stage('Test') {
            when {
                expression { return !changedServices.isEmpty() }
            }
            steps {
                script {
                    changedServices.each { svc ->
                        stage("Test ${svc}") {
                            echo "▶ Chạy test cho service: ${svc}"
                            dir(svc) {
                                // mvn verify sẽ chạy cả unit test (surefire) và integration test (failsafe)
                                // JaCoCo đã được cấu hình trong pom.xml → tự động tạo báo cáo coverage
                                sh 'mvn clean verify -Dmaven.test.failure.ignore=true'
                            }
                        }
                    }
                }
            }
            post {
                always {
                    // Upload kết quả test JUnit (unit test + integration test)
                    junit testResults: '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml',
                         allowEmptyResults: true

                    // Upload báo cáo độ phủ test JaCoCo
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/test/**'
                    )
                }
            }
        }

        // ====================================================================
        // PHASE 2: BUILD - Đóng gói artifact (JAR) cho các service thay đổi
        // ====================================================================
        stage('Build') {
            when {
                expression { return !changedServices.isEmpty() }
            }
            steps {
                script {
                    changedServices.each { svc ->
                        stage("Build ${svc}") {
                            echo "▶ Build artifact cho service: ${svc}"
                            dir(svc) {
                                // Package mà không chạy lại test (đã test ở phase trước)
                                sh 'mvn package -DskipTests'
                            }
                        }
                    }
                }
            }
        }
    }

    // Hành động sau khi Pipeline chạy xong
    post {
        success {
            echo '✅ Pipeline hoàn tất thành công!'
        }
        failure {
            echo '❌ Pipeline thất bại!'
        }
        always {
            echo "Services đã xử lý: ${changedServices ?: 'Không có'}"
        }
    }
}
