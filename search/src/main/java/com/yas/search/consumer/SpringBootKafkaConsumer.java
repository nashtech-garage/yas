package com.yas.search.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.yas.search.service.ProductESService;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import com.yas.search.viewmodel.ProductESDetailVm;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SpringBootKafkaConsumer {
    @Autowired
    private  ProductESService productESService;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    private CountDownLatch latch = new CountDownLatch(1);
//    public ProductESService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
//        this.webClient = webClient;
//        this.serviceUrlConfig = serviceUrlConfig;
//    }

    private Object payload2;

    @KafkaListener(topics = "dbserver1.public.product")
    public void listen(ConsumerRecord<?, ?> consumerRecord)
    {
        LOGGER.info("received payload='{}'", consumerRecord.toString());
        payload2 = consumerRecord.toString();
        JsonObject convertedObject = new Gson().fromJson((String) consumerRecord.key(), JsonObject.class);
        Long id = convertedObject.get("id").getAsLong();
        ProductESDetailVm productESDetailVm = productESService.getProductESDetailById(id);
        LOGGER.info("productESDetailVm='{}'", productESDetailVm);
    }

}
