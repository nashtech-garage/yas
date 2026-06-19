def changedServices = 'none'

pipeline {
    agent any

    stages {

        stage('Detect Changed Services') {
            steps {
                script {
                    // Dam bao co ref main trong workspace Jenkins truoc khi tinh changed files
                    sh 'git fetch --no-tags --prune origin +refs/heads/main:refs/remotes/origin/main || true'

                    def changedFiles = sh(
                        script: '''
                            if git rev-parse --verify origin/main >/dev/null 2>&1; then
                                git diff --name-only origin/main...HEAD
                            else
                                echo "origin/main not found, fallback to latest commit diff" >&2
                                git diff --name-only HEAD~1..HEAD || git ls-files
                            fi
                        ''',
                        returnStdout: true
                    ).trim()

                    echo "Changed files:\n${changedFiles}"

                    def services = [
                        // Business Services (Backend - Java/Spring)
                        'cart', 'customer', 'delivery', 'inventory', 'location', 
                        'media', 'order', 'payment', 'payment-paypal', 'product', 
                        'promotion', 'rating', 'recommendation', 'search', 'tax',
                        
                        // BFF & Gateways
                        'backoffice-bff', 'storefront-bff', 'identity',
                        
                        // Frontend (Next.js)
                        'backoffice', 'storefront'
                    ]

                    def affected = services.findAll { svc ->
                        changedFiles && (changedFiles =~ /(?m)^${java.util.regex.Pattern.quote(svc)}\//)
                    }

                    echo "Affected services detected: ${affected}"

                    changedServices = affected.isEmpty() ? 'none' : affected.join(',')
                    if (changedServices == 'none') {
                        echo "No service changes detected. Skipping build/test."
                    } else {
                        echo "Services to build/test: ${changedServices}"
                    }
                }
            }
        }

       stage('Security Scan') {
    steps {
        echo "--- Đang tải và thực thi GitLeaks ---"
        script {
            // Tải và cài đặt GitLeaks binary trực tiếp trên Jenkins workspace
            sh '''
                curl -sSL https://github.com/gitleaks/gitleaks/releases/download/v8.18.4/gitleaks_8.18.4_linux_x64.tar.gz -o gitleaks.tar.gz
                tar -xzf gitleaks.tar.gz
                chmod +x gitleaks
            '''
            
            // Chi quet commit tren nhanh hien tai so voi main de tranh fail vi leak cu trong lich su du an
            sh './gitleaks detect --source=. --config=gitleaks.toml --log-opts="origin/main..HEAD" --report-format=json --report-path=gitleaks-report.json --exit-code=1'
        }
    }
    post {
        always {
            // Lưu trữ file báo cáo quét bảo mật sau mỗi lần chạy
            archiveArtifacts artifacts: 'gitleaks-report.json', fingerprint: true, allowEmptyArchive: true
        }
    }
}

        stage('Test Phase') {
            when {
                expression { changedServices?.trim() && changedServices != 'none' }
            }
            steps {
                script {
                    def services = (changedServices ?: '').split(',').findAll { it?.trim() }
                    services.each { svc ->
                        echo "Running tests for: ${svc}"
                        dir("${svc}") {
                            sh 'chmod +x mvnw || true'
                            sh "./mvnw verify jacoco:report -DskipITs -f ../pom.xml -pl ${svc} -am -U -Drevision=1.0-SNAPSHOT"
                        }
                    }
                }
            }
            post {
                always {
                    script {
                        def services = (changedServices ?: '').split(',').findAll { it?.trim() }
                        services.each { svc ->
                            // Publish JUnit test results
                            junit(
                                testResults: "${svc}/target/surefire-reports/*.xml",
                                allowEmptyResults: true
                            )
                            // Note: jacoco() DSL step removed - JaCoCo plugin not installed.
                            // Coverage is enforced via the 'Coverage Quality Gate' stage below.
                        }
                    }
                }
            }
        }
        stage('Coverage Quality Gate') {
            when {
                expression { changedServices?.trim() && changedServices != 'none' }
            }
            steps {
                script {
                    def services = (changedServices ?: '').split(',').findAll { it?.trim() }
                    services.each { svc ->
                        def reportPath = "${svc}/target/site/jacoco/jacoco.csv"

                        def coverage = sh(script: """
                            awk -F',' 'NR>1 {
                                missed  += \$4;
                                covered += \$5
                            } END {
                                if (missed+covered > 0)
                                    printf "%.0f", covered/(missed+covered)*100;
                                else
                                    print 0
                            }' ${reportPath}
                        """, returnStdout: true).trim().toInteger()

                        echo "[${svc}] Line Coverage: ${coverage}%"

                        if (coverage <= 70) {
                            error("[${svc}] Coverage ${coverage}% <= 70%. Pipeline failed!")
                        }
                    }
                }
            }
        }

        stage('Build Phase') {
            when {
                expression { changedServices?.trim() && changedServices != 'none' }
            }
            steps {
                script {
                    def services = (changedServices ?: '').split(',').findAll { it?.trim() }
                    services.each { svc ->
                        echo "Building: ${svc}"
                        dir("${svc}") {
                            sh 'chmod +x mvnw || true'
                            sh "./mvnw clean package -DskipTests -f ../pom.xml -pl ${svc} -am -U -Drevision=1.0-SNAPSHOT"
                        }
                    }
                }
            }
            post {
                success {
                    script {
                        def services = (changedServices ?: '').split(',').findAll { it?.trim() }
                        services.each { svc ->
                            archiveArtifacts artifacts: "${svc}/target/*.jar",
                                             allowEmptyArchive: true
                        }
                    }
                }
            }
        }
    }

    // ── POST toàn bộ pipeline ────────────────────────────────────────────
    post {
        success {
            echo "CI Pipeline PASSED – All stages completed successfully."
        }
        failure {
            echo "CI Pipeline FAILED – Check logs above for details."
        }
        always {
            // Dọn workspace sau mỗi lần chạy
            cleanWs()
        }
    }
}