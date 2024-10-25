package com.yas.delivery.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.delivery.model.ShipmentProvider;
import com.yas.delivery.model.ShipmentServiceType;
import com.yas.delivery.viewmodel.CalculateFeesPostVm;
import com.yas.delivery.viewmodel.ShipmentFeeVm;
import com.yas.delivery.viewmodel.ShipmentProviderVm;
import com.yas.delivery.viewmodel.ShipmentServiceTypeVm;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {
    private static final List<ShipmentProvider> shipmentProviders;

    static {
        ShipmentProvider fedexProvider = new ShipmentProvider();
        fedexProvider.setId("FEDEX");
        fedexProvider.setName("Fedex");
        List<ShipmentServiceType> fedexServiceTypes = Arrays.asList(
            ShipmentServiceType.builder()
                .id("FEDEX_INTERNATIONAL_PRIORITY")
                .name("FedEx International Priority")
                .cost(20.0)
                .tax(2.0)
                .build(),
            ShipmentServiceType.builder()
                .id("INTERNATIONAL_ECONOMY")
                .name("FedEx International Economy")
                .cost(30.0)
                .tax(3.0)
                .build()
        );
        fedexProvider.setServiceTypes(fedexServiceTypes);

        ShipmentProvider ghnProvider = new ShipmentProvider();
        ghnProvider.setId("GHN");
        ghnProvider.setName("Giao Hang Nhanh");
        List<ShipmentServiceType> ghnServiceTypes = Arrays.asList(
            ShipmentServiceType.builder()
                .id("53322")
                .name("Hang nhe")
                .cost(10.0)
                .tax(1.0)
                .build(),
            ShipmentServiceType.builder()
                .id("100039")
                .name("Hang nang")
                .cost(15.0)
                .tax(1.5).build()
        );
        ghnProvider.setServiceTypes(ghnServiceTypes);

        shipmentProviders = Arrays.asList(fedexProvider, ghnProvider);
    }

    /**
     * Retrieves a list of available shipment providers.
     *
     * @return a list of {@link ShipmentProviderVm} representing the available shipment providers.
     */
    public List<ShipmentProviderVm> getShipmentProviders() {
        return shipmentProviders.stream().map(shipmentProvider ->
            new ShipmentProviderVm(shipmentProvider.getId(), shipmentProvider.getName())
        ).toList();
    }

    /**
     * Retrieves the available shipment service types for a given shipment provider and recipient address.
     *
     * @param shipmentProviderId the ID of the shipment provider.
     * @param recipientAddressId the ID of the recipient's address.
     * @return a list of {@link ShipmentServiceTypeVm} representing the available shipment service types.
     */
    public List<ShipmentServiceTypeVm> getShipmentServiceTypes(String shipmentProviderId,
                                                               String recipientAddressId) {
        ShipmentProvider shipmentProvider =
            findShipmentProviderById(shipmentProviderId)
                .orElseThrow(() -> new NotFoundException("Invalid shipment provider"));

        return shipmentProvider.getServiceTypes()
            .stream()
            .map(serviceType ->
                new ShipmentServiceTypeVm(serviceType.getId(), serviceType.getName()))
            .toList();
    }

    /**
     * Calculates the shipment fees for the given shipment provider and service type based on checkout items.
     *
     * @param calculateFeePostVm the view model containing the necessary data for fee calculation.
     * @return a list of {@link ShipmentFeeVm} representing the calculated shipment fees for each checkout item.
     */
    public List<ShipmentFeeVm> calculateShipmentFees(CalculateFeesPostVm calculateFeePostVm) {
        ShipmentProvider shipmentProvider =
            findShipmentProviderById(calculateFeePostVm.shipmentProviderId())
                .orElseThrow(() -> new NotFoundException("Invalid shipment provider"));

        ShipmentServiceType shipmentServiceType = shipmentProvider.getServiceTypes()
            .stream()
            .filter(serviceType -> serviceType.getId().equals(calculateFeePostVm.shipmentServiceTypeId()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Invalid shipment service type"));

        return calculateFeePostVm
            .checkoutItems()
            .stream()
            .map(checkoutItemVm ->
                new ShipmentFeeVm(checkoutItemVm.id(), shipmentServiceType.getCost(), shipmentServiceType.getTax()))
            .toList();
    }

    private Optional<ShipmentProvider> findShipmentProviderById(String shipmentProviderId) {
        return shipmentProviders.stream()
            .filter(provider -> provider.getId().equals(shipmentProviderId))
            .findFirst();
    }
}
