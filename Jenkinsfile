pipeline {
    agent any
    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    environment {
        CHANGED_MODULES = ""
        IS_ROOT_CHANGED = "false"
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
                    def baseBranch = env.CHANGE_TARGET ?: "main"
                    echo "Comparing with origin/${baseBranch}"
                    
                    // Fetch để đảm bảo có dữ liệu so sánh
                    sh "git fetch origin ${baseBranch}:refs/remotes/origin/${baseBranch}"

                    def changedFiles = sh(
                        script: "git diff --name-only origin/${baseBranch} HEAD",
                        returnStdout: true
                    ).trim().split('\n')

                    def modules = []
                    def rootFiles = ["pom.xml", "Jenkinsfile", "docker-compose.yml"]

                    for (file in changedFiles) {
                        if (file.contains("cart/")) modules.add("cart")
                        
                        if (file.contains("common-library/")) {
                            modules.addAll(["cart", "customer", "visit"])
                        }
                        
                        // Kiểm tra nếu sửa file ở thư mục gốc
                        if (rootFiles.any { rootFile -> file == rootFile }) {
                            env.IS_ROOT_CHANGED = "true"
                        }
                    }

                    env.CHANGED_MODULES = modules.unique().join(",")
                    echo "Affected modules: ${env.CHANGED_MODULES}"
                    echo "Root changed: ${env.IS_ROOT_CHANGED}"
                }
            }
        }

        stage('Unit Test') {
            when {
                expression { return env.CHANGED_MODULES != "" || env.IS_ROOT_CHANGED == "true" }
            }
            steps {
                script {
                    if (env.IS_ROOT_CHANGED == "true") {
                        echo "Root files changed. Running full test suite..."
                        sh "mvn clean test"
                    }
                    else if (env.CHANGED_MODULES != "") {
                        echo "Testing affected modules: ${env.CHANGED_MODULES}"
                        sh "mvn -pl ${env.CHANGED_MODULES} -am clean test"
                    }
                    else {
                        echo "No specific modules detected for testing. Skipping Maven command."
                    }
                }
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Build Artifact') {
            when {
                anyOf {
                    branch 'main'
                    expression { return env.CHANGED_MODULES != "" || env.IS_ROOT_CHANGED == "true" }
                }
            }
            steps {
                script {
                    // Nếu là nhánh main hoặc sửa file root -> Build tất cả để đảm bảo an toàn
                    if (env.BRANCH_NAME == 'main' || env.IS_ROOT_CHANGED == "true") {
                        sh "mvn clean package -DskipTests"
                    } else {
                        sh "mvn -pl ${env.CHANGED_MODULES} -am package -DskipTests"
                    }
                }
            }
        }
    }
    
    post {
        success { echo "CI Pipeline SUCCESS" }
        failure { echo "CI Pipeline FAILED" }
    }
}