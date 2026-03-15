package com.yas.order;

import com.yas.order.config.ServiceUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class OrderApplication {

    public static void main(String[] args) {
        System.out.println("Starting Order Service"); // Sonar sẽ cảnh báo
        SpringApplication.run(OrderApplication.class, args);
    }

    
    
    public void implementOrderService() {
        // Implement order service logic here
    }

}
