package com.yas.search.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yas.search.constant.Action;
import com.yas.search.service.ProductSyncDataService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductSyncDataConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    @Autowired
    private ProductSyncDataService productSyncDataService;

    @KafkaListener(topics = "${product.topic.name}")
    public void listen(ConsumerRecord<?, ?> consumerRecord) {

        if (consumerRecord != null) {
            JsonObject keyObject = new Gson().fromJson((String) consumerRecord.key(), JsonObject.class);
            if (keyObject != null) {
                JsonObject valueObject = new Gson().fromJson((String) consumerRecord.value(), JsonObject.class);
                if (valueObject != null) {
                    String action = String.valueOf(valueObject.get("op")).replaceAll("\"", "");
                    Long id = keyObject.get("id").getAsLong();

                    switch (action) {
                        case Action.CREATE, Action.READ:
                            productSyncDataService.createProduct(id);
                            break;
                        case Action.UPDATE:
                            productSyncDataService.updateProduct(id);
                            break;
                        case Action.DELETE:
                            productSyncDataService.deleteProduct(id);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
