# YAS: Yet Another Shop - Context Memory & Deployment Status

This document records the full project context, completed work, infrastructure deployment structure, and current deployment status on the `feat/pipeline-cd` branch. The goal is to help project members or AI agents taking over to quickly understand and continue development.

---

## 1. Project Overview & Current Goals

- **Project**: **YAS (Yet Another Shop)** - An e-commerce application built on a microservices architecture using Java (Spring Boot) for the backend and Next.js for the frontend.
- **Goals of the `feat/pipeline-cd` branch**: 
  - Complete Kubernetes deployment configuration for local development (**Minikube**) and Cloud environment (**DigitalOcean Kubernetes - DOKS**).
  - Configure CI/CD system via **GitHub Actions** combined with **Helm**, **NGINX Ingress Controller**, **Argo CD** (for GitOps), and **Istio Service Mesh**.
  - Deploy a centralized API documentation portal using **Swagger UI**.
  - Fix frontend UI issues related to Tax management (`tax-classes`, `tax-rates`).
  - Optimize resource (CPU/Memory requests/limits) for services to ensure stable operation on personal machine configurations.

---

## 2. Completed Work Log (Commit History)

Recent work by author `htrung1105` on the `feat/pipeline-cd` branch includes:

*   **`a71d95bd` (fix: api page)**:
    *   Added **Swagger UI** deployment configuration (`k8s/charts/swagger-ui`).
    *   Created `nginx-configmap.yaml` to set up NGINX as a proxy serving static API documentation for backend microservices.
