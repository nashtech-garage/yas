package com.yas.locacation.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.location.LocationApplication;
import com.yas.location.controller.CountryController;
import com.yas.location.model.Country;
import com.yas.location.service.CountryService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.country.CountryPostVm;
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

}
