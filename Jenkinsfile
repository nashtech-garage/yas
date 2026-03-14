// 1. Define variables here to make them globally accessible and mutable
def changedModules = ''
def runPipeline = false

pipeline {
    agent any

    // Removed the variables from here because we want to update them dynamically

    stages {
        stage('Detect Changed Modules'){
            steps{
                script{
                    List<String> modulesPaths = ['order/', 'cart/', 'payment/', 'common-library/']

                    List<String> changedFiles = sh(
                        script: "git diff --name-only HEAD~1",
                        returnStdout: true
                    ).trim().split("\n").toList().findAll { it?.trim() }

                    echo "Changed Files: ${changedFiles}"

                    List<String> impactedModules = modulesPaths
                        .findAll { modulePath ->
                            changedFiles.any { file -> file.startsWith(modulePath) }
                        }
                        .collect { it.replaceAll('/$', '') }
                        .unique()

                    echo "Impacted modules: ${impactedModules}"

                    // 2. Update the global variables directly
                    if (impactedModules.size() > 0) {
                        changedModules = impactedModules.join(',')
                        runPipeline = true
                    } else {
                        changedModules = ''
                        runPipeline = false
                    }

                    echo "Changed Modules: ${changedModules}"
                    echo "Run Pipeline: ${runPipeline}"
                }
            }
        }

        stage('Run Tests'){
            when {
                // 3. Evaluate the global variable
                expression { return runPipeline == true }
            }
            steps {
                echo "Running tests for impacted modules: ${changedModules}"
            }
        }

        stage('Build and Deploy'){
            when {
                expression { return runPipeline == true }
            }
            steps {
                echo "Building and deploying impacted modules: ${changedModules}"
            }
        }
    }

    post {
        always {
            echo 'Pipeline complete!'
        }
    }
}