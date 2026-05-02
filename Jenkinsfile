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
                    // Đọc danh sách module từ pom.xml (yêu cầu Pipeline Utility Steps plugin)
                    def pom = readMavenPom file: 'pom.xml'
                    
                    // Lọc bỏ 'common-library' vì nó đã được build ở stage trước
                    def services = pom.modules.findAll { it != 'common-library' }

                    // Hàm kiểm tra changeset tương tự như Declarative Pipeline
                    def checkChanges = { serviceName ->
                        if (env.FORCE_BUILD_ALL == 'true') return true
                        
                        def changed = false
                        def changeLogSets = currentBuild.changeSets
                        
                        // Nếu không lấy được lịch sử commit (ví dụ: build thủ công), mặc định build
                        if (changeLogSets.size() == 0) return true
                        
                        for (int i = 0; i < changeLogSets.size(); i++) {
                            def entries = changeLogSets[i].items
                            for (int j = 0; j < entries.length; j++) {
                                def files = entries[j].affectedFiles
                                for (int k = 0; k < files.size(); k++) {
                                    def path = files[k].path
                                    if (path.startsWith("${serviceName}/") || path.startsWith("common-library/") || path == "pom.xml") {
                                        changed = true
                                    }
                                }
                            }
                        }
                        return changed
                    }

                    // Khởi tạo danh sách các stage song song
                    def parallelStages = [:]

                    for (int i = 0; i < services.size(); i++) {
                        // Khai báo biến cục bộ để tránh lỗi scope closure trong Groovy
                        def serviceName = services[i]

                        if (checkChanges(serviceName)) {
                            parallelStages[serviceName] = {
                                stage("Build & Test ${serviceName}") {
                                    echo "Đang Build service: ${serviceName}..."
                                    lock('maven-build') {
                                        sh "mvn compile -pl ${serviceName}"
                                    }
                                    
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