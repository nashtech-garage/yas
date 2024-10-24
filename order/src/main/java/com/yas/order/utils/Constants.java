package com.yas.order.utils;

public final class Constants {
    public final class ErrorCode {

        private ErrorCode() {
            throw new UnsupportedOperationException();
        }

        public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";
        public static final String CHECKOUT_NOT_FOUND = "CHECKOUT_NOT_FOUND";
        public static final String SIGN_IN_REQUIRED = "SIGN_IN_REQUIRED";
        public static final String FORBIDDEN = "FORBIDDEN";
        public static final String CANNOT_CONVERT_TO_STRING = "CANNOT_CONVERT_TO_STRING";
        public static final String PROCESS_CHECKOUT_FAILED = "PROCESS_CHECKOUT_FAILED";
        public static final String ID_NOT_EXISTED = "ID_NOT_EXISTED";
        public static final String STATUS_NOT_EXISTED = "STATUS_NOT_EXISTED";
        public static final String PROGRESS_NOT_EXISTED = "PROGRESS_NOT_EXISTED";
    }

    public final class Column {

        private Column() {
            throw new UnsupportedOperationException();
        }

        // Column name of Checkout table
        public static final String CHECKOUT_ID_COLUMN = "id";
        public static final String CHECKOUT_STATUS_COLUMN = "status";
        public static final String CHECKOUT_PROGRESS_COLUMN = "progress";
        public static final String CHECKOUT_ATTRIBUTES_PAYMENT_ID_FIELD = "payment_id";
    }
}
