package com.yas.order.utils;

public final class Constants {

    public final class ErrorCode {

        private ErrorCode() {
        }

        public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";
        public static final String CHECKOUT_NOT_FOUND = "CHECKOUT_NOT_FOUND";
        public static final String CHECKOUT_ITEM_NOT_EMPTY = "CHECKOUT_ITEM_NOT_EMPTY";
        public static final String SIGN_IN_REQUIRED = "SIGN_IN_REQUIRED";
    }

    public final class MessageCode {

        private MessageCode() {
        }

        public static final String CREATE_CHECKOUT = "Create checkout {} by user {}";
        public static final String UPDATE_CHECKOUT_STATUS = "Update checkout {} STATUS from {} to {}";
        public static final String UPDATE_CHECKOUT_PAYMENT = "Update checkout {} PAYMENT from {} to {}";
    }

    public final class Column {

        private Column() {
        }

        // common column
        public static final String ID_COLUMN = "id";
        public static final String CREATE_ON_COLUMN = "createdOn";
        public static final String CREATE_BY_COLUMN = "createdBy";

        // Order entity
        public static final String ORDER_EMAIL_COLUMN = "email";
        public static final String ORDER_PHONE_COLUMN = "phone";
        public static final String ORDER_ORDER_ID_COLUMN = "orderId";
        public static final String ORDER_ORDER_STATUS_COLUMN = "orderStatus";
        public static final String ORDER_COUNTRY_NAME_COLUMN = "countryName";
        public static final String ORDER_SHIPPING_ADDRESS_ID_COLUMN = "shippingAddressId";
        public static final String ORDER_BILLING_ADDRESS_ID_COLUMN = "billingAddressId";

        // OrderItem entity
        public static final String ORDER_ITEM_PRODUCT_ID_COLUMN = "productId";
        public static final String ORDER_ITEM_PRODUCT_NAME_COLUMN = "productName";

    }
}
