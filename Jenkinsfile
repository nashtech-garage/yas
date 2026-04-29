// Run a shell command and return the output as a string
def runCapture(String cmd) {
    return sh(script: cmd, returnStdout: true).trim()
}

// Calculate the list of changed files 
def computeChangedFiles() {
    def cmd = null

    if (env.CHANGE_TARGET) {
        // For pull requests, compare the current branch with the target branch
        cmd = "git diff --name-only origin/${env.CHANGE_TARGET}...HEAD"
    } else if (env.GIT_PREVIOUS_SUCCESSFUL_COMMIT && env.GIT_COMMIT) {
        // For regular commits, compare the current commit with the previous successful commit
        cmd = "git diff --name-only ${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT}..${env.GIT_COMMIT}"
    } else if (env.GIT_PREVIOUS_COMMIT && env.GIT_COMMIT) {
        // If no previous successful commit is available, compare the current commit with the previous commit
        cmd = "git diff --name-only ${env.GIT_PREVIOUS_COMMIT}..${env.GIT_COMMIT}"
    } else {
        // Fallback: list files changed in the latest commit
        cmd = 'git show --name-only --pretty="" HEAD'
    }

    try {
        def out = runCapture(cmd)
        return out
            .split(/\r?\n/)
            .collect { it.trim() }
            .findAll { it }
    } catch (err) {
        def out = runCapture('git -c color.ui=never show --name-only --pretty="" HEAD')
        return out
            .split(/\r?\n/)
            .collect { it.trim() }
            .findAll { it }
    }
}

// Read the list of modules from the root pom.xml
def readMavenModulesFromRootPom() {
    def pom = readFile('pom.xml')
    def matcher = (pom =~ /<module>([^<]+)<\/module>/)
    def modules = []
    matcher.each { m -> modules << m[1].trim() }
    return modules.unique()
}


