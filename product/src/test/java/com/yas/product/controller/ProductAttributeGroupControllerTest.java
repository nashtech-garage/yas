package com.yas.product.controller;

import com.yas.product.ProductApplication;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupPostVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ProductApplication.class)
@AutoConfigureMockMvc
class ProductAttributeGroupControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ProductAttributeGroupRepository productAttributeGroupRepository;
    private final String USERNAME = "admin";
    private final String ROLE = "ADMIN";
    private final String BACK_OFFICE_URL = "/backoffice/product-attribute-groups";
    private ProductAttributeGroup productAttributeGroup1;
    private ProductAttributeGroup productAttributeGroup2;
    private ProductAttributeGroupPostVm productAttributeGroupPostVmValid;
    private ProductAttributeGroupPostVm productAttributeGroupPostVmInvalid;
    private Long invalidId = 9999L;

    @BeforeEach
    void setUp() {
        productAttributeGroup1 = new ProductAttributeGroup();
        productAttributeGroup2 = new ProductAttributeGroup();
        productAttributeGroup1.setName("productAttributeGroupName1");
        productAttributeGroup2.setName("productAttributeGroupName2");
        productAttributeGroupRepository.saveAndFlush(productAttributeGroup1);
        productAttributeGroupRepository.saveAndFlush(productAttributeGroup2);
    }

    @BeforeEach
    public void tearDown() {
        productAttributeGroupRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void listProductAttributeGroups_ReturnListProductAttributeGroupVm_Success() {
        EntityExchangeResult<List<ProductAttributeGroupVm>> result =
                webTestClient.get().uri(BACK_OFFICE_URL)
                        .accept(MediaType.APPLICATION_JSON).exchange()
                        .expectStatus().isOk()
                        .expectBodyList(ProductAttributeGroupVm.class)
                        .returnResult();
        List<ProductAttributeGroupVm> productAttributeGroupVms = result.getResponseBody();
        assertEquals(2, productAttributeGroupVms.size());
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void getProductAttributeGroup_ReturnProductAttributeGroupVm_Success() {
        EntityExchangeResult<ProductAttributeGroupVm> result =
                webTestClient.get().uri(BACK_OFFICE_URL + "/{id}", productAttributeGroup1.getId())
                        .accept(MediaType.APPLICATION_JSON).exchange()
                        .expectStatus().isOk()
                        .expectBody(ProductAttributeGroupVm.class)
                        .returnResult();
        ProductAttributeGroupVm productAttributeGroupVm = result.getResponseBody();
        assertNotNull(productAttributeGroupVm);
        assertEquals(productAttributeGroup1.getId(), productAttributeGroupVm.id());
        assertEquals(productAttributeGroup1.getName(), productAttributeGroupVm.name());
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void getProductAttributeGroup_ProductAttributeGroupIdIsInvalid_Return404NotFound() {
        ErrorVm errorVmExpected = new ErrorVm("404 NOT_FOUND", "Not Found", "Product attribute group 9999 is not found", Collections.emptyList());
        webTestClient.get().uri(BACK_OFFICE_URL + "/{id}", invalidId)
                .accept(MediaType.APPLICATION_JSON).exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void createProductAttributeGroup_ReturnProductAttributeGroupVm_Success() {
        productAttributeGroupPostVmValid = new ProductAttributeGroupPostVm("productAttributeGroupName3");
        EntityExchangeResult<ProductAttributeGroupVm> result = webTestClient.post().uri(BACK_OFFICE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributeGroupPostVmValid)).exchange()
                .expectStatus().isCreated()
                .expectBody(ProductAttributeGroupVm.class).returnResult();
        ProductAttributeGroupVm productAttributeGroupVm = result.getResponseBody();
        assertNotNull(productAttributeGroupVm);
        assertEquals(productAttributeGroupPostVmValid.name(), productAttributeGroupVm.name());
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void createProductAttributeGroup_NameIsEmpty_Return400BadRequest() {
        List<String> fieldErrors = new ArrayList<>();
        fieldErrors.add("name must not be blank");
        ErrorVm errorVmExpected = new ErrorVm("400 BAD_REQUEST", "Bad Request", "Request information is not valid", fieldErrors);
        productAttributeGroupPostVmInvalid = new ProductAttributeGroupPostVm("");
        webTestClient
                .post()
                .uri(BACK_OFFICE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributeGroupPostVmInvalid))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void updateProductAttributeGroup_ProductAttributeGroupIdIsValid_Success() {
        productAttributeGroupPostVmValid = new ProductAttributeGroupPostVm("productAttributeGroupName3");
        webTestClient
                .put()
                .uri(BACK_OFFICE_URL + "/{id}", productAttributeGroup1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributeGroupPostVmValid))
                .exchange()
                .expectStatus().isNoContent();

    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void updateProductAttributeGroup_ProductAttributeGroupIdIsInvalid_Return404NotFound() {
        ErrorVm errorVmExpected = new ErrorVm(HttpStatus.NOT_FOUND.toString(), "Not Found", String.format("Product attribute group %s is not found", invalidId));
        productAttributeGroupPostVmValid = new ProductAttributeGroupPostVm("productAttributeGroupName3");
        webTestClient.put()
                .uri(BACK_OFFICE_URL + "/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributeGroupPostVmValid))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void updateProductAttributeGroup_ProductAttributeGroupNamIsEmpty_Return400BadRequest() {
        List<String> fieldErrors = new ArrayList<>();
        fieldErrors.add("name must not be blank");
        ErrorVm errorVmExpected = new ErrorVm("400 BAD_REQUEST", "Bad Request", "Request information is not valid", fieldErrors);
        productAttributeGroupPostVmInvalid = new ProductAttributeGroupPostVm("");
        webTestClient.put()
                .uri(BACK_OFFICE_URL + "/{id}", productAttributeGroup1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productAttributeGroupPostVmInvalid))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME ,roles= {ROLE})
    void deleteProductAttributeGroup_givenProductAttributeGroupIsValid_thenSuccess(){
        webTestClient.delete()
                .uri(BACK_OFFICE_URL + "/{id}", productAttributeGroup1.getId())
                .exchange()
                .expectStatus().isNoContent();
        Optional<ProductAttributeGroup> result = productAttributeGroupRepository.findById(productAttributeGroup1.getId());
        assertFalse(result.isPresent());
    }

}