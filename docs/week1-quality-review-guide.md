# Week 1 Quality Review Guide

## 1. Review Objective
The objective of this guide is to define how **Công Phúc** will verify and audit the quality of the deliverables produced by the team members during **Week 1**. This ensures the foundation is sufficient, secure, and compliant before continuing with Week 2 implementation tasks.

---

## 2. Review Rules
To ensure rigor and prevent undocumented or incomplete work from blocking progress:
1. **Evidence is Required**: Every completed task must have direct, verifiable evidence.
2. **Runtime Validation Checks**: Every runtime-dependent claim (e.g., "K8s cluster is running") must be accompanied by command line output, screenshot evidence, or log extracts.
3. **Handle Missing Runtime Environments**: If the runtime system is not available (e.g., Jenkins server offline, Docker Hub unreachable, K8s cluster not started yet), mark the item as `UNVERIFIED_RUNTIME` and supply the exact manual command the developer should use once the system goes online.
4. **Handle Missing Implementations**: If a file/document claims a feature is present but no underlying code, YAML file, or configuration exists, mark it as `UNVERIFIED`.
5. **No Vague Statements**: Statements such as "Jenkins works" or "Kubernetes is ready" without screenshots or terminal logs are unacceptable.

---

## 3. Quality Review for Quang Kan — DevOps & CI/CD Engineer

### 3.1 Week 1 Tasks to Review
1. Survey YAS source and identify services that need Docker image builds.
2. Install/configure Jenkins or GitHub Actions to trigger on user branch commit.
3. Write a basic CI pipeline to build a Docker image for a sample service and tag it with the last commit ID.

### 3.2 Verification Checklist for Công Phúc
* [ ] **Service list exists**: Check that a list of services requiring Docker builds is compiled and documented.
* [ ] **List matches YAS structure**: Confirm the service list matches actual YAS backend/frontend source directories.
* [ ] **Dockerfile survey complete**: Confirm that the Dockerfile/build context is identified for each service, and missing Dockerfiles are marked `TODO`.
* [ ] **Pipeline definition exists**: Check if a `Jenkinsfile` or GitHub Actions workflow exists in the repository.
* [ ] **Branch trigger configuration**: Check for a branch trigger filter or instructions on how Jenkins triggers automatically on branch commits.
* [ ] **Commit ID extraction**: Confirm the pipeline obtains the short commit ID correctly (e.g., using `git rev-parse --short HEAD` or built-in env variables like `GIT_COMMIT`).
* [ ] **Dynamic image tagging**: Verify that the pipeline tags the Docker image using the extracted commit ID.
* [ ] **Docker build integration**: Look for `docker build` stages in the pipeline, or verify they are marked `TODO` if not yet implemented.
* [ ] **Docker push integration**: Look for `docker push` stages, or check if marked `TODO`.
* [ ] **No hardcoded credentials**: Confirm that no Docker Hub passwords, tokens, or personal keys are hardcoded in the pipeline.
* [ ] **Secrets are not committed**: Verify that `.env` files or certificates are not committed.
* [ ] **Logs / Screenshots**: Confirm pipeline run logs or screenshot placeholders are ready in `evidence/jenkins/`.

### 3.3 Verification Commands to Run
Run these commands to review Quang Kan's work:
```powershell
# 1. Check if untracked credentials or configuration files exist locally
git status --short

# 2. Find all Dockerfiles and docker-compose configurations in the project
find . -name "Dockerfile" -o -name "docker-compose*.yml"

# 3. Check for Docker build command references and Git commit/tag extraction logic
grep -R "docker build\|docker push\|GIT_COMMIT\|git rev-parse\|BUILD_NUMBER\|commit" Jenkinsfile .github ci cd 2>/dev/null || true

# 4. Audit files for hardcoded secrets, tokens, or passwords
grep -R "password\|token\|secret\|DOCKERHUB\|dockerhub" Jenkinsfile .github ci cd 2>/dev/null || true
```

### 3.4 Pass/Fail Criteria
* **PASS**: A service directory mapping exists, a `Jenkinsfile` or workflow exists, the pipeline contains logic to extract the Git commit ID and tag images, and credentials are obfuscated via credentials managers.
* **FAIL**: Credentials/secrets are committed in plain text, service list deviates from actual source folders, or no pipeline/workflow is defined.

---

## 4. Quality Review for Quốc Lộc — Infrastructure & K8s Engineer

### 4.1 Week 1 Tasks to Review
1. Set up a Kubernetes cluster with 1 Master node and 1 Worker node (or Minikube / similar local K8s environment).
2. Create base namespaces: `dev`, `staging`, and developer sandbox namespaces.
3. Prepare a local `hosts` file sample mapping temporary domain to the Worker node IP.

### 4.2 Verification Checklist for Công Phúc
* [ ] **Cluster type documented**: The documentation clearly states the cluster deployment model (e.g., Minikube, K3s, kubeadm).
* [ ] **Node wide output**: Evidence of `kubectl get nodes -o wide` is provided.
* [ ] **Node Status Ready**: Nodes display status `Ready`.
* [ ] **Namespace list documented**: The list of created namespaces is provided.
* [ ] **Target namespaces exist**: Core namespaces `dev` and `staging` exist, or missing ones are marked `TODO`.
* [ ] **Worker IP identified**: The worker node IP address is clearly documented, or marked `TODO` if offline.
* [ ] **hosts file example exists**: A draft hosts file entry matching the design is documented.
* [ ] **NodePort compatibility**: The NodePort access plan is compatible with the host OS network architecture.
* [ ] **No kubeconfig committed**: Ensure `kubeconfig`, SSH keys, or cluster admin credentials are not in the git history.

