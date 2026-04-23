package com.yas.payment.paypal.model;

public class CheckoutIdHelper {

    private static String checkoutId;

    private CheckoutIdHelper() {
        // Private constructor to prevent instantiation
    }

    public static String getCheckoutId() {
        return checkoutId;
    }

    public static void setCheckoutId(String checkoutId) {
        CheckoutIdHelper.checkoutId = checkoutId;
    }
}
