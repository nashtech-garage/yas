package com.yas.promotion.utils;

public class Constants {

    public final class ErrorCode {
        public static final String PROMOTION_NOT_FOUND = "Promotion %s is not found";

        public static final String SLUG_ALREADY_EXITED = "Slug %s is already exists";
        public static final String COUPON_CODE_ALREADY_EXISTED = "COUPON_CODE_ALREADY_EXISTED";
        public static final String EXHAUSTED_USAGE_QUANTITY = "EXHAUSTED_USAGE_QUANTITY";
        public static final String DATE_RANGE_INVALID = "End date cannot be before start date";
        public static final String PROMOTION_IN_USE_ERROR_MESSAGE = "PROMOTION_IN_USE";
        public static final String PROMOTION_NOT_FOUND_ERROR_MESSAGE = "PROMOTION_NOT_FOUND";
        public static final String ACCESS_DENIED = "ACCESS_DENIED";
        public static final String INVALID_MINIMUM_ORDER_PURCHASE_AMOUNT = "INVALID_MINIMUM_ORDER_PURCHASE_AMOUNT";
        public static final String PRODUCT_NOT_FOUND_TO_APPLY_PROMOTION = "PRODUCT_NOT_FOUND_TO_APPLY_PROMOTION";
    }

    public final class Pageable {
        public static final String DEFAULT_PAGE_SIZE = "10";
        public static final String DEFAULT_PAGE_NUMBER = "0";
    }

}
