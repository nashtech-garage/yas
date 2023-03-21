package com.yas.product.controller;

import com.yas.product.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.product.ProductOptionValueGetVm;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductOptionValueControllerTest {
    ProductOptionValueRepository productOptionValueRepository;
    ProductRepository productRepository;
    ProductOptionValueController productOptionValueController;
    UriComponentsBuilder uriComponentsBuilder;
    ProductOptionValue productOptionValue = new ProductOptionValue();
    ProductOption productOption = new ProductOption();
    Product product = new Product();

    @BeforeEach
    void setup() {
        productOptionValueRepository = mock(ProductOptionValueRepository.class);
        productRepository = mock(ProductRepository.class);
        uriComponentsBuilder = mock(UriComponentsBuilder.class);
        productOptionValueController = new ProductOptionValueController(productOptionValueRepository, productRepository);
        product = mock(Product.class);
        productOption = mock(ProductOption.class);
        productOptionValue.setProduct(product);
        productOptionValue.setProductOption(productOption);
        productOptionValue.setValue("red");
        productOptionValue.setDisplayType("Text");
        productOptionValue.setDisplayOrder(2);
    }

    @Test
    void listProductOptionValues_ReturnListProductOptionValueGetVm_Success() {
        List<ProductOptionValue> productOptionValues = List.of(productOptionValue);
        when(productOptionValueRepository.findAll()).thenReturn(productOptionValues);
        ResponseEntity<List<com.yas.product.viewmodel.productoption.ProductOptionValueGetVm>> result = productOptionValueController.listProductOptionValues();
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertEquals(Objects.requireNonNull(result.getBody()).size(), productOptionValues.size());
        for (int i = 0; i < productOptionValues.size(); i++) {
            assertEquals(result.getBody().get(i).value(), productOptionValues.get(i).getValue());
            assertEquals(result.getBody().get(i).displayType(), productOptionValues.get(i).getDisplayType());
            assertEquals(result.getBody().get(i).displayOrder(), productOptionValues.get(i).getDisplayOrder());
        }
    }

    @Test
    void listProductOptionValueOfProduct_ProductIdIsInvalid_ThrowNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> productOptionValueController.listProductOptionValueOfProduct(1L));
        assertThat(exception.getMessage(), Matchers.is("Product 1 is not found"));
    }

    @Test
    void listProductOptionValueOfProduct_ProductIdIsValid_Success() {
        List<ProductOptionValue> productOptionValues = List.of(productOptionValue);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionValueRepository.findAllByProduct(product)).thenReturn(productOptionValues);

        ResponseEntity<List<ProductOptionValueGetVm>> result = productOptionValueController.listProductOptionValueOfProduct(1L);

        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertEquals(Objects.requireNonNull(result.getBody()).size(), productOptionValues.size());
        for (int i = 0; i < productOptionValues.size(); i++) {
            assertEquals(result.getBody().get(i).productOptionValue(), productOptionValues.get(i).getValue());
            assertEquals(result.getBody().get(i).productOptionId(), productOptionValues.get(i).getProductOption().getId());
            assertEquals(result.getBody().get(i).productOptionName(), productOptionValues.get(i).getProductOption().getName());
        }
    }
}
