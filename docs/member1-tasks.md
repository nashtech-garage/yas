# TV1 — Hạ Tầng GCP + K8s + ArgoCD + Infrastructure Services

> **Vai trò:** Người dựng nền tảng — team phụ thuộc vào TV1 để apply workload.
> **Ưu tiên:** Hoàn thành Phase 1-3 trong Tuần 1 để TV2/TV4 có thể integration test Tuần 2.

---

## Phase 1 — Provision GCP Server (Ngày 1-2)

### 1.1 Tạo GCP VM Instance
- [ ] Tạo VM: machine type `e2-standard-8`, 32GB RAM, Ubuntu 22.04, **100GB SSD**, region `asia-southeast1`
- [ ] 📸 Screenshot: VM instance trạng thái Running + External IP

### 1.2 Cấu hình Firewall Rules
```bash
# Trong GCP Console → VPC Network → Firewall
```
| Rule name | Protocols/Ports | Dùng cho |
|-----------|----------------|----------|
| allow-k8s-api | tcp:6443 | kubectl từ xa |
| allow-http-https | tcp:80,443 | Ingress |
| allow-nodeport | tcp:30000-32767 | developer-build NodePort |
| allow-jenkins-jnlp | tcp:50000 | Jenkins Agent |
| allow-argocd | tcp:30080 | ArgoCD UI |
| allow-kiali | tcp:20001 | Kiali UI (TV4) |

- [ ] Tạo đủ 6 firewall rules
- [ ] 📸 Screenshot: Danh sách firewall rules

### 1.3 SSH & User Setup
```bash
ssh -i <key> ubuntu@<GCP_EXTERNAL_IP>
sudo useradd -m -s /bin/bash devops
sudo usermod -aG sudo devops
sudo usermod -aG docker devops
```
- [ ] 📸 Screenshot: SSH thành công

### 1.4 Cài đặt Tools
```bash
sudo apt update && sudo apt upgrade -y

# Docker
sudo apt install -y docker.io
sudo systemctl enable docker && sudo systemctl start docker
sudo usermod -aG docker $USER

# kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# Helm
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

# yq, jq, git, curl
sudo apt install -y git curl jq
sudo wget https://github.com/mikefarah/yq/releases/latest/download/yq_linux_amd64 -O /usr/local/bin/yq
sudo chmod +x /usr/local/bin/yq

# Java 21 (cho Jenkins Agent)
sudo apt install -y openjdk-21-jre-headless

# Maven
sudo apt install -y maven

# Kustomize
curl -s "https://raw.githubusercontent.com/kubernetes-sigs/kustomize/master/hack/install_kustomize.sh" | bash
sudo mv kustomize /usr/local/bin/
```
- [ ] Verify: `docker version`, `kubectl version --client`, `helm version`, `java --version`, `kustomize version`
- [ ] 📸 Screenshot: Output tất cả verify commands

---

## Phase 2 — K8s Cluster Setup (Ngày 2-3)

### 2.1 Cài K3s với Docker runtime
```bash
# Dùng --docker để K3s dùng Docker runtime
# Jenkins Agent dùng Docker socket để build image → cần dùng chung runtime
curl -sfL https://get.k3s.io | sh -s - --docker

# Export kubeconfig
mkdir -p ~/.kube
sudo cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
sudo chown $(id -u):$(id -g) ~/.kube/config
chmod 600 ~/.kube/config
```
- [ ] Verify: `kubectl get nodes` → status `Ready`
- [ ] 📸 Screenshot: `kubectl get nodes` + `kubectl get pods -A`

### 2.2 Tạo Namespaces
```bash
kubectl create namespace dev
kubectl create namespace staging
kubectl create namespace developer-build
kubectl create namespace argocd
kubectl create namespace istio-system
```
- [ ] 📸 Screenshot: `kubectl get namespaces`

