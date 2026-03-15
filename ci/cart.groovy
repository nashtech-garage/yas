/**
 * Cart Service CI — loaded and executed by the master Jenkinsfile.
 *
 * Tech stack : Java / Maven (multi-module, uses -pl cart)
 * Phases     : Phase 1 — Test (unit tests, checkstyle, OWASP dependency-check)
 *              Phase 2 — Build artifacts + Docker image
 */ 

def call() {
    def serviceName    = 'cart'
    def dockerRegistry = env.DOCKER_REGISTRY ?: 'ghcr.io'
    def dockerImage    = "${dockerRegistry}/danzgne/yas-${serviceName}"
    try {
        stage("${serviceName}: Prepare Build Dependencies") {
            // Match GitHub Actions flow: install module and internal deps first.
            sh 'mvn clean install -pl cart -DskipTests'
        }
        // ─── Phase 1: Test ─────────────────────────────────────────────────────
        stage("${serviceName}: Phase 1 - Unit Tests") {
            try {
                sh 'mvn test jacoco:report -pl cart'
            } finally {
                junit testResults: 'cart/**/surefire-reports/TEST*.xml',
                      allowEmptyResults: true
                jacoco(
                    execPattern    : 'cart/target/jacoco.exec',
                    classPattern   : 'cart/target/classes',
                    sourcePattern  : 'cart/src/main/java',
                    inclusionPattern: '**/*.class',
                    changeBuildStatus: false
                )
            }
        }

        stage("${serviceName}: Phase 1 - Code Quality") {
            sh 'mvn checkstyle:checkstyle -pl cart -Dcheckstyle.output.file=cart-checkstyle-result.xml'
            echo 'Checkstyle report generated at: cart-checkstyle-result.xml'
        }

        stage("${serviceName}: Phase 1 - Security Scan") {
            try {
                try {
                    withCredentials([string(credentialsId: 'nvd-api-key', variable: 'NVD_API_KEY')]) {
                        sh '''
                            mvn -pl cart org.owasp:dependency-check-maven:check \
                              -DfailBuildOnCVSS=7 \
                              -DnvdApiKey=$NVD_API_KEY \
                              -DdataDirectory=$JENKINS_HOME/dependency-check-data \
                              || true
                        '''
                    }
                } catch (Exception e) {
                    echo "NVD API key credential not found. Running dependency-check without API key (slower)."
                    sh '''
                        mvn -pl cart org.owasp:dependency-check-maven:check \
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
                    reportDir            : 'cart/target',
                    reportFiles          : 'dependency-check-report.html',
                    reportName           : 'OWASP Dependency-Check Report'
                ]
            }
        }

        // ─── Phase 2: Build ─────────────────────────────────────────────────────
        stage("${serviceName}: Phase 2 - Compile & Package") {
            sh 'mvn clean install -pl cart -DskipTests'
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

        echo "Cart service build completed successfully"
    } catch (Exception e) {
        echo "Cart service build failed"
        throw e
    }
}

return this
