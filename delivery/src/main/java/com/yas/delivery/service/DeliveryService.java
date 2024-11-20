package com.yas.delivery.service;

import com.yas.delivery.model.DeliveryProvider;
import com.yas.delivery.model.DeliveryServiceType;
import com.yas.delivery.viewmodel.CalculateFeesPostVm;
import com.yas.delivery.viewmodel.DeliveryFeeVm;
import com.yas.delivery.viewmodel.DeliveryOptions;
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
                .cost(20.0)
                .tax(2.0)
                .expectedDeliveryTime(now.plusDays(1).format(DateTimeFormatter.ISO_INSTANT))
                .build(),
            DeliveryServiceType.builder()
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

    private static DeliveryProvider buildUPSProvider() {
        ZonedDateTime now = ZonedDateTime.now();

        DeliveryProvider upsProvider = new DeliveryProvider();
        upsProvider.setId("UPS");
        upsProvider.setName("UPS");
        List<DeliveryServiceType> upsServiceTypes = Arrays.asList(
            DeliveryServiceType.builder()
                .id("07")
                .name("UPS Worldwide Express")
                .cost(10.0)
                .tax(1.0)
                .expectedDeliveryTime(now.plusDays(5).format(DateTimeFormatter.ISO_INSTANT))
                .build(),
            DeliveryServiceType.builder()
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
     * Calculates the delivery fees for the given delivery address based on delivery items.
     *
     * @param calculateFeePostVm the view model containing the necessary data for fee calculation.
     * @return a {@link DeliveryFeeVm} representing the calculated delivery fees for each checkout item.
     */
    public DeliveryFeeVm calculateDeliveryFee(CalculateFeesPostVm calculateFeePostVm) {
        List<DeliveryOptions> availableDeliveryOptions = deliveryProviders.stream()
            .flatMap(provider -> getDeliveryOptionsByProvider(provider).stream())
            .toList();
        return new DeliveryFeeVm(availableDeliveryOptions);
    }

    public List<DeliveryOptions> getDeliveryOptionsByProvider(DeliveryProvider deliveryProvider) {
        return deliveryProvider.getServiceTypes().stream()
            .map(serviceType -> new DeliveryOptions(
                deliveryProvider.getId(),
                deliveryProvider.getName(),
                serviceType.getId(),
                serviceType.getName(),
                serviceType.getCost(),
                serviceType.getTax(),
                serviceType.getExpectedDeliveryTime()
            ))
            .toList();
    }
}