### 4.3 Verification Commands to Run
Run these commands to review Quốc Lộc's work:
```powershell
# 1. Verify current active Kubernetes context
kubectl config current-context

# 2. Get status, roles, and IP addresses of cluster nodes
kubectl get nodes -o wide

# 3. List all active namespaces in the cluster
kubectl get namespaces

# 4. List all active services across all namespaces to audit ports
kubectl get svc -A

# 5. Scan the repository to ensure no kubeconfig files or keys are committed
grep -R "server:\|client-key-data\|token:" . --exclude-dir=.git 2>/dev/null || true
```

### 4.4 Pass/Fail Criteria
* **PASS**: The Kubernetes cluster shows nodes in `Ready` status, target namespaces (`dev`, `staging`) are created or scripted, a hosts file sample is ready, and no security keys/kubeconfigs are checked into Git.
* **FAIL**: Kubeconfig credentials are committed, namespaces are missing without notice, or node state shows `NotReady` without explanation.

---

## 5. Quality Review for Thanh Phong — System & Service Mesh Engineer

### 5.1 Week 1 Tasks to Review
1. Analyze YAS microservices architecture, mapping main services, dependencies, ports, and communication flows.
2. Draw a rough service connection flow diagram.
3. Prepare the Istio/Kiali plan and select target services for mTLS, retry, and AuthorizationPolicy tests.

### 5.2 Verification Checklist for Công Phúc
* [ ] **Architecture document exists**: Check for a document detailing the microservices architecture.
* [ ] **Services list complete**: Verify that the document lists the main services.
* [ ] **Dependencies map matches config**: Confirm that mapped service dependencies align with code references and configs.
* [ ] **Ports documented**: Service ports are documented or marked `TODO` if unknown.
* [ ] **Flow diagram exists**: A diagram (Image, Mermaid, PlantUML, or Markdown table) shows service connectivity.
* [ ] **Service Mesh plan complete**: The plan outlines the scope for Istio, Kiali, mTLS, VirtualService retry, AuthorizationPolicy, and internal curl verification.
* [ ] **Selected test services listed**: Specific services for initial sidecar/retry testing are selected (e.g., `storefront-bff`, `product`).
* [ ] **Honest status representation**: The documentation does not claim mTLS/retries/AuthorizationPolicies are implemented unless the corresponding Istio manifests actually exist in the workspace.

### 5.3 Verification Commands to Run
Run these commands to review Thanh Phong's work:
```powershell
# 1. Search for architecture, service mesh, or Istio manifests
find . -maxdepth 4 -type f | grep -Ei "architecture|service-flow|mesh|kiali|istio|authorization|virtualservice|peerauthentication|destinationrule"

# 2. Scan application properties and YAMLs to verify documented service ports and names
grep -R "server.port\|SPRING_APPLICATION_NAME\|spring.application.name\|management.endpoints" . --include="*.yml" --include="*.yaml" --include="*.properties" 2>/dev/null || true

# 3. Check documentation for references to Istio CRDs and security terms
grep -R "VirtualService\|AuthorizationPolicy\|PeerAuthentication\|DestinationRule\|Kiali\|mTLS" docs istio 2>/dev/null || true
```

### 5.4 Pass/Fail Criteria
* **PASS**: The architecture document describes YAS services and ports, a diagram shows dependency relationships, and a clear Service Mesh testing scope (Istio, mTLS, retry, AuthorizationPolicy) is planned.
* **FAIL**: Service mappings are completely inaccurate (missing core microservices), or the plan assumes features are implemented when no manifests exist.

---

## 6. Final Week 1 Baseline Approval Checklist
Công Phúc must complete this checklist before declaring the Week 1 baseline approved:

* [ ] **Quang Kan has a verified service list**: Mapped to YAS folders.
* [ ] **Quang Kan has a branch-trigger CI plan**: Jenkins configuration or webhook logic is ready.
* [ ] **Quang Kan has Docker image tag-by-commit-id plan**: Tagging logic is scripted or planned.
* [ ] **Quốc Lộc has K8s cluster evidence**: Cluster nodes status verified or documented.
* [ ] **Quốc Lộc has namespace evidence**: Target namespaces (`dev`/`staging`) are planned/created.
* [ ] **Quốc Lộc has hosts file mapping draft**: Ready for local DNS setup.
* [ ] **Thanh Phong has YAS service architecture analysis**: Detailed services and ports description.
* [ ] **Thanh Phong has service flow diagram**: Visual connection representation.
* [ ] **Thanh Phong has Service Mesh test scope**: Test cases mapped out.
* [ ] **All missing runtime items are marked `UNVERIFIED_RUNTIME`**: No unproven claims.
* [ ] **All Week 2 dependencies are clear**: Team is ready to transition to implementation and testing.
