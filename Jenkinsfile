pipeline {
    agent any

    stages {
        stage('Customer Service - CI') {
            when {
                // Chỉ kích hoạt khi có ai đó sửa code trong thư mục customer/
                changeset "customer/**"
            }
            steps {
                // CỰC KỲ QUAN TRỌNG: Lệnh dir() giúp Jenkins "cd" vào thư mục customer
                // Nếu không có lệnh này, Jenkins sẽ đứng ở thư mục gốc và báo lỗi không tìm thấy code
                dir('customer') {
                    
                    echo '=== 1. Chạy Unit Test ==='
                    // Cấp quyền thực thi cho file Maven Wrapper (phòng trường hợp mất quyền)
                    sh 'chmod +x mvnw || true'
                    // Lệnh chạy test. Nó sẽ tự động sinh ra file báo cáo test (JUnit) và độ phủ (JaCoCo)
                    sh './mvnw clean test'

                    echo '=== 2. Build Docker Image ==='
                    // Đọc file Dockerfile trong thư mục customer và build thành image
                    // Sử dụng ${env.BUILD_ID} để mỗi lần build ra một version khác nhau (VD: yas-customer:15)
                    sh 'docker build -t yas-customer:${env.BUILD_ID} .'
                }
            }
            post {
                always {
                    dir('customer') {
                        echo '=== 3. Upload Kết quả Test (Đáp ứng yêu cầu 5) ==='
                        // Gom nhặt các file kết quả test (đuôi .xml) để hiển thị lên giao diện Jenkins
                        junit 'target/surefire-reports/*.xml'
                        
                        // Gom nhặt báo cáo độ phủ code (Coverage)
                        // Lưu ý: Cần cài đặt plugin JaCoCo trên Jenkins để lệnh này hoạt động
                        // jacoco execPattern: 'target/jacoco.exec' 
                    }
                }
            }
        }
    }
}