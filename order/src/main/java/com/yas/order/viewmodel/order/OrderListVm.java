package com.yas.order.viewmodel.order;

import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record OrderListVm(
    List<OrderBriefVm> orderList,
    long totalElements,
    int totalPages
) {
}
