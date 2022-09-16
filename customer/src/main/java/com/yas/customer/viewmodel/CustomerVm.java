package com.yas.customer.viewmodel;

import org.keycloak.representations.idm.UserRepresentation;

public record CustomerVm(String username, String email, String firstName, String lastName) {
    public static CustomerVm fromUserRepresentation(UserRepresentation userRepresentation) {
        return new CustomerVm(userRepresentation.getUsername(), userRepresentation.getEmail(),
                userRepresentation.getFirstName(), userRepresentation.getLastName());
    }
}
