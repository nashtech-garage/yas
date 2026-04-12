pipeline {
    agent any
    
    tools {
        maven 'Maven3' 
        jdk 'Java21'   
    }

    // Ép Java21
    environment {
        PATH_TO_JAVA = tool name: 'Java21', type: 'jdk'
        JAVA_HOME = "${PATH_TO_JAVA}"
        PATH = "${PATH_TO_JAVA}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                // Lấy code từ GitHub về
                checkout scm
            }
        }

        stage('Test & Coverage') {
            steps {
                echo 'Đang kiểm tra phiên bản Java...'
                sh 'java -version'
                
                echo 'Đang chạy Unit Test và tạo report Coverage cho toàn bộ dự án...'
                sh 'mvn clean test jacoco:report'
            }
        }

        stage('Build') {
            steps {
                echo 'Đang đóng gói toàn bộ ứng dụng (Bỏ qua test vì đã chạy ở stage trước)...'
                sh 'mvn package -DskipTests'
            }
        }
    }

//Lấy báo cáo
    post {
        always {
            echo 'Pipeline hoàn thành (Dù Pass hay Fail). Đang kéo báo cáo Test và Coverage...'
            
            
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            
           
            jacoco execPattern: '**/target/jacoco.exec',
                   classPattern: '**/target/classes',
                   sourcePattern: '**/src/main/java'
        }
    }
}   