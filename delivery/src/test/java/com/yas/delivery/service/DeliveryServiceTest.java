package com.yas.delivery.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.delivery.utils.TestUtils;
import com.yas.delivery.viewmodel.CalculateFeePostVm;
import com.yas.delivery.viewmodel.DeliveryFeeVm;
import com.yas.delivery.viewmodel.DeliveryOption;
import com.yas.delivery.viewmodel.DeliveryProviderVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DeliveryServiceTest {
    private static final String FEDEX_PROVIDER_ID = "FEDEX";
    private static final String FEDEX_PROVIDER_NAME = "FedEx";

    private final DeliveryService deliveryService = new DeliveryService();

    @Nested
    class GetDeliveryProvidersTest {

        @Test
        void testGetDeliveryProviders_shouldReturnDeliveryProviders() {
            List<DeliveryProviderVm> deliveryProviders = deliveryService.getDeliveryProviders();

            assertNotNull(deliveryProviders);
            assertEquals(2, deliveryProviders.size());
            assertTrue(deliveryProviders.stream().anyMatch(p -> p.id().equals(FEDEX_PROVIDER_ID) && p.name().equals(
                FEDEX_PROVIDER_NAME)));
        }
    }

    @Nested
    class CalculateDeliveryFeeTest {

        private CalculateFeePostVm calculateFeePostVm;

        @BeforeEach
        void setUp() {
            calculateFeePostVm = TestUtils.generateCalculateFeePostVm();
        }

        @Test
        void testCalculateDeliveryFee_whenProviderIdIsInvalid_shouldThrowBadRequestException() {
            String invalidProviderId = "INVALID";
            CalculateFeePostVm invalidProviderCalculateFeePostVm =
                calculateFeePostVm.toBuilder().deliveryProviderId(invalidProviderId).build();

            assertThrows(BadRequestException.class,
                () -> deliveryService.calculateDeliveryFee(invalidProviderCalculateFeePostVm));
        }

        @Test
        void testCalculateDeliveryFee_whenCalled_shouldReturnDeliveryFee() {
            DeliveryFeeVm deliveryFee = deliveryService.calculateDeliveryFee(calculateFeePostVm);
            
            assertNotNull(deliveryFee);
            DeliveryOption firstDeliveryOption = deliveryFee.deliveryOptions().getFirst();
            assertEquals(calculateFeePostVm.deliveryProviderId(), firstDeliveryOption.deliveryProviderId());
            assertEquals(FEDEX_PROVIDER_NAME, firstDeliveryOption.deliveryProviderName());
            assertEquals("FEDEX_INTERNATIONAL_PRIORITY", firstDeliveryOption.deliveryServiceTypeId());
            assertEquals("FedEx International Priority", firstDeliveryOption.deliveryServiceTypeName());
            assertEquals(20.0, firstDeliveryOption.totalCost());
            assertEquals(2.0, firstDeliveryOption.totalTax());
        }
    }
}
