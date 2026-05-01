package com.yas.location.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.http.MediaType;
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
    void testListCountries_whenCalled_thenReturnOk() throws Exception {
        // CountryVm record: id, code2, name, code3, isBillingEnabled, isShippingEnabled, isCityEnabled, isZipCodeEnabled, isDistrictEnabled
        CountryVm countryVm1 = new CountryVm(1L, "VN", "Vietnam", null, null, null, null, null, null);
        CountryVm countryVm2 = new CountryVm(2L, "TH", "Thailand", null, null, null, null, null, null);
        given(countryService.findAllCountries()).willReturn(List.of(countryVm1, countryVm2));

        this.mockMvc.perform(get(Constants.ApiConstant.COUNTRIES_STOREFRONT_URL)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testListCountries_whenNoCountries_thenReturnEmptyList() throws Exception {
        given(countryService.findAllCountries()).willReturn(List.of());

        this.mockMvc.perform(get(Constants.ApiConstant.COUNTRIES_STOREFRONT_URL)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
