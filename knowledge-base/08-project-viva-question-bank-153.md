# Ngân hàng 153 câu vấn đáp DevOps theo project YAS

Mỗi nhóm có 9 câu. Câu hỏi ưu tiên kiểm chứng manifest, workflow, runtime behavior và bằng chứng trong project, không chỉ nhắc lại định nghĩa.

# CI

========================

## Câu 1. Trong project, workflow nào build image cho feature branch và điểm chưa đạt yêu cầu monorepo là gì?

**Đáp án mẫu:** `.github/workflows/ci.yml` matrix build 14 image cho mọi push ngoài main; không có change detection nên một service đổi vẫn build tất cả.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nếu `common-library` đổi thì thiết kế path filter thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Mở trigger và matrix của `ci.yml`, chỉ ra số service được build.

**Điểm kiến thức cần nhớ:** CI theo monorepo phải tính cả dependency dùng chung.

**Lỗi sinh viên thường mắc:** Thấy có matrix rồi kết luận pipeline đã tối ưu monorepo.

========================

========================

## Câu 2. Vì sao bước Maven `-DskipTests clean package` trong pipeline build image không phải quality gate?

**Đáp án mẫu:** Nó đóng gói jar nhưng không chạy test; chất lượng phải được chặn bởi job test riêng có report và coverage threshold.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Làm sao bảo đảm job build image không chạy nếu test fail?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Phân biệt compile, test, package và upload report.

**Điểm kiến thức cần nhớ:** Artifact chỉ được phát hành sau quality gate.

**Lỗi sinh viên thường mắc:** Cho rằng package thành công đồng nghĩa test đã pass.

========================

========================

## Câu 3. Project đang drift Java version ở đâu?

**Đáp án mẫu:** Đề và composite action dùng Java 21, nhưng `ci.yml` cùng workflow deploy setup JDK 25.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Drift này có thể gây lỗi class version thế nào giữa build và runtime?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Tìm `setup-java` trong các workflow và root POM.

**Điểm kiến thức cần nhớ:** Build JDK, target bytecode và runtime JRE phải tương thích.

**Lỗi sinh viên thường mắc:** Chỉ nhìn một workflow rồi nói toàn project dùng Java 21.

========================

========================

## Câu 4. Workflow CI theo service cần publish những bằng chứng nào để đạt đề Project 1?

**Đáp án mẫu:** JUnit result, coverage report, kết quả quality gate và security scan; run xanh đơn thuần chưa đủ.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Coverage toàn reactor khác coverage service ra sao?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi vị trí artifact hoặc check trong run cụ thể.

**Điểm kiến thức cần nhớ:** Bằng chứng phải gắn với commit SHA.

**Lỗi sinh viên thường mắc:** Chụp trang Actions nhưng không có artifact hoặc threshold.

========================

========================

## Câu 5. Tại sao chỉ đặt `paths: product/**` có thể bỏ sót lỗi product?

**Đáp án mẫu:** Product còn phụ thuộc root POM, common-library, workflow và Docker base; thay đổi các phần đó cũng phải trigger product.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Hãy đề xuất dependency-aware change detection.

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Nêu một file ngoài `product/` có thể làm product build hỏng.

**Điểm kiến thức cần nhớ:** Path filter phải phản ánh graph phụ thuộc.

**Lỗi sinh viên thường mắc:** Đồng nhất thư mục service với toàn bộ dependency của service.

========================

========================

## Câu 6. Coverage trên 70% phải được enforce ở đâu?

**Đáp án mẫu:** Trong JaCoCo/Sonar quality gate hoặc step kiểm tra report, làm job fail trước merge.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Line coverage hay branch coverage phù hợp hơn và vì sao?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu chỉ ra exit code nào làm pipeline đỏ.

**Điểm kiến thức cần nhớ:** Upload coverage không đồng nghĩa enforce coverage.

**Lỗi sinh viên thường mắc:** Dashboard có 70% nhưng branch protection không yêu cầu check đó.

========================

========================

## Câu 7. Tại sao CI nên tạo image một lần rồi promote thay vì rebuild ở staging?

**Đáp án mẫu:** Cùng digest đã test được chuyển môi trường, tránh source/dependency thay đổi giữa các lần build.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nếu cần config khác môi trường thì xử lý ở đâu?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi cách chứng minh image staging chính là image đã test.

**Điểm kiến thức cần nhớ:** Build once, deploy many.

**Lỗi sinh viên thường mắc:** Rebuild cùng tag và cho rằng artifact vẫn giống nhau.

========================

========================

## Câu 8. `fail-fast: false` trong matrix có tác dụng gì?

**Đáp án mẫu:** Một service fail không hủy ngay các matrix job khác, giúp thu đủ kết quả nhưng dùng nhiều runner hơn.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Khi nào nên bật fail-fast?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu mô tả trạng thái các job còn lại khi một job lỗi.

**Điểm kiến thức cần nhớ:** Matrix failure policy là trade-off feedback và chi phí.

**Lỗi sinh viên thường mắc:** Nhầm với việc workflow vẫn thành công khi một job fail.

========================

========================

## Câu 9. Branch protection liên hệ với CI thế nào?

**Đáp án mẫu:** Protection yêu cầu đúng status checks và approvals trước merge; YAML không tự cấm push trực tiếp vào main.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nếu đổi tên job thì branch protection có thể xảy ra gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Chỉ ra cấu hình nào nằm ngoài repository.

**Điểm kiến thức cần nhớ:** Policy GitHub và workflow phải đồng bộ tên check.

**Lỗi sinh viên thường mắc:** Nói có workflow là đã bảo vệ main.

========================

# CD

========================

## Câu 10. Luồng CD dev của project bắt đầu và kết thúc ở đâu?

**Đáp án mẫu:** Push main build/push image, cập nhật values GitOps; Argo CD đọc Git và reconcile namespace dev.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Bước nào không nên trực tiếp giữ kubeconfig?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu vẽ producer artifact, Git commit và controller.

**Điểm kiến thức cần nhớ:** CI tạo artifact, GitOps controller deploy.

**Lỗi sinh viên thường mắc:** Nói workflow vừa update Git vừa Helm upgrade cùng resource.

========================

========================

## Câu 11. Developer build giải branch thành image nào?

**Đáp án mẫu:** `resolve_tag` dùng `git ls-remote` lấy commit SHA; riêng main hiện trả `latest`.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Vì sao main cũng nên resolve SHA?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Mở hàm `resolve_tag` và giải thích hai nhánh logic.

**Điểm kiến thức cần nhớ:** Feature environment phải truy vết được artifact.

**Lỗi sinh viên thường mắc:** Dùng branch name như image bất biến.

========================

========================

## Câu 12. Điểm rủi ro của tag `latest` trong developer environment là gì?

**Đáp án mẫu:** Mutable, race giữa build/deploy, cache khó đoán và rollback không rõ.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Pin digest thay tag sẽ thay values thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi Pod đang chạy digest nào bằng jsonpath.

**Điểm kiến thức cần nhớ:** Desired state nên bất biến.

**Lỗi sinh viên thường mắc:** Cho rằng `imagePullPolicy: Always` giải quyết truy vết.

========================

========================

## Câu 13. Cleanup developer environment đang làm gì và rủi ro gì?

**Đáp án mẫu:** Workflow xóa toàn namespace `yas-developer`; dọn sạch tốt nhưng blast radius lớn nếu biến/context sai.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Thêm guard nào trước lệnh delete?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu xác nhận namespace, context và label ownership.

**Điểm kiến thức cần nhớ:** Destructive CD cần validation và approval.

**Lỗi sinh viên thường mắc:** Dùng namespace biến rỗng hoặc nhầm cluster.

========================

========================

## Câu 14. Profile `lean` khác `full` trong developer CD thế nào?

**Đáp án mẫu:** Lean tắt Istio injection và hạ resources; full bật injection.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Vì sao lean không dùng làm bằng chứng service mesh?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Chỉ ra label namespace và pod annotation liên quan.

**Điểm kiến thức cần nhớ:** Môi trường tiết kiệm có thể khác hành vi production.

**Lỗi sinh viên thường mắc:** Demo mTLS trên namespace không có sidecar.

========================

========================

## Câu 15. Staging nên trigger bằng gì theo đề?

**Đáp án mẫu:** Release tag như `v1.2.3`, sau đó dùng artifact bất biến tương ứng và cập nhật staging desired state.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Làm sao chặn tag không đúng SemVer?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Phân biệt branch main và tag ref trong GitHub Actions.

**Điểm kiến thức cần nhớ:** Staging là promotion có version.

**Lỗi sinh viên thường mắc:** Deploy staging bằng `latest`.

========================

========================

## Câu 16. Nếu deployment dev và staging chạy đồng thời, cần kiểm soát gì?

**Đáp án mẫu:** Concurrency theo environment, quyền write Git, conflict values và thứ tự commit/promotion.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Thiết kế concurrency group cụ thể.

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi điều gì xảy ra nếu hai bot push cùng branch.

**Điểm kiến thức cần nhớ:** Một environment chỉ nên có một luồng mutate desired state tại một thời điểm.

**Lỗi sinh viên thường mắc:** Chỉ dựa vào thời gian chạy ngắn để tránh race.

========================

========================

## Câu 17. Rollback CD theo GitOps khác rollback Helm trực tiếp thế nào?

**Đáp án mẫu:** Revert desired state trong Git để Argo CD reconcile; Helm rollback trực tiếp có thể bị self-heal đảo lại.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Khi migration DB không backward-compatible thì rollback ra sao?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi nguồn sự thật sau rollback là Git hay cluster.

