/**
 * Master CI/CD Pipeline — Unified Orchestrator
 *
 * Single Multibranch Pipeline for all services.
 * - Detects which services changed on each branch push
 * - Builds all affected services in parallel
 * - No per-service jobs needed on Jenkins server
 *
 * Prerequisites:
 *   - Create ONE Multibranch Pipeline job ("yas-master" or "yas-ci")
 *   - Point to this Jenkinsfile at repo root
 *   - Configure GitHub webhook to trigger on push/PR
 */

// ─── Service Configuration ──────────────────────────────────────────────────────
def SERVICE_CONFIG = [
    // Java services
    'backoffice-bff': [type: 'java', port: 8080],
    'cart'          : [type: 'java', port: 8080],
    'customer'      : [type: 'java', port: 8080],
    'delivery'      : [type: 'java', port: 8080],
    'inventory'     : [type: 'java', port: 8080],
    'location'      : [type: 'java', port: 8080],
    'media'         : [type: 'java', port: 8080],
    'order'         : [type: 'java', port: 8080],
    'payment'       : [type: 'java', port: 8080],
    'payment-paypal': [type: 'java', port: 8080],
    'product'       : [type: 'java', port: 8080],
    'promotion'     : [type: 'java', port: 8080],
    'rating'        : [type: 'java', port: 8080],
    'recommendation': [type: 'java', port: 8080],
    'sampledata'    : [type: 'java', port: 8080],
    'search'        : [type: 'java', port: 8080],
    'storefront-bff': [type: 'java', port: 8080],
    'tax'           : [type: 'java', port: 8080],
    'webhook'       : [type: 'java', port: 8080],
    // Node.js services
    'backoffice'    : [type: 'nodejs', port: 3000],
    'storefront'    : [type: 'nodejs', port: 3000],
]

def JAVA_SERVICES = SERVICE_CONFIG.findAll { k, v -> v.type == 'java' }.keySet()
def NODEJS_SERVICES = SERVICE_CONFIG.findAll { k, v -> v.type == 'nodejs' }.keySet()

// ─── Pipeline ─────────────────────────────────────────────────────────────────
pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'ghcr.io'
    }

    tools {
        jdk   'jdk-21'
        maven 'maven-3'
        nodejs 'nodejs-20'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timestamps()
        timeout(time: 120, unit: 'MINUTES')
        skipDefaultCheckout()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    def changedFiles = ''

                    if (env.CHANGE_ID) {
                        // ── PR build ──────────────────────────────────────────────────
                        sh "git fetch origin ${env.CHANGE_TARGET}:refs/remotes/origin/${env.CHANGE_TARGET} --no-tags || true"
                        changedFiles = sh(
                            script: "git diff --name-only \$(git merge-base HEAD refs/remotes/origin/${env.CHANGE_TARGET})",
                            returnStdout: true
                        ).trim()
                        echo "PR #${env.CHANGE_ID}: diffing against merge-base of origin/${env.CHANGE_TARGET}"
                    } else {
                        // ── Regular branch push ───────────────────────────────────────
                        def previousCommit = ''
                        try {
                            previousCommit = sh(
                                script: 'git rev-parse HEAD~1',
                                returnStdout: true
                            ).trim()
                        } catch (Exception e) {
                            echo "No previous commit found, treating all files as changed."
                            previousCommit = '4b825dc642cb6eb9a060e54bf8d69288fbee4904'
                        }
                        changedFiles = sh(
                            script: "git diff --name-only ${previousCommit} HEAD",
                            returnStdout: true
                        ).trim()
                    }

                    echo "=== Changed files ===\n${changedFiles}"

                    def rootPomChanged = changedFiles.contains('pom.xml') &&
                                        !changedFiles.split('\n').any { it.contains('/pom.xml') && it.split('/').length > 1 }

                    Set<String> servicesToBuild = []

                    changedFiles.split('\n').each { file ->
                        if (!file || file.trim().isEmpty()) return

                        def parts = file.trim().split('/')
                        def topDir = parts[0]

                        if (SERVICE_CONFIG.containsKey(topDir)) {
                            servicesToBuild << topDir
                        }
                    }

                    // If root pom.xml changed, trigger all Java services
                    if (rootPomChanged) {
                        echo "Root pom.xml changed — scheduling all Java services."
                        JAVA_SERVICES.each { svc -> servicesToBuild << svc }
                    }

                    if (servicesToBuild.isEmpty()) {
                        echo "No service directories changed. Nothing to build."
                        currentBuild.result = 'SUCCESS'
                        return
                    }

                    echo "=== Services to build: ${servicesToBuild.join(', ')} ==="
                    env.SERVICES_TO_BUILD = servicesToBuild.join(',')
                }
            }
        }

        stage('Build Services') {
            when {
                expression { return env.SERVICES_TO_BUILD?.trim() != null && env.SERVICES_TO_BUILD.trim() != '' }
            }
            steps {
                script {
                    def services = env.SERVICES_TO_BUILD.split(',')
                    def parallelJobs = [:]

                    services.each { svc ->
                        parallelJobs["${svc}"] = {
                            loadAndBuildService(svc)
                        }
                    }

                    parallel parallelJobs
                }
            }
        }
    }

    post {
        success {
            echo "✓ All services built successfully."
        }
        failure {
            echo "✗ One or more services failed. Check logs above."
        }
        always {
            cleanWs()
        }
    }
}

// ─── Service Build Loader ───────────────────────────────────────────────────────
/**
 * Load and execute service-specific Jenkinsfile functions
 */
void loadAndBuildService(String serviceName) {
    try {
        echo "========== Building Service: ${serviceName} =========="
        
        def jenkinsfilePath = "${serviceName}/Jenkinsfile"
        
        if (fileExists(jenkinsfilePath)) {
            echo "Loading Jenkinsfile from ${jenkinsfilePath}"
            
            // Load the service Jenkinsfile (function-only, no pipeline block)
            def serviceScript = load(jenkinsfilePath)
            
            // Execute the service build logic
            serviceScript.runBuild(serviceName)
            
        } else {
            echo "WARNING: No Jenkinsfile found at ${jenkinsfilePath}"
            echo "Skipping ${serviceName} build"
        }
        
        echo "✓ ${serviceName} build completed"
    } catch (Exception e) {
        echo "✗ ${serviceName} build failed: ${e.message}"
        throw e
    }
}
