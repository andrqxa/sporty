package com.sporty.ticketing.api;

import com.sporty.ticketing.exception.*;
import org.springframework.http.*;
import org.springframework.validation.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Global exception handler that maps domain-specific and validation errors to corresponding HTTP
 * responses with proper status codes and error messages.
 *
 * <p>Uses Spring's {@link RestControllerAdvice} mechanism to catch exceptions thrown by controllers
 * and translate them into consistent API error responses.
 */
@RestControllerAdvice
public class ErrorHandler {

  /**
   * Handles {@link NotFoundException} by returning a 404 Not Found status and an error message in
   * the response body.
   *
   * @param e the {@code NotFoundException} thrown when a requested resource is not found
   * @return a {@link ResponseEntity} with HTTP 404 status and error details
   */
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> handleNotFound(NotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
  }

  /**
   * Handles {@link ConflictException} by returning a 409 Conflict status and an error message in
   * the response body.
   *
   * @param e the {@code ConflictException} thrown when a conflict occurs (e.g., concurrent update)
   * @return a {@link ResponseEntity} with HTTP 409 status and error details
   */
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<?> handleConflict(ConflictException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
  }

  /**
   * Handles common validation-related exceptions by returning a 400 Bad Request status and an error
   * message in the response body.
   *
   * <p>Specifically catches:
   *
   * <ul>
   *   <li>{@link MethodArgumentNotValidException} - thrown when method argument validation fails
   *   <li>{@link BindException} - thrown when binding request parameters fails
   *   <li>{@link IllegalArgumentException} - thrown for invalid method arguments
   * </ul>
   *
   * @param e the exception indicating a bad request due to invalid input
   * @return a {@link ResponseEntity} with HTTP 400 status and error details
   */
  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    BindException.class,
    IllegalArgumentException.class
  })
  public ResponseEntity<?> handleBadRequest(Exception e) {
    return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
  }
}
