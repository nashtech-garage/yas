/**
 * Charts CI — loaded and executed by the master Jenkinsfile.
 *
 * Runs chart-releaser (cr) to package, upload GitHub releases, and update the
 * gh-pages Helm index.  Only executes on the 'main' branch.
 *
 * Requires Jenkins credentials:
 *   github-token  — GitHub personal access token with repo scope
 */

def call() { 
    def chartsDir      = 'k8s/charts'
    def owner          = 'danzgne'
    def repo           = 'devops'
    def crPackagePath  = '.cr-release-packages'
    def crIndexPath    = '.cr-index'

    // Guard: chart releases only happen on main
    if (env.BRANCH_NAME != 'main') {
        echo "Charts: skipping release (branch '${env.BRANCH_NAME}' is not 'main')"
        return
    }
    try {
        stage('charts: Checkout') {
            // Full-depth checkout with tags so chart-releaser can read history
            checkout([
                $class           : 'GitSCM',
                branches         : scm.branches,
                userRemoteConfigs: scm.userRemoteConfigs,
                extensions       : [[$class: 'CloneOption', depth: 0, noTags: false]]
            ])
        }

        stage('charts: Configure Git') {
            sh '''
                git config user.email "jenkins@yas.local"
                git config user.name "Jenkins"
            '''
        }

        stage('charts: Install Tools') {
            sh """
                # Install Helm into workspace .tools directory
                mkdir -p "\$WORKSPACE/.tools"
                HELM_VERSION="v3.20.1"
                curl -sSLo helm.tar.gz "https://get.helm.sh/helm-\${HELM_VERSION}-linux-amd64.tar.gz"
                tar -xzf helm.tar.gz
                mv linux-amd64/helm "\$WORKSPACE/.tools/helm"
                chmod +x "\$WORKSPACE/.tools/helm"
                rm -rf helm.tar.gz linux-amd64

                # Install chart-releaser (cr) into workspace .tools directory
                CR_VERSION="1.6.1"
                curl -sSLo cr.tar.gz \\
                  "https://github.com/helm/chart-releaser/releases/download/v\${CR_VERSION}/chart-releaser_\${CR_VERSION}_linux_amd64.tar.gz"
                tar -xzf cr.tar.gz -C "\$WORKSPACE/.tools" cr
                chmod +x "\$WORKSPACE/.tools/cr"
                rm -f cr.tar.gz
            """
        }

        stage('charts: Add Helm Repo') {
            sh """
                export PATH="\$WORKSPACE/.tools:\$PATH"
                helm repo add stakater https://stakater.github.io/stakater-charts
                helm repo update
            """
        }

        stage('charts: Package Charts') {
            sh """
                export PATH="\$WORKSPACE/.tools:\$PATH"
                mkdir -p ${crPackagePath}
                for chart in k8s/charts/*/; do
                    if [ -f "\${chart}Chart.yaml" ]; then
                        cr package "\$chart" --package-path ${crPackagePath}
                    fi
                done
            """
        }

        stage('charts: Upload Releases') {
            withCredentials([string(credentialsId: 'github-token', variable: 'CR_TOKEN')]) {
                sh """
                    export PATH="\$WORKSPACE/.tools:\$PATH"
                    cr upload \\
                      --owner ${owner} \\
                      --git-repo ${repo} \\
                      --package-path ${crPackagePath} \\
                      --token \$CR_TOKEN \\
                      --skip-existing
                """
            }
        }

        stage('charts: Update Index') {
            withCredentials([string(credentialsId: 'github-token', variable: 'CR_TOKEN')]) {
                sh """
                    export PATH="\$WORKSPACE/.tools:\$PATH"
                    git fetch origin +refs/heads/gh-pages:refs/remotes/origin/gh-pages
                    git show-ref --verify --quiet refs/remotes/origin/gh-pages
                    mkdir -p ${crIndexPath}
                    cr index \\
                      --owner ${owner} \\
                      --git-repo ${repo} \\
                      --package-path ${crPackagePath} \\
                      --index-path ${crIndexPath} \\
                      --pages-branch gh-pages \\
                      --token \$CR_TOKEN \\
                      --push
                """
            }
        }

        echo 'Charts released successfully'
    } catch (Exception e) {
        echo 'Chart release failed'
        throw e
    }
}

return this
