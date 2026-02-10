def CHANGED_SERVICES = ""
def IS_ROOT_CHANGED = false
def BUILD_BACKOFFICE = false
def BUILD_STOREFRONT = false

pipeline {
    agent any

    tools {
        maven 'maven3'
        nodejs 'node20'
    }

    environment {
        // Use local repository within the workspace for faster caching
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
                    echo "Analyzing changes to determine build scope..."

                    def baseBranch = env.CHANGE_TARGET ?: 'main'
                    def diffCommand = ""

                    if (env.BRANCH_NAME == 'main') {
                        diffCommand = "git diff --name-only HEAD~1 HEAD"
                    } else {
                        sh "git fetch origin ${baseBranch}:refs/remotes/origin/${baseBranch}"
                        diffCommand = "git diff --name-only origin/${baseBranch} HEAD"
                    }
                    
                    // Get the list of changed files
                    def changedFilesList = []
                    try {
                        def changedOutput = sh(script: diffCommand, returnStdout: true).trim()
                        changedFilesList = changedOutput.split('\n')
                    } catch (Exception e) {
                        echo "First build or error detected. Need to build ALL."
                        IS_ROOT_CHANGED = true
                    }
                    echo "List of changed files: ${changedFilesList}"

                    def services = [
                        "media/", "product/", "cart/", "order/", "rating/",
                        "customer/", "location/", "inventory/", "tax/", "search/"
                    ]

                    def servicesToBuild = []

                    // Iterate through changed files to detect affected services
                    for (file in changedFilesList) {
                        // If root configuration files change, rebuild ALL
                        if (file == "pom.xml") {
                            IS_ROOT_CHANGED = true
                        }

                        // Check backend services
                        for (svcPath in services) {
                            if (file.startsWith(svcPath)) {
                                // Extract service name (e.g., "cart/" -> "cart")
                                servicesToBuild.add(svcPath.replace("/", ""))
                            }
                        }

                        // Check frontend services
                        if (file.startsWith("backoffice/")) BUILD_BACKOFFICE = true
                        if (file.startsWith("storefront/")) BUILD_STOREFRONT = true
                        
                        // Check common library
                        // If common library changes, we must rebuild services that depend on it
                        if (file.startsWith("common-library/")) {
                            echo "Common library changed. Marking dependent services for build..."
                            servicesToBuild.addAll([
                                "media", "product", "cart", "order", "rating",
                                "customer", "location", "inventory", "tax", "search"
                            ])
                        }
                    }

                    // Finalize build plan
                    if (IS_ROOT_CHANGED) {
                        echo "Root configuration changed. Building ALL services."
                        BUILD_BACKOFFICE = true
                        BUILD_STOREFRONT = true
                    } else {
                        // Remove duplicates and join with commas for Maven (e.g., "cart,order")
                        CHANGED_SERVICES = servicesToBuild.unique().join(",")
                    }
                    
                    echo "----- BUILD PLAN -----"
                    echo "Backend Services: ${IS_ROOT_CHANGED ? 'ALL' : (CHANGED_SERVICES ?: 'None')}"
                    echo "Frontend Backoffice: ${BUILD_BACKOFFICE}"
                    echo "Frontend Storefront: ${BUILD_STOREFRONT}"
                    echo "----------------------"
                }
            }
        }

        // --- STAGE 3: BUILD & TEST ---
        stage('Build & Test') {
            parallel {
                // Backend Services (Spring Boot)
                stage('Backend Pipeline') {
                    when { expression { return IS_ROOT_CHANGED || CHANGED_SERVICES != "" } }
                    stages {
                        // --- PHASE 1: UNIT TEST & COVERAGE ---
                        stage('Unit Test') {
                            steps {
                                script {
                                    echo "Phase 1: Running tests & SonarQube analysis..."

                                    withSonarQubeEnv('SonarQube-Local') {
                                        if (IS_ROOT_CHANGED) {
                                            // Clean and Test everything
                                            sh 'mvn clean test jacoco:report sonar:sonar'
                                        } else {
                                            // Clean and Test changed services + dependencies (-am)
                                            sh "mvn clean test jacoco:report sonar:sonar -pl ${CHANGED_SERVICES} -am"
                                        }
                                    }
                                }
                            }

                            post {
                                always {
                                    // Upload test results
                                    junit '**/target/surefire-reports/*.xml'

                                    // Upload coverage results
                                    recordCoverage(
                                        tools: [[
                                            parser: 'JACOCO', 
                                            pattern: '**/target/site/jacoco/jacoco.xml'
                                        ]],
                                        // qualityGates: [
                                        //     [threshold: 70.0, metric: 'LINE', baseline: 'PROJECT', criticality: 'UNSTABLE'],
                                        //     [threshold: 60.0, metric: 'BRANCH', baseline: 'PROJECT', criticality: 'FAILURE'],
                                        //     [threshold: 70.0, metric: 'INSTRUCTION', baseline: 'PROJECT', criticality: 'FAILURE'],
                                        //     [threshold: 80.0, metric: 'METHOD', baseline: 'PROJECT', criticality: 'UNSTABLE']
                                        //     [threshold: 70.0, metric: 'CLASS', baseline: 'PROJECT', criticality: 'FAILURE']
                                        // ]
                                    )
                                }
                            }
                        }

                        // --- PHASE 2: QUALITY CHECK ---
                        stage("Quality Gate") {
                            steps {
                                echo "Phase 2: Checking quality of code..."
                                timeout(time: 5, unit: 'MINUTES') {
                                    waitForQualityGate abortPipeline: true
                                }
                            }
                        }

                        // --- PHASE 3: BUILD ARTIFACT ---
                        stage('Build Artifacts') {
                            steps {
                                script {
                                    echo "Phase 3: Building artifacts..."
                                    if (IS_ROOT_CHANGED) {
                                        sh 'mvn package -DskipTests'
                                    } else {
                                        sh "mvn package -DskipTests -pl ${CHANGED_SERVICES} -am"
                                    }
                                }
                            }
                        }
                    }
                }

                // Frontend Backoffice (NextJS)
                stage('Backoffice Pipeline') {
                    when { expression { return BUILD_BACKOFFICE || IS_ROOT_CHANGED } }
                    steps {
                        dir('backoffice') {
                            echo "Building Backoffice UI..."
                            sh 'npm install'
                            sh 'npm run build'
                        }
                    }
                }

                // Frontend Storefront (NextJS)
                stage('Storefront Pipeline') {
                    when { expression { return BUILD_STOREFRONT || IS_ROOT_CHANGED } }
                    steps {
                        dir('storefront') {
                            echo "Building Storefront UI..."
                            sh 'npm install'
                            sh 'npm run build'
                        }
                    }
                }
            }
        }
    }
}
