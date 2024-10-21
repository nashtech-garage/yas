package com.yas.search.viewmodel;


import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record ProductEsDetailVm(
        Long id,
        String name,
        String slug,
        Double price,
        Double rating,
        boolean isPublished,
        boolean isVisibleIndividually,
        boolean isAllowedToOrder,
        boolean isFeatured,
        Long thumbnailMediaId,
        String brand,
        List<String> categories,
        List<String> attributes) {
}
