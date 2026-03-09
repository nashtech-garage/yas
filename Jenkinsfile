pipeline {
    agent any
    
    tools {
        maven 'maven'  // Tên Maven đã config trong Global Tool Configuration
        jdk 'JDK21'    // Tên JDK đã config
    }
    
    environment {
        // Cập nhật danh sách các services của dự án YAS
        SERVICES = 'product,cart,media'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo "=== Checking out code from GitHub ==="
                checkout scm
            }
        }
        
        stage('Detect Changes') {
            steps {
                script {
                    echo "=== Detecting which services changed ==="
                    
                    // Lấy danh sách file thay đổi
                    def changes = ""
                    try {
                        changes = sh(
                            script: 'git diff --name-only HEAD~1 HEAD || git diff --name-only HEAD',
                            returnStdout: true
                        ).trim()
                    } catch (Exception e) {
                        // Nếu là commit đầu tiên hoặc có lỗi, build tất cả
                        changes = "all"
                    }
                    
                    echo "Changed files:\n${changes}"
                    
                    // Set biến môi trường cho 3 service của bạn
                    // Lưu ý: Đảm bảo 'product/', 'cart/', 'media/' khớp với tên thư mục trên GitHub
                    env.BUILD_PRODUCT = changes.contains('product/') || changes == 'all' ? 'true' : 'false'
                    env.BUILD_CART    = changes.contains('cart/')    || changes == 'all' ? 'true' : 'false'
                    env.BUILD_MEDIA   = changes.contains('media/')   || changes == 'all' ? 'true' : 'false'
                    
                    echo "Build Product: ${env.BUILD_PRODUCT}"
                    echo "Build Cart: ${env.BUILD_CART}"
                    echo "Build Media: ${env.BUILD_MEDIA}"
                }
            }
        }
        
        // ========== PRODUCT SERVICE ==========
        stage('Product Service Pipeline') {
            when {
                environment name: 'BUILD_PRODUCT', value: 'true'
            }
            stages {
                stage('Product: Test') {
                    steps {
                        dir('product') {
                            echo "=== Running tests for Product Service ==="
                            sh 'mvn clean test'
                        }
                    }
                }
                
                stage('Product: Coverage Report') {
                    steps {
                        dir('product') {
                            echo "=== Generating coverage report ==="
                            sh 'mvn jacoco:report'
                        }
                    }
                }
                
                stage('Product: Build') {
                    steps {
                        dir('product') {
                            echo "=== Building Product Service ==="
                            sh 'mvn package -DskipTests'
                        }
                    }
                }
            }
            post {
                always {
                    // Upload test results
                    junit allowEmptyResults: true, testResults: '**/product/target/surefire-reports/*.xml'
                    
                    // Upload coverage
                    jacoco(
                        execPattern: '**/product/target/jacoco.exec',
                        classPattern: '**/product/target/classes',
                        sourcePattern: '**/product/src/main/java',
                        exclusionPattern: '**/*Test*.class'
                    )
                }
            }
        }
        
        // ========== CART SERVICE ==========
        stage('Cart Service Pipeline') {
            when {
                environment name: 'BUILD_CART', value: 'true'
            }
            stages {
                stage('Cart: Test') {
                    steps {
                        dir('cart') {
                            echo "=== Running tests for Cart Service ==="
                            sh 'mvn clean test'
                        }
                    }
                }
                
                stage('Cart: Coverage Report') {
                    steps {
                        dir('cart') {
                            echo "=== Generating coverage report ==="
                            sh 'mvn jacoco:report'
                        }
                    }
                }
                
                stage('Cart: Build') {
                    steps {
                        dir('cart') {
                            echo "=== Building Cart Service ==="
                            sh 'mvn package -DskipTests'
                        }
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/cart/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/cart/target/jacoco.exec',
                        classPattern: '**/cart/target/classes',
                        sourcePattern: '**/cart/src/main/java',
                        exclusionPattern: '**/*Test*.class'
                    )
                }
            }
        }
        
        // ========== MEDIA SERVICE ==========
        stage('Media Service Pipeline') {
            when {
                environment name: 'BUILD_MEDIA', value: 'true'
            }
            stages {
                stage('Media: Test') {
                    steps {
                        dir('media') {
                            echo "=== Running tests for Media Service ==="
                            sh 'mvn clean test'
                        }
                    }
                }
                
                stage('Media: Coverage Report') {
                    steps {
                        dir('media') {
                            echo "=== Generating coverage report ==="
                            sh 'mvn jacoco:report'
                        }
                    }
                }
                
                stage('Media: Build') {
                    steps {
                        dir('media') {
                            echo "=== Building Media Service ==="
                            sh 'mvn package -DskipTests'
                        }
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/media/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/media/target/jacoco.exec',
                        classPattern: '**/media/target/classes',
                        sourcePattern: '**/media/src/main/java',
                        exclusionPattern: '**/*Test*.class'
                    )
                }
            }
        }
    }
    
    post {
        always {
            echo "=== Pipeline completed ==="
            cleanWs()  // Clean workspace
        }
        success {
            echo "✅ Build SUCCESS - All tests passed!"
        }
        failure {
            echo "❌ Build FAILED - Check logs above"
        }
    }
}
