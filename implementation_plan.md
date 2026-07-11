# Implementation Plan - Distributed Master/Worker Node Setup

We will configure the K3s cluster architecture to support a hybrid deployment:
1. **Master Node (PC 64GB at home):** Will act as the control plane and run all heavy workloads (Postgres, Kafka, Elasticsearch, Keycloak, Observability, and Spring Boot Backends).
2. **Worker Node (Laptop at school):** Will connect as a worker node and run only lightweight UI services (`storefront-ui`, `backoffice-ui`).

This plan resolves the cold startup bottlenecks and avoids resource crashes on the Laptop while fully satisfying the project's multi-node K8s requirement.

## User Review Required

> [!IMPORTANT]
> To execute this plan, the Master Node (64GB PC) must be un-tainted to accept normal workloads, and nodes must be labeled properly. 
> Ensure the Laptop is connected to the same Tailscale network as the PC.

## Proposed Changes

### 1. Cluster Nodes Configuration (Manual Actions on Master)
Run the following commands on the 64GB PC Master Node (`k3s-server-0`):
```bash
# 1. Allow scheduling on the Master Node (by removing the master/control-plane taint)
kubectl taint nodes k3s-server-0 node-role.kubernetes.io/master-
kubectl taint nodes k3s-server-0 node-role.kubernetes.io/control-plane-

# 2. Label the Master Node for heavy workloads
kubectl label nodes k3s-server-0 type=heavy --overwrite

# 3. Label the Laptop Worker Node once it connects
kubectl label nodes <laptop-hostname> type=light --overwrite
```

---

### 2. Helm Chart Configuration (Workload Pinning)

We will modify the Helm values of the services to pin them to the correct nodes using `nodeSelector`.

#### [MODIFY] [backend/values.yaml](file:///c:/Users/Admin/Documents/A-devops/yas/k8s/charts/backend/values.yaml)
Pin all microservices backend components (inheriting from the base backend chart) to run only on the heavy 64GB node.
```yaml
nodeSelector:
  type: heavy
```

#### [MODIFY] [ui/values.yaml](file:///c:/Users/Admin/Documents/A-devops/yas/k8s/charts/ui/values.yaml)
Pin all frontend UI components to run on the lightweight Laptop node.
```yaml
nodeSelector:
  type: light
```

---

### 3. Core Infrastructures Configuration (Workload Pinning)

We will update the templates of heavy infrastructure services (Kafka, Postgres) and Helm values of the Observability stack (Prometheus, Grafana, Loki, Tempo).

#### [MODIFY] [kafka-cluster.yaml](file:///c:/Users/Admin/Documents/A-devops/yas/k8s/deploy/kafka/kafka-cluster/templates/kafka-cluster.yaml)
Pin Kafka and Zookeeper pods to the heavy node.
```yaml
spec:
  kafka:
    # ...
    template:
      pod:
        spec:
          nodeSelector:
            type: heavy
  zookeeper:
    # ...
    template:
      pod:
        spec:
          nodeSelector:
            type: heavy
```

#### [MODIFY] [debezium-connect-cluster.yaml](file:///c:/Users/Admin/Documents/A-devops/yas/k8s/deploy/kafka/kafka-cluster/templates/debezium-connect-cluster.yaml)
Pin Debezium connect pods to the heavy node.
```yaml
spec:
  # ...
  template:
    pod:
      spec:
        nodeSelector:
          type: heavy
```

#### [MODIFY] [postgresql.yaml](file:///c:/Users/Admin/Documents/A-devops/yas/k8s/deploy/postgres/postgresql/templates/postgresql.yaml)
Pin Postgresql Database pods to the heavy node.
```yaml
spec:
  # ...
  nodeSelector:
    type: heavy
```

#### [MODIFY] [elasticsearch CRD via operator patch]
We have already successfully patched Elasticsearch to run on the Master node:
```yaml
spec:
  nodeSets:
  - name: node
    podTemplate:
      spec:
        nodeSelector:
          kubernetes.io/hostname: k3s-server-0
```

#### [MODIFY] [keycloak CRD via operator patch]
We have already successfully patched Keycloak to run on the Master node:
```yaml
spec:
  unsupported:
    podTemplate:
      spec:
        nodeSelector:
          kubernetes.io/hostname: k3s-server-0
```

#### [MODIFY] [prometheus.values.yaml](file:///c:/Users/Admin/Documents/A-devops/yas/k8s/deploy/observability/prometheus.values.yaml)
Pin Prometheus server and Grafana to the heavy node.
```yaml
prometheus:
  prometheusSpec:
    enableRemoteWriteReceiver: true
    nodeSelector:
      type: heavy
grafana:
  nodeSelector:
    type: heavy
```

#### [MODIFY] [loki.values.yaml](file:///c:/Users/Admin/Documents/A-devops/yas/k8s/deploy/observability/loki.values.yaml)
Pin Loki read/write/backend and Minio to the heavy node.
```yaml
write:
  nodeSelector:
    type: heavy
read:
  nodeSelector:
    type: heavy
backend:
  nodeSelector:
    type: heavy
minio:
  nodeSelector:
    type: heavy
```

#### [MODIFY] [tempo.values.yaml](file:///c:/Users/Admin/Documents/A-devops/yas/k8s/deploy/observability/tempo.values.yaml)
Pin Tempo tracing backend to the heavy node.
```yaml
tempo:
  nodeSelector:
    type: heavy
```

---

## Verification Plan

### Automated Tests & Checks
* Validate node labels:
  `kubectl get nodes --show-labels`
* Verify pod placements:
  `kubectl get pods -A -o wide`
  Verify that all databases, backends, Kafka, and Observability pods reside on `k3s-server-0` (64GB PC), and UI pods (`storefront-ui`, `backoffice-ui`) reside on the laptop worker node.

### Manual Verification
* Access Kiali dashboard: `http://<IP-Tailscale>:30089` to verify service graph topology.
* Access Grafana: `http://grafana.yas.local.com` to verify metrics and tracing dashboard during demo.
