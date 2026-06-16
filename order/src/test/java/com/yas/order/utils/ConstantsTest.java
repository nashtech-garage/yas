package com.yas.order.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class ConstantsTest {

    @Test
    void testErrorCode() {
        assertEquals("ORDER_NOT_FOUND", Constants.ErrorCode.ORDER_NOT_FOUND);
        assertEquals("CHECKOUT_NOT_FOUND", Constants.ErrorCode.CHECKOUT_NOT_FOUND);
        assertEquals("CHECKOUT_ITEM_NOT_EMPTY", Constants.ErrorCode.CHECKOUT_ITEM_NOT_EMPTY);
        assertEquals("SIGN_IN_REQUIRED", Constants.ErrorCode.SIGN_IN_REQUIRED);
    }

    @Test
    void testPrivateConstructors() throws Exception {
        testPrivateConstructor(Constants.ErrorCode.class);
        testPrivateConstructor(Constants.MessageCode.class);
        testPrivateConstructor(Constants.Column.class);
    }

    private void testPrivateConstructor(Class<?> clazz) throws Exception {
        Constructor<?> constructor = clazz.getDeclaredConstructor(Constants.class);
        constructor.setAccessible(true);
        constructor.newInstance(new Constants());
    }

    @Test
    void testMessageCode() {
        assertEquals("Create checkout {} by user {}", Constants.MessageCode.CREATE_CHECKOUT);
        assertEquals("Update checkout {} STATUS from {} to {}", Constants.MessageCode.UPDATE_CHECKOUT_STATUS);
        assertEquals("Update checkout {} PAYMENT from {} to {}", Constants.MessageCode.UPDATE_CHECKOUT_PAYMENT);
    }

    @Test
    void testColumn() {
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
}
