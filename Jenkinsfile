pipeline {
    // Chạy trên bất kỳ Agent nào khả dụng
    agent any
    
    // Khai báo công cụ đã cài trong Manage Jenkins -> Tools
    tools {
        maven 'Maven3' 
        jdk 'Java21'   
    }

    // Ép biến môi trường để đảm bảo Jenkins chắc chắn dùng Java 21 thay vì bản mặc định (Java 8)
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
                
                echo 'Đang build các dependencies nội bộ và chạy Unit Test cho Media Service...'
                // Chạy test ở root, dùng cờ -pl để chỉ định service media và -am để build kèm common-library
                sh 'mvn clean test jacoco:report -pl media -am'
            }
        }

        stage('Build') {
            steps {
                echo 'Đang đóng gói ứng dụng (Bỏ qua test vì đã chạy ở stage trước)...'
                sh 'mvn clean package -DskipTests -pl media -am'
            }
        }
    }

    // Khối hành động sau khi các stages hoàn thành
    post {
        success {
            echo 'Pipeline chạy thành công! Đang upload báo cáo Test và Coverage...'
            
            // Upload báo cáo Pass/Fail của Unit Test
            junit '**/media/target/surefire-reports/*.xml'
            
            // Upload báo cáo độ phủ code của JaCoCo
            jacoco execPattern: '**/media/target/jacoco.exec',
                   classPattern: '**/media/target/classes',
                   sourcePattern: '**/media/src/main/java'
        }
        failure {
            echo 'Pipeline có lỗi! Đang kéo báo cáo Test về xem bị fail ở đâu (nếu có)...'
            // allowEmptyResults giúp Jenkins không bị crash nếu lỗi xảy ra trước khi test kịp chạy
            junit allowEmptyResults: true, testResults: '**/media/target/surefire-reports/*.xml'
        }
    }
}