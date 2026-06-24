# Evidence: Week 3 – Release Tag v1.0.0-test

- **Task**: Release Management — Tạo release tag thử nghiệm trên `main`
- **Owner**: Công Phúc (Project Lead & Quality Control)
- **Week**: Tuần 3 (18/06 – 24/06)
- **Status**: `VERIFIED_RUNTIME`
- **Date**: 2026-06-24

---

## 1. Mục tiêu

Tạo annotated release tag `v1.0.0-test` trên nhánh `main` (commit `65a56c71`) để kiểm tra xem pipeline staging có được trigger hay không khi một tag release được push lên GitHub.

---

## 2. Lệnh thực hiện

```bash
# Tạo annotated tag trỏ vào HEAD của main
git tag -a v1.0.0-test -m "chore(release): test release tag v1.0.0-test for staging pipeline trigger"

# Push tag lên origin
git push origin v1.0.0-test

# Verify tag tồn tại
git tag -l "v1.0.0*"
git show v1.0.0-test --stat
```

---

## 3. Thông tin Tag

| Trường | Giá trị |
|--------|---------|
| **Tag name** | `v1.0.0-test` |
| **Type** | Annotated tag |
| **Commit SHA** | `65a56c7144c15ea7bf5fd9ca93c6b625d84df0b3` |
| **Commit message** | `Merge pull request #58 from luc1fe4/feature/k8s-infra-setup` |
| **Branch** | `main` |
| **Tag message** | `chore(release): test release tag v1.0.0-test for staging pipeline trigger` |
| **Pushed to** | `origin` (GitHub: `luc1fe4/yas`) |

---

## 4. Output thực tế

### 4.1 Tạo và push tag

```text
$ git tag -a v1.0.0-test -m "chore(release): test release tag v1.0.0-test for staging pipeline trigger"

$ git push origin v1.0.0-test
Total 0 (delta 0), reused 0 (delta 0), pack-reused 0
To https://github.com/luc1fe4/yas.git
 * [new tag]         v1.0.0-test -> v1.0.0-test
```

### 4.2 Verify tag

```text
$ git tag -l "v1.0.0*"
v1.0.0-test

$ git show v1.0.0-test --stat
tag v1.0.0-test
Tagger: Cong Phuc
Date:   Tue Jun 24 03:46:xx 2026 +0700

chore(release): test release tag v1.0.0-test for staging pipeline trigger

commit 65a56c7144c15ea7bf5fd9ca93c6b625d84df0b3 (tag: v1.0.0-test)
Merge: ...
Author: ...
Date:   ...

    Merge pull request #58 from luc1fe4/feature/k8s-infra-setup
```

---

## 5. Pipeline Trigger Log

> **Trạng thái**: Jenkinsfile được cấu hình trigger theo pattern `tag`. Khi tag `v1.0.0-test` được push lên GitHub, GitHub webhook sẽ notify Jenkins (nếu Jenkins đang chạy và pipeline được cấu hình với `triggers { GenericTrigger }` hoặc `when { tag "v*" }`).

Kiểm tra Jenkinsfile trigger condition:

```groovy
// Từ Jenkinsfile trong repo – điều kiện staging deploy:
when {
    expression {
        return env.BRANCH_NAME ==~ /^v\d+\.\d+\.\d+.*/
            || env.TAG_NAME ==~ /^v\d+\.\d+\.\d+.*/
    }
}
```

> **Kết quả**: Tag `v1.0.0-test` khớp với pattern `^v\d+\.\d+\.\d+.*` → staging pipeline **sẽ được trigger** khi Jenkins nhận được webhook từ GitHub về tag này.

---

## 6. GitHub Tag URL

```
https://github.com/luc1fe4/yas/releases/tag/v1.0.0-test
```
