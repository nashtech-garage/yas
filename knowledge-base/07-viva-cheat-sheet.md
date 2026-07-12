# DevOps Viva Cheat Sheet

## 1. CI

- Khái niệm: Continuous Integration, tích hợp code liên tục.
- Ý chính: Mỗi thay đổi được build, test và kiểm tra chất lượng tự động.
- Khi nào dùng: Push branch, mở hoặc cập nhật pull request.
- Vì sao cần: Phát hiện lỗi sớm và giữ nhánh chính ổn định.
- Một ví dụ: Thay đổi `product/**` chỉ chạy test và build product.
- Một câu hỏi thường gặp: CI xanh có chứng minh ứng dụng deploy thành công không? Không.

## 2. CD

- Khái niệm: Continuous Delivery hoặc Continuous Deployment.
- Ý chính: Đưa artifact đã kiểm chứng đến môi trường đích một cách lặp lại.
- Khi nào dùng: Sau CI, khi merge `main`, tạo release hoặc phê duyệt deployment.
- Vì sao cần: Giảm thao tác tay, drift và lỗi triển khai.
- Một ví dụ: Tag `v1.2.3` cập nhật image staging rồi Argo CD đồng bộ.
- Một câu hỏi thường gặp: Delivery khác Deployment? Delivery còn bước duyệt production, Deployment tự động hoàn toàn.

## 3. Git

- Khái niệm: Hệ thống quản lý phiên bản phân tán.
- Ý chính: Commit là snapshot; branch là con trỏ; merge/rebase kết hợp lịch sử.
- Khi nào dùng: Quản lý source, manifest, review và rollback.
- Vì sao cần: Audit, cộng tác và truy vết thay đổi.
- Một ví dụ: Revert commit GitOps để Argo CD đưa cluster về desired state cũ.
- Một câu hỏi thường gặp: `merge` khác `rebase`? Merge giữ nhánh lịch sử, rebase viết lại base commit.

## 4. Docker

- Khái niệm: Nền tảng build và chạy OCI container image.
- Ý chính: Image bất biến tạo container process cô lập bằng kernel.
- Khi nào dùng: Đóng gói service và tạo môi trường chạy nhất quán.
- Vì sao cần: Giảm khác biệt giữa máy developer, CI và cluster.
- Một ví dụ: Build `yas-product:<commit-sha>` từ `product/Dockerfile`.
- Một câu hỏi thường gặp: Image khác container? Image là template chỉ đọc, container là instance đang chạy.

## 5. Kubernetes

- Khái niệm: Nền tảng orchestration container theo desired state.
- Ý chính: Controller liên tục reconcile actual state về desired state.
- Khi nào dùng: Chạy workload phân tán cần scale, self-heal và rollout.
- Vì sao cần: Chuẩn hóa scheduling, discovery, configuration và lifecycle.
- Một ví dụ: Deployment product giữ một Pod sẵn sàng trong namespace `yas`.
- Một câu hỏi thường gặp: Kubernetes có tự sửa lỗi ứng dụng không? Chỉ khôi phục theo probe/desired state, không sửa business bug.

## 6. Helm

- Khái niệm: Package manager và template engine cho Kubernetes.
- Ý chính: Chart cộng values tạo manifest và release.
- Khi nào dùng: Nhiều service hoặc môi trường dùng chung cấu trúc manifest.
- Vì sao cần: Giảm lặp YAML và quản lý cấu hình theo môi trường.
- Một ví dụ: Chart product phụ thuộc chart backend và override image/tag.
- Một câu hỏi thường gặp: `Chart.version` khác `appVersion`? Một cái version chart, một cái metadata version ứng dụng.

## 7. GitHub Actions

- Khái niệm: Nền tảng automation dựa trên event của GitHub.
- Ý chính: Workflow gồm event, jobs, matrix, steps, permissions và artifacts.
- Khi nào dùng: CI, security scan, build image và cập nhật GitOps.
- Vì sao cần: Automation nằm gần repository và pull request.
- Một ví dụ: `ci.yml` matrix build 14 image rồi push GHCR.
- Một câu hỏi thường gặp: Cache khác artifact? Cache tăng tốc lần chạy sau, artifact lưu output của run.

## 8. Jenkins

- Khái niệm: Automation server với controller, agent và plugin.
- Ý chính: Pipeline as Code thường nằm trong Jenkinsfile.
- Khi nào dùng: Cần self-host, agent tùy biến hoặc tích hợp hệ thống nội bộ.
- Vì sao cần: Điều phối build, test và deployment ngoài nền tảng Git hosting.
- Một ví dụ: Multibranch Pipeline tự tạo job cho từng branch.
- Một câu hỏi thường gặp: Project này dùng Jenkins không? Không, repository hiện dùng GitHub Actions và không có Jenkinsfile.

