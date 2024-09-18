package com.yas.promotion.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.promotion.PromotionApplication;
import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import com.yas.promotion.service.PromotionService;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionListVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import com.yas.promotion.viewmodel.PromotionPutVm;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PromotionController.class)
@ContextConfiguration(classes = PromotionApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class PromotionControllerTest {

    @MockBean
    private PromotionService promotionService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        var objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    void testCreatePromotion_whenRequestIsValid_thenReturnOk() throws Exception {
        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
                .name("name")
                .slug("slug")
                .applyTo(ApplyTo.PRODUCT)
                .couponCode("code")
                .discountPercentage(10L)
                .usageType(UsageType.UNLIMITED)
                .discountType(DiscountType.PERCENTAGE)
                .productIds(List.of(1L, 2L, 3L))
                .isActive(true)
                .usageLimit(0)
                .startDate(Date.from(Instant.now()))
                .endDate(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .build();

        String request = objectWriter.writeValueAsString(promotionPostVm);

        this.mockMvc.perform(post("/backoffice/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreatePromotion_whenNameIsOverMaxLength_thenReturnBadRequest() throws Exception {
        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
                .name("1234567890".repeat(46))
                .slug("slug")
                .discountPercentage(0L)
                .applyTo(ApplyTo.PRODUCT)
                .couponCode("code")
                .usageType(UsageType.UNLIMITED)
                .discountType(DiscountType.PERCENTAGE)
                .productIds(List.of(1L, 2L, 3L))
                .isActive(true)
                .usageLimit(0)
                .startDate(Date.from(Instant.now()))
                .endDate(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .build();

        String request = objectWriter.writeValueAsString(promotionPostVm);

        this.mockMvc.perform(post("/backoffice/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreatePromotion_whenNameIsBlank_thenReturnBadRequest() throws Exception {
        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
                .name("")
                .slug("slug")
                .discountPercentage(0L)
                .applyTo(ApplyTo.PRODUCT)
                .couponCode("code")
                .usageType(UsageType.UNLIMITED)
                .discountType(DiscountType.PERCENTAGE)
                .productIds(List.of(1L, 2L, 3L))
                .isActive(true)
                .usageLimit(0)
                .startDate(Date.from(Instant.now()))
                .endDate(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .build();

        String request = objectWriter.writeValueAsString(promotionPostVm);

        this.mockMvc.perform(post("/backoffice/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreatePromotion_whenDiscountAmountIsSmallerThanZero_thenReturnBadRequest() throws Exception {
        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
            .name("amount smaller than 0")
            .slug("amount-smaller-than-0")
            .discountAmount(-15L)
            .applyTo(ApplyTo.PRODUCT)
            .couponCode("code")
            .usageType(UsageType.UNLIMITED)
            .discountType(DiscountType.FIXED)
            .productIds(List.of(1L, 2L, 3L))
            .isActive(true)
            .usageLimit(0)
            .startDate(Date.from(Instant.now()))
            .endDate(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
            .build();

        String request = objectWriter.writeValueAsString(promotionPostVm);

        this.mockMvc.perform(post("/backoffice/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreatePromotion_whenDiscountPercentageIsSmallerThanZero_thenReturnBadRequest() throws Exception {
        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
            .name("percentage smaller than 0")
            .slug("percentage-smaller-than-0")
            .discountPercentage(-2L)
            .applyTo(ApplyTo.PRODUCT)
            .couponCode("code")
            .usageType(UsageType.UNLIMITED)
            .discountType(DiscountType.PERCENTAGE)
            .productIds(List.of(1L, 2L, 3L))
            .isActive(true)
            .usageLimit(0)
            .startDate(Date.from(Instant.now()))
            .endDate(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
            .build();

        String request = objectWriter.writeValueAsString(promotionPostVm);

        this.mockMvc.perform(post("/backoffice/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreatePromotion_whenDiscountPercentageIsGreaterThanHundred_thenReturnBadRequest() throws Exception {
        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
            .name("percentage greater than 100")
            .slug("percentage-greater-than-100")
            .discountPercentage(112L)
            .applyTo(ApplyTo.PRODUCT)
            .couponCode("code")
            .usageType(UsageType.UNLIMITED)
            .discountType(DiscountType.PERCENTAGE)
            .productIds(List.of(1L, 2L, 3L))
            .isActive(true)
            .usageLimit(0)
            .startDate(Date.from(Instant.now()))
            .endDate(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
            .build();

        String request = objectWriter.writeValueAsString(promotionPostVm);

        this.mockMvc.perform(post("/backoffice/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testListPromotions_whenValidRequest_thenReturnPromotionListVm() throws Exception {

        PromotionDetailVm promoDetail1 = PromotionDetailVm.builder()
            .id(1L)
            .name("Winter Sale")
            .slug("winter-sale")
            .description("Get up to 50% off on winter clothing.")
            .couponCode("WINTER50")
            .usageLimit(100)
            .usageCount(25)
            .discountType(DiscountType.PERCENTAGE)
            .discountPercentage(50L)
            .discountAmount(null)
            .isActive(true)
            .build();

        PromotionDetailVm promoDetail2 = PromotionDetailVm.builder()
            .id(2L)
            .name("Summer Clearance")
            .slug("summer-clearance")
            .description("Flat $20 off on all summer products.")
            .couponCode("SUMMER20")
            .usageLimit(200)
            .usageCount(50)
            .discountPercentage(null)
            .discountAmount(20L)
            .isActive(true)
            .build();
        List<PromotionDetailVm> promotionDetails = new ArrayList<>();
        promotionDetails.add(promoDetail1);
        promotionDetails.add(promoDetail2);

        PromotionListVm promotionList = PromotionListVm.builder()
            .promotionDetailVmList(promotionDetails)
            .pageNo(1)
            .pageSize(10)
            .totalElements(25)
            .totalPages(3)
            .build();

        when(promotionService.getPromotions(anyInt(), anyInt(), anyString(), anyString(),
            any(Instant.class), any(Instant.class))).thenReturn(promotionList);

        mockMvc.perform(MockMvcRequestBuilders.get("/backoffice/promotions")
                .param("pageNo", "0")
                .param("pageSize", "5")
                .param("promotionName", "")
                .param("couponCode", "")
                .param("startDate", "1970-01-01T00:00:00Z")
                .param("endDate", Instant.now().toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(promotionList)));
    }

    @Test
    void testUpdatePromotion_whenValidRequest_thenReturnPromotionDetailVm() throws Exception {

        PromotionPutVm promotionPutVm = getPromotionPutVm();

        PromotionDetailVm promoDetail = PromotionDetailVm.builder()
            .id(1L)
            .name("Holiday Discount")
            .slug("holiday-discount")
            .description("Enjoy a 30% discount on all items during the holiday season.")
            .couponCode("HOLIDAY30")
            .usageLimit(100)
            .usageCount(5)
            .discountType(DiscountType.PERCENTAGE)
            .discountPercentage(30L)
            .discountAmount(null)
            .isActive(true)
            .build();

        when(promotionService.updatePromotion(promotionPutVm)).thenReturn(promoDetail);

        mockMvc.perform(MockMvcRequestBuilders.put("/backoffice/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(promotionPutVm)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(promoDetail)));
    }

    @Test
    void testDeletePromotion_whenValidRequest_deleteSuccess() throws Exception {
        Long promotionId = 123L;

        doNothing().when(promotionService).deletePromotion(promotionId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/backoffice/promotions/{promotionId}", promotionId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());

        verify(promotionService).deletePromotion(promotionId);

    }

    private static @NotNull PromotionPutVm getPromotionPutVm() {
        PromotionPutVm promotionPutVm = new PromotionPutVm();
        promotionPutVm.setId(1L);
        promotionPutVm.setName("abc");
        promotionPutVm.setSlug("slug");
        promotionPutVm.setCouponCode("coupon");
        promotionPutVm.setUsageType(UsageType.LIMITED);
        promotionPutVm.setUsageLimit(1);
        promotionPutVm.setDiscountPercentage(1L);
        promotionPutVm.setDiscountType(DiscountType.PERCENTAGE);
        promotionPutVm.setApplyTo(ApplyTo.PRODUCT);
        promotionPutVm.setProductIds(List.of(1L));
        Instant startDate = Instant.parse("2024-12-01T00:00:00Z");
        Instant endDate = Instant.parse("2024-12-31T23:59:59Z");
        promotionPutVm.setStartDate(Date.from(startDate));
        promotionPutVm.setEndDate(Date.from(endDate));
        return promotionPutVm;
    }

}
