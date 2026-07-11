FROM jenkins/jenkins:lts

USER root

# Install Docker CLI (static binary) so Jenkins can build/push images via host docker.sock
RUN curl -fsSL https://download.docker.com/linux/static/stable/x86_64/docker-27.4.1.tgz -o docker.tgz \
    && tar -xzf docker.tgz --strip-components=1 -C /usr/local/bin docker/docker \
    && rm docker.tgz \
    && docker --version

# Install Node.js and npm so frontend services can run npm test/build in pipeline.
RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates curl gnupg \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key \
        | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
    && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_20.x nodistro main" \
        > /etc/apt/sources.list.d/nodesource.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends nodejs \
    && node -v \
    && npm -v \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

USER jenkins

# Cài đặt plugin cấu hình JCasC và Job DSL
RUN jenkins-plugin-cli --plugins configuration-as-code job-dsl workflow-aggregator git

