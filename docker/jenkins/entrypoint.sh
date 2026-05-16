#!/bin/bash

# Fix docker.sock permission mỗi lần container khởi động
# (socket được mount từ host → permission có thể thay đổi)
if [ -S /var/run/docker.sock ]; then
    # Chạy bằng sudo (USER jenkins nhưng có quyền qua ENTRYPOINT)
    sudo chmod 666 /var/run/docker.sock 2>/dev/null || true
fi

# Chạy Jenkins bình thường
exec /usr/local/bin/jenkins.sh "$@"
