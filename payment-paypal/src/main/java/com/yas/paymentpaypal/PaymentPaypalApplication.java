package com.yas.paymentpaypal;

import com.yas.commonlibrary.config.CorsConfig;
import com.yas.paymentpaypal.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.yas.paymentpaypal", "com.yas.commonlibrary"})
@EnableConfigurationProperties({ServiceUrlConfig.class, CorsConfig.class})
public class PaymentPaypalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentPaypalApplication.class, args);
    }
}
