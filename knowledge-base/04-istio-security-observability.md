# 4. Istio, security và observability

## 4.1 Istio và service mesh

Istio được Google, IBM và Lyft công bố năm 2017, xây trên Envoy. Trước service mesh, ứng dụng tự cài TLS, retry, circuit breaker, discovery và telemetry bằng library hoặc API gateway. Mesh đưa logic L7 vào data plane proxy và control plane phân phối cấu hình. Ưu điểm là policy nhất quán và telemetry không phụ thuộc ngôn ngữ; nhược điểm là thêm latency, memory, certificate/control-plane complexity và failure mode khó debug.

Istiod nhận Kubernetes/Istio resources, tạo xDS config cho Envoy. Sidecar interception dùng iptables; traffic từ UID Envoy có thể bypass capture. Vì vậy curl trong container `istio-proxy` không phải phép thử workload-to-workload chuẩn.

### Gateway

Istio Gateway cấu hình listener Envoy ở ingress/egress gateway: `selector` chọn gateway workload, `servers.port` định nghĩa number/name/protocol, `hosts` giới hạn SNI/Host, `tls` chọn mode và credential. Gateway không tự route backend, cần VirtualService bind bằng `gateways`. Project chủ yếu dùng NGINX Ingress cho public entrypoint, nên không được tự nhận là Istio Ingress Gateway nếu manifest không dùng nó.

### VirtualService

`hosts` xác định destination, `gateways` chọn mesh/gateway, `http.match` chọn request, `route.destination` chọn host/subset/port, `retries`, `timeout`, `fault`, `rewrite` điều khiển L7. Project có `tax-retry` 3 retries, 5 giây mỗi try, tổng timeout 20 giây; `order-retry` 3 retries, 10 giây mỗi try, tổng 30 giây.

`attempts: 3` nghĩa tối đa ba lần retry sau lần đầu, có thể thành bốn request server. Retry 5xx cho POST không idempotent có thể tạo đơn hàng hoặc thanh toán trùng. Production chỉ retry method/operation idempotent, dùng idempotency key, retry budget, jitter và timeout nhỏ hơn deadline caller.

### DestinationRule

DestinationRule áp policy sau khi route chọn host. `host` phải khớp service; `subsets` nối version label; `trafficPolicy.tls.mode: ISTIO_MUTUAL` dùng certificate do Istio cấp; connection pool giới hạn connection/request. Project lặp rule cho 10 service. Thiếu rule không nhất thiết làm mTLS hỏng khi auto mTLS hoạt động, nhưng explicit rule làm ý đồ rõ. Sai host khiến policy không áp; `maxRequestsPerConnection: 10` tăng churn nếu quá thấp.

### PeerAuthentication

PeerAuthentication điều khiển mTLS inbound. `STRICT` chỉ nhận mTLS, `PERMISSIVE` nhận cả plaintext và mTLS, `DISABLE` tắt. File hiện đặt `yas` và `yas-developer` STRICT, nhưng `dev` và `staging` PERMISSIVE. Tên resource giống nhau được phép vì namespace khác nhau. Production chuyển dần permissive sang strict sau khi xác nhận mọi workload có sidecar hoặc ambient participation.

### AuthorizationPolicy

Policy chọn workload đích bằng selector. Khi có ít nhất một `ALLOW`, request không match bị deny ngầm. `source.principals` chỉ đáng tin khi mTLS cung cấp SPIFFE identity. Project chọn product và cho phép service account order, cart, search, storefront-bff, backoffice-bff. Nếu label selector không khớp, policy không bảo vệ workload. Nếu bỏ một principal hợp lệ, chức năng tương ứng nhận 403. Nếu thêm wildcard, policy mất ý nghĩa least privilege.

Production dùng service account riêng từng workload, default deny theo namespace, allow theo method/path khi cần, dry-run/audit, policy test và kiểm soát egress. Không nhầm authentication với authorization: PeerAuthentication xác minh kênh/identity, AuthorizationPolicy quyết định identity được làm gì.

### Kiali

Kiali đọc Istio config, Kubernetes state và Prometheus metrics để dựng graph. Nó không nằm trên data path. Graph trống thường do chưa có traffic, Prometheus không scrape Envoy, time range sai hoặc label telemetry thiếu. Padlock cho biết telemetry đánh dấu mTLS, nhưng bằng chứng security vẫn cần config và test. Production bảo vệ Kiali bằng SSO/RBAC, không expose anonymous admin.

## 4.2 Ba trụ cột telemetry

Metrics là chuỗi số theo thời gian, tốt cho alert và xu hướng nhưng high-cardinality label gây nổ chi phí. Logs là event rời rạc giàu chi tiết nhưng tìm kiếm tốn storage. Traces nối span xuyên service, tốt cho latency và dependency nhưng cần context propagation/sampling. Observability không chỉ là cài ba backend, mà là khả năng đặt câu hỏi mới từ output hệ thống.

