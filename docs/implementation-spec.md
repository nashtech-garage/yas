# YAS: Yet Another Shop - CI/CD Implementation Specification

> **Target**: Coding agent full implementation guide  
> **Stack**: GitHub Actions, DigitalOcean Kubernetes (DOKS), Helm, NGINX Ingress, Istio, Argo CD  
> **Source repo**: https://github.com/nashtech-garage/yas

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture and Services](#2-architecture-and-services)
3. [Prerequisites and Cloud Setup](#3-prerequisites-and-cloud-setup)
4. [Repository Structure to Create](#4-repository-structure-to-create)
5. [Phase 1 - DigitalOcean Kubernetes Cluster](#5-phase-1---digitalocean-kubernetes-cluster)
6. [Phase 2 - CI Pipeline](#6-phase-2---ci-pipeline)
7. [Phase 3 - CD Pipeline: developer_build Job](#7-phase-3---cd-pipeline-developer_build-job)
8. [Phase 4 - CD Pipeline: dev and staging Environments](#8-phase-4---cd-pipeline-dev-and-staging-environments)
9. [Phase 5 - Argo CD](#9-phase-5---argo-cd)
10. [Phase 6 - Service Mesh with Istio](#10-phase-6---service-mesh-with-istio)
11. [Helm Chart Requirements](#11-helm-chart-requirements)
12. [Kubernetes Manifests Reference](#12-kubernetes-manifests-reference)
13. [Deliverables Checklist](#13-deliverables-checklist)
14. [Quick Start Command Sequence](#14-quick-start-command-sequence)

---

## 1. Project Overview

YAS (Yet Another Shop) is a Java-based microservice e-commerce application. The goal is to build a complete CI/CD and deployment system that can:

- Build Docker images on every commit and push them to Docker Hub.
- Deploy a selected feature-branch image for developer testing while all other services use `main` or `latest`.
- Deploy automatically to the `yas-dev` namespace when `main` changes.
- Deploy to the `yas-staging` namespace when a semantic version tag is pushed.
- Use DigitalOcean Kubernetes (DOKS) instead of Minikube.
- Expose developer, dev, and staging environments using a DigitalOcean Load Balancer through an ingress controller.
- Optionally use Argo CD for GitOps deployment.
- Optionally enforce mTLS, authorization policies, and retry behavior using Istio and Kiali.

DigitalOcean Kubernetes provides a managed Kubernetes control plane. In this project, the "master node" is represented by the managed DOKS control plane, and the "worker node" requirement is satisfied by a DOKS node pool. Use at least two worker nodes for this microservice workload unless budget constraints require one node.

---

## 2. Architecture and Services

### 2.1 Application Services

`sampledata` runs once to seed the database, then can be disabled.

| # | Service Name | Type | Description | Dependencies |
|---|-------------|------|-------------|-------------|
| 1 | `product` | Spring Boot | Product catalog | PostgreSQL |
| 2 | `cart` | Spring Boot | Shopping cart | PostgreSQL |
| 3 | `order` | Spring Boot | Order processing | cart, inventory, tax |
| 4 | `customer` | Spring Boot | Customer data | PostgreSQL |
| 5 | `inventory` | Spring Boot | Stock management | PostgreSQL |
| 6 | `tax` | Spring Boot | Tax calculation | PostgreSQL |
| 7 | `media` | Spring Boot | Product image upload | PostgreSQL |
| 8 | `search` | Spring Boot | Product search | Elasticsearch |
| 9 | `storefront-bff` | Spring Cloud Gateway | BFF for storefront UI | Keycloak |
| 10 | `storefront-ui` | Next.js | Customer-facing UI | storefront-bff |
| 11 | `backoffice-bff` | Spring Cloud Gateway | BFF for admin UI | Keycloak |
| 12 | `backoffice-ui` | Next.js | Admin UI | backoffice-bff |
| 13 | `swagger-ui` | Static | API docs | all APIs |
| 14 | `sampledata` | Spring Boot job | Database seeder | all services |

### 2.2 Infrastructure Services

| Service | Purpose |
|---------|---------|
| PostgreSQL | Primary database. For the assignment, one in-cluster instance is acceptable. |
| Keycloak | Identity provider and OAuth2/OIDC server. |
| Kafka | Event streaming. |
| Kafka Connect and Debezium | Change data capture for search indexing. |
| Elasticsearch | Full-text search backend. |
| NGINX Ingress Controller | Public HTTP entry point. Exposed by DigitalOcean Load Balancer. |
| DigitalOcean Load Balancer | Cloud load balancer automatically provisioned for the ingress controller. |

### 2.3 Network Flow

```text
Browser
  -> DigitalOcean Load Balancer
  -> NGINX Ingress Controller
     -> yas-dev / yas-staging / yas-developer namespace
        -> storefront-ui -> storefront-bff -> product, cart, order, customer, search, media
        -> backoffice-ui -> backoffice-bff -> business services
        -> swagger-ui
        -> Keycloak
```

Use unique hostnames per environment. If the team does not own a public domain, use local hosts-file entries pointing to the Load Balancer external IP.

Example:

```text
<LOAD_BALANCER_IP>  developer.yas.local
<LOAD_BALANCER_IP>  dev.yas.local
<LOAD_BALANCER_IP>  staging.yas.local
<LOAD_BALANCER_IP>  keycloak.dev.yas.local
```

---

## 3. Prerequisites and Cloud Setup

### 3.1 Required Local Tools

Install these tools on the implementation machine:

```bash
# DigitalOcean CLI
curl -sL https://github.com/digitalocean/doctl/releases/latest/download/doctl-1.162.0-linux-amd64.tar.gz | tar -xzv
sudo mv doctl /usr/local/bin

# kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install kubectl /usr/local/bin/kubectl

# Helm
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

# Argo CD CLI, required only for the advanced GitOps phase
curl -sSL -o argocd https://github.com/argoproj/argo-cd/releases/latest/download/argocd-linux-amd64
sudo install argocd /usr/local/bin/argocd

# Istio CLI, required only for the service mesh phase
curl -L https://istio.io/downloadIstio | sh -
sudo mv istio-*/bin/istioctl /usr/local/bin/istioctl
```

Authenticate `doctl`:

```bash
doctl auth init
doctl account get
```

### 3.2 DigitalOcean Account Requirements

Create or prepare:

- A DigitalOcean personal access token with access to Kubernetes, Load Balancers, and Droplets.
- A DOKS-supported region, for example `sgp1`, `nyc1`, or another team-approved region.
- A Docker Hub account and Docker Hub access token.
- Optional: a real DNS zone for the project. If unavailable, use hosts-file entries.

### 3.3 Recommended DOKS Sizing

The YAS workload contains many services plus PostgreSQL, Kafka, Elasticsearch, and Keycloak. Use the following default for the implementation:

```text
Cluster name: yas-doks
Region: sgp1
Kubernetes version: latest supported DOKS patch
Node pool name: yas-workers
Node size: s-4vcpu-8gb
Node count: 2
Autoscaling: optional, min 2, max 3
High availability control plane: enabled when available and budget allows
```

For a lower-cost demo, `s-2vcpu-4gb` can be used, but the coding agent must reduce replicas and resource requests in Helm values. Do not use tiny node sizes for the full YAS stack with Kafka and Elasticsearch.

### 3.4 GitHub Secrets and Variables

Navigate to GitHub repository `Settings -> Secrets and variables -> Actions` and add:

| Name | Type | Purpose |
|------|------|---------|
| `DOCKERHUB_USERNAME` | Secret | Docker Hub username. |
| `DOCKERHUB_TOKEN` | Secret | Docker Hub token used by CI to push images. |
| `DIGITALOCEAN_ACCESS_TOKEN` | Secret | DigitalOcean personal access token used by GitHub Actions. |
| `DOKS_CLUSTER_NAME` | Variable | Default: `yas-doks`. |
| `DOKS_REGION` | Variable | Default: `sgp1` or the selected region. |
| `BASE_DOMAIN` | Variable | Example: `yas.local` or a real domain such as `yas.example.com`. |

Do not store a static kubeconfig as the primary authentication method. In GitHub Actions, install `doctl` and run:

```bash
doctl kubernetes cluster kubeconfig save "$DOKS_CLUSTER_NAME"
```

This creates a kubeconfig dynamically from the DigitalOcean token.

---

## 4. Repository Structure to Create

Create the following structure in the YAS fork:

```text
yas/
|-- .github/
|   `-- workflows/
|       |-- ci.yml
|       |-- cd-developer.yml
|       |-- cd-dev.yml
|       `-- cd-staging.yml
|-- helm/
|   `-- yas/
|       |-- Chart.yaml
|       |-- values.yaml
|       |-- values-dev.yaml
|       |-- values-staging.yaml
|       |-- values-developer.yaml
|       `-- templates/
|           |-- _helpers.tpl
|           |-- namespace.yaml
|           |-- ingress.yaml
|           |-- serviceaccounts.yaml
|           |-- secrets.yaml
|           |-- configmap.yaml
|           |-- services/
|           `-- jobs/
|-- k8s/
|   |-- namespaces.yaml
|   |-- ingress-nginx-values.yaml
|   |-- argocd/
|   |   |-- app-dev.yaml
|   |   `-- app-staging.yaml
|   `-- istio/
|       |-- peer-authentication.yaml
|       |-- destination-rules.yaml
|       |-- virtual-services.yaml
|       `-- authorization-policies.yaml
|-- scripts/
|   |-- create-doks-cluster.sh
|   |-- bootstrap-doks.sh
|   |-- install-ingress-nginx.sh
|   |-- install-argocd.sh
|   |-- install-istio.sh
|   `-- destroy-developer-env.sh
`-- README.md
```

---

## 5. Phase 1 - DigitalOcean Kubernetes Cluster

### 5.1 Create the Cluster

Create `scripts/create-doks-cluster.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail

CLUSTER_NAME="${DOKS_CLUSTER_NAME:-yas-doks}"
REGION="${DOKS_REGION:-sgp1}"
NODE_SIZE="${DOKS_NODE_SIZE:-s-4vcpu-8gb}"
NODE_COUNT="${DOKS_NODE_COUNT:-2}"
NODE_POOL_NAME="${DOKS_NODE_POOL_NAME:-yas-workers}"

echo "Creating DOKS cluster: ${CLUSTER_NAME}"

doctl kubernetes cluster create "${CLUSTER_NAME}" \
  --region "${REGION}" \
  --version latest \
  --node-pool "name=${NODE_POOL_NAME};size=${NODE_SIZE};count=${NODE_COUNT};tag=yas;label=app=yas" \
  --auto-upgrade=true \
  --maintenance-window "sunday=18:00"

doctl kubernetes cluster kubeconfig save "${CLUSTER_NAME}"
kubectl get nodes -o wide
```

Notes for the coding agent:

- DOKS automatically manages the Kubernetes control plane.
- `doctl kubernetes cluster create` adds the new cluster context to local `kubectl`.
- Use `doctl kubernetes options regions`, `doctl kubernetes options sizes`, and `doctl kubernetes options versions` if the configured region, size, or version is unavailable.
- If the team needs a stricter one-worker-node demo, set `DOKS_NODE_COUNT=1`, but expect limited reliability and scheduling pressure.

### 5.2 Namespaces

Create `k8s/namespaces.yaml`:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: yas-dev
  labels:
    istio-injection: enabled
---
apiVersion: v1
kind: Namespace
metadata:
  name: yas-staging
  labels:
    istio-injection: enabled
---
apiVersion: v1
kind: Namespace
metadata:
  name: yas-developer
  labels:
    istio-injection: enabled
```

Apply:

```bash
kubectl apply -f k8s/namespaces.yaml
```

### 5.3 Install NGINX Ingress Controller

Create `k8s/ingress-nginx-values.yaml`:

```yaml
controller:
  replicaCount: 2
  service:
    type: LoadBalancer
    annotations:
      service.beta.kubernetes.io/do-loadbalancer-name: yas-ingress
      service.beta.kubernetes.io/do-loadbalancer-size-unit: "1"
  admissionWebhooks:
    enabled: true
```

Create `scripts/install-ingress-nginx.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace \
  --values k8s/ingress-nginx-values.yaml \
  --timeout 10m0s

kubectl rollout status deployment/ingress-nginx-controller -n ingress-nginx --timeout=5m
kubectl get svc ingress-nginx-controller -n ingress-nginx
```

After the service receives an external IP, configure DNS or hosts-file entries:

```bash
LOAD_BALANCER_IP=$(kubectl get svc ingress-nginx-controller -n ingress-nginx -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
echo "${LOAD_BALANCER_IP} developer.${BASE_DOMAIN:-yas.local}"
echo "${LOAD_BALANCER_IP} dev.${BASE_DOMAIN:-yas.local}"
echo "${LOAD_BALANCER_IP} staging.${BASE_DOMAIN:-yas.local}"
```

### 5.4 Bootstrap Infrastructure Services

Create `scripts/bootstrap-doks.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="${1:-yas-dev}"

helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add elastic https://helm.elastic.co
helm repo update

kubectl create namespace "${NAMESPACE}" --dry-run=client -o yaml | kubectl apply -f -

helm upgrade --install postgresql bitnami/postgresql \
  --namespace "${NAMESPACE}" \
  --set auth.postgresPassword=postgres \
  --set auth.database=yas \
  --set primary.persistence.size=10Gi \
  --set primary.resources.requests.cpu=250m \
  --set primary.resources.requests.memory=512Mi

helm upgrade --install keycloak bitnami/keycloak \
  --namespace "${NAMESPACE}" \
  --set auth.adminUser=admin \
  --set auth.adminPassword=admin \
  --set postgresql.enabled=false \
  --set externalDatabase.host=postgresql \
  --set externalDatabase.database=keycloak \
  --set externalDatabase.user=postgres \
  --set externalDatabase.password=postgres \
  --set resources.requests.cpu=250m \
  --set resources.requests.memory=512Mi

helm upgrade --install kafka bitnami/kafka \
  --namespace "${NAMESPACE}" \
  --set controller.replicaCount=1 \
  --set broker.replicaCount=1 \
  --set kraft.enabled=true \
  --set persistence.size=10Gi

helm upgrade --install elasticsearch elastic/elasticsearch \
  --namespace "${NAMESPACE}" \
  --set replicas=1 \
  --set minimumMasterNodes=1 \
  --set resources.requests.cpu=250m \
  --set resources.requests.memory=1Gi \
  --set resources.limits.memory=2Gi \
  --set volumeClaimTemplate.resources.requests.storage=10Gi

kubectl get pods -n "${NAMESPACE}"
```

The Helm chart may also manage these dependencies using `Chart.yaml` dependencies. If dependencies are embedded in the application Helm chart, keep `bootstrap-doks.sh` focused on cluster-level setup and run infrastructure through the chart.

---

## 6. Phase 2 - CI Pipeline

Requirement: on every commit to every branch, build Docker images tagged with the commit SHA and push them to Docker Hub. For `main`, also publish `latest`.

Create `.github/workflows/ci.yml`:

```yaml
name: CI - Build and Push Images

on:
  push:
    branches:
      - "**"
    tags-ignore:
      - "v*"
  pull_request:
    branches:
      - main

env:
  DOCKERHUB_USERNAME: ${{ secrets.DOCKERHUB_USERNAME }}

jobs:
  build-and-push:
    runs-on: ubuntu-latest
    strategy:
      fail-fast: false
      matrix:
        service:
          - product
          - cart
          - order
          - customer
          - inventory
          - tax
          - media
          - search
          - storefront-bff
          - storefront-ui
          - backoffice-bff
          - backoffice-ui
          - swagger-ui

    steps:
      - uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Log in to Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Prepare image tags
        id: tags
        shell: bash
        run: |
          IMAGE="${{ secrets.DOCKERHUB_USERNAME }}/yas-${{ matrix.service }}"
          TAGS="${IMAGE}:${{ github.sha }}"
          SAFE_BRANCH="$(echo "${{ github.ref_name }}" | tr '/_' '--' | tr -cd '[:alnum:].-')"
          TAGS="${TAGS},${IMAGE}:${SAFE_BRANCH}"
          if [ "${{ github.ref_name }}" = "main" ]; then
            TAGS="${TAGS},${IMAGE}:latest"
          fi
          echo "tags=${TAGS}" >> "$GITHUB_OUTPUT"

      - name: Build and push image
        uses: docker/build-push-action@v6
        with:
          context: ./${{ matrix.service }}
          push: true
          tags: ${{ steps.tags.outputs.tags }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
```

Implementation notes:

- If the actual YAS repository has different Dockerfile paths, the coding agent must inspect the repository and adjust each service context.
- The assignment requires branch images to use the last commit ID. The SHA tag is the source of truth. Branch-name tags are convenience tags only.
- If some services are not changed by a commit, it is acceptable to optimize later using path filters, but the first implementation should build all required services for reliability.

---

## 7. Phase 3 - CD Pipeline: developer_build Job

Requirement: the developer manually enters a branch for one or more services. The selected service uses the image built from that branch commit. All other services use `latest`. After deployment, the workflow prints the public domain and port/path for testing.

On DOKS, prefer Ingress and a DigitalOcean Load Balancer instead of NodePort. The public test endpoint is:

```text
http://developer.<BASE_DOMAIN>/
```

If no real DNS is available, print the Load Balancer IP and hosts-file entry.

Create `.github/workflows/cd-developer.yml`:

```yaml
name: CD - Developer Build

on:
  workflow_dispatch:
    inputs:
      action:
        description: "deploy or cleanup"
        required: true
        default: "deploy"
        type: choice
        options:
          - deploy
          - cleanup
      product_branch:
        description: "Branch for product service"
        required: false
        default: "main"
      cart_branch:
        description: "Branch for cart service"
        required: false
        default: "main"
      order_branch:
        description: "Branch for order service"
        required: false
        default: "main"
      customer_branch:
        description: "Branch for customer service"
        required: false
        default: "main"
      inventory_branch:
        description: "Branch for inventory service"
        required: false
        default: "main"
      tax_branch:
        description: "Branch for tax service"
        required: false
        default: "main"
      media_branch:
        description: "Branch for media service"
        required: false
        default: "main"
      search_branch:
        description: "Branch for search service"
        required: false
        default: "main"
      storefront_bff_branch:
        description: "Branch for storefront-bff service"
        required: false
        default: "main"
      storefront_ui_branch:
        description: "Branch for storefront-ui service"
        required: false
        default: "main"
      backoffice_bff_branch:
        description: "Branch for backoffice-bff service"
        required: false
        default: "main"
      backoffice_ui_branch:
        description: "Branch for backoffice-ui service"
        required: false
        default: "main"

jobs:
  resolve-tags:
    if: ${{ github.event.inputs.action == 'deploy' }}
    runs-on: ubuntu-latest
    outputs:
      product_tag: ${{ steps.resolve.outputs.product_tag }}
      cart_tag: ${{ steps.resolve.outputs.cart_tag }}
      order_tag: ${{ steps.resolve.outputs.order_tag }}
      customer_tag: ${{ steps.resolve.outputs.customer_tag }}
      inventory_tag: ${{ steps.resolve.outputs.inventory_tag }}
      tax_tag: ${{ steps.resolve.outputs.tax_tag }}
      media_tag: ${{ steps.resolve.outputs.media_tag }}
      search_tag: ${{ steps.resolve.outputs.search_tag }}
      storefront_bff_tag: ${{ steps.resolve.outputs.storefront_bff_tag }}
      storefront_ui_tag: ${{ steps.resolve.outputs.storefront_ui_tag }}
      backoffice_bff_tag: ${{ steps.resolve.outputs.backoffice_bff_tag }}
      backoffice_ui_tag: ${{ steps.resolve.outputs.backoffice_ui_tag }}
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Resolve branch commit tags
        id: resolve
        shell: bash
        run: |
          resolve_tag() {
            local branch="$1"
            if [ -z "$branch" ] || [ "$branch" = "main" ]; then
              echo "latest"
              return
            fi
            git fetch origin "$branch" --depth=1
            git rev-parse "origin/$branch"
          }

          echo "product_tag=$(resolve_tag '${{ inputs.product_branch }}')" >> "$GITHUB_OUTPUT"
          echo "cart_tag=$(resolve_tag '${{ inputs.cart_branch }}')" >> "$GITHUB_OUTPUT"
          echo "order_tag=$(resolve_tag '${{ inputs.order_branch }}')" >> "$GITHUB_OUTPUT"
          echo "customer_tag=$(resolve_tag '${{ inputs.customer_branch }}')" >> "$GITHUB_OUTPUT"
          echo "inventory_tag=$(resolve_tag '${{ inputs.inventory_branch }}')" >> "$GITHUB_OUTPUT"
          echo "tax_tag=$(resolve_tag '${{ inputs.tax_branch }}')" >> "$GITHUB_OUTPUT"
          echo "media_tag=$(resolve_tag '${{ inputs.media_branch }}')" >> "$GITHUB_OUTPUT"
          echo "search_tag=$(resolve_tag '${{ inputs.search_branch }}')" >> "$GITHUB_OUTPUT"
          echo "storefront_bff_tag=$(resolve_tag '${{ inputs.storefront_bff_branch }}')" >> "$GITHUB_OUTPUT"
          echo "storefront_ui_tag=$(resolve_tag '${{ inputs.storefront_ui_branch }}')" >> "$GITHUB_OUTPUT"
          echo "backoffice_bff_tag=$(resolve_tag '${{ inputs.backoffice_bff_branch }}')" >> "$GITHUB_OUTPUT"
          echo "backoffice_ui_tag=$(resolve_tag '${{ inputs.backoffice_ui_branch }}')" >> "$GITHUB_OUTPUT"

  deploy:
    if: ${{ github.event.inputs.action == 'deploy' }}
    needs: resolve-tags
    runs-on: ubuntu-latest
    environment: developer
    steps:
      - uses: actions/checkout@v4

      - name: Install doctl
        uses: digitalocean/action-doctl@v2
        with:
          token: ${{ secrets.DIGITALOCEAN_ACCESS_TOKEN }}

      - name: Set up kubectl
        uses: azure/setup-kubectl@v4

      - name: Set up Helm
        uses: azure/setup-helm@v4

      - name: Configure DOKS kubeconfig
        run: doctl kubernetes cluster kubeconfig save "${{ vars.DOKS_CLUSTER_NAME }}"

      - name: Deploy developer environment
        run: |
          helm upgrade --install yas-developer ./helm/yas \
            --namespace yas-developer \
            --create-namespace \
            --values ./helm/yas/values.yaml \
            --values ./helm/yas/values-developer.yaml \
            --set global.dockerhubUsername=${{ secrets.DOCKERHUB_USERNAME }} \
            --set global.baseDomain=${{ vars.BASE_DOMAIN }} \
            --set ingress.host=developer.${{ vars.BASE_DOMAIN }} \
            --set services.product.tag=${{ needs.resolve-tags.outputs.product_tag }} \
            --set services.cart.tag=${{ needs.resolve-tags.outputs.cart_tag }} \
            --set services.order.tag=${{ needs.resolve-tags.outputs.order_tag }} \
            --set services.customer.tag=${{ needs.resolve-tags.outputs.customer_tag }} \
            --set services.inventory.tag=${{ needs.resolve-tags.outputs.inventory_tag }} \
            --set services.tax.tag=${{ needs.resolve-tags.outputs.tax_tag }} \
            --set services.media.tag=${{ needs.resolve-tags.outputs.media_tag }} \
            --set services.search.tag=${{ needs.resolve-tags.outputs.search_tag }} \
            --set services.storefront-bff.tag=${{ needs.resolve-tags.outputs.storefront_bff_tag }} \
            --set services.storefront-ui.tag=${{ needs.resolve-tags.outputs.storefront_ui_tag }} \
            --set services.backoffice-bff.tag=${{ needs.resolve-tags.outputs.backoffice_bff_tag }} \
            --set services.backoffice-ui.tag=${{ needs.resolve-tags.outputs.backoffice_ui_tag }} \
            --timeout 15m0s

      - name: Verify rollout
        run: kubectl rollout status deployment -n yas-developer --timeout=10m

      - name: Print access information
        shell: bash
        run: |
          LB_IP="$(kubectl get svc ingress-nginx-controller -n ingress-nginx -o jsonpath='{.status.loadBalancer.ingress[0].ip}')"
          echo "Developer environment deployed."
          echo "URL: http://developer.${{ vars.BASE_DOMAIN }}/"
          echo "If DNS is not configured, add this hosts entry:"
          echo "${LB_IP} developer.${{ vars.BASE_DOMAIN }}"

  cleanup:
    if: ${{ github.event.inputs.action == 'cleanup' }}
    runs-on: ubuntu-latest
    environment: developer
    steps:
      - name: Install doctl
        uses: digitalocean/action-doctl@v2
        with:
          token: ${{ secrets.DIGITALOCEAN_ACCESS_TOKEN }}

      - name: Set up Helm
        uses: azure/setup-helm@v4

      - name: Configure DOKS kubeconfig
        run: doctl kubernetes cluster kubeconfig save "${{ vars.DOKS_CLUSTER_NAME }}"

      - name: Delete developer deployment
        run: |
          helm uninstall yas-developer --namespace yas-developer || true
          kubectl delete namespace yas-developer || true
          echo "Developer deployment cleaned up."
```

---

## 8. Phase 4 - CD Pipeline: dev and staging Environments

### 8.1 Dev Environment

Create `.github/workflows/cd-dev.yml`:

```yaml
name: CD - Deploy to Dev

on:
  push:
    branches:
      - main

jobs:
  deploy-dev:
    runs-on: ubuntu-latest
    environment: dev
    steps:
      - uses: actions/checkout@v4

      - name: Install doctl
        uses: digitalocean/action-doctl@v2
        with:
          token: ${{ secrets.DIGITALOCEAN_ACCESS_TOKEN }}

      - name: Set up Helm
        uses: azure/setup-helm@v4

      - name: Configure DOKS kubeconfig
        run: doctl kubernetes cluster kubeconfig save "${{ vars.DOKS_CLUSTER_NAME }}"

      - name: Deploy to dev namespace
        run: |
          helm upgrade --install yas-dev ./helm/yas \
            --namespace yas-dev \
            --create-namespace \
            --values ./helm/yas/values.yaml \
            --values ./helm/yas/values-dev.yaml \
            --set global.dockerhubUsername=${{ secrets.DOCKERHUB_USERNAME }} \
            --set global.imageTag=${{ github.sha }} \
            --set global.baseDomain=${{ vars.BASE_DOMAIN }} \
            --set ingress.host=dev.${{ vars.BASE_DOMAIN }} \
            --timeout 15m0s

      - name: Verify deployment
        run: kubectl rollout status deployment -n yas-dev --timeout=10m
```

### 8.2 Staging Environment

Create `.github/workflows/cd-staging.yml`:

```yaml
name: CD - Deploy to Staging

on:
  push:
    tags:
      - "v[0-9]+.[0-9]+.[0-9]+"

jobs:
  build-release-images:
    runs-on: ubuntu-latest
    strategy:
      fail-fast: false
      matrix:
        service:
          - product
          - cart
          - order
          - customer
          - inventory
          - tax
          - media
          - search
          - storefront-bff
          - storefront-ui
          - backoffice-bff
          - backoffice-ui
          - swagger-ui
    steps:
      - uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Log in to Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Extract tag version
        id: version
        run: echo "VERSION=${GITHUB_REF#refs/tags/}" >> "$GITHUB_OUTPUT"

      - name: Build and push release image
        uses: docker/build-push-action@v6
        with:
          context: ./${{ matrix.service }}
          push: true
          tags: |
            ${{ secrets.DOCKERHUB_USERNAME }}/yas-${{ matrix.service }}:${{ steps.version.outputs.VERSION }}
            ${{ secrets.DOCKERHUB_USERNAME }}/yas-${{ matrix.service }}:latest

  deploy-staging:
    needs: build-release-images
    runs-on: ubuntu-latest
    environment: staging
    steps:
      - uses: actions/checkout@v4

      - name: Install doctl
        uses: digitalocean/action-doctl@v2
        with:
          token: ${{ secrets.DIGITALOCEAN_ACCESS_TOKEN }}

      - name: Set up Helm
        uses: azure/setup-helm@v4

      - name: Configure DOKS kubeconfig
        run: doctl kubernetes cluster kubeconfig save "${{ vars.DOKS_CLUSTER_NAME }}"

      - name: Extract tag version
        id: version
        run: echo "VERSION=${GITHUB_REF#refs/tags/}" >> "$GITHUB_OUTPUT"

      - name: Deploy to staging namespace
        run: |
          helm upgrade --install yas-staging ./helm/yas \
            --namespace yas-staging \
            --create-namespace \
            --values ./helm/yas/values.yaml \
            --values ./helm/yas/values-staging.yaml \
            --set global.dockerhubUsername=${{ secrets.DOCKERHUB_USERNAME }} \
            --set global.imageTag=${{ steps.version.outputs.VERSION }} \
            --set global.baseDomain=${{ vars.BASE_DOMAIN }} \
            --set ingress.host=staging.${{ vars.BASE_DOMAIN }} \
            --timeout 15m0s

      - name: Verify deployment
        run: kubectl rollout status deployment -n yas-staging --timeout=10m
```

---

## 9. Phase 5 - Argo CD

### 9.1 Install Argo CD on DOKS

Create `scripts/install-argocd.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail

kubectl create namespace argocd --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
kubectl rollout status deployment/argocd-server -n argocd --timeout=5m

kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "ClusterIP"}}'

echo "Initial admin password:"
kubectl -n argocd get secret argocd-initial-admin-secret \
  -o jsonpath="{.data.password}" | base64 -d && echo
```

Expose Argo CD through the existing ingress controller instead of a NodePort. Create an optional ingress manifest:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: argocd
  namespace: argocd
  annotations:
    nginx.ingress.kubernetes.io/backend-protocol: "HTTPS"
spec:
  ingressClassName: nginx
  rules:
    - host: argocd.yas.local
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: argocd-server
                port:
                  number: 443
```

### 9.2 Argo CD Application - Dev

Create `k8s/argocd/app-dev.yaml`:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: yas-dev
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://github.com/<YOUR_USERNAME>/yas.git
    targetRevision: main
    path: helm/yas
    helm:
      valueFiles:
        - values.yaml
        - values-dev.yaml
      parameters:
        - name: ingress.host
          value: dev.<BASE_DOMAIN>
  destination:
    server: https://kubernetes.default.svc
    namespace: yas-dev
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
    syncOptions:
      - CreateNamespace=true
```

### 9.3 Argo CD Application - Staging

Create `k8s/argocd/app-staging.yaml`:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: yas-staging
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://github.com/<YOUR_USERNAME>/yas.git
    targetRevision: main
    path: helm/yas
    helm:
      valueFiles:
        - values.yaml
        - values-staging.yaml
      parameters:
        - name: ingress.host
          value: staging.<BASE_DOMAIN>
  destination:
    server: https://kubernetes.default.svc
    namespace: yas-staging
  syncPolicy:
    automated:
      prune: true
      selfHeal: false
    syncOptions:
      - CreateNamespace=true
```

### 9.4 GitOps Workflow Adjustment

If implementing Argo CD, change `cd-dev.yml` and `cd-staging.yml` so they update Helm values in Git instead of running `helm upgrade` directly.

Example:

```yaml
- name: Update dev image tag
  run: |
    yq -i '.global.imageTag = "${{ github.sha }}"' helm/yas/values-dev.yaml
    git config user.email "ci@github.com"
    git config user.name "GitHub Actions"
    git add helm/yas/values-dev.yaml
    git commit -m "ci: update dev image tag to ${{ github.sha }}"
    git push
```

Argo CD will detect the commit and sync it to DOKS.

---

## 10. Phase 6 - Service Mesh with Istio

### 10.1 Install Istio on DOKS

Create `scripts/install-istio.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail

istioctl install --set profile=demo -y

kubectl label namespace yas-dev istio-injection=enabled --overwrite
kubectl label namespace yas-staging istio-injection=enabled --overwrite
kubectl label namespace yas-developer istio-injection=enabled --overwrite

kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.27/samples/addons/kiali.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.27/samples/addons/prometheus.yaml

kubectl rollout status deployment/istiod -n istio-system --timeout=5m
kubectl rollout status deployment/kiali -n istio-system --timeout=5m
```

Use port-forward for Kiali during demos:

```bash
istioctl dashboard kiali
```

### 10.2 Strict mTLS

Create `k8s/istio/peer-authentication.yaml`:

```yaml
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default-mtls
  namespace: yas-dev
spec:
  mtls:
    mode: STRICT
---
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default-mtls
  namespace: yas-staging
spec:
  mtls:
    mode: STRICT
```

### 10.3 Destination Rules

Create `k8s/istio/destination-rules.yaml`:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: yas-dev-mtls-all
  namespace: yas-dev
spec:
  host: "*.yas-dev.svc.cluster.local"
  trafficPolicy:
    tls:
      mode: ISTIO_MUTUAL
---
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: yas-staging-mtls-all
  namespace: yas-staging
spec:
  host: "*.yas-staging.svc.cluster.local"
  trafficPolicy:
    tls:
      mode: ISTIO_MUTUAL
```

### 10.4 Retry Policy

Create `k8s/istio/virtual-services.yaml`:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: tax-retry
  namespace: yas-dev
spec:
  hosts:
    - tax
  http:
    - retries:
        attempts: 3
        perTryTimeout: 5s
        retryOn: "5xx,reset,connect-failure,retriable-4xx"
      route:
        - destination:
            host: tax
            port:
              number: 8080
---
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: order-retry
  namespace: yas-dev
spec:
  hosts:
    - order
  http:
    - retries:
        attempts: 3
        perTryTimeout: 10s
        retryOn: "5xx,reset,connect-failure"
      route:
        - destination:
            host: order
            port:
              number: 8080
```

### 10.5 Authorization Policies

Create `k8s/istio/authorization-policies.yaml`:

```yaml
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: deny-all
  namespace: yas-dev
spec: {}
---
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: allow-storefront-to-product
  namespace: yas-dev
spec:
  selector:
    matchLabels:
      app: product
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/yas-dev/sa/storefront-bff"
              - "cluster.local/ns/yas-dev/sa/backoffice-bff"
---
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: allow-order-to-tax
  namespace: yas-dev
spec:
  selector:
    matchLabels:
      app: tax
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/yas-dev/sa/order"
              - "cluster.local/ns/yas-dev/sa/backoffice-bff"
---
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: allow-search-access
  namespace: yas-dev
spec:
  selector:
    matchLabels:
      app: search
  action: ALLOW
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/yas-dev/sa/storefront-bff"
              - "cluster.local/ns/yas-dev/sa/backoffice-bff"
```

The coding agent must expand these allow rules for all real service-to-service calls after inspecting YAS runtime configuration.

### 10.6 Service Mesh Test Plan

```bash
# Verify mTLS proxy sidecars are injected.
kubectl get pods -n yas-dev -o jsonpath='{range .items[*]}{.metadata.name}{" containers="}{range .spec.containers[*]}{.name}{","}{end}{"\n"}{end}'

# Allowed call: order -> tax.
kubectl exec -n yas-dev deploy/order -c order -- \
  curl -v http://tax.yas-dev.svc.cluster.local:8080/actuator/health

# Blocked call: search -> tax.
kubectl exec -n yas-dev deploy/search -c search -- \
  curl -v http://tax.yas-dev.svc.cluster.local:8080/actuator/health

# Expected for blocked call: 403 RBAC access denied.

# Verify retry behavior from Envoy logs.
kubectl logs -n yas-dev deploy/order -c istio-proxy | grep x-envoy-attempt-count
```

---

## 11. Helm Chart Requirements

### 11.1 Chart Defaults

`helm/yas/values.yaml` must support DOKS and ingress by default:

```yaml
global:
  dockerhubUsername: "your-dockerhub-username"
  imageTag: "latest"
  imagePullPolicy: IfNotPresent
  baseDomain: "yas.local"

ingress:
  enabled: true
  className: nginx
  host: dev.yas.local
  annotations: {}

services:
  product:
    tag: ""
    replicas: 1
    port: 8080
  cart:
    tag: ""
    replicas: 1
    port: 8080
  order:
    tag: ""
    replicas: 1
    port: 8080
  storefront-ui:
    tag: ""
    replicas: 1
    port: 3000
  backoffice-ui:
    tag: ""
    replicas: 1
    port: 3000
```

Template rule:

```text
Image tag = service-specific tag if set, otherwise global.imageTag.
```

### 11.2 Ingress Template

Create `helm/yas/templates/ingress.yaml`:

```yaml
{{- if .Values.ingress.enabled }}
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: {{ include "yas.fullname" . }}
  namespace: {{ .Release.Namespace }}
  annotations:
    {{- toYaml .Values.ingress.annotations | nindent 4 }}
spec:
  ingressClassName: {{ .Values.ingress.className | quote }}
  rules:
    - host: {{ .Values.ingress.host | quote }}
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: storefront-ui
                port:
                  number: 3000
          - path: /backoffice
            pathType: Prefix
            backend:
              service:
                name: backoffice-ui
                port:
                  number: 3000
          - path: /swagger
            pathType: Prefix
            backend:
              service:
                name: swagger-ui
                port:
                  number: 80
{{- end }}
```

The coding agent must adjust paths if the actual YAS frontend expects a different base path.

### 11.3 Service Accounts

Create one Kubernetes `ServiceAccount` per application service. The Deployment for each service must use its matching service account so Istio `AuthorizationPolicy` can use SPIFFE identities:

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: product
  namespace: {{ .Release.Namespace }}
```

---

## 12. Kubernetes Manifests Reference

### 12.1 LoadBalancer Service Example

The ingress controller owns the only required public LoadBalancer service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: ingress-nginx-controller
  namespace: ingress-nginx
  annotations:
    service.beta.kubernetes.io/do-loadbalancer-name: yas-ingress
    service.beta.kubernetes.io/do-loadbalancer-size-unit: "1"
spec:
  type: LoadBalancer
  ports:
    - name: http
      port: 80
      targetPort: http
    - name: https
      port: 443
      targetPort: https
```

Do not expose every YAS service with `NodePort`. Keep internal services as `ClusterIP` and expose only HTTP entry points through Ingress.

### 12.2 Developer Cleanup Script

Create `scripts/destroy-developer-env.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail

helm uninstall yas-developer --namespace yas-developer || true
kubectl delete namespace yas-developer || true
```

### 12.3 DOKS Verification Commands

```bash
doctl kubernetes cluster list
doctl kubernetes cluster get yas-doks
kubectl get nodes -o wide
kubectl get ns
kubectl get svc -n ingress-nginx
kubectl get ingress -A
kubectl get pods -n yas-dev
```

---

## 13. Deliverables Checklist

### Base Requirements

- [ ] DOKS cluster is created and reachable with `kubectl`.
- [ ] Cluster has a managed control plane and at least one worker node pool.
- [ ] NGINX Ingress Controller is installed and exposed by a DigitalOcean Load Balancer.
- [ ] `ci.yml` builds and pushes Docker Hub images tagged with commit SHA on every branch push.
- [ ] `main` builds also publish `latest`.
- [ ] `developer_build` workflow accepts branch parameters and deploys selected service images by branch commit SHA.
- [ ] All unselected developer services use `latest`.
- [ ] Developer workflow prints `http://developer.<BASE_DOMAIN>/` and the hosts-file entry if DNS is not configured.
- [ ] Cleanup workflow deletes the developer Helm release and namespace.
- [ ] Dev workflow deploys to `yas-dev` on every `main` push.
- [ ] Staging workflow builds release images and deploys to `yas-staging` on tags like `v1.2.3`.

### Advanced - Argo CD

- [ ] Argo CD is installed on DOKS.
- [ ] `k8s/argocd/app-dev.yaml` syncs `yas-dev`.
- [ ] `k8s/argocd/app-staging.yaml` syncs `yas-staging`.
- [ ] GitHub Actions updates Helm values in Git instead of directly running Helm when GitOps mode is enabled.

### Advanced - Service Mesh

- [ ] Istio is installed on DOKS.
- [ ] Sidecar injection is enabled for `yas-dev`, `yas-staging`, and `yas-developer`.
- [ ] Strict mTLS is enabled with `PeerAuthentication`.
- [ ] `DestinationRule` uses `ISTIO_MUTUAL`.
- [ ] `VirtualService` implements retry policy for `tax` and `order`.
- [ ] `AuthorizationPolicy` includes deny-all plus explicit allow rules.
- [ ] Kiali topology screenshot is saved.
- [ ] Test logs show allowed traffic, blocked traffic, and retry attempts.

### Report Requirements

Report file naming convention:

```text
<MSSV1>_<MSSV2>_<MSSV3>_<MSSV4>.docx
```

Include:

- DigitalOcean cluster creation screenshots.
- `kubectl get nodes` output.
- DigitalOcean Load Balancer external IP.
- GitHub Actions CI and CD run screenshots.
- Application access screenshots.
- Argo CD screenshots if advanced GitOps is implemented.
- Kiali topology and policy test screenshots if service mesh is implemented.
- README with deployment steps and cleanup steps.

---

## 14. Quick Start Command Sequence

```bash
# 1. Clone and fork YAS.
git clone https://github.com/nashtech-garage/yas.git
cd yas

# 2. Authenticate DigitalOcean.
doctl auth init

# 3. Create DOKS cluster.
export DOKS_CLUSTER_NAME=yas-doks
export DOKS_REGION=sgp1
bash scripts/create-doks-cluster.sh

# 4. Create namespaces.
kubectl apply -f k8s/namespaces.yaml

# 5. Install ingress controller and wait for Load Balancer IP.
bash scripts/install-ingress-nginx.sh
kubectl get svc ingress-nginx-controller -n ingress-nginx

# 6. Configure DNS or local hosts-file entries.
export BASE_DOMAIN=yas.local
LOAD_BALANCER_IP=$(kubectl get svc ingress-nginx-controller -n ingress-nginx -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
echo "${LOAD_BALANCER_IP} dev.${BASE_DOMAIN}"
echo "${LOAD_BALANCER_IP} staging.${BASE_DOMAIN}"
echo "${LOAD_BALANCER_IP} developer.${BASE_DOMAIN}"

# 7. Deploy infrastructure for dev.
bash scripts/bootstrap-doks.sh yas-dev

# 8. Deploy app with Helm.
helm upgrade --install yas-dev ./helm/yas \
  --namespace yas-dev \
  --create-namespace \
  --values ./helm/yas/values.yaml \
  --values ./helm/yas/values-dev.yaml \
  --set ingress.host=dev.${BASE_DOMAIN}

# 9. Optional: install Istio.
bash scripts/install-istio.sh
kubectl apply -f k8s/istio/

# 10. Optional: install Argo CD.
bash scripts/install-argocd.sh
kubectl apply -f k8s/argocd/
```

---

## Research Notes Used for This Revision

- DigitalOcean Kubernetes supports managed control planes, autoscaling, standard Kubernetes tooling, and DigitalOcean Load Balancers.
- `doctl kubernetes cluster create` can create a DOKS cluster and configure a local kubectl context.
- `doctl kubernetes cluster kubeconfig save <cluster>` adds DOKS credentials to kubeconfig for local or GitHub Actions deployment.
- DOKS provisions cloud Load Balancers from Kubernetes `Service` objects with `type: LoadBalancer`.
- `digitalocean/action-doctl@v2` installs and authenticates `doctl` in GitHub Actions.
