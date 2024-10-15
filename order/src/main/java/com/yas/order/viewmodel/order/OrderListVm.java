package com.yas.order.viewmodel.order;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record OrderListVm(
        List<OrderBriefVm> orderList,
        long totalElements,
        int totalPages
) {
}
