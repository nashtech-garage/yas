package com.yas.product.controller;

import com.yas.product.ProductApplication;
import com.yas.product.model.ProductOption;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.viewmodel.error.ErrorVm;
import com.yas.product.viewmodel.productoption.ProductOptionGetVm;
import com.yas.product.viewmodel.productoption.ProductOptionPostVm;
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

@SpringBootTest(classes = ProductApplication.class)
@AutoConfigureMockMvc
public class ProductOptionControllerTest {
    @Autowired
    private ProductOptionRepository productOptionRepository;
    private ProductOption productOption;
    @Autowired
    private WebTestClient webTestClient;
    private final String USERNAME = "admin";
    private final String ROLE = "ADMIN";
    private final String BACK_OFFICE_URL = "/backoffice/product-options";
    private Long invalidId = 9999L;

    @BeforeEach
    public void setUp() {
        productOption = new ProductOption();
        productOption.setName("camera");
        productOption = productOptionRepository.save(productOption);
    }

    @AfterEach
    public void tearDown() {
        productOptionRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    public void listProductOption_ReturnListProductOption_Success() {
        EntityExchangeResult<List<ProductOptionGetVm>> result =
                webTestClient.get().uri(BACK_OFFICE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(ProductOptionGetVm.class)
                        .returnResult();
        List<ProductOptionGetVm> productOptionGetVms = result.getResponseBody();
        assertEquals(1, productOptionGetVms.size());
        assertEquals(productOption.getName(), productOptionGetVms.get(0).name());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    public void getProductOption_ReturnProductOptionGetVm_Success() {
        EntityExchangeResult<ProductOptionGetVm> result =
                webTestClient.get().uri(BACK_OFFICE_URL + "/{id}", productOption.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(ProductOptionGetVm.class)
                        .returnResult();
        ProductOptionGetVm productOptionGetVm = result.getResponseBody();
        assertNotNull(productOptionGetVm);
        assertEquals(productOption.getName(), productOptionGetVm.name());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    public void getProductOption_ProductOptionIdIsInvalid_Return404NotFound() {
        ErrorVm errorVmExpected = new ErrorVm("404 NOT_FOUND", "Not Found", "Product option 9999 is not found", Collections.emptyList());
        webTestClient.get().uri(BACK_OFFICE_URL + "/{id}", invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    public void createProductOption_VaildProductOptionPostVm_Success() {
        ProductOptionPostVm productOptionPostVm = new ProductOptionPostVm("speaker");
        EntityExchangeResult<ProductOptionGetVm> result =
                webTestClient.post().uri(BACK_OFFICE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(productOptionPostVm))
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(ProductOptionGetVm.class)
                        .returnResult();
        ProductOptionGetVm productOptionGetVm = result.getResponseBody();
        assertNotNull(productOptionGetVm);
        assertEquals(productOptionPostVm.name(), productOptionGetVm.name());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    public void updateProductOption_ProductOptionIdIsValid_Success() {
        ProductOptionPostVm productOptionPostVm = new ProductOptionPostVm("speaker");
        webTestClient.put().uri(BACK_OFFICE_URL + "/{id}", productOption.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productOptionPostVm))
                .exchange()
                .expectStatus().isNoContent();
        Optional<ProductOption> productOptionOptional = productOptionRepository.findById(productOption.getId());
        assertTrue(productOptionOptional.isPresent());
        assertEquals(productOptionPostVm.name(), productOptionOptional.get().getName());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    public void updateProductOption_ProductOptionIdIsInvalid_Return404NotFound() {
        ErrorVm errorVmExpected = new ErrorVm("404 NOT_FOUND", "Not Found", "Product option 9999 is not found", Collections.emptyList());
        ProductOptionPostVm productOptionPostVm = new ProductOptionPostVm("speaker");
        webTestClient.put().uri(BACK_OFFICE_URL + "/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(productOptionPostVm))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    public void deleteProductOption_givenProductOptionIdValid_thenSuccess() {
        webTestClient.delete().uri(BACK_OFFICE_URL + "/{id}", productOption.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
        Optional<ProductOption> productOptionOptional = productOptionRepository.findById(productOption.getId());
        assertFalse(productOptionOptional.isPresent());
    }
}
