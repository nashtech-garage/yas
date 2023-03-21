package com.yas.product.viewmodel.product;

import com.yas.product.viewmodel.productattribute.ProductAttributeGroupGetVm;

import java.util.List;

public record ProductDetailGetVm(
        long id,
        String name,
        String brandName,
        List<String> productCategories,
        List<ProductAttributeGroupGetVm> productAttributeGroups,
        String shortDescription,
        String description,
        String specification,
        Boolean isAllowedToOrder,
        Boolean isPublished,
        Boolean isFeatured,
        Boolean hasOptions,
        Double price,
        String thumbnailMediaUrl,
        List<String> productImageMediaUrls
) {
}
