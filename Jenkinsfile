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
        // CỤM 4: PAYMENT PAYPAL SERVICE
        // ==========================================
        stage('Payment Paypal Service') {
            // Yêu cầu 6: Chỉ chạy khi code trong thư mục    bị thay đổi
            when { 
                changeset "payment-paypal/**" 
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

                stage('Build Payment Paypal') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Payment Paypal Service..."
                        // Không cần dùng dir() vì đã có Parent POM
                        sh 'mvn --projects payment-paypal --also-make clean install -DskipTests'
                    }
                }
                
                stage('Test Payment Paypal') {
                    steps {
                        echo "Đang chạy Test cho Payment Paypal Service..."
                        sh 'mvn --projects payment-paypal --also-make test'
                    }
                    post {
                        always {
                            echo "Đang lưu kết quả Test và Coverage của Payment Paypal..."
                            // Đọc file kết quả test (có sẵn trên Jenkins)
                            junit 'payment-paypal/target/surefire-reports/*.xml'
                            
                            // Cách 1: Nén HTML Report của Jacoco để xem trực tiếp (không cần cài thêm plugin)
                            archiveArtifacts artifacts: 'payment-paypal/target/site/jacoco/**', allowEmptyArchive: true
                            
                            // Cách 2: Hoặc dùng plugin Jacoco (nếu máy Jenkins của bạn đã cài plugin Jacoco)
                            // jacoco execPattern: 'payment-paypal/target/jacoco.exec'
                        }
                    }
                }

                stage('Quality: SonarQube Scan Payment Paypal') {
                    steps {
                        echo 'Đang gửi code và báo cáo Test của Payment Paypal lên SonarQube...'
                        sh '''
                        mvn sonar:sonar \
                        -pl payment-paypal -am \
                        -Dsonar.projectKey=yas-payment-paypal \
                        -Dsonar.projectName="YAS Payment Paypal Service" \
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



        // ==========================================
        // CỤM 5: DELIVERY SERVICE
        // ==========================================
        stage('Delivery Service') {
            // Yêu cầu 6: Chỉ chạy khi code trong thư mục delivery bị thay đổi
            when { 
                changeset "delivery/**" 
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

                stage('Build Delivery') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Delivery Service..."
                        // Không cần dùng dir() vì đã có Parent POM
                        sh 'mvn --projects delivery --also-make clean install -DskipTests'
                    }
                }
                
                stage('Test Delivery') {
                    steps {
                        echo "Đang chạy Test cho Delivery Service..."
                        sh 'mvn --projects delivery --also-make test'
                    }
                    post {
                        always {
                            echo "Đang lưu kết quả Test và Coverage của Delivery..."
                            // Đọc file kết quả test (có sẵn trên Jenkins)
                            junit 'delivery/target/surefire-reports/*.xml'
                            
                            // Cách 1: Nén HTML Report của Jacoco để xem trực tiếp (không cần cài thêm plugin)
                            archiveArtifacts artifacts: 'delivery/target/site/jacoco/**', allowEmptyArchive: true
                            
                            // Cách 2: Hoặc dùng plugin Jacoco (nếu máy Jenkins của bạn đã cài plugin Jacoco)
                            // jacoco execPattern: 'delivery/target/jacoco.exec'
                        }
                    }
                }

                stage('Quality: SonarQube Scan Delivery') {
                    steps {
                        echo 'Đang gửi code và báo cáo Test của Delivery lên SonarQube...'
                        sh '''
                        mvn sonar:sonar \
                        -pl delivery -am \
                        -Dsonar.projectKey=yas-delivery \
                        -Dsonar.projectName="YAS Delivery Service" \
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
        // CỤM 6: INVENTORY SERVICE
        // ==========================================
        stage('Inventory Service') {
            // Yêu cầu 6: Chỉ chạy khi code trong thư mục inventory bị thay đổi
            when { 
                changeset "inventory/**" 
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

                stage('Build Inventory') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Inventory Service..."
                        // Không cần dùng dir() vì đã có Parent POM
                        sh 'mvn --projects inventory --also-make clean install -DskipTests'
                    }
                }
                
                stage('Test Inventory') {
                    steps {
                        echo "Đang chạy Test cho Inventory Service..."
                        sh 'mvn --projects inventory --also-make test'
                    }
                    post {
                        always {
                            echo "Đang lưu kết quả Test và Coverage của Inventory..."
                            // Đọc file kết quả test (có sẵn trên Jenkins)
                            junit 'inventory/target/surefire-reports/*.xml'
                            
                            // Cách 1: Nén HTML Report của Jacoco để xem trực tiếp (không cần cài thêm plugin)
                            archiveArtifacts artifacts: 'inventory/target/site/jacoco/**', allowEmptyArchive: true
                            
                            // Cách 2: Hoặc dùng plugin Jacoco (nếu máy Jenkins của bạn đã cài plugin Jacoco)
                            // jacoco execPattern: 'inventory/target/jacoco.exec'
                        }
                    }
                }

                stage('Quality: SonarQube Scan Inventory') {
                    steps {
                        echo 'Đang gửi code và báo cáo Test của Inventory lên SonarQube...'
                        sh '''
                        mvn sonar:sonar \
                        -pl inventory -am \
                        -Dsonar.projectKey=yas-inventory \
                        -Dsonar.projectName="YAS Inventory Service" \
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
        // CỤM 7: RATING SERVICE
        // ==========================================
        stage('Rating Service') {
            // Yêu cầu 6: Chỉ chạy khi code trong thư mục rating bị thay đổi
            when { 
                changeset "rating/**" 
            }
            stages {
                stage('Security: Gitleaks Scan') {
                    steps {
                        echo 'Đang tải và chạy Gitleaks để quét Secret...'
                        sh '''
                        curl -sL https://github.com/gitleaks/gitleaks/releases/download/v8.18.2/gitleaks_8.18.2_linux_x64.tar.gz | tar -xz
                        chmod +x gitleaks
                        ./gitleaks detect --source . -v || true
                        '''
                    }
                }

                stage('Build Rating') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Rating Service..."
                        sh 'mvn --projects rating --also-make clean install -DskipTests'
                    }
                }
                
                stage('Test Rating') {
                    steps {
                        echo "Đang chạy Test cho Rating Service..."
                        sh 'mvn --projects rating --also-make test'
                    }
                    post {
                        always {
                            echo "Đang lưu kết quả Test và Coverage của Rating..."
                            junit 'rating/target/surefire-reports/*.xml'
                            archiveArtifacts artifacts: 'rating/target/site/jacoco/**', allowEmptyArchive: true
                        }
                    }
                }

                stage('Quality: SonarQube Scan Rating') {
                    steps {
                        echo 'Đang gửi code và báo cáo Test của Rating lên SonarQube...'
                        sh '''
                        mvn sonar:sonar \
                        -pl rating -am \
                        -Dsonar.projectKey=yas-rating \
                        -Dsonar.projectName="YAS Rating Service" \
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
                        curl --compressed https://static.snyk.io/cli/latest/snyk-linux -o snyk
                        chmod +x ./snyk
                        ./snyk test --all-projects --token=$SNYK_TOKEN || true
                        '''
                    }
                }
            }
        }
        // ==========================================
        // CỤM 8: WEBHOOK SERVICE
        // ==========================================
        stage('Webhook Service') {
            // Yêu cầu 6: Chỉ chạy khi code trong thư mục webhook bị thay đổi
            when { 
                changeset "webhook/**" 
            }
            stages {
                stage('Security: Gitleaks Scan') {
                    steps {
                        echo 'Đang tải và chạy Gitleaks để quét Secret...'
                        sh '''
                        curl -sL https://github.com/gitleaks/gitleaks/releases/download/v8.18.2/gitleaks_8.18.2_linux_x64.tar.gz | tar -xz
                        chmod +x gitleaks
                        ./gitleaks detect --source . -v || true
                        '''
                    }
                }

                stage('Build Webhook') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Webhook Service..."
                        sh 'mvn --projects webhook --also-make clean install -DskipTests'
                    }
                }
                
                stage('Test Webhook') {
                    steps {
                        echo "Đang chạy Test cho Webhook Service..."
                        sh 'mvn --projects webhook --also-make test'
                    }
                    post {
                        always {
                            echo "Đang lưu kết quả Test và Coverage của Webhook..."
                            junit 'webhook/target/surefire-reports/*.xml'
                            archiveArtifacts artifacts: 'webhook/target/site/jacoco/**', allowEmptyArchive: true
                        }
                    }
                }

                stage('Quality: SonarQube Scan Webhook') {
                    steps {
                        echo 'Đang gửi code và báo cáo Test của Webhook lên SonarQube...'
                        sh '''
                        mvn sonar:sonar \
                        -pl webhook -am \
                        -Dsonar.projectKey=yas-webhook \
                        -Dsonar.projectName="YAS Webhook Service" \
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
                        curl --compressed https://static.snyk.io/cli/latest/snyk-linux -o snyk
                        chmod +x ./snyk
                        ./snyk test --all-projects --token=$SNYK_TOKEN || true
                        '''
                    }
                }
            }
        }



        // ==========================================
        // CỤM 9: LOCATION SERVICE
        // ==========================================
        stage('Location Service') {
            when { 
                changeset "location/**" 
            }
            stages {
                stage('Security: Gitleaks Scan') {
                    steps {
                        echo 'Đang tải và chạy Gitleaks để quét Secret...'
                        sh '''
                        curl -sL https://github.com/gitleaks/gitleaks/releases/download/v8.18.2/gitleaks_8.18.2_linux_x64.tar.gz | tar -xz
                        chmod +x gitleaks
                        ./gitleaks detect --source . -v || true
                        '''
                    }
                }

                stage('Build Location') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Location Service..."
                        sh 'mvn --projects location --also-make clean install -DskipTests'
                    }
                }
                
                stage('Test Location') {
                    steps {
                        echo "Đang chạy Test cho Location Service..."
                        sh 'mvn --projects location --also-make test'
                    }
                    post {
                        always {
                            echo "Đang lưu kết quả Test và Coverage của Location..."
                            junit 'location/target/surefire-reports/*.xml'
                            archiveArtifacts artifacts: 'location/target/site/jacoco/**', allowEmptyArchive: true
                        }
                    }
                }

                stage('Quality: SonarQube Scan Location') {
                    steps {
                        echo 'Đang gửi code và báo cáo Test của Location lên SonarQube...'
                        sh '''
                        mvn sonar:sonar \
                        -pl location -am \
                        -Dsonar.projectKey=yas-location \
                        -Dsonar.projectName="YAS Location Service" \
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
                        curl --compressed https://static.snyk.io/cli/latest/snyk-linux -o snyk
                        chmod +x ./snyk
                        ./snyk test --all-projects --token=$SNYK_TOKEN || true
                        '''
                    }
                }
            }
        }



        // ==========================================
        // CỤM 10: ORDER SERVICE
        // ==========================================
        stage('Order Service') {
            when { 
                changeset "order/**" 
            }
            stages {
                stage('Security: Gitleaks Scan') {
                    steps {
                        echo 'Đang tải và chạy Gitleaks cho Order...'
                        sh '''
                        curl -sL https://github.com/gitleaks/gitleaks/releases/download/v8.18.2/gitleaks_8.18.2_linux_x64.tar.gz | tar -xz
                        chmod +x gitleaks
                        ./gitleaks detect --source . -v || true
                        '''
                    }
                }

                stage('Build Order') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Order Service..."
                        sh 'mvn --projects order --also-make clean install -DskipTests'
                    }
                }
                
                stage('Test Order') {
                    steps {
                        echo "Đang chạy Test cho Order Service..."
                        sh 'mvn --projects order --also-make test'
                    }
                    post {
                        always {
                            echo "Đang lưu kết quả Test cho Order..."
                            junit 'order/target/surefire-reports/*.xml'
                            archiveArtifacts artifacts: 'order/target/site/jacoco/**', allowEmptyArchive: true
                        }
                    }
                }

                stage('Quality: SonarQube Scan Order') {
                    steps {
                        echo 'Đang gửi báo cáo của Order lên SonarQube...'
                        sh '''
                        mvn sonar:sonar \
                        -pl order -am \
                        -Dsonar.projectKey=yas-order \
                        -Dsonar.projectName="YAS Order Service" \
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
                        echo 'Đang kiểm tra lỗ hổng thư viện cho Order...'
                        sh '''
                        curl --compressed https://static.snyk.io/cli/latest/snyk-linux -o snyk
                        chmod +x ./snyk
                        ./snyk test --all-projects --token=$SNYK_TOKEN || true
                        '''
                    }
                }
            }
        }
    }
}