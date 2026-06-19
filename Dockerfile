# Sử dụng bản LTS JDK 21 theo yêu cầu đồ án
FROM jenkins/jenkins:lts-jdk21

USER root

# Cập nhật và cài đặt các công cụ cơ bản
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Tải và cài đặt Gitleaks (Yêu cầu 7c)
RUN wget https://github.com/gitleaks/gitleaks/releases/download/v8.18.2/gitleaks_8.18.2_linux_x64.tar.gz \
    && tar -xzf gitleaks_8.18.2_linux_x64.tar.gz \
    && mv gitleaks /usr/local/bin/gitleaks \
    && chmod +x /usr/local/bin/gitleaks \
    && rm gitleaks_8.18.2_linux_x64.tar.gz

# Trả lại quyền cho user jenkins để đảm bảo bảo mật
USER jenkins