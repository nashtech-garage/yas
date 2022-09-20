package com.yas.customer.service;

import com.yas.customer.config.KeycloakPropsConfig;
import com.yas.customer.exception.AccessDeniedException;
import com.yas.customer.exception.NotFoundException;
import com.yas.customer.exception.WrongEmailFormatException;
import com.yas.customer.viewmodel.CustomerAdminVm;
import com.yas.customer.viewmodel.CustomerVm;
import org.apache.commons.validator.routines.EmailValidator;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Service;

import javax.ws.rs.ForbiddenException;
import java.util.List;

@Service
public class CustomerService {

    private final Keycloak keycloak;

    private final KeycloakPropsConfig keycloakPropsConfig;
    private final String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public CustomerService(Keycloak keycloak, KeycloakPropsConfig keycloakPropsConfig) {
        this.keycloak = keycloak;
        this.keycloakPropsConfig = keycloakPropsConfig;
    }

    public List<CustomerAdminVm> getCustomers() {
        try {
            return keycloak.realm(keycloakPropsConfig.getRealm()).users().list().stream()
                    .map(CustomerAdminVm::fromUserRepresentation)
                    .toList();
        } catch (ForbiddenException exception) {
            throw new AccessDeniedException(exception.getMessage() + String.format(": Client %s don't have access right for this resource", keycloakPropsConfig.getResource()));
        }
    }

    public CustomerAdminVm getCustomerByEmail(String email) {
        try {
            System.out.println(EmailValidator.getInstance().isValid(email));
            if (EmailValidator.getInstance().isValid(email)) {
                if (keycloak.realm(keycloakPropsConfig.getRealm()).users().search(email).isEmpty()) {
                    throw new NotFoundException(String.format("User with email %s not found", email));
                }
                return CustomerAdminVm.fromUserRepresentation(keycloak.realm(keycloakPropsConfig.getRealm()).users().search(email).get(0));
            } else {
                throw new WrongEmailFormatException(String.format("Wrong email format for %s", email));
            }
        } catch (ForbiddenException exception) {
            throw new AccessDeniedException(exception.getMessage() + String.format(": Client %s don't have access right for this resource", keycloakPropsConfig.getResource()));
        }
    }

    public CustomerVm getCustomerProfile(String userId) {
        try {
            return CustomerVm.fromUserRepresentation(keycloak.realm(keycloakPropsConfig.getRealm()).users().get(userId).toRepresentation());

        } catch (ForbiddenException exception) {
            throw new AccessDeniedException(exception.getMessage() + String.format(": Client %s don't have access right for this resource", keycloakPropsConfig.getResource()));
        }
    }
}
