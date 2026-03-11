# jenkins/Dockerfile
FROM jenkins/jenkins:lts

USER root

# Install dependencies
RUN apt-get update && apt-get install -y curl git jq unzip maven

# 1. Install Gitleaks (Architecture aware)
RUN ARCH=$(dpkg --print-architecture) && \
    if [ "$ARCH" = "arm64" ]; then GITLEAKS_ARCH="arm64"; else GITLEAKS_ARCH="x64"; fi && \
    GITLEAKS_VERSION=$(curl -s https://api.github.com/repos/gitleaks/gitleaks/releases/latest | jq -r .tag_name) && \
    curl -L "https://github.com/gitleaks/gitleaks/releases/download/${GITLEAKS_VERSION}/gitleaks_${GITLEAKS_VERSION#v}_linux_${GITLEAKS_ARCH}.tar.gz" -o gitleaks.tar.gz && \
    tar -xzf gitleaks.tar.gz && \
    mv gitleaks /usr/local/bin/gitleaks && \
    rm gitleaks.tar.gz

# 2. Install Snyk CLI (Architecture aware)
RUN ARCH=$(dpkg --print-architecture) && \
    if [ "$ARCH" = "arm64" ]; then SNYK_BINARY="snyk-linux-arm64"; else SNYK_BINARY="snyk-linux"; fi && \
    curl -L "https://static.snyk.io/cli/latest/${SNYK_BINARY}" -o snyk && \
    chmod +x snyk && \
    mv snyk /usr/local/bin/snyk

# 3. Install SonarScanner CLI (Architecture aware)
RUN ARCH=$(dpkg --print-architecture) && \
    if [ "$ARCH" = "arm64" ]; then \
        SONAR_ZIP="sonar-scanner-cli-5.0.1.3006.zip" && SONAR_DIR="sonar-scanner-5.0.1.3006"; \
    else \
        SONAR_ZIP="sonar-scanner-cli-5.0.1.3006-linux.zip" && SONAR_DIR="sonar-scanner-5.0.1.3006-linux"; \
    fi && \
    curl -L "https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/${SONAR_ZIP}" -o scanner.zip && \
    unzip scanner.zip && \
    mv "${SONAR_DIR}" /opt/sonar-scanner && \
    ln -s /opt/sonar-scanner/bin/sonar-scanner /usr/local/bin/sonar-scanner && \
    rm scanner.zip

# 4. Install Docker CLI (so Testcontainers can talk to the Docker socket)
RUN apt-get install -y docker.io

# 5. Add jenkins user to the docker group so it can use /var/run/docker.sock
RUN groupadd -f docker && usermod -aG docker jenkins

# Entrypoint script: Fix the docker socket group at runtime, then start Jenkins
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

USER jenkins

ENTRYPOINT ["/entrypoint.sh"]