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
                    // Lấy danh sách folder bị thay đổi
                    def changedDirs = sh(
                        script: "git diff --name-only origin/main | cut -d/ -f1 | sort -u || true",
                        returnStdout: true
                    ).trim().split("\n")

                    echo "Changed dirs: ${changedDirs}"

                    def services = []

                    for (dir in changedDirs) {
                        if (["media", "product", "cart"].contains(dir)) {
                            services.add(dir)
                        }
                    }

                    // Nếu không detect được → build all
                    if (services.isEmpty()) {
                        echo "No service detected → build ALL"
                        services = ["media", "product", "cart"]
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
                        sh """
                        cd ${svc}
                        mvn clean test
                        """
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
                        def report = "${svc}/target/site/jacoco/jacoco.xml"

                        if (!fileExists(report)) {
                            error "Missing coverage report: ${report}"
                        }

                        def coverage = sh(
                            script: """
                            grep -o 'line-rate="[^"]*"' ${report} | head -1 | cut -d'"' -f2
                            """,
                            returnStdout: true
                        ).trim()

                        coverage = coverage.toFloat() * 100

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
                        sh """
                        cd ${svc}
                        mvn clean package -DskipTests
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "PIPELINE SUCCESS"
        }
        failure {
            echo "PIPELINE FAILED"
        }
    }
}
