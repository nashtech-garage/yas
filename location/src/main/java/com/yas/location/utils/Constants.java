package com.yas.location.utils;

public final class Constants {

    public final class ErrorCode {
        private ErrorCode() {
        }

        public static final String COUNTRY_NOT_FOUND = "COUNTRY_NOT_FOUND";
        public static final String NAME_ALREADY_EXITED = "NAME_ALREADY_EXITED";
        public static final String STATE_OR_PROVINCE_NOT_FOUND = "STATE_OR_PROVINCE_NOT_FOUND";
        public static final String ADDRESS_NOT_FOUND = "ADDRESS_NOT_FOUND";
        public static final String CODE_ALREADY_EXISTED = "CODE_ALREADY_EXISTED";
    }

    public final class PageableConstant {
        private PageableConstant() {
        }

        public static final String DEFAULT_PAGE_SIZE = "10";
        public static final String DEFAULT_PAGE_NUMBER = "0";
    }

    public final class ApiConstant {
        private ApiConstant() {
        }

        public static final String STATE_OR_PROVINCES_URL = "/backoffice/state-or-provinces";

        public static final String STATE_OR_PROVINCES_STOREFRONT_URL = "/storefront/state-or-provinces";

        public static final String COUNTRIES_URL = "/backoffice/countries";

        public static final String COUNTRIES_STOREFRONT_URL = "/storefront/countries";

        public static final String CODE_200 = "200";
        public static final String OK = "Ok";
        public static final String CODE_404 = "404";
        public static final String NOT_FOUND = "Not found";
        public static final String CODE_201 = "201";
        public static final String CREATED = "Created";
        public static final String CODE_400 = "400";
        public static final String BAD_REQUEST = "Bad request";
        public static final String CODE_204 = "204";
        public static final String NO_CONTENT = "No content";
    }
}
