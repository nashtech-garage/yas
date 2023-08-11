package com.yas.paymentpaypal.flow;

import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.MessageChannel;

@Configuration
public class IntegrationFlows {

    @Bean
    public MessageChannel capturePaymentMessageChanel() {
        DirectChannel directChannel = new DirectChannel();
        directChannel.setDatatypes(CapturedPaymentVm.class);
        return directChannel;
    }

    @Bean
    public IntegrationFlow sendToCapturePaymentTopicFlow(
            ProducerFactory<?, ?> producerFactory,
            MessageChannel capturePaymentMessageChanel
    ) {
       return IntegrationFlow.from(capturePaymentMessageChanel)
               .handle(Kafka.outboundChannelAdapter(producerFactory)
                       .topic("yas.payment.capture-payment"))
               .get();
    }
}
