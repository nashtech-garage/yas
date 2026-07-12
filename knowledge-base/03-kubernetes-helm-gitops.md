# 3. Kubernetes, Helm và GitOps

## 3.1 Kubernetes

Kubernetes được Google công bố năm 2014, lấy kinh nghiệm từ Borg/Omega và vào CNCF năm 2015. Trước đó đội vận hành container bằng script, Fleet, Mesos/Marathon hoặc scheduler riêng. Kubernetes dùng reconciliation: người dùng gửi desired state đến API server; controller liên tục đưa actual state về desired state.

Control plane gồm API server, etcd, scheduler, controller manager và cloud controller. Node có kubelet, container runtime và kube-proxy/CNI. DOKS quản control plane, nhóm vẫn chịu trách nhiệm workload, RBAC, network policy, upgrade compatibility, backup dữ liệu và chi phí node.

### Pod

Pod là đơn vị schedule nhỏ nhất, chứa một hay nhiều container dùng chung network namespace và volume. `2/2 Running` nghĩa là hai container ready, thường app và Envoy, không phải hai replica. Bỏ readiness probe đưa traffic vào app chưa sẵn sàng; liveness sai gây restart loop; startup probe bảo vệ app khởi động chậm. Production dùng requests/limits, securityContext, probes, topology spread, PDB và graceful termination.

### ReplicaSet

ReplicaSet giữ số Pod theo selector. Deployment tạo và quản ReplicaSet theo revision. Không nên sửa ReplicaSet do Deployment quản vì controller sẽ ghi đè hoặc rollout sau làm mất thay đổi. Old ReplicaSet desired 0 giữ lịch sử rollback.

### Deployment

Field cốt lõi:

- `metadata.name/namespace/labels`: identity và truy vấn. Đổi tên thường tạo resource mới.
- `spec.replicas`: desired Pod count. Bỏ mặc định 1; khi HPA quản lý, GitOps không nên liên tục ép replicas.
- `selector.matchLabels`: immutable và phải khớp template labels. Sai thì API reject hoặc Service không chọn Pod.
- `template.metadata.labels`: nối Deployment, Service, policy và telemetry.
- `strategy.rollingUpdate.maxUnavailable/maxSurge`: trade-off availability và capacity.
- `containers[].image`: artifact chạy. Mutable tag gây rollout không xác định.
- `ports`: metadata và named port cho Service/probe.
- `env/envFrom`: config injection. Secret env dễ lộ qua process/debug; volume có thể rotate tốt hơn tùy app.
- `resources.requests`: scheduler dùng để đặt Pod; limit được cgroup enforce. Thiếu request làm overcommit thiếu kiểm soát.
- `readiness/liveness/startupProbe`: traffic, restart và thời gian bootstrap, ba mục đích khác nhau.
- `lifecycle.preStop` cùng `terminationGracePeriodSeconds`: drain trước SIGKILL.
- `serviceAccountName`: workload identity. Dùng default làm authz thô và quyền dễ quá rộng.

Chart backend render Deployment chung cho nhiều service, mount Logback/config, inject DB URL và expose metric port. `values.yaml` hiện có hard-coded `hostAliases` IP, chỉ thích hợp lab; production dùng DNS và không gắn IP cluster động.

### Service

Service tạo virtual IP và stable DNS, chọn endpoint bằng selector. `ClusterIP` nội bộ; `NodePort` mở port trên node; `LoadBalancer` nhờ cloud tạo LB; headless `clusterIP: None` trả Pod IP trực tiếp. Sai selector tạo Service không endpoint dù Service tồn tại. `port` là cổng Service, `targetPort` là cổng Pod, `nodePort` là cổng node. Production ưu tiên ClusterIP sau Ingress/Gateway, đặt tên port theo protocol và kiểm tra EndpointSlice.

### Ingress

Ingress là API route HTTP(S), chỉ có tác dụng khi có Ingress Controller. `ingressClassName` chọn controller; `rules.host/http.paths` chọn backend; `pathType` quyết định matching; `tls.secretName` chứa certificate. Bỏ TLS gửi plaintext từ client tới ingress. Annotation phụ thuộc controller, không portable. Kubernetes đang dịch chuyển chức năng mới sang Gateway API, nhưng Ingress vẫn phổ biến.

### ConfigMap và Secret

ConfigMap giữ cấu hình không nhạy cảm. Secret chỉ base64 encode, không tự mã hóa. Field `data` yêu cầu base64, `stringData` nhận plaintext khi apply. Env var không tự refresh; volume update có độ trễ và app phải reload. Repository có `yas-credentials.secret.yaml`, nhiều credential template và secret values; production không commit secret plaintext/base64, dùng External Secrets + Vault/cloud secret manager, KMS encryption at rest, RBAC tối thiểu và rotation.

### HPA

