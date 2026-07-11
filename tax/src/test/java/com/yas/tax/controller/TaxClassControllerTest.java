package com.yas.tax.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.tax.model.TaxClass;
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TaxClassController.class, excludeAutoConfiguration = {
    org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration.class,
    org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class TaxClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaxClassService taxClassService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void findAllTaxClasses_whenCalled_thenReturnOk() throws Exception {
        when(taxClassService.findAllTaxClasses()).thenReturn(List.of());
        mockMvc.perform(get("/backoffice/tax-classes"))
                .andExpect(status().isOk());
    }

    @Test
    void getTaxClass_whenCalled_thenReturnOk() throws Exception {
        when(taxClassService.findById(1L)).thenReturn(new TaxClassVm(1L, "Tax Class"));
        mockMvc.perform(get("/backoffice/tax-classes/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createTaxClass_whenValid_thenReturnCreated() throws Exception {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Tax Class");
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        when(taxClassService.create(any())).thenReturn(taxClass);
        
        mockMvc.perform(post("/backoffice/tax-classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateTaxClass_whenValid_thenReturnNoContent() throws Exception {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Updated");
        mockMvc.perform(put("/backoffice/tax-classes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTaxClass_whenCalled_thenReturnNoContent() throws Exception {
        mockMvc.perform(delete("/backoffice/tax-classes/1"))
                .andExpect(status().isNoContent());
    }
}
