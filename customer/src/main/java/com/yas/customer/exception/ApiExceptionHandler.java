package com.yas.customer.exception;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.CreateGuestUserException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.WrongEmailFormatException;
import com.yas.customer.viewmodel.ErrorVm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorVm> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, ex, request, 403, "Access Denied");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorVm> handleNotFoundException(NotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, ex, request, 404, "");
    }

    @ExceptionHandler(WrongEmailFormatException.class)
    public ResponseEntity<ErrorVm> handleWrongEmailFormatException(WrongEmailFormatException ex, WebRequest request) {
        return handleBadRequest(ex, request);
    }

    @ExceptionHandler(CreateGuestUserException.class)
    public ResponseEntity<ErrorVm> handleCreateGuestUserException(CreateGuestUserException ex, WebRequest request) {
        return handleBadRequest(ex, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorVm> handleOtherException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, ex, request, 500, "");
    }

    private String getServletPath(WebRequest webRequest) {
        ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
        return servletRequest.getRequest().getServletPath();
    }

    private ResponseEntity<ErrorVm> handleBadRequest(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getMessage();

        return buildErrorResponse(status, message, ex, request, 400, "");
    }

    private ResponseEntity<ErrorVm> buildErrorResponse(HttpStatus status, String message,
                                                       Exception ex, WebRequest request, int statusCode, String title) {
        ErrorVm errorVm =
            new ErrorVm(status.toString(), title.isEmpty() ? status.getReasonPhrase() : title, message);

        if (request != null) {
            log.error(ERROR_LOG_FORMAT, this.getServletPath(request), statusCode, message);
        }
        log.error(message, ex);
        return ResponseEntity.status(status).body(errorVm);
    }
}
