package com.yas.location.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.location.service.StateOrProvinceService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = StateOrProvinceStoreFrontController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class StateOrProvinceStoreFrontControllerTest {

    @MockitoBean
    private StateOrProvinceService stateOrProvinceService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetStateOrProvince_whenCountryIdProvided_shouldReturnStateOrProvinceList() throws Exception {
        Long countryId = 1L;
        StateOrProvinceVm sp1 = new StateOrProvinceVm(1L, "Ho Chi Minh", "HCM", "City", countryId);
        StateOrProvinceVm sp2 = new StateOrProvinceVm(2L, "Ha Noi", "HN", "City", countryId);

        given(stateOrProvinceService.getAllByCountryId(countryId)).willReturn(List.of(sp1, sp2));

        mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL + "/{countryId}", countryId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Ho Chi Minh"))
            .andExpect(jsonPath("$[0].code").value("HCM"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Ha Noi"));
    }

    @Test
    void testGetStateOrProvince_whenNoStateOrProvinces_shouldReturnEmptyList() throws Exception {
        Long countryId = 99L;

        given(stateOrProvinceService.getAllByCountryId(countryId)).willReturn(List.of());

        mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL + "/{countryId}", countryId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetStateOrProvince_whenCountryIdIsValid_shouldReturnCorrectCountryId() throws Exception {
        Long countryId = 5L;
        StateOrProvinceVm sp = new StateOrProvinceVm(10L, "Sai Gon", "SG", "City", countryId);

        given(stateOrProvinceService.getAllByCountryId(countryId)).willReturn(List.of(sp));

        mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL + "/{countryId}", countryId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].countryId").value(5));
    }
}
