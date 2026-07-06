# Khoa - CI/CD Developer Build and Service Mesh

This document records Khoa's work in Project02: CI Docker image build by commit SHA, CD `developer_build`, developer environment cleanup, and Istio Service Mesh configuration.

## 1. What Was Done

### CI Docker Hub

Added workflow:

```text
.github/workflows/ci.yml
```

This workflow:

- Runs on push to any branch.
- For backend services, runs Maven package first to create `target/*.jar`.
- Builds Docker images for the main services.
- Pushes images to Docker Hub.
- Tags images with the commit SHA, e.g., `yas-tax:<commit_sha>`.
- Also tags with the sanitized branch name, e.g., `yas-tax:feature-tax`.
- If pushed to `main`, also pushes the `latest` tag.

Images built:

```text
yas-product
yas-cart
yas-order
yas-customer
yas-inventory
yas-tax
yas-media
yas-search
yas-storefront-bff
yas-storefront-ui
yas-backoffice-bff
yas-backoffice-ui
yas-sampledata
yas-swagger-ui
```

`swagger-ui/Dockerfile` was added so CI can build the `yas-swagger-ui` image according to the list of services needed for the demo.

### CD Developer Build

Added workflow:

```text
.github/workflows/cd-developer.yml
```

This workflow:

- Runs manually via `workflow_dispatch`.
- Has an `action` input with `deploy` or `cleanup`.
- Has a `developer_profile` input with `lean` or `full`.
- Deploys to namespace:

```text
yas-developer
```

- Allows entering individual branch names for each service.
- If the input branch is `main`, the workflow uses the `latest` image tag.
- If the input branch is not `main`, the workflow resolves the latest commit SHA of that branch and deploys the image with that SHA.
- Prints URL and hosts entries in the GitHub Actions summary.

`developer_profile=lean` is the default profile to avoid RAM overflow on the lab cluster:

- Disables Istio sidecar specifically for the `yas-developer` namespace.
- Reduces memory request/limit for backend, UI, and swagger-ui.
- Reduces Java heap for backend.
- Increases rollout timeout to 15 minutes.

`developer_profile=full` keeps Istio sidecar for `yas-developer`, suitable when the cluster has enough RAM or when you need to demonstrate the developer environment within the mesh.

### Important Notes on `developer_profile=lean`

`developer_profile=lean` is a workaround configuration to run CD on a lab cluster with insufficient RAM. This configuration differs from the full service-mesh version in the following ways:

- The `yas-developer` namespace is labeled `istio-injection=disabled`.
- Pods in `yas-developer` do not have `istio-proxy`, so they are typically `1/1` instead of `2/2`.
- The developer environment in this profile does not use mTLS sidecar-to-sidecar.
- This is used to demonstrate the `developer_build` pipeline: select a branch, resolve commit SHA, deploy the correct image tag, expose URL, and cleanup.

This profile does not replace the Service Mesh demonstration for the assignment. The Service Mesh portion is still demonstrated by:

- Istio installed on the cluster.
- Namespaces `yas`, `dev`, `staging` can still have `istio-injection=enabled`.
- The `yas` namespace already runs pods `2/2` with `istio-proxy`.
- `PeerAuthentication` STRICT, `DestinationRule` ISTIO_MUTUAL, and `VirtualService` retry are still in `k8s/istio/`.
- When the cluster has enough RAM, run CD with `developer_profile=full` so `yas-developer` is also in the mesh.

In short:

```text
lean = used to run CD developer_build on a cluster with insufficient RAM
full = used to demo developer environment with full Istio sidecar/mTLS
```

Reason for adding the `lean` profile: the cluster currently has only 2 nodes `s-4vcpu-8gb`, but is already running `yas`, ArgoCD, Kafka, Elasticsearch, Keycloak, Redis, Postgres, observability, ingress-nginx, and Istio. When deploying the full `yas-developer` with sidecars, nodes experience `MemoryPressure` and pods go `Pending`.

Developer domains following the assignment requirements:

```text
developer.yas.local.com
backoffice-developer.yas.local.com
api-developer.yas.local.com
```

These domains point to the Load Balancer of the ingress controller.

### Istio Service Mesh

Added directory:

```text
k8s/istio/
```

Main files:

```text
k8s/istio/peer-authentication.yaml
k8s/istio/destination-rules.yaml
k8s/istio/virtual-services.yaml
k8s/istio/public-entrypoints.yaml
k8s/istio/install-istio.ps1
k8s/istio/install-istio.sh
k8s/istio/README.md
```

Installed Istio `1.30.2` on the DOKS cluster and applied configuration:

- Namespaces `yas`, `yas-developer`, `dev`, `staging` have `istio-injection=enabled`.
- The `ingress-nginx` namespace also has injection enabled so public ingress traffic goes through Envoy.
- Pods in the `yas` namespace have restarted and have `istio-proxy`.
- `PeerAuthentication` set to `STRICT` mTLS in the main namespaces.
- `DestinationRule` uses `ISTIO_MUTUAL`.
- `VirtualService` retry for `tax` and `order`.
- Public entrypoints `storefront-bff`, `backoffice-bff`, `swagger-ui` set to `PERMISSIVE` so NGINX Ingress can still access them, while backend services still follow `STRICT` policy.

