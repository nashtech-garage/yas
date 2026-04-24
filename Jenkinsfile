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
                sh 'git fetch origin +refs/heads/main:refs/remotes/origin/main --prune'

                def baseCommit = ''
                def hasOriginMain = (sh(
                    script: 'git rev-parse --verify refs/remotes/origin/main >/dev/null 2>&1',
                    returnStatus: true
                ) == 0)

                if (hasOriginMain) {
                    baseCommit = sh(
                        script: 'git merge-base HEAD refs/remotes/origin/main',
                        returnStdout: true
                    ).trim()
                    echo 'Using refs/remotes/origin/main as base'
                } else {
                    def hasHeadPrev = (sh(
                        script: 'git rev-parse --verify HEAD~1 >/dev/null 2>&1',
                        returnStatus: true
                    ) == 0)

                    if (hasHeadPrev) {
                        baseCommit = sh(
                            script: 'git rev-parse HEAD~1',
                            returnStdout: true
                        ).trim()
                        echo 'origin/main not found, fallback to HEAD~1'
                    } else {
                        baseCommit = sh(
                            script: 'git rev-parse HEAD',
                            returnStdout: true
                        ).trim()
                        echo 'Single-commit branch, fallback to HEAD'
                    }
                }

                def changedFiles = sh(
                    script: "git diff --name-only ${baseCommit} HEAD",
                    returnStdout: true
                ).trim()

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