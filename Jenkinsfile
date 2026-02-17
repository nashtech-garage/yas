pipeline {
    agent any

    tools {
        jdk 'jdk-21'
    }

    environment {
        DOCKER_HOST = 'unix:///var/run/docker.sock'
    }

    stages {

        // ========== CUSTOMER SERVICE ==========
        stage('Test Customer') {
            when { changeset 'customer/**' }
            steps {
                dir('customer') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'customer/target/surefire-reports/*.xml'
                    jacoco execPattern: 'customer/target/jacoco.exec',
                           classPattern: 'customer/target/classes',
                           sourcePattern: 'customer/src/main/java'
                }
            }
        }
        stage('Build Customer') {
            when { changeset 'customer/**' }
            steps {
                dir('customer') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== CART SERVICE ==========
        stage('Test Cart') {
            when { changeset 'cart/**' }
            steps {
                dir('cart') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'cart/target/surefire-reports/*.xml'
                    jacoco execPattern: 'cart/target/jacoco.exec',
                           classPattern: 'cart/target/classes',
                           sourcePattern: 'cart/src/main/java'
                }
            }
        }
        stage('Build Cart') {
            when { changeset 'cart/**' }
            steps {
                dir('cart') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== ORDER SERVICE ==========
        stage('Test Order') {
            when { changeset 'order/**' }
            steps {
                dir('order') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'order/target/surefire-reports/*.xml'
                    jacoco execPattern: 'order/target/jacoco.exec',
                           classPattern: 'order/target/classes',
                           sourcePattern: 'order/src/main/java'
                }
            }
        }
        stage('Build Order') {
            when { changeset 'order/**' }
            steps {
                dir('order') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== PRODUCT SERVICE ==========
        stage('Test Product') {
            when { changeset 'product/**' }
            steps {
                dir('product') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'product/target/surefire-reports/*.xml'
                    jacoco execPattern: 'product/target/jacoco.exec',
                           classPattern: 'product/target/classes',
                           sourcePattern: 'product/src/main/java'
                }
            }
        }
        stage('Build Product') {
            when { changeset 'product/**' }
            steps {
                dir('product') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== INVENTORY SERVICE ==========
        stage('Test Inventory') {
            when { changeset 'inventory/**' }
            steps {
                dir('inventory') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'inventory/target/surefire-reports/*.xml'
                    jacoco execPattern: 'inventory/target/jacoco.exec',
                           classPattern: 'inventory/target/classes',
                           sourcePattern: 'inventory/src/main/java'
                }
            }
        }
        stage('Build Inventory') {
            when { changeset 'inventory/**' }
            steps {
                dir('inventory') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== PAYMENT SERVICE ==========
        stage('Test Payment') {
            when { changeset 'payment/**' }
            steps {
                dir('payment') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'payment/target/surefire-reports/*.xml'
                    jacoco execPattern: 'payment/target/jacoco.exec',
                           classPattern: 'payment/target/classes',
                           sourcePattern: 'payment/src/main/java'
                }
            }
        }
        stage('Build Payment') {
            when { changeset 'payment/**' }
            steps {
                dir('payment') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== PAYMENT-PAYPAL SERVICE ==========
        stage('Test Payment-Paypal') {
            when { changeset 'payment-paypal/**' }
            steps {
                dir('payment-paypal') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'payment-paypal/target/surefire-reports/*.xml'
                    jacoco execPattern: 'payment-paypal/target/jacoco.exec',
                           classPattern: 'payment-paypal/target/classes',
                           sourcePattern: 'payment-paypal/src/main/java'
                }
            }
        }
        stage('Build Payment-Paypal') {
            when { changeset 'payment-paypal/**' }
            steps {
                dir('payment-paypal') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== LOCATION SERVICE ==========
        stage('Test Location') {
            when { changeset 'location/**' }
            steps {
                dir('location') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'location/target/surefire-reports/*.xml'
                    jacoco execPattern: 'location/target/jacoco.exec',
                           classPattern: 'location/target/classes',
                           sourcePattern: 'location/src/main/java'
                }
            }
        }
        stage('Build Location') {
            when { changeset 'location/**' }
            steps {
                dir('location') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== MEDIA SERVICE ==========
        stage('Test Media') {
            when { changeset 'media/**' }
            steps {
                dir('media') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'media/target/surefire-reports/*.xml'
                    jacoco execPattern: 'media/target/jacoco.exec',
                           classPattern: 'media/target/classes',
                           sourcePattern: 'media/src/main/java'
                }
            }
        }
        stage('Build Media') {
            when { changeset 'media/**' }
            steps {
                dir('media') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== PROMOTION SERVICE ==========
        stage('Test Promotion') {
            when { changeset 'promotion/**' }
            steps {
                dir('promotion') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'promotion/target/surefire-reports/*.xml'
                    jacoco execPattern: 'promotion/target/jacoco.exec',
                           classPattern: 'promotion/target/classes',
                           sourcePattern: 'promotion/src/main/java'
                }
            }
        }
        stage('Build Promotion') {
            when { changeset 'promotion/**' }
            steps {
                dir('promotion') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== RATING SERVICE ==========
        stage('Test Rating') {
            when { changeset 'rating/**' }
            steps {
                dir('rating') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'rating/target/surefire-reports/*.xml'
                    jacoco execPattern: 'rating/target/jacoco.exec',
                           classPattern: 'rating/target/classes',
                           sourcePattern: 'rating/src/main/java'
                }
            }
        }
        stage('Build Rating') {
            when { changeset 'rating/**' }
            steps {
                dir('rating') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== SEARCH SERVICE ==========
        stage('Test Search') {
            when { changeset 'search/**' }
            steps {
                dir('search') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'search/target/surefire-reports/*.xml'
                    jacoco execPattern: 'search/target/jacoco.exec',
                           classPattern: 'search/target/classes',
                           sourcePattern: 'search/src/main/java'
                }
            }
        }
        stage('Build Search') {
            when { changeset 'search/**' }
            steps {
                dir('search') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== TAX SERVICE ==========
        stage('Test Tax') {
            when { changeset 'tax/**' }
            steps {
                dir('tax') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'tax/target/surefire-reports/*.xml'
                    jacoco execPattern: 'tax/target/jacoco.exec',
                           classPattern: 'tax/target/classes',
                           sourcePattern: 'tax/src/main/java'
                }
            }
        }
        stage('Build Tax') {
            when { changeset 'tax/**' }
            steps {
                dir('tax') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== WEBHOOK SERVICE ==========
        stage('Test Webhook') {
            when { changeset 'webhook/**' }
            steps {
                dir('webhook') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'webhook/target/surefire-reports/*.xml'
                    jacoco execPattern: 'webhook/target/jacoco.exec',
                           classPattern: 'webhook/target/classes',
                           sourcePattern: 'webhook/src/main/java'
                }
            }
        }
        stage('Build Webhook') {
            when { changeset 'webhook/**' }
            steps {
                dir('webhook') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== RECOMMENDATION SERVICE ==========
        stage('Test Recommendation') {
            when { changeset 'recommendation/**' }
            steps {
                dir('recommendation') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'recommendation/target/surefire-reports/*.xml'
                    jacoco execPattern: 'recommendation/target/jacoco.exec',
                           classPattern: 'recommendation/target/classes',
                           sourcePattern: 'recommendation/src/main/java'
                }
            }
        }
        stage('Build Recommendation') {
            when { changeset 'recommendation/**' }
            steps {
                dir('recommendation') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== DELIVERY SERVICE ==========
        stage('Test Delivery') {
            when { changeset 'delivery/**' }
            steps {
                dir('delivery') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'delivery/target/surefire-reports/*.xml'
                    jacoco execPattern: 'delivery/target/jacoco.exec',
                           classPattern: 'delivery/target/classes',
                           sourcePattern: 'delivery/src/main/java'
                }
            }
        }
        stage('Build Delivery') {
            when { changeset 'delivery/**' }
            steps {
                dir('delivery') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== BACKOFFICE-BFF SERVICE ==========
        stage('Test Backoffice-BFF') {
            when { changeset 'backoffice-bff/**' }
            steps {
                dir('backoffice-bff') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'backoffice-bff/target/surefire-reports/*.xml'
                    jacoco execPattern: 'backoffice-bff/target/jacoco.exec',
                           classPattern: 'backoffice-bff/target/classes',
                           sourcePattern: 'backoffice-bff/src/main/java'
                }
            }
        }
        stage('Build Backoffice-BFF') {
            when { changeset 'backoffice-bff/**' }
            steps {
                dir('backoffice-bff') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== STOREFRONT-BFF SERVICE ==========
        stage('Test Storefront-BFF') {
            when { changeset 'storefront-bff/**' }
            steps {
                dir('storefront-bff') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'storefront-bff/target/surefire-reports/*.xml'
                    jacoco execPattern: 'storefront-bff/target/jacoco.exec',
                           classPattern: 'storefront-bff/target/classes',
                           sourcePattern: 'storefront-bff/src/main/java'
                }
            }
        }
        stage('Build Storefront-BFF') {
            when { changeset 'storefront-bff/**' }
            steps {
                dir('storefront-bff') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== COMMON-LIBRARY ==========
        stage('Test Common-Library') {
            when { changeset 'common-library/**' }
            steps {
                dir('common-library') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'common-library/target/surefire-reports/*.xml'
                    jacoco execPattern: 'common-library/target/jacoco.exec',
                           classPattern: 'common-library/target/classes',
                           sourcePattern: 'common-library/src/main/java'
                }
            }
        }
        stage('Build Common-Library') {
            when { changeset 'common-library/**' }
            steps {
                dir('common-library') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }

        // ========== SAMPLEDATA SERVICE ==========
        stage('Test Sampledata') {
            when { changeset 'sampledata/**' }
            steps {
                dir('sampledata') {
                    sh 'chmod +x ../mvnw'
                    sh '../mvnw clean verify'
                }
            }
            post {
                always {
                    junit 'sampledata/target/surefire-reports/*.xml'
                    jacoco execPattern: 'sampledata/target/jacoco.exec',
                           classPattern: 'sampledata/target/classes',
                           sourcePattern: 'sampledata/src/main/java'
                }
            }
        }
        stage('Build Sampledata') {
            when { changeset 'sampledata/**' }
            steps {
                dir('sampledata') {
                    sh '../mvnw package -DskipTests'
                }
            }
        }
    }
}
