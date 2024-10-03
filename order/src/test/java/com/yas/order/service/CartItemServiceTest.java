package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.CartItemMapper;
import com.yas.order.model.CartItem;
import com.yas.order.model.CartItemId;
import com.yas.order.repository.CartItemRepository;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.cart.CartItemPostVm;
import com.yas.order.viewmodel.product.ProductThumbnailGetVm;
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
class CartItemServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartItemService cartItemService;

    @Spy
    private CartItemMapper cartItemMapper = new CartItemMapper();

    @BeforeEach
    void setUp() {
        Mockito.reset(cartItemRepository, productService, cartItemMapper);
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
        void testAddCartItem_whenQuantityIsNegative_thenThrowIllegalArgumentException() {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.quantity(-1).build();
            IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> cartItemService.addCartItem(cartItemPostVm));
            assertEquals(Constants.ErrorCode.NEGATIVE_CART_ITEM_QUANTITY, exception.getMessage());
        }

        @Test
        void testAddCartItem_whenProductNotFound_thenThrowIllegalArgumentException() {
            cartItemPostVmBuilder.productId(-1L);
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();
            Mockito.when(productService.getProductById(cartItemPostVm.productId()))
                .thenThrow(new NotFoundException(anyString()));
            IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> cartItemService.addCartItem(cartItemPostVm));
            assertEquals(Constants.ErrorCode.PRODUCT_NOT_FOUND, exception.getMessage());
        }

        @Test
        void testAddCartItem_whenCartItemExists_thenUpdateQuantity() {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();
            Mockito.when(productService.getProductById(cartItemPostVm.productId()))
                .thenReturn(Mockito.mock(ProductThumbnailGetVm.class));

            mockCurrentUserId("customerId");

            CartItem existingCartItem = CartItem
                .builder()
                .customerId("customerId")
                .productId(cartItemPostVm.productId())
                .quantity(1)
                .build();
            Mockito.when(cartItemRepository.findById(any())).thenReturn(java.util.Optional.of(existingCartItem));

            cartItemService.addCartItem(cartItemPostVm);

            Mockito.verify(cartItemRepository).save(any());
        }

        @Test
        void testAddCartItem_whenCartItemDoesNotExist_thenCreateCartItem() {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();
            Mockito.when(productService.getProductById(cartItemPostVm.productId()))
                .thenReturn(Mockito.mock(ProductThumbnailGetVm.class));

            mockCurrentUserId("customerId");

            Mockito.when(cartItemRepository.findById(any(CartItemId.class))).thenReturn(java.util.Optional.empty());

            cartItemService.addCartItem(cartItemPostVm);

            Mockito.verify(cartItemRepository).save(any());
        }
    }

    private void mockCurrentUserId(String userIdToMock) {
        Jwt jwt = Mockito.mock(Jwt.class);
        JwtAuthenticationToken jwtToken = new JwtAuthenticationToken(jwt);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(jwtToken);

        Mockito.when(jwt.getSubject()).thenReturn(userIdToMock);
        SecurityContextHolder.setContext(securityContext);
    }

}
