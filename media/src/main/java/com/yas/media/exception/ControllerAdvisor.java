package com.yas.media.exception;

import com.yas.media.viewmodel.ErrorVm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ControllerAdvisor {
    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<Object> handleUnsupportedMediaTypeException(UnsupportedMediaTypeException ex, WebRequest request) {
        ErrorVm errorVm = new ErrorVm("400", "Unsupported media type", "File uploaded media type is not supported");
        return new ResponseEntity<>(errorVm, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorVm> handleNotFoundException(NotFoundException ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.NOT_FOUND.toString(), "NotFound", message);
        return new ResponseEntity<>(errorVm, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .toList();

        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Request information is not valid", errors);
        return ResponseEntity.badRequest().body(errorVm);
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Request information is not valid", errors);
        return ResponseEntity.badRequest().body(errorVm);
    }
}
