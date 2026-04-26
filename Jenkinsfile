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

        stage('Determine Changed Services') {
            steps {
                script {
                    // Lấy các thay đổi từ commit mới nhất
                    def changedFiles = sh(script: "git diff-tree --no-commit-id --name-only -r HEAD || true", returnStdout: true).trim()
                    
                    def changedModules = []
                    def lines = changedFiles.split('\n')
                    for (line in lines) {
                        if (line.contains('/')) {
                            def module = line.substring(0, line.indexOf('/'))
                            // Chỉ lấy các thư mục có file pom.xml
                            if (fileExists("${module}/pom.xml") && !changedModules.contains(module)) {
                                changedModules.add(module)
                            }
                        }
                    }
                    
                    if (changedModules.isEmpty()) {
                        env.MAVEN_PROJECT_LIST = ""
                        echo "Không phát hiện thay đổi trong các service cụ thể. Sẽ build toàn bộ hệ thống."
                    } else {
                        env.MAVEN_PROJECT_LIST = changedModules.join(',')
                        echo "Phát hiện thay đổi ở các service: ${env.MAVEN_PROJECT_LIST}. Chỉ build và test các service này."
                    }
                }
            }
        }

        stage('Test & Coverage') {
            steps {
                echo 'Đang kiểm tra phiên bản Java...'
                sh 'java -version'
                
                script {
                    def mavenArgs = ""
                    if (env.MAVEN_PROJECT_LIST != "") {
                        mavenArgs = "-pl ${env.MAVEN_PROJECT_LIST} -am"
                        echo "Đang chạy Unit Test và tạo report Coverage cho các service thay đổi: ${env.MAVEN_PROJECT_LIST}..."
                    } else {
                        echo 'Đang chạy Unit Test và tạo report Coverage cho toàn bộ dự án...'
                    }
                    
                    sh "mvn clean test jacoco:report ${mavenArgs} '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java'"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    def mavenArgs = ""
                    if (env.MAVEN_PROJECT_LIST != "") {
                        mavenArgs = "-pl ${env.MAVEN_PROJECT_LIST} -am"
                        echo "Đang đóng gói các service thay đổi: ${env.MAVEN_PROJECT_LIST}..."
                    } else {
                        echo 'Đang đóng gói toàn bộ ứng dụng...'
                    }
                    
                    sh "mvn package -DskipTests -DskipCompile=false ${mavenArgs}"
                }
            }
        }
    }

//Lấy báo cáo
    post {
        always {
            echo 'Pipeline hoàn thành (Dù Pass hay Fail). Đang kéo báo cáo Test và Coverage...'
            
            
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            
           
            jacoco execPattern: '**/target/jacoco.exec',
                   classPattern: '**/target/classes',
                   sourcePattern: '**/src/main/java',
                   changeBuildStatus: true,
                   minimumLineCoverage: '70',
                   maximumLineCoverage: '70'
        }
    }
}   