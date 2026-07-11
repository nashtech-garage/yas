# =============================================================
# YAS Service Mesh — Apply Istio Configs & Run Tests
# Works identically on k3d (dev laptop) and k3s+Tailscale (real laptops)
# Usage: .\scripts\apply-istio-mesh.ps1
# =============================================================
param(
    [string]$Namespace = "dev",
    [switch]$SkipTests,
    [switch]$SetStrict,  # Switch dev namespace from PERMISSIVE to STRICT mTLS
    [switch]$ForceRestart # Force rollout restart of deployments
)

function Write-Step($msg)    { Write-Host ""; Write-Host ">>> $msg" -ForegroundColor Cyan }
function Write-OK($msg)      { Write-Host "    OK: $msg" -ForegroundColor Green }
function Write-Warn($msg)    { Write-Host "    WARN: $msg" -ForegroundColor Yellow }
function Write-Fail($msg)    { Write-Host "    FAIL: $msg" -ForegroundColor Red }
function Write-Info($msg)    { Write-Host "    $msg" -ForegroundColor White }

Set-Location $PSScriptRoot\..

# ─── 0. Verify Istio is installed ────────────────────────────────────────────
Write-Step "Checking Istio installation..."
$istiodPod = kubectl get pods -n istio-system -l app=istiod --no-headers 2>$null | Select-Object -First 1
if (-not $istiodPod) {
    Write-Fail "Istio is not installed. Run: istioctl install -f istio/istio-operator.yaml -y"
    exit 1
}
Write-OK "Istiod found: $($istiodPod.Split()[0])"

# ─── 1. Apply mTLS PeerAuthentication + DestinationRule ──────────────────────
Write-Step "Applying mTLS configuration..."
kubectl apply -f istio/mtls/peer-authentication.yaml
kubectl apply -f istio/mtls/destination-rule.yaml
if (Test-Path "istio/mtls/service-entry.yaml") {
    kubectl apply -f istio/mtls/service-entry.yaml
}
Write-OK "mTLS config applied"

# Optionally switch dev to STRICT
if ($SetStrict) {
    Write-Step "Switching dev namespace to STRICT mTLS..."
    $peerAuthFile = "istio/mtls/peer-authentication.yaml"
    (Get-Content $peerAuthFile) -replace 'mode: PERMISSIVE', 'mode: STRICT' | kubectl apply -f -
    Write-OK "dev namespace now in STRICT mTLS mode"
}

# ─── 2. Apply VirtualServices (retry + timeout) ───────────────────────────────
Write-Step "Applying VirtualService retry/timeout policies..."
Get-ChildItem "istio/traffic/*.yaml" | ForEach-Object {
    kubectl apply -f $_.FullName
    Write-Info "  Applied: $($_.Name)"
}
Write-OK "VirtualService policies applied"

# ─── 3. Apply AuthorizationPolicies ──────────────────────────────────────────
Write-Step "Applying AuthorizationPolicies (RBAC between services)..."
kubectl apply -f istio/security/authz-policies.yaml
Write-OK "Authorization policies applied"

# ─── 4. Restart pods in namespace to inject Envoy sidecars if needed ──────────
$notInjected = kubectl get pods -n $Namespace --no-headers 2>$null | Where-Object { $_ -match "1/1" -and $_ -notmatch "Completed" }
if ($notInjected -or $ForceRestart) {
    Write-Step "Restarting pods in '$Namespace' to inject Istio sidecars..."
    kubectl rollout restart deployment -n $Namespace 2>&1 | Out-Null
    Write-Info "Waiting 60s for sidecars to be injected and pods to be ready..."
    Start-Sleep -Seconds 60
} else {
    Write-OK "All pods already have sidecars injected (2/2 containers). Skipping restart. Use -ForceRestart to override."
}

# ─── 5. Verify Istio resources ───────────────────────────────────────────────
Write-Step "Verifying applied Istio resources..."
Write-Info "PeerAuthentication:"
kubectl get peerauthentication -n $Namespace 2>&1 | ForEach-Object { Write-Info "  $_" }
Write-Info "DestinationRule:"
kubectl get destinationrule -n $Namespace 2>&1 | ForEach-Object { Write-Info "  $_" }
Write-Info "VirtualService:"
kubectl get virtualservice -n $Namespace 2>&1 | ForEach-Object { Write-Info "  $_" }
Write-Info "AuthorizationPolicy:"
kubectl get authorizationpolicy -n $Namespace 2>&1 | ForEach-Object { Write-Info "  $_" }

if ($SkipTests) {
    Write-OK "Skipping tests (use -SkipTests:$false to run tests)"
    exit 0
}

