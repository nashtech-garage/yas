#!/bin/bash
set -e

ENV=${1:?Usage: update-gitops-manifest.sh <dev|staging> [image-tag]}
COMMIT_ID=$(git rev-parse --short HEAD)
# If an explicit image tag is provided (e.g. v1.0.0 for staging), use it;
# otherwise fall back to the short commit SHA.
IMAGE_TAG=${2:-${COMMIT_ID}}
GITOPS_REPO="https://${GH_TOKEN}@github.com/com-suon-bi-cha/gitops-manifest-k8s.git"
WORKDIR="/tmp/gitops-update-$$"

if [ "${ENV}" != "dev" ] && [ "${ENV}" != "staging" ]; then
    echo "ERROR: ENV must be dev or staging, got '${ENV}'"
    exit 1
fi

trap 'rm -rf "${WORKDIR}"' EXIT

# Detect which services changed.
#
# Strategy:
#   1. Find merge-base between origin/main and HEAD.
#   2. If merge-base == HEAD (detached-HEAD after merge, or HEAD is an ancestor
#      of origin/main), the standard diff would be empty.
#      In that case fall back to HEAD~1..HEAD — but only if the repo has at
#      least 2 commits (guards against shallow clones / initial commit).
#   3. Otherwise use the merge-base diff (feature-branch case).
#
MERGE_BASE=$(git merge-base origin/main HEAD 2>/dev/null || true)
HEAD_SHA=$(git rev-parse HEAD)

echo "=== Updating environment: ${ENV} ==="
echo "Commit:     ${COMMIT_ID}"
echo "Image tag:  ${IMAGE_TAG}"
echo "HEAD:       ${HEAD_SHA}"
echo "Merge-base: ${MERGE_BASE}"

UPDATE_ALL=false
if [ "${ENV}" = "staging" ] && [ "$#" -ge 2 ]; then
    UPDATE_ALL=true
    echo "Mode: staging release — updating all scoped service images"
fi

if [ "${UPDATE_ALL}" = "true" ]; then
    CHANGED_FILES=""
elif [ "${MERGE_BASE}" = "${HEAD_SHA}" ]; then
    # HEAD is already on (or an ancestor of) origin/main — diff vs previous commit
    PARENT_COUNT=$(git rev-list --count HEAD 2>/dev/null || echo 0)
    if [ "${PARENT_COUNT}" -lt 2 ]; then
        echo "WARNING: repo has only 1 commit, cannot diff HEAD~1. No services to update."
        exit 0
    fi
    CHANGED_FILES=$(git diff --name-only HEAD~1 HEAD)
    echo "Mode: main/post-merge — diffing HEAD~1..HEAD"
else
    CHANGED_FILES=$(git diff --name-only "${MERGE_BASE}" HEAD)
    echo "Mode: feature branch — diffing merge-base..HEAD"
fi

echo "Changed files: ${CHANGED_FILES}"

git clone "${GITOPS_REPO}" "${WORKDIR}"
cd "${WORKDIR}/environments/${ENV}"

declare -A SERVICE_PATHS=(
    ["media"]="media"
    ["product"]="product"
    ["order"]="order"
    ["inventory"]="inventory"
    ["payment"]="payment"
    ["sampledata"]="sampledata"
    ["customer"]="customer"
    ["cart"]="cart"
    ["tax"]="tax"
    ["search"]="search"
    ["backoffice-bff"]="backoffice-bff"
    ["storefront-bff"]="storefront-bff"
    ["backoffice"]="backoffice"
    ["storefront"]="storefront"
)

UPDATED=0
for svc in "${!SERVICE_PATHS[@]}"; do
    source_path="${SERVICE_PATHS[$svc]}"
    if [ "${UPDATE_ALL}" = "true" ] || echo "${CHANGED_FILES}" | grep -q "^${source_path}/"; then
        echo "Updating ${svc} → ${IMAGE_TAG}"
        kustomize edit set image "bingsu1103/${svc}:${IMAGE_TAG}"
        UPDATED=$((UPDATED + 1))
    fi
done

if [ $UPDATED -eq 0 ]; then
    echo "No service manifests to update."
    exit 0
fi

git config user.email "jenkins-ci@project.local"
git config user.name "Jenkins CI"
git add -A
git commit -m "ci(${ENV}): update ${UPDATED} service(s) to ${IMAGE_TAG}"
git push

echo "=== GitOps manifest updated ==="
