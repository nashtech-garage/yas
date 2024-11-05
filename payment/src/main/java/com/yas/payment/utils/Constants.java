package com.yas.payment.utils;

public final class Constants {

    public final class ErrorCode {

        public static final String PAYMENT_PROVIDER_NOT_FOUND = "PAYMENT_PROVIDER_NOT_FOUND";
        public static final String CANNOT_CONVERT_TO_STRING = "CANNOT_CONVERT_TO_STRING";
        public static final String ID_NOT_EXISTED = "ID_NOT_EXISTED";
        public static final String PAYMENT_NOT_FOUND = "PAYMENT_NOT_FOUND";
        public static final String ORDER_CREATION_FAILED = "ORDER_CREATION_FAILED";

        private ErrorCode() {
        }
    }

    public final class Column {

        private Column() {
        }

        public static final String ID_COLUMN = "id";

        // Column name of Checkout table
        public static final String REDIRECT_URL_ID_COLUMN = "redirect-url";
    }

    public final class Message {

        private Message() {
        }

        public static final String SUCCESS_MESSAGE = "SUCCESS";
        public static final String PAYMENT_METHOD_COD = "PAYMENT_METHOD_COD";
        public static final String PAYMENT_METHOD_PAYPAL = "PAYMENT_METHOD_PAYPAL";
        public static final String PAYMENT_ID_REQUIRED = "PAYMENT_ID_REQUIRED";
    }

}
