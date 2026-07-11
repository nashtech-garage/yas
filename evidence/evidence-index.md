# Project 02 Evidence Index

This document tracks all required evidence artifacts to verify the implementation of Project 02: "CD and Monitor System for YAS Microservices". All files will be populated during later implementation weeks.

| Evidence ID | Requirement ID | Evidence Type | Expected Path | Owner | Status | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **EVID-01** | BASIC-03 | Image / Log | `evidence/jenkins/branch-ci-build.png` | Quang Kan | `TODO` | Screenshot/log showing branch CI build log, documented in [week2-developer-test-flow.md](file:///d:/Regular_School/N3/HK2/Devops/yas/evidence/jenkins/week2-developer-test-flow.md) |
| **EVID-02** | BASIC-03 | Image | `evidence/dockerhub/commit-tag.png` | Quang Kan | `TODO` | Docker Hub registry view, documented in [week2-commit-id-image.md](file:///d:/Regular_School/N3/HK2/Devops/yas/evidence/dockerhub/week2-commit-id-image.md) |
| **EVID-03** | ADV-ARGO-03 | Image | `evidence/dockerhub/release-tag.png` | Quang Kan | `TODO` | Docker Hub registry view showing image with release tag (e.g. v1.2.3) |
| **EVID-04** | BASIC-04 BASIC-05 | Image | `evidence/jenkins/developer-build-job.png` | Quang Kan | `TODO` | Screenshot of the Jenkins parameterized developer_build configuration screen |
| **EVID-05** | BASIC-06 | Log | `evidence/jenkins/developer-build-selected-tag-logs.txt` | Quang Kan | `TODO` | Jenkins pipeline log showing resolution of branch commit tag, documented in [week2-developer-test-flow.md](file:///d:/Regular_School/N3/HK2/Devops/yas/evidence/jenkins/week2-developer-test-flow.md) |
| **EVID-06** | BASIC-01 BASIC-07 | Log | `evidence/k8s/developer-build-pod-images.txt` | Quốc Lộc / Quang Kan | `TODO` | `kubectl describe pods` output verifying selected service tag vs default tags, documented in [week2-nodeport-access.md](file:///d:/Regular_School/N3/HK2/Devops/yas/evidence/k8s/week2-nodeport-access.md) |
| **EVID-07** | BASIC-02 | Image | `evidence/k8s/cluster-nodes.png` | Quốc Lộc | `TODO` | Screenshot of `kubectl get nodes -o wide` verifying Master/Worker architecture |
| **EVID-08** | BASIC-08 | Log | `evidence/k8s/svc-nodeport.txt` | Quốc Lộc | `TODO` | `kubectl get svc -A` output showing exposed microservices via NodePort, documented in [week2-nodeport-access.md](file:///d:/Regular_School/N3/HK2/Devops/yas/evidence/k8s/week2-nodeport-access.md) |
| **EVID-09** | BASIC-10 | Log | `evidence/repository/hosts-mapping.txt` | Quốc Lộc | `TODO` | Copy of hosts file showing domain name mapped to Kubernetes Worker IP, documented in [week2-nodeport-access.md](file:///d:/Regular_School/N3/HK2/Devops/yas/evidence/k8s/week2-nodeport-access.md) |
| **EVID-10** | BASIC-09 | Log | `evidence/k8s/ingress-port-mapping.txt` | Quốc Lộc | `TODO` | Port layout and domain mapping details for testing endpoints |
| **EVID-11** | BASIC-11 | Log | `evidence/jenkins/cleanup-job-logs.txt` | Quang Kan | `TODO` | Execution output of Jenkins job that deletes development namespace deployments |
| **EVID-12** | ADV-ARGO-01 | Image | `evidence/argocd/argocd-ui-apps.png` | Quốc Lộc | `TODO` | Screenshot of ArgoCD Dashboard displaying dev and staging applications |
| **EVID-13** | ADV-ARGO-02 | Image | `evidence/argocd/argocd-dev-sync.png` | Quốc Lộc | `TODO` | ArgoCD application synchronization history screenshot for the `dev` namespace |
| **EVID-14** | ADV-ARGO-03 | Image | `evidence/argocd/argocd-staging-sync.png` | Quốc Lộc | `TODO` | ArgoCD application synchronization history screenshot for the `staging` namespace |
| **EVID-15** | ADV-MESH-01 | Log | `evidence/k8s/istio-injection.txt` | Thanh Phong | `TODO` | `kubectl get pods` output displaying sidecar container READY statuses (2/2) |
| **EVID-16** | ADV-MESH-03 | Image | `evidence/kiali/topology-graph.png` | Thanh Phong | `TODO` | Kiali dashboard view showing the system's operational graph and microservices |
| **EVID-17** | ADV-MESH-02 | Image | `evidence/kiali/mtls-status.png` | Thanh Phong | `TODO` | Kiali topology overlay or security view displaying padlock icons |
| **EVID-18** | ADV-MESH-04 ADV-MESH-08 | Log | `evidence/logs/envoy-retry-logs.txt` | Thanh Phong | `TODO` | Envoy sidecar logs verifying retry attempts (attempt counts) on HTTP 500 |
| **EVID-19** | ADV-MESH-05 ADV-MESH-07 | Log | `evidence/logs/auth-policy-logs.txt` | Thanh Phong | `TODO` | Envoy sidecar logs showing ALLOW and DENY tags based on AuthorizationPolicies |
| **EVID-20** | ADV-MESH-06 | Log | `evidence/logs/curl-internal-tests.txt` | Thanh Phong | `TODO` | Console output of curl commands executed within client pods to verify mTLS |
