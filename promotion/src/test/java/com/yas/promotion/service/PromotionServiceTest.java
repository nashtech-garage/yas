package com.yas.promotion.service;

import com.yas.promotion.exception.DuplicatedException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.utils.Constants;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionListVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class PromotionServiceTest {
    private PromotionRepository promotionRepository;

    private PromotionService promotionService;

    private Promotion promotion1;
    private Promotion promotion2;
    private PromotionPostVm promotionPostVm;
    private List<Promotion> promotionList;

    @BeforeEach
    void setUp() {
        promotionRepository = mock(PromotionRepository.class);
        promotionService = new PromotionService(promotionRepository);

        promotion1 = Promotion.builder()
                .id(1L)
                .name("Promotion 1")
                .slug("promotion-1")
                .description("Description 1")
                .couponCode("code1")
                .discountAmount(100L)
                .discountPercentage(10L)
                .isActive(true)
                .startDate(ZonedDateTime.now())
                .endDate(ZonedDateTime.now().plusDays(30))
                .build();

        promotion2 = Promotion.builder()
                .id(2L)
                .name("Promotion 2")
                .slug("promotion-2")
                .description("Description 2")
                .couponCode("code2")
                .discountAmount(200L)
                .discountPercentage(20L)
                .isActive(false)
                .startDate(ZonedDateTime.now().minusDays(30))
                .endDate(ZonedDateTime.now().plusDays(60))
                .build();

        promotionList = Arrays.asList(promotion1, promotion2);

        promotionPostVm = PromotionPostVm.builder()
                .name("Promotion 3")
                .slug("promotion-3")
                .description("Description 3")
                .couponCode("code3")
                .discountAmount(300L)
                .discountPercentage(30L)
                .isActive(true)
                .startDate(ZonedDateTime.now().plusDays(60))
                .endDate(ZonedDateTime.now().plusDays(90))
                .build();

    }

    @Test
    void createPromotion_ThenSuccess() {
        Promotion promotion = Promotion.builder()
                .name(promotionPostVm.name())
                .slug(promotionPostVm.slug())
                .description(promotionPostVm.description())
                .couponCode(promotionPostVm.couponCode())
                .discountPercentage(promotionPostVm.discountPercentage())
                .discountAmount(promotionPostVm.discountAmount())
                .isActive(true)
                .startDate(promotionPostVm.startDate())
                .endDate(promotionPostVm.endDate())
                .build();

        when(promotionRepository.findBySlugAndIsActiveTrue(promotion.getSlug())).thenReturn(Optional.empty());
        when(promotionRepository.saveAndFlush(any(Promotion.class))).thenReturn(promotion);

        PromotionDetailVm result = promotionService.createPromotion(promotionPostVm);

        assertEquals(promotion.getSlug(), result.slug());
        assertEquals(true, result.isActive());
    }

    @Test
    void createPromotion_WhenExistedSlug_ThenDuplicatedExceptionThrown() {
        String slug = "test-promotion";
        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
                .slug(slug)
                .build();

        when(promotionRepository.findBySlugAndIsActiveTrue(slug)).thenReturn(Optional.of(new Promotion()));
        assertThrows(DuplicatedException.class, () -> promotionService.createPromotion(promotionPostVm),
                String.format(Constants.ERROR_CODE.SLUG_ALREADY_EXITED, slug));

    }

    @Test
    void getPromotionList_ThenSuccess() {
        Page<Promotion> promotionPage = new PageImpl<>(promotionList);
        when(promotionRepository.findPromotions(anyString(), anyString(), any(ZonedDateTime.class), any(ZonedDateTime.class), any(Pageable.class)))
                .thenReturn(promotionPage);

        // invoke the service method
        PromotionListVm result = promotionService.getPromotions(0, 5, "Promotion", "code", ZonedDateTime.now(), ZonedDateTime.now().plusDays(30));

        assertEquals(2, result.promotionDetailVmList().size());

        PromotionDetailVm promotionDetailVm = result.promotionDetailVmList().get(0);
        assertEquals("promotion-1", promotionDetailVm.slug());
    }
}
