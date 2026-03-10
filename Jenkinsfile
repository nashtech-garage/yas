pipeline {
    agent any

    environment {
        // Bạn có thể định nghĩa các biến dùng chung ở đây
        APP_NAME = "my-web-app"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Đang lấy code từ branch: ${env.BRANCH_NAME}"
            }
        }

        stage('Install Dependencies') {
            steps {
                echo "Đang cài đặt thư viện cho ${env.APP_NAME}..."
                // sh 'npm install' hoặc 'mvn install' tùy project của bạn
            }
        }

        stage('Unit Test') {
            steps {
                echo "Đang chạy unit test..."
                // sh 'npm test'
            }
        }

        stage('Build & Deploy') {
            steps {
                script {
                    // Logic riêng cho từng branch
                    if (env.BRANCH_NAME == 'main') {
                        echo "--- Đang Deploy lên Production (Server Thật) ---"
                    } else if (env.BRANCH_NAME == 'develop') {
                        echo "--- Đang Deploy lên Staging (Server Thử Nghiệm) ---"
                    } else {
                        echo "--- Đây là branch tính năng (${env.BRANCH_NAME}), chỉ Build thử chứ không Deploy ---"
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Kết thúc quy trình cho branch ${env.BRANCH_NAME}"
        }
        success {
            echo "Build thành công rồi Luan ơi! Check ngrok link nhé."
        }
        failure {
            echo "Build lỗi rồi, kiểm tra lại code đi!"
        }
    }
}