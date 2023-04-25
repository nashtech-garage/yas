package com.yas.shipment.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends AbstractAuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String trackingNumber;
    private Long warehouseId;
    @OneToMany(mappedBy = "shipmentId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public List<ShipmentItem> shipmentItems = new ArrayList<>();

}