package com.yas.delivery.viewmodel;

public record ShipmentFeeVm(String checkoutItemId,
                            Double shipmentCost,
                            Double shipmentTax) {
}
