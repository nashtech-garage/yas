package com.yas.inventory.service;

import static com.yas.inventory.util.SecurityContextUtils.setUpSecurityContext;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.inventory.config.ServiceUrlConfig;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.product.ProductQuantityPostVm;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

class ProductServiceTest {

    private RestClient restClient;

    private ServiceUrlConfig serviceUrlConfig;

    private ProductService productService;

    private RestClient.ResponseSpec responseSpec;

    private static final String PRODUCT_URL = "http://api.yas.local/product";

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        productService = new ProductService(restClient, serviceUrlConfig);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        setUpSecurityContext("test");
        when(serviceUrlConfig.product()).thenReturn(PRODUCT_URL);
    }

    @Test
    void testGetProduct_whenNormalCase_returnProductInfoVm() {

        Long productId = 1L;

        final URI url = UriComponentsBuilder
            .fromHttpUrl(PRODUCT_URL)
            .path("/backoffice/products/" + productId)
            .build()
            .toUri();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec
            = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        ProductInfoVm productInfoVm = new ProductInfoVm(productId,
            "ProductName", "ProductSKU", true);
        when(responseSpec.body(ProductInfoVm.class))
            .thenReturn(productInfoVm);

        ProductInfoVm result = productService.getProduct(productId);

        assertNotNull(result);
        assertEquals(productId, result.id());
        assertEquals("ProductName", result.name());
    }

    @Test
    void testFilterProducts_whenNormalCase_returnListProductInfoVm() {
        String productName = "ProductName";
        String productSku = "ProductSKU";
        List<Long> productIds = List.of(1L, 2L);
        FilterExistInWhSelection selection = FilterExistInWhSelection.YES;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", productName);
        params.add("sku", productSku);
        params.add("selection", selection.name());
        params.add("productIds", productIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

        final URI url = UriComponentsBuilder
            .fromHttpUrl(PRODUCT_URL)
            .path("/backoffice/products/for-warehouse")
            .queryParams(params)
            .build()
            .toUri();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec
            = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        ResponseEntity responseEntity = mock(ResponseEntity.class);
        ProductInfoVm productInfoVm = new ProductInfoVm(1L, productName, productSku, true);
        when(responseSpec.toEntity(new ParameterizedTypeReference<List<ProductInfoVm>>() {}))
            .thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(List.of(productInfoVm));

        List<ProductInfoVm> result = productService.filterProducts(productName, productSku, productIds, selection);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(productName, result.getFirst().name());
    }

    @Test
    void testUpdateProductQuantity_whenNormalCase_shouldNoException() {

        List<ProductQuantityPostVm> productQuantityPostVms = List.of(new ProductQuantityPostVm(1L, 100L));

        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.product())
            .path("/backoffice/products/update-quantity")
            .buildAndExpand()
            .toUri();

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(productQuantityPostVms)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        assertDoesNotThrow(() -> productService.updateProductQuantity(productQuantityPostVms));
    }
}
