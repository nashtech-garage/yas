package com.yas.product.viewmodel;

import com.yas.product.model.Brand;

public record BrandVm(Long id, String name, String slug) {
    public static BrandVm fromModel(Brand brand) {
        return new BrandVm(brand.getId(), brand.getName(), brand.getSlug());
    }
}
