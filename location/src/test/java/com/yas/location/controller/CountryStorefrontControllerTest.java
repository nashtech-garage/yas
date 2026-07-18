package com.yas.location.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.location.service.CountryService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.country.CountryVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CountryStorefrontController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class CountryStorefrontControllerTest {

    @MockitoBean
    private CountryService countryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listCountries_returnsOk() throws Exception {
        given(countryService.findAllCountries()).willReturn(List.of());

        mockMvc.perform(get(Constants.ApiConstant.COUNTRIES_STOREFRONT_URL))
            .andExpect(status().isOk());
    }

    @Test
    void listCountries_whenServiceReturnsData_returnsOk() throws Exception {
        given(countryService.findAllCountries()).willReturn(
            List.of(new CountryVm(1L, "US", "United States", "USA", true, true, true, true, true)));

        mockMvc.perform(get(Constants.ApiConstant.COUNTRIES_STOREFRONT_URL))
            .andExpect(status().isOk());
    }
}
