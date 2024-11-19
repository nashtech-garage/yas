package com.yas.payment.service.order.handler;

import static com.yas.payment.paypal.model.enumeration.PaypalPaymentStatus.PAYMENT_AUTHORIZATION_VOIDED;

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
public class VoidedAuthorizationHandler extends PaypalWebhookHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(VoidedAuthorizationHandler.class);

    public VoidedAuthorizationHandler(
        IPaypalWebhookHandlerRegistry<PaypalWebhookEvent> registry,
        PaymentService paymentService
    ) {
        super(registry, paymentService, LOGGER);
    }

    @Override
    protected PaypalPaymentStatus providePaypalPaymentStatus() {
        return PAYMENT_AUTHORIZATION_VOIDED;
    }

    @Override
    protected PaymentStatus getPaymentStatus() {
        return PaymentStatus.FAILURE;
    }

}
