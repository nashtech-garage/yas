package com.yas.promotion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.yas.promotion.PromotionApplication;
import com.yas.promotion.exception.BadRequestException;
import com.yas.promotion.exception.DuplicatedException;
import com.yas.promotion.exception.NotFoundException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.PromotionUsage;
import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.repository.PromotionUsageRepository;
import com.yas.promotion.utils.Constants;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionListVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import com.yas.promotion.viewmodel.PromotionPutVm;
import config.IntegrationTestConfiguration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = PromotionApplication.class)
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PromotionServiceIT {

    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PromotionService promotionService;
    @MockBean
    private ProductService productService;
    @Autowired
    private PromotionUsageRepository promotionUsageRepository;

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
                .endDate(Instant.now().plusSeconds(2592000))
                .applyTo(ApplyTo.CATEGORY)
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
                .applyTo(ApplyTo.CATEGORY)
                .startDate(Instant.now().minusSeconds(2592000))
                .endDate(Instant.now().plusSeconds(5184000))
                .build();
        promotionRepository.save(promotion2);

        wrongRangeDatePromotion = Promotion.builder()
            .name("Wrong date")
            .slug("wrong-date")
            .description("Promotion with invalid date range")
            .couponCode("codeWrong")
            .discountAmount(200L)
            .discountPercentage(20L)
            .isActive(false)
            .startDate(Instant.now().minusSeconds(2592000))
            .endDate(Instant.now().minusSeconds(5184000))
            .build();
        wrongRangeDatePromotion = promotionRepository.save(wrongRangeDatePromotion);
    }

    @AfterEach
    void tearDown() {
        promotionUsageRepository.deleteAll();
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
                .startDate(Date.from(Instant.now().plusSeconds(5184000)))
                .endDate(Date.from(Instant.now().plusSeconds(7776000)))
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
                .build();
        assertThrows(DuplicatedException.class, () -> promotionService.createPromotion(promotionPostVm),
                String.format(Constants.ErrorCode.SLUG_ALREADY_EXITED, promotionPostVm.getSlug()));
    }

    @Test
    void createPromotion_WhenEndDateBeforeStartDate_ThenDateRangeExceptionThrown() {
        promotionPostVm = PromotionPostVm.builder()
            .endDate(Date.from(wrongRangeDatePromotion.getEndDate()))
            .startDate(Date.from(wrongRangeDatePromotion.getStartDate()))
            .build();

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            promotionService.createPromotion(promotionPostVm)
        );
        assertEquals(String.format(Constants.ErrorCode.DATE_RANGE_INVALID), exception.getMessage());
    }

    @Test
    void getPromotionLists_existsPromotions_ThenPromotionListVm() {

        PromotionListVm result =  getPromotionListVm();
        assertEquals(2, result.promotionDetailVmList().size());
        PromotionDetailVm promotionDetailVm = result.promotionDetailVmList().getFirst();
        assertEquals("promotion-1", promotionDetailVm.slug());
    }

    @Test
    void testUpdatePromotion_whenNormalCase_updatePromotion() {

        when(productService.getCategoryByIds(anyList())).thenReturn(List.of());
        PromotionListVm result =  getPromotionListVm();
        PromotionDetailVm promotionDetailVm = result.promotionDetailVmList().getFirst();

        PromotionPutVm promotionPutVm = PromotionPutVm.builder()
            .id(promotionDetailVm.id())
            .name("Promotion 1")
            .slug("promotion-1")
            .description("Description 123")
            .couponCode("code1")
            .discountAmount(100L)
            .discountPercentage(20L)
            .isActive(true)
            .startDate(Date.from(Instant.now().plusSeconds(5184000)))
            .endDate(Date.from(Instant.now().plusSeconds(7776000)))
            .applyTo(ApplyTo.PRODUCT)
            .productIds(List.of(1L, 2L, 3L))
            .build();

        promotionService.updatePromotion(promotionPutVm);

        PromotionListVm actual =  getPromotionListVm();
        assertEquals(2, result.promotionDetailVmList().size());

        PromotionDetailVm promotionDetailVmActual = actual.promotionDetailVmList().getFirst();
        assertEquals("Description 123", promotionDetailVmActual.description());
    }

    @Test
    void testUpdatePromotion_whenPromotionIsEmpty_throwNotFoundException() {
        PromotionPutVm promotionPutVm = PromotionPutVm.builder()
            .id(433L)
            .name("Promotion 1")
            .endDate(Date.from(Instant.now().plusSeconds(7776000)))
            .applyTo(ApplyTo.PRODUCT)
            .productIds(List.of(1L, 2L, 3L))
            .build();

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> promotionService.updatePromotion(promotionPutVm));
        assertEquals("Promotion 433 is not found", exception.getMessage());

    }

    @Test
    void testDeletePromotion_normalCase_deletePromotion() {

        PromotionListVm result =  getPromotionListVm();
        PromotionDetailVm promotionDetailVm = result.promotionDetailVmList().getFirst();

        promotionService.deletePromotion(promotionDetailVm.id());

        PromotionListVm actual =  getPromotionListVm();
        assertEquals(1, actual.promotionDetailVmList().size());
        assertNotEquals(promotionDetailVm.id(), actual.promotionDetailVmList().getFirst().id());

    }

    @Test
    void testDeletePromotion_existsPromotionUsageByPromotionId_throwBadRequestException() {

        PromotionListVm result =  getPromotionListVm();
        PromotionDetailVm promotionDetailVm = result.promotionDetailVmList().getFirst();
        Promotion promotion = new Promotion();
        promotion.setId(promotionDetailVm.id());

        PromotionUsage promotionUsage = PromotionUsage.builder()
            .promotion(promotion)
            .productId(1L)
            .userId("user-id")
            .orderId(1L)
            .build();

        promotionUsageRepository.save(promotionUsage);
        Long promotionId  = promotionDetailVm.id();

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> promotionService.deletePromotion(promotionId));
        assertEquals("Can't delete promotion " + promotionId + " because it is in use", exception.getMessage());
    }

    private PromotionListVm getPromotionListVm() {
        return promotionService.getPromotions(0, 5,
            "Promotion", "code",
            Instant.now().minusSeconds(10368000), Instant.now().plusSeconds(10368000));
    }

}
