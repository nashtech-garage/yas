package com.yas.promotion.service;

import com.yas.promotion.PromotionApplication;
import com.yas.promotion.exception.DuplicatedException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.utils.Constants;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionListVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest(classes = PromotionApplication.class)
class PromotionServiceTest {
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PromotionService promotionService;

    private Promotion promotion1;
    private Promotion promotion2;
    private PromotionPostVm promotionPostVm;

    @BeforeEach
    void setUp() {
        promotion1 = Promotion.builder()
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
        promotion1 = promotionRepository.save(promotion1);

        promotion2 = Promotion.builder()
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
        promotion2 = promotionRepository.save(promotion2);
    }

    @AfterEach
    void tearDown() {
        promotionRepository.deleteAll();
    }

    @Test
    void createPromotion_ThenSuccess() {
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

        PromotionDetailVm result = promotionService.createPromotion(promotionPostVm);
        assertEquals(promotionPostVm.slug(), result.slug());
        assertEquals(promotionPostVm.name(), result.name());
        assertEquals(true, result.isActive());
    }

    @Test
    void createPromotion_WhenExistedSlug_ThenDuplicatedExceptionThrown() {
        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
                .slug(promotion1.getSlug())
                .build();
        assertThrows(DuplicatedException.class, () -> promotionService.createPromotion(promotionPostVm),
                String.format(Constants.ERROR_CODE.SLUG_ALREADY_EXITED, promotionPostVm.slug()));
    }

    @Test
    void getPromotionList_ThenSuccess() {
        PromotionListVm result = promotionService.getPromotions(0, 5,
                "Promotion", "code",
                ZonedDateTime.now().minusDays(120), ZonedDateTime.now().plusDays(120));
        assertEquals(2, result.promotionDetailVmList().size());
        PromotionDetailVm promotionDetailVm = result.promotionDetailVmList().get(0);
        assertEquals("promotion-1", promotionDetailVm.slug());
    }
}
