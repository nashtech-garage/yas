package com.yas.payment.service;

import static com.yas.payment.util.SecurityContextUtils.setUpSecurityContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.payment.config.ServiceUrlConfig;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.viewmodel.paymentprovider.MediaVm;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // CHÌA KHÓA LÀ DÒNG NÀY ĐÂY!
@SuppressWarnings({"unchecked", "rawtypes"})
class MediaServiceTest {

    public static final String URL_COD = "http://cod";
    public static final String URL_PAYPAL = "http://paypal";

    @InjectMocks
    private MediaService mediaService;
    @Mock
    private RestClient restClient;
    @Mock
    private ServiceUrlConfig serviceUrlConfig;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        setUpSecurityContext("test");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getMedia_whenProvideValidProviders_shouldProcessSuccess() {
        // Given
        final String MEDIA = "http://api.yas.local/medias";
        when(serviceUrlConfig.media()).thenReturn(MEDIA);
        mockRestClientGetMethod(restClient);
        long codMediaId = -1L;
        long paypalMediaId = -2L;
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(List.of(
                        MediaVm.builder().id(codMediaId).url(URL_COD).build(),
                        MediaVm.builder().id(paypalMediaId).url(URL_PAYPAL).build()
                ));

        // When
        var cod = new PaymentProvider();
        cod.setId(PaymentMethod.COD.name());
        cod.setMediaId(codMediaId);

        var paypal = new PaymentProvider();
        paypal.setId(PaymentMethod.PAYPAL.name());
        paypal.setMediaId(paypalMediaId);

        var mediaVmMap = mediaService.getMediaVmMap(List.of(cod, paypal));

        // Then
        assertEquals(2, mediaVmMap.size());
        assertEquals(URL_COD, mediaVmMap.get(codMediaId).getUrl());
        assertEquals(URL_PAYPAL, mediaVmMap.get(paypalMediaId).getUrl());

        verify(restClient, times(1)).get();
    }

    @Test
    public void getMedia_whenProvideEmptyProviders_shouldNotInvokeApi() {
        // When
        var medias = mediaService.getMediaVmMap(List.of());

        // Then
        assertTrue(medias.isEmpty());
        verify(restClient, times(0)).get();
    }

    @Test
    void fallbackGetMediaVmMap_shouldReturnEmptyMap() {
        var cod = new PaymentProvider();
        cod.setId(PaymentMethod.COD.name());
        cod.setMediaId(-1L);
        Throwable t = new RuntimeException("test circuit breaker error");

        Map<Long, MediaVm> result = ReflectionTestUtils.invokeMethod(
                mediaService, "fallbackGetMediaVmMap", List.of(cod), t
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private void mockRestClientGetMethod(RestClient restClient) {
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        
        when(requestHeadersUriSpec.headers(any())).thenAnswer(invocation -> {
            Consumer<HttpHeaders> headersConsumer = invocation.getArgument(0);
            headersConsumer.accept(new HttpHeaders());
            return requestHeadersUriSpec;
        });
        
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    }
}