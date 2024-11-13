package com.yas.recommendation.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class RecommendationConfig {

    @Value("${yas.services.order}")
    private String orderUrl;

    @Value("${yas.services.cart}")
    private String cartUrl;

    @Value("${yas.services.product}")
    private String productUrl;

}
