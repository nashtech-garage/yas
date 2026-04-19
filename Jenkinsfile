pipeline {
    agent any

    // Header thông tin đồ án (Tùy chọn để giảng viên dễ theo dõi)
    // Sinh viên: Nguyễn Duy Trường - MSSV: 23120182
    
    stages {
        // ==========================================
        // 1. PIPELINE CHO PAYMENT SERVICE
        // ==========================================
        stage('Payment Service') {
            when {
                // Chỉ kích hoạt khi có thay đổi trong thư mục payment
                changeset "payment/**"
            }
            stages {
                stage('Build Payment') {
                    steps {
                        dir('payment') {
                            echo "--- ĐANG THỰC HIỆN BUILD: PAYMENT SERVICE ---"
                            // sh './mvnw clean package -DskipTests' // Ví dụ lệnh build cho Java
                        }
                    }
                }
                stage('Test Payment') {
                    steps {
                        dir('payment') {
                            echo "--- ĐANG THỰC HIỆN TEST: PAYMENT SERVICE ---"
                            // sh './mvnw test' 
                        }
                    }
                    post {
                        always {
                            echo "Đang thu thập báo cáo test và độ phủ cho Payment..."
                            // 1. Upload kết quả test (JUnit XML)
                            // junit 'payment/target/surefire-reports/*.xml'
                            
                            // 2. Lưu trữ báo cáo độ phủ (Coverage HTML/Artifacts)
                            // archiveArtifacts artifacts: 'payment/target/site/jacoco/**', allowEmptyArchive: true
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. PIPELINE CHO RATING SERVICE
        // ==========================================
        stage('Rating Service') {
            when {
                // Chỉ kích hoạt khi có thay đổi trong thư mục rating
                changeset "rating/**"
            }
            stages {
                stage('Build Rating') {
                    steps {
                        dir('rating') {
                            echo "--- ĐANG THỰC HIỆN BUILD: RATING SERVICE ---"
                        }
                    }
                }
                stage('Test Rating') {
                    steps {
                        dir('rating') {
                            echo "--- ĐANG THỰC HIỆN TEST: RATING SERVICE ---"
                        }
                    }
                    post {
                        always {
                            echo "Đang thu thập báo cáo cho Rating..."
                            // archiveArtifacts artifacts: 'rating/coverage/**', allowEmptyArchive: true
                        }
                    }
                }
            }
        }
    }

    post {
        failure {
            echo "Pipeline thất bại. Vui lòng kiểm tra lại log của service tương ứng."
        }
    }
}
