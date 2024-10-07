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
import com.yas.order.mapper.CartItemMapper;
import com.yas.order.model.CartItem;
import com.yas.order.model.CartItemId;
import com.yas.order.repository.CartItemRepository;
import com.yas.order.viewmodel.cart.CartItemGetVm;
import com.yas.order.viewmodel.cart.CartItemPostVm;
import com.yas.order.viewmodel.cart.CartItemPutVm;
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

    @Spy
    private CartItemMapper cartItemMapper = new CartItemMapper();

    @InjectMocks
    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {
        Mockito.reset(cartItemRepository, productService);
    }

    private static final String CURRENT_USER_ID_SAMPLE = "customerId";

    @Nested
    class AddCartItemTest {
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
            CartItem existingCartItem = CartItem
                .builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(cartItemPostVm.productId())
                .quantity(1)
                .build();
            int expectedQuantity = existingCartItem.getQuantity() + cartItemPostVm.quantity();

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
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

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(productService.getProductById(cartItemPostVm.productId()))
                .thenReturn(mock(ProductThumbnailGetVm.class));
            when(cartItemRepository.findById(any(CartItemId.class))).thenReturn(java.util.Optional.empty());

            cartItemService.addCartItem(cartItemPostVm);

            ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
            verify(cartItemRepository).save(cartItemCaptor.capture());
            CartItem savedCartItem = cartItemCaptor.getValue();

            assertEquals(CURRENT_USER_ID_SAMPLE, savedCartItem.getCustomerId());
            assertEquals(cartItemPostVm.productId(), savedCartItem.getProductId());
            assertEquals(cartItemPostVm.quantity(), savedCartItem.getQuantity());
        }
    }

    @Nested
    class UpdateCartItemTest {
        private CartItemPutVm.CartItemPutVmBuilder cartItemPutVmBuilder;
        private static final Long PRODUCT_ID_SAMPLE = 1L;

        @BeforeEach
        void setUp() {
            cartItemPutVmBuilder = CartItemPutVm.builder().quantity(10);
        }

        @Test
        void testUpdateCartItem_whenProductNotFound_thenThrowBadRequestException() {
            Long notExistingProductId = -1L;
            CartItemPutVm cartItemPutVm = cartItemPutVmBuilder.build();
            when(productService.getProductById(notExistingProductId)).thenThrow(new NotFoundException(anyString()));
            assertThrows(BadRequestException.class,
                () -> cartItemService.updateCartItem(notExistingProductId, cartItemPutVm));
        }

        @Test
        void testUpdateCartItem_whenCartItemExists_thenUpdateQuantity() {
            CartItemPutVm cartItemPutVm = cartItemPutVmBuilder.build();
            CartItem existingCartItem = CartItem.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1)
                .build();

            mockCurrentUserId(existingCartItem.getCustomerId());
            when(productService.getProductById(PRODUCT_ID_SAMPLE)).thenReturn(mock(ProductThumbnailGetVm.class));
            when(cartItemRepository.findById(any())).thenReturn(Optional.of(existingCartItem));
            when(cartItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            CartItemGetVm updatedCartItem = cartItemService.updateCartItem(PRODUCT_ID_SAMPLE, cartItemPutVm);

            verify(cartItemRepository).save(any());
            assertEquals(CURRENT_USER_ID_SAMPLE, updatedCartItem.customerId());
            assertEquals(PRODUCT_ID_SAMPLE, updatedCartItem.productId());
            assertEquals(cartItemPutVm.quantity(), updatedCartItem.quantity());
        }

        @Test
        void testUpdateCartItem_whenCartItemDoesNotExist_thenCreateCartItem() {
            CartItemPutVm cartItemPutVm = cartItemPutVmBuilder.build();

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(productService.getProductById(PRODUCT_ID_SAMPLE)).thenReturn(mock(ProductThumbnailGetVm.class));
            when(cartItemRepository.findById(any(CartItemId.class))).thenReturn(Optional.empty());
            when(cartItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            CartItemGetVm createdCartItem = cartItemService.updateCartItem(PRODUCT_ID_SAMPLE, cartItemPutVm);

            verify(cartItemRepository).save(any());
            assertEquals(CURRENT_USER_ID_SAMPLE, createdCartItem.customerId());
            assertEquals(PRODUCT_ID_SAMPLE, createdCartItem.productId());
            assertEquals(cartItemPutVm.quantity(), createdCartItem.quantity());
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
