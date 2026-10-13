package com.acme.salary.exception;

import com.acme.salary.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void oversizedUploadReturns413WithMatchingEnvelope() {
        ResponseEntity<ApiResponse<Void>> response = handler.uploadTooLarge();

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertEquals(413, response.getBody().status());
        assertFalse(response.getBody().success());
    }

    @Test
    void notFoundUsesHttpStatusAndTheSharedErrorEnvelope() {
        ResponseEntity<ApiResponse<Void>> response = handler.notFound(new NotFoundException("Employee not found."));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().status());
        assertFalse(response.getBody().success());
        assertEquals("Employee not found.", response.getBody().message());
        assertEquals(Map.of(), response.getBody().errors());
    }

    @Test
    void conflictUsesHttpStatusAndTheSharedErrorEnvelope() {
        ResponseEntity<ApiResponse<Void>> response = handler.conflict(new ConflictException("Email is in use."));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().status());
        assertFalse(response.getBody().success());
        assertEquals("Email is in use.", response.getBody().message());
    }
}
