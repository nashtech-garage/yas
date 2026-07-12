## 1. Project Overview & Repository
* **Project Name:** YAS: Yet Another Shop [1]
* **Repository Link:** [https://github.com/nashtech-garage/yas](https://github.com/nashtech-garage/yas) [1]
* **Objective:** Build a Continuous Delivery (CD) pipeline, deployment configuration, and service mesh environment for a Java-based microservices application [1]. 

---

## 2. System Architecture & Technologies

### 2.1 Technology Stack
* **Languages & Frameworks:** Java 21, Spring Boot 3.2, Next.js, Testcontainers [1]
* **Infrastructure & Database:** PostgreSQL, Elasticsearch, Kafka, Keycloak [1]
* **CI/CD & DevOps:** Kubernetes (K8s), Helm, Jenkins / GitHub Actions, ArgoCD [2, 3]
* **Observability & Service Mesh:** OpenTelemetry, Prometheus, Grafana, Loki, Tempo, Istio, Kiali, SonarCloud [1, 2, 3, 4]

### 2.2 System Architecture Diagram Description
The application flow is organized as follows:
1. **User Client Access:** Users access the application via browsers through an **Nginx** reverse proxy [1].
2. **Routing:**
   * `/identity` routes to an **Identity** service powered by **Keycloak** [1].
   * `/storefront...` routes to **Storefront.BFF** (Spring Cloud Gateway), which proxies the **Storefront** UI (Next.js) [1].
   * `/backoffice...` routes to **BackOffice.BFF** (Spring Cloud Gateway), which proxies the **Backoffice** UI (Next.js) [1].
   * `/swagger` routes to **Swagger UI** [1].
   * `/pgadmin` routes to **pgAdmin** [1].
3. **Observability Stack:** OpenTelemetry, Prometheus, Grafana Loki, Grafana Tempo, and Grafana hook into the services for telemetry data collection [1].
4. **Business Services (Spring Boot):**
   * These include: `Media`, `Product`, `Cart`, `Order`, `Rating`, `Customer`, `Location`, `Inventory`, `Tax`, and `Search` [1].
5. **Data Storage & Event Streaming:**
   * PostgreSQL instances act as databases for the business services [1].
   * Kafka and Kafka Connect (using Debezium) manage events and database change capture (CDC) [1].
   * Elasticsearch powers the `Search` service [1].

---

## 3. Service Classification: Retained Services for Demo
The following table details which services from the repository must be retained and configured for the E-commerce and Service Mesh demonstration:

| Service Name | Keep / Core | Reason to Keep / Role in Demo |
| :--- | :---: | :--- |
| `product` | Yes | Products – Center of the shop architecture. |
| `cart` | Yes | Shopping Cart – Demo purchase flow. |
| `order` | Yes | Orders – Demo ordering flow, used to test the retry policy (`order` $\rightarrow$ `cart`/`payment`/`inventory`/`tax`). |
| `customer` | Yes | Customer information. |
| `inventory` | Yes | Inventory – Dependency for the order flow. |
| `tax` | Yes | Tax service – Dependency for the order flow, used to demo `VirtualService` retries. |
| `media` | Yes | Media service – Uploading product images. |
| `search` | Yes | Search service – Depends on `product`, used to demo `AuthorizationPolicy`. |
| `storefront-bff` | Yes | BFF (Backend for Frontend) for the customer user interface. |
| `storefront-ui`| Yes | Storefront UI – User interface demo. |
| `backoffice-bff`| Yes | BFF for administration. |
| `backoffice-ui` | Yes | Admin dashboard user interface. |
| `swagger-ui` | Yes | API documentation. |
| `sampledata` | Once | Seed data service – Run once to populate the database, then it can be stopped. |

* **Summary:** 14 total services (13 persistent services + 1 run-once utility service).

---

## 4. CD System Requirements

### 4.1 CI/CD Workflow (Basic Jenkins / GitHub Actions)
1. **Commit:** A developer commits code changes to a repository branch [2].
2. **Jenkins Trigger:** Jenkins is triggered automatically by the commit [2].
3. **Push Images:** Jenkins builds and pushes the Docker image with a tag matching the last commit ID of that branch to Docker Hub [2].
4. **Helm (CD):** Helm deploys or updates the configurations in the K8s cluster [2].
5. **Pull Images:** The K8s cluster pulls the updated images from Docker Hub [2].

### 4.2 Specific Pipeline Rules
* **Default Tagging:** By default, services use images tagged with `main` or `latest` [2]. (Deployment of Grafana and Prometheus observability is not strictly required in the basic phase) [2].
* **K8s Cluster:** Use a cluster with 1 Master node and 1 Worker node (e.g., Minikube, or any compatible K8s architecture) [2].
* **Feature Branch Builds (`developer_build` job):**
  * Create a Jenkins job named `developer_build` with parameterized inputs for developers to deploy individual feature branches [2].
  * *Example:* If a developer is working on `dev_tax_service` in the `tax-service` repo, they run `developer_build` setting the `tax-service` parameter to `dev_tax_service`. The deployment must update the `tax-service` to use the image tagged with that branch's latest commit, while all other microservices default to the `main` or `latest` tag [2].
  * *Access:* Deploy services as `NodePort` so developers can directly test connections using their machine's local `/etc/hosts` file mapped to the K8s Worker node IP [2, 3].
* **Deployment Cleanup:** Create a Jenkins job specifically dedicated to deleting/cleaning up the configurations deployed by the `developer_build` job [3].
* **Dev & Staging Environments (Standard CI/CD):**
  * **Dev:** Commits to the `main` branch automatically deploy and overwrite configurations in the `dev` namespace [3].
  * **Staging:** When a release tag is pushed on `main` (e.g., `v1.2.3`), the CI/CD job must build the tagged image, push it to Docker Hub, and deploy it to the `staging` namespace [3].

---

## 5. Advanced Requirements (Optional but highly recommended)

### 5.1 GitOps with ArgoCD (2 Points)
Instead of standard Jenkins push-deploy, use **ArgoCD** to sync and manage the `dev` and `staging` namespaces [3].
* **ArgoCD Flow:**
  1. Commit made to repository [3].
  2. Jenkins triggers build [3].
  3. Jenkins builds and pushes images to Docker Hub [3].
  4. Jenkins updates the Helm charts/K8s manifest repository [3].
  5. ArgoCD detects manifest updates and automatically synchronizes (pulls and deploys) the changes to the K8s cluster [3].

### 5.2 Service Mesh Configuration using Istio (2 Points)
Configure a Service Mesh to enforce security and resilience policies [3]:
1. **mTLS (Mutual TLS):** Enable mTLS between services deployed on K8s for the `yas` application [3].
2. **Observability:** Use **Kiali** to visualize and inspect the service mesh topology and communication flow [3].
3. **Test Scenarios:**
   * **Retry Policy:** Configure an automatic retry policy in the Service Mesh (`VirtualService`) if a service returns a `500` error [3].
   * **Authorization Policy:** Configure network access policies to restrict service-to-service communication [3]. Only approved services should be allowed to establish connections (using Istio `AuthorizationPolicy`) [3].
   * **Validation:** Validate policies by exec'ing into a pod inside the cluster and attempting to connection-test using `curl` to verify allowed or blocked traffic [3].

---

## 6. Implementation Hints (Service Mesh)
* Install **Istio** on the K8s cluster alongside **Kiali** [4].
* Apply mTLS mesh-wide or per-namespace using `PeerAuthentication` or `DestinationRule` resources [4].
* Enforce access control restrictions with `AuthorizationPolicy` and `RequestAuthentication` resources [4].
* Define timeout and retry configurations within `VirtualService` manifests [4].
* **Verification Command Template:**
  ```bash
  kubectl exec -n <namespace> <pod-name> -- curl -v http://<service-name>.<namespace>:<port>/
  ``` [4]

---

## 7. Submission Deliverables
* **Repository/Artifacts:**
  * YAML manifests for mTLS and authorization policies [4].
  * Step-by-step `README` guide on deployment [4].
* **Reports:**
  * Kiali topology screenshots illustrating and explaining traffic flow [4].
  * Detailed test plans and corresponding logs (including `curl` command responses and retry policy verification) [4].
  * Document named in the following format (student IDs sorted in ascending order): `<STUDENT_ID_1>_<STUDENT_ID_2>_<STUDENT_ID_3>.docx` [4].
