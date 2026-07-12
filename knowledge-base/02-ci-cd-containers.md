# 2. CI/CD, Jenkins và container

## 2.1 GitHub Actions

### Mục đích và lịch sử

GitHub Actions ra mắt công khai năm 2019 để đặt automation gần source, event và pull request. Trước đó nhóm thường dùng Jenkins/TeamCity/Travis CI, webhook từ GitHub sang server CI và tự quản credential/runner. Ưu điểm là tích hợp GitHub, marketplace action, matrix, OIDC và hosted runner. Nhược điểm là vendor coupling, YAML khó tái sử dụng ở quy mô lớn, supply-chain risk từ third-party action và chi phí runner.

### Mô hình thực thi

`on` tạo workflow run. Một run có `jobs`; job chạy trên runner độc lập; `steps` dùng action hoặc shell. Output trong step không tự tồn tại ở job khác, phải dùng artifact/output/cache. `needs` tạo DAG. `permissions` cấp quyền cho `GITHUB_TOKEN`. `environment` có thể thêm approval, secret và protection rule.

### Field quan trọng

- `on.push.branches/paths`: AND với nhau. Bỏ `paths` làm chạy thừa; lọc quá hẹp làm bỏ sót shared dependency.
- `workflow_dispatch.inputs`: hợp với developer deployment. Input là dữ liệu không tin cậy, phải quote và validate.
- `permissions`: mặc định tối thiểu. `contents: write` chỉ cần cho job commit GitOps; không nên cấp toàn workflow nếu không cần.
- `strategy.matrix`: mở rộng một job thành nhiều service. `fail-fast: false` thu thập đủ kết quả nhưng tiêu tốn runner.
- `uses: owner/action@v4`: pin major tiện cập nhật nhưng production an toàn hơn khi pin full commit SHA và dùng Dependabot.
- `if`: skip ở runtime. Sai biểu thức có thể khiến security step không chạy mà workflow vẫn xanh.
- `cache`: tăng tốc, không phải artifact. Không cache secret hoặc output không tin cậy.
- `concurrency`: repository hiện nên bổ sung cho deploy để hai run không cập nhật cùng environment đồng thời.

### Phân tích workflow thực tế

`ci.yml` chạy mọi push ngoài `main` và tag `v*`, sau đó matrix build 14 image. Nó tạo tag SHA và tag branch đã sanitize, push GHCR. Điểm đúng là SHA immutable và cache tách theo image. Điểm yếu là không có `paths` hoặc change detection, không đáp ứng tối ưu monorepo; Java dùng 25 thay vì baseline 21; bước Maven dùng `-DskipTests`; registry owner hard-code `23122004`; toàn matrix build dù chỉ một service đổi.

Các workflow `*-ci.yaml` là CI theo service. Chúng cần được xem là nơi test, JaCoCo, Sonar và artifact xảy ra. Production nên chuyển logic chung thành reusable workflow `workflow_call`, không sao chép 20 YAML. Composite action `actions/action.yaml` chỉ setup JDK 21 và cache Sonar, không tự test hay enforce coverage.

`cd-developer.yml` có input branch cho 14 service và action deploy/cleanup. `resolve_tag` dùng `git ls-remote` lấy SHA. Logic phụ thuộc CI đã push đúng tag SHA. `main` trả `latest`, tạo race và không reproducible. Nên resolve SHA của main giống mọi branch. Profile lean tắt Istio và giảm resource, hữu ích cho cluster nhỏ nhưng không thể dùng để chứng minh service mesh. Cleanup xóa cả namespace, đúng với môi trường ephemeral nhưng cần environment approval và TTL/quota.

`deploy-dev.yml` build/push tag `${sha}` và `main`, rồi cập nhật GitOps values. `paths-ignore: k8s/environments/**` ngăn commit bot tự kích hoạt vòng lặp. Rủi ro: matrix vẫn build toàn bộ, `-DskipTests`, bot push thẳng `main`, mutable tag và thiếu concurrency. Production nên build một lần, promote digest, mở PR vào config repo và ký commit.

`deploy-staging.yml` phải trigger tag release, build đúng release/digest và cập nhật staging values. Kiểm tra đặc biệt `github.ref_name`, semver validation, quyền contents write và branch mà Argo CD theo dõi.

`codeql.yml` phân tích code theo ngôn ngữ. Bỏ `security-events: write` có thể không upload SARIF. `gitleaks-check.yaml` phát hiện secret trong history/diff, nhưng allowlist trong `.gitleaksignore` phải được review. `charts-ci.yaml` nên chạy `helm lint`, render nhiều values và schema validation; lint thành công không chứng minh resource chạy được.

### Production best practice

- Pin action SHA, bật Dependabot, dùng OIDC thay long-lived cloud token.
- Branch protection yêu cầu đúng tên status check, CODEOWNERS và hai approval.
- Change detection phải tính shared modules, root POM, workflow và Docker base.
- Tách CI khỏi CD. CI tạo image digest, SBOM, provenance, signature và scan. CD promote digest đã kiểm chứng.
- Dùng `concurrency: group: deploy-${environment}` và cancel policy có chủ ý.
- Environment approval cho staging/prod, audit deployment URL và rollback.
- Không dùng `latest` làm desired state. Tag dễ đọc có thể tồn tại, nhưng manifest pin digest.

