package com.yas.promotion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.promotion.PromotionApplication;
import com.yas.promotion.service.PromotionService;
import com.yas.promotion.viewmodel.PromotionPostVm;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testCreatePromotion_whenRequestIsValid_thenReturnOk() throws Exception {
        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
                .name("name")
                .slug("slug")
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
            .build();

        String request = objectWriter.writeValueAsString(promotionPostVm);

        this.mockMvc.perform(post("/backoffice/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors[0]", containsString("discountAmount")));
    }

    @Test
    void testCreatePromotion_whenDiscountPercentageIsSmallerThanZero_thenReturnBadRequest() throws Exception {
        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
            .name("percentage smaller than 0")
            .slug("percentage-smaller-than-0")
            .discountPercentage(-2L)
            .build();

        String request = objectWriter.writeValueAsString(promotionPostVm);

        this.mockMvc.perform(post("/backoffice/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors[0]", containsString("discountPercentage")));
    }

    @Test
    void testCreatePromotion_whenDiscountPercentageIsGreaterThanHundred_thenReturnBadRequest() throws Exception {
        PromotionPostVm promotionPostVm = PromotionPostVm.builder()
            .name("percentage greater than 100")
            .slug("percentage-greater-than-100")
            .discountPercentage(112L)
            .build();

        String request = objectWriter.writeValueAsString(promotionPostVm);

        this.mockMvc.perform(post("/backoffice/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors[0]", containsString("discountPercentage")));
    }

}
