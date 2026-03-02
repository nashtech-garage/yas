package com.yas.commonlibrary.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.commonlibrary.viewmodel.error.ErrorVm;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler apiExceptionHandler = new ApiExceptionHandler();

    @Test
    void testHandleNotFoundException() {
        // Given
        String errorMessage = "Resource not found";
        NotFoundException exception = new NotFoundException(errorMessage);

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleNotFoundException(exception, null);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().detail());
        assertEquals(HttpStatus.NOT_FOUND.toString(), response.getBody().statusCode());
    }

    @Test
    void testHandleBadRequestException() {
        // Given
        String errorMessage = "Invalid input";
        BadRequestException exception = new BadRequestException(errorMessage);

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleBadRequestException(exception, null);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().detail());
    }

    @Test
    void testHandleAccessDeniedException() {
        // Given
        String errorMessage = "Access denied";
        AccessDeniedException exception = new AccessDeniedException(errorMessage);

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleAccessDeniedException(exception, null);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().detail());
    }

    @Test
    void testHandleResourceExistedException() {
        // Given
        String errorMessage = "Resource already exists";
        ResourceExistedException exception = new ResourceExistedException(errorMessage);

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleResourceExistedException(exception, null);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().detail());
    }

    @Test
    void testHandleInternalServerErrorException() {
        // Given
        String errorMessage = "Internal error";
        InternalServerErrorException exception = new InternalServerErrorException(errorMessage);

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleInternalServerErrorException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().detail());
    }

    @Test
    void testHandleDuplicatedException() {
        // Given
        String errorMessage = "Duplicate entry";
        DuplicatedException exception = new DuplicatedException(errorMessage);

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleDuplicated(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().detail());
    }

    @Test
    void testHandleDataIntegrityViolationException() {
        // Given
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Data integrity violation");

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleDataIntegrityViolationException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testHandleWrongEmailFormatException() {
        // Given
        String errorMessage = "Wrong email format";
        WrongEmailFormatException exception = new WrongEmailFormatException(errorMessage);

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleWrongEmailFormatException(exception, null);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().detail());
    }

    @Test
    void testHandleCreateGuestUserException() {
        // Given
        String errorMessage = "Failed to create guest user";
        CreateGuestUserException exception = new CreateGuestUserException(errorMessage);

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleCreateGuestUserException(exception, null);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testHandleStockExistingException() {
        // Given
        String errorMessage = "Stock exists";
        StockExistingException exception = new StockExistingException(errorMessage);

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleStockExistingException(exception, null);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testHandleSignInRequiredException() {
        // Given
        String errorMessage = "Sign in required";
        SignInRequiredException exception = new SignInRequiredException(errorMessage);

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleSignInRequired(exception);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().detail());
    }

    @Test
    void testHandleMissingServletRequestParameterException() {
        // Given
        MissingServletRequestParameterException exception = 
            new MissingServletRequestParameterException("param", "String");

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleMissingParams(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testHandleOtherException() {
        // Given
        String errorMessage = "Unexpected error";
        Exception exception = new RuntimeException(errorMessage);

        // When
        ResponseEntity<ErrorVm> response = apiExceptionHandler.handleOtherException(exception, null);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().detail());
    }
}
