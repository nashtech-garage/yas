package com.yas.location.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.location.service.CountryService;
import com.yas.location.service.DistrictService;
import com.yas.location.service.StateOrProvinceService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.country.CountryVm;
import com.yas.location.viewmodel.district.DistrictGetVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    CountryStorefrontController.class,
    DistrictStorefrontController.class,
    StateOrProvinceStoreFrontController.class
}, excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class StorefrontControllerTest {

    @MockitoBean
    private CountryService countryService;

    @MockitoBean
    private DistrictService districtService;

    @MockitoBean
    private StateOrProvinceService stateOrProvinceService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listCountries_whenCountriesExist_thenReturnCountries() throws Exception {
        given(countryService.findAllCountries()).willReturn(List.of(
            new CountryVm(1L, "VN", "Vietnam", "VNM", true, true, true, true, true)
        ));

        mockMvc.perform(get(Constants.ApiConstant.COUNTRIES_STOREFRONT_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Vietnam"));
    }

    @Test
    void getStorefrontDistricts_whenStateProvinceExists_thenReturnDistricts() throws Exception {
        given(districtService.getList(1L)).willReturn(List.of(new DistrictGetVm(10L, "District 1")));

        mockMvc.perform(get("/storefront/district/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(10))
            .andExpect(jsonPath("$[0].name").value("District 1"));
    }

    @Test
    void getBackofficeDistricts_whenStateProvinceExists_thenReturnDistricts() throws Exception {
        given(districtService.getList(1L)).willReturn(List.of(new DistrictGetVm(11L, "District 2")));

        mockMvc.perform(get("/backoffice/district/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(11))
            .andExpect(jsonPath("$[0].name").value("District 2"));
    }

    @Test
    void getStateOrProvinces_whenCountryExists_thenReturnStateOrProvinces() throws Exception {
        given(stateOrProvinceService.getAllByCountryId(1L)).willReturn(List.of(
            new StateOrProvinceVm(20L, "Ho Chi Minh", "HCM", "city", 1L)
        ));

        mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_STOREFRONT_URL + "/{countryId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(20))
            .andExpect(jsonPath("$[0].name").value("Ho Chi Minh"));
    }
}
