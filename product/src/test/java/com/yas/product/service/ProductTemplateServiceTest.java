package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.ProductApplication;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeTemplate;
import com.yas.product.model.attribute.ProductTemplate;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.repository.ProductAttributeTemplateRepository;
import com.yas.product.repository.ProductTemplateRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.producttemplate.ProductAttributeTemplatePostVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplateListGetVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplatePostVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplateVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ProductApplication.class)
@Transactional
class ProductTemplateServiceTest {
    ProductAttribute productAttribute1;
    ProductAttribute productAttribute2;
    List<ProductAttribute> productAttributes;
    ProductAttributeTemplate productAttributeTemplate1;
    ProductAttributeTemplate productAttributeTemplate2;
    ProductTemplate productTemplate1;
    ProductTemplate productTemplate2;
    @Autowired
    private ProductTemplateService productTemplateService;
    @Autowired
    private ProductAttributeRepository productAttributeRepository;
    @Autowired
    private ProductAttributeTemplateRepository productAttributeTemplateRepository;
    @Autowired
    private ProductAttributeGroupRepository productAttributeGroupRepository;
    @Autowired
    private ProductTemplateRepository productTemplateRepository;

    @BeforeEach
    void setUp() {
        productAttribute1 = ProductAttribute.builder().name("productAttribute1").build();
        productAttribute1 = productAttributeRepository.save(productAttribute1);
        productAttribute2 = ProductAttribute.builder().name("productAttribute2").build();
        productAttribute2 = productAttributeRepository.save(productAttribute2);
        productAttributes = List.of(productAttribute1, productAttribute2);

        productTemplate1 = ProductTemplate.builder().name("productTemplate1").build();
        productTemplate1 = productTemplateRepository.save(productTemplate1);
        productTemplate2 = ProductTemplate.builder().name("productTemplate2").build();
        productTemplate2 = productTemplateRepository.save(productTemplate2);

        productAttributeTemplate1 = ProductAttributeTemplate.builder()
                .productTemplate(productTemplate1)
                .productAttribute(productAttribute1)
                .build();
        productAttributeTemplate2 = ProductAttributeTemplate.builder()
                .productTemplate(productTemplate2)
                .productAttribute(productAttribute2)
                .build();
        productAttributeTemplateRepository.saveAll(
                List.of(productAttributeTemplate1, productAttributeTemplate2));
    }

    @AfterEach
    void tearDown() {
        productAttributeTemplateRepository.deleteAll();
        productAttributeRepository.deleteAll();
        productTemplateRepository.deleteAll();
    }

    @Test
    void getPageableProductTemplate_WhenGetPageable_thenSuccess() {
        int pageNo = 0;
        int pageSize = 10;
        int totalElement = 2;
        int totalPages = 1;
        ProductTemplateListGetVm actualResponse = productTemplateService.getPageableProductTemplate(pageNo, pageSize);
        assertEquals(true, actualResponse.isLast());
        assertEquals(totalPages, actualResponse.totalPages());
        assertEquals(pageNo, actualResponse.pageNo());
        assertEquals(pageSize, actualResponse.pageSize());
        assertEquals(totalElement, actualResponse.productTemplateVms().size());

    }

