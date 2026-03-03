pipeline {
    agent any
    
    tools {
        jdk 'JDK-21' 
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    stages {
        stage('Security: Gitleaks Scan') {
            steps {
                script {
                    echo "Running Gitleaks to detect hardcoded secrets..."
                    sh 'docker run --rm -v $(pwd):/path zricethezav/gitleaks:latest detect --source="/path" -v || true'
                }
            }
        }

        stage('Monorepo Services CI') {
            matrix {
                axes {
                    axis {
                        name 'SERVICE'
                        values 'media', 'product', 'cart', 'order', 'payment', 
                               'search', 'storefront', 'storefront-bff', 'backoffice', 
                               'backoffice-bff', 'customer', 'inventory', 'delivery', 
                               'identity', 'location', 'promotion', 'rating', 
                               'recommendation', 'sampledata', 'tax', 'webhook'
                    }
                }
                
                stages {
                    stage('Service CI') {
                        when { 
                            changeset "${SERVICE}/**" 
                        }
                        stages {
                            stage('Test & Coverage') {
                                steps {
                                    dir("${SERVICE}") {
                                        sh 'mvn clean test jacoco:report'
                                    }
                                }
                                post {
                                    always {
                                        dir("${SERVICE}") {
                                            junit 'target/surefire-reports/*.xml'

                                            jacoco(
                                                execPattern: 'target/jacoco.exec',
                                                classPattern: 'target/classes',
                                                sourcePattern: 'src/main/java',
                                                inclusionPattern: '**/*.class',
                                                minimumLineCoverage: '70', 
                                                changeBuildStatus: true
                                            )
                                        }
                                    }
                                }
                            }
                            
                            stage('Code Quality & SAST') {
                                steps {
                                    dir("${SERVICE}") {
                                        withSonarQubeEnv('SonarQube-Server') {
                                            sh 'mvn sonar:sonar'
                                        }
                                    }
                                }
                            }

                            stage('Build') {
                                steps {
                                    dir("${SERVICE}") {
                                        sh 'mvn package -DskipTests'
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}