### 2.3 ImagePullSecret cho Docker Hub
```bash
for ns in dev staging developer-build; do
  kubectl create secret docker-registry dockerhub-secret \
    --docker-server=https://index.docker.io/v1/ \
    --docker-username=bingsu1103 \
    --docker-password=<DOCKERHUB_TOKEN> \
    --docker-email=team@example.com \
    -n $ns
done
```
- [ ] Verify: `kubectl get secret dockerhub-secret -n dev`
- [ ] 📸 Screenshot: Secrets đã tạo (3 namespaces)

### 2.4 Export kubeconfig cho Jenkins Agent
```bash
# Tạo bản kubeconfig với External IP thay vì 127.0.0.1
cat ~/.kube/config | sed 's/127.0.0.1/<GCP_EXTERNAL_IP>/g' > /tmp/kubeconfig-external.yaml
cat /tmp/kubeconfig-external.yaml
```
- [ ] Gửi file `/tmp/kubeconfig-external.yaml` cho TV2 để cấu hình Jenkins credential
- [ ] ⚠️ KHÔNG commit kubeconfig vào bất kỳ Git repo nào

---

## Phase 3 — ArgoCD Installation (Ngày 4-5)

### 3.1 Cài ArgoCD
```bash
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
kubectl wait --for=condition=Ready pods --all -n argocd --timeout=300s
```
- [ ] 📸 Screenshot: `kubectl get pods -n argocd` → tất cả Running

### 3.2 Expose ArgoCD Server (NodePort)
```bash
kubectl patch svc argocd-server -n argocd \
  -p '{"spec":{"type":"NodePort","ports":[{"port":443,"targetPort":8080,"nodePort":30080}]}}'
```
- [ ] Truy cập: `https://<GCP_IP>:30080` (bỏ qua SSL warning)
- [ ] 📸 Screenshot: ArgoCD login page

### 3.3 Setup Authentication
```bash
# Lấy admin password
kubectl get secret argocd-initial-admin-secret -n argocd \
  -o jsonpath="{.data.password}" | base64 -d && echo

# Cài argocd CLI
sudo curl -sSL -o /usr/local/bin/argocd https://github.com/argoproj/argo-cd/releases/latest/download/argocd-linux-amd64
sudo chmod +x /usr/local/bin/argocd

# Login
argocd login <GCP_IP>:30080 --username admin --password <password> --insecure

# Đổi password
argocd account update-password
```
- [ ] 📸 Screenshot: ArgoCD dashboard sau login

### 3.4 Connect ArgoCD tới GitOps Repo
```bash
# Dùng HTTPS + Personal Access Token
argocd repo add https://github.com/<org>/gitops-manifest-k8s.git \
  --username <github-user> \
  --password <github-pat>
```
- [ ] Verify: ArgoCD UI → Settings → Repositories → Connection Status: Successful
- [ ] 📸 Screenshot: Repo connected

### 3.5 Tạo ArgoCD Application cho `dev` (auto-sync)
```bash
argocd app create yas-dev \
  --repo https://github.com/<org>/gitops-manifest-k8s.git \
  --path environments/dev \
  --dest-server https://kubernetes.default.svc \
  --dest-namespace dev \
  --sync-policy automated \
  --auto-prune \
  --self-heal
```
- [ ] 📸 Screenshot: App `yas-dev` trên UI (status: Synced hoặc OutOfSync nếu TV3 chưa push)

### 3.6 Tạo ArgoCD Application cho `staging` (manual sync)
```bash
argocd app create yas-staging \
  --repo https://github.com/<org>/gitops-manifest-k8s.git \
  --path environments/staging \
  --dest-server https://kubernetes.default.svc \
  --dest-namespace staging
  # Không có --sync-policy automated → manual sync
```
- [ ] 📸 Screenshot: App `yas-staging` trên UI

### 3.7 Test ArgoCD Sync (phối hợp TV3)
- [ ] TV3 push manifest → verify ArgoCD detect trong 30s
- [ ] Sync app: `argocd app sync yas-dev`
- [ ] 📸 Screenshot: Sync thành công + `kubectl get pods -n dev`

---

## Phase 4 — Infrastructure Services (Tuần 2)

> Đây là phần **thiếu quan trọng** — không có infra thì app pods không start được.
> deploy vào namespace `dev`, `developer-build` sẽ cross-namespace access.

