package com.yas.order.viewmodel.payment;

import lombok.Builder;

@Builder(toBuilder = true)
public record PaymentProviderVm (
    String id,
    String name,
    String configureUrl,
    String additionalSettings
) {
}
