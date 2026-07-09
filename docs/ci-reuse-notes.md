# CI Reuse Analysis and Notes from Previous Project

## 1. Jenkinsfile Summary
The YAS repository contains a pre-configured, multi-stage Jenkins pipeline in its root [Jenkinsfile](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile). It uses `agent any` (running on a Jenkins node, in this case, on Docker Desktop) and limits JVM memory using `MAVEN_OPTS = '-Xmx384m -XX:+UseG1GC'` to prevent container out-of-memory issues. The pipeline is parameter-driven, accepting `DIFF_BASE_BRANCH` (defaulting to `main`) to compute changes relative to parent branches. It utilizes `skipDefaultCheckout()` in its options to manage workspace cleanups and checkouts explicitly within the pipeline stages.

---

## 2. Existing Stages
The pipeline consists of 9 distinct stages, detailed below:

| Stage Name | Description & Action | Reusability for Project 02 | Citation (Jenkinsfile Lines) |
| :--- | :--- | :--- | :--- |
| **Checkout** | Performs workspace cleaning using `cleanWs()`, pulls code using `checkout scm`, and makes the Maven wrapper executable (`sh 'chmod +x mvnw'`). | **Highly Reusable** - Required as the entry point for all builds. | [Lines 19-25](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile#L19-L25) |
| **Detect Changed Services** | Resolves the target branch for diffing. Compares the current HEAD against the base branch (`DIFF_BASE_BRANCH` or PR target `CHANGE_TARGET`). Fallbacks to the last commit diff or `git ls-files` if no diff is found. Filters files to identify modified YAS services, populating `env.CHANGED_SERVICES`. | **Highly Reusable** - Essential for building only the modified services, keeping CI/CD execution times minimal. | [Lines 27-101](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile#L27-L101) |
| **Frontend Build Start Test** | Checks if `backoffice` or `storefront` frontend applications were modified. If so, downloads Node.js v20.12.2 if not found locally, runs `npm install` and `npm run build`, starts the application in the background (ports 3100 and 3101), and runs Jest tests with JSON coverage summaries. | **Highly Reusable** - Ensures frontend quality before deployment. | [Lines 103-147](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile#L103-L147) |
| **Security Scan** | Downloads GitLeaks v8.18.4 and runs credentials scans against changes made in the branch relative to the base branch. Failures halt the pipeline. Archives `gitleaks-report.json`. | **Highly Reusable** - Essential guardrail to prevent secrets from reaching production. | [Lines 149-172](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile#L149-L172) |
| **Snyk Dependency Scan** | Installs Snyk CLI. Resolves and builds internal YAS shared resources (`common-library`). Executes Snyk vulnerability tests on Maven `pom.xml` files or Node `package.json` files for modified services. Requires `snyk-token` credential. Archives JSON reports. | **Highly Reusable** - Keeps third-party dependency vulnerabilities in check. | [Lines 174-215](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile#L174-L215) |
| **Test Phase** | Runs unit/integration tests for modified backend services (`./mvnw verify jacoco:report -DskipITs ...`). If no changes are detected (e.g. main branch full build), runs tests for all 20 backend services. Publishes JUnit reports. | **Highly Reusable** - Core quality gate. | [Lines 217-284](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile#L217-L284) |
| **Coverage Quality Gate** | Asserts line coverage for all modified services. Parses Jest JSON reports for frontends and JaCoCo CSV reports for backends. Enforces a strict threshold of **70%**. Failing this threshold halts the pipeline. | **Highly Reusable** - Ensures test writing standards. | [Lines 286-362](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile#L286-L362) |
| **SonarQube Scan** | Resolves `SONAR_HOST_URL`. Downloads Sonar Scanner CLI (for frontends). Executes analysis for Maven and Node modules and pushes reports to SonarQube. Requires `sonar-token` credential. | **Highly Reusable** - Code quality check baseline. | [Lines 364-479](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile#L364-L479) |
| **Build Phase** | Runs Maven compilation (`./mvnw clean package -DskipTests ...`) for changed backend microservices. On success, archives generated `.jar` files. | **Highly Reusable** - Replaces local packaging. | [Lines 481-542](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile#L481-L542) |

---

## 3. Existing Service Detection Logic
The pipeline uses a hardcoded list of services within the `Detect Changed Services` stage:
* **Backend Services**: `cart`, `customer`, `delivery`, `inventory`, `location`, `media`, `order`, `payment`, `payment-paypal`, `product`, `promotion`, `rating`, `recommendation`, `search`, `tax`, `backoffice-bff`, `storefront-bff`, `identity`, `sampledata`, `webhook`.
* **Frontend Services**: `backoffice`, `storefront`.

It detects changes using a Groovy regular expression lookup:
```groovy
def affected = services.findAll { svc ->
    changedFiles && (changedFiles =~ /(?m)^${java.util.regex.Pattern.quote(svc)}\//)
}
```
If a file path in the git diff is prefixed with a service folder name (e.g., `cart/src/...`), the service is flagged for building. If no services are affected, it returns `'none'`.

---

## 4. Existing Credentials
The following credentials are referenced in the existing `Jenkinsfile`:
* **`snyk-token`**: String credential used by the Snyk CLI to authenticate and perform vulnerability checks. (Verified: [Line 179](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile#L179)).
* **`sonar-token`**: String credential used by the SonarQube scanner to authenticate and publish metrics to SonarQube. (Verified: [Line 366](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile#L366)).
* **Git Checkout (Implicit)**: Configured in the Jenkins job definition and implicitly utilized by the `checkout scm` pipeline step. (Verified: [Line 22](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile#L22)).

The following credentials are **NOT** found in the existing `Jenkinsfile` and must be created later for CD:
* **Docker Hub Credential (`TODO`)**: Needed by the future Docker image build/push stage to authenticate against Docker Hub.
* **Kubernetes Cluster Credential (`TODO`)**: (e.g., `kubeconfig` string or certificate) Needed by the CD pipeline to access and apply manifests on the K8s cluster.
* **ArgoCD API Token / Credentials (`TODO`)**: Needed if Jenkins triggers ArgoCD syncs programmatically.

---

## 5. Reusable Parts for Project 02
* **Selective Verification Flow**: Building and testing only what has changed keeps resource utilization inside limits. This must be preserved.
* **Credentials Integration**: The Snyk and SonarQube credentials integrations are fully functional and ready to be maintained.
* **Security & Coverage Checks**: The GitLeaks, Jest, and JaCoCo coverage quality checks are already in place and can serve as gatekeepers before CD steps are triggered.

---

## 6. Required Additions for Project 02
To implement the new CD requirements, the following steps must be added in future weeks:
1. **Docker Containerization Stage**: Create `Dockerfile` assets for each YAS microservice (if they do not exist, or reuse existing docker definitions) and write a Jenkins stage to execute `docker build` and `docker push`.
2. **Release Trigger Stage**: An additional stage or branch detection block in the pipeline to capture git release tags (e.g. `v*`) and push images with the corresponding release tag.
3. **Commit-ID Image Tagging**: CI logic to extract the short commit SHA (`git rev-parse --short HEAD`) to tag branch images.
4. **Developer CD Pipeline (`developer_build`)**: A new parameter-driven Jenkins pipeline job that fetches specific commit images and deploys them to Kubernetes.
5. **GitOps Manifest Repositories**: Update manifests under `k8s/` or `helm/` with new image tags and commit changes so ArgoCD can detect and synchronize the state.
6. **Istio Traffic Routing and Policies**: Service Mesh manifests (VirtualServices, PeerAuthentications, AuthorizationPolicies) deployed to the cluster.

---

## 7. Risks and Unknowns
* **Docker Daemon Availability**: The Jenkins pipeline agent runs on a node and must have access to a Docker daemon (via Docker-in-Docker or host socket mount) to perform `docker build` and `docker push`. This is **UNVERIFIED_RUNTIME**.
* **Kubernetes Connectivity**: The pipeline will need network access and credentials to reach the Kubernetes API server. This is **UNVERIFIED_RUNTIME**.
* **ArgoCD Server URL and Access**: The method Jenkins will use to trigger/notify ArgoCD (automatic Git polling vs. webhook triggers vs. API calls) is **UNVERIFIED_RUNTIME**.
* **Maven Cache Sharing**: Selective service tests often require internal libraries. The Snyk scan currently resolves `common-library` manually (`./mvnw install -pl common-library -am ...`). If other stages require it, caching or building libraries must be handled carefully.

---

## 8. Do-Not-Break Notes
* **Do NOT remove or rewrite any existing CI stages**. The stages (`Checkout`, `Detect Changed Services`, `Frontend Build Start Test`, `Security Scan`, `Snyk Dependency Scan`, `Test Phase`, `Coverage Quality Gate`, `SonarQube Scan`, and `Build Phase`) must be kept intact.
* **Do NOT delete the coverage check step** (`Coverage Quality Gate`). Dropping coverage checks will break previous QA requirements.
* **Do NOT hardcode branch names** or bypass the Git diff change-detection logic.
* **Ensure all additions are appended after the successful verification stages** (specifically after the `Build Phase`) so that deployments only occur for builds that have fully passed all test gates.
