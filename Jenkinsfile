pipeline {
    agent any
    
    tools {
        jdk 'jdk22'
        maven 'maven3'
    }

    stages {
        stage('Initialize') {
            steps {
                echo 'Đang khởi tạo hệ thống CI cho nhóm 3 người....'
                sh 'mvn --version'
            }
        }

        stage('Customer Service') {
            when { changeset "customer/**" }
            steps {
                echo 'Change detection in Customer Service....'
                sh 'mvn clean install -pl customer -am'
            }
            post {
                always {
                    jacoco(minimumInstructionCoverage: '70')
                }
            }
        }

        // stage('Vets Service') {
        //     when { changeset "vets/**" }
        //     steps {
        //         echo 'Phát hiện thay đổi tại Vets Service...'
        //         sh 'mvn clean install -pl vets -am'
        //     }
        //     post {
        //         always {
        //             jacoco(minimumInstructionCoverage: '70')
        //         }
        //     }
        // }

        // stage('Visits Service') {
        //     when { changeset "visit/**" }
        //     steps {
        //         echo 'Phát hiện thay đổi tại Visits Service...'
        //         sh 'mvn clean install -pl visit -am'
        //     }
        //     post {
        //         always {
        //             jacoco(minimumInstructionCoverage: '70')
        //         }
        //     }
        // }
    }
}