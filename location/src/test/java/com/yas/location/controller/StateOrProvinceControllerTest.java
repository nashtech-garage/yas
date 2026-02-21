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
import com.yas.location.model.StateOrProvince;
import com.yas.location.service.StateOrProvinceService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceListGetVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceAndCountryGetNameVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvincePostVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
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

    @Test
    void testGetStateOrProvince_WithValidId_Success() throws Exception {
        StateOrProvinceVm stateOrProvinceVm = new StateOrProvinceVm(1L, "California", "CA", "State", 1L);

        given(stateOrProvinceService.findById(1L)).willReturn(stateOrProvinceVm);

        this.mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("California"));
    }

    @Test
    void testListStateOrProvinces_Success() throws Exception {
        StateOrProvinceVm state1 = new StateOrProvinceVm(1L, "California", "CA", "State", 1L);
        StateOrProvinceVm state2 = new StateOrProvinceVm(2L, "Texas", "TX", "State", 1L);

        given(stateOrProvinceService.getAllByCountryId(1L)).willReturn(List.of(state1, state2));

        this.mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "?countryId=1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void testDeleteStateOrProvince_WithValidId_Success() throws Exception {
        doNothing().when(stateOrProvinceService).delete(1L);

        this.mockMvc.perform(delete(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testGetStateOrProvinceAndCountryNames_Success() throws Exception {
        StateOrProvinceAndCountryGetNameVm vm1 = new StateOrProvinceAndCountryGetNameVm(1L, "California", "United States");
        StateOrProvinceAndCountryGetNameVm vm2 = new StateOrProvinceAndCountryGetNameVm(2L, "Texas", "United States");

        given(stateOrProvinceService.getStateOrProvinceAndCountryNames(List.of(1L, 2L)))
            .willReturn(List.of(vm1, vm2));

        this.mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/state-country-names?stateOrProvinceIds=1,2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].stateOrProvinceId").value(1))
            .andExpect(jsonPath("$[1].stateOrProvinceId").value(2));
    }

    @Test
    void testGetPageableStateOrProvinces_Success() throws Exception {
        List<StateOrProvinceVm> stateList = List.of(
            new StateOrProvinceVm(1L, "State1", "S1", "State", 1L)
        );
        StateOrProvinceListGetVm result = new StateOrProvinceListGetVm(stateList, 0, 10, 1, 1, true);

        given(stateOrProvinceService.getPageableStateOrProvinces(0, 10, 1L)).willReturn(result);

        this.mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/paging?pageNo=0&pageSize=10&countryId=1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageNo").value(0));
    }

    @Test
    void testCreateStateOrProvince_WithAllFields_Success() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("New State")
            .code("NS")
            .type("State")
            .countryId(1L)
            .build();

        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(1L)
            .name("New State")
            .code("NS")
            .type("State")
            .country(Country.builder().id(1L).build())
            .build();

        given(stateOrProvinceService.createStateOrProvince(stateOrProvincePostVm))
            .willReturn(stateOrProvince);

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);

        this.mockMvc.perform(post(Constants.ApiConstant.STATE_OR_PROVINCES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated());
    }
}
