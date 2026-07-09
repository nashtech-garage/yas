# Developer Build and Pipeline Validation Guide

## 1. Purpose
This guide outlines the validation flow to ensure the Continuous Integration (CI) pipeline builds and tags images correctly according to the branch commit ID, and that the Continuous Delivery (CD) job (`developer_build`) successfully deploys the selected microservice using the target image.

---

## 2. Test Branch and Safe Change Strategy
To test the CI/CD pipeline triggers and tagging without risking damage to the active production codebase:
1. **Branch Naming**: Use the specific test branch name: `project02/test-commit-id-image`.
2. **Safe Code-Only Change**: The change must be entirely non-functional.
3. **Selective Detection Bypass**: Because the existing Jenkinsfile uses a regular expression matching folder prefixes (e.g., `cart/`, `product/`) to compile only modified services, a commit outside service directories (like root docs) would trigger nothing. Therefore, the safe test change must occur within a valid microservice directory by adding a documentation-only marker file:
   `<selected-service>/PROJECT02_CI_TEST.md`
4. **Constraints**:
   * Do NOT change any application business logic (Java, JavaScript, TypeScript, HTML).
   * Do NOT modify production settings, configurations, or application properties.
   * Do NOT modify secrets or credentials.
   * Do NOT modify the `Jenkinsfile`.

---

## 3. Step-by-Step Developer Test Flow (CI Execution)

### Step 3.1: Create Test Branch and Commit Marker File
Create a new branch from `main` and write the test marker file:
```powershell
# 1. Ensure you are on main and up-to-date
git checkout main
git pull origin main

# 2. Create the test branch
git checkout -b project02/test-commit-id-image

# 3. Create a non-functional marker file in the 'cart' service folder
# (cart is selected as a sample service detected in the baseline Jenkinsfile)
echo "Project 02 CI test marker - $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" > cart/PROJECT02_CI_TEST.md

# 4. Stage and commit the change
git add cart/PROJECT02_CI_TEST.md
git commit -m "test: add project02 commit-id image validation marker"
```

### Step 3.2: Get the Commit ID
Retrieve the short commit ID (first 8 characters of the SHA) which will serve as the Docker image tag:
```bash
git rev-parse --short HEAD
```
*Actual local execution output: **`13f4c2a2`***

### Step 3.3: Push the Branch to Trigger Jenkins
Push the branch to the remote repository. 
> [!IMPORTANT]
> Do NOT push to the remote repository unless explicitly approved by the user. Ensure you have permissions before executing this command.

```bash
git push origin project02/test-commit-id-image
```

---

## 4. Pipeline Verification Steps

### 4.1 Verify Jenkins Build Log
1. Open the Jenkins dashboard and select the YAS multi-branch pipeline project.
2. Locate the branch pipeline execution for `project02/test-commit-id-image`.
3. Check the **Detect Changed Services** stage logs to verify that the `cart` service was successfully detected as changed.
4. Verify that the Docker build step uses the tag format `cart:<COMMIT_ID>` (e.g., `cart:13f4c2a2`).
5. Verify that the Docker push stage successfully logs in and pushes to Docker Hub.
*Expected Jenkins log path: `evidence/jenkins/week2-developer-test-flow.md` (logs/screenshots)*

### 4.2 Verify Docker Hub Tag
1. Open Docker Hub in your browser.
2. Navigate to your project namespace repository (e.g., `<your-namespace>/cart`).
3. Under the **Tags** tab, confirm that a tag matching your exact commit ID (e.g., `13f4c2a2`) exists and was recently pushed.
*Expected Docker Hub evidence path: `evidence/dockerhub/week2-commit-id-image.md`*

---

## 5. Running the `developer_build` CD Pipeline

Once the CI build completes, trigger the CD pipeline to deploy the service.

### 5.1 Parameter Inputs
1. Open the Jenkins dashboard and click on the `developer_build` pipeline job.
2. Select **Build with Parameters** and input:
   * **`SERVICE_NAME`**: `cart`
   * **`BRANCH_NAME`**: `project02/test-commit-id-image`
   * **`IMAGE_TAG`** (expected commit ID): `13f4c2a2` (retrieved in Step 3.2)
   * **`NAMESPACE`**: `dev`
3. Click **Build**.

---

## 6. Verifying Deployment in Kubernetes

Once the `developer_build` job logs indicate success, run the following verification steps on the K8s cluster:

### 6.1 Check Selected Service Deployed Image Tag
Confirm that the selected service (`cart`) deployment was updated to use the new Docker Hub image tagged with the commit ID:
```bash
kubectl get deploy -n dev cart -o yaml | grep image:
```
*Expected output: `image: <dockerhub-username>/cart:13f4c2a2`*

### 6.2 Check Non-Selected Services Tags
Verify that all other backend services were unaffected or remained on their default `main` or `latest` tag:
```bash
kubectl get deploy -n dev -o jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.spec.template.spec.containers[*].image}{"\n"}{end}'
```
*Expected outcome: The `cart` service uses the tag `13f4c2a2`, while other services (e.g., `product`, `customer`) show `main` or `latest` tags.*

### 6.3 Verify NodePort Connectivity
Identify the port and hit the URL:
```bash
# Retrieve service and NodePort
kubectl get svc -n dev

# Perform validation request via curl
curl -v http://yas-dev.local:<NODE_PORT>
```

---

## 7. Pass/Fail Criteria

A test run is considered **PASS** if and only if all the following conditions are met:
1. The Jenkins branch pipeline is triggered by the push to `project02/test-commit-id-image`.
2. The Docker image for the modified service is successfully compiled and tagged with the commit ID.
3. The image is successfully pushed and visible in the Docker Hub registry.
4. The `developer_build` parameters are accepted, and execution updates the `cart` deployment.
5. The `cart` deployment container image uses the tag matching the commit ID.
6. Non-selected deployments retain `main` or `latest` tags.
7. Access to the updated service via NodePort returns a valid HTTP response (e.g., status 200).

A run is marked **FAIL** if any step errors out, authentication fails, or tags mismatches.

---

## 8. Runtime Verification Status (UNVERIFIED_RUNTIME)
Because active CI/CD infrastructure is not running in this environment, this test plan execution is marked as `UNVERIFIED_RUNTIME`.
Exact verification commands can be run once the runtime environment is active:
* To check local commit ID: `git rev-parse --short HEAD`
* To check K8s deployment: `kubectl get deploy -n dev <deployment-name> -o yaml | grep image:`
* To check K8s node IPs: `kubectl get nodes -o wide`
* To check NodePorts: `kubectl get svc -n dev`

---

## 9. Evidence Checklist
Verify the following logs/screenshots are populated inside the `evidence/` directory:
* [ ] **Branch CI Build Logs**: Stored under `evidence/jenkins/week2-developer-test-flow.md`
* [ ] **Docker Hub Tag Screenshot**: Stored under `evidence/dockerhub/week2-commit-id-image.md`
* [ ] **K8s Deploy Verification Logs**: Stored under `evidence/k8s/week2-nodeport-access.md`
