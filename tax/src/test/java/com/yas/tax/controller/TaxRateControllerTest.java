package com.yas.tax.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TaxRateController.class, excludeAutoConfiguration = {
    org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration.class,
    org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class TaxRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaxRateService taxRateService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getTaxRate_whenCalled_thenReturnOk() throws Exception {
        when(taxRateService.findById(1L)).thenReturn(new TaxRateVm(1L, 10.0, "123", 1L, 1L, 1L));
        mockMvc.perform(get("/backoffice/tax-rates/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createTaxRate_whenValid_thenReturnCreated() throws Exception {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "123", 1L, 1L, 1L);
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        TaxRate taxRate = new TaxRate();
        taxRate.setId(1L);
        taxRate.setTaxClass(taxClass);
        
        when(taxRateService.createTaxRate(any())).thenReturn(taxRate);
        
        mockMvc.perform(post("/backoffice/tax-rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateTaxRate_whenValid_thenReturnNoContent() throws Exception {
        TaxRatePostVm postVm = new TaxRatePostVm(15.0, "123", 1L, 1L, 1L);
        mockMvc.perform(put("/backoffice/tax-rates/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTaxRate_whenCalled_thenReturnNoContent() throws Exception {
        mockMvc.perform(delete("/backoffice/tax-rates/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTaxPercentByAddress_whenCalled_thenReturnOk() throws Exception {
        when(taxRateService.getTaxPercent(anyLong(), anyLong(), anyLong(), any())).thenReturn(10.0);
        mockMvc.perform(get("/backoffice/tax-rates/tax-percent")
                .param("taxClassId", "1")
                .param("countryId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getBatchTaxPercentsByAddress_whenCalled_thenReturnOk() throws Exception {
        when(taxRateService.getBulkTaxRate(any(), any(), any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/backoffice/tax-rates/location-based-batch")
                .param("taxClassIds", "1,2")
                .param("countryId", "1"))
                .andExpect(status().isOk());
    }
}
