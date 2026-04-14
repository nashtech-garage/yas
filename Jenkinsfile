pipeline {
    agent any

    environment {
        MVN_CMD = "mvn -B"
        COVERAGE_THRESHOLD = 70
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    def changedFiles = sh(
                        script: "git diff --name-only origin/main || true",
                        returnStdout: true
                    ).trim()

                    echo "Changed files: ${changedFiles}"

                    def services = []

                    if (changedFiles.contains("media-service")) {
                        services.add("media-service")
                    }
                    if (changedFiles.contains("product-service")) {
                        services.add("product-service")
                    }
                    if (changedFiles.contains("cart-service")) {
                        services.add("cart-service")
                    }

                    // Nếu không detect được (ví dụ commit đầu)
                    if (services.isEmpty()) {
                        echo "No specific service detected → build all"
                        services = ["media-service", "product-service", "cart-service"]
                    }

                    env.CHANGED_SERVICES = services.join(" ")
                    echo "Services to build: ${env.CHANGED_SERVICES}"
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    for (svc in env.CHANGED_SERVICES.split()) {
                        echo "Testing ${svc}"
                        sh "cd ${svc} && ${MVN_CMD} test"
                    }
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Coverage Check') {
            steps {
                script {
                    for (svc in env.CHANGED_SERVICES.split()) {
                        echo "Checking coverage for ${svc}"

                        def reportPath = "${svc}/target/site/jacoco/jacoco.xml"

                        if (!fileExists(reportPath)) {
                            error "Coverage report not found for ${svc}"
                        }

                        def coverage = sh(
                            script: """
                            grep -o 'line-rate="[^"]*"' ${reportPath} | head -1 | cut -d'"' -f2
                            """,
                            returnStdout: true
                        ).trim()

                        coverage = (coverage.toFloat() * 100)

                        echo "Coverage ${svc}: ${coverage}%"

                        if (coverage < COVERAGE_THRESHOLD.toInteger()) {
                            error "Coverage < 70% for ${svc}"
                        }
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    for (svc in env.CHANGED_SERVICES.split()) {
                        echo "Building ${svc}"
                        sh "cd ${svc} && ${MVN_CMD} clean package -DskipTests"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline SUCCESS"
        }
        failure {
            echo "Pipeline FAILED"
        }
    }
}
