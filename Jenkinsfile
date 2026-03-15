/**
 * Root Jenkinsfile — CI Change Detector & Inline Builder
 *
 * This pipeline runs on every push to the repo (single Multibranch Pipeline).
 * It detects which service directories changed, then either:
 *   • Loads the service's Groovy script from ci/<service>.groovy and runs it
 *     inline in parallel  (for the 8 services listed in INLINE_SERVICES), or
 *   • Triggers the corresponding per-service Multibranch Pipeline job
 *     (for all remaining services).
 */

// ─── Inline services (scripts live in ci/) ────────────────────────────────────
def INLINE_SERVICES = [
    'backoffice', 'backoffice-bff', 'cart', 'charts',
    'customer', 'inventory', 'location', 'media'
]

// ─── Remaining services — still trigger separate Multibranch jobs ─────────────
def SERVICE_JOBS = [
    'order'         : 'yas-order-ci',
    'payment'       : 'yas-payment-ci',
    'payment-paypal': 'yas-payment-paypal-ci',
    'product'       : 'yas-product-ci',
    'promotion'     : 'yas-promotion-ci',
    'rating'        : 'yas-rating-ci',
    'recommendation': 'yas-recommendation-ci',
    'sampledata'    : 'yas-sampledata-ci',
    'search'        : 'yas-search-ci',
    'storefront'    : 'yas-storefront-ci',
    'storefront-bff': 'yas-storefront-bff-ci',
    'tax'           : 'yas-tax-ci',
    'webhook'       : 'yas-webhook-ci',
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

                    def rootPomChanged = changedFiles.contains('pom.xml') &&
                                        !changedFiles.split('\n').any { it.contains('/pom.xml') && it.split('/').length > 1 }

                    Set<String> servicesToBuild = []

                    changedFiles.split('\n').each { file ->
                        if (!file || file.trim().isEmpty()) return

                        def parts = file.trim().split('/')
                        def topDir = parts[0]

                        def allServices = INLINE_SERVICES + SERVICE_JOBS.keySet().toList()
                        if (allServices.contains(topDir)) {
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

        stage('Wait for Branch Scan') {
            when {
                expression {
                    // Only needed when there are job-triggered (non-inline) services to build
                    if (!env.SERVICES_TO_BUILD?.trim()) return false
                    def services = env.SERVICES_TO_BUILD.split(',').toList()
                    return services.any { svc -> SERVICE_JOBS.containsKey(svc.trim()) }
                }
            }
            steps {
                echo "Waiting 5s for Multibranch Pipeline branch scans to complete..."
                sleep(time: 5, unit: 'SECONDS')
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

                    services.each { svc ->
                        def localSvc = svc.trim()

                        if (INLINE_SERVICES.contains(localSvc)) {
                            // ── Inline: load and run the service Groovy script ────────
                            parallelBuilds["Build ${localSvc}"] = {
                                def svcScript = load("ci/${localSvc}.groovy")
                                svcScript.run()
                            }
                        } else {
                            // ── External: trigger the service's Multibranch job ───────
                            def jobName = SERVICE_JOBS[localSvc]
                            if (!jobName) {
                                echo "WARNING: No Jenkins job mapping found for service '${localSvc}'. Skipping."
                                return
                            }
                            parallelBuilds["Build ${localSvc}"] = {
                                def branchName    = env.BRANCH_NAME ?: 'main'
                                def encodedBranch = branchName.replaceAll('/', '%2F')
                                def fullJobPath   = "${jobName}/${encodedBranch}"
                                echo "Triggering Multibranch job: ${fullJobPath}"
                                try {
                                    build job: fullJobPath,
                                          wait: true,
                                          propagate: true
                                } catch (Exception e) {
                                    echo "ERROR: Failed to trigger ${fullJobPath}: ${e.message}"
                                    throw e
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
            echo "All triggered service builds completed successfully."
        }
        failure {
            echo "One or more service builds failed. Check triggered jobs for details."
        }
        always {
            cleanWs()
        }
    }
}
