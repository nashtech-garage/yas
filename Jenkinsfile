pipeline {
    agent any

    environment {
        // --- CẤU HÌNH BẮT BUỘC SỬA THEO MÔI TRƯỜNG CỦA BẠN ---
        DOCKER_REGISTRY   = credentials('docker-registry')   // username/password hoặc token
        DOCKER_REGISTRY_URL = 'https://index.docker.io/v1/'   // hoặc registry riêng
        // Nếu dùng SonarCloud:
        // SONAR_TOKEN = credentials('sonar-token')
        // ---------------------------------------------------
    }

    stages {
        stage('Detect changed services') {
            steps {
                script {
                    // Lấy danh sách file thay đổi so với base branch (main)
                    def changedFiles = getChangedFiles()
                    echo "Changed files: ${changedFiles}"

                    // Danh sách tất cả service (thư mục con cấp 1 chứa mã nguồn)
                    // Duyệt thư mục gốc để lấy động, tránh hardcode
                    def allServices = findServiceDirs()

                    // Xác định service nào bị ảnh hưởng
                    def affectedServices = []
                    allServices.each { service ->
                        def servicePrefix = "${service}/"
                        if (changedFiles.any { it.startsWith(servicePrefix) }) {
                            affectedServices.add(service)
                        }
                    }

                    // Nếu có thay đổi ở thư mục common-library hoặc deployment, build tất cả để an toàn
                    def commonDirs = ['common-library', 'scripts', 'deployment', 'k8s', 'docker']
                    if (changedFiles.any { file -> commonDirs.any { dir -> file.startsWith("${dir}/") } }) {
                        echo "Changes in common directories → rebuilding all services"
                        affectedServices = allServices
                    }

                    // Nếu không có service nào thay đổi (chỉ doc, readme), có thể skip hoặc build tất cả
                    if (affectedServices.isEmpty()) {
                        echo "No service changes detected. Skipping build."
                        // Gán biến để stage sau có thể kiểm tra và bỏ qua
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
            parallel {
                script {
                    def serviceList = env.AFFECTED_SERVICES.split(',')
                    def parallelStages = [:]
                    serviceList.each { service ->
                        parallelStages[service] = {
                            stage("Test ${service}") {
                                dir(service) {
                                    script {
                                        // Kiểm tra loại service: Java (build.gradle) hay Next.js (package.json)
                                        if (fileExists('build.gradle')) {
                                            // Java Spring Boot với Gradle
                                            sh './gradlew clean test jacocoTestReport'
                                            // Upload test result (JUnit XML)
                                            junit '**/build/test-results/test/*.xml'
                                            // Upload code coverage (JaCoCo)
                                            jacoco execPattern: '**/build/jacoco/test.exec'
                                        } 
                                        else if (fileExists('package.json')) {
                                            // Next.js frontend
                                            sh 'npm install'
                                            sh 'npm test -- --coverage --watchAll=false' // tuỳ cấu hình test của frontend
                                            // Upload test result (Jest JUnit)
                                            junit '**/junit.xml' // cần cấu hình Jest xuất JUnit
                                            // Có thể publish coverage với cobertura nếu có
                                            // cobertura coberturaReportFile: '**/coverage/cobertura-coverage.xml'
                                        }
                                        else {
                                            echo "No known test framework for ${service}, skipping test"
                                        }
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
            parallel {
                script {
                    def serviceList = env.AFFECTED_SERVICES.split(',')
                    def parallelStages = [:]
                    serviceList.each { service ->
                        parallelStages[service] = {
                            stage("Build ${service}") {
                                dir(service) {
                                    script {
                                        // Xác định Dockerfile (thường nằm trong thư mục service hoặc có Dockerfile riêng)
                                        def dockerfile = fileExists('Dockerfile') ? 'Dockerfile' : '../docker/Dockerfile'
                                        // Tên image: registry/service:build_number
                                        def imageName = "${DOCKER_REGISTRY}/${service}:${env.BUILD_NUMBER}"
                                        // Build image
                                        sh "docker build -t ${imageName} -f ${dockerfile} ."
                                        // Push lên registry
                                        sh "docker push ${imageName}"
                                        // Lưu lại tên image để dùng cho các stage sau (nếu cần deploy)
                                        env["IMAGE_${service.toUpperCase()}"] = imageName
                                    }
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
            // Dọn dẹp workspace (nếu agent dùng tạm)
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded!'
            // Có thể cập nhật GitHub commit status thành công (nếu dùng plugin GitHub Checks)
        }
        failure {
            echo 'Pipeline failed!'
            // Cập nhật GitHub commit status thất bại
        }
    }
}

// --- HÀM HỖ TRỢ ---

// Lấy danh sách file thay đổi so với base branch (cho PR) hoặc commit trước (cho push)
def getChangedFiles() {
    def files = []
    if (env.CHANGE_ID) {
        // Đây là PR: so sánh với target branch (thường là main)
        sh(script: "git fetch origin ${env.CHANGE_TARGET}", returnStdout: true)
        def output = sh(script: "git diff --name-only HEAD origin/${env.CHANGE_TARGET}", returnStdout: true).trim()
        files = output.split('\n') as List
    } else {
        // Push trực tiếp lên branch (không qua PR) - so với commit trước
        def output = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim()
        files = output.split('\n') as List
    }
    return files
}

// Tự động tìm tất cả thư mục con cấp 1 có chứa build.gradle hoặc package.json
// (loại trừ các thư mục không phải service)
def findServiceDirs() {
    def serviceDirs = []
    def dirs = sh(script: "find . -maxdepth 1 -type d -not -name '.' -printf '%f\\n'", returnStdout: true).trim().split('\n')
    dirs.each { dir ->
        // Kiểm tra trong thư mục có build.gradle hoặc package.json không
        if (fileExists("${dir}/build.gradle") || fileExists("${dir}/package.json")) {
            serviceDirs.add(dir)
        }
    }
    // Loại bỏ các thư mục như common-library, deployment, k8s,... nếu chúng có file trên (thường không)
    def excludeDirs = ['common-library', 'deployment', 'k8s', 'docker', 'scripts', 'docs', 'automation-ui', 'checkstyle']
    serviceDirs = serviceDirs - excludeDirs
    return serviceDirs
}
