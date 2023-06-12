package com.yas.paymentpaypal.utils;

public final class Constants {
    public final class ErrorCode  {
        // Private constructor to prevent instantiation
        private ErrorCode () {}
        public static final String SIGN_IN_REQUIRED = "SIGN_IN_REQUIRED";
        public static final String FORBIDDEN = "FORBIDDEN";
    }

    public final class Message {
        // Private constructor to prevent instantiation
        private Message() {}
        public static final String PAYMENT_FAIL_MESSAGE = "PAYMENT_FAIL_MESSAGE";
        public static final String PAYMENT_SUCCESS_MESSAGE = "PAYMENT_SUCCESS_MESSAGE";
    }

    public final class Yas {
        private Yas() {}
        public static final String BRAND_NAME = "Yas";
    }
}
