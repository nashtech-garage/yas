package com.yas.order.viewmodel;

import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;

import java.util.List;

public record OrderListGetVm(
        List<OrderMapper> orderContent,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast) {
}
