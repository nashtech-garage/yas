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

        stage('Init') {
            steps {
                publishChecks(
                    name: 'jenkins/ci',
                    title: 'CI Pipeline',
                    summary: 'Pipeline started...',
                    status: 'IN_PROGRESS',
                    conclusion: 'NONE'
                )
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // ✅ Detect changed services (PR-safe)
        stage('Detect Changed Services') {
            steps {
                script {
                    sh "git fetch origin main"

                    def changedDirs = sh(
                        script: "git diff --name-only origin/main...HEAD | cut -d/ -f1 | sort -u || true",
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

        stage('Prepare Dependencies') {
            steps {
                echo "Building full project to resolve dependencies..."
                sh "mvn clean install -DskipTests"
            }
        }

        stage('Test') {
            steps {
                script {
                    for (svc in env.CHANGED_SERVICES.split()) {
                        echo "Testing ${svc}"
                        sh "mvn -pl ${svc} -am test"
                    }
                }
            }
            post {
                always {
                    // ✅ Upload test results
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

                    // ✅ Upload coverage HTML per service
                    script {
                        for (svc in env.CHANGED_SERVICES.split()) {

                            def reportPath = "${svc}/target/site/jacoco"

                            if (fileExists("${reportPath}/index.html")) {
                                publishHTML(target: [
                                    reportDir: reportPath,
                                    reportFiles: 'index.html',
                                    reportName: "Coverage - ${svc}",
                                    allowMissing: true,
                                    alwaysLinkToLastBuild: true,
                                    keepAll: true
                                ])
                            } else {
                                echo "⚠️ No HTML report for ${svc}"
                            }
                        }
                    }
                }
            }
        }

        // ✅ Coverage check (JaCoCo chuẩn)
        stage('Coverage Check') {
            steps {
                script {
                    for (svc in env.CHANGED_SERVICES.split()) {

                        def report = "${svc}/target/site/jacoco/jacoco.xml"

                        if (!fileExists(report)) {
                            error "❌ No coverage report for ${svc}"
                        }

                        def content = readFile(report)

                        def matcher = content =~ /<counter type="LINE" missed="(\d+)" covered="(\d+)"/

                        if (!matcher) {
                            error "❌ Cannot parse coverage for ${svc}"
                        }

                        def missed = matcher[0][1].toInteger()
                        def covered = matcher[0][2].toInteger()

                        def coverage = (covered * 100) / (covered + missed)

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
                        sh "mvn -pl ${svc} -am package -DskipTests"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ PIPELINE SUCCESS"
            publishChecks(
                name: 'jenkins/ci',
                title: 'CI Pipeline',
                summary: 'All stages passed ✅',
                status: 'COMPLETED',
                conclusion: 'SUCCESS'
            )
        }
        failure {
            echo "❌ PIPELINE FAILED"
            publishChecks(
                name: 'jenkins/ci',
                title: 'CI Pipeline',
                summary: 'Pipeline failed ❌',
                status: 'COMPLETED',
                conclusion: 'FAILURE'
            )
        }
    }
}
