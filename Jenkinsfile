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

        // ─── CART ───────────────────────────────────────────────
        stage('Test & Build: cart') {
            when {
                changeset "cart/**"
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
                changeset "customer/**"
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
                changeset "order/**"
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
                changeset "product/**"
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
                changeset "rating/**"
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
                changeset "inventory/**"
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
                changeset "media/**"
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
                changeset "tax/**"
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

        // ─── LOCATION ────────────────────────────────────────────────
        stage('Test & Build: location') {
            when {
                changeset "location/**"
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
                changeset "promotion/**"
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
