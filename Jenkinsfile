def IMPACTED_MODULES = []

pipeline {
    agent any

    environment {
        CHANGED_MODULES = ''
        RUN_PIPELINE = 'false'
        MODULES_PATH = {
            'cart': 'cart/',
            'order': 'order/',
            'payment': 'payment/',
            'common-library': 'common-library/'
        }
    }

    stages {
        stage('Detect Changed Modules'){
            steps{
                script{
                    // Get the list of changed files in the last commit
                    def changedFiles = sh(script: "git diff --name-only HEAD~1", returnStdout: true).trim().split("\n")

                    // Check which modules are impacted based on the changed files
                    for (file in changedFiles) {
                        for (module in MODULES_PATH.keySet()) {
                            if (file.startsWith(MODULES_PATH[module])) {
                                IMPACTED_MODULES.add(module)
                                break
                            }
                        }
                    }

                    // Set environment variables based on impacted modules
                    if (IMPACTED_MODULES.size() > 0) {
                        env.CHANGED_MODULES = IMPACTED_MODULES.join(',')
                        env.RUN_PIPELINE = 'true'
                    } else {
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