    @Test
    void getProductTemplate_WhenIdProductTemplateNotExit_ThrowsNotFoundException() {
        Long invalidId = 9999L;
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productTemplateService.getProductTemplate(invalidId));
        assertEquals(Constants.ErrorCode.PRODUCT_TEMPlATE_IS_NOT_FOUND, exception.getMessage());
    }

    @Test
    void getProductTemplate_WhenIdProductTemplateValid_thenSuccess() {
        ProductTemplate productTemplateDB = productTemplateRepository.findAll().getFirst();
        ProductTemplateVm actualResponse = productTemplateService.getProductTemplate(productTemplateDB.getId());
        assertEquals(productTemplateDB.getId(), actualResponse.id());
        assertEquals(productTemplateDB.getName(), actualResponse.name());
        assertEquals(1, actualResponse.productAttributeTemplates().size());
    }

    @Test
    void saveProductTemplate_WhenDuplicateName_ThenThrowDuplicatedException() {
        ProductTemplatePostVm productTemplatePostVm = new ProductTemplatePostVm("productTemplate1", null);
        DuplicatedException exception = assertThrows(DuplicatedException.class, () -> productTemplateService.saveProductTemplate(productTemplatePostVm));
        assertEquals("Request name productTemplate1 is already existed", exception.getMessage());

    }

    @Test
    void saveProductTemplate_WhenProductAttributesNotFound_ThenThrowBadRequestException() {
        List<ProductAttributeTemplatePostVm> listProductAttTemplates = new ArrayList<>();
        listProductAttTemplates.add(new ProductAttributeTemplatePostVm(9999L, 0));
        ProductTemplatePostVm productTemplatePostVm = new ProductTemplatePostVm("productTemplate3", listProductAttTemplates);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> productTemplateService.saveProductTemplate(productTemplatePostVm));
        assertThat(exception.getMessage()).isEqualTo(Constants.ErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND);
    }

    @Test
    void saveProductTemplate_WhenProductTemplatePostVm_ThenSuccess() {
        ProductTemplatePostVm productTemplatePostVm = new ProductTemplatePostVm("productTemplate3",
                List.of(new ProductAttributeTemplatePostVm(productAttribute1.getId(), 0)));
        ProductTemplateVm productTemplateVmDB = productTemplateService.saveProductTemplate(productTemplatePostVm);
        Optional<ProductTemplate> productTemplate = productTemplateRepository.findById(productTemplateVmDB.id());
        assertTrue(productTemplate.isPresent());
        assertEquals(productTemplatePostVm.name(), productTemplate.get().getName());
    }

    @Test
    void updateProductTemplate_WhenIdProductTemplateNotExist_ThenThrowNotFoundException() {
        ProductTemplatePostVm productTemplatePostVm = new ProductTemplatePostVm("productTemplate2",
                List.of(new ProductAttributeTemplatePostVm(productAttribute1.getId(), 0)));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productTemplateService.updateProductTemplate(9999L, productTemplatePostVm));
        assertEquals(Constants.ErrorCode.PRODUCT_TEMPlATE_IS_NOT_FOUND, exception.getMessage());
    }

    @Test
    void updateProductTemplate_WhenValidData_ThenSuccess() {
        // Cập nhật template1 với attribute2
        ProductTemplatePostVm postVm = new ProductTemplatePostVm("Updated Template 1",
                List.of(new ProductAttributeTemplatePostVm(productAttribute2.getId(), 1)));

        productTemplateService.updateProductTemplate(productTemplate1.getId(), postVm);

        ProductTemplate updatedTemplate = productTemplateRepository.findById(productTemplate1.getId()).get();
        assertEquals("Updated Template 1", updatedTemplate.getName());
        
        // Verify attribute template đã thay đổi
        List<ProductAttributeTemplate> attrs = productAttributeTemplateRepository
                .findAllByProductTemplateId(productTemplate1.getId());
        assertEquals(1, attrs.size());
        assertEquals(productAttribute2.getId(), attrs.getFirst().getProductAttribute().getId());
    }

    @Test
    void updateProductTemplate_WhenAttributeIdNotFound_ThenThrowBadRequestException() {
        // Cố tình truyền vào một Attribute ID không tồn tại trong DB (9999L)
        ProductTemplatePostVm postVm = new ProductTemplatePostVm("Updated Template",
                List.of(new ProductAttributeTemplatePostVm(9999L, 1)));

        assertThrows(BadRequestException.class, () -> 
            productTemplateService.updateProductTemplate(productTemplate1.getId(), postVm));
    }

    @Test
    void updateProductTemplate_WhenPartialAttributeIdsNotFound_ThenThrowBadRequestException() {
        // Truyền vào 1 Attribute ID hợp lệ và 1 ID KHÔNG hợp lệ
        ProductTemplatePostVm postVm = new ProductTemplatePostVm("Updated Template",
                List.of(
                    new ProductAttributeTemplatePostVm(productAttribute1.getId(), 1),
                    new ProductAttributeTemplatePostVm(9999L, 2)
                ));

        assertThrows(BadRequestException.class, () -> 
            productTemplateService.updateProductTemplate(productTemplate1.getId(), postVm));
    }

    @Test
    void checkExistedName_ShouldReturnTrue_WhenNameExistsAndDifferentId() {
        // productTemplate2 đã tồn tại trong DB, thử check xem nếu truyền id của template1 vào thì có báo trùng không
        boolean isExisted = productTemplateService.checkExistedName("productTemplate2", productTemplate1.getId());
        assertTrue(isExisted);
    }
}