**Điểm kiến thức cần nhớ:** Rollback phải nhất quán với source of truth.

**Lỗi sinh viên thường mắc:** Chạy `kubectl rollout undo` rồi coi là hoàn tất.

========================

========================

## Câu 18. Tại sao cần smoke test sau sync?

**Đáp án mẫu:** Synced chỉ xác nhận desired/live match, không chứng minh route và business flow hoạt động.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Smoke test nào tối thiểu cho YAS?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu phân biệt health probe với user journey.

**Điểm kiến thức cần nhớ:** CD kết thúc bằng verification, không chỉ apply.

**Lỗi sinh viên thường mắc:** Chỉ kiểm tra Pod Running.

========================

# Docker

========================

## Câu 19. Dockerfile backend phụ thuộc bước nào trước Docker build?

**Đáp án mẫu:** Workflow Maven phải tạo jar đúng path trong build context rồi Dockerfile mới COPY được.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Multi-stage build Maven trong Docker có trade-off gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Xóa jar và hỏi build sẽ fail ở instruction nào.

**Điểm kiến thức cần nhớ:** Build context và artifact contract phải khớp.

**Lỗi sinh viên thường mắc:** Chạy Docker build trực tiếp nhưng jar chưa tồn tại.

========================

========================

## Câu 20. Tại sao production không nên chạy container bằng root?

**Đáp án mẫu:** Nếu process bị khai thác, quyền trong container và khả năng tác động kernel/mount lớn hơn; nên USER non-root và drop capabilities.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Read-only root filesystem cần chuẩn bị writable path nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi UID thực tế bằng lệnh nào.

**Điểm kiến thức cần nhớ:** Container không phải security boundary tuyệt đối.

**Lỗi sinh viên thường mắc:** Cho rằng namespace đã đủ bảo mật.

========================

========================

## Câu 21. `EXPOSE` trong Dockerfile có tạo public port không?

**Đáp án mẫu:** Không, chỉ metadata; publish do `docker -p`, Compose ports hoặc Kubernetes Service/Ingress.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Service targetPort phải khớp gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu chỉ ra nơi project thực sự expose port.

**Điểm kiến thức cần nhớ:** Phân biệt container port với publish/routing.

**Lỗi sinh viên thường mắc:** Nói EXPOSE mở firewall.

========================

========================

## Câu 22. Vì sao exec-form ENTRYPOINT tốt hơn shell-form?

**Đáp án mẫu:** App nhận signal trực tiếp, hỗ trợ graceful shutdown và exit code đúng.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** PID 1 có đặc điểm signal/zombie gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi process nào nhận SIGTERM khi Pod terminate.

**Điểm kiến thức cần nhớ:** Signal handling quyết định rollout an toàn.

**Lỗi sinh viên thường mắc:** Dùng `sh -c` nhưng không exec app.

========================

========================

## Câu 23. Vì sao không dùng `ARG` để truyền secret build?

**Đáp án mẫu:** ARG có thể lộ trong history, cache hoặc provenance; dùng BuildKit secret mount và secret manager.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nếu secret đã vào layer rồi xóa ở layer sau có an toàn không?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu inspect image history.

**Điểm kiến thức cần nhớ:** Secret không được trở thành image layer.

**Lỗi sinh viên thường mắc:** Cho rằng xóa file ở RUN sau làm secret biến mất khỏi history.

========================

========================

## Câu 24. UI Next.js có bẫy gì với environment variable?

**Đáp án mẫu:** Biến public có thể được bake lúc build; đổi ConfigMap runtime không đổi bundle nếu app không có runtime config mechanism.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Thiết kế runtime config cho một image nhiều môi trường.

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi biến được đọc ở build server hay browser runtime.

**Điểm kiến thức cần nhớ:** Phân biệt build-time và runtime configuration.

**Lỗi sinh viên thường mắc:** Chỉ set env trong Deployment rồi mong JS bundle đổi.

========================

========================

## Câu 25. Pin base image digest giải quyết vấn đề gì?

**Đáp án mẫu:** Tạo build reproducible và tránh tag base bị thay đổi ngoài ý muốn.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nhược điểm vận hành của pin digest?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi cách nhận CVE fix khi digest bất biến.

**Điểm kiến thức cần nhớ:** Bất biến phải đi cùng bot cập nhật có kiểm soát.

**Lỗi sinh viên thường mắc:** Pin digest rồi không bao giờ update.

========================

========================

## Câu 26. `.dockerignore` cần loại gì trong monorepo?

**Đáp án mẫu:** `.git`, build cache, IDE files, test output, secret và file không cần runtime.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Loại nhầm jar sẽ gây gì với Dockerfile hiện tại?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu so sánh build context size.

**Điểm kiến thức cần nhớ:** Context nhỏ giảm thời gian và rò rỉ dữ liệu.

**Lỗi sinh viên thường mắc:** Copy toàn repository vào image.

========================

========================

## Câu 27. Image scan xanh có đảm bảo image an toàn không?

**Đáp án mẫu:** Không; scanner phụ thuộc database, scope package, config và runtime. Cần SBOM, signature, policy và least privilege.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** CVE base image không fix được xử lý thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi scanner có quét secret/IaC hay chỉ package.

**Điểm kiến thức cần nhớ:** Security là nhiều lớp.

**Lỗi sinh viên thường mắc:** Đồng nhất không có CVE known với an toàn tuyệt đối.

========================

# Git

========================

## Câu 28. Vì sao commit SHA được dùng làm image tag?

**Đáp án mẫu:** Nó liên kết artifact với snapshot source cụ thể và không đổi.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** SHA tag có chống registry overwrite không?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi cách lấy image digest từ tag.

**Điểm kiến thức cần nhớ:** SHA truy vết source, digest định danh content.

**Lỗi sinh viên thường mắc:** Cho rằng tag không thể bị ghi đè.

========================

========================

## Câu 29. Revert phù hợp GitOps hơn reset ở điểm nào?

**Đáp án mẫu:** Revert tạo commit mới có audit và an toàn với lịch sử đã chia sẻ.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Khi revert manifest nhưng image cũ đã bị xóa thì sao?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi thay đổi nào xuất hiện trong Git history.

**Điểm kiến thức cần nhớ:** Không viết lại lịch sử shared branch.

**Lỗi sinh viên thường mắc:** Force push main để rollback.

========================

========================

## Câu 30. Merge và rebase khác nhau thế nào trong PR workflow?

**Đáp án mẫu:** Merge giữ topology; rebase đặt commit lên base mới và viết lại SHA.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Tại sao không rebase branch người khác đang dùng?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu vẽ lịch sử trước/sau.

**Điểm kiến thức cần nhớ:** Rebase chỉ an toàn với lịch sử chưa chia sẻ hoặc phối hợp rõ.

**Lỗi sinh viên thường mắc:** Nói rebase và merge cho lịch sử y hệt.

========================

========================

## Câu 31. Branch protection cần gì theo đề?

**Đáp án mẫu:** Cấm push trực tiếp main, ít nhất hai approval và required CI checks pass.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Admin bypass có nên bật không?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi bằng chứng cấu hình vì file Git không chứa rule này.

**Điểm kiến thức cần nhớ:** Governance nằm cả ngoài repository.

**Lỗi sinh viên thường mắc:** Dùng README tuyên bố thay cho GitHub setting.

========================

========================

## Câu 32. Tại sao bot CD push thẳng main là rủi ro?

**Đáp án mẫu:** Bypass review, tạo conflict/race và làm source branch vừa chứa app vừa chứa mutation môi trường.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Thay bằng config repo và PR promotion thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi token bot có quyền gì.

**Điểm kiến thức cần nhớ:** Automation cũng phải theo least privilege và review policy.

**Lỗi sinh viên thường mắc:** Cấp PAT toàn quyền để pipeline dễ chạy.

========================

========================

## Câu 33. `git fetch` khác `git pull`?

**Đáp án mẫu:** Fetch tải refs không đổi worktree; pull fetch rồi merge/rebase.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Trong CI resolve branch SHA nên dùng cách nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi lệnh nào có thể tạo merge commit.

**Điểm kiến thức cần nhớ:** Hiểu mutation của command trước khi dùng.

**Lỗi sinh viên thường mắc:** Dùng pull trong job chỉ cần đọc remote ref.

========================

========================

## Câu 34. Tại sao không commit `.env` chứa credential?

**Đáp án mẫu:** Git giữ history lâu dài, xóa file ở commit mới không xóa secret cũ.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nếu đã lộ secret phải làm gì trước?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi rotation và history rewrite khác nhau thế nào.

**Điểm kiến thức cần nhớ:** Rotate secret trước, sau đó làm sạch history nếu cần.

**Lỗi sinh viên thường mắc:** Chỉ thêm `.gitignore` sau khi secret đã commit.

========================

========================

## Câu 35. Tag release nên annotated hay lightweight?

**Đáp án mẫu:** Annotated tag có metadata, message và khả năng ký, phù hợp release audit hơn.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Làm sao verify signed tag?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi tag có trỏ đúng commit main đã kiểm chứng không.

**Điểm kiến thức cần nhớ:** Release ref cần provenance.

**Lỗi sinh viên thường mắc:** Tạo tag từ local commit chưa push.

========================

========================

## Câu 36. Vì sao lock file phải đi cùng thay đổi dependency?

**Đáp án mẫu:** Nó cố định graph dependency thực tế để CI và developer cài giống nhau.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Khi nào regenerate lock file?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi package manifest và lock khác nhau thì `npm ci` làm gì.

**Điểm kiến thức cần nhớ:** Reproducible dependency resolution.

