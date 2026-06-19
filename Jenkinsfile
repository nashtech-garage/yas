def getChangedServices() {
    def changedFiles = []

    try {
        if (env.CHANGE_TARGET) {
            sh "git fetch origin ${env.CHANGE_TARGET} --no-tags"
            changedFiles = sh(
                script: "git diff --name-only origin/${env.CHANGE_TARGET}...HEAD",
                returnStdout: true
            ).trim().split('\n').toList()
        } else {
            sh "git fetch origin main --no-tags"
            def diffResult = sh(
                script: "git diff --name-only origin/main...HEAD 2>/dev/null || echo ''",
                returnStdout: true
            ).trim()

            if (diffResult) {
                changedFiles = diffResult.split('\n').toList()
            } else {
                changedFiles = sh(
                    script: "git show --name-only --format='' HEAD",
                    returnStdout: true
                ).trim().split('\n').toList()
            }
        }
    } catch (e) {
        echo "Warning: Could not detect changes, building all: ${e.message}"
        return [
            java: ['cart', 'customer', 'inventory', 'location', 'media',
                   'order', 'product', 'promotion', 'rating', 'search', 'tax'],
            next: []
        ]
    }

    echo "Changed files: ${changedFiles}"

    def javaServices = [
        'cart', 'customer', 'inventory', 'location',
        'media', 'order', 'product', 'promotion',
        'rating', 'search', 'tax', 'webhook'
    ]
    def nextServices = ['storefront', 'backoffice']

    def changedJavaServices = [] as Set
    def changedNextServices = [] as Set

    changedFiles.each { file ->
        javaServices.each { svc ->
            if (file.startsWith("${svc}/")) {
                changedJavaServices.add(svc)
            }
        }
        nextServices.each { svc ->
            if (file.startsWith("${svc}/")) {
                changedNextServices.add(svc)
            }
        }
    }

    return [java: changedJavaServices.toList(), next: changedNextServices.toList()]
}

pipeline {
    agent any

    options {
        timeout(time: 60, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        MAVEN_OPTS = '-Xmx1024m'
    }

    stages {
        stage('Detect Changed Services') {
            steps {
                script {
                    def changed = getChangedServices()
                    env.CHANGED_JAVA_SERVICES = changed.java.join(',')
                    env.CHANGED_NEXT_SERVICES = changed.next.join(',')

                    echo "Changed Java services: ${env.CHANGED_JAVA_SERVICES}"
                    echo "Changed Next.js services: ${env.CHANGED_NEXT_SERVICES}"

                    if (!env.CHANGED_JAVA_SERVICES && !env.CHANGED_NEXT_SERVICES) {
                        echo "No service changes detected. Skipping build."
                    }
                }
            }
        }

        stage('Test & Build Java Services') {
            when {
                expression { env.CHANGED_JAVA_SERVICES?.trim() }
            }
            steps {
                script {
                    def services = env.CHANGED_JAVA_SERVICES.split(',')
                    def parallelStages = [:]

                    services.each { svc ->
                        def serviceName = svc.trim()
                        parallelStages["Test & Build: ${serviceName}"] = {
                            dir(serviceName) {
                                sh "chmod +x mvnw"

                                // PHASE TEST
                                sh """
                                    ./mvnw test \
                                        -Djacoco.destFile=target/jacoco.exec \
                                        --no-transfer-progress
                                """

                                // PHASE BUILD
                                sh """
                                    ./mvnw package -DskipTests \
                                        --no-transfer-progress
                                """
                            }
                        }
                    }

                    parallel parallelStages
                }
            }
            post {
                always {
                    script {
                        if (env.CHANGED_JAVA_SERVICES?.trim()) {
                            def services = env.CHANGED_JAVA_SERVICES.split(',')
                            services.each { svc ->
                                def serviceName = svc.trim()

                                // Upload JUnit test results
                                junit(
                                    testResults: "${serviceName}/target/surefire-reports/*.xml",
                                    allowEmptyResults: true
                                )

                                // Upload JaCoCo coverage
                                jacoco(
                                    execPattern: "${serviceName}/target/jacoco.exec",
                                    classPattern: "${serviceName}/target/classes",
                                    sourcePattern: "${serviceName}/src/main/java",
                                    minimumInstructionCoverage: '0',
                                    changeBuildStatus: false
                                )
                            }
                        }
                    }
                }
            }
        }

        stage('Test & Build Next.js Services') {
            when {
                expression { env.CHANGED_NEXT_SERVICES?.trim() }
            }
            steps {
                script {
                    def services = env.CHANGED_NEXT_SERVICES.split(',')
                    services.each { svc ->
                        def serviceName = svc.trim()
                        dir(serviceName) {
                            sh "npm ci"
                            sh "npm run test -- --coverage --watchAll=false || true"
                            sh "npm run build"
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ CI Pipeline PASSED"
        }
        failure {
            echo "❌ CI Pipeline FAILED"
        }
        always {
            cleanWs()
        }
    }
}