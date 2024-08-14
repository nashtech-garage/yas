package com.yas.media.exception;

import com.yas.media.viewmodel.ErrorVm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ControllerAdvisor {
    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ErrorVm> handleUnsupportedMediaTypeException(UnsupportedMediaTypeException ex,
                                                                       WebRequest request) {
        ErrorVm errorVm = new ErrorVm(HttpStatus.BAD_REQUEST.toString(), "Unsupported media type",
            "File uploaded media type is not supported");
        return ResponseEntity.badRequest().body(errorVm);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorVm> handleNotFoundException(NotFoundException ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.NOT_FOUND.toString(), "NotFound", message);
        return new ResponseEntity<>(errorVm, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorVm> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .toList();

        ErrorVm errorVm =
            new ErrorVm(HttpStatus.BAD_REQUEST.toString(), "Bad Request",
                "Request information is not valid", errors);
        return ResponseEntity.badRequest().body(errorVm);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorVm> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " "
                + violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ErrorVm errorVm = new ErrorVm("400", "Bad Request",
            "Request information is not valid", errors);
        return ResponseEntity.badRequest().body(errorVm);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorVm> handleOtherException(Exception ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), message);
        return new ResponseEntity<>(errorVm, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
