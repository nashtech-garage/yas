package com.yas.customer;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import com.yas.customer.CustomerApplication;

class CustomerApplicationTest {

    @Test
    void testMain() {
        // Sử dụng MockedStatic để "chặn" không cho Spring Boot thực sự khởi động context (tránh lỗi DB/Keycloak)
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            
            // Giả lập hành vi: Khi ai đó gọi SpringApplication.run thì đừng làm gì cả
            mocked.when(() -> SpringApplication.run(CustomerApplication.class, new String[]{}))
                    .thenReturn(null);

            // Thực thi hàm main của chúng ta
            CustomerApplication.main(new String[]{});

            // Xác nhận (Assert) rằng lệnh SpringApplication.run thực sự đã được gọi ở bên trong hàm main
            mocked.verify(() -> SpringApplication.run(CustomerApplication.class, new String[]{}));
        }
    }
}