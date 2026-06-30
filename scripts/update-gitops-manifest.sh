#!/bin/bash
set -e

ENV=${1:?Usage: update-gitops-manifest.sh <dev|staging>}
COMMIT_ID=$(git rev-parse --short HEAD)
GITOPS_REPO="https://${GH_TOKEN}@github.com/com-suon-bi-cha/gitops-manifest-k8s.git"
WORKDIR="/tmp/gitops-update-$$"

# Detect which services changed (compare vs main merge-base)
MERGE_BASE=$(git merge-base origin/main HEAD)
CHANGED_FILES=$(git diff --name-only "${MERGE_BASE}" HEAD)

echo "=== Updating environment: ${ENV} ==="
echo "Commit: ${COMMIT_ID}"
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
