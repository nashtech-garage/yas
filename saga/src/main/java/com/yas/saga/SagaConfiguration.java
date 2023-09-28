package com.yas.saga;

import io.eventuate.messaging.kafka.basic.consumer.DefaultKafkaConsumerFactory;
import io.eventuate.messaging.kafka.basic.consumer.KafkaConsumerFactory;
import io.eventuate.tram.spring.optimisticlocking.OptimisticLockingDecoratorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(OptimisticLockingDecoratorConfiguration.class)
public class SagaConfiguration {
    @Bean
    public KafkaConsumerFactory kafkaEventuateConsumerFactory() {
        return new DefaultKafkaConsumerFactory();
    }
}
