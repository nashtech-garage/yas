package com.yas.sampledata.controller;

import com.yas.sampledata.service.SampleDataService;
import com.yas.sampledata.viewmodel.SampleDataVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SampleDataControllerTest {

    private MockMvc mockMvc;
    private SampleDataService sampleDataService;

    @BeforeEach
    void setUp() {
        sampleDataService = Mockito.mock(SampleDataService.class);
        SampleDataController sampleDataController = new SampleDataController(sampleDataService);
        mockMvc = MockMvcBuilders.standaloneSetup(sampleDataController).build();
    }

    @Test
    void createSampleData_shouldReturnSuccess() throws Exception {
        // Given
        SampleDataVm response = new SampleDataVm("Success");
        when(sampleDataService.createSampleData()).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/storefront/sampledata")
                        .contentType("application/json")
                        .content("{\"message\":\"test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }
}