**Lỗi sinh viên thường mắc:** Sửa package.json nhưng bỏ package-lock.json.

========================

# Linux

========================

## Câu 37. Container nhận SIGTERM nhưng không dừng, kiểm tra gì?

**Đáp án mẫu:** PID 1, entrypoint shell, signal handler, preStop và termination grace period.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Sau grace period kubelet gửi signal gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi process tree trong container.

**Điểm kiến thức cần nhớ:** Graceful shutdown phụ thuộc Linux signal.

**Lỗi sinh viên thường mắc:** Tăng grace period mà không sửa signal forwarding.

========================

========================

## Câu 38. OOMKilled khác Java OutOfMemoryError thế nào?

**Đáp án mẫu:** OOMKilled do cgroup/kernel kill, thường exit 137; Java OOME do JVM heap/metaspace và có log exception.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** `-Xmx` nên quan hệ với memory limit thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi Last State và exit code ở đâu.

**Điểm kiến thức cần nhớ:** Phân biệt kernel memory limit với JVM limit.

**Lỗi sinh viên thường mắc:** Chỉ xem application log rồi bỏ qua describe Pod.

========================

========================

## Câu 39. Load average cao có luôn nghĩa CPU 100% không?

**Đáp án mẫu:** Không; còn gồm task runnable và uninterruptible I/O wait.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Lệnh nào phân biệt CPU, I/O và memory pressure?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi ý nghĩa ba số load average.

**Điểm kiến thức cần nhớ:** Đọc nhiều chỉ số trước kết luận.

**Lỗi sinh viên thường mắc:** Đồng nhất load average với phần trăm CPU.

========================

========================

## Câu 40. File descriptor cạn gây triệu chứng gì?

**Đáp án mẫu:** Không mở được socket/file, lỗi too many open files, request fail dù CPU còn thấp.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Kiểm tra limit và FD đang mở thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi `ulimit -n` và `/proc/<pid>/fd`.

**Điểm kiến thức cần nhớ:** Resource không chỉ CPU/memory.

**Lỗi sinh viên thường mắc:** Restart rồi không tìm leak connection/file.

========================

========================

## Câu 41. Permission 755 nghĩa gì?

**Đáp án mẫu:** Owner rwx, group r-x, others r-x.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Tại sao secret file không nên 755?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu giải thích từng bit octal.

**Điểm kiến thức cần nhớ:** Least privilege áp dụng cả filesystem.

**Lỗi sinh viên thường mắc:** Dùng `chmod 777` để chữa mọi lỗi permission.

========================

========================

## Câu 42. Zombie process là gì?

**Đáp án mẫu:** Process đã exit nhưng parent chưa wait, còn entry trong process table.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Container PID 1 cần làm gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi zombie có dùng CPU không.

**Điểm kiến thức cần nhớ:** Init/reaping quan trọng với container nhiều child process.

**Lỗi sinh viên thường mắc:** Kill zombie trực tiếp thay vì sửa parent.

========================

========================

## Câu 43. Disk còn trống nhưng ghi file vẫn fail có thể vì gì?

**Đáp án mẫu:** Hết inode, filesystem read-only, quota, permission hoặc deleted-open file.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Kiểm tra inode bằng lệnh nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi khác nhau giữa `df` và `du`.

**Điểm kiến thức cần nhớ:** Không kết luận chỉ từ `df -h`.

**Lỗi sinh viên thường mắc:** Chỉ mở rộng volume khi nguyên nhân là inode/leak.

========================

========================

## Câu 44. DNS trên Linux resolve theo thứ tự nào?

**Đáp án mẫu:** Theo `/etc/nsswitch.conf`, thường hosts rồi DNS; `/etc/resolv.conf` chứa nameserver/search.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Kubernetes Pod có search domain gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi vì sao hosts file có thể che DNS thật.

**Điểm kiến thức cần nhớ:** Name resolution có nhiều lớp.

**Lỗi sinh viên thường mắc:** Chỉ ping IP rồi kết luận DNS tốt.

========================

========================

## Câu 45. Vì sao cần đọc log `--previous` với CrashLoop?

**Đáp án mẫu:** Container mới đã thay instance cũ; log previous giữ output lần crash trước.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nếu log rỗng thì nguồn bằng chứng kế tiếp?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi events, termination reason và exit code.

**Điểm kiến thức cần nhớ:** Thu bằng chứng trước restart thủ công.

**Lỗi sinh viên thường mắc:** Chỉ xem log container đang khởi động.

========================

# Networking

========================

## Câu 46. Luồng request storefront trên DOKS đi qua các lớp nào?

**Đáp án mẫu:** Client DNS/hosts, cloud LoadBalancer, NGINX Ingress, BFF Service, EndpointSlice, Pod và có thể Envoy.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** TLS terminate ở đâu trong cấu hình hiện tại?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu chỉ ra từng port và hostname.

**Điểm kiến thức cần nhớ:** Debug theo từng hop.

**Lỗi sinh viên thường mắc:** Nhảy thẳng vào restart Pod khi client timeout.

========================

========================

## Câu 47. Service tồn tại nhưng không có endpoint, nguyên nhân chính?

**Đáp án mẫu:** Selector không khớp Pod label hoặc Pod chưa Ready.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Headless Service khác gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi lệnh xem EndpointSlice.

**Điểm kiến thức cần nhớ:** Service object không bảo đảm backend tồn tại.

**Lỗi sinh viên thường mắc:** Chỉ kiểm tra ClusterIP.

========================

========================

## Câu 48. `port`, `targetPort`, `nodePort` khác nhau thế nào?

**Đáp án mẫu:** Port là cổng Service, targetPort đến Pod, nodePort mở trên mỗi node cho NodePort Service.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Named targetPort giúp gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu map một request cụ thể.

**Điểm kiến thức cần nhớ:** Mỗi lớp có namespace cổng riêng.

**Lỗi sinh viên thường mắc:** Đặt mọi cổng giống nhau rồi không hiểu ý nghĩa.

========================

========================

## Câu 49. Ingress khác Ingress Controller?

**Đáp án mẫu:** Ingress là desired routing resource; controller là workload thực thi và cấu hình proxy/LB.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Không có controller thì Ingress status ra sao?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi project dùng controller nào.

**Điểm kiến thức cần nhớ:** API object cần controller tương ứng.

**Lỗi sinh viên thường mắc:** Tạo Ingress rồi nghĩ Kubernetes tự route.

========================

========================

## Câu 50. HTTP 503 từ proxy thường gợi ý gì?

**Đáp án mẫu:** Không có healthy upstream, route/service/endpoint sai hoặc upstream reset; phải đọc proxy log và endpoint.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** 503 khác 404 ở lớp nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu xác định response do NGINX, Envoy hay app tạo.

**Điểm kiến thức cần nhớ:** Mã lỗi phải gắn với component phát sinh.

**Lỗi sinh viên thường mắc:** Thấy 503 rồi kết luận app code lỗi.

========================

========================

## Câu 51. Connection refused khác timeout?

**Đáp án mẫu:** Refused thường host reachable nhưng không listener/RST; timeout thường packet bị drop, route hoặc firewall/policy.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** DNS failure khác hai trường hợp này thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi `curl -v`, `ss`, NetworkPolicy.

**Điểm kiến thức cần nhớ:** Symptom network định hướng layer kiểm tra.

**Lỗi sinh viên thường mắc:** Gộp mọi lỗi kết nối thành DNS.

========================

========================

## Câu 52. Vì sao hard-coded `hostAliases` IP trong chart backend không phù hợp production?

**Đáp án mẫu:** IP service/LB thay đổi, bypass DNS và gây drift theo môi trường.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Thay bằng service DNS hoặc external DNS thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi khi IP cũ tái sử dụng sẽ xảy ra gì.

**Điểm kiến thức cần nhớ:** Dùng stable discovery thay IP tĩnh trong workload.

**Lỗi sinh viên thường mắc:** Sửa hosts trong image/Pod để chữa routing lâu dài.

========================

========================

## Câu 53. mTLS và TLS ingress bảo vệ hai đoạn nào?

**Đáp án mẫu:** TLS ingress bảo vệ client đến gateway; Istio mTLS bảo vệ workload-to-workload trong mesh.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Đoạn gateway đến Pod có thể plaintext khi nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu vẽ trust boundary.

**Điểm kiến thức cần nhớ:** End-to-end gồm nhiều TLS hop.

**Lỗi sinh viên thường mắc:** Thấy HTTPS ngoài vào rồi nói nội bộ đã mTLS.

========================

========================

## Câu 54. NetworkPolicy và Istio AuthorizationPolicy bổ sung nhau thế nào?

**Đáp án mẫu:** NetworkPolicy kiểm soát L3/L4 theo Pod/IP/port; Istio kiểm soát identity và L7 khi traffic qua proxy.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Traffic bypass sidecar được lớp nào chặn?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi policy nào xử lý method/path.

**Điểm kiến thức cần nhớ:** Defense in depth ở network và application identity.

**Lỗi sinh viên thường mắc:** Dùng một lớp rồi bỏ lớp còn lại.

========================

# Kubernetes

========================

## Câu 55. `2/2 Running` trong project nghĩa gì?

**Đáp án mẫu:** Một Pod có hai container ready, thường app và Envoy; không phải hai replica.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Làm sao biết số replica thật?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu đọc Deployment readyReplicas.

**Điểm kiến thức cần nhớ:** Container readiness và replica count là hai khái niệm.

**Lỗi sinh viên thường mắc:** Báo cáo 2/2 là hai Pod HA.

========================

========================

## Câu 56. Deployment, ReplicaSet và Pod liên hệ thế nào?

