package com.yas.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.customer.CustomerApplication;
import com.yas.customer.service.UserAddressService;
import com.yas.customer.viewmodel.address.ActiveAddressVm;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.useraddress.UserAddressVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserAddressController.class)
@ContextConfiguration(classes = CustomerApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class UserAddressControllerTest {

    private static final String USER_ADDRESS_BASE_URL = "/storefront/user-address";

    @MockBean
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
            ActiveAddressVm.builder()
                .id(1L)
                .contactName("John Doe")
                .phone("123-456-7890")
                .addressLine1("123 Elm Street")
                .city("Springfield")
                .zipCode("12345")
                .districtId(101L)
                .districtName("District A")
                .stateOrProvinceId(10L)
                .stateOrProvinceName("State A")
                .stateOrProvinceCode("ST")
                .countryId(1L)
                .countryName("Country A")
                .countryCode2("CA")
                .countryCode3("CAN")
                .isActive(true)
                .build(),
            ActiveAddressVm.builder()
                .id(2L)
                .contactName("Jane Smith")
                .phone("987-654-3210")
                .addressLine1("456 Oak Avenue")
                .city("Springfield")
                .zipCode("67890")
                .districtId(102L)
                .districtName("District B")
                .stateOrProvinceId(11L)
                .stateOrProvinceName("State B")
                .stateOrProvinceCode("SB")
                .countryId(2L)
                .countryName("Country B")
                .countryCode2("CB")
                .countryCode3("CBB")
                .isActive(false)
                .build()
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