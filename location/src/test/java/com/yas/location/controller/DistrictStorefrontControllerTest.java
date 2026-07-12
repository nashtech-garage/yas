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
    void testGetDistricts_whenStateOrProvinceIdProvided_shouldReturnDistrictList() throws Exception {
        Long stateOrProvinceId = 1L;
        DistrictGetVm district1 = new DistrictGetVm(1L, "District A");
        DistrictGetVm district2 = new DistrictGetVm(2L, "District B");

        given(districtService.getList(stateOrProvinceId))
            .willReturn(List.of(district1, district2));

        mockMvc.perform(get("/storefront/district/{id}", stateOrProvinceId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("District A"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("District B"));
    }

    @Test
    void testGetDistricts_viaBackofficeUrl_shouldReturnDistrictList() throws Exception {
        Long stateOrProvinceId = 2L;
        DistrictGetVm district = new DistrictGetVm(10L, "District X");

        given(districtService.getList(stateOrProvinceId)).willReturn(List.of(district));

        mockMvc.perform(get("/backoffice/district/{id}", stateOrProvinceId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(10))
            .andExpect(jsonPath("$[0].name").value("District X"));
    }

    @Test
    void testGetDistricts_whenNoDistricts_shouldReturnEmptyList() throws Exception {
        Long stateOrProvinceId = 99L;

        given(districtService.getList(stateOrProvinceId)).willReturn(List.of());

        mockMvc.perform(get("/storefront/district/{id}", stateOrProvinceId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
