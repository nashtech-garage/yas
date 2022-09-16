package com.yas.customer.service;

import com.yas.customer.config.KeycloakPropsConfig;
import com.yas.customer.exception.AccessDeniedException;
import com.yas.customer.viewmodel.CustomerVm;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Service;

import javax.ws.rs.ForbiddenException;
import java.util.List;

@Service
public class CustomerService {

    private final Keycloak keycloak;

    private final KeycloakPropsConfig keycloakPropsConfig;

    public CustomerService(Keycloak keycloak, KeycloakPropsConfig keycloakPropsConfig) {
        this.keycloak = keycloak;
        this.keycloakPropsConfig = keycloakPropsConfig;
    }

    public List<CustomerVm> getCustomers() {
        try {
            return keycloak.realm(keycloakPropsConfig.getRealm()).users().list().stream()
                    .map(CustomerVm::fromUserRepresentation)
                    .toList();
        } catch (ForbiddenException Exception) {
            throw new AccessDeniedException(Exception.getMessage() + String.format(": Client %s don't have access right for this resource", keycloakPropsConfig.getResource()));
        }
    }
}
