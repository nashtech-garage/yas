package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.utils.KeycloakUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KeycloakUtilsTest {
    @Mock
    private Keycloak keycloak;

    @Mock
    private TokenManager tokenManager;

    @InjectMocks
    private KeycloakUtils keycloakUtils;


    @BeforeEach
    public void setUp() {
        keycloakUtils.init();

        // Mock the tokenManager to return the mocked TokenManager
        when(keycloak.tokenManager()).thenReturn(tokenManager);
    }

    @Test
    public void testGetAccessToken() {
        String mockAccessToken = "mock-access-token";
        when(tokenManager.getAccessTokenString()).thenReturn(mockAccessToken);

        String accessToken = KeycloakUtils.getAccessToken();

        assertEquals(mockAccessToken, accessToken);
    }
}
