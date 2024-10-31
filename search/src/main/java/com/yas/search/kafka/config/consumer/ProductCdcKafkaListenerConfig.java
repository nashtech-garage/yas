package com.yas.search.kafka.config.consumer;

import com.yas.commonlibrary.kafka.cdc.config.BaseKafkaListenerConfig;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

/**
 * Product CDC kafka listener, support convert product cdc message to java object.
 */
@EnableKafka
@Configuration
public class ProductCdcKafkaListenerConfig extends BaseKafkaListenerConfig<ProductMsgKey, ProductCdcMessage> {

    public static final String PRODUCT_CDC_LISTENER_CONTAINER_FACTORY = "productCdcListenerContainerFactory";

    public ProductCdcKafkaListenerConfig(KafkaProperties kafkaProperties) {
        super(ProductMsgKey.class, ProductCdcMessage.class, kafkaProperties);
    }

    @Bean(name = PRODUCT_CDC_LISTENER_CONTAINER_FACTORY)
    @Override
    public ConcurrentKafkaListenerContainerFactory<ProductMsgKey, ProductCdcMessage> listenerContainerFactory() {
        return super.kafkaListenerContainerFactory();
    }

}