package com.yas.location.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.location.LocationApplication;
import com.yas.location.model.Country;
import com.yas.location.service.CountryService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.country.CountryListGetVm;
import com.yas.location.viewmodel.country.CountryPostVm;
import com.yas.location.viewmodel.country.CountryVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CountryController.class)
@ContextConfiguration(classes = LocationApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class CountryControllerTest {

    @MockBean
    private CountryService countryService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testCreateCountry_whenRequestIsValid_thenReturnOk() throws Exception {
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .id("id")
            .code2("123")
            .name("name")
            .build();

        String request = objectWriter.writeValueAsString(countryPostVm);
        given(countryService.create(countryPostVm)).willReturn(new Country());

        this.mockMvc.perform(post(Constants.ApiConstant.COUNTRIES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated());
    }

    @Test
    void testCreateCountry_whenCodeIsOverMaxLength_thenReturnBadRequest() throws Exception {
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .id("id")
            .code2("1234")
            .name("name")
            .build();

        String request = objectWriter.writeValueAsString(countryPostVm);

        this.mockMvc.perform(post(Constants.ApiConstant.COUNTRIES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateCountry_whenIdIsBlank_thenReturnBadRequest() throws Exception {
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .id("")
            .code2("123")
            .name("name")
            .build();

        String request = objectWriter.writeValueAsString(countryPostVm);

        this.mockMvc.perform(post(Constants.ApiConstant.COUNTRIES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCountry_whenRequestIsValid_thenReturnOk() throws Exception {
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .id("id")
            .code2("123")
            .name("name")
            .build();

        String request = objectWriter.writeValueAsString(countryPostVm);

        this.mockMvc.perform(put(Constants.ApiConstant.COUNTRIES_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateCountry_whenCodeIsOverMaxLength_thenReturnBadRequest() throws Exception {
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .id("id")
            .code2("1234")
            .name("name")
            .build();

        String request = objectWriter.writeValueAsString(countryPostVm);

        this.mockMvc.perform(put(Constants.ApiConstant.COUNTRIES_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCountry_whenIdIsBlank_thenReturnBadRequest() throws Exception {
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .id("")
            .code2("123")
            .name("name")
            .build();

        String request = objectWriter.writeValueAsString(countryPostVm);

        this.mockMvc.perform(put(Constants.ApiConstant.COUNTRIES_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCountry_WithValidId_Success() throws Exception {
        CountryVm countryVm = new CountryVm(1L, "US", "United States", "USA", true, true, true, true, false);

        given(countryService.findById(1L)).willReturn(countryVm);

        this.mockMvc.perform(get(Constants.ApiConstant.COUNTRIES_URL + "/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("United States"));
    }

    @Test
    void testListCountries_Success() throws Exception {
        CountryVm country1 = new CountryVm(1L, "US", "United States", "USA", true, true, true, true, false);
        CountryVm country2 = new CountryVm(2L, "CA", "Canada", "CAN", true, true, false, true, false);

        given(countryService.findAllCountries()).willReturn(List.of(country1, country2));

        this.mockMvc.perform(get(Constants.ApiConstant.COUNTRIES_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void testDeleteCountry_WithValidId_Success() throws Exception {
        doNothing().when(countryService).delete(1L);

        this.mockMvc.perform(delete(Constants.ApiConstant.COUNTRIES_URL + "/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testGetCountries_Pagination_Success() throws Exception {
        List<CountryVm> countries = List.of(
            new CountryVm(1L, "US", "United States", "USA", true, true, true, true, false)
        );
        CountryListGetVm result = new CountryListGetVm(countries, 0, 10, 1, 1, true);

        given(countryService.getPageableCountries(0, 10)).willReturn(result);

        this.mockMvc.perform(get(Constants.ApiConstant.COUNTRIES_URL + "/paging?pageNo=0&pageSize=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageNo").value(0));
    }

    @Test
    void testCreateCountry_WithAllFields_Success() throws Exception {
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .id("id1")
            .code2("XX")
            .name("New Country")
            .code3("XXX")
            .isBillingEnabled(true)
            .isShippingEnabled(true)
            .isCityEnabled(false)
            .isZipCodeEnabled(true)
            .isDistrictEnabled(false)
            .build();

        Country country = Country.builder()
            .id(1L)
            .code2("XX")
            .name("New Country")
            .build();

        given(countryService.create(countryPostVm)).willReturn(country);

        String request = objectWriter.writeValueAsString(countryPostVm);

        this.mockMvc.perform(post(Constants.ApiConstant.COUNTRIES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated());
    }

    @Test
    void testUpdateCountry_WithAllFields_Success() throws Exception {
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .id("id1")
            .code2("YY")
            .name("Updated Country")
            .code3("YYY")
            .isBillingEnabled(false)
            .isShippingEnabled(false)
            .isCityEnabled(true)
            .isZipCodeEnabled(false)
            .isDistrictEnabled(true)
            .build();

        String request = objectWriter.writeValueAsString(countryPostVm);

        this.mockMvc.perform(put(Constants.ApiConstant.COUNTRIES_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isNoContent());
    }
}