## 9. GitOps

- Khái niệm: Git là source of truth cho desired state vận hành.
- Ý chính: Pipeline cập nhật Git, controller trong cluster pull và reconcile.
- Khi nào dùng: Quản lý Kubernetes nhiều môi trường cần audit và drift correction.
- Vì sao cần: Rollback rõ ràng và giảm credential cluster trong CI.
- Một ví dụ: Workflow đổi image tag trong values, Argo CD tự sync.
- Một câu hỏi thường gặp: Có nên vừa `helm upgrade` từ CI vừa dùng Argo CD? Không, sẽ có hai writer.

## 10. Argo CD

- Khái niệm: GitOps continuous delivery controller cho Kubernetes.
- Ý chính: Application nối Git source với cluster destination.
- Khi nào dùng: Tự động sync, self-heal và prune manifest từ Git.
- Vì sao cần: Phát hiện drift và cung cấp trạng thái deployment tập trung.
- Một ví dụ: `dev-yas-app` theo dõi chart umbrella trên nhánh `main`.
- Một câu hỏi thường gặp: `Synced` có nghĩa `Healthy` không? Không.

## 11. Istio

- Khái niệm: Service mesh dùng Istiod và Envoy proxy.
- Ý chính: Quản lý mTLS, routing, retry, authorization và telemetry.
- Khi nào dùng: Microservices cần policy và quan sát traffic nhất quán.
- Vì sao cần: Tách cross-cutting networking khỏi business code.
- Một ví dụ: VirtualService retry tax khi gặp 5xx.
- Một câu hỏi thường gặp: `attempts: 3` có thể tạo bao nhiêu request? Tối đa 4 tính cả lần đầu.

## 12. Observability

- Khái niệm: Khả năng suy luận trạng thái bên trong từ output hệ thống.
- Ý chính: Kết hợp metrics, logs, traces, correlation và alerting.
- Khi nào dùng: Theo dõi health, latency, lỗi và điều tra sự cố.
- Vì sao cần: Monitoring biết điều gì sai, observability hỗ trợ tìm vì sao sai.
- Một ví dụ: Từ Grafana metric lỗi chuyển sang Tempo trace rồi tìm Loki log cùng trace ID.
- Một câu hỏi thường gặp: Cài đủ ba backend đã là observable chưa? Chưa, còn cần instrumentation và correlation.

## 13. Prometheus

- Khái niệm: Hệ thống pull metrics và time-series database.
- Ý chính: Scrape target, lưu series, query PromQL và đánh giá alert rule.
- Khi nào dùng: Metrics hạ tầng, ứng dụng, Kubernetes và Envoy.
- Vì sao cần: Theo dõi xu hướng, SLI và cảnh báo định lượng.
- Một ví dụ: Query rate lỗi từ `istio_requests_total`.
- Một câu hỏi thường gặp: Label cardinality cao gây gì? Tăng memory, storage và thời gian query.

## 14. Grafana

- Khái niệm: Nền tảng dashboard, exploration và alert visualization.
- Ý chính: Query datasource như Prometheus, Loki và Tempo.
- Khi nào dùng: Quan sát metric, log và trace trong một giao diện.
- Vì sao cần: Liên kết tín hiệu và cung cấp dashboard vận hành.
- Một ví dụ: Dashboard YAS hiển thị request rate, error rate và latency.
- Một câu hỏi thường gặp: Grafana có mặc định lưu metrics không? Không.

## 15. Loki

- Khái niệm: Log backend index label, không full-text index toàn nội dung.
- Ý chính: LogQL query stream theo label rồi lọc nội dung.
- Khi nào dùng: Centralized logging tích hợp Grafana và Kubernetes.
- Vì sao cần: Tìm log nhiều Pod mà không SSH từng node.
- Một ví dụ: `{namespace="yas", app="product"} |= "ERROR"`.
- Một câu hỏi thường gặp: Có nên dùng trace ID làm Loki label? Không, cardinality quá cao.

## 16. Tempo

- Khái niệm: Distributed tracing backend của Grafana Labs.
- Ý chính: Lưu trace/span, thường trên object storage, query bằng trace ID/TraceQL.
- Khi nào dùng: Phân tích đường đi request và latency xuyên service.
- Vì sao cần: Xác định dependency hoặc span gây chậm/lỗi.
- Một ví dụ: Trace order qua inventory, tax và payment.
- Một câu hỏi thường gặp: Head sampling khác tail sampling? Head quyết định sớm, tail quyết định sau khi thấy toàn trace.

