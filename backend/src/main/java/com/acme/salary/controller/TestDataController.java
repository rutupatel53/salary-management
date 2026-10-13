package com.acme.salary.controller;

import com.acme.salary.dto.ApiResponse;
import com.acme.salary.seed.TestDataResetService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@ConditionalOnProperty(name = "app.test-tools-enabled", havingValue = "true")
public class TestDataController {
    private final TestDataResetService resetService;

    public TestDataController(TestDataResetService resetService) {
        this.resetService = resetService;
    }

    @PostMapping("/reset")
    public ApiResponse<ResetResponse> reset() {
        return ApiResponse.success(HttpStatus.OK.value(),
                new ResetResponse(resetService.reset()), "Test data reset.");
    }

    public record ResetResponse(int employees) { }
}