## 2. Required GitHub Secrets and Variables

Go to GitHub repository:

```text
Settings -> Secrets and variables -> Actions
```

`Secrets` tab, need:

```text
DOCKERHUB_USERNAME
DOCKERHUB_TOKEN
DIGITALOCEAN_ACCESS_TOKEN
```

`Variables` tab, need:

```text
DOKS_CLUSTER_NAME
BASE_DOMAIN
```

Current `BASE_DOMAIN` value:

```text
yas.local.com
```

`DOKS_CLUSTER_NAME` must match the actual DigitalOcean Kubernetes cluster name the workflow uses to run:

```bash
doctl kubernetes cluster kubeconfig save "$DOKS_CLUSTER_NAME"
```

## 3. How to Commit and Push

Check changed files:

```bash
git status
```

Add Khoa's files:

```bash
git add .github/workflows/ci.yml
git add .github/workflows/cd-developer.yml
git add k8s/namespaces.yaml
git add k8s/istio
git add swagger-ui/Dockerfile
git add docs/khoa-ci-cd-service-mesh.md
git add docs/context-memory.md
```

Commit:

```bash
git commit -m "feat: add developer ci cd and istio service mesh"
```

Push current branch:

```bash
git push origin feat/pipeline-cd
```

If you are on a different branch, replace `feat/pipeline-cd` with your current branch name:

```bash
git branch --show-current
git push origin <branch-name>
```

## 4. How to Test CI After Push

After pushing, GitHub Actions will automatically run:

```text
CI - Build Docker Hub Images
```

Check:

1. Go to GitHub repo.
2. Open the `Actions` tab.
3. Select the workflow `CI - Build Docker Hub Images`.
4. Select the latest run for the branch you just pushed.
5. Wait for the matrix jobs to finish building.

CI is considered complete when:

- Workflow status is green.
- All service jobs pass.
- The log shows the `Log in to Docker Hub` step.
- For backend, the log shows the `Build backend jar` step.
- The log shows the `Build and push` step.
- Docker Hub shows the commit SHA image tag.

For example, if commit SHA is:

```text
abc123...
```

then Docker Hub must have images:

```text
<DOCKERHUB_USERNAME>/yas-product:abc123...
<DOCKERHUB_USERNAME>/yas-tax:abc123...
```

If the branch is `main`, also check for the tag:

```text
latest
```

Note: CD developer uses `latest` for services set to `main`. So before running CD developer with many `main` inputs, ensure Docker Hub already has the `latest` images. The safest way is to merge/push to `main` so CI creates `latest`, or select a specific branch for the service you need to test after CI for that branch has finished.

### If CI Fails All Jobs

The first run on the `feat/pipeline-cd` branch failed at the `Build and push` step, with two error groups:

1. Backend service failed with error:

```text
lstat /target: no such file or directory
```

Cause: The backend Dockerfile copies `target/*.jar`, but the workflow initially did not run Maven package. Fixed by adding the `Build backend jar` step.

2. UI/static service failed with error:

```text
401 Unauthorized: access token has insufficient scopes
```

Cause: `DOCKERHUB_TOKEN` could log in but did not have permission to push images, or `DOCKERHUB_USERNAME` did not match the Docker Hub namespace the token was allowed to write to.

Fix on GitHub/Docker Hub:

- Go to Docker Hub and create a new Personal Access Token with `Read & Write` permissions.
- Go to GitHub `Settings -> Secrets and variables -> Actions -> Secrets`.
- Update `DOCKERHUB_TOKEN` with the new token.
- Verify `DOCKERHUB_USERNAME` is the actual Docker Hub username/namespace, not a GitHub username or email if they differ.
- After updating the secret, click `Re-run jobs` for the CI workflow.

## 5. How to Test CD developer_build

Only run CD after CI has finished building the images needed for deployment.

Important note: workflows triggered manually via `workflow_dispatch` usually only appear stably in the GitHub Actions tab after the `.github/workflows/cd-developer.yml` file is on the default branch of the repository. If you push to the `feat/pipeline-cd` branch but don't see the `CD - Developer Build` workflow, open a PR and merge this workflow into `main` first, then return to the Actions tab to run CD.

On GitHub:

1. Go to the `Actions` tab.
2. Select the workflow:

```text
CD - Developer Build
```

3. Click `Run workflow`.
4. Select the branch containing the workflow.
5. Select:

```text
action = deploy
developer_profile = lean
```

6. Enter the branch for the service you need to test.

For example, if a developer modified `tax` on branch:

```text
dev_tax_service
```

then enter:

```text
tax_branch = dev_tax_service
```

Leave other branch fields as `main`.

The workflow will:

