package com.yas.promotion.service;

import static com.yas.promotion.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.promotion.config.ServiceUrlConfig;
import com.yas.promotion.viewmodel.BrandVm;
import com.yas.promotion.viewmodel.CategoryGetVm;
import com.yas.promotion.viewmodel.ProductVm;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
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
    void testGetProductByIds_ifNormalCase_returnProductVms() {

        List<Long> ids = List.of(1L);

        URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.product())
            .path("/backoffice/products/by-ids")
            .queryParams(createIdParams(ids))
            .build()
            .toUri();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        ProductVm product = new ProductVm(
            1L,
            "Example Product",
            "example-product",
            true,
            true,
            false,
            true,
            ZonedDateTime.now(),
            2L
        );

        when(responseSpec.toEntity(new ParameterizedTypeReference<List<ProductVm>>() {}))
            .thenReturn(ResponseEntity.ok(List.of(product)));

        List<ProductVm> result = productService.getProductByIds(ids);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(1);

    }

    @Test
    void testGetCategoryByIds_ifNormalCase_returnCategoryGetVms() {

        List<Long> ids = List.of(2L);

        URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.product())
            .path("/backoffice/categories/by-ids")
            .queryParams(createIdParams(ids))
            .build()
            .toUri();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        CategoryGetVm category = new CategoryGetVm(
            2L,
            "Example Category",
            "example-category",
            0L
        );

        when(responseSpec.toEntity(new ParameterizedTypeReference<List<CategoryGetVm>>() {}))
            .thenReturn(ResponseEntity.ok(List.of(category)));

        List<CategoryGetVm> result = productService.getCategoryByIds(ids);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(2);
    }

    @Test
    void testGetBrandByIds_ifNormalCase_returnBrandVms() {

        List<Long> ids = List.of(4L);

        URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.product())
            .path("/backoffice/brands/by-ids")
            .queryParams(createIdParams(ids))
            .build()
            .toUri();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        BrandVm brand = new BrandVm(
            3L,
            "Example Brand",
            "example-brand",
            true
        );

        when(responseSpec.toEntity(new ParameterizedTypeReference<List<BrandVm>>() {}))
            .thenReturn(ResponseEntity.ok(List.of(brand)));

        List<BrandVm> result = productService.getBrandByIds(ids);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(3);
    }

    private MultiValueMap<String, String> createIdParams(List<Long> ids) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        ids.stream().map(Objects::toString).forEach(id -> params.add("ids", id));
        return params;
    }
}