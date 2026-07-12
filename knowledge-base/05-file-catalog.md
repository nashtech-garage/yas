# 5. File catalog đầy đủ

Catalog được tạo từ `git ls-files` cộng các PDF input chưa được track. Tổng số: **2204 file**. Không liệt kê nội dung `.git` vì đó là database nội bộ của Git, không phải source project. Chạy lại `./knowledge-base/generate-catalog.ps1` sau khi repository thay đổi.

## Cách áp dụng 10 tiêu chí cho mỗi dòng

Mỗi dòng có archetype và vai trò riêng. Với mọi archetype: (1) vai trò là cột Vai trò; (2) lý do cần là cung cấp artifact cho consumer; (3) workflow là producer tạo/maintain rồi build/controller/runtime đọc; (4) quan hệ là đường dẫn và module chứa file; (5) logic nằm trong code/config của file; (6) field quan trọng được giải thích trong chương tương ứng; (7) bỏ file làm consumer build/runtime mất input hoặc mất bằng chứng; (8) sửa file thay behavior của consumer và phải chạy test phù hợp; (9) production yêu cầu versioning, validation, least privilege, reproducibility và monitoring; (10) lỗi thường gặp là sai path/type/name/version, drift giữa environment, secret lộ hoặc thiếu test. Bảng archetype dưới đây cụ thể hóa hậu quả.

| Archetype | Workflow và quan hệ | Nếu bỏ/sửa | Production và lỗi thường gặp |
|---|---|---|---|
| APP-CONFIG | Spring/Logback load profile/env -> configure runtime | App dùng default hoặc fail boot; sửa key/port/URL làm kết nối sai | Typed config, secret external, profile test; lỗi commit credential |
| ARGO | Argo controller đọc Git/Helm -> diff -> sync cluster | Mất reconcile; sửa repo/path/destination có thể deploy nhầm/xóa resource | AppProject, RBAC, signed Git, digest; lỗi Synced=Healthy hoặc prune quá rộng |
| ASSET | Build/static server/seed data đọc binary theo path | UI/sample data thiếu; đổi tên/path gây 404 | Optimize, license, hash/CDN; lỗi binary lớn/secret metadata |
| COMPOSE | Compose resolve env -> network/volume -> start services | Local stack thiếu dependency; sửa port/volume làm conflict hoặc mất data | Chỉ dùng local/test; lỗi depends_on bị hiểu là readiness |
| DB-MIGRATION | Liquibase/bootstrap apply ordered change -> schema/data | Fresh DB thiếu schema/data; sửa changeset đã chạy gây checksum/drift | Append-only, expand-contract, backup/test; lỗi destructive migration |
| DOC | Người vận hành/học viên đọc runbook/design | Mất tri thức; sửa không đồng bộ code tạo hướng dẫn sai | Docs as code, command tested, owner/date; lỗi copy output cũ |
| DOCKERFILE | CI + build context -> image -> registry -> Pod | Không build image; sửa base/COPY/entrypoint có thể fail hoặc tăng CVE | Pin digest, non-root, multi-stage, SBOM/sign; lỗi copy secret/chạy root/mutable tag |
| FRONTEND-SOURCE | Next build/router/browser -> BFF/API | UI/route/component mất; sửa env/SSR contract gây runtime mismatch | CSP, accessibility, runtime config, bundle budget; lỗi bake sai public env |
| GHA | GitHub event -> runner -> test/build/push/update Git | Mất automation; sửa trigger/quyền có thể skip gate hoặc cấp quyền quá rộng | Pin action SHA, OIDC, concurrency, reusable workflow; lỗi paths/secret/skip test |
| HELM-CHART | Helm load metadata/dependencies -> package/render release | Chart không hợp lệ; sửa version/dependency đổi graph build | SemVer, lock dependency; lỗi nhầm appVersion với chart version |
| HELM-TEMPLATE | Helm merge values -> render manifest -> API server | Resource không được render; sửa indentation/name/selector làm install hoặc runtime fail | Schema, lint, template, policy test; lỗi toYaml/nindent/type |
| HELM-VALUES | Environment/workflow cung cấp override -> template consume | Rơi về default hoặc render lỗi; sửa nesting/type có thể bị bỏ qua im lặng | values.schema.json, secret ngoài Git; lỗi --set coercion |
| ISTIO | Istiod đọc policy -> xDS -> Envoy enforce/observe | Mất mTLS/authz/retry/demo; sửa selector/host có thể không áp hoặc chặn traffic | Default deny, policy tests, retry idempotent; lỗi curl từ proxy, wildcard principal |
| JAVA-BOOT | JVM main -> Spring context -> web server | Jar không boot; sửa package làm component scan mất bean | External config, actuator, graceful shutdown; lỗi hard-code profile |
| JAVA-CONTROLLER | HTTP route -> validation -> service | Endpoint mất/đổi contract; sửa path/status làm client/test hỏng | Authz, validation, idempotency; lỗi business logic trong controller |
| JAVA-MODEL | Request/domain/ORM map dữ liệu giữa layer | Serialization/schema/compile fail; sửa field phá compatibility | Version contract, validation, PII control; lỗi expose entity trực tiếp |
| JAVA-REPOSITORY | Service -> ORM/query -> database | Persistence operation mất; sửa query có thể sai data hoặc chậm | Index/query plan/integration test; lỗi N+1/cascade |
| JAVA-SERVICE | Controller/event -> business transaction -> repository/client | Mất use case; sửa transaction/retry có thể partial update hoặc duplicate | Timeout/outbox/idempotency; lỗi retry nhiều tầng |
| JAVA-SOURCE | Spring/module gọi class theo dependency graph | Compile/runtime/use case hỏng; sửa API lan sang caller | SOLID vừa đủ, observability, test; lỗi hidden coupling |
| K8S | kubectl/Helm/controller apply -> reconciliation | Mất workload/infra; sửa namespace/selector/name có thể recreate hoặc mất route | Dry-run/schema/policy, GitOps; lỗi label/port/probe/resource |
| MAVEN | Maven resolve reactor/dependency/plugin -> compile/test/package | Module không build; sửa version/plugin ảnh hưởng dependency graph | Wrapper, lock/update policy, reproducible build; lỗi skip test/version drift |
| NODE-META | npm resolve lock/scripts/types -> build frontend | Không cài/build đúng; sửa lock và package lệch gây nondeterminism | npm ci, audit, pin engine; lỗi sửa package không cập nhật lock |
| OBS | Agent/collector/backend/dashboard nhận và truy vấn telemetry | Mất signal hoặc dashboard; sửa endpoint/label gây data loss/cardinality | TLS/auth/HA/object storage/retention; lỗi local config dùng production |
| OTHER | Build/tool/runtime đọc theo loại file cụ thể | Tác động phụ thuộc consumer; phải kiểm tra reference trước khi bỏ/sửa | Giữ provenance/license/wrapper; lỗi coi file lạ là không cần |
| PDF | Người chấm và nhóm dùng để đối chiếu yêu cầu/bằng chứng | Mất source of truth hoặc làm chú thích lệch bằng chứng | Gắn revision, run URL, timestamp; tránh screenshot thiếu ngữ cảnh |
| QUALITY-CONFIG | Lint/scanner/tool đọc policy/ignore | Gate mất hoặc dùng default; sửa allowlist có thể che lỗi | Review exception, expiry, pin rule; lỗi broad ignore |
| SCRIPT | Operator/CI chạy lệnh tuần tự lên local/cluster | Mất bootstrap; sửa thiếu guard có thể tác động sai cluster | set strict, idempotent, context check; lỗi hard-code/không quote |
| TEST | Test runner -> fixture -> assertion -> report/coverage | Mất regression evidence; sửa assertion sai làm false green | Hermetic, deterministic, artifact; lỗi shared state/flaky/external dependency |

## Inventory

