package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.product.ProductCheckoutListVm;
import com.yas.order.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private RestClient restClient;
    
    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    // Các Mock Interface chuyên dụng để xử lý chuỗi Fluent API của RestClient
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.RequestBodySpec requestBodySpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private ProductService productService;

    // Biến để Mock hàm Static
    private MockedStatic<AuthenticationUtils> authUtilsMock;

    @BeforeEach
    void setUp() {
        // "Hack" hệ thống: Giả lập hàm extractJwt luôn trả về một chuỗi token giả
        authUtilsMock = mockStatic(AuthenticationUtils.class);
        authUtilsMock.when(AuthenticationUtils::extractJwt).thenReturn("mock-jwt-token");

        lenient().when(serviceUrlConfig.product()).thenReturn("http://api.product.local");
    }

    @AfterEach
    void tearDown() {
        // Phải đóng MockedStatic sau mỗi test để không làm ô nhiễm bộ nhớ của các test khác
        authUtilsMock.close();
    }

    // ==========================================
    // TEST HÀM: getProductVariations (GET Request)
    // ==========================================
    @Test
    void getProductVariations_Success() {
        // Arrange: Cấu hình chuỗi gọi RestClient
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        ProductVariationVm mockVariation = mock(ProductVariationVm.class);
        ResponseEntity<List<ProductVariationVm>> responseEntity = ResponseEntity.ok(List.of(mockVariation));
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        // Act
        List<ProductVariationVm> result = productService.getProductVariations(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ==========================================
    // TEST HÀM: subtractProductStockQuantity (PUT Request)
    // ==========================================
    @Test
    void subtractProductStockQuantity_Success() {
        // Arrange: Cấu hình chuỗi gọi RestClient cho PUT
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(List.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        OrderItemVm mockItem = mock(OrderItemVm.class);
        when(mockItem.productId()).thenReturn(100L);
        when(mockItem.quantity()).thenReturn(2);
        
        OrderVm mockOrder = mock(OrderVm.class);
        when(mockOrder.orderItemVms()).thenReturn(Set.of(mockItem));

        // Act & Assert: Đảm bảo hàm chạy không văng lỗi
        assertDoesNotThrow(() -> productService.subtractProductStockQuantity(mockOrder));
    }

    // ==========================================
    // TEST HÀM: getProductInfomation (Bao phủ các nhánh if/else)
    // ==========================================
    @Test
    void getProductInfomation_Success() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        ProductCheckoutListVm mockProduct = mock(ProductCheckoutListVm.class);
        when(mockProduct.getId()).thenReturn(10L); // Giả lập ID để code map vào key của HashMap
        
        ProductGetCheckoutListVm mockResponseList = mock(ProductGetCheckoutListVm.class);
        when(mockResponseList.productCheckoutListVms()).thenReturn(List.of(mockProduct));
        
        ResponseEntity<ProductGetCheckoutListVm> responseEntity = ResponseEntity.ok(mockResponseList);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        Map<Long, ProductCheckoutListVm> result = productService.getProductInfomation(Set.of(10L), 0, 10);

        assertNotNull(result);
        assertTrue(result.containsKey(10L));
    }

    @Test
    void getProductInfomation_WhenResponseNull_ShouldThrowNotFoundException() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        // Giả lập nhánh if (response == null)
        ResponseEntity<ProductGetCheckoutListVm> responseEntity = ResponseEntity.ok(null);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        assertThrows(NotFoundException.class, () -> productService.getProductInfomation(Set.of(10L), 0, 10));
    }

    @Test
    void getProductInfomation_WhenListNull_ShouldThrowNotFoundException() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        // Giả lập nhánh if (response.productCheckoutListVms() == null)
        ProductGetCheckoutListVm mockResponseList = mock(ProductGetCheckoutListVm.class);
        when(mockResponseList.productCheckoutListVms()).thenReturn(null);
        
        ResponseEntity<ProductGetCheckoutListVm> responseEntity = ResponseEntity.ok(mockResponseList);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        assertThrows(NotFoundException.class, () -> productService.getProductInfomation(Set.of(10L), 0, 10));
    }

    // ==========================================
    // TEST HÀM: Fallbacks (Của CircuitBreaker)
    // ==========================================
    @Test
    void testFallbacks_ShouldCallSuperClassMethod() {
        Throwable t = new RuntimeException("Test Exception");
        
        // Vì fallback gọi hàm của lớp cha (vốn ném ra lỗi), ta assertThrows để kiểm tra
        assertThrows(Throwable.class, () -> productService.handleProductVariationListFallback(t));
        assertThrows(Throwable.class, () -> productService.handleProductInfomationFallback(t));
    }
}