#!/usr/bin/env bash
# Fail fast if required CLI tools are missing or wrong version (no download in CI).
set -euo pipefail

GITLEAKS_EXPECTED="${GITLEAKS_EXPECTED:-8.18.4}"

if ! command -v gitleaks >/dev/null 2>&1; then
  echo "ERROR: gitleaks not found on PATH. Install on agent (label yas-build-worker); CI does not download it."
  exit 1
fi

GL_LINE="$(gitleaks version 2>/dev/null | head -1 | tr -d '\r')"
if [[ -z "${GL_LINE}" ]] || [[ "${GL_LINE}" != *"${GITLEAKS_EXPECTED}"* ]]; then
  echo "ERROR: expected gitleaks containing '${GITLEAKS_EXPECTED}', got '${GL_LINE}'"
  exit 1
fi
echo "gitleaks OK (${GL_LINE})"

if ! command -v snyk >/dev/null 2>&1; then
  echo "ERROR: snyk not found on PATH. Install Snyk CLI on agent."
  exit 1
fi

SNYK_LINE="$(snyk --version 2>/dev/null | head -1 | tr -d '\r')"
if [[ -z "${SNYK_LINE}" ]]; then
  echo "ERROR: snyk --version produced no output"
  exit 1
fi
echo "snyk OK (${SNYK_LINE})"
