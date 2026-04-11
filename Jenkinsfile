pipeline {
    agent any
    tools {
        // Nếu bạn đã cài được JDK21 trong Jenkins thì dùng, 
        // còn chưa thì cứ bỏ qua phần tools này và dùng mvnw như tui chỉ
        jdk 'jdk21' 
    }
    stages {
        stage('Detect Changes') {
            steps {
                script {
                    sh 'chmod +x mvnw'
                    // So sánh branch hiện tại với main để biết cái nào mới sửa
                    def changedFiles = sh(script: "git diff --name-only origin/main", returnStdout: true).trim()
                    
                    // Danh sách service (Tui lấy đúng list của dự án YAS cho bạn)
                    def SERVICES = ['backoffice-bff', 'cart', 'customer', 'inventory', 'location', 'media', 'order', 'payment', 'product', 'promotion', 'rating', 'search', 'storefront-bff']
                    
                    def toBuild = []
                    SERVICES.each { svc ->
                        if (changedFiles.contains("${svc}/")) { toBuild.add(svc) }
                    }
                    env.CHANGED_SERVICES = toBuild.join(",")
                }
            }
        }
        stage('Test & Build (YAS Standard)') {
            when { expression { return env.CHANGED_SERVICES != "" } }
            steps {
                script {
                    def list = env.CHANGED_SERVICES.split(",")
                    for (svc in list) {
                        dir(svc) {
                            echo "--- Đang xử lý service: ${svc} ---"
                            // Dùng verify để JaCoCo xuất báo cáo độ phủ
                            sh '../mvnw clean verify' 
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            // Đây mới là cái thầy cô cần nè
            junit '**/target/surefire-reports/*.xml'
        }
    }
}