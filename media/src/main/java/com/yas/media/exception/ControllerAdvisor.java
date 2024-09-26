package com.yas.media.exception;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.UnsupportedMediaTypeException;
import com.yas.media.viewmodel.ErrorVm;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class ControllerAdvisor {

    private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";

    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ErrorVm> handleUnsupportedMediaTypeException(UnsupportedMediaTypeException ex,
                                                                       WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "File uploaded media type is not supported";

        return buildErrorResponse(status, message, null, ex, request, 400, "Unsupported media type");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorVm> handleNotFoundException(NotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 404, "");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorVm> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .toList();

        return buildErrorResponse(status, "Request information is not valid", errors, ex, null, 0, "");
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorVm> handleConstraintViolation(ConstraintViolationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> errors = ex.getConstraintViolations().stream()
            .map(violation -> String.format("%s %s: %s",
                violation.getRootBeanClass().getName(),
                violation.getPropertyPath(),
                violation.getMessage()))
            .toList();

        return buildErrorResponse(status, "Request information is not valid", errors, ex, null, 0, "");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorVm> handleIoException(RuntimeException ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 500, "RuntimeException");
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorVm> handleOtherException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, null, ex, request, 500, "");
    }

    private String getServletPath(WebRequest webRequest) {
        ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
        return servletRequest.getRequest().getServletPath();
    }

    private ResponseEntity<ErrorVm> buildErrorResponse(HttpStatus status, String message, List<String> errors,
                                                       Exception ex, WebRequest request, int statusCode, String title) {
        ErrorVm errorVm =
            new ErrorVm(status.toString(), title.isEmpty() ? status.getReasonPhrase() : title, message, errors);

        if (request != null) {
            log.error(ERROR_LOG_FORMAT, this.getServletPath(request), statusCode, message);
        }
        log.error(message, ex);
        return ResponseEntity.status(status).body(errorVm);
    }
}
