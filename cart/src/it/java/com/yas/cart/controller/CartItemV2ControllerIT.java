package com.yas.cart.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.yas.cart.model.CartItemV2;
import com.yas.cart.repository.CartItemV2Repository;
import com.yas.cart.service.ProductService;
import com.yas.cart.viewmodel.CartItemV2DeleteVm;
import com.yas.cart.viewmodel.CartItemV2PostVm;
import com.yas.cart.viewmodel.CartItemV2PutVm;
import com.yas.cart.viewmodel.ProductThumbnailVm;
import com.yas.commonlibrary.AbstractControllerIT;
import com.yas.commonlibrary.IntegrationTestConfiguration;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartItemV2ControllerIT extends AbstractControllerIT {

    @Autowired
    private CartItemV2Repository cartItemRepository;

    @MockBean
    private ProductService productService;

    private ProductThumbnailVm existingProduct;

    @BeforeEach
    void setUp() {
        existingProduct = ProductThumbnailVm
            .builder()
            .id(Long.MIN_VALUE)
            .name("product-name")
            .slug("product-slug")
            .thumbnailUrl("thumbnail-url")
            .build();
    }

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
    }

    @Nested
    class AddCartItemTest {

        @Test
        void testAddCartItem_whenRequestIsValid_shouldReturnCartItemGetVm() {
            CartItemV2PostVm cartItemPostVm = new CartItemV2PostVm(existingProduct.id(), 1);

            when(productService.existsById(cartItemPostVm.productId())).thenReturn(true);

            performCreateCartItemAndExpectSuccess(cartItemPostVm)
                .body("productId", is(cartItemPostVm.productId()))
                .body("quantity", equalTo(cartItemPostVm.quantity()))
                .log().ifValidationFails();
        }

        @Test
        void testAddCartItem_whenCartItemExists_shouldUpdateCartItemQuantity() {
            CartItemV2PostVm addCartItemVm = CartItemV2PostVm
                .builder()
                .productId(existingProduct.id())
                .quantity(1)
                .build();
            when(productService.existsById(anyLong())).thenReturn(true);
            performCreateCartItemAndExpectSuccess(addCartItemVm);

            CartItemV2PostVm addDuplicatedCartItemVm = CartItemV2PostVm
                .builder()
                .productId(addCartItemVm.productId())
                .quantity(1)
                .build();
            int expectedQuantity = addCartItemVm.quantity() + addDuplicatedCartItemVm.quantity();

            performCreateCartItemAndExpectSuccess(addDuplicatedCartItemVm)
                .body("productId", equalTo(addDuplicatedCartItemVm.productId()))
                .body("quantity", equalTo(expectedQuantity))
                .log().ifValidationFails();
        }
    }

    @Nested
    class UpdateCartItemTest {

        @Test
        void testUpdateCartItem_whenRequestIsValid_shouldReturnCartItemGetVm() {
            CartItemV2PutVm cartItemPutVm = new CartItemV2PutVm(1);

            when(productService.existsById(existingProduct.id())).thenReturn(true);

            givenLoggedInAsAdmin()
                .body(cartItemPutVm)
                .when()
                .put("/v1/storefront/cart/items/" + existingProduct.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("productId", equalTo(existingProduct.id()))
                .body("quantity", equalTo(cartItemPutVm.quantity()))
                .log().ifValidationFails();
        }
    }

    @Nested
    class GetCartItemsTest {

        @Test
        void testGetCartItems_whenCartItemsExist_shouldReturnCartItems() {
            CartItemV2PostVm cartItemPostVm = new CartItemV2PostVm(existingProduct.id(), 1);

            when(productService.existsById(cartItemPostVm.productId())).thenReturn(true);
            performCreateCartItemAndExpectSuccess(cartItemPostVm);

            givenLoggedInAsAdmin()
                .when()
                .get("/v1/storefront/cart/items")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(1))
                .body("[0].productId", equalTo(cartItemPostVm.productId()))
                .body("[0].quantity", equalTo(cartItemPostVm.quantity()))
                .log().ifValidationFails();
        }

    }

    @Nested
    class DeleteOrAdjustCartItemTest {
        private CartItemV2 existingCartItem;

        @BeforeEach
        void setUp() {
            CartItemV2PostVm cartItemPostVm = new CartItemV2PostVm(existingProduct.id(), 10);
            when(productService.existsById(cartItemPostVm.productId())).thenReturn(true);
            performCreateCartItemAndExpectSuccess(cartItemPostVm);
            existingCartItem = CartItemV2
                .builder()
                .productId(cartItemPostVm.productId())
                .quantity(cartItemPostVm.quantity())
                .build();
        }

        @Test
        void testDeleteOrAdjustCartItem_whenDeleteQuantityIsGreaterThanOrEqualToCartItemQuantity_shouldDeleteCartItem() {
            CartItemV2DeleteVm cartItemDeleteVm =
                new CartItemV2DeleteVm(existingCartItem.getProductId(), existingCartItem.getQuantity() + 1);

            givenLoggedInAsAdmin()
                .when()
                .body(List.of(cartItemDeleteVm))
                .post("/v1/storefront/cart/items/remove")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(0))
                .log().ifValidationFails();
        }

        @Test
        void testDeleteOrAdjustCartItem_whenDeleteQuantityIsLessThanCartItemQuantity_shouldAdjustCartItemQuantity() {
            CartItemV2DeleteVm cartItemDeleteVm =
                new CartItemV2DeleteVm(existingCartItem.getProductId(), existingCartItem.getQuantity() - 1);
            int expectedQuantity = existingCartItem.getQuantity() - cartItemDeleteVm.quantity();

            givenLoggedInAsAdmin()
                .when()
                .body(List.of(cartItemDeleteVm))
                .post("/v1/storefront/cart/items/remove")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(1))
                .body("[0].productId", equalTo(existingCartItem.getProductId()))
                .body("[0].quantity", equalTo(expectedQuantity))
                .log().ifValidationFails();
        }
    }

    private ValidatableResponse performCreateCartItemAndExpectSuccess(CartItemV2PostVm cartItemPostVm) {
        return givenLoggedInAsAdmin()
            .body(cartItemPostVm)
            .when()
            .post("/v1/storefront/cart/items")
            .then()
            .statusCode(HttpStatus.OK.value());
    }
}
