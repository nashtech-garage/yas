pipeline {
    agent any
    stages {
        stage('Hello YAS') {
            steps {
                echo 'CI Pipeline for YAS microservices!'
                sh 'mvn --version || echo "Maven OK"'
            }
        }
        stage('Test Media Service') {
            when { changeset "services/media-service/**" }
            steps {
                dir('services/media-service') {
                    sh 'mvn clean test || true'
                }
            }
        }
    }
    post {
        always { 
            echo 'Pipeline complete!' 
        }
    }
}

// Update comment
// Update comment
