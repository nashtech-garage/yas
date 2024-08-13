package com.yas.locacation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.location.LocationApplication;
import com.yas.location.controller.AddressController;
import com.yas.location.service.AddressService;
import com.yas.location.viewmodel.address.AddressPostVm;
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
@WebMvcTest(controllers = AddressController.class)
@ContextConfiguration(classes = LocationApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class AddressControllerTest {

    @MockBean
    private AddressService addressService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testCreateAddress_whenRequestIsValid_thenReturnOk() throws Exception {
        AddressPostVm addressPostVm = AddressPostVm.builder()
            .contactName("contactName")
            .phone("12345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(addressPostVm);

        this.mockMvc.perform(post("/storefront/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isOk());
    }

    @Test
    void testCreateAddress_whenPhoneIsOverMaxLength_thenReturnBadRequest() throws Exception {
        AddressPostVm addressPostVm = AddressPostVm.builder()
            .contactName("contactName")
            .phone("12345678912345678912345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(addressPostVm);

        this.mockMvc.perform(post("/storefront/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAddress_whenDistrictIsNull_thenReturnBadRequest() throws Exception {
        AddressPostVm addressPostVm = AddressPostVm.builder()
            .contactName("contactName")
            .phone("12345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(null)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(addressPostVm);

        this.mockMvc.perform(post("/storefront/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateAddress_whenRequestIsValid_thenReturnOk() throws Exception {
        AddressPostVm addressPostVm = AddressPostVm.builder()
            .contactName("contactName")
            .phone("12345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(addressPostVm);

        this.mockMvc.perform(put("/storefront/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateAddress_whenPhoneIsOverMaxLength_thenReturnBadRequest() throws Exception {
        AddressPostVm addressPostVm = AddressPostVm.builder()
            .contactName("contactName")
            .phone("12345678912345678912345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(addressPostVm);

        this.mockMvc.perform(put("/storefront/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateAddress_whenDistrictIsNull_thenReturnBadRequest() throws Exception {
        AddressPostVm addressPostVm = AddressPostVm.builder()
            .contactName("contactName")
            .phone("12345678")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .city("city")
            .zipCode("zipCode")
            .districtId(null)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(addressPostVm);

        this.mockMvc.perform(put("/storefront/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }
}
