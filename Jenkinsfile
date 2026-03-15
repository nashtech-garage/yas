/**
 * Root Jenkinsfile — CI Change Detector & Inline Builder
 *
 * This pipeline runs on every push to the repo (single Multibranch Pipeline).
 * It detects which service directories changed, then loads the service's Groovy
 * script from ci/<service>.groovy and runs all affected services in parallel.
 */

// ─── All services — scripts live in ci/ ──────────────────────────────────────
def INLINE_SERVICES = [
    'backoffice', 'backoffice-bff', 'cart', 'charts',
    'customer', 'inventory', 'location', 'media',
    'order', 'payment', 'payment-paypal', 'product', 'promotion',
    'rating', 'recommendation', 'sampledata', 'search',
    'storefront', 'storefront-bff', 'tax', 'webhook'
]
   

// ─── Root pom.xml affects all Java services ───────────────────────────────────
def JAVA_SERVICES = [
    'backoffice-bff', 'cart', 'customer', 'inventory',
    'location', 'media', 'order', 'payment', 'payment-paypal',
    'product', 'promotion', 'rating', 'recommendation', 'sampledata',
    'search', 'storefront-bff', 'tax', 'webhook'
]

// ─── Pipeline ─────────────────────────────────────────────────────────────────
pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'ghcr.io'
        MAVEN_OPTS      = '-Drevision=1.0-SNAPSHOT'
    }

    tools {
        jdk    'jdk-21'
        maven  'maven-3'
        nodejs 'nodejs-20'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timestamps()
        timeout(time: 60, unit: 'MINUTES')
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

                    def rootPomChanged = changedFiles.split('\n').any { it.trim() == 'pom.xml' }

                    Set<String> servicesToBuild = []

                    changedFiles.split('\n').each { file ->
                        if (!file || file.trim().isEmpty()) return

                        def parts = file.trim().split('/')
                        def topDir = parts[0]

                        if (INLINE_SERVICES.contains(topDir)) {
                            servicesToBuild << topDir
                        }

                        // Special handling: k8s/charts/** changes should trigger 'charts' service
                        if (topDir == 'k8s' && parts.length > 1 && parts[1] == 'charts') {
                            servicesToBuild << 'charts'
                        }

                        // Special handling: ci/<service>.groovy changes should trigger that service
                        if (topDir == 'ci' && parts.length > 1) {
                            def scriptName = parts[1].replaceAll('\\.groovy$', '')
                            if (INLINE_SERVICES.contains(scriptName)) {
                                servicesToBuild << scriptName
                            }
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

        stage('Build Shared Modules') {
            when {
                expression {
                    if (!env.SERVICES_TO_BUILD?.trim()) return false
                    return env.SERVICES_TO_BUILD.split(',').any { svc ->
                        JAVA_SERVICES.contains(svc.trim())
                    }
                }
            }
            steps {
                sh 'rm -rf ~/.m2/repository/com/yas/'
                sh 'mvn install -N -DskipTests'
                sh 'mvn clean install -pl common-library -DskipTests'
            }
        }

        stage('Build Services') {
            when {
                expression { return env.SERVICES_TO_BUILD?.trim() }
            }
            steps {
                script {
                    def services = env.SERVICES_TO_BUILD.split(',')
                    def parallelBuilds = [:]
                    def baseNode = env.NODE_NAME
                    def baseWorkspace = env.WORKSPACE

                    services.each { svc ->
                        def localSvc = svc.trim()
                        parallelBuilds["Build ${localSvc}"] = {
                            node(baseNode) {
                                ws("${baseWorkspace}@${localSvc}") {
                                    checkout scm
                                    def svcScript = load("ci/${localSvc}.groovy")
                                    svcScript.call()
                                }
                            }
                        }
                    }

                    parallel parallelBuilds
                }
            }
        }
    }

    post {
        success {
            echo "All service builds completed successfully."
        }
        failure {
            echo "One or more service builds failed."
        }
        always {
            cleanWs()
        }
    }
}
