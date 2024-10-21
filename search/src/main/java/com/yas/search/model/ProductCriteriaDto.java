package com.yas.search.model;

import com.yas.search.constant.enums.SortType;
import lombok.Builder;

@Builder(toBuilder = true)
public record ProductCriteriaDto(String keyword,
                                 Integer page,
                                 Integer size,
                                 String brand,
                                 String category,
                                 String attribute,
                                 Double minPrice,
                                 Double maxPrice,
                                 Double minRating,
                                 Double maxRating,
                                 SortType sortType) {
}
