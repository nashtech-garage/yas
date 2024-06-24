package com.yas.product.controller;

import com.yas.product.ProductApplication;
import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.product.ProductOptionValueGetVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductOptionValueControllerTest extends BaseControllerTest {
    @Autowired
    private ProductOptionValueRepository productOptionValueRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductOptionRepository productOptionRepository;
    private ProductOptionValue productOptionValue;
    private ProductOption productOption;
    private Product product;
    private final String STORE_FRONT_URL = "/storefront/product-option-values";
    private final String BACK_OFFICE_URL = "/backoffice/product-option-values";
    private Long invalidId = 9999L;

    @BeforeEach
    void setup() {
        super.setup();
        product = Product.builder()
                .name(String.format("product"))
                .slug(String.format("slug"))
                .isAllowedToOrder(true)
                .isPublished(true)
                .isFeatured(true)
                .isVisibleIndividually(true)
                .stockTrackingEnabled(true)
                .build();
        product = productRepository.save(product);

        productOption = new ProductOption();
        productOption.setName("camera");
        productOption = productOptionRepository.save(productOption);

        productOptionValue = new ProductOptionValue();
        productOptionValue.setProduct(product);
        productOptionValue.setProductOption(productOption);
        productOptionValue.setValue("red");
        productOptionValue.setDisplayType("Text");
        productOptionValue.setDisplayOrder(2);
        productOptionValue = productOptionValueRepository.save(productOptionValue);
    }

    @AfterEach
    void tearDown() {
        productOptionValueRepository.deleteAll();
        productOptionRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void listProductOptionValues_ReturnListProductOptionValueGetVm_Success() {
        EntityExchangeResult<List<com.yas.product.viewmodel.productoption.ProductOptionValueGetVm>> result =
                webTestClient.get().uri(BACK_OFFICE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(com.yas.product.viewmodel.productoption.ProductOptionValueGetVm.class)
                        .returnResult();
        List<com.yas.product.viewmodel.productoption.ProductOptionValueGetVm> productOptionValueGetVms = result.getResponseBody();
        assertEquals(1, productOptionValueGetVms.size());
        assertEquals(productOptionValue.getValue(), productOptionValueGetVms.get(0).value());
    }

    @Test
    void listProductOptionValueOfProduct_ProductIdIsInvalid_Return404NotFound() {
        ErrorVm errorVmExpected = new ErrorVm("404 NOT_FOUND", "Not Found", "Product 9999 is not found", Collections.emptyList());
        webTestClient.get().uri(STORE_FRONT_URL + "/{productId}", invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    void listProductOptionValueOfProduct_ProductIdIsValid_Success() {
        EntityExchangeResult<List<ProductOptionValueGetVm>> result =
                webTestClient.get().uri(STORE_FRONT_URL + "/{productId}", product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(ProductOptionValueGetVm.class)
                        .returnResult();
        List<ProductOptionValueGetVm> productOptionValueGetVms = result.getResponseBody();
        assertEquals(1, productOptionValueGetVms.size());
        assertEquals(productOptionValue.getValue(), productOptionValueGetVms.getFirst().productOptionValue());
    }
}
