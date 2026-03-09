package com.yas.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import com.yas.customer.service.UserAddressService;
import com.yas.customer.viewmodel.address.ActiveAddressVm;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.useraddress.UserAddressVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = UserAddressController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class UserAddressControllerTest {

    private static final String USER_ADDRESS_BASE_URL = "/storefront/user-address";

    @MockitoBean
    private UserAddressService userAddressService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testGetUserAddresses_whenNormalCase_responseActiveAddressVms() throws Exception {

        List<ActiveAddressVm> activeAddressVmList = getActiveAddressVms();
        when(userAddressService.getUserAddressList()).thenReturn(activeAddressVmList);

        mockMvc.perform(MockMvcRequestBuilders.get(USER_ADDRESS_BASE_URL)
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(activeAddressVmList)));
    }

    @Test
    void testGetDefaultAddress_whenNormalCase_responseAddressDetailVm() throws Exception {

        AddressDetailVm addressDetailVm = getAddressDetailVm();
        when(userAddressService.getAddressDefault()).thenReturn(addressDetailVm);

        mockMvc.perform(MockMvcRequestBuilders.get(USER_ADDRESS_BASE_URL + "/default-address")
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(addressDetailVm)));
    }

    @Test
    void testCreateAddress_whenNormalCase_responseUserAddressVm() throws Exception {
        UserAddressVm userAddressVm = getUserAddressVm();
        when(userAddressService.createAddress(any(AddressPostVm.class))).thenReturn(userAddressVm);

        mockMvc.perform(MockMvcRequestBuilders.post(USER_ADDRESS_BASE_URL)
                .contentType("application/json")
            .content(objectWriter.writeValueAsString(userAddressVm)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(userAddressVm)));
    }

    @Test
    void testDeleteAddress_whenNormalCase_responseOk() throws Exception {
        Long id = 1L;
        doNothing().when(userAddressService).deleteAddress(id);

        mockMvc.perform(MockMvcRequestBuilders.delete(USER_ADDRESS_BASE_URL + "/{id}", id)
            .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testChooseDefaultAddress_whenNormalCase_responseOk() throws Exception {
        Long id = 1L;
        doNothing().when(userAddressService).chooseDefaultAddress(id);

        mockMvc.perform(MockMvcRequestBuilders.put(USER_ADDRESS_BASE_URL + "/{id}", id)
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private List<ActiveAddressVm> getActiveAddressVms() {
        return List.of(
            new ActiveAddressVm(
                1L,
                "John Doe",
                "123-456-7890",
                "123 Elm Street",
                "Springfield",
                "12345",
                101L,
                "District A",
                10L,
                "State A",
                1L,
                "Country A",
                true
            ),
            new ActiveAddressVm(
                2L,
                "Jane Smith",
                "987-654-3210",
                "456 Oak Avenue",
                "Springfield",
                "67890",
                102L,
                "District B",
                11L,
                "State B",
                2L,
                "Country B",
                false
            )
        );

    }


    private AddressDetailVm getAddressDetailVm() {
        return AddressDetailVm.builder()
                .id(102L)
                .contactName("John Doe")
                .phone("123-456-7890")
                .addressLine1("123 Elm Street")
                .city("Springfield")
                .zipCode("12345")
                .districtId(1L)
                .districtName("District A")
                .stateOrProvinceId(1L)
                .stateOrProvinceName("State A")
                .countryId(1L)
                .countryName("Country A")
                .build();
    }

    private UserAddressVm getUserAddressVm() {

        AddressVm address = AddressVm.builder()
            .id(1L)
            .contactName("John Doe")
            .phone("123-456-7890")
            .addressLine1("123 Elm Street")
            .city("Springfield")
            .zipCode("12345")
            .districtId(101L)
            .stateOrProvinceId(10L)
            .countryId(1L)
            .build();

        return new UserAddressVm(
            1L,
            "user123",
            address,
            true
        );
    }
}