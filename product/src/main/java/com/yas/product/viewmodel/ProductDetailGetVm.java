package com.yas.product.viewmodel;

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
        Double price,
        String thumbnailMediaUrl
) {
}
