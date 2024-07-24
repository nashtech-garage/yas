package com.yas.locacation.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.location.LocationApplication;
import com.yas.location.controller.StateOrProvinceController;
import com.yas.location.model.Country;
import com.yas.location.model.StateOrProvince;
import com.yas.location.service.StateOrProvinceService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.stateorprovince.StateOrProvincePostVm;
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
@WebMvcTest(controllers = StateOrProvinceController.class)
@ContextConfiguration(classes = LocationApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class StateOrProvinceControllerTest {

  @MockBean
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
