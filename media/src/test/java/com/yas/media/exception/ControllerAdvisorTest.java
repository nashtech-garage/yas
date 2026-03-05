package com.yas.media.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.UnsupportedMediaTypeException;
import com.yas.media.viewmodel.ErrorVm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

class ControllerAdvisorTest {

    private ControllerAdvisor controllerAdvisor;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        controllerAdvisor = new ControllerAdvisor();
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setServletPath("/test");
        webRequest = new ServletWebRequest(httpRequest);
    }

    @Test
    void handleUnsupportedMediaTypeException_thenReturn400() {
        UnsupportedMediaTypeException ex = new UnsupportedMediaTypeException("Unsupported type");

        ResponseEntity<ErrorVm> response =
            controllerAdvisor.handleUnsupportedMediaTypeException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("File uploaded media type is not supported", response.getBody().detail());
    }

    @Test
    void handleNotFoundException_thenReturn404() {
        NotFoundException ex = new NotFoundException("Media 1 is not found");

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleNotFoundException(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Media 1 is not found", response.getBody().detail());
    }

    @Test
    void handleMethodArgumentNotValid_thenReturn400WithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("mediaPostVm", "multipartFile", "must not be null");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleMethodArgumentNotValid(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().fieldErrors().size());
        assertEquals("multipartFile must not be null", response.getBody().fieldErrors().get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleConstraintViolation_thenReturn400WithErrors() {
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getRootBeanClass()).thenReturn((Class) String.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("fieldName");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().fieldErrors().size());
    }

    @Test
    void handleRuntimeException_thenReturn500() {
        RuntimeException ex = new RuntimeException("Unexpected runtime error");

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleIoException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unexpected runtime error", response.getBody().detail());
    }

    @Test
    void handleOtherException_thenReturn500() throws Exception {
        Exception ex = new Exception("Other unexpected error");

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleOtherException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Other unexpected error", response.getBody().detail());
    }
}
