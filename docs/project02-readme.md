# Project 02: CD and Monitor System for YAS Microservices
## Team Member Assignments & Project Guide (Week 1 Baseline)

Welcome to the Project 02 project repository directory. This workspace contains the configuration files and evidence indexing for the Continuous Delivery (CD), GitOps deployment, and Service Mesh configuration of the YAS Microservices system.

### 1. Group Information & Team Roles
* **Group**: Project 02 - CD and Monitor System Group
* **Team Members**:
  1. **Công Phúc** (MSSV: `TODO`) - *Project Coordinator & QA Lead*
     * Responsibility: Requirement mapping, evidence index framework, CI reuse analysis, integration test planning, and final report compilation.
  2. **Quang Kan** (MSSV: `TODO`) - *CI/CD & Automation Specialist*
     * Responsibility: Docker Hub dynamic tagging, parameterized Jenkins CD jobs (`developer_build`), and deployment cleanup automation.
  3. **Quốc Lộc** (MSSV: `TODO`) - *Infrastructure & GitOps Specialist*
     * Responsibility: Kubernetes cluster provisioning, Helm templating, and ArgoCD dev/staging GitOps deployments.
  4. **Thanh Phong** (MSSV: `TODO`) - *Service Mesh & Security Specialist*
     * Responsibility: Istio sidecar injection, strict mTLS policy enforcement, VirtualService retry policy configuration, AuthorizationPolicies, and Kiali setup.

---

### 2. Project Directory Structure
The workspace has been organized to accommodate all future artifacts and verification evidence without affecting existing application code:

```text
yas/
├── ci/                            # CI-specific scripts or helper configurations
│   └── .gitkeep
├── cd/                            # CD-specific pipelines (e.g., developer_build Jenkinsfile)
│   └── .gitkeep
├── k8s/                           # Kubernetes YAML files
│   ├── base/                      # Common deployments, services, and configuration maps
│   ├── dev/                       # Environment overlays/patches for the dev namespace
│   └── staging/                   # Environment overlays/patches for the staging namespace
├── helm/                          # Helm Charts for YAS microservices
│   └── .gitkeep
├── argocd/                        # ArgoCD GitOps Configurations
│   └── apps/                      # Application manifests targeting dev and staging
│       └── .gitkeep
├── istio/                         # Istio Service Mesh Policies
│   ├── mtls/                      # PeerAuthentication and DestinationRules
│   ├── traffic/                   # VirtualServices and Gateway configurations (retries)
│   └── security/                  # AuthorizationPolicies (allowed/denied flows)
├── docs/                          # Project documentation
│   ├── requirement-mapping.md     # Traceability matrix for all project specifications
│   ├── test-plan.md               # Detailed integration test plan and command templates
│   ├── ci-reuse-notes.md          # Stage-by-stage analysis of the existing Jenkinsfile
│   ├── week1-cong-phuc-checklist.md # Week 1 tasks completion status
│   └── project02-readme.md        # This Project 02 guide file
├── evidence/                      # Operational proofs and outputs (populated in later weeks)
│   ├── jenkins/                   # Jenkins job console logs and execution screens
│   ├── dockerhub/                 # Registry views showing image tag structures
│   ├── k8s/                       # Node statuses, service ports, pod readiness
│   ├── argocd/                    # Synchronized and healthy dashboard shots
│   ├── kiali/                     # Mesh network graphs and communication paths
│   ├── logs/                      # Envoy curl tests, authorization deny outputs, retry proofs
│   └── repository/                # Local host mapping validation
└── report/                        # Compilation of reports and documentation
    └── .gitkeep
```

---

### 3. Documentation Reference Links
Detailed instructions and planning baselines are available via the links below:
* [Requirement Mapping Checklist](file:///d:/Regular_School/N3/HK2/Devops/yas/docs/requirement-mapping.md) - The master matrix connecting tasks to owners, schedules, and expected outputs.
* [Integration Test Plan](file:///d:/Regular_School/N3/HK2/Devops/yas/docs/test-plan.md) - The test cases and commands needed to verify all behaviors.
* [CI Reuse Notes](file:///d:/Regular_School/N3/HK2/Devops/yas/docs/ci-reuse-notes.md) - Summary of the pre-existing Jenkinsfile stages, variables, and security scans.
* [Week 1 Completion Checklist](file:///d:/Regular_School/N3/HK2/Devops/yas/docs/week1-cong-phuc-checklist.md) - The status checklist for Công Phúc.
* [Evidence Index](file:///d:/Regular_School/N3/HK2/Devops/yas/evidence/evidence-index.md) - The tracker for audit files.

---

### 4. Implementation Handoff & Next Steps
With the repository structure and test-plan templates finalized by Công Phúc, the team members can proceed with their respective tasks in Week 2:
1. **Quang Kan**:
   * Refer to [CI Reuse Notes](file:///d:/Regular_School/N3/HK2/Devops/yas/docs/ci-reuse-notes.md) for credentials and environment setup.
   * Append Docker build/push stages to the `Jenkinsfile` for git branch commits (commit ID tag) and release tag events.
   * Implement the `developer_build` Jenkins CD job and the cleanup job.
2. **Quốc Lộc**:
   * Provision the Kubernetes cluster nodes.
   * Prepare base deployment files under `k8s/base/` and configuration overlays under `k8s/dev/` and `k8s/staging/`.
   * Configure local hosts mapping to resolve test endpoints.
3. **Thanh Phong**:
   * Prepare Istio injection namespaces and mTLS templates under `istio/mtls/`.
   * Plan VirtualService retries and AuthorizationPolicies under `istio/traffic/` and `istio/security/`.
   * Refer to [Integration Test Plan](file:///d:/Regular_School/N3/HK2/Devops/yas/docs/test-plan.md) for expected logs and curl outputs.
