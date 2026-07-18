package com.yas.location.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.location.model.Country;
import com.yas.location.service.CountryService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.country.CountryListGetVm;
import com.yas.location.viewmodel.country.CountryPostVm;
import com.yas.location.viewmodel.country.CountryVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CountryController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class CountryControllerTest {

    @MockitoBean
    private CountryService countryService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testGetPageableCountries_thenReturnOk() throws Exception {
        given(countryService.getPageableCountries(0, 10)).willReturn(new CountryListGetVm(List.of(), 0, 0, 0, 0, false));

        this.mockMvc.perform(get(Constants.ApiConstant.COUNTRIES_URL + "/paging")
                .param("pageNo", "0")
                .param("pageSize", "10"))
            .andExpect(status().isOk());
    }

    @Test
    void testListCountries_thenReturnOk() throws Exception {
        given(countryService.findAllCountries()).willReturn(List.of());

        this.mockMvc.perform(get(Constants.ApiConstant.COUNTRIES_URL))
            .andExpect(status().isOk());
    }

    @Test
    void testGetCountry_thenReturnOk() throws Exception {
        given(countryService.findById(1L)).willReturn(new CountryVm(1L, "VN", "Vietnam", "VNM", true, true, true, true, true));

        this.mockMvc.perform(get(Constants.ApiConstant.COUNTRIES_URL + "/1"))
            .andExpect(status().isOk());
    }

    @Test
    void testCreateCountry_whenRequestIsValid_thenReturnOk() throws Exception {
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .id("id")
            .code2("123")
            .name("name")
            .build();

        String request = objectWriter.writeValueAsString(countryPostVm);
        Country country = Country.builder().id(1L).build();
        given(countryService.create(countryPostVm)).willReturn(country);

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
    void testDeleteCountry_thenReturnOk() throws Exception {
        this.mockMvc.perform(delete(Constants.ApiConstant.COUNTRIES_URL + "/1"))
            .andExpect(status().isNoContent());
        verify(countryService).delete(1L);
    }
}