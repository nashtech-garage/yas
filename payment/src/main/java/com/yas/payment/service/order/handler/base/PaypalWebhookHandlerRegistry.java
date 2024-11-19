package com.yas.payment.service.order.handler.base;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.payment.paypal.model.PaypalWebhookEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class PaypalWebhookHandlerRegistry implements IPaypalWebhookHandlerRegistry<PaypalWebhookEvent> {

    private final Map<String, IPaypalWebhookHandler<PaypalWebhookEvent>> registry = new HashMap<>();

    public IPaypalWebhookHandler<PaypalWebhookEvent> get(String type) {
        IPaypalWebhookHandler<PaypalWebhookEvent> handler = this.registry.get(type);
        if (Objects.isNull(handler)) {
            throw new NotFoundException("MessageType {} is not supported", type);
        }
        return handler;
    }

    @Override
    public void register(String type, IPaypalWebhookHandler<PaypalWebhookEvent> handler) {
        this.registry.put(type, handler);
    }
}
