package com.yas.search.viewmodel;

import java.util.List;
import java.util.Map;

public record ProductListGetVm(
        List<ProductGetVm> product,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLast,
        Map<String, Map<String, Long>> aggregations
) {
}
