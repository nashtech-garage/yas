pipeline {
    agent any

    tools {
        jdk 'jdk25'
        maven 'maven3'
    }

    stages {
        stage('Build Common Library') {
            steps {
                echo 'Đang Build Common Library...'
                sh 'mvn clean install -pl common-library -am'
            }
        }

        stage('Build & Test All Services') {
            steps {
                script {
                    // Đọc file pom.xml dưới dạng văn bản thuần để tránh lỗi Jenkins Sandbox (RejectedAccessException)
                    def pomText = readFile('pom.xml')
                    def services = []
                    def lines = pomText.split('\n')
                    for (int j = 0; j < lines.length; j++) {
                        def line = lines[j].trim()
                        if (line.startsWith('<module>') && line.endsWith('</module>')) {
                            def moduleName = line.substring(8, line.length() - 9)
                            if (moduleName != 'common-library') {
                                services.add(moduleName)
                            }
                        }
                    }

                    // 1. Lấy danh sách toàn bộ các file có thay đổi (Giải quyết Rủi ro số 3)
                    def changedFiles = []
                    
                    try {
                        if (env.CHANGE_TARGET) {
                            sh "git fetch --no-tags origin ${env.CHANGE_TARGET}:refs/remotes/origin/${env.CHANGE_TARGET} || true"
                            
                            // Nếu là Pull Request, so sánh độ lệch với nhánh đích
                            def diffStr = sh(script: "git diff --name-only origin/${env.CHANGE_TARGET}...HEAD", returnStdout: true).trim()
                            if (diffStr) changedFiles.addAll(diffStr.split('\n'))
                        } else {
                            // Chạy trên nhánh trực tiếp (main hoặc feature branch), lấy file thay đổi của commit đó
                            sh "git fetch --unshallow || git fetch --depth=50 origin HEAD || true"
                            def diffStr = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim()
                            if (diffStr) changedFiles.addAll(diffStr.split('\n'))
                        }
                    } catch (Exception e) {
                        echo "Warning: git diff thất bại, hệ thống sẽ sử dụng Jenkins changeSets làm phương án dự phòng."
                    }

                    // Lấy dữ liệu an toàn thông qua hàm @NonCPS để tránh lỗi NotSerializableException
                    changedFiles.addAll(extractChangedFiles())
                    
                    // Hàm kiểm tra cuối cùng (Không tự động chạy tất cả nếu rỗng)
                    def checkChanges = { serviceName ->
                        if (env.FORCE_BUILD_ALL == 'true') return true
                        return changedFiles.any { path ->
                            path.startsWith("${serviceName}/") || path.startsWith("common-library/")
                        }
                    }

                    // Khởi tạo danh sách các stage song song
                    def parallelStages = [:]

                    for (int i = 0; i < services.size(); i++) {
                        // Khai báo biến cục bộ để tránh lỗi scope closure trong Groovy
                        def serviceName = services[i]

                        if (checkChanges(serviceName)) {
                            parallelStages[serviceName] = {
                                stage("Build Phase - ${serviceName}") {
                                    echo "Đang Build service: ${serviceName}..."
                                    lock('maven-build') {
                                        sh "mvn install -pl ${serviceName} -am -DskipTests"
                                    }
                                }
                                
                                stage("Test Phase - ${serviceName}") {
                                    echo "Đang Test và Đo lường độ phủ cho service: ${serviceName}..."
                                    lock('maven-build') {
                                        sh "mvn org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report -pl ${serviceName} -Dserver.port=0 -Dspring.jmx.enabled=false" 
                                    }
                                    
                                    junit allowEmptyResults: true, 
                                          testResults: "${serviceName}/target/surefire-reports/*.xml"
                                          
                                    jacoco(
                                        execPattern: "${serviceName}/target/jacoco.exec",
                                        classPattern: "${serviceName}/target/classes",
                                        sourcePattern: "${serviceName}/src/main/java",
                                        exclusionPattern: '**/config/**,**/exception/**,**/constants/**,**/*Application.class', 
                                        changeBuildStatus: true,
                                        minimumLineCoverage: '70', 
                                        maximumLineCoverage: '70'       
                                    )
                                }
                            }
                        } else {
                            echo "Bỏ qua ${serviceName} vì không có sự thay đổi mã nguồn."
                        }
                    }

                    // Thực thi các stage (hiển thị giao diện Jenkins giống matrix)
                    if (parallelStages.size() > 0) {
                        parallel parallelStages
                    } else {
                        echo "Không có thay đổi nào trong các service, bỏ qua bước Build & Test."
                    }
                }
            }
        }
    }
}

@NonCPS
def extractChangedFiles() {
    def files = []
    def changeLogSets = currentBuild.changeSets
    if (changeLogSets != null) {
        for (int i = 0; i < changeLogSets.size(); i++) {
            def entries = changeLogSets[i].items
            for (int j = 0; j < entries.length; j++) {
                def affectedFiles = entries[j].affectedFiles
                for (int k = 0; k < affectedFiles.size(); k++) {
                    files.add(affectedFiles[k].path)
                }
            }
        }
    }
    return files
}

