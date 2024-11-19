package com.yas.payment.service;

import static com.yas.payment.util.SecurityContextUtils.setUpSecurityContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.payment.config.ServiceUrlConfig;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.viewmodel.paymentprovider.MediaVm;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

class MediaServiceTest {

    public static final String URL_COD = "http://cod";
    public static final String URL_PAYPAL = "http://paypal";

    private RestClient restClient;
    private MediaService mediaService;
    private ServiceUrlConfig serviceUrlConfig;
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        restClient = mock();
        serviceUrlConfig = mock();
        responseSpec = mock();
        mediaService = new MediaService(restClient, serviceUrlConfig);

        setUpSecurityContext("test");
        final String MEDIA = "http://api.yas.local/medias";
        when(serviceUrlConfig.media()).thenReturn(MEDIA);
    }

    @Test
    @DisplayName("Getting payment provider image, when media service is unavailable, service should return empty")
    public void getMedia_whenMediaServiceIsUnavailable_shouldReturnEmpty() {
        // Given
        mockRestClientGetMethod(restClient);
        when(responseSpec.body(new ParameterizedTypeReference<List<MediaVm>>() {})).thenThrow(RuntimeException.class);

        // When
        var cod = new PaymentProvider();
        cod.setId(PaymentMethod.COD.name());
        cod.setMediaId(-1L);

        var medias = mediaService.getMediaVmMap(List.of(cod));

        // Then
        assertTrue(medias.isEmpty());
        verify(restClient, times(1)).get();
    }

    @Test
    public void getMedia_whenDuplicateMediaResponse_shouldProcessSuccess() {
        // Given
        mockRestClientGetMethod(restClient);
        long codMediaId = -1L;
        long paypalMediaId = -2L;
        when(responseSpec.body(new ParameterizedTypeReference<List<MediaVm>>() {}))
            .thenReturn(List.of(
                MediaVm.builder().id(codMediaId).url(URL_COD).build(),
                MediaVm.builder().id(paypalMediaId).url(URL_PAYPAL).build(),
                MediaVm.builder().id(codMediaId).url(URL_COD).build()
            ));

        // When
        var cod = new PaymentProvider();
        cod.setId(PaymentMethod.COD.name());
        cod.setMediaId(codMediaId);

        var duplicateCod = new PaymentProvider();
        cod.setId(PaymentMethod.COD.name());
        cod.setMediaId(codMediaId);

        var paypal = new PaymentProvider();
        paypal.setId(PaymentMethod.PAYPAL.name());
        paypal.setMediaId(paypalMediaId);

        var medias = mediaService.getMediaVmMap(List.of(cod, duplicateCod, paypal));

        // Then
        assertFalse(medias.isEmpty());
        assertEquals(2, medias.size());
        verify(restClient, times(1)).get();
    }

    private void mockRestClientGetMethod(RestClient restClient) {
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void getMedia_whenProvideEmptyProviders_shouldNotInvokeApi() {
        // When
        var medias = mediaService.getMediaVmMap(List.of());

        // Then
        assertTrue(medias.isEmpty());
        verify(restClient, times(0)).get();
    }

}
