package com.yas.order.viewmodel;

import com.yas.order.model.Order;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemVm(
        Long id,
        Long productId,
        int quantity,
        BigDecimal price,
        String note,
        Long orderId) {


}