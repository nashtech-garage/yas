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
    void testListCountries_thenReturnOkAndPayload() throws Exception {
        given(countryService.findAllCountries()).willReturn(List.of(
            new CountryVm(1L, "VN", "Viet Nam", "VNM", true, true, true, true, true)
        ));

        mockMvc.perform(get(Constants.ApiConstant.COUNTRIES_STOREFRONT_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].code2").value("VN"))
            .andExpect(jsonPath("$[0].name").value("Viet Nam"));
    }
}
