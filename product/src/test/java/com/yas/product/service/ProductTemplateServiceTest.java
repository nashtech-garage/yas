package com.yas.product.service;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.DuplicatedException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeTemplate;
import com.yas.product.model.attribute.ProductTemplate;
import com.yas.product.repository.*;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.producttemplate.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductTemplateServiceTest {
   private ProductTemplateService productTemplateService;
   private ProductAttributeRepository productAttributeRepository;
   private ProductAttributeTemplateRepository productAttributeTemplateRepository;
   private ProductAttributeGroupRepository productAttributeGroupRepository ;
   private ProductTemplateRepository productTemplateRepository;

    ProductAttribute productAttribute1;
    ProductAttribute productAttribute2;
    List<ProductTemplate> productTemplates;
    ProductAttributeTemplate productAttributeTemplate1;
    ProductAttributeTemplate productAttributeTemplate2;
    ProductTemplate productTemplate1;
    ProductTemplate productTemplate2;

    @BeforeEach
    void setUp(){
        productTemplateService =mock(ProductTemplateService.class);
        productAttributeRepository = mock(ProductAttributeRepository.class);
        productAttributeTemplateRepository = mock(ProductAttributeTemplateRepository.class);
        productAttributeGroupRepository = mock(ProductAttributeGroupRepository.class);
        productTemplateRepository= mock(ProductTemplateRepository.class);
        productTemplateService = new ProductTemplateService(
                productAttributeRepository,
                productAttributeTemplateRepository,
                productAttributeGroupRepository,
                productTemplateRepository
        );
        productAttribute1 = new ProductAttribute(1L,"productAttribute1",null,null,null);
        productAttribute2 = new ProductAttribute(2L,"productAttribute2",null,null,null);
        productTemplate1 = ProductTemplate.builder()
                .id(1L)
                .name("productTemplate1")
                .productAttributeTemplates(null)
                .build();
        productTemplate2 = ProductTemplate.builder()
                .id(2L)
                .name("productTemplate2")
                .productAttributeTemplates(null)
                .build();
        productAttributeTemplate1 = ProductAttributeTemplate.builder()
                .productTemplate(productTemplate1)
                .productAttribute(productAttribute1)
                .build();
        productAttributeTemplate2 = ProductAttributeTemplate.builder()
                .productTemplate(productTemplate2)
                .productAttribute(productAttribute2)
                .build();
        productTemplates = List.of(productTemplate1, productTemplate1);
    }

    @Test
    void getPageableProductTemplate_WhenGetPageable_thenSuccess(){
        Page<ProductTemplate> productTemplatePage = mock(Page.class);
        List<ProductTemplateGetVm> productTemplateListGetVms = new ArrayList<>();
        productTemplateListGetVms.add(new ProductTemplateGetVm(productTemplates.get(0).getId(), productTemplates.get(0).getName()));
        productTemplateListGetVms.add(new ProductTemplateGetVm(productTemplates.get(1).getId(), productTemplates.get(1).getName()));
        int pageNo = 1;
        int pageSize = 10;
        int totalElement = 20;
        int totalPages = 1;
        var pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(productTemplateRepository.findAll(any(Pageable.class))).thenReturn(productTemplatePage);
        when(productTemplatePage.getContent()).thenReturn(productTemplates);
        when(productTemplatePage.getNumber()).thenReturn(pageNo);
        when(productTemplatePage.getTotalElements()).thenReturn((long) totalElement);
        when(productTemplatePage.getTotalPages()).thenReturn(totalPages);
        when(productTemplatePage.isLast()).thenReturn(false);
        //when
        ProductTemplateListGetVm actualResponse = productTemplateService.getPageableProductTemplate(pageNo,pageSize);
        //then
        verify(productTemplateRepository).findAll(pageableCaptor.capture());
        assertThat(actualResponse.isLast()).isEqualTo(productTemplatePage.isLast());
        assertThat(actualResponse.totalPages()).isEqualTo(productTemplatePage.getTotalPages());
        assertThat(actualResponse.pageNo()).isEqualTo(productTemplatePage.getNumber());
        assertThat(actualResponse.pageSize()).isEqualTo(productTemplatePage.getSize());
        assertThat(actualResponse.productTemplateVms()).isEqualTo(productTemplateListGetVms);
    }
    @Test
    void getProductTemplate_WhenIdProductTemplateNotExit_ThrowsNotFoundException() {
        when(productTemplateRepository.findById(1L)).thenReturn(Optional.empty());
        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productTemplateService.getProductTemplate(1L));
        //then
        assertThat(exception.getMessage()).isEqualTo(Constants.ERROR_CODE.PRODUCT_TEMPlATE_IS_NOT_FOUND, 1L);
    }

    @Test
    void getProductTemplate_WhenIdProductTemplateValid_thenSuccess(){
        when(productTemplateRepository.findById(1L)).thenReturn(Optional.of(productTemplates.get(0)));
        when(productAttributeTemplateRepository.findAllByProductTemplateId(1L))
                .thenReturn(List.of(productAttributeTemplate1));
        //when
        ProductTemplateVm actualResponse = productTemplateService.getProductTemplate(1L);

        assertThat(actualResponse.id()).isEqualTo(1L);
        assertThat(actualResponse.name()).isEqualTo(productTemplates.get(0).getName());
        assertThat(actualResponse.productAttributeTemplates()).isEqualTo(Stream.of(productAttributeTemplate1).map(ProductAttributeTemplateGetVm::fromModel).toList());
    }

    @Test
    void saveProductTemplate_WhenDuplicateName_ThenThrowDuplicatedException(){
        ProductTemplatePostVm productTemplatePostVm = new ProductTemplatePostVm("productTemplate1",null);

        when(productTemplateRepository.findExistedName(productTemplatePostVm.name(),null)).thenReturn(productTemplate1);
        //when
        DuplicatedException exception = assertThrows(DuplicatedException.class, () -> productTemplateService.saveProductTemplate(productTemplatePostVm));
        //then
        assertThat(exception.getMessage()).isEqualTo("Request name productTemplate1 is already existed");

    }
    @Test
    void saveProductTemplate_WhenProductAttributesNull_ThenThrowBadRequestException(){
        List<ProductAttributeTemplatePostVm>  listProductAttTemplates = new ArrayList<>();
        listProductAttTemplates.add(new ProductAttributeTemplatePostVm(1L,0));
        ProductTemplatePostVm productTemplatePostVm = new ProductTemplatePostVm("productTemplate1",listProductAttTemplates);
        when(productAttributeRepository.findAllById(listProductAttTemplates.stream().map(ProductAttributeTemplatePostVm::ProductAttributeId).toList())).thenReturn(new ArrayList<>());
        //when
        BadRequestException exception = assertThrows(BadRequestException.class, () -> productTemplateService.saveProductTemplate(productTemplatePostVm));
        //then
        assertThat(exception.getMessage()).isEqualTo(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_NOT_FOUND);
    }

    @Test
    void saveProductTemplate_WhenProductAttributesMissId_ThenThrowBadRequestException(){
        List<ProductAttributeTemplatePostVm>  listProductAttTemplates = Arrays.asList(
                new ProductAttributeTemplatePostVm(1L,0),
                new ProductAttributeTemplatePostVm(5L,0)
        );
        ProductTemplatePostVm productTemplatePostVm2 = new ProductTemplatePostVm("productTemplate2",listProductAttTemplates);
        List<Long> idAttributes = new ArrayList<>(listProductAttTemplates.stream().map(ProductAttributeTemplatePostVm::ProductAttributeId).toList());
        List<ProductAttribute> productAttributes = Collections.singletonList(
                productAttribute1
        );

        //when
        when(productAttributeRepository.findById(listProductAttTemplates.get(0).ProductAttributeId())).thenReturn(Optional.ofNullable(productAttribute1));
        when(productAttributeRepository.findById(listProductAttTemplates.get(1).ProductAttributeId())).thenReturn(Optional.empty());
        when(productAttributeRepository.findAllById(idAttributes)).thenReturn(notNull());
        when(productAttributeRepository.findAllById(idAttributes)).thenReturn(productAttributes);

        //then
        idAttributes.removeAll(productAttributes.stream().map(ProductAttribute::getId).toList());
        BadRequestException exception = assertThrows(BadRequestException.class, () -> productTemplateService.saveProductTemplate(productTemplatePostVm2));

        assertThat(exception.getMessage()).isEqualTo(Constants.ERROR_CODE.PRODUCT_ATTRIBUTE_NOT_FOUND);
    }

    @Test
    void saveProductTemplate_WhenProductTemplatePostVm_ThenSuccess(){
        List<ProductAttributeTemplate> attributeTemplateList = new ArrayList<>();
        List<ProductAttributeTemplatePostVm>  listProductAttTemplates = Arrays.asList(
                new ProductAttributeTemplatePostVm(1L,0),
                new ProductAttributeTemplatePostVm(2L,0)
        );
        ProductTemplate productTemplate = new ProductTemplate();
        ProductTemplatePostVm productTemplatePostVm = new ProductTemplatePostVm("productTemplate1",listProductAttTemplates);
        List<Long> idAttributes = new ArrayList<>(listProductAttTemplates.stream().map(ProductAttributeTemplatePostVm::ProductAttributeId).toList());
        List<ProductAttribute> productAttributes = Arrays.asList(
                productAttribute1,
                productAttribute2
        );

        productTemplate.setName(productTemplatePostVm.name());

        //when
        when(productAttributeRepository.findAllById(idAttributes)).thenReturn(productAttributes);
        when(productAttributeRepository.findAllById(idAttributes)).thenReturn(notNull());
        when(productAttributeRepository.findAllById(idAttributes)).thenReturn(productAttributes);
        assertFalse(productAttributeRepository.findAllById(idAttributes).isEmpty());
        assertFalse(productAttributeRepository.findAllById(idAttributes).size() < productTemplatePostVm.ProductAttributeTemplates().size());

        Map<Long, ProductAttribute> mockProductAttributeMap = mock(Map.class);

        when(mockProductAttributeMap.get(1L)).thenReturn(productAttributes.get(0));
        when(mockProductAttributeMap.get(2L)).thenReturn(productAttributes.get(1));

        for (ProductAttributeTemplatePostVm attributeTemplatePostVm : listProductAttTemplates) {
            attributeTemplateList.add(ProductAttributeTemplate
                    .builder()
                    .productAttribute(mockProductAttributeMap.get(attributeTemplatePostVm.ProductAttributeId()))
                    .productTemplate(productTemplate)
                    .displayOrder(attributeTemplatePostVm.displayOrder())
                    .build()
            );
        }
        productAttributeTemplate1 = ProductAttributeTemplate.builder()
                .id(1L)
                .productTemplate(productTemplate)
                .productAttribute(productAttribute1)
                .build();
        productAttributeTemplate2 = ProductAttributeTemplate.builder()
                .id(2L)
                .productTemplate(productTemplate)
                .productAttribute(productAttribute2)
                .build();
        List<ProductAttributeTemplate> productAttributeTemplates = List.of(productAttributeTemplate1, productAttributeTemplate2);

        ProductTemplate mainSavedProductTemplate = ProductTemplate.builder()
                .id(1L)
                .productAttributeTemplates(productAttributeTemplates)
                .name(productTemplatePostVm.name())
                .build();

        when(productTemplateRepository.save(productTemplate)).thenReturn(mainSavedProductTemplate);
        when(productAttributeTemplateRepository.saveAllAndFlush(attributeTemplateList)).thenReturn(productAttributeTemplates);

        assertEquals("productAttribute1", attributeTemplateList.get(0).getProductAttribute().getName());
        assertEquals(0, attributeTemplateList.get(0).getDisplayOrder());
        assertEquals(mainSavedProductTemplate.getName(), productTemplate.getName());
    }
    @Test
    void updateProductTemplate_WhenIdProductTemplateNotExist_ThenThrowNotFoundException(){
        when(productTemplateRepository.findById(1L)).thenReturn(Optional.empty());
        ProductTemplatePostVm productTemplatePostVm = mock(ProductTemplatePostVm.class);
        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productTemplateService.updateProductTemplate(1L,productTemplatePostVm));
        //then
        assertThat(exception.getMessage()).isEqualTo(Constants.ERROR_CODE.PRODUCT_TEMPlATE_IS_NOT_FOUND, 1L);
    }

}
