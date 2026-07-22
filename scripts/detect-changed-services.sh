#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
    echo "Usage: $0 <base-commit> <head-commit>"
    exit 1
fi

BASE_COMMIT="$1"
HEAD_COMMIT="$2"

SUPPORTED_SERVICES=(
    "product"
    "tax"
)

echo "Comparing commits:"
echo "Base commit: $BASE_COMMIT"
echo "Head commit: $HEAD_COMMIT"
echo

mapfile -t CHANGED_FILES < <(
    git diff --name-only "$BASE_COMMIT" "$HEAD_COMMIT"
)

echo "Changed files:"

if [[ ${#CHANGED_FILES[@]} -eq 0 ]]; then
    echo "No changed files detected"
else
    printf -- "- %s\n" "${CHANGED_FILES[@]}"
fi

CHANGED_SERVICES=()

for service in "${SUPPORTED_SERVICES[@]}"; do
    for file in "${CHANGED_FILES[@]}"; do
        if [[ "$file" == "$service" || "$file" == "$service/" ]]; then
            CHANGED_SERVICES+=("$service")
            break
        fi
    done
done

if [[ ${#CHANGED_SERVICES[@]} -eq 0 ]]; then
    MATRIX='{"include":[]}'
    HAS_CHANGES="false"
else
    MATRIX_ENTRIES=""

    for service in "${CHANGED_SERVICES[@]}"; do
        if [[ -n "$MATRIX_ENTRIES" ]]; then
            MATRIX_ENTRIES+=","
        fi

        MATRIX_ENTRIES+="{\"service\":\"$service\"}"
    done

    MATRIX="{\"include\":[${MATRIX_ENTRIES}]}"
    HAS_CHANGES="true"
fi

echo 
echo "Changed services:"

if [[ ${#CHANGED_SERVICES[@]} -eq 0]]; then
    echo "None"
else
    printf -- "- %s\n" "${CHANGED_SERVICES[@]}"
fi

echo
echo "Dynamic matrix:"
echo "$MATRIX"

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
    echo "matrix=$MATRIX" >> "$GITHUB_OUTPUT"
    echo "has_changes=$HAS_CHANGES" >> "$GITHUB_OUTPUT"
fi
