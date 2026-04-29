@com.cloudbees.groovy.cps.NonCPS
def getAffectedPaths() {
    def paths = []
    for (changeSet in currentBuild.changeSets) {
        for (entry in changeSet.items) {
            for (file in entry.affectedFiles) {
                paths.add(file.path) // Lưu vào mảng String đơn giản
            }
        }
    }
    return paths
}

def getChangedServices() {
    def changedServices = [] as Set
    def gitDiffOutput = ''

    try {
        sh(script: 'git fetch origin main --no-tags --depth=1', returnStdout: false)
        gitDiffOutput = sh(script: 'git diff --name-only origin/main...HEAD', returnStdout: true).trim()
    } catch (e) {   
        echo "git diff failed, fallback to changeSets: ${e.message}"
    }

    def paths = []
    if (gitDiffOutput) {
        paths = gitDiffOutput.split('\n').toList()
    } else {
        paths = getAffectedPaths()
    }

    for (path in paths) {
        if (path.contains('/')) {
            def folder = path.split('/')[0]
            if (fileExists("${folder}/pom.xml")) {
                changedServices.add(folder)
            }
        }
    }
    return changedServices
}


pipeline {
    agent any
    
    tools {
        maven 'Maven3' 
        jdk 'Java21'   
    }

    // Ép Java21
    environment {
        PATH_TO_JAVA = tool name: 'Java21', type: 'jdk'
        JAVA_HOME = "${PATH_TO_JAVA}"
        PATH = "${PATH_TO_JAVA}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                // Lấy code từ GitHub về
                checkout scm
            }
        }

        stage('Test & Coverage') {
            steps {
                echo 'Đang kiểm tra phiên bản Java...'
                sh 'java -version'
                
                script {
                    def services = getChangedServices()
                    
                    if (services.isEmpty()) {
                        echo 'Đang chạy Unit Test và tạo report Coverage cho TOÀN BỘ dự án...'
                        sh "mvn clean verify '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java'"
                    } else {
                        echo "Đang chạy Unit Test và tạo report Coverage cho CÁC SERVICE BỊ THAY ĐỔI: ${services}"
                        for (service in services) {
                            stage("Test ${service}") {
                                sh "mvn clean verify -pl ${service} -am '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java'"
                            }
                        }
                    }
                }
            }

            // Di chuyển logic upload sang Phase Test theo yêu cầu của bài
            post {
                always {
                    echo 'Upload Test Result và TestCoverage cho Phase Test...'
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    script {
                        def services = getChangedServices()
                        def classPatterns = '**/target/classes'
                        def sourcePatterns = '**/src/main/java'
                        def execPatterns = '**/target/jacoco.exec'

                        if (!services.isEmpty()) {
                            classPatterns = services.collect { "${it}/target/classes" }.join(',')
                            sourcePatterns = services.collect { "${it}/src/main/java" }.join(',')
                            execPatterns = services.collect { "${it}/target/jacoco.exec" }.join(',')
                            echo "JaCoCo scope theo service thay đổi: ${services}"
                        }

                        jacoco execPattern: execPatterns,
                               classPattern: classPatterns,
                               sourcePattern: sourcePatterns,
                               changeBuildStatus: true,
                               minimumLineCoverage: '0.90'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    def services = getChangedServices()
                    
                    if (services.isEmpty()) {
                        echo 'Đang đóng gói TOÀN BỘ ứng dụng (Bỏ qua test vì đã chạy ở stage trước)...'
                        sh 'mvn package -DskipTests -DskipCompile=false'
                    } else {
                        echo 'Đang đóng gói CÁC SERVICE BỊ THAY ĐỔI...'
                        for (service in services) {
                            stage("Build ${service}") {
                                sh "mvn package -pl ${service} -am -DskipTests -DskipCompile=false"
                            }
                        }
                    }
                }
            }
        }
    }
}