pipeline {
    agent any
    tools { maven 'maven3' }
    environment {
        JAVA_HOME = "/var/jenkins_home/tools/hudson.model.JDK/jdk25/jdk-25.0.2"
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        SERVICES = "backoffice-bff,cart,customer,inventory,location,media,order,payment,payment-paypal,product,promotion,rating,recommendation,search,storefront-bff,tax"
        // Cấu hình để Testcontainers biết chỗ tìm Docker
        DOCKER_HOST = "unix:///var/run/docker.sock"
        TESTCONTAINERS_RYUK_DISABLED = "true" 
    }
    stages {
        stage('Phase 0: Kill The Variable') {
            steps {
                script {
                    sh "find . -name 'pom.xml' -exec sed -i 's/\\\${revision}/1.0-SNAPSHOT/g' {} +"
                }
            }
        }
        stage('Phase 1: Scan & Detect') {
            steps {
                script {
                    sh "git fetch origin main"
                    def changedFiles = sh(script: "git diff --name-only FETCH_HEAD...HEAD", returnStdout: true).trim()
                    def toBuild = []
                    def serviceList = SERVICES.split(',')
                    def lines = changedFiles.split("\n")
                    for (svc in serviceList) {
                        if (lines.any { it.startsWith("${svc}/") }) { toBuild.add(svc) }
                    }
                    env.CHANGED_SERVICES = toBuild.join(",")
                }
            }
        }
        stage('Phase 2: Build & Coverage') {
            when { expression { return env.CHANGED_SERVICES != "" } }
            steps {
                script {
                    sh "mvn install -N -DskipTests" 
                    dir('common-library') {
                        sh "mvn clean install -DskipTests"
                    }

                    def list = env.CHANGED_SERVICES.split(",")
                    for (svc in list) {
                        if (svc == 'common-library') continue
                        stage("Test & Coverage: ${svc}") {
                            dir(svc) {
                                // CHỖ NÀY LÀ QUAN TRỌNG NHẤT: Ép dùng Java 25 để chạy test
                                sh "mvn clean verify -U -DskipTests=false"
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            jacoco (
                execPattern: '**/target/*.exec',
                classPattern: '**/target/classes',
                sourcePattern: '**/src/main/java'
            )
        }
    }
}