package com.yas.product.service;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.ProductOption;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.viewmodel.productoption.ProductOptionListGetVm;
import com.yas.product.viewmodel.productoption.ProductOptionPostVm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class ProductOptionServiceTest {

    @Mock
    private ProductOptionRepository productOptionRepository;
    @InjectMocks
    private ProductOptionService productOptionService;

    // Retrieve a pageable list of product options successfully
    @Test
    void test_get_pageable_product_options_success() {
        List<ProductOption> productOptions = List.of(new ProductOption(), new ProductOption());
        Page<ProductOption> productOptionPage = new PageImpl<>(productOptions);
        when(productOptionRepository.findAll(any(Pageable.class))).thenReturn(productOptionPage);

        ProductOptionListGetVm result = productOptionService.getPageableProductOptions(0, 2);

        assertEquals(2, result.productOptionContent().size());
        assertEquals(0, result.pageNo());
        assertEquals(2, result.pageSize());
    }

    // Create a new product option when the name is unique
    @Test
    void test_create_product_option_unique_name() {
        ProductOptionPostVm postVm = new ProductOptionPostVm("UniqueName");
        when(productOptionRepository.findExistedName(anyString(), isNull())).thenReturn(null);
        when(productOptionRepository.save(any(ProductOption.class))).thenReturn(new ProductOption());

        ProductOption result = productOptionService.create(postVm);

        assertNotNull(result);
        verify(productOptionRepository).save(any(ProductOption.class));
    }

    // Update an existing product option when the name is unique
    @Test
    void test_update_product_option_unique_name() {
        ProductOption existingProductOption = new ProductOption();
        existingProductOption.setId(1L);
        existingProductOption.setName("OldName");

        ProductOptionPostVm postVm = new ProductOptionPostVm("NewUniqueName");
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(existingProductOption));
        when(productOptionRepository.findExistedName(anyString(), eq(1L))).thenReturn(null);
        when(productOptionRepository.save(any(ProductOption.class))).thenReturn(existingProductOption);

        ProductOption result = productOptionService.update(postVm, 1L);

        assertNotNull(result);
        assertEquals("NewUniqueName", result.getName());
    }

    // Attempt to create a product option with a duplicated name
    @Test
    void test_create_product_option_duplicated_name() {
        ProductOptionPostVm postVm = new ProductOptionPostVm("DuplicatedName");
        when(productOptionRepository.findExistedName(anyString(), isNull())).thenReturn(new ProductOption());

        assertThrows(DuplicatedException.class, () -> {
            productOptionService.create(postVm);
        });
    }

    // Attempt to update a product option with a duplicated name
    @Test
    void test_update_product_option_duplicated_name() {
        ProductOption existingProductOption = new ProductOption();
        existingProductOption.setId(1L);
        existingProductOption.setName("OldName");

        ProductOptionPostVm postVm = new ProductOptionPostVm("DuplicatedName");
        when(productOptionRepository.findById(1L)).thenReturn(Optional.of(existingProductOption));
        when(productOptionRepository.findExistedName(anyString(), eq(1L))).thenReturn(new ProductOption());

        assertThrows(DuplicatedException.class, () -> {
            productOptionService.update(postVm, 1L);
        });
    }

    // Attempt to update a non-existent product option
    @Test
    void test_update_non_existent_product_option() {
        ProductOptionPostVm postVm = new ProductOptionPostVm("NewName");
        when(productOptionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            productOptionService.update(postVm, 1L);
        });
    }

    // Handle an empty list of product options gracefully
    @Test
    void test_get_pageable_product_options_empty_list() {
        Page<ProductOption> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 2), 0);
        when(productOptionRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        ProductOptionListGetVm result = productOptionService.getPageableProductOptions(0, 2);

        assertTrue(result.productOptionContent().isEmpty());
        assertEquals(0, result.pageNo());
        assertEquals(2, result.pageSize());
    }
}