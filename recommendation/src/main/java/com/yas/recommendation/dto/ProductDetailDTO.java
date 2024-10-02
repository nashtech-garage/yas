package com.yas.recommendation.dto;

import java.util.List;

public record ProductDetailDTO(
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
        Boolean stockTrackingEnabled,
        Double price,
        Long brandId,
        List<CategoryDTO> categories,
        String metaTitle,
        String metaKeyword,
        String metaDescription,
        Long taxClassId
) {
}
