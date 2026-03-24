#!/bin/bash

# =============================================================================
# EvoMaster Black-Box Test Generator — YAS (Yet Another Shop)
# =============================================================================
# Usage:
#   ./evomaster-blackbox.sh <service> [role]
#
# Examples:
#   ./evomaster-blackbox.sh product admin
#   ./evomaster-blackbox.sh cart customer
#   ./evomaster-blackbox.sh product none
#
# Available services:
#   product, media, customer, cart, rating, order, payment,
#   location, inventory, tax, promotion, search
#
# Available roles:
#   admin    — token with roles ADMIN + CUSTOMER
#   customer — token with role CUSTOMER only
#   none     — no authentication (permitAll endpoints)
#
# Environment variables:
#   EVOMASTER_MAX_TIME   Max time in seconds (default: 60)
#   EVOMASTER_RATE       Requests per minute (default: 60)
#   EVOMASTER_SEED       Seed for reproducibility (default: random)
#   EVOMASTER_IMAGE      Docker image (default: webfuzzing/evomaster:4.0.0)
#   EVOMASTER_VERSION    Version string for run-info.json (default: 4.0.0; matches "* EvoMaster version:" in evomaster.log)
#   YAS_API_URL          API base URL (default: http://api.yas.local)
#   KEYCLOAK_URL         Keycloak URL (default: http://identity)
# =============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUTPUT_BASE_DIR="$SCRIPT_DIR/../generated-tests/blackbox"

# EvoMaster parameters (default version matches evomaster.log: * EvoMaster version: 4.0.0)
EVOMASTER_VERSION="${EVOMASTER_VERSION:-4.0.0}"
EVOMASTER_IMAGE="${EVOMASTER_IMAGE:-webfuzzing/evomaster:${EVOMASTER_VERSION}}"
MAX_TIME="${EVOMASTER_MAX_TIME:-60}"
RATE_PER_MINUTE="${EVOMASTER_RATE:-60}"
SEED="${EVOMASTER_SEED:-}"

# Script arguments
SERVICE_NAME="${1:-}"
USER_ROLE="${2:-none}"

# Timestamp recorded in run metadata
RUN_TIMESTAMP="$(date +%Y%m%d_%H%M%S)"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

# Load authentication configuration
source "$SCRIPT_DIR/auth-config.sh"

# =============================================================================
# Service map: name -> context path on api.yas.local
# =============================================================================
declare -A SERVICE_PATHS=(
    [product]="product"
    [media]="media"
    [customer]="customer"
    [cart]="cart"
    [rating]="rating"
    [order]="order"
    [payment]="payment"
    [location]="location"
    [inventory]="inventory"
    [tax]="tax"
    [promotion]="promotion"
    [search]="search"
    [sampledata]="sampledata"
)

# =============================================================================
# Functions
# =============================================================================

show_usage() {
    echo -e "${BLUE}Usage:${NC} $0 <service> [role]"
    echo -e ""
    echo -e "${BLUE}Available services:${NC}"
    for svc in "${!SERVICE_PATHS[@]}"; do
        echo -e "  - $svc"
    done | sort
    echo -e ""
    echo -e "${BLUE}Roles:${NC}"
    echo -e "  admin    — ADMIN + CUSTOMER (backoffice and storefront endpoints)"
    echo -e "  customer — CUSTOMER only (storefront cart and customer endpoints)"
    echo -e "  none     — no authentication (public endpoints)"
    echo -e ""
    echo -e "${BLUE}Environment variables:${NC}"
    echo -e "  EVOMASTER_MAX_TIME   Max time in seconds (default: 60)"
    echo -e "  EVOMASTER_RATE       Requests per minute (default: 60)"
    echo -e "  EVOMASTER_SEED       Seed for reproducibility (default: random)"
    echo -e "  EVOMASTER_IMAGE      Docker image (default: webfuzzing/evomaster:4.0.0)"
    echo -e "  EVOMASTER_VERSION    Version in run-info.json (default: 4.0.0)"
    echo -e "  YAS_API_URL          API base URL (default: http://api.yas.local)"
    echo -e "  KEYCLOAK_URL         Keycloak URL (default: http://identity)"
}

# =============================================================================
# Validations
# =============================================================================

if [ -z "$SERVICE_NAME" ]; then
    echo -e "${RED}Error: service name is required${NC}\n"
    show_usage
    exit 1
