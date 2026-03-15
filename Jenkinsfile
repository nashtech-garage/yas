pipeline {
    agent any

    environment {
        SONAR_PROJECT_KEY = "yas-project"
        SONAR_HOST_URL = "http://localhost:9000"
        SONAR_TOKEN = credentials('sonar-token')
        SNYK_TOKEN = credentials('snyk-token')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Secret Scan (Gitleaks)') {
            steps {
                sh '''
                docker run --rm -v $WORKSPACE:/repo \
                zricethezav/gitleaks detect \
                --source=/repo \
                --exit-code 1
                '''
            }
        }

        stage('Install Dependencies') {
            steps {
                sh 'npm install'
            }
        }

        stage('Dependency Security Scan (Snyk)') {
            steps {
                sh '''
                snyk auth $SNYK_TOKEN
                snyk test
                '''
            }
        }

        stage('Run Tests with Coverage') {
            steps {
                sh 'npm test -- --coverage'
            }
        }

        stage('Coverage Check') {
            steps {
                script {
                    def coverage = sh(
                        script: "jq '.total.lines.pct' coverage/coverage-summary.json",
                        returnStdout: true
                    ).trim()

                    echo "Coverage: ${coverage}%"

                    if (coverage.toFloat() < 70) {
                        error("Test coverage ${coverage}% is below required 70%")
                    }
                }
            }
        }

        stage('SonarQube Scan') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh """
                    sonar-scanner \
                    -Dsonar.projectKey=$SONAR_PROJECT_KEY \
                    -Dsonar.sources=. \
                    -Dsonar.host.url=$SONAR_HOST_URL \
                    -Dsonar.login=$SONAR_TOKEN \
                    -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

    }

    post {

        success {
            echo "Pipeline passed successfully"
        }

        failure {
            echo "Pipeline failed due to security issues, low coverage, or quality gate failure"
        }

    }
}