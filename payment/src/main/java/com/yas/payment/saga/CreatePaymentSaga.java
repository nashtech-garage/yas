package com.yas.payment.saga;

import com.yas.payment.model.Payment;
import com.yas.payment.saga.data.CompletePaymentSagaData;
import com.yas.payment.saga.data.CreatePaymentSagaData;
import com.yas.payment.saga.data.PaymentData;
import com.yas.payment.service.OrderMessageService;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.PaymentRequest;
import com.yas.saga.order.reply.CheckoutNotFound;
import com.yas.saga.order.reply.OrderNotFound;
import com.yas.saga.order.reply.UpdateCheckoutStatusSuccess;
import com.yas.saga.order.reply.UpdateOrderPaymentStatusSuccess;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatePaymentSaga implements SimpleSaga<CreatePaymentSagaData> {

    private final PaymentService paymentService;
    private final SagaDefinition<CreatePaymentSagaData> sagaDefinition =
            step()
                .invokeLocal(this::createPayment)
                .onReply(CreatePaymentSuccess.class, this::capturePayment)
                .onReply(CreatePaymentSuccessFail.class, (data, reply) -> log.warn(reply.message()))
            .step()
                .invokeLocal(this::capturePayment)
                .withCompensation(this::compensatePayment)
            .build();

    private void createPayment(CreatePaymentSagaData data) {
        log.info("create Payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .totalPrice(data.getTotalPrice())
                .paymentMethod(data.getPaymentMethod())
                .build();
        this.paymentService.createPayment(paymentRequest);
    }

    private void compensatePayment(CreatePaymentSagaData data) {
        log.info("Compensate Payment");
    }

    private void capturePayment(CreatePaymentSagaData data) {
        this.paymentService.capturePayment(data.getCapturedPayment());
    }
}
