package com.yas.product.viewmodel.product;

import com.yas.product.model.Product;
import java.time.ZonedDateTime;
import java.util.Objects;

public record ProductListVm(Long id,
                            String name,
                            String slug,
                            Boolean isAllowedToOrder,
                            Boolean isPublished,
                            Boolean isFeatured,
                            Boolean isVisibleIndividually,
                            Double price,
                            ZonedDateTime createdOn,
                            Long taxClassId,
                            Long parentId) {
    public static ProductListVm fromModel(Product product) {
        return new ProductListVm(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.isAllowedToOrder(),
                product.isPublished(),
                product.isFeatured(),
                product.isVisibleIndividually(),
                product.getPrice(),
                product.getCreatedOn(),
                product.getTaxClassId(),
                Objects.isNull(product.getParent()) ? null : product.getParent().getId()
        );
    }
}
