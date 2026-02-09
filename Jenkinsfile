pipeline {
    agent any
    
    tools {
        // Đảm bảo tên này khớp với cấu hình trong Manage Jenkins > Tools
        jdk 'JDK21' 
        maven 'Maven3'
    }

    stages {
        stage('Detect Changes in Cart Service') {
            when {
                // Chỉ chạy các bước tiếp theo nếu có thay đổi trong thư mục 'cart'
                changeset 'cart/**'
            }
            steps {
                echo "Detected changes in Cart Service. Proceeding with CI..."
            }
        }

        stage('Unit Test - Cart Service') {
            when {
                changeset 'cart/**'
            }
            steps {
                // Di chuyển trực tiếp vào thư mục 'cart' ở root
                dir('cart') { 
                    echo 'Running Maven Tests for Cart Service...'
                    sh 'mvn clean test' 
                }
            }
            post {
                always {
                    // Thu thập kết quả test từ thư mục target của cart
                    junit 'cart/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Artifact - Cart Service') {
            when {
                // Chỉ build khi ở nhánh main hoặc khi có thay đổi ở cart trên feature branch
                anyOf {
                    branch 'main'
                    changeset 'cart/**'
                }
            }
            steps {
                dir('cart') {
                    echo 'Packaging Cart Service into JAR...'
                    sh 'mvn package -DskipTests' 
                }
            }
        }
    }
}