package com.yas.product.controller;

import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.brand.BrandPostVm;
import com.yas.product.viewmodel.brand.BrandVm;
import com.yas.product.viewmodel.error.ErrorVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BrandControllerTest extends BaseControllerTest{
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductRepository productRepository;

    private final Brand brand1 = new Brand();
    private Long brandId;

    private final String STORE_FRONT_URL = "/storefront/brands";
    private final String BACK_OFFICE_URL = "/backoffice/brands";
    private Long invalidId = 9999L;

    @BeforeEach
    void setup() {
        super.setup();
        brand1.setName("iphone13");
        brand1.setSlug("iphone-13");
        brand1.setProducts(List.of());
        brandId = brandRepository.save(brand1).getId();
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        brandRepository.deleteAll();
    }

    @Test
    void listBrands_StoreFrontReturnList_Success() {
        EntityExchangeResult<List<BrandVm>> result =
                webTestClient.get().uri(STORE_FRONT_URL)
                        .accept(MediaType.APPLICATION_JSON).exchange()
                        .expectStatus().isOk()
                        .expectBodyList(BrandVm.class)
                        .returnResult();
        List<BrandVm> brandVms = result.getResponseBody();
        assertEquals(1, brandVms.size());
        assertEquals(brand1.getName(), brandVms.get(0).name());
        assertEquals(brand1.getSlug(), brandVms.get(0).slug());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void listBrands_BackOfficeReturnList_Success() {
        EntityExchangeResult<List<BrandVm>> result =
                webTestClient
                        .get().uri(BACK_OFFICE_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBodyList(BrandVm.class)
                        .returnResult();
        List<BrandVm> brandVms = result.getResponseBody();
        assertEquals(1, brandVms.size());
        assertEquals(brand1.getName(), brandVms.get(0).name());
        assertEquals(brand1.getSlug(), brandVms.get(0).slug());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void getBrand_FindIdBrand_Return404NotFound() {
        ErrorVm errorVmExpected = new ErrorVm("404 NOT_FOUND", "Not Found", "Brand 9999 is not found", Collections.emptyList());

        webTestClient.get().uri(BACK_OFFICE_URL + "/{id}", invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void getBrand_FindIdBrand_Success() {
        EntityExchangeResult<BrandVm> result = webTestClient.get().uri(BACK_OFFICE_URL + "/{id}", brandId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BrandVm.class)
                .returnResult();
        BrandVm brandVm = result.getResponseBody();
        assertNotNull(brandVm);
        assertEquals(brand1.getName(), brandVm.name());
        assertEquals(brand1.getSlug(), brandVm.slug());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void createBrand_SaveBrandPostVm_Success() {
        BrandPostVm brandPostVm = new BrandPostVm("samsung", "samsung", true);
        EntityExchangeResult<BrandVm> result = webTestClient
                .post().uri(BACK_OFFICE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(brandPostVm))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BrandVm.class)
                .returnResult();
        BrandVm brandVm = result.getResponseBody();
        assertNotNull(brandVm);
        assertEquals(brandPostVm.name(), brandVm.name());
        assertEquals(brandPostVm.slug(), brandVm.slug());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void updateBrand_FindIdBrandUpdate_404NotFound() {
        BrandPostVm brandPostVm = new BrandPostVm("samsung", "samsung", true);
        ErrorVm errorVmExpected = new ErrorVm("404 NOT_FOUND", "Not Found", "Brand 9999 is not found", Collections.emptyList());

        webTestClient.put().uri(BACK_OFFICE_URL + "/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(brandPostVm))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void updateBrand_UpdateBrand_Success() {
        BrandPostVm brandPostVm = new BrandPostVm("samsung", "samsung", true);
        webTestClient.put().uri(BACK_OFFICE_URL + "/{id}", brandId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(brandPostVm))
                .exchange()
                .expectStatus().isNoContent();
        Optional<Brand> brand = brandRepository.findById(brandId);
        assertTrue(brand.isPresent());
        assertEquals("samsung", brand.get().getName());
        assertEquals("samsung", brand.get().getSlug());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void deleteBrand_ValidCategory_Success() {
        webTestClient.delete().uri(BACK_OFFICE_URL + "/{id}", brandId).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
        Optional<Brand> brand = brandRepository.findById(brandId);
        assertFalse(brand.isPresent());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void deleteBrand_NotFoundCategory_404NotFound() {
        ErrorVm errorVmExpected = new ErrorVm("404 NOT_FOUND", "Not Found", "Brand 9999 is not found", Collections.emptyList());
        webTestClient.delete().uri(BACK_OFFICE_URL + "/{id}", invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isNotFound()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {ROLE})
    void deleteBrand_CategoryContainsProducts_400BadRequest() {
        ErrorVm errorVmExpected = new ErrorVm("400 BAD_REQUEST", "Bad Request", "Please make sure this brand don't contains any product", Collections.emptyList());
        Product product = Product.builder()
                .name(String.format("product"))
                .slug(String.format("slug"))
                .isAllowedToOrder(true)
                .isPublished(true)
                .isFeatured(true)
                .isVisibleIndividually(true)
                .stockTrackingEnabled(true)
                .brand(brand1)
                .build();
        productRepository.save(product);
        brand1.setProducts(List.of(product));
        brandRepository.save(brand1).getId();
        webTestClient.delete().uri(BACK_OFFICE_URL + "/{id}", brandId)
                .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isBadRequest()
                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
    }
}