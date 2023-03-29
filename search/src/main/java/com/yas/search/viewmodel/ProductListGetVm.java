package com.yas.search.viewmodel;

import java.util.List;

public record ProductListGetVm(
        List<ProductListVm> productContent,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {
}
