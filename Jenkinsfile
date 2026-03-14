pipeline {
    agent any

    environment {
        CHANGED_MODULES = ''
        RUN_PIPELINE = 'false'
    }

    stages {
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
                        ? (rawChangedFiles.split('\n') as List<String>)
                            .collect { it?.trim()?.replaceFirst('^\\./', '') }
                            .findAll { it }
                        : []

                    echo "Changed files:\n${changedFiles.join('\n')}"

                    boolean shouldRunAll = changedFiles.any { file ->
                        globalImpactPaths.any { impacted ->
                            impacted.endsWith('/') ? file.startsWith(impacted) : file == impacted
                        }
                    }

                    List<String> impactedModules = shouldRunAll ? modules : []

                    if (!shouldRunAll) {
                        Set<String> detectedModules = new LinkedHashSet<>()

                        changedFiles.each { file ->
                            String topLevel = file.tokenize('/')[0]
                            if (topLevel && modules.contains(topLevel)) {
                                detectedModules.add(topLevel)
                            }

                            if (file.startsWith('services/')) {
                                def matcher = (file =~ /^services\/([a-z0-9-]+)-service\//)
                                if (matcher.find()) {
                                    String serviceModule = matcher.group(1)
                                    if (modules.contains(serviceModule)) {
                                        detectedModules.add(serviceModule)
                                    }
                                }
                            }
                        }

                        impactedModules = modules.findAll { detectedModules.contains(it) }
                    }

                    echo "Detected modules: ${impactedModules.join(',')}"

                    env.CHANGED_MODULES = impactedModules.join(',')
                    boolean hasImpactedModules = !impactedModules.isEmpty()
                    env.RUN_PIPELINE = hasImpactedModules.toString()
                    echo "RUN_PIPELINE=${env.RUN_PIPELINE}"

                    if (hasImpactedModules) {
                        echo "Impacted modules: ${env.CHANGED_MODULES}"
                    } else {
                        echo 'No service change detected. Skip build/test stages.'
                    }
                }
            }
        }

        stage('Build & Test Changed Services') {
            when {
                expression { env.RUN_PIPELINE?.toBoolean() }
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
                expression { !env.RUN_PIPELINE?.toBoolean() }
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
