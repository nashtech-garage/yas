pipeline {
    agent any

    environment {
        DOCKER_REGISTRY   = credentials('109dfa26-aa69-4c70-afb7-68ca0e9b6343')
        DOCKER_REGISTRY_URL = 'https://index.docker.io/v1/'
    }

    stages {
        stage('Detect changed services') {
            steps {
                script {
                    def changedFiles = getChangedFiles()
                    echo "Changed files: ${changedFiles}"

                    def allServices = findServiceDirs()
                    def affectedServices = []
                    allServices.each { service ->
                        def servicePrefix = "${service}/"
                        if (changedFiles.any { it.startsWith(servicePrefix) }) {
                            affectedServices.add(service)
                        }
                    }

                    def commonDirs = ['common-library', 'scripts', 'deployment', 'k8s', 'docker']
                    if (changedFiles.any { file -> commonDirs.any { dir -> file.startsWith("${dir}/") } }) {
                        echo "Changes in common directories → rebuilding all services"
                        affectedServices = allServices
                    }

                    if (affectedServices.isEmpty()) {
                        echo "No service changes detected. Skipping build."
                        env.SKIP_BUILD = 'true'
                        return
                    }

                    env.AFFECTED_SERVICES = affectedServices.join(',')
                    env.SKIP_BUILD = 'false'
                    echo "Affected services: ${env.AFFECTED_SERVICES}"
                }
            }
        }

        stage('Test') {
            when {
                expression { env.SKIP_BUILD != 'true' }
            }
            steps {
                script {
                    def serviceList = env.AFFECTED_SERVICES.split(',')
                    def parallelStages = [:]
                    serviceList.each { service ->
                        parallelStages["Test ${service}"] = {
                            stage("Test ${service}") {
                                dir(service) {
                                    if (fileExists('build.gradle')) {
                                        sh './gradlew clean test jacocoTestReport'
                                        junit '**/build/test-results/test/*.xml'
                                        jacoco execPattern: '**/build/jacoco/test.exec'
                                    } else if (fileExists('package.json')) {
                                        sh 'npm install'
                                        sh 'npm test -- --coverage --watchAll=false'
                                        junit '**/junit.xml'
                                    } else {
                                        echo "No known test framework for ${service}, skipping test"
                                    }
                                }
                            }
                        }
                    }
                    parallel parallelStages
                }
            }
        }

        stage('Build and Publish') {
            when {
                expression { env.SKIP_BUILD != 'true' }
            }
            steps {
                script {
                    def serviceList = env.AFFECTED_SERVICES.split(',')
                    def parallelStages = [:]
                    serviceList.each { service ->
                        parallelStages["Build ${service}"] = {
                            stage("Build ${service}") {
                                dir(service) {
                                    def dockerfile = fileExists('Dockerfile') ? 'Dockerfile' : '../docker/Dockerfile'
                                    def imageName = "${DOCKER_REGISTRY}/${service}:${env.BUILD_NUMBER}"
                                    sh "docker build -t ${imageName} -f ${dockerfile} ."
                                    sh "docker push ${imageName}"
                                    env["IMAGE_${service.toUpperCase()}"] = imageName
                                }
                            }
                        }
                    }
                    parallel parallelStages
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}

// --- HÀM HỖ TRỢ ---
def getChangedFiles() {
    def files = []
    if (env.CHANGE_ID) {
        sh(script: "git fetch origin ${env.CHANGE_TARGET}", returnStdout: true)
        def output = sh(script: "git diff --name-only HEAD origin/${env.CHANGE_TARGET}", returnStdout: true).trim()
        files = output.split('\n') as List
    } else {
        def output = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim()
        files = output.split('\n') as List
    }
    return files
}

def findServiceDirs() {
    def serviceDirs = []
    def dirs = sh(script: "find . -maxdepth 1 -type d -not -name '.' -printf '%f\\n'", returnStdout: true).trim().split('\n')
    dirs.each { dir ->
        if (fileExists("${dir}/build.gradle") || fileExists("${dir}/package.json")) {
            serviceDirs.add(dir)
        }
    }
    def excludeDirs = ['common-library', 'deployment', 'k8s', 'docker', 'scripts', 'docs', 'automation-ui', 'checkstyle']
    serviceDirs = serviceDirs - excludeDirs
    return serviceDirs
}
