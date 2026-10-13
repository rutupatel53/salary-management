package com.acme.salary.dto;
public record ApiResponse<T>(T data, String message) { }
