pipeline {
    agent any

    environment {
        // Docker Hub Credentials ID configured in Jenkins (Username/Password)
        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-creds'
        // Replace with your actual Docker Hub username
        DOCKERHUB_USERNAME = 'holycore1' 
        // Maven Home (ensure 'maven-3.9' is configured in Jenkins Global Tool Configuration)
        MAVEN_HOME = tool 'maven-3.9.x'
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    echo "Starting CI Pipeline for branch: ${env.BRANCH_NAME}"
                }
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    def changedServices = []
                    def diffRange = ""

                    // Determine the range for git diff
                    if (env.CHANGE_ID) { 
                        // If it's a Pull Request, compare against the target branch (e.g., main)
                        sh "git fetch origin ${env.CHANGE_TARGET}"
                        diffRange = "origin/${env.CHANGE_TARGET}...HEAD"
                    } else if (env.GIT_PREVIOUS_SUCCESSFUL_COMMIT) {
                        // If it's a regular branch build, compare against the last successful commit
                        diffRange = "${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT}...HEAD"
                    } else {
                        // Fallback for the first build
                        diffRange = "HEAD~1...HEAD"
                    }

                    echo "Calculating diff for range: ${diffRange}"
                    def changedFiles = sh(script: "git diff --name-only ${diffRange}", returnStdout: true).trim().split('\n')
                    
                    def allServices = [
                        'cart', 'customer', 'inventory', 'media', 'order', 'payment', 
                        'product', 'promotion', 'rating', 'recommendation', 'search', 
                        'tax', 'webhook', 'backoffice', 'backoffice-bff', 'storefront', 'storefront-bff'
                    ]

                    for (file in changedFiles) {
                        // If core files change, build everything
                        if (file.startsWith('common-library/') || file == 'pom.xml' || file.startsWith('.github/')) {
                            echo "Core change detected in ${file}. Marking all services for build."
                            changedServices = allServices
                            break
                        }
                        // Check which service directory the file belongs to
                        for (service in allServices) {
                            if (file.startsWith("${service}/")) {
                                changedServices.add(service)
                            }
                        }
                    }

                    env.CHANGED_SERVICES = changedServices.unique().join(',')
                    echo "Services to build: ${env.CHANGED_SERVICES ?: 'None'}"
                }
            }
        }

        stage('Build and Test Services') {
            when {
                expression { env.CHANGED_SERVICES != '' }
            }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')
                    for (service in services) {
                        stage("Service: ${service}") {
                            echo "--- Building and Testing ${service} ---"
                            
                            // 1. Run Maven Build and Test
                            // -pl: project list, -am: also make (dependencies)
                            sh "${MAVEN_HOME}/bin/mvn clean install -pl ${service} -am -DskipTests=false"
                            
                            // 2. Publish Unit Test Results
                            junit testResults: "${service}/target/surefire-reports/*.xml", allowEmptyResults: true
                            
                            // 3. Publish and Enforce Jacoco Coverage (> 70% requirement)
                            // Note: Requires Jenkins Jacoco Plugin
                            jacoco(
                                execPattern: "${service}/target/*.exec",
                                classPattern: "${service}/target/classes",
                                sourcePattern: "${service}/src/main/java",
                                inclusionPattern: "**/*.class",
                                // Thresholds for build success
                                minimumInstructionCoverage: '70',
                                minimumBranchCoverage: '70',
                                minimumLineCoverage: '70',
                                buildOverBuild: true
                            )
                        }
                    }
                }
            }
        }

        stage('Docker Build & Push') {
            when {
                // Only build and push Docker images on main branch or tags
                anyOf {
                    branch 'main'
                    buildingTag()
                }
                expression { env.CHANGED_SERVICES != '' }
            }
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(',')
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
                        for (service in services) {
                            // Only build Docker images for actual services (skip library)
                            if (service == 'common-library') continue
                            
                            def imageName = "${DOCKERHUB_USERNAME}/yas-${service}"
                            def imageTag = "${env.BUILD_ID}"
                            
                            echo "Building Docker image: ${imageName}:${imageTag}"
                            def customImage = docker.build("${imageName}:${imageTag}", "-f ${service}/Dockerfile ./${service}")
                            
                            echo "Pushing image to Docker Hub..."
                            customImage.push()
                            customImage.push('latest')
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning up workspace..."
            cleanWs()
        }
        success {
            echo "CI Pipeline completed successfully!"
        }
        failure {
            echo "CI Pipeline failed. Please check the logs."
        }
    }
}
