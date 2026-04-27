pipeline {
    agent any

  

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare Git') {
            steps {
                script {
                    sh """
                        git fetch origin main:refs/remotes/origin/main
                        git branch -a
                    """
                }
            }
        }

       stage('Detect Changed Services') {
            steps {
                script {

                   

                    def currentBranch = sh(
                        script: "git rev-parse --abbrev-ref HEAD",
                        returnStdout: true
                    ).trim()
                   

                    def headCommit = sh(
                        script: "git rev-parse HEAD",
                        returnStdout: true
                    ).trim()
                   

                    def mainCommit = sh(
                        script: "git rev-parse origin/main || true",
                        returnStdout: true
                    ).trim()
                    

                    echo "========== FETCH CHECK =========="
                    sh "git branch -a"
                    sh "git remote -v"

                    echo "========== DIFF FILES =========="

                   
                    def changedFilesRaw = sh(
                        script: "git diff --name-only origin/main..HEAD || true",
                        returnStdout: true
                    ).trim()

                    def changedFiles = changedFilesRaw ? changedFilesRaw.split("\\n") : []

                    def allServices = [
                        "backoffice", "storefront",
                        "backoffice-bff", "storefront-bff",
                        "media", "product", "cart", "order", "rating",
                        "customer", "location", "inventory", "tax",
                        "search", "recommendation", "promotion",
                        "payment", "payment-paypal", "webhook", "sampledata",
                        "common-library", "delivery"
                    ]

                    def changed = []

                  

                    for (file in changedFiles) {
                        echo "Checking file: ${file}"
                        for (svc in allServices) {
                            if (file.startsWith("${svc}/") || file.contains("/${svc}/")) {
                                echo "→ Matched service: ${svc}"
                                changed.add(svc)
                            }
                        }
                    }

                    
                    changed = changed.unique()
                    

                    if (changed.contains("common-library")) {
                        echo "Common library changed → rebuild all services"
                        changed = allServices
                    }

                    def result = changed.join(",")

                    env.CHANGED_SERVICES = result.toString()

                    if (!env.CHANGED_SERVICES?.trim()) {
                        env.CHANGED_SERVICES = ""
                    }
                    echo "========== FINAL RESULT =========="
                    echo "Changed services: ${CHANGED_SERVICES}"
                }
            }
        }

        // =========================
        // TEST PHASE
        // =========================
        stage('Test') {
            when {
                expression { env.CHANGED_SERVICES?.trim() }
            }
            steps {
                script {
                    def services = env.CHANGED_SERVICES?.trim() ? env.CHANGED_SERVICES.split(",") : []

                    def jobs = [:]

                    for (svc in services) {
                        jobs[svc] = {
                            if (svc in ["backoffice", "storefront"]) {
                                sh """
                                cd ${svc}
                                npm ci
                                npm test -- --coverage
                                """
                            } else {
                                sh """
                                cd ${svc}
                                mvn test
                                """
                            }
                        }
                    }

                    parallel jobs
                }
            }
        }

        // =========================
        // SECURITY SCAN
        // =========================
        stage('Security Scan') {
            when {
                expression { env.CHANGED_SERVICES?.trim() }
            }
            steps {
                echo "Placeholder for SonarQube, Snyk, Gitleaks"
            }
        }

        // =========================
        // BUILD PHASE
        // =========================
        stage('Build') {
            when {
                expression { env.CHANGED_SERVICES?.trim() }
            }
            steps {
                script {
                    def services = env.CHANGED_SERVICES?.trim() ? env.CHANGED_SERVICES.split(",") : []

                    def jobs = [:]

                    for (svc in services) {
                        jobs[svc] = {
                            if (svc in ["backoffice", "storefront"]) {
                                sh """
                                cd ${svc}
                                npm run build
                                docker build -t ${svc}:latest .
                                """
                            } else {
                                sh """
                                cd ${svc}
                                mvn package -DskipTests
                                docker build -t ${svc}:latest .
                                """
                            }
                        }
                    }

                    parallel jobs
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            publishCoverage adapters: [
                jacocoAdapter('**/target/site/jacoco/jacoco.xml')
            ]
        }
    }
}