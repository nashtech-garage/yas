pipeline {
    agent any

    tools {
        jdk   'JDK-21'
        maven 'Maven-3.9'
    }

    options {
        disableConcurrentBuilds()
        timestamps()
        skipDefaultCheckout()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        MIN_COVERAGE = '70'
        // Testcontainers in DinD (Docker-in-Docker via socket mount)
        DOCKER_API_VERSION        = '1.44'
        TESTCONTAINERS_RYUK_DISABLED   = 'true'
        TESTCONTAINERS_HOST_OVERRIDE   = 'host.docker.internal'
        DOCKER_HOST               = 'unix:///var/run/docker.sock'
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
                    def allServices = [
                        'backoffice-bff', 'cart', 'customer', 'delivery',
                        'inventory', 'location', 'media', 'order',
                        'payment', 'payment-paypal', 'product', 'promotion',
                        'rating', 'recommendation', 'sampledata', 'search',
                        'storefront-bff', 'tax', 'webhook'
                    ]

                    def targetBranch = env.CHANGE_TARGET ?: 'main'
                    def changedFiles = ''

                    if (env.BRANCH_NAME == 'main') {
                        changedFiles = sh(
                            script: "git diff --name-only HEAD~1 2>/dev/null || git ls-tree -r --name-only HEAD",
                            returnStdout: true
                        ).trim()
                    } else {
                        sh "git fetch origin ${targetBranch}:refs/remotes/origin/${targetBranch} || true"
                        changedFiles = sh(
                            script: "git diff --name-only origin/${targetBranch}...HEAD",
                            returnStdout: true
                        ).trim()
                    }

                    echo "── Changed files ──\n${changedFiles}"

                    def fileList         = changedFiles ? changedFiles.split('\n').toList() : []
                    def commonLibChanged = fileList.any { it.startsWith('common-library/') }
                    def rootPomChanged   = fileList.any { it == 'pom.xml' }

                    def changedServices = allServices.findAll { svc ->
                        fileList.any { it.startsWith("${svc}/") }
                    }

                    if (commonLibChanged || rootPomChanged) {
                        changedServices = allServices
                        echo "Shared code changed (common-library or root pom.xml) → will build ALL services"
                    }

                    env.CHANGED_SERVICES = changedServices.join(',')
                    env.HAS_CHANGES      = changedServices.size() > 0 ? 'true' : 'false'

                    echo "Services to process: ${env.CHANGED_SERVICES ?: '(none — pipeline will skip build/test stages)'}"
                }
            }
        }

        stage('Gitleaks Scan') {
            steps {
                script {
                    def gitleaksCmd = ''
                    if (env.BRANCH_NAME == 'main') {
                        gitleaksCmd = "gitleaks detect --source . --config gitleaks.toml --log-opts='-1' --report-format json --report-path gitleaks-report.json --verbose"
                    } else {
                        def target = env.CHANGE_TARGET ?: 'main'
                        gitleaksCmd = "gitleaks detect --source . --config gitleaks.toml --log-opts='origin/${target}..HEAD' --report-format json --report-path gitleaks-report.json --verbose"
                    }

                    def exitCode = sh(script: gitleaksCmd, returnStatus: true)

                    if (exitCode == 1) {
                        error 'Gitleaks found hardcoded secrets — check gitleaks-report.json'
                    } else if (exitCode > 1) {
                        error "Gitleaks crashed with exit code ${exitCode} — check config or installation"
                    }
                    echo 'No secrets detected'
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'gitleaks-report.json', allowEmptyArchive: true
                }
            }
        }

        stage('Build') {
            when { expression { env.HAS_CHANGES == 'true' } }
            steps {
                script {
                    sh "mvn compile -pl ${env.CHANGED_SERVICES} -am"
                    echo "Compilation successful for: ${env.CHANGED_SERVICES}"
                }
            }
        }

        stage('Unit Test') {
            when { expression { env.HAS_CHANGES == 'true' } }
            steps {
                script {
                    sh "mvn verify -pl ${env.CHANGED_SERVICES} -am"
                }
            }
            post {
                always {
                    script {
                        // Only publish JUnit reports from changed services
                        def svcs = env.CHANGED_SERVICES.tokenize(',')
                        svcs.each { svc ->
                            junit testResults: "${svc}/target/surefire-reports/*.xml",  allowEmptyResults: true
                            junit testResults: "${svc}/target/failsafe-reports/*.xml", allowEmptyResults: true
                        }
                    }
                }
            }
        }

        stage('Coverage Gate') {
            when { expression { env.HAS_CHANGES == 'true' } }
            steps {
                script {
                    def services   = env.CHANGED_SERVICES.tokenize(',')
                    def failedList = []
                    def missingList = []

                    services.each { svc ->
                        def csv = "${svc}/target/site/jacoco/jacoco.csv"
                        if (fileExists(csv)) {
                            def pct = sh(
                                script: """
                                    awk -F',' 'NR>1 {m+=\$8; c+=\$9} END {
                                        t=m+c; if(t>0) printf "%.2f",(c/t)*100; else printf "100.00"
                                    }' ${csv}
                                """,
                                returnStdout: true
                            ).trim()

                            echo "${svc}: line coverage = ${pct}%"

                            if (pct.toFloat() < env.MIN_COVERAGE.toFloat()) {
                                failedList << "${svc} (${pct}%)"
                            }
                        } else {
                            missingList << svc
                        }
                    }

                    // Archive JaCoCo HTML reports for debugging
                    archiveArtifacts artifacts: '**/target/site/jacoco/**', allowEmptyArchive: true

                    if (missingList) {
                        echo "WARNING: No JaCoCo report for: ${missingList.join(', ')} — these services have no tests or JaCoCo is not configured"
                    }

                    if (failedList) {
                        error "Coverage below ${env.MIN_COVERAGE}%: ${failedList.join(', ')}"
                    }

                    echo "All changed services meet the ${env.MIN_COVERAGE}% coverage threshold"
                }
            }
        }

        stage('SonarQube Analysis') {
            when { expression { env.HAS_CHANGES == 'true' } }
            steps {
                withSonarQubeEnv('SonarCloud') {
                    script {
                        env.CHANGED_SERVICES.tokenize(',').each { svc ->
                            sh """
                                mvn sonar:sonar -pl ${svc} -am \
                                    -Dsonar.projectKey=com.yas:${svc} \
                                    -Dsonar.projectName=${svc} \
                                    -Dsonar.host.url=\${SONAR_HOST_URL} \
                                    -Dsonar.token=\${SONAR_AUTH_TOKEN}
                            """
                        }
                    }
                }
            }
        }

        stage('Quality Gate') {
            when { expression { env.HAS_CHANGES == 'true' } }
            steps {
                withSonarQubeEnv('SonarCloud') {
                    timeout(time: 5, unit: 'MINUTES') {
                        script {
                            def services = env.CHANGED_SERVICES.tokenize(',')
                            services.each { svc ->
                                def projectKey = "com.yas:${svc}"
                                def status = 'NONE'
                                def attempts = 0
                                // Poll SonarQube API until analysis is complete
                                while (attempts < 30) {
                                    attempts++
                                    status = sh(
                                        script: """
                                            curl -s -u \${SONAR_AUTH_TOKEN}: \
                                                "\${SONAR_HOST_URL}/api/qualitygates/project_status?projectKey=${projectKey}" \
                                            | grep -oP '"status"\\s*:\\s*"\\K[^"]+' | head -1
                                        """,
                                        returnStdout: true
                                    ).trim()

                                    if (status == 'OK' || status == 'ERROR' || status == 'WARN') {
                                        break
                                    }
                                    echo "${svc}: Quality Gate status = ${status ?: 'PENDING'}, retrying in 10s... (${attempts}/30)"
                                    sleep(10)
                                }
                                echo "${svc}: Quality Gate = ${status}"
                                if (status == 'ERROR') {
                                    error "${svc} failed the SonarQube Quality Gate"
                                }
                                if (!status || status == 'NONE') {
                                    echo "WARNING: ${svc} Quality Gate result not available after polling"
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Snyk Security Scan') {
            when { expression { env.HAS_CHANGES == 'true' } }
            steps {
                withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                    script {
                        sh 'snyk auth ${SNYK_TOKEN}'
                        def hasVulns = false

                        env.CHANGED_SERVICES.tokenize(',').each { svc ->
                            def rc = sh(
                                script: "snyk test --file=${svc}/pom.xml --severity-threshold=high",
                                returnStatus: true
                            )
                            if (rc != 0) {
                                echo "Snyk found high-severity issues in ${svc}"
                                hasVulns = true
                            }
                        }

                        if (hasVulns) {
                            unstable('Snyk detected high-severity vulnerabilities')
                        }
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
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed — check the stage logs for details.'
        }
    }
}
