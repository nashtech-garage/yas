package com.yas.promotion.service;

import com.yas.promotion.exception.DuplicatedException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.utils.Constants;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class PromotionServiceTest {
    private PromotionRepository promotionRepository;

    private PromotionService promotionService;

    @BeforeEach
    void setUp() {
        promotionRepository = mock(PromotionRepository.class);
        promotionService = new PromotionService(promotionRepository);
    }

    @Test
    void createPromotion_ThenSuccess() {
        String name = "Test Promotion";
        String slug = "test-promotion";
        String description = "This is a test promotion.";
        String couponCode = "TEST123";
        Long value = 10L;
        Long amount = 100L;
        ZonedDateTime startDate = ZonedDateTime.now();
        ZonedDateTime endDate = ZonedDateTime.now().plusDays(7);

        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .couponCode(couponCode)
                .value(value)
                .amount(amount)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        Promotion promotion = Promotion.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .couponCode(couponCode)
                .value(value)
                .amount(amount)
                .isActive(true)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        when(promotionRepository.findBySlugAndIsActiveTrue(slug)).thenReturn(Optional.empty());
        when(promotionRepository.saveAndFlush(any(Promotion.class))).thenReturn(promotion);

        PromotionDetailVm result = promotionService.create(promotionPostVm);

        assertEquals(slug, result.slug());
        assertEquals(true, result.isActive());
    }

    @Test
    void createPromotion_WhenExistedSlug_ThenDuplicatedExceptionThrown() {
        String slug = "test-promotion";
        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
                .slug(slug)
                .build();

        when(promotionRepository.findBySlugAndIsActiveTrue(slug)).thenReturn(Optional.of(new Promotion()));
        assertThrows(DuplicatedException.class, () -> promotionService.create(promotionPostVm),
                String.format(Constants.ERROR_CODE.SLUG_ALREADY_EXITED, slug));

    }
}
