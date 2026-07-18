package com.yas.location.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void getStateOrProvince_returnsOk() throws Exception {
        given(stateOrProvinceService.getAllByCountryId(5L)).willReturn(List.of());

        mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL + "/5"))
            .andExpect(status().isOk());
    }

    @Test
    void getStateOrProvince_whenServiceReturnsData_returnsOk() throws Exception {
        given(stateOrProvinceService.getAllByCountryId(5L)).willReturn(
            List.of(new StateOrProvinceVm(1L, "California", "CA", "state", 5L)));

        mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL + "/5"))
            .andExpect(status().isOk());
    }
}
