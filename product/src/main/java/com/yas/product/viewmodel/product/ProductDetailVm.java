package com.yas.product.viewmodel.product;

import com.yas.product.model.Category;

import java.util.List;

public record ProductDetailVm(
        long id,
        String name,
        String shortDescription,
        String description,
        String specification,
        String sku,
        String gtin,
        String slug,
        Boolean isAllowedToOrder,
        Boolean isPublished,
        Boolean isFeatured,
        Boolean isVisible,
        Double price,
        Long brandId,
        List<Category> categories,
        String metaTitle,
        String metaKeyword,
        String metaDescription,
        String thumbnailMediaUrl,
        List<String> productImageMediaUrls) {
}
