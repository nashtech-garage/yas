package com.yas.order.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.AbstractControllerIT;
import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.order.repository.CartItemRepository;
import com.yas.order.service.ProductService;
import com.yas.order.viewmodel.cart.CartItemPostVm;
import com.yas.order.viewmodel.cart.CartItemPutVm;
import com.yas.order.viewmodel.product.ProductThumbnailGetVm;
import io.restassured.specification.RequestSpecification;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartItemControllerIT extends AbstractControllerIT {

    private static final String CART_ITEM_BASE_URL = "v1/storefront/cart/items";
    private static final String ADD_CART_ITEM_URL = CART_ITEM_BASE_URL;
    private static final String UPDATE_CART_ITEM_TEMPLATE = CART_ITEM_BASE_URL + "/%d";

    @SpyBean
    private CartItemRepository cartItemRepository;

    @MockBean
    private ProductService productService;

    private ProductThumbnailGetVm existingProduct;

    @BeforeEach
    void setUp() {
        existingProduct = ProductThumbnailGetVm
            .builder()
            .id(generateRandomLong())
            .name(RandomStringUtils.randomAlphabetic(5))
            .price(10000.0D)
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
        void testAddCartItem_whenProductExists_shouldSuccess() {
            CartItemPostVm cartItemPostVm = new CartItemPostVm(existingProduct.id(), 1);

            when(productService.getProductById(existingProduct.id())).thenReturn(existingProduct);

            givenLoggedInAsAdmin()
                .body(cartItemPostVm)
                .when()
                .post(ADD_CART_ITEM_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().ifValidationFails();
        }
    }

    @Nested
    class UpdateCartItemTest {

        @Test
        void testUpdateCartItem_whenCartItemDoesNotExist_shouldReturnCreatedCartItem() {
            CartItemPutVm cartItemPutVm = new CartItemPutVm(1);

            when(productService.getProductById(existingProduct.id())).thenReturn(existingProduct);

            givenLoggedInAsAdmin()
                .body(cartItemPutVm)
                .when()
                .put(getUpdateCartItemUrl(existingProduct.id()))
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("productId", equalTo(existingProduct.id()))
                .body("quantity", equalTo(cartItemPutVm.quantity()))
                .log().ifValidationFails();
        }

        @Test
        void testUpdateCartItem_whenCartItemExists_shouldReturnUpdatedCartItem() {
            createCartItem(existingProduct.id());
            CartItemPutVm cartItemPutVm = new CartItemPutVm(2);

            when(productService.getProductById(existingProduct.id())).thenReturn(existingProduct);

            givenLoggedInAsAdmin()
                .body(cartItemPutVm)
                .when()
                .put(getUpdateCartItemUrl(existingProduct.id()))
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("productId", equalTo(existingProduct.id()))
                .body("quantity", equalTo(cartItemPutVm.quantity()))
                .log().ifValidationFails();
        }

        private String getUpdateCartItemUrl(Long productId) {
            return String.format(UPDATE_CART_ITEM_TEMPLATE, productId);
        }

    }

    private RequestSpecification givenLoggedInAsAdmin() {
        return given(getRequestSpecification())
            .auth().oauth2(getAccessToken("admin", "admin"));
    }

    private void createCartItem(Long productId) {
        givenLoggedInAsAdmin()
            .body(CartItemPostVm.builder().productId(productId).quantity(1).build())
            .when()
            .post(ADD_CART_ITEM_URL)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    private Long generateRandomLong() {
        return ThreadLocalRandom.current().nextLong();
    }
}
