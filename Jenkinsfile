// =============================================================================
// Jenkinsfile — YAS Monorepo CI/CD
// Mirrors all jobs defined in .github/workflows/*.yaml
//
// Pipelines covered:
//   • Java microservices (cart, customer, inventory, location, media, order,
//     payment, payment-paypal, product, promotion, rating, recommendation,
//     sampledata, search, tax, webhook, backoffice-bff, storefront-bff)
//   • Node.js frontends  (backoffice, storefront)
//   • Helm charts release (k8s/charts/**)
//   • GitLeaks nightly secret scan
//   • CodeQL SAST (Java + JavaScript/TypeScript)
//
// Required Jenkins credentials (Manage Jenkins → Credentials):
//   SONAR_TOKEN      – SonarCloud token            (Secret text)
//   GHCR_TOKEN       – GitHub PAT for ghcr.io push (Secret text)
//   GHCR_USERNAME    – GitHub username / org        (Secret text)
//
// Required Jenkins plugins:
//   Pipeline, Git, Docker Pipeline, JUnit, Warnings NG (checkstyle),
//   JaCoCo, OWASP Dependency-Check, Credentials Binding
//
// Required tools configured in Jenkins Global Tool Configuration:
//   JDK    named "JDK-25"       (Temurin / Eclipse Adoptium)
//   Maven  named "Maven-3"
//   NodeJS named "NodeJS-20"
// =============================================================================

def GHCR_REGISTRY = 'ghcr.io'
def GHCR_ORG      = 'nashtech-garage'
def MAIN_BRANCH   = 'main'

// ---------------------------------------------------------------------------
// Helper closure: login to GHCR, build & push a Docker image
// ---------------------------------------------------------------------------
def dockerBuildAndPush(String imageSlug, String contextDir) {
    withCredentials([
        string(credentialsId: 'GHCR_USERNAME', variable: 'GHCR_USER'),
        string(credentialsId: 'GHCR_TOKEN',    variable: 'GHCR_PASS')
    ]) {
        sh """
            echo "\$GHCR_PASS" | docker login ${GHCR_REGISTRY} -u "\$GHCR_USER" --password-stdin
            docker build -t ${GHCR_REGISTRY}/${GHCR_ORG}/${imageSlug}:latest ${contextDir}
            docker push  ${GHCR_REGISTRY}/${GHCR_ORG}/${imageSlug}:latest
        """
    }
}

