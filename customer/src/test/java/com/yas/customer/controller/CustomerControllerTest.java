package com.yas.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yas.customer.CustomerApplication;
import com.yas.customer.service.CustomerService;
import com.yas.customer.util.SecurityContextUtils;
import com.yas.customer.viewmodel.customer.CustomerAdminVm;
import com.yas.customer.viewmodel.customer.CustomerListVm;
import com.yas.customer.viewmodel.customer.CustomerPostVm;
import com.yas.customer.viewmodel.customer.CustomerProfileRequestVm;
import com.yas.customer.viewmodel.customer.CustomerVm;
import com.yas.customer.viewmodel.customer.GuestUserVm;
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
@WebMvcTest(controllers = CustomerController.class)
@ContextConfiguration(classes = CustomerApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    private static final String BACK_OFFICE_CUSTOMER_BASE_URL = "/backoffice/customers";

    private static final String STORE_FRONT_CUSTOMER_BASE_URL = "/storefront/customer";

    @MockBean
    private CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testGetCustomers_whenNormalCase_responseCustomerListVm() throws Exception {

        CustomerListVm customerListVm = new CustomerListVm(
            2,
            null,
            1
        );
        when(customerService.getCustomers(anyInt())).thenReturn(customerListVm);

        mockMvc.perform(MockMvcRequestBuilders.get(BACK_OFFICE_CUSTOMER_BASE_URL)
                .param("pageNo", "0")
            .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(customerListVm)));

    }

    @Test
    void testGetCustomerByEmail_whenNormalCase_responseCustomerAdminVm() throws Exception {

        CustomerAdminVm customerAdminVm = new CustomerAdminVm(
            "12345",
            "john_doe",
            "john.doe@example.com",
            "John",
            "Doe",
            null
        );
        when(customerService.getCustomerByEmail("test@gmail.com")).thenReturn(customerAdminVm);

        mockMvc.perform(MockMvcRequestBuilders.get(
                    BACK_OFFICE_CUSTOMER_BASE_URL + "/{email}", "test@gmail.com")
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(customerAdminVm)));

    }

    @Test
    void testGetCustomerProfile_whenNormalCase_responseCustomerVm() throws Exception {

        SecurityContextUtils.setUpSecurityContext("test");
        CustomerVm customerVm = new CustomerVm(
            "12345",
            "john_doe",
            "john.doe@example.com",
            "John",
            "Doe"
        );
        when(customerService.getCustomerProfile("test")).thenReturn(customerVm);

        mockMvc.perform(MockMvcRequestBuilders.get(
                    STORE_FRONT_CUSTOMER_BASE_URL + "/profile")
                .accept("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(customerVm)));

    }

    @Test
    void testCreateGuestUser_whenNormalCase_responseGuestUserVm() throws Exception {

        GuestUserVm guestUserVm = new GuestUserVm(
            "guest123",
            "guest@example.com",
            "securepassword"
        );
        when(customerService.createGuestUser()).thenReturn(guestUserVm);

        mockMvc.perform(MockMvcRequestBuilders.post(STORE_FRONT_CUSTOMER_BASE_URL + "/guest-user")
                .contentType("application/json"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(objectWriter.writeValueAsString(guestUserVm)));
    }

    @Test
    void testUpdateProfile_whenNormalCase_methodSuccess() throws Exception {
        CustomerProfileRequestVm customerProfileRequestVm = new CustomerProfileRequestVm(
            "John",
            "Doe",
            "john.doe@example.com"
        );
        doNothing().when(customerService).updateCustomer(anyString(), any());

        mockMvc.perform(MockMvcRequestBuilders.put(BACK_OFFICE_CUSTOMER_BASE_URL + "/profile" + "/test")
                .contentType("application/json")
                .content(objectWriter.writeValueAsString(customerProfileRequestVm)))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void testCreateCustomer_whenNormalCase_methodSuccess() throws Exception {
        CustomerPostVm customerPostVm = new CustomerPostVm("user1", "test@gmail.com", "John",
            "Doe", "123", "ADMIN");

        when(customerService.create(any(CustomerPostVm.class))).thenReturn(mock(CustomerVm.class));

        mockMvc.perform(MockMvcRequestBuilders.post(BACK_OFFICE_CUSTOMER_BASE_URL)
                .contentType("application/json")
                .content(objectWriter.writeValueAsString(customerPostVm)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void testDeleteCustomer_whenNormalCase_methodSuccess() throws Exception {
        doNothing().when(customerService).deleteCustomer(anyString());

        mockMvc.perform(MockMvcRequestBuilders.delete(BACK_OFFICE_CUSTOMER_BASE_URL + "/profile" + "/test")
                .contentType("application/json"))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}