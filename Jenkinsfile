pipeline {
    agent any
    
    tools {
        maven 'maven'
        jdk 'JDK21'
    }
    
    environment {
        SERVICES = 'product,cart,media'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo "=== Checking out code from GitHub ==="
                checkout scm
            }
        }
        
        stage('Build Common Dependencies') {
            steps {
                echo "=== Building common-library ==="
                sh 'mvn clean install -pl common-library -am -DskipTests'
            }
        }
        
        stage('Detect Changes') {
            steps {
                script {
                    echo "=== Detecting which services changed ==="
                    
                    def changes = ""
                    try {
                        changes = sh(
                            script: 'git diff --name-only HEAD~1 HEAD || echo "all"',
                            returnStdout: true
                        ).trim()
                    } catch (Exception e) {
                        changes = "all"
                    }
                    
                    echo "Changed files:\n${changes}"
                    
                    env.BUILD_PRODUCT = (changes.contains('product/') || changes == 'all') ? 'true' : 'false'
                    env.BUILD_CART = (changes.contains('cart/') || changes == 'all') ? 'true' : 'false'
                    env.BUILD_MEDIA = (changes.contains('media/') || changes == 'all') ? 'true' : 'false'
                    
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
                            sh 'mvn test'
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
                    
                    // Upload coverage - SỬ DỤNG COVERAGE PLUGIN
                    publishCoverage adapters: [
                        jacocoAdapter('**/product/target/site/jacoco/jacoco.xml')
                    ]
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
                            sh 'mvn test'
                        }
                    }
                }
                
                stage('Cart: Coverage Report') {
                    steps {
                        dir('cart') {
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
                    publishCoverage adapters: [
                        jacocoAdapter('**/cart/target/site/jacoco/jacoco.xml')
                    ]
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
                            sh 'mvn test'
                        }
                    }
                }
                
                stage('Media: Coverage Report') {
                    steps {
                        dir('media') {
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
                    publishCoverage adapters: [
                        jacocoAdapter('**/media/target/site/jacoco/jacoco.xml')
                    ]
                }
            }
        }
    }
    
    post {
        always {
            echo "=== Pipeline completed ==="
            cleanWs()
        }
        success {
            echo "✅ Build SUCCESS - All tests passed!"
        }
        failure {
            echo "❌ Build FAILED - Check logs above"
        }
    }
}
