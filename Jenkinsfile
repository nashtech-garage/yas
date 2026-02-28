pipeline {
    agent any

    tools {
        jdk 'jdk-21'
    }

    environment {
        DOCKER_HOST = 'unix:///var/run/docker.sock'
    }

    stages {

        // ========== INSTALL COMMON-LIBRARY (required by other services) ==========
        stage('Install Common-Library') {
            steps {
                dir('customer') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean install -f ../common-library/pom.xml -DskipTests'
                }
            }
        }

        // ========== CUSTOMER SERVICE ==========
        stage('Test Customer') {
            when { changeset 'customer/**' }
            steps {
                dir('customer') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'customer/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'customer/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Customer') {
            when { changeset 'customer/**' }
            steps {
                dir('customer') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== CART SERVICE ==========
        stage('Test Cart') {
            when { changeset 'cart/**' }
            steps {
                dir('cart') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'cart/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'cart/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Cart') {
            when { changeset 'cart/**' }
            steps {
                dir('cart') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== ORDER SERVICE ==========
        stage('Test Order') {
            when { changeset 'order/**' }
            steps {
                dir('order') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'order/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'order/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Order') {
            when { changeset 'order/**' }
            steps {
                dir('order') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== PRODUCT SERVICE ==========
        stage('Test Product') {
            when { changeset 'product/**' }
            steps {
                dir('product') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'product/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'product/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Product') {
            when { changeset 'product/**' }
            steps {
                dir('product') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== INVENTORY SERVICE ==========
        stage('Test Inventory') {
            when { changeset 'inventory/**' }
            steps {
                dir('inventory') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'inventory/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'inventory/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Inventory') {
            when { changeset 'inventory/**' }
            steps {
                dir('inventory') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== PAYMENT SERVICE ==========
        stage('Test Payment') {
            when { changeset 'payment/**' }
            steps {
                dir('payment') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'payment/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'payment/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Payment') {
            when { changeset 'payment/**' }
            steps {
                dir('payment') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== PAYMENT-PAYPAL SERVICE ==========
        stage('Test Payment-Paypal') {
            when { changeset 'payment-paypal/**' }
            steps {
                dir('payment-paypal') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'payment-paypal/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'payment-paypal/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Payment-Paypal') {
            when { changeset 'payment-paypal/**' }
            steps {
                dir('payment-paypal') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== LOCATION SERVICE ==========
        stage('Test Location') {
            when { changeset 'location/**' }
            steps {
                dir('location') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'location/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'location/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Location') {
            when { changeset 'location/**' }
            steps {
                dir('location') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== MEDIA SERVICE ==========
        stage('Test Media') {
            when { changeset 'media/**' }
            steps {
                dir('media') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'media/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'media/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Media') {
            when { changeset 'media/**' }
            steps {
                dir('media') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== PROMOTION SERVICE ==========
        stage('Test Promotion') {
            when { changeset 'promotion/**' }
            steps {
                dir('promotion') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'promotion/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'promotion/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Promotion') {
            when { changeset 'promotion/**' }
            steps {
                dir('promotion') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== RATING SERVICE ==========
        stage('Test Rating') {
            when { changeset 'rating/**' }
            steps {
                dir('rating') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'rating/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'rating/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Rating') {
            when { changeset 'rating/**' }
            steps {
                dir('rating') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== SEARCH SERVICE ==========
        stage('Test Search') {
            when { changeset 'search/**' }
            steps {
                dir('search') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'search/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'search/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Search') {
            when { changeset 'search/**' }
            steps {
                dir('search') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== TAX SERVICE ==========
        stage('Test Tax') {
            when { changeset 'tax/**' }
            steps {
                dir('tax') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'tax/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'tax/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Tax') {
            when { changeset 'tax/**' }
            steps {
                dir('tax') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== WEBHOOK SERVICE ==========
        stage('Test Webhook') {
            when { changeset 'webhook/**' }
            steps {
                dir('webhook') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'webhook/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'webhook/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Webhook') {
            when { changeset 'webhook/**' }
            steps {
                dir('webhook') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== RECOMMENDATION SERVICE ==========
        stage('Test Recommendation') {
            when { changeset 'recommendation/**' }
            steps {
                dir('recommendation') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'recommendation/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'recommendation/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Recommendation') {
            when { changeset 'recommendation/**' }
            steps {
                dir('recommendation') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== DELIVERY SERVICE ==========
        stage('Test Delivery') {
            when { changeset 'delivery/**' }
            steps {
                dir('delivery') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'delivery/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'delivery/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Delivery') {
            when { changeset 'delivery/**' }
            steps {
                dir('delivery') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== BACKOFFICE-BFF SERVICE ==========
        stage('Test Backoffice-BFF') {
            when { changeset 'backoffice-bff/**' }
            steps {
                dir('backoffice-bff') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backoffice-bff/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'backoffice-bff/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Backoffice-BFF') {
            when { changeset 'backoffice-bff/**' }
            steps {
                dir('backoffice-bff') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== STOREFRONT-BFF SERVICE ==========
        stage('Test Storefront-BFF') {
            when { changeset 'storefront-bff/**' }
            steps {
                dir('storefront-bff') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'storefront-bff/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'storefront-bff/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Storefront-BFF') {
            when { changeset 'storefront-bff/**' }
            steps {
                dir('storefront-bff') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== COMMON-LIBRARY ==========
        stage('Test Common-Library') {
            when { changeset 'common-library/**' }
            steps {
                dir('common-library') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'common-library/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'common-library/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Common-Library') {
            when { changeset 'common-library/**' }
            steps {
                dir('common-library') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        // ========== SAMPLEDATA SERVICE ==========
        stage('Test Sampledata') {
            when { changeset 'sampledata/**' }
            steps {
                dir('sampledata') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean verify'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'sampledata/target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'sampledata/target/site/jacoco/jacoco.xml']],
                        sourceCodeRetention: 'EVERY_BUILD'
                    )
                }
            }
        }
        stage('Build Sampledata') {
            when { changeset 'sampledata/**' }
            steps {
                dir('sampledata') {
                    sh './mvnw package -DskipTests'
                }
            }
        }
    }
}
