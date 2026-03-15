/**
 * Backoffice Service CI — loaded and executed by the master Jenkinsfile.
 *
 * Tech stack : Node.js (npm / Jest / ESLint / Prettier)
 * Phases     : Phase 1 — Test (unit tests, lint, security audit)
 *              Phase 2 — Build application + Docker image
 */

def run() {
    def serviceName    = 'backoffice'
    def dockerRegistry = env.DOCKER_REGISTRY ?: 'ghcr.io'
    def dockerImage    = "${dockerRegistry}/danzgne/yas-${serviceName}"

    try {
        stage("${serviceName}: Install Dependencies") {
            dir(serviceName) {
                withEnv(['NODE_OPTIONS=--max-old-space-size=4096']) {
                    sh 'npm ci --no-optional'
                }
            }
        }

        // ─── Phase 1: Test ─────────────────────────────────────────────────────
        stage("${serviceName}: Phase 1 - Unit Tests") {
            try {
                dir(serviceName) {
                    withEnv(['NODE_OPTIONS=--max-old-space-size=4096']) {
                        sh '''
                            if node -e "const p=require('./package.json'); const hasJest=Boolean((p.devDependencies&&p.devDependencies.jest)||(p.dependencies&&p.dependencies.jest)||(p.scripts&&p.scripts.test)); process.exit(hasJest?0:1)"; then
                                npx jest --coverage --ci --reporters=default --reporters=jest-junit
                            else
                                echo 'No Jest configuration found in backoffice/package.json. Skipping unit tests.'
                            fi
                        '''
                    }
                }
            } finally {
                junit testResults: 'backoffice/junit.xml', allowEmptyResults: true
                publishHTML([
                    allowMissing         : true,
                    alwaysLinkToLastBuild: true,
                    keepAll              : true,
                    reportDir            : 'backoffice/coverage/lcov-report',
                    reportFiles          : 'index.html',
                    reportName           : 'Backoffice Coverage Report'
                ])
            }
        }

        stage("${serviceName}: Phase 1 - Lint & Code Quality") {
            dir(serviceName) {
                sh 'npm run lint || true'
                sh 'npx prettier --check . || true'
            }
        }

        stage("${serviceName}: Phase 1 - Security Audit") {
            dir(serviceName) {
                sh 'npm audit --omit=dev || true'
            }
        }

        // ─── Phase 2: Build ─────────────────────────────────────────────────────
        stage("${serviceName}: Phase 2 - Build Application") {
            dir(serviceName) {
                withEnv(['NODE_OPTIONS=--max-old-space-size=4096']) {
                    sh 'npm run build'
                }
            }
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

        echo "✓ Backoffice service build completed successfully"
    } catch (Exception e) {
        echo "✗ Backoffice service build failed"
        throw e
    }
}

return this
