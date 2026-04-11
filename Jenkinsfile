pipeline {
    agent any
    tools { maven 'maven3' }
    environment {
        JAVA_HOME = "/var/jenkins_home/tools/hudson.model.JDK/jdk25/jdk-25.0.2"
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        SERVICES = "backoffice-bff,cart,customer,inventory,location,media,order,payment,payment-paypal,product,promotion,rating,recommendation,search,storefront-bff,tax"
    }
    stages {
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
        stage('Phase 2: Build & Fix Versions') {
            when { expression { return env.CHANGED_SERVICES != "" } }
            steps {
                script {
                    def VERSION = "1.0-SNAPSHOT"
                    
                    // LỆNH QUAN TRỌNG: Flatten toàn bộ project để xóa bỏ biến ${revision}
                    echo "--- Flattening and Installing All Modules ---"
                    sh "mvn clean install -N -Drevision=${VERSION} -DskipTests"
                    
                    dir('common-library') {
                        // Dùng lệnh flatten để nó ghi đè file POM chuẩn vào kho
                        sh "mvn clean install -Drevision=${VERSION} -DskipTests"
                    }

                    def list = env.CHANGED_SERVICES.split(",")
                    for (svc in list) {
                        if (svc == 'common-library') continue
                        stage("Test: ${svc}") {
                            dir(svc) {
                                // Ép Maven dùng file POM đã được flatten
                                sh "mvn clean verify -Drevision=${VERSION} -DskipTests=false -U"
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