// =============================================================================
// Main pipeline
// =============================================================================
pipeline {
    agent any

    // -------------------------------------------------------------------------
    // Triggers
    // -------------------------------------------------------------------------
    triggers {
        // Nightly GitLeaks scan  (mirrors schedule: "0 0 * * *")
        cron('H 0 * * *')
        // Weekly CodeQL scan     (mirrors cron: '19 21 * * 0')
        cron('19 21 * * 0')
        // SCM polling for push / PR events (replace with webhook if preferred)
        pollSCM('H/5 * * * *')
    }

    // -------------------------------------------------------------------------
    // Tools (must match names in Global Tool Configuration)
    // -------------------------------------------------------------------------
    tools {
        jdk    'JDK-25'
        maven  'Maven-3'
        nodejs 'NodeJS-20'
    }

    // -------------------------------------------------------------------------
    // Options
    // -------------------------------------------------------------------------
    options {
        buildDiscarder(logRotator(numToKeepStr: '30'))
        timeout(time: 120, unit: 'MINUTES')
        skipDefaultCheckout(true)   // explicit checkout with full depth below
        timestamps()
    }

    // =========================================================================
    // Stages
    // =========================================================================
    stages {

        // =====================================================================
        // 1. CHECKOUT
        //    fetch-depth: 0 required by SonarCloud & Gitleaks for full history
        // =====================================================================
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: scm.branches,
                    extensions: [
                        [$class: 'CloneOption', depth: 0, shallow: false]
                    ],
                    userRemoteConfigs: scm.userRemoteConfigs
                ])
            }
        }

        // =====================================================================
        // 2. SECURITY SCANS
        // =====================================================================

        // ---------------------------------------------------------------------
        // 2a. GitLeaks — nightly secret scan
        //     Mirrors: gitleaks-check.yaml  (schedule "0 0 * * *")
        // ---------------------------------------------------------------------
        stage('GitLeaks — Secret Scan') {
            steps {
                sh '''
                    docker pull zricethezav/gitleaks:v8.18.4
                    docker run --rm \
                        -v "$(pwd)":/work \
                        -w /work \
                        zricethezav/gitleaks:v8.18.4 \
                        detect --source="." --config="/work/gitleaks.toml" --verbose --no-git
                '''
            }
        }

        // ---------------------------------------------------------------------
        // 2b. CodeQL — Java/Kotlin SAST
        //     Mirrors: codeql.yml  language: java-kotlin
        //     Note: CodeQL CLI must be on PATH of the Jenkins agent.
        // ---------------------------------------------------------------------
        stage('CodeQL — Java/Kotlin Analysis') {
            when {
                anyOf {
                    branch MAIN_BRANCH
                    changeRequest target: MAIN_BRANCH
                    triggeredBy 'TimerTrigger'
                }
            }
            steps {
                sh '''
                    codeql database create codeql-db-java \
                        --language=java \
                        --overwrite
                    codeql database analyze codeql-db-java \
                        --format=sarif-latest \
                        --output=codeql-java-results.sarif \
                        java-security-extended.qls
                '''
                archiveArtifacts artifacts: 'codeql-java-results.sarif', allowEmptyArchive: true
            }
        }

        // ---------------------------------------------------------------------
        // 2c. CodeQL — JavaScript/TypeScript SAST
        //     Mirrors: codeql.yml  language: javascript-typescript
        // ---------------------------------------------------------------------
        stage('CodeQL — JavaScript/TypeScript Analysis') {
            when {
                anyOf {
                    branch MAIN_BRANCH
                    changeRequest target: MAIN_BRANCH
                    triggeredBy 'TimerTrigger'
                }
            }
            steps {
                sh '''
                    codeql database create codeql-db-js \
                        --language=javascript \
                        --overwrite
                    codeql database analyze codeql-db-js \
                        --format=sarif-latest \
                        --output=codeql-js-results.sarif \
                        javascript-security-extended.qls
                '''
                archiveArtifacts artifacts: 'codeql-js-results.sarif', allowEmptyArchive: true
            }
        }

        // =====================================================================
        // 3. JAVA MICROSERVICES CI
        //    Pattern (all Java services):
        //      Maven Build → Checkstyle → Unit Tests → SonarCloud →
        //      OWASP Dependency Check → JaCoCo → Docker Build & Push (main only)
        // =====================================================================

        // -------------------------------------------------------------------
        // cart-ci.yaml
        // -------------------------------------------------------------------
        stage('Cart Service CI') {
            when {
                anyOf {
                    changeset 'cart/**'
                    changeset 'pom.xml'
                    branch MAIN_BRANCH
                }
            }
            stages {
                stage('Cart — Maven Build') {
                    steps { sh 'mvn clean install -pl cart -am' }
                }
                stage('Cart — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl cart -am -Dcheckstyle.output.file=cart-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/cart-checkstyle-result.xml')]
                    }
                }
                stage('Cart — Unit Test Report') {
                    steps {
                        junit allowEmptyResults: true, testResults: 'cart/**/*-reports/TEST*.xml'
                    }
                }
                stage('Cart — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -pl cart -am -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Cart — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Cart — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'cart/**/target/jacoco.exec',
                               classPattern: 'cart/**/target/classes',
                               sourcePattern: 'cart/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Cart — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-cart', './cart') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // customer-ci.yaml
        // -------------------------------------------------------------------
        stage('Customer Service CI') {
            when {
                anyOf { changeset 'customer/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Customer — Maven Build') {
                    steps { sh 'mvn clean install -pl customer -am' }
                }
                stage('Customer — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl customer -am -Dcheckstyle.output.file=customer-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/customer-checkstyle-result.xml')]
                    }
                }
                stage('Customer — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'customer/**/*-reports/TEST*.xml' }
                }
                stage('Customer — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -pl customer -am -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Customer — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Customer — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'customer/**/target/jacoco.exec',
                               classPattern: 'customer/**/target/classes',
                               sourcePattern: 'customer/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Customer — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-customer', './customer') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // inventory-ci.yaml
        // -------------------------------------------------------------------
        stage('Inventory Service CI') {
            when {
                anyOf { changeset 'inventory/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Inventory — Maven Build') {
                    steps { sh 'mvn clean install -pl inventory -am' }
                }
                stage('Inventory — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl inventory -am -Dcheckstyle.output.file=inventory-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/inventory-checkstyle-result.xml')]
                    }
                }
                stage('Inventory — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'inventory/**/*-reports/TEST*.xml' }
                }
                stage('Inventory — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -pl inventory -am -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Inventory — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Inventory — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'inventory/**/target/jacoco.exec',
                               classPattern: 'inventory/**/target/classes',
                               sourcePattern: 'inventory/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Inventory — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-inventory', './inventory') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // location-ci.yaml
        // -------------------------------------------------------------------
        stage('Location Service CI') {
            when {
                anyOf { changeset 'location/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Location — Maven Build') {
                    steps { sh 'mvn clean install -pl location -am' }
                }
                stage('Location — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl location -am -Dcheckstyle.output.file=location-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/location-checkstyle-result.xml')]
                    }
                }
                stage('Location — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'location/**/*-reports/TEST*.xml' }
                }
                stage('Location — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -pl location -am -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Location — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Location — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'location/**/target/jacoco.exec',
                               classPattern: 'location/**/target/classes',
                               sourcePattern: 'location/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Location — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-location', './location') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // media-ci.yaml
        // -------------------------------------------------------------------
        stage('Media Service CI') {
            when {
                anyOf { changeset 'media/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Media — Maven Build') {
                    steps { sh 'mvn clean install -pl media -am' }
                }
                stage('Media — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl media -am -Dcheckstyle.output.file=media-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/media-checkstyle-result.xml')]
                    }
                }
                stage('Media — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'media/**/*-reports/TEST*.xml' }
                }
                stage('Media — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -pl media -am -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Media — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Media — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'media/**/target/jacoco.exec',
                               classPattern: 'media/**/target/classes',
                               sourcePattern: 'media/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Media — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-media', './media') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // order-ci.yaml
        // -------------------------------------------------------------------
        stage('Order Service CI') {
            when {
                anyOf { changeset 'order/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Order — Maven Build') {
                    steps { sh 'mvn clean install -pl order -am' }
                }
                stage('Order — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl order -am -Dcheckstyle.output.file=order-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/order-checkstyle-result.xml')]
                    }
                }
                stage('Order — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'order/**/*-reports/TEST*.xml' }
                }
                stage('Order — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -pl order -am -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Order — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Order — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'order/**/target/jacoco.exec',
                               classPattern: 'order/**/target/classes',
                               sourcePattern: 'order/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Order — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-order', './order') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // payment-ci.yaml
        // -------------------------------------------------------------------
        stage('Payment Service CI') {
            when {
                anyOf { changeset 'payment/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Payment — Maven Build') {
                    steps { sh 'mvn clean install -pl payment -am' }
                }
                stage('Payment — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl payment -am -Dcheckstyle.output.file=payment-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/payment-checkstyle-result.xml')]
                    }
                }
                stage('Payment — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'payment/**/*-reports/TEST*.xml' }
                }
                stage('Payment — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -pl payment -am -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Payment — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Payment — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'payment/**/target/jacoco.exec',
                               classPattern: 'payment/**/target/classes',
                               sourcePattern: 'payment/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Payment — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-payment', './payment') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // payment-paypal-ci.yaml
        // -------------------------------------------------------------------
        stage('Payment-Paypal Service CI') {
            when {
                anyOf { changeset 'payment-paypal/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Payment-Paypal — Maven Build') {
                    steps { sh 'mvn clean install -pl payment-paypal -am' }
                }
                stage('Payment-Paypal — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl payment-paypal -am -Dcheckstyle.output.file=payment-paypal-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/payment-paypal-checkstyle-result.xml')]
                    }
                }
                stage('Payment-Paypal — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'payment-paypal/**/*-reports/TEST*.xml' }
                }
                stage('Payment-Paypal — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -f payment-paypal -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Payment-Paypal — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Payment-Paypal — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'payment-paypal/**/target/jacoco.exec',
                               classPattern: 'payment-paypal/**/target/classes',
                               sourcePattern: 'payment-paypal/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Payment-Paypal — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-payment-paypal', './payment-paypal') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // product-ci.yaml
        // -------------------------------------------------------------------
        stage('Product Service CI') {
            when {
                anyOf { changeset 'product/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Product — Maven Build') {
                    steps { sh 'mvn clean install -pl product -am' }
                }
                stage('Product — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl product -am -Dcheckstyle.output.file=product-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/product-checkstyle-result.xml')]
                    }
                }
                stage('Product — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'product/**/*-reports/TEST*.xml' }
                }
                stage('Product — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -f product -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Product — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Product — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'product/**/target/jacoco.exec',
                               classPattern: 'product/**/target/classes',
                               sourcePattern: 'product/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Product — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-product', './product') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // promotion-ci.yaml
        // -------------------------------------------------------------------
        stage('Promotion Service CI') {
            when {
                anyOf { changeset 'promotion/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Promotion — Maven Build') {
                    steps { sh 'mvn clean install -pl promotion -am' }
                }
                stage('Promotion — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl promotion -am -Dcheckstyle.output.file=promotion-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/promotion-checkstyle-result.xml')]
                    }
                }
                stage('Promotion — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'promotion/**/*-reports/TEST*.xml' }
                }
                stage('Promotion — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -pl promotion -am -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Promotion — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Promotion — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'promotion/**/target/jacoco.exec',
                               classPattern: 'promotion/**/target/classes',
                               sourcePattern: 'promotion/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Promotion — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-promotion', './promotion') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // rating-ci.yaml
        // -------------------------------------------------------------------
        stage('Rating Service CI') {
            when {
                anyOf { changeset 'rating/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Rating — Maven Build') {
                    steps { sh 'mvn clean install -pl rating -am' }
                }
                stage('Rating — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl rating -am -Dcheckstyle.output.file=rating-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/rating-checkstyle-result.xml')]
                    }
                }
                stage('Rating — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'rating/**/*-reports/TEST*.xml' }
                }
                stage('Rating — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -pl rating -am -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Rating — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Rating — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'rating/**/target/jacoco.exec',
                               classPattern: 'rating/**/target/classes',
                               sourcePattern: 'rating/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Rating — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-rating', './rating') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // recommendation-ci.yaml
        // -------------------------------------------------------------------
        stage('Recommendation Service CI') {
            when {
                anyOf { changeset 'recommendation/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Recommendation — Maven Build') {
                    steps { sh 'mvn clean install -pl recommendation -am' }
                }
                stage('Recommendation — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl recommendation -am -Dcheckstyle.output.file=recommendation-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/recommendation-checkstyle-result.xml')]
                    }
                }
                stage('Recommendation — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'recommendation/**/*-reports/TEST*.xml' }
                }
                stage('Recommendation — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -f recommendation -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Recommendation — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Recommendation — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'recommendation/**/target/jacoco.exec',
                               classPattern: 'recommendation/**/target/classes',
                               sourcePattern: 'recommendation/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Recommendation — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-recommendation', './recommendation') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // sampledata-ci.yaml   (no JaCoCo / JUnit — matches original)
        // -------------------------------------------------------------------
        stage('Sampledata Service CI') {
            when {
                anyOf { changeset 'sampledata/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Sampledata — Maven Build') {
                    steps { sh 'mvn clean install -pl sampledata -am' }
                }
                stage('Sampledata — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl sampledata -am -Dcheckstyle.output.file=sampledata-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/sampledata-checkstyle-result.xml')]
                    }
                }
                stage('Sampledata — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -f sampledata -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Sampledata — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Sampledata — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-sampledata', './sampledata') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // search-ci.yaml
        // -------------------------------------------------------------------
        stage('Search Service CI') {
            when {
                anyOf { changeset 'search/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Search — Maven Build') {
                    steps { sh 'mvn clean install -pl search -am' }
                }
                stage('Search — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl search -am -Dcheckstyle.output.file=search-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/search-checkstyle-result.xml')]
                    }
                }
                stage('Search — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'search/**/*-reports/TEST*.xml' }
                }
                stage('Search — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -pl search -am -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Search — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Search — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'search/**/target/jacoco.exec',
                               classPattern: 'search/**/target/classes',
                               sourcePattern: 'search/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Search — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-search', './search') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // tax-ci.yaml
        // -------------------------------------------------------------------
        stage('Tax Service CI') {
            when {
                anyOf { changeset 'tax/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Tax — Maven Build') {
                    steps { sh 'mvn clean install -pl tax -am' }
                }
                stage('Tax — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl tax -am -Dcheckstyle.output.file=tax-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/tax-checkstyle-result.xml')]
                    }
                }
                stage('Tax — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'tax/**/*-reports/TEST*.xml' }
                }
                stage('Tax — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -pl tax -am -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Tax — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Tax — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'tax/**/target/jacoco.exec',
                               classPattern: 'tax/**/target/classes',
                               sourcePattern: 'tax/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Tax — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-tax', './tax') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // webhook-ci.yaml
        // -------------------------------------------------------------------
        stage('Webhook Service CI') {
            when {
                anyOf { changeset 'webhook/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Webhook — Maven Build') {
                    steps { sh 'mvn clean install -pl webhook -am' }
                }
                stage('Webhook — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl webhook -am -Dcheckstyle.output.file=webhook-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/webhook-checkstyle-result.xml')]
                    }
                }
                stage('Webhook — Unit Test Report') {
                    steps { junit allowEmptyResults: true, testResults: 'webhook/**/*-reports/TEST*.xml' }
                }
                stage('Webhook — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -pl webhook -am -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Webhook — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Webhook — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'webhook/**/target/jacoco.exec',
                               classPattern: 'webhook/**/target/classes',
                               sourcePattern: 'webhook/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Webhook — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-webhook', './webhook') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // backoffice-bff-ci.yaml
        //   Uses -f flag (not -pl/-am); no JUnit report; uses clean verify
        // -------------------------------------------------------------------
        stage('Backoffice-BFF Service CI') {
            when {
                anyOf { changeset 'backoffice-bff/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Backoffice-BFF — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -f backoffice-bff -Dcheckstyle.output.file=backoffice-bff-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/backoffice-bff-checkstyle-result.xml')]
                    }
                }
                stage('Backoffice-BFF — Maven Verify') {
                    steps { sh 'mvn clean verify -f backoffice-bff' }
                }
                stage('Backoffice-BFF — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -f backoffice-bff -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Backoffice-BFF — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Backoffice-BFF — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-backoffice-bff', './backoffice-bff') } }
                }
            }
        }

        // -------------------------------------------------------------------
        // storefront-bff-ci.yaml
        // -------------------------------------------------------------------
        stage('Storefront-BFF Service CI') {
            when {
                anyOf { changeset 'storefront-bff/**'; changeset 'pom.xml'; branch MAIN_BRANCH }
            }
            stages {
                stage('Storefront-BFF — Maven Build') {
                    steps { sh 'mvn clean install -pl storefront-bff -am' }
                }
                stage('Storefront-BFF — Checkstyle') {
                    steps {
                        sh 'mvn checkstyle:checkstyle -pl storefront-bff -am -Dcheckstyle.output.file=storefront-bff-checkstyle-result.xml'
                        recordIssues tools: [checkStyle(pattern: '**/storefront-bff-checkstyle-result.xml')]
                    }
                }
                stage('Storefront-BFF — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -f storefront-bff -Dsonar.token=$SONAR_TOKEN'
                        }
                    }
                }
                stage('Storefront-BFF — OWASP Dependency Check') {
                    steps {
                        dependencyCheck additionalArguments: '--project yas --scan . --format HTML --out reports/', odcInstallation: 'OWASP-DC'
                        dependencyCheckPublisher pattern: 'reports/dependency-check-report.html'
                    }
                }
                stage('Storefront-BFF — JaCoCo Coverage') {
                    steps {
                        jacoco execPattern: 'storefront-bff/**/target/jacoco.exec',
                               classPattern: 'storefront-bff/**/target/classes',
                               sourcePattern: 'storefront-bff/**/src/main/java',
                               minimumInstructionCoverage: '80',
                               minimumBranchCoverage: '60'
                    }
                }
                stage('Storefront-BFF — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-storefront-bff', './storefront-bff') } }
                }
            }
        }

        // =====================================================================
        // 4. NODE.JS FRONTENDS CI
        // =====================================================================

        // -------------------------------------------------------------------
        // backoffice-ci.yaml
        //   Extra steps vs storefront: npm audit (continue-on-error) + Trivy
        // -------------------------------------------------------------------
        stage('Backoffice Frontend CI') {
            when {
                anyOf { changeset 'backoffice/**'; branch MAIN_BRANCH }
            }
            stages {
                stage('Backoffice — npm ci') {
                    steps { dir('backoffice') { sh 'npm ci' } }
                }
                stage('Backoffice — Build') {
                    steps { dir('backoffice') { sh 'npm run build' } }
                }
                stage('Backoffice — Lint') {
                    steps { dir('backoffice') { sh 'npm run lint' } }
                }
                stage('Backoffice — Prettier Check') {
                    steps { dir('backoffice') { sh 'npx prettier --check .' } }
                }
                stage('Backoffice — npm audit') {
                    // continue-on-error: true  →  catchError keeps build green
                    steps {
                        catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                            dir('backoffice') { sh 'npm audit --omit=dev' }
                        }
                    }
                }
                stage('Backoffice — Trivy FS Scan') {
                    // Mirrors: aquasecurity/trivy-action scan-type: fs  scan-ref: ./backoffice
                    steps {
                        sh '''
                            docker run --rm \
                                -v "$(pwd)":/src \
                                aquasec/trivy:latest fs \
                                --format sarif \
                                --output /src/trivy-fs-results.sarif \
                                /src/backoffice
                        '''
                        archiveArtifacts artifacts: 'trivy-fs-results.sarif', allowEmptyArchive: true
                    }
                }
                stage('Backoffice — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            dir('backoffice') {
                                sh 'npx sonarqube-scanner -Dsonar.host.url=https://sonarcloud.io -Dsonar.token=$SONAR_TOKEN'
                            }
                        }
                    }
                }
                stage('Backoffice — Docker Build') {
                    // Build first (no push) so Trivy image scan can run before push
                    when { branch MAIN_BRANCH }
                    steps {
                        script {
                            withCredentials([
                                string(credentialsId: 'GHCR_USERNAME', variable: 'GHCR_USER'),
                                string(credentialsId: 'GHCR_TOKEN',    variable: 'GHCR_PASS')
                            ]) {
                                sh """
                                    echo "\$GHCR_PASS" | docker login ${GHCR_REGISTRY} -u "\$GHCR_USER" --password-stdin
                                    docker build -t ${GHCR_REGISTRY}/${GHCR_ORG}/yas-backoffice:latest ./backoffice
                                """
                            }
                        }
                    }
                }
                stage('Backoffice — Trivy Image Scan') {
                    // Mirrors: aquasecurity/trivy-action image-ref scan on main
                    when { branch MAIN_BRANCH }
                    steps {
                        sh """
                            docker run --rm \
                                -v /var/run/docker.sock:/var/run/docker.sock \
                                aquasec/trivy:latest image \
                                --format sarif \
                                --output trivy-image-results.sarif \
                                ${GHCR_REGISTRY}/${GHCR_ORG}/yas-backoffice:latest
                        """
                        archiveArtifacts artifacts: 'trivy-image-results.sarif', allowEmptyArchive: true
                    }
                }
                stage('Backoffice — Docker Push') {
                    when { branch MAIN_BRANCH }
                    steps {
                        sh "docker push ${GHCR_REGISTRY}/${GHCR_ORG}/yas-backoffice:latest"
                    }
                }
            }
        }

        // -------------------------------------------------------------------
        // storefront-ci.yaml  (Next.js, Node 20)
        // -------------------------------------------------------------------
        stage('Storefront Frontend CI') {
            when {
                anyOf { changeset 'storefront/**'; branch MAIN_BRANCH }
            }
            stages {
                stage('Storefront — npm ci') {
                    steps { dir('storefront') { sh 'npm ci' } }
                }
                stage('Storefront — Build') {
                    steps { dir('storefront') { sh 'npm run build' } }
                }
                stage('Storefront — Lint') {
                    steps { dir('storefront') { sh 'npm run lint' } }
                }
                stage('Storefront — Prettier Check') {
                    steps { dir('storefront') { sh 'npx prettier --check .' } }
                }
                stage('Storefront — SonarCloud') {
                    steps {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            dir('storefront') {
                                sh 'npx sonarqube-scanner -Dsonar.host.url=https://sonarcloud.io -Dsonar.token=$SONAR_TOKEN'
                            }
                        }
                    }
                }
                stage('Storefront — Docker Build & Push') {
                    when { branch MAIN_BRANCH }
                    steps { script { dockerBuildAndPush('yas-storefront', './storefront') } }
                }
            }
        }

        // =====================================================================
        // 5. HELM CHARTS RELEASE
        //    Mirrors: charts-ci.yaml  (push to main + k8s/charts/** changeset)
        // =====================================================================
        stage('Helm Charts Release') {
            when {
                allOf {
                    branch MAIN_BRANCH
                    changeset 'k8s/charts/**'
                }
            }
            stages {
                stage('Charts — Configure Git') {
                    steps {
                        script {
                            withCredentials([string(credentialsId: 'GHCR_USERNAME', variable: 'GIT_USER')]) {
                                sh """
                                    git config user.name "\$GIT_USER"
                                    git config user.email "\$GIT_USER@users.noreply.github.com"
                                """
                            }
                        }
                    }
                }
                stage('Charts — Install Helm') {
                    steps {
                        sh 'curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash'
                    }
                }
                stage('Charts — Add Stakater Repo') {
                    // Mirrors: helm repo add stakater https://stakater.github.io/stakater-charts
                    steps {
                        sh 'helm repo add stakater https://stakater.github.io/stakater-charts'
                    }
                }
                stage('Charts — Package & Release') {
                    // Mirrors: helm/chart-releaser-action@v1.5.0  charts_dir: k8s/charts
                    steps {
                        script {
                            withCredentials([string(credentialsId: 'GHCR_TOKEN', variable: 'CR_TOKEN')]) {
                                sh '''
                                    docker run --rm \
                                        -e CR_TOKEN="$CR_TOKEN" \
                                        -v "$(pwd)":/workspace \
                                        -w /workspace \
                                        quay.io/helmpack/chart-releaser:v1.5.0 \
                                        release \
                                        --charts-dir k8s/charts \
                                        --git-base-url https://api.github.com
                                '''
                            }
                        }
                    }
                }
            }
        }

    } // end stages

    // =========================================================================
    // Post actions
    // =========================================================================
    post {
        always {
            // Clean up dangling Docker images to conserve disk space
            sh 'docker image prune -f || true'
            // Archive any OWASP reports produced during the run
            archiveArtifacts artifacts: 'reports/**', allowEmptyArchive: true
        }
        success {
            echo "✅  Pipeline succeeded — branch: ${env.BRANCH_NAME}"
        }
        failure {
            echo "❌  Pipeline FAILED — branch: ${env.BRANCH_NAME}"
        }
        unstable {
            echo "⚠️   Pipeline UNSTABLE (e.g. npm audit warning) — branch: ${env.BRANCH_NAME}"
        }
    }
}
