pipeline {
    agent any

    tools {
        jdk 'JDK_25'
        maven 'maven'
        nodejs 'NodeJS'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare Git') {
            steps {
                script {
                    sh """
                        git fetch origin main:refs/remotes/origin/main
                        git branch -a
                    """
                }
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    echo "========== FETCH ORIGIN/MAIN =========="
                    sh "git fetch origin main"

                    echo "========== BRANCH INFO =========="
                    sh "git branch -a"
                    sh "git remote -v"

                    echo "========== COMMIT INFO =========="
                    def headCommit = sh(
                        script: "git rev-parse HEAD",
                        returnStdout: true
                    ).trim()
                    echo "HEAD commit:        ${headCommit}"

                    echo "========== RAW GIT DIFF =========="
                    // So sánh với chính nhánh hiện tại (HEAD~1..HEAD), nếu clone nông (shallow clone) thì fallback sang git show
                    def changedFilesRaw = sh(
                        script: "git diff --name-only HEAD~1..HEAD || git show --name-only --format='' HEAD",
                        returnStdout: true
                    ).trim()
                    echo "Raw output from git diff:\n[${changedFilesRaw}]"

                    def changedFiles = changedFilesRaw ? changedFilesRaw.split("\\n") : []
                    echo "Total files detected: ${changedFiles.size()}"
                    for (file in changedFiles) {
                        echo "  FILE: [${file}]"
                    }

                    echo "========== MATCHING SERVICES =========="
                    def allServices = [
                        "backoffice", "storefront",
                        "backoffice-bff", "storefront-bff",
                        "media", "product", "cart", "order", "rating",
                        "customer", "location", "inventory", "tax",
                        "search", "recommendation", "promotion",
                        "payment", "payment-paypal", "webhook", "sampledata",
                        "common-library", "delivery", "swagger"
                    ]

                    def changed = []

                    for (file in changedFiles) {
                        def matched = false
                        for (svc in allServices) {
                            if (file.startsWith("${svc}/")) {
                                echo "  [MATCH]    file='${file}' → service='${svc}'"
                                if (!changed.contains(svc)) {
                                    changed.add(svc)
                                }
                                matched = true
                            }
                        }
                        if (!matched) {
                            echo "  [NO MATCH] file='${file}' → root file, ignored"
                        }
                    }

                    echo "========== PRE-COMMON-LIBRARY CHECK =========="
                    echo "Changed services before common-library check: ${changed}"

                    if (changed.contains("common-library")) {
                        echo "common-library changed → rebuild ALL services"
                        changed = allServices
                    }

                    env.CHANGED_SERVICES = changed.join(",")

                    echo "========== FINAL RESULT =========="
                    echo "CHANGED_SERVICES = [${env.CHANGED_SERVICES}]"
                }
            }
        }

        // =========================
        // PREPARE DEPENDENCIES
        // =========================
        stage('Prepare Dependencies') {
            when {
                expression { env.CHANGED_SERVICES?.trim() }
            }
            steps {
                script {
                    def rawServices = env.CHANGED_SERVICES?.trim() ? env.CHANGED_SERVICES.split(',').collect { it.trim() } : []
                    def services = rawServices.unique()
                    def hasMaven = services.any { !(it in ["backoffice", "storefront"]) }
                    
                    if (hasMaven) {
                        echo "=========================================================="
                        echo "[PREPARE] INSTALLING MAVEN DEPENDENCIES TO LOCAL REPO"
                        echo "=========================================================="
                        sh "mvn clean install -DskipTests"
                    }
                }
            }
        }

        // =========================
        // TEST PHASE
        // =========================
        stage('Test') {
            when {
                expression { env.CHANGED_SERVICES?.trim() }
            }
            steps {
                script {
                    // 1. Get the list of services, trim whitespace, and remove DUPLICATES (unique)
                    def rawServices = env.CHANGED_SERVICES?.trim() ? env.CHANGED_SERVICES.split(',').collect { it.trim() } : []
                    def services = rawServices.unique() // This completely solves the issue of running multiple times

                    // 2. Categorize Java and NodeJS (Frontend) services
                    def javaServices = services.findAll { !(it in ['backoffice', 'storefront']) && it != '' }
                    def nodeServices = services.findAll { it in ['backoffice', 'storefront'] }

                    def jobs = [:]

                    // 3. Process Java Services: Combine into a single command!
                    if (!javaServices.isEmpty()) {
                        def plArgs = javaServices.join(',') // Example: "product,cart"
                        def classPatterns = javaServices.collect { "${it}/target/classes" }.join(',')
                        def sourcePatterns = javaServices.collect { "${it}/src/main/java" }.join(',')
                        jobs['Java Services Tests'] = {
                            sh "chmod +x mvnw"
                            // Bring back the -am flag. Since we run in a single command, there are no race conditions or ${revision} errors
                            sh "./mvnw -B test jacoco:report -pl ${plArgs} -am -DskipITs -Dmaven.test.failure.ignore=true"

                            // Aggregate coverage reports from all modules using **
                            jacoco(
                                execPattern: '**/target/jacoco.exec',
                                exclusionPattern: '**/com/yas/**/*Application.class, **/com/yas/**/config/**, **/com/yas/**/exception/**, **/com/yas/**/constants/**',
                                classPattern: classPatterns,
                                sourcePattern: sourcePatterns,
                                minimumInstructionCoverage: '0', maximumInstructionCoverage: '0',
                                // minimumLineCoverage: '70', maximumLineCoverage: '70',
                                // minimumBranchCoverage: '70', maximumBranchCoverage: '70',
                                changeBuildStatus: false
                            )
                            // if (currentBuild.result == 'FAILURE' || currentBuild.result == 'UNSTABLE') {
                            //     error("Test coverage below 70%")
                            // }
                        }
                    }

                    // 4. Process Frontend (Node): Keep running in parallel as they are completely independent
                    for (nodeSvc in nodeServices) {
                        def svcName = nodeSvc // Assign to a local variable to avoid Groovy loop scope issues
                        jobs[svcName] = {
                            dir(svcName) {
                                sh """
                                    if ! command -v npm >/dev/null 2>&1; then
                                        echo "WARNING: npm command not found on agent. Skipping tests for ${svcName}."
                                        exit 0
                                    fi
                                    if ! grep -q '"test":' package.json; then
                                        echo "WARNING: No 'test' script defined in package.json. Skipping tests for ${svcName}."
                                        exit 0
                                    fi
                                    npm ci
                                    npm test -- --coverage
                                """
                            }
                        }
                    }

                    // 5. Execute in parallel
                    if (jobs.size() > 0) {
                        parallel jobs
                    } else {
                        echo "No services to test."
                    }
                }
            }
        }

        // =========================
        // SECURITY SCAN
        // =========================
        stage('Security Scan') {
            when {
                expression { env.CHANGED_SERVICES?.trim() }
            }
            steps {
                script {
                    def rawServices = env.CHANGED_SERVICES?.trim() ? env.CHANGED_SERVICES.split(',').collect { it.trim() } : []
                    def services = rawServices.unique()
                    def securityJobs = [:]

                    securityJobs['Gitleaks'] = {
                        catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                            sh """
                                echo "=========================================================="
                                echo "[SECURITY] STARTING GITLEAKS SCAN (origin/main..HEAD)"
                                echo "=========================================================="
                                curl -sL https://github.com/gitleaks/gitleaks/releases/download/v8.18.4/gitleaks_8.18.4_linux_x64.tar.gz | tar xz
                                ./gitleaks detect --log-opts="origin/main..HEAD" --verbose
                            """
                        }
                    }

                    for (svc in services) {
                        def currentSvc = svc

                        securityJobs["SonarCloud-${currentSvc}"] = {
                            catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                                withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                                    if (currentSvc in ["backoffice", "storefront"]) {
                                        sh """
                                            echo "=========================================================="
                                            echo "[SECURITY] STARTING SONARCLOUD SCAN (JS/NPM): ${currentSvc}"
                                            echo "=========================================================="
                                            cd ${currentSvc}
                                            sonar-scanner -Dsonar.projectKey=intro-to-devops_yas-${currentSvc} -Dsonar.organization=intro-to-devops -Dsonar.sources=. -Dsonar.host.url=https://sonarcloud.io -Dsonar.token=\$SONAR_TOKEN
                                        """
                                    } else {
                                        sh """
                                            echo "=========================================================="
                                            echo "[SECURITY] STARTING SONARCLOUD SCAN (MAVEN): ${currentSvc}"
                                            echo "=========================================================="
                                            cd ${currentSvc}
                                            mvn sonar:sonar -Dsonar.projectKey=intro-to-devops_yas-${currentSvc} -Dsonar.organization=intro-to-devops -Dsonar.host.url=https://sonarcloud.io -Dsonar.token=\$SONAR_TOKEN
                                        """
                                    }
                                }
                            }
                        }

                        securityJobs["Snyk-${currentSvc}"] = {
                            catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                                withCredentials([string(credentialsId: 'SNYK_TOKEN', variable: 'SNYK_TOKEN')]) {
                                    if (currentSvc in ["backoffice", "storefront"]) {
                                        sh """
                                            echo "=========================================================="
                                            echo "[SECURITY] STARTING SNYK VULNERABILITY SCAN (NPM): ${currentSvc}"
                                            echo "=========================================================="
                                            cd ${currentSvc}
                                            npx snyk test
                                        """
                                    } else {
                                        sh """
                                            echo "=========================================================="
                                            echo "[SECURITY] STARTING SNYK VULNERABILITY SCAN (MAVEN): ${currentSvc}"
                                            echo "=========================================================="
                                            chmod +x mvnw || true
                                            chmod +x ${currentSvc}/mvnw || true
                                            npx snyk test --file=${currentSvc}/pom.xml --command=mvn
                                        """
                                    }
                                }
                            }
                        }
                    }

                    // Execute security scans in parallel
                    parallel securityJobs
                }
            }
        }

        // =========================
        // BUILD PHASE
        // =========================
        stage('Build') {
            when {
                expression { env.CHANGED_SERVICES?.trim() }
            }
            steps {
                script {
                    echo " ===== START BUILD PHASE ===== "
                    echo "Changed services raw: ${env.CHANGED_SERVICES}"

                    def rawServices = env.CHANGED_SERVICES?.trim() 
                        ? env.CHANGED_SERVICES.split(',').collect { it.trim() } 
                        : []

                    def services = rawServices.unique()

                    echo "Services to build: ${services}"

                    def jobs = [:]

                    for (svc in services) {
                        def serviceName = svc

                        jobs[serviceName] = {
                            echo " START building ${serviceName}"

                            dir(serviceName) {
                                if (serviceName in ["backoffice", "storefront"]) {
                                    echo "Skipping local npm build for ${serviceName} because it is built inside the Dockerfile during the Push Docker Images stage."
                                } else {
                                    sh '''
                                        set -e
                                        echo "=== Building Java service: $(pwd) ==="
                                        java -version
                                        mvn clean package -DskipTests
                                    '''
                                }
                            }

                            echo " DONE building ${serviceName}"
                        }
                    }

                    parallel jobs
                }
            }
        }

        // =========================
        // PUSH DOCKER IMAGES PHASE
        // =========================
        stage('Push Docker Images') {
            when {
                expression { env.CHANGED_SERVICES?.trim() }
            }
            steps {
                script {
                    echo " ===== START DOCKER BUILD & PUSH PHASE ===== "
                    
                    // Determine current branch dynamically
                    def branchName = env.BRANCH_NAME ?: sh(
                        script: "git rev-parse --abbrev-ref HEAD",
                        returnStdout: true
                    ).trim()
                    echo "Current Branch: ${branchName}"

                    // Get the latest commit ID (short SHA)
                    def commitId = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()
                    
                    // Determine tag based on branch name
                    def imageTag = commitId
                    if (branchName == 'main' || branchName == 'master' || branchName == 'origin/main' || branchName == 'origin/master') {
                        imageTag = 'latest'
                    }
                    echo "Target image tag: ${imageTag}"

                    def rawServices = env.CHANGED_SERVICES?.trim() 
                        ? env.CHANGED_SERVICES.split(',').collect { it.trim() } 
                        : []
                    def services = rawServices.unique()

                    def dockerUsername = env.DOCKER_HUB_USERNAME ?: 'your_docker_hub_username'
                    def dockerCredsId = env.DOCKER_HUB_CREDS_ID ?: 'docker-hub-credentials'

                    if (dockerUsername == 'your_docker_hub_username' && fileExists('.env')) {
                        def lines = readFile('.env').split('\n')
                        for (line in lines) {
                            def trimmedLine = line.trim()
                            if (trimmedLine.startsWith('DOCKER_HUB_USERNAME')) {
                                def parts = trimmedLine.split('=', 2)
                                if (parts.size() == 2) dockerUsername = parts[1].trim()
                            }
                            if (trimmedLine.startsWith('DOCKER_HUB_CREDS_ID')) {
                                def parts = trimmedLine.split('=', 2)
                                if (parts.size() == 2) dockerCredsId = parts[1].trim()
                            }
                        }
                    }

                    withCredentials([usernamePassword(credentialsId: dockerCredsId, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"
                    }

                    def jobs = [:]

                    for (svc in services) {
                        def serviceName = svc
                        if (fileExists("${serviceName}/Dockerfile")) {
                            jobs[serviceName] = {
                                echo "=== Building and Pushing Multi-Arch Docker image for: ${serviceName} ==="
                                dir(serviceName) {
                                    // Khởi tạo và sử dụng builder hỗ trợ multi-platform
                                    sh "docker buildx create --use --name yas-builder || docker buildx use yas-builder"
                                    sh "docker buildx inspect --bootstrap"
                                    
                                    // Build cho cả 2 nền tảng và push trực tiếp lên Docker Hub
                                    sh "docker buildx build --platform linux/amd64,linux/arm64 -t ${dockerUsername}/yas-${serviceName}:${imageTag} --push ."
                                }
                            }
                        } else {
                            echo "Skipping ${serviceName} because no Dockerfile was found."
                        }
                    }

                    if (jobs.size() > 0) {
                        parallel jobs
                    } else {
                        echo "No containerized services to build/push."
                    }
                }
            }
            post {
                always {
                    sh "docker logout || true"
                }
            }
        }

        // =========================
        // UPDATE GITOPS REPO PHASE
        // =========================
        stage('Update GitOps') {
            when {
                expression { env.CHANGED_SERVICES?.trim() }
            }
            steps {
                script {
                    echo " ===== START GITOPS UPDATE PHASE ===== "
                    
                    // 1. Read configurations from .env
                    def dockerUsername = env.DOCKER_HUB_USERNAME ?: 'your_docker_hub_username'
                    def gitopsRepoUrl = env.GITOPS_REPO_URL ?: 'your_gitops_repo_url'
                    def gitopsCredsId = env.GITOPS_CREDS_ID ?: 'gitops-credentials'
                    def gitopsValuesFile = env.GITOPS_VALUES_FILE ?: 'dev/values.yaml'

                    if (fileExists('.env')) {
                        def lines = readFile('.env').split('\n')
                        for (line in lines) {
                            def trimmedLine = line.trim()
                            if (trimmedLine.startsWith('DOCKER_HUB_USERNAME') && dockerUsername == 'your_docker_hub_username') {
                                def parts = trimmedLine.split('=', 2)
                                if (parts.size() == 2) dockerUsername = parts[1].trim()
                            }
                            if (trimmedLine.startsWith('GITOPS_REPO_URL') && gitopsRepoUrl == 'your_gitops_repo_url') {
                                def parts = trimmedLine.split('=', 2)
                                if (parts.size() == 2) gitopsRepoUrl = parts[1].trim()
                            }
                            if (trimmedLine.startsWith('GITOPS_CREDS_ID') && gitopsCredsId == 'gitops-credentials') {
                                def parts = trimmedLine.split('=', 2)
                                if (parts.size() == 2) gitopsCredsId = parts[1].trim()
                            }
                            if (trimmedLine.startsWith('GITOPS_VALUES_FILE') && gitopsValuesFile == 'dev/values.yaml') {
                                def parts = trimmedLine.split('=', 2)
                                if (parts.size() == 2) gitopsValuesFile = parts[1].trim()
                            }
                        }
                    }

                    if (gitopsRepoUrl == 'your_gitops_repo_url' || !gitopsRepoUrl) {
                        echo "GitOps repository URL not configured. Skipping GitOps update."
                        return
                    }

                    // Get the latest commit ID (short SHA)
                    def commitId = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()
                    
                    // Determine tag based on branch name
                    def branchName = env.BRANCH_NAME ?: sh(
                        script: "git rev-parse --abbrev-ref HEAD",
                        returnStdout: true
                    ).trim()
                    def imageTag = commitId
                    if (branchName == 'main' || branchName == 'master' || branchName == 'origin/main' || branchName == 'origin/master') {
                        imageTag = 'latest'
                    }

                    def rawServices = env.CHANGED_SERVICES?.trim() 
                        ? env.CHANGED_SERVICES.split(',').collect { it.trim() } 
                        : []
                    def services = rawServices.unique()

                    // Filter services to only those that have a Dockerfile (meaning we actually built and pushed their image)
                    def builtServices = services.findAll { fileExists("${it}/Dockerfile") }
                    if (builtServices.isEmpty()) {
                        echo "No services built in this run. Skipping GitOps update."
                        return
                    }

                    echo "Updating GitOps repo for services: ${builtServices}"

                    // Download yq dynamically since it is not pre-installed on the agent
                    echo "Downloading yq binary..."
                    sh """
                        ARCH=\$(uname -m | sed 's/x86_64/amd64/' | sed 's/aarch64/arm64/')
                        curl -sL "https://github.com/mikefarah/yq/releases/download/v4.44.1/yq_linux_\${ARCH}" -o ./yq
                        chmod +x ./yq
                    """

                    // Clone the GitOps repository
                    sh "rm -rf gitops-repo"
                    
                    withCredentials([usernamePassword(credentialsId: gitopsCredsId, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                        def repoHostAndPath = gitopsRepoUrl.replace("https://", "")
                        sh "git clone https://\$GIT_USER:\$GIT_PASS@${repoHostAndPath} gitops-repo"
                    }

                    dir('gitops-repo') {
                        // Check if the values file exists in the cloned repository
                        if (!fileExists(gitopsValuesFile)) {
                            error "GitOps values file not found at: ${gitopsValuesFile}"
                        }

                        // Run yq to update the YAML configuration for each built service
                        for (svc in builtServices) {
                            if (svc == 'swagger') {
                                echo "Updating Swagger UI service [${svc}] (key: swagger-ui) in GitOps values..."
                                sh "../yq e '.swagger-ui.image.repository = \"${dockerUsername}/yas-${svc}\"' -i ${gitopsValuesFile}"
                                sh "../yq e '.swagger-ui.image.tag = \"${imageTag}\"' -i ${gitopsValuesFile}"
                            } else {
                                def isUi = (svc == 'backoffice' || svc == 'storefront')
                                def chartKey = svc
                                if (svc == 'backoffice') chartKey = 'backoffice-ui'
                                if (svc == 'storefront') chartKey = 'storefront-ui'
                                
                                if (isUi) {
                                    echo "Updating UI service [${svc}] (key: ${chartKey}) in GitOps values..."
                                    sh "../yq e '.${chartKey}.ui.image.repository = \"${dockerUsername}/yas-${svc}\"' -i ${gitopsValuesFile}"
                                    sh "../yq e '.${chartKey}.ui.image.tag = \"${imageTag}\"' -i ${gitopsValuesFile}"
                                } else {
                                    echo "Updating backend service [${svc}] (key: ${chartKey}) in GitOps values..."
                                    sh "../yq e '.${chartKey}.backend.image.repository = \"${dockerUsername}/yas-${svc}\"' -i ${gitopsValuesFile}"
                                    sh "../yq e '.${chartKey}.backend.image.tag = \"${imageTag}\"' -i ${gitopsValuesFile}"
                                }
                            }
                        }

                        // Check git diff to see if anything changed
                        def hasChanges = sh(script: "git diff --quiet || echo 'changes'", returnStdout: true).trim()
                        if (hasChanges == 'changes') {
                            echo "Changes detected in GitOps values. Committing and pushing..."
                            sh "git config user.email 'jenkins-ci@yourdomain.com'"
                            sh "git config user.name 'Jenkins CI'"
                            sh "git add ${gitopsValuesFile}"
                            sh "git commit -m 'ci: update service images to build ${imageTag} [skip ci]'"
                            
                            // Push back using authenticated URL
                            withCredentials([usernamePassword(credentialsId: gitopsCredsId, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                                def repoHostAndPath = gitopsRepoUrl.replace("https://", "")
                                sh "git push https://\$GIT_USER:\$GIT_PASS@${repoHostAndPath} HEAD:main || git push https://\$GIT_USER:\$GIT_PASS@${repoHostAndPath} HEAD:master"
                            }
                            echo "Successfully pushed changes to GitOps repository!"
                        } else {
                            echo "No changes in GitOps values file."
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                if (!env.CHANGED_SERVICES?.trim()) {
                    echo "No services changed → skip post actions"
                    return
                }

                junit allowEmptyResults: true,
                      testResults: '**/target/surefire-reports/*.xml'

                def jacocoFileExists = fileExists('**/target/site/jacoco/jacoco.xml')

                if (jacocoFileExists) {
                    publishCoverage adapters: [
                        jacocoAdapter('**/target/site/jacoco/jacoco.xml')
                    ]
                } else {
                    echo "No JaCoCo report found → skip coverage publishing"
                }
            }
        }
    }
}