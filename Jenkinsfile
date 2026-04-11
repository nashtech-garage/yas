pipeline {
    agent any

    tools {
        // Bạn PHẢI vào Manage Jenkins -> Tools đặt tên đúng như này
        jdk 'jdk21'
        maven 'maven3'
    }

    environment {
        // List các service Java của YAS
        SERVICES = "backoffice-bff,cart,customer,inventory,location,media,order,payment,payment-paypal,product,promotion,rating,recommendation,search,storefront-bff,tax"
    }

    stages {
        stage('Phase 1: Scan & Detect') {
            steps {
                script {
                    echo "--- Đang kiểm tra các thay đổi trong Monorepo ---"
                    // So sánh với main để biết folder nào bị sửa
                    def changedFiles = sh(script: "git diff --name-only origin/main", returnStdout: true).trim()
                    echo "Files changed: \n${changedFiles}"

                    def toBuild = []
                    def serviceList = SERVICES.split(',')
                    for (svc in serviceList) {
                        if (changedFiles.contains("${svc}/")) {
                            toBuild.add(svc)
                        }
                    }
                    env.CHANGED_SERVICES = toBuild.join(",")
                }
            }
        }

        stage('Phase 2: Unit Test & Coverage') {
            when { expression { return env.CHANGED_SERVICES != "" } }
            steps {
                script {
                    def list = env.CHANGED_SERVICES.split(",")
                    for (svc in list) {
                        stage("Testing ${svc}") {
                            dir(svc) {
                                echo "--- Running Tests for ${svc} ---"
                                // Chạy test và tạo báo cáo JaCoCo
                                sh 'mvn clean test'
                            }
                        }
                    }
                }
            }
        }

        stage('Phase 3: Build Artifact') {
            when { expression { return env.CHANGED_SERVICES != "" } }
            steps {
                script {
                    def list = env.CHANGED_SERVICES.split(",")
                    for (svc in list) {
                        stage("Building ${svc}") {
                            dir(svc) {
                                echo "--- Packaging ${svc} ---"
                                // Build ra file jar (skip test vì đã chạy ở phase 2 rồi)
                                sh 'mvn package -DskipTests'
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo "--- Generating Reports ---"
            // Yêu cầu số 5: Upload kết quả test
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            
            // Yêu cầu số 7b: Độ phủ JaCoCo (nếu đã cài plugin JaCoCo trên Jenkins)
            jacoco execPattern: '**/target/*.exec', allowEmptyResults: true
        }
        success {
            echo "Pipeline hoàn thành rực rỡ!"
        }
        failure {
            echo "Build oẹo rồi, check log đi Sỹ!"
        }
    }
}