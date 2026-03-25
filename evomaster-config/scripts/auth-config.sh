#!/bin/bash

# Authentication configuration for EvoMaster on YAS (Yet Another Shop)
# YAS uses Keycloak as identity provider (OAuth2/OIDC)

# ============================================
# KEYCLOAK SETTINGS
# ============================================

export KEYCLOAK_URL="${KEYCLOAK_URL:-http://identity}"
export KEYCLOAK_REALM="Yas"
export KEYCLOAK_TOKEN_URL="${KEYCLOAK_URL}/realms/${KEYCLOAK_REALM}/protocol/openid-connect/token"
export KEYCLOAK_ADMIN_URL="${KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}"

# Client used to obtain application user tokens
export KEYCLOAK_CLIENT_ID="backoffice-bff"
export KEYCLOAK_CLIENT_SECRET="TVacLC0cQ8tiiEKiTVerTb2YvwQ1TRJF"

# API base URL
export YAS_API_URL="${YAS_API_URL:-http://api.yas.local}"

# Optional --resolve flags for curl (set by evomaster-blackbox.sh)
declare -a CURL_HOST_RESOLVE=()

# ============================================
# USER CREDENTIALS
# ============================================

# ADMIN user (roles: ADMIN + CUSTOMER via default-roles-yas)
export ADMIN_USERNAME="admin"
export ADMIN_PASSWORD="password"

# CUSTOMER user (role: CUSTOMER only via default-roles-yas)
# Created via Keycloak Admin API if it does not exist
export CUSTOMER_USERNAME="user"
export CUSTOMER_PASSWORD="password"

# Keycloak Admin Console credentials (master realm)
export KC_ADMIN_USERNAME="admin"
export KC_ADMIN_PASSWORD="admin"

# ============================================
# AUTHENTICATION FUNCTIONS
# ============================================

# Obtain an OAuth2 token via Resource Owner Password Credentials (ROPC)
# Usage: get_token <username> <password>
get_token() {
    local username="$1"
    local password="$2"

    local response
    response=$(curl -s "${CURL_HOST_RESOLVE[@]}" -X POST "$KEYCLOAK_TOKEN_URL" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "grant_type=password" \
        -d "client_id=${KEYCLOAK_CLIENT_ID}" \
        -d "client_secret=${KEYCLOAK_CLIENT_SECRET}" \
        -d "username=${username}" \
        -d "password=${password}")

    local token
    token=$(echo "$response" | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)

    if [ -z "$token" ]; then
        echo "Error obtaining token for ${username}. Response: $response" >&2
        return 1
    fi

    echo "$token"
}

get_admin_token() {
    get_token "$ADMIN_USERNAME" "$ADMIN_PASSWORD"
}

get_customer_token() {
    get_token "$CUSTOMER_USERNAME" "$CUSTOMER_PASSWORD"
}

# ============================================
# CUSTOMER USER MANAGEMENT
# ============================================

# Ensures the CUSTOMER user exists in Keycloak.
# Creates it via Admin API if missing — CUSTOMER role is assigned automatically
# via the default-roles-yas composite role.
ensure_customer_user_exists() {
    echo "Checking user '${CUSTOMER_USERNAME}' in Keycloak..."

    # Obtain admin token from master realm
    local kc_admin_token
    kc_admin_token=$(curl -s "${CURL_HOST_RESOLVE[@]}" -X POST "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "grant_type=password" \
        -d "client_id=admin-cli" \
        -d "username=${KC_ADMIN_USERNAME}" \
        -d "password=${KC_ADMIN_PASSWORD}" | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)

    if [ -z "$kc_admin_token" ]; then
        echo "Error: could not obtain Keycloak admin token" >&2
        return 1
    fi

    # Check if user already exists
    local users_response
    users_response=$(curl -s "${CURL_HOST_RESOLVE[@]}" \
        -H "Authorization: Bearer $kc_admin_token" \
        "${KEYCLOAK_ADMIN_URL}/users?username=${CUSTOMER_USERNAME}&exact=true")

    local user_count
    user_count=$(echo "$users_response" | grep -o '"id"' | wc -l)

    if [ "$user_count" -gt 0 ]; then
        echo "✓ User '${CUSTOMER_USERNAME}' already exists"
        return 0
    fi

    echo "Creating user '${CUSTOMER_USERNAME}' with CUSTOMER role..."

    local http_code
    http_code=$(curl -s "${CURL_HOST_RESOLVE[@]}" -o /dev/null -w "%{http_code}" \
        -X POST "${KEYCLOAK_ADMIN_URL}/users" \
        -H "Authorization: Bearer $kc_admin_token" \
        -H "Content-Type: application/json" \
        -d "{
            \"username\": \"${CUSTOMER_USERNAME}\",
            \"enabled\": true,
            \"credentials\": [{
                \"type\": \"password\",
                \"value\": \"${CUSTOMER_PASSWORD}\",
                \"temporary\": false
            }]
        }")

    if [ "$http_code" = "201" ]; then
        echo "✓ User '${CUSTOMER_USERNAME}' created (CUSTOMER role assigned automatically via default-roles-yas)"
    else
        echo "Error creating user (HTTP $http_code)" >&2
        return 1
    fi
}
