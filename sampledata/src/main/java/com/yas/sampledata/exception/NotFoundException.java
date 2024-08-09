package com.yas.sampledata.exception;

import com.yas.sampledata.utils.MessagesUtils;

public class NotFoundException extends RuntimeException{

  private String message;

  public NotFoundException(String errorCode, Object... var2) {
    this.message = MessagesUtils.getMessage(errorCode, var2);
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
