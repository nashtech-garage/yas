package com.yas.payment.flow;

import com.yas.payment.flow.handler.CapturePaymentHandler;
import com.yas.payment.viewmodel.CapturedPayment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class IntegrationFlows {
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
