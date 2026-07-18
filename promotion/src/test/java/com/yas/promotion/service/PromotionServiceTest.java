package com.yas.promotion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;
    
    @Mock
    private ProductService productService;
    
    @InjectMocks
    private PromotionService promotionService;

    private Promotion promotion1;
    private Promotion promotion2;
    private Promotion promotion3;
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
                .startDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .endDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .applyTo(ApplyTo.BRAND)
                .minimumOrderPurchaseAmount(0L)
                .build();

        var promotionApply1 = PromotionApply.builder()
            .promotion(promotion1)
            .brandId(1L).build();
        promotion1.setPromotionApplies(List.of(promotionApply1));

        promotion2 = Promotion.builder()
                .id(2L)
                .name("Promotion 2")
                .slug("promotion-2")
                .description("Description 2")
                .couponCode("code2")
                .discountAmount(200L)
                .discountPercentage(20L)
                .isActive(true)
                .startDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .endDate(Instant.now().plus(60, ChronoUnit.DAYS))
                .applyTo(ApplyTo.PRODUCT)
                .discountType(DiscountType.PERCENTAGE)
                .minimumOrderPurchaseAmount(100L)
                .build();

        var promotionApply2 = PromotionApply.builder()
            .promotion(promotion2)
            .productId(1L).build();
        promotion2.setPromotionApplies(List.of(promotionApply2));

        promotion3 = Promotion.builder()
            .id(3L)
            .name("Promotion 3")
            .slug("promotion-3")
            .description("Description 3")
            .couponCode("code3")
            .discountAmount(200L)
            .discountPercentage(20L)
            .isActive(true)
            .startDate(Instant.now().minus(1, ChronoUnit.DAYS))
            .endDate(Instant.now().plus(60, ChronoUnit.DAYS))
            .applyTo(ApplyTo.CATEGORY)
            .discountType(DiscountType.FIXED)
            .minimumOrderPurchaseAmount(100L)
            .build();

        var promotionApply3 = PromotionApply.builder()
            .promotion(promotion3)
            .productId(1L).build();
        promotion3.setPromotionApplies(List.of(promotionApply3));

        wrongRangeDatePromotion = Promotion.builder()
            .id(4L)
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
            .startDate(Instant.now().minus(10, ChronoUnit.DAYS))
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
                
        Promotion savedPromotion = new Promotion();
        savedPromotion.setId(4L);
        savedPromotion.setName("Promotion 4");
        savedPromotion.setSlug("promotion-4");
        savedPromotion.setIsActive(true);

        org.mockito.Mockito.lenient().when(promotionRepository.findBySlugAndIsActiveTrue(anyString())).thenReturn(Optional.empty());
        org.mockito.Mockito.lenient().when(promotionRepository.findByCouponCodeAndIsActiveTrue(anyString())).thenReturn(Optional.empty());
        when(promotionRepository.save(any(Promotion.class))).thenReturn(savedPromotion);

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
        
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("code3")).thenReturn(Optional.of(promotion3));
        
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
                
        org.mockito.Mockito.lenient().when(promotionRepository.findBySlugAndIsActiveTrue(promotion1.getSlug())).thenReturn(Optional.of(promotion1));
        org.mockito.Mockito.lenient().when(promotionRepository.findByCouponCodeAndIsActiveTrue(anyString())).thenReturn(Optional.empty());
        
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
        Page<Promotion> page = new PageImpl<>(List.of(promotion1, promotion2, promotion3));
        when(promotionRepository.findPromotions(anyString(), anyString(), any(Instant.class), any(Instant.class), any(PageRequest.class)))
            .thenReturn(page);
            
        PromotionListVm result = promotionService.getPromotions(0, 5,
                "Promotion", "code",
                Instant.now().minus(120, ChronoUnit.DAYS), Instant.now().plus(120, ChronoUnit.DAYS));
        assertEquals(3, result.promotionDetailVmList().size());
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
        var promotionVerifyVm = new PromotionVerifyVm("code2", 10L, List.of(1L));
        
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("code2")).thenReturn(Optional.of(promotion2));
        
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Invalid minimum order purchase amount", exception.getMessage());
    }

    @Test
    void testVerifyPromotion_ProductNotFound() {
        var promotionVerifyVm = new PromotionVerifyVm("code2", 1000L, List.of(1L,2L,3L));
        
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("code2")).thenReturn(Optional.of(promotion2));
        when(productService.getProductByIds(anyList())).thenReturn(List.of());

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
        when(productService.getProductByBrandIds(anyList())).thenReturn(createProductVms());
        
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
        
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("code2")).thenReturn(Optional.of(promotion2));
        when(productService.getProductByIds(anyList())).thenReturn(createProductVms());
        
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
        
        when(promotionRepository.findByCouponCodeAndIsActiveTrue("code3")).thenReturn(Optional.of(promotion3));
        when(productService.getProductByCategoryIds(anyList())).thenReturn(createProductVms());
        
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
