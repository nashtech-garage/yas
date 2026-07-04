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
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
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
class TaxClassControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TaxClassService taxClassService;

    @InjectMocks
    private TaxClassController taxClassController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taxClassController).build();
    }

    @Nested
    class ListTaxClasses {
        @Test
        void listTaxClasses_shouldReturnOk() throws Exception {
            TaxClassVm vm1 = new TaxClassVm(1L, "Standard Tax");
            TaxClassVm vm2 = new TaxClassVm(2L, "Reduced Tax");
            when(taxClassService.findAllTaxClasses()).thenReturn(List.of(vm1, vm2));

            mockMvc.perform(get("/backoffice/tax-classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Standard Tax"));
        }
    }

    @Nested
    class GetTaxClass {
        @Test
        void getTaxClass_whenExists_shouldReturnOk() throws Exception {
            TaxClassVm vm = new TaxClassVm(1L, "Standard Tax");
            when(taxClassService.findById(1L)).thenReturn(vm);

            mockMvc.perform(get("/backoffice/tax-classes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Standard Tax"));
        }
    }

    @Nested
    class GetPageableTaxClasses {
        @Test
        void getPageableTaxClasses_shouldReturnOk() throws Exception {
            TaxClassVm vm = new TaxClassVm(1L, "Standard Tax");
            TaxClassListGetVm listVm = new TaxClassListGetVm(
                List.of(vm), 0, 10, 1, 1, true
            );
            when(taxClassService.getPageableTaxClasses(0, 10)).thenReturn(listVm);

            mockMvc.perform(get("/backoffice/tax-classes/paging")
                    .param("pageNo", "0")
                    .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.taxClassContent[0].name").value("Standard Tax"));
        }
    }

    @Nested
    class CreateTaxClass {
        @Test
        void createTaxClass_shouldReturnCreated() throws Exception {
            TaxClassPostVm postVm = new TaxClassPostVm("id1", "New Tax");
            TaxClass savedTaxClass = TaxClass.builder().id(3L).name("New Tax").build();

            when(taxClassService.create(any(TaxClassPostVm.class))).thenReturn(savedTaxClass);

            mockMvc.perform(post("/backoffice/tax-classes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("New Tax"));
        }
    }

    @Nested
    class UpdateTaxClass {
        @Test
        void updateTaxClass_shouldReturnNoContent() throws Exception {
            TaxClassPostVm postVm = new TaxClassPostVm("id1", "Updated Tax");

            doNothing().when(taxClassService).update(any(TaxClassPostVm.class), eq(1L));

            mockMvc.perform(put("/backoffice/tax-classes/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postVm)))
                .andExpect(status().isNoContent());

            verify(taxClassService).update(any(TaxClassPostVm.class), eq(1L));
        }
    }

    @Nested
    class DeleteTaxClass {
        @Test
        void deleteTaxClass_shouldReturnNoContent() throws Exception {
            doNothing().when(taxClassService).delete(1L);

            mockMvc.perform(delete("/backoffice/tax-classes/1"))
                .andExpect(status().isNoContent());

            verify(taxClassService).delete(1L);
        }
    }
}
