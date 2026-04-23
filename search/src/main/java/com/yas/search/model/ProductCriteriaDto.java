package com.yas.search.model;

import com.yas.search.constant.enums.SortType;

public record ProductCriteriaDto(String keyword,
                                 Integer page,
                                 Integer size,
                                 String brand,
                                 String category,
                                 String attribute,
                                 Double minPrice,
                                 Double maxPrice,
                                 SortType sortType) {
}
