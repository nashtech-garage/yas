package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.product.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

import java.net.URI;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private MediaService mediaService;

    // Các interface dùng để mock chuỗi builder của RestClient
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.RequestBodySpec requestBodySpec;
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        // Cấu hình URL mặc định khi mock
        lenient().when(serviceUrlConfig.media()).thenReturn("http://api.yas.local/media");
    }

    @AfterEach
    void tearDown() {
        // Dọn dẹp SecurityContext sau mỗi test để không ảnh hưởng chéo
        SecurityContextHolder.clearContext();
    }

    // Hàm tiện ích để mock SecurityContext chứa JWT token
    private void setupSecurityContext() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        when(jwt.getTokenValue()).thenReturn("mock-jwt-token");
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getMedia_WhenIdIsNull_ShouldReturnDefaultNoFileMediaVm() {
        // Act
        NoFileMediaVm result = mediaService.getMedia(null);

        // Assert
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("", result.caption());
        assertEquals("", result.fileName());
        assertEquals("", result.mediaType());
        assertEquals("", result.url());
    }

    @Test
    void getMedia_WhenIdIsNotNull_ShouldReturnMedia() {
        // Arrange
        Long mediaId = 1L;
        NoFileMediaVm mockVm = new NoFileMediaVm(mediaId, "caption", "file", "image/png", "url");

        // Mock RestClient chain cho method GET
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(mockVm);

        // Act
        NoFileMediaVm result = mediaService.getMedia(mediaId);

        // Assert
        assertNotNull(result);
        assertEquals(mediaId, result.id());
    }

    @Test
    void saveFile_ShouldReturnSavedMedia() {
        // Arrange
        setupSecurityContext(); // Mock đăng nhập
        MockMultipartFile file = new MockMultipartFile("file", "test.png", MediaType.IMAGE_PNG_VALUE, "test data".getBytes());
        NoFileMediaVm mockVm = new NoFileMediaVm(1L, "caption", "test.png", "image/png", "url");

        // Mock RestClient chain cho method POST
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.MULTIPART_FORM_DATA)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(mockVm);

        // Act
        NoFileMediaVm result = mediaService.saveFile(file, "caption", "test.png");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void removeMedia_ShouldExecuteSuccessfully() {
        // Arrange
        setupSecurityContext(); // Mock đăng nhập
        Long mediaId = 1L;

        // Mock RestClient chain cho method DELETE
        when(restClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Void.class)).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> mediaService.removeMedia(mediaId));
        
        // Xác minh rằng hàm delete của RestClient thực sự được gọi 1 lần
        verify(restClient, times(1)).delete();
    }
}