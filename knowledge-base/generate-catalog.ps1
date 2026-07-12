param(
    [string]$RepositoryRoot = (Split-Path -Parent $PSScriptRoot)
)

$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $RepositoryRoot

function Get-Archetype([string]$Path) {
    switch -Regex ($Path) {
        '^docs/.*\.pdf$' { return 'PDF' }
        '^\.github/workflows/.*\.ya?ml$' { return 'GHA' }
        '(^|/)Dockerfile' { return 'DOCKERFILE' }
        '^docker-compose.*\.ya?ml$|^yaslocal\.yaml$' { return 'COMPOSE' }
        '^k8s/argocd/' { return 'ARGO' }
        '^k8s/istio/' { return 'ISTIO' }
        '^k8s/.*/templates/.*\.ya?ml$' { return 'HELM-TEMPLATE' }
        '^k8s/.*/Chart\.ya?ml$' { return 'HELM-CHART' }
        '^k8s/.*values.*\.ya?ml$' { return 'HELM-VALUES' }
        '^k8s/.*\.ya?ml$' { return 'K8S' }
        '^docker/(grafana|loki|prometheus|tempo|otel-collector)/' { return 'OBS' }
        '(^|/)pom\.xml$' { return 'MAVEN' }
        '(^|/)package(-lock)?\.json$|(^|/)tsconfig\.json$' { return 'NODE-META' }
        '/src/main/java/.*Application\.java$' { return 'JAVA-BOOT' }
        '/src/main/java/.*Controller\.java$' { return 'JAVA-CONTROLLER' }
        '/src/main/java/.*Service.*\.java$' { return 'JAVA-SERVICE' }
        '/src/main/java/.*Repository\.java$' { return 'JAVA-REPOSITORY' }
        '/src/main/java/.*(Entity|model)/.*\.java$' { return 'JAVA-MODEL' }
        '/src/test/|/src/it/' { return 'TEST' }
        '/src/main/java/.*\.java$' { return 'JAVA-SOURCE' }
        '\.(tsx?|jsx?)$' { return 'FRONTEND-SOURCE' }
        'application.*\.ya?ml$|application.*\.properties$|logback.*\.xml$' { return 'APP-CONFIG' }
        '/db/changelog/.*\.sql$|postgres_init\.sql$' { return 'DB-MIGRATION' }
        '\.(png|jpg|jpeg|gif|svg|ico|woff2?|ttf)$' { return 'ASSET' }
        '\.(md|txt)$' { return 'DOC' }
        '\.(sh|ps1)$' { return 'SCRIPT' }
        '^\.gitignore$|^\.gitattributes$|gitleaks|sonar|checkstyle|\.eslintrc' { return 'QUALITY-CONFIG' }
        default { return 'OTHER' }
    }
}

$descriptions = @{
    'PDF'='Đề bài hoặc báo cáo làm căn cứ yêu cầu và bằng chứng.'
    'GHA'='Workflow/composite action tự động hóa CI, security hoặc CD.'
    'DOCKERFILE'='Công thức tạo OCI image cho một service.'
    'COMPOSE'='Mô tả stack nhiều container cho local/integration.'
    'ARGO'='Desired state của Argo CD Application.'
    'ISTIO'='Policy/routing/demo/service-mesh manifest hoặc hướng dẫn.'
    'HELM-TEMPLATE'='Go template render Kubernetes resource.'
    'HELM-CHART'='Metadata/dependency của Helm chart.'
    'HELM-VALUES'='Public configuration input/override của chart.'
    'K8S'='Kubernetes manifest hoặc cluster deployment configuration.'
    'OBS'='Cấu hình metrics, logs, traces hoặc dashboard.'
    'MAVEN'='Maven module, dependency và build lifecycle.'
    'NODE-META'='Dependency/build/type metadata cho frontend Node.js.'
    'JAVA-BOOT'='Spring Boot entrypoint.'
    'JAVA-CONTROLLER'='HTTP boundary của service.'
    'JAVA-SERVICE'='Business logic/orchestration.'
    'JAVA-REPOSITORY'='Persistence access boundary.'
    'JAVA-MODEL'='Domain/entity/DTO/view model.'
    'TEST'='Unit, integration, fixture hoặc test configuration.'
    'JAVA-SOURCE'='Java implementation hỗ trợ domain/config/client/security.'
    'FRONTEND-SOURCE'='Next.js/React UI, route, component hoặc client logic.'
    'APP-CONFIG'='Runtime application/logging configuration.'
    'DB-MIGRATION'='Schema/data migration hoặc database bootstrap.'
    'ASSET'='Ảnh, font hoặc static asset được build/serve/seed.'
    'DOC'='Tài liệu, runbook hoặc giải thích dự án.'
    'SCRIPT'='Imperative automation/bootstrap helper.'
    'QUALITY-CONFIG'='Lint, scan, ignore hoặc repository quality policy.'
    'OTHER'='File hỗ trợ, wrapper, license hoặc metadata đặc thù.'
}

