package com.yas.search.viewmodel;

import com.yas.search.model.Product;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record ProductGetVm(Long id,
                           String name,
                           String slug,
                           Long thumbnailId,
                           Double price,
                           Double rating,
                           Boolean isAllowedToOrder,
                           Boolean isPublished,
                           Boolean isFeatured,
                           Boolean isVisibleIndividually,
                           ZonedDateTime createdOn) {
    public static ProductGetVm fromModel(Product product) {
        return new ProductGetVm(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getThumbnailMediaId(),
                product.getPrice(),
                product.getRating(),
                product.getIsAllowedToOrder(),
                product.getIsPublished(),
                product.getIsFeatured(),
                product.getIsVisibleIndividually(),
                product.getCreatedOn()
        );
    }
}
