/**
 * Product Service CI — placeholder script.
 * TODO: Replace with full CI pipeline (tests, checkstyle, OWASP, Docker) when ready.
 */

def call() {
    def serviceName = 'product'
    try {
        stage("${serviceName}: Build") {
            sh "mvn clean install -pl ${serviceName} -DskipTests"
        }

        stage("${serviceName}: Unit Tests") {
            try {
                sh "mvn test jacoco:report -pl ${serviceName}"
            } finally {
                junit testResults: "${serviceName}/**/surefire-reports/TEST*.xml",
                      allowEmptyResults: true
            }
        }

        echo "✓ ${serviceName} placeholder build completed"
    } catch (Exception e) {
        echo "✗ ${serviceName} build failed"
        throw e
    }
}

return this
