package com.yas.customer.exception;

public class InternalErrorException extends  InternalError {
    public InternalErrorException(final String message) {
        super(message);
    }
}