*   **`0a308e9f` (fix)**:
    *   Fixed display and data saving issues on Tax management pages in the **Backoffice UI**:
        *   Tax class edit/create page: [edit.tsx](file:///d:/Y3S2/%5BDevOps%5D_Project02/backoffice/pages/tax/tax-classes/%5Bid%5D/edit.tsx), [create.tsx](file:///d:/Y3S2/%5BDevOps%5D_Project02/backoffice/pages/tax/tax-classes/create.tsx), [index.tsx](file:///d:/Y3S2/%5BDevOps%5D_Project02/backoffice/pages/tax/tax-classes/index.tsx).
        *   Tax rate management page: [edit.tsx](file:///d:/Y3S2/%5BDevOps%5D_Project02/backoffice/pages/tax/tax-rates/%5Bid%5D/edit.tsx), [index.tsx](file:///d:/Y3S2/%5BDevOps%5D_Project02/backoffice/pages/tax/tax-rates/index.tsx).
    *   Updated `values.yaml` for **Swagger UI**.
*   **`7455e1d1` (fix)**:
    *   Added common backend Ingress configuration [yas-backend-apis-ingress.yaml](file:///d:/Y3S2/%5BDevOps%5D_Project02/k8s/yas-backend-apis-ingress.yaml) to route URLs like `/product`, `/cart`, `/order`,... to the correct service in the `yas` namespace.
    *   Configured NGINX service and service aliases in [service-aliases.yaml](file:///d:/Y3S2/%5BDevOps%5D_Project02/k8s/service-aliases.yaml).
*   **`ce982c4e` (fix: reduce unless service)**:
    *   Optimized hardware resources by reducing CPU/Memory requests/limits in Helm Chart files of services (`product`, `cart`, `order`,...).
    *   Edited `payment-paypal` Dockerfile to optimize the image build process.
*   **`153386c5` (update: deploy k8s service)**:
    *   Updated all Kubernetes infrastructure deployment manifests (`Elasticsearch`, `Kibana`, `Kafka`, `Debezium Connect`, `Keycloak`, `PostgreSQL`, `pgAdmin`, `Prometheus`, `Grafana`, `Loki`, `Tempo`, `OpenTelemetry Collector`, `Zookeeper`).
    *   Created automated application installation scripts:
        *   [deploy-yas-applications.sh](file:///d:/Y3S2/%5BDevOps%5D_Project02/k8s/deploy/deploy-yas-applications.sh) (Shell script for Linux/macOS).
        *   [deploy-yas-applications.ps1](file:///d:/Y3S2/%5BDevOps%5D_Project02/k8s/deploy/deploy-yas-applications.ps1) (PowerShell script for Windows).
    *   Authored detailed specification document [implementation-spec.md](file:///d:/Y3S2/%5BDevOps%5D_Project02/docs/implementation-spec.md) guiding DOKS cloud infrastructure setup and CI/CD workflow.

---

## 3. Deployment Architecture & Infrastructure (Kubernetes)

The YAS application is divided into two main groups when running on Kubernetes:

### 3.1 Business Application (Microservices) - Namespace `yas`
Microservices running in the `yas` namespace, managed via Helm Charts in `k8s/charts/`:
- `storefront-ui` & `storefront-bff`: Customer shopping application and Gateway.
- `backoffice-ui` & `backoffice-bff`: Admin system application and Gateway.
- `swagger-ui`: API documentation portal.
- Business API Services: `product`, `cart`, `order`, `customer`, `inventory`, `tax`, `media`, `search`.
- `sampledata`: Job supporting sample data seeding during system initialization.

### 3.2 Infrastructure Services
Support services deployed via Operators and Helm Charts in `k8s/deploy/`:
- **Database**: PostgreSQL (managed by Zalando Postgres Operator) in the `postgres` namespace.
- **Cache/Session**: Redis in the `redis` namespace.
- **Identity & Access Management (IAM)**: Keycloak in the `keycloak` namespace.
- **Event Streaming & CDC**: Kafka (managed by Strimzi Operator) and Debezium Connect in the `kafka` namespace.
- **Search Engine**: Elasticsearch and Kibana (managed by ECK Operator) in the `elasticsearch` namespace.
- **Observability**: Loki (Log), Tempo (Trace), Prometheus & Grafana (Metric), and OpenTelemetry Collector in the `observability` namespace.

---

## 4. Local Deployment Guide

To test the entire system on **Minikube** locally, follow this sequence:

### Step 1: Environment Preparation
- Minimum requirements: CPU 4 cores, RAM 16GB, 40GB free disk space.
- Start Minikube and enable Ingress:
  ```bash
  minikube start --disk-size='40000mb' --memory='16g'
  minikube addons enable ingress
  ```

### Step 2: Deploy Infrastructure Services
Navigate to the [k8s/deploy](file:///d:/Y3S2/%5BDevOps%5D_Project02/k8s/deploy) directory and run the setup scripts in order:
1. Deploy Keycloak as the identity service:
   ```bash
   ./setup-keycloak.sh
   ```
2. Deploy Redis for session management:
   ```bash
   ./setup-redis.sh
   ```
3. Deploy database and supporting components (Postgres, Kafka, Elasticsearch, Observability stack):
   ```bash
   ./setup-cluster.sh
   ```
   *Note: Wait for all Pods in the namespaces (`postgres`, `elasticsearch`, `kafka`, `keycloak`, `observability`) to reach `Running` status before proceeding to the next step.*

### Step 3: Deploy the Full YAS Application
Run the application installation script:
- On Linux/macOS:
  ```bash
  ./deploy-yas-applications.sh
  ```
- On Windows (PowerShell):
  ```powershell
  .\deploy-yas-applications.ps1
  ```
This script automatically updates Helm Chart dependencies and deploys all YAS microservices into the `yas` namespace.

### Step 4: Configure Hosts File
Get the Minikube IP address:
```bash
minikube ip
```
Add the following line to `/etc/hosts` (Linux/macOS) or `C:\Windows\System32\drivers\etc\hosts` (Windows) to map the IP to local domain names:
```text
<MINIKUBE_IP> pgoperator.yas.local.com
<MINIKUBE_IP> pgadmin.yas.local.com
<MINIKUBE_IP> akhq.yas.local.com
<MINIKUBE_IP> kibana.yas.local.com
<MINIKUBE_IP> identity.yas.local.com
<MINIKUBE_IP> backoffice.yas.local.com
<MINIKUBE_IP> storefront.yas.local.com
<MINIKUBE_IP> grafana.yas.local.com
<MINIKUBE_IP> api.yas.local.com
<MINIKUBE_IP> argocd.yas.local.com
```

Now you can access:
- Storefront: `http://storefront.yas.local.com`
- Backoffice: `http://backoffice.yas.local.com`
- API Documentation (Swagger UI): `http://api.yas.local.com/swagger-ui.html`
- Grafana Monitoring: `http://grafana.yas.local.com` (503 error)

---

## 5. CI/CD Pipeline Design on Cloud (DOKS)

The [implementation-spec.md](file:///d:/Y3S2/%5BDevOps%5D_Project02/docs/implementation-spec.md) document specifies the detailed deployment model on DigitalOcean Kubernetes.

### 5.1 Main GitHub Actions Workflows to Configure:
1.  **`ci.yml`**: Automatically triggered on new commits to any branch. Builds Docker Images using Docker Buildx and pushes to Docker Hub with tags: `${IMAGE}:${COMMIT_SHA}` and `${IMAGE}:${BRANCH_NAME}`.
2.  **`cd-developer.yml`**: Manually triggered (workflow_dispatch). Allows developers to specify an arbitrary branch for a specific service to test; all other services automatically use the `latest` tag. Deploys to the `yas-developer` namespace.
3.  **`cd-dev.yml`**: Automatically triggered when code is merged into the `main` branch. Deploys the latest version to the `yas-dev` namespace.
4.  **`cd-staging.yml`**: Automatically triggered when a version tag (`v*`) is pushed. Builds release images and deploys to the `yas-staging` namespace.

### 5.2 Required GitHub Secrets & Variables:
- **Secrets**:
  - `DOCKERHUB_USERNAME`: Docker Hub account username.
  - `DOCKERHUB_TOKEN`: Access Token used to push images.
  - `DIGITALOCEAN_ACCESS_TOKEN`: DigitalOcean API token for managing the DOKS cluster.
- **Variables**:
  - `DOKS_CLUSTER_NAME`: DOKS cluster name (default: `yas-doks`).
  - `DOKS_REGION`: Cluster region (default: `sgp1`).
  - `BASE_DOMAIN`: Base domain pointing to the DigitalOcean Load Balancer (e.g., `yas.example.com`).

### 5.3 Khoa's Section - CI/CD Developer Build and Service Mesh

Detailed operational documentation has been added at [khoa-ci-cd-service-mesh.md](khoa-ci-cd-service-mesh.md).

Key changes:

1. **CI Docker Hub**:
   - Added workflow `.github/workflows/ci.yml`.
   - Build Docker images for core services on every branch push.
   - Push images to Docker Hub tagged with commit SHA.
   - Also push branch-name and `latest` tags when branch is `main`.

2. **CD Developer Build**:
   - Added workflow `.github/workflows/cd-developer.yml`.
   - Manually triggered via `workflow_dispatch`.
   - Deploys to the `yas-developer` namespace.
   - Allows entering individual branch names per service.
   - Services with a specific branch use the commit SHA image tag; remaining services use `latest`.
   - Has `developer_profile=lean/full`: `lean` is a workaround for clusters with insufficient RAM, disabling Istio sidecar specifically for `yas-developer`; `full` keeps sidecar/mTLS for the developer environment when the cluster has sufficient resources.
   - Has a `cleanup` action to delete the `yas-developer` namespace.

3. **Service Mesh**:
   - Added `k8s/istio/` directory.
   - Installed Istio `1.30.2` on DOKS.
   - Enabled sidecar injection for `yas`, `dev`, `staging`, and `ingress-nginx`; `yas-developer` can run `full` to enable sidecar or `lean` to reduce RAM during CD demos.
   - Applied `PeerAuthentication` STRICT, `DestinationRule` ISTIO_MUTUAL, and `VirtualService` retry for `tax`/`order`.
   - Public entrypoints `storefront-bff`, `backoffice-bff`, `swagger-ui` set to `PERMISSIVE` for NGINX Ingress compatibility.

4. **Actual Test Status**:
   - Pods in the `yas` namespace are running `2/2` after sidecar injection.
   - `storefront.yas.local.com`, `backoffice.yas.local.com`, and `api.yas.local.com/swagger-ui/index.html` return HTTP `200`.
   - `istioctl proxy-status` shows proxies in `yas` and `ingress-nginx` are synced with `istiod`.

---

## 6. Important Technical Notes

1.  **BFF (Backend-for-Frontend) & Security**:
    *   The project uses SameSite Cookie security between the browser and BFF (Spring Cloud Gateway).
    *   The BFF acts as an OAuth2 Client, communicating directly with Keycloak and automatically attaching Access Tokens (`TokenRelay`) to headers when sending requests to backend API Resource Servers. This prevents token storage in browser LocalStorage/SessionStorage, enhancing XSS attack resistance.
2.  **Swagger UI (API Portal)**:
    *   Swagger UI has been deployed independently in the `yas` namespace and configured with routing via Ingress (`api.yas.local.com`).
    *   It aggregates all API documentation from backend microservices into a centralized interface for convenient system integration.
3.  **Change Data Capture (CDC)**:
    *   Debezium Connector listens to data changes (Insert/Update/Delete) from the PostgreSQL database (`postgres`) and pushes them to Kafka topics.
    *   A background service listens to these Kafka topics and synchronizes information to Elasticsearch in real-time to support high-speed product search functionality.
