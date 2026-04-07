pipeline {
    agent any
    
    tools {
        maven 'Maven3' // Nhớ đảm bảo tên này vẫn đúng với cấu hình trên Jenkins của bạn nhé
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
                echo 'Đang build các dependencies nội bộ và chạy Unit Test cho Media Service...'
                // Đứng ở thư mục gốc, dùng cờ -pl (chỉ định module) và -am (build luôn các module phụ thuộc)
                sh 'mvn clean test jacoco:report -pl media -am'
            }
        }

        stage('Build') {
            steps {
                echo 'Đang đóng gói ứng dụng...'
                sh 'mvn clean package -DskipTests -pl media -am'
            }
        }
    }

    post {
        // Chỉ upload nếu test thành công để tránh lỗi missing file
        success {
            echo 'Pipeline chạy thành công! Đang upload báo cáo...'
            junit '**/media/target/surefire-reports/*.xml'
            
            jacoco execPattern: '**/media/target/jacoco.exec',
                   classPattern: '**/media/target/classes',
                   sourcePattern: '**/media/src/main/java'
        }
        // Nếu test rớt (ví dụ code sai) thì vẫn ráng lôi báo cáo về xem
        failure {
            echo 'Pipeline có lỗi! Đang kéo báo cáo Test về xem bị fail ở đâu...'
            junit allowEmptyResults: true, testResults: '**/media/target/surefire-reports/*.xml'
        }
    }
}