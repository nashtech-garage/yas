package com.yas.commonlibrary.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ExceptionsTest {

    @Test
    void testNotFoundException() {
        // Given
        String message = "Resource not found";

        // When
        NotFoundException exception = new NotFoundException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertNotNull(exception);
    }

    @Test
    void testBadRequestException() {
        // Given
        String message = "Invalid request";

        // When
        BadRequestException exception = new BadRequestException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testAccessDeniedException() {
        // Given
        String message = "Access denied";

        // When
        AccessDeniedException exception = new AccessDeniedException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testDuplicatedException() {
        // Given
        String message = "Duplicate entry";

        // When
        DuplicatedException exception = new DuplicatedException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testInternalServerErrorException() {
        // Given
        String message = "Internal server error";

        // When
        InternalServerErrorException exception = new InternalServerErrorException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testResourceExistedException() {
        // Given
        String message = "Resource already exists";

        // When
        ResourceExistedException exception = new ResourceExistedException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testCreateGuestUserException() {
        // Given
        String message = "Failed to create guest user";

        // When
        CreateGuestUserException exception = new CreateGuestUserException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testWrongEmailFormatException() {
        // Given
        String message = "Wrong email format";

        // When
        WrongEmailFormatException exception = new WrongEmailFormatException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testStockExistingException() {
        // Given
        String message = "Stock already exists";

        // When
        StockExistingException exception = new StockExistingException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testSignInRequiredException() {
        // Given
        String message = "Sign in required";

        // When
        SignInRequiredException exception = new SignInRequiredException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testForbiddenException() {
        // Given
        String message = "Forbidden";

        // When
        ForbiddenException exception = new ForbiddenException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testUnsupportedMediaTypeException() {
        // Given
        String message = "Unsupported media type";

        // When
        UnsupportedMediaTypeException exception = new UnsupportedMediaTypeException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testMultipartFileContentException() {
        // Given
        String message = "Invalid multipart file";

        // When
        MultipartFileContentException exception = new MultipartFileContentException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }
}
