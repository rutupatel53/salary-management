package com.acme.salary.controller;

import com.acme.salary.dto.ApiResponse;
import com.acme.salary.dto.EmployeeRequest;
import com.acme.salary.dto.EmployeeResponse;
import com.acme.salary.dto.PageResponse;
import com.acme.salary.service.EmployeeExportService;
import com.acme.salary.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/employees")
public class EmployeeController {
    private static final MediaType XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final EmployeeService employeeService;
    private final EmployeeExportService exportService;

    public EmployeeController(EmployeeService employeeService, EmployeeExportService exportService) {
        this.employeeService = employeeService;
        this.exportService = exportService;
    }

    @GetMapping
    public ApiResponse<PageResponse<EmployeeResponse>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        validatePagination(page, size);
        return ApiResponse.success(HttpStatus.OK.value(),
                employeeService.list(q, country, parseDepartment(department), jobTitle, page, size),
                "Employees retrieved.");
    }

    @GetMapping("/{id}")
    public ApiResponse<EmployeeResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(HttpStatus.OK.value(), employeeService.get(id), "Employee retrieved.");
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String jobTitle) {
        byte[] workbook = exportService.export(q, country, parseDepartment(department), jobTitle);
        return ResponseEntity.ok()
                .contentType(XLSX)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=salary-management-employees.xlsx")
                .body(workbook);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(), employee, "Employee added."));
    }

    @PutMapping("/{id}")
    public ApiResponse<EmployeeResponse> update(
            @PathVariable UUID id, @Valid @RequestBody EmployeeRequest request) {
        return ApiResponse.success(HttpStatus.OK.value(), employeeService.update(id, request), "Employee saved.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> archive(@PathVariable UUID id) {
        employeeService.archive(id);
        return ApiResponse.success(HttpStatus.OK.value(), null, "Employee archived.");
    }

    private void validatePagination(int page, int size) {
        PageRequest.of(page, size);
        if (size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100.");
        }
    }

    private UUID parseDepartment(String department) {
        if (department == null || department.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(department);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Department filter must be a UUID.");
        }
    }
}