- Resolve the latest commit SHA of `dev_tax_service`.
- Deploy `tax` using that SHA image tag.
- Deploy all other services using the `latest` tag.
- Deploy to the `yas-developer` namespace.
- With `developer_profile=lean`, deploy a RAM-efficient version to avoid `Pending` pods due to insufficient memory.
- Print test URL and hosts entries in the summary.

### If CD Fails at Rollout Due to Insufficient Memory

Symptoms:

```text
Waiting for deployment "backoffice-bff" rollout to finish: 0 of 1 updated replicas are available...
deployment "..." exceeded its progress deadline
0/2 nodes are available: Insufficient memory
node.kubernetes.io/memory-pressure:NoSchedule
```

Common causes in a lab cluster:

- Cluster has only 2 nodes `s-4vcpu-8gb`.
- Cluster is already running `yas`, ArgoCD, Kafka, Elasticsearch, Keycloak, Redis, Postgres, observability, Istio.
- CD developer deploys a second YAS instance in `yas-developer`.
- If Istio sidecar is enabled, each app pod becomes `2/2`, consuming more RAM.

How to handle:

1. Run cleanup first:

```text
action = cleanup
```

2. Re-run deploy with:

```text
action = deploy
developer_profile = lean
```

3. If still insufficient RAM, reduce the deployment scope or increase cluster nodes/RAM.

## 6. Hosts File for Testing Developer Environment

After the workflow finishes, check the summary section for the Load Balancer IP. For the current cluster, the IP is:

```text
129.212.208.194
```

Add to the hosts file on your test machine:

```text
129.212.208.194 developer.yas.local.com
129.212.208.194 backoffice-developer.yas.local.com
129.212.208.194 api-developer.yas.local.com
```

Access:

```text
http://developer.yas.local.com
http://backoffice-developer.yas.local.com
http://api-developer.yas.local.com/swagger-ui/index.html
```

## 7. How to Verify CD Developer is Complete

Run using a local kubeconfig or ask someone with cluster access to run:

```bash
kubectl get pods -n yas-developer
kubectl get deploy -n yas-developer -o wide
kubectl get ingress -n yas-developer
```

Expected results:

- Pods in `yas-developer` are `Running`.
- If Istio injection is enabled, app pods will be `2/2`.
- Services with a specific branch use the commit SHA image tag.
- Remaining services use the `latest` tag.
- Ingress has hosts `developer.yas.local.com`, `backoffice-developer.yas.local.com`, `api-developer.yas.local.com`.

Check specific image tag:

```bash
kubectl get deploy tax -n yas-developer -o jsonpath="{.spec.template.spec.containers[0].image}"
```

If `tax_branch=dev_tax_service`, output must contain the commit SHA of that branch:

```text
<DOCKERHUB_USERNAME>/yas-tax:<commit_sha>
```

## 8. How to Test Cleanup

On GitHub:

1. Go to `Actions`.
2. Select `CD - Developer Build`.
3. Click `Run workflow`.
4. Select:

```text
action = cleanup
```

After the workflow finishes, verify:

```bash
kubectl get ns yas-developer
```

If cleanup is successful, the namespace no longer exists.

## 9. How to Check Service Mesh

Check Istio system:

```bash
kubectl get pods -n istio-system
```

Expected results:

```text
istiod                       Running
istio-ingressgateway         Running
istio-egressgateway          Running
```

Check sidecars:

```bash
kubectl get pods -n yas
```

Expected results: app pods in `yas` are `2/2`.

Check policies:

```bash
kubectl get peerauthentication -A
kubectl get destinationrule -n yas
kubectl get virtualservice -n yas
```

Expected results:

- `default-strict-mtls` mode `STRICT`.
- DestinationRule has internal service host and uses `ISTIO_MUTUAL`.
- VirtualService has `tax-retry` and `order-retry`.

Check public endpoints after mesh is enabled:

```powershell
Invoke-WebRequest -Uri http://storefront.yas.local.com -UseBasicParsing -TimeoutSec 15
Invoke-WebRequest -Uri http://backoffice.yas.local.com -UseBasicParsing -TimeoutSec 15
Invoke-WebRequest -Uri http://api.yas.local.com/swagger-ui/index.html -UseBasicParsing -TimeoutSec 15
```

Expected results:

```text
StatusCode: 200
```

Check proxy sync:

```bash
istioctl proxy-status
```

Expected results: proxies in `yas` and `ingress-nginx` are synced with `istiod`.

## 10. Screenshots/Logs to Include in Report

Capture the following:

- GitHub Actions workflow `CI - Build Docker Hub Images` passing.
- Docker Hub showing the commit SHA image tag.
- GitHub Actions workflow `CD - Developer Build` passing.
- CD summary showing URLs and hosts entries.
- `kubectl get pods -n yas-developer`.
- `kubectl get deploy -n yas-developer -o wide`.
- `kubectl get pods -n yas` showing `2/2`.
- `kubectl get peerauthentication -A`.
- `kubectl get destinationrule -n yas`.
- `kubectl get virtualservice -n yas`.
- Website `developer.yas.local.com` accessible.
