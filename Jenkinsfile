// ============================================================
//  YAS Monorepo – Jenkins CI Pipeline (Merged)
//  Controller (AWS): orchestration + UI + logs.
//  Agents (máy nhóm): build/test/scan — label: yas-build-worker
//  SonarQube: http://3.27.92.213:9000
//  JDK/Maven tools: JDK-25, Maven (Global Tool Configuration)
// ============================================================
//
//  Pipeline flow:
//  1. Checkout + fetch origin/main (fix shallow clone)
//  2. Detect Changed Modules (shell script, so sánh với origin/main)
//  3. Gitleaks – Secret Scan (luôn chạy, fail nếu phát hiện secret)
//  4. Test + JaCoCo report (chỉ modules thay đổi, skip Integration Tests)
//  5. Coverage Gate (≥ 70%, graceful skip nếu không có code)
//  6. Build (chỉ modules thay đổi)
//  7. SonarQube Analysis (chỉ modules thay đổi)
//  8. Snyk Dependency Scan (chỉ modules thay đổi)
// ============================================================

pipeline {
    agent {
        label 'yas-build-worker'
    }

    // JDK/Maven: resolve once via tool step (avoids repeating "Tool Installation" every stage).
    // Names must match Jenkins → Manage Jenkins → Tools (JDK-25, Maven).

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 60, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    environment {
        COVERAGE_THRESHOLD   = '70'
        SONAR_HOST_URL       = 'http://3.27.92.213:9000'
        MAVEN_OPTS           = '-Xmx512m -XX:MaxMetaspaceSize=256m'
        GITLEAKS_EXPECTED    = '8.18.4'
    }

    stages {

        // ══════════════════════════════════════════════════════
        // STAGE 1 – Checkout & Prepare
        // ══════════════════════════════════════════════════════
        stage('Checkout') {
            steps {
                script {
                    def jdkHome = tool name: 'JDK-25', type: 'jdk'
                    env.JAVA_HOME = jdkHome
                    env.PATH = "${jdkHome}/bin:${env.PATH}"
                    def mvnHome = tool name: 'Maven', type: 'maven'
                    env.M2_HOME = mvnHome
                    env.PATH = "${mvnHome}/bin:${env.PATH}"
                }
                checkout scm
                sh 'git fetch origin main:refs/remotes/origin/main || true'
                sh 'chmod +x ci/detect-changed-modules.sh ci/check-coverage.sh ci/verify-ci-tools.sh'
                sh 'ci/verify-ci-tools.sh'
            }
        }

        // ══════════════════════════════════════════════════════
        // STAGE 2 – Detect Changed Modules (monorepo path filter)
        // So sánh với origin/main để lấy đúng danh sách thay đổi
        // ══════════════════════════════════════════════════════
        stage('Detect Changed Modules') {
            steps {
                script {
                    env.CHANGED_MODULES = sh(
                        script: 'ci/detect-changed-modules.sh',
                        returnStdout: true
                    ).trim()
                    echo "CHANGED_MODULES=${env.CHANGED_MODULES}"
                }
            }
        }

        // ══════════════════════════════════════════════════════
        // STAGE 3 – Gitleaks: Secret Scanning
        // Chạy TRƯỚC test/build – phát hiện secret → FAIL ngay
        // ══════════════════════════════════════════════════════
        stage('Gitleaks – Secret Scan') {
            steps {
                sh '''
                    if [ -f gitleaks-baseline.json ]; then
                        gitleaks detect --source=. --no-git --config=gitleaks.toml --baseline-path=gitleaks-baseline.json --report-path=gitleaks-report.json --exit-code=1
                    else
                        gitleaks detect --source=. --no-git --config=gitleaks.toml --report-path=gitleaks-report.json --exit-code=1
                    fi
                    echo "Gitleaks: OK"
                '''
            }
        }

        // ══════════════════════════════════════════════════════
        // STAGE 4 – Test (JUnit + JaCoCo)
        // Chỉ test modules thay đổi, skip Integration Tests
        // ══════════════════════════════════════════════════════
        stage('Test') {
            steps {
                script {
                    def modules = env.CHANGED_MODULES.split(',')
                    for (module in modules) {
                        sh "mvn -B -ntp -pl ${module} -am test jacoco:report -DskipITs"
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: '**/target/surefire-reports/*.xml,**/target/failsafe-reports/*.xml'
                    archiveArtifacts allowEmptyArchive: true,
                                     artifacts: '**/target/site/jacoco/jacoco.xml'
                }
            }
        }

        // ══════════════════════════════════════════════════════
        // STAGE 5 – Coverage Gate (≥ 70%)
        // Graceful skip nếu module không có executable code
        // ══════════════════════════════════════════════════════
        stage('Coverage Gate') {
            steps {
                script {
                    def modules = env.CHANGED_MODULES.split(',')
                    def serviceModules = modules.findAll { it != 'common-library' }

                    if (serviceModules.isEmpty()) {
                        echo "Coverage gate: skip"
                    } else {
                        for (module in serviceModules) {
                            sh "ci/check-coverage.sh ${module} ${env.COVERAGE_THRESHOLD}"
                        }
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        // STAGE 6 – Build
        // Chỉ build modules thay đổi, skip tests (đã chạy ở trên)
        // ══════════════════════════════════════════════════════
        stage('Build') {
            steps {
                script {
                    def modules = env.CHANGED_MODULES.split(',')
                    for (module in modules) {
                        sh "mvn -B -ntp -DskipTests -pl ${module} -am package"
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        // STAGE 7 – SonarQube Analysis
        // Override sonar.host.url → AWS server (không dùng SonarCloud)
        // ══════════════════════════════════════════════════════
        stage('SonarQube – Analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                    script {
                        def modules = env.CHANGED_MODULES.split(',')
                        def serviceModules = modules.findAll { it != 'common-library' }

                        if (serviceModules.isEmpty()) {
                            echo "SonarQube: skip"
                        } else {
                            def plArg = serviceModules.join(',')
                            echo "SonarQube: ${plArg}"
                            sh """
                                mvn sonar:sonar \
                                  -pl ${plArg} -am \
                                  -Dsonar.host.url=${SONAR_HOST_URL} \
                                  -Dsonar.token=${SONAR_TOKEN} \
                                  -Dsonar.organization= \
                                  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                            """
                        }
                    }
                }
            }
        }

        // ══════════════════════════════════════════════════════
        // STAGE 8 – Snyk: Dependency Vulnerability Scan
        // Chỉ scan modules thay đổi
        // ══════════════════════════════════════════════════════
        stage('Snyk – Dependency Scan') {
            steps {
                withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
                    script {
                        def modules = env.CHANGED_MODULES.split(',')
                        def serviceModules = modules.findAll { it != 'common-library' }

                        if (serviceModules.isEmpty()) {
                            echo "Snyk: skip"
                        } else {
                            // Snyk CLI is Node-based; Maven resolver children need RAM — exit -13 is often OOM.
                            // Scan light → full reactor → capped depth (avoid starting with --all-sub-projects only).
                            withEnv([
                                'NODE_OPTIONS=--max-old-space-size=6144',
                                'MAVEN_OPTS=-Xmx1536m -XX:MaxMetaspaceSize=384m'
                            ]) {
                                sh "snyk auth \$SNYK_TOKEN"
                                for (mod in serviceModules) {
                                    sh "snyk test --file=${mod}/pom.xml --severity-threshold=high || snyk test --file=${mod}/pom.xml --severity-threshold=high --all-sub-projects || snyk test --file=${mod}/pom.xml --severity-threshold=high --all-sub-projects --max-depth=3"
                                }
                                for (mod in serviceModules) {
                                    sh "snyk monitor --file=${mod}/pom.xml --project-name=yas-${mod} || snyk monitor --file=${mod}/pom.xml --project-name=yas-${mod} --all-sub-projects || snyk monitor --file=${mod}/pom.xml --project-name=yas-${mod} --all-sub-projects --max-depth=3"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ─── Post-build actions ───────────────────────────────────
    post {
        always {
            cleanWs()
        }
        success {
            echo "PASS ${env.BRANCH_NAME} #${env.BUILD_NUMBER}"
        }
        failure {
            echo "FAIL ${env.BRANCH_NAME} #${env.BUILD_NUMBER}"
        }
    }
}
