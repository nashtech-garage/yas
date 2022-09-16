package com.yas.customer.exception;

import com.yas.customer.viewmodel.ErrorVm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorVm> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.FORBIDDEN.toString(), "Access Denied", message);
        return new ResponseEntity<>(errorVm, HttpStatus.FORBIDDEN);
    }
}
