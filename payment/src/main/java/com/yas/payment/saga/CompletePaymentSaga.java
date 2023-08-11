package com.yas.payment.saga;

import com.yas.payment.model.Payment;
import com.yas.payment.saga.data.CompletePaymentSagaData;
import com.yas.payment.saga.data.PaymentData;
import com.yas.payment.service.OrderMessageService;
import com.yas.payment.service.PaymentService;
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
public class CompletePaymentSaga implements SimpleSaga<CompletePaymentSagaData> {

    private final PaymentService paymentService;
    private final OrderMessageService orderMessageService;
    private final SagaDefinition<CompletePaymentSagaData> sagaDefinition =
        step()
            .invokeLocal(this::createPayment)
            .withCompensation(this::compensatePayment)
        .step()
            .invokeParticipant(this::updateCheckoutStatus)
            .onReply(UpdateCheckoutStatusSuccess.class, this::onUpdateCheckoutStatusSuccess)
            .onReply(CheckoutNotFound.class, (data, reply) -> log.warn(reply.message()))
        .step()
            .invokeParticipant(this::updateOrderPaymentStatus)
            .onReply(UpdateOrderPaymentStatusSuccess.class, this::onUpdateOrderPaymentStatusSuccess)
            .onReply(OrderNotFound.class, (data, reply) -> log.warn(reply.message()))
        .build();

    private void onUpdateCheckoutStatusSuccess(CompletePaymentSagaData data, UpdateCheckoutStatusSuccess success) {
        log.info("On update checkout status success with reply {}", success);
        data.setOrderId(success.orderId());
    }

    private CommandWithDestination updateCheckoutStatus(CompletePaymentSagaData data) {
        log.info("Update checkout status step with data {}", data);
        return orderMessageService.sendUpdateCheckoutStatusCommand(data.getPayment());
    }


    @Override
    public SagaDefinition<CompletePaymentSagaData> getSagaDefinition() {
        return this.sagaDefinition;
    }

    private void createPayment(CompletePaymentSagaData data) {
        Payment payment = this.paymentService.createPayment(data.getCapturedPayment());
        data.setPayment(PaymentData.from(payment));
    }

    private void compensatePayment(CompletePaymentSagaData data) {
        log.info("Compensate Payment");
    }

    private CommandWithDestination updateOrderPaymentStatus(CompletePaymentSagaData data) {
        log.info("Update order payment status step with data {}", data);
        return orderMessageService.sendUpdateOrderPaymentStatusCommand(data.getOrderId(), data.getPayment());
    }

    private void onUpdateOrderPaymentStatusSuccess(CompletePaymentSagaData data, UpdateOrderPaymentStatusSuccess success) {
        log.info("Order id {} has been moved to {} status", success.orderId(), success.orderStatus());
    }
}
