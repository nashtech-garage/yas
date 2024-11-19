package com.yas.payment.service.order.handler;

import static com.yas.payment.paypal.model.enumeration.PaypalPaymentStatus.PAYMENT_CAPTURE_REVERSED;

import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.paypal.model.PaypalWebhookEvent;
import com.yas.payment.paypal.model.enumeration.PaypalPaymentStatus;
import com.yas.payment.service.PaymentService;
import com.yas.payment.service.order.handler.base.IPaypalWebhookHandlerRegistry;
import com.yas.payment.service.order.handler.base.PaypalWebhookHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReversedCaptureHandler extends PaypalWebhookHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReversedCaptureHandler.class);

    public ReversedCaptureHandler(
        IPaypalWebhookHandlerRegistry<PaypalWebhookEvent> registry,
        PaymentService paymentService
    ) {
        super(registry, paymentService, LOGGER);
    }

    @Override
    protected PaypalPaymentStatus providePaypalPaymentStatus() {
        return PAYMENT_CAPTURE_REVERSED;
    }

    @Override
    protected PaymentStatus getPaymentStatus() {
        return PaymentStatus.FAILURE;
    }

}
