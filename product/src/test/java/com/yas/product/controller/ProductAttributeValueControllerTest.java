package com.yas.product.controller;

import com.yas.product.ProductApplication;
import com.yas.product.model.Product;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.repository.ProductAttributeValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeValueGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeValuePostVm;
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
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProductAttributeValueControllerTest extends BaseControllerTest {

    @Autowired
    private ProductAttributeGroupRepository productAttributeGroupRepository;
    @Autowired
    private ProductAttributeValueRepository productAttributeValueRepository;
    @Autowired
    private ProductAttributeRepository productAttributeRepository;
    @Autowired
    private ProductRepository productRepository;
    private final String BACK_OFFICE_URL = "/backoffice/product-attribute-value";
    private Long invalidId = 9999L;
    private ProductAttributeGroup productAttributeGroup;
    private ProductAttribute productAttribute;
    private ProductAttributeValue productAttributeValue;
    private Product product;

    @BeforeEach
    public void setup() {
        super.setup();
        productAttributeGroup = new ProductAttributeGroup();
        productAttributeGroup.setName("Computer");
        productAttributeGroup = productAttributeGroupRepository.save(productAttributeGroup);
        productAttribute = new ProductAttribute();
        productAttribute.setName("Ram");
        productAttribute.setProductAttributeGroup(productAttributeGroup);
        productAttribute = productAttributeRepository.save(productAttribute);
        productAttribute.setProductAttributeGroup(productAttributeGroup);

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

        productAttributeValue = new ProductAttributeValue();
        productAttributeValue.setValue("2 cm");
        productAttributeValue.setProduct(product);
        productAttributeValue.setProductAttribute(productAttribute);
        productAttributeValue = productAttributeValueRepository.save(productAttributeValue);
    }

    @AfterEach
    void tearDown() {
        productAttributeValueRepository.deleteAll();
        productRepository.deleteAll();
        productAttributeRepository.deleteAll();
        productAttributeGroupRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void listProductAttributeValuesByProductId_ProductIdIsInvalid_Return400BadRequest() {
        ErrorVm errorVmExpected = new ErrorVm("400 BAD_REQUEST", "Bad Request", "Product 9999 is not found", Collections.emptyList());
        webTestClient.get().uri(BACK_OFFICE_URL + "/{productId}", invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void listProductAttributeValuesByProductId_ReturnListProductAttributeValueGetVms_Success() {
        EntityExchangeResult<List<ProductAttributeValueGetVm>> result =
                webTestClient.get().uri(BACK_OFFICE_URL + "/{productId}", product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(ProductAttributeValueGetVm.class)
                        .returnResult();
        List<ProductAttributeValueGetVm> productAttributeValueGetVms = result.getResponseBody();
        assertEquals(1, productAttributeValueGetVms.size());
        assertEquals(productAttributeValue.getValue(), productAttributeValueGetVms.get(0).value());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void createProductAttributeValue_ProductIdIsInValid_Return400BadRequest() {
        ProductAttributeValuePostVm productAttributeValuePostVm = new ProductAttributeValuePostVm(invalidId, productAttribute.getId(), "4 cm");
        ErrorVm errorVmExpected = new ErrorVm("400 BAD_REQUEST", "Bad Request", "Product 9999 is not found", Collections.emptyList());
        webTestClient.post().uri(BACK_OFFICE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributeValuePostVm))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void createProductAttributeValue_ProductAttributeIdIsInValid_Return400BadRequest() {
        ProductAttributeValuePostVm productAttributeValuePostVm = new ProductAttributeValuePostVm(product.getId(), invalidId, "4 cm");
        ErrorVm errorVmExpected = new ErrorVm("400 BAD_REQUEST", "Bad Request", "Product Attribute 9999 is not found", Collections.emptyList());
        webTestClient.post().uri(BACK_OFFICE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributeValuePostVm))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void createProductAttributeValue_ReturnProductAttributeValueVm_Success() {
        ProductAttributeValuePostVm productAttributeValuePostVm = new ProductAttributeValuePostVm(product.getId(), productAttribute.getId(), "4 cm");
        EntityExchangeResult<ProductAttributeValueGetVm> result = webTestClient.post().uri(BACK_OFFICE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributeValuePostVm))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductAttributeValueGetVm.class)
                .returnResult();
        ProductAttributeValueGetVm productAttributeValueGetVm = result.getResponseBody();
        assertNotNull(productAttributeValueGetVm);
        assertEquals(productAttributeValuePostVm.value(), productAttributeValueGetVm.value());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void updateProductAttributeValue_ProductAttributeValueIdIsInValid_Return404NotFound() {
        ProductAttributeValuePostVm productAttributeValuePostVm = new ProductAttributeValuePostVm(product.getId(), productAttribute.getId(), "4 cm");
        ErrorVm errorVmExpected = new ErrorVm("404 NOT_FOUND", "Not Found", "Product attribute value 9999 is not found", Collections.emptyList());

        webTestClient.put().uri(BACK_OFFICE_URL + "/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributeValuePostVm))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void updateProductAttributeValue_ProductAttributeValueIdIsValid_Success() {
        ProductAttributeValuePostVm productAttributeValuePostVm = new ProductAttributeValuePostVm(product.getId(), productAttribute.getId(), "4 cm");

        webTestClient.put().uri(BACK_OFFICE_URL + "/{id}", productAttributeValue.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributeValuePostVm))
                .exchange()
                .expectStatus().isNoContent();
        Optional<ProductAttributeValue> productAttributeValueOptional = productAttributeValueRepository.findById(productAttributeValue.getId());
        assertTrue(productAttributeValueOptional.isPresent());
        assertEquals(productAttributeValuePostVm.value(), productAttributeValueOptional.get().getValue());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void deleteProductAttributeValueById_ProductAttributeValueIdIsInValid_Success() {
        webTestClient.delete().uri(BACK_OFFICE_URL + "/{id}", productAttributeValue.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
        Optional<ProductAttributeValue> productAttributeValueOptional = productAttributeValueRepository.findById(productAttributeValue.getId());
        assertFalse(productAttributeValueOptional.isPresent());
    }
}