**Đáp án mẫu:** Deployment quản rollout/revision, tạo ReplicaSet; ReplicaSet giữ số Pod theo selector.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Vì sao không sửa ReplicaSet do Deployment quản?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi ownerReferences.

**Điểm kiến thức cần nhớ:** Thao tác ở controller cấp cao nhất.

**Lỗi sinh viên thường mắc:** Patch Pod trực tiếp để sửa lâu dài.

========================

========================

## Câu 57. Readiness, liveness và startup probe khác nhau?

**Đáp án mẫu:** Readiness điều khiển nhận traffic, liveness restart container, startup bảo vệ app khởi động chậm.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Probe nào không nên phụ thuộc external DB?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Cho một app boot chậm và hỏi cấu hình.

**Điểm kiến thức cần nhớ:** Probe phải phản ánh đúng quyết định controller.

**Lỗi sinh viên thường mắc:** Dùng cùng endpoint nặng cho cả ba.

========================

========================

## Câu 58. Requests và limits ảnh hưởng project JVM thế nào?

**Đáp án mẫu:** Request dùng scheduling/HPA; limit là cgroup ceiling. Heap cộng native memory phải dưới memory limit.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Vì sao node có thể 95% memory requests dù usage thấp?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi scheduler dùng con số nào.

**Điểm kiến thức cần nhớ:** Capacity planning dựa trên request và usage.

**Lỗi sinh viên thường mắc:** Chỉ nhìn usage rồi tăng replica.

========================

========================

## Câu 59. Pod Pending cần kiểm tra gì trước?

**Đáp án mẫu:** Events, requests so với allocatable, taint/toleration, affinity, PVC và quota.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Preemption có giải quyết mọi Pending không?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu đọc scheduler event.

**Điểm kiến thức cần nhớ:** Pending là scheduling, chưa phải app crash.

**Lỗi sinh viên thường mắc:** Xem application log của Pod chưa được schedule.

========================

========================

## Câu 60. ServiceAccount quan trọng với Istio AuthorizationPolicy thế nào?

**Đáp án mẫu:** Istio tạo SPIFFE principal từ namespace và ServiceAccount dưới mTLS.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Dùng default ServiceAccount cho mọi app gây gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi principal product policy đang allow.

**Điểm kiến thức cần nhớ:** Workload identity phải tách theo service.

**Lỗi sinh viên thường mắc:** Chỉ dùng label app làm identity bảo mật.

========================

========================

## Câu 61. HPA CPU utilization cần điều kiện gì?

**Đáp án mẫu:** Container phải có CPU request và metrics pipeline hoạt động.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** HPA khác Cluster Autoscaler?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi vì sao current metric unknown.

**Điểm kiến thức cần nhớ:** HPA scale Pod, autoscaler scale node.

**Lỗi sinh viên thường mắc:** Có CPU limit nhưng không có request rồi mong HPA chạy.

========================

========================

## Câu 62. ConfigMap đổi nhưng app không nhận cấu hình mới, vì sao?

**Đáp án mẫu:** Env var không tự cập nhật; volume có thể cập nhật nhưng app phải reload; Deployment annotation/reloader có thể trigger rollout.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Project dùng annotation reloader nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi config được mount hay envFrom.

**Điểm kiến thức cần nhớ:** Cấu hình update semantics phụ thuộc cách consume.

**Lỗi sinh viên thường mắc:** Apply ConfigMap rồi không restart/reload.

========================

========================

## Câu 63. Secret Kubernetes có phải đã mã hóa?

**Đáp án mẫu:** `data` chỉ base64; cần encryption at rest/KMS, RBAC và secret manager.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Secret mounted volume có ưu điểm gì so với env?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu decode một giá trị để chứng minh.

**Điểm kiến thức cần nhớ:** Encoding không phải encryption.

**Lỗi sinh viên thường mắc:** Commit base64 secret vào Git.

========================

# Helm

========================

## Câu 64. Chart backend được dùng lại thế nào?

**Đáp án mẫu:** Chart từng service khai báo dependency backend và cung cấp values image, port, env, ingress.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Khi backend template đổi, service nào bị ảnh hưởng?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu trace dependency từ product Chart.yaml.

**Điểm kiến thức cần nhớ:** Shared chart giảm lặp nhưng tăng blast radius.

**Lỗi sinh viên thường mắc:** Test chỉ một chart consumer.

========================

========================

## Câu 65. `helm lint` thành công có đủ không?

**Đáp án mẫu:** Không; cần render từng environment, schema/policy validation, server dry-run và runtime test.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Lỗi selector nào lint có thể không bắt?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi manifest render thực tế.

**Điểm kiến thức cần nhớ:** Template hợp lệ cú pháp chưa chắc hợp lệ vận hành.

**Lỗi sinh viên thường mắc:** Dùng lint làm bằng chứng deployment hoàn chỉnh.

========================

========================

## Câu 66. `values.yaml` là public API của chart nghĩa là gì?

**Đáp án mẫu:** Tên, kiểu và nesting key là contract với workflow và environment overrides.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Đổi key có backward compatibility thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi key sai có thể bị Helm bỏ qua im lặng không.

**Điểm kiến thức cần nhớ:** Version và validate values contract.

**Lỗi sinh viên thường mắc:** Refactor values mà không cập nhật mọi environment.

========================

========================

## Câu 67. `--set` và `--set-string` khác gì?

**Đáp án mẫu:** `--set` có type coercion; `--set-string` giữ tag/host/value đúng dạng chuỗi.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Tag giống số hoặc boolean gây gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu dự đoán giá trị `--set tag=001`.

**Điểm kiến thức cần nhớ:** Kiểu dữ liệu values phải rõ.

**Lỗi sinh viên thường mắc:** Dùng `--set` cho mọi thứ.

========================

========================

## Câu 68. `Chart.version` và `appVersion` dùng ra sao?

**Đáp án mẫu:** Chart.version version hóa package; appVersion mô tả app và có thể làm default image tag nhưng không tự deploy.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Thay appVersion có tạo rollout nếu template không dùng không?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi field nào Helm repository index dùng.

**Điểm kiến thức cần nhớ:** Metadata chỉ có tác dụng khi template consume.

**Lỗi sinh viên thường mắc:** Đổi appVersion rồi nghĩ image tự đổi.

========================

========================

## Câu 69. Tại sao cần `helm dependency build`?

**Đáp án mẫu:** Nó tải/đóng gói dependency theo Chart.yaml/lock vào charts để render/install.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Chart.lock có vai trò gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Xóa charts cache rồi hỏi install offline.

**Điểm kiến thức cần nhớ:** Dependency phải được pin và reproducible.

**Lỗi sinh viên thường mắc:** Dùng latest dependency mỗi lần CI.

========================

========================

## Câu 70. Umbrella chart có ưu và nhược điểm gì trong YAS?

**Đáp án mẫu:** Cài đồng bộ nhiều service và central values; nhưng release lớn, blast radius và upgrade chậm.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Khi nào tách Application per service?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi một service fail render ảnh hưởng toàn sync ra sao.

**Điểm kiến thức cần nhớ:** Chọn granularity release theo ownership và coupling.

**Lỗi sinh viên thường mắc:** Cho rằng umbrella luôn đơn giản hơn.

========================

========================

## Câu 71. Helm rollback có rollback database migration không?

**Đáp án mẫu:** Không; nó rollback Kubernetes release, DB cần migration strategy riêng và backward compatibility.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Expand-contract hoạt động thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi chuyện gì xảy ra khi app cũ gặp schema mới.

**Điểm kiến thức cần nhớ:** Application và data rollback là hai bài toán.

**Lỗi sinh viên thường mắc:** Tin Helm history có thể hoàn tác dữ liệu.

========================

========================

## Câu 72. Secret trong Helm values có an toàn không?

**Đáp án mẫu:** Không nếu values nằm trong Git/release metadata; dùng External Secrets, SOPS/secret manager và giới hạn quyền release Secret.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Helm release lưu values ở đâu?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi ai có thể đọc Secret release trong namespace.

**Điểm kiến thức cần nhớ:** Template Secret không làm plaintext biến mất.

**Lỗi sinh viên thường mắc:** Base64 trong template được xem là bảo mật.

========================

# GitHub Actions

========================

## Câu 73. `permissions` của GITHUB_TOKEN nên đặt thế nào?

**Đáp án mẫu:** Mặc định tối thiểu; job build chỉ contents read/packages write, job GitOps mới cần contents write.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Tại sao cấp ở job tốt hơn workflow?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi token PR từ fork có quyền gì.

**Điểm kiến thức cần nhớ:** Least privilege cho từng job.

**Lỗi sinh viên thường mắc:** Cấp `write-all` để tránh lỗi permission.

========================

========================

## Câu 74. Third-party action nên pin thế nào?

**Đáp án mẫu:** Production pin full commit SHA, dùng bot theo dõi update và review provenance.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Pin major tag có rủi ro gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi tag upstream có thể bị di chuyển không.

**Điểm kiến thức cần nhớ:** Action cũng là dependency thực thi code.

**Lỗi sinh viên thường mắc:** Tin marketplace badge là đủ.

========================

========================

## Câu 75. Matrix trong `ci.yml` giải quyết gì và chưa giải quyết gì?

**Đáp án mẫu:** Giảm lặp định nghĩa build 14 service; chưa chọn service thay đổi.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Kết hợp matrix với changed-files output thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu phân biệt parallelization và change detection.

**Điểm kiến thức cần nhớ:** Matrix là fan-out, không phải monorepo intelligence.

**Lỗi sinh viên thường mắc:** Cho rằng matrix tự đọc Git diff.

========================

========================

