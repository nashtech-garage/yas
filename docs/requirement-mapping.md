# Project 02: CD and Monitor System for YAS Microservices
## Requirement Mapping Checklist

### 1. Project Information
* **Project Name**: CD and Monitor System for YAS Microservices (Project 02)
* **Target Application**: YAS (Yet Another Shop) Microservices
* **Source Repository**: [luc1fe4/yas](https://github.com/luc1fe4/yas)
* **Team Members & Roles**:
  * **Công Phúc** (MSSV: `TODO`) - Role: Project Manager & QA Lead (Week 1 Owner: Requirement mapping, repository organization, CI analysis, test plan preparation, project documentation baseline).
  * **Quang Kan** (MSSV: `TODO`) - Role: CI/CD & Automation Specialist (Docker Hub integration, dynamic branch image tagging, `developer_build` CD job, cleanup job).
  * **Quốc Lộc** (MSSV: `TODO`) - Role: Infrastructure & GitOps Specialist (Kubernetes cluster provisioning, Helm chart templates, ArgoCD multi-environment CD for `dev` & `staging`).
  * **Thanh Phong** (MSSV: `TODO`) - Role: Service Mesh & Security Specialist (Istio configuration, mTLS policies, VirtualService retries, AuthorizationPolicy, Kiali visualization & verification logs).
* **Jenkins Server URL**: `TODO` (Unverified runtime value)
* **Docker Hub Namespace**: `TODO` (Unverified runtime value)
* **Kubernetes Cluster Type/Worker IP**: `TODO` (Unverified runtime value)
* **ArgoCD URL**: `TODO` (Unverified runtime value)
* **Kiali URL**: `TODO` (Unverified runtime value)

---

### 2. Reused CI Baseline from Previous Project
The previous CI project established a solid build, test, and scan baseline in the repository's root [Jenkinsfile](file:///d:/Regular_School/N3/HK2/Devops/yas/Jenkinsfile). The following components will be reused:
* **Repository Layout**: Multiple backend microservices and frontend applications in a single repository.
* **Changed Services Detection**: Git-diff-based selective testing and building (stage `Detect Changed Services`).
* **Static Verification**: Lint/Secret Scanning (GitLeaks), Dependency Scanning (Snyk), Unit/Integration Testing (Maven/Jest), Coverage Verification (JaCoCo/Jest at 70% quality gate), and SonarQube Scanner.
* **Build Artifacts**: Compiled Maven `.jar` files archived upon success.

---

### 3. Basic Requirement Mapping Table
This table lists the basic requirements for Project 02, including owner assignments, target weeks, expected evidence paths, and current status.

| Requirement ID | Description | Owner | Target Week | Required Evidence | Current Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **BASIC-01** | Default image for all services uses tag `main` or `latest`. | Quốc Lộc | Week 2 | `k8s/base/` manifest templates; `evidence/k8s/default-images.txt` | `TODO` |
| **BASIC-02** | Build a Kubernetes cluster with 1 Master node and 1 Worker node, or Minikube/any valid model. | Quốc Lộc | Week 2 | `evidence/k8s/cluster-nodes.png` showing `kubectl get nodes -o wide` | `TODO` |
| **BASIC-03** | After commit on user branch, CI builds Docker image tagged with last commit ID and pushes to Docker Hub. | Quang Kan | Week 2 | `evidence/jenkins/branch-ci-build.png`; `evidence/dockerhub/commit-tag.png` | `TODO` |
| **BASIC-04** | Create a CD job named `developer_build` in Jenkins. | Quang Kan | Week 2 | `evidence/jenkins/developer-build-job.png` (job config screen) | `TODO` |
| **BASIC-05** | `developer_build` accepts input parameters for the branch/service to deploy. | Quang Kan | Week 2 | `evidence/jenkins/developer-build-parameters.png` (parameter configuration) | `TODO` |
| **BASIC-06** | The selected service in `developer_build` uses the image tag matching the branch commit ID. | Quang Kan | Week 2 | `evidence/jenkins/developer-build-selected-tag-logs.txt` showing tag resolution | `TODO` |
| **BASIC-07** | All other non-selected services in `developer_build` use default tag `main` or `latest`. | Quang Kan / Quốc Lộc | Week 2 | `evidence/k8s/developer-build-pod-images.txt` showing image tags in namespace | `TODO` |
| **BASIC-08** | Deploy services to Kubernetes and expose them as `NodePort`. | Quốc Lộc | Week 2 | `evidence/k8s/svc-nodeport.txt` showing `kubectl get svc -A` | `TODO` |
| **BASIC-09** | Provide `domain_name:port` for developer testing. | Quốc Lộc | Week 2 | `evidence/k8s/ingress-port-mapping.txt` or testing URL references | `TODO` |
| **BASIC-10** | Provide local hosts file mapping so domain points to the Kubernetes Worker node IP. | Quốc Lộc | Week 2 | `evidence/repository/hosts-mapping.txt` showing entry from `/etc/hosts` or `C:\Windows\System32\drivers\etc\hosts` | `TODO` |
| **BASIC-11** | Create a Jenkins job to delete the deployment created by `developer_build`. | Quang Kan | Week 2 | `evidence/jenkins/cleanup-job-logs.txt` showing deployment deletion | `TODO` |

---

### 4. Advanced ArgoCD Requirement Mapping Table
This table lists the GitOps and continuous delivery requirements using ArgoCD.

| Requirement ID | Description | Owner | Target Week | Required Evidence | Current Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **ADV-ARGO-01** | Use ArgoCD to handle `dev` and `staging` environments. | Quốc Lộc | Week 3 | `evidence/argocd/argocd-ui-apps.png` showing applications list in ArgoCD | `TODO` |
| **ADV-ARGO-02** | When `main` branch changes, automatically deploy continuously into namespace `dev`. | Quốc Lộc / Quang Kan | Week 3 | `evidence/argocd/argocd-dev-sync.png` showing sync status and history for `dev` | `TODO` |
| **ADV-ARGO-03** | When `main` has a release tag (e.g. `v1.2.3`), CI/CD builds release image, pushes, and deploys to `staging`. | Quang Kan / Quốc Lộc | Week 3 | `evidence/dockerhub/release-tag.png` on Docker Hub; `evidence/argocd/argocd-staging-sync.png` | `TODO` |

---

### 5. Advanced Service Mesh Requirement Mapping Table
This table maps the security, traffic management, and observability requirements via Istio Service Mesh.

| Requirement ID | Description | Owner | Target Week | Required Evidence | Current Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **ADV-MESH-01** | Configure Service Mesh on Kubernetes for YAS microservices. | Thanh Phong | Week 4 | `evidence/k8s/istio-injection.txt` showing sidecar container in pods | `TODO` |
| **ADV-MESH-02** | Enable mTLS between deployed microservices. | Thanh Phong | Week 4 | `istio/mtls/` peer authentication YAML; `evidence/kiali/mtls-status.png` | `TODO` |
| **ADV-MESH-03** | Use Kiali to observe topology and service flow charts. | Thanh Phong | Week 4 | `evidence/kiali/topology-graph.png` showing traffic flowing between services | `TODO` |
| **ADV-MESH-04** | Configure retry behavior when a service returns HTTP 500 using VirtualService retry policy. | Thanh Phong | Week 4 | `istio/traffic/` VirtualService retry YAML; `evidence/logs/retry-evidence.txt` | `TODO` |
| **ADV-MESH-05** | Configure AuthorizationPolicy so only permitted services can communicate. | Thanh Phong | Week 4 | `istio/security/` AuthorizationPolicy YAMLs | `TODO` |
| **ADV-MESH-06** | Test from another pod inside the cluster using `kubectl exec ... curl ...`. | Thanh Phong | Week 4 | `evidence/logs/curl-internal-tests.txt` showing response code and verbose details | `TODO` |
| **ADV-MESH-07** | Save logs proving allowed and denied connections. | Thanh Phong | Week 4 | `evidence/logs/auth-policy-logs.txt` containing sidecar logs showing ALLOW/DENY | `TODO` |
| **ADV-MESH-08** | Save retry evidence (such as Envoy logs showing retries). | Thanh Phong | Week 4 | `evidence/logs/envoy-retry-logs.txt` showing X-Envoy-Attempt-Count or retry calls | `TODO` |

---

### 6. Final Deliverables Mapping Table
This table maps the required deliverables, format, ownership, and planning details.

| Deliverable ID | Deliverable Description | Format / Path | Owner | Target Week | Current Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **DELIV-01** | CI/CD Pipeline Configuration | `Jenkinsfile` (updated in root) | Quang Kan | Week 3 | `VERIFIED_FROM_JENKINSFILE` (CI baseline only) / `TODO` (CD addition) |
| **DELIV-02** | Docker Hub Repository & Images | Docker Hub Registry | Quang Kan | Week 2 | `TODO` |
| **DELIV-03** | Kubernetes Manifests or Helm Charts | `k8s/` and `helm/` | Quốc Lộc | Week 2 | `TODO` |
| **DELIV-04** | ArgoCD Application Manifests | `argocd/apps/` | Quốc Lộc | Week 3 | `TODO` |
| **DELIV-05** | Istio Manifests (mTLS, retry, auth) | `istio/` | Thanh Phong | Week 4 | `TODO` |
| **DELIV-06** | Kiali Topology Graph & Flows | Image in `evidence/kiali/` | Thanh Phong | Week 4 | `TODO` |
| **DELIV-07** | Test Plan and Verification Logs | `docs/test-plan.md` & `evidence/logs/` | Công Phúc / Thanh Phong | Week 1 (Template) / Week 5 (Logs) | `VERIFIED_FROM_JENKINSFILE` (Template done) / `TODO` (Logs) |
| **DELIV-08** | Step-by-Step README Deployment Guide | `docs/project02-readme.md` | Công Phúc / All | Week 5 | `TODO` |
| **DELIV-09** | Final Word Report with Screenshots | `report/<MSSV_Sorted>.docx` | Công Phúc | Week 5 | `TODO` |
| **DELIV-10** | Correct Report Filename Format | `<MSSV1>_<MSSV2>_<MSSV3>_<MSSV4>.docx` | Công Phúc | Week 5 | `TODO` |

---

### 7. Evidence Checklist
The following evidence must be generated and stored under the `evidence/` folder to prove successful implementation of all Project 02 requirements:
* [ ] **Jenkins Evidence (`evidence/jenkins/`)**:
  * [ ] Branch CI build log or screenshot proving docker image build and push (BASIC-03).
  * [ ] `developer_build` parameter screen and execution log (BASIC-04, BASIC-05, BASIC-06).
  * [ ] CD cleanup job configuration and execution output (BASIC-11).
* [ ] **Docker Hub Evidence (`evidence/dockerhub/`)**:
  * [ ] Screenshot of repository showing tags: `latest`, commit IDs, and release tags (BASIC-03, ADV-ARGO-03).
* [ ] **Kubernetes Evidence (`evidence/k8s/`)**:
  * [ ] Cluster status verification showing 1 Master and 1 Worker node (BASIC-02).
  * [ ] Expose configuration output and service listings showing NodePort (BASIC-08).
  * [ ] Pod configurations showing default images used for non-selected services (BASIC-01, BASIC-07).
  * [ ] Ingress/Port mapping and URL outputs (BASIC-09).
  * [ ] Sidecar container validation proving Istio injection (ADV-MESH-01).
* [ ] **ArgoCD Evidence (`evidence/argocd/`)**:
  * [ ] ArgoCD UI screenshots displaying synced/healthy applications for `dev` (ADV-ARGO-02) and `staging` (ADV-ARGO-03).
* [ ] **Kiali Evidence (`evidence/kiali/`)**:
  * [ ] Topology map with traffic flow overlay showing active microservice communication (ADV-MESH-03).
  * [ ] mTLS locks showing secure communication status (ADV-MESH-02).
* [ ] **Logs & Test Results (`evidence/logs/`)**:
  * [ ] Internal cluster curl connection test outputs showing status 200 (ADV-MESH-06).
  * [ ] Allowed / Denied traffic logs from sidecars based on AuthorizationPolicy (ADV-MESH-07).
  * [ ] Envoy sidecar logs verifying retry attempts on HTTP 500 (ADV-MESH-04, ADV-MESH-08).
* [ ] **Repository & Host Evidence (`evidence/repository/`)**:
  * [ ] Copy of hosts file showing domain-to-IP mappings (BASIC-10).

---

### 8. Open Questions / TODOs
1. **Docker Hub Credentials**: Where are the credentials stored in Jenkins? We need to declare `DOCKER_HUB_CREDS` in Jenkins Credentials. (Owner: Quang Kan, Target: Week 2)
2. **Kubernetes Configuration (`kubeconfig`)**: How does Jenkins access the Kubernetes cluster? Does the Jenkins agent have direct access, or do we use a Kubeconfig credential in the pipeline? (Owner: Quốc Lộc / Quang Kan, Target: Week 2)
3. **Hosts Resolution**: For developer testing with domain names, do we need to dynamically provision DNS, or is a static hosts file addition on developer local machine sufficient? (Confirmed: local hosts file mapping is sufficient). (Owner: Quốc Lộc, Target: Week 2)
4. **Mocking HTTP 500 for Retry Tests**: Which microservice is easiest to mock or trigger an HTTP 500 error in order to test the VirtualService retry policy? (Owner: Thanh Phong / Công Phúc, Target: Week 4)
5. **ArgoCD API Credentials**: Does Jenkins trigger ArgoCD syncs via API, or does ArgoCD auto-poll git? (Confirmed: GitOps pull-based polling or Webhooks will be configured). (Owner: Quốc Lộc, Target: Week 3)
