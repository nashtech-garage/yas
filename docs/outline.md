# Project 02: Building a CD System for YAS (Yet Another Shop)

## Core Services to Keep

These 14 services are essential for the E-commerce + Service Mesh demo:

| Service | Purpose | Importance |
|---------|---------|------------|
| **product** | Product management - central to the shop | Core |
| **cart** | Shopping cart - demo purchase flow | Core |
| **order** | Orders - demo order flow, test retry policy (order→cart/payment/inventory/tax) | Core |
| **customer** | Customer information | Core |
| **inventory** | Inventory - order dependency | Core |
| **tax** | Tax - order dependency, demo VirtualService retry | Core |
| **media** | Product image upload | Core |
| **search** | Search - depends on product, demo AuthorizationPolicy | Core |
| **storefront-bff** | BFF for user interface | Core |
| **storefront-ui** | Store UI - demo for instructors | Core |
| **backoffice-bff** | BFF for admin | Core |
| **backoffice-ui** | Admin UI | Core |
| **swagger-ui** | API documentation | Core |
| **sampledata** | Sample data - runs once, can be stopped after data is loaded | Temporary |

**Total:** 14 services - 1 temporary (sampledata)

---

## Project Overview

Build a CI/CD pipeline and monitoring system to deploy, operate, and monitor the **YAS (Yet Another Shop)** system from: https://github.com/nashtech-garage/yas

### Technologies & Frameworks

| Category | Technologies |
|----------|--------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.2 |
| **Testing** | Testcontainers |
| **Frontend** | Next.js |
| **Auth** | Keycloak |
| **Messaging** | Kafka |
| **Search** | Elasticsearch |
| **Orchestration** | Kubernetes (K8s) |
| **CI/CD** | GitHub Actions / Jenkins |
| **Code Quality** | SonarCloud |
| **Observability** | OpenTelemetry |
| **Monitoring** | Grafana, Loki, Prometheus, Tempo |

---

## Assignment Requirements (6 points)

### Default Setup
- All services use image with tag `main` or `latest` by default
- No need to deploy Grafana and Prometheus (Observability) in this assignment

### 1. Kubernetes Cluster Setup
Build a K8s cluster with:
- 1 Master node
- 1 Worker node
- *OR* Minikube
- *OR* Any other K8s model

### 2. CI Pipeline - Per Branch
For each branch created by the user:
- On commit, build an image with tag = commit ID of that branch
- Push image to Docker Hub

### 3. CD Job for Developers (`developer_build`)

**Purpose:** Allow developers to deploy and test their specific branch

**Input Parameter:** Branch name to deploy

**Example Scenario:**
- Developer works on branch: `dev_tax_service`
- Updates code in the service
- Enters `developer_build` job
- Sets parameter: `tax-service` = `dev_tax_service`
- All other services default to `main` or `latest` tags
- System deploys:
  - `dev_tax_service` → uses image with commit-id tag
  - All other services → use `main`/`latest` images

**Output:** Domain name:port (NodePort service) for developer to test code directly

### 4. Jenkins Job to Delete Deployment
Create a Jenkins job to remove the deployment from step 4.

### 5. Dev and Staging Deployment (Base Requirements)

#### a. Dev Environment
- Auto-deploy when `main` branch changes
- Deploys continuously to `dev` namespace

#### b. Staging Environment
- Triggered by tags on `main` branch
- Format: `v1.2.3`
- Build image with that tag
- Push to Docker Hub
- Deploy to `staging` namespace
- *Alternative:* Branch `rc_v1.2.3` or both tag and branch

---

## Advanced Requirements (2 points each)

### Advanced 1: ArgoCD Implementation
Use ArgoCD to handle `dev` and `staging` deployments.

### Advanced 2: Service Mesh Implementation

**Goal:** Configure Service Mesh (mTLS, connection policies) on K8S for microservices

#### Requirements:

1. **Enable TLS (mTLS)** between services on K8S for YAS application

2. **Topology Visualization**
   - Draw flow chart/topology of services
   - Use Kiali for observation

3. **Test Scenarios:**
   - **Retryable:** If service returns 500 error, automatically retry (define retry policy in service mesh)
   - **Authorization Policy:** Only allow specific services to communicate
   - **Test:** From another pod in cluster, `curl` to service to check policy enforcement (allow/block)

#### Implementation Suggestions (Istio):

| Component | Purpose |
|-----------|---------|
| **Istio** | Service mesh (install on K8S) |
| **Kiali** | Visualization |
| **PeerAuthentication/DestinationRule** | Enable mTLS per namespace or mesh-wide |
| **AuthorizationPolicy/RequestAuthentication** | Limit service-to-service access |
| **VirtualService** | Configure retry policy and timeout |

#### Sample Test Command:
```bash
kubectl exec -n <namespace> <pod> -- curl -v http://<service>.<namespace>:<port>/
```

#### Deliverables:
- YAML manifests for mTLS and authorization policy
- Screenshots of Kiali topology with explanation
- Test plan + logs (curl results, retry evidence)
- README with step-by-step deployment instructions

---

## CI/CD Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           GITHUB REPOSITORY                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────────────┐ │
│  │   main       │  │   feature/   │  │   v1.2.3 (tag)                   │ │
│  │   branch     │  │   branches   │  │                                  │ │
│  └──────┬───────┘  └──────┬───────┘  └─────────────┬────────────────────┘ │
└─────────┼─────────────────┼────────────────────────┼──────────────────────┘
          │                 │                        │
          ▼                 ▼                        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           CI PIPELINE                                      │
│  • Build image per branch with commit ID tag                               │
│  • Push to Docker Hub                                                      │
└─────────┬─────────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    CD JOBS (Jenkins / GitHub Actions)                      │
├─────────────────────────────────────────────────────────────────────────────┤
│  developer_build  │  Dev Auto-deploy  │  Staging Release   │  Delete Job   │
│  • Branch input   │  • main branch    │  • Tag v1.2.3     │  • Cleanup    │
│  • Deploy to      │  • Deploy to      │  • Deploy to      │               │
│    test env       │    dev namespace  │    staging ns     │               │
└─────────┬─────────┴────────┬──────────┴──────────┬────────┴───────────────┘
          │                  │                     │
          ▼                  ▼                     ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       KUBERNETES CLUSTER                                  │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────────┐│
│  │   Master Node   │  │   Worker Node   │  │  Service Mesh (Istio)      ││
│  │                 │  │                 │  │  • mTLS                    ││
│  └─────────────────┘  └─────────────────┘  │  • Auth Policies          ││
│                                             │  • Retry Policies         ││
│                                             │  • Kiali Visualization    ││
│                                             └─────────────────────────────┘│
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Quick Reference: Service Dependencies

```
                    ┌─────────────┐
                    │   product   │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │   search    │◄──── Depends on product
                    └─────────────┘

                    ┌─────────────┐
                    │    cart     │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │    order    │
                    └──────┬──────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
       ┌──────▼──────┐ ┌───▼────┐ ┌────▼────┐
       │  inventory  │ │  tax   │ │  cart   │
       └─────────────┘ └────────┘ └─────────┘
```

---

## Istio Service Mesh Checklist

- [x] Install Istio on K8S cluster
- [x] Enable mTLS (PeerAuthentication)
- [x] Configure DestinationRule for mTLS
- [x] Create VirtualService for retry policies
- [x] Set up AuthorizationPolicy
- [x] Install Kiali
- [x] Capture topology screenshots
- [x] Test retry with 500 errors
- [x] Test authorization blocking/allowing
- [x] Document all steps in README

