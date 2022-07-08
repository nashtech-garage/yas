package com.yas.product.exception;

public class NotFoundException extends RuntimeException{

  public NotFoundException(final String message) {
    super(message);
  }
}
