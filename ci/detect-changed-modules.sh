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

# Lab 2 CD: GitOps/docs/spec/agent-only commits should not trigger full Maven
# test/build/image work. Jenkins will run lightweight validation only.
if [[ -n "${changed_files}" ]] \
  && ! grep -Ev '^(deploy/gitops/|docs/|specs/|\.agents/|\.specify/)' <<< "${changed_files}" | grep -q .; then
  printf '%s\n' "__skip_full_ci__"
  exit 0
fi

# Pipeline changes affect build/push/GitOps behavior, so validate the full path.
if grep -Eq '^Jenkinsfile$' <<< "${changed_files}"; then
  printf '%s\n' "${modules[@]}" | paste -sd ','
  exit 0
fi

# Only ci/* changed → CI-only; avoid rebuilding every module (Coverage Gate on all services).
if [[ -n "${changed_files}" ]] \
  && ! grep -Ev '^ci/' <<< "${changed_files}" | grep -q .; then
  printf '%s\n' "common-library" | paste -sd ','
  exit 0
fi

# If shared build, root files, or the service catalog change, rebuild all modules.
if [[ -z "${changed_files}" ]] \
  || grep -Eq '^(pom\.xml|services\.yaml|\.github/|checkstyle/|common-library/|docker/|k8s/|scripts/)' <<< "${changed_files}"; then
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

is_selected() {
  local candidate="$1"
  local selected
  for selected in "${selected_modules[@]}"; do
    [[ "${selected}" == "${candidate}" ]] && return 0
  done
  return 1
}

module_exists() {
  local candidate="$1"
  local module
  for module in "${modules[@]}"; do
    [[ "${module}" == "${candidate}" ]] && return 0
  done
  return 1
}

service_dependencies="$(
  awk '
    function emit() {
      if (name != "") {
        gsub(/[[:space:]]/, "", deps)
        print name ":" deps
      }
    }
    function reset() {
      name = ""
      deps = ""
    }
    $1 == "-" && $2 == "name:" {
      emit()
      reset()
      name = $3
      next
    }
    $1 == "dependencies:" {
      deps = $0
      sub(/^.*\[/, "", deps)
      sub(/\].*$/, "", deps)
      next
    }
    END { emit() }
  ' services.yaml
)"

added=1
while [[ ${added} -eq 1 ]]; do
  added=0
  while IFS=: read -r service deps; do
    [[ -n "${service}" ]] || continue
    module_exists "${service}" || continue
    is_selected "${service}" && continue

    IFS=',' read -r -a dep_list <<< "${deps}"
    for dependency in "${dep_list[@]}"; do
      if is_selected "${dependency}"; then
        selected_modules+=("${service}")
        added=1
        break
      fi
    done
  done <<< "${service_dependencies}"
done

printf '%s\n' "${selected_modules[@]}" | paste -sd ','
