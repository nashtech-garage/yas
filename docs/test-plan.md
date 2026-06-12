# Project 02 Integration Test Plan Template
## 1. Test Plan Objective
The objective of this integration test plan is to define the verification steps and criteria for the Continuous Integration (CI), Continuous Delivery (CD), Kubernetes deployment, GitOps (ArgoCD), and Service Mesh (Istio) configurations for the YAS Microservices system. This plan ensures that all functional and infrastructure requirements are met and documented with audit-ready evidence.

---

## 2. Test Status Legend
* **TODO**: Test case is drafted but execution has not started.
* **UNVERIFIED_RUNTIME**: Test case logic is planned, but execution requires active CI/CD infrastructure, Docker Hub registries, or K8s runtime environments.
* **PASS**: Test case has been executed successfully, and expected results match actual results (with evidence saved).
* **FAIL**: Test case failed during execution (logs/screenshots captured for debugging).
* **BLOCKED**: Test case cannot be executed due to dependency failures (e.g., cluster offline, image push failed).

---

## 3. Test Environment Placeholders
* **Jenkins Master URL**: `TODO`
* **ArgoCD Server URL**: `TODO`
* **Kiali Console URL**: `TODO`
* **Docker Hub Username/Namespace**: `TODO`
* **Kubernetes API Server / Node IP**: `TODO`
* **Developer Domain Name**: `TODO`
* **Developer Testing Port**: `TODO`

---

## 4. Test Case Table

| Test ID | Req ID | Title / Objective | Preconditions | Test Steps | Expected Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-CI-01** | BASIC-03 | CI Branch Trigger | Git push on non-main branch (e.g., `feature/cart-update`) | 1. Make a small commit on a branch.<br>2. Push commit to remote.<br>3. Check Jenkins pipeline trigger. | Jenkins detects the push and automatically triggers the pipeline for that branch. | `UNVERIFIED_RUNTIME` |
| **TC-CI-02** | BASIC-03 | Docker Image Tag using Commit ID | Successful trigger of branch pipeline (TC-CI-01) | 1. Inspect the Jenkins build logs for the docker build step.<br>2. Verify image tag variable. | Docker image is built with the tag matching the last git commit SHA (e.g., `cart:a1b2c3d`). | `UNVERIFIED_RUNTIME` |
| **TC-CI-03** | BASIC-03 | Docker Hub Push | TC-CI-02 completes successfully | 1. Log in to Docker Hub.<br>2. Locate the microservice repository.<br>3. Verify tags list. | The image with the commit ID tag is pushed and publicly visible on Docker Hub. | `UNVERIFIED_RUNTIME` |
| **TC-CI-04** | BASIC-01 | Default Image `main`/`latest` Tag | Merge changes to `main` branch | 1. Push or merge code to `main` branch.<br>2. Run CI pipeline.<br>3. Verify Docker Hub tags. | Docker image is built and pushed with tag `main` or `latest`. | `UNVERIFIED_RUNTIME` |
| **TC-CD-01** | BASIC-04 BASIC-05 | `developer_build` Parameter Input | Jenkins CD job configured | 1. Open Jenkins Dashboard.<br>2. Select `developer_build` job.<br>3. Click "Build with Parameters". | Input fields for `BRANCH_NAME` and `SERVICE_NAME` are presented. | `UNVERIFIED_RUNTIME` |
| **TC-CD-02** | BASIC-06 | `developer_build` Selected Service tag | Parameter inputs provided (e.g., service `cart` on branch `feature/cart-update`) | 1. Trigger `developer_build` with parameters.<br>2. Inspect CD execution logs.<br>3. Verify deployed image tag. | The selected service is deployed using the commit ID tag (e.g., `cart:a1b2c3d`). | `UNVERIFIED_RUNTIME` |
| **TC-CD-03** | BASIC-07 | Non-selected Services default tag | TC-CD-02 execution completes | 1. Describe the pods in the development namespace.<br>2. Inspect images for all other services. | All services other than the selected one run the image tagged `main` or `latest`. | `UNVERIFIED_RUNTIME` |
| **TC-CD-04** | BASIC-08 | Kubernetes NodePort Access | Services deployed in K8s | 1. Run `kubectl get svc -A`.<br>2. Identify NodePorts assigned to Gateway/BFF. | The services are successfully bound to NodePorts on the Kubernetes worker nodes. | `UNVERIFIED_RUNTIME` |
| **TC-CD-05** | BASIC-09 | Domain & Port access | TC-CD-04 completed | 1. Open web browser.<br>2. Navigate to `http://<domain_name>:<nodeport>`. | The YAS Storefront or Backoffice homepage loads correctly. | `UNVERIFIED_RUNTIME` |
| **TC-CD-06** | BASIC-10 | Hosts File Mapping | Local OS hosts file configured | 1. Add mapping: `<Worker-IP> <domain_name>`.<br>2. Ping the `<domain_name>`. | The ping resolves to the exact IP of the Kubernetes Worker node. | `UNVERIFIED_RUNTIME` |
| **TC-CD-07** | BASIC-11 | CD Cleanup Job | Deployment exists from `developer_build` | 1. Trigger the cleanup Jenkins job.<br>2. Verify pod/svc deletion in namespace. | All Kubernetes deployments created by `developer_build` are deleted. | `UNVERIFIED_RUNTIME` |
| **TC-GITOPS-01**| ADV-ARGO-01 | ArgoCD App Health & Sync | ArgoCD apps created for dev/staging | 1. Open ArgoCD web UI.<br>2. Select YAS applications. | All applications show Status `Synced` and Health `Healthy`. | `UNVERIFIED_RUNTIME` |
| **TC-GITOPS-02**| ADV-ARGO-02 | Auto Deploy on `main` push | Code push to `main` branch | 1. Push a commit to `main`.<br>2. Wait for CI completion.<br>3. Check ArgoCD `dev` application. | ArgoCD automatically detects the Git change and syncs the `dev` namespace deployment. | `UNVERIFIED_RUNTIME` |
| **TC-GITOPS-03**| ADV-ARGO-03 | Staging Release Tag Deploy | Git release tag pushed (e.g. `v1.2.3`) | 1. Tag git: `git tag v1.2.3 && git push origin v1.2.3`.<br>2. Check CI pipeline and ArgoCD `staging`. | CI builds release-tagged image, pushes to Docker Hub, and ArgoCD syncs the `staging` namespace. | `UNVERIFIED_RUNTIME` |
| **TC-MESH-01** | ADV-MESH-01 | Istio Sidecar Injection | Istio enabled on namespace | 1. Deploy YAS pods into the namespace.<br>2. Run `kubectl get pods`. | Pod listings display `2/2` or `3/3` ready containers, indicating Envoy sidecar injection. | `UNVERIFIED_RUNTIME` |
| **TC-MESH-02** | ADV-MESH-02 | peer mTLS Enforcement | PeerAuthentication strict mode enabled | 1. Inspect mTLS status in Kiali or using `istioctl authn tls-check`. | All connections between microservices show locked padlock symbols (strict mTLS active). | `UNVERIFIED_RUNTIME` |
| **TC-MESH-03** | ADV-MESH-03 | Kiali Observability | Active traffic in YAS | 1. Access storefront and perform actions.<br>2. Open Kiali topology graph. | Kiali displays a complete graphical node structure showing traffic routing between YAS pods. | `UNVERIFIED_RUNTIME` |
| **TC-MESH-04** | ADV-MESH-04 ADV-MESH-08 | VirtualService HTTP 500 Retry | VirtualService retry policy applied | 1. Trigger HTTP 500 in target service (e.g. `product` or `cart`).<br>2. Monitor Envoy sidecar logs during request. | Envoy logs display retry attempts (matching retry count configured in VirtualService). | `UNVERIFIED_RUNTIME` |
| **TC-MESH-05** | ADV-MESH-05 ADV-MESH-07 | AuthorizationPolicy Allow | AuthorizationPolicy active | 1. Send request from allowed service (e.g. `storefront-bff` to `product`).<br>2. Check sidecar logs. | The request succeeds (HTTP 200), and Envoy logs record `ALLOW`. | `UNVERIFIED_RUNTIME` |
| **TC-MESH-06** | ADV-MESH-05 ADV-MESH-07 | AuthorizationPolicy Deny | AuthorizationPolicy active | 1. Exec into unpermitted pod.<br>2. Send request to protected service (e.g. `customer` to `payment`). | The request fails with HTTP 403 (Forbidden), and Envoy logs record `RBAC: denied`. | `UNVERIFIED_RUNTIME` |
| **TC-MESH-07** | ADV-MESH-06 | Internal Pod Curl Test | Pod inside cluster | 1. Execute: `kubectl exec -n dev <pod> -- curl -v http://<svc>.<ns>:<port>/`. | Standard curl output returns verbose handshake details and response payload. | `UNVERIFIED_RUNTIME` |

