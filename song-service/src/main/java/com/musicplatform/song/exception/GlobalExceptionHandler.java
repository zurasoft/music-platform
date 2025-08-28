package com.musicplatform.song.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Error message constants
    private static final String VALIDATION_ERROR_MESSAGE = "Validation error";
    private static final String GENERIC_ERROR_MESSAGE = "An error occurred on the server";
    private static final String ALREADY_EXISTS_KEYWORD = "already exists";

    // HTTP status codes as strings
    private static final String BAD_REQUEST_CODE = "400";
    private static final String CONFLICT_CODE = "409";
    private static final String INTERNAL_SERVER_ERROR_CODE = "500";

    // Response field names
    private static final String ERROR_MESSAGE_FIELD = "errorMessage";
    private static final String ERROR_CODE_FIELD = "errorCode";
    private static final String DETAILS_FIELD = "details";

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        if (ex.getMessage().contains(ALREADY_EXISTS_KEYWORD)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            ERROR_MESSAGE_FIELD, ex.getMessage(),
                            ERROR_CODE_FIELD, CONFLICT_CODE));
        }

        return ResponseEntity.badRequest()
                .body(Map.of(
                        ERROR_MESSAGE_FIELD, ex.getMessage(),
                        ERROR_CODE_FIELD, BAD_REQUEST_CODE));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                details.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(Map.of(
                        ERROR_MESSAGE_FIELD, VALIDATION_ERROR_MESSAGE,
                        DETAILS_FIELD, details,
                        ERROR_CODE_FIELD, BAD_REQUEST_CODE));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        ERROR_MESSAGE_FIELD, GENERIC_ERROR_MESSAGE,
                        ERROR_CODE_FIELD, INTERNAL_SERVER_ERROR_CODE));
    }
}