### 4.1 Add Helm repos
```bash
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add codecentric https://codecentric.github.io/helm-charts
helm repo update
```

### 4.2 PostgreSQL
```bash
# Dùng Helm chart từ k8s/deploy/postgres/ trong repo yas (đã có sẵn)
# Hoặc dùng bitnami nếu chart sẵn có không đủ
helm upgrade --install postgres bitnami/postgresql \
  -n dev \
  --set auth.postgresPassword=password \
  --set auth.database=yas \
  --set primary.initdb.scriptsConfigMap=postgres-init \
  --set primary.resources.requests.memory=512Mi \
  --set primary.resources.limits.memory=1Gi

# ConfigMap init SQL (tạo multiple databases)
kubectl apply -f - -n dev <<'EOF'
apiVersion: v1
kind: ConfigMap
metadata:
  name: postgres-init
data:
  init.sql: |
    CREATE DATABASE media;
    CREATE DATABASE product;
    CREATE DATABASE order_db;
    CREATE DATABASE inventory;
    CREATE DATABASE payment;
    CREATE DATABASE promotion;
    CREATE DATABASE rating;
    CREATE DATABASE customer;
    CREATE DATABASE location;
    CREATE DATABASE cart;
    CREATE DATABASE tax;
    CREATE DATABASE webhook;
    CREATE DATABASE delivery;
EOF
```
- [ ] Verify: `kubectl get pods -n dev | grep postgres`
- [ ] 📸 Screenshot: PostgreSQL pod Running

### 4.3 Redis
```bash
helm upgrade --install redis bitnami/redis \
  -n dev \
  --set auth.enabled=false \
  --set master.resources.requests.memory=256Mi
```
- [ ] 📸 Screenshot: Redis pod Running

### 4.4 Keycloak
```bash
# Dùng chart từ k8s/deploy/keycloak/ hoặc bitnami
helm upgrade --install keycloak bitnami/keycloak \
  -n dev \
  --set auth.adminUser=admin \
  --set auth.adminPassword=admin \
  --set postgresql.enabled=false \
  --set externalDatabase.host=postgres-postgresql.dev.svc.cluster.local \
  --set externalDatabase.port=5432 \
  --set externalDatabase.user=postgres \
  --set externalDatabase.password=password \
  --set externalDatabase.database=keycloak \
  --set resources.requests.memory=512Mi \
  --set resources.limits.memory=1Gi
```
- [ ] 📸 Screenshot: Keycloak pod Running

### 4.5 Kafka + Zookeeper
```bash
helm upgrade --install kafka bitnami/kafka \
  -n dev \
  --set zookeeper.enabled=true \
  --set replicaCount=1 \
  --set resources.requests.memory=512Mi \
  --set resources.limits.memory=1Gi \
  --set zookeeper.resources.requests.memory=256Mi
```
- [ ] 📸 Screenshot: Kafka + Zookeeper pods Running

### 4.6 Elasticsearch
```bash
helm upgrade --install elasticsearch bitnami/elasticsearch \
  -n dev \
  --set master.replicaCount=1 \
  --set data.replicaCount=1 \
  --set coordinating.replicaCount=1 \
  --set master.resources.requests.memory=512Mi \
  --set master.resources.limits.memory=1Gi \
  --set data.resources.limits.memory=1Gi \
  --set security.enabled=false
```
- [ ] 📸 Screenshot: Elasticsearch pods Running

### 4.7 Kiểm tra toàn bộ infra
```bash
kubectl get pods -n dev
# Kết quả mong đợi:
# postgres-postgresql-0     1/1  Running
# redis-master-0             1/1  Running
# keycloak-0                 1/1  Running
# kafka-0                    1/1  Running
# kafka-zookeeper-0          1/1  Running
# elasticsearch-master-0     1/1  Running
```
- [ ] 📸 Screenshot: Tất cả infra pods Running

---

## Phase 5 — Jenkins Agent trên GCP (Tuần 2)

