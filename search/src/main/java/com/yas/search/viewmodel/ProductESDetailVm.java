package com.yas.search.viewmodel;


import java.util.List;

public record ProductESDetailVm(
        Long id,
        String name,
        String slug,
        Double price,
        Boolean isPublished,
        Boolean isVisibleIndividually,
        Boolean isAllowedToOrder,
        Boolean isFeatured,
        Long thumbnailMediaId,
        String brand,
        List<String> categories,
        List<String> attributes) {
}
