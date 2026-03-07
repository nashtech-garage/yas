pipeline {
    agent any
    
    // Thiết lập môi trường để Maven dùng đúng Java 21 (nếu server có sẵn)
    // Hoặc tạm thời để agent any để chạy test
    
    stages {
        stage('Prepare Root & Commons') {
            steps {
                echo '=== 1. Cài đặt Parent POM và Common Library ==='
                // Chạy lệnh install tại thư mục gốc nhưng chỉ cài đặt thư viện chung
                // Lệnh này giúp Jenkins nhận diện được cấu hình Parent của YAS
                sh './mvnw clean install -DskipTests -pl common-library -am'
            }
        }

        stage('Customer Service - CI') {
            when {
                changeset "customer/**"
            }
            steps {
                dir('customer') {
                    echo '=== 2. Chạy Unit Test cho Customer ==='
                    sh 'chmod +x mvnw || true'
                    // Lưu ý: Nếu bước này báo lỗi Java version, hãy báo thầy ngay
                    sh './mvnw clean test'

                    echo '=== 3. Build Docker Image ==='
                    sh 'docker build -t yas-customer:${env.BUILD_ID} .'
                }
            }
            post {
                always {
                    dir('customer') {
                        junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    }
                }
            }
        }
    }
}