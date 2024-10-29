package com.yas.delivery.viewmodel;

import lombok.Builder;

@Builder
public record ShipmentFeeVm(String checkoutItemId,
                            String shipmentProviderId,
                            String shipmentProviderName,
                            String shipmentServiceTypeId,
                            String shipmentServiceTypeName,
                            Double shipmentCost,
                            Double shipmentTax,
                            String expectedDeliveryTime) {
}
