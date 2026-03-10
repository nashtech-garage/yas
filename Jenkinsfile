pipeline {
    agent any

    options {
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        timestamps()
    }

    tools {
        jdk   'JDK-21'
        maven 'Maven-3.9'
    }

    environment {
        SONAR_TOKEN = credentials('sonarcloud-token') 
        SNYK_TOKEN  = credentials('snyk-token')  
        TARGET_SERVICE = ""                     
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Detect Service') {
            steps {
                script {
                    echo "Detecting which microservice this branch is modifying..."

                    def baseBranch = env.CHANGE_TARGET ? "origin/${env.CHANGE_TARGET}" : "origin/main"
                    sh "git fetch --no-tags origin"

                    def changedFiles = sh(
                        script: "git diff --name-only ${baseBranch}...HEAD",
                        returnStdout: true
                    ).trim()

                    if (!changedFiles) {
                        error "No changes detected compared to ${baseBranch}. Pipeline stopping."
                    }

                    echo "Changed files across branch:\n${changedFiles}"

                    def serviceSet = [] as Set
                    for (file in changedFiles.tokenize('\n')) {
                        def parts = file.split('/')
                        if (parts.size() > 1) {
                            def folder = parts[0]
                            if (fileExists("${folder}/pom.xml")) {
                                serviceSet.add(folder)
                            }
                        }
                    }

                    if (serviceSet.size() == 0) {
                        error "No service changes detected (no modified folders with pom.xml found). Pipeline stopping."
                    } else if (serviceSet.size() > 1) {
                        echo "WARNING: Changes detected in multiple services: ${serviceSet}. Using the first one."
                        env.TARGET_SERVICE = serviceSet[0]
                    } else {
                        env.TARGET_SERVICE = serviceSet[0]
                    }

                    echo "--------------------------------------------------------"
                    echo " TARGET SERVICE DETECTED: [ ${env.TARGET_SERVICE} ]"
                    echo "--------------------------------------------------------"
                }
            }
        }

        stage('Gitleaks Scan') {
            steps {
                echo "Running Gitleaks to scan for hardcoded secrets/passwords..."
                sh 'gitleaks detect --source . -v'
            }
        }

        stage('Unit Test') {
            steps {
                echo "Running Unit Tests for ${env.TARGET_SERVICE} via Testcontainers..."
                dir("${env.TARGET_SERVICE}") {
                    sh 'mvn clean test org.jacoco:jacoco-maven-plugin:prepare-agent install'
                }
            }
            post {
                always {
                    junit testResults: "${env.TARGET_SERVICE}/target/surefire-reports/*.xml",
                          allowEmptyResults: true
                }
            }
        }

        stage('Coverage Check') {
            steps {
                echo "Generating JaCoCo coverage report and enforcing >= 70% line coverage..."
                dir("${env.TARGET_SERVICE}") {
                    sh 'mvn org.jacoco:jacoco-maven-plugin:report'

                    jacoco(
                        execPattern: 'target/jacoco.exec',
                        classPattern: 'target/classes',
                        sourcePattern: 'src/main/java',
                        inclusionPattern: '**/*.class',
                        lineCoverageTargets: '70, 100, 0', 
                        changeBuildStatus: true
                    )
                }
            }
        }

        stage('Build Service') {
            steps {
                echo "Packaging ${env.TARGET_SERVICE} into a JAR..."
                dir("${env.TARGET_SERVICE}") {
                    sh 'mvn package -DskipTests'
                }
            }
        }

        stage('Security & Quality') {
            parallel {
                stage('SonarQube Code Quality') {
                    steps {
                        dir("${env.TARGET_SERVICE}") {
                            withSonarQubeEnv('SonarQube-Server') { 
                                sh """
                                    mvn sonar:sonar \\
                                      -Dsonar.projectKey=yas-${env.TARGET_SERVICE} \\
                                      -Dsonar.host.url=$SONAR_HOST_URL \\
                                      -Dsonar.login=$SONAR_TOKEN
                                """
                            }
                        }
                    }
                }

                stage('Snyk Vulnerability Scan') {
                    steps {
                        dir("${env.TARGET_SERVICE}") {
                            withEnv(["SNYK_TOKEN=${env.SNYK_TOKEN}"]) {
                                sh "snyk test --fail-on=all --severity-threshold=high"
                            }
                        }
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo "Waiting for SonarQube Quality Gate result..."
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline finished. Cleaning up workspace..."
            cleanWs()
        }
        success {
            echo "✅ SUCCESS: The ${env.TARGET_SERVICE} pipeline passed all quality and security gates!"
        }
        failure {
            echo "❌ FAILED: The ${env.TARGET_SERVICE} pipeline failed. Please check the logs."
        }
    }
}
