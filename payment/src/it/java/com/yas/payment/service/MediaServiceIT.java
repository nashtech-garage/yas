package com.yas.payment.service;

import static com.yas.payment.util.SecurityContextUtils.setUpSecurityContext;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.yas.payment.config.ServiceUrlConfig;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.model.enumeration.PaymentMethod;

@SpringBootTest
public class MediaServiceIT {

    @Autowired
    private MediaService mediaService;
    @MockitoBean
    private RestClient restClient;

    @MockitoBean
    private ServiceUrlConfig serviceUrlConfig;

    @BeforeEach
    void setUp() {
        setUpSecurityContext("test");
        final String MEDIA = "http://api.yas.local/medias";
        when(serviceUrlConfig.media()).thenReturn(MEDIA);
    }

    @Test
    @DisplayName("Getting payment provider image, when media service is unavailable, service should return empty")
    public void getMedia_whenMediaServiceIsUnavailable_shouldReturnEmpty() {
        // Given
        when(restClient.get()).thenThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE));

        // When
        var cod = new PaymentProvider();
        cod.setId(PaymentMethod.COD.name());
        cod.setMediaId(-1L);

        var medias = mediaService.getMediaVmMap(List.of(cod));

        // Then
        assertTrue(medias.isEmpty());
        verify(restClient, times(1)).get();
    }
}