package com.yas.promotion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.yas.promotion.PromotionApplication;
import com.yas.promotion.exception.BadRequestException;
import com.yas.promotion.exception.DuplicatedException;
import com.yas.promotion.exception.NotFoundException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.PromotionApply;
import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.utils.Constants;
import com.yas.promotion.viewmodel.ProductVm;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionListVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import com.yas.promotion.viewmodel.PromotionVerifyVm;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
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
                .discountType(DiscountType.PERCENTAGE)
                .discountAmount(100L)
                .discountPercentage(10L)
                .isActive(true)
                .startDate(Instant.now())
                .endDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .applyTo(ApplyTo.BRAND)
                .minimumOrderPurchaseAmount(0L)
                .build();

        var promotionApply = PromotionApply.builder()
            .promotion(promotion1)
            .brandId(1L).build();
        promotion1.setPromotionApplies(List.of(promotionApply));

        promotion1 = promotionRepository.save(promotion1);

        Promotion promotion2 = Promotion.builder()
                .name("Promotion 2")
                .slug("promotion-2")
                .description("Description 2")
                .couponCode("code2")
                .discountAmount(200L)
                .discountPercentage(20L)
                .isActive(true)
                .startDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .endDate(Instant.now().plus(60, ChronoUnit.DAYS))
                .applyTo(ApplyTo.PRODUCT)
                .discountType(DiscountType.PERCENTAGE)
                .minimumOrderPurchaseAmount(100L)
                .build();

        var promotionApply2 = PromotionApply.builder()
            .promotion(promotion2)
            .productId(1L).build();
        promotion2.setPromotionApplies(List.of(promotionApply2));

        promotionRepository.save(promotion2);

        Promotion promotion3 = Promotion.builder()
            .name("Promotion 3")
            .slug("promotion-3")
            .description("Description 3")
            .couponCode("code3")
            .discountAmount(200L)
            .discountPercentage(20L)
            .isActive(true)
            .startDate(Instant.now().plus(30, ChronoUnit.DAYS))
            .endDate(Instant.now().plus(60, ChronoUnit.DAYS))
            .applyTo(ApplyTo.CATEGORY)
            .discountType(DiscountType.FIXED)
            .minimumOrderPurchaseAmount(100L)
            .build();

        var promotionApply3 = PromotionApply.builder()
            .promotion(promotion2)
            .productId(1L).build();
        promotion3.setPromotionApplies(List.of(promotionApply3));

        promotionRepository.save(promotion3);

        wrongRangeDatePromotion = Promotion.builder()
            .name("Wrong date")
            .slug("wrong-date")
            .description("Promotion with invalid date range")
            .couponCode("codeWrong")
            .discountAmount(200L)
            .discountPercentage(20L)
            .applyTo(ApplyTo.PRODUCT)
            .usageType(UsageType.LIMITED)
            .minimumOrderPurchaseAmount(100L)
            .usageCount(10)
            .usageLimit(10)
            .isActive(true)
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
                .name("Promotion 4")
                .slug("promotion-4")
                .description("Description 4")
                .couponCode("code4")
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
    void createPromotion_WhenCouponCodeAlreadyExisted_ThenDuplicatedExceptionThrown() {
        promotionPostVm = PromotionPostVm.builder()
                .couponCode("code3")
                .build();
        DuplicatedException duplicatedException = assertThrows(DuplicatedException.class,
            () -> promotionService.createPromotion(promotionPostVm));
        assertEquals("The coupon code code3 is already existed", duplicatedException.getMessage());
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
        assertEquals(3, result.promotionDetailVmList().size());
        PromotionDetailVm promotionDetailVm = result.promotionDetailVmList().getFirst();
        assertEquals("promotion-1", promotionDetailVm.slug());
    }

    @Test
    void getPromotion_ThenSuccess() {
        PromotionDetailVm result = promotionService.getPromotion(promotion1.getId());
        assertEquals("promotion-1", result.slug());
        assertEquals("Promotion 1", result.name());
        assertEquals("code1", result.couponCode());
        assertEquals(DiscountType.PERCENTAGE, result.discountType());
        assertEquals(10L, result.discountPercentage().longValue());
        assertEquals(100L, result.discountAmount().longValue());
        assertEquals(true, result.isActive());
        assertEquals(ApplyTo.BRAND, result.applyTo());
    }

    @Test
    void getPromotion_WhenNotExist_ThenNotFoundExceptionThrown() {
        var exception = assertThrows(NotFoundException.class, () -> promotionService.getPromotion(0L));
        assertEquals(String.format(Constants.ErrorCode.PROMOTION_NOT_FOUND, 0L), exception.getMessage());
    }

    @Test
    void testVerifyPromotion_PromotionNotFound() {
        var promotionVerifyVm = new PromotionVerifyVm("COUPON123", 150L, List.of(1L, 2L, 3L));

        // Expect a NotFoundException to be thrown
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Promotion COUPON123 is not found", exception.getMessage());
    }

    @Test
    void testVerifyPromotion_ExhaustedUsageQuantity() {
        // Mock the repository to return the promotion
        var promotionVerifyVm = new PromotionVerifyVm("codeWrong", 130L, List.of(1L));

        // Expect a BadRequestException due to exhausted usage quantity
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Exhausted usage quantity", exception.getMessage());
    }

    @Test
    void testVerifyPromotion_InvalidOrderPrice() {
        var promotionVerifyVm = new PromotionVerifyVm("code2", 10L, List.of(1L));
        // Expect a BadRequestException due to invalid order price
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Invalid minimum order purchase amount", exception.getMessage());
    }

    @Test
    void testVerifyPromotion_ProductNotFound() {
        var promotionVerifyVm = new PromotionVerifyVm("code2", 1000L, List.of(1L,2L,3L));
        Mockito.when(productService.getProductByCategoryIds(ArgumentMatchers.anyList())).thenReturn(List.of());

        // Expect a NotFoundException due to no products found for promotion
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Not found product to apply promotion", exception.getMessage());
    }

    @Test
    void verifyPromotion_applyToBrand_ThenSuccess() {
        PromotionVerifyVm promotionVerifyData = new PromotionVerifyVm(
            "code1",
            1000000L,
            List.of(1L, 2L, 3L)
        );
        Mockito.when(productService.getProductByBrandIds(ArgumentMatchers.anyList()))
            .thenReturn(createProductVms());
        var result = promotionService.verifyPromotion(promotionVerifyData);

        assertEquals(true, result.isValid());
        assertEquals(1L, result.productId());
        assertEquals(DiscountType.PERCENTAGE, result.discountType());
        assertEquals(10L, result.discountValue().longValue());
    }

    @Test
    void verifyPromotion_applyToProduct_ThenSuccess() {
        PromotionVerifyVm promotionVerifyData = new PromotionVerifyVm(
            "code2",
            1000000L,
            List.of(1L, 2L, 3L)
        );
        Mockito.when(productService.getProductByIds(ArgumentMatchers.anyList()))
            .thenReturn(createProductVms());
        var result = promotionService.verifyPromotion(promotionVerifyData);

        assertEquals(true, result.isValid());
        assertEquals(1L, result.productId());
        assertEquals(DiscountType.PERCENTAGE, result.discountType());
        assertEquals(20L, result.discountValue().longValue());
    }

    @Test
    void verifyPromotion_applyToCategory_ThenSuccess() {
        PromotionVerifyVm promotionVerifyData = new PromotionVerifyVm(
            "code3",
            1000000L,
            List.of(1L, 2L, 3L)
        );
        Mockito.when(productService.getProductByCategoryIds(ArgumentMatchers.anyList()))
            .thenReturn(createProductVms());
        var result = promotionService.verifyPromotion(promotionVerifyData);

        assertEquals(true, result.isValid());
        assertEquals(1L, result.productId());
        assertEquals(DiscountType.FIXED, result.discountType());
        assertEquals(200L, result.discountValue().longValue());
    }

    private List<ProductVm> createProductVms() {
        return List.of(
            new ProductVm(
                1L,
                "Product 01",
                "product-01",
                true,
                true,
                false,
                true,
                10000000.0,
                ZonedDateTime.now(),
                2L
            )
        );
    }
}
