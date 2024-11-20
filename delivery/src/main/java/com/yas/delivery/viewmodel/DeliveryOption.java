package com.yas.delivery.viewmodel;

import lombok.Builder;

@Builder
public record DeliveryOption(String deliveryProviderId,
                             String deliveryProviderName,
                             String deliveryServiceTypeId,
                             String deliveryServiceTypeName,
                             Double totalCost,
                             Double totalTax,
                             String expectedDeliveryTime) {
}
