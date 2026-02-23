package com.yas.customer;

import com.yas.commonlibrary.config.CorsConfig;
import com.yas.customer.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = { "com.yas.customer", "com.yas.commonlibrary" })
@EnableConfigurationProperties({ ServiceUrlConfig.class, CorsConfig.class })
public class CustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}
