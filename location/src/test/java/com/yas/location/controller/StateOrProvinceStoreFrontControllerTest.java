package com.yas.location.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.location.LocationApplication;
import com.yas.location.service.StateOrProvinceService;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
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
@WebMvcTest(controllers = StateOrProvinceStoreFrontController.class)
@ContextConfiguration(classes = LocationApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class StateOrProvinceStoreFrontControllerTest {

    @MockBean
    private StateOrProvinceService stateOrProvinceService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetStateOrProvince_WithValidCountryId_Success() throws Exception {
        StateOrProvinceVm state1 = new StateOrProvinceVm(1L, "California", "CA", "State", 1L);
        StateOrProvinceVm state2 = new StateOrProvinceVm(2L, "Texas", "TX", "State", 1L);

        given(stateOrProvinceService.getAllByCountryId(1L)).willReturn(List.of(state1, state2));

        mockMvc.perform(get("/storefront/state-or-provinces/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("California"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Texas"));
    }

    @Test
    void testGetStateOrProvince_WithEmptyList_Success() throws Exception {
        given(stateOrProvinceService.getAllByCountryId(1L)).willReturn(List.of());

        mockMvc.perform(get("/storefront/state-or-provinces/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetStateOrProvince_WithSingleState_Success() throws Exception {
        StateOrProvinceVm state = new StateOrProvinceVm(1L, "New York", "NY", "State", 1L);

        given(stateOrProvinceService.getAllByCountryId(1L)).willReturn(List.of(state));

        mockMvc.perform(get("/storefront/state-or-provinces/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("NY"));
    }

    @Test
    void testGetStateOrProvince_WithMultipleStates_Success() throws Exception {
        List<StateOrProvinceVm> states = List.of(
            new StateOrProvinceVm(1L, "State1", "S1", "State", 1L),
            new StateOrProvinceVm(2L, "State2", "S2", "State", 1L),
            new StateOrProvinceVm(3L, "State3", "S3", "State", 1L),
            new StateOrProvinceVm(4L, "State4", "S4", "State", 1L)
        );

        given(stateOrProvinceService.getAllByCountryId(1L)).willReturn(states);

        mockMvc.perform(get("/storefront/state-or-provinces/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void testGetStateOrProvince_WithDifferentCountryId_Success() throws Exception {
        StateOrProvinceVm state = new StateOrProvinceVm(1L, "Ontario", "ON", "Province", 2L);

        given(stateOrProvinceService.getAllByCountryId(2L)).willReturn(List.of(state));

        mockMvc.perform(get("/storefront/state-or-provinces/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].countryId").value(2));
    }
}
