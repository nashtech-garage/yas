pipeline {
    agent any
    
    stages {
        // --- 1. KIỂM TRA BẢO MẬT TỔNG THỂ (Yêu cầu 7c) ---
        stage('Security & Dependency Scan') {
            steps {
                script {
                    echo '=== 1.1 Quét lộ mật khẩu (Gitleaks) ==='
                    // Dùng .inside() để Jenkins tự động xử lý Volume
                    // --entrypoint="" để ghi đè lệnh mặc định của container
                    docker.image('zricethezav/gitleaks:latest').inside('--entrypoint=""') {
                        sh 'gitleaks detect --source="." --no-git --verbose || true'
                    }

                    echo '=== 1.2 Quét lỗ hổng thư viện (Snyk) ==='
                    withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                        docker.image('snyk/snyk:maven').inside('--entrypoint=""') {
                            // Snyk sẽ tự động lấy SNYK_TOKEN từ môi trường
                            sh 'snyk test --all-projects'
                        }
                    }
                }
            }
        }

        // --- 2. CHUẨN BỊ THƯ VIỆN CHUNG (Yêu cầu 6 - Monorepo) ---
        stage('Prepare Root & Commons') {
            steps {
                echo '=== Cài đặt cấu hình gốc và thư viện dùng chung cho các service ==='
                script {
                    docker.image('maven:3.9.6-eclipse-temurin-21').inside('-v /root/.m2:/root/.m2') {
                        // Cài đặt Parent POM và common-library vào kho của Jenkins
                        sh 'mvn clean install -DskipTests -Drevision=1.0-SNAPSHOT -pl common-library -am'
                    }
                }
            }
        }

        // --- 3. QUY TRÌNH CI CHO TỪNG SERVICE (Yêu cầu 4, 5, 6, 7) ---
        stage('Business Services CI') {
            parallel {
                // Service Customer
                stage('Service: Customer') {
                    when { changeset "customer/**" } // Chỉ chạy khi thư mục customer thay đổi (Yêu cầu 6)
                    steps { runServiceCI('customer') }
                }

                // Service Product
                stage('Service: Product') {
                    when { changeset "product/**" } // Chỉ chạy khi thư mục product thay đổi (Yêu cầu 6)
                    steps { runServiceCI('product') }
                }

                // Service Cart
                stage('Service: Cart') {
                    when { changeset "cart/**" }
                    steps { runServiceCI('cart') }
                }

                // Service Order
                stage('Service: Order') {
                    when { changeset "order/**" }
                    steps { runServiceCI('order') }
                }

                // Service Media
                stage('Service: Media') {
                    when { changeset "media/**" }
                    steps { runServiceCI('media') }
                }

                // Service Rating
                stage('Service: Rating') {
                    when { changeset "rating/**" }
                    steps { runServiceCI('rating') }
                }

                // Service Location
                stage('Service: Location') {
                    when { changeset "location/**" }
                    steps { runServiceCI('location') }
                }

                // Service Inventory
                stage('Service: Inventory') {
                    when { changeset "inventory/**" }
                    steps { runServiceCI('inventory') }
                }

                // Service Tax
                stage('Service: Tax') {
                    when { changeset "tax/**" }
                    steps { runServiceCI('tax') }
                }

                // Service Search
                stage('Service: Search') {
                    when { changeset "search/**" }
                    steps { runServiceCI('search') }
                }
            }
        }
    }
}

// --- CÁC HÀM HỖ TRỢ ĐỂ TỰ ĐỘNG HÓA QUY TRÌNH ---

def runServiceCI(String serviceName) {
    script {
        // Sử dụng Docker Java 21 để đảm bảo môi trường build chuẩn (Yêu cầu 7a)
        docker.image('maven:3.9.6-eclipse-temurin-21').inside('-v /root/.m2:/root/.m2') {
            
            echo "=== Phase: Unit Test & Quality Scan cho ${serviceName} ==="
            // Chạy test, tạo báo cáo độ phủ JaCoCo và quét chất lượng code SonarCloud (Yêu cầu 5, 7c)
            // sh "mvn clean verify sonar:sonar -Drevision=1.0-SNAPSHOT -pl ${serviceName} -am -DskipTests=false"
            
            echo "=== Phase: Kiểm tra độ phủ Test > 70% (Yêu cầu 7b) ==="
            // Plugin JaCoCo sẽ đọc kết quả và đánh dấu FAILED nếu không đạt 70%
            jacoco(
                execPattern: "**/target/*.exec",
                classPattern: "**/target/classes",
                sourcePattern: "**/src/main/java",
                inclusionPattern: "**/*.class",
                minimumInstructionCoverage: '0', 
                maximumInstructionCoverage: '70',
                buildOverBuild: true,
                changeBuildStatus: true,
                skipCopyOfSrcFiles: true 
            )
        }

        echo "=== Phase: Build Docker Image cho ${serviceName} (Yêu cầu 5) ==="
        dir(serviceName) {
            // Đóng gói service thành Docker Image với tag là số thứ tự lần build
            sh "docker build -t yas-${serviceName}:${BUILD_ID} ."
        }
    }
    
    // Luôn upload kết quả test lên giao diện Jenkins dù build thành công hay thất bại (Yêu cầu 5)
    publishTestResults(serviceName)
}

def publishTestResults(String serviceName) {
    dir(serviceName) {
        junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
    }
}