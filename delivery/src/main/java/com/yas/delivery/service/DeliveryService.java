package com.yas.delivery.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.delivery.model.DeliveryProvider;
import com.yas.delivery.model.DeliveryServiceType;
import com.yas.delivery.viewmodel.CalculateFeePostVm;
import com.yas.delivery.viewmodel.DeliveryFeeVm;
import com.yas.delivery.viewmodel.DeliveryOptions;
import com.yas.delivery.viewmodel.DeliveryProviderVm;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {
    private static final List<DeliveryProvider> deliveryProviders;

    static {
        DeliveryProvider fedexProvider = buildFedexProvider();
        DeliveryProvider upsProvider = buildUPSProvider();
        deliveryProviders = Arrays.asList(fedexProvider, upsProvider);
    }

    private static DeliveryProvider buildFedexProvider() {
        ZonedDateTime now = ZonedDateTime.now();

        DeliveryProvider fedexProvider = new DeliveryProvider();
        fedexProvider.setId("FEDEX");
        fedexProvider.setName("Fedex");
        List<DeliveryServiceType> fedexServiceTypes = Arrays.asList(
            DeliveryServiceType.builder()
                .id("FEDEX_INTERNATIONAL_PRIORITY")
                .name("FedEx International Priority")
                .totalCost(20.0)
                .totalTax(2.0)
                .expectedDeliveryTime(now.plusDays(1).format(DateTimeFormatter.ISO_INSTANT))
                .build(),
            DeliveryServiceType.builder()
                .id("INTERNATIONAL_ECONOMY")
                .name("FedEx International Economy")
                .totalCost(30.0)
                .totalTax(3.0)
                .expectedDeliveryTime(now.plusDays(3).format(DateTimeFormatter.ISO_INSTANT))
                .build()
        );
        fedexProvider.setServiceTypes(fedexServiceTypes);
        return fedexProvider;
    }

    private static DeliveryProvider buildUPSProvider() {
        ZonedDateTime now = ZonedDateTime.now();

        DeliveryProvider upsProvider = new DeliveryProvider();
        upsProvider.setId("UPS");
        upsProvider.setName("UPS");
        List<DeliveryServiceType> upsServiceTypes = Arrays.asList(
            DeliveryServiceType.builder()
                .id("07")
                .name("UPS Worldwide Express")
                .totalCost(10.0)
                .totalTax(1.0)
                .expectedDeliveryTime(now.plusDays(5).format(DateTimeFormatter.ISO_INSTANT))
                .build(),
            DeliveryServiceType.builder()
                .id("11")
                .name("UPS Standard")
                .totalCost(15.0)
                .expectedDeliveryTime(now.plusDays(7).format(DateTimeFormatter.ISO_INSTANT))
                .totalTax(1.5)
                .build()
        );
        upsProvider.setServiceTypes(upsServiceTypes);
        return upsProvider;
    }

    /**
     * Retrieves a list of available delivery providers.
     *
     * @return a list of {@link DeliveryProviderVm} representing the available delivery providers.
     */
    public List<DeliveryProviderVm> getDeliveryProviders() {
        return deliveryProviders.stream().map(deliveryProvider ->
            new DeliveryProviderVm(deliveryProvider.getId(), deliveryProvider.getName())
        ).toList();
    }

    /**
     * Calculates the delivery fee based on the provided delivery information.
     *
     * @param calculateFeePostVm the delivery information including delivery provider ID, warehouse address,
     *                           recipient address, and delivery items.
     * @return a {@link DeliveryFeeVm} containing the calculated delivery fee options.
     * @throws IllegalArgumentException if the delivery provider ID is invalid.
     */
    public DeliveryFeeVm calculateDeliveryFee(CalculateFeePostVm calculateFeePostVm) {
        DeliveryProvider deliveryProvider = deliveryProviders.stream()
            .filter(provider -> provider.getId().equals(calculateFeePostVm.deliveryProviderId()))
            .findFirst()
            .orElseThrow(() -> new BadRequestException("Invalid delivery provider ID"));
        return new DeliveryFeeVm(getDeliveryOptionsByProvider(deliveryProvider));
    }

    public List<DeliveryOptions> getDeliveryOptionsByProvider(DeliveryProvider deliveryProvider) {
        return deliveryProvider.getServiceTypes().stream()
            .map(serviceType -> DeliveryOptions
                .builder()
                .deliveryProviderId(deliveryProvider.getId())
                .deliveryProviderName(deliveryProvider.getName())
                .deliveryServiceTypeId(serviceType.getId())
                .deliveryServiceTypeName(serviceType.getName())
                .totalCost(serviceType.getTotalCost())
                .totalTax(serviceType.getTotalTax())
                .expectedDeliveryTime(serviceType.getExpectedDeliveryTime())
                .build())
            .toList();
    }
}
