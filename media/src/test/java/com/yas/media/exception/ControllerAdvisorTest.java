package com.yas.media.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.UnsupportedMediaTypeException;
import com.yas.media.viewmodel.ErrorVm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

class ControllerAdvisorTest {

    private ControllerAdvisor controllerAdvisor;

    @BeforeEach
    void setUp() {
        controllerAdvisor = new ControllerAdvisor();
    }

    private ServletWebRequest createMockServletWebRequest(String servletPath) {
        ServletWebRequest request = mock(ServletWebRequest.class);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(request.getRequest()).thenReturn(httpRequest);
        when(httpRequest.getServletPath()).thenReturn(servletPath);
        return request;
    }

    private MethodParameter createMockMethodParameter() {
        MethodParameter parameter = mock(MethodParameter.class);
        Method method = mock(Method.class);
        when(parameter.getExecutable()).thenReturn(method);
        when(method.toGenericString()).thenReturn("void testMethod()");
        return parameter;
    }

    @Test
    void handleUnsupportedMediaTypeException_thenReturnBadRequest() {
        UnsupportedMediaTypeException exception = new UnsupportedMediaTypeException("Unsupported type");
        ServletWebRequest request = createMockServletWebRequest("/medias");

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleUnsupportedMediaTypeException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unsupported media type", response.getBody().title());
        assertEquals("File uploaded media type is not supported", response.getBody().detail());
    }

    @Test
    void handleNotFoundException_thenReturn404() {
        NotFoundException exception = new NotFoundException("Media 1 is not found");
        ServletWebRequest request = createMockServletWebRequest("/medias/1");

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleNotFoundException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void handleMethodArgumentNotValid_thenReturnBadRequestWithFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("mediaPostVm", "multipartFile", "may not be null");
        FieldError fieldError2 = new FieldError("mediaPostVm", "caption", "size must be between 0 and 255");

        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError1, fieldError2));

        MethodParameter parameter = createMockMethodParameter();
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleMethodArgumentNotValid(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request information is not valid", response.getBody().detail());
        assertEquals(2, response.getBody().fieldErrors().size());
    }

    @Test
    void handleConstraintViolation_thenReturnBadRequestWithViolationMessages() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation1 = mock(ConstraintViolation.class);
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation2 = mock(ConstraintViolation.class);

        Path path1 = mock(Path.class);
        Path path2 = mock(Path.class);

        when(violation1.getRootBeanClass()).thenReturn((Class) String.class);
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(path1.toString()).thenReturn("fileName");
        when(violation1.getMessage()).thenReturn("must not be blank");

        when(violation2.getRootBeanClass()).thenReturn((Class) String.class);
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(path2.toString()).thenReturn("caption");
        when(violation2.getMessage()).thenReturn("size must be between 0 and 100");

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation1);
        violations.add(violation2);

        ConstraintViolationException exception = new ConstraintViolationException("Constraint violation", violations);

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleConstraintViolation(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request information is not valid", response.getBody().detail());
        assertEquals(2, response.getBody().fieldErrors().size());
    }

    @Test
    void handleRuntimeException_thenReturnInternalServerError() {
        RuntimeException exception = new RuntimeException("File system error");
        ServletWebRequest request = createMockServletWebRequest("/medias");

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleIoException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("File system error", response.getBody().detail());
        assertEquals("RuntimeException", response.getBody().title());
    }

    @Test
    void handleOtherException_thenReturnInternalServerError() {
        Exception exception = new Exception("Unexpected error");
        ServletWebRequest request = createMockServletWebRequest("/medias");

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleOtherException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unexpected error", response.getBody().detail());
    }

    @Test
    void handleUnsupportedMediaTypeException_whenRequestIsNull_thenReturnBadRequest() {
        UnsupportedMediaTypeException exception = new UnsupportedMediaTypeException("Invalid media");

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleUnsupportedMediaTypeException(exception, null);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unsupported media type", response.getBody().title());
    }

    @Test
    void handleNotFoundException_whenNotFound_thenUseExceptionMessage() {
        NotFoundException exception = new NotFoundException("Media not found");
        ServletWebRequest request = createMockServletWebRequest("/medias/999");

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleNotFoundException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleConstraintViolation_withEmptyViolations_thenReturnBadRequest() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("No violations", violations);

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleConstraintViolation(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request information is not valid", response.getBody().detail());
    }

    @Test
    void handleMethodArgumentNotValid_withNoErrors_thenReturnBadRequest() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of());

        MethodParameter parameter = createMockMethodParameter();
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleMethodArgumentNotValid(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request information is not valid", response.getBody().detail());
    }

    @Test
    void handleRuntimeException_withDifferentMessage_thenReturnCorrectError() {
        RuntimeException exception = new RuntimeException("Database connection failed");
        ServletWebRequest request = createMockServletWebRequest("/medias");

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleIoException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Database connection failed", response.getBody().detail());
    }

    @Test
    void handleMethodArgumentNotValid_withSingleFieldError_thenReturnErrorList() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("mediaPostVm", "fileName", "must not be empty");

        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        MethodParameter parameter = createMockMethodParameter();
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorVm> response = controllerAdvisor.handleMethodArgumentNotValid(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().fieldErrors().size());
        assertEquals("fileName must not be empty", response.getBody().fieldErrors().get(0));
    }
}
