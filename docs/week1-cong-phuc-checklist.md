# Week 1 Completion Checklist - Công Phúc

This checklist tracks the completion of Week 1 tasks assigned to **Công Phúc** for Project 02: “CD and Monitor System for YAS Microservices”.

## 1. Requirement Mapping
- [ ] Create mapping of basic requirements (BASIC-01 to BASIC-11) to owners and target weeks.
- [ ] Create mapping of advanced ArgoCD requirements (ADV-ARGO-01 to ADV-ARGO-03) to owners and target weeks.
- [ ] Create mapping of advanced Service Mesh requirements (ADV-MESH-01 to ADV-MESH-08) to owners and target weeks.
- [ ] Create mapping of final deliverables (DELIV-01 to DELIV-10) to formats and owners.
- [ ] Draft an evidence checklist mapping each requirement to a specific output path in `evidence/`.
- [ ] Document open questions, risks, and unknowns as placeholders/TODOs.

## 2. Repository Structure
- [ ] Validate root directory layout without modifying existing code files.
- [ ] Create empty directories for future artifacts:
  - [ ] `ci/`
  - [ ] `cd/`
  - [ ] `k8s/`
  - [ ] `k8s/base/`
  - [ ] `k8s/dev/`
  - [ ] `k8s/staging/`
  - [ ] `helm/`
  - [ ] `argocd/`
  - [ ] `argocd/apps/`
  - [ ] `istio/`
  - [ ] `istio/mtls/`
  - [ ] `istio/traffic/`
  - [ ] `istio/security/`
  - [ ] `docs/`
  - [ ] `evidence/`
  - [ ] `evidence/jenkins/`
  - [ ] `evidence/dockerhub/`
  - [ ] `evidence/k8s/`
  - [ ] `evidence/argocd/`
  - [ ] `evidence/kiali/`
  - [ ] `evidence/logs/`
  - [ ] `evidence/repository/`
  - [ ] `report/`
- [ ] Create and verify `.gitkeep` files inside each of these newly created empty folders.
- [ ] Verify that no existing files were deleted or renamed.

## 3. CI Reuse Documentation
- [ ] Inspect the root `Jenkinsfile` to extract the existing pipeline stages and environment configuration.
- [ ] Document the service detection logic (`CHANGED_SERVICES` via git diff) and list the 22 detected backend/frontend services.
- [ ] Document verified credentials used in the pipeline (`snyk-token` and `sonar-token`).
- [ ] Document required future credentials marked as TODO (`DOCKER_HUB_CREDS`, `kubeconfig`, and ArgoCD credentials).
- [ ] Identify which pipeline components are fully reusable and which require additions.
- [ ] Outline risks, runtime unknowns (Jenkins URL, Docker Hub namespace, K8s IP), and guidelines to avoid breaking previous CI stages.

## 4. Evidence Index
- [ ] Create the index file at `evidence/evidence-index.md` listing all required deliverables.
- [ ] Specify columns: Evidence ID, Requirement ID, Evidence Type, Expected Path, Owner, Status, and Notes.
- [ ] Initialize all statuses to `TODO` or `UNVERIFIED_RUNTIME` according to instructions.

## 5. Test Plan
- [ ] Create the integration test plan template at `docs/test-plan.md`.
- [ ] Define the test status legend (`TODO`, `UNVERIFIED_RUNTIME`, `PASS`, `FAIL`, `BLOCKED`).
- [ ] Include detailed test cases for CI branch triggers, commit ID image tagging, Docker Hub push, default tagging, `developer_build` execution, NodePort exposure, local hosts mapping, cleanup job, namespace deploys, ArgoCD syncing, Istio injection, mTLS, Kiali topology, HTTP 500 retry policy, AuthorizationPolicy ALLOW/DENY, and internal cluster curls.
- [ ] Define command templates for runtime execution.

## 6. Handoff to Quang Kan (CI/CD Automation)
- [ ] Document CI reuse notes in `docs/ci-reuse-notes.md` to establish the reusable baseline.
- [ ] Provide the list of YAS services detected by git diff (20 backend services + 2 frontend services).
- [ ] Outline the TODO tasks for branch commit ID image tagging, Docker Hub builds, the parameterized `developer_build` job, and the cleanup job.

## 7. Handoff to Quốc Lộc (K8s & GitOps Infrastructure)
- [ ] Define namespace expectations for local testing (`dev` and `staging`).
- [ ] Document NodePort and local hosts file evidence requirements (`evidence/k8s/` and `evidence/repository/`).
- [ ] Confirm folder structure creation for K8s manifests, Helm charts, and ArgoCD applications (`k8s/`, `helm/`, `argocd/`).

## 8. Handoff to Thanh Phong (Service Mesh & Observability)
- [ ] Map all Service Mesh requirements (mTLS, retry, AuthorizationPolicy).
- [ ] Provide test cases for mTLS locks, VirtualService retries, Kiali graphs, and allowed/denied sidecar curls.
- [ ] Confirm folder structure creation for Istio YAMLs under `istio/` and Kiali/logs evidence under `evidence/`.

## 9. Final Week 1 Baseline Review
- [ ] Verify that every single Project 02 requirement has at least one planned evidence item in `docs/requirement-mapping.md`.
- [ ] Check that all file content uses technical academic writing standards with placeholders for unknown parameters.
- [ ] Ensure that no application code was modified and the existing `Jenkinsfile` was left unchanged.
