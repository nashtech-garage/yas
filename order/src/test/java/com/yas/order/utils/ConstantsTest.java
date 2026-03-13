package com.yas.order.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void testErrorCodeConstants_shouldHaveCorrectValues() {
        assertEquals("ORDER_NOT_FOUND", Constants.ErrorCode.ORDER_NOT_FOUND);
        assertEquals("CHECKOUT_NOT_FOUND", Constants.ErrorCode.CHECKOUT_NOT_FOUND);
        assertEquals("CHECKOUT_ITEM_NOT_EMPTY", Constants.ErrorCode.CHECKOUT_ITEM_NOT_EMPTY);
        assertEquals("SIGN_IN_REQUIRED", Constants.ErrorCode.SIGN_IN_REQUIRED);
    }

    @Test
    void testMessageCodeConstants_shouldHaveCorrectValues() {
        assertEquals("Create checkout {} by user {}", Constants.MessageCode.CREATE_CHECKOUT);
        assertEquals("Update checkout {} STATUS from {} to {}", Constants.MessageCode.UPDATE_CHECKOUT_STATUS);
        assertEquals("Update checkout {} PAYMENT from {} to {}", Constants.MessageCode.UPDATE_CHECKOUT_PAYMENT);
    }

    @Test
    void testColumnConstants_shouldHaveCorrectValues() {
        assertEquals("id", Constants.Column.ID_COLUMN);
        assertEquals("createdOn", Constants.Column.CREATE_ON_COLUMN);
        assertEquals("createdBy", Constants.Column.CREATE_BY_COLUMN);
        assertEquals("email", Constants.Column.ORDER_EMAIL_COLUMN);
        assertEquals("phone", Constants.Column.ORDER_PHONE_COLUMN);
        assertEquals("orderId", Constants.Column.ORDER_ORDER_ID_COLUMN);
        assertEquals("orderStatus", Constants.Column.ORDER_ORDER_STATUS_COLUMN);
        assertEquals("countryName", Constants.Column.ORDER_COUNTRY_NAME_COLUMN);
        assertEquals("shippingAddressId", Constants.Column.ORDER_SHIPPING_ADDRESS_ID_COLUMN);
        assertEquals("billingAddressId", Constants.Column.ORDER_BILLING_ADDRESS_ID_COLUMN);
        assertEquals("productId", Constants.Column.ORDER_ITEM_PRODUCT_ID_COLUMN);
        assertEquals("productName", Constants.Column.ORDER_ITEM_PRODUCT_NAME_COLUMN);
    }

    @Test
    void testConstants_shouldBeFinalClass() {
        assertTrue(Modifier.isFinal(Constants.class.getModifiers()));
    }

    @Test
    void testErrorCode_shouldBeFinalClass() {
        assertTrue(Modifier.isFinal(Constants.ErrorCode.class.getModifiers()));
    }

    @Test
    void testMessageCode_shouldBeFinalClass() {
        assertTrue(Modifier.isFinal(Constants.MessageCode.class.getModifiers()));
    }

    @Test
    void testColumn_shouldBeFinalClass() {
        assertTrue(Modifier.isFinal(Constants.Column.class.getModifiers()));
    }
}
