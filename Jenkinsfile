pipeline {
    agent { label 'individual-agent' }

    stages {
        stage('Pre-check') {
            steps {
                script {
                    echo "Checking environment..."
                    sh 'java -version'
                    sh 'mvn -version'
                    sh 'gitleaks version'
                }
            }
        }
        stage('Secret Scanning') {
            steps {
                sh '''
                    gitleaks detect --source . \
                        --config gitleaks.toml \
                        --report-format json \
                        --report-path gitleaks-report.json \
                        --exit-code 1
                '''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'gitleaks-report.json',
                                     allowEmptyArchive: true
                }
            }
        }

        stage('Monorepo Execution') {
            steps {
                script {
                    def changedFiles = sh(script: 'git diff --name-only HEAD~1 HEAD', returnStdout: true).trim().split('\n')
                    def services = [
                        'media', 'product', 'order', 'inventory', 'payment', 'promotion', 
                        'rating', 'delivery', 'sampledata', 'recommendation', // TV2 (10 modules)
                        'customer', 'location', 'cart', 'tax', 'search', 'webhook', // TV4 (6 modules)
                        'common-library', 'backoffice-bff', 'storefront-bff', 'payment-paypal' // Other modules
                    ]

                    for (service in services) {
                        if (changedFiles.any { it.startsWith("${service}/") }) {
                            echo "Processing service: ${service}"
                            sh "mvn test -pl ${service} -am"
                            junit testResults: "${service}/target/surefire-reports/*.xml", allowEmptyResults: true
                            sh "mvn package -DskipTests -pl ${service} -am"
                        } else {
                            echo "${service} no change, skip."
                        }
                    }
                }
            }
        }

        stage('Coverage Report') {
            steps {
                script {
                    def changedFiles = sh(script: 'git diff --name-only HEAD~1 HEAD', returnStdout: true).trim().split('\n')
                    def services = [
                        'media', 'product', 'order', 'inventory', 'payment', 'promotion', 
                        'rating', 'delivery', 'sampledata', 'recommendation', // TV2 (10 modules)
                        'customer', 'location', 'cart', 'tax', 'search', 'webhook', // TV4 (6 modules)
                        'common-library', 'backoffice-bff', 'storefront-bff', 'payment-paypal' // Other modules
                    ]

                    for (service in services) {
                        if (changedFiles.any { it.startsWith("${service}/") }) {
                            echo "Publishing JaCoCo report for: ${service}"
                            jacoco(
                                execPattern: "${service}/target/jacoco.exec",
                                classPattern: "${service}/target/classes",
                                sourcePattern: "${service}/src/main/java",
                                exclusionPattern: '**/config/**,**/exception/**,**/constants/**,**/*Application.class',
                                minimumInstructionCoverage: '70',
                                minimumBranchCoverage: '0',
                                changeBuildStatus: true
                            )
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline Succeeded"
        }
        failure {
            echo "❌ Pipeline Failed — Coverage may be below 70% or build error occurred."
        }
        always {
            cleanWs()
        }
    }
}