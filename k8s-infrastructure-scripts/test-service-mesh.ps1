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

# =====================================================================
# KIEM TRA TRANG THAI
# =====================================================================
Write-Step "KIEM TRA TRANG THAI: Pods 2/2 (co Envoy sidecar)"
Write-Host "  Pods trong namespace $NS :" -ForegroundColor Yellow
kubectl get pods -n $NS -o wide
Write-Host ""
Write-Host "  Neu pod nao khong phai 2/2 thi Istio injection chua hoat dong" -ForegroundColor Yellow
Write-Host "  Fix: kubectl rollout restart deployment/<service> -n $NS" -ForegroundColor DarkGray

Start-Sleep -Seconds 3

# =====================================================================
# TEST 1: mTLS STRICT - Pod ngoai mesh bi chan
# - Tao curl pod trong namespace 'default' (khong co Envoy sidecar)
# - Goi product service -> phai bi connection reset do mTLS STRICT
# =====================================================================
Write-Step "TEST 1: mTLS STRICT - Pod ngoai mesh bi chan"
Write-Host "  Tao pod o namespace default (NGOAI mesh, khong co Envoy)..." -ForegroundColor Yellow
Write-Host "  Goi product service, phai bi Connection Reset..." -ForegroundColor Yellow
Write-Host ""

# Xoa pod cu neu con ton tai tu lan chay truoc
kubectl delete pod mtls-test --namespace=default --ignore-not-found=true 2>$null | Out-Null
Start-Sleep -Seconds 2

# Chay pod khong co -it (tranh loi TTY trong PowerShell), doi pod hoan thanh roi lay log
kubectl run mtls-test --image=curlimages/curl --namespace=default `
    --restart=Never -- `
    curl -sv --max-time 5 "http://product.$NS.svc.cluster.local/product/storefront/products/featured" 2>&1 | Out-Null

# Doi pod chay xong (toi da 15 giay)
$waited = 0
do {
    Start-Sleep -Seconds 2
    $waited += 2
    $podPhase = kubectl get pod mtls-test -n default -o jsonpath='{.status.phase}' 2>$null
} while ($podPhase -notin @('Succeeded','Failed') -and $waited -lt 15)

# Lay log cua pod
$mtlsResult = kubectl logs mtls-test --namespace=default 2>&1

# Xoa pod sau khi lay log
kubectl delete pod mtls-test --namespace=default --ignore-not-found=true 2>$null | Out-Null

if ($mtlsResult -match "reset|refused|Connection reset|timed out|timeout|connection reset by peer") {
    Write-Result "mTLS chan traffic tu ngoai mesh" $true "Connection bi reset/refused (mTLS STRICT dang hoat dong)"
} else {
    Write-Result "mTLS chan traffic tu ngoai mesh" $false "Traffic KHONG bi chan: $mtlsResult"
}

Start-Sleep -Seconds 3

# =====================================================================
# TEST 2: AuthorizationPolicy - Service khong duoc phep bi 403
# - Dung pod 'cart' (dang chay tot) goi sang 'customer'
# - 'cart' KHONG co trong allowedCallers cua 'customer'
# - => Phai bi RBAC deny (HTTP 403)
# FIX: Doi tu deployment/search (dang crash) sang deployment/cart
#      Va dung wget --server-response de bat HTTP status code
# =====================================================================
Write-Step "TEST 2: AuthorizationPolicy - Service khong duoc phep bi 403"
Write-Host "  cart goi customer, phai bi 403 (cart khong nam trong allowedCallers cua customer)" -ForegroundColor Yellow
Write-Host ""

$denyResult = kubectl exec -n $NS deployment/cart -c cart -- `
    wget -S -O /dev/null --timeout=5 "http://customer.$NS.svc.cluster.local/customer/storefront/customers/profile" 2>&1

if ($denyResult -match "403|RBAC|denied|forbidden") {
    Write-Result "AuthzPolicy chan service khong duoc phep" $true "HTTP 403 - RBAC: access denied"
} else {
    Write-Result "AuthzPolicy chan service khong duoc phep" $false "Response: $denyResult"
}

Start-Sleep -Seconds 3

# =====================================================================
# TEST 3: AuthorizationPolicy - Service DUOC phep tra ve 200
# - storefront-bff goi product -> co trong allowedCallers => phai duoc phep
# FIX: Chinh regex match sang 'productList|totalPage' vi Product API
#      tra ve JSON dang {"productList":[],"totalPage":0} khi khong co data.
#      Day van la response 200 hop le, khong phai loi.
# =====================================================================
Write-Step "TEST 3: AuthorizationPolicy - Service DUOC phep tra ve 200"
Write-Host "  storefront-bff goi product, phai duoc 200 (co trong allowedCallers)" -ForegroundColor Yellow
Write-Host ""

$allowResult = kubectl exec -n $NS deployment/storefront-bff -c storefront-bff -- `
    wget -S -O - --timeout=15 "http://product.$NS.svc.cluster.local/product/storefront/products/featured" 2>&1

if ($allowResult -match "200 OK|productList|totalPage|pageNumber") {
    Write-Result "AuthzPolicy cho phep service hop le" $true "HTTP 200 - Du lieu JSON tra ve thanh cong"
} else {
    Write-Result "AuthzPolicy cho phep service hop le" $false "Response: $allowResult"
}

Start-Sleep -Seconds 3

# =====================================================================
# TEST 4: Retry Policy - Inject loi 503 va kiem tra retry
# - Apply VirtualService inject 30% loi 503 vao product
# - storefront-bff gui 10 requests -> Envoy retry nen phai >= 8/10 thanh cong
# FIX: Chinh regex match sang 'productList|totalPage' (giong TEST 3)
#      Va tang timeout len 25s de cho Envoy co du thoi gian retry (3 lan x 5s)
# =====================================================================
Write-Step "TEST 4: Retry Policy - Inject loi 503 va kiem tra retry"
Write-Host "  Buoc 4a: Apply Fault Injection (30% loi 503 vao product)..." -ForegroundColor Yellow

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
    $retryResult = kubectl exec -n $NS deployment/storefront-bff -c storefront-bff -- `
        wget -S -O - --timeout=25 "http://product.$NS.svc.cluster.local/product/storefront/products/featured" 2>&1
    if ($retryResult -match "200 OK|productList|totalPage|pageNumber") {
        $success++
        Write-Host "    Request $i -> 200 OK (retry thanh cong)" -ForegroundColor Green
    } else {
        $fail++
        Write-Host "    Request $i -> Failed (503 khong duoc retry)" -ForegroundColor Red
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

# =====================================================================
# TOM TAT KET QUA
# =====================================================================
Write-Step "TOM TAT KET QUA"
Write-Host ""
Write-Host "  TEST 1: mTLS          -> Pod ngoai bi chan" -ForegroundColor Cyan
Write-Host "  TEST 2: AuthzPolicy   -> Service sai bi 403" -ForegroundColor Cyan
Write-Host "  TEST 3: AuthzPolicy   -> Service dung duoc 200" -ForegroundColor Cyan
Write-Host "  TEST 4: Retry Policy  -> Retry hap thu loi" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Mo Kiali de chup screenshot topology:" -ForegroundColor Cyan
Write-Host "    kubectl port-forward svc/kiali 20001:20001 -n istio-system" -ForegroundColor White
Write-Host "    http://localhost:20001 -> Graph -> Namespace: dev" -ForegroundColor White
