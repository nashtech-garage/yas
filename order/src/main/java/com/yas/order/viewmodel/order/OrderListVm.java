package com.yas.order.viewmodel.order;

import java.util.List;

public record OrderListVm(
        List<OrderVm> orderList,
        long totalElements,
        int totalPages
){

}
