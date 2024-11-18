package com.yas.payment.service.order.handler.base;

import com.yas.payment.paypal.model.PaypalWebhookEvent;

public interface IPaypalWebhookHandler<T extends PaypalWebhookEvent> {

    /**
     * Processes the webhook payload.
     *
     * @param payload the webhook payload of type PaypalWebhookEvent
     */
    void handle(T payload);

}
