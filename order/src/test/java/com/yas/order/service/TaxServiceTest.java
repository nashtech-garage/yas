package com.yas.order.service;

import static com.yas.order.utils.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.order.config.ServiceUrlConfig;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

class TaxServiceTest {

    private RestClient restClient;

    private ServiceUrlConfig serviceUrlConfig;

    private TaxService taxService;

    private RestClient.ResponseSpec responseSpec;

    private static final String TAX_URL = "http://api.yas.local/tax";

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        taxService = new TaxService(restClient, serviceUrlConfig);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        setUpSecurityContext("test");
        when(serviceUrlConfig.tax()).thenReturn(TAX_URL);
    }


    @Test
    void testGetTaxPercentByAddress_ifNormalCase_returnCustomerVm() {

        Long taxClassId = 1L;
        Long countryId = 2L;
        Long stateOrProvinceId = 3L;
        String zipCode = "TEST";

        URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.tax())
            .path("/backoffice/tax-rates/tax-percent")
            .queryParam("taxClassId", taxClassId)
            .queryParam("countryId", countryId)
            .queryParam("stateOrProvinceId", stateOrProvinceId)
            .queryParam("zipCode", zipCode)
            .build().toUri();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Double.class))
            .thenReturn(1.1);

        Double result = taxService.getTaxPercentByAddress(taxClassId,
            countryId, stateOrProvinceId, zipCode);

        assertThat(result).isEqualTo(1.1);
    }
}