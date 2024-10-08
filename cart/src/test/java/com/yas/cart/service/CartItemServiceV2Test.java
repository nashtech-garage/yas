package com.yas.cart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.cart.mapper.CartItemMapperV2;
import com.yas.cart.model.CartItemIdV2;
import com.yas.cart.model.CartItemV2;
import com.yas.cart.repository.CartItemRepositoryV2;
import com.yas.cart.viewmodel.CartItemGetVmV2;
import com.yas.cart.viewmodel.CartItemPostVmV2;
import com.yas.cart.viewmodel.ProductThumbnailVm;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class CartItemServiceV2Test {
    @Mock
    private CartItemRepositoryV2 cartItemRepository;

    @Mock
    private ProductService productService;

    @Spy
    private CartItemMapperV2 cartItemMapper = new CartItemMapperV2();

    @InjectMocks
    private CartItemServiceV2 cartItemService;

    @BeforeEach
    void setUp() {
        Mockito.reset(cartItemRepository, productService);
    }

    private static final String CURRENT_USER_ID_SAMPLE = "userId";

    @Nested
    class AddCartItemTest {
        private CartItemPostVmV2.CartItemPostVmV2Builder cartItemPostVmBuilder;

        @BeforeEach
        void setUp() {
            cartItemPostVmBuilder = CartItemPostVmV2.builder()
                .productId(1L)
                .quantity(1);
        }

        @Test
        void testAddCartItem_whenProductNotFound_shouldThrowBadRequestException() {
            cartItemPostVmBuilder.productId(-1L);
            CartItemPostVmV2 cartItemPostVm = cartItemPostVmBuilder.build();

            when(productService.getProductById(cartItemPostVm.productId()))
                .thenThrow(new NotFoundException(anyString()));

            assertThrows(BadRequestException.class, () -> cartItemService.addCartItem(cartItemPostVm));
        }

        @Test
        void testAddCartItem_whenCartItemExists_shouldUpdateQuantity() {
            CartItemPostVmV2 cartItemPostVm = cartItemPostVmBuilder.build();
            CartItemV2 existingCartItem = CartItemV2
                .builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(cartItemPostVm.productId())
                .quantity(1)
                .build();
            int expectedQuantity = existingCartItem.getQuantity() + cartItemPostVm.quantity();

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(productService.getProductById(cartItemPostVm.productId()))
                .thenReturn(mock(ProductThumbnailVm.class));
            when(cartItemRepository.findByIdWithLock(any())).thenReturn(Optional.of(existingCartItem));
            when(cartItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            CartItemGetVmV2 cartItem = cartItemService.addCartItem(cartItemPostVm);

            verify(cartItemRepository).save(any());
            assertEquals(expectedQuantity, cartItem.quantity());
            assertEquals(CURRENT_USER_ID_SAMPLE, cartItem.customerId());
            assertEquals(cartItemPostVm.productId(), cartItem.productId());
        }

        @Test
        void testAddCartItem_whenCartItemDoesNotExist_shouldCreateCartItem() {
            CartItemPostVmV2 cartItemPostVm = cartItemPostVmBuilder.build();

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(productService.getProductById(cartItemPostVm.productId()))
                .thenReturn(mock(ProductThumbnailVm.class));
            when(cartItemRepository.findByIdWithLock(any(CartItemIdV2.class))).thenReturn(java.util.Optional.empty());
            when(cartItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            CartItemGetVmV2 cartItem = cartItemService.addCartItem(cartItemPostVm);

            verify(cartItemRepository).save(any());
            assertEquals(CURRENT_USER_ID_SAMPLE, cartItem.customerId());
            assertEquals(cartItemPostVm.productId(), cartItem.productId());
            assertEquals(cartItemPostVm.quantity(), cartItem.quantity());
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
