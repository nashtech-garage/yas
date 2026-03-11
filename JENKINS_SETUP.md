# Jenkins + SonarQube Local Setup Guide

A simple local CI stack: **Jenkins** (with Gitleaks + Snyk + SonarScanner baked in) + **SonarQube** + **PostgreSQL**, all running via Docker Compose.

---

## 1. Prerequisites

| Requirement    | Version     | Notes                                                      |
| -------------- | ----------- | ---------------------------------------------------------- |
| Docker Desktop | ≥ 24        | [docker.com/get-docker](https://www.docker.com/get-docker) |
| Docker Compose | ≥ v2        | Bundled with Docker Desktop                                |
| Free ports     | 8080, 9000  | Jenkins and SonarQube UIs                                  |
| RAM            | ≥ 4 GB free | SonarQube is memory-hungry                                 |

**Linux only** — increase the virtual memory limit required by SonarQube's embedded Elasticsearch:

```bash
# Apply temporarily (resets on reboot)
sudo sysctl -w vm.max_map_count=262144

# Apply permanently
echo "vm.max_map_count=262144" | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
```

macOS / Windows Docker Desktop already applies this inside the Linux VM automatically.

---

## 2. Run Docker

From the **project root**:

```bash
# Build the Jenkins image and start all services
docker compose -f docker-compose.jenkins.yml up -d --build

# Tail logs to watch startup
docker compose -f docker-compose.jenkins.yml logs -f
```

Wait ~60 seconds for both services to become healthy, then open:

| Service   | URL                   | Default credentials     |
| --------- | --------------------- | ----------------------- |
| Jenkins   | http://localhost:8080 | (see unlock step below) |
| SonarQube | http://localhost:9000 | admin / admin           |

**Unlock Jenkins (first run only)**

```bash
# Print the initial admin password
docker exec jenkins_local cat /var/jenkins_home/secrets/initialAdminPassword
```

Paste the output into the Jenkins UI when prompted.

---

## 3. Dockerfile Explanation

`docker/jenkins/Dockerfile`

```dockerfile
FROM jenkins/jenkins:lts          # Official LTS image — stable base

USER root                         # Need root to install system packages

# Base tools
RUN apt-get update && apt-get install -y curl git jq unzip

# 1. Gitleaks — architecture aware
RUN ARCH=$(dpkg --print-architecture) && \
    if [ "$ARCH" = "arm64" ]; then GITLEAKS_ARCH="arm64"; else GITLEAKS_ARCH="x64"; fi && \
    GITLEAKS_VERSION=$(curl -s https://api.github.com/repos/gitleaks/gitleaks/releases/latest | jq -r .tag_name) && \
    curl -L "https://github.com/gitleaks/gitleaks/releases/download/${GITLEAKS_VERSION}/gitleaks_${GITLEAKS_VERSION#v}_linux_${GITLEAKS_ARCH}.tar.gz" -o gitleaks.tar.gz && \
    tar -xzf gitleaks.tar.gz && \
    mv gitleaks /usr/local/bin/gitleaks && \
    rm gitleaks.tar.gz

# 2. Snyk CLI — architecture aware
RUN ARCH=$(dpkg --print-architecture) && \
    if [ "$ARCH" = "arm64" ]; then SNYK_BINARY="snyk-linux-arm64"; else SNYK_BINARY="snyk-linux"; fi && \
    curl -L "https://static.snyk.io/cli/latest/${SNYK_BINARY}" -o snyk && \
    chmod +x snyk && \
    mv snyk /usr/local/bin/snyk

# 3. SonarScanner CLI — architecture aware
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

USER jenkins                      # Drop back to the unprivileged jenkins user
```

**Key decisions:**

- All three security tools are baked into the image so pipelines have zero extra install time.
- `USER jenkins` at the end follows the principle of least privilege.

---

## 4. Docker Compose Explanation

`docker-compose.jenkins.yml`

```yaml
services:
  jenkins:
    build: ./docker/jenkins # Uses our custom Dockerfile above
    ports:
      - "8080:8080" # Web UI
      - "50000:50000" # Jenkins agent JNLP port
    volumes:
      - jenkins_data:/var/jenkins_home # Persist all Jenkins config/jobs
      - /var/run/docker.sock:/var/run/docker.sock # Let Jenkins run Docker commands
    networks:
      - devops_net

  sonarqube:
    image: sonarqube:community # Free community edition
    ports:
      - "9000:9000"
    environment:
      - SONAR_JDBC_URL=jdbc:postgresql://sonar_db:5432/sonar
      - SONAR_JDBC_USERNAME=sonar
      - SONAR_JDBC_PASSWORD=sonar
    volumes:
      # Persist SonarQube state across restarts
      - sonarqube_data:/opt/sonarqube/data
      - sonarqube_extensions:/opt/sonarqube/extensions
      - sonarqube_logs:/opt/sonarqube/logs
    depends_on:
      - sonar_db
    networks:
      - devops_net

  sonar_db:
    image: postgres:15 # SonarQube requires an external DB (not H2) for production-like use
    environment:
      - POSTGRES_USER=sonar
      - POSTGRES_PASSWORD=sonar
      - POSTGRES_DB=sonar
    volumes:
      - sonar_db_data:/var/lib/postgresql/data
    networks:
      - devops_net

networks:
  devops_net: # Shared bridge so all containers can reach each other by service name

volumes: # Named volumes = data survives "docker compose down"
  jenkins_data:
  sonarqube_data:
  sonarqube_extensions:
  sonarqube_logs:
  sonar_db_data:
```

**Why PostgreSQL?** SonarQube's built-in H2 database is not recommended even for local use — it can corrupt data on unclean shutdown. PostgreSQL is the official recommendation.

---

## 5. Install Jenkins Plugins

After unlocking Jenkins, install the required plugins:

1. Go to **Manage Jenkins → Plugins → Available plugins**
2. Search and install (check the box, then click **Install**):

| Plugin name to search            | Purpose                                   |
| -------------------------------- | ----------------------------------------- |
| `SonarQube Scanner`              | `withSonarQubeEnv()` step + server config |
| `Temurin`                        | to install JDK-21 automatically     |

3. Tick **Restart Jenkins when installation is complete**.

### Configure Global Tools (Manage Jenkins → Tools)

| Tool  | Name (must match Jenkinsfile) | How to install                                             |
| ----- | ----------------------------- | ---------------------------------------------------------- |
| JDK   | `JDK-21`                      | Add installer → Install from adoptium.net → **Temurin 21** |
| Maven | `Maven-3.9`                   | Add installer → Install from Apache → **3.9.x**            |

### Add Credentials (Manage Jenkins → Credentials → Global → Add)

| ID                         | Kind        | Value                |
| -------------------------- | ----------- | -------------------- |
| `sonarcloud-token`         | Secret text | SonarQube user token |
| `snyk-token` (optional)    | Secret text | Snyk API token       |

### Configure SonarQube Server (Manage Jenkins → System)

1. Scroll to **SonarQube servers** → **Add SonarQube**
2. Set:
   - **Name:** `SonarCloud` _(must match `withSonarQubeEnv('SonarCloud')` in Jenkinsfile)_
   - **Server URL:** `http://sonarqube:9000` _(uses the Docker service name, not localhost)_
   - **Authentication token:** Add a **Secret text** credential with your SonarQube token
   - To get a token, go to `localhost:9000` and click on **My Account** → **Security** → **Generate Token**

---

## 6. How Things Work

```
Developer pushes code
        │
        ▼
  Jenkins Pipeline (Jenkinsfile)
        │
        ├─ Stage: Gitleaks
        │     Scans git history for secrets/credentials
        │     Binary is already in the Jenkins image
        │
        ├─ Stage: Build & Test
        │     mvn clean verify
        │     Runs unit + integration tests
        │
        ├─ Stage: Publish JUnit Results
        │     Parses TEST-*.xml → shows pass/fail in Jenkins UI
        │
        ├─ Stage: SonarCloud
        │     Sends analysis to SonarQube at http://sonarqube:9000
        │     (containers share devops_net, so DNS resolves by service name)
        │
        └─ Stage: Snyk (optional, set SNYK_ENABLED=true)
              Scans pom.xml dependencies for CVEs
```

**Container networking:** All three containers (`jenkins`, `sonarqube`, `sonar_db`) are on the same Docker bridge network `devops_net`. Jenkins reaches SonarQube at `http://sonarqube:9000` — the service name acts as DNS.

---

## 7. Verification Steps

### Verify containers are running

```bash
docker compose -f docker-compose.jenkins.yml ps
# All three should show "running" / "healthy"
```

### Verify Gitleaks is installed in Jenkins

```bash
docker exec jenkins_local gitleaks version
```

### Verify Snyk is installed in Jenkins

```bash
docker exec jenkins_local snyk --version
```

### Verify SonarScanner is installed in Jenkins

```bash
docker exec jenkins_local sonar-scanner --version
```

### Verify SonarQube is up

```bash
curl -s http://localhost:9000/api/system/status | python3 -m json.tool
# Should show: "status": "UP"
```

### Run a test pipeline

1. In Jenkins, create a new **Pipeline** job.
2. Set **Pipeline script** → Copy the code in `Jenkinsfile`.
3. Click **Build Now** and watch the stages complete.

---

## Teardown

```bash
# Stop and remove containers (data is preserved in volumes)
docker compose -f docker-compose.jenkins.yml down

# Full reset — removes all volumes (loses Jenkins config and Sonar data)
docker compose -f docker-compose.jenkins.yml down -v
```
