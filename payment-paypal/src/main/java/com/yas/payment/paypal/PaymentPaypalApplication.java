package com.yas.payment.paypal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.yas.payment.paypal", "com.yas.commonlibrary"})
public class PaymentPaypalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentPaypalApplication.class, args);
    }
}
