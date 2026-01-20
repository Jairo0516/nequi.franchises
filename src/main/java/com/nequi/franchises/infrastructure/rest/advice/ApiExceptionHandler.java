package com.nequi.franchises.infrastructure.rest.advice;

import com.nequi.franchises.domain.exceptions.BusinessRuleException;
import com.nequi.franchises.domain.exceptions.ConflictException;
import com.nequi.franchises.domain.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

  record ApiError(
      Instant timestamp,
      int status,
      String error,
      String message,
      String path,
      Map<String, Object> details
  ) {}

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
  }

  @ExceptionHandler({BusinessRuleException.class, IllegalArgumentException.class})
  public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req, null);
  }

  @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class})
  public ResponseEntity<ApiError> handleConflict(Exception ex, HttpServletRequest req) {
    String msg = (ex instanceof ConflictException) ? ex.getMessage() : "Conflict / constraint violation";
    return build(HttpStatus.CONFLICT, msg, req, null);
  }

  @ExceptionHandler({ObjectOptimisticLockingFailureException.class})
  public ResponseEntity<ApiError> handleOptimisticLock(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.CONFLICT, "Concurrent update conflict. Please retry.", req, null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    Map<String, Object> details = new LinkedHashMap<>();
    Map<String, String> fieldErrors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
    details.put("fields", fieldErrors);
    return build(HttpStatus.BAD_REQUEST, "Validation failed", req, details);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", req, Map.of("exception", ex.getClass().getSimpleName()));
  }

  private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest req, Map<String, Object> details) {
    ApiError body = new ApiError(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        req.getRequestURI(),
        details
    );
    return ResponseEntity.status(status).body(body);
  }
}
