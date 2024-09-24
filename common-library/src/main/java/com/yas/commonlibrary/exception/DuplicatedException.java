package com.yas.commonlibrary.exception;

import com.yas.commonlibrary.utils.MessagesUtils;

public class DuplicatedException extends RuntimeException {

    private final String message;

    public DuplicatedException(String errorCode, Object... var2) {
        this.message = MessagesUtils.getMessage(errorCode, var2);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
