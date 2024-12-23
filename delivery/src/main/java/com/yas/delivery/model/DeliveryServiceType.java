package com.yas.delivery.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@lombok.Getter
@lombok.Setter
@Builder
public class DeliveryServiceType {
    private String id;
    private String name;
    private double totalCost;
    private double totalTax;
    private String expectedDeliveryTime;
}
