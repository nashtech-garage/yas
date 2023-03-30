package com.yas.order.viewmodel;

import java.math.BigDecimal;

public record OrderItemPostVm(Long productId, int quantity, BigDecimal price, String note) {
}
