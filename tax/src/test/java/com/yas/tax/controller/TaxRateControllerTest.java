package com.yas.tax.controller;

import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaxRateControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaxRateService taxRateService;

    @InjectMocks
    private TaxRateController taxRateController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taxRateController).build();
    }

    @Test
    void getTaxRate_ShouldReturnOk() throws Exception {
        TaxRateVm taxRateVm = mock(TaxRateVm.class);
        when(taxRateService.findById(1L)).thenReturn(taxRateVm);

        mockMvc.perform(get("/backoffice/tax-rates/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void createTaxRate_ShouldReturnCreated() throws Exception {
        TaxClass taxClass = mock(TaxClass.class);
        lenient().when(taxClass.getName()).thenReturn("VAT");
        lenient().when(taxClass.getId()).thenReturn(1L);

        TaxRate taxRate = mock(TaxRate.class);
        lenient().when(taxRate.getId()).thenReturn(1L);
        lenient().when(taxRate.getRate()).thenReturn(10.0);
        lenient().when(taxRate.getTaxClass()).thenReturn(taxClass);
        lenient().when(taxRate.getCountryId()).thenReturn(1L);
        lenient().when(taxRate.getStateOrProvinceId()).thenReturn(1L);
        lenient().when(taxRate.getZipCode()).thenReturn("10000");

        when(taxRateService.createTaxRate(any(TaxRatePostVm.class))).thenReturn(taxRate);

        String json = "{" +
                "\"rate\":10.0," +
                "\"taxClassId\":1," +
                "\"countryId\":1," +
                "\"stateOrProvinceId\":1," +
                "\"zipCode\":\"10000\"" +
                "}";

        mockMvc.perform(post("/backoffice/tax-rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void updateTaxRate_ShouldReturnNoContent() throws Exception {
        String json = "{" +
                "\"rate\":12.0," +
                "\"taxClassId\":1," +
                "\"countryId\":1," +
                "\"stateOrProvinceId\":1," +
                "\"zipCode\":\"10000\"" +
                "}";

        mockMvc.perform(put("/backoffice/tax-rates/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTaxRate_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/backoffice/tax-rates/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTaxPercent_ShouldReturnOk() throws Exception {
        when(taxRateService.getTaxPercent(anyLong(), anyLong(), anyLong(), anyString()))
                .thenReturn(10.0);

        mockMvc.perform(get("/backoffice/tax-rates/tax-percent")
                        .param("taxClassId", "1")
                        .param("countryId", "1")
                        .param("stateOrProvinceId", "1")
                        .param("zipCode", "10000"))
                .andExpect(status().isOk());
    }

    @Test
    void getBulkTaxRate_ShouldReturnOk() throws Exception {
        TaxRateVm taxRateVm = mock(TaxRateVm.class);
        when(taxRateService.getBulkTaxRate(anyList(), anyLong(), anyLong(), anyString()))
                .thenReturn(List.of(taxRateVm));

        // Sửa URL: /location-based-batch thay vì /tax-percent-batch
        mockMvc.perform(get("/backoffice/tax-rates/location-based-batch")
                        .param("taxClassIds", "1,2")
                        .param("countryId", "1")
                        .param("stateOrProvinceId", "1")
                        .param("zipCode", "10000"))
                .andExpect(status().isOk());
    }

    @Test
    void getPageableTaxRates_ShouldReturnOk() throws Exception {
        TaxRateListGetVm mockVm = mock(TaxRateListGetVm.class);
        when(taxRateService.getPageableTaxRates(anyInt(), anyInt())).thenReturn(mockVm);

        // Sửa URL: /paging thay vì /backoffice/tax-rates
        mockMvc.perform(get("/backoffice/tax-rates/paging")
                        .param("pageNo", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk());
    }
}