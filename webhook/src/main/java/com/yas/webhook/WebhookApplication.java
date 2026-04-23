package com.yas.webhook;

import com.yas.commonlibrary.config.CorsConfig;
import com.yas.webhook.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.yas.webhook", "com.yas.commonlibrary"})
@EnableConfigurationProperties({ServiceUrlConfig.class, CorsConfig.class})
public class WebhookApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebhookApplication.class, args);
    }

}
