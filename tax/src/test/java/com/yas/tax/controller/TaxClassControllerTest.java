package com.yas.tax.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
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
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaxClassController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaxClassControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TaxClassService taxClassService;

    @Test
    void getPageableTaxClasses_shouldReturnPage() throws Exception {
        TaxClassVm vm = new TaxClassVm(1L, "Standard");
        TaxClassListGetVm listVm = new TaxClassListGetVm(List.of(vm), 0, 10, 1, 1, true);
        when(taxClassService.getPageableTaxClasses(0, 10)).thenReturn(listVm);

        mockMvc.perform(get("/backoffice/tax-classes/paging"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taxClassContent[0].name", is("Standard")))
            .andExpect(jsonPath("$.pageNo", is(0)))
            .andExpect(jsonPath("$.isLast", is(true)));
    }

    @Test
    void listTaxClasses_shouldReturnList() throws Exception {
        when(taxClassService.findAllTaxClasses()).thenReturn(List.of(new TaxClassVm(1L, "Standard")));

        mockMvc.perform(get("/backoffice/tax-classes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("Standard")));
    }

    @Test
    void getTaxClass_shouldReturnItem() throws Exception {
        when(taxClassService.findById(5L)).thenReturn(new TaxClassVm(5L, "Premium"));

        mockMvc.perform(get("/backoffice/tax-classes/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(5)))
            .andExpect(jsonPath("$.name", is("Premium")));
    }

    @Test
    void createTaxClass_shouldReturnCreated() throws Exception {
        TaxClassPostVm request = new TaxClassPostVm("id", "Standard");
        TaxClass saved = TaxClass.builder().id(9L).name("Standard").build();
        when(taxClassService.create(any(TaxClassPostVm.class))).thenReturn(saved);

        mockMvc.perform(post("/backoffice/tax-classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/tax-classes/9")))
            .andExpect(jsonPath("$.id", is(9)))
            .andExpect(jsonPath("$.name", is("Standard")));
    }

    @Test
    void updateTaxClass_shouldReturnNoContent() throws Exception {
        doNothing().when(taxClassService).update(any(TaxClassPostVm.class), eq(3L));

        mockMvc.perform(put("/backoffice/tax-classes/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TaxClassPostVm("id", "Updated"))))
            .andExpect(status().isNoContent());

        verify(taxClassService).update(any(TaxClassPostVm.class), eq(3L));
    }

    @Test
    void deleteTaxClass_shouldReturnNoContent() throws Exception {
        doNothing().when(taxClassService).delete(4L);

        mockMvc.perform(delete("/backoffice/tax-classes/4"))
            .andExpect(status().isNoContent());

        verify(taxClassService).delete(4L);
    }
}
