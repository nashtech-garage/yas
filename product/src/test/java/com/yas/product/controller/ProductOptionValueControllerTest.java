package com.yas.product.controller;

import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.ProductOptionValueGetVm;
import com.yas.product.viewmodel.ProductOptionValuePostVm;
import org.hamcrest.Matchers;
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

public class ProductOptionValueControllerTest {
    ProductOptionValueRepository productOptionValueRepository;
    ProductRepository productRepository;
    ProductOptionRepository productOptionRepository;
    ProductOptionValueController productOptionValueController;
    UriComponentsBuilder uriComponentsBuilder;
    ProductOptionValue productOptionValue = new ProductOptionValue();
    ProductOption productOption = new ProductOption() ;
    Product product = new Product();

    @BeforeEach
    void setup(){
        productOptionValueRepository = mock(ProductOptionValueRepository.class);
        productRepository = mock(ProductRepository.class);
        productOptionRepository = mock(ProductOptionRepository.class);
        uriComponentsBuilder = mock(UriComponentsBuilder.class);
        productOptionValueController = new ProductOptionValueController(productOptionValueRepository, productRepository, productOptionRepository);
        product = mock(Product.class);
        productOption = mock(ProductOption.class);
        productOptionValue.setProduct(product);
        productOptionValue.setProductOption(productOption);
        productOptionValue.setValue("red");
        productOptionValue.setDisplayType("Text");
        productOptionValue.setDisplayOrder(2);
    }
    @Test
    void listProductOptionValues_ReturnListProductOptionValueGetVm_Success(){
        List<ProductOptionValue> productOptionValues = List.of(productOptionValue);
        when(productOptionValueRepository.findAll()).thenReturn(productOptionValues);
        ResponseEntity<List<ProductOptionValueGetVm>> result = productOptionValueController.listProductOptionValues();
        assertThat(result.getStatusCode(),is(HttpStatus.OK));
        assertEquals(Objects.requireNonNull(result.getBody()).size(), productOptionValues.size());
        for(int i=0;i<productOptionValues.size();i++){
            assertEquals(result.getBody().get(i).value(), productOptionValues.get(i).getValue());
            assertEquals(result.getBody().get(i).displayType(), productOptionValues.get(i).getDisplayType());
            assertEquals(result.getBody().get(i).displayOrder(), productOptionValues.get(i).getDisplayOrder());
        }
    }
    @Test
    void listProductOptionValueOfProduct_ProductIdIsInvalid_ThrowNotFoundException(){
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                ()->productOptionValueController.listProductOptionValueOfProduct(1L));
        assertThat(exception.getMessage(), Matchers.is("Product 1 is not found"));
    }
    @Test
    void listProductOptionValueOfProduct_ProductIdIsValid_Success(){
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        List<ProductOptionValue> productOptionValues = List.of(productOptionValue);
        when(productOptionValueRepository.findAllByProduct(product)).thenReturn(productOptionValues);
        ResponseEntity<List<ProductOptionValueGetVm>> result = productOptionValueController.listProductOptionValueOfProduct(1L);
        assertThat(result.getStatusCode(),is(HttpStatus.OK));
        assertEquals(Objects.requireNonNull(result.getBody()).size(), productOptionValues.size());
        for(int i=0;i<productOptionValues.size();i++){
            assertEquals(result.getBody().get(i).value(), productOptionValues.get(i).getValue());
            assertEquals(result.getBody().get(i).displayType(), productOptionValues.get(i).getDisplayType());
            assertEquals(result.getBody().get(i).displayOrder(), productOptionValues.get(i).getDisplayOrder());
        }
    }
    @Test
    void createProductOptionValue_ProductIdIsValid_ThrowNotFoundException(){
        ProductOptionValuePostVm productOptionValuePostVm = new ProductOptionValuePostVm(1L , 1L , "Text",2,"Yellow");
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                ()->productOptionValueController.createProductOptionValue(productOptionValuePostVm, UriComponentsBuilder.fromPath("/product-option-values/{id}")));
        assertThat(exception.getMessage(), Matchers.is("Product 1 is not found"));
    }
    @Test
    void createProductOptionValue_ProductOptionIdIsInvalid_ThrowNotFoundException(){
        ProductOptionValuePostVm productOptionValuePostVm = new ProductOptionValuePostVm(1L , 1L , "Text",2,"Yellow");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                ()->productOptionValueController.createProductOptionValue(productOptionValuePostVm, UriComponentsBuilder.fromPath("/product-option-values/{id}")));
        assertThat(exception.getMessage(), Matchers.is("Product option 1 is not found"));
    }
    @Test
    void createProductOptionValue_ReturnProductOptionValueGetVm_Success(){
        ProductOptionValuePostVm productOptionValuePostVm = new ProductOptionValuePostVm(1L , 1L , "Text",2,"Yellow");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));

        var ProductOptionValueCaptor = ArgumentCaptor.forClass(ProductOptionValue.class);
        ProductOptionValue savedProductOptionValue= mock(ProductOptionValue.class);

        when(savedProductOptionValue.getProductOption()).thenReturn(productOption);
        when(savedProductOptionValue.getProduct()).thenReturn(product);
        when(savedProductOptionValue.getValue()).thenReturn(productOptionValuePostVm.value());
        when(savedProductOptionValue.getDisplayOrder()).thenReturn(productOptionValuePostVm.displayOrder());
        when(savedProductOptionValue.getDisplayType()).thenReturn(productOptionValuePostVm.displayType());
        when(productOptionValueRepository.saveAndFlush(ProductOptionValueCaptor.capture())).thenReturn(savedProductOptionValue);
        UriComponentsBuilder newUriComponentsBuilder = mock(UriComponentsBuilder.class);
        UriComponents uriComponents = mock(UriComponents.class);
        when(uriComponentsBuilder.replacePath("/product-option-values/{id}")).thenReturn(newUriComponentsBuilder);
        when(newUriComponentsBuilder.buildAndExpand(savedProductOptionValue.getId())).thenReturn(uriComponents);

        ResponseEntity<ProductOptionValueGetVm> result = productOptionValueController.createProductOptionValue(productOptionValuePostVm
                , uriComponentsBuilder);
        verify(productOptionValueRepository).saveAndFlush(ProductOptionValueCaptor.capture());
        assertEquals(ProductOptionValueCaptor.getValue().getValue(), productOptionValuePostVm.value());
        assertEquals(Objects.requireNonNull(result.getBody()).value() , ProductOptionValueCaptor.getValue().getValue());
    }
    @Test
    void updateProductOptionValue_ProductOptionValueIdIsInValid_ThrowNotFoundException(){
        ProductOptionValuePostVm productOptionValuePostVm = new ProductOptionValuePostVm(1L , 1L , "Text",2,"Yellow");
        when(productOptionValueRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                ()->productOptionValueController.updateProductOptionValue(1L,productOptionValuePostVm));
        assertThat(exception.getMessage(), Matchers.is("Product option value 1 is not found"));
    }

    @Test
    void updateProductOptionValue_ProductIdIsInValid_ThrowNotFoundException(){
        ProductOptionValuePostVm productOptionValuePostVm = new ProductOptionValuePostVm(1L , 1L , "Text",2,"Yellow");
        when(productOptionValueRepository.findById(1L)).thenReturn(Optional.of(productOptionValue));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                ()->productOptionValueController.updateProductOptionValue(1L,productOptionValuePostVm));
        assertThat(exception.getMessage(), Matchers.is("Product 1 is not found"));
    }
    @Test
    void updateProductOptionValue_ProductOptionIdIsInValid_ThrowNotFoundException(){
        ProductOptionValuePostVm productOptionValuePostVm = new ProductOptionValuePostVm(1L , 1L , "Text",2,"Yellow");
        when(productOptionValueRepository.findById(1L)).thenReturn(Optional.of(productOptionValue));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                ()->productOptionValueController.updateProductOptionValue(1L,productOptionValuePostVm));
        assertThat(exception.getMessage(), Matchers.is("Product option 1 is not found"));
    }
    @Test
    void updateProductOptionValue_AllIdIsValid_Success(){
        ProductOptionValuePostVm productOptionValuePostVm = new ProductOptionValuePostVm(1L , 1L , "Text",2,"Yellow");
        when(productOptionValueRepository.findById(1L)).thenReturn(Optional.of(productOptionValue));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(productOption));

        var ProductOptionValueCaptor = ArgumentCaptor.forClass(ProductOptionValue.class);
        ProductOptionValue savedProductOptionValue= mock(ProductOptionValue.class);

        when(savedProductOptionValue.getProductOption()).thenReturn(productOption);
        when(savedProductOptionValue.getProduct()).thenReturn(product);
        when(savedProductOptionValue.getValue()).thenReturn(productOptionValuePostVm.value());
        when(savedProductOptionValue.getDisplayOrder()).thenReturn(productOptionValuePostVm.displayOrder());
        when(savedProductOptionValue.getDisplayType()).thenReturn(productOptionValuePostVm.displayType());
        when(productOptionValueRepository.saveAndFlush(ProductOptionValueCaptor.capture())).thenReturn(savedProductOptionValue);

        ResponseEntity<Void> result = productOptionValueController.updateProductOptionValue(1L,productOptionValuePostVm);
        verify(productOptionValueRepository).saveAndFlush(ProductOptionValueCaptor.capture());
        assertThat(result.getStatusCode(), Matchers.is(HttpStatus.NO_CONTENT));
    }
}
