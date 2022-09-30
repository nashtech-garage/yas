package com.yas.customer.service;

import com.yas.customer.config.KeycloakClientConfig;
import com.yas.customer.config.KeycloakPropsConfig;
import com.yas.customer.exception.AccessDeniedException;
import com.yas.customer.exception.InternalErrorException;
import com.yas.customer.exception.NotFoundException;
import com.yas.customer.exception.WrongEmailFormatException;
import com.yas.customer.viewmodel.CustomerAdminVm;
import com.yas.customer.viewmodel.CustomerVm;
import com.yas.customer.viewmodel.GuestUserVm;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.ForbiddenException;
import java.util.Collections;
import java.util.List;

@Service
public class CustomerService {

    private final Keycloak keycloak;
    private  final KeycloakClientConfig keycloakClientConfig;
    private final KeycloakPropsConfig keycloakPropsConfig;
    private static final String ERROR_FORMAT = "%s: Client %s don't have access right for this resource";
    private static final String PASSWORD_HARDCODED = "GUEST";

    public CustomerService(Keycloak keycloak, KeycloakPropsConfig keycloakPropsConfig, KeycloakClientConfig keycloakClientConfig) {
        this.keycloak = keycloak;
        this.keycloakPropsConfig = keycloakPropsConfig;
        this.keycloakClientConfig = keycloakClientConfig;
    }

    public List<CustomerAdminVm> getCustomers() {
        try {
            return keycloak.realm(keycloakPropsConfig.getRealm()).users().list().stream()
                    .map(CustomerAdminVm::fromUserRepresentation)
                    .toList();
        } catch (ForbiddenException exception) {
            throw new AccessDeniedException(String.format(ERROR_FORMAT, exception.getMessage(), keycloakPropsConfig.getResource()));
        }
    }

    public CustomerAdminVm getCustomerByEmail(String email) {
        try {
            if (EmailValidator.getInstance().isValid(email)) {
                List<UserRepresentation> searchResult = keycloak.realm(keycloakPropsConfig.getRealm()).users().search(email, true);
                if (searchResult.isEmpty()) {
                    throw new NotFoundException(String.format("User with email %s not found", email));
                }
                return CustomerAdminVm.fromUserRepresentation(searchResult.get(0));
            } else {
                throw new WrongEmailFormatException(String.format("Wrong email format for %s", email));
            }
        } catch (ForbiddenException exception) {
            throw new AccessDeniedException(String.format(ERROR_FORMAT, exception.getMessage(), keycloakPropsConfig.getResource()));
        }
    }

    public CustomerVm getCustomerProfile(String userId) {
        try {
            return CustomerVm.fromUserRepresentation(keycloak.realm(keycloakPropsConfig.getRealm()).users().get(userId).toRepresentation());

        } catch (ForbiddenException exception) {
            throw new AccessDeniedException(String.format(ERROR_FORMAT, exception.getMessage(), keycloakPropsConfig.getResource()));
        }
    }

    public GuestUserVm createGuestUser() {
        try {
            UsersResource resource = keycloakClientConfig.getAdminKeyCloak().realm(keycloakPropsConfig.getRealm()).users();
            String randomGuestName = RandomStringUtils.randomAlphanumeric(12);
            String guestUserEmail = randomGuestName + "_guest@yas.com";
            CredentialRepresentation credential = createPasswordCredentials(PASSWORD_HARDCODED);

            UserRepresentation user = new UserRepresentation();
            user.setUsername(guestUserEmail);
            user.setFirstName("GUEST");
            user.setLastName(randomGuestName);
            user.setEmail(guestUserEmail);
            user.setCredentials(Collections.singletonList(credential));
            user.setRealmRoles(List.of("GUEST"));
            user.setEnabled(true);

            resource.create(user);
            return new GuestUserVm(guestUserEmail, PASSWORD_HARDCODED);
        } catch (InternalError exception) {
            throw new InternalErrorException(exception.getMessage());
        }
    }

    public static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
}
