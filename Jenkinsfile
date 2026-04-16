def runCapture(String cmd) {
  if (isUnix()) {
    return sh(script: cmd, returnStdout: true).trim()
  }
  // On Windows agents, `bat(returnStdout: true)` returns with CRLF; normalize later.
  return bat(script: cmd, returnStdout: true).trim()
}

def computeChangedFiles() {
  def cmd = null

  if (env.CHANGE_TARGET) {
    // PR build (Multibranch): diff against merge-base with target branch
    cmd = "git diff --name-only origin/${env.CHANGE_TARGET}...HEAD"
  } else if (env.GIT_PREVIOUS_SUCCESSFUL_COMMIT && env.GIT_COMMIT) {
    cmd = "git diff --name-only ${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT}..${env.GIT_COMMIT}"
  } else if (env.GIT_PREVIOUS_COMMIT && env.GIT_COMMIT) {
    cmd = "git diff --name-only ${env.GIT_PREVIOUS_COMMIT}..${env.GIT_COMMIT}"
  } else {
    // Fallback: only last commit
    cmd = 'git show --name-only --pretty="" HEAD'
  }

  try {
    def out = runCapture(cmd)
    return out
      .split(/\r?\n/)
      .collect { it.trim() }
      .findAll { it }
  } catch (err) {
    def out = runCapture('git show --name-only --pretty="" HEAD')
    return out
      .split(/\r?\n/)
      .collect { it.trim() }
      .findAll { it }
  }
}

def readMavenModulesFromRootPom() {
  def pom = readFile('pom.xml')
  def matcher = (pom =~ /<module>([^<]+)<\/module>/)
  def modules = []
  matcher.each { m -> modules << m[1].trim() }
  return modules.unique()
}

pipeline {
  agent any

  tools {
    maven 'maven3' // Name of Maven installation configured in Jenkins global tools
  }

  // For branch-by-branch execution, create a Multibranch Pipeline job in Jenkins.
  // This Jenkinsfile is designed to run correctly in Multibranch (BRANCH_NAME/CHANGE_TARGET).
  options {
    timestamps()
    disableConcurrentBuilds()
    skipDefaultCheckout(true)
    buildDiscarder(logRotator(numToKeepStr: '30'))
  }

  // Fallback trigger if webhooks/branch indexing isn't configured.
  triggers {
    pollSCM('H/15 * * * *')
  }

  environment {
    MVN_ARGS = '-B -ntp'
    AFFECTED_MODULES = ''
    MVN_MAKE_FLAGS = '-am'
  }

  stages {
    stage('Checkout code') {
      steps {
        checkout scm
        script {
          // Ensure remote refs exist for diff calculations (PR builds, etc.)
          if (isUnix()) {
            sh 'git fetch --no-tags --prune origin +refs/heads/*:refs/remotes/origin/*'
          } else {
            bat 'git fetch --no-tags --prune origin +refs/heads/*:refs/remotes/origin/*'
          }
        }
      }
    }

    stage('Detect changed services (monorepo)') {
      steps {
        script {
          def allModules = readMavenModulesFromRootPom()
          def changedFiles = computeChangedFiles()

          // Root-level changes that should rebuild everything
          def rebuildAll = changedFiles.any { f ->
            f == 'pom.xml' ||
            f == 'Jenkinsfile' ||
            f.startsWith('checkstyle/')
          }

          def touchedTopDirs = changedFiles
            .findAll { it.contains('/') }
            .collect { it.tokenize('/')[0] }
            .unique()

          def affected = touchedTopDirs.findAll { d -> allModules.contains(d) }

          if (rebuildAll) {
            affected = allModules
          }

          // If common-library changes, rebuild dependents too.
          if (affected.contains('common-library')) {
            env.MVN_MAKE_FLAGS = '-am -amd'
          }

          env.AFFECTED_MODULES = affected.join(',')

          if (env.AFFECTED_MODULES?.trim()) {
            currentBuild.description = "${env.BRANCH_NAME ?: ''} | modules: ${env.AFFECTED_MODULES}"
            echo "Changed files:\n${changedFiles.join('\n')}"
            echo "Affected Maven modules: ${env.AFFECTED_MODULES}"
          } else {
            currentBuild.description = "${env.BRANCH_NAME ?: ''} | no service changes"
            echo "Changed files:\n${changedFiles.join('\n')}"
            echo 'No Maven service module changed; Test/Build stages will run as no-ops.'
          }
        }
      }
    }

    stage('Install dependencies') {
      steps {
        script {
          if (env.AFFECTED_MODULES?.trim()) {
            def mods = env.AFFECTED_MODULES
            if (isUnix()) {
              sh "mvn ${env.MVN_ARGS} -pl ${mods} ${env.MVN_MAKE_FLAGS} -DskipTests dependency:go-offline"
            } else {
              bat "mvn ${env.MVN_ARGS} -pl ${mods} ${env.MVN_MAKE_FLAGS} -DskipTests dependency:go-offline"
            }
          } else {
            echo 'No affected module detected; downloading dependencies for full reactor.'
            if (isUnix()) {
              sh "mvn ${env.MVN_ARGS} -DskipTests dependency:go-offline"
            } else {
              bat "mvn ${env.MVN_ARGS} -DskipTests dependency:go-offline"
            }
          }
        }
      }
    }

    stage('Test (upload results + coverage)') {
      steps {
        script {
          if (env.AFFECTED_MODULES?.trim()) {
            def mods = env.AFFECTED_MODULES
            if (isUnix()) {
              sh "mvn ${env.MVN_ARGS} -pl ${mods} ${env.MVN_MAKE_FLAGS} verify"
            } else {
              bat "mvn ${env.MVN_ARGS} -pl ${mods} ${env.MVN_MAKE_FLAGS} verify"
            }
          } else {
            echo 'No affected module detected; skipping tests (still running stage).'
          }
        }
      }
    }

    stage('Build') {
      steps {
        script {
          if (env.AFFECTED_MODULES?.trim()) {
            def mods = env.AFFECTED_MODULES
            if (isUnix()) {
              sh "mvn ${env.MVN_ARGS} -pl ${mods} ${env.MVN_MAKE_FLAGS} -DskipTests package"
            } else {
              bat "mvn ${env.MVN_ARGS} -pl ${mods} ${env.MVN_MAKE_FLAGS} -DskipTests package"
            }
          } else {
            echo 'No affected module detected; skipping build (still running stage).'
          }
        }
      }
    }
  }

  post {
    always {
      // Upload JUnit test results
      junit allowEmptyResults: true,
            testResults: '**/target/surefire-reports/*.xml,**/target/failsafe-reports/*.xml'

      // Upload coverage artifacts produced by jacoco-maven-plugin (configured in root pom.xml)
      archiveArtifacts allowEmptyArchive: true,
                       artifacts: '**/target/site/jacoco/**,**/target/jacoco.exec'

      // Keep build artifacts if any were produced
      archiveArtifacts allowEmptyArchive: true,
                       artifacts: '**/target/*.jar,**/target/*.war'
    }

    success {
      echo 'Pipeline succeeded.'
    }

    failure {
      echo 'Pipeline failed.'
    }

    unstable {
      echo 'Pipeline unstable (test failures or quality gates).'
    }
  }
}
