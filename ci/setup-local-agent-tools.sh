#!/usr/bin/env bash
# Cài công cụ đồng bộ cho dev / Jenkins agent (Ubuntu/Debian).
# Chạy: bash ci/setup-local-agent-tools.sh
# Nếu thiếu quyền: sudo bash ci/setup-local-agent-tools.sh

set -euo pipefail

if [[ "${EUID}" -ne 0 ]]; then
  echo "Cần quyền root (sudo). Chạy: sudo $0" >&2
  exit 1
fi

apt-get update -qq
apt-get install -y openjdk-21-jdk git maven docker.io

# Cho user hiện tại (SUDO_USER) dùng docker không cần sudo
if [[ -n "${SUDO_USER:-}" && "${SUDO_USER}" != "root" ]]; then
  usermod -aG docker "${SUDO_USER}"
  echo "Đã thêm ${SUDO_USER} vào group docker. Đăng xuất/đăng nhập lại hoặc: newgrp docker"
fi

echo "----"
java -version
echo "----"
mvn -version | head -2
echo "----"
git --version
docker --version
