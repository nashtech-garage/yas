package com.yas.search.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.yas.search.service.ProductSyncDataService;
import org.springframework.beans.factory.annotation.Autowired;

import com.yas.search.constants.Action;


@Service
public class ProductSyncDataConsumer {
    @Autowired
    private ProductSyncDataService productSyncDataService;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "${product.topic.name}")
    public void listen(ConsumerRecord<?, ?> consumerRecord) {

        if(consumerRecord != null){
            JsonObject convertedObject = new Gson().fromJson((String) consumerRecord.key(), JsonObject.class);
            if(convertedObject != null){
                JsonObject convertedObjectRoot = new Gson().fromJson((String) consumerRecord.value(), JsonObject.class);
                if(convertedObjectRoot != null){
                    String action = String.valueOf(convertedObjectRoot.get("op")).replaceAll("\"", "");
                    Long id = convertedObject.get("id").getAsLong();

                    switch (action) {
                        case Action.CREATE:
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
