# 🟦 Nguyễn Lê Thế Vinh (Leader) – CI Pipeline & GitHub Config

> **Vai trò:** Leader, phụ trách pipeline CI chính, cấu hình GitHub, tổng hợp báo cáo  
> **Liên hệ phối hợp:** Vinh.PQ (hạ tầng Jenkins), Trí (test & coverage)

---

## Phase 1: Setup & Foundation (Tuần 1)

### A1. Fork repo YAS
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 1 | **Status:** ⬜
- **Mô tả:** Fork repo [https://github.com/nashtech-garage/yas](https://github.com/nashtech-garage/yas) về tài khoản GitHub nhóm
- **Chi tiết:**
  - Tạo organization hoặc dùng tài khoản cá nhân
  - Fork toàn bộ repo (bao gồm tất cả branches)
  - Add tất cả thành viên nhóm làm collaborator (quyền Write)
- **Output:** Link repo đã fork

### A2. Cấu hình Branch Protection Rules
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 2 | **Status:** ⬜
- **Mô tả:** Cấu hình để không cho push trực tiếp vào main branch
- **Chi tiết:**
  - Vào Settings → Branches → Add branch protection rule
  - Branch name pattern: `main`
  - ✅ Require a pull request before merging
  - ✅ Require approvals: **2** reviewers
  - ✅ Require status checks to pass before merging (chọn CI job sau khi có pipeline)
  - ✅ Do not allow bypassing the above settings
  - Chụp screenshot cấu hình
- **Output:** Screenshot branch protection rules

### A3. Kết nối Jenkins/GitHub Actions với repo
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 3 | **Status:** ⬜
- **Phụ thuộc:** Vinh.PQ hoàn thành cài Jenkins (B1)
- **Chi tiết:**
  - **Nếu dùng Jenkins:**
    - Tạo GitHub Personal Access Token (PAT) với quyền `repo`, `admin:repo_hook`
    - Cấu hình Jenkins credentials (GitHub token)
    - Tạo Multibranch Pipeline job trong Jenkins
    - Cấu hình webhook GitHub → Jenkins
  - **Nếu dùng GitHub Actions:**
    - Tạo secrets trong repo (SONAR_TOKEN, SNYK_TOKEN...)
    - Không cần webhook (tự động trigger)
- **Output:** Jenkins job quét được repo / GitHub Actions trigger được

### A4. Tạo pipeline cơ bản
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 3-4 | **Status:** ⬜
- **Chi tiết:**
  - **Nếu Jenkins:** Tạo `Jenkinsfile` ở root repo
  - **Nếu GitHub Actions:** Tạo `.github/workflows/ci.yml`
  - Pipeline đơn giản: checkout code → echo "Hello CI"
  - Commit vào branch mới, tạo PR test thử
  - Verify pipeline chạy tự động khi push
- **Output:** Pipeline chạy thành công lần đầu

---

## Phase 2: CI Pipeline Core (Tuần 2)

### A5. Viết pipeline CI 2 phase: Test → Build
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 1-2 | **Status:** ⬜
- **Chi tiết:**
  - **Phase Test:**
    ```yaml
    # Ví dụ GitHub Actions
    - name: Run Tests
      run: mvn test -pl <service-name>
    ```
  - **Phase Build:**
    ```yaml
    - name: Build
      run: mvn package -pl <service-name> -DskipTests
    ```
  - Đảm bảo Build chỉ chạy khi Test pass
- **Output:** Pipeline 2 phase chạy thành công

### A6. Upload JUnit test result
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 2 | **Status:** ⬜
- **Chi tiết:**
  - **GitHub Actions:** Dùng `dorny/test-reporter` hoặc `mikepenz/action-junit-report`
    ```yaml
    - name: Publish Test Results
      uses: dorny/test-reporter@v1
      with:
        name: Tests
        path: '**/surefire-reports/*.xml'
        reporter: java-junit
    ```
  - **Jenkins:** Dùng `junit` step trong Jenkinsfile
    ```groovy
    post { always { junit '**/surefire-reports/*.xml' } }
    ```
- **Output:** Test result hiển thị trong PR/Jenkins job

### A7. Upload JaCoCo coverage report
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 2-3 | **Status:** ⬜
- **Chi tiết:**
  - Thêm JaCoCo plugin vào `pom.xml` service (nếu chưa có):
    ```xml
    <plugin>
      <groupId>org.jacoco</groupId>
      <artifactId>jacoco-maven-plugin</artifactId>
      <version>0.8.11</version>
    </plugin>
    ```
  - Upload report trong pipeline
  - Chụp screenshot coverage report
- **Output:** Coverage report hiển thị sau mỗi build

### A8. Cấu hình path filter cho monorepo
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 3-4 | **Status:** ⬜
- **Phối hợp:** Trí (cung cấp danh sách service + đường dẫn)
- **Chi tiết:**
  - **GitHub Actions:** Dùng `dorny/paths-filter`
    ```yaml
    - uses: dorny/paths-filter@v3
      id: changes
      with:
        filters: |
          media: 'media/**'
          product: 'product/**'
          cart: 'cart/**'
    ```
  - **Jenkins:** Dùng `changeset` hoặc `when { changeset "media/**" }`
  - Mỗi service chỉ build khi thư mục tương ứng thay đổi
  - Test bằng cách tạo commit chỉ thay đổi 1 service
- **Output:** Chỉ service thay đổi được trigger build

---

## Phase 3: Advanced & Báo cáo (Tuần 3)

### A9. Cấu hình fail khi coverage < 70%
- **Ưu tiên:** 🟡 TB | **Deadline:** Ngày 1-2 | **Status:** ⬜
- **Chi tiết:**
  - Cấu hình JaCoCo minimum coverage threshold:
    ```xml
    <rule>
      <element>BUNDLE</element>
      <limit>
        <counter>LINE</counter>
        <value>COVEREDRATIO</value>
        <minimum>0.70</minimum>
      </limit>
    </rule>
    ```
  - Hoặc check trong pipeline script
  - Test: tạo commit với test ít → verify pipeline fail
- **Output:** Pipeline tự động fail nếu coverage < 70%

### A10. Viết file báo cáo .docx
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 4-5 | **Status:** ⬜
- **Chi tiết:**
  - Tổng hợp screenshots từ Vinh.PQ
  - Nội dung báo cáo:
    1. Link GitHub repo nhóm
    2. Mô tả pipeline CI (các phase, tool)
    3. Screenshots cấu hình Jenkins/GitHub Actions
    4. Screenshots Branch Protection
    5. Screenshots Gitleaks, SonarQube, Snyk
    6. Kết quả coverage
  - Đặt tên file: `<MSSV1>_<MSSV2>_<MSSV3>.docx` (sắp xếp MSSV tăng dần)
- **Output:** File `.docx` hoàn chỉnh sẵn sàng nộp

### A11. Review & dry-run cuối cùng
- **Ưu tiên:** 🔴 Cao | **Deadline:** Ngày 5 | **Status:** ⬜
- **Chi tiết:**
  - Chạy thử toàn bộ flow: tạo branch → commit → push → tạo PR → CI chạy
  - Verify: test pass, coverage upload, security scan, path filter
  - Đảm bảo có ≥ 1 PR đang open trên repo
  - Checklist cuối cùng trước khi nộp
- **Output:** Hệ thống CI hoàn chỉnh, sẵn sàng demo

---

## 📋 Tóm tắt tiến độ

| Phase | Done | Total | Progress |
|-------|------|-------|----------|
| Phase 1 | 0 | 4 | ░░░░░░░░░░ 0% |
| Phase 2 | 0 | 4 | ░░░░░░░░░░ 0% |
| Phase 3 | 0 | 3 | ░░░░░░░░░░ 0% |
| **Tổng** | **0** | **11** | ░░░░░░░░░░ **0%** |
