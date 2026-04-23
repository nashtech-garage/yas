package com.yas.webhook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.ByteArrayJacksonJsonMessageConverter;
import org.springframework.kafka.support.converter.JacksonJsonMessageConverter;

@Configuration
public class KafkaConfig {

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new ByteArrayJacksonJsonMessageConverter();
    }
}
