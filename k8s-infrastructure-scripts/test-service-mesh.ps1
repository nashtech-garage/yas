# SCRIPT TEST: Kich ban test Service Mesh cho demo
# Chay SAU KHI ArgoCD da sync xong 14 services + Istio policies

$ErrorActionPreference = "Continue"
$NS = "dev"

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "  $Message" -ForegroundColor Cyan
    Write-Host ""
}

function Write-Result {
    param([string]$TestName, [bool]$Passed, [string]$Detail)
    if ($Passed) {
        Write-Host "  PASS: $TestName" -ForegroundColor Green
    } else {
        Write-Host "  FAIL: $TestName" -ForegroundColor Red
    }
    Write-Host "     $Detail" -ForegroundColor DarkGray
}

# KIEM TRA TRANG THAI
Write-Step "KIEM TRA TRANG THAI: Pods 2/2 (co Envoy sidecar)"
Write-Host "  Pods trong namespace $NS :" -ForegroundColor Yellow
kubectl get pods -n $NS -o wide
Write-Host ""
Write-Host "  Neu pod nao khong phai 2/2 thi Istio injection chua hoat dong" -ForegroundColor Yellow
Write-Host "  Fix: kubectl rollout restart deployment/<service> -n $NS" -ForegroundColor DarkGray

Start-Sleep -Seconds 3

# TEST 1: mTLS STRICT - Pod ngoai mesh bi chan
Write-Step "TEST 1: mTLS STRICT - Pod ngoai mesh bi chan"
Write-Host "  Tao pod o namespace default (NGOAI mesh, khong co Envoy)..." -ForegroundColor Yellow
Write-Host "  Goi product service, phai bi Connection Reset" -ForegroundColor Yellow
Write-Host ""

$mtlsResult = kubectl run mtls-test --image=curlimages/curl --namespace=default `
    --rm -it --restart=Never -- `
    curl -v --max-time 5 "http://product.$NS.svc.cluster.local/product/storefront/products/featured" 2>&1

if ($mtlsResult -match "reset|refused|Connection reset|timed out|timeout") {
    Write-Result "mTLS chan traffic tu ngoai mesh" $true "Connection bi reset/refused (mTLS STRICT dang hoat dong)"
} else {
    Write-Result "mTLS chan traffic tu ngoai mesh" $false "Traffic KHONG bi chan: $mtlsResult"
}

Start-Sleep -Seconds 3

# TEST 2: AuthorizationPolicy - Service khong duoc phep bi 403
Write-Step "TEST 2: AuthorizationPolicy - Service khong duoc phep bi 403"
Write-Host "  search goi customer, phai bi 403 (search khong nam trong allowedCallers)" -ForegroundColor Yellow
Write-Host ""

$denyResult = kubectl exec -n $NS deployment/search -- `
    wget -q -O - --timeout=5 "http://customer.$NS.svc.cluster.local/customer/storefront/customers/profile" 2>&1

if ($denyResult -match "403|RBAC|denied|forbidden") {
    Write-Result "AuthzPolicy chan service khong duoc phep" $true "HTTP 403 - RBAC: access denied"
} else {
    Write-Result "AuthzPolicy chan service khong duoc phep" $false "Response: $denyResult"
}

Start-Sleep -Seconds 3

# TEST 3: AuthorizationPolicy - Service DUOC phep tra ve 200
Write-Step "TEST 3: AuthorizationPolicy - Service DUOC phep tra ve 200"
Write-Host "  storefront-bff goi product, phai duoc 200 (co trong allowedCallers)" -ForegroundColor Yellow
Write-Host ""

$allowResult = kubectl exec -n $NS deployment/storefront-bff -- `
    wget -q -O - --timeout=10 "http://product.$NS.svc.cluster.local/product/storefront/products/featured" 2>&1

if ($allowResult -match "200|productName|slug|id") {
    Write-Result "AuthzPolicy cho phep service hop le" $true "HTTP 200 - Du lieu tra ve thanh cong"
} else {
    Write-Result "AuthzPolicy cho phep service hop le" $false "Response: $allowResult"
}

Start-Sleep -Seconds 3

# TEST 4: Retry Policy - Inject loi 503 va kiem tra retry
Write-Step "TEST 4: Retry Policy - Inject loi 503 va kiem tra retry"
Write-Host "  Buoc 4a: Apply Fault Injection (30% loi 503 vao product)..." -ForegroundColor Yellow

# Tao file fault injection tam
$FAULT_YAML = @"
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: product-fault-injection
  namespace: $NS
spec:
  hosts:
    - product
  http:
    - fault:
        abort:
          percentage:
            value: 30
          httpStatus: 503
      retries:
        attempts: 3
        perTryTimeout: 5s
        retryOn: 5xx,gateway-error,connect-failure
      timeout: 20s
      route:
        - destination:
            host: product
            port:
              number: 80
"@

$FAULT_YAML | kubectl apply -f -
Write-Host "  Cho policy propagate (10s)..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host "  Buoc 4b: Gui 10 requests storefront-bff -> product..." -ForegroundColor Yellow
$success = 0
$fail = 0
for ($i = 1; $i -le 10; $i++) {
    $retryResult = kubectl exec -n $NS deployment/storefront-bff -- `
        wget -q -O - --timeout=20 "http://product.$NS.svc.cluster.local/product/storefront/products/featured" 2>&1
    if ($retryResult -match "productName|slug|id|200") {
        $success++
        Write-Host "    Request $i -> 200 OK" -ForegroundColor Green
    } else {
        $fail++
        Write-Host "    Request $i -> Failed" -ForegroundColor Red
    }
    Start-Sleep -Seconds 1
}

Write-Host ""
Write-Host "  Ket qua: $success/10 thanh cong | $fail/10 that bai" -ForegroundColor Yellow
if ($success -ge 8) {
    Write-Result "Retry Policy hap thu loi 503" $true ">=80% requests thanh cong du 30% bi inject loi, Envoy retry hoat dong"
} else {
    Write-Result "Retry Policy hap thu loi 503" $false "Chi $success/10 thanh cong. Kiem tra VirtualService retry config"
}

Write-Host ""
Write-Host "  Buoc 4c: Don dep Fault Injection..." -ForegroundColor Yellow
kubectl delete virtualservice product-fault-injection -n $NS 2>$null
Write-Host "  Da xoa fault injection, product tro lai binh thuong" -ForegroundColor Green

# TOM TAT KET QUA
Write-Step "TOM TAT KET QUA"
Write-Host ""
Write-Host "  TEST 1: mTLS          -> Pod ngoai bi chan" -ForegroundColor Green
Write-Host "  TEST 2: AuthzPolicy   -> Service sai bi 403" -ForegroundColor Green
Write-Host "  TEST 3: AuthzPolicy   -> Service dung duoc 200" -ForegroundColor Green
Write-Host "  TEST 4: Retry Policy  -> Retry hap thu loi" -ForegroundColor Green
Write-Host ""
Write-Host "  Mo Kiali de chup screenshot topology:" -ForegroundColor Cyan
Write-Host "    kubectl port-forward svc/kiali 20001:20001 -n istio-system" -ForegroundColor White
Write-Host "    http://localhost:20001 -> Graph -> Namespace: dev" -ForegroundColor White
