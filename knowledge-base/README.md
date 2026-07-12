# YAS DevOps Knowledge Base

Tài liệu này là bản đồ học và vấn đáp của toàn repository YAS tại thời điểm kiểm kê. Phạm vi gồm 2.200 file được Git theo dõi, 4 PDF đầu vào chưa được track, source Java/Next.js, CI, container, Kubernetes, Helm, GitOps, Istio và observability.

## Cách đọc

1. [01-requirements-and-architecture.md](01-requirements-and-architecture.md): đề bài, báo cáo, kiến trúc và luồng end-to-end.
2. [02-ci-cd-containers.md](02-ci-cd-containers.md): GitHub Actions, Jenkins, Docker, Dockerfile, Compose và phân tích workflow thực tế.
3. [03-kubernetes-helm-gitops.md](03-kubernetes-helm-gitops.md): Kubernetes, Helm, Argo CD và từng field trọng yếu.
4. [04-istio-security-observability.md](04-istio-security-observability.md): Istio, security, metrics, logs, traces, Prometheus, Grafana, Loki, Tempo và Kiali.
5. [05-file-catalog.md](05-file-catalog.md): quy tắc phân tích từng file và inventory đầy đủ được tạo từ Git.
6. [06-viva-question-bank.md](06-viva-question-bank.md): câu hỏi vấn đáp, đáp án chuẩn và các bẫy thường gặp.
7. [07-viva-cheat-sheet.md](07-viva-cheat-sheet.md): cheat sheet 23 chủ đề và top 100 command phải nhớ.
8. [08-project-viva-question-bank-153.md](08-project-viva-question-bank-153.md): 153 câu vấn đáp theo 17 nhóm, ưu tiên tình huống thực tế của project.

## Phương pháp truy vết từng file

Mỗi file trong catalog có một `archetype`. Archetype trỏ đến bản phân tích 10 mục: vai trò, lý do tồn tại, workflow, quan hệ, logic, field quan trọng, hậu quả khi bỏ, hậu quả khi sửa, production practice và lỗi sinh viên. Các file đặc thù như workflow, chart nền, manifest Argo CD, Istio và observability được phân tích riêng trong các chương tương ứng. File source cùng vai trò không bị lặp lại một đoạn mô tả máy móc hàng trăm lần, nhưng vẫn có một dòng inventory riêng để không file nào bị bỏ sót.

## Kết luận kỹ thuật cần nhớ

- Project dùng GitHub Actions, không có Jenkinsfile được Git theo dõi. Vì vậy không được trả lời rằng Jenkins đang vận hành pipeline này. Jenkins chỉ là một phương án trong đề và phải được phân tích như phương án thay thế.
- CI hiện có hai lớp không hoàn toàn thống nhất: nhiều workflow riêng theo service phục vụ test/coverage/Sonar, còn `ci.yml` build toàn bộ matrix cho mọi push ngoài `main`. Điều này xung đột với yêu cầu monorepo chỉ chạy service thay đổi.
- Pipeline build Java dùng JDK 25 trong `ci.yml` và các workflow deploy, trong khi đề và composite action dùng Java 21. Đây là drift cần sửa.
- Các bước build image đang dùng `-DskipTests`. Vì vậy build image không thay thế quality gate của workflow CI.
- GitOps đúng về hướng pull-based: workflow cập nhật values, Argo CD theo dõi `main`, tự sync, prune và self-heal. Production nên tách source repo và config repo, dùng immutable digest, ký image và có promotion PR.
- Namespace `yas` bật mTLS `STRICT`; `dev` và `staging` đang `PERMISSIVE`. Không được tuyên bố toàn bộ môi trường đều strict.
- `AuthorizationPolicy` của product cho phép 5 caller thực tế, không phải chỉ order. File demo riêng mới thể hiện kịch bản order-only.
- Cấu hình local Loki/Tempo dùng filesystem và retention ngắn, phù hợp lab, không phù hợp production.
