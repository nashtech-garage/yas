package com.yas.rating.service;

import static com.yas.rating.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.rating.config.ServiceUrlConfig;
import com.yas.rating.viewmodel.OrderExistsByProductAndUserGetVm;
import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

class OrderServiceTest {

    private RestClient restClient;

    private ServiceUrlConfig serviceUrlConfig;

    private OrderService orderService;

    private RestClient.ResponseSpec responseSpec;

    private static final String ORDER_URL = "http://api.yas.local/order";

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        orderService = new OrderService(restClient, serviceUrlConfig);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCheckOrderExistsByProductAndUserWithStatus_whenNormalCase_returnOrderExistsByProductAndUserGetVm() {

        when(serviceUrlConfig.order()).thenReturn(ORDER_URL);
        URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.order())
            .path("/storefront/orders/completed")
            .queryParam("productId", "1")
            .buildAndExpand()
            .toUri();

        setUpSecurityContext("test");
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        OrderExistsByProductAndUserGetVm orderExistsByProductAndUserGetVm
            = new OrderExistsByProductAndUserGetVm(true);
        when(responseSpec.body(OrderExistsByProductAndUserGetVm.class))
            .thenReturn(orderExistsByProductAndUserGetVm);

        OrderExistsByProductAndUserGetVm result = orderService.checkOrderExistsByProductAndUserWithStatus(1L);

        assertThat(result.isPresent()).isTrue();

    }

    @Test
    void testHandleFallback_whenNewOrderExistsByProductAndUserGetVm_returnOrderExistsByProductAndUserGetVm()
        throws Throwable {

        OrderExistsByProductAndUserGetVm result = orderService.handleFallback(mock(Throwable.class));
        assertThat(result.isPresent()).isFalse();

    }

}