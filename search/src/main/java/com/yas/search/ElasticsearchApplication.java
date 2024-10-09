package com.yas.search;

import com.yas.search.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@EnableConfigurationProperties(ServiceUrlConfig.class)
@SpringBootApplication(scanBasePackages = {"com.yas.search", "com.yas.commonlibrary"})
@Configuration
public class ElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchApplication.class, args);
    }
}