## Câu 76. `needs` có ý nghĩa gì trong workflow?

**Đáp án mẫu:** Tạo dependency DAG và cho job sau chỉ chạy khi job trước thành công mặc định.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Làm job cleanup chạy dù dependency fail thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi `if: always()` và kết quả needs.

**Điểm kiến thức cần nhớ:** Job ordering và failure propagation phải rõ.

**Lỗi sinh viên thường mắc:** Dựa vào thứ tự YAML để sắp job.

========================

========================

## Câu 77. Environment trong GitHub Actions dùng để làm gì?

**Đáp án mẫu:** Gắn secrets/variables, protection rule, approval và deployment history theo môi trường.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Developer và staging nên có rule khác gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi secret repository khác environment secret.

**Điểm kiến thức cần nhớ:** Môi trường nhạy cảm cần approval và scope credential.

**Lỗi sinh viên thường mắc:** Chỉ dùng environment như nhãn trang trí.

========================

========================

## Câu 78. Tại sao deploy cần `concurrency`?

**Đáp án mẫu:** Ngăn hai run đồng thời mutate cùng environment hoặc Git values gây race.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nên cancel run staging đang deploy không?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Đề xuất group key cụ thể.

**Điểm kiến thức cần nhớ:** Concurrency policy tùy tính chất môi trường.

**Lỗi sinh viên thường mắc:** Dùng một group toàn repo làm nghẽn mọi CI.

========================

========================

## Câu 79. `paths-ignore: k8s/environments/**` trong deploy dev nhằm gì?

**Đáp án mẫu:** Tránh commit bot cập nhật values tự kích hoạt lại pipeline và tạo vòng lặp.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nhược điểm nếu manifest khác trong path đó cần build?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi event nào tạo commit thứ hai.

**Điểm kiến thức cần nhớ:** Chống loop phải không che thay đổi hợp lệ.

**Lỗi sinh viên thường mắc:** Bỏ ignore rồi pipeline tự gọi vô hạn.

========================

========================

## Câu 80. OIDC tốt hơn cloud token dài hạn thế nào?

**Đáp án mẫu:** Runner đổi identity GitHub lấy credential ngắn hạn theo claims, không lưu secret cloud tĩnh.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Claims nào nên giới hạn trust policy?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi token bị leak còn sống bao lâu.

**Điểm kiến thức cần nhớ:** Federated short-lived credentials giảm blast radius.

**Lỗi sinh viên thường mắc:** OIDC nhưng trust mọi repository/branch.

========================

========================

## Câu 81. Artifact và output khác nhau thế nào?

**Đáp án mẫu:** Output truyền giá trị nhỏ giữa steps/jobs; artifact lưu file/report có retention.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Image có nên truyền bằng artifact tar hay registry digest?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi giới hạn và lifetime.

**Điểm kiến thức cần nhớ:** Chọn kênh truyền theo loại dữ liệu.

**Lỗi sinh viên thường mắc:** Nhét report lớn vào job output.

========================

# Jenkins

========================

## Câu 82. Project này có Jenkinsfile không?

**Đáp án mẫu:** Không có Jenkinsfile được Git theo dõi; nhóm dùng GitHub Actions dù đề cho phép Jenkins.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nếu chuyển sang Jenkins, mapping workflow nào thành stage?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu chỉ ra bằng `git ls-files`.

**Điểm kiến thức cần nhớ:** Không mô tả artifact không tồn tại.

**Lỗi sinh viên thường mắc:** Trả lời theo lý thuyết Jenkins thay vì project.

========================

========================

## Câu 83. Controller và agent khác nhau?

**Đáp án mẫu:** Controller quản job/schedule/state; agent thực thi build.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Vì sao không mount Docker socket trên controller?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi khi agent chết build state ra sao.

**Điểm kiến thức cần nhớ:** Cô lập execution khỏi control plane.

**Lỗi sinh viên thường mắc:** Chạy mọi build trên built-in node.

========================

========================

## Câu 84. Multibranch Pipeline đáp ứng yêu cầu branch thế nào?

**Đáp án mẫu:** Scan SCM, phát hiện Jenkinsfile từng branch/PR và tạo job tương ứng.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Orphaned branch job cleanup ra sao?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi webhook và periodic scan khác nhau.

**Điểm kiến thức cần nhớ:** Branch automation cần discovery và retention policy.

**Lỗi sinh viên thường mắc:** Tạo job tay cho từng branch.

========================

========================

## Câu 85. Jenkins credentials binding có đảm bảo secret không lộ không?

**Đáp án mẫu:** Không tuyệt đối; shell trace, process, artifact hoặc plugin có thể lộ. Cần masking, scope nhỏ và trusted agent.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Secret file binding nằm ở đâu?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi vì sao không echo environment.

**Điểm kiến thức cần nhớ:** Credential handling cần defense in depth.

**Lỗi sinh viên thường mắc:** Tin dấu **** nghĩa secret chưa từng lộ.

========================

========================

## Câu 86. Shared Library dùng khi nào?

**Đáp án mẫu:** Tái sử dụng logic pipeline có version giữa nhiều service/job.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Tại sao phải pin library version?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi code library chạy với quyền gì.

**Điểm kiến thức cần nhớ:** Pipeline code cũng cần version/review/test.

**Lỗi sinh viên thường mắc:** Dùng library `main` mutable cho production.

========================

========================

## Câu 87. `stash/unstash` khác archive artifact?

**Đáp án mẫu:** Stash chuyển file giữa stage/node trong cùng run; archive giữ output sau run cho người dùng/hệ thống.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** File image lớn nên đưa qua gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi retention và storage controller.

**Điểm kiến thức cần nhớ:** Không dùng controller làm kho binary lớn.

**Lỗi sinh viên thường mắc:** Stash hàng GB giữa agent.

========================

========================

## Câu 88. Webhook tốt hơn SCM polling ở điểm nào?

**Đáp án mẫu:** Phản hồi nhanh và giảm tải định kỳ; vẫn có thể cần scan dự phòng.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Bảo vệ webhook bằng gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi mất webhook thì branch mới được phát hiện thế nào.

**Điểm kiến thức cần nhớ:** Event delivery cần authentication và retry.

**Lỗi sinh viên thường mắc:** Expose Jenkins webhook không secret.

========================

========================

## Câu 89. Ephemeral Kubernetes agent có lợi gì?

**Đáp án mẫu:** Môi trường sạch, scale theo job và giảm persistence sau build.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Cache Maven/Docker xử lý thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi agent Pod cần RBAC gì.

**Điểm kiến thức cần nhớ:** Ephemeral không đồng nghĩa stateless hoàn toàn.

**Lỗi sinh viên thường mắc:** Cho agent service account cluster-admin.

========================

========================

## Câu 90. `post { always { ... } }` dùng làm gì?

**Đáp án mẫu:** Publish report, cleanup và notification bất kể stage success/fail.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Cleanup fail có nên làm đổi kết quả build?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi khác success/failure/unstable.

**Điểm kiến thức cần nhớ:** Evidence và cleanup phải chạy cả khi lỗi.

**Lỗi sinh viên thường mắc:** Đặt report upload trong stage chỉ chạy khi test pass.

========================

# GitOps

========================

## Câu 91. Source of truth của deployment nằm ở đâu?

**Đáp án mẫu:** Git chứa chart/values/manifests; cluster là actual state do controller reconcile.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Secret không nằm Git thì desired state biểu diễn thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi live edit có được giữ không.

**Điểm kiến thức cần nhớ:** GitOps quản intent, không nhất thiết chứa plaintext secret.

**Lỗi sinh viên thường mắc:** Coi cluster là nơi sửa cấu hình chính.

========================

========================

## Câu 92. Pull-based tốt hơn push-based ở điểm credential?

**Đáp án mẫu:** Controller trong cluster pull Git; CI không cần quyền cluster rộng.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Controller bị compromise có blast radius gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi outbound access từ cluster.

**Điểm kiến thức cần nhớ:** Giảm credential CI nhưng phải bảo vệ controller.

**Lỗi sinh viên thường mắc:** Nói pull-based không cần secret nào.

========================

========================

## Câu 93. Drift là gì trong project?

**Đáp án mẫu:** Live resource khác desired state trong Git do sửa tay, controller khác hoặc mutation.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Mutation webhook tạo khác biệt xử lý thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi selfHeal phản ứng ra sao.

**Điểm kiến thức cần nhớ:** Chỉ ignore difference có chủ ý và hẹp.

**Lỗi sinh viên thường mắc:** Ignore toàn spec để dashboard xanh.

========================

========================

## Câu 94. Promotion dev sang staging nên thực hiện thế nào?

**Đáp án mẫu:** Promote cùng image digest bằng PR/commit thay values staging, không rebuild.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Approval đặt ở Git hay Argo CD?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi cách chứng minh digest hai môi trường giống nhau.

**Điểm kiến thức cần nhớ:** Promotion thay desired reference, không tạo artifact mới.

**Lỗi sinh viên thường mắc:** Copy tag `latest`.

========================

========================

## Câu 95. Git revert có đủ cho rollback mọi trường hợp?

**Đáp án mẫu:** Không nếu DB/schema/external state không backward-compatible hoặc artifact cũ đã bị xóa.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Thiết kế retention image bao lâu?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi rollback plan trước migration.

**Điểm kiến thức cần nhớ:** Git rollback cần artifact và data compatibility.

**Lỗi sinh viên thường mắc:** Chỉ nói revert commit là xong.

========================

========================

## Câu 96. Tại sao nên tách app repo và config repo?

