package com.yas.product.exception;

/**
 * @author toaitrano
 * @version 1.0
 * @since 2022/06/18
 */
public class NotFoundException extends RuntimeException{

  public NotFoundException(final String message) {
    super(message);
  }
}
