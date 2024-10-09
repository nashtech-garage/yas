package com.yas.cart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.cart.mapper.CartItemV2Mapper;
import com.yas.cart.model.CartItemV2;
import com.yas.cart.repository.CartItemV2Repository;
import com.yas.cart.viewmodel.CartItemV2GetVm;
import com.yas.cart.viewmodel.CartItemV2PostVm;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.InternalServerErrorException;
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
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class CartItemV2ServiceTest {
    @Mock
    private CartItemV2Repository cartItemRepository;

    @Mock
    private ProductService productService;

    @Spy
    private CartItemV2Mapper cartItemMapper = new CartItemV2Mapper();

    @InjectMocks
    private CartItemV2Service cartItemService;

    @BeforeEach
    void setUp() {
        Mockito.reset(cartItemRepository, productService);
    }

    private static final String CURRENT_USER_ID_SAMPLE = "userId";

    @Nested
    class AddCartItemTest {
        private CartItemV2PostVm.CartItemV2PostVmBuilder cartItemPostVmBuilder;

        @BeforeEach
        void setUp() {
            cartItemPostVmBuilder = CartItemV2PostVm.builder()
                .productId(1L)
                .quantity(1);
        }

        @Test
        void testAddCartItem_whenProductNotFound_shouldThrowBadRequestException() {
            cartItemPostVmBuilder.productId(-1L);
            CartItemV2PostVm cartItemPostVm = cartItemPostVmBuilder.build();

            when(productService.existsById(cartItemPostVm.productId())).thenReturn(false);

            assertThrows(BadRequestException.class, () -> cartItemService.addCartItem(cartItemPostVm));
        }

        @Test
        void testAddCartItem_whenCartItemExists_shouldUpdateQuantity() {
            CartItemV2PostVm cartItemPostVm = cartItemPostVmBuilder.build();
            CartItemV2 existingCartItem = CartItemV2
                .builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(cartItemPostVm.productId())
                .quantity(1)
                .build();
            int expectedQuantity = existingCartItem.getQuantity() + cartItemPostVm.quantity();

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(productService.existsById(cartItemPostVm.productId())).thenReturn(true);
            when(cartItemRepository.findWithLock(anyString(), anyLong())).thenReturn(Optional.of(existingCartItem));
            when(cartItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            CartItemV2GetVm cartItem = cartItemService.addCartItem(cartItemPostVm);

            verify(cartItemRepository).save(any());
            assertEquals(expectedQuantity, cartItem.quantity());
            assertEquals(CURRENT_USER_ID_SAMPLE, cartItem.customerId());
            assertEquals(cartItemPostVm.productId(), cartItem.productId());
        }

        @Test
        void testAddCartItem_whenCartItemDoesNotExist_shouldCreateCartItem() {
            CartItemV2PostVm cartItemPostVm = cartItemPostVmBuilder.build();

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(productService.existsById(cartItemPostVm.productId())).thenReturn(true);
            when(cartItemRepository.findWithLock(anyString(), anyLong())).thenReturn(java.util.Optional.empty());
            when(cartItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            CartItemV2GetVm cartItem = cartItemService.addCartItem(cartItemPostVm);

            verify(cartItemRepository).save(any());
            assertEquals(CURRENT_USER_ID_SAMPLE, cartItem.customerId());
            assertEquals(cartItemPostVm.productId(), cartItem.productId());
            assertEquals(cartItemPostVm.quantity(), cartItem.quantity());
        }

        @Test
        void testAddCartItem_whenAcquireLockFailed_shouldThrowInternalServerErrorException() {
            CartItemV2PostVm cartItemPostVm = cartItemPostVmBuilder.build();

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(productService.existsById(cartItemPostVm.productId())).thenReturn(true);
            when(cartItemRepository.findWithLock(anyString(), anyLong()))
                .thenThrow(new PessimisticLockingFailureException("Locking failed"));

            assertThrows(InternalServerErrorException.class, () -> cartItemService.addCartItem(cartItemPostVm));
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