**Đáp án mẫu:** Tách quyền, vòng đời, audit promotion và tránh bot mutation source branch; đổi lại tăng coordination.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Monorepo nhỏ có thể giữ chung khi nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi ai được merge vào staging config.

**Điểm kiến thức cần nhớ:** Repository boundary là security/ownership decision.

**Lỗi sinh viên thường mắc:** Tách repo theo phong trào nhưng không có automation.

========================

========================

## Câu 97. Desired state dùng tag hay digest?

**Đáp án mẫu:** Digest bảo đảm đúng bytes; tag chỉ dễ đọc và có thể mutable.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Làm sao vẫn hiển thị version thân thiện?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi registry garbage collection ảnh hưởng digest cũ.

**Điểm kiến thức cần nhớ:** Pin digest, giữ metadata tag/version.

**Lỗi sinh viên thường mắc:** Dùng SHA tag rồi gọi là content-addressed.

========================

========================

## Câu 98. Manual sync có còn là GitOps không?

**Đáp án mẫu:** Có thể, nếu Git vẫn là source of truth và controller apply; automation level khác nhau.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Khi nào staging nên manual sync?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi sự khác nhau giữa approval commit và approval sync.

**Điểm kiến thức cần nhớ:** GitOps không bắt buộc auto-deploy mọi môi trường.

**Lỗi sinh viên thường mắc:** Đồng nhất GitOps với auto sync.

========================

========================

## Câu 99. Config drift vì HPA thay replicas xử lý thế nào?

**Đáp án mẫu:** Không quản field replicas bằng Git khi HPA sở hữu hoặc cấu hình ignoreDifference hẹp có lý do.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Field ownership server-side apply giúp gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi controller nào là owner của replicas.

**Điểm kiến thức cần nhớ:** Một field nên có một controller chịu trách nhiệm.

**Lỗi sinh viên thường mắc:** Tắt selfHeal toàn Application.

========================

# ArgoCD

========================

## Câu 100. `Synced` và `Healthy` khác nhau?

**Đáp án mẫu:** Synced là live khớp desired revision; Healthy là resource vận hành tốt. Có thể Synced nhưng Degraded.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** ImagePullBackOff sẽ cho trạng thái nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu nêu bằng chứng từ Application và Pod.

**Điểm kiến thức cần nhớ:** Sync status không thay health verification.

**Lỗi sinh viên thường mắc:** Báo cáo Synced là deployment thành công.

========================

========================

## Câu 101. `prune: true` làm gì?

**Đáp án mẫu:** Xóa resource từng do Application quản nhưng không còn trong desired state.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Xóa nhầm directory Git có hậu quả gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi resource orphan khác prune candidate.

**Điểm kiến thức cần nhớ:** Prune cần scope, review và project restriction.

**Lỗi sinh viên thường mắc:** Bật prune với Application quá rộng.

========================

========================

## Câu 102. `selfHeal: true` làm gì?

**Đáp án mẫu:** Tự đảo drift live về Git khi resource bị sửa tay.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Debug hotfix tạm thời nên làm sao?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi vì sao kubectl edit bị mất.

**Điểm kiến thức cần nhớ:** Hotfix phải quay lại Git hoặc tạm suspend có kiểm soát.

**Lỗi sinh viên thường mắc:** Đấu với controller bằng patch lặp lại.

========================

========================

## Câu 103. AppProject production cần giới hạn gì?

**Đáp án mẫu:** Source repo, destination cluster/namespace, resource kinds và roles.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Vì sao `default` project rủi ro?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi Application có thể tạo ClusterRole không.

**Điểm kiến thức cần nhớ:** Giới hạn blast radius của GitOps controller.

**Lỗi sinh viên thường mắc:** Chỉ dùng namespace RBAC mà bỏ AppProject.

========================

========================

## Câu 104. `targetRevision: main` có ưu và nhược điểm gì?

**Đáp án mẫu:** Đơn giản, tự nhận commit mới; nhưng mutable và promotion khó pin. Production nhạy cảm có thể dùng tag/commit hoặc PR flow.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Argo CD hiển thị revision thực tế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi rollback về commit cụ thể.

**Điểm kiến thức cần nhớ:** Branch ref được resolve thành commit.

**Lỗi sinh viên thường mắc:** Nói cluster chạy branch chứ không phải commit.

========================

========================

## Câu 105. `CreateNamespace=true` có tạo đủ policy namespace không?

**Đáp án mẫu:** Không; chỉ tạo namespace, không tự thêm quota, limit range, network policy hay injection label mong muốn.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Quản namespace bằng chart riêng thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi dev namespace có Istio label gì.

**Điểm kiến thức cần nhớ:** Namespace lifecycle gồm nhiều policy.

**Lỗi sinh viên thường mắc:** Tin sync option thay platform bootstrap.

========================

========================

## Câu 106. Sync wave/hook dùng khi nào?

**Đáp án mẫu:** Sắp thứ tự CRD/operator, migration, app và post-sync verification.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Hook fail ảnh hưởng health/sync thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi migration Job có được chạy lại không.

**Điểm kiến thức cần nhớ:** Ordering phải idempotent và observable.

**Lỗi sinh viên thường mắc:** Dùng sleep cố định thay readiness/dependency.

========================

========================

## Câu 107. Argo CD không đọc được private repo, kiểm tra gì?

**Đáp án mẫu:** Repo credential, URL, TLS/SSH host key, network/DNS và project source restriction.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Deploy key hay GitHub App tốt hơn?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi repo-server log.

**Điểm kiến thức cần nhớ:** Troubleshoot source trước render/sync.

**Lỗi sinh viên thường mắc:** Tạo PAT cá nhân quyền rộng.

========================

========================

## Câu 108. Application render Helm lỗi values file, nguyên nhân project có thể là gì?

**Đáp án mẫu:** Path relative sai, branch thiếu file, YAML/type lỗi hoặc dependency chart chưa resolve.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** `helm template` local cần mô phỏng gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi đường dẫn values được tính từ đâu.

**Điểm kiến thức cần nhớ:** Reproduce render ở cùng revision.

**Lỗi sinh viên thường mắc:** Chỉ restart argocd-repo-server.

========================

# Istio

========================

## Câu 109. Namespace nào đang STRICT và namespace nào PERMISSIVE?

**Đáp án mẫu:** `yas` và `yas-developer` STRICT; `dev` và `staging` PERMISSIVE trong manifest hiện tại.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Lộ trình chuyển dev/staging sang STRICT?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Mở `peer-authentication.yaml` và đọc từng document.

**Điểm kiến thức cần nhớ:** Không khái quát một policy cho mọi namespace.

**Lỗi sinh viên thường mắc:** Nói toàn cluster đã STRICT.

========================

========================

## Câu 110. PeerAuthentication và DestinationRule khác nhau?

**Đáp án mẫu:** PeerAuthentication quy định inbound mTLS của server; DestinationRule quy định outbound traffic policy sau route.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Auto mTLS có thể làm explicit rule dư khi nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu xác định client và server side.

**Điểm kiến thức cần nhớ:** Hai resource không thay thế nhau.

**Lỗi sinh viên thường mắc:** Nói cả hai đều bật TLS giống nhau.

========================

========================

## Câu 111. AuthorizationPolicy product hiện cho ai gọi?

**Đáp án mẫu:** Order, cart, search, storefront-bff và backoffice-bff; caller khác bị deny ngầm khi policy ALLOW áp đúng selector.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Vì sao không giữ order-only ở runtime?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu đọc principals trong YAML.

**Điểm kiến thức cần nhớ:** Policy phải cân bằng yêu cầu demo và dependency thật.

**Lỗi sinh viên thường mắc:** Trả lời product chỉ cho order vì nhớ đề mà không đọc manifest.

========================

========================

## Câu 112. Vì sao curl trong container `istio-proxy` không phải test mTLS chuẩn?

**Đáp án mẫu:** UID Envoy thường bypass traffic capture nên curl có thể đi plaintext và bị STRICT peer reset.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Test client đúng phải có đặc điểm gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi project có mesh tester nào.

**Điểm kiến thức cần nhớ:** Phép thử phải đi qua data path của workload.

**Lỗi sinh viên thường mắc:** Kết luận mTLS hỏng từ curl proxy.

========================

========================

## Câu 113. VirtualService tax retry tạo tối đa bao nhiêu request?

**Đáp án mẫu:** Một lần đầu cộng ba retry, tối đa bốn nếu điều kiện và timeout cho phép.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Vì sao `retriable-4xx` cần thận trọng?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi perTryTimeout và total timeout.

**Điểm kiến thức cần nhớ:** Retry là traffic amplification.

**Lỗi sinh viên thường mắc:** Nói attempts là tổng số lần gọi.

========================

========================

## Câu 114. Retry order có rủi ro business gì?

**Đáp án mẫu:** POST không idempotent có thể tạo order/payment trùng; cần idempotency key hoặc giới hạn route/method.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Retry ở client và mesh đồng thời gây gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi retry budget tổng.

**Điểm kiến thức cần nhớ:** Resilience policy phải hiểu semantics.

**Lỗi sinh viên thường mắc:** Retry mọi 5xx mặc định.

========================

========================

## Câu 115. Kiali graph trống dù Pod có sidecar, kiểm tra gì?

**Đáp án mẫu:** Có traffic, Prometheus scrape Envoy, time range, telemetry labels và Kiali datasource.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Padlock trên graph chứng minh tới mức nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi query `istio_requests_total`.

**Điểm kiến thức cần nhớ:** Visualization phụ thuộc telemetry, không trực tiếp data path.

**Lỗi sinh viên thường mắc:** Restart Kiali trước khi kiểm tra Prometheus.

========================

========================