### 5.1 Tạo Node trên Jenkins Controller (AWS)
Trên Jenkins UI:
- Manage Jenkins → Nodes → New Node
- Name: `gcp-k8s-agent`
- Type: Permanent Agent
- Executors: 2
- Remote root directory: `/home/devops/jenkins-agent`
- Labels: `gcp-k8s-agent`
- Launch method: Launch agent by connecting it to the controller
- Copy Secret token để dùng ở bước tiếp

### 5.2 Cài Jenkins Agent (JNLP) trên GCP
```bash
# Tạo thư mục
sudo mkdir -p /home/devops/jenkins-agent
sudo chown devops:devops /home/devops/jenkins-agent

# Download agent.jar từ Jenkins Controller
wget http://<JENKINS_AWS_IP>:8080/jnlpJars/agent.jar -P /home/devops/

# Test chạy thủ công trước
java -jar /home/devops/agent.jar \
  -url http://<JENKINS_AWS_IP>:8080/ \
  -secret <SECRET_TOKEN> \
  -name gcp-k8s-agent \
  -workDir /home/devops/jenkins-agent
```

### 5.3 Tạo systemd service để auto-restart
```bash
sudo tee /etc/systemd/system/jenkins-agent.service <<'EOF'
[Unit]
Description=Jenkins Agent
After=network.target

[Service]
User=devops
WorkingDirectory=/home/devops/jenkins-agent
ExecStart=/usr/bin/java -jar /home/devops/agent.jar \
  -url http://<JENKINS_AWS_IP>:8080/ \
  -secret <SECRET_TOKEN> \
  -name gcp-k8s-agent \
  -workDir /home/devops/jenkins-agent
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable jenkins-agent
sudo systemctl start jenkins-agent
sudo systemctl status jenkins-agent
```
- [ ] 📸 Screenshot: Agent online trong Jenkins UI (Jenkins → Nodes → gcp-k8s-agent → Connected)

### 5.4 Verify tools trên agent
```bash
# Tạo test pipeline trên Jenkins:
# agent { label 'gcp-k8s-agent' }
# stages { stage('Test') { steps {
#   sh 'kubectl get nodes'
#   sh 'docker version'
#   sh 'mvn --version'
#   sh 'java --version'
#   sh 'kustomize version'
# }}}
```
- [ ] 📸 Screenshot: Console output test job thành công

---

## Phase 6 — Integration & Support (Tuần 3)

- [ ] Phối hợp TV2 test CI pipeline end-to-end
- [ ] Phối hợp TV2 test developer_build + cleanup
- [ ] Phối hợp TV4: chạy `kubectl label namespace dev istio-injection=enabled` TRƯỚC khi TV4 deploy app pods
- [ ] Fix CrashLoopBackOff / ImagePullBackOff / resource OOM issues
- [ ] Điều chỉnh resource limits nếu node hết RAM
- [ ] 📸 Screenshot: Pods running + ArgoCD synced

---

## Phase 7 — Documentation (Tuần 3)

- [ ] Viết `docs/infrastructure-setup.md`: hướng dẫn step-by-step setup GCP + K3s + ArgoCD + infra
- [ ] Ghi lại các External IP, NodePorts, credentials (không commit secrets)
- [ ] Gửi screenshots + nội dung báo cáo phần TV1 cho TV4

---

## Checklist Cuối Cùng

- [ ] GCP VM `e2-standard-8` running, SSH accessible
- [ ] 6 firewall rules mở đúng ports
- [ ] K3s cluster với Docker runtime, `kubectl get nodes` → Ready
- [ ] 5 namespaces: dev, staging, developer-build, argocd, istio-system
- [ ] dockerhub-secret trong 3 namespaces app
- [ ] ArgoCD deployed, connected gitops repo, 2 Applications (yas-dev, yas-staging)
- [ ] Infrastructure: Postgres, Redis, Keycloak, Kafka, Elasticsearch running trong `dev`
- [ ] Jenkins Agent online với label `gcp-k8s-agent`, đủ tools
- [ ] kubeconfig-external.yaml gửi cho TV2
- [ ] Test job chạy thành công trên gcp-k8s-agent
- [ ] Báo cáo + screenshots gửi TV4
