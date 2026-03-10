/**
 * Root Jenkinsfile — CI Change Detector
 *
 * This pipeline runs on every push to the repo.
 * It detects which service directories changed and triggers the corresponding
 * per-service build jobs in parallel.
 *
 * Prerequisites on Jenkins controller:
 *   - Each service job must be created and named exactly as listed in SERVICE_JOBS below.
 *   - This pipeline job must be configured with SCM polling or a webhook trigger.
 */

// ─── Service registry ────────────────────────────────────────────────────────
// Maps a top-level directory in the repo to the Jenkins job name for that service.
// Adjust job names to match what you create on the Jenkins server.
def SERVICE_JOBS = [
    'backoffice'    : 'yas-backoffice-ci',
    'backoffice-bff': 'yas-backoffice-bff-ci',
    'cart'          : 'yas-cart-ci',
    'customer'      : 'yas-customer-ci',
    'delivery'      : 'yas-delivery-ci',
    'inventory'     : 'yas-inventory-ci',
    'location'      : 'yas-location-ci',
    'media'         : 'yas-media-ci',
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
    'backoffice-bff', 'cart', 'customer', 'delivery', 'inventory',
    'location', 'media', 'order', 'payment', 'payment-paypal',
    'product', 'promotion', 'rating', 'recommendation', 'sampledata',
    'search', 'storefront-bff', 'tax', 'webhook'
]

// ─── Pipeline ─────────────────────────────────────────────────────────────────
pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timestamps()
        timeout(time: 10, unit: 'MINUTES')
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
                        // CHANGE_TARGET is the base branch (e.g. 'main').
                        // Diff against the merge-base so we see ALL changes in the PR,
                        // not just the last commit.
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
                            previousCommit = '4b825dc642cb6eb9a060e54bf8d69288fbee4904' // empty-tree SHA
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

                        // Extract the top-level directory (= service name)
                        def parts = file.trim().split('/')
                        def topDir = parts[0]

                        if (SERVICE_JOBS.containsKey(topDir)) {
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

        stage('Trigger Service Builds') {
            when {
                expression { return env.SERVICES_TO_BUILD?.trim() != null && env.SERVICES_TO_BUILD.trim() != '' }
            }
            steps {
                script {
                    def services = env.SERVICES_TO_BUILD.split(',')

                    // Build all affected service jobs in parallel
                    def parallelJobs = [:]

                    services.each { svc ->
                        def jobName = SERVICE_JOBS[svc]
                        if (!jobName) {
                            echo "WARNING: No Jenkins job mapping found for service '${svc}'. Skipping."
                            return
                        }

                        parallelJobs["Build ${svc}"] = {
                            // For Multibranch Pipeline jobs the target path is:
                            //   <job-name>/<url-encoded-branch>
                            // For regular pushes: BRANCH_NAME = 'main', 'feature/xyz', etc.
                            // For PR builds:      BRANCH_NAME = 'PR-1', 'PR-2', etc.
                            //   (service Multibranch jobs must also discover PRs)
                            def branchName = env.BRANCH_NAME ?: 'main'
                            def encodedBranch = branchName.replaceAll('/', '%2F')
                            def fullJobPath = "${jobName}/${encodedBranch}"
                            echo "Triggering Multibranch job: ${fullJobPath}"
                            try {
                                build job: fullJobPath,
                                      wait: true,
                                      propagate: true
                            } catch (Exception e) {
                                // Branch/PR may not yet exist in child job (first scan pending)
                                echo "WARNING: Could not trigger ${fullJobPath}: ${e.message}"
                            }
                        }
                    }

                    parallel parallelJobs
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
