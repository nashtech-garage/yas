package com.yas.promotion.viewmodel;

import com.yas.promotion.model.Promotion;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class PromotionVmTest {

    @Test
    void testFromModel() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Summer Sale");
        promotion.setSlug("summer-sale");
        promotion.setDiscountPercentage(15L);
        promotion.setDiscountAmount(50000L);
        promotion.setIsActive(true);
        Instant now = Instant.now();
        promotion.setStartDate(now);
        promotion.setEndDate(now.plusSeconds(3600));

        PromotionVm vm = PromotionVm.fromModel(promotion);

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Summer Sale", vm.name());
        assertEquals("summer-sale", vm.slug());
        assertEquals(15L, vm.discountPercentage());
        assertEquals(50000L, vm.discountAmount());
        assertTrue(vm.isActive());
        assertEquals(now, vm.startDate());
        assertEquals(now.plusSeconds(3600), vm.endDate());
        
        assertNull(vm.couponCode());
    }
}