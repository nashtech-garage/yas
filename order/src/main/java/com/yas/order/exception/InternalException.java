package com.yas.order.exception;

import com.yas.order.utils.MessagesUtils;

public class InternalException extends RuntimeException {
    private String message;

    public InternalException(String errorCode, Object... var2) {
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