HPA `autoscaling/v2` scale Deployment theo metric. CPU utilization dựa trên request, nên thiếu CPU request làm metric không tính được. Min/max bảo vệ biên. HPA không thay Cluster Autoscaler. Scale theo memory có thể không hiệu quả với JVM nếu memory không giảm sau GC.

## 3.2 Helm

Helm xuất hiện năm 2015 trong hệ sinh thái Kubernetes, Helm 3 bỏ Tiller năm 2019. Trước Helm, đội copy YAML, dùng shell substitution hoặc công cụ template riêng. Helm đóng gói chart, template và release history; nhược điểm là Go template khó debug, values merge có bẫy và chart quá generic trở nên khó hiểu.

`Chart.yaml` định nghĩa `apiVersion`, `name`, `version`, `appVersion`, `dependencies`. `version` là version chart, `appVersion` chỉ metadata/default image tag. Đổi dependency cần chạy `helm dependency build` và nên commit lock file.

`values.yaml` là public API của chart. Template dùng `.Values`, `.Release`, `.Chart`, `.Capabilities`, `include`, `toYaml`, `nindent`. `values.template.yaml` và `Chart.template.yaml` phục vụ tạo chart service; `create-charts.sh` cơ khí hóa việc sinh chart. Các chart service phụ thuộc chart `backend` hoặc `ui`, rồi override image, port, ingress và env.

Nếu bỏ một values field có `default`/`required` khác nhau: field có default rơi về default, field được dereference trực tiếp có thể render `<no value>` hoặc fail. Nếu sửa type từ number sang string, `--set` có thể ép kiểu ngoài ý muốn; dùng `--set-string` cho tag, host hoặc value giống số/boolean.

Kiểm thử production: `helm lint`, `helm template` với từng environment, kubeconform/schema, policy-as-code, server-side dry-run và smoke test. Thêm `values.schema.json`, pin dependency, tránh `lookup` gây render không tái lập, không để Secret plaintext trong values.

Umbrella chart `yas-umbrella` tập hợp các service và `k8s/environments/dev|staging/values.yaml` là overlay môi trường. Đây là điểm Argo CD render. Sai key nesting như `product.backend.image.tag` khiến Helm im lặng dùng default, vì vậy phải kiểm tra manifest render.

## 3.3 GitOps và Argo CD

Thuật ngữ GitOps được Weaveworks phổ biến năm 2017. Trước đó CI thường giữ kubeconfig và trực tiếp chạy `kubectl apply`/`helm upgrade`. GitOps đặt desired state trong Git, controller trong cluster pull và reconcile. Ưu điểm là audit, drift correction, rollback qua Git và giảm cloud credential trong CI. Nhược điểm là secret management, controller blast radius, eventual consistency và khó xử lý imperative migration.

Argo CD là Kubernetes controller/CD tool ra mắt năm 2018. `Application` nối source Git/Helm với destination cluster/namespace.

Phân tích `dev-application.yaml` và `staging-application.yaml`:

- `apiVersion: argoproj.io/v1alpha1`, `kind: Application`: CRD phải tồn tại trước.
- `metadata.namespace: argocd`: nơi Argo CD tìm Application.
- `spec.project: default`: production nên tạo AppProject giới hạn repo, namespace và resource kind.
- `source.repoURL`: repo desired state. Đổi URL cần credential và có thể đổi toàn bộ nguồn tin cậy.
- `targetRevision: main`: controller theo branch mutable. Promotion tốt hơn dùng PR/tag/commit cho production.
- `path: k8s/charts/yas-umbrella`: root chart. Sai path làm ComparisonError.
- `helm.valueFiles`: relative với chart theo semantics Argo CD. Sai path làm render fail.
- `destination.server`: in-cluster API. Đổi cluster có thể deploy nhầm môi trường.
- `destination.namespace`: namespace mặc định, không override manifest có namespace cứng.
- `automated.prune: true`: xóa resource không còn trong Git. Cần thiết để chống rác nhưng nguy hiểm nếu Git xóa nhầm.
- `selfHeal: true`: đảo live edit. Khi debug phải hiểu `kubectl edit` sẽ bị revert.
- `CreateNamespace=true`: tạo namespace nhưng không thay thế policy/quota/labels quản trị namespace.
- `RespectIgnoreDifferences=true`: chỉ có tác dụng cùng `ignoreDifferences`; lạm dụng có thể che drift.

`Synced` chỉ nói live tương ứng desired revision; `Healthy` nói health resource. Có thể Synced nhưng Degraded, như image pull fail. Rollback GitOps chuẩn là revert/promote commit, không chỉ `kubectl rollout undo` vì self-heal có thể đưa lỗi trở lại.

Production nên tách config repo, dùng AppProject, least privilege, SSO/RBAC, signed commits, image updater có policy hoặc promotion PR, sync waves/hooks cho migration, notifications, backup, HA và pin image digest.

