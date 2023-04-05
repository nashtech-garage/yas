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
        String productName,
        int quantity,
        BigDecimal productPrice,
        String note,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal taxPercent,
        Long orderId) {


}