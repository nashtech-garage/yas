package com.yas.product.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.viewmodel.productattribute.ProductAttributeListGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributePostVm;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
class ProductAttributeServiceTest {

    @Mock
    private ProductAttributeRepository productAttributeRepository;

    @Mock
    private ProductAttributeGroupRepository productAttributeGroupRepository;

    @InjectMocks
    private ProductAttributeService productAttributeService;

    // --- CÁC TEST CASE CŨ CỦA BẠN (GIỮ NGUYÊN) ---

    @Test
    void test_retrieve_pageable_product_attributes_successfully() {
        List<ProductAttribute> productAttributes = new ArrayList<>();
        productAttributes.add(new ProductAttribute(1L, "Attribute1", null, null, null));
        Page<ProductAttribute> page = new PageImpl<>(productAttributes);
        when(productAttributeRepository.findAll(any(Pageable.class))).thenReturn(page);

        ProductAttributeListGetVm result = productAttributeService.getPageableProductAttributes(0, 10);

        assertEquals(1, result.productAttributeContent().size());
        assertEquals("Attribute1", result.productAttributeContent().get(0).name());
    }

    @Test
    void test_save_new_product_attribute_with_valid_name_and_group_id() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Group1");

        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setId(1L);
        productAttribute.setName("Attribute1");
        productAttribute.setProductAttributeGroup(group);

        when(productAttributeGroupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(productAttributeRepository.save(any(ProductAttribute.class))).thenReturn(productAttribute);

        ProductAttributePostVm postVm = new ProductAttributePostVm("Attribute1", 1L);
        ProductAttribute result = productAttributeService.save(postVm);

        assertEquals("Attribute1", result.getName());
        assertEquals(group, result.getProductAttributeGroup());
    }

    @Test
    void test_update_existing_product_attribute_with_valid_name_and_group_id() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Group1");

        ProductAttribute existingAttr = new ProductAttribute();
        existingAttr.setId(1L);
        existingAttr.setName("Attribute1");
        existingAttr.setProductAttributeGroup(group);

        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(existingAttr));
        when(productAttributeGroupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(productAttributeRepository.save(any(ProductAttribute.class))).thenReturn(existingAttr);

        ProductAttributePostVm postVm = new ProductAttributePostVm("Updated Attribute", 1L);
        ProductAttribute result = productAttributeService.update(postVm, 1L);

        assertEquals("Updated Attribute", result.getName());
        assertEquals(group, result.getProductAttributeGroup());
    }

    @Test
    void test_save_product_attribute_with_null_group_id() {
        when(productAttributeRepository.save(any(ProductAttribute.class)))
                .thenReturn(new ProductAttribute(1L, "New Attribute", null, null, null));

        ProductAttributePostVm postVm = new ProductAttributePostVm("New Attribute", null);
        ProductAttribute result = productAttributeService.save(postVm);

        assertEquals("New Attribute", result.getName());
        assertNull(result.getProductAttributeGroup());
    }

    @Test
    void test_update_product_attribute_with_null_group_id() {
        ProductAttribute existingAttr = new ProductAttribute(1L, "Old Attribute", null, null, null);
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(existingAttr));
        when(productAttributeRepository.save(any(ProductAttribute.class)))
                .thenReturn(new ProductAttribute(1L, "Updated Attribute", null, null, null));

        ProductAttributePostVm postVm = new ProductAttributePostVm("Updated Attribute", null);
        ProductAttribute result = productAttributeService.update(postVm, 1L);

        assertEquals("Updated Attribute", result.getName());
        assertNull(result.getProductAttributeGroup());
    }

    @Test
    void test_save_product_attribute_with_duplicated_name() {
        when(productAttributeRepository.findExistedName("Duplicate Name", null)).thenReturn(new ProductAttribute());

        ProductAttributePostVm vm = new ProductAttributePostVm("Duplicate Name", null);
        assertThrows(DuplicatedException.class, () -> productAttributeService.save(vm));
    }

    @Test
    void test_update_product_attribute_with_duplicated_name() {
        when(productAttributeRepository.findExistedName("Duplicate Name", 1L)).thenReturn(new ProductAttribute());

        ProductAttributePostVm vm = new ProductAttributePostVm("Duplicate Name", null);
        assertThrows(DuplicatedException.class, () -> productAttributeService.update(vm, 1L));
    }

    // --- BỔ SUNG CÁC TEST CASE CÒN THIẾU ---

    @Test
    @DisplayName("Lưu thuộc tính thất bại khi không tìm thấy Group ID")
    void test_save_product_attribute_fails_when_group_not_found() {
        when(productAttributeGroupRepository.findById(99L)).thenReturn(Optional.empty());

        ProductAttributePostVm postVm = new ProductAttributePostVm("Attribute1", 99L);
        
        assertThrows(BadRequestException.class, () -> productAttributeService.save(postVm));
    }

    @Test
    @DisplayName("Cập nhật thất bại khi không tìm thấy ID của thuộc tính")
    void test_update_product_attribute_fails_when_attribute_not_found() {
        when(productAttributeRepository.findById(99L)).thenReturn(Optional.empty());

        ProductAttributePostVm postVm = new ProductAttributePostVm("Updated Name", null);
        
        assertThrows(NotFoundException.class, () -> productAttributeService.update(postVm, 99L));
    }

    @Test
    @DisplayName("Cập nhật thất bại khi ID thuộc tính tồn tại nhưng Group ID truyền vào không tồn tại")
    void test_update_product_attribute_fails_when_group_id_not_found() {
        ProductAttribute existingAttr = new ProductAttribute(1L, "Name", null, null, null);
        
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(existingAttr));
        when(productAttributeGroupRepository.findById(88L)).thenReturn(Optional.empty());

        ProductAttributePostVm postVm = new ProductAttributePostVm("Name", 88L);
        
        assertThrows(BadRequestException.class, () -> productAttributeService.update(postVm, 1L));
    }

    @Test
    @DisplayName("Lấy danh sách phân trang khi repository trả về rỗng")
    void test_retrieve_pageable_product_attributes_empty() {
        when(productAttributeRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        ProductAttributeListGetVm result = productAttributeService.getPageableProductAttributes(0, 10);

        assertEquals(0, result.productAttributeContent().size());
        assertEquals(0, result.totalElements());
    }
}
