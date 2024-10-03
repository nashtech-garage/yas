package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.model.CartItem;
import com.yas.order.model.CartItemId;
import com.yas.order.repository.CartItemRepository;
import com.yas.order.viewmodel.cart.CartItemPostVm;
import com.yas.order.viewmodel.product.ProductThumbnailGetVm;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {
        Mockito.reset(cartItemRepository, productService);
    }

    @Nested
    class AddToCartTest {
        private CartItemPostVm.CartItemPostVmBuilder cartItemPostVmBuilder;

        @BeforeEach
        void setUp() {
            cartItemPostVmBuilder = CartItemPostVm.builder()
                .productId(1L)
                .quantity(1);
        }

        @Test
        void testAddCartItem_whenProductNotFound_thenThrowBadRequestException() {
            cartItemPostVmBuilder.productId(-1L);
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();
            when(productService.getProductById(cartItemPostVm.productId()))
                .thenThrow(new NotFoundException(anyString()));
            assertThrows(BadRequestException.class, () -> cartItemService.addCartItem(cartItemPostVm));
        }

        @Test
        void testAddCartItem_whenCartItemExists_thenUpdateQuantity() {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();
            String currentUserId = "customerId";
            CartItem existingCartItem = CartItem
                .builder()
                .customerId(currentUserId)
                .productId(cartItemPostVm.productId())
                .quantity(1)
                .build();
            int expectedQuantity = existingCartItem.getQuantity() + cartItemPostVm.quantity();

            mockCurrentUserId(currentUserId);
            when(productService.getProductById(cartItemPostVm.productId()))
                .thenReturn(mock(ProductThumbnailGetVm.class));
            when(cartItemRepository.findById(any())).thenReturn(Optional.of(existingCartItem));

            cartItemService.addCartItem(cartItemPostVm);

            verify(cartItemRepository).save(any());
            assertEquals(expectedQuantity, existingCartItem.getQuantity());
        }

        @Test
        void testAddCartItem_whenCartItemDoesNotExist_thenCreateCartItem() {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();
            String currentUserId = "customerId";

            mockCurrentUserId(currentUserId);
            when(productService.getProductById(cartItemPostVm.productId()))
                .thenReturn(mock(ProductThumbnailGetVm.class));
            when(cartItemRepository.findById(any(CartItemId.class))).thenReturn(java.util.Optional.empty());

            cartItemService.addCartItem(cartItemPostVm);

            ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
            verify(cartItemRepository).save(cartItemCaptor.capture());
            CartItem savedCartItem = cartItemCaptor.getValue();

            assertEquals(currentUserId, savedCartItem.getCustomerId());
            assertEquals(cartItemPostVm.productId(), savedCartItem.getProductId());
            assertEquals(cartItemPostVm.quantity(), savedCartItem.getQuantity());
        }
    }

    private void mockCurrentUserId(String userIdToMock) {
        Jwt jwt = mock(Jwt.class);
        JwtAuthenticationToken jwtToken = new JwtAuthenticationToken(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(jwtToken);

        when(jwt.getSubject()).thenReturn(userIdToMock);
        SecurityContextHolder.setContext(securityContext);
    }

}
