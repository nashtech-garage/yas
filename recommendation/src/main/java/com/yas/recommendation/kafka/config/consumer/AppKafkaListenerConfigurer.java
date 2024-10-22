package com.yas.recommendation.kafka.config.consumer;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@EnableKafka
@Configuration
public class AppKafkaListenerConfigurer implements KafkaListenerConfigurer {

    private LocalValidatorFactoryBean validator;

    public AppKafkaListenerConfigurer(LocalValidatorFactoryBean validator) {
        this.validator = validator;
    }

    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        // Enable message validation
        registrar.setValidator(this.validator);
    }
}
