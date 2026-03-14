pipeline {
    agent any

    environment {
        CHANGED_MODULES = ''
        RUN_PIPELINE = 'false'
    }

    stages {
        stage('Detect Changed Modules'){
            steps{
                script{
                    List<String> modulesPaths = ['order/', 'cart/', 'payment/', 'common-library/']

                    // Get the list of changed files in the last commit
                    List<String> changedFiles = sh(
                        script: "git diff --name-only HEAD~1",
                        returnStdout: true
                    ).trim().split("\n").toList().findAll { it?.trim() }

                    echo "Changed Files: ${changedFiles}"

                    // Check which modules are impacted based on the changed files
                    Set<String> impactedModules = new LinkedHashSet<>()
                    for (file in changedFiles) {
                        for (modulePath in modulesPaths) {
                            if (file.startsWith(modulePath)) {
                                impactedModules.add(modulePath.replaceAll('/$', ''))
                                break
                            }
                        }
                    }

                    // Set environment variables based on impacted modules
                    if (!impactedModules.isEmpty()) {
                        env.CHANGED_MODULES = impactedModules.join(',')
                        env.RUN_PIPELINE = 'true'
                    } else {
                        env.CHANGED_MODULES = ''
                        env.RUN_PIPELINE = 'false'
                    }

                    echo "Changed Modules: ${env.CHANGED_MODULES}"
                    echo "Run Pipeline: ${env.RUN_PIPELINE}"
                }
            }
        }

        stage('Run Tests'){
            when {
                expression { return env.RUN_PIPELINE == 'true' }
            }
            steps {
                echo "Running tests for impacted modules: ${env.CHANGED_MODULES}"
            }
        }

        stage('Build and Deploy'){
            when {
                expression { return env.RUN_PIPELINE == 'true' }
            }
            steps {
                echo "Building and deploying impacted modules: ${env.CHANGED_MODULES}"
            }
        }
    }

    post {
        always {
            echo 'Pipeline complete!'
        }
    }
}
