package com.yas.inventory.service;

import static com.yas.inventory.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.inventory.config.ServiceUrlConfig;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressPostVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
        setUpSecurityContext("test");
        when(serviceUrlConfig.location()).thenReturn(INVENTORY_URL);
    }

    @Test
    void testGetAddressById_ifNormalCase_returnAddressDetailVm() {

        Long addressId = 1L;
        AddressDetailVm addressDetail = AddressDetailVm.builder()
            .id(1L)
            .contactName("John Doe")
            .phone("123-456-7890")
            .addressLine1("123 Main St")
            .addressLine2("Apt 4B")
            .city("Metropolis")
            .zipCode("12345")
            .districtId(100L)
            .districtName("Central District")
            .stateOrProvinceId(200L)
            .stateOrProvinceName("StateName")
            .countryId(300L)
            .countryName("CountryName")
            .build();

        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.location())
            .path("/storefront/addresses/{id}")
            .buildAndExpand(addressId)
            .toUri();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(AddressDetailVm.class))
            .thenReturn(addressDetail);

        AddressDetailVm result = locationService.getAddressById(addressId);

        assertNotNull(result);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void testCreateAddress_ifNormalCase_returnAddressVm() {
        AddressPostVm addressPost = new AddressPostVm(
            "John Smith",
            "555-1234",
            "789 Oak St",
            "Apt 101",
            "Smalltown",
            "67890",
            1L,
            2L,
            3L
        );
        AddressVm address = AddressVm.builder()
            .id(1L)
            .contactName("Jane Doe")
            .phone("987-654-3210")
            .addressLine1("456 Elm St")
            .city("Gotham")
            .zipCode("54321")
            .districtId(10L)
            .stateOrProvinceId(20L)
            .countryId(30L)
            .build();

        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.location())
            .path("/storefront/addresses")
            .buildAndExpand()
            .toUri();

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(addressPost)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(AddressVm.class)).thenReturn(address);

        AddressVm result = locationService.createAddress(addressPost);

        assertNotNull(result);
        assertThat(result.id()).isEqualTo(1L);
    }


    @Test
    void testUpdateAddress_ifNormalCase_shouldNoException() {
        Long addressId = 1L;
        AddressPostVm addressPostVm = new AddressPostVm(
            "Alice Johnson",
            "555-9876",
            "123 Maple Ave",
            "Suite 2",
            "Springfield",
            "98765",
            100L,
            200L,
            300L
        );

        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.location())
            .path("/storefront/addresses/{id}")
            .buildAndExpand(addressId)
            .toUri();

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(addressPostVm)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        assertDoesNotThrow(() -> locationService.updateAddress(addressId, addressPostVm));
    }

    @Test
    void testDeleteAddress_ifNormalCase_shouldNoException() {

        Long addressId = 1L;
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.location()).path("/storefront/addresses/{id}")
            .buildAndExpand(addressId).toUri();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        assertDoesNotThrow(() -> locationService.deleteAddress(addressId));
    }

}