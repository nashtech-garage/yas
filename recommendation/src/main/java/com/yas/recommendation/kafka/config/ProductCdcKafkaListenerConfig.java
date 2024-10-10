package com.yas.recommendation.kafka.config;

import com.yas.recommendation.kafka.message.ProductCdcMessage;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Product CDC kafka listener, support convert product cdc message to java object.
 */
@EnableKafka
@Configuration
public class ProductCdcKafkaListenerConfig extends BaseKafkaListenerConfig<ProductCdcMessage> {

    public ProductCdcKafkaListenerConfig(KafkaProperties kafkaProperties) {
        super(ProductCdcMessage.class, kafkaProperties);
    }

    @Bean("productCdcListenerContainerFactory")
    @Override
    public ConcurrentKafkaListenerContainerFactory<String, ProductCdcMessage> listenerContainerFactory() {
        return super.kafkaListenerContainerFactory();
    }

}
