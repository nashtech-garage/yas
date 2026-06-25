package com.yas.payment.paypal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@SpringBootTest(
    useMainMethod = SpringBootTest.UseMainMethod.ALWAYS,
    properties = {
        "yas.public.url=http://api.yas.local.com/payment-paypal",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=test"
    }
)
class PaymentPaypalApplicationTest {

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void contextLoads() {
        // It is supposed to be empty
    }

}
