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
import com.yas.cart.viewmodel.CartItemV2DeleteVm;
import com.yas.cart.viewmodel.CartItemV2GetVm;
import com.yas.cart.viewmodel.CartItemV2PostVm;
import com.yas.cart.viewmodel.CartItemV2PutVm;
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
    private static final Long PRODUCT_ID_SAMPLE = 1L;

    @Nested
    class AddCartItemTest {
        private CartItemV2PostVm.CartItemV2PostVmBuilder cartItemPostVmBuilder;

        @BeforeEach
        void setUp() {
            cartItemPostVmBuilder = CartItemV2PostVm.builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1);
        }

        @Test
        void testAddCartItem_whenProductNotFound_shouldThrowNotFoundException() {
            cartItemPostVmBuilder.productId(-1L);
            CartItemV2PostVm cartItemPostVm = cartItemPostVmBuilder.build();

            when(productService.existsById(cartItemPostVm.productId())).thenReturn(false);

            assertThrows(NotFoundException.class, () -> cartItemService.addCartItem(cartItemPostVm));
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
            when(cartItemRepository.findByCustomerIdAndProductId(anyString(), anyLong())).thenReturn(
                Optional.of(existingCartItem));
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
            when(cartItemRepository.findByCustomerIdAndProductId(anyString(), anyLong())).thenReturn(
                java.util.Optional.empty());
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
            when(cartItemRepository.findByCustomerIdAndProductId(anyString(), anyLong()))
                .thenThrow(new PessimisticLockingFailureException("Locking failed"));

            assertThrows(InternalServerErrorException.class, () -> cartItemService.addCartItem(cartItemPostVm));
        }
    }

    @Nested
    class UpdateCartItemTest {
        private CartItemV2PutVm cartItemPutVm;

        @BeforeEach
        void setUp() {
            cartItemPutVm = new CartItemV2PutVm(1);
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

            CartItemV2GetVm updatedCartItem = cartItemService.updateCartItem(PRODUCT_ID_SAMPLE, cartItemPutVm);

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
            CartItemV2 existingCartItem = CartItemV2.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(1L)
                .quantity(1)
                .build();
            List<CartItemV2> existingCartItems = List.of(existingCartItem);

            when(cartItemRepository.findByCustomerIdOrderByCreatedOnDesc(CURRENT_USER_ID_SAMPLE))
                .thenReturn(existingCartItems);
            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);

            List<CartItemV2GetVm> cartItemGetVms = cartItemService.getCartItems();

            verify(cartItemRepository).findByCustomerIdOrderByCreatedOnDesc(CURRENT_USER_ID_SAMPLE);
            assertEquals(existingCartItems.size(), cartItemGetVms.size());
        }
    }

    @Nested
    class DeleteOrAdjustCartItemTest {

        @Test
        void testDeleteOrAdjustCartItem_whenCartItemDeleteVmsDuplicated_shouldThrowBadRequestException() {
            CartItemV2DeleteVm cartItemDeleteVm1 = new CartItemV2DeleteVm(PRODUCT_ID_SAMPLE, 1);
            CartItemV2DeleteVm cartItemDeleteVm2 = new CartItemV2DeleteVm(cartItemDeleteVm1.productId(), 2);

            List<CartItemV2DeleteVm> cartItemDeleteVms = List.of(cartItemDeleteVm1, cartItemDeleteVm2);

            assertThrows(BadRequestException.class,
                () -> cartItemService.deleteOrAdjustCartItem(cartItemDeleteVms));
        }

        @Test
        void testDeleteOrAdjustCartItem_whenDeleteQuantityGreaterThanCartItemQuantity_shouldDeleteCartItem() {
            CartItemV2 existingCartItem = CartItemV2.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1)
                .build();
            CartItemV2DeleteVm cartItemDeleteVm =
                new CartItemV2DeleteVm(existingCartItem.getProductId(), existingCartItem.getQuantity() + 1);
            List<CartItemV2DeleteVm> cartItemDeleteVms = List.of(cartItemDeleteVm);

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(cartItemRepository.findByCustomerIdAndProductIdIn(any(), any())).thenReturn(List.of(existingCartItem));

            List<CartItemV2GetVm> cartItemGetVms = cartItemService.deleteOrAdjustCartItem(cartItemDeleteVms);

            verify(cartItemRepository).deleteAll(List.of(existingCartItem));
            assertEquals(0, cartItemGetVms.size());
        }

        @Test
        void testDeleteOrAdjustCartItem_whenDeleteQuantityLessThanCartItemQuantity_shouldUpdateCartItem() {
            CartItemV2DeleteVm cartItemDeleteVm = new CartItemV2DeleteVm(PRODUCT_ID_SAMPLE, 1);
            CartItemV2 existingCartItem = CartItemV2.builder()
                .customerId(CURRENT_USER_ID_SAMPLE)
                .productId(cartItemDeleteVm.productId())
                .quantity(cartItemDeleteVm.quantity() + 1)
                .build();
            List<CartItemV2DeleteVm> cartItemDeleteVms = List.of(cartItemDeleteVm);
            int expectedQuantity = existingCartItem.getQuantity() - cartItemDeleteVm.quantity();

            mockCurrentUserId(CURRENT_USER_ID_SAMPLE);
            when(cartItemRepository.findByCustomerIdAndProductIdIn(any(), any())).thenReturn(List.of(existingCartItem));
            when(cartItemRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

            List<CartItemV2GetVm> cartItemGetVms = cartItemService.deleteOrAdjustCartItem(cartItemDeleteVms);

            verify(cartItemRepository).saveAll(List.of(existingCartItem));
            assertEquals(1, cartItemGetVms.size());
            assertEquals(expectedQuantity, cartItemGetVms.getFirst().quantity());
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