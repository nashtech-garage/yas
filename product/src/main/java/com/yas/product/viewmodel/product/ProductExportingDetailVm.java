package com.yas.product.viewmodel.product;

import java.util.List;

public record ProductExportingDetailVm(
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
        String brandName,
        String metaTitle,
        String metaKeyword,
        String metaDescription
) {}
