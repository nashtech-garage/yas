package com.yas.promotion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.yas.promotion.PromotionApplication;
import com.yas.promotion.exception.BadRequestException;
import com.yas.promotion.exception.DuplicatedException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.PromotionApply;
import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.utils.Constants;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionListVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = PromotionApplication.class)
class PromotionServiceTest {
    @Autowired
    private PromotionRepository promotionRepository;
    @MockBean
    private ProductService productService;
    @Autowired
    private PromotionService promotionService;

    private Promotion promotion1;
    private Promotion wrongRangeDatePromotion;
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
                .startDate(Instant.now())
                .endDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .applyTo(ApplyTo.BRAND)
                .promotionApplies(
                    List.of(PromotionApply.builder().brandId(1L).build()))
                .build();
        promotion1 = promotionRepository.save(promotion1);

        Promotion promotion2 = Promotion.builder()
                .name("Promotion 2")
                .slug("promotion-2")
                .description("Description 2")
                .couponCode("code2")
                .discountAmount(200L)
                .discountPercentage(20L)
                .isActive(false)
                .startDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .endDate(Instant.now().plus(60, ChronoUnit.DAYS))
                .applyTo(ApplyTo.PRODUCT)
                .promotionApplies(
                    List.of(PromotionApply.builder().productId(1L).build()))
                .build();
        promotionRepository.save(promotion2);

        wrongRangeDatePromotion = Promotion.builder()
            .name("Wrong date")
            .slug("wrong-date")
            .description("Promotion with invalid date range")
            .couponCode("codeWrong")
            .discountAmount(200L)
            .discountPercentage(20L)
            .applyTo(ApplyTo.PRODUCT)
            .isActive(false)
            .startDate(Instant.now().plus(30, ChronoUnit.DAYS))
            .endDate(Instant.now().plus(60, ChronoUnit.DAYS))
            .build();
        wrongRangeDatePromotion = promotionRepository.save(wrongRangeDatePromotion);
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
                .discountType(DiscountType.FIXED)
                .discountAmount(300L)
                .discountPercentage(30L)
                .isActive(true)
                .startDate(Date.from(Instant.now().plus(60, ChronoUnit.DAYS)))
                .endDate(Date.from(Instant.now().plus(90, ChronoUnit.DAYS)))
                .applyTo(ApplyTo.PRODUCT)
                .productIds(List.of(1L, 2L, 3L))
                .build();

        PromotionDetailVm result = promotionService.createPromotion(promotionPostVm);
        assertEquals(promotionPostVm.getSlug(), result.slug());
        assertEquals(promotionPostVm.getName(), result.name());
        assertEquals(true, result.isActive());
    }

    @Test
    void createPromotion_WhenExistedSlug_ThenDuplicatedExceptionThrown() {
        promotionPostVm = PromotionPostVm.builder()
                .slug(promotion1.getSlug())
                .applyTo(ApplyTo.PRODUCT)
                .name("12345")
                .couponCode("cp-12345")
                .productIds(List.of(1L, 2L, 3L))
                .discountType(DiscountType.FIXED)
                .discountAmount(300L)
                .discountPercentage(30L)
                .build();
        assertThrows(DuplicatedException.class, () -> promotionService.createPromotion(promotionPostVm),
                String.format(Constants.ErrorCode.SLUG_ALREADY_EXITED, promotionPostVm.getSlug()));
    }

    @Test
    void createPromotion_WhenEndDateBeforeStartDate_ThenDateRangeExceptionThrown() {
        promotionPostVm = PromotionPostVm.builder()
            .applyTo(ApplyTo.PRODUCT)
            .name("12345")
            .couponCode("cp-12345")
            .productIds(List.of(1L, 2L, 3L))
            .endDate(Date.from(Instant.now().minus(2, ChronoUnit.DAYS)))
            .startDate(Date.from(Instant.now()))
            .build();

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            promotionService.createPromotion(promotionPostVm)
        );
        assertEquals(String.format(Constants.ErrorCode.DATE_RANGE_INVALID), exception.getMessage());
    }

    @Test
    void getPromotionList_ThenSuccess() {
        PromotionListVm result = promotionService.getPromotions(0, 5,
                "Promotion", "code",
                Instant.now().minus(120, ChronoUnit.DAYS), Instant.now().plus(120, ChronoUnit.DAYS));
        assertEquals(2, result.promotionDetailVmList().size());
        PromotionDetailVm promotionDetailVm = result.promotionDetailVmList().getFirst();
        assertEquals("promotion-1", promotionDetailVm.slug());
    }
}
