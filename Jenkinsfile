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

        // ✅ CHỈ thêm wrapper ở đây
        stage('CI') {
            steps {
                withChecks('ci/jenkins') {

                    // ===== GIỮ NGUYÊN TOÀN BỘ LOGIC CỦA BẠN =====

                    checkout scm

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

                    echo "Building full project to resolve dependencies..."
                    sh "mvn clean install -DskipTests"

                    script {
                        for (svc in env.CHANGED_SERVICES.split()) {
                            echo "Testing ${svc}"
                            sh "mvn -pl ${svc} -am test"
                        }
                    }

                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

                    script {
                        for (svc in env.CHANGED_SERVICES.split()) {

                            def report = "${svc}/target/site/jacoco/jacoco.xml"

                            if (!fileExists(report)) continue

                            def coverage = sh(
                                script: """
                                grep -o 'line-rate="[^"]*"' ${report} | head -1 | cut -d'"' -f2
                                """,
                                returnStdout: true
                            ).trim()

                            if (!coverage) continue

                            coverage = coverage.toFloat() * 100

                            echo "Coverage ${svc}: ${coverage}%"

                            if (coverage < COVERAGE_THRESHOLD.toInteger()) {
                                error "❌ Coverage < 70% for ${svc}"
                            }
                        }
                    }

                    script {
                        for (svc in env.CHANGED_SERVICES.split()) {
                            echo "Building ${svc}"
                            sh "mvn -pl ${svc} -am package -DskipTests"
                        }
                    }

                    // ===== HẾT LOGIC CỦA BẠN =====
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
