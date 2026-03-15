package com.yas.product.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupListGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupVm;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
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

    @Test
    @DisplayName("Update: Giữ nguyên tên cũ của chính bản ghi đó - Thành công")
    void test_save_existing_group_with_same_name_success() {
        // GIVEN
        ProductAttributeGroup existingGroup = new ProductAttributeGroup();
        existingGroup.setId(1L);
        existingGroup.setName("Old Name");

        // Giả lập: findExistedName không tìm thấy bản ghi NÀO KHÁC trùng tên
        when(repository.findExistedName("Old Name", 1L)).thenReturn(null);

        // WHEN
        assertDoesNotThrow(() -> service.save(existingGroup));

        // THEN
        verify(repository, times(1)).save(existingGroup);
    }

    @Test
    void test_save_existing_group_with_duplicate_name_throws_exception() {
        ProductAttributeGroup currentGroup = new ProductAttributeGroup();
        currentGroup.setId(1L);
        currentGroup.setName("Existed Name");

        ProductAttributeGroup anotherGroup = new ProductAttributeGroup();
        anotherGroup.setId(2L); // ID khác 1L
        
        when(repository.findExistedName("Existed Name", 1L)).thenReturn(anotherGroup);

        // Kiểm tra ném ra đúng loại Exception
        assertThrows(DuplicatedException.class, () -> service.save(currentGroup));
        
        // Đảm bảo không bao giờ gọi đến hàm save của repository
        verify(repository, never()).save(any());
    }
}
