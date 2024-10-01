package com.yas.order.utils;

import com.yas.commonlibrary.exception.SignInRequiredException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class AuthenticationUtils {
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new SignInRequiredException(Constants.ErrorCode.SIGN_IN_REQUIRED);
        }

        JwtAuthenticationToken contextHolder = (JwtAuthenticationToken) authentication;

        return contextHolder.getToken().getSubject();
    }
}
