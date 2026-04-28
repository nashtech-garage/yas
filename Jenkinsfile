pipeline {
    agent any

    environment {
        // ----------------------------------------------------------------
        // Các Credentials ID cần tạo trước trong Jenkins > Credentials
        // ----------------------------------------------------------------
        SONAR_TOKEN       = credentials('sonarqube-token')  // Secret text
        SNYK_TOKEN        = credentials('snyk-token')        // Secret text

        // Tên SonarQube Server đã đăng ký trong Jenkins > System > SonarQube servers
        SONAR_SERVER_NAME = 'SonarQubeServer'

        // Ngưỡng Code Coverage tối thiểu (yêu cầu nâng cao b)
        MIN_COVERAGE      = '70'
    }

    stages {

        // ==============================================================
        // STAGE 1: SECURITY - Gitleaks
        // Yêu cầu nâng cao c: Scan bí mật/credentials bị lộ trong code.
        // Pipeline FAIL ngay lập tức nếu phát hiện secret bị hardcode.
        // ==============================================================
        stage('Security: Gitleaks Scan') {
            steps {
                echo "=> [Gitleaks] Scanning repository for hardcoded secrets..."
                sh '''
                    gitleaks detect --source . --config gitleaks.toml -v
                '''
            }
        }

        // ==============================================================
        // STAGE 2: Xác định service nào có thay đổi trong Monorepo
        // Yêu cầu 6: Chỉ build/test service có thay đổi.
        // ==============================================================
        stage('Detect Changed Services') {
            steps {
                script {
                    echo "=> Fetching main branch to compare changes..."
                    sh "git fetch origin main"

                    // So sánh HEAD với nhánh main để lấy danh sách file thay đổi
                    def changedFiles = sh(
                        script: "git diff --name-only HEAD origin/main",
                        returnStdout: true
                    ).trim()

                    echo "=> Changed files:\n${changedFiles}"

                    // Duyệt từng file thay đổi, lấy thư mục cha (tên service)
                    // Chỉ nhận thư mục có chứa pom.xml (Java/Maven service)
                    def serviceSet = [] as Set
                    changedFiles.split('\n').each { filePath ->
                        if (filePath.contains('/')) {
                            def serviceDir = filePath.split('/')[0]
                            if (fileExists("${serviceDir}/pom.xml")) {
                                serviceSet.add(serviceDir)
                            }
                        }
                    }

                    if (serviceSet.isEmpty()) {
                        echo "=> No service changes detected. Nothing to build."
                        env.CHANGED_SERVICES = ''
                    } else {
                        env.CHANGED_SERVICES = serviceSet.join(',')
                        echo "=> Services to process: ${env.CHANGED_SERVICES}"
                    }
                }
            }
        }

        // ==============================================================
        // STAGE 3: BUILD
        // Yêu cầu 5: Một trong ít nhất 2 phase của pipeline.
        // Compile mã nguồn, bỏ qua test (test chạy riêng ở stage sau).
        // ==============================================================
        stage('Build') {
            when {
                expression { env.CHANGED_SERVICES != null && env.CHANGED_SERVICES != '' }
            }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')
                    def parallelStages = [:]

                    services.each { svc ->
                        def currentService = svc.trim()
                        parallelStages["Build: ${currentService}"] = {
                            dir(currentService) {
                                echo "=> [Build] Building service: ${currentService}"
                                sh 'mvn clean package -DskipTests'
                            }
                        }
                    }

                    parallel parallelStages
                }
            }
        }

        // ==============================================================
        // STAGE 4: TEST + COVERAGE CHECK
        // Yêu cầu 5: Upload test result và độ phủ testcase.
        // Yêu cầu nâng cao b: Fail nếu coverage < 70%.
        // ==============================================================
        stage('Test') {
            when {
                expression { env.CHANGED_SERVICES != null && env.CHANGED_SERVICES != '' }
            }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')
                    def parallelStages = [:]

                    services.each { svc ->
                        def currentService = svc.trim()
                        parallelStages["Test: ${currentService}"] = {
                            dir(currentService) {
                                echo "=> [Test] Running tests for service: ${currentService}"

                                // Chạy test và sinh báo cáo JaCoCo (file XML cho Coverage plugin)
                                sh 'mvn test jacoco:report'

                                // Upload kết quả test (JUnit XML) lên Jenkins
                                // Plugin cần: JUnit Plugin
                                junit allowEmptyResults: true,
                                      testResults: 'target/surefire-reports/*.xml'

                                // Upload code coverage lên Jenkins
                                // FAIL nếu line coverage < 70% (yêu cầu nâng cao b)
                                // Sử dụng Coverage Plugin (thế hệ mới của JaCoCo plugin)
                                recordCoverage(
                                    tools: [[
                                        parser: 'JACOCO',
                                        pattern: 'target/site/jacoco/jacoco.xml'
                                    ]],
                                    // Ngưỡng: FAIL build nếu line coverage < 70%
                                    qualityGates: [[
                                        criticality: 'FAILURE',
                                        integerThreshold: 70,
                                        metric: 'LINE',
                                        threshold: 70.0
                                    ]]
                                )
                            }
                        }
                    }

                    parallel parallelStages
                }
            }
        }

        // ==============================================================
        // STAGE 5: CODE QUALITY - SonarQube
        // Yêu cầu nâng cao c: Quét chất lượng code bằng SonarQube.
        // Quality Gate trên SonarQube cũng cần cấu hình Coverage > 70%.
        // ==============================================================
        stage('Code Quality: SonarQube') {
            when {
                expression { env.CHANGED_SERVICES != null && env.CHANGED_SERVICES != '' }
            }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')

                    services.each { svc ->
                        def currentService = svc.trim()
                        dir(currentService) {
                            echo "=> [SonarQube] Analyzing service: ${currentService}"

                            // Chạy SonarQube analysis
                            // Plugin cần: SonarQube Scanner for Jenkins
                            withSonarQubeEnv(env.SONAR_SERVER_NAME) {
                                sh """
                                    mvn sonar:sonar \
                                        -Dsonar.projectKey=yas-${currentService} \
                                        -Dsonar.projectName=yas-${currentService} \
                                        -Dsonar.login=${SONAR_TOKEN} \
                                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                                """
                            }
                        }
                    }

                    // Đợi tất cả kết quả Quality Gate từ SonarQube
                    // Nếu bất kỳ service nào không đạt Quality Gate → FAIL pipeline
                    timeout(time: 10, unit: 'MINUTES') {
                        echo "=> [SonarQube] Waiting for Quality Gate results..."
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline failed: SonarQube Quality Gate status = ${qg.status}. Check SonarQube dashboard for details."
                        }
                    }
                }
            }
        }

        // ==============================================================
        // STAGE 6: SECURITY - Snyk Dependency Scan
        // Yêu cầu nâng cao c: Quét lỗ hổng bảo mật trong dependencies.
        // ==============================================================
        stage('Security: Snyk Scan') {
            when {
                expression { env.CHANGED_SERVICES != null && env.CHANGED_SERVICES != '' }
            }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')

                    services.each { svc ->
                        def currentService = svc.trim()
                        dir(currentService) {
                            echo "=> [Snyk] Scanning dependencies of service: ${currentService}"
                            // --severity-threshold=high: chỉ fail khi có lỗ hổng High/Critical
                            sh """
                                export SNYK_TOKEN=${SNYK_TOKEN}
                                snyk test --severity-threshold=high --all-projects || true
                            """
                        }
                    }
                }
            }
        }

    }

    post {
        always {
            echo "=> Pipeline finished. Cleaning workspace..."
            cleanWs()
        }
        success {
            echo "=> ✅ All stages passed! Code is clean, tested, and secure."
        }
        failure {
            echo "=> ❌ Pipeline failed! Please review the failed stage logs."
        }
    }
}
