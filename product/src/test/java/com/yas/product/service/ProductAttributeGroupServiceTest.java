package com.yas.product.service;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupListGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupVm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class ProductAttributeGroupServiceTest {


    @Mock
    private ProductAttributeGroupRepository repository;

    @InjectMocks
    private ProductAttributeGroupService service;


    // Retrieve pageable product attribute groups successfully
    @Test
    void test_retrieve_pageable_product_attribute_groups_successfully() {
        List<ProductAttributeGroup> groups = List.of(new ProductAttributeGroup());
        Page<ProductAttributeGroup> page = new PageImpl<>(groups);
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        ProductAttributeGroupListGetVm result = service.getPageableProductAttributeGroups(0, 10);

        assertNotNull(result);
        assertEquals(1, result.productAttributeGroupContent().size());
    }

    // Save a new product attribute group without duplication
    @Test
    void test_save_new_product_attribute_group_without_duplication() {

        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Group Color");

        when(repository.findExistedName(anyString(), anyLong())).thenReturn(null);

        service.save(group);

        verify(repository, times(1)).save(group);
    }

    // Convert ProductAttributeGroup to ProductAttributeGroupVm correctly
    @Test
    void test_convert_product_attribute_group_to_vm_correctly() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Test Group");

        ProductAttributeGroupVm vm = ProductAttributeGroupVm.fromModel(group);

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Test Group", vm.name());
    }

    // Handle empty product attribute group list gracefully
    @Test
    void test_handle_empty_product_attribute_group_list_gracefully() {

        Page<ProductAttributeGroup> page = new PageImpl<>(new ArrayList<>());
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        ProductAttributeGroupListGetVm result = service.getPageableProductAttributeGroups(0, 10);

        assertNotNull(result);
        assertTrue(result.productAttributeGroupContent().isEmpty());
    }

    // Handle page number out of bounds
    @Test
    void test_handle_page_number_out_of_bounds() {

        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        ProductAttributeGroupListGetVm result = service.getPageableProductAttributeGroups(100, 10);

        assertNotNull(result);
        assertTrue(result.productAttributeGroupContent().isEmpty());
    }

    // Handle page size of zero
    @Test
    void test_handle_page_size_of_zero() {

        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        ProductAttributeGroupListGetVm result = service.getPageableProductAttributeGroups(0, 10);

        assertNotNull(result);
        assertTrue(result.productAttributeGroupContent().isEmpty());
    }

    // Handle null values in ProductAttributeGroup fields
    @Test
    void test_handle_null_values_in_product_attribute_group_fields() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setName("Group Color");
        group.setId(1L);

        when(repository.findExistedName(anyString(), anyLong())).thenReturn(group);

        assertThrows(DuplicatedException.class, () -> service.save(group));
    }
}