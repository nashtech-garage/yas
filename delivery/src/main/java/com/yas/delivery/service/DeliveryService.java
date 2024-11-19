package com.yas.delivery.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.delivery.model.ShipmentProvider;
import com.yas.delivery.model.ShipmentServiceType;
import com.yas.delivery.viewmodel.CalculateFeesPostVm;
import com.yas.delivery.viewmodel.CheckoutItemVm;
import com.yas.delivery.viewmodel.ShipmentFeeVm;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {
    private static final List<ShipmentProvider> shipmentProviders;

    static {
        ShipmentProvider fedexProvider = buildFedexProvider();
        ShipmentProvider upsProvider = buildUPSProvider();
        shipmentProviders = Arrays.asList(fedexProvider, upsProvider);
    }

    private static ShipmentProvider buildFedexProvider() {
        ZonedDateTime now = ZonedDateTime.now();

        ShipmentProvider fedexProvider = new ShipmentProvider();
        fedexProvider.setId("FEDEX");
        fedexProvider.setName("Fedex");
        List<ShipmentServiceType> fedexServiceTypes = Arrays.asList(
            ShipmentServiceType.builder()
                .id("FEDEX_INTERNATIONAL_PRIORITY")
                .name("FedEx International Priority")
                .cost(20.0)
                .tax(2.0)
                .expectedDeliveryTime(now.plusDays(1).format(DateTimeFormatter.ISO_INSTANT))
                .build(),
            ShipmentServiceType.builder()
                .id("INTERNATIONAL_ECONOMY")
                .name("FedEx International Economy")
                .cost(30.0)
                .tax(3.0)
                .expectedDeliveryTime(now.plusDays(3).format(DateTimeFormatter.ISO_INSTANT))
                .build()
        );
        fedexProvider.setServiceTypes(fedexServiceTypes);
        return fedexProvider;
    }

    private static ShipmentProvider buildUPSProvider() {
        ZonedDateTime now = ZonedDateTime.now();

        ShipmentProvider upsProvider = new ShipmentProvider();
        upsProvider.setId("UPS");
        upsProvider.setName("UPS");
        List<ShipmentServiceType> upsServiceTypes = Arrays.asList(
            ShipmentServiceType.builder()
                .id("07")
                .name("UPS Worldwide Express")
                .cost(10.0)
                .tax(1.0)
                .expectedDeliveryTime(now.plusDays(5).format(DateTimeFormatter.ISO_INSTANT))
                .build(),
            ShipmentServiceType.builder()
                .id("11")
                .name("UPS Standard")
                .cost(15.0)
                .expectedDeliveryTime(now.plusDays(7).format(DateTimeFormatter.ISO_INSTANT))
                .tax(1.5)
                .build()
        );
        upsProvider.setServiceTypes(upsServiceTypes);
        return upsProvider;
    }

    /**
     * Calculates the shipment fees for the given shipment provider based on checkout items.
     *
     * @param calculateFeePostVm the view model containing the necessary data for fee calculation.
     * @return a list of {@link ShipmentFeeVm} representing the calculated shipment fees for each checkout item.
     */
    public List<ShipmentFeeVm> calculateShipmentFees(CalculateFeesPostVm calculateFeePostVm) {
        ShipmentProvider shipmentProvider =
            findShipmentProviderById(calculateFeePostVm.shipmentProviderId())
                .orElseThrow(() -> new NotFoundException("Invalid shipment provider"));

        List<ShipmentFeeVm> shipmentFees = new ArrayList<>();

        for (CheckoutItemVm checkoutItem : calculateFeePostVm.checkoutItems()) {
            shipmentFees.addAll(calculateShipmentFeesForCheckoutItem(shipmentProvider, checkoutItem));
        }
        return shipmentFees;
    }

    private List<ShipmentFeeVm> calculateShipmentFeesForCheckoutItem(ShipmentProvider shipmentProvider,
                                                                     CheckoutItemVm checkoutItem) {

        // To make the response more dynamic, we calculate the shipment fees by multiplying the cost and tax with
        // the quantity of the checkout item.
        // The actual calculation should be based on the actual business logic.
        return shipmentProvider.getServiceTypes().stream()
            .map(serviceType ->
                ShipmentFeeVm.builder()
                    .checkoutItemId(checkoutItem.id())
                    .shipmentProviderId(shipmentProvider.getId())
                    .shipmentProviderName(shipmentProvider.getName())
                    .shipmentServiceTypeId(serviceType.getId())
                    .shipmentServiceTypeName(serviceType.getName())
                    .shipmentCost(serviceType.getCost() * checkoutItem.quantity())
                    .shipmentTax(serviceType.getTax() * checkoutItem.quantity())
                    .expectedDeliveryTime(serviceType.getExpectedDeliveryTime())
                    .build()
            ).toList();
    }

    private Optional<ShipmentProvider> findShipmentProviderById(String shipmentProviderId) {
        return shipmentProviders.stream()
            .filter(provider -> provider.getId().equals(shipmentProviderId))
            .findFirst();
    }
}
