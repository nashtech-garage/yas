package com.yas.payment.paypal;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class PaymentPaypalApplicationTest {

    @Test
    void mainRunsSpringApplication() {
        String[] args = {"--spring.main.web-application-type=none"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            PaymentPaypalApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(PaymentPaypalApplication.class, args));
        }
    }
}
