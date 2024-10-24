package com.yas.delivery.controller;

import com.yas.delivery.service.DeliveryService;
import com.yas.delivery.viewmodel.CalculateFeesPostVm;
import com.yas.delivery.viewmodel.ShipmentFeeVm;
import com.yas.delivery.viewmodel.ShipmentProviderVm;
import com.yas.delivery.viewmodel.ShipmentServiceTypeVm;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @GetMapping("/storefront/shipment/providers")
    public ResponseEntity<List<ShipmentProviderVm>> getShipmentProviders() {
        List<ShipmentProviderVm> shipmentProviders = deliveryService.getShipmentProviders();
        return ResponseEntity.ok(shipmentProviders);
    }

    @GetMapping("/storefront/shipment/providers/{shipmentProviderId}/service-types")
    public ResponseEntity<List<ShipmentServiceTypeVm>> getShipmentServiceTypes(@PathVariable String shipmentProviderId,
                                                                               @RequestParam
                                                                               String recipientAddressId) {
        List<ShipmentServiceTypeVm> shipmentServiceTypes =
            deliveryService.getShipmentServiceTypes(shipmentProviderId, recipientAddressId);
        return ResponseEntity.ok(shipmentServiceTypes);
    }

    @PostMapping("/storefront/shipment/calculate")
    public ResponseEntity<List<ShipmentFeeVm>> calculateShipmentFees(
        @RequestBody CalculateFeesPostVm calculateFeePostVm) {
        List<ShipmentFeeVm> shipmentFees = deliveryService.calculateShipmentFees(calculateFeePostVm);
        return ResponseEntity.ok(shipmentFees);
    }
}
