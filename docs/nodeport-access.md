# NodePort Access and Hosts File Resolution Guide

## 1. Purpose
This document provides instructions for developers to access deployed microservices in the Kubernetes cluster using NodePort and local DNS hostname resolution via the `hosts` file.

---

## 2. Required Infrastructure Parameters (from Quốc Lộc)
To verify access, the following parameters must be provided by the infrastructure team (Quốc Lộc):
* **Kubernetes Cluster Type**: (e.g., Minikube, K3s, Rancher, Bare-metal)
* **Worker Node IP / Minikube IP**: `UNVERIFIED_RUNTIME` (Expected: Worker Node IP, e.g., `192.168.64.100` or local VM IP)
* **Target Namespace**: `dev` (Developer sandbox namespace)
* **Service Name**: (e.g., `storefront-bff`, `backoffice-bff`, `web-gateway`)
* **NodePort**: `UNVERIFIED_RUNTIME` (The high-port assigned between 30000-32767, e.g., `31080` for the Web Gateway or BFF)
* **Temporary Domain Name**: `yas-dev.local` (or standard domain mapped in project requirements)

---

## 3. Local Hostname Resolution Setup (hosts File)
To access services using friendly domains instead of raw IP addresses, add an entry to your local operating system's hosts file.

### 3.1 Windows Setup
1. Open Notepad or any text editor **as Administrator**.
2. Open the file located at:
   `C:\Windows\System32\drivers\etc\hosts`
3. Append a new line at the bottom:
   ```text
   <WORKER_NODE_IP> yas-dev.local
   ```
   *(Replace `<WORKER_NODE_IP>` with the actual IP address of the K8s Worker node, e.g., `192.168.64.100 yas-dev.local`)*
4. Save and close the file.

### 3.2 Linux / macOS Setup
1. Open a terminal.
2. Edit `/etc/hosts` with administrative privileges:
   ```bash
   sudo nano /etc/hosts
   ```
3. Append a new line at the bottom:
   ```text
   <WORKER_NODE_IP> yas-dev.local
   ```
   *(Replace `<WORKER_NODE_IP>` with the actual IP address of the K8s Worker node)*
4. Save the changes (`Ctrl+O`, then `Enter`) and exit (`Ctrl+X`).

---

## 4. Expected Access URL
Once the hosts file is updated, access the storefront/backoffice services via your browser using:
`http://yas-dev.local:<NODE_PORT>`

---

## 5. Verification Commands
Run these commands locally to verify the configuration:

### 5.1 Get Node IPs
Identify the external or internal IP of the K8s worker node:
```bash
kubectl get nodes -o wide
```
*Expected outcome: List of cluster nodes with status `Ready` and their IP addresses.*

### 5.2 Retrieve Service NodePort
Get the NodePort port mapping for the gateway/BFF:
```bash
kubectl get svc -n <namespace>
```
*Replace `<namespace>` with the active namespace (e.g., `dev`). Look for the Port mapping column in the output, e.g., `80:31080/TCP` indicates NodePort `31080`.*

### 5.3 Verify DNS and HTTP Connectivity
Test hostname resolution and HTTP headers using verbose curl:
```bash
curl -v http://yas-dev.local:<NODE_PORT>
```
*Replace `<NODE_PORT>` with the retrieved NodePort (e.g., `31080`). Expected outcome: Verbose output showing resolution to the worker IP, successful HTTP handshake (e.g., HTTP 200 OK or redirect), and response payload.*

---

## 6. Troubleshooting Checklist
If the service is not accessible via `http://yas-dev.local:<NODE_PORT>`, verify each item in this troubleshooting checklist:

| Issue | Potential Cause | Troubleshooting Action |
| :--- | :--- | :--- |
| **DNS/hosts not updated** | Changes to hosts file did not take effect or spelling error. | Run `ping yas-dev.local` in command line. Confirm it resolves to the K8s Worker IP. |
| **Wrong Worker Node IP** | Minikube IP changed or wrong cluster node IP was copied. | Run `kubectl get nodes -o wide` or `minikube ip` to verify current IP. |
| **Wrong NodePort** | Service Port configuration updated or incorrect port retrieved. | Run `kubectl get svc -n dev` and double check the NodePort mapping (usually between 30000-32767). |
| **Service has no endpoints** | Deployed pods are failing, crashing, or labels do not match selector. | Run `kubectl get ep -n dev` to check if endpoints list is populated. Run `kubectl get pods -n dev` to ensure pods are `Running` and healthy. |
| **Firewall issues** | NodePort range (30000-32767) is blocked by host/VM firewall or network security group. | Open ports 30000-32767 on the worker node security groups/firewall. If using Docker Desktop / Minikube, check VM port forwarding. |
| **Kubernetes service not ready** | Service manifests not applied or target namespace does not exist. | Verify namespace and service exists via `kubectl get ns` and `kubectl get svc -n dev`. |

---

## 7. Evidence Checklist
To confirm successful completion, the developer must attach or document the following evidence:
* [ ] **Screenshot of hosts file entry**: Showing the DNS mapping.
* [ ] **`kubectl get svc` output**: Validating service creation, type NodePort, and port assignments.
* [ ] **Browser / curl access result**: Showing the YAS page loading or HTTP headers return.
* [ ] **NodePort URL**: The actual URL tested.
