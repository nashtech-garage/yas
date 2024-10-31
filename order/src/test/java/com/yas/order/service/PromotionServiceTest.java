package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.promotion.PromotionUsageVm;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

import static com.yas.order.utils.SecurityContextUtils.setUpSecurityContext;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PromotionServiceTest {

    private RestClient restClient;

    private ServiceUrlConfig serviceUrlConfig;

    private PromotionService promotionService;

    private RestClient.ResponseSpec responseSpec;

    private static final String PROMOTION_URL = "http://api.yas.local/promotion";

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        promotionService = new PromotionService(restClient, serviceUrlConfig);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        setUpSecurityContext("test");
        when(serviceUrlConfig.promotion()).thenReturn(PROMOTION_URL);
    }

    @Test
    void testUpdateUsagePromotion_ifNormalCase_shouldNoException() {

        List<PromotionUsageVm> promotionUsageVms = getPromotionUsageVms();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Object.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        assertDoesNotThrow(() -> promotionService.updateUsagePromotion(promotionUsageVms));
    }

    private static @NotNull List<PromotionUsageVm> getPromotionUsageVms() {
        return List.of(
                PromotionUsageVm.builder()
                        .promotionCode("123")
                        .userId("user123")
                        .orderId(1001L)
                        .productId(5001L)
                        .build(),

                PromotionUsageVm.builder()
                        .promotionCode("1234")
                        .userId("user456")
                        .orderId(1002L)
                        .productId(5002L)
                        .build()
        );
    }
}
