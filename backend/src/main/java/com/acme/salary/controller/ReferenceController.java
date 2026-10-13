package com.acme.salary.controller;

import com.acme.salary.dto.ApiResponse;
import com.acme.salary.dto.CountryResponse;
import com.acme.salary.dto.DepartmentResponse;
import com.acme.salary.service.ReferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReferenceController {
    private final ReferenceService referenceService;

    public ReferenceController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @GetMapping("/countries")
    public ApiResponse<List<CountryResponse>> countries() {
        return ApiResponse.success(HttpStatus.OK.value(), referenceService.countries(), "Countries retrieved.");
    }

    @GetMapping("/departments")
    public ApiResponse<List<DepartmentResponse>> departments() {
        return ApiResponse.success(HttpStatus.OK.value(), referenceService.departments(), "Departments retrieved.");
    }
}
