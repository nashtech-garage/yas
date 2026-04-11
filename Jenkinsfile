pipeline {
    agent any

    tools {
        maven 'maven3'
    }

    environment {
        // Cố định đường dẫn JDK 25 mà Jenkins đã tải về
        JAVA_HOME = "/var/jenkins_home/tools/hudson.model.JDK/jdk25/jdk-25"
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
                    echo "Services detected: ${env.CHANGED_SERVICES}"
                }
            }
        }

        stage('Phase 2: Unit Test & Coverage') {
            when { expression { return env.CHANGED_SERVICES != "" } }
            steps {
                script {
                    // 1. Phải install common-library trước để các service khác thấy
                    echo "--- Installing Common Library ---"
                    dir('common-library') {
                        sh 'mvn clean install -DskipTests'
                    }

                    // 2. Chạy Test và Coverage cho các service thay đổi
                    def list = env.CHANGED_SERVICES.split(",")
                    for (svc in list) {
                        if (svc == 'common-library') continue
                        stage("Test & Coverage: ${svc}") {
                            dir(svc) {
                                echo "--- Running Tests for ${svc} ---"
                                // Lệnh verify sẽ kích hoạt jacoco:report trong pom.xml của Sỹ
                                sh 'mvn clean verify'
                            }
                        }
                    }
                }
            }
        }

        stage('Phase 3: Package') {
            when { expression { return env.CHANGED_SERVICES != "" } }
            steps {
                script {
                    def list = env.CHANGED_SERVICES.split(",")
                    for (svc in list) {
                        if (svc == 'common-library') continue
                        dir(svc) {
                            echo "--- Packaging ${svc} ---"
                            sh 'mvn package -DskipTests'
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo "--- Collecting All Reports ---"
            // Hiển thị kết quả Test Case
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            
            // Hiển thị biểu đồ độ phủ Code Coverage (Yêu cầu 7b)
            jacoco (
                execPattern: '**/target/*.exec',
                classPattern: '**/target/classes',
                sourcePattern: '**/src/main/java',
                inclusionPattern: '**/*.class'
            )
        }
    }
}