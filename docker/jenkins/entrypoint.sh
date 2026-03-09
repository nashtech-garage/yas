#!/bin/bash
# Fix Docker socket group at runtime so the jenkins user can access it.
# The GID of /var/run/docker.sock on the host may differ from the image's docker group.

DOCKER_SOCK=/var/run/docker.sock

if [ -S "$DOCKER_SOCK" ]; then
    DOCKER_GID=$(stat -c '%g' "$DOCKER_SOCK")
    # If docker group in the container doesn't match, update it
    if ! getent group "$DOCKER_GID" > /dev/null 2>&1; then
        # Run as root to fix GID (requires container to start briefly as root)
        groupmod -g "$DOCKER_GID" docker 2>/dev/null || true
    fi
    # Make sure jenkins user is in the docker group
    usermod -aG docker jenkins 2>/dev/null || true
    chmod 666 "$DOCKER_SOCK" 2>/dev/null || true
fi

# Start Jenkins using the official entrypoint
exec /usr/bin/tini -- /usr/local/bin/jenkins.sh "$@"
