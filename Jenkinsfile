pipeline {
    agent any

    tools {
        maven 'maven-3.9'
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token')
    }

    stages {
        stage('Initialize & Clean') {
            steps {
                cleanWs()
                checkout scm
                script {
                    sh 'git fetch origin main --quiet'

                    def targetBranch = env.CHANGE_TARGET ?: 'main'
                    def baseRef = sh(
                        script: """
                            git merge-base HEAD origin/${targetBranch} 2>/dev/null \
                            || git merge-base HEAD origin/master 2>/dev/null \
                            || echo ''
                        """,
                        returnStdout: true
                    ).trim()

                    def currentHead = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
                    if (!baseRef || baseRef == currentHead) {
                        baseRef = sh(
                            script: "git rev-parse HEAD^1 2>/dev/null || echo ''",
                            returnStdout: true
                        ).trim()
                        echo "On target branch — using HEAD^1: ${baseRef}"
                    }

                    env.GIT_BASE_REF = baseRef

                    def changedFiles = ''
                    if (env.GIT_BASE_REF) {
                        changedFiles = sh(
                            script: "git diff --name-only ${env.GIT_BASE_REF} HEAD 2>/dev/null || echo ''",
                            returnStdout: true
                        ).trim()
                    }

                    echo "Base ref: ${env.GIT_BASE_REF ?: '(none — build all)'}"
                    echo "Changed files:\n${changedFiles ?: '(none — build all)'}"

                    def allServices = ['backoffice-bff', 'cart', 'customer', 'inventory', 'location',
                                    'media', 'order', 'payment', 'payment-paypal', 'product',
                                    'promotion', 'rating', 'recommendation', 'sampledata', 'search',
                                    'storefront-bff', 'tax', 'webhook', 'common-library']

                    def fileList = changedFiles ? changedFiles.split('\n') as List : []

                    def commonChanged = fileList.any { it.startsWith('common-library/') }

                    def servicesToBuild
                    if (!changedFiles || commonChanged) {
                        servicesToBuild = allServices
                    } else {
                        servicesToBuild = allServices.findAll { svc ->
                            fileList.any { it.startsWith("${svc}/") }
                        }
                    }

                    echo "Services to build: ${servicesToBuild}"
                    env.SERVICES_TO_BUILD = servicesToBuild.join(',')
                }

                echo 'Workspace cleaned and initialized.'
            }
        }

        stage('Secret Scan (Gitleaks)') {
            steps {
                script {
                    def logOpts = env.GIT_BASE_REF ? "${env.GIT_BASE_REF}..HEAD" : "-1"
                    echo "Gitleaks scanning commits: ${logOpts}"

                    sh """
                        gitleaks detect --source . \
                        --log-opts="${logOpts}" \
                        --report-format sarif \
                        --report-path gitleaks-report.sarif \
                        --redact \
                        --exit-code 0
                    """
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'gitleaks-report.sarif', allowEmptyArchive: true
                }
            }
        }

        stage('Global Dependencies Setup & Common Library') {
            steps {
                script {
                    sh 'mvn install -N -DskipTests'
                    
                    def services = env.SERVICES_TO_BUILD.tokenize(',')
                    if (services.contains('common-library')) {
                        echo "Common library changed (or full build). Testing & scanning sequentially first!"
                        buildService('common-library')
                        
                        services.remove('common-library')
                        env.SERVICES_TO_BUILD = services.join(',')
                    } else {
                        echo "Common library unchanged. Installing quickly to cache without tests..."
                        sh 'mvn clean install -DskipTests -pl common-library -am'
                    }
                }
            }
        }

        stage('Microservices Pipelines') {
            steps {
                script {
                    def services = env.SERVICES_TO_BUILD.tokenize(',')
                    def chunks = services.collate(4)

                    for (chunk in chunks) {
                        def parallelBuilds = [:]
                        chunk.each { serviceName ->
                            def svc = serviceName
                            parallelBuilds[svc] = {
                                buildService(svc)
                            }
                        }
                        parallel parallelBuilds
                    }
                }
            }
        }

        stage('Security Scan (Snyk - Root)') {
            when { changeRequest() }
            steps {
                echo 'Running Snyk scan for entire monorepo...'

                snykSecurity(
                    snykInstallation: 'snyk-tool',
                    snykTokenId: 'snyk-token',
                    failOnIssues: false,
                    targetFile: 'pom.xml',
                    additionalArguments: '--maven-aggregate-project'
                )
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            recordIssues(
                tools: [sarif(pattern: 'gitleaks-report.sarif')],
                id: 'gitleaks',
                name: 'Gitleaks Scan',
                skipPublishingChecks: false
            )

            script {
                def buildResult = currentBuild.currentResult ?: 'SUCCESS'
                def conclusion = (buildResult == 'SUCCESS') ? 'SUCCESS' : 'FAILURE'
                
                def mySummary = """### Yas Monorepo Pipeline Results
* **Build Status:** ${buildResult}
* **Triggered By:** ${env.CHANGE_AUTHOR ?: 'Auto'}
* **Duration:** ${currentBuild.durationString.replace(' and counting', '')}
* **Processed Services:** `${env.SERVICES_TO_BUILD ?: 'None'}`
"""
                def myText = "### Links\n* [Jenkins Console Details](${env.BUILD_URL}console)\n* [JUnit Report](${env.BUILD_URL}testReport)"

                publishChecks name: 'Yas Pipeline Summary', 
                    title: "Build ${buildResult}", 
                    summary: mySummary,
                    text: myText,
                    status: 'COMPLETED',
                    conclusion: conclusion
            }
        }

        success {
            echo 'Aggregating Jacoco coverage reports and enforcing 70% threshold...'
            recordCoverage(
                tools: [[parser: 'JACOCO', pattern: '**/target/site/jacoco/jacoco.xml']],
                sourceDirectories: getSourcePaths(),
                qualityGates: [
                    [threshold: 70.0, metric: 'LINE', baseline: 'PROJECT', criticality: 'FAILURE']
                ]
            )
        }
    }
}

def buildService(String serviceName) {
    echo "--- Processing Service: ${serviceName} ---"

    def mvnCmd = "mvn -Drevision=1.0-SNAPSHOT -pl ${serviceName}"

    sh "${mvnCmd} install -DskipTests"
    sh "${mvnCmd} test jacoco:report"

    withSonarQubeEnv('yas') {
        sh """
            mvn -Drevision=1.0-SNAPSHOT sonar:sonar \
            -pl .,${serviceName} \
            -Dsonar.projectKey=thuonghong_yas-${serviceName} \
            -Dsonar.projectName=yas-${serviceName} \
            -Dsonar.coverage.jacoco.xmlReportPaths=${WORKSPACE}/${serviceName}/target/site/jacoco/jacoco.xml \
            -Dsonar.working.directory=${WORKSPACE}/target/sonar-${serviceName}
        """
    }
}

def getSourcePaths() {
    def services = [
        'search', 'media', 'cart', 'customer', 'inventory', 'location',
        'order', 'payment', 'payment-paypal', 'product', 'promotion',
        'rating', 'recommendation', 'sampledata', 'storefront-bff',
        'tax', 'webhook', 'backoffice-bff', 'common-library'
    ]
    return services.collect { [path: "${it}/src/main/java"] }
}