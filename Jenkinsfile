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
                dir('customer') {
                    echo '=== 2. Chạy Unit Test cho Customer ==='
                    script {
                        docker.image('maven:3.9.6-eclipse-temurin-21').inside('-v /root/.m2:/root/.m2') {
                            // Tiếp tục thêm -Drevision ở đây
                            sh 'mvn clean test -Drevision=1.0-SNAPSHOT'
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