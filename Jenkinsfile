pipeline {

    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    environment {
        CHANGED_MODULES = ""
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Detect Changed Modules') {
            steps {
                script {
                    // Cập nhật thông tin từ Remote để Jenkins "thấy" được nhánh main
                    echo "Fetching origin main to compare..."
                    sh "git fetch origin main"

                    def changedFiles = sh(
                        script: "git diff --name-only origin/main...HEAD",
                        returnStdout: true
                    ).trim()

                    echo "Changed files:\n${changedFiles}"

                    def modules = []

                    if (changedFiles.contains("cart/")) {
                        modules.add("cart")
                    }

                    // --- Thêm service ở đây ---

                    // Nếu common-library thay đổi
                    if (changedFiles.contains("common-library/")) {
                        modules.add("cart")
                        //modules.add("customer")
                    }

                    env.CHANGED_MODULES = modules.unique().join(",")

                    echo "Affected modules: ${env.CHANGED_MODULES}"
                }
            }
        }

        stage('Unit Test') {
            when {
                expression { return env.CHANGED_MODULES != "" }
            }
            steps {
                script {
                    sh "mvn -pl ${env.CHANGED_MODULES} -am clean test"
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Artifact') {
            when {
                anyOf {
                    branch 'main'
                    expression { return env.CHANGED_MODULES != "" }
                }
            }
            steps {
                script {
                    sh "mvn -pl ${env.CHANGED_MODULES} -am package -DskipTests"
                }
            }
        }
    }

    post {
        success {
            echo "CI Pipeline SUCCESS"
        }
        failure {
            echo "CI Pipeline FAILED"
        }
    }
}
