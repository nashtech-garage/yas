package com.yas.promotion.viewmodel;

import com.yas.promotion.model.Promotion;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromotionVmTest {

    @Test
    void testFromModel() {
        Promotion promotion = Promotion.builder()
                .id(1L)
                .name("Summer Sale")
                .slug("summer-sale")
                .discountPercentage(20L)
                .discountAmount(0L)
                .isActive(true)
                .startDate(Instant.now())
                .endDate(Instant.now().plusSeconds(86400))
                .build();

        PromotionVm vm = PromotionVm.fromModel(promotion);

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Summer Sale", vm.name());
        assertEquals("summer-sale", vm.slug());
        assertEquals(20L, vm.discountPercentage());
        assertEquals(0L, vm.discountAmount());
        assertTrue(vm.isActive());
    }
}
