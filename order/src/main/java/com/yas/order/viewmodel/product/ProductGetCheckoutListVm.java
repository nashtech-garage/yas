package com.yas.order.viewmodel.product;

import java.util.List;

public record ProductGetCheckoutListVm(
        List<ProductCheckoutListVm> productCheckoutListVms,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {
}
