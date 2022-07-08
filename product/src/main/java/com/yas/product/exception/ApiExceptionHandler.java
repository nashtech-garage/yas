package com.yas.product.exception;

import com.yas.product.viewmodel.ErrorVm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
  private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorVm> handleNotFoundException(NotFoundException ex, WebRequest request) {
    String message = ex.getMessage();
    ErrorVm errorVm = new ErrorVm(HttpStatus.NOT_FOUND.toString(), "NotFound", message);
    log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), 404, message);
    log.debug(ex.toString());
    return new ResponseEntity<>(errorVm, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorVm> handleBadRequestException(BadRequestException ex, WebRequest request) {
    String message = ex.getMessage();
    ErrorVm errorVm = new ErrorVm(HttpStatus.BAD_REQUEST.toString(), "Bad request", message);
    return ResponseEntity.badRequest().body(errorVm);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                HttpStatus status, WebRequest request) {
    List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .toList();

    ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Request information is not valid", errors);
    return ResponseEntity.badRequest().body(errorVm);
  }

  private String getServletPath(WebRequest webRequest) {
    ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
    return servletRequest.getRequest().getServletPath();
  }
}
