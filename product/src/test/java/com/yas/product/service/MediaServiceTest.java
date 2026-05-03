package com.yas.product.service;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.product.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    RestClient restClient;

    @Mock
    ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    MediaService mediaService;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        
        org.mockito.Mockito.lenient().when(jwt.getTokenValue()).thenReturn("fake-jwt-token");
        org.mockito.Mockito.lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        org.mockito.Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        org.mockito.Mockito.lenient().when(serviceUrlConfig.media()).thenReturn("http://media-service");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testSaveFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());
        NoFileMediaVm expectedMedia = new NoFileMediaVm(1L, "caption", "file", "img", "url");

        when(restClient.post().uri(any(java.net.URI.class))
                .contentType(any())
                .headers(any())
                .body(any(Object.class))
                .retrieve()
                .body(NoFileMediaVm.class)).thenReturn(expectedMedia);

        NoFileMediaVm result = mediaService.saveFile(file, "caption", "filename");
        assertEquals("caption", result.caption());
    }

    @Test
    void testGetMedia_WithNullId() {
        NoFileMediaVm result = mediaService.getMedia(null);
        assertNull(result.id());
    }

    @Test
    void testGetMedia_WithValidId() {
        NoFileMediaVm expectedMedia = new NoFileMediaVm(1L, "cap", "file", "img", "url");
        when(restClient.get().uri(any(java.net.URI.class)).retrieve().body(NoFileMediaVm.class)).thenReturn(expectedMedia);

        NoFileMediaVm result = mediaService.getMedia(1L);
        assertEquals(1L, result.id());
    }

    @Test
    void testRemoveMedia() {
        RestClient.RequestHeadersUriSpec deleteUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestBodySpec deleteHeaderSpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.delete()).thenReturn(deleteUriSpec);
        when(deleteUriSpec.uri(any(java.net.URI.class))).thenReturn(deleteHeaderSpec);
        when(deleteHeaderSpec.headers(any())).thenReturn(deleteHeaderSpec);
        when(deleteHeaderSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Void.class)).thenReturn(null);

        mediaService.removeMedia(1L);
    }
}