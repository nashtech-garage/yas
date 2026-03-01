package com.yas.tax.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateGetDetailVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaxRateController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaxRateControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TaxRateService taxRateService;

    @Test
    void getPageableTaxRates_shouldReturnPage() throws Exception {
        TaxRateGetDetailVm detailVm = new TaxRateGetDetailVm(1L, 5.5, "12345", "Standard", "State", "Country");
        TaxRateListGetVm listVm = new TaxRateListGetVm(List.of(detailVm), 0, 10, 1, 1, true);
        when(taxRateService.getPageableTaxRates(0, 10)).thenReturn(listVm);

        mockMvc.perform(get("/backoffice/tax-rates/paging"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taxRateGetDetailContent[0].rate", is(5.5)))
            .andExpect(jsonPath("$.totalElements", is(1)))
            .andExpect(jsonPath("$.isLast", is(true)));
    }

    @Test
    void getTaxRate_shouldReturnItem() throws Exception {
        TaxRateVm vm = new TaxRateVm(2L, 7.0, "", 1L, 2L, 3L);
        when(taxRateService.findById(2L)).thenReturn(vm);

        mockMvc.perform(get("/backoffice/tax-rates/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(2)))
            .andExpect(jsonPath("$.taxClassId", is(1)));
    }

    @Test
    void createTaxRate_shouldReturnCreated() throws Exception {
        TaxRatePostVm request = new TaxRatePostVm(9.1, "70000", 1L, 3L, 4L);
        TaxClass taxClass = TaxClass.builder().id(1L).name("Standard").build();
        TaxRate saved = TaxRate.builder()
            .id(10L)
            .rate(9.1)
            .zipCode("70000")
            .taxClass(taxClass)
            .stateOrProvinceId(3L)
            .countryId(4L)
            .build();
        when(taxRateService.createTaxRate(any(TaxRatePostVm.class))).thenReturn(saved);

        mockMvc.perform(post("/backoffice/tax-rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/tax-rates/10")))
            .andExpect(jsonPath("$.id", is(10)))
            .andExpect(jsonPath("$.stateOrProvinceId", is(3)))
            .andExpect(jsonPath("$.countryId", is(4)));
    }

    @Test
    void updateTaxRate_shouldReturnNoContent() throws Exception {
        doNothing().when(taxRateService).updateTaxRate(any(TaxRatePostVm.class), eq(5L));

        mockMvc.perform(put("/backoffice/tax-rates/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TaxRatePostVm(1.0, "", 1L, null, 1L))))
            .andExpect(status().isNoContent());

        verify(taxRateService).updateTaxRate(any(TaxRatePostVm.class), eq(5L));
    }

    @Test
    void deleteTaxRate_shouldReturnNoContent() throws Exception {
        doNothing().when(taxRateService).delete(6L);

        mockMvc.perform(delete("/backoffice/tax-rates/6"))
            .andExpect(status().isNoContent());

        verify(taxRateService).delete(6L);
    }

    @Test
    void getTaxPercentByAddress_shouldReturnValue() throws Exception {
        when(taxRateService.getTaxPercent(1L, 2L, 3L, "75000")).thenReturn(4.2);

        mockMvc.perform(get("/backoffice/tax-rates/tax-percent")
                .param("taxClassId", "1")
                .param("countryId", "2")
                .param("stateOrProvinceId", "3")
                .param("zipCode", "75000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(4.2)));
    }

    @Test
    void getBatchTaxPercentsByAddress_shouldReturnList() throws Exception {
        TaxRateVm vm = new TaxRateVm(3L, 8.8, "", 1L, 2L, 3L);
        when(taxRateService.getBulkTaxRate(List.of(1L, 2L), 3L, 4L, ""))
            .thenReturn(List.of(vm));

        mockMvc.perform(get("/backoffice/tax-rates/location-based-batch")
                .param("taxClassIds", "1,2")
                .param("countryId", "3")
                .param("stateOrProvinceId", "4")
                .param("zipCode", ""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(3)))
            .andExpect(jsonPath("$[0].rate", is(8.8)));
    }
}
