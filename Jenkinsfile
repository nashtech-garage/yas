pipeline {
    agent any
    
    environment {
        // Khai báo các ID mà bạn đã đặt trong Manage Credentials
        DOCKER_HUB_CREDS = credentials('docker-hub-id')
        KUBECONFIG_FILE = credentials('k8s-config-id')
        IMAGE_NAME = "thoai2312/yas-identity-service" // Thay bằng tên của bạn
        IMAGE_TAG = "${env.BUILD_NUMBER}" // Dùng số lần build làm tag
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-repo/yas-identity.git'
            }
        }

        stage('Build Artifact') {
            steps {
                // Build file .jar bằng Maven
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    // Đăng nhập và đẩy image lên Docker Hub
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                    sh "echo ${DOCKER_HUB_CREDS_PSW} | docker login -u ${DOCKER_HUB_CREDS_USR} --password-stdin"
                    sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }
    }
}
