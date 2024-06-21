package com.yas.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.product.ProductApplication;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.repository.ProductAttributeValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributePostVm;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ProductApplication.class)
@AutoConfigureMockMvc
class ProductAttributeControllerTest {

    @Autowired
    private ProductAttributeRepository productAttributeRepository;
    @Autowired
    private ProductAttributeGroupRepository productAttributeGroupRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductAttributeValueRepository productAttributeValueRepository;
    @Autowired
    private WebTestClient webTestClient;
    private Long productAttributeId;
    private final String USERNAME = "admin";
    private final String ROLE = "ADMIN";
    private final String BACK_OFFICE_URL = "/backoffice/product-attribute";
    private ProductAttribute productAttribute;
    private ProductAttributeGroup productAttributeGroup;
    private Long invalidId = 9999L;

    @BeforeEach
    void setUp(){
        productAttributeGroup = new ProductAttributeGroup();
        productAttributeGroup.setName("Computer");
        productAttributeGroup = productAttributeGroupRepository.save(productAttributeGroup);
        productAttribute = new ProductAttribute();
        productAttribute.setName("Ram");
        productAttribute.setProductAttributeGroup(productAttributeGroup);
        productAttribute = productAttributeRepository.save(productAttribute);
        productAttributeId = productAttribute.getId();
    }

    @AfterEach
    void tearDown() {
        productAttributeValueRepository.deleteAll();
        productRepository.deleteAll();
        productAttributeRepository.deleteAll();
        productAttributeGroupRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void listProductAttributes_ValidListProductAttributeGetVm_Success() {
        EntityExchangeResult<List<ProductAttributeGetVm>> result =
                webTestClient.get().uri(BACK_OFFICE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(ProductAttributeGetVm.class)
                        .returnResult();
        List<ProductAttributeGetVm> productAttributeGetVms = result.getResponseBody();
        assertEquals(1, productAttributeGetVms.size());
        assertEquals(productAttribute.getName(), productAttributeGetVms.get(0).name());
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void getProductAttribute_FinProductAttributeById_404NotFound() {
        ErrorVm errorVmExpected = new ErrorVm("404 NOT_FOUND", "Not Found", "Product attribute 9999 is not found", Collections.emptyList());
        webTestClient.get().uri(BACK_OFFICE_URL + "/{id}", invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void getProductAttribute_FindProductAttribute_Success() {
        EntityExchangeResult<ProductAttributeGetVm> result =
                webTestClient.get().uri(BACK_OFFICE_URL + "/{id}", productAttributeId)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(ProductAttributeGetVm.class)
                        .returnResult();
        ProductAttributeGetVm productAttributeGetVm = result.getResponseBody();
        assertNotNull(productAttributeGetVm);
        assertEquals(productAttribute.getName(), productAttributeGetVm.name());
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void createProductAttribute_FindIdProductAttributeGroup_400BadRequest() {
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("CPU", 9999L);
        ErrorVm errorVmExpected = new ErrorVm("400 BAD_REQUEST", "Bad Request", "Product attribute group 9999 is not found", Collections.emptyList());
        webTestClient.post().uri(BACK_OFFICE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributePostVm))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void createProductAttribute_ValidProductAttributeWithIdProductAttributeGroup_Success() {
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("CPU", productAttributeGroup.getId());
        EntityExchangeResult<ProductAttributeGetVm> result = webTestClient.post().uri(BACK_OFFICE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributePostVm))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductAttributeGetVm.class)
                .returnResult();
        ProductAttributeGetVm productAttributeGetVm = result.getResponseBody();
        assertNotNull(productAttributeGetVm);
        assertEquals(productAttributePostVm.name(), productAttributeGetVm.name());
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void updateProductAttribute_FindIdProductAttribute_404NotFound() {
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("CPU", productAttributeGroup.getId());
        ErrorVm errorVmExpected = new ErrorVm("404 NOT_FOUND", "Not Found", "Product attribute 9999 is not found", Collections.emptyList());

        webTestClient.put().uri(BACK_OFFICE_URL + "/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributePostVm))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void updateProductAttribute_FindProductAttributeGroupId_400BadRequest() {
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("CPU", invalidId);
        ErrorVm errorVmExpected = new ErrorVm("400 BAD_REQUEST", "Bad Request", "Product attribute group 9999 is not found", Collections.emptyList());

        webTestClient.put().uri(BACK_OFFICE_URL + "/{id}", productAttributeId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributePostVm))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);

    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void updateProductAttribute_ValidProductAttributePostVmWithProductAttributeGroupId_Success() {
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("CPU", productAttributeGroup.getId());
        webTestClient.put().uri(BACK_OFFICE_URL + "/{id}", productAttributeId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributePostVm))
                .exchange()
                .expectStatus().isNoContent();
        Optional<ProductAttribute> optionalProductAttribute = productAttributeRepository.findById(productAttributeId);
        assertTrue(optionalProductAttribute.isPresent());
        assertEquals(productAttributePostVm.name(), optionalProductAttribute.get().getName());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void deleteProductAttribute_givenProductAttributeIdValid_thenSuccess() {
        webTestClient.delete().uri(BACK_OFFICE_URL + "/{id}", productAttributeId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
        Optional<ProductAttribute> productAttribute = productAttributeRepository.findById(productAttributeId);
        assertFalse(productAttribute.isPresent());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void deleteProductAttribute_givenProductAttributeIdInvalid_thenReturn404NotFound() {
        ErrorVm errorVmExpected = new ErrorVm("404 NOT_FOUND", "Not Found", "Product attribute 9999 is not found", Collections.emptyList());

        webTestClient.delete().uri(BACK_OFFICE_URL + "/{id}", invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void deleteProductAttribute_givenProductAttributeIdContainProductAttributeValue_thenReturn400BadRequest() throws Exception {
        Product product = Product.builder()
                .name(String.format("product"))
                .slug(String.format("slug"))
                .isAllowedToOrder(true)
                .isPublished(true)
                .isFeatured(true)
                .isVisibleIndividually(true)
                .stockTrackingEnabled(true)
                .build();
        productRepository.save(product);

        ProductAttributeValue productAttributeValue = new ProductAttributeValue();
        productAttributeValue.setProduct(product);
        productAttributeValue.setProductAttribute(productAttribute);
        productAttributeValueRepository.save(productAttributeValue);

        ErrorVm errorVmExpected = new ErrorVm("400 BAD_REQUEST", "Bad Request",
                "Please make sure this Product Attribute doesn't exist in any Product Attribute Values", Collections.emptyList());

        webTestClient.delete().uri(BACK_OFFICE_URL + "/{id}", productAttributeId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }
}
