/**
 * Backoffice BFF Service CI — loaded and executed by the master Jenkinsfile.
 *
 * Tech stack : Java / Maven (uses -f flag — standalone pom.xml, no parent reactor)
 * Phases     : Phase 1 — Test (unit tests, checkstyle, OWASP dependency-check)
 *              Phase 2 — Build artifacts + Docker image
 */

def run() {
    def serviceName    = 'backoffice-bff'
    def dockerRegistry = env.DOCKER_REGISTRY ?: 'ghcr.io'
    def dockerImage    = "${dockerRegistry}/danzgne/yas-${serviceName}"

    try {
        stage("${serviceName}: Prepare Build Dependencies") {
            // Use -f flag since backoffice-bff has its own pom.xml
            sh 'mvn clean install -f backoffice-bff/pom.xml -DskipTests'
        }

        // ─── Phase 1: Test ─────────────────────────────────────────────────────
        stage("${serviceName}: Phase 1 - Unit Tests") {
            try {
                sh 'mvn test jacoco:report -f backoffice-bff/pom.xml'
            } finally {
                junit testResults: 'backoffice-bff/**/surefire-reports/TEST*.xml',
                      allowEmptyResults: true
                jacoco(
                    execPattern    : 'backoffice-bff/target/jacoco.exec',
                    classPattern   : 'backoffice-bff/target/classes',
                    sourcePattern  : 'backoffice-bff/src/main/java',
                    inclusionPattern: '**/*.class',
                    changeBuildStatus: false
                )
            }
        }

        stage("${serviceName}: Phase 1 - Code Quality") {
            sh 'mvn checkstyle:checkstyle -f backoffice-bff/pom.xml -Dcheckstyle.output.file=backoffice-bff-checkstyle-result.xml'
            echo 'Checkstyle report generated at: backoffice-bff-checkstyle-result.xml'
        }

        stage("${serviceName}: Phase 1 - Security Scan") {
            try {
                try {
                    withCredentials([string(credentialsId: 'nvd-api-key', variable: 'NVD_API_KEY')]) {
                        sh '''
                            mvn -f backoffice-bff/pom.xml org.owasp:dependency-check-maven:check \
                              -DfailBuildOnCVSS=7 \
                              -DnvdApiKey=$NVD_API_KEY \
                              -DdataDirectory=$JENKINS_HOME/dependency-check-data \
                              || true
                        '''
                    }
                } catch (Exception e) {
                    echo "NVD API key credential not found. Running dependency-check without API key (slower)."
                    sh '''
                        mvn -f backoffice-bff/pom.xml org.owasp:dependency-check-maven:check \
                          -DfailBuildOnCVSS=7 \
                          -DdataDirectory=$JENKINS_HOME/dependency-check-data \
                          || true
                    '''
                }
            } finally {
                publishHTML target: [
                    allowMissing         : true,
                    alwaysLinkToLastBuild: false,
                    keepAll              : true,
                    reportDir            : 'backoffice-bff/target',
                    reportFiles          : 'dependency-check-report.html',
                    reportName           : 'OWASP Dependency-Check Report'
                ]
            }
        }

        // ─── Phase 2: Build ─────────────────────────────────────────────────────
        stage("${serviceName}: Phase 2 - Compile & Package") {
            sh 'mvn clean install -f backoffice-bff/pom.xml -DskipTests'
        }

        if (env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'develop') {
            stage("${serviceName}: Phase 2 - Build & Push Docker Image") {
                withCredentials([usernamePassword(
                    credentialsId   : 'docker-registry-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        mkdir -p \$WORKSPACE/.docker
                        echo "\$DOCKER_PASS" | docker --config \$WORKSPACE/.docker login ${dockerRegistry} -u "\$DOCKER_USER" --password-stdin
                        docker --config \$WORKSPACE/.docker build -t ${dockerImage}:latest -t ${dockerImage}:${env.BUILD_NUMBER} ./${serviceName}
                        docker --config \$WORKSPACE/.docker push ${dockerImage}:latest
                        docker --config \$WORKSPACE/.docker push ${dockerImage}:${env.BUILD_NUMBER}
                        docker --config \$WORKSPACE/.docker logout ${dockerRegistry}
                    """
                }
            }
        }

        echo "Backoffice BFF service build completed successfully"
    } catch (Exception e) {
        echo "Backoffice BFF service build failed"
        throw e
    }
}

return this
