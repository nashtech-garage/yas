# 1. Đề bài, báo cáo và kiến trúc

## 1.1 Bốn PDF đầu vào

### `docs/Project 01_HKII_25_26.pdf`

Vai trò: đặc tả chấm điểm CI. File yêu cầu fork YAS, bảo vệ `main`, ít nhất hai reviewer, CI phải pass, pipeline theo branch, test và build, publish test result và coverage, đồng thời chỉ chạy service bị thay đổi trong monorepo. Phần nâng cao yêu cầu coverage trên 70%, Gitleaks, SonarQube/SonarCloud và Snyk.

Vì sao cần: đây là source of truth để phân biệt một pipeline chạy được với một bài làm đạt yêu cầu. Nếu bỏ file này khỏi hồ sơ, nhóm vẫn có code nhưng mất căn cứ nghiệm thu. Nếu sửa nội dung mà không có xác nhận giảng viên, tiêu chí chấm bị sai lệch.

Field/yêu cầu quan trọng:

- Branch protection: kiểm soát quy trình merge, không phải cấu hình trong YAML repository. Cần bằng chứng ở GitHub Settings.
- Hai phase test và build: test phải tạo JUnit report và coverage artifact, không chỉ chạy `mvn package`.
- Path-based CI: thay đổi `media/**` chỉ kích hoạt media. Workflow matrix build tất cả không đạt đúng ý này.
- Coverage trên 70%: phải có rule fail pipeline. Việc chỉ upload JaCoCo không phải quality gate.
- Secret và quality scanning: Gitleaks tìm secret, Sonar phân tích chất lượng/SAST, Snyk tập trung dependency/container/IaC. Ba công cụ không thay thế nhau.

Lỗi thường gặp: dùng `paths` sai khiến thay đổi thư viện chung không trigger các consumer; chạy test nhưng không upload report; dùng token trong YAML; cho phép workflow PR từ fork đọc secret; nhầm branch protection với workflow trigger.

### `docs/Project02_HKII_25_26.pdf`

Vai trò: đặc tả CD. Luồng bắt buộc là build image theo commit cuối branch, push registry, developer build chọn branch từng service, deploy các service còn lại bằng `main/latest`, cung cấp endpoint test, có cleanup job, tự động dev khi `main` đổi và staging khi tạo release tag. Hai phần nâng cao là Argo CD và service mesh.

Điểm cần đọc chính xác:

- Image cho feature branch phải gắn commit ID. Branch name có thể là alias tiện dụng nhưng không đủ tính bất biến.
- Developer build cần chọn branch theo service. `cd-developer.yml` giải branch thành SHA bằng `git ls-remote`, đây là logic đúng nếu image SHA đã được CI push.
- Đề gợi ý NodePort và hosts file. Project DOKS dùng NGINX Ingress và LoadBalancer. Đây là biến thể hợp lý về kỹ thuật nhưng báo cáo phải giải thích khác biệt.
- Dev nhận thay đổi `main`; staging nhận semantic release tag. Không nên deploy staging trực tiếp bằng mutable `latest`.
- Argo CD phải là bên apply desired state. Nếu pipeline vừa `helm upgrade` vừa để Argo CD quản lý cùng resource thì có hai controller ghi đè nhau.
- Istio phải có bằng chứng mTLS, topology Kiali, retry 500 và allow/deny bằng curl từ workload có sidecar.

### `docs/Report Project 1.pdf`

Vai trò: bằng chứng thực hiện Project 1. Khi dùng để vấn đáp, cần đối chiếu ảnh UI với YAML thực tế và lịch sử commit. Ảnh workflow xanh chỉ chứng minh một run cụ thể, không chứng minh path filter, coverage gate hoặc branch protection luôn đúng.

Workflow kiểm chứng: tìm run ID trong ảnh, kiểm tra commit SHA, workflow revision, artifacts và conclusion của từng job. Nếu bỏ report, mất bằng chứng cấu hình ngoài repo. Nếu sửa ảnh hoặc chú thích mà không cập nhật run link, tính audit giảm.

Production: lưu run URL, commit SHA, policy export và artifact retention; không dùng screenshot làm bằng chứng duy nhất. Lỗi sinh viên: ảnh cắt mất URL/time, ảnh Sonar không cho thấy quality gate, coverage toàn project bị trình bày như coverage service.

### `docs/Report Project 2.pdf`

Vai trò: bằng chứng CD, DOKS, Helm, Argo CD, Istio và observability. Báo cáo cần được đọc cùng `docs/problem_completion_check.md`, các manifest và trạng thái live vì trạng thái cluster thay đổi theo thời gian.

Logic kiểm chứng: mỗi tuyên bố phải có ba lớp bằng chứng: desired state trong Git, controller status như Argo CD/Deployment, và hành vi quan sát được như HTTP, curl, metric hoặc trace. Screenshot `Synced` nhưng `Degraded` không phải deployment thành công. Kiali có graph không tự chứng minh policy deny; cần 403 và Envoy RBAC log.

