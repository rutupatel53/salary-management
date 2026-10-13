package com.acme.salary.exception;

import com.acme.salary.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.util.Map;
import java.util.TreeMap;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> notFound(NotFoundException exception) {
        return failure(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of(), null);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> conflict(ConflictException exception) {
        return failure(HttpStatus.CONFLICT, exception.getMessage(), Map.of(), null);
    }

    @ExceptionHandler(ImportValidationException.class)
    public ResponseEntity<ApiResponse<Object>> importValidation(ImportValidationException exception) {
        return failure(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of(), exception.getPreview());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> invalidRequest(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new TreeMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return failure(HttpStatus.BAD_REQUEST, "Please correct the highlighted fields.", errors, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> integrityConflict() {
        return failure(HttpStatus.CONFLICT,
                "A matching record already exists or the record is referenced.", Map.of(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> badRequest(IllegalArgumentException exception) {
        return failure(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of(), null);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestPartException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiResponse<Void>> malformedRequest(Exception exception) {
        return failure(HttpStatus.BAD_REQUEST, "Request data is missing or invalid.", Map.of(), null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> unsupportedMediaType() {
        return failure(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "This request content type is not supported.", Map.of(), null);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ApiResponse<Void>> unknownRoute() {
        return failure(HttpStatus.NOT_FOUND, "The requested endpoint was not found.", Map.of(), null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> unsupportedMethod() {
        return failure(HttpStatus.METHOD_NOT_ALLOWED, "This method is not supported for the endpoint.", Map.of(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> unexpected(Exception exception) {
        LOGGER.error("Unhandled API error", exception);
        return failure(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred.", Map.of(), null);
    }

    private <T> ResponseEntity<ApiResponse<T>> failure(
            HttpStatus status, String message, Map<String, String> errors, T data) {
        return ResponseEntity.status(status)
                .body(ApiResponse.failure(status.value(), message, errors, data));
    }
}
