package com.yas.product.viewmodel.product;

import java.util.List;

public record ProductEsDetailVm(
        Long id,
        String name,
        String slug,
        Double price,
        boolean isPublished,
        boolean isVisibleIndividually,
        boolean isAllowedToOrder,
        boolean isFeatured,
        Long thumbnailMediaId,
        String brand,
        List<String> categories,
        List<String> attributes) {
}
