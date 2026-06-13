# 👤 TV1 — Hạ Tầng GCP + K8s Cluster + ArgoCD + Jenkins Agent

> **Vai trò:** Người dựng nền tảng — team phụ thuộc vào TV1 để apply workload.  
> **Ưu tiên:** 🔴 Cao nhất — hoàn thành Phase 1-2 trong Tuần 1.

---

## Phase 1 — Provision GCP Server (Ngày 1-2)

### 1.1 Tạo GCP VM Instance
- [ ] Tạo VM: `e2-standard-8`, 32GB RAM, Ubuntu 22.04, 100GB SSD, region `asia-southeast1`
- [ ] 📸 Screenshot: VM instance Running

### 1.2 Cấu hình Firewall Rules
- [ ] `allow-k8s-api`: tcp:6443
- [ ] `allow-http-https`: tcp:80,443
- [ ] `allow-nodeport`: tcp:30000-32767
- [ ] `allow-jenkins-jnlp`: tcp:50000
- [ ] `allow-argocd`: tcp:30080
- [ ] 📸 Screenshot: Danh sách firewall rules

### 1.3 SSH & User Setup
- [ ] SSH vào VM, tạo user `devops`, cấu hình SSH key
- [ ] 📸 Screenshot: SSH thành công

### 1.4 Cài đặt Tools
```bash
sudo apt update && sudo apt install -y docker.io
sudo systemctl enable docker && sudo systemctl start docker
sudo usermod -aG docker $USER
# kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
# helm
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
```
- [ ] Verify: `docker --version`, `kubectl version --client`, `helm version`
- [ ] 📸 Screenshot: Output verify

---

## Phase 2 — K8s Cluster Setup (Ngày 2-3)

### 2.1 Cài K3s
```bash
curl -sfL https://get.k3s.io | sh -
mkdir -p ~/.kube
sudo cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
sudo chown $(id -u):$(id -g) ~/.kube/config
```
- [ ] Verify: `kubectl get nodes` → Ready
- [ ] 📸 Screenshot: `kubectl get nodes` + `kubectl get pods -A`

### 2.2 Tạo Namespaces
```bash
kubectl create namespace dev
kubectl create namespace staging
kubectl create namespace developer-build
```
- [ ] 📸 Screenshot: `kubectl get namespaces`

### 2.3 ImagePullSecret cho Docker Hub
```bash
for ns in dev staging developer-build; do
  kubectl create secret docker-registry dockerhub-secret \
    --docker-server=https://index.docker.io/v1/ \
    --docker-username=bingsu1103 \
    --docker-password=<PASSWORD> -n $ns
done
```
- [ ] 📸 Screenshot: Secret đã tạo

### 2.4 Export kubeconfig cho Jenkins Agent
- [ ] Copy `~/.kube/config`, thay `127.0.0.1` bằng External IP
- [ ] Gửi cho TV2 cấu hình Jenkins credential
- [ ] ⚠️ KHÔNG commit kubeconfig vào Git

---

## Phase 3 — ArgoCD Installation (Ngày 4-5)

### 3.1 Cài ArgoCD
```bash
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
kubectl wait --for=condition=Ready pods --all -n argocd --timeout=300s
```
- [ ] 📸 Screenshot: ArgoCD pods Running

### 3.2 Expose ArgoCD Server (NodePort)
```bash
kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "NodePort"}}'
kubectl get svc argocd-server -n argocd
```
- [ ] Truy cập: `https://<GCP_IP>:<NodePort>`
- [ ] 📸 Screenshot: ArgoCD login page

### 3.3 Setup Authentication
```bash
# Lấy admin password
kubectl get secret argocd-initial-admin-secret -n argocd \
  -o jsonpath="{.data.password}" | base64 -d
```
- [ ] Đăng nhập, đổi password
- [ ] 📸 Screenshot: ArgoCD dashboard

### 3.4 Connect ArgoCD tới GitOps Repo
- [ ] Settings → Repositories → Connect Repo (HTTPS + PAT)
- [ ] Test Connection → Successful
- [ ] 📸 Screenshot: Repo connected

### 3.5 Tạo ArgoCD App cho `dev` (auto-sync)
```bash
argocd app create yas-dev \
  --repo https://github.com/<org>/gitops-manifest-k8s.git \
  --path environments/dev \
  --dest-server https://kubernetes.default.svc \
  --dest-namespace dev \
  --sync-policy automated --auto-prune
```
- [ ] 📸 Screenshot: App `yas-dev` trên UI

### 3.6 Tạo ArgoCD App cho `staging`
- [ ] Tương tự 3.5 nhưng path `environments/staging`, namespace `staging`
- [ ] 📸 Screenshot: App `yas-staging` trên UI

### 3.7 Test ArgoCD Sync
- [ ] TV3 push manifest → ArgoCD detect → pods deploy
- [ ] `kubectl get pods -n dev`
- [ ] 📸 Screenshot: Sync thành công

---

## Phase 4 — Jenkins Agent trên GCP (Tuần 2)

### 4.1 Cài Java 21
```bash
sudo apt install -y openjdk-21-jre-headless
java --version
```

### 4.2 Cài Jenkins Agent (JNLP)
- [ ] Trên Jenkins (AWS): Manage Jenkins → Nodes → New Node `gcp-k8s-agent`
  - Labels: `gcp-k8s-agent`, Launch: JNLP, Executors: 2
- [ ] Trên GCP:
```bash
wget http://<JENKINS_URL>/jnlpJars/agent.jar
java -jar agent.jar -url http://<JENKINS_URL> -secret <SECRET> -name gcp-k8s-agent -workDir /home/devops/jenkins-agent
```
- [ ] Tạo systemd service để auto-start
- [ ] 📸 Screenshot: Agent online trong Jenkins UI

### 4.3 Cài thêm tools: maven, yq, git
```bash
sudo apt install -y maven
sudo wget https://github.com/mikefarah/yq/releases/latest/download/yq_linux_amd64 -O /usr/local/bin/yq && sudo chmod +x /usr/local/bin/yq
```

### 4.4 Test kết nối end-to-end
- [ ] Tạo test pipeline chạy trên `gcp-k8s-agent` → verify `kubectl get nodes`, `docker version`
- [ ] 📸 Screenshot: Console output test job

---

## Phase 5 — Integration & Fix (Tuần 2-3)

- [ ] Test CI/CD e2e với TV2
- [ ] Test developer_build + cleanup với TV2
- [ ] Hỗ trợ TV4 cài Istio
- [ ] Fix CrashLoopBackOff / ImagePullBackOff / resource issues
- [ ] 📸 Screenshot: Pods chạy thành công

---

## Phase 6 — Documentation (Tuần 3)

- [ ] Viết báo cáo: GCP VM setup, K3s, ArgoCD, Jenkins Agent
- [ ] Viết `docs/infrastructure-setup.md` (README hướng dẫn)
- [ ] Gửi text + screenshots cho TV4 tổng hợp

---

## ✅ Checklist Cuối Cùng

- [ ] GCP VM running + SSH accessible
- [ ] Firewall rules mở đúng ports
- [ ] K3s cluster Ready, 3 namespaces, ImagePullSecret
- [ ] ArgoCD installed + connected repo + 2 Apps (dev, staging)
- [ ] Jenkins Agent online label `gcp-k8s-agent` + đủ tools
- [ ] Test job chạy thành công
- [ ] Báo cáo + screenshots gửi TV4