Correlation yêu cầu trace ID trong log, exemplars từ metric sang trace và resource attributes thống nhất như `service.name`, namespace, environment, version. Không đưa user ID, order ID hoặc URL raw có cardinality cao vào metric labels.

## 4.3 OpenTelemetry Collector

Collector tách nhận, xử lý và xuất telemetry. File local có OTLP gRPC 5555, HTTP 6666; trace sang Tempo và servicegraph; metrics remote-write sang Prometheus; logs sang Loki. `batch` giảm request overhead. `resource/loki` biến service name/namespace thành label. CORS `http://*` và `https://*`, TLS insecure, endpoint bind mọi interface phù hợp lab nhưng quá rộng cho production.

Nếu bỏ receiver, SDK không gửi được. Bỏ processor batch tăng overhead. Bỏ exporter mất signal tương ứng. Bỏ một pipeline không có đường từ receiver đến exporter dù component vẫn khai báo. Production dùng TLS/mTLS, auth extension, memory limiter, queued retry, persistent queue, load balancing, backpressure monitoring và cardinality control.

## 4.4 Prometheus

Prometheus bắt đầu tại SoundCloud năm 2012, vào CNCF năm 2016. Trước đó phổ biến Nagios, Graphite, StatsD và vendor monitoring. Prometheus pull metrics, lưu TSDB, query PromQL và đánh giá alert rule.

`global.scrape_interval` 2 giây trong local tạo dữ liệu dày và tốn tài nguyên. `scrape_configs.job_name`, `targets`, `metrics_path`, `scheme`, timeout quyết định scrape. Remote write receiver phải được bật nếu collector ghi `/api/v1/write`. Production dùng ServiceMonitor/PodMonitor với Prometheus Operator, recording rules, Alertmanager, HA pair, retention/capacity và long-term store như Thanos/Mimir khi cần.

Lỗi thường gặp: alert theo CPU limit thay vì saturation/request; label chứa path động; scrape port Service nhưng selector không có endpoint; nhầm application metric với Envoy metric; không có SLO. Golden signals: latency, traffic, errors, saturation.

## 4.5 Grafana

Grafana ra đời năm 2014 như dashboard frontend cho time-series data. Nó query datasource, không phải mặc định là nơi lưu metric/log/trace. Provisioning datasource và dashboard trong `docker/grafana/provisioning` giúp tái lập. UID datasource/dashboard phải ổn định để link không vỡ.

Production không dùng `admin/admin`, lưu credential trong secret manager, SSO/RBAC, folder permission, provision dashboard từ Git, backup database, alert contact point an toàn và dashboard theo SLO. Dashboard đẹp không thay alert và runbook.

## 4.6 Loki

Loki được Grafana Labs công bố năm 2018, index labels thay vì full text để giảm chi phí so với ELK trong nhiều workload. Trước Loki, đội dùng file/SSH, syslog, ELK/EFK hoặc vendor SaaS. Ưu điểm là tích hợp Grafana và mô hình label giống Prometheus; nhược điểm là truy vấn nội dung lớn kém hơn search engine full-text và rất nhạy với cardinality label.

File local tắt auth, replication factor 1, ring in-memory, object store filesystem, schema v11, retention delete tắt. Đây là single-node lab. Bỏ persistent mount mất log khi container recreate. Production dùng object storage, schema hiện hành, retention rõ, multi-tenant auth, replication, compactor, limits và label thấp cardinality. Không label trace ID/request ID, hãy giữ trong log body.

## 4.7 Tempo

Tempo được Grafana Labs giới thiệu năm 2020 để lưu trace chi phí thấp trên object storage, thường không index mọi attribute. Trước đó dùng Jaeger/Zipkin hoặc vendor APM. File local nhận Jaeger, Zipkin, OTLP, OpenCensus; lưu local; trace idle 10 giây; block retention 1 giờ. Phù hợp demo, không HA và không bền.

Production dùng object storage, distributor/ingester/query frontend HA, metrics-generator/exemplar, tail sampling có kiểm soát và retention theo compliance. Sampling head bỏ trace trước khi biết lỗi; tail sampling mạnh hơn nhưng tốn state và collector capacity.

## 4.8 Logging, tracing, metrics và security production

- Log JSON có timestamp UTC, severity, service, version, environment, trace/span ID; redact token, cookie, password, PII.
- Propagate W3C Trace Context qua HTTP và message broker. Span name ổn định, attribute theo semantic conventions.
- Metric có unit, help, label budget và owner. Alert dựa trên symptom/SLO, kèm runbook.
- Mã hóa in transit và at rest, workload identity, NetworkPolicy bổ sung L3/L4 cho Istio L7, Pod Security Standards, read-only rootfs, non-root, SBOM/signature/admission verification.
- Secret không nằm trong Git, image, log, workflow output hoặc screenshot. Rotate và audit access.

