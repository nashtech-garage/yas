package com.yas.delivery.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.commonlibrary.exception.ApiExceptionHandler;
import com.yas.delivery.service.DeliveryService;
import com.yas.delivery.utils.InvalidCalculatePostVmTestCase;
import com.yas.delivery.utils.TestUtils;
import com.yas.delivery.viewmodel.CalculateFeePostVm;
import com.yas.delivery.viewmodel.DeliveryFeeVm;
import com.yas.delivery.viewmodel.DeliveryOption;
import com.yas.delivery.viewmodel.DeliveryProviderVm;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {DeliveryController.class, ApiExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class DeliveryControllerTest {

    private static final String UPS_PROVIDER_ID = "UPS";
    private static final String UPS_PROVIDER_NAME = "UPS";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeliveryService deliveryService;

    @Nested
    class GetDeliveryProvidersTest {

        @Test
        void testGetDeliveryProviders_whenRequestIsValid_shouldReturnDeliveryProviders() throws Exception {
            DeliveryProviderVm upsProvider = new DeliveryProviderVm(UPS_PROVIDER_ID, UPS_PROVIDER_NAME);

            when(deliveryService.getDeliveryProviders()).thenReturn(List.of(upsProvider));

            mockMvc.perform(get("/storefront/delivery/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(upsProvider.id()))
                .andExpect(jsonPath("$[0].name").value(upsProvider.name()));

            verify(deliveryService).getDeliveryProviders();
        }
    }

    @Nested
    class CalculateDeliveryFeeTest {

        private static final CalculateFeePostVm calculateFeePostVm = TestUtils.generateCalculateFeePostVm();

        @Test
        void testCalculateDeliveryFee_whenRequestIsValid_shouldReturnDeliveryFee() throws Exception {
            DeliveryOption deliveryOption = DeliveryOption
                .builder()
                .deliveryProviderId(UPS_PROVIDER_ID)
                .deliveryProviderName(UPS_PROVIDER_NAME)
                .deliveryServiceTypeId("07")
                .deliveryServiceTypeName("UPS Worldwide Express")
                .totalCost(10.0)
                .totalTax(1.0)
                .build();
            DeliveryFeeVm expectedDeliveryFeeVm = new DeliveryFeeVm(List.of(deliveryOption));

            when(deliveryService.calculateDeliveryFee(calculateFeePostVm)).thenReturn(expectedDeliveryFeeVm);

            performCalculateFeeAndExpectValid(deliveryOption);

            verify(deliveryService).calculateDeliveryFee(calculateFeePostVm);
        }

        @ParameterizedTest(name = "should return bad request when {0}")
        @MethodSource({"invalidDeliveryProviderCases", "invalidAddressesCases", "invalidDeliveryItemsCases"})
        void testCalculateDeliveryFee_whenAddressesAreInvalid_shouldReturnBadRequest(
            InvalidCalculatePostVmTestCase testCase) throws Exception {
            performCalculateFeeAndExpectBadRequest(testCase.getInput());
        }

        private void performCalculateFeeAndExpectValid(DeliveryOption deliveryOption) throws Exception {
            mockMvc.perform(post("/storefront/delivery/calculate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(calculateFeePostVm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryOptions[0].deliveryProviderId")
                    .value(deliveryOption.deliveryProviderId()))
                .andExpect(jsonPath("$.deliveryOptions[0].deliveryProviderName")
                    .value(deliveryOption.deliveryProviderName()))
                .andExpect(jsonPath("$.deliveryOptions[0].deliveryServiceTypeId")
                    .value(deliveryOption.deliveryServiceTypeId()))
                .andExpect(jsonPath("$.deliveryOptions[0].deliveryServiceTypeName")
                    .value(deliveryOption.deliveryServiceTypeName()))
                .andExpect(jsonPath("$.deliveryOptions[0].totalCost")
                    .value(deliveryOption.totalCost()))
                .andExpect(jsonPath("$.deliveryOptions[0].totalTax")
                    .value(deliveryOption.totalTax()));
        }


        @SuppressWarnings("unused")
        private static Stream<InvalidCalculatePostVmTestCase> invalidDeliveryProviderCases() {
            return Stream.of(
                new InvalidCalculatePostVmTestCase(
                    "delivery provider ID is null",
                    calculateFeePostVm.toBuilder().deliveryProviderId(null).build()
                )
            );
        }

        @SuppressWarnings("unused")
        private static Stream<InvalidCalculatePostVmTestCase> invalidAddressesCases() {
            return Stream.of(
                new InvalidCalculatePostVmTestCase(
                    "warehouse address is null",
                    calculateFeePostVm.toBuilder().warehouseAddress(null).build()
                ),
                new InvalidCalculatePostVmTestCase(
                    "warehouse address ID is null",
                    calculateFeePostVm.toBuilder()
                        .warehouseAddress(calculateFeePostVm.warehouseAddress().toBuilder().id(null).build())
                        .build()
                ),
                new InvalidCalculatePostVmTestCase(
                    "warehouse zip code is null",
                    calculateFeePostVm.toBuilder()
                        .warehouseAddress(calculateFeePostVm.warehouseAddress().toBuilder().zipCode(null).build())
                        .build()
                ),
                new InvalidCalculatePostVmTestCase(
                    "warehouse country ID is null",
                    calculateFeePostVm.toBuilder()
                        .warehouseAddress(calculateFeePostVm.warehouseAddress().toBuilder().countryId(null).build())
                        .build()
                ),
                new InvalidCalculatePostVmTestCase(
                    "recipient address is null",
                    calculateFeePostVm.toBuilder().recipientAddress(null).build()
                )
            );
        }

        @SuppressWarnings("unused")
        private static Stream<InvalidCalculatePostVmTestCase> invalidDeliveryItemsCases() {
            return Stream.of(
                new InvalidCalculatePostVmTestCase(
                    "delivery items is empty",
                    calculateFeePostVm.toBuilder().deliveryItems(List.of()).build()
                ),
                new InvalidCalculatePostVmTestCase(
                    "delivery item product ID is null",
                    calculateFeePostVm.toBuilder()
                        .deliveryItems(
                            List.of(calculateFeePostVm.deliveryItems().getFirst().toBuilder().productId(null).build()))
                        .build()
                ),
                new InvalidCalculatePostVmTestCase(
                    "delivery item quantity is null",
                    calculateFeePostVm.toBuilder()
                        .deliveryItems(
                            List.of(calculateFeePostVm.deliveryItems().getFirst().toBuilder().quantity(null).build()))
                        .build()
                ),
                new InvalidCalculatePostVmTestCase(
                    "delivery item weight is null",
                    calculateFeePostVm.toBuilder()
                        .deliveryItems(
                            List.of(calculateFeePostVm.deliveryItems().getFirst().toBuilder().weight(null).build()))
                        .build()
                )
            );
        }

        void performCalculateFeeAndExpectBadRequest(CalculateFeePostVm calculateFeePostVm) throws Exception {
            mockMvc.perform(post("/storefront/delivery/calculate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(calculateFeePostVm)))
                .andExpect(status().isBadRequest());
        }
    }
}
