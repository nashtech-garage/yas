package com.yas.media.exception;

/**
 * Exception thrown when file operations fail in the media service.
 */
public class MediaFileException extends RuntimeException {

    public MediaFileException(String message) {
        super(message);
    }

    public MediaFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaFileException(Throwable cause) {
        super(cause);
    }
}