Production: ẩn token, kubeconfig, IP nhạy cảm; ghi phiên bản chart/controller; dùng timestamp và command tái lập. Lỗi thường gặp: gọi `2/2` là hai replica trong khi đó là hai container, nhầm `Synced` với `Healthy`, dùng curl từ container `istio-proxy` rồi kết luận mTLS hỏng.

## 1.2 Kiến trúc source

YAS là monorepo thương mại điện tử. Backend chủ yếu là Spring Boot, frontend là Next.js, BFF dùng Spring Cloud Gateway. PostgreSQL lưu dữ liệu theo service, Kafka và Debezium hỗ trợ event/CDC, Elasticsearch phục vụ search, Keycloak cung cấp identity.

Luồng request điển hình:

1. Browser gọi hostname storefront hoặc backoffice.
2. DigitalOcean LoadBalancer chuyển traffic vào NGINX Ingress Controller.
3. Ingress chọn BFF theo host/path.
4. BFF xử lý OAuth/OIDC session hoặc token relay, phục vụ UI và route `/api/*` đến service.
5. Kubernetes Service chọn Pod bằng label selector.
6. Nếu sidecar được inject, outbound và inbound đi qua Envoy. Envoy thực hiện mTLS, authz, retry và phát telemetry.
7. Service truy cập PostgreSQL/Kafka/Elasticsearch và trả response.

Quan hệ source quan trọng:

- Root `pom.xml` là Maven aggregator. `-pl product -am` build product và dependency module cần thiết.
- Mỗi backend có `pom.xml`, `Dockerfile`, `application.properties`, `logback-spring.xml`, source `src/main/java`, test và Liquibase changelog.
- `common-library` là shared dependency. Thay đổi nó có thể ảnh hưởng nhiều service, vì vậy path filter chỉ theo thư mục service là chưa đủ.
- `storefront`/`backoffice` phụ thuộc BFF khi chạy vì auth. Dockerfile UI build artifact Next.js; chart UI đưa env runtime/build-time theo cách template cho phép.
- `*-bff/application-dev.yaml` và `application-prod.yaml` quyết định route URI. Sai service DNS hoặc RewritePath làm 404 dù Pod vẫn healthy.
- Changelog Liquibase là lịch sử append-only. Sửa changeset đã chạy gây checksum mismatch hoặc drift database.

## 1.3 Archetype source, đủ 10 tiêu chí

### Java application class

Vai trò: bootstrap Spring context. Cần để component scan và auto-configuration bắt đầu. Workflow: JVM gọi `main`, Spring tạo bean, web server mở port. Quan hệ với `pom.xml`, properties, controller/service/repository. Field quan trọng là package gốc và annotation `@SpringBootApplication`. Bỏ class thì jar không có entrypoint; đổi package có thể mất component scan. Production nên có graceful shutdown, actuator và external config. Lỗi thường gặp là đặt application class quá sâu hoặc hard-code profile.

### Controller

Vai trò: ranh giới HTTP, map route, validate input, gọi service. Bỏ mapping làm endpoint 404; sửa path làm BFF/OpenAPI/test lệch. Production cần validation, authz, idempotency cho write, error model thống nhất và không log dữ liệu nhạy cảm. Lỗi thường gặp là nhét business logic vào controller hoặc trả entity JPA trực tiếp.

### Service

Vai trò: business logic và transaction boundary. Quan hệ controller, repository và client service khác. Bỏ `@Transactional` có thể sinh partial update; thêm transaction quá rộng giữ connection lâu. Production cần timeout, retry có điều kiện, circuit breaker và outbox cho event. Lỗi thường gặp là retry non-idempotent operation ở nhiều tầng.

### Repository/entity/migration

Vai trò: persistence. Entity map schema, repository cung cấp query, Liquibase tiến hóa schema. Field quan trọng gồm ID, nullability, unique/index, FK, changeset id/author. Bỏ index làm query chậm; đổi kiểu/cột có thể phá backward compatibility. Production dùng expand-migrate-contract, backup và migration test. Lỗi thường gặp là sửa changeset đã apply, cascade delete ngoài ý muốn và thiếu index FK.

### Test

Vai trò: regression evidence và quality gate. Unit test cô lập logic; integration test kiểm tra DB/broker/container; UI automation kiểm tra luồng. Bỏ test làm coverage giảm và lỗi lọt. Sửa assertion cho “xanh” mà không giữ contract làm test vô nghĩa. Production pipeline tách fast tests, integration tests và smoke test; lưu JUnit/coverage artifacts. Lỗi thường gặp là test phụ thuộc thứ tự, time, shared state hoặc external service không được pin version.

