pipeline {
    agent any
    
    stages {
        stage('Prepare Root & Commons') {
            steps {
                echo '=== 1. Cài đặt Parent POM và Common Library ==='
                script {
                    docker.image('maven:3.9.6-eclipse-temurin-21').inside('-v /root/.m2:/root/.m2') {
                        // Thêm -Drevision=1.0-SNAPSHOT để Maven hiểu biến version
                        sh 'mvn clean install -DskipTests -Drevision=1.0-SNAPSHOT -pl common-library -am'
                    }
                }
            }
        }

        stage('Customer Service - CI') {
            when {
                changeset "customer/**"
            }
            steps {
                echo '=== 2. Chạy Unit Test & Đóng gói (Package) ==='
                script {
                    docker.image('maven:3.9.6-eclipse-temurin-21').inside('-v /root/.m2:/root/.m2') {
                        // Đổi từ 'test' sang 'package' để tạo file .jar
                        sh 'mvn clean package -Drevision=1.0-SNAPSHOT -U -pl customer -am -DskipTests=false'
                    }
                }

                dir('customer') {
                    echo '=== 3. Build Docker Image ==='
                    // Đảm bảo file .jar đã tồn tại trong thư mục target của customer
                    sh "docker build -t yas-customer:${BUILD_ID} ."
                }
            }
            post {
                always {
                    dir('customer') {
                        junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    }
                }
            }
        }
    }
}