## 17. Kiali

- Khái niệm: Console quan sát và kiểm tra cấu hình Istio.
- Ý chính: Dùng Kubernetes state và Prometheus metrics để dựng service graph.
- Khi nào dùng: Kiểm tra topology, traffic, mTLS và lỗi cấu hình mesh.
- Vì sao cần: Làm luồng service-to-service dễ quan sát.
- Một ví dụ: Graph namespace `yas` hiển thị cạnh order đến tax.
- Một câu hỏi thường gặp: Kiali có nằm trên data path không? Không.

## 18. Security

- Khái niệm: Bảo vệ confidentiality, integrity và availability.
- Ý chính: Least privilege, defense in depth, identity, encryption và supply-chain security.
- Khi nào dùng: Mọi lớp từ Git, CI, image đến runtime và network.
- Vì sao cần: Giảm xác suất và blast radius của tấn công.
- Một ví dụ: OIDC cấp credential cloud ngắn hạn thay token dài hạn.
- Một câu hỏi thường gặp: Kubernetes Secret có an toàn vì base64 không? Không, base64 không phải mã hóa.

## 19. Networking

- Khái niệm: Cơ chế địa chỉ, kết nối và định tuyến giữa client, node, Pod và service.
- Ý chính: DNS, IP, port, protocol, routing, load balancing và policy.
- Khi nào dùng: Expose ứng dụng hoặc debug service-to-service.
- Vì sao cần: Pod động cần endpoint ổn định và kiểm soát luồng traffic.
- Một ví dụ: Ingress host chuyển traffic đến ClusterIP Service rồi EndpointSlice chọn Pod.
- Một câu hỏi thường gặp: `port`, `targetPort`, `nodePort` khác nhau thế nào? Service port, Pod port và node port.

## 20. Linux

- Khái niệm: Hệ điều hành nền của phần lớn server và container.
- Ý chính: Process, file descriptor, permission, signal, namespace và cgroup.
- Khi nào dùng: Vận hành node, container, runner và troubleshooting.
- Vì sao cần: Container dựa trên primitive của Linux kernel.
- Một ví dụ: SIGTERM cho ứng dụng thời gian graceful shutdown trước SIGKILL.
- Một câu hỏi thường gặp: Load average có phải CPU percentage không? Không, nó gồm runnable và uninterruptible tasks.

## 21. Container

- Khái niệm: Process cô lập dùng image, namespace và cgroup.
- Ý chính: Chia sẻ kernel host nhưng có filesystem/network/process view riêng.
- Khi nào dùng: Đóng gói workload nhẹ và triển khai nhất quán.
- Vì sao cần: Khởi động nhanh và mật độ cao hơn VM trong nhiều trường hợp.
- Một ví dụ: Pod product chứa app container và Envoy sidecar.
- Một câu hỏi thường gặp: Container có phải VM không? Không, container chia sẻ kernel host.

## 22. Deployment

- Khái niệm: Quá trình đưa version ứng dụng vào môi trường; trong Kubernetes còn là resource quản ReplicaSet.
- Ý chính: Rollout có strategy, health check, revision và rollback.
- Khi nào dùng: Phát hành image/config mới.
- Vì sao cần: Thay đổi có kiểm soát và giữ availability.
- Một ví dụ: RollingUpdate product tạo ReplicaSet mới rồi giảm ReplicaSet cũ.
- Một câu hỏi thường gặp: Vì sao sửa image tag mà Pod không đổi? Template chưa đổi, tag mutable bị cache hoặc controller chưa sync.

## 23. Troubleshooting

- Khái niệm: Quy trình xác định symptom, phạm vi và root cause bằng bằng chứng.
- Ý chính: Kiểm tra từ ngoài vào trong: DNS, ingress, service, endpoint, Pod, app, dependency.
- Khi nào dùng: Deployment degraded, timeout, 5xx, crash loop hoặc thiếu telemetry.
- Vì sao cần: Tránh sửa ngẫu nhiên và làm sự cố nặng hơn.
- Một ví dụ: Service không trả lời thì kiểm tra EndpointSlice trước khi restart Pod.
- Một câu hỏi thường gặp: Bước đầu khi Pod CrashLoopBackOff? `describe` và đọc log container trước đó.

# Top 100 command phải nhớ

## Git

