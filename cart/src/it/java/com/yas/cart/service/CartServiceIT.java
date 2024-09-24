package com.yas.cart.service;

import static com.yas.cart.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.yas.cart.config.IntegrationTestConfiguration;
import com.yas.cart.model.Cart;
import com.yas.cart.model.CartItem;
import com.yas.cart.repository.CartItemRepository;
import com.yas.cart.repository.CartRepository;
import com.yas.cart.viewmodel.CartDetailVm;
import com.yas.cart.viewmodel.CartGetDetailVm;
import com.yas.cart.viewmodel.CartItemPutVm;
import com.yas.cart.viewmodel.CartItemVm;
import com.yas.cart.viewmodel.CartListVm;
import com.yas.cart.viewmodel.ProductThumbnailVm;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartServiceIT {

    final String CUSTOMER_2 = "customer-2";
    final String CUSTOMER_1 = "customer-1";
    private Cart cart1;
    private Cart cart2;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @MockBean
    private ProductService productService;
    @Autowired
    private CartService cartService;

    @BeforeEach
    void setUp() {

        cart1 = cartRepository.save(Cart.builder().customerId(CUSTOMER_1).build());
        cart2 = cartRepository.save(Cart.builder().customerId(CUSTOMER_2).build());

        CartItem cartItem1 = new CartItem();
        cartItem1.setProductId(1L);
        cartItem1.setQuantity(2);
        cartItem1.setCart(cart1);

        CartItem cartItem2 = new CartItem();
        cartItem2.setProductId(2L);
        cartItem2.setQuantity(3);
        cartItem2.setCart(cart2);

        cartItemRepository.saveAll(List.of(cartItem1, cartItem2));
    }

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    void getCarts_ExistInDatabase_Success() {
        List<CartListVm> cartListVmActual = cartService.getCarts();
        assertThat(cartListVmActual).hasSize(2);
        for (CartListVm cartListVm : cartListVmActual) {
            assertThat(cartListVm.customerId()).startsWith("customer-");
        }
    }

    @Test
    void getCartDetailByCustomerId_ExistingCustomer_Success() {

        List<CartGetDetailVm> cartDetailVms = cartService.getCartDetailByCustomerId(CUSTOMER_1);

        assertThat(cartDetailVms).hasSize(1);

        CartGetDetailVm cartDetailVm = cartDetailVms.getFirst();
        assertThat(cartDetailVm.cartDetails()).isNotEmpty();
        assertThat(cartDetailVm.cartDetails()).hasSize(1);
        assertThat(cartDetailVm.cartDetails().getFirst().productId()).isEqualTo(1);

    }

    @Test
    void getLastCart_ExistingCarts_ReturnsLatestCart() {
        CartGetDetailVm lastCart = cartService.getLastCart(CUSTOMER_1);
        assertThat(lastCart).isNotNull();
        assertThat(lastCart.id()).isEqualTo(cart1.getId());
    }

    @Test
    void getLastCart_NoCarts_ReturnsEmptyCart() {

        CartGetDetailVm lastCart = cartService.getLastCart("customer-nonexistent");
        assertThat(lastCart).isNotNull();
        assertThat(lastCart.cartDetails()).isEmpty();
    }

    @Test
    void removeCartItemListByProductIdList_SomeProductsExist_RemovesThem() {

        List<Long> productIdList = List.of(1L);

        cartService.removeCartItemListByProductIdList(productIdList, CUSTOMER_1);

        Set<CartItem> remainingItems = cartItemRepository.findAllByCart(cart1);
        assertThat(remainingItems).isEmpty();
    }

    @Test
    void removeCartItemListByProductIdList_NoCartItems_ThrowBadRequestException() {

        Cart cart3 = cartRepository.save(Cart
            .builder().customerId("customer-3").build());

        cartRepository.save(cart3);

        List<Long> productIdList = List.of(3L);

        BadRequestException thrownException = assertThrows(BadRequestException.class, () -> {
            cartService.removeCartItemListByProductIdList(productIdList, "customer-3");
        });

        assertThat(thrownException).isInstanceOf(BadRequestException.class);
        assertThat(thrownException.getMessage())
            .isEqualTo("There is no cart item in current cart to update!");

    }

    @Test
    void removeCartItemListByProductIdList_NotMatchProductId_ThrowNotFoundException() {

        List<Long> productIdList = List.of(4L);

        NotFoundException thrownException = assertThrows(NotFoundException.class, () -> {
            cartService.removeCartItemListByProductIdList(productIdList, CUSTOMER_1);
        });

        assertThat(thrownException).isInstanceOf(NotFoundException.class);
        assertThat(thrownException.getMessage())
            .isEqualTo("There is no product with ID: 4 in the current cart");

    }

    @Test
    void removeCartItemByProductId_ProductExists_RemovesItem() {
        cartService.removeCartItemByProductId(1L, CUSTOMER_1);
        Set<CartItem> remainingItems = cartItemRepository.findAllByCart(cart1);
        assertThat(remainingItems).isEmpty();
    }

    @Test
    void countNumberItemInCart_NoCart_ReturnsZero() {
        Long itemCount = cartService.countNumberItemInCart("customer-3");
        assertThat(itemCount).isZero();
    }

    @Test
    void countNumberItemInCart_EmptyCart_ReturnsZero() {

        Cart cart3 = cartRepository.save(Cart
            .builder().customerId("customer-3").build());

        cartRepository.save(cart3);

        Long itemCount = cartService.countNumberItemInCart("customer-3");
        assertThat(itemCount).isZero();
    }

    @Test
    void countNumberItemInCart_NonEmptyCart_ReturnsCorrectCount() {
        Long itemCount = cartService.countNumberItemInCart(CUSTOMER_1);
        assertThat(itemCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Add to cart the product which is already in cart")
    void addToCart_ProductsExist_AddsItemsToNewCart() {
        // Given
        int quantity = 2;
        List<CartItemVm> cartItemVms = List.of(
            new CartItemVm(1L, quantity, 10L)
        );
        List<ProductThumbnailVm> productThumbnails = List.of(
            new ProductThumbnailVm(1L, "A21", "A22", "A23")
        );

        // When
        when(productService.getProducts(List.of(1L))).thenReturn(productThumbnails);
        setUpSecurityContext(CUSTOMER_1);
        long cartQuantity = cartService.countNumberItemInCart(CUSTOMER_1);

        // Then
        CartGetDetailVm actual = cartService.addToCart(cartItemVms);
        assertThat(actual.customerId()).isEqualTo(CUSTOMER_1);
        assertThat(actual.cartDetails())
                .hasSize(1)
                .first()
                .extracting(CartDetailVm::quantity)
                .isEqualTo((int) (cartQuantity + quantity));
    }

    @Test
    void addToCart_ProductsExist_AddsItemsToExistingCart() {
        List<CartItemVm> cartItemVms = List.of(
            new CartItemVm(3L, 2, 10L)
        );

        List<ProductThumbnailVm> productThumbnails = List.of(
            new ProductThumbnailVm(3L, "A21", "A22", "A23")
        );

        when(productService.getProducts(List.of(3L))).thenReturn(productThumbnails);

        setUpSecurityContext("customer-3");

        CartGetDetailVm actual = cartService.addToCart(cartItemVms);
        assertThat(actual.customerId()).isEqualTo("customer-3");
        assertThat(actual.cartDetails()).hasSize(1);

    }

    @Test
    void addToCart_SomeProductsDoNotExist_ThrowsNotFoundException() {
        List<CartItemVm> cartItemVms = List.of(
            new CartItemVm(2L, 2, 10L)
        );

        when(productService.getProducts(List.of(2L))).thenReturn(List.of());

        NotFoundException thrownException = assertThrows(NotFoundException.class, () -> {
            cartService.addToCart(cartItemVms);
        });

        assertThat(thrownException).isInstanceOf(NotFoundException.class);
        assertThat(thrownException.getMessage())
            .isEqualTo("Not found product [2]");

    }


    @Test
    void updateCartItems_ProductExists_UpdatesQuantity() {

        CartItemVm cartItemVm = new CartItemVm(1L, 2, 10L);

        setUpSecurityContext(CUSTOMER_1);
        CartItemPutVm actual = cartService.updateCartItems(cartItemVm, CUSTOMER_1);

        assertThat(actual.status()).isEqualTo("PRODUCT UPDATED");
    }

    @Test
    void updateCartItems_QuantityZero_DeletesItem() {

        CartItemVm cartItemVm = new CartItemVm(1L, 0, 10L);

        setUpSecurityContext(CUSTOMER_1);
        CartItemPutVm actual = cartService.updateCartItems(cartItemVm, CUSTOMER_1);

        assertThat(actual.status()).isEqualTo("PRODUCT DELETED");
    }

}
