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
import com.yas.delivery.utils.TestUtils;
import com.yas.delivery.viewmodel.CalculateFeePostVm;
import com.yas.delivery.viewmodel.DeliveryFeeVm;
import com.yas.delivery.viewmodel.DeliveryOption;
import com.yas.delivery.viewmodel.DeliveryProviderVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

        private CalculateFeePostVm calculateFeePostVm;

        @BeforeEach
        void setUp() {
            calculateFeePostVm = TestUtils.generateCalculateFeePostVm();
        }

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

        @Test
        void testCalculateDeliveryFee_whenProviderIdIsInvalid_shouldReturnBadRequest() throws Exception {
            CalculateFeePostVm nullProviderIdPostVm = calculateFeePostVm
                .toBuilder()
                .deliveryProviderId(null)
                .build();

            performCalculateFeeAndExpectBadRequest(nullProviderIdPostVm);
        }

        @Test
        void testCalculateDeliveryFee_whenWarehouseAddressIsInvalid_shouldReturnBadRequest() throws Exception {
            CalculateFeePostVm nullWarehouseAddressPostVm = calculateFeePostVm
                .toBuilder()
                .warehouseAddress(null)
                .build();

            CalculateFeePostVm nullZipCodeWarehousePostVm = calculateFeePostVm
                .toBuilder()
                .warehouseAddress(calculateFeePostVm.warehouseAddress().toBuilder().zipCode(null).build())
                .build();

            performCalculateFeeAndExpectBadRequest(nullWarehouseAddressPostVm);
            performCalculateFeeAndExpectBadRequest(nullZipCodeWarehousePostVm);
        }

        @Test
        void testCalculateDeliveryFee_whenRecipientAddressIsInvalid_shouldReturnBadRequest() throws Exception {
            CalculateFeePostVm invalidCalculateFeePostVm = calculateFeePostVm
                .toBuilder()
                .recipientAddress(null)
                .build();

            performCalculateFeeAndExpectBadRequest(invalidCalculateFeePostVm);
        }

        @Test
        void testCalculateDeliveryFee_whenDeliveryItemsIsInvalid_shouldReturnBadRequest() throws Exception {
            CalculateFeePostVm emptyDeliveryItemsPostVm = calculateFeePostVm
                .toBuilder()
                .deliveryItems(List.of())
                .build();

            CalculateFeePostVm nullQuantityDeliveryItemsPostVm = calculateFeePostVm
                .toBuilder()
                .deliveryItems(List.of(calculateFeePostVm.deliveryItems().getFirst().toBuilder().quantity(null).build()))
                .build();

            performCalculateFeeAndExpectBadRequest(emptyDeliveryItemsPostVm);
            performCalculateFeeAndExpectBadRequest(nullQuantityDeliveryItemsPostVm);
        }

        void performCalculateFeeAndExpectBadRequest(CalculateFeePostVm calculateFeePostVm) throws Exception {
            mockMvc.perform(post("/storefront/delivery/calculate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(calculateFeePostVm)))
                .andExpect(status().isBadRequest());
        }
    }

}
