package com.yas.payment.viewmodel;

import com.yas.payment.model.PaymentProvider;
import lombok.Builder;

@Builder
public record PaymentProviderVm(
    String id,
    String name,
    String configureUrl,
    String additionalSettings
) {
  public static PaymentProviderVm fromModel(PaymentProvider paymentProvider) {
    return PaymentProviderVm.builder()
        .id(paymentProvider.getId())
        .name(paymentProvider.getName())
        .configureUrl(paymentProvider.getConfigureUrl())
        .additionalSettings(paymentProvider.getAdditionalSettings())
        .build();
  }
}