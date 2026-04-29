package com.yas.customer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomerApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void testMain() {
        CustomerApplication.main(new String[]{});
    }
}
