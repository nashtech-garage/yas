pipeline {
    agent any
    
    stages {
        stage('Prepare Root & Commons') {
            steps {
                echo '=== 1. Cài đặt Parent POM và Common Library ==='
                script {
                    // Dùng Docker image Maven 3.9 + Java 21
                    // Chúng ta dùng lệnh 'mvn' thay vì './mvnw'
                    docker.image('maven:3.9.6-eclipse-temurin-21').inside('-v /root/.m2:/root/.m2') {
                        sh 'mvn clean install -DskipTests -pl common-library -am'
                    }
                }
            }
        }

        stage('Customer Service - CI') {
            when {
                changeset "customer/**"
            }
            steps {
                dir('customer') {
                    echo '=== 2. Chạy Unit Test cho Customer ==='
                    script {
                        docker.image('maven:3.9.6-eclipse-temurin-21').inside('-v /root/.m2:/root/.m2') {
                            // Ở đây dùng 'mvn' vì Docker image đã có sẵn Maven rồi
                            sh 'mvn clean test'
                        }
                    }

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