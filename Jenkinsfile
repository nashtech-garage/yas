pipeline {
    agent any

    stages {
        // ==========================================
        // Cụm cấu hình cho MEDIA SERVICE
        // ==========================================
        stage('Media Service') {
            // YÊU CẦU 6: Chỉ kích hoạt cụm này khi có thay đổi trong thư mục 'media/'
            when { 
                changeset "media/**" 
            }
            stages {
                // YÊU CẦU 5: Phase 1 - Build
                stage('Build Media') {
                    steps {
                        echo "Phát hiện thay đổi ở Media Service. Bắt đầu Build..."
                        // Build Media và các thư viện xài chung (also-make), tạm lờ test (-DskipTests)
                        sh 'mvn --projects media --also-make clean package -DskipTests'
                    }
                }
                
                // YÊU CẦU 5: Phase 2 - Test
                stage('Test Media') {
                    steps {
                        echo "Bắt đầu Test cho Media Service..."
                        sh 'mvn --projects media test'
                    }
                    post {
                        always {
                            // YÊU CẦU 5: Upload Test Result và Độ phủ Testcase (Jacoco) riêng cho phần tử media
                            junit 'media/target/surefire-reports/*.xml'
                            jacoco execPattern: 'media/target/jacoco.exec'
                        }
                    }
                }
            }
        }

        // ==========================================
        // Cụm cấu hình cho PRODUCT SERVICE
        // ==========================================
        stage('Product Service') {
            when { 
                changeset "product/**" 
            }
            stages {
                stage('Build Product') {
                    steps {
                        echo "Phát hiện thay đổi ở Product Service. Bắt đầu Build..."
                        sh 'mvn --projects product --also-make clean package -DskipTests'
                    }
                }
                stage('Test Product') {
                    steps {
                        echo "Bắt đầu Test cho Product Service..."
                        sh 'mvn --projects product test'
                    }
                    post {
                        always {
                            junit 'product/target/surefire-reports/*.xml'
                            jacoco execPattern: 'product/target/jacoco.exec'
                        }
                    }
                }
            }
        }

        // ==========================================
        // CỤM 3: CART SERVICE
        // ==========================================
        stage('Cart Service') {
            // Yêu cầu 6: Chỉ chạy khi code trong thư mục cart bị thay đổi
            when { 
                changeset "cart/**" 
            }
            stages {
                stage('Security: Gitleaks Scan Cart') {
                    steps {
                        echo 'Đang quét Secret cho Cart Service...'
                        sh '''
                        # Tải và chạy Gitleaks để quét Secret
                        curl -sL https://github.com/gitleaks/gitleaks/releases/download/v8.18.2/gitleaks_8.18.2_linux_x64.tar.gz | tar -xz
                        chmod +x gitleaks
                        ./gitleaks detect --source cart/ -v || true
                        '''
                    }
                }

                stage('Build Cart') {
                    steps {
                        echo "Đang build Cart Service..."
                        sh 'mvn --projects cart --also-make clean install -DskipTests'
                    }
                }

                stage('Test Cart') {
                    steps {
                        echo "Đang chạy Test cho Cart Service..."
                        sh 'mvn --projects cart --also-make test'
                    }
                    post {
                        always {
                            echo "Đang lưu kết quả Test và Coverage của Cart..."
                            junit 'cart/target/surefire-reports/*.xml'
                            // Lưu báo cáo Jacoco HTML
                            archiveArtifacts artifacts: 'cart/target/site/jacoco/**', allowEmptyArchive: true
                        }
                    }
                }

                stage('Quality: SonarQube Scan Cart') {
                    steps {
                        echo 'Đang gửi báo cáo của Cart lên SonarQube...'
                        sh '''
                        mvn sonar:sonar \
                        -pl cart -am \
                        -Dsonar.projectKey=yas-cart \
                        -Dsonar.projectName="YAS Cart Service" \
                        -Dsonar.host.url=http://192.168.31.16:9000 \
                        -Dsonar.login=squ_e4b2aecfd410669cc972426e5a7b160c1760e2e5 \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        '''
                    }
                }

                stage('Security: Snyk Dependency Scan Cart') {
                    environment {
                        SNYK_TOKEN = credentials('snyk-token')
                    }
                    steps {
                        echo 'Đang quét lỗ hổng thư viện cho Cart bằng Snyk...'
                        sh '''
                        curl --compressed https://static.snyk.io/cli/latest/snyk-linux -o snyk
                        chmod +x ./snyk
                        ./snyk test --file=cart/pom.xml --token=$SNYK_TOKEN || true
                        '''
                    }
                }
            }
        }

        // Tương tự, nếu có service khác, nhóm bạn nhân bản cụm này ra đổi tên thành chữ tương ứng nhé.
    }
}
