package com.yas.location.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.location.service.DistrictService;
import com.yas.location.viewmodel.district.DistrictGetVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DistrictStorefrontController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class DistrictStorefrontControllerTest {

    @MockitoBean
    private DistrictService districtService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetDistrictList_storefront_whenValidStateId_thenReturnOk() throws Exception {
        // DistrictGetVm record: id, name
        DistrictGetVm district1 = new DistrictGetVm(1L, "Hoan Kiem");
        DistrictGetVm district2 = new DistrictGetVm(2L, "Ba Dinh");
        given(districtService.getList(1L)).willReturn(List.of(district1, district2));

        this.mockMvc.perform(get("/storefront/district/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetDistrictList_backoffice_whenValidStateId_thenReturnOk() throws Exception {
        DistrictGetVm district1 = new DistrictGetVm(1L, "Hoan Kiem");
        given(districtService.getList(1L)).willReturn(List.of(district1));

        this.mockMvc.perform(get("/backoffice/district/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetDistrictList_whenNoDistricts_thenReturnEmptyList() throws Exception {
        given(districtService.getList(99L)).willReturn(List.of());

        this.mockMvc.perform(get("/storefront/district/99")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
