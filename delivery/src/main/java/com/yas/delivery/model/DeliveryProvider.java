package com.yas.delivery.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@lombok.Getter
@lombok.Setter
@Builder
public class DeliveryProvider {
    private String id;
    private String name;
    private List<DeliveryServiceType> serviceTypes;
}
