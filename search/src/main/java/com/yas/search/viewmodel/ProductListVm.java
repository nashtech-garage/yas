package com.yas.search.viewmodel;

import com.yas.search.document.Product;

import java.time.ZonedDateTime;

public record ProductListVm(Long id,
                            String name,
                            String slug,
                            Boolean isAllowedToOrder,
                            Boolean isPublished,
                            Boolean isFeatured,
                            Boolean isVisibleIndividually,
                            ZonedDateTime createdOn) {
    public static ProductListVm fromModel(Product product) {
        return new ProductListVm(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getIsAllowedToOrder(),
                product.getIsPublished(),
                product.getIsFeatured(),
                product.getIsVisibleIndividually(),
                product.getCreatedOn()
        );
    }
}
