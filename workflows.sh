#!/bin/bash

WORKFLOW_TEMPLATE=$(cat .github/workflow-template.yaml)
SERVICES=(product pricing media)

for SERVICE_NAME in "${SERVICES[@]}"; do
    echo "Generating workflow for ${SERVICE_NAME} service"

    WORKFLOW="${WORKFLOW_TEMPLATE//\{\{SERVICE_NAME\}\}/${SERVICE_NAME}}"
    echo "${WORKFLOW}" > ".github/workflows/${SERVICE_NAME}-ci.yaml"
done