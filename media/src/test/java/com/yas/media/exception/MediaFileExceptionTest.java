package com.yas.media.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class MediaFileExceptionTest {

    @Test
    void testMediaFileExceptionWithMessage() {
        String message = "File operation failed";
        MediaFileException exception = new MediaFileException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testMediaFileExceptionWithMessageAndCause() {
        String message = "Failed to read file";
        IOException cause = new IOException("IO Error");
        MediaFileException exception = new MediaFileException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testMediaFileExceptionWithCause() {
        IOException cause = new IOException("Underlying IO error");
        MediaFileException exception = new MediaFileException(cause);

        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("java.io.IOException"));
    }

    @Test
    void testMediaFileExceptionIsRuntimeException() {
        MediaFileException exception = new MediaFileException("Test");

        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testMediaFileExceptionStackTrace() {
        IOException cause = new IOException("File not found");
        MediaFileException exception = new MediaFileException("Cannot access file", cause);

        assertNotNull(exception.getStackTrace());
        assertEquals("Cannot access file", exception.getMessage());
    }
}
