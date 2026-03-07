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
                echo '=== 2. Chạy Unit Test cho Customer ==='
                script {
                    docker.image('maven:3.9.6-eclipse-temurin-21').inside('-v /root/.m2:/root/.m2') {
                        // -U: Ép cập nhật lại thư viện vừa build ở Stage 1
                        // Chạy từ thư mục gốc, dùng -pl để chỉ định build riêng customer
                        // -am: để nó tự liên kết với common-library đã install
                        sh 'mvn clean test -Drevision=1.0-SNAPSHOT -U -pl customer -am'
                    }
                }

                dir('customer') {
                    echo '=== 3. Build Docker Image ==='
                    sh 'docker build -t yas-customer:${env.BUILD_ID} .'
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