//package com.yas.product.controller;
//
//import com.yas.product.model.attribute.ProductAttributeGroup;
//import com.yas.product.repository.ProductAttributeGroupRepository;
//import com.yas.product.viewmodel.error.ErrorVm;
//import com.yas.product.viewmodel.productattribute.ProductAttributeGroupPostVm;
//import com.yas.product.viewmodel.productattribute.ProductAttributeGroupVm;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.springframework.web.reactive.function.BodyInserters;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//class ProductAttributeGroupControllerTest {
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    @Autowired
//    private ProductAttributeGroupRepository productAttributeGroupRepository;
//    private ProductAttributeGroupVm productAttributeGroupVmExpected1;
//    private List<ProductAttributeGroupVm> listProductAttributeGroupVmExpected;
//    private Long validId;
//    private Long invalidId;
//    private ProductAttributeGroupPostVm productAttributeGroupPostVmValid;
//    private ProductAttributeGroupPostVm productAttributeGroupPostVmInvalid;
//
//    @BeforeEach
//    void setUp() {
//        ProductAttributeGroup productAttributeGroup1 = new ProductAttributeGroup();
//        ProductAttributeGroup productAttributeGroup2 = new ProductAttributeGroup();
//        productAttributeGroup1.setName("productAttributeGroupName1");
//        productAttributeGroup2.setName("productAttributeGroupName2");
//        productAttributeGroupRepository.saveAndFlush(productAttributeGroup1);
//        productAttributeGroupRepository.saveAndFlush(productAttributeGroup2);
//
//        productAttributeGroupVmExpected1 = new ProductAttributeGroupVm(1L, "productAttributeGroupName1");
//        listProductAttributeGroupVmExpected = new ArrayList<>();
//        listProductAttributeGroupVmExpected.addAll(List.of(
//                new ProductAttributeGroupVm(1L, "productAttributeGroupName1"),
//                new ProductAttributeGroupVm(2L, "productAttributeGroupName2")));
//
//        validId = 1L;
//        invalidId = 3L;
//
//        productAttributeGroupPostVmValid = new ProductAttributeGroupPostVm("productAttributeGroupName3");
//        productAttributeGroupPostVmInvalid = new ProductAttributeGroupPostVm("");
//    }
//
//    @Test
//    void listProductAttributeGroups_ReturnListProductAttributeGroupVm_Success() {
//        webTestClient.get()
//                .uri("/backoffice/product-attribute-groups")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(ProductAttributeGroupVm.class).isEqualTo(listProductAttributeGroupVmExpected);
//    }
//
//    @Test
//    void getProductAttributeGroup_ReturnProductAttributeGroupVm_Success() {
//        webTestClient.get()
//                .uri("/backoffice/product-attribute-groups/{id}", validId)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(ProductAttributeGroupVm.class).isEqualTo(productAttributeGroupVmExpected1);
//    }
//
//    @Test
//    void getProductAttributeGroup_ProductAttributeGroupIdIsInvalid_ThrowsNotFoundException() {
//        ErrorVm errorVmExpected = new ErrorVm(HttpStatus.NOT_FOUND.toString(), "NotFound",
//                String.format("Product attribute group %s is not found", invalidId));
//
//        webTestClient.get()
//                .uri("/backoffice/product-attribute-groups/{id}", invalidId)
//                .exchange()
//                .expectStatus().isNotFound()
//                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
//    }
//
//    @Test
//    void createProductAttributeGroup_ReturnProductAttributeGroupVm_Success() {
//        ProductAttributeGroupVm productAttributeGroupVmExpected3 = new ProductAttributeGroupVm(3L, "productAttributeGroupName3");
//
//        webTestClient.post()
//                .uri("/backoffice/product-attribute-groups")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(productAttributeGroupPostVmValid))
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(ProductAttributeGroupVm.class).isEqualTo(productAttributeGroupVmExpected3);
//    }
//
//    @Test
//    void createProductAttributeGroup_NameIsEmpty_ThrowsMethodArgumentNotValidException() {
//        List<String> fieldErrors = new ArrayList<>();
//        fieldErrors.add("name must not be empty");
//        ErrorVm errorVmExpected = new ErrorVm("400", "Bad Request", "Request information is not valid", fieldErrors);
//
//        webTestClient.post()
//                .uri("/backoffice/product-attribute-groups")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(productAttributeGroupPostVmInvalid))
//                .exchange()
//                .expectStatus().isBadRequest()
//                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
//    }
//
//    @Test
//    void updateProductAttributeGroup_ProductAttributeGroupIdIsValid_Success() {
//        webTestClient.put()
//                .uri("/backoffice/product-attribute-groups/{id}", validId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(productAttributeGroupPostVmValid))
//                .exchange()
//                .expectStatus().isNoContent();
//    }
//
//    @Test
//    void updateProductAttributeGroup_ProductAttributeGroupIdIsInvalid_ThrowsNotFoundException() {
//        ErrorVm errorVmExpected = new ErrorVm(HttpStatus.NOT_FOUND.toString(), "NotFound", String.format("Product attribute group %s is not found", invalidId));
//
//        webTestClient.put()
//                .uri("/backoffice/product-attribute-groups/{id}", invalidId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(productAttributeGroupPostVmValid))
//                .exchange()
//                .expectStatus().isNotFound()
//                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
//    }
//
//    @Test
//    void updateProductAttributeGroup_ProductAttributeGroupNamIsEmpty_ThrowsMethodArgumentNotValidException() {
//        List<String> fieldErrors = new ArrayList<>();
//        fieldErrors.add("name must not be empty");
//        ErrorVm errorVmExpected = new ErrorVm("400", "Bad Request", "Request information is not valid", fieldErrors);
//
//        webTestClient.put()
//                .uri("/backoffice/product-attribute-groups/{id}", validId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(productAttributeGroupPostVmInvalid))
//                .exchange()
//                .expectStatus().isBadRequest()
//                .expectBody(ErrorVm.class).isEqualTo(errorVmExpected);
//    }
//
//    @Test
//    void deleteProductAttributeGroup_givenProductAttributeGroupIsValid_thenSuccess(){
//        webTestClient.delete()
//                .uri("/backoffice/product-attribute-groups/{id}", validId)
//                .exchange()
//                .expectStatus().isNoContent();
//    }
//
//}