$tracked = @(git ls-files)
$extra = @('docs/Project 01_HKII_25_26.pdf','docs/Project02_HKII_25_26.pdf','docs/Report Project 1.pdf','docs/Report Project 2.pdf') |
    Where-Object { $tracked -notcontains $_ -and (Test-Path -LiteralPath (Join-Path $RepositoryRoot $_)) }
$all = @($tracked + $extra | Sort-Object -Unique)

$sb = [System.Text.StringBuilder]::new()
[void]$sb.AppendLine('# 5. File catalog đầy đủ')
[void]$sb.AppendLine()
[void]$sb.AppendLine("Catalog được tạo từ ``git ls-files`` cộng các PDF input chưa được track. Tổng số: **$($all.Count) file**. Không liệt kê nội dung ``.git`` vì đó là database nội bộ của Git, không phải source project. Chạy lại ``./knowledge-base/generate-catalog.ps1`` sau khi repository thay đổi.")
[void]$sb.AppendLine()
[void]$sb.AppendLine('## Cách áp dụng 10 tiêu chí cho mỗi dòng')
[void]$sb.AppendLine()
[void]$sb.AppendLine('Mỗi dòng có archetype và vai trò riêng. Với mọi archetype: (1) vai trò là cột Vai trò; (2) lý do cần là cung cấp artifact cho consumer; (3) workflow là producer tạo/maintain rồi build/controller/runtime đọc; (4) quan hệ là đường dẫn và module chứa file; (5) logic nằm trong code/config của file; (6) field quan trọng được giải thích trong chương tương ứng; (7) bỏ file làm consumer build/runtime mất input hoặc mất bằng chứng; (8) sửa file thay behavior của consumer và phải chạy test phù hợp; (9) production yêu cầu versioning, validation, least privilege, reproducibility và monitoring; (10) lỗi thường gặp là sai path/type/name/version, drift giữa environment, secret lộ hoặc thiếu test. Bảng archetype dưới đây cụ thể hóa hậu quả.')
[void]$sb.AppendLine()
[void]$sb.AppendLine('| Archetype | Workflow và quan hệ | Nếu bỏ/sửa | Production và lỗi thường gặp |')
[void]$sb.AppendLine('|---|---|---|---|')
$archDetails = @{
 'PDF'='Người chấm và nhóm dùng để đối chiếu yêu cầu/bằng chứng|Mất source of truth hoặc làm chú thích lệch bằng chứng|Gắn revision, run URL, timestamp; tránh screenshot thiếu ngữ cảnh'
 'GHA'='GitHub event -> runner -> test/build/push/update Git|Mất automation; sửa trigger/quyền có thể skip gate hoặc cấp quyền quá rộng|Pin action SHA, OIDC, concurrency, reusable workflow; lỗi paths/secret/skip test'
 'DOCKERFILE'='CI + build context -> image -> registry -> Pod|Không build image; sửa base/COPY/entrypoint có thể fail hoặc tăng CVE|Pin digest, non-root, multi-stage, SBOM/sign; lỗi copy secret/chạy root/mutable tag'
 'COMPOSE'='Compose resolve env -> network/volume -> start services|Local stack thiếu dependency; sửa port/volume làm conflict hoặc mất data|Chỉ dùng local/test; lỗi depends_on bị hiểu là readiness'
 'ARGO'='Argo controller đọc Git/Helm -> diff -> sync cluster|Mất reconcile; sửa repo/path/destination có thể deploy nhầm/xóa resource|AppProject, RBAC, signed Git, digest; lỗi Synced=Healthy hoặc prune quá rộng'
 'ISTIO'='Istiod đọc policy -> xDS -> Envoy enforce/observe|Mất mTLS/authz/retry/demo; sửa selector/host có thể không áp hoặc chặn traffic|Default deny, policy tests, retry idempotent; lỗi curl từ proxy, wildcard principal'
 'HELM-TEMPLATE'='Helm merge values -> render manifest -> API server|Resource không được render; sửa indentation/name/selector làm install hoặc runtime fail|Schema, lint, template, policy test; lỗi toYaml/nindent/type'
 'HELM-CHART'='Helm load metadata/dependencies -> package/render release|Chart không hợp lệ; sửa version/dependency đổi graph build|SemVer, lock dependency; lỗi nhầm appVersion với chart version'
 'HELM-VALUES'='Environment/workflow cung cấp override -> template consume|Rơi về default hoặc render lỗi; sửa nesting/type có thể bị bỏ qua im lặng|values.schema.json, secret ngoài Git; lỗi --set coercion'
 'K8S'='kubectl/Helm/controller apply -> reconciliation|Mất workload/infra; sửa namespace/selector/name có thể recreate hoặc mất route|Dry-run/schema/policy, GitOps; lỗi label/port/probe/resource'
 'OBS'='Agent/collector/backend/dashboard nhận và truy vấn telemetry|Mất signal hoặc dashboard; sửa endpoint/label gây data loss/cardinality|TLS/auth/HA/object storage/retention; lỗi local config dùng production'
 'MAVEN'='Maven resolve reactor/dependency/plugin -> compile/test/package|Module không build; sửa version/plugin ảnh hưởng dependency graph|Wrapper, lock/update policy, reproducible build; lỗi skip test/version drift'
 'NODE-META'='npm resolve lock/scripts/types -> build frontend|Không cài/build đúng; sửa lock và package lệch gây nondeterminism|npm ci, audit, pin engine; lỗi sửa package không cập nhật lock'
 'JAVA-BOOT'='JVM main -> Spring context -> web server|Jar không boot; sửa package làm component scan mất bean|External config, actuator, graceful shutdown; lỗi hard-code profile'
 'JAVA-CONTROLLER'='HTTP route -> validation -> service|Endpoint mất/đổi contract; sửa path/status làm client/test hỏng|Authz, validation, idempotency; lỗi business logic trong controller'
 'JAVA-SERVICE'='Controller/event -> business transaction -> repository/client|Mất use case; sửa transaction/retry có thể partial update hoặc duplicate|Timeout/outbox/idempotency; lỗi retry nhiều tầng'
 'JAVA-REPOSITORY'='Service -> ORM/query -> database|Persistence operation mất; sửa query có thể sai data hoặc chậm|Index/query plan/integration test; lỗi N+1/cascade'
 'JAVA-MODEL'='Request/domain/ORM map dữ liệu giữa layer|Serialization/schema/compile fail; sửa field phá compatibility|Version contract, validation, PII control; lỗi expose entity trực tiếp'
 'TEST'='Test runner -> fixture -> assertion -> report/coverage|Mất regression evidence; sửa assertion sai làm false green|Hermetic, deterministic, artifact; lỗi shared state/flaky/external dependency'
 'JAVA-SOURCE'='Spring/module gọi class theo dependency graph|Compile/runtime/use case hỏng; sửa API lan sang caller|SOLID vừa đủ, observability, test; lỗi hidden coupling'
 'FRONTEND-SOURCE'='Next build/router/browser -> BFF/API|UI/route/component mất; sửa env/SSR contract gây runtime mismatch|CSP, accessibility, runtime config, bundle budget; lỗi bake sai public env'
 'APP-CONFIG'='Spring/Logback load profile/env -> configure runtime|App dùng default hoặc fail boot; sửa key/port/URL làm kết nối sai|Typed config, secret external, profile test; lỗi commit credential'
 'DB-MIGRATION'='Liquibase/bootstrap apply ordered change -> schema/data|Fresh DB thiếu schema/data; sửa changeset đã chạy gây checksum/drift|Append-only, expand-contract, backup/test; lỗi destructive migration'
 'ASSET'='Build/static server/seed data đọc binary theo path|UI/sample data thiếu; đổi tên/path gây 404|Optimize, license, hash/CDN; lỗi binary lớn/secret metadata'
 'DOC'='Người vận hành/học viên đọc runbook/design|Mất tri thức; sửa không đồng bộ code tạo hướng dẫn sai|Docs as code, command tested, owner/date; lỗi copy output cũ'
 'SCRIPT'='Operator/CI chạy lệnh tuần tự lên local/cluster|Mất bootstrap; sửa thiếu guard có thể tác động sai cluster|set strict, idempotent, context check; lỗi hard-code/không quote'
 'QUALITY-CONFIG'='Lint/scanner/tool đọc policy/ignore|Gate mất hoặc dùng default; sửa allowlist có thể che lỗi|Review exception, expiry, pin rule; lỗi broad ignore'
 'OTHER'='Build/tool/runtime đọc theo loại file cụ thể|Tác động phụ thuộc consumer; phải kiểm tra reference trước khi bỏ/sửa|Giữ provenance/license/wrapper; lỗi coi file lạ là không cần'
}
foreach ($key in ($archDetails.Keys | Sort-Object)) {
    $parts = $archDetails[$key] -split '\|', 3
    [void]$sb.AppendLine("| $key | $($parts[0]) | $($parts[1]) | $($parts[2]) |")
}
[void]$sb.AppendLine()
[void]$sb.AppendLine('## Inventory')
[void]$sb.AppendLine()
[void]$sb.AppendLine('| # | File | Archetype | Vai trò |')
[void]$sb.AppendLine('|---:|---|---|---|')
$i = 0
foreach ($file in $all) {
    $i++
    $arch = Get-Archetype $file
    $escaped = $file.Replace('|','\|')
    [void]$sb.AppendLine("| $i | ``$escaped`` | $arch | $($descriptions[$arch]) |")
}

$output = Join-Path $PSScriptRoot '05-file-catalog.md'
[System.IO.File]::WriteAllText($output, $sb.ToString(), [System.Text.UTF8Encoding]::new($false))
Write-Output "Generated $output with $($all.Count) files."
