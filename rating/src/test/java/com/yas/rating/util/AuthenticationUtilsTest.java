package com.yas.rating.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.rating.utils.AuthenticationUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.util.List;

class AuthenticationUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testExtractUserId_whenJwtAuthentication_returnSubject() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user-123");

        JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.setContext(new SecurityContextImpl(jwtAuthToken));

        String userId = AuthenticationUtils.extractUserId();
        assertThat(userId).isEqualTo("user-123");
    }

    @Test
    void testExtractUserId_whenAnonymousAuthentication_throwAccessDeniedException() {
        AnonymousAuthenticationToken anonymousAuth = new AnonymousAuthenticationToken(
            "key", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );
        SecurityContextHolder.setContext(new SecurityContextImpl(anonymousAuth));

        assertThatThrownBy(AuthenticationUtils::extractUserId)
            .isInstanceOf(AccessDeniedException.class)
            .hasMessage("ACCESS_DENIED");
    }
}
