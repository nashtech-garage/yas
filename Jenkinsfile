pipeline {
    agent any

    environment {
        CHANGED_MODULES = ''
        RUN_PIPELINE = 'false'
    }

    stages {
        stage('Init') {
            steps {
                echo 'CI Pipeline for YAS monorepo!'
                sh 'mvn --version'
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    List<String> modules = [
                        'backoffice-bff', 'cart', 'customer', 'inventory', 'location',
                        'media', 'order', 'payment-paypal', 'payment', 'product',
                        'promotion', 'rating', 'search', 'storefront-bff', 'tax',
                        'webhook', 'sampledata', 'recommendation', 'delivery'
                    ]

                    List<String> globalImpactPaths = [
                        'pom.xml',
                        'common-library/',
                        'checkstyle/'
                    ]

                    String compareBase = env.CHANGE_TARGET ?: 'HEAD~1'
                    if (env.CHANGE_TARGET) {
                        sh "git fetch --no-tags --depth=200 origin ${env.CHANGE_TARGET}"
                        compareBase = "origin/${env.CHANGE_TARGET}"
                    }

                    String rawChangedFiles = sh(
                        script: "git diff --name-only ${compareBase}...HEAD",
                        returnStdout: true
                    ).trim()

                    List<String> changedFiles = rawChangedFiles
                        ? rawChangedFiles.split('\n') as List<String>
                        : []

                    echo "Changed files:\n${changedFiles.join('\n')}"

                    boolean shouldRunAll = changedFiles.any { file ->
                        globalImpactPaths.any { impacted ->
                            impacted.endsWith('/') ? file.startsWith(impacted) : file == impacted
                        }
                    }

                    List<String> impactedModules = shouldRunAll
                        ? modules
                        : modules.findAll { module ->
                            changedFiles.any { file ->
                                file.startsWith("${module}/") || file.startsWith("services/${module}-service/")
                            }
                        }

                    env.CHANGED_MODULES = impactedModules.join(',')
                    env.RUN_PIPELINE = impactedModules ? 'true' : 'false'

                    if (env.RUN_PIPELINE == 'true') {
                        echo "Impacted modules: ${env.CHANGED_MODULES}"
                    } else {
                        echo 'No service change detected. Skip build/test stages.'
                    }
                }
            }
        }

        stage('Build & Test Changed Services') {
            when {
                expression { env.RUN_PIPELINE == 'true' }
            }
            steps {
                script {
                    env.CHANGED_MODULES.split(',').findAll { it?.trim() }.each { module ->
                        echo "Build & test module: ${module}"
                        sh "mvn -B -pl ${module} -am clean test"
                    }
                }
            }
        }

        stage('No Service Changes') {
            when {
                expression { env.RUN_PIPELINE != 'true' }
            }
            steps {
                echo 'No impacted service in this commit/PR. Nothing to build.'
            }
        }
    }

    post {
        always {
            echo 'Pipeline complete!'
        }
    }
}
