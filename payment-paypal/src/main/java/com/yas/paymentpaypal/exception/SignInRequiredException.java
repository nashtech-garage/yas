package com.yas.paymentpaypal.exception;

import com.yas.paymentpaypal.utils.MessagesUtils;

public class SignInRequiredException extends RuntimeException {
    private String message;

    public SignInRequiredException(String errorCode, Object... var2) {
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
