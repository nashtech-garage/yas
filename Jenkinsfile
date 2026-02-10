def CHANGED_MODULES = ""
def IS_ROOT_CHANGED = false

pipeline {
    agent any
    tools {
        jdk 'JDK21'
        maven 'Maven3'
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

                    echo "Files changed: ${changedFiles}"

                    def modules = []
                    def rootFiles = ["pom.xml"]

                    for (file in changedFiles) {
                        if (file.startsWith("cart/")) {
                            modules.add("cart")
                        }

                        if (file.startsWith("common-library/")) {
                            //modules.addAll(["cart", "customer", "visit"])
                            modules.addAll(["cart"])
                        }

                        if (rootFiles.contains(file)) {
                            IS_ROOT_CHANGED = true
                        }
                    }

                    CHANGED_MODULES = modules.unique().join(",")

                    echo "Affected modules: ${CHANGED_MODULES}"
                    echo "Root changed: ${IS_ROOT_CHANGED}"
                }
            }
        }

        stage('Unit Test') {
            when {
                expression { return CHANGED_MODULES != "" || IS_ROOT_CHANGED }
            }
            steps {
                script {
                    if (IS_ROOT_CHANGED) {
                        echo "Root files changed. Running full test suite..."
                        sh "mvn clean test"
                    }
                    else if (CHANGED_MODULES != "") {
                        echo "Testing affected modules: ${CHANGED_MODULES}"
                        sh "mvn -pl ${CHANGED_MODULES} -am clean test"
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
                    expression { return CHANGED_MODULES != "" || IS_ROOT_CHANGED }
                }
            }
            steps {
                script {
                    // Nếu là nhánh main hoặc sửa file root -> Build tất cả để đảm bảo an toàn
                    if (BRANCH_NAME == 'main' || IS_ROOT_CHANGED ) {
                        sh "mvn clean package -DskipTests"
                    } else {
                        sh "mvn -pl ${CHANGED_MODULES} -am package -DskipTests"
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