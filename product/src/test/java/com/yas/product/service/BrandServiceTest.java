package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product; // Đảm bảo đã import đúng class Product
import com.yas.product.repository.BrandRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.brand.BrandListGetVm;
import com.yas.product.viewmodel.brand.BrandPostVm;
import com.yas.product.viewmodel.brand.BrandVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    private Brand brand;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(1L);
        brand.setName("Apple");
        brand.setSlug("apple");
        // Khởi tạo danh sách trống để tránh NullPointerException khi gọi getProducts()
        brand.setProducts(Collections.emptyList());
    }

    // --- CÁC TEST CASE CHO GET BRANDS (PHÂN TRANG) ---

    @Test
    @DisplayName("Lấy danh sách brand phân trang thành công")
    void test_retrieve_paginated_brands_successfully() {
        List<Brand> brands = List.of(brand, new Brand());
        Page<Brand> brandPage = new PageImpl<>(brands);
        when(brandRepository.findAll(any(Pageable.class))).thenReturn(brandPage);

        BrandListGetVm result = brandService.getBrands(0, 2);

        assertNotNull(result);
        assertEquals(2, result.brandContent().size());
        assertEquals(0, result.pageNo());
    }

    // --- CÁC TEST CASE CHO CREATE ---

    @Test
    @DisplayName("Tạo brand mới thành công khi dữ liệu hợp lệ")
    void test_create_brand_successfully() {
        BrandPostVm brandPostVm = new BrandPostVm("Samsung", "samsung-slug", true);
        when(brandRepository.findExistedName("Samsung", null)).thenReturn(null);
        when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Brand result = brandService.create(brandPostVm);

        assertEquals("Samsung", result.getName());
        verify(brandRepository).save(any(Brand.class));
    }

    @Test
    @DisplayName("Tạo brand thất bại khi tên đã tồn tại")
    void test_create_brand_with_existing_name() {
        BrandPostVm brandPostVm = new BrandPostVm("Apple", "apple-slug", true);
        when(brandRepository.findExistedName("Apple", null)).thenReturn(new Brand());

        assertThrows(DuplicatedException.class, () -> brandService.create(brandPostVm));
    }

    // --- CÁC TEST CASE CHO UPDATE ---

    @Test
    @DisplayName("Cập nhật brand thành công")
    void test_update_brand_successfully() {
        BrandPostVm brandPostVm = new BrandPostVm("Apple Updated", "apple-slug-updated", true);
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.findExistedName("Apple Updated", 1L)).thenReturn(null);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        Brand result = brandService.update(brandPostVm, 1L);

        assertEquals("Apple Updated", result.getName());
        verify(brandRepository).save(brand);
    }

    @Test
    @DisplayName("Cập nhật brand thất bại khi ID không tồn tại")
    void test_update_nonexistent_brand() {
        BrandPostVm brandPostVm = new BrandPostVm("Sony", "sony-slug", true);
        when(brandRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> brandService.update(brandPostVm, 99L));
    }

    // --- CÁC TEST CASE CHO DELETE ---

    @Test
    @DisplayName("Xóa brand thành công khi không chứa sản phẩm")
    void test_delete_brand_successfully() {
        brand.setProducts(List.of()); // Dùng List.of() thay cho Set
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        assertDoesNotThrow(() -> brandService.delete(1L));
        verify(brandRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Xóa brand thất bại khi brand đang chứa sản phẩm")
    void test_delete_brand_fails_when_contains_products() {
        // Tạo một list chứa ít nhất 1 sản phẩm để kích hoạt logic BadRequest
        Product mockProduct = new Product();
        brand.setProducts(List.of(mockProduct)); 
        
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        assertThrows(BadRequestException.class, () -> brandService.delete(1L));
        verify(brandRepository, never()).deleteById(anyLong());
    }

    // --- CÁC TEST CASE CHO GET BY IDS ---

    @Test
    @DisplayName("Lấy danh sách brand theo list IDs thành công")
    void test_getBrandsByIds_successfully() {
        List<Long> ids = List.of(1L);
        when(brandRepository.findAllById(ids)).thenReturn(List.of(brand));

        List<BrandVm> result = brandService.getBrandsByIds(ids);

        assertEquals(1, result.size());
        assertEquals("Apple", result.get(0).name());
    }

    @Test
    @DisplayName("Xóa brand thất bại khi brand không tồn tại")
    void test_delete_brand_not_found() {

        when(brandRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> brandService.delete(99L));

        verify(brandRepository, never()).deleteById(anyLong());
    }

    // @Test
    // @DisplayName("Cập nhật brand thất bại khi tên đã tồn tại")
    // void test_update_brand_duplicate_name() {

    //     BrandPostVm vm = new BrandPostVm("Apple", "apple-slug", true);

    //     when(brandRepository.findById(1L))
    //             .thenReturn(Optional.of(brand));

    //     when(brandRepository.findExistedName("Apple", 1L))
    //             .thenReturn(new Brand());

    //     assertThrows(DuplicatedException.class,
    //             () -> brandService.update(vm, 1L));

    //     verify(brandRepository, never()).save(any());
    // }
    @Test
    @DisplayName("Lấy brand theo ids khi không có dữ liệu")
    void test_getBrandsByIds_empty_result() {

        List<Long> ids = List.of(1L);

        when(brandRepository.findAllById(ids)).thenReturn(List.of());

        List<BrandVm> result = brandService.getBrandsByIds(ids);

        assertTrue(result.isEmpty());
    }
    @Test
    @DisplayName("Lấy danh sách brand khi không có dữ liệu")
    void test_retrieve_paginated_brands_empty() {

        Page<Brand> brandPage = new PageImpl<>(List.of());

        when(brandRepository.findAll(any(Pageable.class))).thenReturn(brandPage);

        BrandListGetVm result = brandService.getBrands(0, 10);

        assertNotNull(result);
        assertEquals(0, result.brandContent().size());
    }
    @Test
    @DisplayName("Tạo brand kiểm tra slug mapping")
    void test_create_brand_slug_mapping() {

        BrandPostVm vm = new BrandPostVm("Xiaomi", "xiaomi", true);

        when(brandRepository.findExistedName("Xiaomi", null)).thenReturn(null);
        when(brandRepository.save(any(Brand.class))).thenAnswer(i -> i.getArgument(0));

        Brand result = brandService.create(vm);

        assertEquals("xiaomi", result.getSlug());
    }
}
