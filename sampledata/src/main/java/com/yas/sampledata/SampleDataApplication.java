package com.yas.sampledata;

import com.yas.commonlibrary.config.CorsConfig;
import com.yas.commonlibrary.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},
    scanBasePackages = {"com.yas.sampledata", "com.yas.commonlibrary"})
@EnableConfigurationProperties({ServiceUrlConfig.class, CorsConfig.class})
public class SampleDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleDataApplication.class, args);
    }
}