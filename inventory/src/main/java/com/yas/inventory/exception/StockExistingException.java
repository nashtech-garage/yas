package com.yas.inventory.exception;

import com.yas.inventory.utils.MessagesUtils;

public class StockExistingException extends RuntimeException {
    private String message;

    public StockExistingException(String errorCode, Object... var2) {
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
