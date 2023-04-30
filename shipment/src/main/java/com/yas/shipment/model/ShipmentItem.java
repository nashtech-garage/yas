package com.yas.shipment.model;

import jakarta.persistence.*;

public class ShipmentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipmentId", referencedColumnName = "id")
    private Shipment shipmentId;
    private Long orderItemId;
    private Long productId;
    private int quantity;

//    private Double height;
//    private Double width;
//    private Double weight;
}



