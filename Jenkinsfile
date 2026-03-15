pipeline {
    agent any
    
    stages {
        // --- STAGE 1: QUÉT BẢO MẬT TỔNG THỂ (Snyk chạy 1 lần ở đầu) ---
        stage('Global Security Scan') {
            steps {
                script {
                    echo '=== 1.1 Quét lộ mật khẩu (Gitleaks) ==='
                    docker.image('zricethezav/gitleaks:latest').inside('--entrypoint=""') {
                        sh 'gitleaks detect --source="." --no-git --verbose || true'
                    }

                    // echo '=== 1.2 Quét lỗ hổng thư viện (Snyk Full Project) ==='
                    // // Quét toàn bộ để lấy kết quả tổng quan trước khi đi vào từng service
                    // withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                    //     docker.image('snyk/snyk:maven').inside('--entrypoint=""') {
                    //         sh 'snyk test --all-projects --token=$SNYK_TOKEN --exclude=recommendation,backoffice,storefront || true'
                    //     }
                    // }
                }
            }
        }

        // --- STAGE 2: CHUẨN BỊ POM GỐC (Để fix lỗi ${revision}) ---
        stage('Prepare Root & Commons') {
            steps {
                sh 'find . -name "*.exec" -type f -delete || true'
                sh 'rm -rf */target || true'
                echo '=== Cài đặt cấu hình gốc và thư viện dùng chung ==='
                script {
                    docker.image('maven:3.9.6-eclipse-temurin-21').inside('-v /root/.m2:/root/.m2') {
                        // Quan trọng: Cài đặt tệp POM cha vào .m2 để các module con không bị lỗi dependency
                        sh 'mvn install -N -Drevision=1.0-SNAPSHOT'
                        sh 'mvn clean install -DskipTests -Drevision=1.0-SNAPSHOT -pl common-library -am'
                    }
                }
            }
        }

        // --- STAGE 3: QUY TRÌNH CI TUẦN TỰ ---
        stage('Business Services CI') {
            stages {
                // Chạy tuần tự từng service để tránh quá tải RAM và xung đột file
                stage('Service: Customer') {
                    when { changeset "customer/**" }
                    steps { runServiceCI('customer') }
                }
                stage('Service: Product') {
                    when { changeset "product/**" }
                    steps { runServiceCI('product') }
                }
                stage('Service: Cart') {
                    when { changeset "cart/**" }
                    steps { runServiceCI('cart') }
                }
                stage('Service: Order') {
                    when { changeset "order/**" }
                    steps { runServiceCI('order') }
                }
                stage('Service: Media') {
                    when { changeset "media/**" }
                    steps { runServiceCI('media') }
                }
                stage('Service: Rating') {
                    when { changeset "rating/**" }
                    steps { runServiceCI('rating') }
                }
                stage('Service: Location') {
                    when { changeset "location/**" }
                    steps { runServiceCI('location') }
                }
                stage('Service: Inventory') {
                    when { changeset "inventory/**" }
                    steps { runServiceCI('inventory') }
                }
                stage('Service: Tax') {
                    when { changeset "tax/**" }
                    steps { runServiceCI('tax') }
                }
                stage('Service: Search') {
                    when { changeset "search/**" }
                    steps { runServiceCI('search') }
                }
                stage('Service: Payment') {
                    when { changeset "payment/**" }
                    steps { runServiceCI('payment') }
                }
                stage('Service: Promotion') {
                    when { changeset "promotion/**" }
                    steps { runServiceCI('promotion') }
                }
                stage('Service: payment-paypal') {
                    when { changeset "payment-paypal/**" }
                    steps { runServiceCI('payment-paypal') }
                }
            }
        }
    }

    post {
        always {
            script {
                echo "=== BẮT ĐẦU DỌN DẸP TÀI NGUYÊN (RESET) ==="
                sh 'docker ps -aq --filter label=org.testcontainers=true | xargs -r docker rm -f || true'
                sh 'docker network prune -f || true'
                
                sh 'rm -rf common-library/target/classes || true'
                echo "=== Tổng hợp báo cáo JaCoCo toàn dự án ==="
                jacoco(
                    execPattern: "*/target/*.exec",      
                    classPattern: "*/target/classes",     
                    sourcePattern: "*/src/main/java",     
                    inclusionPattern: "**/*.class",
                    minimumInstructionCoverage: '70',     
                    maximumInstructionCoverage: '70',
                    buildOverBuild: false,
                    changeBuildStatus: true,
                    skipCopyOfSrcFiles: true 
                )
                
                echo "=== Quét SonarCloud tổng hợp cho TOÀN BỘ dự án ==="
                docker.image('maven:3.9.6-eclipse-temurin-21').inside('-v /root/.m2:/root/.m2') {
                    withCredentials([
                        string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN'),
                        string(credentialsId: 'sonar-organization', variable: 'SONAR_ORGANIZATION'),
                        string(credentialsId: 'sonar-project-key', variable: 'SONAR_PROJECT_KEY')
                    ]) {
                        sh """mvn compile sonar:sonar \
                        -Drevision=1.0-SNAPSHOT \
                        -Dsonar.token=\$SONAR_TOKEN \
                        -Dsonar.organization=\$SONAR_ORGANIZATION \
                        -Dsonar.projectKey=\$SONAR_PROJECT_KEY || true"""
                    }
                }
            }
        }
    }
}

// --- HÀM HỖ TRỢ XỬ LÝ TỪNG SERVICE ---
def runServiceCI(String serviceName) {
    script {
        docker.image('maven:3.9.6-eclipse-temurin-21').inside('-v /root/.m2:/root/.m2') {
            echo "=== Phase: Unit Test cho ${serviceName} ==="
            
            sh """mvn install \
            -Drevision=1.0-SNAPSHOT -pl ${serviceName} -am \
            -DskipITs=true"""
            // echo "=== Phase: Unit Test & Sonar Scan cho ${serviceName} ==="
            
            // withCredentials([
            //     string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN'),
            //     string(credentialsId: 'sonar-organization', variable: 'SONAR_ORGANIZATION'),
            //     string(credentialsId: 'sonar-project-key', variable: 'SONAR_PROJECT_KEY')
            // ]) {
            //     sh """mvn install sonar:sonar \
            //     -Drevision=1.0-SNAPSHOT -pl ${serviceName} -am \
            //     -DskipITs=true \
            //     -Dsonar.token=\$SONAR_TOKEN \
            //     -Dsonar.organization=\$SONAR_ORGANIZATION \
            //     -Dsonar.projectKey=\$SONAR_PROJECT_KEY"""
            // }
            
            // echo "=== Phase: Kiểm tra độ phủ Test > 70% (Yêu cầu 7b) ==="
            // jacoco(
            //     execPattern: "${serviceName}/target/*.exec",
            //     classPattern: "${serviceName}/target/classes",
            //     sourcePattern: "${serviceName}/src/main/java",
            //     inclusionPattern: "**/*.class",
            //     minimumInstructionCoverage: '70',
            //     maximumInstructionCoverage: '70',
                
            //     buildOverBuild: false,
            //     changeBuildStatus: true,
            //     skipCopyOfSrcFiles: true 
            // )
        }

        echo "=== Phase: Build Docker Image cho ${serviceName} ==="
        dir(serviceName) {
            sh "rm -f target/*-tests.jar target/*.jar.original || true"
            sh "docker build -t yas-${serviceName}:${BUILD_ID} ."
        }
    }
    publishTestResults(serviceName)
}

def publishTestResults(String serviceName) {
    dir(serviceName) {
        junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
    }
}

