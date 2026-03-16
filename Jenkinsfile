pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'JDK21'
    }

    environment {
        SONAR_HOST_URL = 'http://192.168.23.135:9000'  // ← đổi thành IP SonarQube server của bạn
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // ─── GITLEAKS ───────────────────────────────────────────
        stage('Security Scan: Gitleaks') {
            steps {
                script {
                    sh 'git fetch origin main:refs/remotes/origin/main || true'

                    def scanRange = ''
                    if (env.BRANCH_NAME == 'main') {
                        scanRange = 'HEAD~1..HEAD'
                    } else {
                        def mainExists = sh(
                            script: 'git rev-parse --verify origin/main > /dev/null 2>&1',
                            returnStatus: true
                        )
                        scanRange = (mainExists == 0) ? 'origin/main..HEAD' : 'HEAD~1..HEAD'
                    }

                    echo "Scanning range: ${scanRange}"

                    def result = sh(
                        script: """
                            gitleaks detect \
                                --source=. \
                                --log-opts="${scanRange}" \
                                --report-format=json \
                                --report-path=gitleaks-report.json \
                                --exit-code=1 \
                                --redact
                        """,
                        returnStatus: true
                    )

                    if (result == 0) {
                        echo "✅ Gitleaks: No secrets found in range [${scanRange}]"
                    } else {
                        def report = readFile('gitleaks-report.json')
                        echo "❌ Gitleaks detected secrets!\n${report}"
                        error("Pipeline failed: secrets found in code!")
                    }
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'gitleaks-report.json',
                                     allowEmptyArchive: true
                }
            }
        }

        // ─── DETECT CHANGES ─────────────────────────────────────
        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = ''

                    try {
                        changedFiles = sh(
                            script: "git diff --name-only HEAD~1 HEAD",
                            returnStdout: true
                        ).trim()
                    } catch (Exception e) {
                        changedFiles = sh(
                            script: "git diff --name-only origin/main...HEAD",
                            returnStdout: true
                        ).trim()
                    }

                    if (changedFiles == '') {
                        echo "No changed files detected."
                    }

                    echo "Changed files:\n${changedFiles}"

                    env.CART_CHANGED      = changedFiles.contains('cart/')      ? 'true' : 'false'
                    env.CUSTOMER_CHANGED  = changedFiles.contains('customer/')  ? 'true' : 'false'
                    env.ORDER_CHANGED     = changedFiles.contains('order/')     ? 'true' : 'false'
                    env.PRODUCT_CHANGED   = changedFiles.contains('product/')   ? 'true' : 'false'
                    env.RATING_CHANGED    = changedFiles.contains('rating/')    ? 'true' : 'false'
                    env.INVENTORY_CHANGED = changedFiles.contains('inventory/') ? 'true' : 'false'
                    env.MEDIA_CHANGED     = changedFiles.contains('media/')     ? 'true' : 'false'
                    env.TAX_CHANGED       = changedFiles.contains('tax/')       ? 'true' : 'false'
                    env.LOCATION_CHANGED  = changedFiles.contains('location/')  ? 'true' : 'false'
                    env.PROMOTION_CHANGED = changedFiles.contains('promotion/') ? 'true' : 'false'

                    echo """
                    ┌─────────────────────────────────┐
                    │        SERVICES TO BUILD         │
                    ├─────────────────────────────────┤
                    │ cart:      ${env.CART_CHANGED}
                    │ customer:  ${env.CUSTOMER_CHANGED}
                    │ order:     ${env.ORDER_CHANGED}
                    │ product:   ${env.PRODUCT_CHANGED}
                    │ rating:    ${env.RATING_CHANGED}
                    │ inventory: ${env.INVENTORY_CHANGED}
                    │ media:     ${env.MEDIA_CHANGED}
                    │ tax:       ${env.TAX_CHANGED}
                    │ location:  ${env.LOCATION_CHANGED}
                    │ promotion: ${env.PROMOTION_CHANGED}
                    └─────────────────────────────────┘
                    """
                }
            }
        }

        // ─── CART ───────────────────────────────────────────────
        stage('Test & Build: cart') {
            when {
                environment name: 'CART_CHANGED', value: 'true'
            }
            stages {
                stage('cart - Unit Test + Coverage') {
                    steps {
                        sh 'mvn clean test jacoco:report -pl cart -am'
                    }
                    post {
                        always {
                            junit testResults: 'cart/target/surefire-reports/*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }
                stage('cart - Publish Coverage') {
                    steps {
                        recordCoverage(
                            tools: [[
                                $class: 'CoverageTool',
                                parser: 'JACOCO',
                                pattern: '**/cart/target/site/jacoco/jacoco.xml'
                            ]],
                            sourceDirectories: [[path: 'cart/src/main/java']]
                        )
                    }
                }
                stage('cart - SonarQube Analysis') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                mvn sonar:sonar -pl cart \
                                    -Dsonar.projectKey=yas-cart \
                                    -Dsonar.projectName="YAS - Cart" \
                                    -Dsonar.coverage.jacoco.xmlReportPaths=cart/target/site/jacoco/jacoco.xml
                            '''
                        }
                    }
                }
                stage('cart - Quality Gate') {
                    steps {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }
                stage('cart - Build') {
                    steps {
                        sh 'mvn package -pl cart -am -DskipTests'
                    }
                }
            }
        }

        // ─── CUSTOMER ───────────────────────────────────────────
        stage('Test & Build: customer') {
            when {
                environment name: 'CUSTOMER_CHANGED', value: 'true'
            }
            stages {
                stage('customer - Unit Test + Coverage') {
                    steps {
                        sh 'mvn clean test jacoco:report -pl customer -am'
                    }
                    post {
                        always {
                            junit testResults: 'customer/target/surefire-reports/*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }
                stage('customer - Publish Coverage') {
                    steps {
                        recordCoverage(
                            tools: [[
                                $class: 'CoverageTool',
                                parser: 'JACOCO',
                                pattern: '**/customer/target/site/jacoco/jacoco.xml'
                            ]],
                            sourceDirectories: [[path: 'customer/src/main/java']]
                        )
                    }
                }
                stage('customer - SonarQube Analysis') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                mvn sonar:sonar -pl customer \
                                    -Dsonar.projectKey=yas-customer \
                                    -Dsonar.projectName="YAS - Customer" \
                                    -Dsonar.coverage.jacoco.xmlReportPaths=customer/target/site/jacoco/jacoco.xml
                            '''
                        }
                    }
                }
                stage('customer - Quality Gate') {
                    steps {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }
                stage('customer - Build') {
                    steps {
                        sh 'mvn package -pl customer -am -DskipTests'
                    }
                }
            }
        }

        // ─── ORDER ──────────────────────────────────────────────
        stage('Test & Build: order') {
            when {
                environment name: 'ORDER_CHANGED', value: 'true'
            }
            stages {
                stage('order - Unit Test + Coverage') {
                    steps {
                        sh 'mvn clean test jacoco:report -pl order -am'
                    }
                    post {
                        always {
                            junit testResults: 'order/target/surefire-reports/*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }
                stage('order - Publish Coverage') {
                    steps {
                        recordCoverage(
                            tools: [[
                                $class: 'CoverageTool',
                                parser: 'JACOCO',
                                pattern: '**/order/target/site/jacoco/jacoco.xml'
                            ]],
                            sourceDirectories: [[path: 'order/src/main/java']]
                        )
                    }
                }
                stage('order - SonarQube Analysis') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                mvn sonar:sonar -pl order \
                                    -Dsonar.projectKey=yas-order \
                                    -Dsonar.projectName="YAS - Order" \
                                    -Dsonar.coverage.jacoco.xmlReportPaths=order/target/site/jacoco/jacoco.xml
                            '''
                        }
                    }
                }
                stage('order - Quality Gate') {
                    steps {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }
                stage('order - Build') {
                    steps {
                        sh 'mvn package -pl order -am -DskipTests'
                    }
                }
            }
        }

        // ─── PRODUCT ────────────────────────────────────────────
        stage('Test & Build: product') {
            when {
                environment name: 'PRODUCT_CHANGED', value: 'true'
            }
            stages {
                stage('product - Unit Test + Coverage') {
                    steps {
                        sh 'mvn clean test jacoco:report -pl product -am'
                    }
                    post {
                        always {
                            junit testResults: 'product/target/surefire-reports/*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }
                stage('product - Publish Coverage') {
                    steps {
                        recordCoverage(
                            tools: [[
                                $class: 'CoverageTool',
                                parser: 'JACOCO',
                                pattern: '**/product/target/site/jacoco/jacoco.xml'
                            ]],
                            sourceDirectories: [[path: 'product/src/main/java']]
                        )
                    }
                }
                stage('product - SonarQube Analysis') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                mvn sonar:sonar -pl product \
                                    -Dsonar.projectKey=yas-product \
                                    -Dsonar.projectName="YAS - Product" \
                                    -Dsonar.coverage.jacoco.xmlReportPaths=product/target/site/jacoco/jacoco.xml
                            '''
                        }
                    }
                }
                stage('product - Quality Gate') {
                    steps {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }
                stage('product - Build') {
                    steps {
                        sh 'mvn package -pl product -am -DskipTests'
                    }
                }
            }
        }

        // ─── RATING ─────────────────────────────────────────────
        stage('Test & Build: rating') {
            when {
                environment name: 'RATING_CHANGED', value: 'true'
            }
            stages {
                stage('rating - Unit Test + Coverage') {
                    steps {
                        sh 'mvn clean test jacoco:report -pl rating -am'
                    }
                    post {
                        always {
                            junit testResults: 'rating/target/surefire-reports/*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }
                stage('rating - Publish Coverage') {
                    steps {
                        recordCoverage(
                            tools: [[
                                $class: 'CoverageTool',
                                parser: 'JACOCO',
                                pattern: '**/rating/target/site/jacoco/jacoco.xml'
                            ]],
                            sourceDirectories: [[path: 'rating/src/main/java']]
                        )
                    }
                }
                stage('rating - SonarQube Analysis') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                mvn sonar:sonar -pl rating \
                                    -Dsonar.projectKey=yas-rating \
                                    -Dsonar.projectName="YAS - Rating" \
                                    -Dsonar.coverage.jacoco.xmlReportPaths=rating/target/site/jacoco/jacoco.xml
                            '''
                        }
                    }
                }
                stage('rating - Quality Gate') {
                    steps {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }
                stage('rating - Build') {
                    steps {
                        sh 'mvn package -pl rating -am -DskipTests'
                    }
                }
            }
        }

        // ─── INVENTORY ──────────────────────────────────────────
        stage('Test & Build: inventory') {
            when {
                environment name: 'INVENTORY_CHANGED', value: 'true'
            }
            stages {
                stage('inventory - Unit Test + Coverage') {
                    steps {
                        sh 'mvn clean test jacoco:report -pl inventory -am'
                    }
                    post {
                        always {
                            junit testResults: 'inventory/target/surefire-reports/*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }
                stage('inventory - Publish Coverage') {
                    steps {
                        recordCoverage(
                            tools: [[
                                $class: 'CoverageTool',
                                parser: 'JACOCO',
                                pattern: '**/inventory/target/site/jacoco/jacoco.xml'
                            ]],
                            sourceDirectories: [[path: 'inventory/src/main/java']]
                        )
                    }
                }
                stage('inventory - SonarQube Analysis') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                mvn sonar:sonar -pl inventory \
                                    -Dsonar.projectKey=yas-inventory \
                                    -Dsonar.projectName="YAS - Inventory" \
                                    -Dsonar.coverage.jacoco.xmlReportPaths=inventory/target/site/jacoco/jacoco.xml
                            '''
                        }
                    }
                }
                stage('inventory - Quality Gate') {
                    steps {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }
                stage('inventory - Build') {
                    steps {
                        sh 'mvn package -pl inventory -am -DskipTests'
                    }
                }
            }
        }

        // ─── MEDIA ──────────────────────────────────────────────
        stage('Test & Build: media') {
            when {
                environment name: 'MEDIA_CHANGED', value: 'true'
            }
            stages {
                stage('media - Unit Test + Coverage') {
                    steps {
                        sh 'mvn clean test jacoco:report -pl media -am'
                    }
                    post {
                        always {
                            junit testResults: 'media/target/surefire-reports/*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }
                stage('media - Publish Coverage') {
                    steps {
                        recordCoverage(
                            tools: [[
                                $class: 'CoverageTool',
                                parser: 'JACOCO',
                                pattern: '**/media/target/site/jacoco/jacoco.xml'
                            ]],
                            sourceDirectories: [[path: 'media/src/main/java']]
                        )
                    }
                }
                stage('media - SonarQube Analysis') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                mvn sonar:sonar -pl media \
                                    -Dsonar.projectKey=yas-media \
                                    -Dsonar.projectName="YAS - Media" \
                                    -Dsonar.coverage.jacoco.xmlReportPaths=media/target/site/jacoco/jacoco.xml
                            '''
                        }
                    }
                }
                stage('media - Quality Gate') {
                    steps {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }
                stage('media - Build') {
                    steps {
                        sh 'mvn package -pl media -am -DskipTests'
                    }
                }
            }
        }

        // ─── TAX ────────────────────────────────────────────────
        stage('Test & Build: tax') {
            when {
                environment name: 'TAX_CHANGED', value: 'true'
            }
            stages {
                stage('tax - Unit Test + Coverage') {
                    steps {
                        sh 'mvn clean test jacoco:report -pl tax -am'
                    }
                    post {
                        always {
                            junit testResults: 'tax/target/surefire-reports/*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }
                stage('tax - Publish Coverage') {
                    steps {
                        recordCoverage(
                            tools: [[
                                $class: 'CoverageTool',
                                parser: 'JACOCO',
                                pattern: '**/tax/target/site/jacoco/jacoco.xml'
                            ]],
                            sourceDirectories: [[path: 'tax/src/main/java']]
                        )
                    }
                }
                stage('tax - SonarQube Analysis') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                mvn sonar:sonar -pl tax \
                                    -Dsonar.projectKey=yas-tax \
                                    -Dsonar.projectName="YAS - Tax" \
                                    -Dsonar.coverage.jacoco.xmlReportPaths=tax/target/site/jacoco/jacoco.xml
                            '''
                        }
                    }
                }
                stage('tax - Quality Gate') {
                    steps {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }
                stage('tax - Build') {
                    steps {
                        sh 'mvn package -pl tax -am -DskipTests'
                    }
                }
            }
        }

        // ─── LOCATION ───────────────────────────────────────────
        stage('Test & Build: location') {
            when {
                environment name: 'LOCATION_CHANGED', value: 'true'
            }
            stages {
                stage('location - Unit Test + Coverage') {
                    steps {
                        sh 'mvn clean test jacoco:report -pl location -am'
                    }
                    post {
                        always {
                            junit testResults: 'location/target/surefire-reports/*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }
                stage('location - Publish Coverage') {
                    steps {
                        recordCoverage(
                            tools: [[
                                $class: 'CoverageTool',
                                parser: 'JACOCO',
                                pattern: '**/location/target/site/jacoco/jacoco.xml'
                            ]],
                            sourceDirectories: [[path: 'location/src/main/java']]
                        )
                    }
                }
                stage('location - SonarQube Analysis') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                mvn sonar:sonar -pl location \
                                    -Dsonar.projectKey=yas-location \
                                    -Dsonar.projectName="YAS - Location" \
                                    -Dsonar.coverage.jacoco.xmlReportPaths=location/target/site/jacoco/jacoco.xml
                            '''
                        }
                    }
                }
                stage('location - Quality Gate') {
                    steps {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }
                stage('location - Build') {
                    steps {
                        sh 'mvn package -pl location -am -DskipTests'
                    }
                }
            }
        }

        // ─── PROMOTION ──────────────────────────────────────────
        stage('Test & Build: promotion') {
            when {
                environment name: 'PROMOTION_CHANGED', value: 'true'
            }
            stages {
                stage('promotion - Unit Test + Coverage') {
                    steps {
                        sh 'mvn clean test jacoco:report -pl promotion -am'
                    }
                    post {
                        always {
                            junit testResults: 'promotion/target/surefire-reports/*.xml',
                                  allowEmptyResults: true
                        }
                    }
                }
                stage('promotion - Publish Coverage') {
                    steps {
                        recordCoverage(
                            tools: [[
                                $class: 'CoverageTool',
                                parser: 'JACOCO',
                                pattern: '**/promotion/target/site/jacoco/jacoco.xml'
                            ]],
                            sourceDirectories: [[path: 'promotion/src/main/java']]
                        )
                    }
                }
                stage('promotion - SonarQube Analysis') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                mvn sonar:sonar -pl promotion \
                                    -Dsonar.projectKey=yas-promotion \
                                    -Dsonar.projectName="YAS - Promotion" \
                                    -Dsonar.coverage.jacoco.xmlReportPaths=promotion/target/site/jacoco/jacoco.xml
                            '''
                        }
                    }
                }
                stage('promotion - Quality Gate') {
                    steps {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }
                stage('promotion - Build') {
                    steps {
                        sh 'mvn package -pl promotion -am -DskipTests'
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "✅ Pipeline completed — only affected services were built."
        }
        failure {
            echo "❌ Pipeline failed. Check logs above."
        }
    }
}
