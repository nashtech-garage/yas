## Hướng dẫn đăng nhập

Để đăng nhập vào hệ thống, bạn cần thực hiện các bước sau:

1. Truy cập vào trang web của hệ thống.
2. Nhập thông tin đăng nhập bao gồm:
   - Tên đăng nhập admin
   - Mật khẩu 123456
3. Nhấn nút "Đăng nhập" để hoàn tất quá trình đăng nhập.

Nếu bạn quên mật khẩu, bạn có thể sử dụng chức năng "Quên mật khẩu" để khôi phục lại mật khẩu của mình.

## Chạy Jenkins kèm Node.js (để test frontend)

Jenkinsfile hiện tại có thể chạy npm cho các service frontend. Để Jenkins container có sẵn npm:

1. Mở terminal tại thư mục `docker-jenkins`.
2. Build lại image Jenkins:
   - `docker compose build --no-cache jenkins`
3. Khởi động lại service Jenkins:
   - `docker compose up -d jenkins`
4. Kiểm tra npm trong container:
   - `docker exec -it jenkins_master npm -v`

Sau đó chạy lại pipeline, frontend services (`backoffice`, `storefront`) sẽ không còn bị skip do thiếu npm.
   - Tên đăng nhập admin    
   - Mật khẩu 123456
3. Nhấn nút "Đăng nhập" để hoàn tất quá trình đăng nhập.

Nếu bạn quên mật khẩu, bạn có thể sử dụng chức năng "Quên mật khẩu" để khôi phục lại mật khẩu của mình.
