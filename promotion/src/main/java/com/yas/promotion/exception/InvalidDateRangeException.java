package com.yas.promotion.exception;

import com.yas.promotion.utils.MessagesUtils;

public class InvalidDateRangeException extends RuntimeException {

    private String message;

    public InvalidDateRangeException(String message, Object... var2) {
        this.message = MessagesUtils.getMessage(message, var2);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
