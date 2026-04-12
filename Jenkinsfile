pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds()
    }

    environment {
        // Populated from the root pom.xml during initialization to avoid drift
        ALL_SERVICES = ""
    }

    stages {

        // ============ CHECKOUT ============
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // ============ INITIALIZE ============
        stage('Initialize') {
            steps {
                script {
                    env.GIT_COMMIT_MSG = sh(
                        script: "git log -1 --pretty=%B",
                        returnStdout: true
                    ).trim()

                    env.BUILD_TIMESTAMP = sh(
                        script: "date '+%Y%m%d_%H%M%S'",
                        returnStdout: true
                    ).trim()

                    def pom = new XmlSlurper().parseText(readFile('pom.xml'))
                    env.ALL_SERVICES = pom.modules.module.collect { it.text().trim() }
                        .findAll { it }
                        .join(' ')

                    echo """
╔════════════════════════════════════════╗
║  YAS Project - CI/CD Pipeline Started  ║
╚════════════════════════════════════════╝

Branch: ${env.BRANCH_NAME}
Commit: ${env.GIT_COMMIT}
Message: ${env.GIT_COMMIT_MSG}
Build: #${env.BUILD_NUMBER}
Time: ${env.BUILD_TIMESTAMP}
"""
                }
            }
        }

        // ============ DETECT CHANGES ============
        stage('Detect Changed Services') {
            steps {
                script {
                    sh '''
                        echo "Detecting changed services..."

                        SERVICES="cart customer delivery inventory location media order payment payment-paypal product promotion rating recommendation search sampledata tax backoffice-bff storefront-bff common-library"

                        if [ "$BRANCH_NAME" = "main" ]; then
                            echo "CHANGED_SERVICES=all" > build.properties
                        else
                            git fetch origin main || true

                            CHANGED_FILES="$(git diff --name-only origin/main...HEAD || true)"
                            CHANGED_SERVICES=""
                            NON_SERVICE_CHANGE="false"

                            for SERVICE in $SERVICES; do
                                if [ -d "$SERVICE" ] && [ -f "$SERVICE/pom.xml" ]; then
                                    if printf '%s\n' "$CHANGED_FILES" | grep -q "^$SERVICE/"; then
                                        CHANGED_SERVICES="$CHANGED_SERVICES $SERVICE"
                                    fi
                                fi
                            done

                            if [ -n "$CHANGED_FILES" ]; then
                                while IFS= read -r FILE; do
                                    [ -z "$FILE" ] && continue
                                    IS_SERVICE_FILE="false"

                                    for SERVICE in $SERVICES; do
                                        if printf '%s\n' "$FILE" | grep -q "^$SERVICE/"; then
                                            IS_SERVICE_FILE="true"
                                            break
                                        fi
                                    done

                                    if [ "$IS_SERVICE_FILE" = "false" ]; then
                                        NON_SERVICE_CHANGE="true"
                                        break
                                    fi
                                done <<EOF
$CHANGED_FILES
EOF
                            fi

                            if [ "$NON_SERVICE_CHANGE" = "true" ]; then
                                echo "CHANGED_SERVICES=all" > build.properties
                            elif [ -z "$CHANGED_SERVICES" ]; then
                                echo "CHANGED_SERVICES=none" > build.properties
                            else
                                echo "CHANGED_SERVICES=$CHANGED_SERVICES" > build.properties
                            fi
                        fi

                        cat build.properties
                    '''

                    def props = readProperties file: 'build.properties'
                    env.CHANGED_SERVICES = props.CHANGED_SERVICES

                    echo "Changed services: ${env.CHANGED_SERVICES}"
                }
            }
        }

        // ============ BUILD ============
        stage('Build Services') {
            when {
                expression { env.CHANGED_SERVICES != 'none' }
            }
            steps {
                script {
                    sh '''
                        echo "Building services..."

                        if [ "$CHANGED_SERVICES" = "all" ]; then
                            mvn clean package -DskipTests
                        else
                            for SERVICE in $CHANGED_SERVICES; do
                                if [ -f "$SERVICE/pom.xml" ]; then
                                    echo "Building $SERVICE"
                                    mvn -pl $SERVICE clean package -DskipTests
                                fi
                            done
                        fi
                    '''
                }
            }
        }

        // ============ TEST ============
        stage('Run Tests') {
            when {
                expression { env.CHANGED_SERVICES != 'none' }
            }
            steps {
                script {
                    sh '''
                        echo "Running tests..."

                        if [ "$CHANGED_SERVICES" = "all" ]; then
                            ./mvnw verify
                        else
                            for SERVICE in $CHANGED_SERVICES; do
                                if [ -f "$SERVICE/pom.xml" ]; then
                                    echo "Testing $SERVICE"
                                    ./mvnw -pl $SERVICE verify
                                fi
                            done
                        fi
                    '''
                }
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml'
                }
            }
        }

        // ============ COVERAGE CHECK ============
        stage('Check Coverage') {
            when {
                expression { env.CHANGED_SERVICES != 'none' }
            }
            steps {
                script {
                    echo "Checking aggregate coverage (threshold: 70%)..."
                    
                    // This logic parses target/site/jacoco/jacoco.xml after Maven verify
                    // In a monorepo, we'll aggregate or check service-by-service
                    sh '''
                        #!/bin/bash
                        # Simple script to aggregate coverage from all changed services
                        TOTAL_COVERED=0
                        TOTAL_MISSED=0
                        
                        SERVICES_TO_CHECK="$CHANGED_SERVICES"
                        if [ "$CHANGED_SERVICES" == "all" ]; then
                            SERVICES_TO_CHECK="$ALL_SERVICES"
                        fi

                        for SERVICE in $SERVICES_TO_CHECK; do
                            REPORT="$SERVICE/target/site/jacoco/jacoco.xml"
                            if [ -f "$REPORT" ]; then
                                # Extract metrics using grep/sed for speed without specialized XML parsers
                                COVERED=$(grep -oP '(?<=<counter type="LINE" missed=")[0-9]+(?=" covered=")[0-9]+' "$REPORT" | awk -F'covered="' '{print $2}' | awk '{s+=$1} END {print s}')
                                MISSED=$(grep -oP '(?<=<counter type="LINE" missed=")[0-9]+(?=" covered=")[0-9]+' "$REPORT" | awk -F'missed="' '{print $2}' | awk '{s+=$1} END {print s}')
                                
                                # Fallback if regex fails (simplified extraction using cut)
                                if [ -z "$COVERED" ]; then
                                    COVERED=$(grep 'counter type="LINE"' "$REPORT" | head -1 | cut -d' ' -f5 | cut -d'"' -f2)
                                    MISSED=$(grep 'counter type="LINE"' "$REPORT" | head -1 | cut -d' ' -f4 | cut -d'"' -f2)
                                fi

                                TOTAL_COVERED=$((TOTAL_COVERED + COVERED))
                                TOTAL_MISSED=$((TOTAL_MISSED + MISSED))
                                echo "Service $SERVICE: $COVERED covered, $MISSED missed"
                            fi
                        done

                        if [ $((TOTAL_COVERED + TOTAL_MISSED)) -eq 0 ]; then
                            echo "No coverage data found! Skipping check."
                            exit 0
                        fi

                        # Calculate percentage
                        TOTAL=$((TOTAL_COVERED + TOTAL_MISSED))
                        PERCENTAGE=$((TOTAL_COVERED * 100 / TOTAL))
                        
                        echo "════════════════════════════════════"
                        echo "Final Aggregate Coverage: $PERCENTAGE%"
                        echo "════════════════════════════════════"

                        if [ $PERCENTAGE -lt 70 ]; then
                            echo "FAILED: Coverage below 70% threshold!"
                            exit 1
                        fi
                        echo "SUCCESS: Coverage above threshold."
                    '''
                }
            }
        }

        // ============ SONAR (SIMPLIFIED) ============
        stage('SonarQube Scan') {
            steps {
                echo "SonarQube scan placeholder (configure later if needed)"
            }
        }

        // ============ REPORT ============
        stage('Publish Reports') {
            when {
                expression { env.CHANGED_SERVICES != 'none' }
            }
            steps {
                publishHTML([
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'Coverage Report',
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true
                ])

                archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            cleanWs()
        }

        success {
            echo "Pipeline SUCCESS"
        }

        unstable {
            echo "Pipeline UNSTABLE"
        }

        failure {
            echo "Pipeline FAILED"
        }
    }
}