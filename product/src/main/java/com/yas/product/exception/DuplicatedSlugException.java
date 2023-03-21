package com.yas.product.exception;

import com.yas.product.utils.MessagesUtils;

public class DuplicatedSlugException extends RuntimeException{
    private String message;

    public DuplicatedSlugException(String errorCode, Object... var2) {
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
