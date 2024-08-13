package com.yas.sampledata;

import com.yas.sampledata.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class SampleDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleDataApplication.class, args);
    }
}
