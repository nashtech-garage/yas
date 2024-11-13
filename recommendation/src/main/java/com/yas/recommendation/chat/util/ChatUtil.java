package com.yas.recommendation.chat.util;

import static com.yas.commonlibrary.utils.AuthenticationUtils.getAuthentication;

import com.yas.recommendation.chat.User;
import java.util.Optional;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

public final class ChatUtil {

    private ChatUtil() {}

    public static User getUser() {
        var jwt = getJwt();
        // Authentication is not type of AnonymousAuthenticationToken && principal must be in JWT
        // Currently, not aware of any other type of Jwt.
        if (isAuthenticatedUser() && jwt.isPresent()) {
            return new User(
                (String) jwt.get().getClaims().get("name"),
                jwt.get().getTokenValue(),
                true
            );
        }
        return new User("anonymousUser", "", false);
    }

    private static Optional<Jwt> getJwt() {
        if (getAuthentication().getPrincipal() instanceof Jwt jwt) {
            return Optional.of(jwt);
        }
        return Optional.empty();
    }

    private static boolean isAuthenticatedUser() {
        var authentication = getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication != null;
    }


}
