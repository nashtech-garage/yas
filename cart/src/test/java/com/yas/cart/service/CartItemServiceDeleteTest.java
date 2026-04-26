package com.yas.cart.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.cart.mapper.CartItemMapper;
import com.yas.cart.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class CartItemServiceDeleteTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductService productService;

    @Spy
    private CartItemMapper cartItemMapper = new CartItemMapper();

    @InjectMocks
    private CartItemService cartItemService;

    private static final String CURRENT_USER_ID = "user-123";
    private static final Long PRODUCT_ID = 10L;

    private void mockCurrentUserId(String userId) {
        Jwt jwt = mock(Jwt.class);
        JwtAuthenticationToken jwtToken = new JwtAuthenticationToken(jwt);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(jwtToken);
        when(jwt.getSubject()).thenReturn(userId);
        SecurityContextHolder.setContext(securityContext);
    }

    @BeforeEach
    void setUp() {
        mockCurrentUserId(CURRENT_USER_ID);
    }

    @Nested
    class DeleteCartItemTest {

        @Test
        void deleteCartItem_whenValidProductId_thenDeleteSuccessfully() {
            doNothing().when(cartItemRepository)
                .deleteByCustomerIdAndProductId(CURRENT_USER_ID, PRODUCT_ID);

            assertDoesNotThrow(() -> cartItemService.deleteCartItem(PRODUCT_ID));

            verify(cartItemRepository).deleteByCustomerIdAndProductId(CURRENT_USER_ID, PRODUCT_ID);
        }

        @Test
        void deleteCartItem_whenDifferentProductId_thenCallRepositoryWithCorrectArgs() {
            Long otherProductId = 99L;
            doNothing().when(cartItemRepository)
                .deleteByCustomerIdAndProductId(CURRENT_USER_ID, otherProductId);

            cartItemService.deleteCartItem(otherProductId);

            verify(cartItemRepository).deleteByCustomerIdAndProductId(CURRENT_USER_ID, otherProductId);
        }
    }
}
