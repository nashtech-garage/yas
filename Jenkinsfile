pipeline {
    // Gọi "anh thợ" Maven (đã cài sẵn Java JDK 17)
    agent {
        docker { 
            image 'maven:3.9.6-eclipse-temurin-21' 
            // Dòng args dưới đây giúp cache lại thư viện tải về để các lần build sau chạy nhanh như chớp
            args '-v $HOME/.m2:/root/.m2' 
        }
    }

    environment {
        CI = 'true'
    }

    stages {
        // ==========================================
        // CỤM 1: MEDIA SERVICE
        // ==========================================
        stage('Media Service') {
            // Yêu cầu 6: Chỉ chạy khi code trong thư mục media bị thay đổi
            when { 
                changeset "media/**" 
            }
            stages {
                stage('Security: Gitleaks Scan') {
                    steps {
                        echo 'Đang tải và chạy Gitleaks để quét Secret...'
                        sh '''
                        # Dùng curl để tải công cụ Gitleaks trực tiếp từ GitHub về và giải nén
                        curl -sL https://github.com/gitleaks/gitleaks/releases/download/v8.18.2/gitleaks_8.18.2_linux_x64.tar.gz | tar -xz
                        
                        # Cấp quyền thực thi cho file vừa tải
                        chmod +x gitleaks
                        
                        # Chạy Gitleaks để quét toàn bộ thư mục hiện tại (bao gồm cả lịch sử git)
                        ./gitleaks detect --source . -v || true
                        '''
                    }
                }

                stage('Build Media') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Media Service..."
                        // Không cần dùng dir() vì đã có Parent POM
                        sh 'mvn --projects media --also-make clean install -DskipTests'
                    }
                }
                
                stage('Test Media') {
                    steps {
                        echo "Đang chạy Test cho Media Service..."
                        sh 'mvn --projects media --also-make test'
                    }
                    post {
                        always {
                            echo "Đang lưu kết quả Test và Coverage của Media..."
                            // Đọc file kết quả test (có sẵn trên Jenkins)
                            junit 'media/target/surefire-reports/*.xml'
                            
                            // Cách 1: Nén HTML Report của Jacoco để xem trực tiếp (không cần cài thêm plugin)
                            archiveArtifacts artifacts: 'media/target/site/jacoco/**', allowEmptyArchive: true
                            
                            // Cách 2: Hoặc dùng plugin Jacoco (nếu máy Jenkins của bạn đã cài plugin Jacoco)
                            // jacoco execPattern: 'media/target/jacoco.exec'
                        }
                    }
                }

                stage('Quality: SonarQube Scan Media') {
                    steps {
                        echo 'Đang gửi code và báo cáo Test của Media lên SonarQube...'
                        sh '''
                        mvn sonar:sonar \
                        -pl media -am \
                        -Dsonar.projectKey=yas-media \
                        -Dsonar.projectName="YAS Media Service" \
                        -Dsonar.host.url=http://192.168.31.16:9000 \
                        -Dsonar.login=squ_e4b2aecfd410669cc972426e5a7b160c1760e2e5 \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        '''
                    }
                }

                stage('Security: Snyk Dependency Scan') {
                    environment {
                        SNYK_TOKEN = credentials('snyk-token')
                    }
                    steps {
                        echo 'Đang kiểm tra lỗ hổng thư viện với Snyk...'
                        sh '''
                        # Tải Snyk CLI bản cho Linux
                        curl --compressed https://static.snyk.io/cli/latest/snyk-linux -o snyk
                        chmod +x ./snyk
                        
                        # Thực hiện quét lỗ hổng trong các file pom.xml
                        # Thêm "|| true" để pipeline không bị fail nếu thư viện có lỗi (phục vụ mục đích lấy báo cáo)
                        ./snyk test --all-projects --token=$SNYK_TOKEN || true
                        '''
                    }
                }
            }
        }



        // ==========================================
        // CỤM 2: PRODUCT SERVICE
        // ==========================================
        stage('Product Service') {
            when { 
                changeset "product/**" 
            }
            stages {
                stage('Build Product') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Product Service..."
                        sh 'mvn --projects product --also-make clean install -DskipTests'
                    }
                }
                stage('Test Product') {
                    steps {
                        echo "Đang chạy Test cho Product Service..."
                        sh 'mvn --projects product --also-make test'
                    }
                    post {
                        always {
                            junit 'product/target/surefire-reports/*.xml'
                            archiveArtifacts artifacts: 'product/target/site/jacoco/**', allowEmptyArchive: true
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
                stage('Security: Gitleaks Scan') {
                    steps {
                        echo 'Đang tải và chạy Gitleaks để quét Secret...'
                        sh '''
                        # Dùng curl để tải công cụ Gitleaks trực tiếp từ GitHub về và giải nén
                        curl -sL https://github.com/gitleaks/gitleaks/releases/download/v8.18.2/gitleaks_8.18.2_linux_x64.tar.gz | tar -xz
                        
                        # Cấp quyền thực thi cho file vừa tải
                        chmod +x gitleaks
                        
                        # Chạy Gitleaks để quét toàn bộ thư mục hiện tại (bao gồm cả lịch sử git)
                        ./gitleaks detect --source . -v || true
                        '''
                    }
                }

                stage('Build Cart') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Cart Service..."
                        // Không cần dùng dir() vì đã có Parent POM
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
                            // Đọc file kết quả test (có sẵn trên Jenkins)
                            junit 'cart/target/surefire-reports/*.xml'
                            
                            // Cách 1: Nén HTML Report của Jacoco để xem trực tiếp (không cần cài thêm plugin)
                            archiveArtifacts artifacts: 'cart/target/site/jacoco/**', allowEmptyArchive: true
                            
                            // Cách 2: Hoặc dùng plugin Jacoco (nếu máy Jenkins của bạn đã cài plugin Jacoco)
                            // jacoco execPattern: 'cart/target/jacoco.exec'
                        }
                    }
                }

                stage('Quality: SonarQube Scan Cart') {
                    steps {
                        echo 'Đang gửi code và báo cáo Test của Cart lên SonarQube...'
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

                stage('Security: Snyk Dependency Scan') {
                    environment {
                        SNYK_TOKEN = credentials('snyk-token')
                    }
                    steps {
                        echo 'Đang kiểm tra lỗ hổng thư viện với Snyk...'
                        sh '''
                        # Tải Snyk CLI bản cho Linux
                        curl --compressed https://static.snyk.io/cli/latest/snyk-linux -o snyk
                        chmod +x ./snyk
                        
                        # Thực hiện quét lỗ hổng trong các file pom.xml
                        ./snyk test --all-projects --token=$SNYK_TOKEN || true
                        '''
                    }
                }
            }
        }



        // ==========================================
        // CỤM 4: CUSTOMER SERVICE
        // ==========================================
        stage('Customer Service') {
            // Yêu cầu 6: Chỉ chạy khi code trong thư mục customer bị thay đổi
            when { 
                changeset "customer/**" 
            }
            stages {
                stage('Security: Gitleaks Scan') {
                    steps {
                        echo 'Đang tải và chạy Gitleaks để quét Secret...'
                        sh '''
                        # Dùng curl để tải công cụ Gitleaks trực tiếp từ GitHub về và giải nén
                        curl -sL https://github.com/gitleaks/gitleaks/releases/download/v8.18.2/gitleaks_8.18.2_linux_x64.tar.gz | tar -xz
                        
                        # Cấp quyền thực thi cho file vừa tải
                        chmod +x gitleaks
                        
                        # Chạy Gitleaks để quét toàn bộ thư mục hiện tại (bao gồm cả lịch sử git)
                        ./gitleaks detect --source . -v || true
                        '''
                    }
                }

                stage('Build Customer') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Customer Service..."
                        // Không cần dùng dir() vì đã có Parent POM
                        sh 'mvn --projects customer --also-make clean install -DskipTests'
                    }
                }
                
                stage('Test Customer') {
                    steps {
                        echo "Đang chạy Test cho Customer Service..."
                        sh 'mvn --projects customer --also-make test'
                    }
                    post {
                        always {
                            echo "Đang lưu kết quả Test và Coverage của Customer..."
                            // Đọc file kết quả test (có sẵn trên Jenkins)
                            junit 'customer/target/surefire-reports/*.xml'
                            
                            // Cách 1: Nén HTML Report của Jacoco để xem trực tiếp (không cần cài thêm plugin)
                            archiveArtifacts artifacts: 'customer/target/site/jacoco/**', allowEmptyArchive: true
                            
                            // Cách 2: Hoặc dùng plugin Jacoco (nếu máy Jenkins của bạn đã cài plugin Jacoco)
                            // jacoco execPattern: 'customer/target/jacoco.exec'
                        }
                    }
                }

                stage('Quality: SonarQube Scan Customer') {
                    steps {
                        echo 'Đang gửi code và báo cáo Test của Customer lên SonarQube...'
                        sh '''
                        mvn sonar:sonar \
                        -pl customer -am \
                        -Dsonar.projectKey=yas-customer \
                        -Dsonar.projectName="YAS Customer Service" \
                        -Dsonar.host.url=http://192.168.31.16:9000 \
                        -Dsonar.login=squ_e4b2aecfd410669cc972426e5a7b160c1760e2e5 \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        '''
                    }
                }

                stage('Security: Snyk Dependency Scan') {
                    environment {
                        SNYK_TOKEN = credentials('snyk-token')
                    }
                    steps {
                        echo 'Đang kiểm tra lỗ hổng thư viện với Snyk...'
                        sh '''
                        # Tải Snyk CLI bản cho Linux
                        curl --compressed https://static.snyk.io/cli/latest/snyk-linux -o snyk
                        chmod +x ./snyk
                        
                        # Thực hiện quét lỗ hổng trong các file pom.xml
                        # Thêm "|| true" để pipeline không bị fail nếu thư viện có lỗi (phục vụ mục đích lấy báo cáo)
                        ./snyk test --all-projects --token=$SNYK_TOKEN || true
                        '''
                    }
                }
            }
        }
    }
}