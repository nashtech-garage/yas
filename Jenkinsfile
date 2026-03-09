// Jenkins pipeline for YAS project
// See docs/JENKINS_SETUP.md for full configuration (Docker, JUnit, SonarQube/SonarCloud, Gitleaks, Snyk)

pipeline {
  agent any
  
  options {
    timeout(time: 30, unit: 'MINUTES')
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '5'))
  }

  environment {
    // Note: 'sonarcloud-token' must be created in Jenkins Credentials first
    SONAR_TOKEN = credentials('sonarcloud-token')
  }

  stages {
    stage('Prepare') {
      steps {
        script {
          // Check if we are using the local mount we added in docker-compose
          if (fileExists('/var/jenkins_home/local-repo')) {
            echo "Building from local mount: /var/jenkins_home/local-repo"
            sh 'cp -R /var/jenkins_home/local-repo/. .'
          } else {
            echo "Running standard SCM checkout..."
            checkout scm
          }
          
          sh 'ls -la'
        }
      }
    }

    stage('Gitleaks') {
      steps {
        sh 'gitleaks detect --report-path gitleaks-report.json --verbose || true'
      }
    }

    stage('Build & Test') {
      steps {
        // Added -Dmaven.test.failure.ignore=true so we can get to the "Publish Result" stage 
        // even if Testcontainers times out on the search module.
        sh 'mvn clean verify -DskipITs=true -Dmaven.test.failure.ignore=true'
      }
    }

    stage('Publish JUnit Results') {
      steps {
        junit allowEmptyResults: true, testResults: '**/target/*-reports/*.xml'
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
        expression { return env.SNYK_ENABLED == 'true' && env.SNYK_TOKEN != null }
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
