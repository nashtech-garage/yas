package com.yas.rating;

import com.yas.commonlibrary.config.CorsConfig;
import com.yas.rating.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.yas.rating", "com.yas.commonlibrary"})
@EnableConfigurationProperties({ServiceUrlConfig.class, CorsConfig.class})
public class RatingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RatingApplication.class, args);
    }
}
