# 🟩 Phạm Quang Vinh – Security Scanning & Infrastructure

> **Vai trò:** Dựng hạ tầng Jenkins/SonarQube, tích hợp Gitleaks/SonarQube/Snyk  
> **Phối hợp:** Vinh.NL (pipeline), Trí (test results)

---

## Phase 1: Setup & Foundation (Tuần 1)

### B1. Cài đặt Jenkins server
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 1-2 | **Status:** ⬜
- Dùng Docker:
  ```bash
  docker run -d --name jenkins \
    -p 8080:8080 -p 50000:50000 \
    -v jenkins_home:/var/jenkins_home \
    jenkins/jenkins:lts
  ```
- Lấy password: `docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword`
- Cài suggested plugins, tạo admin account
- **Output:** Jenkins chạy tại `http://localhost:8080`

### B2. Cấu hình Jenkins plugins
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 2 | **Status:** ⬜
- Plugins cần cài: GitHub Branch Source, Pipeline, JUnit, JaCoCo, SonarQube Scanner, Docker Pipeline
- Chụp screenshot danh sách plugins
- **Output:** Jenkins sẵn sàng

### B3. Cài đặt SonarQube server
- **Ưu tiên:** 🟡 TB | **Deadline:** Ngày 3-4 | **Status:** ⬜
- Docker Compose:
  ```yaml
  services:
    sonarqube:
      image: sonarqube:community
      ports: ["9000:9000"]
      environment: [SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true]
  ```
- Login mặc định: `admin/admin`, tạo project + token
- **Output:** SonarQube tại `http://localhost:9000`

---

## Phase 2: CI Pipeline Core (Tuần 2)

### B4. Tích hợp Gitleaks vào pipeline
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 1-2 | **Status:** ⬜
- GitHub Actions:
  ```yaml
  - name: Gitleaks Scan
    uses: gitleaks/gitleaks-action@v2
    env:
      GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
  ```
- Jenkins:
  ```groovy
  stage('Gitleaks') {
    steps { sh 'docker run --rm -v $(pwd):/repo zricethezav/gitleaks:latest detect --source /repo' }
  }
  ```
- Gitleaks chạy **đầu tiên** trong pipeline
- Test: commit fake secret → verify phát hiện
- **Output:** Gitleaks chạy, phát hiện secret leak

### B5. Tích hợp SonarQube vào pipeline
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 2-3 | **Status:** ⬜
- GitHub Actions: dùng `SonarSource/sonarqube-scan-action@v5`
- Jenkins: cấu hình SonarQube server trong Global Config, dùng `withSonarQubeEnv`
- Tạo `sonar-project.properties` ở root repo
- **Output:** Code quality report trên SonarQube dashboard

### B6. Tích hợp Snyk vào pipeline
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 3-4 | **Status:** ⬜
- Đăng ký free tier tại [snyk.io](https://snyk.io), lấy SNYK_TOKEN
- GitHub Actions:
  ```yaml
  - name: Snyk Scan
    uses: snyk/actions/maven@master
    env:
      SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
    with:
      args: --severity-threshold=high
  ```
- **Output:** Dependency vulnerability scan hoạt động

### B7. Cấu hình SonarQube Quality Gate
- **Ưu tiên:** 🟡 TB | **Deadline:** Ngày 4 | **Status:** ⬜
- Tạo Quality Gate: Coverage < 70% → Fail, Security Rating worse than A → Fail
- **Output:** Quality Gate tự động pass/fail

---

## Phase 3: Advanced & Báo cáo (Tuần 3)

### B8. Review & fix issues từ SonarQube/Snyk
- **Ưu tiên:** 🟡 TB | **Deadline:** Ngày 1-3 | **Status:** ⬜
- Fix 5-10 issues quan trọng nhất, chụp screenshot before/after

### B9. Chụp screenshots tất cả cấu hình
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 3-4 | **Status:** ⬜
- Checklist screenshots:
  - [ ] Jenkins Dashboard + Job config
  - [ ] Pipeline console output (pass)
  - [ ] GitHub Branch Protection Rules
  - [ ] Gitleaks scan result (pass + fail case)
  - [ ] SonarQube Dashboard + Quality Gate
  - [ ] Snyk vulnerability report
  - [ ] GitHub PR với CI checks
  - [ ] JaCoCo Coverage report
- Lưu vào `screenshots/`, gửi Vinh.NL

### B10. Viết phần Security trong báo cáo
- **Ưu tiên:** 🟡 TB | **Deadline:** Ngày 4 | **Status:** ⬜
- Mô tả cách tích hợp Gitleaks, SonarQube, Snyk, gửi cho Vinh.NL

---

## 📋 Tiến độ

| Phase | Done | Total | Progress |
|-------|------|-------|----------|
| Phase 1 | 0 | 3 | ░░░░░░░░░░ 0% |
| Phase 2 | 0 | 4 | ░░░░░░░░░░ 0% |
| Phase 3 | 0 | 3 | ░░░░░░░░░░ 0% |
| **Tổng** | **0** | **10** | ░░░░░░░░░░ **0%** |
