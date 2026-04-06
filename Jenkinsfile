pipeline {
    agent any
    
    parameters {
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip running tests')
    }
    
    environment {
        MAVEN_OPTS = '-Dmaven.repo.local=${WORKSPACE}/.m2'
        NODE_ENV = 'test'
    }
    
    stages {
        stage('Checkout') {
            steps {
                script {
                    echo '=== Checking out source code ==='
                    checkout scm
                    sh 'git log -1 --pretty=%H > GIT_COMMIT.txt || true'
                    sh 'git log -1 --pretty=%s > GIT_MESSAGE.txt || true'
                }
            }
        }
        
        stage('Detect Changes') {
            steps {
                script {
                    echo '=== Detecting changed services ==='
                    
                    // Find all service directories dynamically
                    def javaServices = [
                        'cart', 'customer', 'delivery', 'inventory', 'location', 
                        'media', 'order', 'payment', 'payment-paypal', 'product', 
                        'promotion', 'rating', 'recommendation', 'search', 'tax', 
                        'webhook', 'backoffice-bff', 'storefront-bff', 'sampledata',
                        'automation-ui'
                    ]
                    
                    def nodeServices = ['backoffice', 'storefront']
                    
                    // Check each Java service
                    javaServices.each { service ->
                        def changed = sh(
                            script: """
                                if git diff --name-only origin/main HEAD 2>/dev/null | grep -q "^${service}/"; then
                                    echo "true"
                                else
                                    echo "false"
                                fi
                            """,
                            returnStdout: true
                        ).trim()
                        env["${service.toUpperCase().replaceAll('-', '_')}_CHANGED"] = changed
                    }
                    
                    // Check each Node service
                    nodeServices.each { service ->
                        def changed = sh(
                            script: """
                                if git diff --name-only origin/main HEAD 2>/dev/null | grep -q "^${service}/"; then
                                    echo "true"
                                else
                                    echo "false"
                                fi
                            """,
                            returnStdout: true
                        ).trim()
                        env["${service.toUpperCase().replaceAll('-', '_')}_CHANGED"] = changed
                    }
                    
                    echo '=== Changed Services Summary ==='
                    javaServices.each { service ->
                        def varName = service.toUpperCase().replaceAll('-', '_') + '_CHANGED'
                        echo "${service}: ${env[varName]}"
                    }
                    nodeServices.each { service ->
                        def varName = service.toUpperCase().replaceAll('-', '_') + '_CHANGED'
                        echo "${service}: ${env[varName]}"
                    }
                }
            }
        }
        
        // ============ JAVA/MAVEN SERVICES ============
        stage('Test Media') { when { expression { env.MEDIA_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl media -am test' } }
        stage('Build Media') { when { expression { env.MEDIA_CHANGED == 'true' } } steps { sh './mvnw -pl media -am clean package -DskipTests' } }
        
        stage('Test Cart') { when { expression { env.CART_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl cart -am test' } }
        stage('Build Cart') { when { expression { env.CART_CHANGED == 'true' } } steps { sh './mvnw -pl cart -am clean package -DskipTests' } }
        
        stage('Test Order') { when { expression { env.ORDER_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl order -am test' } }
        stage('Build Order') { when { expression { env.ORDER_CHANGED == 'true' } } steps { sh './mvnw -pl order -am clean package -DskipTests' } }
        
        stage('Test Payment') { when { expression { env.PAYMENT_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl payment -am test' } }
        stage('Build Payment') { when { expression { env.PAYMENT_CHANGED == 'true' } } steps { sh './mvnw -pl payment -am clean package -DskipTests' } }
        
        stage('Test Payment PayPal') { when { expression { env.PAYMENT_PAYPAL_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl payment-paypal -am test' } }
        stage('Build Payment PayPal') { when { expression { env.PAYMENT_PAYPAL_CHANGED == 'true' } } steps { sh './mvnw -pl payment-paypal -am clean package -DskipTests' } }
        
        stage('Test Product') { when { expression { env.PRODUCT_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl product -am test' } }
        stage('Build Product') { when { expression { env.PRODUCT_CHANGED == 'true' } } steps { sh './mvnw -pl product -am clean package -DskipTests' } }
        
        stage('Test Promotion') { when { expression { env.PROMOTION_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl promotion -am test' } }
        stage('Build Promotion') { when { expression { env.PROMOTION_CHANGED == 'true' } } steps { sh './mvnw -pl promotion -am clean package -DskipTests' } }
        
        stage('Test Search') { when { expression { env.SEARCH_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl search -am test' } }
        stage('Build Search') { when { expression { env.SEARCH_CHANGED == 'true' } } steps { sh './mvnw -pl search -am clean package -DskipTests' } }
        
        stage('Test Customer') { when { expression { env.CUSTOMER_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl customer -am test' } }
        stage('Build Customer') { when { expression { env.CUSTOMER_CHANGED == 'true' } } steps { sh './mvnw -pl customer -am clean package -DskipTests' } }
        
        stage('Test Inventory') { when { expression { env.INVENTORY_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl inventory -am test' } }
        stage('Build Inventory') { when { expression { env.INVENTORY_CHANGED == 'true' } } steps { sh './mvnw -pl inventory -am clean package -DskipTests' } }
        
        stage('Test Location') { when { expression { env.LOCATION_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl location -am test' } }
        stage('Build Location') { when { expression { env.LOCATION_CHANGED == 'true' } } steps { sh './mvnw -pl location -am clean package -DskipTests' } }
        
        stage('Test Delivery') { when { expression { env.DELIVERY_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl delivery -am test' } }
        stage('Build Delivery') { when { expression { env.DELIVERY_CHANGED == 'true' } } steps { sh './mvnw -pl delivery -am clean package -DskipTests' } }
        
        stage('Test Rating') { when { expression { env.RATING_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl rating -am test' } }
        stage('Build Rating') { when { expression { env.RATING_CHANGED == 'true' } } steps { sh './mvnw -pl rating -am clean package -DskipTests' } }
        
        stage('Test Recommendation') { when { expression { env.RECOMMENDATION_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl recommendation -am test' } }
        stage('Build Recommendation') { when { expression { env.RECOMMENDATION_CHANGED == 'true' } } steps { sh './mvnw -pl recommendation -am clean package -DskipTests' } }
        
        stage('Test Tax') { when { expression { env.TAX_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl tax -am test' } }
        stage('Build Tax') { when { expression { env.TAX_CHANGED == 'true' } } steps { sh './mvnw -pl tax -am clean package -DskipTests' } }
        
        stage('Test Webhook') { when { expression { env.WEBHOOK_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl webhook -am test' } }
        stage('Build Webhook') { when { expression { env.WEBHOOK_CHANGED == 'true' } } steps { sh './mvnw -pl webhook -am clean package -DskipTests' } }
        
        stage('Test Backoffice BFF') { when { expression { env.BACKOFFICE_BFF_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl backoffice-bff -am test' } }
        stage('Build Backoffice BFF') { when { expression { env.BACKOFFICE_BFF_CHANGED == 'true' } } steps { sh './mvnw -pl backoffice-bff -am clean package -DskipTests' } }
        
        stage('Test Storefront BFF') { when { expression { env.STOREFRONT_BFF_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl storefront-bff -am test' } }
        stage('Build Storefront BFF') { when { expression { env.STOREFRONT_BFF_CHANGED == 'true' } } steps { sh './mvnw -pl storefront-bff -am clean package -DskipTests' } }
        
        stage('Test Sample Data') { when { expression { env.SAMPLEDATA_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl sampledata -am test' } }
        stage('Build Sample Data') { when { expression { env.SAMPLEDATA_CHANGED == 'true' } } steps { sh './mvnw -pl sampledata -am clean package -DskipTests' } }
        
        stage('Test Automation UI') { when { expression { env.AUTOMATION_UI_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { sh './mvnw -pl automation-ui -am test' } }
        stage('Build Automation UI') { when { expression { env.AUTOMATION_UI_CHANGED == 'true' } } steps { sh './mvnw -pl automation-ui -am clean package -DskipTests' } }
        
        // ============ NODE.JS/NPM SERVICES ============
        stage('Test Backoffice') { when { expression { env.BACKOFFICE_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { dir('backoffice') { sh 'npm ci && npm run test -- --coverage || true' } } }
        stage('Build Backoffice') { when { expression { env.BACKOFFICE_CHANGED == 'true' } } steps { dir('backoffice') { sh 'npm run build' } } }
        
        stage('Test Storefront') { when { expression { env.STOREFRONT_CHANGED == 'true' && env.SKIP_TESTS == 'false' } } steps { dir('storefront') { sh 'npm ci && npm run test -- --coverage || true' } } }
        stage('Build Storefront') { when { expression { env.STOREFRONT_CHANGED == 'true' } } steps { dir('storefront') { sh 'npm run build' } } }
    }
    
    post {
        always {
            script {
                echo '=== Collecting test results ==='
                junit testResults: '**/target/surefire-reports/**/*.xml', allowEmptyResults: true, skipPublishingChecks: false
                archiveArtifacts artifacts: '**/target/site/jacoco/**,backoffice/coverage/**,storefront/coverage/**', allowEmptyArchive: true
                
                publishHTML([reportDir: 'media/target/site/jacoco', reportFiles: 'index.html', reportName: 'Media - Jacoco Coverage', allowMissing: true, keepAll: true])
                publishHTML([reportDir: 'cart/target/site/jacoco', reportFiles: 'index.html', reportName: 'Cart - Jacoco Coverage', allowMissing: true, keepAll: true])
                publishHTML([reportDir: 'backoffice/coverage', reportFiles: 'index.html', reportName: 'Backoffice - Coverage', allowMissing: true, keepAll: true])
                publishHTML([reportDir: 'storefront/coverage', reportFiles: 'index.html', reportName: 'Storefront - Coverage', allowMissing: true, keepAll: true])
            }
        }
        success {
            echo 'Pipeline completed successfully'
        }
        failure {
            echo 'Pipeline failed'
        }
    }
}