## Câu 116. Selector AuthorizationPolicy sai label gây gì?

**Đáp án mẫu:** Policy không chọn product nên không enforce, dù resource Accepted.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Kiểm tra effective policy bằng cách nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi Pod label thực tế và proxy config.

**Điểm kiến thức cần nhớ:** Policy existence không bằng enforcement.

**Lỗi sinh viên thường mắc:** Chỉ `kubectl get` rồi kết luận bảo mật.

========================

========================

## Câu 117. Istio Gateway khác NGINX Ingress trong project?

**Đáp án mẫu:** Gateway là Istio listener trên gateway workload; project public entry chủ yếu dùng NGINX Ingress. Không được gọi hai thứ là một.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Có thể chạy cả hai theo mô hình nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi manifest nào bind VirtualService tới gateway.

**Điểm kiến thức cần nhớ:** Xác định đúng controller trên ingress path.

**Lỗi sinh viên thường mắc:** Thấy Istio cài rồi nói mọi ingress qua Istio gateway.

========================

# Observability

========================

## Câu 118. Ba signal trong project đi theo đường nào?

**Đáp án mẫu:** OTel nhận telemetry; traces sang Tempo, metrics sang Prometheus, logs sang Loki; Grafana query các backend.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Service graph được tạo từ component nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu đọc collector pipelines.

**Điểm kiến thức cần nhớ:** Mỗi signal cần receiver, processor và exporter nối thành pipeline.

**Lỗi sinh viên thường mắc:** Thấy component khai báo rồi nghĩ pipeline đã dùng.

========================

========================

## Câu 119. Metrics, logs và traces dùng cho câu hỏi nào?

**Đáp án mẫu:** Metrics phát hiện/định lượng, logs mô tả event, traces chỉ đường đi và latency xuyên service.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Bắt đầu từ alert 5xx rồi correlate thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu một quy trình điều tra cụ thể.

**Điểm kiến thức cần nhớ:** Các signal bổ sung, không thay thế nhau.

**Lỗi sinh viên thường mắc:** Dùng log cho dashboard số liệu dài hạn.

========================

========================

## Câu 120. Vì sao trace ID nên ở log body nhưng không ở metric/Loki label?

**Đáp án mẫu:** Trace ID cardinality gần mỗi request một giá trị, làm series/index bùng nổ.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Exemplar nối metric với trace thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi low-cardinality label ví dụ.

**Điểm kiến thức cần nhớ:** Thiết kế cardinality là yêu cầu production.

**Lỗi sinh viên thường mắc:** Đưa userId/orderId vào mọi label.

========================

========================

## Câu 121. Collector local có điểm nào không production-ready?

**Đáp án mẫu:** CORS wildcard, TLS insecure, thiếu auth/memory limiter/persistent queue và endpoint bind mọi interface.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Backpressure khi Tempo down xử lý thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi queued retry và sending queue.

**Điểm kiến thức cần nhớ:** Collector cũng là workload cần bảo vệ và monitor.

**Lỗi sinh viên thường mắc:** Coi collector chỉ là forwarder không thể mất dữ liệu.

========================

========================

## Câu 122. Prometheus scrape interval 2 giây có trade-off gì?

**Đáp án mẫu:** Độ phân giải cao nhưng tăng samples, CPU, network và storage; cần dựa SLO/debug need.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Evaluation interval nên quan hệ với scrape interval?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Tính samples/ngày cho một series.

**Điểm kiến thức cần nhớ:** Tần suất metric có chi phí trực tiếp.

**Lỗi sinh viên thường mắc:** Đặt 1s để dashboard mượt.

========================

========================

## Câu 123. Loki local mất log khi nào?

**Đáp án mẫu:** Filesystem `/tmp` hoặc container/volume mất, single replica hỏng; retention hiện không phù hợp lưu dài.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Production dùng backend gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi volume mount và object store.

**Điểm kiến thức cần nhớ:** Durability phải thiết kế ngoài container filesystem.

**Lỗi sinh viên thường mắc:** Tin StatefulSet tự bảo đảm log bền.

========================

========================

## Câu 124. Tempo retention 1 giờ ảnh hưởng demo gì?

**Đáp án mẫu:** Trace cũ biến mất nhanh, không đủ điều tra sự cố qua đêm; phù hợp lab tiết kiệm.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Chọn retention production dựa yếu tố gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi compactor config.

**Điểm kiến thức cần nhớ:** Retention là trade-off chi phí và điều tra/compliance.

**Lỗi sinh viên thường mắc:** Đặt retention vô hạn.

========================

========================

## Câu 125. Dashboard xanh nhưng user vẫn lỗi, vì sao?

**Đáp án mẫu:** Dashboard có thể thiếu metric, aggregate che lỗi, time range sai hoặc không đo user journey.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Thêm synthetic/SLO nào cho storefront?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi metric nào chứng minh checkout thành công.

**Điểm kiến thức cần nhớ:** Quan sát phải gắn symptom người dùng.

**Lỗi sinh viên thường mắc:** Dùng Pod Running làm availability SLI.

========================

========================

## Câu 126. Alert tốt cần những thành phần gì?

**Đáp án mẫu:** Symptom/SLO, threshold/window hợp lý, severity, owner, runbook và chống noise.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Multi-window burn-rate dùng khi nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi alert CPU cao có actionable không.

**Điểm kiến thức cần nhớ:** Alert phải dẫn đến hành động.

**Lỗi sinh viên thường mắc:** Alert mọi metric vượt ngưỡng tức thời.

========================

# Security

========================

## Câu 127. Secret nào không nên xuất hiện trong repo/workflow log?

**Đáp án mẫu:** Cloud token, kubeconfig, registry credential, DB password, Keycloak secret, private key và session/token.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nếu screenshot đã lộ token xử lý gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi rotation trước hay xóa history trước.

**Điểm kiến thức cần nhớ:** Secret exposure cần revoke/rotate ngay.

**Lỗi sinh viên thường mắc:** Chỉ blur ảnh nhưng không rotate.

========================

========================

## Câu 128. Gitleaks, CodeQL, Sonar và image scan khác nhau?

**Đáp án mẫu:** Gitleaks tìm secret; CodeQL/Sonar phân tích code/chất lượng; image scan tìm package CVE. Chúng bổ sung nhau.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Snyk có thể phủ thêm lớp nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Cho một vulnerability và hỏi công cụ phù hợp.

**Điểm kiến thức cần nhớ:** Không có scanner duy nhất bao phủ mọi lớp.

**Lỗi sinh viên thường mắc:** Chạy một tool rồi gọi pipeline secure.

========================

========================

## Câu 129. OIDC trust policy sai rộng có rủi ro gì?

**Đáp án mẫu:** Repository/branch khác có thể nhận credential cloud; phải giới hạn issuer, audience, repo, ref và environment.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** PR fork có claim gì khác?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi blast radius role cloud.

**Điểm kiến thức cần nhớ:** Federation vẫn cần least-privilege trust.

**Lỗi sinh viên thường mắc:** Bỏ PAT nhưng cho OIDC cluster-admin.

========================

========================

## Câu 130. Kubernetes RBAC khác Istio authz?

**Đáp án mẫu:** RBAC kiểm soát API Kubernetes; Istio AuthorizationPolicy kiểm soát service traffic.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** ServiceAccount app cần quyền API server không?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi ai cho phép order gọi product.

**Điểm kiến thức cần nhớ:** Control-plane auth và data-plane auth khác nhau.

**Lỗi sinh viên thường mắc:** Dùng ClusterRole để sửa lỗi HTTP 403 từ Envoy.

========================

========================

## Câu 131. Base64 Secret có che được credential khỏi Git không?

**Đáp án mẫu:** Không; decode tức thì và Git history vẫn giữ plaintext tương đương.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** External Secrets flow thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu decode thử một value mẫu không nhạy cảm.

**Điểm kiến thức cần nhớ:** Secret phải được mã hóa/quản lý ngoài Git.

**Lỗi sinh viên thường mắc:** Xem base64 là encryption.

========================

========================

## Câu 132. Image signature dùng để làm gì?

**Đáp án mẫu:** Xác minh provenance/integrity và policy chỉ cho image từ pipeline tin cậy chạy.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Signature có chứng minh không có CVE không?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi admission controller verify ở đâu.

**Điểm kiến thức cần nhớ:** Authenticity khác vulnerability status.

**Lỗi sinh viên thường mắc:** Ký image rồi bỏ scan và runtime hardening.

========================

========================

## Câu 133. Supply-chain risk từ GitHub Action là gì?

**Đáp án mẫu:** Action chạy code trên runner, có thể đọc token/source; tag bị chiếm hoặc dependency action bị compromise.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Runner self-host tăng rủi ro gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi vì sao pin SHA.

**Điểm kiến thức cần nhớ:** CI dependency là executable trust.

**Lỗi sinh viên thường mắc:** Dùng action ngẫu nhiên vì nhiều stars.

========================

========================

## Câu 134. mTLS giải quyết và không giải quyết gì?

**Đáp án mẫu:** Mã hóa, xác thực workload; không tự quyết quyền nghiệp vụ, không vá app vulnerability và không bảo vệ data at rest.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Authorization dựa principal nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi plaintext ngoài mesh xử lý ra sao.

**Điểm kiến thức cần nhớ:** Authentication, authorization và encryption là lớp khác nhau.

**Lỗi sinh viên thường mắc:** Bật mTLS rồi tuyên bố zero trust hoàn chỉnh.

========================

========================

## Câu 135. Pod securityContext production nên có gì?

