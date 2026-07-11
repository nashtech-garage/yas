# =============================================================
# YAS — Windows helper to launch WSL2 k3s setup scripts
# Run from Windows PowerShell on each laptop
#
# Usage:
#   # On laptop-a (master):
#   .\scripts\launch-wsl2-k3s.ps1 -Role master
#
#   # On laptop-b/c/d (agents):
#   .\scripts\launch-wsl2-k3s.ps1 -Role agent `
#       -MasterIP "100.x.x.1" `
#       -Token "K10xxx..." `
#       -NodeRole "laptop-b"
# =============================================================
param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("master","agent")]
    [string]$Role,

    [string]$MasterIP = "",
    [string]$Token = "",
    [string]$NodeRole = "laptop-b"
)

function Write-Step($msg) { Write-Host ""; Write-Host ">>> $msg" -ForegroundColor Cyan }
function Write-OK($msg)   { Write-Host "    OK: $msg" -ForegroundColor Green }
function Write-Fail($msg) { Write-Host "    FAIL: $msg" -ForegroundColor Red; exit 1 }

# ── Check WSL2 is available ────────────────────────────────────
Write-Step "Checking WSL2..."
$wslCheck = wsl --status 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Fail "WSL2 not available. Install: wsl --install (then reboot)"
}
Write-OK "WSL2 available"

# ── Copy scripts to WSL2 ──────────────────────────────────────
Write-Step "Copying scripts to WSL2 home directory..."
$scriptDir = $PSScriptRoot
# Convert Windows path to WSL2 path
$wslScriptDir = wsl wslpath $scriptDir.Replace('\','/')
wsl cp "$wslScriptDir/setup-k3s-master-wsl2.sh" ~/setup-k3s-master-wsl2.sh
wsl cp "$wslScriptDir/setup-k3s-agent-wsl2.sh" ~/setup-k3s-agent-wsl2.sh
wsl chmod +x ~/setup-k3s-master-wsl2.sh ~/setup-k3s-agent-wsl2.sh
Write-OK "Scripts copied"

if ($Role -eq "master") {
    # ── Install Tailscale in WSL2 ──────────────────────────────
    Write-Step "Installing Tailscale in WSL2 (if not already installed)..."
    wsl bash -c "command -v tailscale || (curl -fsSL https://tailscale.com/install.sh | sh)"

    Write-Step "Starting Tailscale in WSL2..."
    Write-Host "    You may need to authenticate Tailscale. Follow the URL shown." -ForegroundColor Yellow
    wsl sudo tailscale up --accept-routes 2>&1 | ForEach-Object { Write-Host "    $_" }

    # ── Run master setup ───────────────────────────────────────
    Write-Step "Running k3s MASTER setup (laptop-a)..."
    wsl sudo ~/setup-k3s-master-wsl2.sh

    Write-Host ""
    Write-Host "================================================================" -ForegroundColor Green
    Write-Host "  Master setup complete! Next steps:" -ForegroundColor Green
    Write-Host "  1. Copy the JOIN TOKEN shown above" -ForegroundColor Green
    Write-Host "  2. Copy kubeconfig: wsl cat /tmp/kubeconfig-tailscale.yaml" -ForegroundColor Green
    Write-Host "  3. Run on other laptops: .\launch-wsl2-k3s.ps1 -Role agent -MasterIP <ip> -Token <token>" -ForegroundColor Green
    Write-Host "================================================================" -ForegroundColor Green

} else {
    # ── Validate agent params ──────────────────────────────────
    if (-not $MasterIP -or -not $Token) {
        Write-Fail "For agent role, provide -MasterIP and -Token from master setup"
    }

    # ── Install Tailscale in WSL2 ──────────────────────────────
    Write-Step "Installing Tailscale in WSL2 (if not already installed)..."
    wsl bash -c "command -v tailscale || (curl -fsSL https://tailscale.com/install.sh | sh)"

    Write-Step "Starting Tailscale in WSL2..."
    Write-Host "    Authenticate Tailscale to the SAME network as laptop-a!" -ForegroundColor Yellow
    wsl sudo tailscale up --accept-routes 2>&1 | ForEach-Object { Write-Host "    $_" }

    # ── Run agent setup ────────────────────────────────────────
    Write-Step "Running k3s AGENT setup ($NodeRole)..."
    $k3sUrl = "https://${MasterIP}:6443"
    wsl bash -c "K3S_URL='$k3sUrl' K3S_TOKEN='$Token' NODE_ROLE='$NodeRole' sudo -E ~/setup-k3s-agent-wsl2.sh"

    Write-OK "Agent ($NodeRole) joined cluster"

    Write-Host ""
    Write-Host "================================================================" -ForegroundColor Green
    Write-Host "  Agent ($NodeRole) setup complete!" -ForegroundColor Green
    Write-Host "  Verify on master: kubectl get nodes" -ForegroundColor Green
    Write-Host "================================================================" -ForegroundColor Green
}

# ── Open Windows Firewall ports ────────────────────────────────
Write-Step "Opening Windows Firewall ports for NodePort access..."
$ports = @(30080,30081,30082,30084,30085,30086,30088,30089,6443)
foreach ($port in $ports) {
    $rule = Get-NetFirewallRule -DisplayName "YAS-k3s-$port" -ErrorAction SilentlyContinue
    if (-not $rule) {
        New-NetFirewallRule -DisplayName "YAS-k3s-$port" `
            -Direction Inbound -Protocol TCP -LocalPort $port `
            -Action Allow -Profile Any | Out-Null
        Write-Info "  Opened port: $port"
    }
}
Write-OK "Firewall rules applied"
