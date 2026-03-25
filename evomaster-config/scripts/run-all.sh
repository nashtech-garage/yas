#!/bin/bash

# =============================================================================
# Run EvoMaster in black-box mode for all YAS services
# =============================================================================
# Usage:
#   ./run-all.sh [role]
#
# Examples:
#   ./run-all.sh                 # runs admin + customer + none for all services
#   ./run-all.sh admin           # runs with admin role only (ADMIN + CUSTOMER)
#   ./run-all.sh admin_only      # runs with ADMIN-only token for all services
#   ./run-all.sh customer        # runs with customer role only
#   ./run-all.sh none            # runs without authentication only
#
# Environment variables (forwarded to evomaster-blackbox.sh):
#   EVOMASTER_MAX_TIME   Time per service in seconds (default: 60)
#   EVOMASTER_RATE       Requests per minute (default: 60)
#   EVOMASTER_SEED       Seed for reproducibility (default: random)
# =============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

# Services to test (in order)
SERVICES=(product media customer cart rating order payment location inventory tax promotion search sampledata)

# Roles to test — if a specific role is passed, use only that one; otherwise run all three
if [ -n "$1" ]; then
    ROLES=("$1")
else
    ROLES=(admin customer none)
fi

MAX_TIME="${EVOMASTER_MAX_TIME:-60}"
EXPERIMENT_START="$(date +%Y%m%d_%H%M%S)"

echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  EvoMaster — Full YAS Experiment${NC}"
echo -e "${BLUE}============================================${NC}"
echo -e "${YELLOW}Start:${NC}            $EXPERIMENT_START"
echo -e "${YELLOW}Services:${NC}         ${#SERVICES[@]}"
echo -e "${YELLOW}Roles:${NC}            ${ROLES[*]}"
echo -e "${YELLOW}Time per service:${NC} ${MAX_TIME}s"
echo -e "${YELLOW}Estimated total:${NC}  $(( ${#SERVICES[@]} * ${#ROLES[@]} * MAX_TIME / 60 )) min"
echo ""

# Experiment summary file
OUTPUT_BASE_DIR="$SCRIPT_DIR/../generated-tests/blackbox"
mkdir -p "$OUTPUT_BASE_DIR"
SUMMARY_FILE="$OUTPUT_BASE_DIR/experiment_${EXPERIMENT_START}.jsonl"

echo -e "${YELLOW}Summary file:${NC}     $SUMMARY_FILE"
echo ""

TOTAL=0
FAILED=0
SUCCEEDED=0

for ROLE in "${ROLES[@]}"; do
    for SERVICE in "${SERVICES[@]}"; do
        TOTAL=$((TOTAL + 1))
        echo -e "${BLUE}--- [$TOTAL] $SERVICE / $ROLE ---${NC}"

        START_TIME=$(date +%s)

        if bash "$SCRIPT_DIR/evomaster-blackbox.sh" "$SERVICE" "$ROLE"; then
            STATUS="success"
            SUCCEEDED=$((SUCCEEDED + 1))
            echo -e "${GREEN}✓ $SERVICE/$ROLE completed${NC}\n"
        else
            STATUS="failed"
            FAILED=$((FAILED + 1))
            echo -e "${RED}✗ $SERVICE/$ROLE failed${NC}\n"
        fi

        END_TIME=$(date +%s)
        ELAPSED=$((END_TIME - START_TIME))

        # Append entry to the JSONL summary
        echo "{\"service\": \"$SERVICE\", \"role\": \"$ROLE\", \"status\": \"$STATUS\", \"elapsed_seconds\": $ELAPSED}" >> "$SUMMARY_FILE"
    done
done

# =============================================================================
# Final summary
# =============================================================================

EXPERIMENT_END="$(date +%Y%m%d_%H%M%S)"

echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  Experiment complete${NC}"
echo -e "${BLUE}============================================${NC}"
echo -e "${YELLOW}Start:${NC}     $EXPERIMENT_START"
echo -e "${YELLOW}End:${NC}       $EXPERIMENT_END"
echo -e "${YELLOW}Total:${NC}     $TOTAL runs"
echo -e "${GREEN}Success:${NC}   $SUCCEEDED"
if [ $FAILED -gt 0 ]; then
    echo -e "${RED}Failed:${NC}    $FAILED"
fi
echo -e "${YELLOW}Summary:${NC}   $SUMMARY_FILE"
echo -e "${YELLOW}Tests:${NC}     $OUTPUT_BASE_DIR/"
