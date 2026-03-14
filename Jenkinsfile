def IMPACTED_MODULES = []
def MODULES_PATH = ['order/', 'cart/', 'payment/', 'common-library/']

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
                    // Get the list of changed files in the last commit
                    def changedFiles = sh(script: "git diff --name-only HEAD~1", returnStdout: true).trim().split("\n")
                    echo "Changed Files: ${changedFiles}"
                    // Check which modules are impacted based on the changed files
                    for (file in changedFiles) {
                        for (module in MODULES_PATH) {
                            if (file.startsWith(module)) {
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
