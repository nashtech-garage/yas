package com.yas.recommendation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.yas.recommendation.configuration.RecommendationConfig;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

class ProductServiceTest {

    private final RecommendationConfig config = new RecommendationConfig();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockRestServiceServer server;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(config, "apiUrl", "http://product-service/api");
        RestClient.Builder restClientBuilder = RestClient.builder();
        server = MockRestServiceServer.bindTo(restClientBuilder).build();
        productService = new ProductService(restClientBuilder.build(), config);
    }

    @Test
    void shouldFetchProductDetailsFromConfiguredEndpoint() throws Exception {
        ProductDetailVm expected = new ProductDetailVm(
            42L,
            "iPhone 16",
            "Flagship phone",
            "Latest flagship phone",
            "A18 chip",
            "IP16",
            "1234567890123",
            "iphone-16",
            true,
            true,
            false,
            true,
            true,
            999.99,
            7L,
            Collections.emptyList(),
            "iPhone 16",
            "iphone,apple",
            "Latest flagship phone",
            1L,
            "Apple",
            Collections.emptyList(),
            null,
            null,
            null
        );

        server.expect(requestTo("http://product-service/api/storefront/products/detail/42"))
            .andExpect(method(GET))
            .andRespond(withSuccess(objectMapper.writeValueAsString(expected), MediaType.APPLICATION_JSON));

        ProductDetailVm actual = productService.getProductDetail(42L);

        assertNotNull(actual);
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.brandName(), actual.brandName());
        server.verify();
    }
}
