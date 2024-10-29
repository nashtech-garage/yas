package com.yas.delivery.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@lombok.Getter
@lombok.Setter
@Builder
public class ShipmentServiceType {
    private String id;
    private String name;
    private double cost;
    private double tax;
    private String expectedDeliveryTime;
}
