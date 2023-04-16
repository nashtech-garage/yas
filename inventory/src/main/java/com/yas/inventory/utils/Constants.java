package com.yas.inventory.utils;

public final class Constants {

  public final class ERROR_CODE {
    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
    public static final String WAREHOUSE_NOT_FOUND = "WAREHOUSE_NOT_FOUND";
  }

  public final class PageableConstant {

    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_PAGE_NUMBER = "0";
  }

  public final class ApiConstant {

    public static final String WAREHOUSE_URL = "/backoffice/warehouses";

    public static final String STOCK_URL = "/backoffice/stocks";

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
