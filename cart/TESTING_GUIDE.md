# Hướng dẫn Setup và Chạy Unit Test Dự Án YAS (Cart Service)

Tài liệu này cung cấp cái nhìn tổng quan về cách Unit Test được thiết lập cho `cart-service`, cũng như hướng dẫn chi tiết để Developer clone code về có thể chạy thử nghiệm và tích hợp quy trình này lên Jenkins CI/CD.

## 1. Yêu cầu & Cấu hình
Dự án được xây dựng dựa trên **Java 21**, spring-boot 3.x. Để phục vụ việc test:
1.  **Cập nhật Parent POM (`yas/pom.xml`)**: Đã nâng cấp `lombok` từ phiên bản `1.18.34` lên `1.18.36`. Điều này đảm bảo tương thích khi build với một số JDK mới (như JDK 25), tránh lỗi `ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag`.
2.  **Cập nhật Module POM (`yas/cart/pom.xml`)**: 
    - Bổ sung thư viện `spring-boot-starter-test`.
    - Thêm `jacoco-maven-plugin` để đo lường độ phủ Code (Code Coverage). Báo cáo HTML tự động tạo ra sau quá trình test tại thư mục `cart/target/site/jacoco/index.html`.
3.  **Unit Tests**: Code test được đặt chuẩn ở `cart/src/test/java/com/yas/cart/`. Độ bao phủ tính tới hiện tại cho các package chủ chốt (controller, service) đã hoàn toàn **đạt trên 70%** theo yêu cầu ban đầu.

## 2. Cách chạy Unit Test dưới Local (Sử dụng Docker)
Cách ổn định và đảm bảo nhất (đặc biệt khi version Java cài đặt trên máy cá nhân khác với Java 21) là sử dụng Maven Docker image.

Từ **thư mục gốc** của dự án (thư mục chứa `cart`, `pom.xml`, `mvnw`...):

**Đối với Linux / Mac / Git Bash / PowerShell:**
```bash
docker run -it --rm \
  -v "${PWD}:/usr/src/mymaven" \
  -w /usr/src/mymaven \
  maven:3.9.9-eclipse-temurin-21 \
  mvn test -B -pl cart -am
```

**Đối với Windows CMD:**
```cmd
docker run -it --rm -v "%cd%:/usr/src/mymaven" -w /usr/src/mymaven maven:3.9.9-eclipse-temurin-21 mvn test -B -pl cart -am
```

## 3. Tích hợp lên Jenkins (CI/CD)
Trên các hệ thống CI/CD, để đảm bảo tính cô lập, Jenkinsfile nên sử dụng Docker làm agent chạy build.

### Ví dụ mô hình Jenkinsfile (Khuyên dùng)
```groovy
pipeline {
    agent {
        docker {
            image 'maven:3.9.9-eclipse-temurin-21'
            args '-v $HOME/.m2:/root/.m2' // (Tùy chọn) mount folder cache maven
        }
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build & Test Cart Service') {
            steps {
                sh 'mvn test -B -pl cart -am'
            }
        }
    }
    post {
        always {
            // Jenkins lưu lại biểu đồ Unit Test bằng Plugin JUnit
            junit 'cart/target/surefire-reports/**/*.xml'
            // Jenkins xuất report coverage từ JaCoCo plugin
            archiveArtifacts artifacts: 'cart/target/site/jacoco/**', allowEmptyArchive: true
        }
    }
}
```

Với mô hình trên, bất cứ khi nào code được Pull Request / Merge vào nhánh chính (`test/cart`, `main`), hệ thống CI/CD sẽ tự động chạy các suite test và trả về report đảm bảo chất lượng hệ thống (Coverage > 70%).
