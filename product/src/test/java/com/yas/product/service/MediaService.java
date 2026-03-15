package com.yas.product.service;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.product.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MediaServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private MediaService mediaService;

    @Mock private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private RestClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        when(serviceUrlConfig.media()).thenReturn("http://api.yas.com/media");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getMedia: Trả về giá trị mặc định khi ID null")
    void getMedia_WhenIdIsNull_ReturnsDefaultVm() {
        NoFileMediaVm result = mediaService.getMedia(null);
        
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("", result.url());
    }

    @Test
    @DisplayName("getMedia: Lấy thông tin media thành công với ID hợp lệ")
    @SuppressWarnings("unchecked")
    void getMedia_WithValidId_ReturnsMediaVm() {
        Long mediaId = 1L;
        NoFileMediaVm expectedVm = new NoFileMediaVm(mediaId, "test-media", "http://url", "image", "caption");

        // Mock luồng Fluent API: get() -> uri() -> retrieve() -> body()
        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(URI.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(expectedVm).when(responseSpec).body(NoFileMediaVm.class);

        NoFileMediaVm result = mediaService.getMedia(mediaId);

        assertNotNull(result);
        assertEquals(mediaId, result.id());
        verify(restClient).get();
    }

    @Test
    @DisplayName("removeMedia: Thực hiện xóa media thành công")
    @SuppressWarnings("unchecked")
    void removeMedia_Success() {
        mockSecurityContext("mock-token");

        // Mock luồng Fluent API: delete() -> uri() -> headers() -> retrieve()
        doReturn(requestHeadersUriSpec).when(restClient).delete();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(URI.class));
        doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(null).when(responseSpec).body(Void.class);

        assertDoesNotThrow(() -> mediaService.removeMedia(1L));
        verify(restClient).delete();
    }

    /**
     * Giả lập SecurityContext để lấy JWT Token
     */
    private void mockSecurityContext(String tokenValue) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn(tokenValue);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
