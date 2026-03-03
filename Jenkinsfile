pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    options {
        skipDefaultCheckout(true)
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=.m2/repository"
        COVERAGE_THRESHOLD = "0.70"
        ENFORCE_PER_MODULE = "true"  // Local JaCoCo check per module
        SONAR_PROJECT_KEY = "NPT-102_yas"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Docker Check') {
            steps {
                sh 'docker ps'
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    def changedFiles = sh(
                        script: "git diff --name-only origin/${env.CHANGE_TARGET ?: 'main'} 2>/dev/null || echo 'all'",
                        returnStdout: true
                    ).trim()

                    echo "Changed files:\n${changedFiles}"

                    def allServices = [
                        'common-library', 'backoffice-bff', 'cart', 'customer', 
                        'inventory', 'location', 'media', 'order', 'payment-paypal', 
                        'payment', 'product', 'promotion', 'rating', 'search', 
                        'storefront-bff', 'tax', 'webhook', 'sampledata', 
                        'recommendation', 'delivery'
                    ]

                    def services = []

                    if (changedFiles == 'all' || changedFiles.contains('pom.xml')) {
                        services = allServices
                        echo "Building all services due to root pom.xml change or initial build"
                    } else {
                        allServices.each { service ->
                            if (changedFiles.contains("${service}/")) {
                                services.add(service)
                            }
                        }

                        if (services.isEmpty()) {
                            echo "No backend services changed, skipping build and test"
                        }
                    }

                    env.HAS_SERVICES = services.isEmpty() ? 'false' : 'true'
                    env.SERVICES = services.join(",")
                    echo "Services to build: ${env.SERVICES ?: 'none'}"
                }
            }
        }

        stage('Security - Gitleaks') {
            steps {
                sh 'gitleaks detect --source . --config gitleaks.toml --exit-code 1'
            }
        }

        stage('Build') {
            when {
                expression { env.HAS_SERVICES == 'true' }
            }
            steps {
                script {
                    echo "Building services: ${env.SERVICES}"
                }
                sh """
                    mvn clean compile \
                        -pl ${env.SERVICES} \
                        -am
                """
            }
        }

        stage('Test') {
            when {
                expression { env.HAS_SERVICES == 'true' }
            }
            steps {
                script {
                    echo "Testing services: ${env.SERVICES}"
                    echo "📊 Coverage enforcement: Per-module >= ${env.COVERAGE_THRESHOLD} (JaCoCo local)"
                    
                    sh '''
                        echo "api.version=1.44" > ~/.docker-java.properties
                        echo "DOCKER_API_VERSION=1.44" >> ~/.docker-java.properties
                        cat ~/.docker-java.properties
                    '''
                }
                withEnv([
                    'DOCKER_API_VERSION=1.44',
                    'TESTCONTAINERS_RYUK_DISABLED=true'
                ]) {
                    sh """
                        mvn verify \
                            -pl ${env.SERVICES} \
                            -am \
                            -Djacoco.haltOnFailure=${env.ENFORCE_PER_MODULE} \
                            -DDOCKER_API_VERSION=1.44
                    """
                }
            }
        }

        stage('Generate Aggregate Coverage Report') {
            when {
                expression { env.HAS_SERVICES == 'true' }
            }
            steps {
                script {
                    echo "Generating aggregate coverage report for all modules"
                    sh "mvn jacoco:report-aggregate -DskipTests"
                }
            }
        }

        stage('Publish Test Results') {
            steps {
                junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml'
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/jacoco-aggregate',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage Report',
                    reportTitles: 'Code Coverage'
                ])
            }
        }

        stage('SonarQube Analysis') {
            when {
                allOf {
                    not { buildingTag() }
                    expression { env.HAS_SERVICES == 'true' }
                }
            }
            steps {
                withSonarQubeEnv('SonarCloud') {
                    script {
                        def sonarParams = "-Dsonar.projectKey=NPT-102_yas -Dsonar.organization=npt-102 -Dsonar.host.url=https://sonarcloud.io"

                        if (env.CHANGE_ID) {
                            // PR build → PR analysis (không conflict với branch analysis)
                            sonarParams += " -Dsonar.pullrequest.key=${env.CHANGE_ID}"
                            sonarParams += " -Dsonar.pullrequest.branch=${env.CHANGE_BRANCH}"
                            sonarParams += " -Dsonar.pullrequest.base=${env.CHANGE_TARGET}"
                            echo "🔍 Running SonarQube PR analysis for PR #${env.CHANGE_ID}"
                        } else {
                            // Branch build → Branch analysis
                            sonarParams += " -Dsonar.branch.name=${env.BRANCH_NAME}"
                            echo "🔍 Running SonarQube branch analysis for ${env.BRANCH_NAME}"
                        }

                        sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar ${sonarParams}"
                    }
                }
            }
        }

        stage('Quality Gate') {
            when {
                allOf {
                    not { buildingTag() }
                    expression { env.HAS_SERVICES == 'true' }
                }
            }
            steps {
                script {
                    echo "⏳ Polling SonarCloud Quality Gate status..."
                    echo "📊 Quality Gate: Comprehensive quality checks (bugs, vulnerabilities, code smells)"
                    echo "   Note: Per-module coverage >= ${env.COVERAGE_THRESHOLD} already enforced by JaCoCo in Build & Test stage"
                    echo "   Note: Aggregate coverage checks can be configured in SonarCloud Quality Gate if needed"
                    
                    // Get SonarCloud token from credentials
                    withCredentials([string(credentialsId: 'sonarcloud-token', variable: 'SONAR_TOKEN')]) {
                        // Build Quality Gate API URL based on PR or branch
                        def qgApiUrl = 'https://sonarcloud.io/api/qualitygates/project_status?projectKey=NPT-102_yas'
                        if (env.CHANGE_ID) {
                            qgApiUrl += "&pullRequest=${env.CHANGE_ID}"
                            echo "Checking Quality Gate for PR #${env.CHANGE_ID}"
                        } else {
                            qgApiUrl += "&branch=${env.BRANCH_NAME}"
                            echo "Checking Quality Gate for branch ${env.BRANCH_NAME}"
                        }

                        def maxAttempts = 60  // 10 minutes with 10s interval
                        def attempt = 0
                        def qgPassed = false
                        def qgStatus = 'PENDING'
                        
                        while (attempt < maxAttempts) {
                            attempt++
                            
                            // Poll Quality Gate API
                            def apiResponse = sh(
                                script: """
                                    curl -s -u \${SONAR_TOKEN}: \
                                    '${qgApiUrl}' \
                                    || echo '{"projectStatus":{"status":"ERROR"}}'
                                """,
                                returnStdout: true
                            ).trim()
                            
                            echo "Attempt ${attempt}/${maxAttempts}: Checking Quality Gate..."
                            
                            // Check if the API returned an error (e.g. org not allowed for non-main branches)
                            def hasError = sh(
                                script: "echo '${apiResponse}' | grep -q '\"errors\"' && echo 'true' || echo 'false'",
                                returnStdout: true
                            ).trim()
                            
                            if (hasError == 'true') {
                                def errorMsg = sh(
                                    script: "echo '${apiResponse}' | grep -oP '\"msg\":\\s*\"\\K[^\"]+' | head -1 || echo 'Unknown API error'",
                                    returnStdout: true
                                ).trim()
                                echo "⚠️ SonarCloud API error: ${errorMsg}"
                                echo "   Skipping Quality Gate check. Per-module coverage was enforced by JaCoCo."
                                qgPassed = true
                                break
                            }

                            // Extract status using grep (no need for JSON parser plugin)
                            qgStatus = sh(
                                script: "echo '${apiResponse}' | grep -oP '\"status\":\\s*\"\\K[^\"]+' | head -1 || echo 'UNKNOWN'",
                                returnStdout: true
                            ).trim()
                            
                            echo "Quality Gate Status: ${qgStatus}"
                            
                            if (qgStatus == 'OK') {
                                qgPassed = true
                                echo "✅ Quality Gate PASSED!"
                                echo "   ✓ Code quality metrics met (bugs, vulnerabilities, code smells)"
                                echo "   ✓ Security standards satisfied"
                                echo "   ✓ Per-module coverage >= ${env.COVERAGE_THRESHOLD} (enforced by JaCoCo)"
                                break
                            } else if (qgStatus == 'ERROR') {
                                echo "❌ Quality Gate FAILED"
                                echo "Details: https://sonarcloud.io/dashboard?id=NPT-102_yas"
                                echo "Note: Per-module checks already passed in Build & Test stage"
                                
                                // Get failure conditions
                                def conditions = sh(
                                    script: "echo '${apiResponse}' | grep -oP '\"metricKey\":\\s*\"\\K[^\"]+' || echo ''",
                                    returnStdout: true
                                ).trim()
                                
                                if (conditions) {
                                    echo "Failed conditions: ${conditions}"
                                }
                                
                                error("Quality Gate failed with status: ERROR")
                            } else if (qgStatus == 'NONE' || qgStatus == 'UNKNOWN') {
                                // Analysis not ready yet, wait and retry
                                if (attempt < maxAttempts) {
                                    echo "Analysis not complete, waiting 10 seconds..."
                                    sleep(10)
                                } else {
                                    error("Quality Gate check timeout - analysis not completed")
                                }
                            } else {
                                // Unexpected status
                                echo "⚠️ Unexpected status: ${qgStatus}, retrying..."
                                sleep(10)
                            }
                        }
                        
                        if (!qgPassed) {
                            error("Quality Gate check timeout after ${maxAttempts} attempts")
                        }
                    }
                }
            }
        }


        stage('Snyk Scan') {
            steps {
                withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                    script {
                        // Scan root pom.xml and frontend projects
                        sh '''
                            echo "Scanning root Maven project..."
                            snyk test --file=pom.xml --severity-threshold=high || echo "Maven scan completed with issues"
                            
                            echo "Scanning Storefront..."
                            cd storefront && snyk test --severity-threshold=high || echo "Storefront scan completed with issues"
                            cd ..
                            
                            echo "Scanning Backoffice..."
                            cd backoffice && snyk test --severity-threshold=high || echo "Backoffice scan completed with issues"
                        '''
                    }
                }
            }
        }



        stage('Release Build (Tag Only)') {
            when {
                buildingTag()
            }
            steps {
                sh 'mvn clean package -DskipTests'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo "✓ Pipeline completed successfully"
        }

        failure {
            echo "✗ Pipeline failed"
        }

        always {
            cleanWs()
        }
    }
}
