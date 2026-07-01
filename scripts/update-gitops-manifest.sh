#!/bin/bash
set -e

ENV=${1:?Usage: update-gitops-manifest.sh <dev|staging>}
COMMIT_ID=$(git rev-parse --short HEAD)
GITOPS_REPO="https://${GH_TOKEN}@github.com/com-suon-bi-cha/gitops-manifest-k8s.git"
WORKDIR="/tmp/gitops-update-$$"

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
echo "HEAD:       ${HEAD_SHA}"
echo "Merge-base: ${MERGE_BASE}"

if [ "${MERGE_BASE}" = "${HEAD_SHA}" ]; then
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

SERVICES="media product order inventory payment promotion rating delivery \
          sampledata recommendation customer location cart tax search webhook \
          backoffice-bff storefront-bff payment-paypal"

UPDATED=0
for svc in $SERVICES; do
    if echo "${CHANGED_FILES}" | grep -q "^${svc}/"; then
        echo "Updating ${svc} → ${COMMIT_ID}"
        kustomize edit set image "bingsu1103/${svc}=bingsu1103/${svc}:${COMMIT_ID}"
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
git commit -m "ci(${ENV}): update ${UPDATED} service(s) to ${COMMIT_ID}"
git push

rm -rf "${WORKDIR}"
echo "=== GitOps manifest updated ==="
