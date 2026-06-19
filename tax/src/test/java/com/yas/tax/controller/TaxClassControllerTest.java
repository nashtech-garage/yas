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
import com.yas.tax.model.TaxClass;
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
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
    TaxClassController.class,
    ApiExceptionHandler.class
})
@AutoConfigureMockMvc(addFilters = false)
public class TaxClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaxClassService taxClassService;

    @Test
    void getPageableTaxClasses_ShouldReturnTaxClassListGetVm() throws Exception {
        TaxClassListGetVm response = new TaxClassListGetVm(
                List.of(new TaxClassVm(1L, "Standard")),
                0, 10, 1, 1, true
        );
        when(taxClassService.getPageableTaxClasses(anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get(ApiConstant.TAX_CLASS_URL + "/paging")
                .param("pageNo", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void listTaxClasses_ShouldReturnTaxClassVms() throws Exception {
        when(taxClassService.findAllTaxClasses()).thenReturn(List.of(new TaxClassVm(1L, "Standard")));

        mockMvc.perform(get(ApiConstant.TAX_CLASS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Standard"));
    }

    @Test
    void getTaxClass_WhenExist_ShouldReturnTaxClassVm() throws Exception {
        when(taxClassService.findById(1L)).thenReturn(new TaxClassVm(1L, "Standard"));

        mockMvc.perform(get(ApiConstant.TAX_CLASS_URL + "/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Standard"));
    }

    @Test
    void createTaxClass_ValidRequest_ShouldReturnCreated() throws Exception {
        TaxClassPostVm request = new TaxClassPostVm("1", "Standard");
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Standard");
        when(taxClassService.create(any())).thenReturn(taxClass);

        mockMvc.perform(post(ApiConstant.TAX_CLASS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Standard"));
    }

    @Test
    void updateTaxClass_ValidRequest_ShouldReturnNoContent() throws Exception {
        TaxClassPostVm request = new TaxClassPostVm("1", "Standard updated");
        doNothing().when(taxClassService).update(any(), eq(1L));

        mockMvc.perform(put(ApiConstant.TAX_CLASS_URL + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTaxClass_ValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(taxClassService).delete(1L);

        mockMvc.perform(delete(ApiConstant.TAX_CLASS_URL + "/{id}", 1))
                .andExpect(status().isNoContent());
    }
}
