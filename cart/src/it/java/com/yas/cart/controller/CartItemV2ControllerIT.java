package com.yas.cart.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import com.yas.cart.repository.CartItemV2Repository;
import com.yas.cart.service.ProductService;
import com.yas.cart.viewmodel.CartItemV2PostVm;
import com.yas.cart.viewmodel.CartItemV2PutVm;
import com.yas.cart.viewmodel.ProductThumbnailVm;
import com.yas.commonlibrary.AbstractControllerIT;
import com.yas.commonlibrary.IntegrationTestConfiguration;
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
class CartItemV2ControllerIT extends AbstractControllerIT {

    private static final String CART_ITEM_BASE_URL = "/v1/storefront/cart/items";
    private static final String ADD_CART_ITEM_URL = CART_ITEM_BASE_URL;
    private static final String UPDATE_CART_ITEM_TEMPLATE = CART_ITEM_BASE_URL + "/%d";

    @SpyBean
    private CartItemV2Repository cartItemRepository;

    @MockBean
    private ProductService productService;

    private ProductThumbnailVm existingProduct;

    @BeforeEach
    void setUp() {
        existingProduct = ProductThumbnailVm
            .builder()
            .id(generateRandomLong())
            .name(RandomStringUtils.randomAlphabetic(5))
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

            when(productService.getProductById(existingProduct.id())).thenReturn(existingProduct);

            givenLoggedInAsAdmin()
                .body(cartItemPostVm)
                .when()
                .post(ADD_CART_ITEM_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("productId", equalTo(cartItemPostVm.productId()))
                .body("quantity", equalTo(cartItemPostVm.quantity()))
                .log().ifValidationFails();
        }
    }

    @Nested
    class UpdateCartItemTest {

        @Test
        void testUpdateCartItem_whenRequestIsValid_shouldReturnCartItemGetVm() {
            CartItemV2PutVm cartItemPutVm = new CartItemV2PutVm(1);

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

    private Long generateRandomLong() {
        return ThreadLocalRandom.current().nextLong();
    }
}
