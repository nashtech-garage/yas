package com.yas.order.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShipmentProviderService {
    public boolean checkShipmentProviderAvailable(String shipmentProviderId) {
        // This is mock data
        log.info("Get shipment Id: {}", shipmentProviderId);
        return true;
    }
}
