/**
 * Storefront Service CI — placeholder script.
 * Tech stack : Node.js (Next.js / React)
 * TODO: Replace with full CI pipeline (tests, lint, audit, Docker) when ready.
 */

def call() { 
    def serviceName    = 'storefront'
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

        stage("${serviceName}: Build") {
            dir(serviceName) {
                withEnv(['NODE_OPTIONS=--max-old-space-size=4096']) {
                    sh 'npm run build'
                }
            }
        }

        if (env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'develop') {
            stage("${serviceName}: Build & Push Docker Image") {
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

        echo "✓ ${serviceName} placeholder build completed"
    } catch (Exception e) {
        echo "✗ ${serviceName} build failed"
        throw e
    }
}

return this 