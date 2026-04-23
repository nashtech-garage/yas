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

                    // Fallback logic: if origin/main doesn't exit, compare with HEAD~1
                    def baseCommit = sh(
                        script: '''
                            if git rev-parse --verify origin/main >/dev/null 2>&1; then
                                git merge-base HEAD origin/main
                            elif git rev-parse --verify HEAD~1 >/dev/null 2>&1; then
                                echo "origin/main not found, comparing with HEAD~1"
                                git rev-parse HEAD~1
                            else
                                git rev-parse HEAD
                            fi
                        ''',
                        returnStdout: true
                    ).trim()

                    def changedFiles = sh(script: "git diff --name-only ${baseCommit} HEAD", returnStdout: true).trim()

                    echo "Base commit: ${baseCommit}"
                    echo "Files changed:\n${changedFiles}"

                    env.MEDIA_CHANGED = changedFiles.contains('media/') ? 'true' : 'false'
                    env.PRODUCT_CHANGED = changedFiles.contains('product/') ? 'true' : 'false'

                    echo "Media changed: ${env.MEDIA_CHANGED}, Product changed: ${env.PRODUCT_CHANGED}"
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