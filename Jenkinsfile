// For Testcontainers (recommendation service): Jenkins agent needs Docker.
// - If Jenkins runs in Docker: start container with -v /var/run/docker.sock:/var/run/docker.sock and same user/group as host docker.
// - If Jenkins runs on host: install Docker and add the Jenkins process user to the docker group (e.g. usermod -aG docker jenkins).
pipeline {
    agent any

    environment {
        JAVA_HOME = "${WORKSPACE}/.tools/jdk-21"
        MAVEN_HOME = "${WORKSPACE}/.tools/maven"
        PATH = "${WORKSPACE}/.tools/jdk-21/bin:${WORKSPACE}/.tools/maven/bin:${env.PATH}"
        SONAR_HOST_URL = "http://127.0.0.1:9000"

    }

    parameters {
        booleanParam(name: 'FORCE_BUILD_ALL', defaultValue: false, description: 'Build all services regardless of changes')
    }

    stages {

        stage('Setup Tools') {
            steps {
                sh '''
                    mkdir -p .tools

                    if [ ! -d ".tools/jdk-21" ]; then
                        echo "Installing JDK 21..."
                        curl -fsSL https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz -o /tmp/jdk21.tar.gz
                        tar -xzf /tmp/jdk21.tar.gz -C .tools
                        mv .tools/jdk-21.0.2 .tools/jdk-21
                        rm -f /tmp/jdk21.tar.gz
                    fi

                    if [ ! -d ".tools/maven" ]; then
                        echo "Installing Maven 3.9.9..."
                        curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz -o /tmp/maven.tar.gz
                        tar -xzf /tmp/maven.tar.gz -C .tools
                        mv .tools/apache-maven-3.9.9 .tools/maven
                        rm -f /tmp/maven.tar.gz
                    fi

                    java -version
                    mvn -version
                '''
            }
        }

        stage('Detect Changes') {
            steps {
                script {
                    def allServices = [
                        'common-library',
                        'backoffice-bff',
                        'cart',
                        'customer',
                        'delivery',
                        'inventory',
                        'location',
                        'media',
                        'order',
                        'payment',
                        'payment-paypal',
                        'product',
                        'promotion',
                        'rating',
                        'recommendation',
                        'sampledata',
                        'search',
                        'storefront-bff',
                        'tax',
                        'webhook'
                    ]

                    def frontendServices = [
                        'storefront',
                        'backoffice'
                    ]

                    def commonLibDependents = allServices - ['common-library']

                    if (params.FORCE_BUILD_ALL) {
                        env.CHANGED_BACKEND_SERVICES  = allServices.join(',')
                        env.CHANGED_FRONTEND_SERVICES = frontendServices.join(',')
                        echo "FORCE_BUILD_ALL enabled — building everything"
                        return
                    }

                    def changedFiles = []
                    try {
                        changedFiles = sh(
                            script: "git diff --name-only HEAD~1 HEAD",
                            returnStdout: true
                        ).trim().split('\n').findAll { it }
                    } catch (e) {
                        echo "Could not detect changes (first build?). Building all services."
                        env.CHANGED_BACKEND_SERVICES  = allServices.join(',')
                        env.CHANGED_FRONTEND_SERVICES = frontendServices.join(',')
                        return
                    }

                    echo "Changed files:\n${changedFiles.join('\n')}"

                    def changedBackend  = [] as Set
                    def changedFrontend = [] as Set

                    def rootPomChanged = changedFiles.any { it == 'pom.xml' }

                    def commonLibChanged = changedFiles.any { it.startsWith('common-library/') } || rootPomChanged

                    if (commonLibChanged) {
                        changedBackend.addAll(allServices)
                    }

                    for (file in changedFiles) {
                        def service = allServices.find { file.startsWith("${it}/") }
                        if (service) {
                            changedBackend.add(service)
                        }
                        def frontend = frontendServices.find { file.startsWith("${it}/") }
                        if (frontend) {
                            changedFrontend.add(frontend)
                        }
                    }

                    env.CHANGED_BACKEND_SERVICES  = changedBackend ? changedBackend.toList().join(',') : ''
                    env.CHANGED_FRONTEND_SERVICES = changedFrontend ? changedFrontend.toList().join(',') : ''

                    echo "Backend services to build:  ${env.CHANGED_BACKEND_SERVICES ?: '(none)'}"
                    echo "Frontend services to build: ${env.CHANGED_FRONTEND_SERVICES ?: '(none)'}"
                }
            }
        }

        stage('Gitleaks Scan') {
            steps {
                script {
                    // Tự động tải gitleaks binary nếu chưa có trong workspace
                    sh '''
                        if [ ! -f ".tools/gitleaks" ]; then
                            echo "Downloading Gitleaks..."
                            curl -sSfL https://github.com/gitleaks/gitleaks/releases/download/v8.18.2/gitleaks_8.18.2_linux_x64.tar.gz \
                                -o /tmp/gitleaks.tar.gz
                            tar -xzf /tmp/gitleaks.tar.gz -C .tools gitleaks
                            chmod +x .tools/gitleaks
                            rm -f /tmp/gitleaks.tar.gz
                        fi
                    '''

                    // Quét toàn bộ lịch sử git, output ra file report JSON
                    sh '''
                        .tools/gitleaks detect \
                            --source . \
                            --report-format json \
                            --report-path gitleaks-report.json \
                            --exit-code 1 \
                            || true
                    '''
                    // --exit-code 1: trả về lỗi nếu phát hiện secret
                    // || true: tạm thời không fail pipeline, xem phần lưu ý bên dưới
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'gitleaks-report.json',
                                    allowEmptyArchive: true
                }
            }
        }


        // ===================================================================
        //  PHASE 1 — TEST
        // ===================================================================
        stage('Test') {
            when {
                expression { env.CHANGED_BACKEND_SERVICES }
            }
            stages {

                stage('Unit Tests') {
                    steps {
                        script {
                            def services = env.CHANGED_BACKEND_SERVICES.split(',')
                            def projects = services.collect { "-pl ${it}" }.join(' ')

                            sh """
                                mvn test \
                                    ${projects} \
                                    -am \
                                    -Djacoco.skip=false \
                                    -Dmaven.test.failure.ignore=true
                            """
                        }
                    }
                    post {
                        always {
                            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                            jacoco(
                                execPattern:   '**/target/jacoco.exec',
                                classPattern:  '**/target/classes',
                                sourcePattern: '**/src/main/java',
                                exclusionPattern: '**/config/**,**/exception/**,**/constants/**,**/*Application.class'
                            )
                        }
                    }
                }

                stage('Integration Tests') {
                    steps {
                        script {
                            def services = env.CHANGED_BACKEND_SERVICES.split(',')
                            def projects = services.collect { "-pl ${it}" }.join(' ')

                            sh """
                                mvn verify \
                                    ${projects} \
                                    -am \
                                    -DskipUnitTests=true \
                                    -Djacoco.skip=false \
                                    -Dmaven.test.failure.ignore=true
                            """
                        }
                    }
                    post {
                        always {
                            junit testResults: '**/target/failsafe-reports/*.xml', allowEmptyResults: true
                        }
                    }
                }

                stage('Coverage Gate (>70% line)') {
                    steps {
                        script {
                            def services = env.CHANGED_BACKEND_SERVICES.split(',')
                            def skipList = ['common-library', 'sampledata', 'delivery']
                            def servicesToCheck = services.findAll { !(it in skipList) }

                            if (servicesToCheck) {
                                def projects = servicesToCheck.collect { "-pl ${it}" }.join(' ')
                                sh """
                                    mvn verify \
                                        ${projects} \
                                        -am \
                                        -DskipTests \
                                        -Djacoco.check.skip=false
                                """
                            } else {
                                echo "No services to check coverage for."
                            }
                        }
                    }
                }
            }
        }

        stage('Frontend Test') {
            when {
                expression { env.CHANGED_FRONTEND_SERVICES }
            }
            steps {
                script {
                    def services = env.CHANGED_FRONTEND_SERVICES.split(',')
                    for (svc in services) {
                        dir(svc) {
                            sh 'npm ci'
                            sh 'npm run lint  || true'
                            sh 'npm run test -- --coverage --reporters=default --reporters=jest-junit || true'
                        }
                    }
                }
            }
            post {
                always {
                    junit testResults: '**/junit.xml', allowEmptyResults: true

                    publishHTML(target: [
                        reportDir:   'storefront/coverage/lcov-report',
                        reportFiles: 'index.html',
                        reportName:  'Storefront Coverage',
                        allowMissing: true
                    ])
                    publishHTML(target: [
                        reportDir:   'backoffice/coverage/lcov-report',
                        reportFiles: 'index.html',
                        reportName:  'Backoffice Coverage',
                        allowMissing: true
                    ])
                }
            }
        }
        stage('SonarQube Analysis') {
            when {
                expression { env.CHANGED_BACKEND_SERVICES }
            }
            steps {
                script {
                    def services = env.CHANGED_BACKEND_SERVICES.split(',')
                    def skipList = ['sampledata', 'delivery']
                    def servicesToScan = services.findAll { !(it in skipList) }

                    withSonarQubeEnv('jenkin-sonarsonar') { // Tên phải khớp với cấu hình ở Manage Jenkins
                        for (svc in servicesToScan) {
                            echo "SonarQube analyzing: ${svc}"
                            dir(svc) {
                                sh """
                                    mvn sonar:sonar \
                                        -Dsonar.projectKey=yas-${svc} \
                                        -Dsonar.projectName=yas-${svc} \
                                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                                        -DskipTests
                                """
                            }
                        }
                    }
                }
            }
        }

        // ===================================================================
        //  PHASE 2 — BUILD
        // ===================================================================
        stage('Build') {
            parallel {

                stage('Build Backend JARs') {
                    when {
                        expression { env.CHANGED_BACKEND_SERVICES }
                    }
                    steps {
                        script {
                            def services = env.CHANGED_BACKEND_SERVICES.split(',')
                            def projects = services.collect { "-pl ${it}" }.join(' ')

                            sh """
                                mvn package \
                                    ${projects} \
                                    -am \
                                    -DskipTests
                            """
                        }
                    }
                }

                stage('Build Frontend') {
                    when {
                        expression { env.CHANGED_FRONTEND_SERVICES }
                    }
                    steps {
                        script {
                            def services = env.CHANGED_FRONTEND_SERVICES.split(',')
                            for (svc in services) {
                                dir(svc) {
                                    sh 'npm ci'
                                    sh 'npm run build'
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Build Docker Images') {
            when {
                expression { env.CHANGED_BACKEND_SERVICES || env.CHANGED_FRONTEND_SERVICES }
            }
            steps {
                script {
                    def tag = "${env.BUILD_NUMBER}"
                    def allChanged = []

                    if (env.CHANGED_BACKEND_SERVICES) {
                        allChanged.addAll(env.CHANGED_BACKEND_SERVICES.split(',').toList())
                    }
                    if (env.CHANGED_FRONTEND_SERVICES) {
                        allChanged.addAll(env.CHANGED_FRONTEND_SERVICES.split(',').toList())
                    }

                    def servicesToBuild = allChanged.findAll { svc ->
                        fileExists("${svc}/Dockerfile")
                    }

                    for (svc in servicesToBuild) {
                        def imageName = "yas-${svc}:${tag}"
                        def imageLatest = "yas-${svc}:latest"
                        echo "Building Docker image: ${imageName}"
                        dir(svc) {
                            sh "docker build -t ${imageName} -t ${imageLatest} ."
                        }
                    }
                }
            }
        }
    
        stage('Snyk Security Scan') {
            when {
                expression { env.CHANGED_BACKEND_SERVICES }
            }
            steps {
                script {
                    def services = env.CHANGED_BACKEND_SERVICES.split(',')
                    def skipList = ['sampledata', 'delivery']
                    def servicesToScan = services.findAll { !(it in skipList) }

                    for (svc in servicesToScan) {
                        echo "🔒 Snyk scanning: ${svc}"
                        dir(svc) {
                            withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                                snyk(
                                    snykInstallation: 'snyk',
                                    snykTokenId: 'snyk-token',
                                    targetFile: 'pom.xml',
                                    organisation: '',
                                    projectName: "yas-${svc}",
                                    severity: 'high',
                                    failOnIssues: false
                                )
                            }
                        }
                    }
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/snyk-report.json',
                                    allowEmptyArchive: true
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/target/site/jacoco/**', allowEmptyArchive: true
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully."
        }
        failure {
            echo "Pipeline failed — check the test reports and logs above."
        }
    }
}
