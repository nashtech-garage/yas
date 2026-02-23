package com.yas.customer.viewmodel;

import com.yas.customer.viewmodel.customer.CustomerAdminVm;
import com.yas.customer.viewmodel.customer.CustomerListVm;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerListVmTest {

    @Test
    void testConstructor_shouldCreateInstance() {
        // Given
        CustomerAdminVm customer1 = new CustomerAdminVm("1", "user1", "user1@test.com",
                "John", "Doe", LocalDateTime.of(2021, 1, 1, 0, 0));
        CustomerAdminVm customer2 = new CustomerAdminVm("2", "user2", "user2@test.com",
                "Jane", "Smith", LocalDateTime.of(2021, 1, 1, 0, 0));
        List<CustomerAdminVm> customers = Arrays.asList(customer1, customer2);

        // When
        CustomerListVm customerListVm = new CustomerListVm(2, customers, 1);

        // Then
        assertThat(customerListVm).isNotNull();
        assertThat(customerListVm.totalUser()).isEqualTo(2);
        assertThat(customerListVm.customers()).hasSize(2);
        assertThat(customerListVm.totalPage()).isEqualTo(1);
    }

    @Test
    void testConstructor_withEmptyList_shouldWork() {
        // When
        CustomerListVm customerListVm = new CustomerListVm(0, List.of(), 0);

        // Then
        assertThat(customerListVm).isNotNull();
        assertThat(customerListVm.totalUser()).isZero();
        assertThat(customerListVm.customers()).isEmpty();
        assertThat(customerListVm.totalPage()).isZero();
    }

    @Test
    void testConstructor_withMultiplePages_shouldWork() {
        // Given
        CustomerAdminVm customer1 = new CustomerAdminVm("1", "user1", "user1@test.com",
                "John", "Doe", LocalDateTime.of(2021, 1, 1, 0, 0));
        List<CustomerAdminVm> customers = List.of(customer1);

        // When
        CustomerListVm customerListVm = new CustomerListVm(25, customers, 3);

        // Then
        assertThat(customerListVm.totalUser()).isEqualTo(25);
        assertThat(customerListVm.totalPage()).isEqualTo(3);
        assertThat(customerListVm.customers()).hasSize(1);
    }
}
