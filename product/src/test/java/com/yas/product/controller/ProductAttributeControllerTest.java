package com.yas.product.controller;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.viewmodel.productattribute.ProductAttributeGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributePostVm;
import com.yas.product.service.ProductAttributeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductAttributeControllerTest {

    ProductAttributeRepository productAttributeRepository;
    ProductAttributeService productAttributeService;
    ProductAttributeController productAttributeController;
    UriComponentsBuilder uriComponentsBuilder;
    Principal principal;
    ProductAttribute productAttribute = new ProductAttribute();

    ProductAttributeGroup productAttributeGroup = new ProductAttributeGroup();

    @BeforeEach
    void setUp(){
        productAttributeRepository = mock(ProductAttributeRepository.class);
        productAttributeService = mock(ProductAttributeService.class);
        uriComponentsBuilder = mock(UriComponentsBuilder.class);
        principal = mock(Principal.class);
        productAttributeController = new ProductAttributeController(productAttributeRepository,
                productAttributeService);
        productAttributeGroup.setId(1L);
        productAttributeGroup.setName("Computer");
        productAttribute.setId(1L);
        productAttribute.setName("Ram");
        productAttribute.setProductAttributeGroup(productAttributeGroup);
    }

    @Test
    void listProductAttributes_ValidListProductAttributeGetVm_Success(){
        List<ProductAttribute> listProductAttribute = List.of(productAttribute);
        when(productAttributeRepository.findAll()).thenReturn(listProductAttribute);
        ResponseEntity<List<ProductAttributeGetVm>> result = productAttributeController.listProductAttributes();
        assertThat(result.getStatusCode(),is(HttpStatus.OK));
        assertEquals(Objects.requireNonNull(result.getBody()).size(), listProductAttribute.size());
        for(int i=0;i<listProductAttribute.size();i++){
            assertEquals(result.getBody().get(i).id(), listProductAttribute.get(i).getId());
            assertEquals(result.getBody().get(i).name(), listProductAttribute.get(i).getName());
        }
    }

    @Test
    void getProductAttribute_FinProductAttributeById_ThrowException(){
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException notFoundException =  Assertions.assertThrows(NotFoundException.class,
                () -> productAttributeController.getProductAttribute(1L));
        assertThat(notFoundException.getMessage(),is("Product attribute 1 is not found"));
    }

    @Test
    void getProductAttribute_FindProductAttribute_Success(){
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        ResponseEntity<ProductAttributeGetVm> result = productAttributeController.getProductAttribute(1L);
        assertEquals(Objects.requireNonNull(result.getBody()).name(), productAttribute.getName());
        assertEquals(result.getBody().id(), productAttribute.getId());
        assertEquals(result.getBody().id(), productAttribute.getId());
        assertEquals(result.getBody().productAttributeGroup(), productAttribute.getProductAttributeGroup().getName());
    }

    @Test
    void createProductAttribute_FindIdProductAttributeGroup_ThrowException(){
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("Ram",1L);
        when(productAttributeService.save(any())).thenThrow(new BadRequestException("Product attribute group 1 is not found"));
        BadRequestException exception =  Assertions.assertThrows(BadRequestException.class,
                () -> productAttributeController.createProductAttribute(productAttributePostVm, UriComponentsBuilder.fromPath("/product-attribute/{id}"), principal));
        assertThat(exception.getMessage(),is("Product attribute group 1 is not found"));
    }

    @Test
    void createProductAttribute_ValidProductAttributeWithIdProductAttributeGroup_Success(){
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("Ram",1L);
        ProductAttribute savedProductAttribute = new ProductAttribute();
        savedProductAttribute.setName(productAttributePostVm.name());
        savedProductAttribute.setId(1L);
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setName("Computer");
        savedProductAttribute.setProductAttributeGroup(group);
        when(productAttributeService.save(any())).thenReturn(savedProductAttribute);
        UriComponentsBuilder newUriComponentsBuilder = mock(UriComponentsBuilder.class);
        UriComponents uriComponents = mock(UriComponents.class);
        when(uriComponentsBuilder.replacePath("/product-attribute/{id}")).thenReturn(newUriComponentsBuilder);
        when(newUriComponentsBuilder.buildAndExpand(savedProductAttribute.getId())).thenReturn(uriComponents);
        ResponseEntity<ProductAttributeGetVm> result = productAttributeController.createProductAttribute(productAttributePostVm
                , uriComponentsBuilder, principal);
        assertEquals(savedProductAttribute.getName(), productAttributePostVm.name());
        assertEquals(Objects.requireNonNull(result.getBody()).productAttributeGroup() , productAttributeGroup.getName() );
    }

    @Test
    void updateProductAttribute_FindIdProductAttribute_ThrowException(){
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("Ram",1L);
        when(productAttributeService.update(any(), anyLong())).thenThrow(new NotFoundException("Product attribute 1 is not found"));
        NotFoundException exception =  Assertions.assertThrows(NotFoundException.class,
                () -> productAttributeController.updateProductAttribute(1L,productAttributePostVm));
        assertThat(exception.getMessage(),is("Product attribute 1 is not found"));
    }
    @Test
    void updateProductAttribute_FindProductAttributeGroupId_ThrowException(){
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("Ram",1L);
        when(productAttributeService.update(any(), anyLong())).thenThrow(new BadRequestException("Product attribute group 1 is not found"));
        BadRequestException exception =  Assertions.assertThrows(BadRequestException.class,
                () -> productAttributeController.updateProductAttribute(1L,productAttributePostVm));
        assertThat(exception.getMessage(),is("Product attribute group 1 is not found"));
    }
    @Test
    void updateProductAttribute_ValidProductAttributePostVmWithProductAttributeGroupId_Success(){
        ProductAttributePostVm productAttributePostVm = new ProductAttributePostVm("Card",1L);
        ProductAttribute savedProductAttribute = new ProductAttribute();
        savedProductAttribute.setName(productAttributePostVm.name());
        when(productAttributeService.update(any(), anyLong())).thenReturn(savedProductAttribute);
        ResponseEntity<Void> result = productAttributeController.updateProductAttribute(1L,productAttributePostVm);
        assertEquals(savedProductAttribute.getName(), productAttributePostVm.name());
        assertThat(result.getStatusCode(),is(HttpStatus.NO_CONTENT));
    }

    @Test
    void deleteProductAttribute_givenProductAttributeIdValid_thenSuccess(){
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        ResponseEntity<Void> response = productAttributeController.deleteProductAttribute(1L);
        verify(productAttributeRepository).deleteById(1L);
        assertThat(response.getStatusCode(),is(HttpStatus.NO_CONTENT));
    }

    @Test
    void deleteProductAttribute_givenProductAttributeIdInvalid_thenThrowNotFoundException(){
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, ()->productAttributeController.deleteProductAttribute(1L));
    }

    @Test
    void deleteProductAttribute_givenProductAttributeIdContainProductAttributeValue_thenThrowBadRequestException(){
        productAttribute.setAttributeValues(List.of(new ProductAttributeValue()));
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        assertThrows(BadRequestException.class, ()->productAttributeController.deleteProductAttribute(1L));
    }
}
