package com.yas.payment.flow;

import com.yas.payment.flow.handler.CapturePaymentHandler;
import com.yas.payment.viewmodel.CapturedPayment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.MessageChannel;

@Configuration
public class IntegrationFlows {
    @Bean
    public MessageChannel createPaymentMessageChanel() {
        DirectChannel directChannel = new DirectChannel();
        directChannel.setDatatypes(Double.class);
        return directChannel;
    }

    @Bean
    public IntegrationFlow sendToCreatePaymentTopicFlow(
            ProducerFactory<?, ?> producerFactory,
            MessageChannel capturePaymentMessageChanel
    ) {
        return IntegrationFlow.from(capturePaymentMessageChanel)
                .handle(Kafka.outboundChannelAdapter(producerFactory)
                        .topic("yas.payment.create-payment"))
                .get();
    }

    @Bean
    public IntegrationFlow listenCapturePaymentFlow(ConsumerFactory<?, ?> kafkaConsumerFactory,
                                                    CapturePaymentHandler handler) {
        return IntegrationFlow
                .from(Kafka.messageDrivenChannelAdapter(kafkaConsumerFactory, "yas.payment.capture-payment"))
                .transform(Transformers.fromJson(CapturedPayment.class))
                .handle(handler)
                .get();
    }
}