1. `git status --short`
2. `git branch --show-current`
3. `git log --oneline --graph --decorate -20`
4. `git diff`
5. `git diff --staged`
6. `git add <file>`
7. `git commit -m "type: message"`
8. `git fetch --all --prune`
9. `git pull --rebase origin <branch>`
10. `git push -u origin <branch>`
11. `git switch -c <branch>`
12. `git merge <branch>`
13. `git rebase origin/main`
14. `git revert <commit-sha>`
15. `git show <commit-sha>`

## Linux và networking

16. `pwd`
17. `ls -lah`
18. `find . -type f -name '<pattern>'`
19. `rg '<pattern>' <path>`
20. `ps aux`
21. `top`
22. `free -h`
23. `df -h`
24. `du -sh <path>`
25. `journalctl -u <service> -n 100 --no-pager`
26. `systemctl status <service>`
27. `chmod +x <script>`
28. `curl -v http://<host>:<port>/<path>`
29. `curl -sS -o /dev/null -w '%{http_code}\n' http://<url>`
30. `ss -lntp`
31. `dig <hostname>`
32. `nslookup <hostname>`
33. `ping -c 4 <host>`
34. `traceroute <host>`
35. `openssl s_client -connect <host>:443 -servername <host>`

## Docker và Compose

36. `docker version`
37. `docker info`
38. `docker build -t <repo>/<image>:<tag> <context>`
39. `docker image ls`
40. `docker image inspect <image>:<tag>`
41. `docker run --rm -p <host-port>:<container-port> <image>:<tag>`
42. `docker ps`
43. `docker ps -a`
44. `docker logs -f --tail 100 <container>`
45. `docker exec -it <container> sh`
46. `docker inspect <container>`
47. `docker stats`
48. `docker stop <container>`
49. `docker rm <container>`
50. `docker compose config`
51. `docker compose up -d`
52. `docker compose ps`
53. `docker compose logs -f <service>`
54. `docker compose down`
55. `docker compose pull`

## Kubernetes

56. `kubectl config current-context`
57. `kubectl config get-contexts`
58. `kubectl get namespaces`
59. `kubectl get nodes -o wide`
60. `kubectl get pods -A`
61. `kubectl get pods -n <namespace> -o wide`
62. `kubectl get deployment,service,ingress -n <namespace>`
63. `kubectl get endpointslice -n <namespace>`
64. `kubectl describe pod -n <namespace> <pod>`
65. `kubectl logs -n <namespace> <pod> -c <container> --tail=100`
66. `kubectl logs -n <namespace> <pod> -c <container> --previous`
67. `kubectl exec -it -n <namespace> <pod> -c <container> -- sh`
68. `kubectl apply -f <manifest.yaml>`
69. `kubectl diff -f <manifest.yaml>`
70. `kubectl delete -f <manifest.yaml>`
71. `kubectl rollout status deployment/<name> -n <namespace>`
72. `kubectl rollout history deployment/<name> -n <namespace>`
73. `kubectl rollout undo deployment/<name> -n <namespace>`
74. `kubectl scale deployment/<name> -n <namespace> --replicas=<count>`
75. `kubectl top nodes`
76. `kubectl top pods -n <namespace>`
77. `kubectl get events -n <namespace> --sort-by=.lastTimestamp`
78. `kubectl port-forward -n <namespace> svc/<service> <local-port>:<service-port>`
79. `kubectl auth can-i <verb> <resource> -n <namespace>`
80. `kubectl explain deployment.spec.template.spec.containers`

## Helm, Argo CD và Istio

81. `helm lint <chart-directory>`
82. `helm dependency build <chart-directory>`
83. `helm template <release> <chart-directory> -f <values.yaml>`
84. `helm upgrade --install <release> <chart-directory> -n <namespace> --create-namespace -f <values.yaml>`
85. `helm list -A`
86. `helm status <release> -n <namespace>`
87. `helm history <release> -n <namespace>`
88. `helm rollback <release> <revision> -n <namespace>`
89. `argocd app list`
90. `argocd app get <application>`
91. `argocd app diff <application>`
92. `argocd app sync <application>`
93. `argocd app wait <application> --sync --health`
94. `istioctl analyze -A`
95. `istioctl proxy-status`
96. `istioctl proxy-config routes -n <namespace> <pod>`
97. `kubectl get peerauthentication,authorizationpolicy -A`
98. `kubectl get virtualservice,destinationrule -A`

## Observability

99. `curl -s http://<prometheus>:9090/api/v1/query --data-urlencode 'query=up'`
100. `kubectl logs -n <namespace> <pod> -c istio-proxy | rg 'rbac_access_denied| 5[0-9][0-9] '`
