package com.yas.delivery.utils;

import com.yas.delivery.viewmodel.CalculateDeliveryFeeVm;
import com.yas.delivery.viewmodel.DeliveryAddressVm;
import com.yas.delivery.viewmodel.DeliveryItemVm;
import java.util.List;

public class TestUtils {
    public static CalculateDeliveryFeeVm generateCalculateDeliveryFeeVm() {
        DeliveryAddressVm warehouseAddress = DeliveryAddressVm.builder()
            .id(1L)
            .addressLine1("123 Warehouse Street")
            .zipCode("12345")
            .countryId(100L)
            .build();

        DeliveryAddressVm recipientAddress = DeliveryAddressVm.builder()
            .id(2L)
            .addressLine1("456 Recipient Avenue")
            .zipCode("67890")
            .countryId(200L)
            .build();

        DeliveryItemVm deliveryItem = DeliveryItemVm.builder()
            .productId("P12345")
            .quantity(2)
            .weight(1.5)
            .build();

        return CalculateDeliveryFeeVm.builder()
            .deliveryProviderId("FEDEX")
            .warehouseAddress(warehouseAddress)
            .recipientAddress(recipientAddress)
            .deliveryItems(List.of(deliveryItem))
            .build();
    }
}
