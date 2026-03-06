package com.yas.cart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.cart.mapper.CartItemMapper;
import com.yas.cart.model.CartItem;
import com.yas.cart.repository.CartItemRepository;
import com.yas.cart.viewmodel.CartItemDeleteVm;
import com.yas.cart.viewmodel.CartItemGetVm;
import com.yas.cart.viewmodel.CartItemPostVm;
import com.yas.cart.viewmodel.CartItemPutVm;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.InternalServerErrorException;
import com.yas.commonlibrary.exception.NotFoundException;
import java.util.List;
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

    private static final String CURRENT_USER_ID_SAMPLE = "userId";
    private static final Long PRODUCT_ID_SAMPLE = 1L;

    @Nested
    class AddCartItemTest {
        private CartItemPostVm.CartItemPostVmBuilder cartItemPostVmBuilder;

        @BeforeEach
        void setUp() {
            cartItemPostVmBuilder = CartItemPostVm.builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1);
        }

        @Test
        void testAddCartItem_whenProductNotFound_shouldThrowNotFoundException() {
            cartItemPostVmBuilder.productId(-1L);
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();

            when(productService.existsById(cartItemPostVm.productId())).thenReturn(false);

            assertThrows(NotFoundException.class, () -> cartItemService.addCartItem(cartItemPostVm));
        }

        @Test
        void testAddCartItem_whenCartItemExists_shouldUpdateQuantity() {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();
            CartItem existingCartItem = CartItem
                .builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(cartItemPostVm.productId())
                .quantity(1)
                .build();
            int expectedQuantity = existingCartItem.getQuantity() + cartItemPostVm.quantity();

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(productService.existsById(cartItemPostVm.productId())).thenReturn(true);
            when(cartItemRepository.findByCustomerIdAndProductId(anyString(), anyLong())).thenReturn(
                Optional.of(existingCartItem));
            when(cartItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            CartItemGetVm cartItem = cartItemService.addCartItem(cartItemPostVm);

            verify(cartItemRepository).save(any());
            assertEquals(expectedQuantity, cartItem.quantity());
            assertEquals(CURRENT_USER_ID_SAMPLE, cartItem.customerId());
            assertEquals(cartItemPostVm.productId(), cartItem.productId());
        }

        @Test
        void testAddCartItem_whenCartItemDoesNotExist_shouldCreateCartItem() {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(productService.existsById(cartItemPostVm.productId())).thenReturn(true);
            when(cartItemRepository.findByCustomerIdAndProductId(anyString(), anyLong())).thenReturn(
                java.util.Optional.empty());
            when(cartItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            CartItemGetVm cartItem = cartItemService.addCartItem(cartItemPostVm);

            verify(cartItemRepository).save(any());
            assertEquals(CURRENT_USER_ID_SAMPLE, cartItem.customerId());
            assertEquals(cartItemPostVm.productId(), cartItem.productId());
            assertEquals(cartItemPostVm.quantity(), cartItem.quantity());
        }

        @Test
        void testAddCartItem_whenAcquireLockFailed_shouldThrowInternalServerErrorException() {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(productService.existsById(cartItemPostVm.productId())).thenReturn(true);
            when(cartItemRepository.findByCustomerIdAndProductId(anyString(), anyLong()))
                .thenThrow(new PessimisticLockingFailureException("Locking failed"));

            assertThrows(InternalServerErrorException.class, () -> cartItemService.addCartItem(cartItemPostVm));
        }
    }

    @Nested
    class UpdateCartItemTest {
        private CartItemPutVm cartItemPutVm;

        @BeforeEach
        void setUp() {
            cartItemPutVm = new CartItemPutVm(1);
        }

        @Test
        void testUpdateCartItem_whenProductNotFound_shouldThrowNotFoundException() {
            Long notExistingProductId = -1L;

            when(productService.existsById(notExistingProductId)).thenReturn(false);

            assertThrows(NotFoundException.class,
                () -> cartItemService.updateCartItem(notExistingProductId, cartItemPutVm));
        }

        @Test
        void testUpdateCartItem_whenRequestIsValid_shouldReturnCartItem() {
            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(productService.existsById(PRODUCT_ID_SAMPLE)).thenReturn(true);
            when(cartItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            CartItemGetVm updatedCartItem = cartItemService.updateCartItem(PRODUCT_ID_SAMPLE, cartItemPutVm);

            verify(cartItemRepository).save(any());
            assertEquals(CURRENT_USER_ID_SAMPLE, updatedCartItem.customerId());
            assertEquals(PRODUCT_ID_SAMPLE, updatedCartItem.productId());
            assertEquals(cartItemPutVm.quantity(), updatedCartItem.quantity());
        }
    }

    @Nested
    class GetCartItemsTest {

        @Test
        void testGetCartItems_shouldReturnCartItems() {
            CartItem existingCartItem = CartItem.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(1L)
                .quantity(1)
                .build();
            List<CartItem> existingCartItems = List.of(existingCartItem);

            when(cartItemRepository.findByCustomerIdOrderByCreatedOnDesc(CURRENT_USER_ID_SAMPLE))
                .thenReturn(existingCartItems);
            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);

            List<CartItemGetVm> cartItemGetVms = cartItemService.getCartItems();

            verify(cartItemRepository).findByCustomerIdOrderByCreatedOnDesc(CURRENT_USER_ID_SAMPLE);
            assertEquals(existingCartItems.size(), cartItemGetVms.size());
        }
    }

    @Nested
    class DeleteOrAdjustCartItemTest {

        @Test
        void testDeleteOrAdjustCartItem_whenCartItemDeleteVmsDuplicated_shouldThrowBadRequestException() {
            CartItemDeleteVm cartItemDeleteVm1 = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, 1);
            CartItemDeleteVm cartItemDeleteVm2 = new CartItemDeleteVm(cartItemDeleteVm1.productId(), 2);

            List<CartItemDeleteVm> cartItemDeleteVms = List.of(cartItemDeleteVm1, cartItemDeleteVm2);

            assertThrows(BadRequestException.class,
                () -> cartItemService.deleteOrAdjustCartItem(cartItemDeleteVms));
        }

        @Test
        void testDeleteOrAdjustCartItem_whenDeleteQuantityGreaterThanCartItemQuantity_shouldDeleteCartItem() {
            CartItem existingCartItem = CartItem.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1)
                .build();
            CartItemDeleteVm cartItemDeleteVm =
                new CartItemDeleteVm(existingCartItem.getProductId(), existingCartItem.getQuantity() + 1);
            List<CartItemDeleteVm> cartItemDeleteVms = List.of(cartItemDeleteVm);

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(cartItemRepository.findByCustomerIdAndProductIdIn(any(), any())).thenReturn(List.of(existingCartItem));

            List<CartItemGetVm> cartItemGetVms = cartItemService.deleteOrAdjustCartItem(cartItemDeleteVms);

            verify(cartItemRepository).deleteAll(List.of(existingCartItem));
            assertEquals(0, cartItemGetVms.size());
        }

        @Test
        void testDeleteOrAdjustCartItem_whenDeleteQuantityLessThanCartItemQuantity_shouldUpdateCartItem() {
            CartItemDeleteVm cartItemDeleteVm = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, 1);
            CartItem existingCartItem = CartItem.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(cartItemDeleteVm.productId())
                .quantity(cartItemDeleteVm.quantity() + 1)
                .build();
            List<CartItemDeleteVm> cartItemDeleteVms = List.of(cartItemDeleteVm);
            int expectedQuantity = existingCartItem.getQuantity() - cartItemDeleteVm.quantity();

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(cartItemRepository.findByCustomerIdAndProductIdIn(any(), any())).thenReturn(List.of(existingCartItem));
            when(cartItemRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

            List<CartItemGetVm> cartItemGetVms = cartItemService.deleteOrAdjustCartItem(cartItemDeleteVms);

            verify(cartItemRepository).saveAll(List.of(existingCartItem));
            assertEquals(1, cartItemGetVms.size());
            assertEquals(expectedQuantity, cartItemGetVms.getFirst().quantity());
        }

        @Test
        void testDeleteOrAdjustCartItem_whenDeleteQuantityEqualsCartItemQuantity_shouldDeleteCartItem() {
            CartItem existingCartItem = CartItem.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(5)
                .build();
            CartItemDeleteVm cartItemDeleteVm =
                new CartItemDeleteVm(existingCartItem.getProductId(), existingCartItem.getQuantity());
            List<CartItemDeleteVm> cartItemDeleteVms = List.of(cartItemDeleteVm);

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(cartItemRepository.findByCustomerIdAndProductIdIn(any(), any())).thenReturn(List.of(existingCartItem));

            List<CartItemGetVm> cartItemGetVms = cartItemService.deleteOrAdjustCartItem(cartItemDeleteVms);

            verify(cartItemRepository).deleteAll(List.of(existingCartItem));
            assertEquals(0, cartItemGetVms.size());
        }

        @Test
        void testDeleteOrAdjustCartItem_whenMultipleItems_shouldProcessCorrectly() {
            CartItem existingCartItem1 = CartItem.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(1L)
                .quantity(5)
                .build();
            CartItem existingCartItem2 = CartItem.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(2L)
                .quantity(10)
                .build();

            CartItemDeleteVm cartItemDeleteVm1 = new CartItemDeleteVm(1L, 2);
            CartItemDeleteVm cartItemDeleteVm2 = new CartItemDeleteVm(2L, 15);
            List<CartItemDeleteVm> cartItemDeleteVms = List.of(cartItemDeleteVm1, cartItemDeleteVm2);

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(cartItemRepository.findByCustomerIdAndProductIdIn(any(), any()))
                .thenReturn(List.of(existingCartItem1, existingCartItem2));
            when(cartItemRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

            List<CartItemGetVm> cartItemGetVms = cartItemService.deleteOrAdjustCartItem(cartItemDeleteVms);

            verify(cartItemRepository).deleteAll(List.of(existingCartItem2));
            verify(cartItemRepository).saveAll(List.of(existingCartItem1));
            assertEquals(1, cartItemGetVms.size());
            assertEquals(3, cartItemGetVms.getFirst().quantity());
        }

        @Test
        void testDeleteOrAdjustCartItem_whenCartItemNotFound_shouldDoNothing() {
            CartItemDeleteVm cartItemDeleteVm = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, 1);
            List<CartItemDeleteVm> cartItemDeleteVms = List.of(cartItemDeleteVm);

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(cartItemRepository.findByCustomerIdAndProductIdIn(any(), any())).thenReturn(List.of());
            when(cartItemRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

            List<CartItemGetVm> cartItemGetVms = cartItemService.deleteOrAdjustCartItem(cartItemDeleteVms);

            assertEquals(0, cartItemGetVms.size());
        }
    }

    @Nested
    class DeleteCartItemTest {

        @Test
        void testDeleteCartItem_shouldDeleteSuccessfully() {
            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);

            cartItemService.deleteCartItem(PRODUCT_ID_SAMPLE);

            verify(cartItemRepository).deleteByCustomerIdAndProductId(CURRENT_USER_ID_SAMPLE, PRODUCT_ID_SAMPLE);
        }
    }

    @Nested
    class GetCartItemsEmptyTest {

        @Test
        void testGetCartItems_whenNoItems_shouldReturnEmptyList() {
            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(cartItemRepository.findByCustomerIdOrderByCreatedOnDesc(CURRENT_USER_ID_SAMPLE))
                .thenReturn(List.of());

            List<CartItemGetVm> cartItemGetVms = cartItemService.getCartItems();

            verify(cartItemRepository).findByCustomerIdOrderByCreatedOnDesc(CURRENT_USER_ID_SAMPLE);
            assertTrue(cartItemGetVms.isEmpty());
        }

        @Test
        void testGetCartItems_whenMultipleItems_shouldReturnAllItems() {
            CartItem cartItem1 = CartItem.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(1L)
                .quantity(5)
                .build();
            CartItem cartItem2 = CartItem.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(2L)
                .quantity(10)
                .build();
            CartItem cartItem3 = CartItem.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(3L)
                .quantity(15)
                .build();
            List<CartItem> existingCartItems = List.of(cartItem1, cartItem2, cartItem3);

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(cartItemRepository.findByCustomerIdOrderByCreatedOnDesc(CURRENT_USER_ID_SAMPLE))
                .thenReturn(existingCartItems);

            List<CartItemGetVm> cartItemGetVms = cartItemService.getCartItems();

            verify(cartItemRepository).findByCustomerIdOrderByCreatedOnDesc(CURRENT_USER_ID_SAMPLE);
            assertEquals(3, cartItemGetVms.size());
            assertEquals(5, cartItemGetVms.get(0).quantity());
            assertEquals(10, cartItemGetVms.get(1).quantity());
            assertEquals(15, cartItemGetVms.get(2).quantity());
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
