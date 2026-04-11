pipeline {
    agent any

    tools {
        maven 'maven3'
    }

    environment {
        // ĐƯỜNG DẪN VỪA TÌM THẤY - CHUẨN 100%
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
                    echo "Services to build: ${env.CHANGED_SERVICES}"
                }
            }
        }

        stage('Phase 2: Unit Test & Coverage') {
            when { expression { return env.CHANGED_SERVICES != "" } }
            steps {
                script {
                    // 1. Cài đặt file POM gốc (Parent) - BƯỚC MỚI CỰC KỲ QUAN TRỌNG
                    echo "--- Installing Parent POM ---"
                    // Lệnh này cài cái file pom.xml ngay thư mục root vào kho
                    sh 'mvn install -N -DskipTests' 

                    // 2. Install common-library (như cũ)
                    echo "--- Installing Common Library ---"
                    dir('common-library') {
                        sh 'mvn clean install -DskipTests'
                    }

                    // 3. Chạy Test cho service (Thêm flag -U để nó ép cập nhật library mới cài)
                    def list = env.CHANGED_SERVICES.split(",")
                    for (svc in list) {
                        if (svc == 'common-library') continue
                        stage("Test & Coverage: ${svc}") {
                            dir(svc) {
                                echo "--- Running Tests for ${svc} ---"
                                sh 'mvn clean verify -U -DskipTests=false'
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
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            jacoco (
                execPattern: '**/target/*.exec',
                classPattern: '**/target/classes',
                sourcePattern: '**/src/main/java',
                inclusionPattern: '**/*.class'
            )
        }
    }
}