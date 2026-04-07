pipeline {
    agent any
    
    tools {
        maven 'Maven3'
        jdk 'Java21'   
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test & Coverage') {
            steps {
                echo 'Đang chạy Unit Test cho Media Service...'
                // Chuyển vào thư mục service và chạy test
                dir('media') {
                    sh 'mvn clean test jacoco:report'
                }
            }
        }

        stage('Build') {
            steps {
                echo 'Đang đóng gói ứng dụng...'
                dir('media') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
    }

    post {
        always {
            // Yêu cầu 5: Upload kết quả Test
            junit '**/target/surefire-reports/*.xml'
            
            // Yêu cầu 5: Upload báo cáo độ phủ JaCoCo
            jacoco execPattern: '**/target/jacoco.exec',
                   classPattern: '**/target/classes',
                   sourcePattern: '**/src/main/java'
        }
    }
}