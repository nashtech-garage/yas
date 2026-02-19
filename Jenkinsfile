pipeline {
    agent any

    stages {
        stage('CI - Product Service') {
            when {
                anyOf {
                    changeset "product/**"
                    changeset "Jenkinsfile"
                }
            }
            stages {
                stage('Test Product') {
                    steps {
                        sh 'mvn clean test -pl product -am'
                    }
                    post {
                        always {
                            junit 'product/target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('Build Product') {
                    steps {
                        sh 'mvn clean package -pl product -am -DskipTests'
                    }
                }
            }
        }

        stage('CI - Customer Service') {
            when {
                anyOf {
                    changeset "customer/**"
                    changeset "Jenkinsfile"
                }
            }
            stages {
                stage('Test Customer') {
                    steps {
                        sh 'mvn clean test -pl customer -am'
                    }
                    post {
                        always {
                            junit 'customer/target/surefire-reports/*.xml'
                            jacoco(
                                execPattern: 'customer/target/jacoco.exec',
                                classPattern: 'customer/target/classes',
                                sourcePattern: 'customer/src/main/java',
                                inclusionPattern: '**/*.class'
                            )
                        }
                    }
                }
                stage('Build Customer') {
                    steps {
                        sh 'mvn clean package -pl customer -am -DskipTests'
                    }
                }
            }
        }

        stage('CI - Storefront Frontend') {
            when {
                anyOf {
                    changeset "storefront/**"
                    changeset "Jenkinsfile"
                }
            }
            stages {
                stage('Test & Build Storefront') {
                    steps {
                        dir('storefront') {
                            sh 'npm ci'
                            sh 'npm run lint'
                            sh 'npx prettier --check .'
                            sh 'npm run build'
                        }
                    }
                }
            }
        }
    }
}