package com.acme.salary.dto;

import java.util.Map;

/** A stable JSON envelope shared by successful and failed API responses. */
public record ApiResponse<T>(
        int status,
        boolean success,
        String message,
        T data,
        Map<String, String> errors) {

    public static <T> ApiResponse<T> success(int status, T data, String message) {
        return new ApiResponse<>(status, true, message, data, Map.of());
    }

    public static <T> ApiResponse<T> failure(int status, String message, Map<String, String> errors, T data) {
        return new ApiResponse<>(status, false, message, data, errors == null ? Map.of() : Map.copyOf(errors));
    }
}
