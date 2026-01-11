package com.yourorg.securesaas.api.common;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiError> handleIllegalState(
      IllegalStateException ex, HttpServletRequest request) {
    return buildError(HttpStatus.LOCKED, ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    return buildError(HttpStatus.BAD_REQUEST, "validation failed", request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    return buildError(HttpStatus.FORBIDDEN, "forbidden", request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "unexpected error", request);
  }

  private ResponseEntity<ApiError> buildError(
      HttpStatus status, String message, HttpServletRequest request) {
    String requestId = (String) request.getAttribute("requestId");
    ApiError error = new ApiError(message, requestId, OffsetDateTime.now());
    return ResponseEntity.status(status).body(error);
  }
}
