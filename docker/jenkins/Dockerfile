FROM jenkins/jenkins:lts

# ── Chuyển sang root để cài đặt ──────────────────────────────────────────────
USER root

# ── Cài Docker CLI + sudo + thêm jenkins vào group docker ──────────────────
RUN apt-get update && \
    apt-get install -y docker.io sudo && \
    groupadd -f docker && \
    usermod -aG docker jenkins && \
    echo "jenkins ALL=(ALL) NOPASSWD: /usr/bin/chmod" >> /etc/sudoers.d/jenkins && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# ── Script tự fix permission docker.sock mỗi lần khởi động ─────────────────
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

# ── Quay lại user jenkins ───────────────────────────────────────────────────
USER jenkins

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
