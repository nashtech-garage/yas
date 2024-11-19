package com.yas.payment.service.order.handler.base;

import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.paypal.model.PaypalWebhookEvent;
import com.yas.payment.paypal.model.enumeration.PaypalPaymentStatus;
import com.yas.payment.service.PaymentService;
import org.slf4j.Logger;

public abstract class PaypalWebhookHandler implements IPaypalWebhookHandler<PaypalWebhookEvent> {

    private final Logger logger;
    protected final PaymentService paymentService;

    protected PaypalWebhookHandler(
        IPaypalWebhookHandlerRegistry<PaypalWebhookEvent> registry,
        PaymentService paymentService,
        Logger logger
    ) {
        registry.register(this.providePaypalPaymentStatus().getStatus(), this);
        this.paymentService = paymentService;
        this.logger = logger;
    }

    protected abstract PaypalPaymentStatus providePaypalPaymentStatus();

    protected abstract PaymentStatus getPaymentStatus();

    @Override
    public void handle(PaypalWebhookEvent payload) {

        logger.info("Start handling for status {}, Update Payment Status to {}",
            providePaypalPaymentStatus(), getPaymentStatus());
        Payment payment = paymentService.findPaymentByPaypalOrderId(payload.getResource().getId());
        payment.setPaymentStatus(getPaymentStatus());
        paymentService.updatePayment(payment);
        logger.info("End webhook from PayPal: {}", providePaypalPaymentStatus());

    }

}
