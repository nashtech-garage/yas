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