pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'JDK21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // ─── DETECT CHANGES ─────────────────────────────────────
        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = ''

                    try {
                        // Trường hợp branch cũ: so sánh với commit trước đó
                        changedFiles = sh(
                            script: "git diff --name-only HEAD~1 HEAD",
                            returnStdout: true
                        ).trim()
                    } catch (Exception e) {
                        // Trường hợp branch mới từ local: fallback so sánh với main
                        changedFiles = sh(
                            script: "git diff --name-only origin/main...HEAD",
                            returnStdout: true
                        ).trim()
                    }

                    // Nếu vẫn rỗng (ví dụ tạo branch trên GitHub UI lần đầu)
                    // thì coi như không có gì thay đổi
                    if (changedFiles == '') {
                        echo "No changed files detected."
                    }

                    echo "Changed files:\n${changedFiles}"

                    // Set env variable cho từng service
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
            echo "Pipeline completed — only affected services were built."
        }
        failure {
            echo "Pipeline failed. Check logs above."
        }
    }
}
