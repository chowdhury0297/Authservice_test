package com.test.auth.exception;
import com.test.auth.DTO.ErrorResponse;
import com.test.auth.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler  {

    /**
     * Handles {@link UserAlreadyExistsException} and returns a 400 Bad Request.
     *
     * @param ex the exception instance
     * @return a structured error response with timestamp and message
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistsException ex) {
        var errorDetail = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                Instant.now()
        );
        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles {@link UserNotFoundException} and returns a 404 Not Found.
     *
     * @param ex the exception instance
     * @return a 404 response without body
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return  ResponseEntity.notFound().build();
    }

    /**
     * Handles validation errors triggered by {@code @Valid} annotated DTOs.
     *
     * @param ex the validation exception
     * @return a list of field-specific validation errors with 400 Bad Request status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Catches all other unhandled exceptions and returns a generic error message.
     *
     * @param ex the unexpected exception
     * @return a 500 Internal Server Error with a generic error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        var errorDetail = new ErrorResponse(
                "Internal error: something went wrong "+ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now()
        );
        return new ResponseEntity<>(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
