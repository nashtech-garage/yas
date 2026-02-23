package com.yas.customer.viewmodel;

import com.yas.customer.viewmodel.customer.CustomerAdminVm;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerAdminVmTest {

    @Test
    void testFromUserRepresentation_shouldMapAllFields() {
        // Given
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("user-123");
        userRep.setUsername("testuser");
        userRep.setEmail("test@example.com");
        userRep.setFirstName("John");
        userRep.setLastName("Doe");
        userRep.setCreatedTimestamp(1609459200000L); // Jan 1, 2021

        // When
        CustomerAdminVm result = CustomerAdminVm.fromUserRepresentation(userRep);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("user-123");
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.createdTimestamp()).isNotNull();
        assertThat(result.createdTimestamp().getYear()).isEqualTo(2021);
        assertThat(result.createdTimestamp().getMonthValue()).isEqualTo(1);
        assertThat(result.createdTimestamp().getDayOfMonth()).isEqualTo(1);
    }

    @Test
    void testFromUserRepresentation_withNullValues_shouldHandleGracefully() {
        // Given
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId("user-456");
        userRep.setUsername("nulluser");
        userRep.setEmail(null);
        userRep.setFirstName(null);
        userRep.setLastName(null);
        userRep.setCreatedTimestamp(null);

        // When
        CustomerAdminVm result = CustomerAdminVm.fromUserRepresentation(userRep);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("user-456");
        assertThat(result.username()).isEqualTo("nulluser");
        assertThat(result.email()).isNull();
        assertThat(result.firstName()).isNull();
        assertThat(result.lastName()).isNull();
        assertThat(result.createdTimestamp()).isNull();
    }
}
