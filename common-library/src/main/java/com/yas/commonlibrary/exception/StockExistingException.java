package com.yas.commonlibrary.exception;

import com.yas.commonlibrary.utils.MessagesUtils;

public class StockExistingException extends RuntimeException {
    private final String message;

    public StockExistingException(String errorCode, Object... var2) {
        this.message = MessagesUtils.getMessage(errorCode, var2);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