fi

if [ -z "${SERVICE_PATHS[$SERVICE_NAME]+_}" ]; then
    echo -e "${RED}Error: service '$SERVICE_NAME' not recognized${NC}\n"
    show_usage
    exit 1
fi

if [ "$USER_ROLE" != "admin" ] && [ "$USER_ROLE" != "customer" ] && [ "$USER_ROLE" != "none" ]; then
    echo -e "${YELLOW}Warning: role '$USER_ROLE' is invalid. Using 'none'.${NC}"
    USER_ROLE="none"
fi

if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed${NC}"
    exit 1
fi

# =============================================================================
# Directory preparation
# Structure: <service>/<role>/
# =============================================================================

echo -e "${BLUE}=== EvoMaster Black-Box — YAS ===${NC}\n"
echo -e "${YELLOW}Service:${NC}    $SERVICE_NAME"
echo -e "${YELLOW}Role:${NC}       $USER_ROLE"
echo -e "${YELLOW}Max time:${NC}   ${MAX_TIME}s"
echo -e "${YELLOW}Rate:${NC}       ${RATE_PER_MINUTE} req/min"
echo -e "${YELLOW}Timestamp:${NC}  $RUN_TIMESTAMP"
echo -e "${YELLOW}API URL:${NC}    ${YAS_API_URL}"
echo -e "${YELLOW}EvoMaster:${NC}  ${EVOMASTER_IMAGE} (v${EVOMASTER_VERSION})"
if [ -n "$SEED" ]; then
    echo -e "${YELLOW}Seed:${NC}       $SEED"
fi
echo ""

CONTEXT_PATH="${SERVICE_PATHS[$SERVICE_NAME]}"
SWAGGER_URL="${YAS_API_URL}/${CONTEXT_PATH}/v3/api-docs"

# Output folder: <service>/<role>/
OUTPUT_DIR="$OUTPUT_BASE_DIR/$SERVICE_NAME/$USER_ROLE"
mkdir -p "$OUTPUT_DIR"

echo -e "${YELLOW}Output:${NC}     $OUTPUT_DIR"

LOG_FILE="$OUTPUT_DIR/evomaster.log"

# =============================================================================
# Check OpenAPI spec availability
# =============================================================================

echo -e "\n${YELLOW}Checking OpenAPI spec at ${SWAGGER_URL}...${NC}"
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$SWAGGER_URL")

if [ "$HTTP_STATUS" = "200" ]; then
    echo -e "${GREEN}✓ OpenAPI spec available (HTTP 200)${NC}"
else
    echo -e "${RED}✗ OpenAPI spec not available (HTTP $HTTP_STATUS)${NC}"
    echo -e "${YELLOW}Check that service '${SERVICE_NAME}' is running at ${YAS_API_URL}${NC}"
    exit 1
fi

# =============================================================================
# Authentication
# =============================================================================

AUTH_HEADER=""

if [ "$USER_ROLE" = "none" ]; then
    echo -e "\n${YELLOW}Running without authentication${NC}"
else
    if [ "$USER_ROLE" = "customer" ]; then
        echo -e "\n${YELLOW}Ensuring customer user exists...${NC}"
        ensure_customer_user_exists
    fi

    echo -e "${YELLOW}Fetching OAuth2 token from Keycloak...${NC}"

    if [ "$USER_ROLE" = "admin" ]; then
        TOKEN=$(get_admin_token)
    else
        TOKEN=$(get_customer_token)
    fi

    if [ -n "$TOKEN" ]; then
        echo -e "${GREEN}✓ Token obtained${NC}"
        AUTH_HEADER="Authorization:Bearer $TOKEN"
    else
        echo -e "${RED}✗ Failed to obtain token. Check that Keycloak is reachable at ${KEYCLOAK_URL}${NC}"
        exit 1
    fi
fi

# =============================================================================
# Save run metadata (run-info.json)
# =============================================================================

SEED_VALUE="${SEED:-random}"
cat > "$OUTPUT_DIR/run-info.json" <<EOF
{
  "service": "$SERVICE_NAME",
  "role": "$USER_ROLE",
  "timestamp": "$RUN_TIMESTAMP",
  "max_time_seconds": $MAX_TIME,
  "rate_per_minute": $RATE_PER_MINUTE,
  "seed": "$SEED_VALUE",
  "swagger_url": "$SWAGGER_URL",
  "api_url": "$YAS_API_URL",
  "keycloak_url": "$KEYCLOAK_URL",
  "authenticated": $([ "$USER_ROLE" != "none" ] && echo "true" || echo "false"),
  "evomaster_version": "$EVOMASTER_VERSION",
  "evomaster_image": "$EVOMASTER_IMAGE"
}
EOF

