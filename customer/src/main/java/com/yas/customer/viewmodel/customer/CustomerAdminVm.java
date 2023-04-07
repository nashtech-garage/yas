package com.yas.customer.viewmodel.Customer;

import org.keycloak.representations.idm.UserRepresentation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public record CustomerAdminVm(String id, String username, String email, String firstName, String lastName,
                              LocalDateTime createdTimestamp) {
    public static CustomerAdminVm fromUserRepresentation(UserRepresentation userRepresentation) {
        LocalDateTime createdTimestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp()), TimeZone.getDefault().toZoneId());
        return new CustomerAdminVm(userRepresentation.getId(), userRepresentation.getUsername(),
                userRepresentation.getEmail(), userRepresentation.getFirstName(), userRepresentation.getLastName(), createdTimestamp);
    }
}
