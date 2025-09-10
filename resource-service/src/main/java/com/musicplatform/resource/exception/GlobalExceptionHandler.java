package com.musicplatform.resource.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .filter(f -> f.getDefaultMessage() != null)
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage));

        logger.warn("Validation failed: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                "Validation error",
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                details);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        logger.warn("Bad request: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        logger.warn("Not found: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                String.valueOf(HttpStatus.NOT_FOUND.value()));
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler(DataProcessingException.class)
    public ResponseEntity<ErrorResponse> handleMetadataExtraction(DataProcessingException ex) {
        logger.warn("Data processing error: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(InvalidResourceException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResourceException(InvalidResourceException invalidResourceException) {
        logger.warn("Invalid resource: {}", invalidResourceException.getMessage());
        ErrorResponse response = new ErrorResponse(
                invalidResourceException.getMessage(),
                String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException) {
        logger.warn("Not readable: {}", httpMessageNotReadableException.getMessage());
        ErrorResponse response = new ErrorResponse(
                "Malformed JSON request",
                String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
        logger.warn("Type mismatch: {}", methodArgumentTypeMismatchException.getMessage());
        ErrorResponse response = new ErrorResponse(
                String.format("Invalid value '%s' for ID. Must be a positive integer", methodArgumentTypeMismatchException.getValue()),
                String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        logger.warn("Unsupported media type: {}", ex.getMessage());
        String errorMessage = "Unsupported media type. Supported media type is audio/mpeg";

        if (MediaType.APPLICATION_JSON.equals(ex.getContentType())) {
            errorMessage = "Invalid file format: application/json. Only MP3 files are allowed";
        }

        ErrorResponse response = new ErrorResponse(
                errorMessage,
                String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        logger.warn(ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                "An error occurred on the server.",
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
