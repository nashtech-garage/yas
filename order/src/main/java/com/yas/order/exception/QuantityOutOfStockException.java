package com.yas.order.exception;

import com.yas.order.utils.MessagesUtils;

public class QuantityOutOfStockException extends RuntimeException {
    private String message;

    public QuantityOutOfStockException(String errorCode, Object... var2) {
        this.message = MessagesUtils.getMessage(errorCode, var2);
    }

    public QuantityOutOfStockException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
