package com.yas.tax.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TaxRateControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TaxRateService taxRateService;

    @InjectMocks
    private TaxRateController taxRateController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taxRateController).build();
    }

    @Nested
    class GetTaxRate {
        @Test
        void getTaxRate_whenExists_shouldReturnOk() throws Exception {
            TaxRateVm vm = new TaxRateVm(1L, 10.0, "70000", 1L, 100L, 1L);
            when(taxRateService.findById(1L)).thenReturn(vm);

            mockMvc.perform(get("/backoffice/tax-rates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rate").value(10.0))
                .andExpect(jsonPath("$.zipCode").value("70000"));
        }
    }

    @Nested
    class GetPageableTaxRates {
        @Test
        void getPageableTaxRates_shouldReturnOk() throws Exception {
            TaxRateGetDetailVm detailVm = new TaxRateGetDetailVm(
                1L, 10.0, "70000", "VAT", "Ho Chi Minh", "Vietnam"
            );
            TaxRateListGetVm listVm = new TaxRateListGetVm(
                List.of(detailVm), 0, 10, 1, 1, true
            );
            when(taxRateService.getPageableTaxRates(0, 10)).thenReturn(listVm);

            mockMvc.perform(get("/backoffice/tax-rates/paging")
                    .param("pageNo", "0")
                    .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.taxRateGetDetailContent[0].taxClassName").value("VAT"))
                .andExpect(jsonPath("$.taxRateGetDetailContent[0].countryName").value("Vietnam"));
        }
    }

    @Nested
    class CreateTaxRate {
        @Test
        void createTaxRate_shouldReturnCreated() throws Exception {
            TaxRatePostVm postVm = new TaxRatePostVm(10.0, "70000", 1L, 100L, 1L);

            TaxClass taxClass = TaxClass.builder().id(1L).name("VAT").build();
            TaxRate savedTaxRate = TaxRate.builder()
                .id(1L).rate(10.0).zipCode("70000")
                .stateOrProvinceId(100L).countryId(1L)
                .taxClass(taxClass).build();

            when(taxRateService.createTaxRate(any(TaxRatePostVm.class))).thenReturn(savedTaxRate);

            mockMvc.perform(post("/backoffice/tax-rates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rate").value(10.0));
        }
    }

    @Nested
    class UpdateTaxRate {
        @Test
        void updateTaxRate_shouldReturnNoContent() throws Exception {
            TaxRatePostVm postVm = new TaxRatePostVm(15.0, "71000", 1L, 101L, 2L);

            doNothing().when(taxRateService).updateTaxRate(any(TaxRatePostVm.class), eq(1L));

            mockMvc.perform(put("/backoffice/tax-rates/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isNoContent());

            verify(taxRateService).updateTaxRate(any(TaxRatePostVm.class), eq(1L));
        }
    }

    @Nested
    class DeleteTaxRate {
        @Test
        void deleteTaxRate_shouldReturnNoContent() throws Exception {
            doNothing().when(taxRateService).delete(1L);

            mockMvc.perform(delete("/backoffice/tax-rates/1"))
                .andExpect(status().isNoContent());

            verify(taxRateService).delete(1L);
        }
    }

    @Nested
    class GetTaxPercentByAddress {
        @Test
        void getTaxPercent_shouldReturnOk() throws Exception {
            when(taxRateService.getTaxPercent(1L, 1L, 100L, "70000")).thenReturn(10.0);

            mockMvc.perform(get("/backoffice/tax-rates/tax-percent")
                    .param("taxClassId", "1")
                    .param("countryId", "1")
                    .param("stateOrProvinceId", "100")
                    .param("zipCode", "70000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(10.0));
        }
    }

    @Nested
    class GetBatchTaxPercentsByAddress {
        @Test
        void getBatchTaxPercents_shouldReturnOk() throws Exception {
            TaxRateVm vm = new TaxRateVm(1L, 10.0, "70000", 1L, 100L, 1L);
            when(taxRateService.getBulkTaxRate(List.of(1L, 2L), 1L, 100L, "70000"))
                .thenReturn(List.of(vm));

            mockMvc.perform(get("/backoffice/tax-rates/location-based-batch")
                    .param("taxClassIds", "1", "2")
                    .param("countryId", "1")
                    .param("stateOrProvinceId", "100")
                    .param("zipCode", "70000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].rate").value(10.0));
        }
    }
}
