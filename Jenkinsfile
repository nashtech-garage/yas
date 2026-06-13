pipeline {
    agent any
    
    tools {
        jdk 'jdk22'
        maven 'maven3'
    }

    stages {
        stage('Initialize') {
            steps {
                echo 'Khởi tạo hệ thống CI...'
                sh 'mvn --version'
            }
        }

        stage('Customer Build & Test') {
            when { changeset "customer/**" }
            steps {
                echo 'Chạy Build và Unit Test cho Customer Service...'
                // Thêm flag -Dmaven.test.failure.ignore=true để nếu test fail vẫn chạy tiếp Sonar
                sh 'mvn clean install -pl customer -am -DskipTests=false'
            }
            post {
                always {
                    jacoco(minimumInstructionCoverage: '70')
                }
            }
        }

        stage('Static Code Analysis (SonarCloud)') {
            when { changeset "customer/**" }
            steps {
                echo 'Đang quét bảo mật với SonarCloud...'
                withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                    // Dùng -DskipTests để không phải chạy lại Test một lần nữa, tiết kiệm RAM
                    sh "mvn sonar:sonar -Dsonar.token=${SONAR_TOKEN} -pl customer -am -DskipTests"
                }
            }
        }
    }
}