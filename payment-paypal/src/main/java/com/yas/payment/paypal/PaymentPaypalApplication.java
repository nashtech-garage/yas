package com.yas.payment.paypal;

import com.yas.commonlibrary.config.CorsConfig;
import com.yas.commonlibrary.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.yas.payment.paypal", "com.yas.commonlibrary"})
@EnableConfigurationProperties({ServiceUrlConfig.class, CorsConfig.class})
public class PaymentPaypalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentPaypalApplication.class, args);
    }
}
