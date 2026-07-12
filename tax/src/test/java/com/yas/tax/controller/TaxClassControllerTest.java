package com.yas.tax.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.tax.model.TaxClass;
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = TaxClassController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class TaxClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaxClassService taxClassService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getPageableTaxClasses_shouldReturnPage() throws Exception {
        TaxClassListGetVm response = new TaxClassListGetVm(List.of(), 0, 10, 0, 0, true);
        when(taxClassService.getPageableTaxClasses(anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/backoffice/tax-classes/paging"))
                .andExpect(status().isOk());
    }

    @Test
    void listTaxClasses_shouldReturnList() throws Exception {
        TaxClassVm vm = new TaxClassVm(1L, "Standard");
        when(taxClassService.findAllTaxClasses()).thenReturn(List.of(vm));

        mockMvc.perform(get("/backoffice/tax-classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Standard"));
    }

    @Test
    void getTaxClass_shouldReturnTaxClass() throws Exception {
        TaxClassVm vm = new TaxClassVm(1L, "Standard");
        when(taxClassService.findById(1L)).thenReturn(vm);

        mockMvc.perform(get("/backoffice/tax-classes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Standard"));
    }

    @Test
    void createTaxClass_shouldReturnCreated() throws Exception {
        TaxClassPostVm postVm = new TaxClassPostVm("new-class", "New Class");
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("New Class");
        when(taxClassService.create(any(TaxClassPostVm.class))).thenReturn(taxClass);

        mockMvc.perform(post("/backoffice/tax-classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateTaxClass_shouldReturnNoContent() throws Exception {
        TaxClassPostVm postVm = new TaxClassPostVm("updated-class", "Updated Class");
        doNothing().when(taxClassService).update(any(TaxClassPostVm.class), anyLong());

        mockMvc.perform(put("/backoffice/tax-classes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTaxClass_shouldReturnNoContent() throws Exception {
        doNothing().when(taxClassService).delete(1L);

        mockMvc.perform(delete("/backoffice/tax-classes/1"))
                .andExpect(status().isNoContent());
    }
}
