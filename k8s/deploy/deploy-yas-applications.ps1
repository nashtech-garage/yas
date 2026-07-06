$Domain = "yas.local.com"
$ChartsDir = "d:/Y3S2/[DevOps]_Project02/k8s/charts"

Write-Host "Updating backoffice-bff..."
helm dependency update "$ChartsDir/backoffice-bff"
helm upgrade --install backoffice-bff "$ChartsDir/backoffice-bff" --namespace yas --create-namespace --set backend.ingress.host="backoffice.$Domain"

Write-Host "Updating backoffice-ui..."
helm dependency update "$ChartsDir/backoffice-ui"
helm upgrade --install backoffice-ui "$ChartsDir/backoffice-ui" --namespace yas --create-namespace

Start-Sleep -Seconds 10

Write-Host "Updating storefront-bff..."
helm dependency update "$ChartsDir/storefront-bff"
helm upgrade --install storefront-bff "$ChartsDir/storefront-bff" --namespace yas --create-namespace --set backend.ingress.host="storefront.$Domain"

Write-Host "Updating storefront-ui..."
helm dependency update "$ChartsDir/storefront-ui"
helm upgrade --install storefront-ui "$ChartsDir/storefront-ui" --namespace yas --create-namespace

Start-Sleep -Seconds 10

Write-Host "Updating swagger-ui..."
helm upgrade --install swagger-ui "$ChartsDir/swagger-ui" --namespace yas --create-namespace --set ingress.host="api.$Domain"

Start-Sleep -Seconds 5

$charts = @("cart","customer","inventory","media","order","product","search","tax","sampledata")
foreach ($chart in $charts) {
    Write-Host "Updating $chart..."
    helm dependency update "$ChartsDir/$chart"
    helm upgrade --install $chart "$ChartsDir/$chart" --namespace yas --create-namespace --set backend.ingress.host="api.$Domain"
    Start-Sleep -Seconds 10
}
