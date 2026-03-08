// Jenkins pipeline for YAS project
// See docs/JENKINS_SETUP.md for full configuration (Docker, JUnit, SonarQube/SonarCloud, Gitleaks, Snyk)

pipeline {
  agent any

  tools {
    maven 'Maven-3.9'
    jdk   'JDK-21'
  }

  environment {
    SONAR_TOKEN = credentials('sonarcloud-token')
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Gitleaks') {
      steps {
        sh '''
          if command -v gitleaks >/dev/null 2>&1; then
            gitleaks detect --config gitleaks.toml --report-path gitleaks-report.json --verbose || true
          else
            echo "Gitleaks not installed; skipping. See docs/JENKINS_SETUP.md"
          fi
        '''
      }
    }

    stage('Build & Test') {
      steps {
        sh 'mvn clean verify -DskipTests=false'
      }
    }

    stage('Publish JUnit Results') {
      steps {
        junit allowEmptyResults: true, testResults: '**/*-reports/TEST-*.xml'
      }
    }

    stage('SonarCloud') {
      steps {
        withSonarQubeEnv('SonarCloud') {
          sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.token=${SONAR_TOKEN}'
        }
      }
    }

    stage('Snyk') {
      when {
        expression { return env.SNYK_ENABLED == 'true' }
      }
      steps {
        withCredentials([string(credentialsId: 'snyk-token', variable: 'SNYK_TOKEN')]) {
          sh 'snyk test --file=pom.xml || true'
        }
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: '**/*-reports/TEST-*.xml'
      archiveArtifacts artifacts: '**/target/site/jacoco/jacoco.xml', allowEmptyArchive: true
    }
    cleanup {
      cleanWs()
    }
  }
}
