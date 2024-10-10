package com.yas.recommendation.kafka.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;

@EnableKafka
@Configuration
public class KafkaListenerErrorHandlerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaListenerErrorHandlerConfig.class);

    @Bean
    public KafkaListenerErrorHandler validationErrorHandler() {
        return (m, e) -> {
            LOGGER.info("Message validation fail {}", m.getHeaders());
            return e;
        };
    }

}