echo -e "${GREEN}✓ Metadata saved to run-info.json${NC}"

# =============================================================================
# Run EvoMaster via Docker
# =============================================================================

echo -e "\n${GREEN}Running EvoMaster...${NC}"
echo -e "${YELLOW}Swagger URL:${NC} $SWAGGER_URL"
echo -e "${YELLOW}Log:${NC}         $LOG_FILE"
echo ""

TEST_SUITE_NAME="EvoMaster_${SERVICE_NAME}_${USER_ROLE}"

DOCKER_ARGS=(
    "run" "--rm"
    "-v" "$OUTPUT_DIR:/output"
    "--add-host=identity:host-gateway"
    "--add-host=api.yas.local:host-gateway"
    "$EVOMASTER_IMAGE"
    "--blackBox" "true"
    "--bbSwaggerUrl" "$SWAGGER_URL"
    "--maxTime" "${MAX_TIME}s"
    "--ratePerMinute" "$RATE_PER_MINUTE"
    "--outputFormat" "JAVA_JUNIT_5"
    "--outputFolder" "/output"
    "--testSuiteFileName" "$TEST_SUITE_NAME"
    "--writeStatistics" "true"
    "--statisticsFile" "/output/statistics.csv"
    "--snapshotInterval" "1"
)

if [ -n "$SEED" ]; then
    DOCKER_ARGS+=("--seed" "$SEED")
fi

if [ -n "$AUTH_HEADER" ]; then
    DOCKER_ARGS+=("--header0" "$AUTH_HEADER")
    echo -e "${BLUE}Authentication header configured${NC}"
fi

# Run and stream output to log simultaneously
docker "${DOCKER_ARGS[@]}" 2>&1 | tee "$LOG_FILE"
EXIT_CODE=${PIPESTATUS[0]}

# =============================================================================
# Extract coverage summary from log and append to run-info.json
# =============================================================================

if [ $EXIT_CODE -eq 0 ] && [ -f "$LOG_FILE" ]; then
    COVERED=$(grep -oP "Covered targets: \K[0-9]+" "$LOG_FILE" | tail -1 || echo "unknown")
    ENDPOINTS_2XX=$(grep -oP "Successfully executed \(HTTP code 2xx\) \K[0-9]+ endpoints out of [0-9]+" "$LOG_FILE" | tail -1 || echo "unknown")
    TESTS_GENERATED=$(grep -oP "Going to save \K[0-9]+" "$LOG_FILE" | tail -1 || echo "unknown")

    # Append results after evomaster_image line (avoid sed escaping issues with image names)
    TMP=$(mktemp)
    while IFS= read -r line || [ -n "$line" ]; do
        if [[ "$line" == *"\"evomaster_image\""* ]]; then
            printf '%s,\n' "$line"
            printf '%s\n' '  "results": {'
            printf '%s\n' "    \"covered_targets\": \"$COVERED\","
            printf '%s\n' "    \"endpoints_2xx\": \"$ENDPOINTS_2XX\","
            printf '%s\n' "    \"tests_generated\": \"$TESTS_GENERATED\","
            printf '%s\n' "    \"exit_code\": $EXIT_CODE"
            printf '%s\n' '  }'
        else
            printf '%s\n' "$line"
        fi
    done < "$OUTPUT_DIR/run-info.json" > "$TMP"
    mv "$TMP" "$OUTPUT_DIR/run-info.json"
fi

# =============================================================================
# Final result
# =============================================================================

echo ""
if [ $EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}✓ Tests generated successfully!${NC}"
    echo -e "${YELLOW}Location:${NC} $OUTPUT_DIR"
    echo ""
    echo -e "${BLUE}Generated files:${NC}"
    ls "$OUTPUT_DIR" | while read -r f; do
        echo -e "  - $f"
    done
else
    echo -e "${RED}✗ EvoMaster failed (exit code: $EXIT_CODE)${NC}"
    echo -e "${YELLOW}Full log at:${NC} $LOG_FILE"
    exit $EXIT_CODE
fi
