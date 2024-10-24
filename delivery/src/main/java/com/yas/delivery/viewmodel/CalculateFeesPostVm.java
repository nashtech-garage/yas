package com.yas.delivery.viewmodel;

import java.util.List;

public record CalculateFeesPostVm(String shipmentProviderId,
                                  String shipmentServiceTypeId,
                                  String recipientAddressId,
                                  List<CheckoutItemVm> checkoutItems) {
}
