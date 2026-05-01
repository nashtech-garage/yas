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
    void testGetStateOrProvinceByCountryId_whenValidCountryId_thenReturnOk() throws Exception {
        // StateOrProvinceVm record: id, name, code, type, countryId
        StateOrProvinceVm vm1 = new StateOrProvinceVm(1L, "Hanoi", "HN", "City", 1L);
        StateOrProvinceVm vm2 = new StateOrProvinceVm(2L, "Ho Chi Minh", "HCM", "City", 1L);
        given(stateOrProvinceService.getAllByCountryId(1L)).willReturn(List.of(vm1, vm2));

        this.mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetStateOrProvinceByCountryId_whenNoStateOrProvince_thenReturnEmptyList() throws Exception {
        given(stateOrProvinceService.getAllByCountryId(99L)).willReturn(List.of());

        this.mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL + "/99")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
