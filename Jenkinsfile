pipeline {
    agent any 
   
    options {
        timestamps()
    }

    tools {
        jdk 'jdk21'
        maven 'maven-3'
    }

    stages {
        stage('Detect Changes') {
            steps {
                script {
                    sh 'git fetch origin +refs/heads/main:refs/remotes/origin/main --prune'

                    def baseCommit = ''
                    def hasOriginMain = (sh(
                        script: 'git rev-parse --verify refs/remotes/origin/main >/dev/null 2>&1',
                        returnStatus: true
                    ) == 0)

                    if (hasOriginMain) {
                        baseCommit = sh(
                            script: 'git merge-base HEAD refs/remotes/origin/main',
                            returnStdout: true
                        ).trim()
                        echo 'Using refs/remotes/origin/main as base'
                    } else if (sh(script: 'git rev-parse --verify HEAD~1 >/dev/null 2>&1', returnStatus: true) == 0) {
                        baseCommit = sh(
                            script: 'git rev-parse HEAD~1',
                            returnStdout: true
                        ).trim()
                        echo 'origin/main not found, fallback to HEAD~1'
                    } else {
                        baseCommit = sh(
                            script: 'git rev-parse HEAD',
                            returnStdout: true
                        ).trim()
                        echo 'Single-commit branch, fallback to HEAD'
                    }

                    def changedFiles = sh(
                        script: "git diff --name-only ${baseCommit} HEAD",
                        returnStdout: true
                    ).trim()

                    echo "Base commit: ${baseCommit}"
                    echo "Files changed:\n${changedFiles}"
                    env.BASE_COMMIT = baseCommit
                    env.CHANGED_FILES = changedFiles

                    def servicePaths = [
                        'backoffice-bff': 'backoffice-bff/',
                        'cart': 'cart/',
                        'customer': 'customer/',
                        'delivery': 'delivery/',
                        'inventory': 'inventory/',
                        'location': 'location/',
                        'media': 'media/',
                        'order': 'order/',
                        'payment-paypal': 'payment-paypal/',
                        'payment': 'payment/',
                        'product': 'product/',
                        'promotion': 'promotion/',
                        'rating': 'rating/',
                        'recommendation': 'recommendation/',
                        'sampledata': 'sampledata/',
                        'search': 'search/',
                        'storefront-bff': 'storefront-bff/',
                        'tax': 'tax/',
                        'webhook': 'webhook/'
                    ]

                    def changedServices = []

                    if (changedFiles.contains('common-library/')) {
                        changedServices.addAll(servicePaths.keySet())
                        echo 'common-library changed, scheduling all services'
                    } else {
                        servicePaths.each { serviceName, servicePath ->
                            if (changedFiles.contains(servicePath)) {
                                changedServices << serviceName
                            }
                        }
                    }

                    env.CHANGED_SERVICES = changedServices.unique().join(',')
                    echo "Changed services: ${env.CHANGED_SERVICES}"
                }
            }
        }

        stage('Check Tools') {
            steps {
                script {
                    def changedServices = env.CHANGED_SERVICES ? env.CHANGED_SERVICES.split(',').findAll { it?.trim() } : []

                    if (changedServices.isEmpty()) {
                        echo 'No service changes detected. Skipping tool checks.'
                        return
                    }

                    sh 'which gitleaks && gitleaks version'
                    sh 'which snyk && snyk --version'
                }
            }
        }

        stage('Gitleaks Scan') {
            steps {
                script {
                    if (!env.CHANGED_FILES?.trim()) {
                        echo 'No file changes detected. Skipping Gitleaks scan.'
                        return
                    }

                    sh 'gitleaks detect --config gitleaks.toml --source . --log-opts="${BASE_COMMIT}..HEAD" --no-banner'
                }
            }
        }

        stage('Test & Build Changed Services') {
            steps {
                script {
                    def changedServices = env.CHANGED_SERVICES ? env.CHANGED_SERVICES.split(',').findAll { it?.trim() } : []

                    if (changedServices.isEmpty()) {
                        echo 'No service changes detected. Skipping build.'
                        return
                    }

                    changedServices.each { serviceName ->
                        def serviceDir = serviceName.trim()
                        echo "Running tests and build for ${serviceDir}..."

                        dir(serviceDir) {
                            sh "mvn -f ../pom.xml -pl ${serviceDir} -am clean test jacoco:report"
                            junit 'target/surefire-reports/*.xml'
                            jacoco execPattern: 'target/jacoco.exec',
                                   classPattern: 'target/classes',
                                   sourcePattern: 'src/main/java',
                                   inclusionPattern: '**/*.class'
                            sh '''
                                |python3 - <<'PY'
                                |import sys
                                |import xml.etree.ElementTree as ET
                                |
                                |report_path = 'target/site/jacoco/jacoco.xml'
                                |tree = ET.parse(report_path)
                                |root = tree.getroot()
                                |
                                |missed = 0
                                |covered = 0
                                |for counter in root.findall('.//counter[@type="LINE"]'):
                                |    missed += int(counter.attrib.get('missed', 0))
                                |    covered += int(counter.attrib.get('covered', 0))
                                |
                                |total = missed + covered
                                |coverage = 100.0 * covered / total if total else 0.0
                                |
                                |print(f'Line coverage: {coverage:.2f}%')
                                |
                                |if coverage < 70.0:
                                |    print('Coverage is below the required 70% threshold.')
                                |    sys.exit(1)
                                |PY
                            '''.stripMargin()
                            sh "mvn -f ../pom.xml -pl ${serviceDir} -am -DskipTests package"
                        }
                    }
                }
            }
        }

        stage('SonarQube Scan') {
            steps {
                script {
                    def changedServices = env.CHANGED_SERVICES ? env.CHANGED_SERVICES.split(',').findAll { it?.trim() } : []

                    if (changedServices.isEmpty()) {
                        echo 'No service changes detected. Skipping SonarQube scan.'
                        return
                    }

                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        changedServices.each { serviceName ->
                            def serviceDir = serviceName.trim()
                            echo "Running SonarQube scan for ${serviceDir}..."

                            dir(serviceDir) {
                                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                    timeout(time: 10, unit: 'MINUTES') {
                                        retry(2) {
                                            withEnv(["SERVICE_DIR=${serviceDir}"]) {
                                                sh '''
                                                    mvn -f ../pom.xml -pl "$SERVICE_DIR" -am sonar:sonar \
                                                      -DskipTests \
                                                      -Dsonar.token="$SONAR_TOKEN" \
                                                      -Dsonar.projectKey="hcmus-devops-project1_yas_${SERVICE_DIR}" \
                                                      -Dsonar.projectName="yas-${SERVICE_DIR}" \
                                                      -Dsonar.ws.timeout=120
                                                '''.stripIndent()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Snyk Scan') {
            steps {
                script {
                    def changedServices = env.CHANGED_SERVICES ? env.CHANGED_SERVICES.split(',').findAll { it?.trim() } : []

                    if (changedServices.isEmpty()) {
                        echo 'No service changes detected. Skipping Snyk scan.'
                        return
                    }

                    withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                        int snykAuthStatus = sh(
                            script: 'SNYK_TOKEN="$SNYK_TOKEN" snyk whoami >/dev/null 2>&1',
                            returnStatus: true
                        )

                        if (snykAuthStatus != 0) {
                            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                error('Snyk authentication failed (401 likely). Check Jenkins credential snyk-token.')
                            }
                            return
                        }

                        changedServices.each { serviceName ->
                            def serviceDir = serviceName.trim()
                            echo "Running Snyk scan for ${serviceDir}..."

                            dir(serviceDir) {
                                sh 'test -x ./mvnw || chmod +x ./mvnw'

                                int snykExitCode = sh(
                                    script: 'SNYK_TOKEN="$SNYK_TOKEN" snyk test --file=pom.xml --severity-threshold=high --skip-unresolved --prune-repeated-subdependencies',
                                    returnStatus: true
                                )

                                if (snykExitCode != 0) {
                                    catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                        error("Snyk CLI returned exit code ${snykExitCode} for ${serviceDir}. Scan logged and stage marked UNSTABLE.")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
    }
}
