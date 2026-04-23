pipeline {
    agent any

    options {
        timestamps()
    }

    tools {
        jdk 'jdk21'
        maven 'maven-3'
    }

    stages {
        stage('Detect Changes') {
            steps {
                script {
                    sh 'git fetch origin main --prune'
                    def baseCommit = sh(script: 'git merge-base HEAD origin/main', returnStdout: true).trim()
                    def changedFiles = sh(script: "git diff --name-only ${baseCommit} HEAD", returnStdout: true).trim()

                    echo "Files changed:\n${changedFiles}"

                    env.MEDIA_CHANGED = changedFiles.contains('media/') ? 'true' : 'false'
                    env.PRODUCT_CHANGED = changedFiles.contains('product/') ? 'true' : 'false'
                }
            }
        }

        stage('Test & Build Media Service') {
            when { expression { return env.MEDIA_CHANGED == 'true' } }
            steps {
                dir('media') {
                    echo 'Running tests for Media service...'
                    sh 'mvn clean test jacoco:report'
                    junit 'target/surefire-reports/*.xml'
                    jacoco execPattern: 'target/jacoco.exec',
                           classPattern: 'target/classes',
                           sourcePattern: 'src/main/java',
                           inclusionPattern: '**/*.class'
                    sh 'mvn -DskipTests package'
                }
            }
        }

        stage('Test & Build Product Service') {
            when { expression { return env.PRODUCT_CHANGED == 'true' } }
            steps {
                dir('product') {
                    echo 'Running tests for Product service...'
                    sh 'mvn clean test jacoco:report'
                    junit 'target/surefire-reports/*.xml'
                    jacoco execPattern: 'target/jacoco.exec',
                           classPattern: 'target/classes',
                           sourcePattern: 'src/main/java',
                           inclusionPattern: '**/*.class'
                    sh 'mvn -DskipTests package'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
    }
}