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
                    
                    // Kéo code mới nhất của main về
                    sh "git fetch origin main"
                    
                    // Dùng FETCH_HEAD thay cho origin/main
                    def changedFiles = sh(script: "git diff --name-only FETCH_HEAD...HEAD", returnStdout: true).trim()
                    echo "Files changed: \n${changedFiles}"

                    def toBuild = []
                    def serviceList = SERVICES.split(',')
                    // Dùng logic split chuẩn để check folder
                    def lines = changedFiles.split("\n")
                    for (svc in serviceList) {
                        if (lines.any { it.startsWith("${svc}/") }) {
                            toBuild.add(svc)
                        }
                    }
                    env.CHANGED_SERVICES = toBuild.join(",")
                    echo "Services to build: ${env.CHANGED_SERVICES}"
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
            // JUnit thì dùng allowEmptyResults được
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            
            // JaCoCo: Chỉ cần liệt kê các pattern, nếu không có nó sẽ tự log warning chứ không làm tèo cả build
            jacoco (
                execPattern: '**/target/*.exec',
                classPattern: '**/target/classes',
                sourcePattern: '**/src/main/java',
                inclusionPattern: '**/*.class'
            )
        }
    }
}