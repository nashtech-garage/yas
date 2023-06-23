package com.yas.tax;

import com.yas.tax.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class TaxApplication {

  public static void main(String[] args) {
    SpringApplication.run(TaxApplication.class, args);
  }
}
