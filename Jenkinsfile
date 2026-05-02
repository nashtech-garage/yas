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
        def out = sh(script: cmd, returnStdout: true).trim()
        return out
            .split(/\r?\n/)
            .collect { it.trim() }
            .findAll { it }
    } catch (err) {
        def out = sh(script: 'git -c color.ui=never show --name-only --pretty="" HEAD', returnStdout: true).trim()
        return out
            .split(/\r?\n/)
            .collect { it.trim() }
            .findAll { it }
    }
}

def getModules() {
    env.AFFECTED_MODULES?.split(',')?.collect { it.trim() }?.findAll { it } ?: []
}

pipeline {
    agent any

    tools {
        maven 'maven3'
        jdk 'jdk25'
    }

    environment {
        MVN_ARGS = '-B -ntp'
        SERVICES = 'common-library backoffice-bff cart customer inventory location media order payment-paypal payment product promotion rating search storefront-bff tax webhook sampledata recommendation delivery'
        SNYK_HOME = tool name: 'snyk@latest'
    }

    stages {
        stage('Checkout') {
            steps {
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

                    if (status != 0) {
                        echo "GITLEAKS WARNING: secrets detected (see report)"
                        currentBuild.result = 'SUCCESS'
                    } else {
                        echo "No secrets detected"
                    }
                }
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    def allModules = env.SERVICES.split(' ')
                    def changedFiles = computeChangedFiles()

                    // Detect rebuild all
                    def rebuildAll = changedFiles.any { f ->
                        f == 'pom.xml' ||
                        f.startsWith('checkstyle/')
                    }

                    def affected = allModules.findAll { module ->
                        changedFiles.any { f ->
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

                    // Logging
                    echo "rebuildAll=${rebuildAll}"
                    echo "Affected modules: ${affectedModulesCsv}"
                    echo "Changed files:\n${changedFiles.join('\n')}"

                    if (affectedModulesCsv?.trim()) {
                        currentBuild.description = "${env.BRANCH_NAME ?: ''} | services: ${affectedModulesCsv}"
                    } else {
                        currentBuild.description = "${env.BRANCH_NAME ?: ''} | no service changes"
                    }
                }
            }
        }

        stage('Build') {
            when {
                expression { env.AFFECTED_MODULES?.trim() }
            }
            steps {

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

        stage('Snyk Scan') {
            when {
                expression { env.AFFECTED_MODULES?.trim() }
            }
            steps {
                script {
                    withCredentials([string(credentialsId: 'snyk-quan', variable: 'SNYK_TOKEN')]) {

                        sh 'snyk auth $SNYK_TOKEN'

                        def modules = getModules()

                        for (module in modules) {
                            module = module.trim()
                            if (!module) continue

                            echo "Running Snyk scan for module: ${module} "

                            dir(module) {

                                def depStatus = sh(
                                    script: 'snyk test --file=pom.xml --org=036f61e9-4955-4444-b27c-a427cda4feca',
                                    returnStatus: true
                                )

                                def codeStatus = sh(
                                    script: 'snyk code test --org=036f61e9-4955-4444-b27c-a427cda4feca',
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

        stage('Coverage Gate') {
            when {
                expression { env.AFFECTED_MODULES?.trim() }
            }
            steps {
                script {
                    def modules = getModules()

                    echo "Running coverage for modules: ${modules.join(', ')}"

                    // Build Jacoco report paths dynamically
                    def coverageTools = modules.collect { module ->
                        [
                            parser: 'JACOCO',
                            pattern: "${module}/target/site/jacoco/jacoco.xml"
                        ]
                    }

                    // Execute coverage gate
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
                                threshold: 70.0,
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

        stage('SonarQube Analysis') {
            when {
                expression { env.AFFECTED_MODULES?.trim() }
            }
            steps {
                withSonarQubeEnv('Sonar-instances') {
                    sh """
                        mvn ${MVN_ARGS} \
                            -pl ${AFFECTED_MODULES} \
                            ${MVN_MAKE_FLAGS} \
                            sonar:sonar \
                            -Dsonar.projectKey=yas-project
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
