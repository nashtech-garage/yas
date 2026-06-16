package com.yas.order;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;
import org.springframework.boot.SpringApplication;

class OrderApplicationTest {

    @Test
    void testMain() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(OrderApplication.class, new String[]{}))
                    .thenReturn(null);

            OrderApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(OrderApplication.class, new String[]{}));
        }
    }
}
