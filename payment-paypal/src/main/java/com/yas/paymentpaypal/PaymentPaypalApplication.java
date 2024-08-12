package com.yas.paymentpaypal;

import com.yas.paymentpaypal.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class PaymentPaypalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentPaypalApplication.class, args);
    }
}
