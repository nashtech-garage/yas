pipeline {
    agent any

    stages {
        // ==========================================
        // Cụm cấu hình cho MEDIA SERVICE
        // ==========================================
        stage('Media Service') {
            // YÊU CẦU 6: Chỉ kích hoạt cụm này khi có thay đổi trong thư mục 'media/'
            when { 
                changeset "media/**" 
            }
            stages {
                // YÊU CẦU 5: Phase 1 - Build
                stage('Build Media') {
                    steps {
                        echo "Phát hiện thay đổi ở Media Service. Bắt đầu Build..."
                        // Build Media và các thư viện xài chung (also-make), tạm lờ test (-DskipTests)
                        sh 'mvn --projects media --also-make clean package -DskipTests'
                    }
                }
                
                // YÊU CẦU 5: Phase 2 - Test
                stage('Test Media') {
                    steps {
                        echo "Bắt đầu Test cho Media Service..."
                        sh 'mvn --projects media test'
                    }
                    post {
                        always {
                            // YÊU CẦU 5: Upload Test Result và Độ phủ Testcase (Jacoco) riêng cho phần tử media
                            junit 'media/target/surefire-reports/*.xml'
                            jacoco execPattern: 'media/target/jacoco.exec'
                        }
                    }
                }
            }
        }

        // ==========================================
        // Cụm cấu hình cho PRODUCT SERVICE
        // ==========================================
        stage('Product Service') {
            when { 
                changeset "product/**" 
            }
            stages {
                stage('Build Product') {
                    steps {
                        echo "Phát hiện thay đổi ở Product Service. Bắt đầu Build..."
                        sh 'mvn --projects product --also-make clean package -DskipTests'
                    }
                }
                stage('Test Product') {
                    steps {
                        echo "Bắt đầu Test cho Product Service..."
                        sh 'mvn --projects product test'
                    }
                    post {
                        always {
                            junit 'product/target/surefire-reports/*.xml'
                            jacoco execPattern: 'product/target/jacoco.exec'
                        }
                    }
                }
            }
        }

        // Tương tự, nếu có service Cart, nhóm bạn nhân bản cụm này ra đổi tên thành chữ "cart" nhé.
    }
}
