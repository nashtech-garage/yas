# Snyk integration guide

This guide explains how to run Snyk in the existing GitHub Actions CI/CD pipeline.

## 1. Create a Snyk API token

1. Sign in to Snyk.
2. Go to Account Settings.
3. Copy your API token.

## 2. Add repository secret in GitHub

1. Open this repository in GitHub.
2. Go to Settings -> Secrets and variables -> Actions.
3. Add a new repository secret:
   - Name: `snyk_api_key`
   - Value: your Snyk API token

## 3. Verify workflow file is present

The workflow is defined in `.github/workflows/snyk-security.yaml`.

It runs on:

- pushes to `main`
- pull requests to `main` (same repository only)
- nightly schedule (02:00 UTC)
- manual trigger (`workflow_dispatch`)

## 4. Run the workflow manually once

1. Go to Actions tab.
2. Open `Snyk security scan` workflow.
3. Click Run workflow.

This first run validates token and baseline findings.

## 5. Review findings

1. Open Security -> Code scanning alerts in GitHub.
2. Review uploaded SARIF findings from:
   - Snyk OSS dependency scan
   - Snyk code scan

## 6. Understand non-blocking mode (current default)

The scan steps use `continue-on-error: true` for safe adoption.

This means:

- scans still run
- findings are still uploaded
- workflow does not fail build on vulnerabilities yet

## 7. Enforce security gate when ready

When the team is ready to block risky changes:

1. Edit `.github/workflows/snyk-security.yaml`.
2. Remove `continue-on-error: true` from scan steps.
3. Keep `--severity-threshold=high` (or change to `medium` if desired).

After that, builds fail when findings at or above the threshold are detected.

## 8. Optional tuning

- Exclude non-production directories by replacing `--all-projects` with explicit manifests.
- Add `.snyk` policy file to manage ignores with expiration and justification.
- Add Snyk container scanning in service workflows if you want image-level checks in addition to Trivy.