| # | File | Archetype | Vai trò |
|---:|---|---|---|
| 1 | `.env` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2 | `.gitattributes` | QUALITY-CONFIG | Lint, scan, ignore hoặc repository quality policy. |
| 3 | `.github/workflows/actions/action.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 4 | `.github/workflows/backoffice-bff-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 5 | `.github/workflows/backoffice-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 6 | `.github/workflows/cart-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 7 | `.github/workflows/cd-developer.yml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 8 | `.github/workflows/charts-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 9 | `.github/workflows/ci.yml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 10 | `.github/workflows/codeql.yml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 11 | `.github/workflows/customer-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 12 | `.github/workflows/deploy-dev.yml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 13 | `.github/workflows/deploy-staging.yml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 14 | `.github/workflows/gitleaks-check.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 15 | `.github/workflows/inventory-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 16 | `.github/workflows/location-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 17 | `.github/workflows/media-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 18 | `.github/workflows/order-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 19 | `.github/workflows/payment-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 20 | `.github/workflows/payment-paypal-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 21 | `.github/workflows/product-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 22 | `.github/workflows/promotion-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 23 | `.github/workflows/rating-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 24 | `.github/workflows/recommendation-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 25 | `.github/workflows/sampledata-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 26 | `.github/workflows/search-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 27 | `.github/workflows/storefront-bff-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 28 | `.github/workflows/storefront-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 29 | `.github/workflows/tax-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 30 | `.github/workflows/webhook-ci.yaml` | GHA | Workflow/composite action tự động hóa CI, security hoặc CD. |
| 31 | `.github/workflow-template.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 32 | `.gitignore` | QUALITY-CONFIG | Lint, scan, ignore hoặc repository quality policy. |
| 33 | `.gitleaksignore` | QUALITY-CONFIG | Lint, scan, ignore hoặc repository quality policy. |
| 34 | `automation-ui/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 35 | `automation-ui/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 36 | `automation-ui/automation-base/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 37 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/AutomationBaseMain.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 38 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/form/BaseForm.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 39 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/form/InputType.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 40 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/hook/Hooks.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 41 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/hook/WebDriverFactory.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 42 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/page/BasePage.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 43 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/service/impl/CheckBoxService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 44 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/service/impl/DropdownService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 45 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/service/impl/FileService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 46 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/service/impl/TextService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 47 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/service/InputDelegateService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 48 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/service/InputService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 49 | `automation-ui/automation-base/src/main/java/com/yas/automation/base/util/WebElementUtil.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 50 | `automation-ui/backoffice/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 51 | `automation-ui/backoffice/sampledata/images/category.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 52 | `automation-ui/backoffice/sampledata/images/dell.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 53 | `automation-ui/backoffice/src/main/java/com/yas/automation/ui/AutomationUiApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 54 | `automation-ui/backoffice/src/main/java/com/yas/automation/ui/configuration/BackOfficeConfiguration.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 55 | `automation-ui/backoffice/src/main/java/com/yas/automation/ui/enumerate/ProductAttribute.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 56 | `automation-ui/backoffice/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 57 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/configuration/CucumberSpringConfiguration.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 58 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/constants/CategoryConstants.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 59 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/form/CategoryForm.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 60 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/form/ProductForm.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 61 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/JUnitCucumberRunner.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 62 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/pages/CategoryPage.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 63 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/pages/HomePage.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 64 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/pages/LoginPage.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 65 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/pages/NewCategoryPage.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 66 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/pages/ProductPage.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 67 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/service/AuthenticationService.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 68 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/steps/CreateCategorySteps.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 69 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/steps/LoginSteps.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 70 | `automation-ui/backoffice/src/test/java/com/yas/automation/ui/steps/ProductProcessSteps.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 71 | `automation-ui/backoffice/src/test/resources/application.yml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 72 | `automation-ui/backoffice/src/test/resources/features/createCategory.feature` | TEST | Unit, integration, fixture hoặc test configuration. |
| 73 | `automation-ui/backoffice/src/test/resources/features/login.feature` | TEST | Unit, integration, fixture hoặc test configuration. |
| 74 | `automation-ui/backoffice/src/test/resources/features/product-process.feature` | TEST | Unit, integration, fixture hoặc test configuration. |
| 75 | `automation-ui/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 76 | `automation-ui/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 77 | `automation-ui/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 78 | `automation-ui/README.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 79 | `automation-ui/storefront/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 80 | `automation-ui/storefront/src/main/java/com/yas/automation/ui/AutomationUiApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 81 | `automation-ui/storefront/src/main/java/com/yas/automation/ui/configuration/StorefrontConfiguration.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 82 | `automation-ui/storefront/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 83 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/configuration/CucumberSpringConfiguration.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 84 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/form/UserRegisterForm.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 85 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/JUnitCucumberRunner.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 86 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/pages/CartPage.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 87 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/pages/CategoryItemDetailPage.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 88 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/pages/CategoryItemPage.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 89 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/pages/CategoryPage.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 90 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/pages/HomePage.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 91 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/pages/LoginPage.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 92 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/pages/UserRegistrationPage.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 93 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/services/AuthenticationService.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 94 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/steps/CartProcessSteps.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 95 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/steps/LoginSteps.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 96 | `automation-ui/storefront/src/test/java/com/yas/automation/ui/steps/UserRegistrationSteps.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 97 | `automation-ui/storefront/src/test/resources/application.yml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 98 | `automation-ui/storefront/src/test/resources/features/cart-process.feature` | TEST | Unit, integration, fixture hoặc test configuration. |
| 99 | `automation-ui/storefront/src/test/resources/features/login.feature` | TEST | Unit, integration, fixture hoặc test configuration. |
| 100 | `automation-ui/storefront/src/test/resources/features/user-registration.feature` | TEST | Unit, integration, fixture hoặc test configuration. |
| 101 | `backoffice/.eslintrc.json` | QUALITY-CONFIG | Lint, scan, ignore hoặc repository quality policy. |
| 102 | `backoffice/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 103 | `backoffice/.prettierignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 104 | `backoffice/.prettierrc` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 105 | `backoffice/asset/data/sidebar.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 106 | `backoffice/common/components/AuthenticationInfo.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 107 | `backoffice/common/components/ChooseImageCommon.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 108 | `backoffice/common/components/ConfirmationDialog.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 109 | `backoffice/common/components/Layout.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 110 | `backoffice/common/items/Filter.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 111 | `backoffice/common/items/Input.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 112 | `backoffice/common/items/ListGroup.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 113 | `backoffice/common/items/ModalDeleteCustom.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 114 | `backoffice/common/items/OptionSelect.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 115 | `backoffice/common/items/ProductModal.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 116 | `backoffice/common/items/TextEditor.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 117 | `backoffice/common/services/ApiClientService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 118 | `backoffice/common/services/ApiClientService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 119 | `backoffice/common/services/ResponseStatusHandlingService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 120 | `backoffice/common/services/ResponseStatusHandlingService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 121 | `backoffice/common/services/ToastService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 122 | `backoffice/constants/Common.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 123 | `backoffice/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 124 | `backoffice/modules/catalog/components/BrandGeneralInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 125 | `backoffice/modules/catalog/components/CategoryImage.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 126 | `backoffice/modules/catalog/components/CategoryMapping.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 127 | `backoffice/modules/catalog/components/ChooseImages.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 128 | `backoffice/modules/catalog/components/ChooseThumbnail.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 129 | `backoffice/modules/catalog/components/CrossSellProduct.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 130 | `backoffice/modules/catalog/components/CustomOptionInput.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 131 | `backoffice/modules/catalog/components/DisplayTypeModal.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 132 | `backoffice/modules/catalog/components/ExportProduct.js` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 133 | `backoffice/modules/catalog/components/index.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 134 | `backoffice/modules/catalog/components/ProductAttribute.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 135 | `backoffice/modules/catalog/components/ProductAttributeGroupGeneralInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 136 | `backoffice/modules/catalog/components/ProductGeneralInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 137 | `backoffice/modules/catalog/components/ProductImage.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 138 | `backoffice/modules/catalog/components/ProductOptionGeneralInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 139 | `backoffice/modules/catalog/components/ProductSEO.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 140 | `backoffice/modules/catalog/components/ProductVariant.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 141 | `backoffice/modules/catalog/components/ProductVariation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 142 | `backoffice/modules/catalog/components/RelatedProduct.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 143 | `backoffice/modules/catalog/constants/validationPattern.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 144 | `backoffice/modules/catalog/models/Brand.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 145 | `backoffice/modules/catalog/models/Category.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 146 | `backoffice/modules/catalog/models/Filter.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 147 | `backoffice/modules/catalog/models/FormProduct.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 148 | `backoffice/modules/catalog/models/FormProductTemplate.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 149 | `backoffice/modules/catalog/models/Media.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 150 | `backoffice/modules/catalog/models/Product.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 151 | `backoffice/modules/catalog/models/ProductAttribute.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 152 | `backoffice/modules/catalog/models/ProductAttributeGroup.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 153 | `backoffice/modules/catalog/models/ProductAttributeValue.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 154 | `backoffice/modules/catalog/models/ProductAttributeValuePost.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 155 | `backoffice/modules/catalog/models/ProductImage.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 156 | `backoffice/modules/catalog/models/ProductOption.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 157 | `backoffice/modules/catalog/models/ProductOptionValuePost.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 158 | `backoffice/modules/catalog/models/ProductPayload.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 159 | `backoffice/modules/catalog/models/Products.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 160 | `backoffice/modules/catalog/models/ProductTemplate.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 161 | `backoffice/modules/catalog/models/ProductThumbnail.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 162 | `backoffice/modules/catalog/models/ProductThumbnails.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 163 | `backoffice/modules/catalog/models/ProductVariation.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 164 | `backoffice/modules/catalog/models/ProductVariationPost.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 165 | `backoffice/modules/catalog/models/ProductVariationPut.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 166 | `backoffice/modules/catalog/services/BrandService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 167 | `backoffice/modules/catalog/services/CategoryService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 168 | `backoffice/modules/catalog/services/MediaService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 169 | `backoffice/modules/catalog/services/ProductAttributeGroupService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 170 | `backoffice/modules/catalog/services/ProductAttributeService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 171 | `backoffice/modules/catalog/services/ProductAttributeValueService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 172 | `backoffice/modules/catalog/services/ProductOptionService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 173 | `backoffice/modules/catalog/services/ProductService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 174 | `backoffice/modules/catalog/services/ProductTemplateService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 175 | `backoffice/modules/customer/components/CustomerBaseInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 176 | `backoffice/modules/customer/components/CustomerInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 177 | `backoffice/modules/customer/components/ProfileForm.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 178 | `backoffice/modules/customer/models/Customer.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 179 | `backoffice/modules/customer/models/Customers.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 180 | `backoffice/modules/customer/services/CustomerService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 181 | `backoffice/modules/home/components/index.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 182 | `backoffice/modules/home/components/LatestItemPanel.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 183 | `backoffice/modules/home/components/LatestOrders.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 184 | `backoffice/modules/home/components/LatestProducts.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 185 | `backoffice/modules/home/components/LatestRatings.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 186 | `backoffice/modules/inventory/components/WarehouseGeneralInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 187 | `backoffice/modules/inventory/models/ProductInfoVm.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 188 | `backoffice/modules/inventory/models/ProductQuantityInStock.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 189 | `backoffice/modules/inventory/models/Stock.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 190 | `backoffice/modules/inventory/models/StockHistory.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 191 | `backoffice/modules/inventory/models/StockHistoryList.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 192 | `backoffice/modules/inventory/models/StockInfo.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 193 | `backoffice/modules/inventory/models/Warehouse.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 194 | `backoffice/modules/inventory/models/WarehouseDetail.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 195 | `backoffice/modules/inventory/services/StockHistoryService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 196 | `backoffice/modules/inventory/services/StockService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 197 | `backoffice/modules/inventory/services/WarehouseService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 198 | `backoffice/modules/location/components/CountryGeneralInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 199 | `backoffice/modules/location/components/StateOrProvinceGeneralInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 200 | `backoffice/modules/location/models/Country.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 201 | `backoffice/modules/location/models/District.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 202 | `backoffice/modules/location/models/StateOrProvince.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 203 | `backoffice/modules/location/services/CountryService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 204 | `backoffice/modules/location/services/DistrictService.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 205 | `backoffice/modules/location/services/StateOrProvinceService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 206 | `backoffice/modules/order/components/AddressTable.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 207 | `backoffice/modules/order/components/BillingNShippingInfo.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 208 | `backoffice/modules/order/components/OrderBriefInfo.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 209 | `backoffice/modules/order/components/OrderHistory.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 210 | `backoffice/modules/order/components/OrderProductInfo.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 211 | `backoffice/modules/order/components/OrderSearch.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 212 | `backoffice/modules/order/models/Order.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 213 | `backoffice/modules/order/models/OrderAddress.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 214 | `backoffice/modules/order/models/OrderItem.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 215 | `backoffice/modules/order/models/OrderSearchForm.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 216 | `backoffice/modules/order/services/OrderService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 217 | `backoffice/modules/payment/models/Payment.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 218 | `backoffice/modules/profile/models/ProfileRequest.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 219 | `backoffice/modules/promotion/components/MultipleAutoComplete.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 220 | `backoffice/modules/promotion/components/PromotionGeneralInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 221 | `backoffice/modules/promotion/models/Brand.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 222 | `backoffice/modules/promotion/models/Category.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 223 | `backoffice/modules/promotion/models/Product.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 224 | `backoffice/modules/promotion/models/Promotion.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 225 | `backoffice/modules/promotion/services/ProductService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 226 | `backoffice/modules/promotion/services/PromotionService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 227 | `backoffice/modules/rating/components/RatingSearch.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 228 | `backoffice/modules/rating/models/Rating.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 229 | `backoffice/modules/rating/models/RatingSearchForm.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 230 | `backoffice/modules/rating/services/RatingService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 231 | `backoffice/modules/tax/components/TaxClassGeneralInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 232 | `backoffice/modules/tax/components/TaxRateGeneralInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 233 | `backoffice/modules/tax/models/TaxClass.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 234 | `backoffice/modules/tax/models/TaxRate.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 235 | `backoffice/modules/tax/services/TaxClassService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 236 | `backoffice/modules/tax/services/TaxClassService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 237 | `backoffice/modules/tax/services/TaxRateService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 238 | `backoffice/modules/webhook/components/EventInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 239 | `backoffice/modules/webhook/components/WebhookInformation.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 240 | `backoffice/modules/webhook/models/ContentType.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 241 | `backoffice/modules/webhook/models/Event.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 242 | `backoffice/modules/webhook/models/Webhook.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 243 | `backoffice/modules/webhook/services/EventService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 244 | `backoffice/modules/webhook/services/WebhookService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 245 | `backoffice/modules/webhook/services/WebhookService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 246 | `backoffice/next.config.js` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 247 | `backoffice/next-env.d.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 248 | `backoffice/package.json` | NODE-META | Dependency/build/type metadata cho frontend Node.js. |
| 249 | `backoffice/package-lock.json` | NODE-META | Dependency/build/type metadata cho frontend Node.js. |
| 250 | `backoffice/pages/_app.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 251 | `backoffice/pages/_document.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 252 | `backoffice/pages/catalog/brands/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 253 | `backoffice/pages/catalog/brands/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 254 | `backoffice/pages/catalog/brands/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 255 | `backoffice/pages/catalog/categories/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 256 | `backoffice/pages/catalog/categories/[id]/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 257 | `backoffice/pages/catalog/categories/[id]/listProduct.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 258 | `backoffice/pages/catalog/categories/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 259 | `backoffice/pages/catalog/categories/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 260 | `backoffice/pages/catalog/product-attribute-groups/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 261 | `backoffice/pages/catalog/product-attribute-groups/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 262 | `backoffice/pages/catalog/product-attribute-groups/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 263 | `backoffice/pages/catalog/product-attributes/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 264 | `backoffice/pages/catalog/product-attributes/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 265 | `backoffice/pages/catalog/product-attributes/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 266 | `backoffice/pages/catalog/product-options/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 267 | `backoffice/pages/catalog/product-options/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 268 | `backoffice/pages/catalog/product-options/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 269 | `backoffice/pages/catalog/products/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 270 | `backoffice/pages/catalog/products/[id]/productAttributes.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 271 | `backoffice/pages/catalog/products/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 272 | `backoffice/pages/catalog/products/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 273 | `backoffice/pages/catalog/product-templates/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 274 | `backoffice/pages/catalog/product-templates/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 275 | `backoffice/pages/catalog/product-templates/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 276 | `backoffice/pages/customers/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 277 | `backoffice/pages/customers/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 278 | `backoffice/pages/customers/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 279 | `backoffice/pages/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 280 | `backoffice/pages/inventory/warehouse-products/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 281 | `backoffice/pages/inventory/warehouses/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 282 | `backoffice/pages/inventory/warehouses/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 283 | `backoffice/pages/inventory/warehouses/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 284 | `backoffice/pages/inventory/warehouse-stocks/histories/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 285 | `backoffice/pages/inventory/warehouse-stocks/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 286 | `backoffice/pages/location/countries/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 287 | `backoffice/pages/location/countries/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 288 | `backoffice/pages/location/countries/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 289 | `backoffice/pages/location/state-or-provinces/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 290 | `backoffice/pages/location/state-or-provinces/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 291 | `backoffice/pages/location/state-or-provinces/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 292 | `backoffice/pages/profile/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 293 | `backoffice/pages/promotion/manager-promotion/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 294 | `backoffice/pages/promotion/manager-promotion/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 295 | `backoffice/pages/promotion/manager-promotion/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 296 | `backoffice/pages/reviews/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 297 | `backoffice/pages/sales/orders/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 298 | `backoffice/pages/sales/orders/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 299 | `backoffice/pages/sales/shipments/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 300 | `backoffice/pages/system/payment-providers/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 301 | `backoffice/pages/tax/tax-classes/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 302 | `backoffice/pages/tax/tax-classes/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 303 | `backoffice/pages/tax/tax-classes/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 304 | `backoffice/pages/tax/tax-rates/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 305 | `backoffice/pages/tax/tax-rates/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 306 | `backoffice/pages/tax/tax-rates/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 307 | `backoffice/pages/webhook/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 308 | `backoffice/pages/webhook/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 309 | `backoffice/pages/webhook/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 310 | `backoffice/public/favicon.ico` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 311 | `backoffice/public/vercel.svg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 312 | `backoffice/README.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 313 | `backoffice/sonar-project.properties` | QUALITY-CONFIG | Lint, scan, ignore hoặc repository quality policy. |
| 314 | `backoffice/styles/ChooseImage.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 315 | `backoffice/styles/common/app.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 316 | `backoffice/styles/common/color.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 317 | `backoffice/styles/common/grid.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 318 | `backoffice/styles/common/input.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 319 | `backoffice/styles/common/scroll_bar.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 320 | `backoffice/styles/common/style.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 321 | `backoffice/styles/common/typography.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 322 | `backoffice/styles/common/variable.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 323 | `backoffice/styles/CustomToast.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 324 | `backoffice/styles/Filter.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 325 | `backoffice/styles/globals.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 326 | `backoffice/styles/Home.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 327 | `backoffice/styles/Layout.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 328 | `backoffice/styles/ListGroup.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 329 | `backoffice/styles/ProductVariant.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 330 | `backoffice/styles/Sidebar.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 331 | `backoffice/styles/TextEditor.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 332 | `backoffice/tsconfig.json` | NODE-META | Dependency/build/type metadata cho frontend Node.js. |
| 333 | `backoffice/utils/concatQueryString.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 334 | `backoffice/utils/concatQueryString.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 335 | `backoffice/utils/formatPrice.test.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 336 | `backoffice/utils/formatPrice.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 337 | `backoffice/vitest.config.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 338 | `backoffice/vitest.setup.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 339 | `backoffice-bff/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 340 | `backoffice-bff/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 341 | `backoffice-bff/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 342 | `backoffice-bff/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 343 | `backoffice-bff/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 344 | `backoffice-bff/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 345 | `backoffice-bff/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 346 | `backoffice-bff/src/main/java/com/yas/backofficebff/Application.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 347 | `backoffice-bff/src/main/java/com/yas/backofficebff/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 348 | `backoffice-bff/src/main/java/com/yas/backofficebff/controller/AuthenticationController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 349 | `backoffice-bff/src/main/java/com/yas/backofficebff/viewmodel/AuthenticatedUser.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 350 | `backoffice-bff/src/main/resources/application.yaml` | APP-CONFIG | Runtime application/logging configuration. |
| 351 | `backoffice-bff/src/main/resources/application-dev.yaml` | APP-CONFIG | Runtime application/logging configuration. |
| 352 | `backoffice-bff/src/main/resources/application-prod.yaml` | APP-CONFIG | Runtime application/logging configuration. |
| 353 | `backoffice-bff/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 354 | `backoffice-bff/src/test/java/com/yas/backofficebff/ApplicationTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 355 | `backoffice-bff/src/test/java/com/yas/backofficebff/config/SecurityConfigTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 356 | `backoffice-bff/src/test/java/com/yas/backofficebff/controller/AuthenticationControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 357 | `backoffice-bff/src/test/java/com/yas/backofficebff/viewmodel/AuthenticatedUserTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 358 | `backoffice-bff/wait-for-it.sh` | SCRIPT | Imperative automation/bootstrap helper. |
| 359 | `cart/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 360 | `cart/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 361 | `cart/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 362 | `cart/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 363 | `cart/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 364 | `cart/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 365 | `cart/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 366 | `cart/src/it/java/com/yas/cart/controller/CartItemControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 367 | `cart/src/it/java/com/yas/cart/service/ProductServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 368 | `cart/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 369 | `cart/src/main/java/com/yas/cart/CartApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 370 | `cart/src/main/java/com/yas/cart/config/DatabaseAutoConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 371 | `cart/src/main/java/com/yas/cart/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 372 | `cart/src/main/java/com/yas/cart/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 373 | `cart/src/main/java/com/yas/cart/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 374 | `cart/src/main/java/com/yas/cart/controller/CartItemController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 375 | `cart/src/main/java/com/yas/cart/mapper/CartItemMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 376 | `cart/src/main/java/com/yas/cart/model/CartItem.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 377 | `cart/src/main/java/com/yas/cart/model/CartItemId.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 378 | `cart/src/main/java/com/yas/cart/repository/CartItemRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 379 | `cart/src/main/java/com/yas/cart/service/AbstractCircuitBreakFallbackHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 380 | `cart/src/main/java/com/yas/cart/service/CartItemService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 381 | `cart/src/main/java/com/yas/cart/service/ProductService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 382 | `cart/src/main/java/com/yas/cart/utils/Constants.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 383 | `cart/src/main/java/com/yas/cart/viewmodel/CartItemDeleteVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 384 | `cart/src/main/java/com/yas/cart/viewmodel/CartItemGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 385 | `cart/src/main/java/com/yas/cart/viewmodel/CartItemPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 386 | `cart/src/main/java/com/yas/cart/viewmodel/CartItemPutVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 387 | `cart/src/main/java/com/yas/cart/viewmodel/ProductThumbnailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 388 | `cart/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 389 | `cart/src/main/resources/db/changelog/data/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 390 | `cart/src/main/resources/db/changelog/db.changelog-master.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 391 | `cart/src/main/resources/db/changelog/ddl/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 392 | `cart/src/main/resources/db/changelog/ddl/changelog-0002.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 393 | `cart/src/main/resources/db/changelog/ddl/changelog-0003.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 394 | `cart/src/main/resources/db/changelog/ddl/changelog-0004.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 395 | `cart/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 396 | `cart/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 397 | `cart/src/test/java/com/yas/cart/controller/CartItemControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 398 | `cart/src/test/java/com/yas/cart/service/AbstractCircuitBreakFallbackHandlerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 399 | `cart/src/test/java/com/yas/cart/service/CartItemServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 400 | `cart/src/test/java/com/yas/cart/service/ProductServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 401 | `cart/src/test/java/com/yas/cart/util/SecurityContextUtils.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 402 | `cart/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 403 | `cart/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 404 | `cart/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 405 | `checkstyle/checkstyle.xml` | QUALITY-CONFIG | Lint, scan, ignore hoặc repository quality policy. |
| 406 | `checkstyle/README.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 407 | `checkstyle/suppressions.xml` | QUALITY-CONFIG | Lint, scan, ignore hoặc repository quality policy. |
| 408 | `common-library/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 409 | `common-library/src/it/java/common/kafka/CdcConsumerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 410 | `common-library/src/main/java/com/yas/commonlibrary/config/CorsConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 411 | `common-library/src/main/java/com/yas/commonlibrary/config/ServiceUrlConfig.java` | JAVA-SERVICE | Business logic/orchestration. |
| 412 | `common-library/src/main/java/com/yas/commonlibrary/constants/ApiConstant.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 413 | `common-library/src/main/java/com/yas/commonlibrary/constants/MessageCode.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 414 | `common-library/src/main/java/com/yas/commonlibrary/constants/PageableConstant.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 415 | `common-library/src/main/java/com/yas/commonlibrary/csv/anotation/CsvColumn.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 416 | `common-library/src/main/java/com/yas/commonlibrary/csv/anotation/CsvName.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 417 | `common-library/src/main/java/com/yas/commonlibrary/csv/BaseCsv.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 418 | `common-library/src/main/java/com/yas/commonlibrary/csv/CsvExporter.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 419 | `common-library/src/main/java/com/yas/commonlibrary/exception/AccessDeniedException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 420 | `common-library/src/main/java/com/yas/commonlibrary/exception/ApiExceptionHandler.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 421 | `common-library/src/main/java/com/yas/commonlibrary/exception/BadRequestException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 422 | `common-library/src/main/java/com/yas/commonlibrary/exception/CreateGuestUserException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 423 | `common-library/src/main/java/com/yas/commonlibrary/exception/DuplicatedException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 424 | `common-library/src/main/java/com/yas/commonlibrary/exception/Forbidden.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 425 | `common-library/src/main/java/com/yas/commonlibrary/exception/ForbiddenException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 426 | `common-library/src/main/java/com/yas/commonlibrary/exception/InternalServerErrorException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 427 | `common-library/src/main/java/com/yas/commonlibrary/exception/MultipartFileContentException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 428 | `common-library/src/main/java/com/yas/commonlibrary/exception/NotFoundException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 429 | `common-library/src/main/java/com/yas/commonlibrary/exception/ResourceExistedException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 430 | `common-library/src/main/java/com/yas/commonlibrary/exception/SignInRequiredException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 431 | `common-library/src/main/java/com/yas/commonlibrary/exception/StockExistingException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 432 | `common-library/src/main/java/com/yas/commonlibrary/exception/UnsupportedMediaTypeException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 433 | `common-library/src/main/java/com/yas/commonlibrary/exception/WrongEmailFormatException.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 434 | `common-library/src/main/java/com/yas/commonlibrary/kafka/cdc/BaseCdcConsumer.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 435 | `common-library/src/main/java/com/yas/commonlibrary/kafka/cdc/config/BaseKafkaListenerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 436 | `common-library/src/main/java/com/yas/commonlibrary/kafka/cdc/message/Operation.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 437 | `common-library/src/main/java/com/yas/commonlibrary/kafka/cdc/message/Product.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 438 | `common-library/src/main/java/com/yas/commonlibrary/kafka/cdc/message/ProductCdcMessage.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 439 | `common-library/src/main/java/com/yas/commonlibrary/kafka/cdc/message/ProductMsgKey.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 440 | `common-library/src/main/java/com/yas/commonlibrary/kafka/cdc/RetrySupportDql.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 441 | `common-library/src/main/java/com/yas/commonlibrary/mapper/BaseMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 442 | `common-library/src/main/java/com/yas/commonlibrary/mapper/EntityCreateUpdateMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 443 | `common-library/src/main/java/com/yas/commonlibrary/model/AbstractAuditEntity.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 444 | `common-library/src/main/java/com/yas/commonlibrary/model/listener/CustomAuditingEntityListener.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 445 | `common-library/src/main/java/com/yas/commonlibrary/utils/AuthenticationUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 446 | `common-library/src/main/java/com/yas/commonlibrary/utils/DateTimeUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 447 | `common-library/src/main/java/com/yas/commonlibrary/utils/MessagesUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 448 | `common-library/src/main/java/com/yas/commonlibrary/viewmodel/error/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 449 | `common-library/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 450 | `common-library/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 451 | `common-library/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 452 | `common-library/src/test/java/com/yas/commonlibrary/AbstractControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 453 | `common-library/src/test/java/com/yas/commonlibrary/csv/CsvExporterTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 454 | `common-library/src/test/java/com/yas/commonlibrary/IntegrationTestConfiguration.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 455 | `common-library/src/test/java/com/yas/commonlibrary/model/listener/CustomAuditingEntityListenerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 456 | `common-library/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 457 | `common-library/src/test/resources/test-realm.json` | TEST | Unit, integration, fixture hoặc test configuration. |
| 458 | `customer/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 459 | `customer/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 460 | `customer/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 461 | `customer/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 462 | `customer/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 463 | `customer/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 464 | `customer/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 465 | `customer/src/it/java/com/yas/customer/constant/TestConstants.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 466 | `customer/src/it/java/com/yas/customer/service/LocationServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 467 | `customer/src/it/java/com/yas/customer/service/UserAddressServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 468 | `customer/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 469 | `customer/src/it/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 470 | `customer/src/main/java/com/yas/customer/config/DatabaseAutoConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 471 | `customer/src/main/java/com/yas/customer/config/KeycloakClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 472 | `customer/src/main/java/com/yas/customer/config/KeycloakPropsConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 473 | `customer/src/main/java/com/yas/customer/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 474 | `customer/src/main/java/com/yas/customer/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 475 | `customer/src/main/java/com/yas/customer/config/ServiceUrlConfig.java` | JAVA-SERVICE | Business logic/orchestration. |
| 476 | `customer/src/main/java/com/yas/customer/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 477 | `customer/src/main/java/com/yas/customer/controller/CustomerController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 478 | `customer/src/main/java/com/yas/customer/controller/UserAddressController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 479 | `customer/src/main/java/com/yas/customer/CustomerApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 480 | `customer/src/main/java/com/yas/customer/model/UserAddress.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 481 | `customer/src/main/java/com/yas/customer/repository/UserAddressRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 482 | `customer/src/main/java/com/yas/customer/service/AbstractCircuitBreakFallbackHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 483 | `customer/src/main/java/com/yas/customer/service/CustomerService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 484 | `customer/src/main/java/com/yas/customer/service/LocationService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 485 | `customer/src/main/java/com/yas/customer/service/UserAddressService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 486 | `customer/src/main/java/com/yas/customer/utils/Constants.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 487 | `customer/src/main/java/com/yas/customer/utils/MessagesUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 488 | `customer/src/main/java/com/yas/customer/viewmodel/address/ActiveAddressVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 489 | `customer/src/main/java/com/yas/customer/viewmodel/address/AddressDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 490 | `customer/src/main/java/com/yas/customer/viewmodel/address/AddressPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 491 | `customer/src/main/java/com/yas/customer/viewmodel/address/AddressVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 492 | `customer/src/main/java/com/yas/customer/viewmodel/customer/CustomerAdminVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 493 | `customer/src/main/java/com/yas/customer/viewmodel/customer/CustomerListVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 494 | `customer/src/main/java/com/yas/customer/viewmodel/customer/CustomerPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 495 | `customer/src/main/java/com/yas/customer/viewmodel/customer/CustomerProfileRequestVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 496 | `customer/src/main/java/com/yas/customer/viewmodel/customer/CustomerVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 497 | `customer/src/main/java/com/yas/customer/viewmodel/customer/GuestUserVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 498 | `customer/src/main/java/com/yas/customer/viewmodel/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 499 | `customer/src/main/java/com/yas/customer/viewmodel/useraddress/UserAddressVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 500 | `customer/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 501 | `customer/src/main/resources/db/changelog/data/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 502 | `customer/src/main/resources/db/changelog/db.changelog-master.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 503 | `customer/src/main/resources/db/changelog/ddl/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 504 | `customer/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 505 | `customer/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 506 | `customer/src/test/java/com/yas/customer/controller/CustomerControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 507 | `customer/src/test/java/com/yas/customer/controller/UserAddressControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 508 | `customer/src/test/java/com/yas/customer/service/CustomerServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 509 | `customer/src/test/java/com/yas/customer/service/LocationServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 510 | `customer/src/test/java/com/yas/customer/service/UserAddressServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 511 | `customer/src/test/java/com/yas/customer/util/SecurityContextUtils.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 512 | `customer/src/test/java/com/yas/customer/utils/MessagesUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 513 | `customer/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 514 | `customer/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 515 | `customer/src/test/resources/messages/messages.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 516 | `customer/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 517 | `delivery/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 518 | `delivery/src/main/java/com/yas/delivery/controller/DeliveryController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 519 | `delivery/src/main/java/com/yas/delivery/DeliveryApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 520 | `delivery/src/main/java/com/yas/delivery/service/DeliveryService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 521 | `delivery/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 522 | `delivery/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 523 | `DEMO-HUONG-DAN.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 524 | `deployment/app-config/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 525 | `docker/grafana/provisioning/dashboards/general.yaml` | OBS | Cấu hình metrics, logs, traces hoặc dashboard. |
| 526 | `docker/grafana/provisioning/dashboards/observability_dashboard.json` | OBS | Cấu hình metrics, logs, traces hoặc dashboard. |
| 527 | `docker/grafana/provisioning/dashboards/opentelemetry-collector.json` | OBS | Cấu hình metrics, logs, traces hoặc dashboard. |
| 528 | `docker/grafana/provisioning/dashboards/prometheus-dashboard.json` | OBS | Cấu hình metrics, logs, traces hoặc dashboard. |
| 529 | `docker/grafana/provisioning/datasources/datasource.yml` | OBS | Cấu hình metrics, logs, traces hoặc dashboard. |
| 530 | `docker/libs/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 531 | `docker/libs/opentelemetry-javaagent.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 532 | `docker/loki/loki-local.yaml` | OBS | Cấu hình metrics, logs, traces hoặc dashboard. |
| 533 | `docker/otel-collector/otelcol-config.yml` | OBS | Cấu hình metrics, logs, traces hoặc dashboard. |
| 534 | `docker/postgres/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 535 | `docker/postgres/postgresql.conf.sample` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 536 | `docker/prometheus/prometheus.yml` | OBS | Cấu hình metrics, logs, traces hoặc dashboard. |
| 537 | `docker/tempo/tempo-local.yaml` | OBS | Cấu hình metrics, logs, traces hoặc dashboard. |
| 538 | `docker-compose.o11y.yml` | COMPOSE | Mô tả stack nhiều container cho local/integration. |
| 539 | `docker-compose.search.yml` | COMPOSE | Mô tả stack nhiều container cho local/integration. |
| 540 | `docker-compose.yml` | COMPOSE | Mô tả stack nhiều container cho local/integration. |
| 541 | `docs/context.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 542 | `docs/developer-guidelines.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 543 | `docs/huy-service-mesh-report.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 544 | `docs/images/yas-authen-bff.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 545 | `docs/images/yas-cdc-debezium-kafka.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 546 | `docs/images/yas-ci.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 547 | `docs/images/yas-ci-check.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 548 | `docs/images/yas-maven-project-structure.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 549 | `docs/images/yas-observability.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 550 | `docs/implementation-spec.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 551 | `docs/khoa-ci-cd-service-mesh.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 552 | `docs/outline.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 553 | `docs/problem_completion_check.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 554 | `docs/Project 01_HKII_25_26.pdf` | PDF | Đề bài hoặc báo cáo làm căn cứ yêu cầu và bằng chứng. |
| 555 | `docs/Project02_HKII_25_26.pdf` | PDF | Đề bài hoặc báo cáo làm căn cứ yêu cầu và bằng chứng. |
| 556 | `docs/project-report.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 557 | `docs/README.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 558 | `docs/Report Project 1.pdf` | PDF | Đề bài hoặc báo cáo làm căn cứ yêu cầu và bằng chứng. |
| 559 | `docs/Report Project 2.pdf` | PDF | Đề bài hoặc báo cáo làm căn cứ yêu cầu và bằng chứng. |
| 560 | `docs/walkthrough.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 561 | `docs/worklog-2026-07-05.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 562 | `gitleaks.toml` | QUALITY-CONFIG | Lint, scan, ignore hoặc repository quality policy. |
| 563 | `identity/realm-export.json` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 564 | `identity/themes/README.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 565 | `identity/themes/yas/META-INF/keycloak-themes.json` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 566 | `identity/themes/yas/theme/yas/login/components/link/primary.ftl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 567 | `identity/themes/yas/theme/yas/login/components/link/secondary.ftl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 568 | `identity/themes/yas/theme/yas/login/login.ftl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 569 | `identity/themes/yas/theme/yas/login/register.ftl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 570 | `identity/themes/yas/theme/yas/login/resources/css/login.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 571 | `identity/themes/yas/theme/yas/login/resources/img/background.svg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 572 | `identity/themes/yas/theme/yas/login/resources/img/eye.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 573 | `identity/themes/yas/theme/yas/login/resources/img/eye-off.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 574 | `identity/themes/yas/theme/yas/login/resources/img/yaslogo.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 575 | `identity/themes/yas/theme/yas/login/template.ftl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 576 | `identity/themes/yas/theme/yas/login/theme.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 577 | `inventory/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 578 | `inventory/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 579 | `inventory/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 580 | `inventory/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 581 | `inventory/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 582 | `inventory/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 583 | `inventory/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 584 | `inventory/src/it/java/com/yas/inventory/constant/TestConstants.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 585 | `inventory/src/it/java/com/yas/inventory/repository/StockHistoryRepositoryIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 586 | `inventory/src/it/java/com/yas/inventory/repository/StockRepositoryIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 587 | `inventory/src/it/java/com/yas/inventory/repository/WarehouseRepositoryIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 588 | `inventory/src/it/java/com/yas/inventory/service/LocationServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 589 | `inventory/src/it/java/com/yas/inventory/service/ProductServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 590 | `inventory/src/it/java/com/yas/inventory/service/StockHistoryServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 591 | `inventory/src/it/java/com/yas/inventory/service/StockServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 592 | `inventory/src/it/java/com/yas/inventory/service/WarehouseServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 593 | `inventory/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 594 | `inventory/src/it/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 595 | `inventory/src/it/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 596 | `inventory/src/main/java/com/yas/inventory/config/DatabaseAutoConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 597 | `inventory/src/main/java/com/yas/inventory/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 598 | `inventory/src/main/java/com/yas/inventory/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 599 | `inventory/src/main/java/com/yas/inventory/config/ServiceUrlConfig.java` | JAVA-SERVICE | Business logic/orchestration. |
| 600 | `inventory/src/main/java/com/yas/inventory/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 601 | `inventory/src/main/java/com/yas/inventory/constants/ApiConstant.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 602 | `inventory/src/main/java/com/yas/inventory/constants/MessageCode.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 603 | `inventory/src/main/java/com/yas/inventory/constants/PageableConstant.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 604 | `inventory/src/main/java/com/yas/inventory/controller/StockController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 605 | `inventory/src/main/java/com/yas/inventory/controller/StockHistoryController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 606 | `inventory/src/main/java/com/yas/inventory/controller/WarehouseController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 607 | `inventory/src/main/java/com/yas/inventory/InventoryApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 608 | `inventory/src/main/java/com/yas/inventory/model/enumeration/FilterExistInWhSelection.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 609 | `inventory/src/main/java/com/yas/inventory/model/Stock.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 610 | `inventory/src/main/java/com/yas/inventory/model/StockHistory.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 611 | `inventory/src/main/java/com/yas/inventory/model/Warehouse.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 612 | `inventory/src/main/java/com/yas/inventory/repository/StockHistoryRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 613 | `inventory/src/main/java/com/yas/inventory/repository/StockRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 614 | `inventory/src/main/java/com/yas/inventory/repository/WarehouseRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 615 | `inventory/src/main/java/com/yas/inventory/service/AbstractCircuitBreakFallbackHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 616 | `inventory/src/main/java/com/yas/inventory/service/LocationService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 617 | `inventory/src/main/java/com/yas/inventory/service/ProductService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 618 | `inventory/src/main/java/com/yas/inventory/service/StockHistoryService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 619 | `inventory/src/main/java/com/yas/inventory/service/StockService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 620 | `inventory/src/main/java/com/yas/inventory/service/WarehouseService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 621 | `inventory/src/main/java/com/yas/inventory/utils/AuthenticationUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 622 | `inventory/src/main/java/com/yas/inventory/viewmodel/address/AddressDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 623 | `inventory/src/main/java/com/yas/inventory/viewmodel/address/AddressPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 624 | `inventory/src/main/java/com/yas/inventory/viewmodel/address/AddressVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 625 | `inventory/src/main/java/com/yas/inventory/viewmodel/error/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 626 | `inventory/src/main/java/com/yas/inventory/viewmodel/product/ProductInfoVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 627 | `inventory/src/main/java/com/yas/inventory/viewmodel/product/ProductQuantityPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 628 | `inventory/src/main/java/com/yas/inventory/viewmodel/stock/StockPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 629 | `inventory/src/main/java/com/yas/inventory/viewmodel/stock/StockQuantityUpdateVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 630 | `inventory/src/main/java/com/yas/inventory/viewmodel/stock/StockQuantityVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 631 | `inventory/src/main/java/com/yas/inventory/viewmodel/stock/StockVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 632 | `inventory/src/main/java/com/yas/inventory/viewmodel/stockhistory/StockHistoryListVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 633 | `inventory/src/main/java/com/yas/inventory/viewmodel/stockhistory/StockHistoryVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 634 | `inventory/src/main/java/com/yas/inventory/viewmodel/warehouse/WarehouseDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 635 | `inventory/src/main/java/com/yas/inventory/viewmodel/warehouse/WarehouseGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 636 | `inventory/src/main/java/com/yas/inventory/viewmodel/warehouse/WarehouseListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 637 | `inventory/src/main/java/com/yas/inventory/viewmodel/warehouse/WarehousePostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 638 | `inventory/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 639 | `inventory/src/main/resources/db/changelog/data/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 640 | `inventory/src/main/resources/db/changelog/db.changelog-master.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 641 | `inventory/src/main/resources/db/changelog/ddl/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 642 | `inventory/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 643 | `inventory/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 644 | `inventory/src/test/java/com/yas/inventory/controller/StockControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 645 | `inventory/src/test/java/com/yas/inventory/controller/StockHistoryControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 646 | `inventory/src/test/java/com/yas/inventory/controller/WarehouseControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 647 | `inventory/src/test/java/com/yas/inventory/service/LocationServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 648 | `inventory/src/test/java/com/yas/inventory/service/ProductServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 649 | `inventory/src/test/java/com/yas/inventory/service/StockServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 650 | `inventory/src/test/java/com/yas/inventory/service/WarehouseServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 651 | `inventory/src/test/java/com/yas/inventory/util/SecurityContextUtils.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 652 | `inventory/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 653 | `inventory/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 654 | `inventory/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 655 | `k8s/argocd/dev-application.yaml` | ARGO | Desired state của Argo CD Application. |
| 656 | `k8s/argocd/staging-application.yaml` | ARGO | Desired state của Argo CD Application. |
| 657 | `k8s/charts/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 658 | `k8s/charts/backend/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 659 | `k8s/charts/backend/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 660 | `k8s/charts/backend/README.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 661 | `k8s/charts/backend/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 662 | `k8s/charts/backend/templates/deployment.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 663 | `k8s/charts/backend/templates/extra-manifests.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 664 | `k8s/charts/backend/templates/hpa.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 665 | `k8s/charts/backend/templates/ingress.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 666 | `k8s/charts/backend/templates/NOTES.txt` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 667 | `k8s/charts/backend/templates/service.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 668 | `k8s/charts/backend/templates/serviceaccount.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 669 | `k8s/charts/backend/templates/servicemonitoring.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 670 | `k8s/charts/backend/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 671 | `k8s/charts/backoffice-bff/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 672 | `k8s/charts/backoffice-bff/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 673 | `k8s/charts/backoffice-bff/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 674 | `k8s/charts/backoffice-bff/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 675 | `k8s/charts/backoffice-bff/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 676 | `k8s/charts/backoffice-ui/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 677 | `k8s/charts/backoffice-ui/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 678 | `k8s/charts/backoffice-ui/charts/ui-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 679 | `k8s/charts/backoffice-ui/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 680 | `k8s/charts/backoffice-ui/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 681 | `k8s/charts/cart/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 682 | `k8s/charts/cart/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 683 | `k8s/charts/cart/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 684 | `k8s/charts/cart/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 685 | `k8s/charts/cart/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 686 | `k8s/charts/Chart.template.yaml` | K8S | Kubernetes manifest hoặc cluster deployment configuration. |
| 687 | `k8s/charts/create-charts.sh` | SCRIPT | Imperative automation/bootstrap helper. |
| 688 | `k8s/charts/customer/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 689 | `k8s/charts/customer/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 690 | `k8s/charts/customer/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 691 | `k8s/charts/customer/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 692 | `k8s/charts/customer/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 693 | `k8s/charts/inventory/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 694 | `k8s/charts/inventory/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 695 | `k8s/charts/inventory/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 696 | `k8s/charts/inventory/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 697 | `k8s/charts/inventory/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 698 | `k8s/charts/location/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 699 | `k8s/charts/location/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 700 | `k8s/charts/location/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 701 | `k8s/charts/location/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 702 | `k8s/charts/location/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 703 | `k8s/charts/media/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 704 | `k8s/charts/media/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 705 | `k8s/charts/media/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 706 | `k8s/charts/media/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 707 | `k8s/charts/media/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 708 | `k8s/charts/order/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 709 | `k8s/charts/order/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 710 | `k8s/charts/order/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 711 | `k8s/charts/order/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 712 | `k8s/charts/order/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 713 | `k8s/charts/payment/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 714 | `k8s/charts/payment/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 715 | `k8s/charts/payment/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 716 | `k8s/charts/payment/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 717 | `k8s/charts/payment/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 718 | `k8s/charts/payment-paypal/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 719 | `k8s/charts/payment-paypal/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 720 | `k8s/charts/payment-paypal/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 721 | `k8s/charts/payment-paypal/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 722 | `k8s/charts/payment-paypal/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 723 | `k8s/charts/product/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 724 | `k8s/charts/product/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 725 | `k8s/charts/product/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 726 | `k8s/charts/product/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 727 | `k8s/charts/product/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 728 | `k8s/charts/promotion/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 729 | `k8s/charts/promotion/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 730 | `k8s/charts/promotion/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 731 | `k8s/charts/promotion/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 732 | `k8s/charts/promotion/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 733 | `k8s/charts/rating/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 734 | `k8s/charts/rating/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 735 | `k8s/charts/rating/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 736 | `k8s/charts/rating/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 737 | `k8s/charts/rating/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 738 | `k8s/charts/recommendation/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 739 | `k8s/charts/recommendation/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 740 | `k8s/charts/recommendation/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 741 | `k8s/charts/recommendation/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 742 | `k8s/charts/recommendation/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 743 | `k8s/charts/sampledata/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 744 | `k8s/charts/sampledata/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 745 | `k8s/charts/sampledata/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 746 | `k8s/charts/sampledata/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 747 | `k8s/charts/sampledata/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 748 | `k8s/charts/search/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 749 | `k8s/charts/search/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 750 | `k8s/charts/search/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 751 | `k8s/charts/search/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 752 | `k8s/charts/search/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 753 | `k8s/charts/storefront-bff/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 754 | `k8s/charts/storefront-bff/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 755 | `k8s/charts/storefront-bff/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 756 | `k8s/charts/storefront-bff/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 757 | `k8s/charts/storefront-bff/templates/storefront-bff.configmap.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 758 | `k8s/charts/storefront-bff/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 759 | `k8s/charts/storefront-ui/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 760 | `k8s/charts/storefront-ui/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 761 | `k8s/charts/storefront-ui/charts/ui-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 762 | `k8s/charts/storefront-ui/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 763 | `k8s/charts/storefront-ui/templates/storefront-env-production.configmap.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 764 | `k8s/charts/storefront-ui/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 765 | `k8s/charts/swagger-ui/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 766 | `k8s/charts/swagger-ui/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 767 | `k8s/charts/swagger-ui/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 768 | `k8s/charts/swagger-ui/templates/deployment.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 769 | `k8s/charts/swagger-ui/templates/hpa.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 770 | `k8s/charts/swagger-ui/templates/ingress.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 771 | `k8s/charts/swagger-ui/templates/nginx-configmap.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 772 | `k8s/charts/swagger-ui/templates/NOTES.txt` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 773 | `k8s/charts/swagger-ui/templates/service.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 774 | `k8s/charts/swagger-ui/templates/serviceaccount.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 775 | `k8s/charts/swagger-ui/templates/tests/test-connection.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 776 | `k8s/charts/swagger-ui/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 777 | `k8s/charts/tax/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 778 | `k8s/charts/tax/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 779 | `k8s/charts/tax/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 780 | `k8s/charts/tax/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 781 | `k8s/charts/tax/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 782 | `k8s/charts/ui/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 783 | `k8s/charts/ui/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 784 | `k8s/charts/ui/README.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 785 | `k8s/charts/ui/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 786 | `k8s/charts/ui/templates/deployment.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 787 | `k8s/charts/ui/templates/hpa.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 788 | `k8s/charts/ui/templates/ingress.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 789 | `k8s/charts/ui/templates/NOTES.txt` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 790 | `k8s/charts/ui/templates/service.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 791 | `k8s/charts/ui/templates/serviceaccount.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 792 | `k8s/charts/ui/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 793 | `k8s/charts/values.template.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 794 | `k8s/charts/webhook/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 795 | `k8s/charts/webhook/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 796 | `k8s/charts/webhook/charts/backend-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 797 | `k8s/charts/webhook/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 798 | `k8s/charts/webhook/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 799 | `k8s/charts/yas-configuration/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 800 | `k8s/charts/yas-configuration/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 801 | `k8s/charts/yas-configuration/charts/reloader-1.0.29.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 802 | `k8s/charts/yas-configuration/README.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 803 | `k8s/charts/yas-configuration/templates/yas-configurations.configmap.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 804 | `k8s/charts/yas-configuration/templates/yas-credentials.secret.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 805 | `k8s/charts/yas-configuration/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 806 | `k8s/charts/yas-umbrella/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 807 | `k8s/charts/yas-umbrella/charts/backoffice-bff-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 808 | `k8s/charts/yas-umbrella/charts/backoffice-ui-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 809 | `k8s/charts/yas-umbrella/charts/cart-0.2.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 810 | `k8s/charts/yas-umbrella/charts/customer-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 811 | `k8s/charts/yas-umbrella/charts/inventory-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 812 | `k8s/charts/yas-umbrella/charts/media-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 813 | `k8s/charts/yas-umbrella/charts/order-0.2.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 814 | `k8s/charts/yas-umbrella/charts/product-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 815 | `k8s/charts/yas-umbrella/charts/sampledata-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 816 | `k8s/charts/yas-umbrella/charts/search-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 817 | `k8s/charts/yas-umbrella/charts/storefront-bff-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 818 | `k8s/charts/yas-umbrella/charts/storefront-ui-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 819 | `k8s/charts/yas-umbrella/charts/swagger-ui-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 820 | `k8s/charts/yas-umbrella/charts/tax-0.1.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 821 | `k8s/charts/yas-umbrella/charts/yas-configuration-0.4.0.tgz` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 822 | `k8s/charts/yas-umbrella/templates/backend-apis-ingress.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 823 | `k8s/charts/yas-umbrella/templates/nginx-service.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 824 | `k8s/charts/yas-umbrella/templates/service-aliases.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 825 | `k8s/charts/yas-umbrella/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 826 | `k8s/deploy/cluster-config.yaml` | K8S | Kubernetes manifest hoặc cluster deployment configuration. |
| 827 | `k8s/deploy/deploy-yas-applications.ps1` | SCRIPT | Imperative automation/bootstrap helper. |
| 828 | `k8s/deploy/deploy-yas-applications.sh` | SCRIPT | Imperative automation/bootstrap helper. |
| 829 | `k8s/deploy/deploy-yas-configuration.sh` | SCRIPT | Imperative automation/bootstrap helper. |
| 830 | `k8s/deploy/elasticsearch/elasticsearch-cluster/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 831 | `k8s/deploy/elasticsearch/elasticsearch-cluster/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 832 | `k8s/deploy/elasticsearch/elasticsearch-cluster/filerealm/users` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 833 | `k8s/deploy/elasticsearch/elasticsearch-cluster/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 834 | `k8s/deploy/elasticsearch/elasticsearch-cluster/templates/elasticsearch.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 835 | `k8s/deploy/elasticsearch/elasticsearch-cluster/templates/kibana.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 836 | `k8s/deploy/elasticsearch/elasticsearch-cluster/templates/kibana-ingress.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 837 | `k8s/deploy/elasticsearch/elasticsearch-cluster/templates/user-credentials.secret.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 838 | `k8s/deploy/elasticsearch/elasticsearch-cluster/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 839 | `k8s/deploy/kafka/akhq.values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 840 | `k8s/deploy/kafka/kafka-cluster/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 841 | `k8s/deploy/kafka/kafka-cluster/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 842 | `k8s/deploy/kafka/kafka-cluster/templates/credentials.secret.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 843 | `k8s/deploy/kafka/kafka-cluster/templates/debezium-connect-cluster.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 844 | `k8s/deploy/kafka/kafka-cluster/templates/debezium-connector-postgresql-product-db.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 845 | `k8s/deploy/kafka/kafka-cluster/templates/kafka-cluster.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 846 | `k8s/deploy/kafka/kafka-cluster/templates/role.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 847 | `k8s/deploy/kafka/kafka-cluster/templates/role-binding.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 848 | `k8s/deploy/kafka/kafka-cluster/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 849 | `k8s/deploy/keycloak/keycloak/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 850 | `k8s/deploy/keycloak/keycloak/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 851 | `k8s/deploy/keycloak/keycloak/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 852 | `k8s/deploy/keycloak/keycloak/templates/keycloak.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 853 | `k8s/deploy/keycloak/keycloak/templates/keycloak-credential.secret.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 854 | `k8s/deploy/keycloak/keycloak/templates/keycloak-yas-realm-import.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 855 | `k8s/deploy/keycloak/keycloak/templates/postgresql-credential.secret.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 856 | `k8s/deploy/keycloak/keycloak/templates/yas-themes.configmap.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 857 | `k8s/deploy/keycloak/keycloak/themes/yas.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 858 | `k8s/deploy/keycloak/keycloak/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 859 | `k8s/deploy/observability/grafana/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 860 | `k8s/deploy/observability/grafana/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 861 | `k8s/deploy/observability/grafana/observability_dashboard.json` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 862 | `k8s/deploy/observability/grafana/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 863 | `k8s/deploy/observability/grafana/templates/dashboards.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 864 | `k8s/deploy/observability/grafana/templates/grafana.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 865 | `k8s/deploy/observability/grafana/templates/grafana-credentials.secret.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 866 | `k8s/deploy/observability/grafana/templates/loki-datasource.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 867 | `k8s/deploy/observability/grafana/templates/observability-dashboard.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 868 | `k8s/deploy/observability/grafana/templates/tempo-datasource.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 869 | `k8s/deploy/observability/grafana/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 870 | `k8s/deploy/observability/loki.values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 871 | `k8s/deploy/observability/opentelemetry/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 872 | `k8s/deploy/observability/opentelemetry/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 873 | `k8s/deploy/observability/opentelemetry/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 874 | `k8s/deploy/observability/opentelemetry/templates/instrumentation.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 875 | `k8s/deploy/observability/opentelemetry/templates/opentelemetry-collector.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 876 | `k8s/deploy/observability/opentelemetry/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 877 | `k8s/deploy/observability/prometheus.values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 878 | `k8s/deploy/observability/promtail.values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 879 | `k8s/deploy/observability/tempo.values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 880 | `k8s/deploy/patch-entity-operator.json` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 881 | `k8s/deploy/postgres/pgadmin/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 882 | `k8s/deploy/postgres/pgadmin/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 883 | `k8s/deploy/postgres/pgadmin/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 884 | `k8s/deploy/postgres/pgadmin/templates/deployment.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 885 | `k8s/deploy/postgres/pgadmin/templates/hpa.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 886 | `k8s/deploy/postgres/pgadmin/templates/ingress.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 887 | `k8s/deploy/postgres/pgadmin/templates/NOTES.txt` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 888 | `k8s/deploy/postgres/pgadmin/templates/pvc.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 889 | `k8s/deploy/postgres/pgadmin/templates/secret.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 890 | `k8s/deploy/postgres/pgadmin/templates/service.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 891 | `k8s/deploy/postgres/pgadmin/templates/serviceaccount.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 892 | `k8s/deploy/postgres/pgadmin/templates/tests/test-connection.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 893 | `k8s/deploy/postgres/pgadmin/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 894 | `k8s/deploy/postgres/postgres-operator-ui.values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 895 | `k8s/deploy/postgres/postgresql/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 896 | `k8s/deploy/postgres/postgresql/templates/credentials.secret.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 897 | `k8s/deploy/postgres/postgresql/templates/postgresql.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 898 | `k8s/deploy/postgres/postgresql/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 899 | `k8s/deploy/README.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 900 | `k8s/deploy/setup-cluster.sh` | SCRIPT | Imperative automation/bootstrap helper. |
| 901 | `k8s/deploy/setup-keycloak.sh` | SCRIPT | Imperative automation/bootstrap helper. |
| 902 | `k8s/deploy/setup-redis.sh` | SCRIPT | Imperative automation/bootstrap helper. |
| 903 | `k8s/deploy/zookeeper/.helmignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 904 | `k8s/deploy/zookeeper/Chart.yaml` | HELM-CHART | Metadata/dependency của Helm chart. |
| 905 | `k8s/deploy/zookeeper/templates/_helpers.tpl` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 906 | `k8s/deploy/zookeeper/templates/deployment.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 907 | `k8s/deploy/zookeeper/templates/hpa.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 908 | `k8s/deploy/zookeeper/templates/ingress.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 909 | `k8s/deploy/zookeeper/templates/NOTES.txt` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 910 | `k8s/deploy/zookeeper/templates/service.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 911 | `k8s/deploy/zookeeper/templates/serviceaccount.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 912 | `k8s/deploy/zookeeper/templates/tests/test-connection.yaml` | HELM-TEMPLATE | Go template render Kubernetes resource. |
| 913 | `k8s/deploy/zookeeper/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 914 | `k8s/environments/dev/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 915 | `k8s/environments/staging/values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 916 | `k8s/ingress.yaml` | K8S | Kubernetes manifest hoặc cluster deployment configuration. |
| 917 | `k8s/ingress-nginx-values.yaml` | HELM-VALUES | Public configuration input/override của chart. |
| 918 | `k8s/istio/authorization-policies.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 919 | `k8s/istio/demo/authorization-policy-strict-demo.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 920 | `k8s/istio/demo/mesh-test-clients.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 921 | `k8s/istio/demo/retry-demo-httpbin.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 922 | `k8s/istio/destination-rules.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 923 | `k8s/istio/install-istio.ps1` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 924 | `k8s/istio/install-istio.sh` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 925 | `k8s/istio/kiali-ingress.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 926 | `k8s/istio/peer-authentication.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 927 | `k8s/istio/public-entrypoints.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 928 | `k8s/istio/README.md` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 929 | `k8s/istio/staging-demo/authorization-policies-staging.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 930 | `k8s/istio/staging-demo/demo/authorization-policy-strict-demo-staging.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 931 | `k8s/istio/staging-demo/demo/mesh-test-clients-staging.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 932 | `k8s/istio/staging-demo/demo/retry-demo-httpbin-staging.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 933 | `k8s/istio/staging-demo/destination-rules-staging.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 934 | `k8s/istio/staging-demo/peer-authentication-staging-permissive-ROLLBACK.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 935 | `k8s/istio/staging-demo/peer-authentication-staging-strict.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 936 | `k8s/istio/staging-demo/public-entrypoints-staging.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 937 | `k8s/istio/staging-demo/virtual-services-staging.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 938 | `k8s/istio/virtual-services.yaml` | ISTIO | Policy/routing/demo/service-mesh manifest hoặc hướng dẫn. |
| 939 | `k8s/namespaces.yaml` | K8S | Kubernetes manifest hoặc cluster deployment configuration. |
| 940 | `k8s/service-aliases.yaml` | K8S | Kubernetes manifest hoặc cluster deployment configuration. |
| 941 | `k8s/yas-backend-apis-ingress.yaml` | K8S | Kubernetes manifest hoặc cluster deployment configuration. |
| 942 | `k8s/yas-nginx-service.yaml` | K8S | Kubernetes manifest hoặc cluster deployment configuration. |
| 943 | `kafka/connects/debezium-order.json` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 944 | `kafka/connects/debezium-product.json` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 945 | `LICENSE` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 946 | `location/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 947 | `location/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 948 | `location/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 949 | `location/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 950 | `location/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 951 | `location/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 952 | `location/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 953 | `location/src/it/java/com/yas/location/AddressControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 954 | `location/src/it/java/com/yas/location/CountryControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 955 | `location/src/it/java/com/yas/location/DistrictControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 956 | `location/src/it/java/com/yas/location/StateOrProvinceControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 957 | `location/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 958 | `location/src/main/java/com/yas/location/config/DatabaseAutoConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 959 | `location/src/main/java/com/yas/location/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 960 | `location/src/main/java/com/yas/location/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 961 | `location/src/main/java/com/yas/location/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 962 | `location/src/main/java/com/yas/location/controller/AddressController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 963 | `location/src/main/java/com/yas/location/controller/CountryController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 964 | `location/src/main/java/com/yas/location/controller/CountryStorefrontController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 965 | `location/src/main/java/com/yas/location/controller/DistrictStorefrontController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 966 | `location/src/main/java/com/yas/location/controller/StateOrProvinceController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 967 | `location/src/main/java/com/yas/location/controller/StateOrProvinceStoreFrontController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 968 | `location/src/main/java/com/yas/location/LocationApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 969 | `location/src/main/java/com/yas/location/mapper/AddressResponseMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 970 | `location/src/main/java/com/yas/location/mapper/CountryMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 971 | `location/src/main/java/com/yas/location/mapper/StateOrProvinceMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 972 | `location/src/main/java/com/yas/location/model/Address.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 973 | `location/src/main/java/com/yas/location/model/Country.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 974 | `location/src/main/java/com/yas/location/model/District.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 975 | `location/src/main/java/com/yas/location/model/StateOrProvince.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 976 | `location/src/main/java/com/yas/location/repository/AddressRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 977 | `location/src/main/java/com/yas/location/repository/CountryRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 978 | `location/src/main/java/com/yas/location/repository/DistrictRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 979 | `location/src/main/java/com/yas/location/repository/StateOrProvinceRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 980 | `location/src/main/java/com/yas/location/service/AddressService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 981 | `location/src/main/java/com/yas/location/service/CountryService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 982 | `location/src/main/java/com/yas/location/service/DistrictService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 983 | `location/src/main/java/com/yas/location/service/StateOrProvinceService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 984 | `location/src/main/java/com/yas/location/utils/Constants.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 985 | `location/src/main/java/com/yas/location/utils/MessagesUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 986 | `location/src/main/java/com/yas/location/viewmodel/address/AddressDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 987 | `location/src/main/java/com/yas/location/viewmodel/address/AddressGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 988 | `location/src/main/java/com/yas/location/viewmodel/address/AddressPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 989 | `location/src/main/java/com/yas/location/viewmodel/country/CountryListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 990 | `location/src/main/java/com/yas/location/viewmodel/country/CountryPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 991 | `location/src/main/java/com/yas/location/viewmodel/country/CountryVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 992 | `location/src/main/java/com/yas/location/viewmodel/district/DistrictGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 993 | `location/src/main/java/com/yas/location/viewmodel/error/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 994 | `location/src/main/java/com/yas/location/viewmodel/stateorprovince/StateOrProvinceAndCountryGetNameVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 995 | `location/src/main/java/com/yas/location/viewmodel/stateorprovince/StateOrProvinceListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 996 | `location/src/main/java/com/yas/location/viewmodel/stateorprovince/StateOrProvincePostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 997 | `location/src/main/java/com/yas/location/viewmodel/stateorprovince/StateOrProvinceVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 998 | `location/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 999 | `location/src/main/resources/db/changelog/data/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1000 | `location/src/main/resources/db/changelog/db.changelog-master.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1001 | `location/src/main/resources/db/changelog/ddl/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1002 | `location/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 1003 | `location/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1004 | `location/src/test/java/com/yas/location/controller/AddressControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1005 | `location/src/test/java/com/yas/location/controller/CountryControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1006 | `location/src/test/java/com/yas/location/controller/CountryStorefrontControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1007 | `location/src/test/java/com/yas/location/controller/DistrictStorefrontControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1008 | `location/src/test/java/com/yas/location/controller/StateOrProvinceControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1009 | `location/src/test/java/com/yas/location/controller/StateOrProvinceStoreFrontControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1010 | `location/src/test/java/com/yas/location/service/AddressServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1011 | `location/src/test/java/com/yas/location/service/CountryServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1012 | `location/src/test/java/com/yas/location/service/DistrictServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1013 | `location/src/test/java/com/yas/location/service/StateOrProvinceServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1014 | `location/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1015 | `location/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1016 | `location/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1017 | `media/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1018 | `media/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1019 | `media/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1020 | `media/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 1021 | `media/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1022 | `media/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1023 | `media/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 1024 | `media/src/it/java/com/yas/media/controller/MediaControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1025 | `media/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1026 | `media/src/it/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1027 | `media/src/it/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1028 | `media/src/main/java/com/yas/media/config/FilesystemConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1029 | `media/src/main/java/com/yas/media/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1030 | `media/src/main/java/com/yas/media/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1031 | `media/src/main/java/com/yas/media/config/YasConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1032 | `media/src/main/java/com/yas/media/controller/MediaController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1033 | `media/src/main/java/com/yas/media/exception/ControllerAdvisor.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1034 | `media/src/main/java/com/yas/media/mapper/MediaVmMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1035 | `media/src/main/java/com/yas/media/MediaApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 1036 | `media/src/main/java/com/yas/media/model/dto/MediaDto.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1037 | `media/src/main/java/com/yas/media/model/Media.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1038 | `media/src/main/java/com/yas/media/repository/FileSystemRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1039 | `media/src/main/java/com/yas/media/repository/MediaRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1040 | `media/src/main/java/com/yas/media/service/MediaService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1041 | `media/src/main/java/com/yas/media/service/MediaServiceImpl.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1042 | `media/src/main/java/com/yas/media/utils/FileTypeValidator.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1043 | `media/src/main/java/com/yas/media/utils/StringUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1044 | `media/src/main/java/com/yas/media/utils/ValidFileType.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1045 | `media/src/main/java/com/yas/media/viewmodel/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1046 | `media/src/main/java/com/yas/media/viewmodel/MediaPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1047 | `media/src/main/java/com/yas/media/viewmodel/MediaVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1048 | `media/src/main/java/com/yas/media/viewmodel/NoFileMediaVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1049 | `media/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 1050 | `media/src/main/resources/db/changelog/db.changelog-master.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1051 | `media/src/main/resources/db/changelog/ddl/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1052 | `media/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 1053 | `media/src/test/java/com/yas/media/FileSystemRepositoryTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1054 | `media/src/test/java/com/yas/media/MediaServiceUnitTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1055 | `media/src/test/java/com/yas/media/utils/StringUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1056 | `media/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1057 | `nginx/configuration/custom_proxy_settings.conf` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1058 | `nginx/templates/default.conf.template` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1059 | `order/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1060 | `order/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1061 | `order/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1062 | `order/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 1063 | `order/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1064 | `order/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1065 | `order/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 1066 | `order/src/it/java/com/yas/order/constant/TestConstants.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1067 | `order/src/it/java/com/yas/order/service/CustomerServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1068 | `order/src/it/java/com/yas/order/service/OrderServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1069 | `order/src/it/java/com/yas/order/service/ProductServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1070 | `order/src/it/java/com/yas/order/service/TaxServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1071 | `order/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1072 | `order/src/it/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1073 | `order/src/it/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1074 | `order/src/main/java/com/yas/order/config/DatabaseAutoConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1075 | `order/src/main/java/com/yas/order/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1076 | `order/src/main/java/com/yas/order/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1077 | `order/src/main/java/com/yas/order/config/ServiceUrlConfig.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1078 | `order/src/main/java/com/yas/order/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1079 | `order/src/main/java/com/yas/order/controller/CheckoutController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1080 | `order/src/main/java/com/yas/order/controller/OrderController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1081 | `order/src/main/java/com/yas/order/mapper/CheckoutMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1082 | `order/src/main/java/com/yas/order/mapper/OrderMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1083 | `order/src/main/java/com/yas/order/model/Checkout.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1084 | `order/src/main/java/com/yas/order/model/CheckoutItem.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1085 | `order/src/main/java/com/yas/order/model/csv/OrderItemCsv.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1086 | `order/src/main/java/com/yas/order/model/enumeration/CheckoutState.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1087 | `order/src/main/java/com/yas/order/model/enumeration/DeliveryMethod.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1088 | `order/src/main/java/com/yas/order/model/enumeration/DeliveryStatus.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1089 | `order/src/main/java/com/yas/order/model/enumeration/OrderStatus.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1090 | `order/src/main/java/com/yas/order/model/enumeration/PaymentMethod.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1091 | `order/src/main/java/com/yas/order/model/enumeration/PaymentStatus.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1092 | `order/src/main/java/com/yas/order/model/Order.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1093 | `order/src/main/java/com/yas/order/model/OrderAddress.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1094 | `order/src/main/java/com/yas/order/model/OrderItem.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1095 | `order/src/main/java/com/yas/order/model/request/OrderRequest.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1096 | `order/src/main/java/com/yas/order/OrderApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 1097 | `order/src/main/java/com/yas/order/repository/CheckoutItemRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1098 | `order/src/main/java/com/yas/order/repository/CheckoutRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1099 | `order/src/main/java/com/yas/order/repository/OrderAddressRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1100 | `order/src/main/java/com/yas/order/repository/OrderItemRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1101 | `order/src/main/java/com/yas/order/repository/OrderRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1102 | `order/src/main/java/com/yas/order/service/AbstractCircuitBreakFallbackHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1103 | `order/src/main/java/com/yas/order/service/CartService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1104 | `order/src/main/java/com/yas/order/service/CheckoutService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1105 | `order/src/main/java/com/yas/order/service/CustomerService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1106 | `order/src/main/java/com/yas/order/service/OrderService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1107 | `order/src/main/java/com/yas/order/service/ProductService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1108 | `order/src/main/java/com/yas/order/service/PromotionService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1109 | `order/src/main/java/com/yas/order/service/TaxService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1110 | `order/src/main/java/com/yas/order/specification/OrderSpecification.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1111 | `order/src/main/java/com/yas/order/utils/Constants.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1112 | `order/src/main/java/com/yas/order/viewmodel/cart/CartItemDeleteVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1113 | `order/src/main/java/com/yas/order/viewmodel/checkout/CheckoutItemPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1114 | `order/src/main/java/com/yas/order/viewmodel/checkout/CheckoutItemVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1115 | `order/src/main/java/com/yas/order/viewmodel/checkout/CheckoutPaymentMethodPutVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1116 | `order/src/main/java/com/yas/order/viewmodel/checkout/CheckoutPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1117 | `order/src/main/java/com/yas/order/viewmodel/checkout/CheckoutStatusPutVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1118 | `order/src/main/java/com/yas/order/viewmodel/checkout/CheckoutVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1119 | `order/src/main/java/com/yas/order/viewmodel/customer/CustomerVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1120 | `order/src/main/java/com/yas/order/viewmodel/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1121 | `order/src/main/java/com/yas/order/viewmodel/order/OrderBriefVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1122 | `order/src/main/java/com/yas/order/viewmodel/order/OrderExistsByProductAndUserGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1123 | `order/src/main/java/com/yas/order/viewmodel/order/OrderGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1124 | `order/src/main/java/com/yas/order/viewmodel/order/OrderItemGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1125 | `order/src/main/java/com/yas/order/viewmodel/order/OrderItemPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1126 | `order/src/main/java/com/yas/order/viewmodel/order/OrderItemVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1127 | `order/src/main/java/com/yas/order/viewmodel/order/OrderListVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1128 | `order/src/main/java/com/yas/order/viewmodel/order/OrderPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1129 | `order/src/main/java/com/yas/order/viewmodel/order/OrderVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1130 | `order/src/main/java/com/yas/order/viewmodel/order/PaymentOrderStatusVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1131 | `order/src/main/java/com/yas/order/viewmodel/orderaddress/OrderAddressPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1132 | `order/src/main/java/com/yas/order/viewmodel/orderaddress/OrderAddressVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1133 | `order/src/main/java/com/yas/order/viewmodel/product/ProductCheckoutListVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1134 | `order/src/main/java/com/yas/order/viewmodel/product/ProductGetCheckoutListVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1135 | `order/src/main/java/com/yas/order/viewmodel/product/ProductQuantityItem.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1136 | `order/src/main/java/com/yas/order/viewmodel/product/ProductVariationVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1137 | `order/src/main/java/com/yas/order/viewmodel/promotion/PromotionUsageVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1138 | `order/src/main/java/com/yas/order/viewmodel/ResponeStatusVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1139 | `order/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 1140 | `order/src/main/resources/db/changelog/data/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1141 | `order/src/main/resources/db/changelog/db.changelog-master.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1142 | `order/src/main/resources/db/changelog/ddl/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1143 | `order/src/main/resources/db/changelog/ddl/changelog-0002.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1144 | `order/src/main/resources/db/changelog/ddl/changelog-0003.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1145 | `order/src/main/resources/db/changelog/ddl/changelog-0004.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1146 | `order/src/main/resources/db/changelog/ddl/changelog-0005.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1147 | `order/src/main/resources/db/changelog/ddl/changelog-0006.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1148 | `order/src/main/resources/db/changelog/ddl/changelog-0007.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1149 | `order/src/main/resources/db/changelog/ddl/changelog-0008.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1150 | `order/src/main/resources/db/changelog/ddl/changelog-0009.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1151 | `order/src/main/resources/db/changelog/ddl/changelog-0010.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1152 | `order/src/main/resources/db/changelog/ddl/changelog-0011.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1153 | `order/src/main/resources/db/changelog/ddl/changelog-0012.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1154 | `order/src/main/resources/db/changelog/ddl/changelog-0013.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1155 | `order/src/main/resources/db/changelog/ddl/changelog-0014.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1156 | `order/src/main/resources/db/changelog/ddl/changelog-0015.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1157 | `order/src/main/resources/db/changelog/ddl/changelog-0016.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1158 | `order/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 1159 | `order/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1160 | `order/src/test/java/com/yas/order/controller/CheckoutControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1161 | `order/src/test/java/com/yas/order/controller/OrderControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1162 | `order/src/test/java/com/yas/order/mapper/CheckoutMapperTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1163 | `order/src/test/java/com/yas/order/service/AbstractCircuitBreakFallbackHandlerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1164 | `order/src/test/java/com/yas/order/service/CartServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1165 | `order/src/test/java/com/yas/order/service/CheckoutServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1166 | `order/src/test/java/com/yas/order/service/CustomerServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1167 | `order/src/test/java/com/yas/order/service/PromotionServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1168 | `order/src/test/java/com/yas/order/service/TaxServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1169 | `order/src/test/java/com/yas/order/specification/OrderSpecificationTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1170 | `order/src/test/java/com/yas/order/utils/AuthenticationUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1171 | `order/src/test/java/com/yas/order/utils/SecurityContextUtils.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1172 | `order/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1173 | `order/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1174 | `order/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1175 | `payment/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1176 | `payment/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1177 | `payment/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1178 | `payment/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 1179 | `payment/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1180 | `payment/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1181 | `payment/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 1182 | `payment/src/it/java/com/yas/payment/constant/TestConstants.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1183 | `payment/src/it/java/com/yas/payment/controller/PaymentControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1184 | `payment/src/it/java/com/yas/payment/controller/PaymentProviderControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1185 | `payment/src/it/java/com/yas/payment/service/MediaServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1186 | `payment/src/it/java/com/yas/payment/service/OrderServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1187 | `payment/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1188 | `payment/src/it/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1189 | `payment/src/it/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1190 | `payment/src/main/java/com/yas/payment/config/DatabaseAutoConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1191 | `payment/src/main/java/com/yas/payment/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1192 | `payment/src/main/java/com/yas/payment/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1193 | `payment/src/main/java/com/yas/payment/config/ServiceUrlConfig.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1194 | `payment/src/main/java/com/yas/payment/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1195 | `payment/src/main/java/com/yas/payment/controller/PaymentController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1196 | `payment/src/main/java/com/yas/payment/controller/PaymentProviderController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1197 | `payment/src/main/java/com/yas/payment/mapper/CreatePaymentProviderMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1198 | `payment/src/main/java/com/yas/payment/mapper/PaymentProviderMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1199 | `payment/src/main/java/com/yas/payment/mapper/UpdatePaymentProviderMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1200 | `payment/src/main/java/com/yas/payment/model/CapturedPayment.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1201 | `payment/src/main/java/com/yas/payment/model/enumeration/PaymentMethod.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1202 | `payment/src/main/java/com/yas/payment/model/enumeration/PaymentStatus.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1203 | `payment/src/main/java/com/yas/payment/model/InitiatedPayment.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1204 | `payment/src/main/java/com/yas/payment/model/Payment.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1205 | `payment/src/main/java/com/yas/payment/model/PaymentProvider.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1206 | `payment/src/main/java/com/yas/payment/PaymentApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 1207 | `payment/src/main/java/com/yas/payment/repository/PaymentProviderRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1208 | `payment/src/main/java/com/yas/payment/repository/PaymentRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1209 | `payment/src/main/java/com/yas/payment/service/AbstractCircuitBreakFallbackHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1210 | `payment/src/main/java/com/yas/payment/service/MediaService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1211 | `payment/src/main/java/com/yas/payment/service/OrderService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1212 | `payment/src/main/java/com/yas/payment/service/PaymentProviderService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1213 | `payment/src/main/java/com/yas/payment/service/PaymentService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1214 | `payment/src/main/java/com/yas/payment/service/provider/handler/AbstractPaymentHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1215 | `payment/src/main/java/com/yas/payment/service/provider/handler/PaymentHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1216 | `payment/src/main/java/com/yas/payment/service/provider/handler/PaypalHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1217 | `payment/src/main/java/com/yas/payment/utils/Constants.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1218 | `payment/src/main/java/com/yas/payment/utils/MessagesUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1219 | `payment/src/main/java/com/yas/payment/viewmodel/CapturePaymentRequestVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1220 | `payment/src/main/java/com/yas/payment/viewmodel/CapturePaymentResponseVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1221 | `payment/src/main/java/com/yas/payment/viewmodel/CheckoutStatusVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1222 | `payment/src/main/java/com/yas/payment/viewmodel/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1223 | `payment/src/main/java/com/yas/payment/viewmodel/InitPaymentRequestVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1224 | `payment/src/main/java/com/yas/payment/viewmodel/InitPaymentResponseVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1225 | `payment/src/main/java/com/yas/payment/viewmodel/PaymentOrderStatusVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1226 | `payment/src/main/java/com/yas/payment/viewmodel/paymentprovider/CreatePaymentVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1227 | `payment/src/main/java/com/yas/payment/viewmodel/paymentprovider/MediaVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1228 | `payment/src/main/java/com/yas/payment/viewmodel/paymentprovider/PaymentProviderReqVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1229 | `payment/src/main/java/com/yas/payment/viewmodel/paymentprovider/PaymentProviderVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1230 | `payment/src/main/java/com/yas/payment/viewmodel/paymentprovider/UpdatePaymentVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1231 | `payment/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 1232 | `payment/src/main/resources/db/changelog/data/changelog-0001-provider.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1233 | `payment/src/main/resources/db/changelog/data/changelog-0002-provider.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1234 | `payment/src/main/resources/db/changelog/data/changelog-0003-provider.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1235 | `payment/src/main/resources/db/changelog/data/changelog-0004-provider.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1236 | `payment/src/main/resources/db/changelog/db.changelog-master.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1237 | `payment/src/main/resources/db/changelog/ddl/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1238 | `payment/src/main/resources/db/changelog/ddl/changelog-0002.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1239 | `payment/src/main/resources/db/changelog/ddl/changelog-0003.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1240 | `payment/src/main/resources/db/changelog/ddl/changelog-0004.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1241 | `payment/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 1242 | `payment/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1243 | `payment/src/test/java/com/yas/payment/service/AbstractCircuitBreakFallbackHandlerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1244 | `payment/src/test/java/com/yas/payment/service/MediaServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1245 | `payment/src/test/java/com/yas/payment/service/OrderServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1246 | `payment/src/test/java/com/yas/payment/service/PaymentProviderServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1247 | `payment/src/test/java/com/yas/payment/service/PaymentServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1248 | `payment/src/test/java/com/yas/payment/util/SecurityContextUtils.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1249 | `payment/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1250 | `payment/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1251 | `payment/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1252 | `payment-paypal/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1253 | `payment-paypal/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1254 | `payment-paypal/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1255 | `payment-paypal/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 1256 | `payment-paypal/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1257 | `payment-paypal/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1258 | `payment-paypal/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 1259 | `payment-paypal/src/it/java/com/yas/payment/paypal/config/IntegrationTestConfiguration.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1260 | `payment-paypal/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1261 | `payment-paypal/src/it/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1262 | `payment-paypal/src/it/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1263 | `payment-paypal/src/main/java/com/yas/payment/paypal/model/CheckoutIdHelper.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1264 | `payment-paypal/src/main/java/com/yas/payment/paypal/model/PaymentProviderHelper.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1265 | `payment-paypal/src/main/java/com/yas/payment/paypal/service/AbstractCircuitBreakFallbackHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1266 | `payment-paypal/src/main/java/com/yas/payment/paypal/service/PayPalHttpClientInitializer.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1267 | `payment-paypal/src/main/java/com/yas/payment/paypal/service/PaypalService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1268 | `payment-paypal/src/main/java/com/yas/payment/paypal/utils/AuthenticationUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1269 | `payment-paypal/src/main/java/com/yas/payment/paypal/utils/Constants.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1270 | `payment-paypal/src/main/java/com/yas/payment/paypal/utils/MessagesUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1271 | `payment-paypal/src/main/java/com/yas/payment/paypal/viewmodel/PaypalCapturePaymentRequest.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1272 | `payment-paypal/src/main/java/com/yas/payment/paypal/viewmodel/PaypalCapturePaymentResponse.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1273 | `payment-paypal/src/main/java/com/yas/payment/paypal/viewmodel/PaypalCreatePaymentRequest.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1274 | `payment-paypal/src/main/java/com/yas/payment/paypal/viewmodel/PaypalCreatePaymentResponse.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1275 | `payment-paypal/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 1276 | `payment-paypal/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 1277 | `payment-paypal/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1278 | `payment-paypal/src/test/java/com/yas/payment/paypal/model/CheckoutIdHelperTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1279 | `payment-paypal/src/test/java/com/yas/payment/paypal/service/AbstractCircuitBreakFallbackHandlerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1280 | `payment-paypal/src/test/java/com/yas/payment/paypal/service/PayPalHttpClientInitializerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1281 | `payment-paypal/src/test/java/com/yas/payment/paypal/service/PaypalServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1282 | `payment-paypal/src/test/java/com/yas/payment/paypal/utils/AuthenticationUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1283 | `payment-paypal/src/test/java/com/yas/payment/paypal/utils/MessagesUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1284 | `payment-paypal/src/test/java/com/yas/payment/paypal/utils/SecurityContextUtils.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1285 | `payment-paypal/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1286 | `payment-paypal/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1287 | `payment-paypal/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1288 | `pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 1289 | `postgres_init.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1290 | `product/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1291 | `product/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1292 | `product/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1293 | `product/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 1294 | `product/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1295 | `product/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1296 | `product/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 1297 | `product/src/it/java/com/yas/product/constant/TestConstants.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1298 | `product/src/it/java/com/yas/product/controller/BrandControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1299 | `product/src/it/java/com/yas/product/controller/CategoryControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1300 | `product/src/it/java/com/yas/product/controller/ProductControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1301 | `product/src/it/java/com/yas/product/service/MediaServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1302 | `product/src/it/java/com/yas/product/service/ProductServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1303 | `product/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1304 | `product/src/it/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1305 | `product/src/main/java/com/yas/product/config/DatabaseAutoConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1306 | `product/src/main/java/com/yas/product/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1307 | `product/src/main/java/com/yas/product/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1308 | `product/src/main/java/com/yas/product/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1309 | `product/src/main/java/com/yas/product/constants/PageableConstant.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1310 | `product/src/main/java/com/yas/product/controller/BrandController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1311 | `product/src/main/java/com/yas/product/controller/CategoryController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1312 | `product/src/main/java/com/yas/product/controller/ProductAttributeController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1313 | `product/src/main/java/com/yas/product/controller/ProductAttributeGroupController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1314 | `product/src/main/java/com/yas/product/controller/ProductAttributeValueController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1315 | `product/src/main/java/com/yas/product/controller/ProductController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1316 | `product/src/main/java/com/yas/product/controller/ProductOptionCombinationController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1317 | `product/src/main/java/com/yas/product/controller/ProductOptionController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1318 | `product/src/main/java/com/yas/product/controller/ProductOptionValueController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1319 | `product/src/main/java/com/yas/product/controller/ProductTemplateController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1320 | `product/src/main/java/com/yas/product/model/attribute/ProductAttribute.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1321 | `product/src/main/java/com/yas/product/model/attribute/ProductAttributeGroup.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1322 | `product/src/main/java/com/yas/product/model/attribute/ProductAttributeTemplate.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1323 | `product/src/main/java/com/yas/product/model/attribute/ProductAttributeValue.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1324 | `product/src/main/java/com/yas/product/model/attribute/ProductTemplate.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1325 | `product/src/main/java/com/yas/product/model/Brand.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1326 | `product/src/main/java/com/yas/product/model/Category.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1327 | `product/src/main/java/com/yas/product/model/enumeration/DimensionUnit.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1328 | `product/src/main/java/com/yas/product/model/enumeration/FilterExistInWhSelection.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1329 | `product/src/main/java/com/yas/product/model/Product.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1330 | `product/src/main/java/com/yas/product/model/ProductCategory.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1331 | `product/src/main/java/com/yas/product/model/ProductImage.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1332 | `product/src/main/java/com/yas/product/model/ProductOption.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1333 | `product/src/main/java/com/yas/product/model/ProductOptionCombination.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1334 | `product/src/main/java/com/yas/product/model/ProductOptionValue.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1335 | `product/src/main/java/com/yas/product/model/ProductOptionValueSaveVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1336 | `product/src/main/java/com/yas/product/model/ProductRelated.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1337 | `product/src/main/java/com/yas/product/model/ProductVariationSaveVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1338 | `product/src/main/java/com/yas/product/ProductApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 1339 | `product/src/main/java/com/yas/product/repository/BrandRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1340 | `product/src/main/java/com/yas/product/repository/CategoryRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1341 | `product/src/main/java/com/yas/product/repository/ProductAttributeGroupRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1342 | `product/src/main/java/com/yas/product/repository/ProductAttributeRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1343 | `product/src/main/java/com/yas/product/repository/ProductAttributeTemplateRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1344 | `product/src/main/java/com/yas/product/repository/ProductAttributeValueRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1345 | `product/src/main/java/com/yas/product/repository/ProductCategoryRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1346 | `product/src/main/java/com/yas/product/repository/ProductImageRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1347 | `product/src/main/java/com/yas/product/repository/ProductOptionCombinationRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1348 | `product/src/main/java/com/yas/product/repository/ProductOptionRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1349 | `product/src/main/java/com/yas/product/repository/ProductOptionValueRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1350 | `product/src/main/java/com/yas/product/repository/ProductRelatedRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1351 | `product/src/main/java/com/yas/product/repository/ProductRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1352 | `product/src/main/java/com/yas/product/repository/ProductTemplateRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1353 | `product/src/main/java/com/yas/product/service/AbstractCircuitBreakFallbackHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1354 | `product/src/main/java/com/yas/product/service/BrandService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1355 | `product/src/main/java/com/yas/product/service/CategoryService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1356 | `product/src/main/java/com/yas/product/service/MediaService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1357 | `product/src/main/java/com/yas/product/service/ProductAttributeGroupService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1358 | `product/src/main/java/com/yas/product/service/ProductAttributeService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1359 | `product/src/main/java/com/yas/product/service/ProductDetailService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1360 | `product/src/main/java/com/yas/product/service/ProductOptionService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1361 | `product/src/main/java/com/yas/product/service/ProductService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1362 | `product/src/main/java/com/yas/product/service/ProductTemplateService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1363 | `product/src/main/java/com/yas/product/utils/Constants.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1364 | `product/src/main/java/com/yas/product/utils/MessagesUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1365 | `product/src/main/java/com/yas/product/utils/ProductConverter.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1366 | `product/src/main/java/com/yas/product/validation/PriceValidator.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1367 | `product/src/main/java/com/yas/product/validation/ValidateProductPrice.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1368 | `product/src/main/java/com/yas/product/viewmodel/brand/BrandListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1369 | `product/src/main/java/com/yas/product/viewmodel/brand/BrandPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1370 | `product/src/main/java/com/yas/product/viewmodel/brand/BrandVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1371 | `product/src/main/java/com/yas/product/viewmodel/category/CategoryGetDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1372 | `product/src/main/java/com/yas/product/viewmodel/category/CategoryGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1373 | `product/src/main/java/com/yas/product/viewmodel/category/CategoryListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1374 | `product/src/main/java/com/yas/product/viewmodel/category/CategoryPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1375 | `product/src/main/java/com/yas/product/viewmodel/error/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1376 | `product/src/main/java/com/yas/product/viewmodel/ImageVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1377 | `product/src/main/java/com/yas/product/viewmodel/NoFileMediaVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1378 | `product/src/main/java/com/yas/product/viewmodel/product/ProductCheckoutListVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1379 | `product/src/main/java/com/yas/product/viewmodel/product/ProductDetailGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1380 | `product/src/main/java/com/yas/product/viewmodel/product/ProductDetailInfoVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1381 | `product/src/main/java/com/yas/product/viewmodel/product/ProductDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1382 | `product/src/main/java/com/yas/product/viewmodel/product/ProductEsDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1383 | `product/src/main/java/com/yas/product/viewmodel/product/ProductExportingDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1384 | `product/src/main/java/com/yas/product/viewmodel/product/ProductFeatureGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1385 | `product/src/main/java/com/yas/product/viewmodel/product/ProductGetCheckoutListVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1386 | `product/src/main/java/com/yas/product/viewmodel/product/ProductGetDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1387 | `product/src/main/java/com/yas/product/viewmodel/product/ProductInfoVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1388 | `product/src/main/java/com/yas/product/viewmodel/product/ProductListGetFromCategoryVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1389 | `product/src/main/java/com/yas/product/viewmodel/product/ProductListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1390 | `product/src/main/java/com/yas/product/viewmodel/product/ProductListVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1391 | `product/src/main/java/com/yas/product/viewmodel/product/ProductOptionCombinationGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1392 | `product/src/main/java/com/yas/product/viewmodel/product/ProductOptionValueDisplay.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1393 | `product/src/main/java/com/yas/product/viewmodel/product/ProductOptionValueGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1394 | `product/src/main/java/com/yas/product/viewmodel/product/ProductPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1395 | `product/src/main/java/com/yas/product/viewmodel/product/ProductProperties.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1396 | `product/src/main/java/com/yas/product/viewmodel/product/ProductPutVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1397 | `product/src/main/java/com/yas/product/viewmodel/product/ProductQuantityPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1398 | `product/src/main/java/com/yas/product/viewmodel/product/ProductQuantityPutVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1399 | `product/src/main/java/com/yas/product/viewmodel/product/ProductSaveVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1400 | `product/src/main/java/com/yas/product/viewmodel/product/ProductsGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1401 | `product/src/main/java/com/yas/product/viewmodel/product/ProductSlugGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1402 | `product/src/main/java/com/yas/product/viewmodel/product/ProductThumbnailGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1403 | `product/src/main/java/com/yas/product/viewmodel/product/ProductThumbnailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1404 | `product/src/main/java/com/yas/product/viewmodel/product/ProductVariationGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1405 | `product/src/main/java/com/yas/product/viewmodel/product/ProductVariationPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1406 | `product/src/main/java/com/yas/product/viewmodel/product/ProductVariationPutVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1407 | `product/src/main/java/com/yas/product/viewmodel/productattribute/ProductAttributeGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1408 | `product/src/main/java/com/yas/product/viewmodel/productattribute/ProductAttributeGroupGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1409 | `product/src/main/java/com/yas/product/viewmodel/productattribute/ProductAttributeGroupListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1410 | `product/src/main/java/com/yas/product/viewmodel/productattribute/ProductAttributeGroupPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1411 | `product/src/main/java/com/yas/product/viewmodel/productattribute/ProductAttributeGroupVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1412 | `product/src/main/java/com/yas/product/viewmodel/productattribute/ProductAttributeListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1413 | `product/src/main/java/com/yas/product/viewmodel/productattribute/ProductAttributePostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1414 | `product/src/main/java/com/yas/product/viewmodel/productattribute/ProductAttributeValueGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1415 | `product/src/main/java/com/yas/product/viewmodel/productattribute/ProductAttributeValuePostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1416 | `product/src/main/java/com/yas/product/viewmodel/productattribute/ProductAttributeValueVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1417 | `product/src/main/java/com/yas/product/viewmodel/productattribute/ProductAttributeVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1418 | `product/src/main/java/com/yas/product/viewmodel/productoption/ProductOptionGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1419 | `product/src/main/java/com/yas/product/viewmodel/productoption/ProductOptionListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1420 | `product/src/main/java/com/yas/product/viewmodel/productoption/ProductOptionPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1421 | `product/src/main/java/com/yas/product/viewmodel/productoption/ProductOptionValueGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1422 | `product/src/main/java/com/yas/product/viewmodel/productoption/ProductOptionValuePostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1423 | `product/src/main/java/com/yas/product/viewmodel/productoption/ProductOptionValuePutVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1424 | `product/src/main/java/com/yas/product/viewmodel/producttemplate/ProductAttributeTemplateGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1425 | `product/src/main/java/com/yas/product/viewmodel/producttemplate/ProductAttributeTemplatePostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1426 | `product/src/main/java/com/yas/product/viewmodel/producttemplate/ProductTemplateGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1427 | `product/src/main/java/com/yas/product/viewmodel/producttemplate/ProductTemplateListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1428 | `product/src/main/java/com/yas/product/viewmodel/producttemplate/ProductTemplatePostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1429 | `product/src/main/java/com/yas/product/viewmodel/producttemplate/ProductTemplateVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1430 | `product/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 1431 | `product/src/main/resources/db/changelog/db.changelog-master.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1432 | `product/src/main/resources/db/changelog/ddl/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1433 | `product/src/main/resources/db/changelog/ddl/changelog-0002.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1434 | `product/src/main/resources/db/changelog/ddl/changelog-0003.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1435 | `product/src/main/resources/db/changelog/ddl/changelog-0004.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1436 | `product/src/main/resources/db/changelog/ddl/changelog-0005.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1437 | `product/src/main/resources/db/changelog/ddl/changelog-0006.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1438 | `product/src/main/resources/db/changelog/ddl/changelog-0007-product-related.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1439 | `product/src/main/resources/db/changelog/ddl/changelog-0008.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1440 | `product/src/main/resources/db/changelog/ddl/changelog-0008-add-taxIncluded-table-product.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1441 | `product/src/main/resources/db/changelog/ddl/changelog-0010.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1442 | `product/src/main/resources/db/changelog/ddl/changelog-0011.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1443 | `product/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 1444 | `product/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1445 | `product/src/test/java/com/yas/product/controller/BrandControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1446 | `product/src/test/java/com/yas/product/controller/CategoryControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1447 | `product/src/test/java/com/yas/product/controller/ProductAttributeControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1448 | `product/src/test/java/com/yas/product/controller/ProductAttributeGroupControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1449 | `product/src/test/java/com/yas/product/controller/ProductAttributeValueControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1450 | `product/src/test/java/com/yas/product/controller/ProductControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1451 | `product/src/test/java/com/yas/product/controller/ProductOptionCombinationControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1452 | `product/src/test/java/com/yas/product/controller/ProductOptionControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1453 | `product/src/test/java/com/yas/product/controller/ProductOptionValueControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1454 | `product/src/test/java/com/yas/product/controller/ProductTemplateControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1455 | `product/src/test/java/com/yas/product/service/AbstractCircuitBreakFallbackHandlerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1456 | `product/src/test/java/com/yas/product/service/BrandServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1457 | `product/src/test/java/com/yas/product/service/CategoryServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1458 | `product/src/test/java/com/yas/product/service/ProductAttributeGroupServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1459 | `product/src/test/java/com/yas/product/service/ProductAttributeServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1460 | `product/src/test/java/com/yas/product/service/ProductOptionServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1461 | `product/src/test/java/com/yas/product/service/ProductTemplateServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1462 | `product/src/test/java/com/yas/product/utils/ProductConverterTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1463 | `product/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1464 | `product/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1465 | `product/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1466 | `promotion/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1467 | `promotion/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1468 | `promotion/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1469 | `promotion/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 1470 | `promotion/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1471 | `promotion/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1472 | `promotion/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 1473 | `promotion/src/it/java/com/yas/promotion/service/PromotionServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1474 | `promotion/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1475 | `promotion/src/it/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1476 | `promotion/src/it/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1477 | `promotion/src/main/java/com/yas/promotion/config/DatabaseAutoConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1478 | `promotion/src/main/java/com/yas/promotion/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1479 | `promotion/src/main/java/com/yas/promotion/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1480 | `promotion/src/main/java/com/yas/promotion/config/ServiceUrlConfig.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1481 | `promotion/src/main/java/com/yas/promotion/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1482 | `promotion/src/main/java/com/yas/promotion/controller/PromotionController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1483 | `promotion/src/main/java/com/yas/promotion/model/enumeration/ApplyTo.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1484 | `promotion/src/main/java/com/yas/promotion/model/enumeration/DiscountType.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1485 | `promotion/src/main/java/com/yas/promotion/model/enumeration/UsageType.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1486 | `promotion/src/main/java/com/yas/promotion/model/Promotion.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1487 | `promotion/src/main/java/com/yas/promotion/model/PromotionApply.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1488 | `promotion/src/main/java/com/yas/promotion/model/PromotionUsage.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1489 | `promotion/src/main/java/com/yas/promotion/PromotionApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 1490 | `promotion/src/main/java/com/yas/promotion/repository/PromotionRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1491 | `promotion/src/main/java/com/yas/promotion/repository/PromotionUsageRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1492 | `promotion/src/main/java/com/yas/promotion/service/AbstractCircuitBreakFallbackHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1493 | `promotion/src/main/java/com/yas/promotion/service/ProductService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1494 | `promotion/src/main/java/com/yas/promotion/service/PromotionService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1495 | `promotion/src/main/java/com/yas/promotion/utils/AuthenticationUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1496 | `promotion/src/main/java/com/yas/promotion/utils/Constants.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1497 | `promotion/src/main/java/com/yas/promotion/utils/MessagesUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1498 | `promotion/src/main/java/com/yas/promotion/validation/PromotionConstraint.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1499 | `promotion/src/main/java/com/yas/promotion/validation/PromotionValidator.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1500 | `promotion/src/main/java/com/yas/promotion/viewmodel/BrandVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1501 | `promotion/src/main/java/com/yas/promotion/viewmodel/CategoryGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1502 | `promotion/src/main/java/com/yas/promotion/viewmodel/error/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1503 | `promotion/src/main/java/com/yas/promotion/viewmodel/ProductVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1504 | `promotion/src/main/java/com/yas/promotion/viewmodel/PromotionDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1505 | `promotion/src/main/java/com/yas/promotion/viewmodel/PromotionDto.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1506 | `promotion/src/main/java/com/yas/promotion/viewmodel/PromotionListVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1507 | `promotion/src/main/java/com/yas/promotion/viewmodel/PromotionPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1508 | `promotion/src/main/java/com/yas/promotion/viewmodel/PromotionPutVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1509 | `promotion/src/main/java/com/yas/promotion/viewmodel/PromotionUsageVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1510 | `promotion/src/main/java/com/yas/promotion/viewmodel/PromotionVerifyResultDto.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1511 | `promotion/src/main/java/com/yas/promotion/viewmodel/PromotionVerifyVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1512 | `promotion/src/main/java/com/yas/promotion/viewmodel/PromotionVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1513 | `promotion/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 1514 | `promotion/src/main/resources/db/changelog/data/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1515 | `promotion/src/main/resources/db/changelog/db.changelog-master.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1516 | `promotion/src/main/resources/db/changelog/ddl/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1517 | `promotion/src/main/resources/db/changelog/ddl/changelog-0002.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1518 | `promotion/src/main/resources/db/changelog/ddl/changelog-0003.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1519 | `promotion/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 1520 | `promotion/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1521 | `promotion/src/test/java/com/yas/promotion/controller/PromotionControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1522 | `promotion/src/test/java/com/yas/promotion/service/ProductServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1523 | `promotion/src/test/java/com/yas/promotion/service/PromotionServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1524 | `promotion/src/test/java/com/yas/promotion/util/SecurityContextUtils.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1525 | `promotion/src/test/java/com/yas/promotion/utils/AuthenticationUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1526 | `promotion/src/test/java/com/yas/promotion/utils/MessagesUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1527 | `promotion/src/test/java/com/yas/promotion/validation/PromotionValidatorTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1528 | `promotion/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1529 | `promotion/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1530 | `promotion/src/test/resources/mockito-extentions/org.mokito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1531 | `rating/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1532 | `rating/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1533 | `rating/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1534 | `rating/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 1535 | `rating/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1536 | `rating/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1537 | `rating/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 1538 | `rating/src/it/java/com/yas/rating/controller/RatingControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1539 | `rating/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1540 | `rating/src/it/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1541 | `rating/src/it/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1542 | `rating/src/main/java/com/yas/rating/config/DatabaseAutoConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1543 | `rating/src/main/java/com/yas/rating/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1544 | `rating/src/main/java/com/yas/rating/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1545 | `rating/src/main/java/com/yas/rating/config/ServiceUrlConfig.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1546 | `rating/src/main/java/com/yas/rating/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1547 | `rating/src/main/java/com/yas/rating/controller/RatingController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1548 | `rating/src/main/java/com/yas/rating/model/Rating.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1549 | `rating/src/main/java/com/yas/rating/RatingApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 1550 | `rating/src/main/java/com/yas/rating/repository/RatingRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1551 | `rating/src/main/java/com/yas/rating/service/AbstractCircuitBreakFallbackHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1552 | `rating/src/main/java/com/yas/rating/service/CustomerService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1553 | `rating/src/main/java/com/yas/rating/service/OrderService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1554 | `rating/src/main/java/com/yas/rating/service/RatingService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1555 | `rating/src/main/java/com/yas/rating/utils/AuthenticationUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1556 | `rating/src/main/java/com/yas/rating/utils/Constants.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1557 | `rating/src/main/java/com/yas/rating/utils/MessagesUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1558 | `rating/src/main/java/com/yas/rating/viewmodel/CustomerVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1559 | `rating/src/main/java/com/yas/rating/viewmodel/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1560 | `rating/src/main/java/com/yas/rating/viewmodel/OrderExistsByProductAndUserGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1561 | `rating/src/main/java/com/yas/rating/viewmodel/RatingListVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1562 | `rating/src/main/java/com/yas/rating/viewmodel/RatingPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1563 | `rating/src/main/java/com/yas/rating/viewmodel/RatingVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1564 | `rating/src/main/java/com/yas/rating/viewmodel/ResponeStatusVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1565 | `rating/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 1566 | `rating/src/main/resources/db/changelog/data/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1567 | `rating/src/main/resources/db/changelog/db.changelog-master.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1568 | `rating/src/main/resources/db/changelog/ddl/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1569 | `rating/src/main/resources/db/changelog/ddl/changelog-0002.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 1570 | `rating/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 1571 | `rating/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1572 | `rating/src/test/java/com/yas/rating/controller/RatingControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1573 | `rating/src/test/java/com/yas/rating/RatingApplicationTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1574 | `rating/src/test/java/com/yas/rating/service/CustomerServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1575 | `rating/src/test/java/com/yas/rating/service/OrderServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1576 | `rating/src/test/java/com/yas/rating/service/RatingServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1577 | `rating/src/test/java/com/yas/rating/util/AuthenticationUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1578 | `rating/src/test/java/com/yas/rating/util/MessagesUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1579 | `rating/src/test/java/com/yas/rating/util/SecurityContextUtils.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1580 | `rating/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1581 | `rating/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1582 | `rating/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1583 | `README.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 1584 | `recommendation/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 1585 | `recommendation/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 1586 | `recommendation/src/it/java/com/yas/recommendation/configuration/KafkaConfiguration.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1587 | `recommendation/src/it/java/com/yas/recommendation/kafka/ProductCdcConsumerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1588 | `recommendation/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1589 | `recommendation/src/main/java/com/yas/recommendation/configuration/AppConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1590 | `recommendation/src/main/java/com/yas/recommendation/configuration/EmbeddingSearchConfiguration.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1591 | `recommendation/src/main/java/com/yas/recommendation/configuration/RecommendationConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1592 | `recommendation/src/main/java/com/yas/recommendation/configuration/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1593 | `recommendation/src/main/java/com/yas/recommendation/configuration/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1594 | `recommendation/src/main/java/com/yas/recommendation/configuration/VectorStoreConfiguration.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1595 | `recommendation/src/main/java/com/yas/recommendation/configuration/VectorStoreProperties.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1596 | `recommendation/src/main/java/com/yas/recommendation/constant/Action.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1597 | `recommendation/src/main/java/com/yas/recommendation/controller/EmbeddingQueryController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1598 | `recommendation/src/main/java/com/yas/recommendation/kafka/config/consumer/AppKafkaListenerConfigurer.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1599 | `recommendation/src/main/java/com/yas/recommendation/kafka/config/consumer/ProductCdcKafkaListenerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1600 | `recommendation/src/main/java/com/yas/recommendation/kafka/consumer/ProductSyncDataConsumer.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1601 | `recommendation/src/main/java/com/yas/recommendation/kafka/consumer/ProductSyncService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1602 | `recommendation/src/main/java/com/yas/recommendation/RecommendationApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 1603 | `recommendation/src/main/java/com/yas/recommendation/service/ProductService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1604 | `recommendation/src/main/java/com/yas/recommendation/vector/common/document/BaseDocument.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1605 | `recommendation/src/main/java/com/yas/recommendation/vector/common/document/DefaultIdGenerator.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1606 | `recommendation/src/main/java/com/yas/recommendation/vector/common/document/DocumentMetadata.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1607 | `recommendation/src/main/java/com/yas/recommendation/vector/common/formatter/DefaultDocumentFormatter.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1608 | `recommendation/src/main/java/com/yas/recommendation/vector/common/formatter/DocumentFormatter.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1609 | `recommendation/src/main/java/com/yas/recommendation/vector/common/query/DocumentRowMapper.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1610 | `recommendation/src/main/java/com/yas/recommendation/vector/common/query/JdbcVectorService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1611 | `recommendation/src/main/java/com/yas/recommendation/vector/common/query/VectorQuery.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1612 | `recommendation/src/main/java/com/yas/recommendation/vector/common/store/SimpleVectorRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1613 | `recommendation/src/main/java/com/yas/recommendation/vector/common/store/VectorRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1614 | `recommendation/src/main/java/com/yas/recommendation/vector/product/document/ProductDocument.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1615 | `recommendation/src/main/java/com/yas/recommendation/vector/product/formatter/ProductDocumentFormatter.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1616 | `recommendation/src/main/java/com/yas/recommendation/vector/product/query/RelatedProductQuery.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1617 | `recommendation/src/main/java/com/yas/recommendation/vector/product/service/ProductVectorSyncService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1618 | `recommendation/src/main/java/com/yas/recommendation/vector/product/store/ProductVectorRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1619 | `recommendation/src/main/java/com/yas/recommendation/viewmodel/CategoryVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1620 | `recommendation/src/main/java/com/yas/recommendation/viewmodel/ImageVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1621 | `recommendation/src/main/java/com/yas/recommendation/viewmodel/ProductAttributeValueVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1622 | `recommendation/src/main/java/com/yas/recommendation/viewmodel/ProductDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1623 | `recommendation/src/main/java/com/yas/recommendation/viewmodel/ProductVariationVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1624 | `recommendation/src/main/java/com/yas/recommendation/viewmodel/RelatedProductVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1625 | `recommendation/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 1626 | `recommendation/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 1627 | `recommendation/src/test/java/com/yas/recommendation/config/KafkaIntegrationTestConfiguration.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1628 | `recommendation/src/test/java/com/yas/recommendation/controller/EmbeddingQueryControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1629 | `recommendation/src/test/java/com/yas/recommendation/kafka/consumer/ProductSyncServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1630 | `recommendation/src/test/java/com/yas/recommendation/query/VectorQueryTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1631 | `recommendation/src/test/java/com/yas/recommendation/service/ProductServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1632 | `recommendation/src/test/java/com/yas/recommendation/store/BaseVectorRepositoryTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1633 | `recommendation/src/test/java/com/yas/recommendation/store/ProductVectorRepositoryTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1634 | `recommendation/src/test/java/com/yas/recommendation/vector/product/formatter/ProductDocumentFormatterTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1635 | `recommendation/src/test/java/com/yas/recommendation/vector/product/service/ProductVectorSyncServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1636 | `recommendation/src/test/resources/application-test.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1637 | `recommendation/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1638 | `recommendation/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1639 | `recommendation/src/test/resources/test-realm.json` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1640 | `sampledata/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1641 | `sampledata/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1642 | `sampledata/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1643 | `sampledata/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 1644 | `sampledata/images/sample/category/DELL_category.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1645 | `sampledata/images/sample/category/iphone_category.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1646 | `sampledata/images/sample/category/laptop_category.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1647 | `sampledata/images/sample/category/phone_category.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1648 | `sampledata/images/sample/category/Samsung_category.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1649 | `sampledata/images/sample/category/tablet_category.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1650 | `sampledata/images/sample/payment-provider/cod.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1651 | `sampledata/images/sample/payment-provider/paypal.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1652 | `sampledata/images/sample/product/dellIns/DELL_INS_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1653 | `sampledata/images/sample/product/dellIns/DELL_INS_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1654 | `sampledata/images/sample/product/dellIns/DELL_INS_3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1655 | `sampledata/images/sample/product/dellIns/DELL_INS_4.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1656 | `sampledata/images/sample/product/dellIns/DELL_INS_Thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1657 | `sampledata/images/sample/product/dellXPS/DELL_XPS_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1658 | `sampledata/images/sample/product/dellXPS/DELL_XPS_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1659 | `sampledata/images/sample/product/dellXPS/DELL_XPS_Thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1660 | `sampledata/images/sample/product/galaxyTab/Galaxy_Tab_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1661 | `sampledata/images/sample/product/galaxyTab/Galaxy_Tab_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1662 | `sampledata/images/sample/product/galaxyTab/Galaxy_Tab_3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1663 | `sampledata/images/sample/product/galaxyTab/Galaxy_Tab_4.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1664 | `sampledata/images/sample/product/galaxyTab/Galaxy_Tab_Thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1665 | `sampledata/images/sample/product/ip15/iphone15_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1666 | `sampledata/images/sample/product/ip15/iphone15_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1667 | `sampledata/images/sample/product/ip15/iphone15_3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1668 | `sampledata/images/sample/product/ip15/iphone15_4.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1669 | `sampledata/images/sample/product/ip15/iphone15_5.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1670 | `sampledata/images/sample/product/ip15/iphone15_thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1671 | `sampledata/images/sample/product/ip15plus/iphone15_Plus_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1672 | `sampledata/images/sample/product/ip15plus/iphone15_Plus_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1673 | `sampledata/images/sample/product/ip15plus/iphone15_Plus_3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1674 | `sampledata/images/sample/product/ip15plus/iphone15_Plus_4.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1675 | `sampledata/images/sample/product/ip15plus/iphone15_Plus_5.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1676 | `sampledata/images/sample/product/ip15plus/iphone15_Plus_6.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1677 | `sampledata/images/sample/product/ip15plus/iphone15_Plus_thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1678 | `sampledata/images/sample/product/ip15pro/15pro_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1679 | `sampledata/images/sample/product/ip15pro/15pro_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1680 | `sampledata/images/sample/product/ip15pro/15pro_3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1681 | `sampledata/images/sample/product/ip15pro/15pro_4.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1682 | `sampledata/images/sample/product/ip15pro/15pro_thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1683 | `sampledata/images/sample/product/ip15proMax/iphone15promax_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1684 | `sampledata/images/sample/product/ip15proMax/iphone15promax_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1685 | `sampledata/images/sample/product/ip15proMax/iphone15promax_3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1686 | `sampledata/images/sample/product/ip15proMax/iphone15promax_4.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1687 | `sampledata/images/sample/product/ip15proMax/iphone15promax_5.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1688 | `sampledata/images/sample/product/ip15proMax/iphone15promax_thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1689 | `sampledata/images/sample/product/ipad11/iPad_11_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1690 | `sampledata/images/sample/product/ipad11/iPad_11_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1691 | `sampledata/images/sample/product/ipad11/iPad_11_3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1692 | `sampledata/images/sample/product/ipad11/iPad_11_4.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1693 | `sampledata/images/sample/product/ipad11/iPad_11_Thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1694 | `sampledata/images/sample/product/ipad5/iPad_5th_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1695 | `sampledata/images/sample/product/ipad5/iPad_5th_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1696 | `sampledata/images/sample/product/ipad5/iPad_5th_3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1697 | `sampledata/images/sample/product/ipad5/iPad_5th_4.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1698 | `sampledata/images/sample/product/ipad5/iPad_5th_thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1699 | `sampledata/images/sample/product/macbook/Mac_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1700 | `sampledata/images/sample/product/macbook/Mac_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1701 | `sampledata/images/sample/product/macbook/Mac_3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1702 | `sampledata/images/sample/product/macbook/Mac_4.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1703 | `sampledata/images/sample/product/macbook/Mac_Thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1704 | `sampledata/images/sample/product/macbookAir/MacAir_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1705 | `sampledata/images/sample/product/macbookAir/MacAir_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1706 | `sampledata/images/sample/product/macbookAir/MacAir_4.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1707 | `sampledata/images/sample/product/macbookAir/MacAir_Thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1708 | `sampledata/images/sample/product/macbookAirM3/MacAir_M3_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1709 | `sampledata/images/sample/product/macbookAirM3/MacAir_M3_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1710 | `sampledata/images/sample/product/macbookAirM3/MacAir_M3_3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1711 | `sampledata/images/sample/product/macbookAirM3/MacAir_M3_Thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1712 | `sampledata/images/sample/product/samsungZ/Sam-Z_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1713 | `sampledata/images/sample/product/samsungZ/Sam-Z_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1714 | `sampledata/images/sample/product/samsungZ/Sam-Z_3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1715 | `sampledata/images/sample/product/samsungZ/Sam-Z_4.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1716 | `sampledata/images/sample/product/samsungZ/Sam-Z_5.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1717 | `sampledata/images/sample/product/samsungZ/Sam-Z_Thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1718 | `sampledata/images/sample/product/samsungZFlip/Sam-Z_Flip_1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1719 | `sampledata/images/sample/product/samsungZFlip/Sam-Z_Flip_2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1720 | `sampledata/images/sample/product/samsungZFlip/Sam-Z_Flip_3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1721 | `sampledata/images/sample/product/samsungZFlip/Sam-Z_Flip_4.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1722 | `sampledata/images/sample/product/samsungZFlip/Sam-Z_Flip_5.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1723 | `sampledata/images/sample/product/samsungZFlip/Sam-Z_Flip_Thumbnail.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1724 | `sampledata/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1725 | `sampledata/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1726 | `sampledata/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 1727 | `sampledata/src/main/java/com/yas/sampledata/config/DataSourceConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1728 | `sampledata/src/main/java/com/yas/sampledata/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1729 | `sampledata/src/main/java/com/yas/sampledata/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1730 | `sampledata/src/main/java/com/yas/sampledata/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1731 | `sampledata/src/main/java/com/yas/sampledata/controller/SampleDataController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1732 | `sampledata/src/main/java/com/yas/sampledata/SampleDataApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 1733 | `sampledata/src/main/java/com/yas/sampledata/service/SampleDataService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1734 | `sampledata/src/main/java/com/yas/sampledata/utils/MessagesUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1735 | `sampledata/src/main/java/com/yas/sampledata/utils/SqlScriptExecutor.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1736 | `sampledata/src/main/java/com/yas/sampledata/viewmodel/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1737 | `sampledata/src/main/java/com/yas/sampledata/viewmodel/SampleDataVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1738 | `sampledata/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 1739 | `sampledata/src/main/resources/db/media/media.sql` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1740 | `sampledata/src/main/resources/db/product/product.sql` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1741 | `sampledata/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 1742 | `sampledata/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1743 | `sampledata/src/test/java/com/yas/sampledata/controller/SampleDataControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1744 | `sampledata/src/test/java/com/yas/sampledata/service/SampleDataServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1745 | `sampledata/src/test/java/com/yas/sampledata/utils/MessagesUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1746 | `sampledata/src/test/java/com/yas/sampledata/utils/SqlScriptExecutorTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1747 | `sampledata/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1748 | `sampledata/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1749 | `sampledata/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1750 | `screenshots/yas-backoffice.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1751 | `screenshots/yas-grafana-metrics.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1752 | `screenshots/yas-grafana-tracing.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1753 | `screenshots/yas-storefront.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1754 | `screenshots/yas-swagger.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1755 | `scripts/postman/Cart Service API.postman_collection.json` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1756 | `scripts/postman/Order Service API.postman_collection.json` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1757 | `scripts/postman/Payment Service API.postman_collection.json` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1758 | `scripts/postman/PayPal Service API.postman_collection.json` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1759 | `scripts/postman/Token.postman_collection.json` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1760 | `search/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1761 | `search/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1762 | `search/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1763 | `search/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 1764 | `search/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1765 | `search/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1766 | `search/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 1767 | `search/src/it/java/com/yas/search/config/ElasticTestContainer.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1768 | `search/src/it/java/com/yas/search/config/SearchIntegrationTestConfiguration.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1769 | `search/src/it/java/com/yas/search/controller/ProductControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1770 | `search/src/it/java/com/yas/search/kafka/ProductCdcConsumerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1771 | `search/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1772 | `search/src/it/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1773 | `search/src/it/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1774 | `search/src/main/java/com/yas/search/config/CorsConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1775 | `search/src/main/java/com/yas/search/config/ElasticsearchDataConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1776 | `search/src/main/java/com/yas/search/config/ImperativeClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1777 | `search/src/main/java/com/yas/search/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1778 | `search/src/main/java/com/yas/search/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1779 | `search/src/main/java/com/yas/search/config/ServiceUrlConfig.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1780 | `search/src/main/java/com/yas/search/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1781 | `search/src/main/java/com/yas/search/constant/Action.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1782 | `search/src/main/java/com/yas/search/constant/enums/SortType.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1783 | `search/src/main/java/com/yas/search/constant/MessageCode.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1784 | `search/src/main/java/com/yas/search/constant/ProductField.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1785 | `search/src/main/java/com/yas/search/controller/ProductController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 1786 | `search/src/main/java/com/yas/search/ElasticsearchApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 1787 | `search/src/main/java/com/yas/search/kafka/config/consumer/AppKafkaListenerConfigurer.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1788 | `search/src/main/java/com/yas/search/kafka/config/consumer/ProductCdcKafkaListenerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1789 | `search/src/main/java/com/yas/search/kafka/consumer/ProductSyncDataConsumer.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1790 | `search/src/main/java/com/yas/search/model/Product.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1791 | `search/src/main/java/com/yas/search/model/ProductCriteriaDto.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1792 | `search/src/main/java/com/yas/search/repository/ProductRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 1793 | `search/src/main/java/com/yas/search/service/ProductService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1794 | `search/src/main/java/com/yas/search/service/ProductSyncDataService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 1795 | `search/src/main/java/com/yas/search/utils/MessagesUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 1796 | `search/src/main/java/com/yas/search/viewmodel/error/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1797 | `search/src/main/java/com/yas/search/viewmodel/ProductEsDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1798 | `search/src/main/java/com/yas/search/viewmodel/ProductGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1799 | `search/src/main/java/com/yas/search/viewmodel/ProductListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1800 | `search/src/main/java/com/yas/search/viewmodel/ProductNameGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1801 | `search/src/main/java/com/yas/search/viewmodel/ProductNameListVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 1802 | `search/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 1803 | `search/src/main/resources/esconfig/elastic-analyzer.json` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1804 | `search/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 1805 | `search/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1806 | `search/src/test/java/com/yas/search/consumer/ProductSyncDataConsumerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1807 | `search/src/test/java/com/yas/search/controller/ProductControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1808 | `search/src/test/java/com/yas/search/service/ProductServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1809 | `search/src/test/java/com/yas/search/service/ProductSyncDataServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1810 | `search/src/test/java/com/yas/search/utils/MessagesUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1811 | `search/src/test/java/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1812 | `search/src/test/java/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1813 | `search/src/test/java/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1814 | `search/src/test/java/resources/test-realm.json` | TEST | Unit, integration, fixture hoặc test configuration. |
| 1815 | `search/wait-for-it.sh` | SCRIPT | Imperative automation/bootstrap helper. |
| 1816 | `start-source-connectors.sh` | SCRIPT | Imperative automation/bootstrap helper. |
| 1817 | `start-yas.sh` | SCRIPT | Imperative automation/bootstrap helper. |
| 1818 | `storefront/.env.development` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1819 | `storefront/.env.production` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1820 | `storefront/.eslintrc.json` | QUALITY-CONFIG | Lint, scan, ignore hoặc repository quality policy. |
| 1821 | `storefront/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1822 | `storefront/.prettierignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1823 | `storefront/.prettierrc` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 1824 | `storefront/__tests__/pages/redirect/index.test.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1825 | `storefront/asset/data/data_header_client.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1826 | `storefront/asset/icons/icon-pay-01.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1827 | `storefront/asset/icons/icon-pay-02.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1828 | `storefront/asset/icons/icon-pay-03.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1829 | `storefront/asset/icons/icon-pay-04.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1830 | `storefront/asset/icons/icon-pay-05.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1831 | `storefront/asset/images/main-banner-1.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1832 | `storefront/asset/images/main-banner-2.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1833 | `storefront/asset/images/main-banner-3.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1834 | `storefront/asset/images/no-result.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1835 | `storefront/asset/images/sub-banner.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1836 | `storefront/common/components/AuthenticationInfo.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1837 | `storefront/common/components/BreadcrumbComponent.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1838 | `storefront/common/components/common/Footer.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1839 | `storefront/common/components/common/Header.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1840 | `storefront/common/components/dialog/ConfirmationDialog.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1841 | `storefront/common/components/ImageWithFallback.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1842 | `storefront/common/components/Layout.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1843 | `storefront/common/components/ProductCard.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1844 | `storefront/common/components/ProductCardBase.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1845 | `storefront/common/components/ProductImageGallery.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1846 | `storefront/common/components/ProfileLayout.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1847 | `storefront/common/components/SimilarProductCard.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1848 | `storefront/common/components/SpinnerComponent.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1849 | `storefront/common/components/UserProfileLeftSideBar.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1850 | `storefront/common/constants/Common.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1851 | `storefront/common/constants/Common.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1852 | `storefront/common/images/bg_banner.jpg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1853 | `storefront/common/images/default-avatar.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1854 | `storefront/common/images/search-promote-image.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 1855 | `storefront/common/items/Banner.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1856 | `storefront/common/items/Input.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1857 | `storefront/common/items/ModalChooseDefaultAddress.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1858 | `storefront/common/items/ModalDeleteCustom.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1859 | `storefront/common/items/OptionSelect.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1860 | `storefront/common/items/ProductItems.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1861 | `storefront/common/services/ApiClientService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1862 | `storefront/common/services/ApiClientService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1863 | `storefront/common/services/errors/YasError.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1864 | `storefront/common/services/errors/YasError.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1865 | `storefront/context/AppContext.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1866 | `storefront/context/CartContext.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1867 | `storefront/context/UserInfoContext.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1868 | `storefront/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 1869 | `storefront/modules/address/components/AddressCard.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1870 | `storefront/modules/address/components/AddressForm.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1871 | `storefront/modules/address/models/AddressModel.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1872 | `storefront/modules/address/services/AddressService.test.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1873 | `storefront/modules/address/services/AddressService.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1874 | `storefront/modules/breadcrumb/model/BreadcrumbModel.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1875 | `storefront/modules/cart/components/CartItem.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1876 | `storefront/modules/cart/models/CartItemDeleteVm.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1877 | `storefront/modules/cart/models/CartItemGetVm.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1878 | `storefront/modules/cart/models/CartItemPostVm.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1879 | `storefront/modules/cart/models/CartItemPutVm.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1880 | `storefront/modules/cart/models/ProductSlug.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1881 | `storefront/modules/cart/services/CartService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1882 | `storefront/modules/cart/services/CartService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1883 | `storefront/modules/catalog/components/DetailHeader.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1884 | `storefront/modules/catalog/components/index.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1885 | `storefront/modules/catalog/components/ProductDetails.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1886 | `storefront/modules/catalog/components/RelatedProducts.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1887 | `storefront/modules/catalog/components/SimilarProducts.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1888 | `storefront/modules/catalog/models/Category.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1889 | `storefront/modules/catalog/models/Media.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1890 | `storefront/modules/catalog/models/Product.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1891 | `storefront/modules/catalog/models/ProductAttribute.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1892 | `storefront/modules/catalog/models/ProductDetail.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1893 | `storefront/modules/catalog/models/ProductFeature.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1894 | `storefront/modules/catalog/models/ProductOptions.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1895 | `storefront/modules/catalog/models/ProductOptionValueGet.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1896 | `storefront/modules/catalog/models/ProductsGet.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1897 | `storefront/modules/catalog/models/ProductThumbnail.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1898 | `storefront/modules/catalog/models/ProductVariation.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1899 | `storefront/modules/catalog/models/SimilarProduct.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1900 | `storefront/modules/catalog/services/CategoryService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1901 | `storefront/modules/catalog/services/CategoryService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1902 | `storefront/modules/catalog/services/ProductService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1903 | `storefront/modules/catalog/services/ProductService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1904 | `storefront/modules/catalog/services/ToastService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1905 | `storefront/modules/catalog/services/ToastService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1906 | `storefront/modules/country/models/Country.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1907 | `storefront/modules/country/services/CountryService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1908 | `storefront/modules/country/services/CountryService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1909 | `storefront/modules/customer/models/UserAddressVm.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1910 | `storefront/modules/customer/services/CustomerService.test.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1911 | `storefront/modules/customer/services/CustomerService.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1912 | `storefront/modules/district/models/District.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1913 | `storefront/modules/district/services/DistrictService.test.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1914 | `storefront/modules/district/services/DistrictService.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1915 | `storefront/modules/home/components/Banner.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1916 | `storefront/modules/home/components/Category.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1917 | `storefront/modules/home/components/FeaturedProduct.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1918 | `storefront/modules/home/components/index.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1919 | `storefront/modules/media/models/Media.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1920 | `storefront/modules/media/services/MediaService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1921 | `storefront/modules/media/services/MediaService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1922 | `storefront/modules/order/components/CheckOutAddress.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1923 | `storefront/modules/order/components/CheckOutDetail.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1924 | `storefront/modules/order/components/ModalAddressList.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1925 | `storefront/modules/order/components/OrderCard.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1926 | `storefront/modules/order/components/OrderStatusTab.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1927 | `storefront/modules/order/models/Checkout.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1928 | `storefront/modules/order/models/CheckoutItem.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1929 | `storefront/modules/order/models/EDeliveryMethod.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1930 | `storefront/modules/order/models/EDeliveryStatus.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1931 | `storefront/modules/order/models/EOrderStatus.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1932 | `storefront/modules/order/models/Order.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1933 | `storefront/modules/order/models/OrderGetVm.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1934 | `storefront/modules/order/models/OrderItem.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1935 | `storefront/modules/order/models/OrderItemGetVm.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1936 | `storefront/modules/order/services/OrderService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1937 | `storefront/modules/order/services/OrderService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1938 | `storefront/modules/payment/models/PaymentProvider.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1939 | `storefront/modules/payment/services/PaymentProviderService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1940 | `storefront/modules/payment/services/PaymentProviderService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1941 | `storefront/modules/paymentPaypal/models/CapturePaymentPaypalResponse.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1942 | `storefront/modules/paymentPaypal/models/CapturePaymentRequest.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1943 | `storefront/modules/paymentPaypal/models/InitPaymentPaypalRequest.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1944 | `storefront/modules/paymentPaypal/models/InitPaymentPaypalResponse.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1945 | `storefront/modules/paymentPaypal/models/PaymentPaypalFailureMesasge.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1946 | `storefront/modules/paymentPaypal/services/PaymentPaypalService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1947 | `storefront/modules/paymentPaypal/services/PaymentPaypalService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1948 | `storefront/modules/profile/models/Customer.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1949 | `storefront/modules/profile/models/ProfileRequest.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1950 | `storefront/modules/profile/services/ProfileService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1951 | `storefront/modules/profile/services/ProfileService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1952 | `storefront/modules/promotion/model/Promotion.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1953 | `storefront/modules/promotion/service/PromotionService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1954 | `storefront/modules/promotion/service/PromotionService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1955 | `storefront/modules/rating/components/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1956 | `storefront/modules/rating/components/PostRatingForm.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1957 | `storefront/modules/rating/components/RatingList.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1958 | `storefront/modules/rating/components/Star.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1959 | `storefront/modules/rating/models/Rating.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1960 | `storefront/modules/rating/models/RatingPost.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1961 | `storefront/modules/rating/services/RatingService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1962 | `storefront/modules/rating/services/RatingService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1963 | `storefront/modules/sampledata/models/AddSampleModel.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1964 | `storefront/modules/sampledata/services/SampleDataService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1965 | `storefront/modules/sampledata/services/SampleDataService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1966 | `storefront/modules/search/components/SearchFilter.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1967 | `storefront/modules/search/components/SearchResultLayout.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1968 | `storefront/modules/search/components/SearchSort.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1969 | `storefront/modules/search/models/Aggregations.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1970 | `storefront/modules/search/models/Aggregations.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1971 | `storefront/modules/search/models/ProductSearchResult.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1972 | `storefront/modules/search/models/ProductSearchResult.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1973 | `storefront/modules/search/models/ProductSearchSuggestions.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1974 | `storefront/modules/search/models/ProductSearchSuggestions.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1975 | `storefront/modules/search/models/SearchParams.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1976 | `storefront/modules/search/models/SearchParams.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1977 | `storefront/modules/search/models/SearchProductResponse.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1978 | `storefront/modules/search/models/SearchProductResponse.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1979 | `storefront/modules/search/models/SearchSuggestion.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1980 | `storefront/modules/search/models/SearchSuggestion.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1981 | `storefront/modules/search/models/SortType.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1982 | `storefront/modules/search/models/SortType.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1983 | `storefront/modules/search/services/SearchService.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1984 | `storefront/modules/search/services/SearchService.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1985 | `storefront/modules/stateAndProvince/models/StateOrProvince.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1986 | `storefront/modules/stateAndProvince/services/StatesOrProvicesService.test.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1987 | `storefront/modules/stateAndProvince/services/StatesOrProvicesService.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1988 | `storefront/next.config.js` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1989 | `storefront/next-env.d.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1990 | `storefront/package.json` | NODE-META | Dependency/build/type metadata cho frontend Node.js. |
| 1991 | `storefront/package-lock.json` | NODE-META | Dependency/build/type metadata cho frontend Node.js. |
| 1992 | `storefront/pages/_app.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1993 | `storefront/pages/404.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1994 | `storefront/pages/about/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1995 | `storefront/pages/address/[id]/edit.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1996 | `storefront/pages/address/create.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1997 | `storefront/pages/address/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1998 | `storefront/pages/cart/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 1999 | `storefront/pages/checkout/[id].tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2000 | `storefront/pages/complete-payment/[capture].tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2001 | `storefront/pages/contact/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2002 | `storefront/pages/history/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2003 | `storefront/pages/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2004 | `storefront/pages/my-orders/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2005 | `storefront/pages/products/[slug].tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2006 | `storefront/pages/products/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2007 | `storefront/pages/profile/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2008 | `storefront/pages/redirect/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2009 | `storefront/pages/search/index.tsx` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2010 | `storefront/public/favicon.ico` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 2011 | `storefront/public/static/images/default-fallback-image.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 2012 | `storefront/public/vercel.svg` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 2013 | `storefront/README.md` | DOC | Tài liệu, runbook hoặc giải thích dự án. |
| 2014 | `storefront/sonar-project.properties` | QUALITY-CONFIG | Lint, scan, ignore hoặc repository quality policy. |
| 2015 | `storefront/styles/404.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2016 | `storefront/styles/address.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2017 | `storefront/styles/cart.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2018 | `storefront/styles/checkout.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2019 | `storefront/styles/completePayment.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2020 | `storefront/styles/Footer.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2021 | `storefront/styles/form.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2022 | `storefront/styles/globals.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2023 | `storefront/styles/Header.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2024 | `storefront/styles/HomePage.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2025 | `storefront/styles/modules/search/SearchFilter.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2026 | `storefront/styles/modules/search/SearchPage.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2027 | `storefront/styles/modules/search/SearchSort.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2028 | `storefront/styles/MyOrder.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2029 | `storefront/styles/ProductCard.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2030 | `storefront/styles/productDetail.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2031 | `storefront/styles/productList.module.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2032 | `storefront/styles/spinner.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2033 | `storefront/styles/util.css` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2034 | `storefront/tsconfig.json` | NODE-META | Dependency/build/type metadata cho frontend Node.js. |
| 2035 | `storefront/utils/concatQueryString.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2036 | `storefront/utils/concatQueryString.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2037 | `storefront/utils/formatPrice.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2038 | `storefront/utils/formatPrice.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2039 | `storefront/utils/orderUtil.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2040 | `storefront/utils/orderUtil.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2041 | `storefront/utils/useDebounce.test.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2042 | `storefront/utils/useDebounce.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2043 | `storefront/vitest.config.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2044 | `storefront/vitest.setup.ts` | FRONTEND-SOURCE | Next.js/React UI, route, component hoặc client logic. |
| 2045 | `storefront-bff/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2046 | `storefront-bff/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2047 | `storefront-bff/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2048 | `storefront-bff/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 2049 | `storefront-bff/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2050 | `storefront-bff/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2051 | `storefront-bff/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 2052 | `storefront-bff/src/main/java/com/yas/storefrontbff/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2053 | `storefront-bff/src/main/java/com/yas/storefrontbff/config/ServiceUrlConfig.java` | JAVA-SERVICE | Business logic/orchestration. |
| 2054 | `storefront-bff/src/main/java/com/yas/storefrontbff/controller/AuthenticationController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 2055 | `storefront-bff/src/main/java/com/yas/storefrontbff/StorefrontBffApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 2056 | `storefront-bff/src/main/java/com/yas/storefrontbff/viewmodel/AuthenticatedUserVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2057 | `storefront-bff/src/main/java/com/yas/storefrontbff/viewmodel/AuthenticationInfoVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2058 | `storefront-bff/src/main/java/com/yas/storefrontbff/viewmodel/CartDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2059 | `storefront-bff/src/main/java/com/yas/storefrontbff/viewmodel/CartGetDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2060 | `storefront-bff/src/main/java/com/yas/storefrontbff/viewmodel/CartItemVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2061 | `storefront-bff/src/main/java/com/yas/storefrontbff/viewmodel/GuestUserVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2062 | `storefront-bff/src/main/java/com/yas/storefrontbff/viewmodel/TokenResponseVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2063 | `storefront-bff/src/main/resources/application.yaml` | APP-CONFIG | Runtime application/logging configuration. |
| 2064 | `storefront-bff/src/main/resources/application-dev.yaml` | APP-CONFIG | Runtime application/logging configuration. |
| 2065 | `storefront-bff/src/main/resources/application-prod.yaml` | APP-CONFIG | Runtime application/logging configuration. |
| 2066 | `storefront-bff/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 2067 | `storefront-bff/src/test/java/com/yas/storefrontbff/config/SecurityConfigTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2068 | `storefront-bff/src/test/java/com/yas/storefrontbff/controller/AuthenticationControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2069 | `storefront-bff/src/test/java/com/yas/storefrontbff/viewmodel/ViewModelRecordsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2070 | `storefront-bff/wait-for-it.sh` | SCRIPT | Imperative automation/bootstrap helper. |
| 2071 | `swagger-ui/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 2072 | `tax/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2073 | `tax/.mvn/wrapper/maven-wrapper.jar` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2074 | `tax/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2075 | `tax/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 2076 | `tax/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2077 | `tax/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2078 | `tax/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 2079 | `tax/src/it/java/com/yas/tax/controller/TaxClassControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2080 | `tax/src/it/java/com/yas/tax/controller/TaxRateControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2081 | `tax/src/it/java/com/yas/tax/repository/TaxClassRepositoryIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2082 | `tax/src/it/java/com/yas/tax/repository/TaxRateRepositoryIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2083 | `tax/src/it/java/com/yas/tax/service/LocationServiceIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2084 | `tax/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2085 | `tax/src/it/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2086 | `tax/src/it/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2087 | `tax/src/main/java/com/yas/tax/config/DatabaseAutoConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2088 | `tax/src/main/java/com/yas/tax/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2089 | `tax/src/main/java/com/yas/tax/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2090 | `tax/src/main/java/com/yas/tax/config/ServiceUrlConfig.java` | JAVA-SERVICE | Business logic/orchestration. |
| 2091 | `tax/src/main/java/com/yas/tax/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2092 | `tax/src/main/java/com/yas/tax/constants/ApiConstant.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2093 | `tax/src/main/java/com/yas/tax/constants/MessageCode.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2094 | `tax/src/main/java/com/yas/tax/constants/PageableConstant.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2095 | `tax/src/main/java/com/yas/tax/controller/TaxClassController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 2096 | `tax/src/main/java/com/yas/tax/controller/TaxRateController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 2097 | `tax/src/main/java/com/yas/tax/model/TaxClass.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2098 | `tax/src/main/java/com/yas/tax/model/TaxRate.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2099 | `tax/src/main/java/com/yas/tax/repository/TaxClassRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 2100 | `tax/src/main/java/com/yas/tax/repository/TaxRateRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 2101 | `tax/src/main/java/com/yas/tax/service/AbstractCircuitBreakFallbackHandler.java` | JAVA-SERVICE | Business logic/orchestration. |
| 2102 | `tax/src/main/java/com/yas/tax/service/LocationService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 2103 | `tax/src/main/java/com/yas/tax/service/TaxClassService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 2104 | `tax/src/main/java/com/yas/tax/service/TaxRateService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 2105 | `tax/src/main/java/com/yas/tax/TaxApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 2106 | `tax/src/main/java/com/yas/tax/utils/Constants.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2107 | `tax/src/main/java/com/yas/tax/utils/MessagesUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2108 | `tax/src/main/java/com/yas/tax/viewmodel/error/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2109 | `tax/src/main/java/com/yas/tax/viewmodel/location/StateOrProvinceAndCountryGetNameVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2110 | `tax/src/main/java/com/yas/tax/viewmodel/taxclass/TaxClassListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2111 | `tax/src/main/java/com/yas/tax/viewmodel/taxclass/TaxClassPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2112 | `tax/src/main/java/com/yas/tax/viewmodel/taxclass/TaxClassVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2113 | `tax/src/main/java/com/yas/tax/viewmodel/taxrate/TaxRateGetDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2114 | `tax/src/main/java/com/yas/tax/viewmodel/taxrate/TaxRateListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2115 | `tax/src/main/java/com/yas/tax/viewmodel/taxrate/TaxRatePostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2116 | `tax/src/main/java/com/yas/tax/viewmodel/taxrate/TaxRateVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2117 | `tax/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 2118 | `tax/src/main/resources/db/changelog/data/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 2119 | `tax/src/main/resources/db/changelog/db.changelog-master.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2120 | `tax/src/main/resources/db/changelog/ddl/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 2121 | `tax/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 2122 | `tax/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2123 | `tax/src/test/java/com/yas/tax/controller/TaxClassControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2124 | `tax/src/test/java/com/yas/tax/controller/TaxRateControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2125 | `tax/src/test/java/com/yas/tax/service/LocationServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2126 | `tax/src/test/java/com/yas/tax/service/TaxClassServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2127 | `tax/src/test/java/com/yas/tax/service/TaxRateServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2128 | `tax/src/test/java/com/yas/tax/service/TaxServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2129 | `tax/src/test/java/com/yas/tax/utils/MessagesUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2130 | `tax/src/test/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2131 | `tax/src/test/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2132 | `tax/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2133 | `tempo-data/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2134 | `webhook/.gitignore` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2135 | `webhook/.mvn/wrapper/maven-wrapper.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2136 | `webhook/Dockerfile` | DOCKERFILE | Công thức tạo OCI image cho một service. |
| 2137 | `webhook/mvnw` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2138 | `webhook/mvnw.cmd` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2139 | `webhook/pom.xml` | MAVEN | Maven module, dependency và build lifecycle. |
| 2140 | `webhook/src/it/java/com/yas/webhook/controller/WebhookControllerIT.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2141 | `webhook/src/it/resources/application.properties` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2142 | `webhook/src/it/resources/logback-spring.xml` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2143 | `webhook/src/it/resources/mockito-extensions/org.mockito.plugins.MockMaker` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2144 | `webhook/src/main/java/com/yas/webhook/config/AsyncConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2145 | `webhook/src/main/java/com/yas/webhook/config/constants/ApiConstant.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2146 | `webhook/src/main/java/com/yas/webhook/config/constants/MessageCode.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2147 | `webhook/src/main/java/com/yas/webhook/config/constants/PageableConstant.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2148 | `webhook/src/main/java/com/yas/webhook/config/DatabaseAutoConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2149 | `webhook/src/main/java/com/yas/webhook/config/KafkaConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2150 | `webhook/src/main/java/com/yas/webhook/config/RestClientConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2151 | `webhook/src/main/java/com/yas/webhook/config/SecurityConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2152 | `webhook/src/main/java/com/yas/webhook/config/ServiceUrlConfig.java` | JAVA-SERVICE | Business logic/orchestration. |
| 2153 | `webhook/src/main/java/com/yas/webhook/config/SwaggerConfig.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2154 | `webhook/src/main/java/com/yas/webhook/controller/EventController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 2155 | `webhook/src/main/java/com/yas/webhook/controller/WebhookController.java` | JAVA-CONTROLLER | HTTP boundary của service. |
| 2156 | `webhook/src/main/java/com/yas/webhook/integration/api/WebhookApi.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2157 | `webhook/src/main/java/com/yas/webhook/integration/inbound/OrderEventInbound.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2158 | `webhook/src/main/java/com/yas/webhook/integration/inbound/ProductEventInbound.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2159 | `webhook/src/main/java/com/yas/webhook/model/dto/WebhookEventNotificationDto.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2160 | `webhook/src/main/java/com/yas/webhook/model/enums/EventName.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2161 | `webhook/src/main/java/com/yas/webhook/model/enums/NotificationStatus.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2162 | `webhook/src/main/java/com/yas/webhook/model/enums/Operation.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2163 | `webhook/src/main/java/com/yas/webhook/model/Event.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2164 | `webhook/src/main/java/com/yas/webhook/model/mapper/EventMapper.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2165 | `webhook/src/main/java/com/yas/webhook/model/mapper/WebhookMapper.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2166 | `webhook/src/main/java/com/yas/webhook/model/viewmodel/error/ErrorVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2167 | `webhook/src/main/java/com/yas/webhook/model/viewmodel/webhook/EventVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2168 | `webhook/src/main/java/com/yas/webhook/model/viewmodel/webhook/WebhookDetailVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2169 | `webhook/src/main/java/com/yas/webhook/model/viewmodel/webhook/WebhookListGetVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2170 | `webhook/src/main/java/com/yas/webhook/model/viewmodel/webhook/WebhookPostVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2171 | `webhook/src/main/java/com/yas/webhook/model/viewmodel/webhook/WebhookVm.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2172 | `webhook/src/main/java/com/yas/webhook/model/Webhook.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2173 | `webhook/src/main/java/com/yas/webhook/model/WebhookEvent.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2174 | `webhook/src/main/java/com/yas/webhook/model/WebhookEventNotification.java` | JAVA-MODEL | Domain/entity/DTO/view model. |
| 2175 | `webhook/src/main/java/com/yas/webhook/repository/EventRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 2176 | `webhook/src/main/java/com/yas/webhook/repository/WebhookEventNotificationRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 2177 | `webhook/src/main/java/com/yas/webhook/repository/WebhookEventRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 2178 | `webhook/src/main/java/com/yas/webhook/repository/WebhookRepository.java` | JAVA-REPOSITORY | Persistence access boundary. |
| 2179 | `webhook/src/main/java/com/yas/webhook/service/AbstractWebhookEventNotificationService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 2180 | `webhook/src/main/java/com/yas/webhook/service/EventService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 2181 | `webhook/src/main/java/com/yas/webhook/service/OrderEventService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 2182 | `webhook/src/main/java/com/yas/webhook/service/ProductEventService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 2183 | `webhook/src/main/java/com/yas/webhook/service/WebhookService.java` | JAVA-SERVICE | Business logic/orchestration. |
| 2184 | `webhook/src/main/java/com/yas/webhook/utils/HmacUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2185 | `webhook/src/main/java/com/yas/webhook/utils/MessagesUtils.java` | JAVA-SOURCE | Java implementation hỗ trợ domain/config/client/security. |
| 2186 | `webhook/src/main/java/com/yas/webhook/WebhookApplication.java` | JAVA-BOOT | Spring Boot entrypoint. |
| 2187 | `webhook/src/main/resources/application.properties` | APP-CONFIG | Runtime application/logging configuration. |
| 2188 | `webhook/src/main/resources/db/changelog/data/changelog-0001-event.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 2189 | `webhook/src/main/resources/db/changelog/db.changelog-master.yaml` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2190 | `webhook/src/main/resources/db/changelog/ddl/changelog-0001.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 2191 | `webhook/src/main/resources/db/changelog/ddl/changelog-0002.sql` | DB-MIGRATION | Schema/data migration hoặc database bootstrap. |
| 2192 | `webhook/src/main/resources/logback-spring.xml` | APP-CONFIG | Runtime application/logging configuration. |
| 2193 | `webhook/src/main/resources/messages/messages.properties` | OTHER | File hỗ trợ, wrapper, license hoặc metadata đặc thù. |
| 2194 | `webhook/src/test/java/com/yas/webhook/controller/EventControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2195 | `webhook/src/test/java/com/yas/webhook/controller/WebhookControllerTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2196 | `webhook/src/test/java/com/yas/webhook/service/EventServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2197 | `webhook/src/test/java/com/yas/webhook/service/OrderEventServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2198 | `webhook/src/test/java/com/yas/webhook/service/ProductEventServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2199 | `webhook/src/test/java/com/yas/webhook/service/WebhookServiceTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2200 | `webhook/src/test/java/com/yas/webhook/utils/HmacUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2201 | `webhook/src/test/java/com/yas/webhook/utils/MessagesUtilsTest.java` | TEST | Unit, integration, fixture hoặc test configuration. |
| 2202 | `workflows.sh` | SCRIPT | Imperative automation/bootstrap helper. |
| 2203 | `yas-architecture-local.png` | ASSET | Ảnh, font hoặc static asset được build/serve/seed. |
| 2204 | `yaslocal.yaml` | COMPOSE | Mô tả stack nhiều container cho local/integration. |
