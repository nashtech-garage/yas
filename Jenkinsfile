pipeline {
    agent {
        docker {
            image 'maven:3.9.9-eclipse-temurin-21'
            args '-v /root/.m2:/root/.m2'
        }
    }

    environment {
        COVERAGE_THRESHOLD = 70
        SERVICES = "media product cart"
        GITHUB_CONTEXT = "jenkins-ci"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // ✅ GỬI STATUS START LÊN GITHUB (Checks API)
        stage('Notify GitHub START') {
            steps {
                publishChecks name: "${env.GITHUB_CONTEXT}", status: 'IN_PROGRESS'
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    // 🔧 Fix: đảm bảo có origin/main
                    sh "git fetch origin main || true"

                    def changedDirs = sh(
                        script: "git diff --name-only origin/main...HEAD | cut -d/ -f1 | sort -u || true",
                        returnStdout: true
                    ).trim()

                    def services = []

                    if (changedDirs) {
                        def dirs = changedDirs.split("\n")
                        for (dir in dirs) {
                            if (env.SERVICES.tokenize(' ').contains(dir)) {
                                services.add(dir)
                            }
                        }
                    }

                    if (services.isEmpty()) {
                        echo "No service detected → build ALL"
                        services = env.SERVICES.tokenize(' ')
                    }

                    env.CHANGED_SERVICES = services.join(" ")
                    echo "Services to build: ${env.CHANGED_SERVICES}"
                }
            }
        }

        stage('Prepare Dependencies') {
            steps {
                echo "Building full project to resolve dependencies..."
                sh """
                mvn clean install -DskipTests
                """
            }
        }

        stage('Test') {
            steps {
                script {
                    for (svc in env.CHANGED_SERVICES.split()) {
                        echo "Testing ${svc}"
                        sh """
                        mvn -pl ${svc} -am test
                        """
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Coverage Check') {
            steps {
                script {
                    for (svc in env.CHANGED_SERVICES.split()) {

                        def report = "${svc}/target/site/jacoco/jacoco.xml"

                        if (!fileExists(report)) {
                            echo "⚠️ No coverage report for ${svc} → skip"
                            continue
                        }

                        def coverage = sh(
                            script: """
                            grep -o 'line-rate="[^"]*"' ${report} | head -1 | cut -d'"' -f2
                            """,
                            returnStdout: true
                        ).trim()

                        if (!coverage || !coverage.isFloat()) {
                            echo "⚠️ Cannot read coverage for ${svc}"
                            continue
                        }

                        coverage = coverage.toFloat() * 100

                        echo "Coverage ${svc}: ${coverage}%"

                        if (coverage < COVERAGE_THRESHOLD.toInteger()) {
                            error "❌ Coverage < ${COVERAGE_THRESHOLD}% for ${svc}"
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
                        mvn -pl ${svc} -am package -DskipTests
                        """
                    }
                }
            }
        }
    }

    // ✅ GỬI STATUS VỀ GITHUB
    post {
        success {
            echo "✅ PIPELINE SUCCESS"
            publishChecks name: "${env.GITHUB_CONTEXT}", status: 'COMPLETED', conclusion: 'SUCCESS'
        }
        failure {
            echo "❌ PIPELINE FAILED"
            publishChecks name: "${env.GITHUB_CONTEXT}", status: 'COMPLETED', conclusion: 'FAILURE'
        }
    }
}
