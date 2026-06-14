package com.yas.customer.service;


import static com.yas.customer.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.customer.config.ServiceUrlConfig;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

class LocationServiceTest {

    private RestClient restClient;

    private ServiceUrlConfig serviceUrlConfig;

    private LocationService locationService;

    private RestClient.ResponseSpec responseSpec;

    private static final String INVENTORY_URL = "http://api.yas.local/inventory";

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        locationService = new LocationService(restClient, serviceUrlConfig);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
    }

    @Test
    void testGetAddressesByIdList() {

        List<Long> ids = List.of(1L, 2L, 3L);

        when(serviceUrlConfig.location()).thenReturn(INVENTORY_URL);
        URI uri = UriComponentsBuilder.fromUriString(INVENTORY_URL)
            .path("/storefront/addresses")
            .queryParam("ids", ids)
            .build()
            .toUri();

        setUpSecurityContext("test");
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(uri)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        AddressDetailVm addressDetail = getAddressDetailVm();
        when(responseSpec.body(new ParameterizedTypeReference<List<AddressDetailVm>>() {}))
            .thenReturn(Collections.singletonList(addressDetail));

        List<AddressDetailVm> result = locationService.getAddressesByIdList(ids);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(addressDetail);

    }

    @Test
    void testGetAddressById() {

        Long id = 1L;
        when(serviceUrlConfig.location()).thenReturn(INVENTORY_URL);
        URI uri = UriComponentsBuilder.fromUriString(INVENTORY_URL)
            .path("/storefront/addresses/{id}")
            .buildAndExpand(id)
            .toUri();

        setUpSecurityContext("test");
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(uri)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        AddressDetailVm addressDetail = getAddressDetailVm();
        when(responseSpec.body(AddressDetailVm.class))
            .thenReturn(addressDetail);

        AddressDetailVm result = locationService.getAddressById(id);

        assertThat(result).isEqualTo(addressDetail);
    }

    @Test
    void testCreateAddress() {

        when(serviceUrlConfig.location()).thenReturn(INVENTORY_URL);
        URI uri = UriComponentsBuilder.fromUriString(INVENTORY_URL)
            .path("/storefront/addresses")
            .build()
            .toUri();

        setUpSecurityContext("test");

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        AddressPostVm addressPostVm = getAddressPostVm();
        when(requestBodyUriSpec.body(addressPostVm)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        AddressVm addressVm = getAddressVm();
        when(responseSpec.body(AddressVm.class)).thenReturn(addressVm);

        AddressVm result = locationService.createAddress(addressPostVm);

        assertEquals(addressVm, result);
    }

    private AddressDetailVm getAddressDetailVm() {
        return new AddressDetailVm(
            1L,
            "John Doe",
            "+1234567890",
            "123 Elm Street",
            "Springfield",
            "62701",
            101L,
            "Downtown",
            201L,
            "Illinois",
            301L,
            "United States"
        );
    }

    private AddressPostVm getAddressPostVm() {
        return new AddressPostVm(
            "Jane Smith",
            "+1987654321",
            "456 Oak Avenue",
            "Metropolis",
            "54321",
            102L,
            202L,
            302L
        );
    }

    private AddressVm getAddressVm() {
        return new AddressVm(
            1L,
            "Alice Johnson",
            "+1239874560",
            "789 Pine Road",
            "Gotham",
            "10001",
            103L,
            203L,
            303L
        );
    }

    @Test
    void testHandleAddressDetailListFallback_throwsOriginalException() throws Exception {
        RuntimeException exception = new RuntimeException("boom");

        RuntimeException thrown = assertThrows(RuntimeException.class,
            () -> invokeFallback("handleAddressDetailListFallback", exception));

        assertThat(thrown.getMessage()).isEqualTo("boom");
    }

    @Test
    void testHandleAddressDetailFallback_throwsOriginalException() throws Exception {
        RuntimeException exception = new RuntimeException("boom");

        RuntimeException thrown = assertThrows(RuntimeException.class,
            () -> invokeFallback("handleAddressDetailFallback", exception));

        assertThat(thrown.getMessage()).isEqualTo("boom");
    }

    @Test
    void testHandleAddressFallback_throwsOriginalException() throws Exception {
        RuntimeException exception = new RuntimeException("boom");

        RuntimeException thrown = assertThrows(RuntimeException.class,
            () -> invokeFallback("handleAddressFallback", exception));

        assertThat(thrown.getMessage()).isEqualTo("boom");
    }

    private void invokeFallback(String methodName, Throwable throwable) throws Exception {
        Method method = LocationService.class.getDeclaredMethod(methodName, Throwable.class);
        method.setAccessible(true);
        try {
            method.invoke(locationService, throwable);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw ex;
        }
    }

}