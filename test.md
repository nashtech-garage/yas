Mình sẽ đưa cho bạn một bộ test end-to-end theo dạng checklist chấm điểm, gồm từng case cụ thể, lệnh thao tác, expected log và bằng chứng cần chụp để chứng minh pass Task 2 khi demo.Để pass Task 2 chắc chắn, bạn nên test theo bộ 7 case dưới đây và lưu bằng chứng cho từng case.

Phần pipeline đang cần verify nằm ở Jenkinsfile, Jenkinsfile, Jenkinsfile, Jenkinsfile, Jenkinsfile.

Lưu ý trước khi test:

1. Project dùng Java 25 ở pom.xml, còn Jenkins tool đang là jdk21 ở Jenkinsfile. Nếu không đồng bộ JDK, bạn fail do build chứ không phải fail logic Task 2.
2. Bạn đang có git add toàn bộ. Nên tách branch test riêng để dễ kiểm soát bằng chứng.

Kịch bản test chuẩn để pass task

1. Chuẩn bị branch test
   Chạy:
   git checkout -b test/task2-jenkins
   git status
   Kết quả mong đợi:
   - Working tree sạch hoặc chỉ chứa thay đổi bạn muốn test.

2. Case A: Không có thay đổi service thì skip build
   Mục tiêu:
   - CHANGED_SERVICES là none.
   - Stage Build không chạy.
     Cách làm:
     git checkout main
     git checkout -b test/task2-none
     echo "task2 none test" >> README.md
     git add README.md
     git commit -m "test: docs only"
     git push -u origin test/task2-none
     Kết quả mong đợi trên Jenkins:
   - Có log No service changes detected. Skipping build/test.
   - Build Phase bị skip.
     Bằng chứng:
   - Ảnh log detect stage.
   - Ảnh stage view cho thấy Build skipped.

3. Case B: Chỉ đổi 1 service thì chỉ build 1 service
   Mục tiêu:
   - Chỉ có 1 service trong Services to build/test.
   - Chỉ có 1 vòng lặp Building.
     Cách làm ví dụ với product:
     git checkout -b test/task2-product-only
     echo "// task2 single service test" >> ProductApplication.java
     git add ProductApplication.java
     git commit -m "test: product only change"
     git push -u origin test/task2-product-only
     Kết quả mong đợi:
   - Log có Services to build/test: product.
   - Log có Building: product.
   - Không có Building của service khác.
   - Có artifact của product trong Archived Artifacts.
     Bằng chứng:
   - Ảnh log detect.
   - Ảnh log build.
   - Ảnh archived artifact.

4. Case C: Đổi 2 service thì chỉ build đúng 2 service
   Cách làm:
   git checkout -b test/task2-two-services
   echo "// task2 multi service test" >> ProductApplication.java
   echo "// task2 multi service test" >> OrderApplication.java
   git add ProductApplication.java OrderApplication.java
   git commit -m "test: product and order change"
   git push -u origin test/task2-two-services
   Kết quả mong đợi:
   - Services to build/test chứa product,order.
   - Build chỉ có product và order.
   - Artifact chỉ xuất hiện cho 2 service này.
     Bằng chứng:
   - Ảnh log detect và build.
   - Ảnh archived artifact.

5. Case D: Đổi file nền tảng thì build all services
   Rule này nằm ở Jenkinsfile.
   Cách làm:
   git checkout -b test/task2-build-all
   echo "<!-- task2 build all test -->" >> pom.xml
   git add pom.xml
   git commit -m "test: root pom change"
   git push -u origin test/task2-build-all
   Kết quả mong đợi:
   - Services to build/test là danh sách allBuildableServices.
   - Build lặp qua toàn bộ service backend.
     Bằng chứng:
   - Ảnh log detect thể hiện danh sách dài.
   - Ảnh log build nhiều service.

6. Case E: Verify artifact cho Maven và Gradle path
   Mục tiêu:
   - Archive đúng pattern tại Jenkinsfile.
     Kiểm tra:
   - target/_.jar hoặc target/_.war xuất hiện cho Maven service.
   - Nếu có Gradle service thì build/libs/_.jar hoặc _.war.
     Bằng chứng:
   - Ảnh trang Archived Artifacts.

7. Case F: Verify đường script của anh Lộc hoạt động
   Pipeline ưu tiên script ở Jenkinsfile.
   Vì repo hiện chưa có file scripts detect chính thức, bạn test theo 2 bước:
   1. Chưa có script:
   - Jenkins dùng fallback git diff.
   2. Có script:
   - Tạo scripts/detect-changed-services.sh và cho script echo ra 3 mode để thử:
     - none
     - all
     - product,order
       Kết quả mong đợi:
   - Log có Detect script output.
   - CHANGED_SERVICES đúng theo output script.
     Bằng chứng:
   - Ảnh log từng mode.

Checklist chấm pass Task 2

1. Có ít nhất 1 case skip hoàn toàn Build Phase.
2. Có case build 1 service và không build service khác.
3. Có case build nhiều service đúng theo diff.
4. Có case build all khi đổi file nền tảng.
5. Có archived artifacts đúng service đã build.
6. Có bằng chứng script mode và fallback mode đều chạy đúng.

Bộ bằng chứng nên nộp

1. Ảnh Stage View cho 4 case chính: none, one, multi, all.
2. Ảnh log đoạn Services to build/test.
3. Ảnh log đoạn Building.
4. Ảnh Archived Artifacts.
5. Link commit/branch của từng case test.

Nếu bạn muốn, mình có thể viết luôn một file test plan dạng markdown trong repo để bạn nộp trực tiếp cho giảng viên, gồm template kết quả Pass/Fail cho từng case.
