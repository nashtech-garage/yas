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
                    def changedDirs = sh(
                        script: "git diff --name-only origin/main | cut -d/ -f1 | sort -u || true",
                        returnStdout: true
                    ).trim()

                    def services = []

                    if (changedDirs) {
                        def dirs = changedDirs.split("\n")
                        for (dir in dirs) {
                            if (env.SERVICES.split().contains(dir)) {
                                services.add(dir)
                            }
                        }
                    }

                    if (services.isEmpty()) {
                        echo "No service detected → build ALL"
                        services = env.SERVICES.split()
                    }

                    env.CHANGED_SERVICES = services.join(" ")
                    echo "Services to build: ${env.CHANGED_SERVICES}"
                }
            }
        }

        // ✅ FIX QUAN TRỌNG
        stage('Prepare Dependencies') {
            steps {
                echo "Building shared modules (common-library, etc)..."
                sh '''
                mvn clean install -DskipTests
                '''
            }
        }

        stage('Test') {
            steps {
                script {
                    for (svc in env.CHANGED_SERVICES.split()) {
                        echo "Testing ${svc}"
                        sh """
                        cd ${svc}
                        mvn test
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

                        if (!coverage) {
                            echo "⚠️ Cannot read coverage for ${svc}"
                            continue
                        }

                        coverage = coverage.toFloat() * 100

                        echo "Coverage ${svc}: ${coverage}%"

                        if (coverage < COVERAGE_THRESHOLD.toInteger()) {
                            error "❌ Coverage < 70% for ${svc}"
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
            echo "✅ PIPELINE SUCCESS"
        }
        failure {
            echo "❌ PIPELINE FAILED"
        }
    }
}
