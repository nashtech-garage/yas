package com.yas.payment.service.order.handler.base;

import com.yas.payment.paypal.model.PaypalWebhookEvent;

public interface IPaypalWebhookHandlerRegistry<T extends PaypalWebhookEvent> {

    IPaypalWebhookHandler<T> get(String type);

    void register(String type, IPaypalWebhookHandler<T> handler);
}
