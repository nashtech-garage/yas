$ErrorActionPreference = "Stop"

$Version = "1.30.2"
$IstioDir = "C:\tmp\istio-$Version"
$IstioCtl = "$IstioDir\bin\istioctl.exe"
$Kubeconfig = Join-Path $PSScriptRoot "..\..\..\teammate-kubeconfig.yaml"

if (!(Test-Path $IstioCtl)) {
    $Zip = "C:\tmp\istio-$Version-win.zip"
    Invoke-WebRequest `
        -Uri "https://github.com/istio/istio/releases/download/$Version/istio-$Version-win.zip" `
        -OutFile $Zip `
        -UseBasicParsing
    Expand-Archive -LiteralPath $Zip -DestinationPath "C:\tmp" -Force
}

& $IstioCtl install --set profile=demo -y --kubeconfig $Kubeconfig
kubectl apply -f (Join-Path $PSScriptRoot "..\namespaces.yaml") --kubeconfig $Kubeconfig
kubectl label namespace ingress-nginx istio-injection=enabled --overwrite --kubeconfig $Kubeconfig
kubectl apply -f $PSScriptRoot --kubeconfig $Kubeconfig
kubectl rollout restart deployment -n yas --kubeconfig $Kubeconfig
kubectl rollout restart deployment ingress-nginx-controller -n ingress-nginx --kubeconfig $Kubeconfig
