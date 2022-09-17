package com.yas.product.viewmodel;

import java.util.List;

import javax.validation.constraints.NotEmpty;

public record ProductPostVm(
        @NotEmpty String name,
        @NotEmpty String slug,
        Long brandId,
        List<Long> categoryIds,
        String shortDescription,
        String description,
        String specification,
        String sku,
        String gtin,
        Double price,
        Boolean isAllowedToOrder,
        Boolean isPublished,
        Boolean isFeatured,
        String metaKeyword,
        String metaDescription) {
}
