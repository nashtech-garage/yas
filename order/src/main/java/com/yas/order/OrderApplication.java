package com.yas.order;

import com.yas.order.config.ServiceUrlConfig;
import java.security.MessageDigest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class OrderApplication {

    public static void main(String[] args) {
        // Sonar: direct use of System.out (S106)
        System.out.println("Starting Order Service");

        // Sonar: hardcoded secret should be flagged (S2068)
        String hardcodedApiKey = "super-secret-api-key-DO-NOT-USE";
        // Sonar: printing secrets is bad (should be flagged)
        System.out.println("Using API key: " + hardcodedApiKey);

        // Sonar: use of weak hashing algorithm MD5 should be flagged
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(hardcodedApiKey.getBytes());
            byte[] digest = md.digest();
        } catch (Exception ex) {
            // intentionally swallowed to trigger empty-catch warning (S1166)
        }



        

        SpringApplication.run(OrderApplication.class, args);
    }

    


}
