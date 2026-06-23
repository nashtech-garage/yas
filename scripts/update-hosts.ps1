# PowerShell script to update the Windows hosts file with YAS Local Kubernetes NodePort domains.
# MUST BE RUN AS ADMINISTRATOR.

$hostsPath = "C:\Windows\System32\drivers\etc\hosts"
$entries = @(
    "127.0.0.1 storefront-ui.dev.yas.local.com",
    "127.0.0.1 backoffice-ui.dev.yas.local.com",
    "127.0.0.1 swagger-ui.dev.yas.local.com",
    "127.0.0.1 identity.dev.yas.local.com",
    "127.0.0.1 storefront-bff.dev.yas.local.com",
    "127.0.0.1 backoffice-bff.dev.yas.local.com"
)

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "Updating hosts file for YAS Local Kubernetes Dev..." -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# Check for administrative privileges
$identity = [Security.Principal.WindowsIdentity]::GetCurrent()
$principal = New-Object Security.Principal.WindowsPrincipal($identity)
if (-not $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    Write-Error "This script must be run as Administrator! Please open PowerShell as Administrator and run the script again."
    Exit 1
}

foreach ($entry in $entries) {
    if (!(Get-Content $hostsPath | Select-String -SimpleMatch $entry)) {
        # Ensure new line at the end
        Add-Content $hostsPath "`n$entry"
        Write-Host "Successfully added: $entry" -ForegroundColor Green
    } else {
        Write-Host "Already exists: $entry" -ForegroundColor Yellow
    }
}

Write-Host "==========================================================" -ForegroundColor Green
Write-Host "hosts file successfully configured!" -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green
