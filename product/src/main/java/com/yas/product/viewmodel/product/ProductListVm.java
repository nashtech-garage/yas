package com.yas.product.viewmodel.product;
import com.yas.product.model.Product;

public record ProductListVm(Long id,
                            String name,
                            String slug,
                            Boolean isAllowedToOrder,
                            Boolean isPublished,
                            Boolean isFeatured,
                            Boolean isVisibleIndividually) {
    public static ProductListVm fromModel(Product product) {
        return new ProductListVm(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getIsAllowedToOrder(),
                product.getIsPublished(),
                product.getIsFeatured(),
                product.getIsVisibleIndividually()

        );
    }
}
