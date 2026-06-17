package com.yas.promotion.viewmodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PromotionUsageVmTest {

    @Test
    void testPromotionUsageVm() {
        PromotionUsageVm vm = new PromotionUsageVm("CODE123", 1L, "user123", 100L);

        assertEquals("CODE123", vm.promotionCode());
        assertEquals(1L, vm.productId());
        assertEquals("user123", vm.userId());
        assertEquals(100L, vm.orderId());
    }
}