## 2.2 Jenkins

Jenkins bắt đầu từ Hudson năm 2004, fork thành Jenkins năm 2011. Trước Jenkins, build thường chạy bằng cron, script trên shared server hoặc thao tác tay. Jenkins cung cấp controller, agent, plugin và Pipeline as Code.

Repository hiện không có Jenkinsfile được Git theo dõi. Nếu giảng viên hỏi “Jenkins pipeline ở đâu”, đáp án đúng là nhóm chọn GitHub Actions. Không được mô tả một Jenkinsfile không tồn tại.

Declarative Pipeline điển hình có `pipeline`, `agent`, `options`, `parameters`, `environment`, `stages`, `steps`, `post`. Multibranch Pipeline scan branch và tự tạo job. Webhook tốt hơn polling. Credentials Binding chỉ inject trong scope, nhưng shell tracing vẫn có thể làm lộ secret.

So với Actions, Jenkins có ưu thế self-host, plugin và tùy biến sâu; nhược điểm là vận hành controller, upgrade plugin, backup, agent hygiene và attack surface. Production dùng Configuration as Code, ephemeral Kubernetes agents, plugin allowlist, RBAC, SSO, controller không chạy build, shared library pin version và backup/restore test.

Câu hỏi bẫy: controller khác agent thế nào; `stash` khác artifact; multibranch khác parameterized job; tại sao không build Docker socket trên controller; webhook secret dùng để làm gì; khi nào dùng `disableConcurrentBuilds`.

## 2.3 Docker và Dockerfile

Docker phổ biến container từ 2013 dựa trên Linux namespaces, cgroups và image layers. Trước container, nhóm dùng bare metal, VM hoặc chroot. Container nhẹ và đóng gói nhất quán, nhưng chia sẻ kernel, không phải security boundary tuyệt đối và dễ phát sinh image supply-chain risk.

Dockerfile backend nhận jar do Maven build bên ngoài rồi đóng gói runtime. Quan hệ là workflow phải chạy Maven trước, Docker build context phải chứa jar đúng path, chart phải dùng đúng repository/tag và app phải listen đúng port.

Field/instruction quan trọng:

- `FROM`: base image và attack surface. Bỏ không tạo được stage; đổi major JRE có thể phá runtime. Pin digest và dùng slim/distroless có kiểm chứng.
- `WORKDIR`: chuẩn hóa path. Thiếu nó làm `COPY`/entrypoint phụ thuộc cwd.
- `COPY`: tạo layer và đưa artifact vào image. Copy `.` dễ mang secret, source và cache; cần `.dockerignore`.
- `ARG`: build-time, không dùng cho secret vì có thể lộ trong history/provenance.
- `ENV`: runtime default, bị image kế thừa. Không lưu password.
- `RUN`: tạo layer. Gom hợp lý và xóa package cache trong cùng layer.
- `USER`: bỏ thường chạy root. Production dùng UID không root, filesystem read-only và drop capability.
- `EXPOSE`: metadata, không publish port. Service/Compose mới tạo networking.
- `ENTRYPOINT` và `CMD`: exec form nhận signal đúng. Shell form có thể làm PID 1 không forward SIGTERM.
- `HEALTHCHECK`: có ích ngoài Kubernetes; trong K8s probes mới quyết định restart/readiness.

UI Dockerfile thường multi-stage: Node build, sau đó runner chứa standalone output. Build-time public env của Next.js có thể bị bake vào bundle, không thể đổi chỉ bằng ConfigMap runtime nếu code không đọc runtime config.

Production: multi-stage, non-root, pin digest, scan CVE, SBOM, sign image, reproducible build, no secret, labels OCI, resource limit và graceful shutdown. Lỗi sinh viên: dùng JDK thay JRE ở runtime, copy toàn repo, dùng `latest`, thiếu `.dockerignore`, chạy root, image chứa Maven cache, Java heap lớn hơn container limit.

## 2.4 Docker Compose

Compose khai báo multi-container local environment. Trước Compose, developer chạy nhiều `docker run` hoặc script thủ công. `docker-compose.yml` là stack chính; `docker-compose.search.yml` thêm Elasticsearch/search; `docker-compose.o11y.yml` thêm collector, Prometheus, Loki, Tempo, Grafana.

Field quan trọng: `services`, `image/build`, `ports`, `expose`, `environment`, `env_file`, `volumes`, `networks`, `depends_on`, `healthcheck`, `profiles`, `restart`. `depends_on` chỉ bảo đảm thứ tự start trừ khi dùng condition health, không bảo đảm ứng dụng sẵn sàng. Bỏ named volume mất dữ liệu khi recreate; bind mount sai permission làm container fail; publish database `0.0.0.0` tăng attack surface.

Compose phù hợp local/integration, không thay Kubernetes production. Production phải quản secret, HA, rolling update, scheduling, policy và persistent storage bằng nền tảng orchestration.

