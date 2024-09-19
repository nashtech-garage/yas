package com.yas.product.service;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.repository.BrandRepository;
import com.yas.product.viewmodel.brand.BrandListGetVm;
import com.yas.product.viewmodel.brand.BrandPostVm;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    // Retrieve a paginated list of brands successfully
    @Test
    void test_retrieve_paginated_brands_successfully() {
        List<Brand> brands = List.of(new Brand(), new Brand());
        Page<Brand> brandPage = new PageImpl<>(brands);
        when(brandRepository.findAll(any(Pageable.class))).thenReturn(brandPage);

        BrandListGetVm result = brandService.getBrands(0, 2);

        assertEquals(2, result.brandContent().size());
        assertEquals(0, result.pageNo());
        assertEquals(2, result.pageSize());
    }

    // Create a new brand when valid data is provided
    @Test
    void test_create_brand_successfully() {
        BrandPostVm brandPostVm = new BrandPostVm("BrandName", "brand-slug", true);
        Brand brand = brandPostVm.toModel();
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        Brand result = brandService.create(brandPostVm);

        assertEquals("BrandName", result.getName());
        assertEquals("brand-slug", result.getSlug());
    }

    // Update an existing brand when valid data is provided
    @Test
    void test_update_brand_successfully() {
        BrandPostVm brandPostVm = new BrandPostVm("UpdatedName", "updated-slug", true);
        Brand existingBrand = new Brand();
        existingBrand.setId(1L);
        when(brandRepository.findById(1L)).thenReturn(Optional.of(existingBrand));
        when(brandRepository.save(any(Brand.class))).thenReturn(existingBrand);

        Brand result = brandService.update(brandPostVm, 1L);

        assertEquals("UpdatedName", result.getName());
        assertEquals("updated-slug", result.getSlug());
    }

    // Attempt to create a brand with a name that already exists
    @Test
    void test_create_brand_with_existing_name() {
        BrandPostVm brandPostVm = new BrandPostVm("ExistingName", "existing-slug", true);
        when(brandRepository.findExistedName("ExistingName", null)).thenReturn(new Brand());

        Assertions.assertThrows(DuplicatedException.class, () -> {
            brandService.create(brandPostVm);
        });
    }

    // Attempt to update a brand with a name that already exists
    @Test
    void test_update_brand_with_existing_name() {
        BrandPostVm brandPostVm = new BrandPostVm("ExistingName", "existing-slug", true);
        when(brandRepository.findExistedName("ExistingName", 1L)).thenReturn(new Brand());

        Assertions.assertThrows(DuplicatedException.class, () -> {
            brandService.update(brandPostVm, 1L);
        });
    }

    // Attempt to update a brand that does not exist
    @Test
    void test_update_nonexistent_brand() {
        BrandPostVm brandPostVm = new BrandPostVm("NonExistentName", "nonexistent-slug", true);
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            brandService.update(brandPostVm, 1L);
        });
    }
}