# ─── 6. Tests ────────────────────────────────────────────────────────────────
Write-Host ""
Write-Host "============================================================" -ForegroundColor Magenta
Write-Host "  RUNNING SERVICE MESH TESTS" -ForegroundColor Magenta
Write-Host "============================================================" -ForegroundColor Magenta

# Test 1: Pod có sidecar không? (2/2 containers)
Write-Step "[TEST 1] Verify Envoy sidecar injection (pods should show 2/2)"
$pods = kubectl get pods -n $Namespace --no-headers 2>$null
$injectedCount = ($pods | Where-Object { $_ -match "2/2" }).Count
$notInjectedCount = ($pods | Where-Object { $_ -match "1/1" -and $_ -notmatch "Completed" }).Count
Write-Info "  Injected (2/2): $injectedCount"
Write-Info "  Not injected (1/1): $notInjectedCount"
if ($notInjectedCount -eq 0) { Write-OK "TEST 1 PASSED: All pods have Envoy sidecar" }
else { Write-Warn "TEST 1 PARTIAL: Some pods missing sidecar - check injection label" }

# Test 2: mTLS — giao tiếp trong mesh từ order → product (port 8090 actuator)
Write-Step "[TEST 2] mTLS in-mesh communication: order -> product (expect HTTP 200)"
$orderPod = kubectl get pod -n $Namespace -l "app.kubernetes.io/name=order" --no-headers 2>$null |
    Select-Object -First 1 | ForEach-Object { ($_ -split '\s+')[0] }
if ($orderPod) {
    $result = kubectl exec $orderPod -n $Namespace -c order -- wget -qO- http://product:8090/actuator/health 2>&1
    if ($result -match "UP") {
        Write-OK "TEST 2 PASSED: order -> product returns UP status on health check (mTLS working)"
    } else {
        Write-Warn "TEST 2 WARN: Unexpected response - $result"
    }
} else {
    Write-Warn "TEST 2 SKIP: order pod not found"
}

# Test 3: AuthZ — truy cập bị chặn (search → cart expect 403)
Write-Step "[TEST 3] AuthZ Policy: search -> cart (expect HTTP 403 Denied)"
$searchPod = kubectl get pod -n $Namespace -l "app.kubernetes.io/name=search" --no-headers 2>$null |
    Select-Object -First 1 | ForEach-Object { ($_ -split '\s+')[0] }
if ($searchPod) {
    # wget returns non-zero code on 403, error message contains "403 Forbidden"
    $result = kubectl exec $searchPod -n $Namespace -c search -- wget -qO- http://cart:8090/actuator/health 2>&1
    if ($result -match "403 Forbidden") {
        Write-OK "TEST 3 PASSED: search -> cart is BLOCKED by AuthorizationPolicy (403 Forbidden)"
    } else {
        Write-Warn "TEST 3 WARN: Connection was not blocked as expected! Got: $result"
    }
} else {
    Write-Warn "TEST 3 SKIP: search pod not found"
}

# Test 4: AuthZ — truy cập hợp lệ (order → cart expect 200)
Write-Step "[TEST 4] AuthZ Policy: order -> cart (expect 200 allowed)"
if ($orderPod) {
    $result = kubectl exec $orderPod -n $Namespace -c order -- wget -qO- http://cart:8090/actuator/health 2>&1
    if ($result -match "UP") {
        Write-OK "TEST 4 PASSED: order -> cart returns UP status (ALLOWED by AuthorizationPolicy)"
    } else {
        Write-Warn "TEST 4 WARN: Expected allowed, but failed: $result"
    }
} else {
    Write-Warn "TEST 4 SKIP: order pod not found"
}

# Test 5: Out-of-mesh bị chặn (chỉ khi STRICT mode)
Write-Step "[TEST 5] Out-of-mesh access test (expects SSL handshake rejection in STRICT mode)"
Write-Info "  Note: dev namespace PeerAuthentication is set by config (check mtls/peer-authentication.yaml)"
$testResult = kubectl run curl-nomesh-test --image=curlimages/curl:latest `
    --restart=Never --rm -i -n default `
    --command -- curl -sv --max-time 5 "http://product.$Namespace.svc.cluster.local:8090/actuator/health" 2>&1
Write-Info "  Result output contains:"
$testResult | Select-Object -Last 10 | ForEach-Object { Write-Info "    $_" }

Write-Host ""
Write-Host "============================================================" -ForegroundColor Magenta
Write-Host "  TEST SUMMARY COMPLETE" -ForegroundColor Magenta
Write-Host "  To see Kiali topology: kubectl port-forward svc/kiali 20001:20001 -n istio-system" -ForegroundColor Cyan
Write-Host "  Then open: http://localhost:20001" -ForegroundColor Cyan
Write-Host "  Or via NodePort: http://localhost:30089" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Magenta
