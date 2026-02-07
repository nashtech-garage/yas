pipeline {
    agent any

    tools {
        nodejs 'node20'
    }

    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=.m2/repository"
    }

    stages {
        // --- STAGE 1: CHECKOUT CODE ---
        stage('Checkout Code') {
            steps {
                checkout scm
                script {
                    echo "Checking out branch: ${env.BRANCH_NAME}"
                }
            }
        }

        // --- STAGE 2: ANALYZE CHANGES ---
        stage('Analyze Changes') {
            steps {
                script {
                    echo "Analyzing changes to determine build targets..."

                    // Get list of changed files compared to previous commit
                    def changedFiles = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim()
                    echo "--- Changed Files ---\n${changedFiles}\n---------------------"

                    // If root 'pom.xml' changes, we must build EVERYTHING (dependencies might change)
                    def buildAll = changedFiles.contains("pom.xml")
                    
                    // Backend Services (Spring Boot)
                    env.BUILD_MEDIA      = (buildAll || changedFiles.contains('media/'))      ? "true" : "false"
                    env.BUILD_PRODUCT    = (buildAll || changedFiles.contains('product/'))    ? "true" : "false"
                    env.BUILD_CART       = (buildAll || changedFiles.contains('cart/'))       ? "true" : "false"
                    env.BUILD_ORDER      = (buildAll || changedFiles.contains('order/'))      ? "true" : "false"
                    env.BUILD_RATING     = (buildAll || changedFiles.contains('rating/'))     ? "true" : "false"
                    env.BUILD_CUSTOMER   = (buildAll || changedFiles.contains('customer/'))   ? "true" : "false"
                    env.BUILD_LOCATION   = (buildAll || changedFiles.contains('location/'))   ? "true" : "false"
                    env.BUILD_INVENTORY  = (buildAll || changedFiles.contains('inventory/'))  ? "true" : "false"
                    env.BUILD_TAX        = (buildAll || changedFiles.contains('tax/'))        ? "true" : "false"
                    env.BUILD_SEARCH     = (buildAll || changedFiles.contains('search/'))     ? "true" : "false"

                    // Frontend Services (Next.js)
                    env.BUILD_BACKOFFICE = (buildAll || changedFiles.contains('backoffice/')) ? "true" : "false"
                    env.BUILD_STOREFRONT = (buildAll || changedFiles.contains('storefront/')) ? "true" : "false"
                }
            }
        }

        // --- STAGE 3: BUILD & TEST ---
        stage('Build & Test') {
            parallel {
                stage('Media Service') {
                    when { expression { env.BUILD_MEDIA == "true" } }
                    steps {
                        buildJavaService('media')
                    }
                }

                stage('Product Service') {
                    when { expression { env.BUILD_PRODUCT == "true" } }
                    steps {
                        buildJavaService('product')
                    }
                }

                stage('Cart Service') {
                    when { expression { env.BUILD_CART == "true" } }
                    steps {
                        buildJavaService('cart')
                    }
                }

                stage('Order Service') {
                    when { expression { env.BUILD_ORDER == "true" } }
                    steps {
                        buildJavaService('order')
                    }
                }

                stage('Rating Service') {
                    when { expression { env.BUILD_RATING == "true" } }
                    steps {
                        buildJavaService('rating')
                    }
                }

                stage('Customer Service') {
                    when { expression { env.BUILD_CUSTOMER == "true" } }
                    steps {
                        buildJavaService('customer')
                    }
                }

                stage('Location Service') {
                    when { expression { env.BUILD_LOCATION == "true" } }
                    steps {
                        buildJavaService('location')
                    }
                }

                stage('Inventory Service') {
                    when { expression { env.BUILD_INVENTORY == "true" } }
                    steps {
                        buildJavaService('inventory')
                    }
                }

                stage('Tax Service') {
                    when { expression { env.BUILD_TAX == "true" } }
                    steps {
                        buildJavaService('tax')
                    }
                }

                stage('Search Service') {
                    when { expression { env.BUILD_SEARCH == "true" } }
                    steps {
                        buildJavaService('search')
                    }
                }

                stage('Backoffice Service') {
                    when { expression { env.BUILD_BACKOFFICE == "true" } }
                    steps {
                        dir('backoffice') {
                            sh 'npm install'
                            sh 'npm run build'
                        }
                    }
                }

                stage('Storefront Service') {
                    when { expression { env.BUILD_STOREFRONT == "true" } }
                    steps {
                        dir('storefront') {
                            sh 'npm install'
                            sh 'npm run build'
                        }
                    }
                }
            }
        }
    }
}

def buildJavaService(String serviceName) {
    dir(serviceName) {
        echo "Building service ${serviceName}..."
        sh 'chmod +x ./mvnw'

        // Unit Tests
        sh "./mvnw clean install"
        junit "target/surefire-reports/*.xml"

        // Code Coverage
        jacoco(
            execPattern: "target/jacoco.exec",
            classPattern: "target/classes",
            sourcePattern: "src/main/java"
        )
    }
}
