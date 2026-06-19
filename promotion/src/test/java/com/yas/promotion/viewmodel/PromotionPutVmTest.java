package com.yas.promotion.viewmodel;

import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.PromotionApply;
import com.yas.promotion.model.enumeration.ApplyTo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PromotionPutVmTest {

    @Test
    void testCreatePromotionAppliesForProduct() {
        PromotionPutVm putVm = new PromotionPutVm();
        putVm.setProductIds(List.of(10L, 20L));

        Promotion promotion = Promotion.builder()
                .applyTo(ApplyTo.PRODUCT)
                .build();

        List<PromotionApply> applies = PromotionPutVm.createPromotionApplies(putVm, promotion);

        assertNotNull(applies);
        assertEquals(2, applies.size());
        assertEquals(10L, applies.get(0).getProductId());
        assertEquals(20L, applies.get(1).getProductId());
    }

    @Test
    void testCreatePromotionAppliesForCategory() {
        PromotionPutVm putVm = new PromotionPutVm();
        putVm.setCategoryIds(List.of(30L));

        Promotion promotion = Promotion.builder()
                .applyTo(ApplyTo.CATEGORY)
                .build();

        List<PromotionApply> applies = PromotionPutVm.createPromotionApplies(putVm, promotion);

        assertNotNull(applies);
        assertEquals(1, applies.size());
        assertEquals(30L, applies.get(0).getCategoryId());
    }

    @Test
    void testCreatePromotionAppliesForBrand() {
        PromotionPutVm putVm = new PromotionPutVm();
        putVm.setBrandIds(List.of(40L, 50L, 60L));

        Promotion promotion = Promotion.builder()
                .applyTo(ApplyTo.BRAND)
                .build();

        List<PromotionApply> applies = PromotionPutVm.createPromotionApplies(putVm, promotion);

        assertNotNull(applies);
        assertEquals(3, applies.size());
        assertEquals(40L, applies.get(0).getBrandId());
    }
}
