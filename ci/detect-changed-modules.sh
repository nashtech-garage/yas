#!/usr/bin/env bash
set -euo pipefail

# Keep this list aligned with root pom.xml modules section.
modules=(
  "common-library"
  "backoffice-bff"
  "cart"
  "customer"
  "inventory"
  "location"
  "media"
  "order"
  "payment-paypal"
  "payment"
  "product"
  "promotion"
  "rating"
  "search"
  "storefront-bff"
  "tax"
  "webhook"
  "sampledata"
  "recommendation"
  "delivery"
)

base_ref="${1:-}"
if [[ -z "${base_ref}" ]]; then
  if [[ -n "${CHANGE_TARGET:-}" ]]; then
    base_ref="origin/${CHANGE_TARGET}"
  elif git rev-parse --verify HEAD~1 >/dev/null 2>&1; then
    base_ref="HEAD~1"
  else
    # First commit case: build all modules.
    printf '%s\n' "${modules[@]}" | paste -sd ','
    exit 0
  fi
fi

if ! git rev-parse --verify "${base_ref}" >/dev/null 2>&1; then
  # Missing target branch in local refs (common in fresh Jenkins checkout).
  if [[ "${base_ref}" == origin/* ]]; then
    target_branch="${base_ref#origin/}"
    git fetch origin "${target_branch}:${target_branch}" >/dev/null 2>&1 || true
  fi
fi

changed_files="$(git diff --name-only "${base_ref}"...HEAD || true)"

# If shared build or root files change, rebuild all modules.
if [[ -z "${changed_files}" ]] \
  || grep -Eq '^(pom\.xml|\.github/|checkstyle/|common-library/|docker/|k8s/|scripts/)' <<< "${changed_files}"; then
  printf '%s\n' "${modules[@]}" | paste -sd ','
  exit 0
fi

selected_modules=()
for module in "${modules[@]}"; do
  if grep -Eq "^${module}/" <<<"${changed_files}"; then
    selected_modules+=("${module}")
  fi
done

if [[ ${#selected_modules[@]} -eq 0 ]]; then
  # Non-module change (e.g. docs): keep CI green but cheap by running one core module.
  selected_modules=("common-library")
fi

printf '%s\n' "${selected_modules[@]}" | paste -sd ','
