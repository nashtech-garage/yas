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
import com.yas.location.service.AddressService;
import com.yas.location.viewmodel.address.AddressDetailVm;
import com.yas.location.viewmodel.address.AddressGetVm;
import com.yas.location.viewmodel.address.AddressPostVm;
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

@WebMvcTest(controllers = AddressController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class AddressControllerTest {

    @MockitoBean
    private AddressService addressService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testGetAddressById_thenReturnOk() throws Exception {
        given(addressService.getAddress(1L)).willReturn(new AddressDetailVm(1L, "name", "phone", "line1", "line2", "city", "zip", 1L, "dist", 1L, "state", 1L, "country"));

        this.mockMvc.perform(get("/storefront/addresses/1"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetAddressList_thenReturnOk() throws Exception {
        given(addressService.getAddressList(List.of(1L, 2L))).willReturn(List.of());

        this.mockMvc.perform(get("/storefront/addresses")
                .param("ids", "1,2"))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteAddress_thenReturnOk() throws Exception {
        this.mockMvc.perform(delete("/storefront/addresses/1"))
            .andExpect(status().isOk());
        verify(addressService).deleteAddress(1L);
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