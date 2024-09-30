package com.yas.recommendation;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RecommendationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommendationApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {

        };
    }
}