# 6. Ngân hàng câu hỏi vấn đáp

## CI/CD và container

1. Vì sao `ci.yml` chưa đáp ứng tốt yêu cầu monorepo? Vì nó matrix build 14 service cho mọi push ngoài main, không có change detection. Các workflow service riêng có path filter nhưng pipeline image tổng vẫn chạy thừa.
2. Tại sao SHA tag tốt hơn branch tag? SHA bất biến và truy vết đúng source; branch tag bị ghi đè. Tốt nhất pin image digest.
3. `-DskipTests` khác `-Dmaven.test.skip=true` thế nào? Cái đầu thường vẫn compile test nhưng không chạy; cái sau bỏ cả compile test. Cả hai không được dùng thay quality gate.
4. Cache khác artifact? Cache tối ưu lần chạy sau và có thể eviction; artifact là output của run để tải hoặc chuyển job, có retention rõ.
5. Vì sao production pin action SHA? Tag action có thể bị di chuyển hoặc upstream bị compromise.
6. Jenkins controller và agent? Controller lập lịch, giữ cấu hình/trạng thái; agent thực thi build. Không chạy workload không tin cậy trên controller.
7. `EXPOSE` có mở port không? Không, chỉ metadata. `docker run -p`, Compose `ports`, Service/Ingress mới publish.
8. Vì sao exec-form entrypoint? Process app nhận signal trực tiếp, hỗ trợ graceful shutdown; shell form thường đặt shell làm PID 1.
9. ARG có giữ secret an toàn không? Không. Secret có thể lộ trong layer, history/cache/provenance; dùng BuildKit secret mount.
10. Compose `depends_on` có bảo đảm DB ready không? Không mặc định. Cần healthcheck condition hoặc application retry.

## Kubernetes, Helm và GitOps

11. Deployment, ReplicaSet và Pod liên hệ thế nào? Deployment quản revision/rollout, tạo ReplicaSet, ReplicaSet giữ số Pod.
12. `2/2 Running` là gì? Hai container trong một Pod ready, không phải hai Pod.
13. Service có ClusterIP nhưng curl fail, kiểm tra gì trước? EndpointSlice và selector, sau đó targetPort, readiness, NetworkPolicy/mesh.
14. Readiness khác liveness? Readiness loại Pod khỏi traffic, liveness restart container. Startup trì hoãn hai probe kia khi boot chậm.
15. Request khác limit? Request dùng scheduling và làm mẫu HPA utilization; limit là trần được kernel/runtime enforce.
16. Secret Kubernetes có mã hóa không? Base64 không phải encryption. Encryption at rest cần KMS/config API server, cộng RBAC và external secret manager.
17. `Chart.version` khác `appVersion`? Chart version version hóa package; appVersion là metadata về app và thường làm default image tag.
18. `helm lint` đủ chưa? Không. Cần render từng values, schema/policy validation, server dry-run và runtime test.
19. Argo CD Synced nhưng Degraded có mâu thuẫn không? Không. Desired state đã apply đúng nhưng workload có thể ImagePullBackOff hoặc probe fail.
20. `prune` nguy hiểm ở đâu? Git xóa nhầm hoặc app scope quá rộng có thể khiến controller xóa live resource.
21. Tại sao live `kubectl edit` bị mất? `selfHeal` phát hiện drift và đưa state về Git.
22. Rollback GitOps đúng cách? Revert/promotion commit hoặc đổi desired digest, để controller reconcile. Rollout undo đơn lẻ sẽ bị self-heal đảo lại.

## Istio và observability

23. PeerAuthentication và DestinationRule khác phía nào? PeerAuthentication áp inbound policy ở server; DestinationRule áp outbound traffic policy từ client sau route selection.
24. STRICT mTLS có phải authorization không? Không. Nó xác thực/mã hóa kênh. AuthorizationPolicy mới quyết định caller được phép.
25. Vì sao ALLOW policy có deny ngầm? Khi workload có ALLOW policy, request không match bất kỳ rule ALLOW nào bị từ chối.
26. Vì sao curl trong `istio-proxy` có thể fail plaintext? Envoy UID thường bypass iptables capture, nên curl không được bọc mTLS và STRICT peer reset.
27. `attempts: 3` tạo bao nhiêu lần gọi? Tối đa một lần đầu cộng ba retry, tức bốn, tùy timeout và retry condition.
28. Retry POST order có rủi ro gì? Tạo tác vụ trùng. Cần idempotency key hoặc chỉ retry operation an toàn.
29. Kiali có nằm trên đường request không? Không. Nó đọc config/state/Prometheus để visualize.
30. Metrics, logs và traces trả lời câu hỏi gì? Metrics phát hiện và định lượng, logs giải thích event chi tiết, traces chỉ ra đường đi và latency xuyên service.
31. Vì sao không đặt trace ID làm Loki label? Cardinality gần như mỗi request một giá trị, làm index phình lớn. Giữ trace ID trong log body.
32. Prometheus pull có ưu điểm gì? Target health quan sát được, cấu hình tập trung và backpressure tự nhiên hơn. Batch/short-lived job có thể cần Pushgateway theo trường hợp hẹp.
33. Grafana lưu metric không? Thông thường không. Nó query Prometheus/Loki/Tempo và lưu dashboard/user metadata.
34. Head sampling và tail sampling? Head quyết định sớm, rẻ nhưng có thể bỏ lỗi; tail xem toàn trace rồi quyết định, giữ lỗi tốt hơn nhưng tốn buffer và phức tạp.
35. Ba lỗi production lớn nhất của cấu hình local? Loki/Tempo filesystem single replica, auth/TLS tắt hoặc insecure, retention/capacity không đủ; thêm credential mặc định Grafana nếu còn dùng.

## Câu hỏi phản biện project

36. Project có Jenkinsfile không? Không có file được Git theo dõi. Nhóm triển khai GitHub Actions.
37. Java version có nhất quán không? Không. Đề/composite action dùng 21, pipeline image dùng 25. Cần chọn version theo root POM/runtime support và thống nhất.
38. Mọi namespace đã STRICT mTLS chưa? Không. `yas` và `yas-developer` strict, `dev`/`staging` permissive trong manifest hiện tại.
39. Product chỉ cho order gọi phải không? Manifest áp dụng cho phép năm caller thực tế. Kịch bản order-only nằm trong file demo riêng.
40. Vì sao developer lean profile không dùng để demo mesh? Nó label namespace injection disabled và thêm annotation không inject sidecar.
41. `latest` ở developer build có vấn đề gì? Race, cache và không truy vết. Resolve SHA của main rồi dùng digest/SHA.
42. Nếu Argo CD và workflow cùng Helm upgrade resource thì sao? Hai writer gây drift/race. Với GitOps, pipeline chỉ cập nhật Git, Argo CD apply.
43. Đề yêu cầu NodePort nhưng project dùng Ingress có sai không? Cần giải trình. Ingress + LoadBalancer đáp ứng truy cập tốt hơn trên DOKS, nhưng khác implementation minh họa của đề và phải có hosts/DNS evidence.
44. Screenshot pipeline xanh chứng minh gì? Chỉ run/revision cụ thể. Không tự chứng minh branch protection, coverage threshold, mọi path filter hoặc production readiness.
45. Nếu ServiceMonitor tồn tại mà không có metrics? Kiểm tra Prometheus Operator selector, namespace selector, Service label, named port, endpoint path, TLS/auth và target status.

