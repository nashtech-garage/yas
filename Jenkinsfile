pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven-3'
    }

    stages {
        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = sh(script: "git diff --name-only origin/main", returnStdout: true).trim()
                    echo "Files changed: ${changedFiles}"

                    env.MEDIA_CHANGED = changedFiles.contains("services/media")
                    env.PRODUCT_CHANGED = changedFiles.contains("services/product")
                }
            }
        }

        stage('Test & Build Media Service') {
            when { expression { return env.MEDIA_CHANGED == 'true' } }
            steps {
                dir('services/media') {
                    echo 'Running tests for Media Service...'
                    sh 'mvn test'
                }
            }
        }

        stage('Test & Build Product Service') {
            when { expression { return env.PRODUCT_CHANGED == 'true' } }
            steps {
                dir('services/product') {
                    echo 'Running tests for Product Service...'
                    sh 'mvn test'
                }
            }
        }
    }
}