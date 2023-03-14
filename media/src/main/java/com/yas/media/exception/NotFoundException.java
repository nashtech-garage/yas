package com.yas.media.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super();
    }

    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
