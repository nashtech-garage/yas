package com.yas.customer.service;

import com.yas.customer.config.KeycloakPropsConfig;
import com.yas.customer.exception.AccessDeniedException;
import com.yas.customer.exception.CreateGuestUserException;
import com.yas.customer.exception.NotFoundException;
import com.yas.customer.exception.WrongEmailFormatException;
import com.yas.customer.viewmodel.CustomerAdminVm;
import com.yas.customer.viewmodel.CustomerListVm;
import com.yas.customer.viewmodel.CustomerVm;
import com.yas.customer.viewmodel.GuestUserVm;
import org.apache.commons.validator.routines.EmailValidator;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class CustomerService {

    private final Keycloak keycloak;
    private final KeycloakPropsConfig keycloakPropsConfig;
    private static final String ERROR_FORMAT = "%s: Client %s don't have access right for this resource";
    private static final int USER_PER_PAGE = 2;

    public CustomerService(Keycloak keycloak, KeycloakPropsConfig keycloakPropsConfig) {
        this.keycloak = keycloak;
        this.keycloakPropsConfig = keycloakPropsConfig;
    }

    public CustomerListVm getCustomers(int pageNo) {
        try {
            List<CustomerAdminVm> result = keycloak.realm(keycloakPropsConfig.getRealm()).users()
                    .search(null, pageNo * USER_PER_PAGE, USER_PER_PAGE).stream()
                    .map(CustomerAdminVm::fromUserRepresentation)
                    .toList();
            int totalUser = keycloak.realm(keycloakPropsConfig.getRealm()).users().count();

            return new CustomerListVm(totalUser, result, totalUser / USER_PER_PAGE);
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
            // Get realm
            RealmResource realmResource = keycloak.realm(keycloakPropsConfig.getRealm());
            String randomGuestName = generateSafeString();
            String guestUserEmail = randomGuestName + "_guest@yas.com";
            CredentialRepresentation credential = createPasswordCredentials("GUEST");

            // Define user
            UserRepresentation user = new UserRepresentation();
            user.setUsername(guestUserEmail);
            user.setFirstName("GUEST");
            user.setLastName(randomGuestName);
            user.setEmail(guestUserEmail);
            user.setCredentials(Collections.singletonList(credential));
            user.setEnabled(true);
            Response response = realmResource.users().create(user);

            // get new user
            String userId = CreatedResponseUtil.getCreatedId(response);
            UserResource userResource = realmResource.users().get(userId);
            RoleRepresentation guestRealmRole = realmResource.roles().get("GUEST").toRepresentation();

            // Assign realm role GUEST to user
            userResource.roles().realmLevel().add(Collections.singletonList(guestRealmRole));

            return new GuestUserVm(userId, guestUserEmail, "GUEST");
    }

    public static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    private String generateSafeString() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[12];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
}
