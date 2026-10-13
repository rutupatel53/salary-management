package com.acme.salary.controller;

import com.acme.salary.dto.ApiResponse;
import com.acme.salary.dto.SalaryChangeRequest;
import com.acme.salary.dto.SalaryVersionResponse;
import com.acme.salary.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees/{id}/salary-history")
public class SalaryController {
    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping
    public ApiResponse<List<SalaryVersionResponse>> history(@PathVariable UUID id) {
        return ApiResponse.success(HttpStatus.OK.value(), salaryService.history(id), "Salary history retrieved.");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SalaryVersionResponse>> append(
            @PathVariable UUID id, @Valid @RequestBody SalaryChangeRequest request) {
        SalaryVersionResponse version = salaryService.append(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(), version, "Salary version added."));
    }
}
