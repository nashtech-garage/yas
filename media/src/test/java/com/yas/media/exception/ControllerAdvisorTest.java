package com.yas.media.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.UnsupportedMediaTypeException;
import com.yas.media.exception.ControllerAdvisor;
import com.yas.media.viewmodel.ErrorVm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.io.IOException;

class ControllerAdvisorTest {

    private final ControllerAdvisor advisor = new ControllerAdvisor();

    @Test
    void handleUnsupportedMediaType_returnsBadRequest() {
        UnsupportedMediaTypeException ex = new UnsupportedMediaTypeException("bad");
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath("/uploader");
        WebRequest webRequest = new ServletWebRequest(req);

        var resp = advisor.handleUnsupportedMediaTypeException(ex, webRequest);
        assertThat(resp.getStatusCode().value()).isEqualTo(400);
        ErrorVm body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.title()).isEqualTo("Unsupported media type");
    }

    @Test
    void handleNotFound_returnsNotFound() {
        NotFoundException ex = new NotFoundException("not found");
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath("/none");
        WebRequest webRequest = new ServletWebRequest(req);

        var resp = advisor.handleNotFoundException(ex, webRequest);
        assertThat(resp.getStatusCode().value()).isEqualTo(404);
        ErrorVm body = resp.getBody();
        assertThat(body.detail()).isEqualTo("not found");
    }

    @Test
    void handleMethodArgumentNotValid_buildsErrorList() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(br);
        when(br.getFieldErrors()).thenReturn(
            Collections.singletonList(new FieldError("obj", "field", "must not be null")));

        var resp = advisor.handleMethodArgumentNotValid(ex);
        assertThat(resp.getStatusCode().value()).isEqualTo(400);
        ErrorVm body = resp.getBody();
        assertThat(body.fieldErrors()).isNotEmpty();
    }

    @Test
    void handleConstraintViolation_buildsErrorList() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        @SuppressWarnings("rawtypes")
        ConstraintViolation cv = mock(ConstraintViolation.class);
        when(cv.getRootBeanClass()).thenReturn((Class) ControllerAdvisorTest.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("p");
        when(cv.getPropertyPath()).thenReturn(path);
        when(cv.getMessage()).thenReturn("msg");
        when(ex.getConstraintViolations()).thenReturn(Set.of(cv));

        var resp = advisor.handleConstraintViolation(ex);
       assertThat(resp.getStatusCode().value()).isEqualTo(400);
        ErrorVm body = resp.getBody();
        assertThat(body.fieldErrors()).isNotEmpty();
    }

    @Test
    void handleRuntimeException_returns500() {
        RuntimeException ex = new RuntimeException("boom");
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath("/err");
        WebRequest webRequest = new ServletWebRequest(req);

        var resp = advisor.handleIoException(ex, webRequest);
        assertThat(resp.getStatusCode().value()).isEqualTo(500);
    }

    @Test
    void handleOtherException_returns500() {
        Exception ex = new Exception("other");
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath("/err");
        WebRequest webRequest = new ServletWebRequest(req);

        var resp = advisor.handleOtherException(ex, webRequest);
        assertThat(resp.getStatusCode().value()).isEqualTo(500);
    }
}
