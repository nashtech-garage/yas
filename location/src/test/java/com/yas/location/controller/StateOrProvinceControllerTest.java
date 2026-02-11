package com.yas.location.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import com.yas.location.model.Country;
import com.yas.location.model.StateOrProvince;
import com.yas.location.service.StateOrProvinceService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.stateorprovince.StateOrProvincePostVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = StateOrProvinceController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class StateOrProvinceControllerTest {

    @MockitoBean
    private StateOrProvinceService stateOrProvinceService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testCreateStateOrProvince_whenRequestIsValid_thenReturnOk() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("name")
            .code("code")
            .type("type")
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);
        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(1L)
            .country(Country.builder().id(1L).build())
            .build();
        given(stateOrProvinceService.createStateOrProvince(stateOrProvincePostVm)).willReturn(
            stateOrProvince);

        this.mockMvc.perform(post(Constants.ApiConstant.STATE_OR_PROVINCES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated());
    }

    @Test
    void testCreateStateOrProvince_whenCodeIsOverMaxLength_thenReturnBadRequest() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("name")
            .code("1234567890".repeat(26))
            .type("type")
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);

        this.mockMvc.perform(post(Constants.ApiConstant.STATE_OR_PROVINCES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateStateOrProvince_whenNameIsBlank_thenReturnBadRequest() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("")
            .code("code")
            .type("type")
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);

        this.mockMvc.perform(post(Constants.ApiConstant.STATE_OR_PROVINCES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateStateOrProvince_whenRequestIsValid_thenReturnOk() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("name")
            .code("code")
            .type("type")
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);

        this.mockMvc.perform(put(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateStateOrProvince_whenCodeIsOverMaxLength_thenReturnBadRequest() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("name")
            .code("1234567890".repeat(26))
            .type("type")
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);

        this.mockMvc.perform(put(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateStateOrProvince_whenNameIsBlank_thenReturnBadRequest() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("")
            .code("code")
            .type("type")
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);

        this.mockMvc.perform(put(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }
}