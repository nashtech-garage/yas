param(
    [string]$Namespace = "dev"
)

$CHARTS_DIR = "k8s\charts"
$DOMAIN = "yas.local.com"

function Write-Step($msg) { Write-Host "" ; Write-Host ">>> $msg" -ForegroundColor Cyan }
function Write-OK($msg)   { Write-Host "    OK: $msg" -ForegroundColor Green }
function Write-Warn($msg) { Write-Host "    WARN: $msg" -ForegroundColor Yellow }

function Deploy-Chart {
    param([string]$Chart, [string[]]$Extra = @())
    Write-Step "Deploying $Chart ..."
    Push-Location "$PSScriptRoot\$CHARTS_DIR\$Chart"
    helm dependency build . 2>$null | Out-Null
    Pop-Location
    $helmArgs = @("upgrade","--install",$Chart,"$CHARTS_DIR\$Chart","--namespace",$Namespace,"--create-namespace","--timeout","3m0s") + $Extra
    helm @helmArgs
    if ($LASTEXITCODE -ne 0) { Write-Warn "$Chart failed (exit $LASTEXITCODE)" }
    else { Write-OK "$Chart deployed" }
}

Set-Location $PSScriptRoot

Write-Host "==================================================" -ForegroundColor Green
Write-Host "  YAS Deploy -> namespace: $Namespace"             -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green

kubectl get nodes 2>$null | Out-Null
if ($LASTEXITCODE -ne 0) { Write-Error "Cannot connect to cluster"; exit 1 }

# 1. ConfigMaps + Secrets - deploy vào CÙNG namespace với services
Write-Step "Deploying yas-configuration ..."
Push-Location "$PSScriptRoot\$CHARTS_DIR\yas-configuration"
helm dependency build . 2>$null | Out-Null
Pop-Location
# Deploy vào namespace của services (pods cần tìm ConfigMap cùng namespace)
helm upgrade --install yas-configuration "$CHARTS_DIR\yas-configuration" --namespace $Namespace --create-namespace
Write-OK "yas-configuration deployed to namespace: $Namespace"

# 2. BFF + UI
$noMonitor = "backend.serviceMonitor.enabled=false"

Deploy-Chart "backoffice-bff" @("--set","backend.ingress.host=backoffice.$Namespace.$DOMAIN","--set",$noMonitor)
Deploy-Chart "storefront-bff" @("--set","backend.ingress.host=storefront.$Namespace.$DOMAIN","--set",$noMonitor)
Deploy-Chart "backoffice-ui"
Deploy-Chart "storefront-ui"
Deploy-Chart "swagger-ui"     @("--set","ingress.host=api.$Namespace.$DOMAIN")

# 3. Backend microservices
foreach ($svc in @("product","cart","customer","inventory","order","tax","media","search")) {
    Deploy-Chart $svc @("--set","backend.ingress.host=api.$Namespace.$DOMAIN","--set",$noMonitor)
}

# 4. Sample data (one-time seed)
Deploy-Chart "sampledata" @("--set","backend.ingress.host=api.$Namespace.$DOMAIN","--set",$noMonitor)

Write-Host ""
Write-Host "==================================================" -ForegroundColor Green
Write-Host "  Done! Check: kubectl get pods -n $Namespace"     -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green
