package com.acme.salary.controller;

import com.acme.salary.dto.ApiResponse;
import com.acme.salary.dto.PayInsightsResponse;
import com.acme.salary.dto.SalaryOutlierResponse;
import com.acme.salary.service.InsightsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/insights")
public class InsightsController {
    private static final List<String> SUPPORTED_COUNTRIES = List.of("US", "IN", "GB", "DE", "CA");

    private final InsightsService insightsService;

    public InsightsController(InsightsService insightsService) {
        this.insightsService = insightsService;
    }

    @GetMapping("/pay")
    public ApiResponse<PayInsightsResponse> pay(
            @RequestParam(defaultValue = "country") String groupBy,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String jobTitle) {
        PayInsightsResponse result = insightsService.pay(
                groupBy, validateCountry(country), parseDepartment(department), jobTitle);
        return ApiResponse.success(HttpStatus.OK.value(), result, "Pay insights retrieved.");
    }

    @GetMapping("/outliers")
    public ApiResponse<List<SalaryOutlierResponse>> outliers(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String jobTitle) {
        List<SalaryOutlierResponse> result = insightsService.outliers(
                validateCountry(country), parseDepartment(department), jobTitle);
        return ApiResponse.success(HttpStatus.OK.value(), result, "Salary outliers retrieved.");
    }

    private String validateCountry(String country) {
        if (country == null || country.isBlank()) {
            return null;
        }
        String normalized = country.trim().toUpperCase();
        if (!SUPPORTED_COUNTRIES.contains(normalized)) {
            throw new IllegalArgumentException("Country must be one of the supported country codes.");
        }
        return normalized;
    }

    private UUID parseDepartment(String department) {
        if (department == null || department.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(department);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Department must be a valid UUID.");
        }
    }
}
