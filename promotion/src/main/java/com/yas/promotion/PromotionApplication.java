package com.yas.promotion;

import com.yas.commonlibrary.config.CorsConfig;
import com.yas.promotion.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.yas.promotion", "com.yas.commonlibrary"})
@EnableConfigurationProperties({ServiceUrlConfig.class, CorsConfig.class})
public class PromotionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromotionApplication.class, args);
    }
}
