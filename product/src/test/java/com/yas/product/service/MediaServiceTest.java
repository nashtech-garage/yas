package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.product.viewmodel.NoFileMediaVm;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        mediaService = new MediaService(restClient, serviceUrlConfig);
    }

    @Test
    void getMedia_whenIdIsNull_shouldReturnDefaultNoFileMediaVm() {
        NoFileMediaVm result = mediaService.getMedia(null);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("", result.caption());
        assertEquals("", result.fileName());
        assertEquals("", result.mediaType());
        assertEquals("", result.url());
    }

    @Test
    void getMedia_whenIdIsProvided_shouldCallRestClient() {
        when(serviceUrlConfig.media()).thenReturn("http://media-service");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        NoFileMediaVm expectedMedia = new NoFileMediaVm(1L, "caption", "file.jpg", "image/jpeg",
                "http://example.com/file.jpg");
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(expectedMedia);

        NoFileMediaVm result = mediaService.getMedia(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("caption", result.caption());
        assertEquals("http://example.com/file.jpg", result.url());
    }
}