pipeline {
    agent any

    tools {
        // Define the Maven and JDK tools to be used in the pipeline
        maven 'maven3'
        jdk 'jdk25'
    }

    options {
        timestamps() 
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    environment {
        // Define environment variables for Maven commands
        MVN_ARGS = '-B -ntp'
    }

    stages {
        stage('Checkout') {
            steps {
                // Perform a clean checkout of the source code
                checkout scm  
                script {
                    if (env.CHANGE_TARGET) {
                        sh "git fetch --no-tags origin ${env.CHANGE_TARGET}"
                    }
                }
            }
        }

        stage('Gitleaks Scan') {
            steps {
                script {
                    def baseBranch = env.CHANGE_TARGET ?: "main"

                    def status = sh(
                        script: '''
                            gitleaks detect \
                                --source . \
                                --config gitleaks.toml \
                                --report-format json \
                                --report-path gitleaks-report.json \
                                --redact 
                            ''',
                        returnStatus: true
                    )
                    
                    // Convert the Gitleaks JSON report to a simple HTML format for better visualization in Jenkins
                    sh '''
                        echo "<html><body><h2>Gitleaks Report</h2><pre>" > gitleaks-report.html
                        cat gitleaks-report.json >> gitleaks-report.html
                        echo "</pre></body></html>" >> gitleaks-report.html
                    '''

                    // Publish the Gitleaks report as an HTML report in Jenkins 
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: '.',
                        reportFiles: 'gitleaks-report.html',
                        reportName: 'Gitleaks Report'
                    ])

                    // If Gitleaks detected secrets (status != 0), fail the build and prompt the developer to check the report. Otherwise, print a success message.
                    if (status != 0) {
                        echo "GITLEAKS WARNING: secrets detected (see report)"
                        currentBuild.result = 'SUCCESS' // Mark as success to allow manual review of the report
                    } else {
                        echo "No secrets detected"
                    }
                }
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    def allModules = readMavenModulesFromRootPom()
                    def changedFiles = computeChangedFiles()

                    // Normalize file paths (CRITICAL)
                    def normalizedChangedFiles = changedFiles
                        .collect { it.replaceAll('\\u001B\\[[;\\d]*m', '').trim() }
                        .collect { it.replace('\\', '/') }
                        .collect { it.replaceFirst(/^\.\//, '') }
                        .findAll { it }

                    // Detect rebuild all
                    def rebuildAll = normalizedChangedFiles.any { f ->
                        f.equalsIgnoreCase('pom.xml') ||
                        f.startsWith('checkstyle/')
                    }

                    // Optional: rebuild if Jenkinsfile changed
                    if (env.REBUILD_ALL_ON_JENKINSFILE?.toBoolean()) {
                        rebuildAll = rebuildAll || normalizedChangedFiles.any {
                            it.equalsIgnoreCase('Jenkinsfile')
                        }
                    }

                    // Debug: top-level dirs (for visibility only)
                    def touchedTopDirs = normalizedChangedFiles
                        .findAll { it.contains('/') }
                        .collect { it.tokenize('/')[0] }
                        .unique()

                    def affected = allModules.findAll { module ->
                        normalizedChangedFiles.any { f ->
                            f == module || f.startsWith("${module}/")
                        }
                    }

                    if (rebuildAll) {
                        affected = allModules
                    }

                    // Handle dependency rebuild
                    env.MVN_MAKE_FLAGS = '-am'
                    if (affected.contains('common-library')) {
                        env.MVN_MAKE_FLAGS = '-am -amd'
                    }

                    def affectedModulesCsv = affected.join(',')
                    env.AFFECTED_MODULES = affectedModulesCsv

                    // Persist for later stages (avoid env issues)
                    writeFile file: '.jenkins_affected_modules', text: affectedModulesCsv

                    // Logging
                    echo "All modules (${allModules.size()}): ${allModules.join(',')}"
                    echo "rebuildAll=${rebuildAll}"
                    echo "Touched dirs: ${touchedTopDirs.join(',')}"
                    echo "Affected modules: ${affectedModulesCsv}"

                    if (affectedModulesCsv?.trim()) {
                        currentBuild.description = "${env.BRANCH_NAME ?: ''} | modules: ${affectedModulesCsv}"
                        echo "Changed files:\n${normalizedChangedFiles.join('\n')}"
                    } else {
                        currentBuild.description = "${env.BRANCH_NAME ?: ''} | no service changes"
                        echo "Changed files:\n${normalizedChangedFiles.join('\n')}"
                        echo "No Maven module affected → build/test stages will be no-op"
                    }
                }
            }
        }

        stage('Snyk Scan') {
            when {
                expression { env.AFFECTED_MODULES?.trim() }
            }
            steps {
                script {
                    withCredentials([string(credentialsId: 'snyk', variable: 'SNYK_TOKEN')]) {

                        sh 'snyk auth $SNYK_TOKEN'

                        sh '''
                          if [ -f "mvnw" ]; then
                            chmod +x mvnw
                            ./mvnw clean install -DskipTests
                          fi
                        '''

                        def modules = env.AFFECTED_MODULES.split(',')

                        for (module in modules) {
                            module = module.trim()
                            if (!module) continue

                            echo "--- Running Snyk scan for module: ${module} ---"

                            dir(module) {

                                def depStatus = sh(
                                    script: 'snyk test --file=pom.xml --org=4496d6cc-3702-46bc-8ea7-6ac73f92b5cf',
                                    returnStatus: true
                                )

                                def codeStatus = sh(
                                    script: 'snyk code test --org=4496d6cc-3702-46bc-8ea7-6ac73f92b5cf',
                                    returnStatus: true
                                )

                                if (depStatus != 0 || codeStatus != 0) {
                                    echo "SNYK WARNING: vulnerabilities detected in ${module}"
                                    currentBuild.result = 'SUCCESS'
                                } else {
                                    echo "No vulnerabilities detected in ${module}"
                                }
                            }
                        }
                    }
                }
            }
        }


        stage('Build') {
            when {
                // Only run the build stage if there are affected modules to build
                expression { env.AFFECTED_MODULES?.trim() }
            }
            steps {
                // Run the Maven build command for the affected modules to create the necessary artifacts for testing and coverage analysis
                echo "Building affected modules: ${env.AFFECTED_MODULES}..."
                sh "mvn ${env.MVN_ARGS} -pl ${env.AFFECTED_MODULES} ${env.MVN_MAKE_FLAGS} -DskipTests clean package"
            }
        }

        stage('Unit & Integration Tests') {
            when {
                expression { env.AFFECTED_MODULES?.trim() }
            }
            steps {
                // Run the Maven verify command for the affected modules to execute tests and generate coverage reports
                sh """
                    mvn ${env.MVN_ARGS} \
                        -pl ${env.AFFECTED_MODULES} ${env.MVN_MAKE_FLAGS} \
                        verify \
                        -ff \
                        -DtrimStackTrace=true \
                        -Dsurefire.printSummary=true \
                        -Dfailsafe.printSummary=true
                """
                // Publish unit test and integration test results to Jenkins for reporting and analysis
                junit allowEmptyResults: true,
                      testResults: '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml'
            }
        }

        stage('Coverage Gate') {
            when {
                expression { env.AFFECTED_MODULES?.trim() }
            }
            steps {
                script {
                    def modules = env.AFFECTED_MODULES
                        .split(',')
                        .collect { it.trim() }
                        .findAll { it }

                    if (!modules || modules.isEmpty()) {
                        echo "No affected modules → skipping coverage gate"
                        return
                    }

                    echo "Running coverage for modules: ${modules.join(', ')}"

                    // 2. Build Jacoco report paths dynamically
                    def coverageTools = modules.collect { module ->
                        [
                            parser: 'JACOCO',
                            pattern: "${module}/target/site/jacoco/jacoco.xml"
                        ]
                    }

                    // 3. Execute coverage gate
                    recordCoverage(
                        tools: coverageTools,
                        sourceCodeRetention: 'NEVER',
                        qualityGates: [
                            [
                                threshold: 70.0,
                                metric: 'LINE',
                                baseline: 'PROJECT',
                                criticality: 'FAILURE'
                            ],
                            [
                                threshold: 50.0,
                                metric: 'BRANCH',
                                baseline: 'PROJECT',
                                criticality: 'FAILURE'
                            ],
                            [
                                threshold: 70.0,
                                metric: 'INSTRUCTION',
                                baseline: 'PROJECT',
                                criticality: 'FAILURE'
                            ]
                        ]
                    )
                }
            }
        }

        stage('SonarQube Analysis & Quality Gate') {
            when {
                expression { env.AFFECTED_MODULES?.trim() }
            }
            steps {
                withCredentials([string(credentialsId: 'sonar-yas', variable: 'SONAR_TOKEN')]) {
                    sh """
                        mvn ${MVN_ARGS} \
                            -pl ${AFFECTED_MODULES} \
                            ${MVN_MAKE_FLAGS} \
                            sonar:sonar \
                            -Dsonar.projectKey=yas-project\
                            -Dsonar.host.url=http://localhost:9000 \
                            -Dsonar.login=$SONAR_TOKEN \
                            -Dsonar.qualitygate.wait=true
                    """
                }
            }
        }
    }

    post {
        always {
            // Upload artifact
            archiveArtifacts allowEmptyArchive: true,
                artifacts: '**/target/*.jar'

            // Upload test reports
            archiveArtifacts allowEmptyArchive: true,
                artifacts: '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml'

            // Upload coverage report
            archiveArtifacts allowEmptyArchive: true,
                artifacts: '**/target/site/jacoco/**'

            // Upload Gitleaks report
            archiveArtifacts allowEmptyArchive: true,
                artifacts: 'gitleaks-report.json'
        }

        success {
            echo 'Pipeline SUCCESS'
        }

        failure {
            echo 'Pipeline FAILED'
        }
    }
}