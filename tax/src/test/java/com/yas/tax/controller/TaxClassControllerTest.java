package com.yas.tax.controller;

import com.yas.tax.model.TaxClass;
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaxClassControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaxClassService taxClassService;

    @InjectMocks
    private TaxClassController taxClassController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taxClassController).build();
    }

    @Test
    void getPageableTaxClasses_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/backoffice/tax-classes")
                        .param("pageNo", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getTaxClass_ShouldReturnOk() throws Exception {
        TaxClassVm mockVm = mock(TaxClassVm.class);
        when(taxClassService.findById(1L)).thenReturn(mockVm);

        mockMvc.perform(get("/backoffice/tax-classes/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void createTaxClass_ShouldReturnCreated() throws Exception {
        TaxClass taxClass = mock(TaxClass.class);
        when(taxClass.getId()).thenReturn(1L);
        when(taxClassService.create(any(TaxClassPostVm.class))).thenReturn(taxClass);

        // Thêm field "id" vì TaxClassPostVm có @NotBlank String id
        String json = "{\"id\":\"1\",\"name\":\"Standard Tax\"}";

        mockMvc.perform(post("/backoffice/tax-classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void updateTaxClass_ShouldReturnNoContent() throws Exception {
        doNothing().when(taxClassService).update(any(TaxClassPostVm.class), eq(1L));

        // Thêm field "id" vì TaxClassPostVm có @NotBlank String id
        String json = "{\"id\":\"1\",\"name\":\"Updated Tax\"}";

        mockMvc.perform(put("/backoffice/tax-classes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTaxClass_ShouldReturnNoContent() throws Exception {
        doNothing().when(taxClassService).delete(1L);

        mockMvc.perform(delete("/backoffice/tax-classes/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}