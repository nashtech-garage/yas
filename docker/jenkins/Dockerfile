FROM jenkins/jenkins:lts

USER root

RUN apt-get update && \
    apt-get install -y \
      git \
      curl \
      unzip \
      ca-certificates \
      gnupg && \
    rm -rf /var/lib/apt/lists/*

# Gitleaks architecture-aware
RUN ARCH=$(dpkg --print-architecture) && \
    GITLEAKS_ARCH=$([ "$ARCH" = "arm64" ] && echo arm64 || echo x64) && \
    curl -sL "https://github.com/gitleaks/gitleaks/releases/download/v8.30.0/gitleaks_8.30.0_linux_${GITLEAKS_ARCH}.tar.gz" -o /tmp/gitleaks.tar.gz && \
    tar -xzf /tmp/gitleaks.tar.gz -C /tmp && \
    mv /tmp/gitleaks /usr/local/bin/gitleaks && \
    chmod +x /usr/local/bin/gitleaks && \
    rm -f /tmp/gitleaks.tar.gz

# Snyk CLI — architecture-aware
RUN ARCH=$(dpkg --print-architecture) && \
    SNYK_BIN=$([ "$ARCH" = "arm64" ] && echo snyk-linux-arm64 || echo snyk-linux) && \
    curl -sL --compressed "https://downloads.snyk.io/cli/stable/${SNYK_BIN}" -o /usr/local/bin/snyk && \
    chmod +x /usr/local/bin/snyk
