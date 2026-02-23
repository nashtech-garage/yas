package com.yas.customer.viewmodel;

import com.yas.customer.viewmodel.customer.CustomerVm;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerVmTest {

    @Test
    void testFromUserRepresentation_shouldMapAllFields() {
        // Given
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername("customeruser");
        userRep.setEmail("customer@example.com");
        userRep.setFirstName("Jane");
        userRep.setLastName("Smith");

        // When
        CustomerVm result = CustomerVm.fromUserRepresentation(userRep);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("customeruser");
        assertThat(result.email()).isEqualTo("customer@example.com");
        assertThat(result.firstName()).isEqualTo("Jane");
        assertThat(result.lastName()).isEqualTo("Smith");
    }

    @Test
    void testFromUserRepresentation_withNullValues_shouldHandleGracefully() {
        // Given
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername("minimaluser");
        userRep.setEmail(null);
        userRep.setFirstName(null);
        userRep.setLastName(null);

        // When
        CustomerVm result = CustomerVm.fromUserRepresentation(userRep);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("minimaluser");
        assertThat(result.email()).isNull();
        assertThat(result.firstName()).isNull();
        assertThat(result.lastName()).isNull();
    }

    @Test
    void testFromUserRepresentation_withEmptyStrings_shouldMapCorrectly() {
        // Given
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername("");
        userRep.setEmail("");
        userRep.setFirstName("");
        userRep.setLastName("");

        // When
        CustomerVm result = CustomerVm.fromUserRepresentation(userRep);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEmpty();
        assertThat(result.email()).isEmpty();
        assertThat(result.firstName()).isEmpty();
        assertThat(result.lastName()).isEmpty();
    }
}
