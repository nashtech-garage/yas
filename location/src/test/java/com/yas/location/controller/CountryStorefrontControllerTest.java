package com.yas.location.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.location.LocationApplication;
import com.yas.location.service.CountryService;
import com.yas.location.viewmodel.country.CountryVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CountryStorefrontController.class)
@ContextConfiguration(classes = LocationApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class CountryStorefrontControllerTest {

    @MockBean
    private CountryService countryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testListCountries_Success() throws Exception {
        CountryVm country1 = new CountryVm(1L, "US", "United States", "USA", true, true, true, true, false);
        CountryVm country2 = new CountryVm(2L, "CA", "Canada", "CAN", true, true, false, true, false);

        given(countryService.findAllCountries()).willReturn(List.of(country1, country2));

        mockMvc.perform(get("/storefront/countries"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("United States"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Canada"));
    }

    @Test
    void testListCountries_EmptyList_Success() throws Exception {
        given(countryService.findAllCountries()).willReturn(List.of());

        mockMvc.perform(get("/storefront/countries"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testListCountries_OnlyOneCountry_Success() throws Exception {
        CountryVm country = new CountryVm(1L, "US", "United States", "USA", true, true, true, true, false);

        given(countryService.findAllCountries()).willReturn(List.of(country));

        mockMvc.perform(get("/storefront/countries"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code2").value("US"));
    }
}
