package com.yas.tax.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import com.yas.tax.constants.ApiConstant;
import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.yas.commonlibrary.exception.ApiExceptionHandler;
import org.springframework.test.context.ContextConfiguration;

@WebMvcTest(excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@ContextConfiguration(classes = {
    TaxRateController.class,
    ApiExceptionHandler.class
})
@AutoConfigureMockMvc(addFilters = false)
public class TaxRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaxRateService taxRateService;

    @Test
    void getPageableTaxRates_ShouldReturnTaxRateListGetVm() throws Exception {
        TaxRateListGetVm response = new TaxRateListGetVm(
                List.of(),
                0, 10, 0, 0, true
        );
        when(taxRateService.getPageableTaxRates(anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get(ApiConstant.TAX_RATE_URL + "/paging")
                .param("pageNo", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getTaxRate_WhenExist_ShouldReturnTaxRateVm() throws Exception {
        TaxRateVm response = new TaxRateVm(1L, 10.0, "12345", 1L, 1L, 1L);
        when(taxRateService.findById(1L)).thenReturn(response);

        mockMvc.perform(get(ApiConstant.TAX_RATE_URL + "/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createTaxRate_ValidRequest_ShouldReturnCreated() throws Exception {
        TaxRatePostVm request = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        TaxRate taxRate = new TaxRate();
        taxRate.setId(1L);
        com.yas.tax.model.TaxClass taxClass = new com.yas.tax.model.TaxClass();
        taxClass.setId(1L);
        taxRate.setTaxClass(taxClass);
        
        when(taxRateService.createTaxRate(any())).thenReturn(taxRate);

        mockMvc.perform(post(ApiConstant.TAX_RATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateTaxRate_ValidRequest_ShouldReturnNoContent() throws Exception {
        TaxRatePostVm request = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        doNothing().when(taxRateService).updateTaxRate(any(), eq(1L));

        mockMvc.perform(put(ApiConstant.TAX_RATE_URL + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTaxRate_ValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(taxRateService).delete(1L);

        mockMvc.perform(delete(ApiConstant.TAX_RATE_URL + "/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTaxPercentByAddress_ShouldReturnPercent() throws Exception {
        when(taxRateService.getTaxPercent(any(), any(), any(), any())).thenReturn(10.0);

        mockMvc.perform(get(ApiConstant.TAX_RATE_URL + "/tax-percent")
                .param("taxClassId", "1")
                .param("countryId", "1")
                .param("stateOrProvinceId", "1")
                .param("zipCode", "12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(10.0));
    }

    @Test
    void getBatchTaxPercentsByAddress_ShouldReturnList() throws Exception {
        when(taxRateService.getBulkTaxRate(any(), any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get(ApiConstant.TAX_RATE_URL + "/location-based-batch")
                .param("taxClassIds", "1,2")
                .param("countryId", "1")
                .param("stateOrProvinceId", "1")
                .param("zipCode", "12345"))
                .andExpect(status().isOk());
    }
}
