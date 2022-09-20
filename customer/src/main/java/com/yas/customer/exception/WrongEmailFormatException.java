package com.yas.customer.exception;

public class WrongEmailFormatException extends RuntimeException {

    public WrongEmailFormatException(final String message) {
        super(message);
    }
}
