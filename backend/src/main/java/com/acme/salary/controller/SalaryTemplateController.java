package com.acme.salary.controller;

import com.acme.salary.dto.ApiResponse;
import com.acme.salary.dto.PageResponse;
import com.acme.salary.dto.SalaryAssignmentPreview;
import com.acme.salary.dto.SalaryAssignmentRequest;
import com.acme.salary.dto.SalaryTemplateRequest;
import com.acme.salary.dto.SalaryTemplateResponse;
import com.acme.salary.dto.SalaryVersionResponse;
import com.acme.salary.enums.TemplateStatus;
import com.acme.salary.service.SalaryTemplateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SalaryTemplateController {
    private final SalaryTemplateService service;

    public SalaryTemplateController(SalaryTemplateService service) {
        this.service = service;
    }

    @GetMapping("/salary-templates")
    public ApiResponse<PageResponse<SalaryTemplateResponse>> list(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) TemplateStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        return ApiResponse.success(
                200,
                service.list(q, country, status, page, size),
                "Salary templates retrieved.");
    }

    @PostMapping("/salary-templates")
    public ResponseEntity<ApiResponse<SalaryTemplateResponse>> create(
            @Valid @RequestBody SalaryTemplateRequest request) {
        SalaryTemplateResponse template = service.save(null, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(201, template, "Salary template created."));
    }

    @PutMapping("/salary-templates/{id}")
    public ApiResponse<SalaryTemplateResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody SalaryTemplateRequest request) {
        return ApiResponse.success(
                200,
                service.save(id, request),
                "Salary template updated.");
    }

    @PostMapping("/salary-assignments/preview")
    public ApiResponse<SalaryAssignmentPreview> preview(
            @Valid @RequestBody SalaryAssignmentRequest request) {
        return ApiResponse.success(
                200,
                service.preview(request),
                "Review this salary breakdown before confirming.");
    }

    @PostMapping("/salary-assignments")
    public ResponseEntity<ApiResponse<SalaryVersionResponse>> assign(
            @Valid @RequestBody SalaryAssignmentRequest request) {
        SalaryVersionResponse salary = service.assign(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(201, salary, "Salary assigned. History has been preserved."));
    }
}