**Đáp án mẫu:** runAsNonRoot, readOnlyRootFilesystem, allowPrivilegeEscalation false, drop capabilities, seccomp và UID/GID phù hợp.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** App cần ghi `/tmp` thì làm gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi field nằm container hay Pod level.

**Điểm kiến thức cần nhớ:** Hardening phải tương thích runtime.

**Lỗi sinh viên thường mắc:** Bật read-only rồi cho app crash hoặc tắt toàn bộ security.

========================

# Production

========================

## Câu 136. Ba khác biệt lớn giữa cấu hình local observability và production?

**Đáp án mẫu:** Production cần HA, auth/TLS, object storage/persistence, retention/capacity và backup; local hiện single-node/filesystem/insecure.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Ưu tiên nâng cấp thành phần nào trước?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi failure mode khi node mất.

**Điểm kiến thức cần nhớ:** Lab success không bằng production readiness.

**Lỗi sinh viên thường mắc:** Mang nguyên docker-compose config lên production.

========================

========================

## Câu 137. Tại sao DOKS managed control plane không loại bỏ trách nhiệm vận hành?

**Đáp án mẫu:** Nhóm vẫn quản workload, node pool, RBAC, network, data backup, upgrades compatibility, cost và observability.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** etcd backup thuộc ai trong DOKS?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi shared responsibility.

**Điểm kiến thức cần nhớ:** Managed service chuyển giao một phần, không toàn bộ.

**Lỗi sinh viên thường mắc:** Nói cloud chịu trách nhiệm mọi outage.

========================

========================

## Câu 138. ReplicaCount 1 có rủi ro gì?

**Đáp án mẫu:** Không HA khi Pod/node rollout hoặc failure; nhưng tăng replica cần app stateless và dependency capacity.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** PDB với một replica có ý nghĩa gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi topology spread và anti-affinity.

**Điểm kiến thức cần nhớ:** HA là end-to-end, không chỉ replica app.

**Lỗi sinh viên thường mắc:** Đặt replica 3 cho mọi service mà không tính DB/cost.

========================

========================

## Câu 139. Resource request/limit nên xác định thế nào?

**Đáp án mẫu:** Dựa load test, telemetry percentile, JVM headroom, QoS và node capacity; review định kỳ.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** CPU throttling quan sát bằng metric nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi vì sao request quá cao cũng gây Pending.

**Điểm kiến thức cần nhớ:** Right-sizing cân bằng reliability và cost.

**Lỗi sinh viên thường mắc:** Copy cùng resource cho mọi microservice.

========================

========================

## Câu 140. Database migration production cần chiến lược gì?

**Đáp án mẫu:** Append-only, backup, test, expand-migrate-contract, backward compatibility và tách destructive change.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Rollback app giữa migration xử lý thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi Liquibase checksum nếu sửa changeset cũ.

**Điểm kiến thức cần nhớ:** Schema phải hỗ trợ rolling deployment.

**Lỗi sinh viên thường mắc:** Sửa SQL changeset đã chạy.

========================

========================

## Câu 141. SLO cho storefront có thể là gì?

**Đáp án mẫu:** Tỷ lệ request/user journey thành công và latency p95/p99 trong cửa sổ, kèm error budget.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Pod availability có phải SLO người dùng không?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu định nghĩa numerator/denominator.

**Điểm kiến thức cần nhớ:** SLO đo outcome người dùng.

**Lỗi sinh viên thường mắc:** Chọn CPU dưới 80% làm SLO.

========================

========================

## Câu 142. Disaster recovery cần kiểm thử gì?

**Đáp án mẫu:** Backup restore DB/object storage, recreate cluster từ Git, secret recovery, DNS và RTO/RPO exercise.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** GitOps có backup dữ liệu không?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi RPO và RTO của YAS.

**Điểm kiến thức cần nhớ:** Manifest và data là hai lớp DR.

**Lỗi sinh viên thường mắc:** Có backup nhưng chưa từng restore.

========================

========================

## Câu 143. Production deployment nên dùng canary hay rolling khi nào?

**Đáp án mẫu:** Rolling cho rủi ro thấp; canary khi cần giới hạn blast radius và có metric/rollback tự động.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Istio route weight hỗ trợ canary thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi tiêu chí promote/abort.

**Điểm kiến thức cần nhớ:** Progressive delivery cần measurable guardrail.

**Lỗi sinh viên thường mắc:** Canary chỉ là chạy hai version không có analysis.

========================

========================

## Câu 144. Cost control trong project cần quan sát gì?

**Đáp án mẫu:** Node utilization/requests, LB, volume, log/metric cardinality-retention, egress và ephemeral environment TTL.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Xóa developer namespace tự động theo TTL thế nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi thành phần nào tăng cost khi scrape 2s.

**Điểm kiến thức cần nhớ:** Reliability và cost phải đo cùng nhau.

**Lỗi sinh viên thường mắc:** Chỉ scale node xuống mà bỏ qua scheduling headroom.

========================

# Troubleshooting

========================

## Câu 145. Argo CD Synced nhưng Degraded, thứ tự kiểm tra?

**Đáp án mẫu:** Application health message, workload status, Pod events, image pull, probes, config và dependency.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nếu image placeholder uppercase gây invalid reference thì thấy ở đâu?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Yêu cầu không sync lại mù quáng.

**Điểm kiến thức cần nhớ:** Bắt đầu từ controller status rồi xuống resource.

**Lỗi sinh viên thường mắc:** Bấm Sync liên tục.

========================

========================

## Câu 146. Pod CrashLoopBackOff, lệnh bằng chứng đầu tiên?

**Đáp án mẫu:** `kubectl describe pod` và `kubectl logs --previous`, sau đó kiểm tra exit reason, probes, config và dependency.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Exit 137 hướng điều tra nào?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi khác CrashLoop với Pending.

**Điểm kiến thức cần nhớ:** Thu log lần crash trước.

**Lỗi sinh viên thường mắc:** Xóa Pod trước khi đọc evidence.

========================

========================

## Câu 147. Service trả 404 từ root nhưng authz test nói ALLOW, giải thích?

**Đáp án mẫu:** 404 có thể do request đã qua Envoy đến app nhưng app không có route `/`; 403 RBAC mới là deny.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Làm sao xác định response từ Envoy hay app?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi access log và response body/header.

**Điểm kiến thức cần nhớ:** HTTP code phải hiểu theo layer.

**Lỗi sinh viên thường mắc:** Coi mọi non-200 là policy fail.

========================

========================

## Câu 148. Mesh tester default nhận 403, cần chứng minh gì?

**Đáp án mẫu:** Principal thực tế, product policy selector/rule, Envoy RBAC access log và caller có sidecar/mTLS.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Nếu nhận connection reset thay 403 thì nghi gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi test có chạy trong proxy container không.

**Điểm kiến thức cần nhớ:** Deny evidence cần đúng data path.

**Lỗi sinh viên thường mắc:** Chỉ chụp curl 403 không ghi caller identity.

========================

========================

## Câu 149. Deployment rollout treo ở readiness, kiểm tra gì?

**Đáp án mẫu:** Pod events/log, readiness endpoint từ trong Pod, port/path, startup time, dependency và resource throttling.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Tăng failureThreshold có che lỗi gì?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi readiness có trả traffic trước khi DB ready không.

**Điểm kiến thức cần nhớ:** Sửa probe dựa root cause, không chỉ nới timeout.

**Lỗi sinh viên thường mắc:** Tăng mọi timeout đến khi xanh.

========================

========================

## Câu 150. ImagePullBackOff cần kiểm tra gì?

**Đáp án mẫu:** Tên repository/tag, lowercase, image tồn tại, pull secret, registry auth/network và architecture.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Mutable tag cache có liên quan không?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi event exact message.

**Điểm kiến thức cần nhớ:** Kubelet event thường chỉ ra lớp lỗi.

**Lỗi sinh viên thường mắc:** Restart node trước khi đọc event.

========================

========================

## Câu 151. Prometheus không có target app, kiểm tra chuỗi nào?

**Đáp án mẫu:** ServiceMonitor selector/namespace selector, Service labels, named port/path, EndpointSlice, network/TLS và Prometheus selectors.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** ServiceMonitor tồn tại có nghĩa được Prometheus chọn không?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi targets page và operator log.

**Điểm kiến thức cần nhớ:** CR tồn tại chưa chắc controller consume.

**Lỗi sinh viên thường mắc:** Chỉ restart Prometheus.

========================

========================

## Câu 152. Kiali graph trống nhưng Prometheus up, bước kế tiếp?

**Đáp án mẫu:** Query `istio_requests_total`, tạo traffic, chọn đúng namespace/time range và kiểm tra telemetry labels.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Metric có data nhưng Kiali vẫn trống thì sao?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi datasource URL/config Kiali.

**Điểm kiến thức cần nhớ:** Phân biệt backend health với dữ liệu cần thiết.

**Lỗi sinh viên thường mắc:** Cài lại Istio ngay.

========================

========================

## Câu 153. Node memory requests 98%, Pod mới Pending, giải pháp có thứ tự?

**Đáp án mẫu:** Xác nhận requests/allocatable, right-size workload, dọn môi trường thừa, rollout tuần tự, rồi scale node pool nếu cần.

**Nếu sinh viên trả lời đúng, hỏi xoáy:** Tại sao restart nhiều JVM cùng lúc làm nặng hơn?

**Nếu sinh viên trả lời sai, sẽ bị hỏi:** Hỏi scheduler dựa requests hay usage.

**Điểm kiến thức cần nhớ:** Giảm áp lực có kiểm soát trước khi thêm capacity.

**Lỗi sinh viên thường mắc:** Scale mọi Deployment xuống 0 trong production.

========================
