package com.yas.location.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.location.LocationApplication;
import com.yas.location.service.DistrictService;
import com.yas.location.viewmodel.district.DistrictGetVm;
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
@WebMvcTest(controllers = DistrictStorefrontController.class)
@ContextConfiguration(classes = LocationApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class DistrictStorefrontControllerTest {

    @MockBean
    private DistrictService districtService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetList_WithValidId_Success() throws Exception {
        DistrictGetVm district1 = new DistrictGetVm(1L, "Los Angeles County");
        DistrictGetVm district2 = new DistrictGetVm(2L, "San Francisco County");

        given(districtService.getList(1L)).willReturn(List.of(district1, district2));

        mockMvc.perform(get("/storefront/district/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Los Angeles County"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("San Francisco County"));
    }

    @Test
    void testGetList_WithEmptyResult_Success() throws Exception {
        given(districtService.getList(1L)).willReturn(List.of());

        mockMvc.perform(get("/storefront/district/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetList_WithSingleDistrict_Success() throws Exception {
        DistrictGetVm district = new DistrictGetVm(1L, "Test District");

        given(districtService.getList(1L)).willReturn(List.of(district));

        mockMvc.perform(get("/storefront/district/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Test District"));
    }

    @Test
    void testGetList_WithBackofficeEndpoint_Success() throws Exception {
        DistrictGetVm district = new DistrictGetVm(1L, "Test District");

        given(districtService.getList(1L)).willReturn(List.of(district));

        mockMvc.perform(get("/backoffice/district/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetList_WithMultipleDistricts_Success() throws Exception {
        List<DistrictGetVm> districts = List.of(
            new DistrictGetVm(1L, "District 1"),
            new DistrictGetVm(2L, "District 2"),
            new DistrictGetVm(3L, "District 3")
        );

        given(districtService.getList(1L)).willReturn(districts);

        mockMvc.perform(get("/storefront/district/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testGetList_WithDifferentId_Success() throws Exception {
        DistrictGetVm district = new DistrictGetVm(5L, "Different District");

        given(districtService.getList(2L)).willReturn(List.of(district));

        mockMvc.perform(get("/storefront/district/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(5));
    }
}
