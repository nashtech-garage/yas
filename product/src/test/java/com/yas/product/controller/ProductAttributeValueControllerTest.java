package com.yas.product.controller;

import com.yas.product.exception.BadRequestException;
import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.repository.ProductAttributeValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.ProductAttributeValueGetVm;
import com.yas.product.viewmodel.ProductAttributeValuePostVm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProductAttributeValueControllerTest {

    ProductAttributeValueRepository productAttributeValueRepository;
    ProductAttributeRepository productAttributeRepository;
    ProductRepository productRepository;
    ProductAttributeValueController productAttributeValueController;
    UriComponentsBuilder uriComponentsBuilder;
    ProductAttribute productAttribute = new ProductAttribute();

    ProductAttributeGroup productAttributeGroup = new ProductAttributeGroup();
    ProductAttributeValue productAttributeValue;

    @BeforeEach
    void Setup() {
        productAttributeValueRepository = mock(ProductAttributeValueRepository.class);
        productAttributeRepository = mock(ProductAttributeRepository.class);
        productRepository = mock(ProductRepository.class);
        productAttributeValueController = new ProductAttributeValueController(productAttributeValueRepository, productAttributeRepository, productRepository);
        uriComponentsBuilder = mock(UriComponentsBuilder.class);
        productAttributeGroup.setId(1L);
        productAttributeGroup.setName("Computer");
        productAttribute.setId(1L);
        productAttribute.setName("Ram");
        productAttribute.setProductAttributeGroup(productAttributeGroup);
        productAttributeValue = new ProductAttributeValue();
        productAttributeValue.setId(1L);
        productAttributeValue.setValue("2 cm");
        productAttributeValue.setProduct(mock(Product.class));
        productAttributeValue.setProductAttribute(productAttribute);
    }

    @Test
    void listProductAttributeValuesByProductId_ProductIdIsInvalid_BadRequestException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> productAttributeValueController.listProductAttributeValuesByProductId(1L));
        assertThat(exception.getMessage(), is("Product 1 is not found"));
    }

    @Test
    void listProductAttributeValuesByProductId_ReturnListProductAttributeValueGetVms_Success() {
        Product product = mock(Product.class);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        List<ProductAttributeValue> listProductAttributeValues = List.of(productAttributeValue);
        when(productAttributeValueRepository.findAllByProduct(product)).thenReturn(listProductAttributeValues);
        ResponseEntity<List<ProductAttributeValueGetVm>> result = productAttributeValueController.listProductAttributeValuesByProductId(1L);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertEquals(Objects.requireNonNull(result.getBody()).size(), listProductAttributeValues.size());
        assertEquals(result.getBody().get(0).value(), listProductAttributeValues.get(0).getValue());
        assertEquals(result.getBody().get(0).nameProductAttribute(), listProductAttributeValues.get(0).getProductAttribute().getName());
    }

    @Test
    void createProductAttributeValue_ProductIdIsInValid_BadRequestException() {
        ProductAttributeValuePostVm productAttributeValuePostVm = new ProductAttributeValuePostVm(1L, 1L, "4 cm");
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        UriComponentsBuilder newUriComponentsBuilder = mock(UriComponentsBuilder.class);
        when(uriComponentsBuilder.replacePath("/product-attribute-value/{id}")).thenReturn(newUriComponentsBuilder);

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> productAttributeValueController.createProductAttributeValue(productAttributeValuePostVm, uriComponentsBuilder));
        assertThat(exception.getMessage(), is("Product 1 is not found"));
    }

    @Test
    void createProductAttributeValue_ProductAttributeIdIsInValid_BadRequestException() {
        Product product = mock(Product.class);
        ProductAttributeValuePostVm productAttributeValuePostVm = new ProductAttributeValuePostVm(1L, 1L, "4 cm");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.empty());

        UriComponentsBuilder newUriComponentsBuilder = mock(UriComponentsBuilder.class);
        when(uriComponentsBuilder.replacePath("/product-attribute-value/{id}")).thenReturn(newUriComponentsBuilder);

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> productAttributeValueController.createProductAttributeValue(productAttributeValuePostVm, uriComponentsBuilder));
        assertThat(exception.getMessage(), is("Product Attribute 1 is not found"));
    }

    @Test
    void createProductAttributeValue_ReturnProductAttributeValueVm_Success() {
        Product product = mock(Product.class);
        ProductAttributeValuePostVm productAttributeValuePostVm = new ProductAttributeValuePostVm(1L, 1L, "4 cm");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));

        var ProductAttributeValueCaptor = ArgumentCaptor.forClass(ProductAttributeValue.class);
        ProductAttributeValue savedProductAttributeValue = mock(ProductAttributeValue.class);
        when(savedProductAttributeValue.getProductAttribute()).thenReturn(productAttribute);
        when(savedProductAttributeValue.getValue()).thenReturn(productAttributeValuePostVm.value());
        when(productAttributeValueRepository.saveAndFlush(ProductAttributeValueCaptor.capture())).thenReturn(savedProductAttributeValue);
        UriComponentsBuilder newUriComponentsBuilder = mock(UriComponentsBuilder.class);
        UriComponents uriComponents = mock(UriComponents.class);
        when(uriComponentsBuilder.replacePath("/product-attribute-value/{id}")).thenReturn(newUriComponentsBuilder);
        when(newUriComponentsBuilder.buildAndExpand(savedProductAttributeValue.getId())).thenReturn(uriComponents);

        ResponseEntity<ProductAttributeValueGetVm> result = productAttributeValueController.createProductAttributeValue(productAttributeValuePostVm
                , uriComponentsBuilder);
        verify(productAttributeValueRepository).saveAndFlush(ProductAttributeValueCaptor.capture());
        assertEquals(ProductAttributeValueCaptor.getValue().getValue(), productAttributeValuePostVm.value());
        assertEquals(Objects.requireNonNull(result.getBody()).value() , ProductAttributeValueCaptor.getValue().getValue());
    }

    @Test
    void updateProductAttributeValue_ProductAttributeValueIdIsInValid_NotFoundException(){
        ProductAttributeValuePostVm productAttributeValuePostVm = new ProductAttributeValuePostVm(1L, 1L, "4 cm");
        when(productAttributeValueRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> productAttributeValueController.updateProductAttributeValue(1L, productAttributeValuePostVm));
        assertThat(exception.getMessage(), is("Product attribute value 1 is not found"));
    }

    @Test
    void updateProductAttributeValue_ProductAttributeValueIdIsValid_Success(){
        ProductAttributeValuePostVm productAttributeValuePostVm = new ProductAttributeValuePostVm(1L, 1L, "4 cm");
        when(productAttributeValueRepository.findById(1L)).thenReturn(Optional.of(productAttributeValue));

        var ProductAttributeValueCaptor = ArgumentCaptor.forClass(ProductAttributeValue.class);
        ProductAttributeValue savedProductAttributeValue = mock(ProductAttributeValue.class);
        when(productAttributeValueRepository.saveAndFlush(ProductAttributeValueCaptor.capture())).thenReturn(savedProductAttributeValue);
        ResponseEntity<Void> result = productAttributeValueController.updateProductAttributeValue(1L,productAttributeValuePostVm);
        verify(productAttributeValueRepository).saveAndFlush(ProductAttributeValueCaptor.capture());
        assertThat(result.getStatusCode(),is(HttpStatus.NO_CONTENT));
        assertEquals(ProductAttributeValueCaptor.getValue().getValue(), productAttributeValuePostVm.value());
    }
}
