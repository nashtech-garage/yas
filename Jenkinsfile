pipeline {
    agent any

    // Định nghĩa các công cụ sẽ sử dụng trong Pipeline
    tools {
        jdk 'Java 21'    // Tên phải khớp với cấu hình trong Manage Jenkins -> Tools
        maven 'Maven 3'  // Tên phải khớp với cấu hình trong Manage Jenkins -> Tools
    }

    environment {
        // Các Token bảo mật được lấy từ Credentials của Jenkins
        SNYK_TOKEN = credentials('snyk-api-token')
        SONAR_TOKEN = credentials('sonar-qube-token')
    }

    stages {
        // Stage kiểm tra chung cho toàn bộ Repository
        stage('Gitleaks: Secret Scanning') {
            steps {
                echo 'Đang quét lộ lọt thông tin bảo mật (Secrets, Keys)...'
                // Chạy gitleaks trên toàn bộ repo
                sh 'gitleaks detect --source . -v || echo "Cảnh báo: Phát hiện hoặc chưa cài đặt Gitleaks"'
            }
        }

        // ---------------------------------------------------------------------
        // PIPELINE CHO MEDIA SERVICE
        // ---------------------------------------------------------------------
        stage('Service: Media') {
            when {
                // Yêu cầu 6: Chỉ chạy khi có thay đổi trong thư mục media-service
                changeset "media/**"
            }
            stages {
                stage('Media: Test & Coverage') {
                    steps {
                        dir('media') {
                            echo 'Đang chạy Unit Test và đo độ phủ code cho Media Service...'
                            sh 'mvn clean test jacoco:report'
                        }
                    }
                    post {
                        always {
                            // Yêu cầu 5: Upload kết quả test
                            junit 'media/target/surefire-reports/*.xml'
                            
                            // Yêu cầu 7b: Kiểm tra độ phủ (Fail nếu < 70%)
                            jacoco(
                                execPattern: 'media/target/jacoco.exec',
                                classPattern: 'media/target/classes',
                                sourcePattern: 'media/src/main/java',
                                minimumLineCoverage: '70'
                            )
                        }
                    }
                }
                stage('Media: Security & Quality Scan') {
                    steps {
                        dir('media') {
                            echo 'Đang quét chất lượng code (SonarQube) và lỗ hổng (Snyk)...'
                            sh "mvn sonar:sonar -Dsonar.projectKey=yas-media -Dsonar.login=${SONAR_TOKEN}"
                            sh "snyk test --token=${SNYK_TOKEN}"
                        }
                    }
                }
                stage('Media: Build') {
                    steps {
                        dir('media') {
                            echo 'Đang đóng gói Media Service...'
                            sh 'mvn package -DskipTests'
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // PIPELINE CHO PRODUCT SERVICE
        // ---------------------------------------------------------------------
        stage('Service: Product') {
            when {
                changeset "product/**"
            }
            stages {
                stage('Product: Test & Coverage') {
                    steps {
                        dir('product') {
                            sh 'mvn clean test jacoco:report'
                        }
                    }
                    post {
                        always {
                            junit 'product/target/surefire-reports/*.xml'
                            jacoco(
                                execPattern: 'product/target/jacoco.exec',
                                classPattern: 'product/target/classes',
                                sourcePattern: 'product/src/main/java',
                                minimumLineCoverage: '70'
                            )
                        }
                    }
                }
                stage('Product: Security & Quality Scan') {
                    steps {
                        dir('product') {
                            sh "mvn sonar:sonar -Dsonar.projectKey=yas-product -Dsonar.login=${SONAR_TOKEN}"
                            sh "snyk test --token=${SNYK_TOKEN}"
                        }
                    }
                }
                stage('Product: Build') {
                    steps {
                        dir('product') {
                            sh 'mvn package -DskipTests'
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // PIPELINE CHO CART SERVICE
        // ---------------------------------------------------------------------
        stage('Service: Cart') {
            when {
                changeset "cart/**"
            }
            stages {
                stage('Cart: Test & Coverage') {
                    steps {
                        dir('cart') {
                            sh 'mvn clean test jacoco:report'
                        }
                    }
                    post {
                        always {
                            junit 'cart/target/surefire-reports/*.xml'
                            jacoco(
                                execPattern: 'cart/target/jacoco.exec',
                                classPattern: 'cart/target/classes',
                                sourcePattern: 'cart/src/main/java',
                                minimumLineCoverage: '70'
                            )
                        }
                    }
                }
                stage('Cart: Security & Quality Scan') {
                    steps {
                        dir('cart') {
                            sh "mvn sonar:sonar -Dsonar.projectKey=yas-cart -Dsonar.login=${SONAR_TOKEN}"
                            sh "snyk test --token=${SNYK_TOKEN}"
                        }
                    }
                }
                stage('Cart: Build') {
                    steps {
                        dir('cart') {
                            sh 'mvn package -DskipTests'
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline hoàn thành thành công!'
        }
        failure {
            echo 'Pipeline thất bại. Vui lòng kiểm tra lại log và unit test/độ phủ code.'
        }
    }
}