package com.yas.recommendation.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class RecommendationConfig {

    @Value("${yas.services.product}")
    private String apiUrl;

}
