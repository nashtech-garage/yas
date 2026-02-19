pipeline {
    agent any

    stages {
        stage('CI - Product Service') {
            when {
                changeset "product/**"
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
                changeset "customer/**"
            }
            stages {
                stage('Test Customer') {
                    steps {
                        dir('customer') {
                            sh 'chmod +x ./mvnw'
                            sh './mvnw clean test'
                        }
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
                        dir('customer') {
                            sh 'chmod +x ./mvnw'
                            sh './mvnw clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('CI - Storefront Frontend') {
            when {
                changeset "storefront/**"
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