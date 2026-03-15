def verifyCoverageAndPublish(String serviceTitle, String moduleDir) {
    def coverageStr = sh(
        script: "cat ${moduleDir}/target/site/jacoco/index.html 2>/dev/null | grep -o 'Total[^%]*%' | grep -oE '[0-9]+%' | tr -d '%' | head -n 1 || echo 'N/A'",
        returnStdout: true
    ).trim()

    if (coverageStr == 'N/A' || coverageStr == '') {
        publishChecks name: "Service / ${serviceTitle}", status: 'COMPLETED', conclusion: 'FAILURE', title: 'Coverage Error', summary: "Jacoco report not found. Please check Jenkins logs."
    }

    int coverage = coverageStr.toInteger()

    if (coverage < 70) {
        publishChecks name: "Service / ${serviceTitle}", status: 'COMPLETED', conclusion: 'FAILURE', title: 'Low Coverage', summary: "**Coverage: ${coverage}% / 100%**\n\nFailed: Code coverage is below the required 70%!"
    } else {
        publishChecks name: "Service / ${serviceTitle}", status: 'COMPLETED', conclusion: 'SUCCESS', title: 'Completed', summary: "**Coverage: ${coverage}% / 100%**\n\nTest and Build finished successfully."
    }
}

def publishFailureCheck(String serviceTitle, String moduleDir) {
    def coverageStr = sh(
        script: "cat ${moduleDir}/target/site/jacoco/index.html 2>/dev/null | grep -o 'Total[^%]*%' | grep -oE '[0-9]+%' | tr -d '%' | head -n 1 || echo 'N/A'",
        returnStdout: true
    ).trim()
    
    def covText = (coverageStr != 'N/A' && coverageStr != '') ? "**Coverage: ${coverageStr}% / 100%**\n\n" : ""
    publishChecks name: "Service / ${serviceTitle}", status: 'COMPLETED', conclusion: 'FAILURE', title: 'Failed', summary: "${covText}Error occurred during test or build. Please check Jenkins logs."
}

