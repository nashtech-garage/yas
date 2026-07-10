pipeline {
    agent any
    stages {
        stage('Detect Changes') {
            steps {
                script {
                    // Logic an toàn cho lần build đầu tiên
                    try {
                        // Kiểm tra xem có commit trước đó không (HEAD~1)
                        def changedFiles = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim()
                        echo "Các file thay đổi: ${changedFiles}"
                        
                        env.BUILD_PRODUCT = changedFiles.contains('product/') ? 'true' : 'false'
                        // Thêm các service khác của nhóm vào đây
                    } catch (Exception e) {
                        echo "Lần build đầu tiên hoặc không tìm thấy commit cũ. Mặc định Build All."
                        env.BUILD_PRODUCT = 'true'
                    }
                }
            }
        }
        
        stage('Build Product Service') {
            when { expression { env.BUILD_PRODUCT == 'true' } }
            steps {
                dir('product') {
                    echo "🚀 Đang build Product Service..."
                    // sh './mvnw clean install -DskipTests' 
                }
            }
        }
    }
    
    post {
        always {
            echo "Kết thúc Pipeline. Jenkins sẽ tự động báo trạng thái về GitHub Webhook."
        }
    }
}