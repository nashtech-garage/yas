package com.yas.promotion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.PromotionApply;
import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.repository.PromotionUsageRepository;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private PromotionUsageRepository promotionUsageRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private PromotionService promotionService;

    private Promotion promotion1;
    private Promotion wrongRangeDatePromotion;
    private PromotionPostVm promotionPostVm;

    @BeforeEach
    void setUp() {
        promotion1 = Promotion.builder()
                .id(1L)
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

        wrongRangeDatePromotion = Promotion.builder()
            .id(2L)
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

        when(promotionRepository.findBySlugAndIsActiveTrue(promotionPostVm.getSlug())).thenReturn(Optional.empty());
        when(promotionRepository.findByCouponCodeAndIsActiveTrue(promotionPostVm.getCouponCode())).thenReturn(Optional.empty());
        
        Promotion savedPromotion = Promotion.builder()
                .id(4L)
                .name("Promotion 4")
                .slug("promotion-4")
                .couponCode("code4")
                .isActive(true)
                .applyTo(ApplyTo.PRODUCT)
                .build();
        when(promotionRepository.save(any(Promotion.class))).thenReturn(savedPromotion);

        PromotionDetailVm result = promotionService.createPromotion(promotionPostVm);
        assertEquals(promotionPostVm.getSlug(), result.slug());
        assertEquals(promotionPostVm.getName(), result.name());
        assertEquals(true, result.isActive());
    }

    @Test
    void createPromotion_WhenCouponCodeAlreadyExisted_ThenDuplicatedExceptionThrown() {
        promotionPostVm = PromotionPostVm.builder()
                .slug("slug-123")
                .couponCode("code3")
                .build();
        when(promotionRepository.findBySlugAndIsActiveTrue("slug-123")).thenReturn(Optional.empty());
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("code3")).thenReturn(Optional.of(promotion1));
        
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
        when(promotionRepository.findBySlugAndIsActiveTrue(promotionPostVm.getSlug())).thenReturn(Optional.of(promotion1));
        
        assertThrows(DuplicatedException.class, () -> promotionService.createPromotion(promotionPostVm),
                String.format(Constants.ErrorCode.SLUG_ALREADY_EXITED, promotionPostVm.getSlug()));
    }

    @Test
    void createPromotion_WhenEndDateBeforeStartDate_ThenDateRangeExceptionThrown() {
        promotionPostVm = PromotionPostVm.builder()
            .applyTo(ApplyTo.PRODUCT)
            .name("12345")
            .slug("valid-slug")
            .couponCode("cp-12345")
            .productIds(List.of(1L, 2L, 3L))
            .endDate(Date.from(Instant.now().minus(2, ChronoUnit.DAYS)))
            .startDate(Date.from(Instant.now()))
            .build();

        when(promotionRepository.findBySlugAndIsActiveTrue(any())).thenReturn(Optional.empty());
        when(promotionRepository.findByCouponCodeAndIsActiveTrue(any())).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            promotionService.createPromotion(promotionPostVm)
        );
        assertEquals(String.format(Constants.ErrorCode.DATE_RANGE_INVALID), exception.getMessage());
    }

    @Test
    void getPromotionList_ThenSuccess() {
        when(promotionRepository.findPromotions(any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(promotion1), PageRequest.of(0, 5), 1));
            
        PromotionListVm result = promotionService.getPromotions(0, 5,
                "Promotion", "code",
                Instant.now().minus(120, ChronoUnit.DAYS), Instant.now().plus(120, ChronoUnit.DAYS));
        assertEquals(1, result.promotionDetailVmList().size());
        PromotionDetailVm promotionDetailVm = result.promotionDetailVmList().getFirst();
        assertEquals("promotion-1", promotionDetailVm.slug());
    }

    @Test
    void getPromotion_ThenSuccess() {
        when(promotionRepository.findById(promotion1.getId())).thenReturn(Optional.of(promotion1));
        
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
        when(promotionRepository.findById(0L)).thenReturn(Optional.empty());
        var exception = assertThrows(NotFoundException.class, () -> promotionService.getPromotion(0L));
        assertEquals(String.format(Constants.ErrorCode.PROMOTION_NOT_FOUND, 0L), exception.getMessage());
    }

    @Test
    void testVerifyPromotion_PromotionNotFound() {
        var promotionVerifyVm = new PromotionVerifyVm("COUPON123", 150L, List.of(1L, 2L, 3L));
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("COUPON123")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Promotion COUPON123 is not found", exception.getMessage());
    }

    @Test
    void testVerifyPromotion_ExhaustedUsageQuantity() {
        var promotionVerifyVm = new PromotionVerifyVm("codeWrong", 130L, List.of(1L));
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("codeWrong")).thenReturn(Optional.of(wrongRangeDatePromotion));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Exhausted usage quantity", exception.getMessage());
    }

    @Test
    void testVerifyPromotion_InvalidOrderPrice() {
        var promotionVerifyVm = new PromotionVerifyVm("code1", 10L, List.of(1L));
        Promotion p = Promotion.builder().minimumOrderPurchaseAmount(100L).build();
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("code1")).thenReturn(Optional.of(p));
        
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Invalid minimum order purchase amount", exception.getMessage());
    }

    @Test
    void testVerifyPromotion_ProductNotFound() {
        var promotionVerifyVm = new PromotionVerifyVm("code2", 1000L, List.of(1L,2L,3L));
        Promotion p = Promotion.builder().applyTo(ApplyTo.CATEGORY).minimumOrderPurchaseAmount(0L).couponCode("code2").promotionApplies(List.of()).build();
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("code2")).thenReturn(Optional.of(p));
        Mockito.when(productService.getProductByCategoryIds(ArgumentMatchers.anyList())).thenReturn(List.of());

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
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("code1")).thenReturn(Optional.of(promotion1));
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
        Promotion p2 = Promotion.builder()
            .couponCode("code2")
            .discountType(DiscountType.PERCENTAGE)
            .discountPercentage(20L)
            .applyTo(ApplyTo.PRODUCT)
            .minimumOrderPurchaseAmount(0L)
            .build();
        p2.setPromotionApplies(List.of(PromotionApply.builder().promotion(p2).productId(1L).build()));
            
        PromotionVerifyVm promotionVerifyData = new PromotionVerifyVm(
            "code2",
            1000000L,
            List.of(1L, 2L, 3L)
        );
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("code2")).thenReturn(Optional.of(p2));
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
        Promotion p3 = Promotion.builder()
            .couponCode("code3")
            .discountType(DiscountType.FIXED)
            .discountAmount(200L)
            .applyTo(ApplyTo.CATEGORY)
            .minimumOrderPurchaseAmount(0L)
            .build();
        p3.setPromotionApplies(List.of(PromotionApply.builder().promotion(p3).categoryId(1L).build()));
        
        PromotionVerifyVm promotionVerifyData = new PromotionVerifyVm(
            "code3",
            1000000L,
            List.of(1L, 2L, 3L)
        );
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("code3")).thenReturn(Optional.of(p3));
        Mockito.when(productService.getProductByCategoryIds(ArgumentMatchers.anyList()))
            .thenReturn(createProductVms());
        var result = promotionService.verifyPromotion(promotionVerifyData);

        assertEquals(true, result.isValid());
        assertEquals(1L, result.productId());
        assertEquals(DiscountType.FIXED, result.discountType());
        assertEquals(200L, result.discountValue().longValue());
    }

    @Test
    void updatePromotion_ThenSuccess() {
        com.yas.promotion.viewmodel.PromotionPutVm putVm = new com.yas.promotion.viewmodel.PromotionPutVm();
        putVm.setId(1L);
        putVm.setName("Update");
        putVm.setSlug("update-slug");
        putVm.setCouponCode("code1");
        putVm.setUsageType(UsageType.UNLIMITED);
        putVm.setDiscountType(DiscountType.PERCENTAGE);
        putVm.setDiscountPercentage(20L);
        putVm.setApplyTo(ApplyTo.PRODUCT);
        putVm.setProductIds(List.of(1L, 2L));
        putVm.setIsActive(true);
        putVm.setStartDate(Date.from(Instant.now()));
        putVm.setEndDate(Date.from(Instant.now().plus(10, ChronoUnit.DAYS)));

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion1));
        when(promotionRepository.save(any())).thenReturn(promotion1);

        PromotionDetailVm result = promotionService.updatePromotion(putVm);
        assertEquals("Update", result.name());
    }

    @Test
    void deletePromotion_ThenSuccess() {
        when(promotionUsageRepository.existsByPromotionId(1L)).thenReturn(false);
        promotionService.deletePromotion(1L);
        Mockito.verify(promotionRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deletePromotion_WhenInUse_ThenThrowBadRequestException() {
        when(promotionUsageRepository.existsByPromotionId(1L)).thenReturn(true);
        assertThrows(BadRequestException.class, () -> promotionService.deletePromotion(1L));
    }

    @Test
    void updateUsagePromotion_ThenSuccess() {
        try (org.mockito.MockedStatic<com.yas.promotion.utils.AuthenticationUtils> utilities = Mockito.mockStatic(com.yas.promotion.utils.AuthenticationUtils.class)) {
            utilities.when(com.yas.promotion.utils.AuthenticationUtils::extractUserId).thenReturn("user1");
            
            com.yas.promotion.viewmodel.PromotionUsageVm usageVm = new com.yas.promotion.viewmodel.PromotionUsageVm("code1", 1L, "user1", 1L);
            when(promotionRepository.findByCouponCodeAndIsActiveTrue("code1")).thenReturn(Optional.of(promotion1));
            when(promotionUsageRepository.save(any())).thenReturn(null);
            when(promotionRepository.save(any())).thenReturn(promotion1);

            promotionService.updateUsagePromotion(List.of(usageVm));
            Mockito.verify(promotionUsageRepository, Mockito.times(1)).save(any());
        }
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