---

## 5. Command Templates
Use these templates during verification weeks:

### 5.1 Kubernetes Cluster Verification
```bash
# Verify cluster node status and worker IP
kubectl get nodes -o wide

# Verify deployed YAS pods and sidecars
kubectl get pods -n dev -o wide
kubectl get pods -n staging -o wide
```

### 5.2 Network and Service Mesh Checking
```bash
# Exec curl testing from inside the cluster
kubectl exec -n dev <source-pod-name> -c <source-container> -- curl -v http://<destination-service>.<namespace>.svc.cluster.local:<port>/

# Check Envoy logs for AuthorizationPolicy ALLOW/DENY
kubectl logs -n dev <pod-name> -c istio-proxy | grep -E "RBAC|allow|deny"

# Check Envoy logs for Retry execution
kubectl logs -n dev <pod-name> -c istio-proxy | grep -i "retry"
```

### 5.3 Git and Tag Verification
```bash
# Add a release tag and push to origin
git tag v1.0.0
git push origin v1.0.0
```

---

## 6. Final Acceptance Checklist
Before concluding that Project 02 is complete, verify that:
* [ ] All tests in section 4 have status **PASS**.
* [ ] No test cases remain as **TODO** or **UNVERIFIED_RUNTIME**.
* [ ] All evidence artifacts are saved in the correct paths in `evidence/`.
* [ ] The final report document has been generated in `.docx` format matching the increasingly sorted student IDs: `<MSSV1>_<MSSV2>_<MSSV3>_<MSSV4>.docx`.
* [ ] The root repository has been kept clean and original application code is unaffected.