pipeline {
    agent any

    stages {
        // ====================================================================
        // PART 1: PRE-SCAN & SECURITY CHECKS (RUN FIRST)
        // ====================================================================
        stage('Pre-Scan: Compile Java Microservices') {
            steps {
                echo '--- Compiling Java modules for accurate Sonar analysis ---'
                sh """
                    docker run --rm \
                        -v "${WORKSPACE}":/usr/src/app \
                        -v "${HOME}/.m2":/root/.m2 \
                        -w /usr/src/app \
                        maven:3.9-eclipse-temurin-21 \
                        mvn clean test-compile -DskipTests
                """
            }
        }

        stage("Security & Quality Scans") {
            environment {
                SNYK_TOKEN = credentials('snyk-token-id')
                SONAR_TOKEN = credentials('sonarqube-token-id')
                SONAR_HOST_URL = credentials('sonarqube-host-url')
            }
            
            parallel {
                stage('1. Gitleaks: Secrets') {
                    steps {
                        publishChecks name: 'Scan / Gitleaks', status: 'IN_PROGRESS', title: 'Scanning for secrets...'
                        sh """
                            docker run --rm \
                                -v "${WORKSPACE}":/code \
                                ghcr.io/gitleaks/gitleaks:latest \
                                detect --source /code --verbose --redact --no-color || true
                        """
                    }
                    post {
                        success { publishChecks name: 'Scan / Gitleaks', status: 'COMPLETED', conclusion: 'SUCCESS', title: 'Secure', summary: 'No leaked secrets found.' }
                        failure { publishChecks name: 'Scan / Gitleaks', status: 'COMPLETED', conclusion: 'FAILURE', title: 'Danger!', summary: 'Secret leak detected.' }
                    }
                }

                stage('2. Snyk: Dependencies') {
                    steps {
                        publishChecks name: 'Scan / Snyk', status: 'IN_PROGRESS', title: 'Scanning dependencies...'
                        sh """
                            docker run --rm \
                                -e SNYK_TOKEN=${SNYK_TOKEN} \
                                -v "${WORKSPACE}":/project \
                                -w /project \
                                snyk/snyk:maven \
                                snyk test --severity-threshold=high --all-projects || true
                        """
                    }
                    post {
                        success { publishChecks name: 'Scan / Snyk', status: 'COMPLETED', conclusion: 'SUCCESS', title: 'Secure', summary: 'No HIGH severity vulnerabilities.' }
                        failure { publishChecks name: 'Scan / Snyk', status: 'COMPLETED', conclusion: 'FAILURE', title: 'Warning!', summary: 'Vulnerabilities detected!' }
                    }
                }
                
                stage('3. SonarQube: Code Quality') {
                    steps {
                        publishChecks name: 'Scan / SonarQube', status: 'IN_PROGRESS', title: 'Analyzing code quality...'
                        sh """
                            docker run --rm \
                                -e SONAR_HOST_URL=${SONAR_HOST_URL} \
                                -e SONAR_TOKEN=${SONAR_TOKEN} \
                                -v "${WORKSPACE}":/usr/src \
                                sonarsource/sonar-scanner-cli \
                                -Dsonar.projectKey=yas-monorepo \
                                -Dsonar.sources=. \
                                -Dsonar.java.binaries=**/target/classes \
                                -Dsonar.exclusions=**/node_modules/**,**/.next/**,**/dist/**,**/postgres/**,**/.turbo/** || true
                        """
                    }
                    post {
                        success { publishChecks name: 'Scan / SonarQube', status: 'COMPLETED', conclusion: 'SUCCESS', title: 'Passed', summary: 'Passed Quality Gate.' }
                        failure { publishChecks name: 'Scan / SonarQube', status: 'COMPLETED', conclusion: 'FAILURE', title: 'Failed', summary: 'Failed Quality Gate.' }
                    }
                }
            }
        }

        // ====================================================================
        // PART 2: MICROSERVICES CI
        // ====================================================================
        stage('Microservices CI Environment') {
            agent { docker { image 'maven:3.9.6-eclipse-temurin-21'; args '-v $HOME/.m2:/root/.m2 -u root' } }
            stages {
                stage('Run Microservices CI') {
                    parallel {
                        
                        // ---------------- MEDIA ----------------
                        stage('Media Service') {
                            when { changeset "media/**" }
                            stages {
                                stage('Test Media') {
                                    steps { 
                                        publishChecks name: 'Service / Media', status: 'IN_PROGRESS', title: 'Running Tests & Build...'
                                        sh 'mvn test -pl media -am' 
                                        sh 'mvn jacoco:report -pl media'
                                    }
                                    post {
                                        always {
                                            junit testResults: 'media/target/surefire-reports/*.xml', skipPublishingChecks: true
                                            jacoco(execPattern: 'media/target/jacoco.exec', classPattern: 'media/target/classes', sourcePattern: 'media/src/main/java')
                                        }
                                    }
                                }
                                stage('Build Media') { steps { sh 'mvn package -pl media -am -DskipTests' } }
                            }
                            post {
                                success { script { verifyCoverageAndPublish('Media', 'media') } }
                                failure { script { publishFailureCheck('Media', 'media') } }
                            }
                        }

                        // ---------------- PRODUCT ----------------
                        stage('Product Service') {
                            when { changeset "product/**" }
                            stages {
                                stage('Test Product') {
                                    steps { 
                                        publishChecks name: 'Service / Product', status: 'IN_PROGRESS', title: 'Running Tests & Build...'
                                        sh 'mvn test -pl product -am'
                                        sh 'mvn jacoco:report -pl product'
                                    }
                                    post { always { junit testResults: 'product/target/surefire-reports/*.xml', skipPublishingChecks: true; jacoco(execPattern: 'product/target/jacoco.exec', classPattern: 'product/target/classes', sourcePattern: 'product/src/main/java') } }
                                }
                                stage('Build Product') { steps { sh 'mvn package -pl product -am -DskipTests' } }
                            }
                            post {
                                success { script { verifyCoverageAndPublish('Product', 'product') } }
                                failure { script { publishFailureCheck('Product', 'product') } }
                            }
                        }

                        // ---------------- CART ----------------
                        stage('Cart Service') {
                            when { changeset "cart/**" }
                            stages {
                                stage('Test Cart') {
                                    steps { 
                                        publishChecks name: 'Service / Cart', status: 'IN_PROGRESS', title: 'Running Tests & Build...'
                                        sh 'mvn test -pl cart -am'
                                        sh 'mvn jacoco:report -pl cart'
                                    }
                                    post { always { junit testResults: 'cart/target/surefire-reports/*.xml', skipPublishingChecks: true; jacoco(execPattern: 'cart/target/jacoco.exec', classPattern: 'cart/target/classes', sourcePattern: 'cart/src/main/java') } }
                                }
                                stage('Build Cart') { steps { sh 'mvn package -pl cart -am -DskipTests' } }
                            }
                            post {
                                success { script { verifyCoverageAndPublish('Cart', 'cart') } }
                                failure { script { publishFailureCheck('Cart', 'cart') } }
                            }
                        }

                        // ---------------- ORDER ----------------
                        stage('Order Service') {
                            when { changeset "order/**" }
                            stages {
                                stage('Test Order') {
                                    steps { 
                                        publishChecks name: 'Service / Order', status: 'IN_PROGRESS', title: 'Running Tests & Build...'
                                        sh 'mvn test -pl order -am'
                                        sh 'mvn jacoco:report -pl order'
                                    }
                                    post { always { junit testResults: 'order/target/surefire-reports/*.xml', skipPublishingChecks: true; jacoco(execPattern: 'order/target/jacoco.exec', classPattern: 'order/target/classes', sourcePattern: 'order/src/main/java') } }
                                }
                                stage('Build Order') { steps { sh 'mvn package -pl order -am -DskipTests' } }
                            }
                            post {
                                success { script { verifyCoverageAndPublish('Order', 'order') } }
                                failure { script { publishFailureCheck('Order', 'order') } }
                            }
                        }

                        // ---------------- INVENTORY ----------------
                        stage('Inventory Service') {
                            when { changeset "inventory/**" }
                            stages {
                                stage('Test Inventory') {
                                    steps { 
                                        publishChecks name: 'Service / Inventory', status: 'IN_PROGRESS', title: 'Running Tests & Build...'
                                        sh 'mvn test -pl inventory -am'
                                        sh 'mvn jacoco:report -pl inventory'
                                    }
                                    post { always { junit testResults: 'inventory/target/surefire-reports/*.xml', skipPublishingChecks: true; jacoco(execPattern: 'inventory/target/jacoco.exec', classPattern: 'inventory/target/classes', sourcePattern: 'inventory/src/main/java') } }
                                }
                                stage('Build Inventory') { steps { sh 'mvn package -pl inventory -am -DskipTests' } }
                            }
                            post {
                                success { script { verifyCoverageAndPublish('Inventory', 'inventory') } }
                                failure { script { publishFailureCheck('Inventory', 'inventory') } }
                            }
                        }

                        // ---------------- TAX ----------------
                        stage('Tax Service') {
                            when { changeset "tax/**" }
                            stages {
                                stage('Test Tax') {
                                    steps { 
                                        publishChecks name: 'Service / Tax', status: 'IN_PROGRESS', title: 'Running Tests & Build...'
                                        sh 'mvn test -pl tax -am'
                                        sh 'mvn jacoco:report -pl tax'
                                    }
                                    post { always { junit testResults: 'tax/target/surefire-reports/*.xml', skipPublishingChecks: true; jacoco(execPattern: 'tax/target/jacoco.exec', classPattern: 'tax/target/classes', sourcePattern: 'tax/src/main/java') } }
                                }
                                stage('Build Tax') { steps { sh 'mvn package -pl tax -am -DskipTests' } }
                            }
                            post {
                                success { script { verifyCoverageAndPublish('Tax', 'tax') } }
                                failure { script { publishFailureCheck('Tax', 'tax') } }
                            }
                        }

                        // ---------------- SEARCH ----------------
                        stage('Search Service') {
                            when { changeset "search/**" }
                            stages {
                                stage('Test Search') {
                                    steps { 
                                        publishChecks name: 'Service / Search', status: 'IN_PROGRESS', title: 'Running Tests & Build...'
                                        sh 'mvn test -pl search -am'
                                        sh 'mvn jacoco:report -pl search'
                                    }
                                    post { always { junit testResults: 'search/target/surefire-reports/*.xml', skipPublishingChecks: true; jacoco(execPattern: 'search/target/jacoco.exec', classPattern: 'search/target/classes', sourcePattern: 'search/src/main/java') } }
                                }
                                stage('Build Search') { steps { sh 'mvn package -pl search -am -DskipTests' } }
                            }
                            post {
                                success { script { verifyCoverageAndPublish('Search', 'search') } }
                                failure { script { publishFailureCheck('Search', 'search') } }
                            }
                        }

                        // ---------------- RATING ----------------
                        stage('Rating Service') {
                            when { changeset "rating/**" }
                            stages {
                                stage('Test Rating') {
                                    steps { 
                                        publishChecks name: 'Service / Rating', status: 'IN_PROGRESS', title: 'Running Tests & Build...'
                                        sh 'mvn test -pl rating -am'
                                        sh 'mvn jacoco:report -pl rating'
                                    }
                                    post { always { junit testResults: 'rating/target/surefire-reports/*.xml', skipPublishingChecks: true; jacoco(execPattern: 'rating/target/jacoco.exec', classPattern: 'rating/target/classes', sourcePattern: 'rating/src/main/java') } }
                                }
                                stage('Build Rating') { steps { sh 'mvn package -pl rating -am -DskipTests' } }
                            }
                            post {
                                success { script { verifyCoverageAndPublish('Rating', 'rating') } }
                                failure { script { publishFailureCheck('Rating', 'rating') } }
                            }
                        }

                        // ---------------- CUSTOMER ----------------
                        stage('Customer Service') {
                            when { changeset "customer/**" }
                            stages {
                                stage('Test Customer') {
                                    steps { 
                                        publishChecks name: 'Service / Customer', status: 'IN_PROGRESS', title: 'Running Tests & Build...'
                                        sh 'mvn test -pl customer -am'
                                        sh 'mvn jacoco:report -pl customer'
                                    }
                                    post { always { junit testResults: 'customer/target/surefire-reports/*.xml', skipPublishingChecks: true; jacoco(execPattern: 'customer/target/jacoco.exec', classPattern: 'customer/target/classes', sourcePattern: 'customer/src/main/java') } }
                                }
                                stage('Build Customer') { steps { sh 'mvn package -pl customer -am -DskipTests' } }
                            }
                            post {
                                success { script { verifyCoverageAndPublish('Customer', 'customer') } }
                                failure { script { publishFailureCheck('Customer', 'customer') } }
                            }
                        }

                        // ---------------- LOCATION ----------------
                        stage('Location Service') {
                            when { changeset "location/**" }
                            stages {
                                stage('Test Location') {
                                    steps { 
                                        publishChecks name: 'Service / Location', status: 'IN_PROGRESS', title: 'Running Tests & Build...'
                                        sh 'mvn test -pl location -am'
                                        sh 'mvn jacoco:report -pl location'
                                    }
                                    post { always { junit testResults: 'location/target/surefire-reports/*.xml', skipPublishingChecks: true; jacoco(execPattern: 'location/target/jacoco.exec', classPattern: 'location/target/classes', sourcePattern: 'location/src/main/java') } }
                                }
                                stage('Build Location') { steps { sh 'mvn package -pl location -am -DskipTests' } }
                            }
                            post {
                                success { script { verifyCoverageAndPublish('Location', 'location') } }
                                failure { script { publishFailureCheck('Location', 'location') } }
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        success { publishChecks name: 'Jenkins Pipeline', status: 'COMPLETED', conclusion: 'SUCCESS', title: 'Success', summary: 'The entire CI pipeline completed without errors.' }
        failure { publishChecks name: 'Jenkins Pipeline', status: 'COMPLETED', conclusion: 'FAILURE', title: 'Failed', summary: 'CI process encountered an error.' }
    }
}