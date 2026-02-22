def CHANGED_SERVICES = ""
def IS_ROOT_CHANGED = false
def BUILD_BACKOFFICE = false
def BUILD_STOREFRONT = false

def runFrontendPipeline(String appName, boolean isMainOrPR) {
    dir(appName) {
        echo "Installing ${appName} dependencies..."
        sh 'npm ci'

        echo "Checking code quality (Linting)..."
        sh 'npm run lint'

        echo "Running SonarQube analysis for ${appName}..."
        withSonarQubeEnv('SonarQube-Local') {
            def scannerHome = tool 'SonarScanner'
            sh "${scannerHome}/bin/sonar-scanner"
        }

        echo "Scanning ${appName} dependencies..."
        withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
            def snykHome = tool name: 'snyk-latest', type: 'io.snyk.jenkins.tools.SnykInstallation'
            def snykCmd = "${snykHome}/snyk-linux"
            
            if (isMainOrPR) {
                sh "${snykCmd} test --severity-threshold=high"
            } else {
                sh "${snykCmd} test --severity-threshold=high || true"
            }
        }

        echo "Building ${appName} UI..."
        sh 'npm run build'
    }
}

pipeline {
    agent any

    tools {
        maven 'maven3'
        nodejs 'node20'
    }

    environment {
        // Use local repository within the workspace for faster caching
        MAVEN_OPTS = "-Dmaven.repo.local=${WORKSPACE}/.m2/repository"
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

        // --- STAGE 2: SECRET SCAN ---
        stage('Secret Scan') {
            steps {
                script {
                    echo "Checking for secrets..."

                    if (!fileExists('gitleaks')) {
                        echo "Downloading Gitleaks..."
                        sh 'curl -ssfL https://github.com/gitleaks/gitleaks/releases/download/v8.18.2/gitleaks_8.18.2_linux_x64.tar.gz | tar -xz gitleaks'
                    }

                    sh 'chmod +x gitleaks'

                    try {
                        sh './gitleaks detect --source . --config gitleaks.toml --verbose --no-git'
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error("Gitleaks found secrets in your code")
                    }
                }
            }
        }

        // --- STAGE 3: ANALYZE CHANGES ---
        stage('Analyze Changes') {
            steps {
                script {
                    echo "Analyzing changes to determine build scope..."

                    def baseBranch = env.CHANGE_TARGET ?: 'main'
                    def diffCommand = ""

                    if (env.BRANCH_NAME == 'main') {
                        def hasParent = sh(script: "git rev-parse HEAD~1", returnStatus: true) == 0
                        if (hasParent) {
                            diffCommand = "git diff --name-only HEAD~1 HEAD"
                        } else {
                            diffCommand = "git show --name-only --pretty='' HEAD"
                        }
                    } else {
                        sh "git fetch origin ${baseBranch}:refs/remotes/origin/${baseBranch} --depth=10"
                        diffCommand = "git diff --name-only origin/${baseBranch} HEAD"
                    }

                    def changedFilesList = []
                    try {
                        changedFilesList = sh(script: diffCommand, returnStdout: true).trim().split('\n')
                    } catch (Exception e) {
                        echo "First build or error detected. Need to build ALL."
                        IS_ROOT_CHANGED = true
                    }
                    echo "List of changed files:\n${changedFilesList.join('\n')}"

                    def VALID_BACKEND_SERVICES = [
                        "media", "product", "cart", "order", "rating",
                        "customer", "location", "inventory", "tax", "search"
                    ]

                    def servicesToBuild = [] as Set

                    // Iterate through changed files to detect affected services
                    for (file in changedFilesList) {
                        if (!file) continue

                        // if (file == "pom.xml" || file == "Jenkinsfile") {
                        if (file == "pom.xml") {
                            IS_ROOT_CHANGED = true
                        }

                        if (file.startsWith("common-library/")) {
                            echo "Common library changed. Marking all valid backend services..."
                            servicesToBuild.addAll(VALID_BACKEND_SERVICES)
                        }

                        if (file.startsWith("backoffice/")) BUILD_BACKOFFICE = true
                        if (file.startsWith("storefront/")) BUILD_STOREFRONT = true

                        def pathParts = file.split('/')
                        if (pathParts.length > 1) {
                            def topLevelDir = pathParts[0]
                            if (VALID_BACKEND_SERVICES.contains(topLevelDir)) {
                                servicesToBuild.add(topLevelDir)
                            }
                        }
                    }

                    if (IS_ROOT_CHANGED) {
                        echo "Root configuration changed. Building ALL services."
                        BUILD_BACKOFFICE = true
                        BUILD_STOREFRONT = true
                    } else {
                        // Join with commas for Maven (e.g., "cart,order")
                        CHANGED_SERVICES = servicesToBuild.join(",")
                    }
                    
                    echo "----- BUILD PLAN -----"
                    echo "Backend Services: ${IS_ROOT_CHANGED ? 'ALL' : (CHANGED_SERVICES ?: 'N/A')}"
                    echo "Frontend Backoffice: ${BUILD_BACKOFFICE}"
                    echo "Frontend Storefront: ${BUILD_STOREFRONT}"
                    echo "----------------------"
                }
            }
        }

        // --- STAGE 4: INTEGRATION & VALIDATION ---
        stage('Integration & Validation') {
            parallel {
                // Backend Services (Spring Boot)
                stage('Backend Pipeline') {
                    when { expression { return IS_ROOT_CHANGED || CHANGED_SERVICES != "" } }
                    stages {
                        // --- PHASE 1: BUILD, TEST & COVERAGE ---
                        stage('Build & Test') {
                            steps {
                                script {
                                    echo "Building, testing and installing artifacts..."
                                    if (IS_ROOT_CHANGED) {
                                        sh 'mvn clean install jacoco:report -DskipITs'
                                    } else {
                                        sh "mvn clean install jacoco:report -DskipITs -pl ${CHANGED_SERVICES} -am"
                                    }
                                }
                            }
                            post {
                                always {
                                    script {
                                        // Upload test results
                                        junit '**/target/surefire-reports/*.xml'

                                        def VALID_BACKEND_SERVICES = []
                                        if (IS_ROOT_CHANGED) {
                                            VALID_BACKEND_SERVICES = [
                                                "media", "product", "cart", "order", "rating",
                                                "customer", "location", "inventory", "tax", "search"
                                            ]
                                        } else {
                                            VALID_BACKEND_SERVICES = CHANGED_SERVICES.split(',')
                                        }

                                        for (String service : VALID_BACKEND_SERVICES) {
                                            if (service.trim() == "") continue

                                            // Upload coverage results
                                            recordCoverage(
                                                id: "coverage-${service}",
                                                name: "Coverage: ${service.capitalize()}",
                                                tools: [[
                                                    parser: 'JACOCO', 
                                                    pattern: "${service}/target/site/jacoco/jacoco.xml"
                                                ]],
                                                qualityGates: [
                                                    [threshold: 70.0, metric: 'LINE', baseline: 'PROJECT', criticality: 'UNSTABLE'],
                                                    [threshold: 70.0, metric: 'BRANCH', baseline: 'PROJECT', criticality: 'FAILURE'],
                                                    [threshold: 70.0, metric: 'INSTRUCTION', baseline: 'PROJECT', criticality: 'FAILURE'],
                                                    [threshold: 70.0, metric: 'METHOD', baseline: 'PROJECT', criticality: 'UNSTABLE'],
                                                    [threshold: 70.0, metric: 'CLASS', baseline: 'PROJECT', criticality: 'FAILURE']
                                                ]
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // --- PHASE 2: CODE QUALITY (SONARQUBE) ---
                        stage('SonarQube Analysis') {
                            steps {
                                script {
                                    echo "Running SonarQube analysis..."
                                    withSonarQubeEnv('SonarQube-Local') {
                                        def VALID_BACKEND_SERVICES = []
                                        if (IS_ROOT_CHANGED) {
                                            VALID_BACKEND_SERVICES = [
                                                "media", "product", "cart", "order", "rating",
                                                "customer", "location", "inventory", "tax", "search"
                                            ]
                                        } else {
                                            VALID_BACKEND_SERVICES = CHANGED_SERVICES.split(',')
                                        }

                                        for (String service : VALID_BACKEND_SERVICES) {
                                            if (service.trim() == "") continue
                                            
                                            echo ">>> SonarQube scanning: ${service}"
                                            dir(service) {
                                                sh 'mvn sonar:sonar'
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // --- PHASE 3: QUALITY GATE ---
                        stage("Quality Gate") {
                            steps {
                                echo "Checking quality of code..."
                                timeout(time: 2, unit: 'MINUTES') {
                                    waitForQualityGate abortPipeline: true
                                }
                            }
                        }

                        // --- PHASE 4: VULNERABILITY SCAN (SNYK) ---
                        // stage('Vulnerability Scan') {
                        //     steps {
                        //         script {
                        //             echo "Scanning backend dependencies..."
                        //             withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                        //                 def snykHome = tool name: 'snyk-latest', type: 'io.snyk.jenkins.tools.SnykInstallation'
                        //                 def snykCmd = "${snykHome}/snyk-linux"
                        //                 def isMainOrPR = (env.BRANCH_NAME == 'main' || (env.CHANGE_ID && env.CHANGE_TARGET == 'main'))

                        //                 if (IS_ROOT_CHANGED) {
                        //                     if (isMainOrPR) {
                        //                         sh "${snykCmd} test --all-projects --severity-threshold=high"
                        //                     } else {
                        //                         sh "${snykCmd} test --all-projects --severity-threshold=high || true"
                        //                     }
                        //                 } else {
                        //                     def services = CHANGED_SERVICES.split(',')
                        //                     for (service in services) {
                        //                         echo ">>> Snyk scanning: ${service}"
                        //                         dir(service) {
                        //                             sh 'chmod +x ./mvnw'
                        //                             if (isMainOrPR) {
                        //                                 sh "${snykCmd} test --severity-threshold=high"
                        //                             } else {
                        //                                 sh "${snykCmd} test --severity-threshold=high || true"
                        //                             }
                        //                         }
                        //                     }
                        //                 }
                        //             }
                        //         }
                        //     }
                        // }
                    }
                }

                // Frontend Backoffice (NextJS)
                stage('Backoffice Pipeline') {
                    when { expression { return BUILD_BACKOFFICE || IS_ROOT_CHANGED } }
                    steps {
                        script {
                            def isMainOrPR = (env.BRANCH_NAME == 'main' || (env.CHANGE_ID && env.CHANGE_TARGET == 'main'))
                            runFrontendPipeline('backoffice', isMainOrPR)
                        }
                    }
                }

                // Frontend Storefront (NextJS)
                stage('Storefront Pipeline') {
                    when { expression { return BUILD_STOREFRONT || IS_ROOT_CHANGED } }
                    steps {
                        script {
                            def isMainOrPR = (env.BRANCH_NAME == 'main' || (env.CHANGE_ID && env.CHANGE_TARGET == 'main'))
                            runFrontendPipeline('storefront', isMainOrPR)
                        }
                    }
                }
            }
        }
    }
}
