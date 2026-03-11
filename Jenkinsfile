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
