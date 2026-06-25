/**
 * Unit tests for {@link ProductTemplateService}.
 * Coverage target: >= 70% line coverage.
 * Tests: GetPageableProductTemplateTest, GetProductTemplateTest,
 *        SaveProductTemplateTest, UpdateProductTemplateTest, ValidateExistedNameTest
 */
package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeTemplate;
import com.yas.product.model.attribute.ProductTemplate;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.repository.ProductAttributeTemplateRepository;
import com.yas.product.repository.ProductTemplateRepository;
import com.yas.product.viewmodel.producttemplate.ProductAttributeTemplatePostVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplateListGetVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplatePostVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplateVm;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductTemplateServiceTest {

    private static final Long TEMPLATE_ID = 1L;
    private static final Long ATTR_ID_1 = 10L;
    private static final Long ATTR_ID_2 = 20L;
    private static final String TEMPLATE_NAME = "Test Template";

    @Mock
    private ProductAttributeRepository productAttributeRepository;
    @Mock
    private ProductAttributeTemplateRepository productAttributeTemplateRepository;
    @Mock
    private ProductAttributeGroupRepository productAttributeGroupRepository;
    @Mock
    private ProductTemplateRepository productTemplateRepository;

    @InjectMocks
    private ProductTemplateService productTemplateService;

    private ProductTemplate buildTemplate() {
        ProductTemplate t = new ProductTemplate();
        t.setId(TEMPLATE_ID);
        t.setName(TEMPLATE_NAME);
        t.setProductAttributeTemplates(new ArrayList<>());
        return t;
    }

    @Nested
    class GetPageableProductTemplateTest {
        @Test
        void testGetPageableProductTemplate_whenTemplatesExist_shouldReturnList() {
            ProductTemplate t = buildTemplate();
            Page<ProductTemplate> page = new PageImpl<>(List.of(t), PageRequest.of(0, 10), 1);
            when(productTemplateRepository.findAll(any(Pageable.class))).thenReturn(page);

            ProductTemplateListGetVm result = productTemplateService.getPageableProductTemplate(0, 10);

            assertThat(result.productTemplateVms()).hasSize(1);
            assertThat(result.pageNo()).isZero();
        }

        @Test
        void testGetPageableProductTemplate_whenEmpty_shouldReturnEmpty() {
            Page<ProductTemplate> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            when(productTemplateRepository.findAll(any(Pageable.class))).thenReturn(page);

            ProductTemplateListGetVm result = productTemplateService.getPageableProductTemplate(0, 10);

            assertThat(result.productTemplateVms()).isEmpty();
        }
    }

    @Nested
    class GetProductTemplateTest {
        @Test
        void testGetProductTemplate_whenExists_shouldReturnVm() {
            ProductTemplate t = buildTemplate();
            when(productTemplateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.of(t));
            when(productAttributeTemplateRepository.findAllByProductTemplateId(TEMPLATE_ID))
                    .thenReturn(List.of());

            ProductTemplateVm result = productTemplateService.getProductTemplate(TEMPLATE_ID);

            assertThat(result.id()).isEqualTo(TEMPLATE_ID);
            assertThat(result.name()).isEqualTo(TEMPLATE_NAME);
        }

        @Test
        void testGetProductTemplate_whenNotFound_shouldThrowNotFoundException() {
            when(productTemplateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> productTemplateService.getProductTemplate(TEMPLATE_ID));
        }
    }

    @Nested
    class SaveProductTemplateTest {
        @Test
        void testSaveProductTemplate_whenValid_shouldSaveAndReturn() {
            ProductAttribute attr = ProductAttribute.builder().id(ATTR_ID_1).name("Color").build();
            ProductAttributeTemplatePostVm attrVm = new ProductAttributeTemplatePostVm(ATTR_ID_1, 0);
            ProductTemplatePostVm vm = new ProductTemplatePostVm(TEMPLATE_NAME, List.of(attrVm));

            ProductTemplate saved = buildTemplate();
            when(productTemplateRepository.findExistedName(TEMPLATE_NAME, null)).thenReturn(null);
            when(productTemplateRepository.save(any(ProductTemplate.class))).thenReturn(saved);
            when(productAttributeRepository.findAllById(List.of(ATTR_ID_1))).thenReturn(List.of(attr));
            when(productAttributeTemplateRepository.saveAll(any())).thenReturn(List.of());
            when(productTemplateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.of(saved));
            when(productAttributeTemplateRepository.findAllByProductTemplateId(TEMPLATE_ID))
                    .thenReturn(List.of());

            ProductTemplateVm result = productTemplateService.saveProductTemplate(vm);

            assertThat(result).isNotNull();
            verify(productTemplateRepository).save(any(ProductTemplate.class));
        }

        @Test
        void testSaveProductTemplate_whenDuplicateName_shouldThrowDuplicatedException() {
            ProductTemplatePostVm vm = new ProductTemplatePostVm(TEMPLATE_NAME, List.of());
            when(productTemplateRepository.findExistedName(TEMPLATE_NAME, null))
                    .thenReturn(new ProductTemplate());

            assertThrows(DuplicatedException.class,
                    () -> productTemplateService.saveProductTemplate(vm));
        }

        @Test
        void testSaveProductTemplate_whenAttributeNotFound_shouldThrowBadRequest() {
            ProductAttributeTemplatePostVm attrVm = new ProductAttributeTemplatePostVm(999L, 0);
            ProductTemplatePostVm vm = new ProductTemplatePostVm("New Template", List.of(attrVm));

            when(productTemplateRepository.findExistedName("New Template", null)).thenReturn(null);
            when(productAttributeRepository.findAllById(any())).thenReturn(List.of());

            assertThrows(BadRequestException.class,
                    () -> productTemplateService.saveProductTemplate(vm));
        }
    }

    @Nested
    class UpdateProductTemplateTest {
        @Test
        void testUpdateProductTemplate_whenValid_shouldUpdate() {
            ProductTemplate existing = buildTemplate();
            ProductAttribute attr = ProductAttribute.builder().id(ATTR_ID_1).name("Size").build();
            ProductAttributeTemplatePostVm attrVm = new ProductAttributeTemplatePostVm(ATTR_ID_1, 0);
            ProductTemplatePostVm vm = new ProductTemplatePostVm("Updated", List.of(attrVm));

            when(productTemplateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.of(existing));
            when(productAttributeTemplateRepository.findAllByProductTemplateId(TEMPLATE_ID))
                    .thenReturn(List.of());
            when(productAttributeRepository.findAllById(List.of(ATTR_ID_1))).thenReturn(List.of(attr));

            productTemplateService.updateProductTemplate(TEMPLATE_ID, vm);

            assertThat(existing.getName()).isEqualTo("Updated");
            verify(productTemplateRepository).save(existing);
        }

        @Test
        void testUpdateProductTemplate_whenNotFound_shouldThrowNotFoundException() {
            ProductTemplatePostVm vm = new ProductTemplatePostVm("X", List.of());
            when(productTemplateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> productTemplateService.updateProductTemplate(TEMPLATE_ID, vm));
        }
    }

    @Nested
    class ValidateExistedNameTest {
        @Test
        void testValidateExistedName_whenExists_shouldThrow() {
            when(productTemplateRepository.findExistedName("dup", null)).thenReturn(new ProductTemplate());

            assertThrows(DuplicatedException.class,
                    () -> productTemplateService.validateExistedName("dup", null));
        }

        @Test
        void testCheckExistedName_whenNotExists_shouldReturnFalse() {
            when(productTemplateRepository.findExistedName("unique", null)).thenReturn(null);

            assertThat(productTemplateService.checkExistedName("unique", null)).isFalse();
        }
    }
}
